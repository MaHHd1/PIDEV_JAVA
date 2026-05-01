package services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import utils.AppSecrets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void sendPasswordResetEmail(String recipientEmail, String recipientName, String resetUrl, LocalDateTime expiresAt)
            throws MessagingException {
        String username = AppSecrets.get("mail.username");
        String appPassword = AppSecrets.get("mail.appPassword");
        String fromName = AppSecrets.get("mail.fromName");

        if (username.isBlank() || appPassword.isBlank()) {
            throw new MessagingException("Mail credentials are missing in app-secrets.properties.");
        }

        Session session = Session.getInstance(buildMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username, fromName));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Invalid mail sender configuration.", e);
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("PIDEV password reset");
        message.setContent(buildResetMailBody(recipientName, resetUrl, expiresAt), "text/html; charset=UTF-8");

        Transport.send(message);
    }

    private Properties buildMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        return properties;
    }

    private String buildResetMailBody(String recipientName, String resetUrl, LocalDateTime expiresAt) {
        String safeName = recipientName == null || recipientName.isBlank() ? "User" : recipientName;
        return "<!DOCTYPE html><html><body style=\"font-family:Arial,sans-serif;background:#f5f7fb;padding:24px;color:#1b2338;\">"
                + "<div style=\"max-width:640px;margin:0 auto;background:#ffffff;border-radius:18px;padding:32px;"
                + "box-shadow:0 18px 40px rgba(24,38,70,.12);\">"
                + "<h2 style=\"margin-top:0;\">Reset your password</h2>"
                + "<p>Hello " + safeName + ",</p>"
                + "<p>We received a password reset request for your PIDEV desktop account.</p>"
                + "<p>This link stays valid until <strong>" + expiresAt.format(DATE_TIME_FORMATTER) + "</strong>.</p>"
                + "<p><a href=\"" + resetUrl + "\" style=\"display:inline-block;padding:12px 20px;background:#1f5eff;"
                + "color:#ffffff;text-decoration:none;border-radius:10px;\">Open reset page</a></p>"
                + "<p>The desktop application must be running on this computer when you click the link.</p>"
                + "<p>If you did not request this reset, you can ignore this message.</p>"
                + "</div></body></html>";
    }
}
