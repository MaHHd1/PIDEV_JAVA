package org.example.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour la sidebar de navigation étudiant
 */
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
    private Button profilBtn;

    @FXML
    private Button deconnexionBtn;

    private String studentId = "ETU001";
    private String studentName = "Étudiant";

    private MainLayoutEtudiantController mainController;

    @FXML
    public void initialize() {
        // Initialiser les informations utilisateur
        userNameLabel.setText(studentName);
        userRoleLabel.setText(studentId);
    }

    public void setStudentInfo(String id, String name) {
        this.studentId = id;
        this.studentName = name;
        userNameLabel.setText(name);
        userRoleLabel.setText(id);
    }

    public void setMainController(MainLayoutEtudiantController controller) {
        this.mainController = controller;
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
    private void handleProfil() {
        // TODO: Charger la page de profil
        System.out.println("Profil - à implémenter");
    }

    @FXML
    private void handleDeconnexion() {
        // Retourner à la page de connexion ou fermer l'application
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/evaluation-list.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) deconnexionBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Évaluations - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une vue dans la zone de contenu principale
     * @deprecated Utiliser mainController.showMesSoumissions() ou mainController.showMesNotes()
     */
    @Deprecated
    private void loadView(String fxmlPath, String title) {
        // Cette méthode est obsolète, utiliser mainController à la place
    }

    /**
     * Met à jour le style du bouton actif
     */
    public void setActiveButton(String buttonId) {
        // Réinitialiser tous les boutons
        resetButtonStyles();

        // Mettre en surbrillance le bouton actif
        Button activeBtn = null;
        switch (buttonId) {
            case "soumissions":
                activeBtn = mesSoumissionsBtn;
                break;
            case "notes":
                activeBtn = mesNotesBtn;
                break;
        }

        if (activeBtn != null) {
            activeBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-pref-width: 220;");
        }
    }

    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-pref-width: 220;";
        mesSoumissionsBtn.setStyle(defaultStyle);
        mesNotesBtn.setStyle(defaultStyle);
    }
}
