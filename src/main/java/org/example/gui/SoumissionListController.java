package org.example.gui;

import entities.Etudiant;
import entities.Utilisateur;
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
import org.example.entity.Soumission;
import org.example.service.EvaluationService;
import org.example.service.SoumissionService;
import utils.UserSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SoumissionListController implements MainControllerAwareEtudiant {

    @FXML
    private Button newSoumissionButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortByComboBox;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    @FXML
    private TableView<Soumission> soumissionTableView;

    @FXML
    private TableColumn<Soumission, Integer> idColumn;

    @FXML
    private TableColumn<Soumission, String> evaluationColumn;

    @FXML
    private TableColumn<Soumission, String> studentIdColumn;

    @FXML
    private TableColumn<Soumission, String> dateColumn;

    @FXML
    private TableColumn<Soumission, String> statusColumn;

    @FXML
    private TableColumn<Soumission, Void> actionsColumn;

    @FXML
    private Label paginationSummaryLabel;

    @FXML
    private Pagination pagination;

    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();
    private ObservableList<Soumission> soumissions = FXCollections.observableArrayList();
    private ObservableList<Soumission> filteredSoumissions = FXCollections.observableArrayList();

    // ID de l'étudiant connecté (à remplacer par la session utilisateur)
    private String currentStudentId = "ETU001";

    private MainLayoutEtudiantController mainController;

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser instanceof Etudiant etudiant) {
            currentStudentId = etudiant.getMatricule();
        }
        setupTableColumns();
        setupComboBoxes();
        setupSearch();
        setupPagination();
        loadSoumissions();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("idEtudiant"));
        dateColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateSoumission().toLocalDate().toString()
            );
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Afficher le titre de l'évaluation
        evaluationColumn.setCellValueFactory(cellData -> {
            Evaluation eval = evaluationService.getById(cellData.getValue().getEvaluationId());
            String titre = eval != null ? eval.getTitre() : "Évaluation #" + cellData.getValue().getEvaluationId();
            return new javafx.beans.property.SimpleStringProperty(titre);
        });

        // Style pour le statut
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "soumise":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "en_retard":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case "non_soumise":
                            setStyle("-fx-text-fill: red;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("Voir");
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox pane = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.setOnAction(event -> {
                    Soumission soum = getTableView().getItems().get(getIndex());
                    handleViewSoumission(soum);
                });
                editBtn.setOnAction(event -> {
                    Soumission soum = getTableView().getItems().get(getIndex());
                    handleEditSoumission(soum);
                });
                deleteBtn.setOnAction(event -> {
                    Soumission soum = getTableView().getItems().get(getIndex());
                    handleDeleteSoumission(soum);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupComboBoxes() {
        sortByComboBox.setItems(FXCollections.observableArrayList("Date", "Évaluation", "Statut"));
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
        List<Soumission> list = soumissionService.getAll();
        // Filtrer par étudiant connecté
        List<Soumission> studentSoumissions = list.stream()
            .filter(s -> s.getIdEtudiant().equals(currentStudentId))
            .collect(Collectors.toList());
        soumissions.setAll(studentSoumissions);
        applyFilters();
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        List<Soumission> filtered = soumissions.stream()
            .filter(s -> {
                String evalName = "";
                Evaluation eval = evaluationService.getById(s.getEvaluationId());
                if (eval != null) {
                    evalName = eval.getTitre().toLowerCase();
                }
                return evalName.contains(search) ||
                       s.getStatut().toLowerCase().contains(search) ||
                       s.getIdEtudiant().toLowerCase().contains(search);
            })
            .collect(Collectors.toList());

        String sortBy = sortByComboBox.getValue();
        boolean ascending = sortOrderComboBox.getValue() == null || sortOrderComboBox.getValue().equals("Croissant");

        filtered.sort((s1, s2) -> {
            int cmp = 0;
            if ("Date".equals(sortBy)) {
                cmp = s1.getDateSoumission().compareTo(s2.getDateSoumission());
            } else if ("Évaluation".equals(sortBy)) {
                Evaluation e1 = evaluationService.getById(s1.getEvaluationId());
                Evaluation e2 = evaluationService.getById(s2.getEvaluationId());
                String n1 = e1 != null ? e1.getTitre() : "";
                String n2 = e2 != null ? e2.getTitre() : "";
                cmp = n1.compareToIgnoreCase(n2);
            } else if ("Statut".equals(sortBy)) {
                cmp = s1.getStatut().compareToIgnoreCase(s2.getStatut());
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

        List<Soumission> pageItems = filtered.subList(fromIndex, Math.max(fromIndex, toIndex));
        filteredSoumissions.setAll(pageItems);
        soumissionTableView.setItems(filteredSoumissions);

        paginationSummaryLabel.setText(totalItems + " soumission" + (totalItems > 1 ? "s" : ""));
    }

    @Override
    public void setMainController(MainLayoutEtudiantController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleNewSoumission() {
        if (mainController != null) {
            mainController.showSoumissionCreate();
        }
    }

    private void handleViewSoumission(Soumission soum) {
        if (mainController != null) {
            mainController.showSoumissionDetail(soum);
        }
    }

    private void handleEditSoumission(Soumission soum) {
        // Vérifier si l'évaluation est fermée
        Evaluation eval = evaluationService.getById(soum.getEvaluationId());
        if (eval != null && "fermee".equals(eval.getStatut())) {
            showAlert("Information", "Cette évaluation est fermée. La modification est interdite.");
            return;
        }

        if (mainController != null) {
            mainController.showSoumissionEdit(soum);
        }
    }

    private void handleDeleteSoumission(Soumission soum) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la soumission");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette soumission ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                soumissionService.delete(soum);
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
