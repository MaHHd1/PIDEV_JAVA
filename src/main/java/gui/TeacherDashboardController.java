package gui;

import entities.Enseignant;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;

public class TeacherDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

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
    private void logout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }
}
