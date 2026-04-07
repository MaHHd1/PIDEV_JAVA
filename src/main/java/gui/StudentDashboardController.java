package gui;

import entities.Etudiant;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label currentUserNameLabel;

    @FXML
    private Label currentUserRoleLabel;

    @FXML
    private MenuButton profileMenuButton;

    @FXML
    private ListView<String> profileList;

    @FXML
    private ListView<String> focusList;

    private final ObservableList<String> profileItems = FXCollections.observableArrayList();
    private final ObservableList<String> focusItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        profileList.setItems(profileItems);
        focusList.setItems(focusItems);

        Utilisateur utilisateur = UserSession.getCurrentUser();
        Etudiant etudiant = utilisateur instanceof Etudiant ? (Etudiant) utilisateur : buildDemoStudent();
        welcomeLabel.setText("Hello, " + etudiant.getPrenom());
        roleLabel.setText("Student dashboard");
        currentUserNameLabel.setText(etudiant.getNomComplet());
        currentUserRoleLabel.setText(etudiant.getType());
        profileMenuButton.setText(buildInitials(etudiant));

        profileItems.setAll(
                "Name: " + etudiant.getNomComplet(),
                "Email: " + etudiant.getEmail(),
                "Matricule: " + etudiant.getMatricule(),
                "Level: " + etudiant.getNiveauEtude(),
                "Specialisation: " + etudiant.getSpecialisation(),
                "Phone: " + etudiant.getTelephone(),
                "Status: " + etudiant.getStatut(),
                "Registration: " + etudiant.getDateInscription()
        );

        focusItems.setAll(
                "POO Java - chapter 4 completed",
                "Base de donnees - practical work due Friday",
                "Web services - project sprint in progress",
                "UML - review sequence diagrams before next lab",
                "Prepare for upcoming platform modules"
        );
    }

    @FXML
    private void handleProfileLogout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    private String buildInitials(Utilisateur utilisateur) {
        String prenom = utilisateur.getPrenom() != null && !utilisateur.getPrenom().isBlank()
                ? utilisateur.getPrenom().substring(0, 1).toUpperCase() : "";
        String nom = utilisateur.getNom() != null && !utilisateur.getNom().isBlank()
                ? utilisateur.getNom().substring(0, 1).toUpperCase() : "";
        return prenom + nom;
    }

    private Etudiant buildDemoStudent() {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(3001L);
        etudiant.setNom("Trabelsi");
        etudiant.setPrenom("Amine");
        etudiant.setEmail("amine.trabelsi@demo.tn");
        etudiant.setMatricule("ETU-GL-2401");
        etudiant.setNiveauEtude("Master 1");
        etudiant.setSpecialisation("Genie Logiciel");
        etudiant.setDateNaissance(LocalDate.of(2002, 5, 14));
        etudiant.setTelephone("22123456");
        etudiant.setAdresse("Tunis, Centre Urbain Nord");
        etudiant.setDateInscription(LocalDateTime.now().minusMonths(7));
        etudiant.setStatut("actif");
        return etudiant;
    }
}
