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

public class SidebarEnseignantController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Button profileBtn;

    @FXML
    private Button mesEvaluationsBtn;

    @FXML
    private Button mesCorrectionsBtn;

    @FXML
    private Button studentProfilesBtn;

    @FXML
    private Button changePasswordBtn;

    @FXML
    private Button coursesBtn;

    @FXML
    private Button quizBtn;

    @FXML
    private Button newQuizBtn;

    @FXML
    private Button deconnexionBtn;

    private String teacherId = "PROF001";
    private String teacherName = "Enseignant";
    private MainLayoutEnseignantController mainController;

    @FXML
    public void initialize() {
        userNameLabel.setText(teacherName);
        userRoleLabel.setText(teacherId);
    }

    public void setTeacherInfo(String id, String name) {
        teacherId = id;
        teacherName = name;
        userNameLabel.setText(name);
        userRoleLabel.setText(id);
    }

    public void setMainController(MainLayoutEnseignantController controller) {
        mainController = controller;
    }

    @FXML
    private void handleProfileHub() {
        try {
            DashboardNavigation.openTeacherSection(DashboardNavigation.TeacherSection.PROFILE);
            SceneManager.switchScene("/teacher-dashboard.fxml", "Teacher Profile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStudentProfiles() {
        try {
            DashboardNavigation.openTeacherSection(DashboardNavigation.TeacherSection.STUDENTS);
            SceneManager.switchScene("/teacher-dashboard.fxml", "Teacher Students");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            DashboardNavigation.openTeacherSection(DashboardNavigation.TeacherSection.CHANGE_PASSWORD);
            SceneManager.switchScene("/teacher-dashboard.fxml", "Teacher Security");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCourseManagement() {
        if (mainController != null) {
            mainController.showCourseManagement();
        }
    }

    @FXML
    private void handleMesEvaluations() {
        if (mainController != null) {
            mainController.showMesEvaluations();
        }
    }

    @FXML
    private void handleMesCorrections() {
        if (mainController != null) {
            mainController.showMesCorrections();
        }
    }

    @FXML
    private void handleQuizHub() {
        try {
            QuizNavigation.openTeacherSection(QuizNavigation.TeacherSection.LIST);
            SceneManager.switchScene("/teacher-quiz.fxml", "Teacher Quiz");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewQuizHub() {
        try {
            QuizNavigation.openTeacherSection(QuizNavigation.TeacherSection.CREATE);
            SceneManager.switchScene("/teacher-quiz.fxml", "Teacher Quiz");
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
            case "evaluations":
                activeBtn = mesEvaluationsBtn;
                break;
            case "corrections":
                activeBtn = mesCorrectionsBtn;
                break;
            case "profile":
                activeBtn = profileBtn;
                break;
            case "students":
                activeBtn = studentProfilesBtn;
                break;
            case "security":
                activeBtn = changePasswordBtn;
                break;
            case "courses":
                activeBtn = coursesBtn;
                break;
            case "quiz":
                activeBtn = quizBtn;
                break;
            case "new_quiz":
                activeBtn = newQuizBtn;
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
        if (studentProfilesBtn != null) {
            studentProfilesBtn.getStyleClass().remove("sidebar-button-active");
        }
        if (changePasswordBtn != null) {
            changePasswordBtn.getStyleClass().remove("sidebar-button-active");
        }
        if (coursesBtn != null) {
            coursesBtn.getStyleClass().remove("sidebar-button-active");
        }
        mesEvaluationsBtn.getStyleClass().remove("sidebar-button-active");
        mesCorrectionsBtn.getStyleClass().remove("sidebar-button-active");
        if (quizBtn != null) {
            quizBtn.getStyleClass().remove("sidebar-button-active");
        }
        if (newQuizBtn != null) {
            newQuizBtn.getStyleClass().remove("sidebar-button-active");
        }
    }
}

