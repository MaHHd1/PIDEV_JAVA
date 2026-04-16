package org.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
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
    private Button mesSoumissionsBtn;

    @FXML
    private Button mesNotesBtn;

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
    private void handleDeconnexion() {
        try {
            UserSession.clear();
            SceneManager.switchScene("/gui/login.fxml", "Campus Access");
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
            default:
                break;
        }

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("sidebar-button-active");
        }
    }

    private void resetButtonStyles() {
        mesSoumissionsBtn.getStyleClass().remove("sidebar-button-active");
        mesNotesBtn.getStyleClass().remove("sidebar-button-active");
    }
}
