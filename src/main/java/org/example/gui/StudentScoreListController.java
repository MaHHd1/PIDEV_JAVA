package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.entity.Evaluation;
import org.example.entity.Score;
import org.example.entity.Soumission;
import org.example.service.EvaluationService;
import org.example.service.ScoreService;
import org.example.service.SoumissionService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la page "Mes Notes" (vue étudiant)
 * Affiche uniquement les scores de l'étudiant connecté
 */
public class StudentScoreListController implements MainControllerAwareEtudiant {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortByComboBox;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    @FXML
    private TableView<ScoreWrapper> notesTableView;

    @FXML
    private TableColumn<ScoreWrapper, Integer> idColumn;

    @FXML
    private TableColumn<ScoreWrapper, String> evaluationColumn;

    @FXML
    private TableColumn<ScoreWrapper, String> typeColumn;

    @FXML
    private TableColumn<ScoreWrapper, String> noteColumn;

    @FXML
    private TableColumn<ScoreWrapper, String> pourcentageColumn;

    @FXML
    private TableColumn<ScoreWrapper, String> statutColumn;

    @FXML
    private TableColumn<ScoreWrapper, String> dateColumn;

    @FXML
    private TableColumn<ScoreWrapper, Void> actionsColumn;

    @FXML
    private Label paginationSummaryLabel;

    @FXML
    private Pagination pagination;

    private final ScoreService scoreService = new ScoreService();
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    private ObservableList<ScoreWrapper> scoreWrappers = FXCollections.observableArrayList();
    private ObservableList<ScoreWrapper> filteredWrappers = FXCollections.observableArrayList();

    // ID de l'étudiant connecté (à remplacer par la session utilisateur)
    private String currentStudentId = "ETU001";

    private MainLayoutEtudiantController mainController;

    // Classe wrapper pour combiner Score + Soumission + Evaluation
    public static class ScoreWrapper {
        private final Score score;
        private final Soumission soumission;
        private final Evaluation evaluation;

        public ScoreWrapper(Score score, Soumission soumission, Evaluation evaluation) {
            this.score = score;
            this.soumission = soumission;
            this.evaluation = evaluation;
        }

        public int getId() { return score.getId(); }
        public Score getScore() { return score; }
        public Soumission getSoumission() { return soumission; }
        public Evaluation getEvaluation() { return evaluation; }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupSearch();
        setupPagination();
        loadScores();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        evaluationColumn.setCellValueFactory(cellData -> {
            Evaluation eval = cellData.getValue().getEvaluation();
            return new javafx.beans.property.SimpleStringProperty(eval != null ? eval.getTitre() : "-");
        });

        typeColumn.setCellValueFactory(cellData -> {
            Evaluation eval = cellData.getValue().getEvaluation();
            return new javafx.beans.property.SimpleStringProperty(eval != null ? eval.getTypeEvaluation() : "-");
        });

        noteColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            return new javafx.beans.property.SimpleStringProperty(score.getNote() + "/" + score.getNoteSur());
        });

        pourcentageColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            if (score.getPourcentage() != null) {
                return new javafx.beans.property.SimpleStringProperty(score.getPourcentage() + "%");
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        statutColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            String statut = score.getStatutCorrection();
            return new javafx.beans.property.SimpleStringProperty(
                "corrigé".equals(statut) ? "Corrigé" : "En attente"
            );
        });

        // Style pour le statut
        statutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Corrigé".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });

        dateColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            if (score.getDateCorrection() != null) {
                return new javafx.beans.property.SimpleStringProperty(score.getDateCorrection().toLocalDate().toString());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        // Colonne Actions - seulement "Voir"
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button voirBtn = new Button("Voir");

            {
                voirBtn.setOnAction(event -> {
                    ScoreWrapper wrapper = getTableView().getItems().get(getIndex());
                    handleVoir(wrapper);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(voirBtn);
                }
            }
        });
    }

    private void setupComboBoxes() {
        sortByComboBox.setItems(FXCollections.observableArrayList("Date", "Évaluation", "Type", "Note"));
        sortByComboBox.getSelectionModel().selectFirst();

        sortOrderComboBox.setItems(FXCollections.observableArrayList("Croissant", "Décroissant"));
        sortOrderComboBox.getSelectionModel().selectFirst();

        pageSizeComboBox.setItems(FXCollections.observableArrayList(10, 20, 50));
        pageSizeComboBox.getSelectionModel().selectFirst();

        sortByComboBox.setOnAction(e -> applyFilters());
        sortOrderComboBox.setOnAction(e -> applyFilters());
        pageSizeComboBox.setOnAction(e -> {
            pagination.setCurrentPageIndex(0);
            applyFilters();
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            pagination.setCurrentPageIndex(0);
            applyFilters();
        });
    }

    private void setupPagination() {
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void loadScores() {
        // Récupérer toutes les soumissions de l'étudiant
        List<Soumission> studentSoumissions = soumissionService.getAll().stream()
            .filter(s -> currentStudentId.equals(s.getIdEtudiant()))
            .collect(Collectors.toList());

        // Récupérer les scores associés
        List<Evaluation> allEvaluations = evaluationService.getAll();

        List<ScoreWrapper> wrappers = studentSoumissions.stream()
            .map(s -> {
                Score score = scoreService.getBySoumissionId(s.getId());
                Evaluation eval = allEvaluations.stream()
                    .filter(e -> e.getId() == s.getEvaluationId())
                    .findFirst()
                    .orElse(null);
                return new ScoreWrapper(score, s, eval);
            })
            .filter(w -> w.getScore() != null) // Uniquement les soumissions corrigées
            .collect(Collectors.toList());

        scoreWrappers.setAll(wrappers);

        if (wrappers.isEmpty()) {
            showEmptyMessage();
        } else {
            applyFilters();
        }
    }

    private void showEmptyMessage() {
        notesTableView.setPlaceholder(new Label("Aucune note disponible pour le moment.\nVos corrections apparaîtront ici une fois que l'enseignant aura corrigé vos soumissions."));
        paginationSummaryLabel.setText("0 note");
        pagination.setPageCount(1);
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        List<ScoreWrapper> filtered = scoreWrappers.stream()
            .filter(w -> {
                String evalName = w.getEvaluation() != null ? w.getEvaluation().getTitre().toLowerCase() : "";
                String type = w.getEvaluation() != null ? w.getEvaluation().getTypeEvaluation().toLowerCase() : "";
                return evalName.contains(search) || type.contains(search);
            })
            .collect(Collectors.toList());

        String sortBy = sortByComboBox.getValue();
        boolean ascending = sortOrderComboBox.getValue() == null || sortOrderComboBox.getValue().equals("Croissant");

        filtered.sort((w1, w2) -> {
            int cmp = 0;
            if ("Date".equals(sortBy)) {
                cmp = w1.getScore().getDateCorrection().compareTo(w2.getScore().getDateCorrection());
            } else if ("Évaluation".equals(sortBy)) {
                String n1 = w1.getEvaluation() != null ? w1.getEvaluation().getTitre() : "";
                String n2 = w2.getEvaluation() != null ? w2.getEvaluation().getTitre() : "";
                cmp = n1.compareToIgnoreCase(n2);
            } else if ("Type".equals(sortBy)) {
                String t1 = w1.getEvaluation() != null ? w1.getEvaluation().getTypeEvaluation() : "";
                String t2 = w2.getEvaluation() != null ? w2.getEvaluation().getTypeEvaluation() : "";
                cmp = t1.compareToIgnoreCase(t2);
            } else if ("Note".equals(sortBy)) {
                cmp = Double.compare(w1.getScore().getNote(), w2.getScore().getNote());
            }
            return ascending ? cmp : -cmp;
        });

        int pageSize = pageSizeComboBox.getValue() != null ? pageSizeComboBox.getValue() : 10;
        int totalItems = filtered.size();
        int pageCount = (int) Math.ceil((double) totalItems / pageSize);
        pageCount = Math.max(pageCount, 1);
        pagination.setPageCount(pageCount);

        int currentPage = pagination.getCurrentPageIndex();
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<ScoreWrapper> pageItems = filtered.subList(fromIndex, Math.max(fromIndex, toIndex));
        filteredWrappers.setAll(pageItems);
        notesTableView.setItems(filteredWrappers);

        paginationSummaryLabel.setText(totalItems + " note" + (totalItems > 1 ? "s" : ""));
    }

    @Override
    public void setMainController(MainLayoutEtudiantController controller) {
        this.mainController = controller;
    }

    private void handleVoir(ScoreWrapper wrapper) {
        // Vérification de sécurité : l'étudiant ne peut voir que ses propres notes
        if (!currentStudentId.equals(wrapper.getSoumission().getIdEtudiant())) {
            showAlert(Alert.AlertType.ERROR, "Accès refusé", "Vous ne pouvez pas consulter la note d'un autre étudiant.");
            return;
        }

        if (mainController != null) {
            mainController.showStudentScoreDetail(wrapper.getScore());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
