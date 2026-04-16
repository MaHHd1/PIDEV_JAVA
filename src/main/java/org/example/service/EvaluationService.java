package org.example.service;

import org.example.entity.Evaluation;
import org.example.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvaluationService implements IService<Evaluation> {
    Connection conn;

    public EvaluationService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Evaluation evaluation) {
        // ✅ Sécurité : définir dateCreation si null
        if (evaluation.getDateCreation() == null) {
            evaluation.setDateCreation(LocalDateTime.now());
        }

        String SQL = "INSERT INTO evaluation (titre, type_evaluation, description, cours_id, id_enseignant, date_creation, date_limite, note_max, mode_remise, statut, pdf_filename) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, evaluation.getTitre());
            stmt.setString(2, evaluation.getTypeEvaluation());
            stmt.setString(3, evaluation.getDescription());
            stmt.setInt(4, evaluation.getCoursId());
            stmt.setString(5, evaluation.getIdEnseignant());
            stmt.setTimestamp(6, Timestamp.valueOf(evaluation.getDateCreation()));
            stmt.setTimestamp(7, Timestamp.valueOf(evaluation.getDateLimite()));
            stmt.setDouble(8, evaluation.getNoteMax());
            stmt.setString(9, evaluation.getModeRemise());
            stmt.setString(10, evaluation.getStatut());
            stmt.setString(11, evaluation.getPdfFilename());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Evaluation evaluation) {
        String SQL = "UPDATE evaluation SET titre = ?, type_evaluation = ?, description = ?, cours_id = ?, id_enseignant = ?, date_creation = ?, date_limite = ?, note_max = ?, mode_remise = ?, statut = ?, pdf_filename = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, evaluation.getTitre());
            stmt.setString(2, evaluation.getTypeEvaluation());
            stmt.setString(3, evaluation.getDescription());
            stmt.setInt(4, evaluation.getCoursId());
            stmt.setString(5, evaluation.getIdEnseignant());
            stmt.setTimestamp(6, Timestamp.valueOf(evaluation.getDateCreation()));
            stmt.setTimestamp(7, Timestamp.valueOf(evaluation.getDateLimite()));
            stmt.setDouble(8, evaluation.getNoteMax());
            stmt.setString(9, evaluation.getModeRemise());
            stmt.setString(10, evaluation.getStatut());
            stmt.setString(11, evaluation.getPdfFilename());
            stmt.setInt(12, evaluation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Evaluation evaluation) {
        String SQL = "DELETE FROM evaluation WHERE id = " + evaluation.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Evaluation getById(int id) {  // 🆕 AJOUTER CETTE MÉTHODE
        String SQL = "SELECT * FROM evaluation WHERE id = " + id;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                Evaluation e = new Evaluation();
                e.setId(rs.getInt("id"));
                e.setTitre(rs.getString("titre"));
                e.setTypeEvaluation(rs.getString("type_evaluation"));
                e.setDescription(rs.getString("description"));
                e.setCoursId(rs.getInt("cours_id"));
                e.setIdEnseignant(rs.getString("id_enseignant"));
                e.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                e.setDateLimite(rs.getTimestamp("date_limite").toLocalDateTime());
                e.setNoteMax(rs.getDouble("note_max"));
                e.setModeRemise(rs.getString("mode_remise"));
                e.setStatut(rs.getString("statut"));
                e.setPdfFilename(rs.getString("pdf_filename"));
                return e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Evaluation> getAll() {
        String SQL = "SELECT * FROM evaluation";
        ArrayList<Evaluation> evaluations = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Evaluation e = new Evaluation();
                e.setId(rs.getInt("id"));
                e.setTitre(rs.getString("titre"));
                e.setTypeEvaluation(rs.getString("type_evaluation"));
                e.setDescription(rs.getString("description"));
                e.setCoursId(rs.getInt("cours_id"));
                e.setIdEnseignant(rs.getString("id_enseignant"));
                e.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                e.setDateLimite(rs.getTimestamp("date_limite").toLocalDateTime());
                e.setNoteMax(rs.getDouble("note_max"));
                e.setModeRemise(rs.getString("mode_remise"));
                e.setStatut(rs.getString("statut"));
                e.setPdfFilename(rs.getString("pdf_filename"));
                evaluations.add(e);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return evaluations;
    }
}