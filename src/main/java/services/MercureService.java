package services;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Service Mercure — publie des notifications Server-Sent Events.
 *
 * Hub : http://localhost:3000/.well-known/mercure
 * Clé : !ChangeThisMercureHubJWTSecretKey!
 *
 * Deux topics :
 *  - quiz/resultat/{enseignantId}  → notifier le prof qu'un étudiant a passé son quiz
 *  - cours/inscription/{etudiantId} → notifier l'étudiant qu'il est inscrit à un cours
 */
public class MercureService {

    private static final String HUB_URL   = "http://localhost:3000/.well-known/mercure";
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZXJjdXJlIjp7InB1Ymxpc2giOlsiKiJdLCJzdWJzY3JpYmUiOlsiKiJdfX0.nINXJPMGL7u4vvquYYm3zgMosrqrxTSooTs7R_OJLZA";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    // ═══════════════════════════════════════════════════════════
    // NOTIFICATION PROF : un étudiant a passé son quiz
    // ═══════════════════════════════════════════════════════════

    /**
     * Appelé après resultatService.create() dans StudentQuizController.
     *
     * @param enseignantId  ID du créateur du quiz
     * @param nomEtudiant   Nom complet de l'étudiant
     * @param quizTitre     Titre du quiz passé
     * @param score         Score obtenu (ex: 85.5)
     * @param earnedPoints  Points obtenus
     * @param totalPoints   Points total
     */
    public void notifierProfQuizSoumis(
            long enseignantId, String nomEtudiant,
            String quizTitre, double score,
            int earnedPoints, int totalPoints) {

        String topic = "quiz/resultat"; // ← topic fixe pour tous les profs
        String data  = String.format(
                "{\"type\":\"quiz_soumis\",\"etudiant\":\"%s\",\"quiz\":\"%s\","
                        + "\"score\":%.1f,\"earned\":%d,\"total\":%d}",
                escape(nomEtudiant), escape(quizTitre), score, earnedPoints, totalPoints
        );
        publier(topic, data);
    }

    // ═══════════════════════════════════════════════════════════
    // NOTIFICATION ÉTUDIANT : inscrit à un cours
    // ═══════════════════════════════════════════════════════════

    /**
     * Appelé après coursService.enrollStudent() dans le contrôleur d'inscription.
     *
     * @param etudiantId  ID de l'étudiant inscrit
     * @param coursTitre  Titre du cours
     * @param coursCode   Code du cours
     */
    public void notifierEtudiantInscription(
            long   etudiantId,
            String coursTitre,
            String coursCode) {

        String topic = "cours/inscription/" + etudiantId;
        String data  = String.format(
                "{\"type\":\"inscription\",\"cours\":\"%s\",\"code\":\"%s\"}",
                escape(coursTitre), escape(coursCode != null ? coursCode : "")
        );
        publier(topic, data);
    }

    // ═══════════════════════════════════════════════════════════
    // PUBLICATION HTTP VERS MERCURE
    // ═══════════════════════════════════════════════════════════
    private void publier(String topic, String jsonData) {
        new Thread(() -> {
            try {
                // Corps de la requête : application/x-www-form-urlencoded
                String body = "topic=" + URLEncoder.encode(topic, StandardCharsets.UTF_8)
                        + "&data=" + URLEncoder.encode(jsonData, StandardCharsets.UTF_8);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(HUB_URL))
                        .header("Authorization", "Bearer " + JWT_TOKEN)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    System.out.println("✅ Mercure → topic=" + topic);
                } else {
                    System.err.println("⚠ Mercure erreur " + response.statusCode()
                            + " : " + response.body());
                }
            } catch (Exception e) {
                // Ne pas bloquer l'application si Mercure n'est pas lancé
                System.err.println("⚠ Mercure non disponible : " + e.getMessage());
            }
        }, "mercure-publisher").start();
    }

    // ═══════════════════════════════════════════════════════════
    // UTILITAIRE
    // ═══════════════════════════════════════════════════════════
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}