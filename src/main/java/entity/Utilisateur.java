package com.mehdi.pidev.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "typeUtilisateur", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorMap({
        "etudiant", "com.mehdi.pidev.entity.Etudiant",
        "enseignant", "com.mehdi.pidev.entity.Enseignant",
        "administrateur", "com.mehdi.pidev.entity.Administrateur"
})
@UniqueConstraint(columnNames = {"email"}, name = "uk_utilisateur_email")
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", length = 100, nullable = false)
    @NotBlank(message = "Le nom est obligatoire.")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre {min} et {max} caractères.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\'-]+$", message = "Le nom contient des caractères invalides.")
    private String nom;

    @Column(name = "prenom", length = 100, nullable = false)
    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre {min} et {max} caractères.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\'-]+$", message = "Le prénom contient des caractères invalides.")
    private String prenom;

    @Column(name = "email", length = 180, unique = true, nullable = false)
    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email ${validatedValue} n'est pas valide.")
    private String email;

    @Column(name = "mot_de_passe", length = 255, nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins {min} caractères.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre.")
    private String motDePasse;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // Password Reset fields
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relationships
    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messagesEnvoyes = new ArrayList<>();

    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messagesRecus = new ArrayList<>();

    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evenement> evenementsCrees = new ArrayList<>();

    // Constructors
    public Utilisateur() {
        this.dateCreation = LocalDateTime.now();
    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiresAt() {
        return resetTokenExpiresAt;
    }

    public void setResetTokenExpiresAt(LocalDateTime resetTokenExpiresAt) {
        this.resetTokenExpiresAt = resetTokenExpiresAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Relationship Getters and Setters
    public List<Message> getMessagesEnvoyes() {
        return messagesEnvoyes;
    }

    public void setMessagesEnvoyes(List<Message> messagesEnvoyes) {
        this.messagesEnvoyes = messagesEnvoyes;
    }

    public List<Message> getMessagesRecus() {
        return messagesRecus;
    }

    public void setMessagesRecus(List<Message> messagesRecus) {
        this.messagesRecus = messagesRecus;
    }

    public List<Evenement> getEvenementsCrees() {
        return evenementsCrees;
    }

    public void setEvenementsCrees(List<Evenement> evenementsCrees) {
        this.evenementsCrees = evenementsCrees;
    }

    // Helper Methods
    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    public String getUsername() {
        return this.prenom + " " + this.nom;
    }

    public abstract String getType();

    public boolean isResetTokenValid() {
        return resetToken != null &&
                resetTokenExpiresAt != null &&
                resetTokenExpiresAt.isAfter(LocalDateTime.now());
    }

    // Messages Envoyés Management
    public void addMessageEnvoye(Message message) {
        if (!messagesEnvoyes.contains(message)) {
            messagesEnvoyes.add(message);
            message.setExpediteur(this);
        }
    }

    public void removeMessageEnvoye(Message message) {
        if (messagesEnvoyes.remove(message)) {
            if (message.getExpediteur() == this) {
                message.setExpediteur(null);
            }
        }
    }

    // Messages Reçus Management
    public void addMessageRecu(Message message) {
        if (!messagesRecus.contains(message)) {
            messagesRecus.add(message);
            message.setDestinataire(this);
        }
    }

    public void removeMessageRecu(Message message) {
        if (messagesRecus.remove(message)) {
            if (message.getDestinataire() == this) {
                message.setDestinataire(null);
            }
        }
    }

    // Événements Créés Management
    public void addEvenementCree(Evenement evenement) {
        if (!evenementsCrees.contains(evenement)) {
            evenementsCrees.add(evenement);
            evenement.setCreateur(this);
        }
    }

    public void removeEvenementCree(Evenement evenement) {
        if (evenementsCrees.remove(evenement)) {
            if (evenement.getCreateur() == this) {
                evenement.setCreateur(null);
            }
        }
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", dateCreation=" + dateCreation +
                ", type=" + getType() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}