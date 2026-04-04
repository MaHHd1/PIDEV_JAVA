package com.mehdi.pidev.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Enseignant")
@UniqueConstraint(columnNames = {"matriculeEnseignant"}, name = "uk_enseignant_matricule")
public class Enseignant extends Utilisateur {

    @Column(name = "matriculeEnseignant", length = 50, unique = true, nullable = false)
    @NotBlank(message = "Le matricule enseignant est obligatoire.")
    @Size(min = 5, max = 50, message = "Le matricule doit contenir entre {min} et {max} caractères.")
    @Pattern(regexp = "^ENS-[A-Z0-9-]+$",
            message = "Le matricule doit commencer par ENS- suivi de lettres majuscules et chiffres.")
    private String matriculeEnseignant;

    @Column(name = "diplome", length = 100, nullable = false)
    @NotBlank(message = "Le diplôme est obligatoire.")
    @Pattern(regexp = "^(Licence|Master|Doctorat|HDR|Ingénieur)$",
            message = "Veuillez choisir un diplôme valide.")
    private String diplome;

    @Column(name = "specialite", length = 100, nullable = false)
    @NotBlank(message = "La spécialité est obligatoire.")
    @Size(min = 3, max = 100, message = "La spécialité doit contenir entre {min} et {max} caractères.")
    private String specialite;

    @Column(name = "anneesExperience", nullable = false)
    @NotNull(message = "Les années d'expérience sont obligatoires.")
    @Min(value = 0, message = "Les années d'expérience doivent être au minimum {value}")
    @Max(value = 50, message = "Les années d'expérience doivent être au maximum {value}")
    private Integer anneesExperience;

    @Column(name = "typeContrat", length = 50, nullable = false)
    @NotBlank(message = "Le type de contrat est obligatoire.")
    @Pattern(regexp = "^(CDI|CDD|Vacataire|Contractuel)$",
            message = "Veuillez choisir un type de contrat valide.")
    private String typeContrat;

    @Column(name = "tauxHoraire", precision = 10, scale = 2)
    @DecimalMin(value = "10.0", message = "Le taux horaire doit être au minimum {value} euros")
    @DecimalMax(value = "200.0", message = "Le taux horaire doit être au maximum {value} euros")
    private BigDecimal tauxHoraire;

    @Column(name = "disponibilites", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Les disponibilités ne peuvent pas dépasser {max} caractères.")
    private String disponibilites;

    @Column(name = "statut", length = 20, nullable = false)
    @Pattern(regexp = "^(actif|inactif|conge|retraite)$",
            message = "Statut invalide.")
    private String statut = "actif";

    // Relationships
    @ManyToMany(mappedBy = "enseignants")
    private List<Cours> cours = new ArrayList<>();

    // Constructors
    public Enseignant() {
        super();
    }

    public Enseignant(String nom, String prenom, String email, String motDePasse,
                      String matriculeEnseignant, String diplome, String specialite,
                      Integer anneesExperience, String typeContrat) {
        super(nom, prenom, email, motDePasse);
        this.matriculeEnseignant = matriculeEnseignant;
        this.diplome = diplome;
        this.specialite = specialite;
        this.anneesExperience = anneesExperience;
        this.typeContrat = typeContrat;
        this.statut = "actif";
    }

    // Getters and Setters
    public String getMatriculeEnseignant() {
        return matriculeEnseignant;
    }

    public void setMatriculeEnseignant(String matriculeEnseignant) {
        this.matriculeEnseignant = matriculeEnseignant;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public Integer getAnneesExperience() {
        return anneesExperience;
    }

    public void setAnneesExperience(Integer anneesExperience) {
        this.anneesExperience = anneesExperience;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public BigDecimal getTauxHoraire() {
        return tauxHoraire;
    }

    public void setTauxHoraire(BigDecimal tauxHoraire) {
        this.tauxHoraire = tauxHoraire;
    }

    public String getDisponibilites() {
        return disponibilites;
    }

    public void setDisponibilites(String disponibilites) {
        this.disponibilites = disponibilites;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }

    // Helper Methods
    public void addCours(Cours cours) {
        if (!this.cours.contains(cours)) {
            this.cours.add(cours);
            cours.addEnseignant(this);
        }
    }

    public void removeCours(Cours cours) {
        if (this.cours.remove(cours)) {
            cours.removeEnseignant(this);
        }
    }

    @Override
    public String getType() {
        return "enseignant";
    }

    // Business logic methods
    public boolean isVacataire() {
        return "Vacataire".equals(this.typeContrat);
    }

    public boolean isActif() {
        return "actif".equals(this.statut);
    }

    public String getDisplayName() {
        return getNomComplet() + " (" + matriculeEnseignant + ")";
    }

    @Override
    public String toString() {
        return "Enseignant{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", matriculeEnseignant='" + matriculeEnseignant + '\'' +
                ", diplome='" + diplome + '\'' +
                ", specialite='" + specialite + '\'' +
                ", anneesExperience=" + anneesExperience +
                ", typeContrat='" + typeContrat + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}