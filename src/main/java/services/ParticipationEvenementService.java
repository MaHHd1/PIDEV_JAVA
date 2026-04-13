package services;

import entities.ParticipationEvenement;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationEvenementService implements IService<ParticipationEvenement> {
    private Connection conn;

    public ParticipationEvenementService() {
        this.conn = MyConnection.getInstance().getConnection();
    }

    @Override
    public void create(ParticipationEvenement p) throws SQLException {
        String SQL = "INSERT INTO participation_evenement (evenement_id, utilisateur_id, statut, date_inscription, heure_arrivee, heure_depart, feedback_note, feedback_commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, p.getEvenement_id());
            pstmt.setInt(2, p.getUtilisateur_id());
            pstmt.setString(3, p.getStatut());
            pstmt.setTimestamp(4, p.getDate_inscription() != null ? Timestamp.valueOf(p.getDate_inscription()) : null);
            pstmt.setTimestamp(5, p.getHeure_arrivee() != null ? Timestamp.valueOf(p.getHeure_arrivee()) : null);
            pstmt.setTimestamp(6, p.getHeure_depart() != null ? Timestamp.valueOf(p.getHeure_depart()) : null);
            pstmt.setInt(7, p.getFeedback_note());
            pstmt.setString(8, p.getFeedback_commentaire());
            pstmt.executeUpdate();
            System.out.println("Participation created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating participation: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(ParticipationEvenement p) throws SQLException {
        String SQL = "UPDATE participation_evenement SET evenement_id = ?, utilisateur_id = ?, statut = ?, date_inscription = ?, heure_arrivee = ?, heure_depart = ?, feedback_note = ?, feedback_commentaire = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, p.getEvenement_id());
            pstmt.setInt(2, p.getUtilisateur_id());
            pstmt.setString(3, p.getStatut());
            pstmt.setTimestamp(4, p.getDate_inscription() != null ? Timestamp.valueOf(p.getDate_inscription()) : null);
            pstmt.setTimestamp(5, p.getHeure_arrivee() != null ? Timestamp.valueOf(p.getHeure_arrivee()) : null);
            pstmt.setTimestamp(6, p.getHeure_depart() != null ? Timestamp.valueOf(p.getHeure_depart()) : null);
            pstmt.setInt(7, p.getFeedback_note());
            pstmt.setString(8, p.getFeedback_commentaire());
            pstmt.setInt(9, p.getId());
            pstmt.executeUpdate();
            System.out.println("Participation updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating participation: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(ParticipationEvenement p) throws SQLException {
        String SQL = "DELETE FROM participation_evenement WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, p.getId());
            pstmt.executeUpdate();
            System.out.println("Participation deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting participation: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ParticipationEvenement> readAll() throws SQLException {
        String SQL = "SELECT * FROM participation_evenement";
        List<ParticipationEvenement> participations = new ArrayList<>();
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(SQL)) {
            while (rs.next()) {
                ParticipationEvenement p = new ParticipationEvenement();
                p.setId(rs.getInt("id"));
                p.setEvenement_id(rs.getInt("evenement_id"));
                p.setUtilisateur_id(rs.getInt("utilisateur_id"));
                p.setStatut(rs.getString("statut"));
                
                Timestamp di = rs.getTimestamp("date_inscription");
                if (di != null) p.setDate_inscription(di.toLocalDateTime());
                
                Timestamp ha = rs.getTimestamp("heure_arrivee");
                if (ha != null) p.setHeure_arrivee(ha.toLocalDateTime());
                
                Timestamp hd = rs.getTimestamp("heure_depart");
                if (hd != null) p.setHeure_depart(hd.toLocalDateTime());
                
                p.setFeedback_note(rs.getInt("feedback_note"));
                p.setFeedback_commentaire(rs.getString("feedback_commentaire"));
                participations.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error reading participations: " + e.getMessage());
            throw e;
        }
        return participations;
    }
}
