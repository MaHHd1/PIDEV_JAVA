package gui;

import entities.Etudiant;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import entities.Score;
import entities.Soumission;
import utils.UserSession;

import java.io.IOException;

/**
 * Interface pour les contrôleurs étudiants qui ont besoin d'accéder au MainLayoutEtudiantController
 */
interface MainControllerAwareEtudiant {
    void setMainController(MainLayoutEtudiantController controller);
}

/**
 * Contrôleur pour le layout principal avec sidebar (étudiant)
 * Gère le chargement des vues dans la zone de contenu
 */
public class MainLayoutEtudiantController {

    @FXML
    private StackPane contentPane;

    @FXML
    private SidebarEtudiantController sidebarController;

    private String studentId = "ETU001";
    private String studentName = "Étudiant";

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser instanceof Etudiant etudiant) {
            studentId = etudiant.getMatricule();
            studentName = etudiant.getNomComplet();
        }
        // Configurer la sidebar avec les infos de l'étudiant
        if (sidebarController != null) {
            sidebarController.setStudentInfo(studentId, studentName);
            sidebarController.setMainController(this);
        }

        // Charger la vue par défaut (Mes Soumissions)
        loadContent("/soumission-list.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("soumissions");
        }
    }

    /**
     * Charge une vue FXML dans la zone de contenu
     */
    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            // Vider et ajouter le nouveau contenu
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

            // Injecter le mainController si le contrôleur l'accepte
            Object controller = loader.getController();
            if (controller instanceof MainControllerAwareEtudiant) {
                ((MainControllerAwareEtudiant) controller).setMainController(this);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue de création de soumission
     */
    public void showSoumissionCreate() {
        loadContent("/soumission-create.fxml");
    }

    /**
     * Charge la vue de détail d'une soumission
     */
    public void showSoumissionDetail(Soumission soumission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/soumission-detail.fxml"));
            Node content = loader.load();

            SoumissionDetailController controller = loader.getController();
            controller.setSoumission(soumission);
            controller.setStudentId(studentId);
            controller.setMainController(this);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue détail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue d'édition d'une soumission
     */
    public void showSoumissionEdit(Soumission soumission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/soumission-edit.fxml"));
            Node content = loader.load();

            SoumissionEditController controller = loader.getController();
            controller.setSoumission(soumission);
            controller.setStudentId(studentId);
            controller.setMainController(this);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue édition: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue de détail d'une note
     */
    public void showStudentScoreDetail(Score score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-score-detail.fxml"));
            Node content = loader.load();

            StudentScoreDetailController controller = loader.getController();
            controller.setScore(score);
            controller.setStudentId(studentId);
            controller.setMainController(this);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue note: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge une vue spécifique pour les soumissions
     */
    public void showMesSoumissions() {
        loadContent("/soumission-list.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("soumissions");
        }
    }

    /**
     * Charge la vue des notes
     */
    public void showMesNotes() {
        loadContent("/student-score-list.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("notes");
        }
    }

    public void setStudentInfo(String id, String name) {
        this.studentId = id;
        this.studentName = name;
        if (sidebarController != null) {
            sidebarController.setStudentInfo(id, name);
        }
    }
}

