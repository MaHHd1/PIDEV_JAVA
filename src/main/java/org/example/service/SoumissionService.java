package org.example.service;

import org.example.entity.Soumission;
import org.example.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SoumissionService implements IService<Soumission> {
    Connection conn;

    public SoumissionService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Soumission soumission) {
        // ✅ Sécurité : définir dateSoumission si null
        if (soumission.getDateSoumission() == null) {
            soumission.setDateSoumission(LocalDateTime.now());
        }

        String SQL = "INSERT INTO soumission (evaluation_id, id_etudiant, fichier_soumission_url, commentaire_etudiant, date_soumission, statut, pdf_filename) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, soumission.getEvaluationId());
            stmt.setString(2, soumission.getIdEtudiant());
            stmt.setString(3, soumission.getFichierSoumissionUrl());
            stmt.setString(4, soumission.getCommentaireEtudiant());
            stmt.setTimestamp(5, Timestamp.valueOf(soumission.getDateSoumission()));
            stmt.setString(6, soumission.getStatut());
            stmt.setString(7, soumission.getPdfFilename());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Soumission soumission) {
        String SQL = "UPDATE soumission SET evaluation_id = ?, id_etudiant = ?, fichier_soumission_url = ?, commentaire_etudiant = ?, date_soumission = ?, statut = ?, pdf_filename = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, soumission.getEvaluationId());
            stmt.setString(2, soumission.getIdEtudiant());
            stmt.setString(3, soumission.getFichierSoumissionUrl());
            stmt.setString(4, soumission.getCommentaireEtudiant());
            stmt.setTimestamp(5, Timestamp.valueOf(soumission.getDateSoumission()));
            stmt.setString(6, soumission.getStatut());
            stmt.setString(7, soumission.getPdfFilename());
            stmt.setInt(8, soumission.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Soumission soumission) {
        String SQL = "DELETE FROM soumission WHERE id = " + soumission.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Soumission getById(int id) {  // 🆕 AJOUTER CETTE MÉTHODE
        String SQL = "SELECT * FROM soumission WHERE id = " + id;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                Soumission s = new Soumission();
                s.setId(rs.getInt("id"));
                s.setEvaluationId(rs.getInt("evaluation_id"));
                s.setIdEtudiant(rs.getString("id_etudiant"));
                s.setFichierSoumissionUrl(rs.getString("fichier_soumission_url"));
                s.setCommentaireEtudiant(rs.getString("commentaire_etudiant"));
                s.setDateSoumission(rs.getTimestamp("date_soumission").toLocalDateTime());
                s.setStatut(rs.getString("statut"));
                s.setPdfFilename(rs.getString("pdf_filename"));
                return s;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Soumission> getAll() {
        String SQL = "SELECT * FROM soumission";
        ArrayList<Soumission> soumissions = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Soumission s = new Soumission();
                s.setId(rs.getInt("id"));
                s.setEvaluationId(rs.getInt("evaluation_id"));
                s.setIdEtudiant(rs.getString("id_etudiant"));
                s.setFichierSoumissionUrl(rs.getString("fichier_soumission_url"));
                s.setCommentaireEtudiant(rs.getString("commentaire_etudiant"));
                s.setDateSoumission(rs.getTimestamp("date_soumission").toLocalDateTime());
                s.setStatut(rs.getString("statut"));
                s.setPdfFilename(rs.getString("pdf_filename"));
                soumissions.add(s);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return soumissions;
    }
}