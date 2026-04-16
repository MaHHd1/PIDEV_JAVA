package services;

import entities.Soumission;
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

public class SoumissionService {
    private final Connection conn;

    public SoumissionService() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public void add(Soumission soumission) {
        if (soumission.getDateSoumission() == null) {
            soumission.setDateSoumission(LocalDateTime.now());
        }

        String sql = "INSERT INTO soumission (evaluation_id, id_etudiant, fichier_soumission_url, commentaire_etudiant, date_soumission, statut, pdf_filename) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void update(Soumission soumission) {
        String sql = "UPDATE soumission SET evaluation_id = ?, id_etudiant = ?, fichier_soumission_url = ?, commentaire_etudiant = ?, date_soumission = ?, statut = ?, pdf_filename = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void delete(Soumission soumission) {
        String sql = "DELETE FROM soumission WHERE id = " + soumission.getId();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Soumission getById(int id) {
        String sql = "SELECT * FROM soumission WHERE id = " + id;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                return null;
            }

            Soumission soumission = new Soumission();
            soumission.setId(rs.getInt("id"));
            soumission.setEvaluationId(rs.getInt("evaluation_id"));
            soumission.setIdEtudiant(rs.getString("id_etudiant"));
            soumission.setFichierSoumissionUrl(rs.getString("fichier_soumission_url"));
            soumission.setCommentaireEtudiant(rs.getString("commentaire_etudiant"));
            soumission.setDateSoumission(rs.getTimestamp("date_soumission").toLocalDateTime());
            soumission.setStatut(rs.getString("statut"));
            soumission.setPdfFilename(rs.getString("pdf_filename"));
            return soumission;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Soumission> getAll() {
        String sql = "SELECT * FROM soumission";
        List<Soumission> soumissions = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Soumission soumission = new Soumission();
                soumission.setId(rs.getInt("id"));
                soumission.setEvaluationId(rs.getInt("evaluation_id"));
                soumission.setIdEtudiant(rs.getString("id_etudiant"));
                soumission.setFichierSoumissionUrl(rs.getString("fichier_soumission_url"));
                soumission.setCommentaireEtudiant(rs.getString("commentaire_etudiant"));
                soumission.setDateSoumission(rs.getTimestamp("date_soumission").toLocalDateTime());
                soumission.setStatut(rs.getString("statut"));
                soumission.setPdfFilename(rs.getString("pdf_filename"));
                soumissions.add(soumission);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return soumissions;
    }
}
