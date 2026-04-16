package gui;

import entities.Enseignant;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Evaluation;
import entities.Score;
import entities.Soumission;
import services.EvaluationService;
import services.ScoreService;
import services.SoumissionService;
import utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ScoreCreateController implements MainControllerAware {

    @FXML
    private ComboBox<Soumission> soumissionComboBox;
    @FXML
    private VBox soumissionDetailsBox;
    @FXML
    private Label submissionDateLabel;
    @FXML
    private Label studentCommentLabel;
    @FXML
    private Hyperlink downloadPdfLink;
    @FXML
    private TextField noteField;
    @FXML
    private TextField noteSurField;
    @FXML
    private TextArea commentaireArea;
    @FXML
    private Button cancelButton;

    private final ScoreService scoreService = new ScoreService();
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    private Soumission selectedSoumission;
    private String teacherId = "PROF001";
    private MainLayoutEnseignantController mainController;

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser instanceof Enseignant enseignant) {
            teacherId = enseignant.getMatriculeEnseignant();
        }

        loadSoumissions();
        soumissionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedSoumission = newVal;
                showSoumissionDetails(newVal);
                Evaluation evaluation = evaluationService.getById(newVal.getEvaluationId());
                if (evaluation != null) {
                    noteSurField.setText(String.valueOf(evaluation.getNoteMax()));
                }
            }
        });

        soumissionComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Soumission item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : buildLabel(item));
            }
        });
        soumissionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Soumission item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Selectionner une soumission" : buildLabel(item));
            }
        });
    }

    @Override
    public void setMainController(MainLayoutEnseignantController controller) {
        mainController = controller;
    }

    public void setSoumission(Soumission soumission) {
        selectedSoumission = soumission;
        if (soumission != null) {
            soumissionComboBox.setValue(soumission);
            soumissionComboBox.setDisable(true);
        }
    }

    public void setTeacherId(String id) {
        teacherId = id;
    }

    private void loadSoumissions() {
        var soumissions = soumissionService.getAll();
        var evaluations = evaluationService.getAll();

        var pendingSoumissions = soumissions.stream()
                .filter(soumission -> {
                    if (scoreService.getBySoumissionId(soumission.getId()) != null) {
                        return false;
                    }
                    Evaluation evaluation = evaluations.stream()
                            .filter(item -> item.getId() == soumission.getEvaluationId())
                            .findFirst()
                            .orElse(null);
                    return evaluation != null && teacherId.equals(evaluation.getIdEnseignant());
                })
                .toList();

        soumissionComboBox.setItems(javafx.collections.FXCollections.observableArrayList(pendingSoumissions));
    }

    private String buildLabel(Soumission soumission) {
        Evaluation evaluation = evaluationService.getById(soumission.getEvaluationId());
        String title = evaluation != null ? evaluation.getTitre() : "Evaluation #" + soumission.getEvaluationId();
        return soumission.getIdEtudiant() + " - " + title;
    }

    private void showSoumissionDetails(Soumission soumission) {
        soumissionDetailsBox.setVisible(true);
        submissionDateLabel.setText("Date de soumission: " + soumission.getDateSoumission().toLocalDate());
        studentCommentLabel.setText(soumission.getCommentaireEtudiant() == null || soumission.getCommentaireEtudiant().isBlank()
                ? "Aucun commentaire etudiant."
                : "Commentaire: " + soumission.getCommentaireEtudiant());
        downloadPdfLink.setVisible(soumission.getPdfFilename() != null && !soumission.getPdfFilename().isBlank());
    }

    @FXML
    private void handleDownloadSubmissionPdf() {
        if (selectedSoumission != null && selectedSoumission.getPdfFilename() != null) {
            downloadPdf(selectedSoumission.getPdfFilename());
        }
    }

    @FXML
    private void handleCancel() {
        if (mainController != null) {
            mainController.showMesCorrections();
        }
    }

    @FXML
    private void handleSave() {
        if (!ValidationUtils.validateNotNull(selectedSoumission, "La soumission")) {
            return;
        }
        if (!ValidationUtils.validateRequired(noteField.getText(), "La note")) {
            return;
        }

        Evaluation evaluation = evaluationService.getById(selectedSoumission.getEvaluationId());
        if (evaluation == null || !ValidationUtils.validateDoubleRange(noteField.getText(), 0, evaluation.getNoteMax(), "La note")) {
            return;
        }

        try {
            Score score = new Score();
            score.setSoumissionId(selectedSoumission.getId());
            score.setNote(Double.parseDouble(noteField.getText().trim()));
            score.setNoteSur(Double.parseDouble(noteSurField.getText().trim()));
            score.setCommentaireEnseignant(commentaireArea.getText().trim());
            score.setDateCorrection(LocalDateTime.now());
            score.setStatutCorrection("corrige");
            scoreService.add(score);
            showAlert(Alert.AlertType.INFORMATION, "Succes", "Correction enregistree avec succes.");
            handleCancel();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La note doit etre un nombre valide.");
        }
    }

    private void downloadPdf(String filename) {
        try {
            File pdfFile = Paths.get("uploads", filename).toFile();
            if (!pdfFile.exists()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier PDF n'existe pas.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Telecharger le PDF");
            fileChooser.setInitialFileName(filename);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            Stage stage = (Stage) downloadPdfLink.getScene().getWindow();
            File targetFile = fileChooser.showSaveDialog(stage);
            if (targetFile != null) {
                Files.copy(pdfFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "PDF telecharge avec succes.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de telecharger le PDF: " + e.getMessage());
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

