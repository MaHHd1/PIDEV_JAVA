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

public class ScoreListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortByComboBox;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    @FXML
    private TableView<SoumissionWrapper> correctionTableView;

    @FXML
    private TableColumn<SoumissionWrapper, Integer> idColumn;

    @FXML
    private TableColumn<SoumissionWrapper, String> evaluationColumn;

    @FXML
    private TableColumn<SoumissionWrapper, String> studentColumn;

    @FXML
    private TableColumn<SoumissionWrapper, String> noteColumn;

    @FXML
    private TableColumn<SoumissionWrapper, String> pourcentageColumn;

    @FXML
    private TableColumn<SoumissionWrapper, String> statutColumn;

    @FXML
    private TableColumn<SoumissionWrapper, String> dateColumn;

    @FXML
    private TableColumn<SoumissionWrapper, Void> actionsColumn;

    @FXML
    private Label paginationSummaryLabel;

    @FXML
    private Pagination pagination;

    private final SoumissionService soumissionService = new SoumissionService();
    private final ScoreService scoreService = new ScoreService();
    private final EvaluationService evaluationService = new EvaluationService();
    private ObservableList<SoumissionWrapper> soumissionWrappers = FXCollections.observableArrayList();
    private ObservableList<SoumissionWrapper> filteredWrappers = FXCollections.observableArrayList();

    // ID de l'enseignant connecté (à remplacer par la session utilisateur)
    private String currentTeacherId = "PROF001";

    // Classe wrapper pour combiner Soumission + Score
    public static class SoumissionWrapper {
        private final Soumission soumission;
        private final Score score;
        private final Evaluation evaluation;

        public SoumissionWrapper(Soumission soumission, Score score, Evaluation evaluation) {
            this.soumission = soumission;
            this.score = score;
            this.evaluation = evaluation;
        }

        public int getId() { return soumission.getId(); }
        public Soumission getSoumission() { return soumission; }
        public Score getScore() { return score; }
        public Evaluation getEvaluation() { return evaluation; }
        public boolean isCorrected() { return score != null; }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupSearch();
        setupPagination();
        loadSoumissions();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSoumission().getIdEtudiant());
        });

        evaluationColumn.setCellValueFactory(cellData -> {
            Evaluation eval = cellData.getValue().getEvaluation();
            return new javafx.beans.property.SimpleStringProperty(eval != null ? eval.getTitre() : "-");
        });

        noteColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            if (score != null) {
                return new javafx.beans.property.SimpleStringProperty(score.getNote() + "/" + score.getNoteSur());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        pourcentageColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            if (score != null && score.getPourcentage() != null) {
                return new javafx.beans.property.SimpleStringProperty(score.getPourcentage() + "%");
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        statutColumn.setCellValueFactory(cellData -> {
            Score score = cellData.getValue().getScore();
            String statut = (score != null) ? "Corrigé" : "En attente";
            return new javafx.beans.property.SimpleStringProperty(statut);
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
            if (score != null && score.getDateCorrection() != null) {
                return new javafx.beans.property.SimpleStringProperty(score.getDateCorrection().toLocalDate().toString());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();

            {
                btn.setOnAction(event -> {
                    SoumissionWrapper wrapper = getTableView().getItems().get(getIndex());
                    if (wrapper.isCorrected()) {
                        handleViewScore(wrapper);
                    } else {
                        handleCorriger(wrapper);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    SoumissionWrapper wrapper = getTableView().getItems().get(getIndex());
                    if (wrapper.isCorrected()) {
                        HBox box = new HBox(5);
                        Button voirBtn = new Button("Voir");
                        Button modifBtn = new Button("Modifier");
                        Button supprBtn = new Button("Supprimer");

                        voirBtn.setOnAction(e -> handleViewScore(wrapper));
                        modifBtn.setOnAction(e -> handleEditScore(wrapper));
                        supprBtn.setOnAction(e -> handleDeleteScore(wrapper));

                        box.getChildren().addAll(voirBtn, modifBtn, supprBtn);
                        setGraphic(box);
                    } else {
                        btn.setText("Corriger");
                        btn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                        setGraphic(btn);
                    }
                }
            }
        });
    }

    private void setupComboBoxes() {
        sortByComboBox.setItems(FXCollections.observableArrayList("Date", "Évaluation", "Étudiant", "Statut"));
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

    private void loadSoumissions() {
        List<Soumission> allSoumissions = soumissionService.getAll();
        List<Evaluation> allEvaluations = evaluationService.getAll();

        // Filtrer les soumissions des évaluations de l'enseignant connecté
        List<SoumissionWrapper> wrappers = allSoumissions.stream()
            .map(s -> {
                Score score = scoreService.getBySoumissionId(s.getId());
                Evaluation eval = allEvaluations.stream()
                    .filter(e -> e.getId() == s.getEvaluationId())
                    .findFirst()
                    .orElse(null);
                return new SoumissionWrapper(s, score, eval);
            })
            .filter(w -> w.getEvaluation() != null && currentTeacherId.equals(w.getEvaluation().getIdEnseignant()))
            .collect(Collectors.toList());

        soumissionWrappers.setAll(wrappers);
        applyFilters();
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        List<SoumissionWrapper> filtered = soumissionWrappers.stream()
            .filter(w -> {
                String evalName = w.getEvaluation() != null ? w.getEvaluation().getTitre().toLowerCase() : "";
                String studentId = w.getSoumission().getIdEtudiant().toLowerCase();
                String type = w.getEvaluation() != null ? w.getEvaluation().getTypeEvaluation().toLowerCase() : "";
                String statut = w.isCorrected() ? "corrigé" : "en attente";
                return evalName.contains(search) || studentId.contains(search) || type.contains(search) || statut.contains(search);
            })
            .collect(Collectors.toList());

        String sortBy = sortByComboBox.getValue();
        boolean ascending = sortOrderComboBox.getValue() == null || sortOrderComboBox.getValue().equals("Croissant");

        filtered.sort((w1, w2) -> {
            int cmp = 0;
            if ("Date".equals(sortBy)) {
                cmp = w1.getSoumission().getDateSoumission().compareTo(w2.getSoumission().getDateSoumission());
            } else if ("Évaluation".equals(sortBy)) {
                String n1 = w1.getEvaluation() != null ? w1.getEvaluation().getTitre() : "";
                String n2 = w2.getEvaluation() != null ? w2.getEvaluation().getTitre() : "";
                cmp = n1.compareToIgnoreCase(n2);
            } else if ("Étudiant".equals(sortBy)) {
                cmp = w1.getSoumission().getIdEtudiant().compareToIgnoreCase(w2.getSoumission().getIdEtudiant());
            } else if ("Statut".equals(sortBy)) {
                String s1 = w1.isCorrected() ? "Corrigé" : "En attente";
                String s2 = w2.isCorrected() ? "Corrigé" : "En attente";
                cmp = s1.compareToIgnoreCase(s2);
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

        List<SoumissionWrapper> pageItems = filtered.subList(fromIndex, Math.max(fromIndex, toIndex));
        filteredWrappers.setAll(pageItems);
        correctionTableView.setItems(filteredWrappers);

        paginationSummaryLabel.setText(totalItems + " soumission" + (totalItems > 1 ? "s" : ""));
    }

    private void handleCorriger(SoumissionWrapper wrapper) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-create.fxml"));
            Parent root = loader.load();
            ScoreCreateController controller = loader.getController();
            controller.setSoumission(wrapper.getSoumission());
            controller.setTeacherId(currentTeacherId);
            Stage stage = (Stage) correctionTableView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de correction: " + e.getMessage());
        }
    }

    private void handleViewScore(SoumissionWrapper wrapper) {
        if (wrapper.getScore() == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-detail.fxml"));
            Parent root = loader.load();
            ScoreDetailController controller = loader.getController();
            controller.setScore(wrapper.getScore());
            Stage stage = (Stage) correctionTableView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails: " + e.getMessage());
        }
    }

    private void handleEditScore(SoumissionWrapper wrapper) {
        if (wrapper.getScore() == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-edit.fxml"));
            Parent root = loader.load();
            ScoreEditController controller = loader.getController();
            controller.setScore(wrapper.getScore());
            Stage stage = (Stage) correctionTableView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'édition: " + e.getMessage());
        }
    }

    private void handleDeleteScore(SoumissionWrapper wrapper) {
        if (wrapper.getScore() == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la correction");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette correction ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                scoreService.delete(wrapper.getScore());
                loadSoumissions();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
