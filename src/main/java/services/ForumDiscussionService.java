package services;

import entities.ForumDiscussion;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForumDiscussionService {

    private final Connection connection;

    public ForumDiscussionService() {
        this(DBConnection.getInstance().getConnection());
    }

    public ForumDiscussionService(Connection connection) {
        this.connection = connection;
        ensureSchema();
    }

    public void ajouter(ForumDiscussion forum) throws SQLException {
        String sql = "INSERT INTO forum_discussion "
                + "(titre, description, id_createur, id_cours, date_creation, type, statut, nombre_vues, derniere_activite, tags, regles_moderation, image_couverture_url, piece_jointe_url, likes, dislikes, signalements, est_modifie, date_modification) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindForum(ps, forum, false);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    forum.setId(rs.getLong(1));
                }
            }
        }
    }

    public void modifier(ForumDiscussion forum) throws SQLException {
        String sql = "UPDATE forum_discussion SET "
                + "titre=?, description=?, id_createur=?, id_cours=?, date_creation=?, type=?, statut=?, nombre_vues=?, derniere_activite=?, tags=?, regles_moderation=?, image_couverture_url=?, piece_jointe_url=?, likes=?, dislikes=?, signalements=?, est_modifie=?, date_modification=? "
                + "WHERE id_forum=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindForum(ps, forum, true);
            ps.executeUpdate();
        }
    }

    public void supprimer(long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM forum_discussion WHERE id_forum = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<ForumDiscussion> getAll() throws SQLException {
        List<ForumDiscussion> forums = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM forum_discussion ORDER BY derniere_activite DESC, date_creation DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                forums.add(mapForum(rs));
            }
        }
        return forums;
    }

    public void incrementerVues(long idForum) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE forum_discussion SET nombre_vues = nombre_vues + 1, derniere_activite = ? WHERE id_forum = ?")) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, idForum);
            ps.executeUpdate();
        }
    }

    private void bindForum(PreparedStatement ps, ForumDiscussion forum, boolean includeId) throws SQLException {
        ps.setString(1, forum.getTitre());
        ps.setString(2, forum.getDescription());
        ps.setLong(3, forum.getCreateurId());

        if (forum.getIdCours() == null) {
            ps.setNull(4, Types.INTEGER);
        } else {
            ps.setInt(4, forum.getIdCours());
        }

        ps.setTimestamp(5, Timestamp.valueOf(forum.getDateCreation()));
        ps.setString(6, forum.getType());
        ps.setString(7, forum.getStatut());
        ps.setInt(8, forum.getNombreVues());

        if (forum.getDerniereActivite() == null) {
            ps.setNull(9, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(9, Timestamp.valueOf(forum.getDerniereActivite()));
        }

        ps.setString(10, serializeTags(forum.getTags()));
        ps.setString(11, forum.getReglesModeration());
        ps.setString(12, forum.getImageCouvertureUrl());
        ps.setString(13, forum.getPieceJointeUrl());
        ps.setInt(14, forum.getLikes());
        ps.setInt(15, forum.getDislikes());
        ps.setInt(16, forum.getSignalements());
        ps.setBoolean(17, forum.isEstModifie());

        if (forum.getDateModification() == null) {
            ps.setNull(18, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(18, Timestamp.valueOf(forum.getDateModification()));
        }

        if (includeId) {
            ps.setLong(19, forum.getId());
        }
    }

    private ForumDiscussion mapForum(ResultSet rs) throws SQLException {
        ForumDiscussion forum = new ForumDiscussion();
        forum.setId(rs.getLong("id_forum"));
        forum.setTitre(rs.getString("titre"));
        forum.setDescription(rs.getString("description"));
        forum.setCreateurId(rs.getLong("id_createur"));

        int idCours = rs.getInt("id_cours");
        if (!rs.wasNull()) {
            forum.setIdCours(idCours);
        }

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            forum.setDateCreation(dateCreation.toLocalDateTime());
        }

        forum.setType(rs.getString("type"));
        forum.setStatut(rs.getString("statut"));
        forum.setNombreVues(rs.getInt("nombre_vues"));

        Timestamp derniereActivite = rs.getTimestamp("derniere_activite");
        if (derniereActivite != null) {
            forum.setDerniereActivite(derniereActivite.toLocalDateTime());
        }

        forum.setTags(deserializeTags(rs.getString("tags")));
        forum.setReglesModeration(rs.getString("regles_moderation"));
        forum.setImageCouvertureUrl(rs.getString("image_couverture_url"));
        forum.setPieceJointeUrl(rs.getString("piece_jointe_url"));
        forum.setLikes(rs.getInt("likes"));
        forum.setDislikes(rs.getInt("dislikes"));
        forum.setSignalements(rs.getInt("signalements"));
        forum.setEstModifie(rs.getBoolean("est_modifie"));

        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            forum.setDateModification(dateModification.toLocalDateTime());
        }

        return forum;
    }

    private void ensureSchema() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS forum_discussion (
                        id_forum BIGINT PRIMARY KEY AUTO_INCREMENT,
                        titre VARCHAR(255) NOT NULL,
                        description TEXT NOT NULL,
                        id_createur BIGINT NOT NULL,
                        id_cours INT NULL,
                        date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        type VARCHAR(50) NOT NULL DEFAULT 'public',
                        statut VARCHAR(50) NOT NULL DEFAULT 'ouvert',
                        nombre_vues INT NOT NULL DEFAULT 0,
                        derniere_activite TIMESTAMP NULL,
                        tags TEXT NULL,
                        regles_moderation TEXT NULL,
                        image_couverture_url VARCHAR(255) NULL,
                        piece_jointe_url VARCHAR(255) NULL,
                        likes INT NOT NULL DEFAULT 0,
                        dislikes INT NOT NULL DEFAULT 0,
                        signalements INT NOT NULL DEFAULT 0,
                        est_modifie BOOLEAN NOT NULL DEFAULT FALSE,
                        date_modification TIMESTAMP NULL,
                        CONSTRAINT fk_forum_createur FOREIGN KEY (id_createur) REFERENCES utilisateur(id) ON DELETE CASCADE
                    )
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize forum_discussion table", e);
        }
    }

    private String serializeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "[]";
        }
        return tags.stream()
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(value -> "\"" + value.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    private List<String> deserializeTags(String tags) {
        if (tags == null || tags.isBlank() || "[]".equals(tags.trim())) {
            return new ArrayList<>();
        }
        String cleaned = tags.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        if (cleaned.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .map(value -> value.startsWith("\"") && value.endsWith("\"") ? value.substring(1, value.length() - 1) : value)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toList());
    }
}
