package entities;

import java.time.LocalDateTime;

public class Evenement {

    private Integer id;
    private Integer createurId;
    private String titre;
    private String description;
    private String typeEvenement;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private Integer capaciteMax;
    private String statut;
    private String visibilite;

    public Evenement() {
    }

    public Evenement(
            Integer id,
            Integer createurId,
            String titre,
            String description,
            String typeEvenement,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            String lieu,
            Integer capaciteMax,
            String statut,
            String visibilite
    ) {
        this.id = id;
        this.createurId = createurId;
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
        this.statut = statut;
        this.visibilite = visibilite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreateurId() {
        return createurId;
    }

    public void setCreateurId(Integer createurId) {
        this.createurId = createurId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(String typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getVisibilite() {
        return visibilite;
    }

    public void setVisibilite(String visibilite) {
        this.visibilite = visibilite;
    }
}
