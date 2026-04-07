package gui;

import entities.Enseignant;
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

public class TeacherDashboardController {

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
    private ListView<String> teachingList;

    private final ObservableList<String> profileItems = FXCollections.observableArrayList();
    private final ObservableList<String> teachingItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        profileList.setItems(profileItems);
        teachingList.setItems(teachingItems);

        Utilisateur utilisateur = UserSession.getCurrentUser();
        if (utilisateur instanceof Enseignant) {
            Enseignant enseignant = (Enseignant) utilisateur;
            welcomeLabel.setText("Hello, " + enseignant.getPrenom());
            roleLabel.setText("Teacher dashboard");
            currentUserNameLabel.setText(enseignant.getNomComplet());
            currentUserRoleLabel.setText(enseignant.getType());
            profileMenuButton.setText(buildInitials(enseignant));

            profileItems.setAll(
                    "Name: " + enseignant.getNomComplet(),
                    "Email: " + enseignant.getEmail(),
                    "Teacher ID: " + enseignant.getMatriculeEnseignant(),
                    "Diploma: " + enseignant.getDiplome(),
                    "Speciality: " + enseignant.getSpecialite(),
                    "Contract: " + enseignant.getTypeContrat()
            );
        }

        teachingItems.setAll(
                "Review teaching availability",
                "Prepare your active course material",
                "Track upcoming student interactions",
                "Refresh academic content and announcements"
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
