package services;

import utils.AppSecrets;
import utils.DBConnection;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AdminAssistantService {

    private static final String OUT_OF_SCOPE_MESSAGE =
            "I can only answer questions about the existing desktop application features and database information.";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String answer(String question) throws IOException, InterruptedException, SQLException {
        Optional<String> localReply = tryLocalReply(question);
        if (localReply.isPresent()) {
            return localReply.get();
        }

        if (!isInScope(question)) {
            return OUT_OF_SCOPE_MESSAGE;
        }

        String apiKey = AppSecrets.get("grok.apiKey");
        if (apiKey.isBlank()) {
            throw new IOException("Grok API key is missing in app-secrets.properties.");
        }

        String endpoint = AppSecrets.get("grok.endpoint");
        String model = AppSecrets.get("grok.model");
        String payload = buildGrokPayload(model, question, buildKnowledgeContext());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 401 || response.statusCode() == 403) {
            return "The external AI service rejected this request because the configured Grok API key does not have permission for the selected model. Update grok.apiKey or use a key with xAI/Grok access.";
        }
        if (response.statusCode() >= 400) {
            throw new IOException("Grok API error " + response.statusCode() + ": " + response.body());
        }

        return extractGrokMessage(response.body());
    }

    private Optional<String> tryLocalReply(String question) {
        if (question == null) {
            return Optional.empty();
        }

        String normalized = question.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        if (normalized.matches("^(hi|hello|hey|salem|salam|bonjour|good morning|good afternoon|good evening)[!. ]*$")) {
            return Optional.of("Hello. Ask me about admin features or database data, for example user counts, modules, forums, messages, or login and password reset flows.");
        }

        return Optional.empty();
    }

    private boolean isInScope(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }

        String normalized = question.toLowerCase(Locale.ROOT);
        List<String> keywords = Arrays.asList(
                "admin", "dashboard", "user", "users", "student", "teacher", "administrator",
                "login", "password", "reset", "mail", "email", "module", "course", "content",
                "event", "evenement", "forum", "message", "quiz", "question", "evaluation",
                "score", "soumission", "database", "table", "column", "record", "row",
                "utilisateur", "etudiant", "enseignant", "administrateur"
        );

        for (String keyword : keywords) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String buildKnowledgeContext() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("Desktop application features:\n");
        builder.append("- Login screen with role-based routing.\n");
        builder.append("- Admin dashboard with user registry, statistics, create/update/delete, modules, events, forums, and messages.\n");
        builder.append("- Teacher and student dashboards with profile and password change.\n");
        builder.append("- Password reset by email token.\n");
        builder.append("- MySQL-backed data services for users, modules, courses, content, quizzes, evaluations, submissions, forums, messages, and events.\n");
        builder.append("\nDatabase snapshot:\n");
        builder.append(readDatabaseMetadata());
        return builder.toString();
    }

    private String readDatabaseMetadata() throws SQLException {
        StringBuilder builder = new StringBuilder();
        Connection connection = DBConnection.getInstance().getConnection();
        if (connection == null) {
            builder.append("Database connection unavailable.");
            return builder.toString();
        }

        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                builder.append("Table ").append(tableName).append(": columns ");

                List<String> columns = new ArrayList<>();
                try (ResultSet rsColumns = metaData.getColumns(connection.getCatalog(), null, tableName, "%")) {
                    while (rsColumns.next()) {
                        columns.add(rsColumns.getString("COLUMN_NAME"));
                    }
                }
                builder.append(String.join(", ", columns));

                try (Statement statement = connection.createStatement();
                     ResultSet countResult = statement.executeQuery("SELECT COUNT(*) FROM `" + tableName + "`")) {
                    if (countResult.next()) {
                        builder.append(" | rows=").append(countResult.getInt(1));
                    }
                } catch (SQLException e) {
                    builder.append(" | rows=unavailable");
                }
                builder.append(".\n");
            }
        }

        return builder.toString();
    }

    private String buildGrokPayload(String model, String question, String knowledgeContext) {
        // xAI /v1/responses format: simple input field, not messages array
        String combinedPrompt = "You are the admin assistant for the PIDEV_JAVA desktop application. Only answer questions about: 1. Existing desktop application functionality. 2. The live database structure and database content described in the context. If the question is outside that scope, reply exactly with: I can only answer questions about the existing desktop application features and database information. Do not invent features that are not present. Keep answers concise and practical for an administrator.\n\nContext:\n" + knowledgeContext + "\n\nQuestion:\n" + question;

        return "{"
                + "\"model\":\"" + escapeJson(model) + "\","
                + "\"input\":\"" + escapeJson(combinedPrompt) + "\""
                + "}";
    }

    // Keep for backward compatibility
    private String buildPayload(String model, String question, String knowledgeContext) {
        return buildGrokPayload(model, question, knowledgeContext);
    }

    private String extractGrokMessage(String responseBody) {
        // xAI /v1/responses format: {"output":"..."} or similar
        String marker = "\"output\":";
        int outputIndex = responseBody.indexOf(marker);
        if (outputIndex < 0) {
            // Try "content" field (OpenAI format fallback)
            marker = "\"content\":";
            outputIndex = responseBody.indexOf(marker);
        }
        if (outputIndex < 0) {
            // Fallback to Gemini format
            return extractAssistantMessage(responseBody);
        }

        int quoteStart = responseBody.indexOf('"', outputIndex + marker.length());
        if (quoteStart < 0) {
            return "The assistant returned an unexpected response.";
        }

        int start = quoteStart + 1;
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;

        for (int i = start; i < responseBody.length(); i++) {
            char current = responseBody.charAt(i);
            if (escaping) {
                switch (current) {
                    case 'n' -> builder.append('\n');
                    case 'r' -> builder.append('\r');
                    case 't' -> builder.append('\t');
                    case '"' -> builder.append('"');
                    case '\\' -> builder.append('\\');
                    default -> builder.append(current);
                }
                escaping = false;
                continue;
            }

            if (current == '\\') {
                escaping = true;
                continue;
            }
            if (current == '"') {
                break;
            }
            builder.append(current);
        }

        return builder.toString().trim();
    }

    // Keep for backward compatibility (Gemini format)
    private String extractAssistantMessage(String responseBody) {
        String marker = "\"text\":";
        int textIndex = responseBody.indexOf(marker);
        if (textIndex < 0) {
            return "The assistant returned an unexpected response.";
        }

        int quoteStart = responseBody.indexOf('"', textIndex + marker.length());
        if (quoteStart < 0) {
            return "The assistant returned an unexpected response.";
        }

        int start = quoteStart + 1;
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;

        for (int i = start; i < responseBody.length(); i++) {
            char current = responseBody.charAt(i);
            if (escaping) {
                switch (current) {
                    case 'n' -> builder.append('\n');
                    case 'r' -> builder.append('\r');
                    case 't' -> builder.append('\t');
                    case '"' -> builder.append('"');
                    case '\\' -> builder.append('\\');
                    default -> builder.append(current);
                }
                escaping = false;
                continue;
            }

            if (current == '\\') {
                escaping = true;
                continue;
            }
            if (current == '"') {
                break;
            }
            builder.append(current);
        }

        return builder.toString().trim();
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
