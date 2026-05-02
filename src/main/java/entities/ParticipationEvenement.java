package entities;

import java.time.LocalDateTime;

public class ParticipationEvenement {

    private Integer id;
    private Integer evenementId;
    private Integer utilisateurId;
    private String statut;
    private LocalDateTime dateInscription;
    private LocalDateTime heureArrivee;
    private LocalDateTime heureDepart;
    private Integer feedbackNote;
    private String feedbackCommentaire;

    public ParticipationEvenement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(Integer evenementId) {
        this.evenementId = evenementId;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public LocalDateTime getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(LocalDateTime heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public LocalDateTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalDateTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Integer getFeedbackNote() {
        return feedbackNote;
    }

    public void setFeedbackNote(Integer feedbackNote) {
        this.feedbackNote = feedbackNote;
    }

    public String getFeedbackCommentaire() {
        return feedbackCommentaire;
    }

    public void setFeedbackCommentaire(String feedbackCommentaire) {
        this.feedbackCommentaire = feedbackCommentaire;
    }
}
