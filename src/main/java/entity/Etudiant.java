package com.mehdi.pidev.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Etudiant")
@UniqueConstraint(columnNames = {"matricule"}, name = "uk_etudiant_matricule")
public class Etudiant extends Utilisateur {

    @Column(name = "matricule", length = 50, unique = true, nullable = false)
    @NotBlank(message = "Le matricule est obligatoire.")
    @Size(min = 5, max = 50, message = "Le matricule doit contenir entre {min} et {max} caractères.")
    @Pattern(regexp = "^[A-Z0-9-]+$",
            message = "Le matricule ne peut contenir que des lettres majuscules, chiffres et tirets.")
    private String matricule;

    @Column(name = "niveauEtude", length = 100, nullable = false)
    @NotBlank(message = "Le niveau d'étude est obligatoire.")
    @Pattern(regexp = "^(Licence 1|Licence 2|Licence 3|Master 1|Master 2|Doctorat)$",
            message = "Veuillez choisir un niveau d'étude valide.")
    private String niveauEtude;

    @Column(name = "specialisation", length = 100, nullable = false)
    @NotBlank(message = "La spécialisation est obligatoire.")
    @Size(min = 2, max = 100, message = "La spécialisation doit contenir au moins {min} caractères.")
    private String specialisation;

    @Column(name = "dateNaissance", nullable = false)
    @NotNull(message = "La date de naissance est obligatoire.")
    @Past(message = "La date de naissance doit être dans le passé.")
    private LocalDate dateNaissance;

    @Column(name = "telephone", length = 20, nullable = false)
    @NotBlank(message = "Le téléphone est obligatoire.")
    @Pattern(regexp = "^(\\+?[0-9]{1,3})?[0-9]{8,15}$",
            message = "Le numéro de téléphone n'est pas valide.")
    private String telephone;

    @Column(name = "adresse", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "L'adresse est obligatoire.")
    @Size(min = 10, max = 500, message = "L'adresse doit contenir entre {min} et {max} caractères.")
    private String adresse;

    @Column(name = "dateInscription", nullable = false)
    private LocalDateTime dateInscription;

    @Column(name = "statut", length = 20, nullable = false)
    @Pattern(regexp = "^(actif|inactif|diplome|suspendu)$",
            message = "Statut invalide.")
    private String statut = "actif";

    // Relationships
    @ManyToMany
    @JoinTable(
            name = "etudiant_cours",
            joinColumns = @JoinColumn(name = "etudiant_id"),
            inverseJoinColumns = @JoinColumn(name = "cours_id")
    )
    private List<Cours> coursInscrits = new ArrayList<>();

    // Constructors
    public Etudiant() {
        super();
        this.dateInscription = LocalDateTime.now();
    }

    public Etudiant(String nom, String prenom, String email, String motDePasse,
                    String matricule, String niveauEtude, String specialisation,
                    LocalDate dateNaissance, String telephone, String adresse) {
        super(nom, prenom, email, motDePasse);
        this.matricule = matricule;
        this.niveauEtude = niveauEtude;
        this.specialisation = specialisation;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.adresse = adresse;
        this.dateInscription = LocalDateTime.now();
        this.statut = "actif";
    }

    // Getters and Setters
    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(String niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<Cours> getCoursInscrits() {
        return coursInscrits;
    }

    public void setCoursInscrits(List<Cours> coursInscrits) {
        this.coursInscrits = coursInscrits;
    }

    // Helper Methods
    public int getAge() {
        if (dateNaissance == null) {
            return 0;
        }
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public void addCoursInscrit(Cours cours) {
        if (!coursInscrits.contains(cours)) {
            coursInscrits.add(cours);
            cours.getEtudiants().add(this);
        }
    }

    public void removeCoursInscrit(Cours cours) {
        coursInscrits.remove(cours);
        cours.getEtudiants().remove(this);
    }

    public boolean isInscritAuCours(Cours cours) {
        return coursInscrits.contains(cours);
    }

    public boolean isActif() {
        return "actif".equals(this.statut);
    }

    public boolean isDiplome() {
        return "diplome".equals(this.statut);
    }

    public boolean isSuspendu() {
        return "suspendu".equals(this.statut);
    }

    public String getNiveauEtudeAbrege() {
        switch (niveauEtude) {
            case "Licence 1": return "L1";
            case "Licence 2": return "L2";
            case "Licence 3": return "L3";
            case "Master 1": return "M1";
            case "Master 2": return "M2";
            case "Doctorat": return "PhD";
            default: return niveauEtude;
        }
    }

    @Override
    public String getType() {
        return "etudiant";
    }

    public String getDisplayName() {
        return getNomComplet() + " (" + matricule + ") - " + niveauEtude;
    }

    @Override
    public String toString() {
        return "Etudiant{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", matricule='" + matricule + '\'' +
                ", niveauEtude='" + niveauEtude + '\'' +
                ", specialisation='" + specialisation + '\'' +
                ", telephone='" + telephone + '\'' +
                ", statut='" + statut + '\'' +
                ", age=" + getAge() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etudiant)) return false;
        if (!super.equals(o)) return false;
        Etudiant etudiant = (Etudiant) o;
        return matricule != null && matricule.equals(etudiant.matricule);
    }

    @Override
    public int hashCode() {
        return matricule != null ? matricule.hashCode() : super.hashCode();
    }
}