package services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService{

    private final String senderEmail;
    private final String senderPassword;

    public EmailService() {
        try (InputStream input = getClass()
                .getResourceAsStream("/config.properties")) {
            Properties config = new Properties();
            config.load(input);
            this.senderEmail    = config.getProperty("mail.sender");
            this.senderPassword = config.getProperty("mail.password");
        } catch (IOException e) {
            throw new RuntimeException("config.properties introuvable", e);
        }
    }

    // ── Envoi asynchrone ─────────────────────────────────────────
    public void envoyerResultatAsync(String destinataireEmail,
                                     String nomEtudiant,
                                     String titreQuiz,
                                     int pointsObtenus,
                                     int pointsTotal,
                                     int score,
                                     String datePassation) {
        new Thread(() -> {
            try {
                envoyer(destinataireEmail, nomEtudiant, titreQuiz,
                        pointsObtenus, pointsTotal, score, datePassation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ── Envoi principal ──────────────────────────────────────────
    private void envoyer(String destinataireEmail,
                         String nomEtudiant,
                         String titreQuiz,
                         int pointsObtenus,
                         int pointsTotal,
                         int score,
                         String datePassation) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(destinataireEmail));
        message.setSubject("Résultat — " + titreQuiz);
        message.setContent(
                construireCorps(nomEtudiant, titreQuiz,
                        pointsObtenus, pointsTotal, score, datePassation),
                "text/html; charset=utf-8");

        Transport.send(message);
    }

    // ── Corps HTML ───────────────────────────────────────────────
    private String construireCorps(String nomEtudiant,
                                   String titreQuiz,
                                   int pointsObtenus,
                                   int pointsTotal,
                                   int score,
                                   String datePassation) {
        String mention = score >= 85 ? "Très bien"
                : score >= 70 ? "Bien"
                  : score >= 50 ? "Passable"
                    : "Insuffisant";

        String mentionColor = score >= 70 ? "#16a34a" : score >= 50 ? "#ca8a04" : "#dc2626";

        return """
            <div style="font-family:sans-serif;max-width:600px;margin:auto;padding:24px;">
              <h2 style="color:#0f766e;margin-bottom:4px;">Résultat de votre quiz</h2>
              <p style="color:#888;font-size:13px;margin-top:0;">Campus Access — Quiz Manager</p>
              <hr style="border:none;border-top:1px solid #e5e7eb;margin:16px 0;">
              <p>Bonjour <b>%s</b>,</p>
              <p>Voici votre résultat pour le quiz <b>%s</b> :</p>
              <div style="background:#f0fdf4;border-radius:12px;padding:24px;margin:16px 0;border:1px solid #bbf7d0;">
                <p style="font-size:40px;font-weight:900;color:#16a34a;margin:0;">%d / %d</p>
                <p style="color:#555;margin:6px 0;">Score : <b>%d%%</b></p>
                <p style="color:%s;font-weight:700;margin:4px 0;">%s</p>
                <p style="color:#999;font-size:12px;margin-top:8px;">Passé le : %s</p>
              </div>
              <p style="color:#aaa;font-size:11px;margin-top:24px;">
                Ce message est envoyé automatiquement — Campus Access
              </p>
            </div>
            """.formatted(
                nomEtudiant, titreQuiz,
                pointsObtenus, pointsTotal,
                score, mentionColor, mention,
                datePassation
        );
    }
}