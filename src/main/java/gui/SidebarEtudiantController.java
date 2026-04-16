package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import utils.DashboardNavigation;
import utils.QuizNavigation;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;

public class SidebarEtudiantController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Button profileBtn;

    @FXML
    private Button mesSoumissionsBtn;

    @FXML
    private Button mesNotesBtn;

    @FXML
    private Button changePasswordBtn;

    @FXML
    private Button quizBtn;

    @FXML
    private Button quizResultsBtn;

    @FXML
    private Button deconnexionBtn;

    private String studentId = "ETU001";
    private String studentName = "Etudiant";
    private MainLayoutEtudiantController mainController;

    @FXML
    public void initialize() {
        userNameLabel.setText(studentName);
        userRoleLabel.setText(studentId);
    }

    public void setStudentInfo(String id, String name) {
        studentId = id;
        studentName = name;
        userNameLabel.setText(name);
        userRoleLabel.setText(id);
    }

    public void setMainController(MainLayoutEtudiantController controller) {
        mainController = controller;
    }

    @FXML
    private void handleProfileHub() {
        try {
            DashboardNavigation.openStudentSection(DashboardNavigation.StudentSection.PROFILE);
            SceneManager.switchScene("/student-dashboard.fxml", "Student Profile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            DashboardNavigation.openStudentSection(DashboardNavigation.StudentSection.CHANGE_PASSWORD);
            SceneManager.switchScene("/student-dashboard.fxml", "Student Security");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMesSoumissions() {
        if (mainController != null) {
            mainController.showMesSoumissions();
        }
    }

    @FXML
    private void handleMesNotes() {
        if (mainController != null) {
            mainController.showMesNotes();
        }
    }

    @FXML
    private void handleQuizHub() {
        try {
            QuizNavigation.openStudentSection(QuizNavigation.StudentSection.LIST);
            SceneManager.switchScene("/student-quiz.fxml", "Student Quiz");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuizResultsHub() {
        try {
            QuizNavigation.openStudentSection(QuizNavigation.StudentSection.RESULTS);
            SceneManager.switchScene("/student-quiz.fxml", "Student Quiz Results");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeconnexion() {
        try {
            UserSession.clear();
        SceneManager.switchScene("/login.fxml", "Campus Access");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setActiveButton(String buttonId) {
        resetButtonStyles();

        Button activeBtn = null;
        switch (buttonId) {
            case "soumissions":
                activeBtn = mesSoumissionsBtn;
                break;
            case "notes":
                activeBtn = mesNotesBtn;
                break;
            case "profile":
                activeBtn = profileBtn;
                break;
            case "security":
                activeBtn = changePasswordBtn;
                break;
            case "quiz":
                activeBtn = quizBtn;
                break;
            case "quiz_results":
                activeBtn = quizResultsBtn;
                break;
            default:
                break;
        }

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("sidebar-button-active");
        }
    }

    private void resetButtonStyles() {
        if (profileBtn != null) {
            profileBtn.getStyleClass().remove("sidebar-button-active");
        }
        if (changePasswordBtn != null) {
            changePasswordBtn.getStyleClass().remove("sidebar-button-active");
        }
        mesSoumissionsBtn.getStyleClass().remove("sidebar-button-active");
        mesNotesBtn.getStyleClass().remove("sidebar-button-active");
        if (quizBtn != null) {
            quizBtn.getStyleClass().remove("sidebar-button-active");
        }
        if (quizResultsBtn != null) {
            quizResultsBtn.getStyleClass().remove("sidebar-button-active");
        }
    }
}

