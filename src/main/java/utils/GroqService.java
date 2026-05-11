package utils;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class GroqService {
    private static final String API_KEY = AppSecrets.get("groq.apiKey");
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public static String generateDescription(String nom, String prenom, String annee, String eventTitle, String eventType) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "llama-3.1-8b-instant"); // Remplacement du modèle obsolète
        
        JSONArray messages = new JSONArray();
        
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Tu es un assistant qui génère des descriptions courtes et enthousiastes pour des participations à des événements.");
        messages.put(systemMessage);
        
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Génère une motivation de 2 phrases pour l'étudiant " + prenom + " " + nom + 
                    " qui participe à l'événement '" + eventTitle + 
                    "'.");
        messages.put(userMessage);
        
        json.put("messages", messages);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("Erreur API Groq (" + response.code() + ") : " + responseBody);
            }

            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        }
    }
}
