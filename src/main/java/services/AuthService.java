package services;

import entities.Utilisateur;
import utils.DBConnection;
import utils.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthService {

    private static final String PHP_EXECUTABLE = "C:\\xampp\\php\\php.exe";
    private final UtilisateurService utilisateurService = new UtilisateurService();

    public Utilisateur authenticate(String email, String password) throws SQLException {
        Utilisateur utilisateur = utilisateurService.findByEmail(email);
        if (utilisateur == null) {
            return null;
        }

        if (verifyPassword(password, utilisateur.getMotDePasse())) {
            utilisateur.setLastLogin(LocalDateTime.now());
            updateLastLogin(utilisateur);
            UserSession.start(utilisateur);
            return utilisateur;
        }

        return null;
    }

    public boolean requestPasswordReset(String email) throws SQLException {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        String sql = "UPDATE utilisateur SET reset_token = ?, reset_token_expires_at = ? WHERE email = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, Timestamp.valueOf(expiresAt));
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean resetPassword(String email, String newPassword) throws SQLException, IOException {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE utilisateur SET mot_de_passe = ?, reset_token = ?, reset_token_expires_at = ? WHERE email = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean changePassword(Utilisateur utilisateur, String currentPassword, String newPassword) throws SQLException, IOException {
        if (utilisateur == null || utilisateur.getId() == null) {
            return false;
        }
        if (currentPassword == null || newPassword == null) {
            return false;
        }

        String storedHash = fetchPasswordHashById(utilisateur.getId());
        if (storedHash == null || !verifyPassword(currentPassword, storedHash)) {
            return false;
        }

        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setLong(2, utilisateur.getId());
            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                utilisateur.setMotDePasse(hashedPassword);
                Utilisateur sessionUser = UserSession.getCurrentUser();
                if (sessionUser != null && sessionUser.getId() != null && sessionUser.getId().equals(utilisateur.getId())) {
                    sessionUser.setMotDePasse(hashedPassword);
                }
            }
            return updated;
        }
    }

    private void updateLastLogin(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE utilisateur SET last_login = ? WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(utilisateur.getLastLogin()));
            ps.setLong(2, utilisateur.getId());
            ps.executeUpdate();
        }
    }

    private String fetchPasswordHashById(Long id) throws SQLException {
        String sql = "SELECT mot_de_passe FROM utilisateur WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        if (!storedHash.startsWith("$2")) {
            return rawPassword.equals(storedHash);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                PHP_EXECUTABLE,
                "-r",
                "exit(password_verify($argv[1], $argv[2]) ? 0 : 1);",
                rawPassword,
                storedHash
        );

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public String hashPassword(String rawPassword) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                PHP_EXECUTABLE,
                "-r",
                "echo password_hash($argv[1], PASSWORD_BCRYPT);",
                rawPassword
        );

        Process process = processBuilder.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("PHP password hashing failed with exit code " + exitCode);
            }
            return new String(process.getInputStream().readAllBytes()).trim();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Password hashing interrupted.", e);
        }
    }
}
