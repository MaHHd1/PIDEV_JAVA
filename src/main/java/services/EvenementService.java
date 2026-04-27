package services;

import entities.Evenement;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {

    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    @Override
    public void create(Evenement evenement) throws SQLException {
        String sql = "INSERT INTO evenement (createur_id, titre, description, type_evenement, date_debut, date_fin, lieu, capacite_max, statut, visibilite) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(ps, evenement);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    evenement.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Evenement evenement) throws SQLException {
        if (evenement.getId() == null) {
            throw new SQLException("Missing evenement id for update.");
        }
        String sql = "UPDATE evenement SET createur_id = ?, titre = ?, description = ?, type_evenement = ?, date_debut = ?, date_fin = ?, lieu = ?, capacite_max = ?, statut = ?, visibilite = ? "
                + "WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillStatement(ps, evenement);
            ps.setInt(11, evenement.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM evenement WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Evenement getById(int id) throws SQLException {
        String sql = "SELECT * FROM evenement WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEvenement(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Evenement> getAll() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenement ORDER BY date_debut DESC, id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                evenements.add(mapEvenement(rs));
            }
        }
        return evenements;
    }

    private void fillStatement(PreparedStatement ps, Evenement evenement) throws SQLException {
        if (evenement.getCreateurId() != null) {
            ps.setInt(1, evenement.getCreateurId());
        } else {
            ps.setNull(1, java.sql.Types.INTEGER);
        }
        ps.setString(2, evenement.getTitre());
        ps.setString(3, evenement.getDescription());
        ps.setString(4, evenement.getTypeEvenement());
        ps.setTimestamp(5, evenement.getDateDebut() != null ? Timestamp.valueOf(evenement.getDateDebut()) : null);
        ps.setTimestamp(6, evenement.getDateFin() != null ? Timestamp.valueOf(evenement.getDateFin()) : null);
        ps.setString(7, evenement.getLieu());
        if (evenement.getCapaciteMax() != null) {
            ps.setInt(8, evenement.getCapaciteMax());
        } else {
            ps.setNull(8, java.sql.Types.INTEGER);
        }
        ps.setString(9, evenement.getStatut());
        ps.setString(10, evenement.getVisibilite());
    }

    private Evenement mapEvenement(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setId(rs.getInt("id"));

        int createurId = rs.getInt("createur_id");
        if (!rs.wasNull()) {
            evenement.setCreateurId(createurId);
        }

        evenement.setTitre(rs.getString("titre"));
        evenement.setDescription(rs.getString("description"));
        evenement.setTypeEvenement(rs.getString("type_evenement"));

        Timestamp dateDebut = rs.getTimestamp("date_debut");
        if (dateDebut != null) {
            evenement.setDateDebut(dateDebut.toLocalDateTime());
        }

        Timestamp dateFin = rs.getTimestamp("date_fin");
        if (dateFin != null) {
            evenement.setDateFin(dateFin.toLocalDateTime());
        }

        evenement.setLieu(rs.getString("lieu"));

        int capacite = rs.getInt("capacite_max");
        if (!rs.wasNull()) {
            evenement.setCapaciteMax(capacite);
        }

        evenement.setStatut(rs.getString("statut"));
        evenement.setVisibilite(rs.getString("visibilite"));
        return evenement;
    }
}
