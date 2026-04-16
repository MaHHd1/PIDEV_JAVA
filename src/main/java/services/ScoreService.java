package services;

import entities.Score;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScoreService {
    private final Connection conn;

    public ScoreService() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public void add(Score score) {
        if (score.getDateCorrection() == null) {
            score.setDateCorrection(LocalDateTime.now());
        }

        String sql = "INSERT INTO score (soumission_id, note, note_sur, commentaire_enseignant, date_correction, statut_correction) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void update(Score score) {
        String sql = "UPDATE score SET soumission_id = ?, note = ?, note_sur = ?, commentaire_enseignant = ?, date_correction = ?, statut_correction = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void delete(Score score) {
        String sql = "DELETE FROM score WHERE id = " + score.getId();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Score getById(int id) {
        String sql = "SELECT * FROM score WHERE id = " + id;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                return null;
            }

            Score score = new Score();
            score.setId(rs.getInt("id"));
            score.setSoumissionId(rs.getInt("soumission_id"));
            score.setNote(rs.getDouble("note"));
            score.setNoteSur(rs.getDouble("note_sur"));
            score.setCommentaireEnseignant(rs.getString("commentaire_enseignant"));
            score.setDateCorrection(rs.getTimestamp("date_correction").toLocalDateTime());
            score.setStatutCorrection(rs.getString("statut_correction"));
            return score;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Score> getAll() {
        String sql = "SELECT * FROM score";
        List<Score> scores = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Score score = new Score();
                score.setId(rs.getInt("id"));
                score.setSoumissionId(rs.getInt("soumission_id"));
                score.setNote(rs.getDouble("note"));
                score.setNoteSur(rs.getDouble("note_sur"));
                score.setCommentaireEnseignant(rs.getString("commentaire_enseignant"));
                score.setDateCorrection(rs.getTimestamp("date_correction").toLocalDateTime());
                score.setStatutCorrection(rs.getString("statut_correction"));
                scores.add(score);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }

    public Score getBySoumissionId(int soumissionId) {
        String sql = "SELECT * FROM score WHERE soumission_id = " + soumissionId;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                return null;
            }

            Score score = new Score();
            score.setId(rs.getInt("id"));
            score.setSoumissionId(rs.getInt("soumission_id"));
            score.setNote(rs.getDouble("note"));
            score.setNoteSur(rs.getDouble("note_sur"));
            score.setCommentaireEnseignant(rs.getString("commentaire_enseignant"));
            score.setDateCorrection(rs.getTimestamp("date_correction").toLocalDateTime());
            score.setStatutCorrection(rs.getString("statut_correction"));
            return score;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
