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
        if (utilisateur instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) utilisateur;
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
                    "Status: " + etudiant.getStatut()
            );
        }

        focusItems.setAll(
                "Review your latest course activity",
                "Track your academic profile details",
                "Continue from your current specialization",
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
}
