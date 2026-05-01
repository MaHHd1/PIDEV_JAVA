package services;

import entities.Message;
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
import java.util.List;

public class MessageService {

    private final Connection connection;

    public MessageService() {
        this(DBConnection.getInstance().getConnection());
    }

    public MessageService(Connection connection) {
        this.connection = connection;
        ensureSchema();
    }

    public void ajouter(Message message) throws SQLException {
        String sql = "INSERT INTO `message` "
                + "(expediteur_id, destinataire_id, objet, contenu, date_envoi, date_lecture, statut, priorite, piece_jointe_url, parent_id, categorie, est_archive_expediteur, est_archive_destinataire, est_supprime_expediteur, est_supprime_destinataire) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindMessage(ps, message, false);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getLong(1));
                }
            }
        }
    }

    public void modifier(Message message) throws SQLException {
        String sql = "UPDATE `message` SET "
                + "expediteur_id=?, destinataire_id=?, objet=?, contenu=?, date_envoi=?, date_lecture=?, statut=?, priorite=?, piece_jointe_url=?, parent_id=?, categorie=?, est_archive_expediteur=?, est_archive_destinataire=?, est_supprime_expediteur=?, est_supprime_destinataire=? "
                + "WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindMessage(ps, message, true);
            ps.executeUpdate();
        }
    }

    public void supprimer(long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM `message` WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Message> getAll() throws SQLException {
        return fetchMessages("SELECT * FROM `message` ORDER BY date_envoi DESC", null);
    }

    public List<Message> getMessagesRecus(long destinataireId) throws SQLException {
        return fetchMessages("SELECT * FROM `message` WHERE destinataire_id = ? ORDER BY date_envoi DESC", destinataireId);
    }

    public List<Message> getMessagesEnvoyes(long expediteurId) throws SQLException {
        return fetchMessages("SELECT * FROM `message` WHERE expediteur_id = ? ORDER BY date_envoi DESC", expediteurId);
    }

    public void marquerCommeLu(long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE `message` SET statut = 'lu', date_lecture = ? WHERE id = ?")) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private List<Message> fetchMessages(String sql, Long parameter) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (parameter != null) {
                ps.setLong(1, parameter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }
        }
        return messages;
    }

    private void bindMessage(PreparedStatement ps, Message message, boolean includeId) throws SQLException {
        ps.setLong(1, message.getExpediteurId());
        ps.setLong(2, message.getDestinataireId());
        ps.setString(3, message.getObjet());
        ps.setString(4, message.getContenu());
        ps.setTimestamp(5, Timestamp.valueOf(message.getDateEnvoi()));

        if (message.getDateLecture() == null) {
            ps.setNull(6, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(6, Timestamp.valueOf(message.getDateLecture()));
        }

        ps.setString(7, message.getStatut());
        ps.setString(8, message.getPriorite());
        ps.setString(9, message.getPieceJointeUrl());

        if (message.getParentId() == null) {
            ps.setNull(10, Types.BIGINT);
        } else {
            ps.setLong(10, message.getParentId());
        }

        ps.setString(11, message.getCategorie());
        ps.setBoolean(12, message.isEstArchiveExpediteur());
        ps.setBoolean(13, message.isEstArchiveDestinataire());
        ps.setBoolean(14, message.isEstSupprimeExpediteur());
        ps.setBoolean(15, message.isEstSupprimeDestinataire());

        if (includeId) {
            ps.setLong(16, message.getId());
        }
    }

    private Message mapMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setExpediteurId(rs.getLong("expediteur_id"));
        message.setDestinataireId(rs.getLong("destinataire_id"));
        message.setObjet(rs.getString("objet"));
        message.setContenu(rs.getString("contenu"));

        Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
        if (dateEnvoi != null) {
            message.setDateEnvoi(dateEnvoi.toLocalDateTime());
        }

        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        if (dateLecture != null) {
            message.setDateLecture(dateLecture.toLocalDateTime());
        }

        message.setStatut(rs.getString("statut"));
        message.setPriorite(rs.getString("priorite"));
        message.setPieceJointeUrl(rs.getString("piece_jointe_url"));

        long parentId = rs.getLong("parent_id");
        if (!rs.wasNull()) {
            message.setParentId(parentId);
        }

        message.setCategorie(rs.getString("categorie"));
        message.setEstArchiveExpediteur(rs.getBoolean("est_archive_expediteur"));
        message.setEstArchiveDestinataire(rs.getBoolean("est_archive_destinataire"));
        message.setEstSupprimeExpediteur(rs.getBoolean("est_supprime_expediteur"));
        message.setEstSupprimeDestinataire(rs.getBoolean("est_supprime_destinataire"));
        return message;
    }

    private void ensureSchema() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `message` (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        expediteur_id BIGINT NOT NULL,
                        destinataire_id BIGINT NOT NULL,
                        objet VARCHAR(255) NOT NULL,
                        contenu TEXT NOT NULL,
                        date_envoi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        date_lecture TIMESTAMP NULL,
                        statut VARCHAR(50) NOT NULL DEFAULT 'envoye',
                        priorite VARCHAR(50) NOT NULL DEFAULT 'normal',
                        piece_jointe_url VARCHAR(255) NULL,
                        parent_id BIGINT NULL,
                        categorie VARCHAR(50) NOT NULL DEFAULT 'personnel',
                        est_archive_expediteur BOOLEAN NOT NULL DEFAULT FALSE,
                        est_archive_destinataire BOOLEAN NOT NULL DEFAULT FALSE,
                        est_supprime_expediteur BOOLEAN NOT NULL DEFAULT FALSE,
                        est_supprime_destinataire BOOLEAN NOT NULL DEFAULT FALSE,
                        CONSTRAINT fk_message_expediteur FOREIGN KEY (expediteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
                        CONSTRAINT fk_message_destinataire FOREIGN KEY (destinataire_id) REFERENCES utilisateur(id) ON DELETE CASCADE
                    )
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize message table", e);
        }
    }
}
