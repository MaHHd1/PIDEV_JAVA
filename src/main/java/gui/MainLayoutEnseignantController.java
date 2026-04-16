package gui;

import entities.Enseignant;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import entities.Evaluation;
import entities.Score;
import entities.Soumission;
import utils.UserSession;

import java.io.IOException;

/**
 * Interface pour les contrôleurs qui ont besoin d'accéder au MainLayoutEnseignantController
 */
interface MainControllerAware {
    void setMainController(MainLayoutEnseignantController controller);
}

/**
 * Contrôleur pour le layout principal avec sidebar (enseignant)
 * Gère le chargement des vues dans la zone de contenu
 */
public class MainLayoutEnseignantController {

    @FXML
    private StackPane contentPane;

    @FXML
    private SidebarEnseignantController sidebarController;

    private String teacherId = "PROF001";
    private String teacherName = "Enseignant";

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser instanceof Enseignant enseignant) {
            teacherId = enseignant.getMatriculeEnseignant();
            teacherName = enseignant.getNomComplet();
        }

        // Configurer la sidebar avec les infos de l'enseignant
        if (sidebarController != null) {
            sidebarController.setTeacherInfo(teacherId, teacherName);
            sidebarController.setMainController(this);
        }

        // Charger la vue par défaut (Mes Évaluations)
        loadContent("/evaluation-list.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("evaluations");
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
            if (controller instanceof MainControllerAware) {
                ((MainControllerAware) controller).setMainController(this);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue de création d'évaluation
     */
    public void showEvaluationCreate() {
        loadContent("/evaluation-create.fxml");
    }

    /**
     * Charge la vue de détail d'une évaluation
     */
    public void showEvaluationDetail(Evaluation evaluation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/evaluation-detail.fxml"));
            Node content = loader.load();

            EvaluationDetailController controller = loader.getController();
            controller.setEvaluation(evaluation);
            controller.setMainController(this);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue détail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue d'édition d'une évaluation
     */
    public void showEvaluationEdit(Evaluation evaluation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/evaluation-edit.fxml"));
            Node content = loader.load();

            EvaluationEditController controller = loader.getController();
            controller.setEvaluation(evaluation);
            controller.setMainController(this);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue édition: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue des évaluations
     */
    public void showMesEvaluations() {
        loadContent("/evaluation-list.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("evaluations");
        }
    }

    /**
     * Charge la vue des corrections
     */
    public void showMesCorrections() {
        loadContent("/score-list.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("corrections");
        }
    }

    public void showCourseManagement() {
        loadContent("/course-management.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("courses");
        }
    }

    public void showScoreCreate(Soumission soumission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-create.fxml"));
            Node content = loader.load();

            ScoreCreateController controller = loader.getController();
            controller.setTeacherId(teacherId);
            controller.setSoumission(soumission);
            controller.setMainController(this);

            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la creation de correction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showScoreDetail(Score score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-detail.fxml"));
            Node content = loader.load();

            ScoreDetailController controller = loader.getController();
            controller.setScore(score);
            controller.setMainController(this);

            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du detail de correction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showScoreEdit(Score score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-edit.fxml"));
            Node content = loader.load();

            ScoreEditController controller = loader.getController();
            controller.setScore(score);
            controller.setMainController(this);

            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'edition de correction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setTeacherInfo(String id, String name) {
        this.teacherId = id;
        this.teacherName = name;
        if (sidebarController != null) {
            sidebarController.setTeacherInfo(id, name);
        }
    }
}

