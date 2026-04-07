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
import java.math.BigDecimal;

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
        Enseignant enseignant = utilisateur instanceof Enseignant ? (Enseignant) utilisateur : buildDemoTeacher();
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
                "Contract: " + enseignant.getTypeContrat(),
                "Experience: " + (enseignant.getAnneesExperience() != null ? enseignant.getAnneesExperience() : 0) + " years",
                "Hourly rate: " + (enseignant.getTauxHoraire() != null ? enseignant.getTauxHoraire() : BigDecimal.ZERO) + " TND",
                "Status: " + enseignant.getStatut()
        );

        teachingItems.setAll(
                "Java avancé - 24 students - next session Tuesday 10:00",
                "Spring Boot APIs - content review pending",
                "Correct quiz results for module Integration Continue",
                "Publish new PDF support for UML modelling",
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

    private Enseignant buildDemoTeacher() {
        Enseignant enseignant = new Enseignant();
        enseignant.setId(2001L);
        enseignant.setNom("Ben Salem");
        enseignant.setPrenom("Yasmine");
        enseignant.setEmail("yasmine.bensalem@demo.tn");
        enseignant.setMatriculeEnseignant("ENS-JAVA-24");
        enseignant.setDiplome("Doctorat");
        enseignant.setSpecialite("Genie Logiciel");
        enseignant.setTypeContrat("CDI");
        enseignant.setAnneesExperience(8);
        enseignant.setTauxHoraire(new BigDecimal("95.00"));
        enseignant.setDisponibilites("Lundi au jeudi 08:00-16:00");
        enseignant.setStatut("actif");
        return enseignant;
    }
}
