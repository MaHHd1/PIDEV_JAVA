package org.example.service;

import org.example.entity.Score;
import org.example.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScoreService implements IService<Score> {
    Connection conn;

    public ScoreService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Score score) {
        // ✅ Sécurité : définir dateCorrection si null
        if (score.getDateCorrection() == null) {
            score.setDateCorrection(LocalDateTime.now());
        }

        String SQL = "INSERT INTO score (soumission_id, note, note_sur, commentaire_enseignant, date_correction, statut_correction) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, score.getSoumissionId());
            stmt.setDouble(2, score.getNote());
            stmt.setDouble(3, score.getNoteSur());
            stmt.setString(4, score.getCommentaireEnseignant());
            stmt.setTimestamp(5, Timestamp.valueOf(score.getDateCorrection()));
            stmt.setString(6, score.getStatutCorrection());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Score score) {
        String SQL = "UPDATE score SET soumission_id = ?, note = ?, note_sur = ?, commentaire_enseignant = ?, date_correction = ?, statut_correction = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, score.getSoumissionId());
            stmt.setDouble(2, score.getNote());
            stmt.setDouble(3, score.getNoteSur());
            stmt.setString(4, score.getCommentaireEnseignant());
            stmt.setTimestamp(5, Timestamp.valueOf(score.getDateCorrection()));
            stmt.setString(6, score.getStatutCorrection());
            stmt.setInt(7, score.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Score score) {
        String SQL = "DELETE FROM score WHERE id = " + score.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Score getById(int id) {  // 🆕 AJOUTER CETTE MÉTHODE
        String SQL = "SELECT * FROM score WHERE id = " + id;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                Score s = new Score();
                s.setId(rs.getInt("id"));
                s.setSoumissionId(rs.getInt("soumission_id"));
                s.setNote(rs.getDouble("note"));
                s.setNoteSur(rs.getDouble("note_sur"));
                s.setCommentaireEnseignant(rs.getString("commentaire_enseignant"));
                s.setDateCorrection(rs.getTimestamp("date_correction").toLocalDateTime());
                s.setStatutCorrection(rs.getString("statut_correction"));
                return s;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Score> getAll() {
        String SQL = "SELECT * FROM score";
        ArrayList<Score> scores = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Score s = new Score();
                s.setId(rs.getInt("id"));
                s.setSoumissionId(rs.getInt("soumission_id"));
                s.setNote(rs.getDouble("note"));
                s.setNoteSur(rs.getDouble("note_sur"));
                s.setCommentaireEnseignant(rs.getString("commentaire_enseignant"));
                s.setDateCorrection(rs.getTimestamp("date_correction").toLocalDateTime());
                s.setStatutCorrection(rs.getString("statut_correction"));
                scores.add(s);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }

    // Récupérer un score par ID de soumission
    public Score getBySoumissionId(int soumissionId) {
        String SQL = "SELECT * FROM score WHERE soumission_id = " + soumissionId;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                Score s = new Score();
                s.setId(rs.getInt("id"));
                s.setSoumissionId(rs.getInt("soumission_id"));
                s.setNote(rs.getDouble("note"));
                s.setNoteSur(rs.getDouble("note_sur"));
                s.setCommentaireEnseignant(rs.getString("commentaire_enseignant"));
                s.setDateCorrection(rs.getTimestamp("date_correction").toLocalDateTime());
                s.setStatutCorrection(rs.getString("statut_correction"));
                return s;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}