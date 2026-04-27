package services;

import entities.ParticipationEvenement;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ParticipationEvenementService implements IService<ParticipationEvenement> {

    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    @Override
    public void create(ParticipationEvenement participation) throws SQLException {
        String sql = "INSERT INTO participation_evenement (evenement_id, utilisateur_id, statut, date_inscription, heure_arrivee, heure_depart, feedback_note, feedback_commentaire) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(ps, participation);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    participation.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(ParticipationEvenement participation) throws SQLException {
        if (participation.getId() == null) {
            throw new SQLException("Missing participation id for update.");
        }
        String sql = "UPDATE participation_evenement SET evenement_id = ?, utilisateur_id = ?, statut = ?, date_inscription = ?, heure_arrivee = ?, heure_depart = ?, feedback_note = ?, feedback_commentaire = ? "
                + "WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillStatement(ps, participation);
            ps.setInt(9, participation.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM participation_evenement WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public ParticipationEvenement getById(int id) throws SQLException {
        String sql = "SELECT * FROM participation_evenement WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapParticipation(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ParticipationEvenement> getAll() throws SQLException {
        List<ParticipationEvenement> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation_evenement ORDER BY date_inscription DESC, id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                participations.add(mapParticipation(rs));
            }
        }
        return participations;
    }

    private void fillStatement(PreparedStatement ps, ParticipationEvenement participation) throws SQLException {
        if (participation.getEvenementId() != null) {
            ps.setInt(1, participation.getEvenementId());
        } else {
            ps.setNull(1, java.sql.Types.INTEGER);
        }
        if (participation.getUtilisateurId() != null) {
            ps.setInt(2, participation.getUtilisateurId());
        } else {
            ps.setNull(2, java.sql.Types.INTEGER);
        }
        ps.setString(3, participation.getStatut());
        ps.setTimestamp(4, participation.getDateInscription() != null ? Timestamp.valueOf(participation.getDateInscription()) : null);
        ps.setTimestamp(5, participation.getHeureArrivee() != null ? Timestamp.valueOf(participation.getHeureArrivee()) : null);
        ps.setTimestamp(6, participation.getHeureDepart() != null ? Timestamp.valueOf(participation.getHeureDepart()) : null);
        if (participation.getFeedbackNote() != null) {
            ps.setInt(7, participation.getFeedbackNote());
        } else {
            ps.setNull(7, java.sql.Types.INTEGER);
        }
        ps.setString(8, participation.getFeedbackCommentaire());
    }

    private ParticipationEvenement mapParticipation(ResultSet rs) throws SQLException {
        ParticipationEvenement participation = new ParticipationEvenement();
        participation.setId(rs.getInt("id"));

        int evenementId = rs.getInt("evenement_id");
        if (!rs.wasNull()) {
            participation.setEvenementId(evenementId);
        }

        int utilisateurId = rs.getInt("utilisateur_id");
        if (!rs.wasNull()) {
            participation.setUtilisateurId(utilisateurId);
        }

        participation.setStatut(rs.getString("statut"));

        Timestamp dateInscription = rs.getTimestamp("date_inscription");
        if (dateInscription != null) {
            participation.setDateInscription(dateInscription.toLocalDateTime());
        }

        Timestamp heureArrivee = rs.getTimestamp("heure_arrivee");
        if (heureArrivee != null) {
            participation.setHeureArrivee(heureArrivee.toLocalDateTime());
        }

        Timestamp heureDepart = rs.getTimestamp("heure_depart");
        if (heureDepart != null) {
            participation.setHeureDepart(heureDepart.toLocalDateTime());
        }

        int feedbackNote = rs.getInt("feedback_note");
        if (!rs.wasNull()) {
            participation.setFeedbackNote(feedbackNote);
        }

        participation.setFeedbackCommentaire(rs.getString("feedback_commentaire"));
        return participation;
    }
}
