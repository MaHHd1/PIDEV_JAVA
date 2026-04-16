package org.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
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
    private Button mesEvaluationsBtn;

    @FXML
    private Button mesCorrectionsBtn;

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
            default:
                break;
        }

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("sidebar-button-active");
        }
    }

    private void resetButtonStyles() {
        mesEvaluationsBtn.getStyleClass().remove("sidebar-button-active");
        mesCorrectionsBtn.getStyleClass().remove("sidebar-button-active");
    }
}
