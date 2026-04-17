package gui;

import entities.Cours;
import entities.Enseignant;
import entities.Evaluation;
import entities.Score;
import entities.Soumission;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import utils.DashboardNavigation;
import utils.QuizNavigation;
import utils.UserSession;

import java.io.IOException;

interface MainControllerAware {
    void setMainController(MainLayoutEnseignantController controller);
}

public class MainLayoutEnseignantController {

    @FXML
    private StackPane contentPane;

    @FXML
    private SidebarEnseignantController sidebarController;

    private String teacherId = "PROF001";
    private String teacherName = "Enseignant";
    private Cours editingCourse;

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser instanceof Enseignant enseignant) {
            teacherId = enseignant.getMatriculeEnseignant();
            teacherName = enseignant.getNomComplet();
        }

        if (sidebarController != null) {
            sidebarController.setTeacherInfo(teacherId, teacherName);
            sidebarController.setMainController(this);
        }

        loadTeacherOverview();
    }

    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentPane.getChildren().setAll(content);

            Object controller = loader.getController();
            if (controller instanceof MainControllerAware aware) {
                aware.setMainController(this);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadEmbeddedCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Node content = root;
            if (root instanceof BorderPane borderPane && borderPane.getCenter() != null) {
                content = borderPane.getCenter();
            }
            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement embarque de la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void loadTeacherOverview() {
        loadContent("/teacher-overview.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("overview");
        }
    }

    public void showTeacherCourses() {
        loadContent("/teacher-courses.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("courses");
        }
    }

    public void showTeacherStudents() {
        loadContent("/teacher-students.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("students");
        }
    }

    public void showTeacherCourseForm(Cours cours) {
        editingCourse = cours;
        loadContent("/teacher-course-form.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("new_course");
        }
    }

    public void showMesEvaluations() {
        loadContent("/evaluation-list.fxml");
    }

    public void showMesCorrections() {
        loadContent("/score-list.fxml");
    }

    public void showCourseManagement() {
        loadContent("/course-management.fxml");
    }

    public void showTeacherProfilePage() {
        DashboardNavigation.openTeacherSection(DashboardNavigation.TeacherSection.PROFILE);
        loadEmbeddedCenterContent("/teacher-dashboard.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("profile");
        }
    }

    public void showTeacherDashboardStudentsPage() {
        DashboardNavigation.openTeacherSection(DashboardNavigation.TeacherSection.STUDENTS);
        loadEmbeddedCenterContent("/teacher-dashboard.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("students_profile");
        }
    }

    public void showTeacherChangePasswordPage() {
        DashboardNavigation.openTeacherSection(DashboardNavigation.TeacherSection.CHANGE_PASSWORD);
        loadEmbeddedCenterContent("/teacher-dashboard.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("security");
        }
    }

    public void showTeacherQuizPage() {
        QuizNavigation.openTeacherSection(QuizNavigation.TeacherSection.LIST);
        loadEmbeddedCenterContent("/teacher-quiz.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("quiz");
        }
    }

    public void showTeacherNewQuizPage() {
        QuizNavigation.openTeacherSection(QuizNavigation.TeacherSection.CREATE);
        loadEmbeddedCenterContent("/teacher-quiz.fxml");
        if (sidebarController != null) {
            sidebarController.setActiveButton("new_quiz");
        }
    }

    public Cours consumeEditingCourse() {
        Cours cours = editingCourse;
        editingCourse = null;
        return cours;
    }

    public void showEvaluationCreate() {
        loadContent("/evaluation-create.fxml");
    }

    public void showEvaluationDetail(Evaluation evaluation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/evaluation-detail.fxml"));
            Node content = loader.load();

            EvaluationDetailController controller = loader.getController();
            controller.setEvaluation(evaluation);
            controller.setMainController(this);

            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showEvaluationEdit(Evaluation evaluation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/evaluation-edit.fxml"));
            Node content = loader.load();

            EvaluationEditController controller = loader.getController();
            controller.setEvaluation(evaluation);
            controller.setMainController(this);

            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue edition: " + e.getMessage());
            e.printStackTrace();
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
