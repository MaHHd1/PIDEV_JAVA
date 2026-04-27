package gui;

import entities.Cours;
import entities.Etudiant;
import entities.Utilisateur;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import services.ContenuService;
import services.CoursService;
import utils.UserSession;

import java.sql.SQLException;
import java.util.List;

public class StudentMyCoursesController implements MainControllerAwareEtudiant {

    @FXML private TableView<Cours> coursesTable;
    @FXML private TableColumn<Cours, String> codeColumn;
    @FXML private TableColumn<Cours, String> titleColumn;
    @FXML private TableColumn<Cours, String> moduleColumn;
    @FXML private TableColumn<Cours, String> levelColumn;
    @FXML private TableColumn<Cours, String> statusColumn;
    @FXML private TableColumn<Cours, Number> contentsColumn;
    @FXML private Label messageLabel;
    @FXML private Label detailTitleLabel;
    @FXML private Label detailCodeLabel;
    @FXML private Label detailModuleLabel;
    @FXML private Label detailDescriptionLabel;
    @FXML private Label detailMetaLabel;

    private final CoursService coursService = new CoursService();
    private final ContenuService contenuService = new ContenuService();
    private MainLayoutEtudiantController mainController;

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodeCours()));
        titleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitre()));
        moduleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getModule() != null ? cell.getValue().getModule().getTitreModule() : "-"));
        levelColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNiveau()));
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        contentsColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(contentCount(cell.getValue())));
        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateDetails(newValue));
        loadCourses();
    }

    private void loadCourses() {
        Utilisateur utilisateur = UserSession.getCurrentUser();
        if (!(utilisateur instanceof Etudiant etudiant) || etudiant.getId() == null) {
            messageLabel.setText("Session etudiant introuvable.");
            return;
        }
        try {
            List<Cours> courses = coursService.getByStudentId(etudiant.getId());
            coursesTable.setItems(FXCollections.observableArrayList(courses));
            messageLabel.setText(courses.isEmpty() ? "Aucun cours inscrit pour le moment." : courses.size() + " cours inscrits.");
            if (!courses.isEmpty()) {
                coursesTable.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            messageLabel.setText("Chargement impossible: " + e.getMessage());
        }
    }

    private int contentCount(Cours cours) {
        try {
            return cours.getId() == null ? 0 : contenuService.getByCoursId(cours.getId()).size();
        } catch (SQLException e) {
            return 0;
        }
    }

    private void updateDetails(Cours cours) {
        if (cours == null) {
            detailTitleLabel.setText("Aucun cours selectionne");
            detailCodeLabel.setText("-");
            detailModuleLabel.setText("-");
            detailDescriptionLabel.setText("-");
            detailMetaLabel.setText("-");
            return;
        }
        detailTitleLabel.setText(cours.getTitre());
        detailCodeLabel.setText(cours.getCodeCours());
        detailModuleLabel.setText(cours.getModule() != null ? cours.getModule().getTitreModule() : "-");
        detailDescriptionLabel.setText(cours.getDescription() == null || cours.getDescription().isBlank() ? "Aucune description." : cours.getDescription());
        detailMetaLabel.setText("Niveau: " + valueOrDash(cours.getNiveau()) + " | Statut: " + valueOrDash(cours.getStatut()) + " | Contenus: " + contentCount(cours));
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    @FXML
    private void handleOpenCatalog() {
        if (mainController != null) {
            mainController.showAllCourses();
        }
    }

    @Override
    public void setMainController(MainLayoutEtudiantController controller) {
        mainController = controller;
    }
}
