package services;

import entities.Utilisateur;
import utils.DBConnection;
import utils.UserSession;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthService {

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
            if (connection == null) {
                throw new SQLException("Database connection is not available");
            }
            ps.setString(1, token);
            ps.setTimestamp(2, Timestamp.valueOf(expiresAt));
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean resetPassword(String email, String newPassword) throws SQLException {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE utilisateur SET mot_de_passe = ?, reset_token = ?, reset_token_expires_at = ? WHERE email = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (connection == null) {
                throw new SQLException("Database connection is not available");
            }
            ps.setString(1, hashedPassword);
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean changePassword(Utilisateur utilisateur, String currentPassword, String newPassword) throws SQLException {
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
            if (connection == null) {
                throw new SQLException("Database connection is not available");
            }
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
            if (connection == null) {
                throw new SQLException("Database connection is not available");
            }
            ps.setTimestamp(1, Timestamp.valueOf(utilisateur.getLastLogin()));
            ps.setLong(2, utilisateur.getId());
            ps.executeUpdate();
        }
    }

    private String fetchPasswordHashById(Long id) throws SQLException {
        String sql = "SELECT mot_de_passe FROM utilisateur WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (connection == null) {
                throw new SQLException("Database connection is not available");
            }
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

        // Check if password is BCrypt hashed (starts with $2a$, $2b$, or $2y$)
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return BCrypt.verifyer().verify(rawPassword.toCharArray(), storedHash).verified;
        }

        // Fallback to plain text comparison for backwards compatibility
        return rawPassword.equals(storedHash);
    }

    public String hashPassword(String rawPassword) {
        return BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
    }
}