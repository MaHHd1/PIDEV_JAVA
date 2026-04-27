package org.example.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.entity.Evaluation;
import org.example.entity.Score;
import org.example.entity.Soumission;
import org.example.service.EvaluationService;
import org.example.service.ScoreService;
import org.example.service.SoumissionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ScoreCreateController {

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

    @FXML
    private Button saveButton;

    private final ScoreService scoreService = new ScoreService();
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    private Soumission selectedSoumission;
    private String teacherId = "PROF001";

    @FXML
    public void initialize() {
        loadSoumissions();

        // Listener pour afficher les détails de la soumission sélectionnée
        soumissionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedSoumission = newVal;
                showSoumissionDetails(newVal);

                // Récupérer la note max de l'évaluation
                Evaluation eval = evaluationService.getById(newVal.getEvaluationId());
                if (eval != null) {
                    noteSurField.setText(String.valueOf(eval.getNoteMax()));
                }
            } else {
                soumissionDetailsBox.setVisible(false);
            }
        });

        // Configurer l'affichage du ComboBox
        soumissionComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Soumission item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Evaluation eval = evaluationService.getById(item.getEvaluationId());
                    String titre = eval != null ? eval.getTitre() : "Évaluation #" + item.getEvaluationId();
                    setText(item.getIdEtudiant() + " - " + titre);
                }
            }
        });
        soumissionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Soumission item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Sélectionner une soumission");
                } else {
                    Evaluation eval = evaluationService.getById(item.getEvaluationId());
                    String titre = eval != null ? eval.getTitre() : "Évaluation #" + item.getEvaluationId();
                    setText(item.getIdEtudiant() + " - " + titre);
                }
            }
        });
    }

    public void setSoumission(Soumission soum) {
        this.selectedSoumission = soum;
        if (soum != null) {
            soumissionComboBox.setValue(soum);
            soumissionComboBox.setDisable(true); // Pré-sélectionnée, non modifiable
        }
    }

    public void setTeacherId(String id) {
        this.teacherId = id;
    }

    private void loadSoumissions() {
        // Charger uniquement les soumissions non corrigées des évaluations de l'enseignant
        java.util.List<Soumission> allSoums = soumissionService.getAll();
        java.util.List<Evaluation> allEvals = evaluationService.getAll();

        java.util.List<Soumission> uncorrectedSoums = allSoums.stream()
            .filter(s -> {
                // Vérifier si déjà corrigée
                Score existing = scoreService.getBySoumissionId(s.getId());
                if (existing != null) return false;

                // Vérifier si c'est une évaluation de l'enseignant
                Evaluation eval = allEvals.stream()
                    .filter(e -> e.getId() == s.getEvaluationId())
                    .findFirst()
                    .orElse(null);
                return eval != null && teacherId.equals(eval.getIdEnseignant());
            })
            .collect(java.util.stream.Collectors.toList());

        soumissionComboBox.setItems(javafx.collections.FXCollections.observableArrayList(uncorrectedSoums));
    }

    private void showSoumissionDetails(Soumission soum) {
        soumissionDetailsBox.setVisible(true);
        submissionDateLabel.setText("Date de soumission: " + soum.getDateSoumission().toLocalDate().toString());

        if (soum.getCommentaireEtudiant() != null && !soum.getCommentaireEtudiant().isEmpty()) {
            studentCommentLabel.setText("Commentaire: " + soum.getCommentaireEtudiant());
            studentCommentLabel.setVisible(true);
        } else {
            studentCommentLabel.setVisible(false);
        }

        downloadPdfLink.setVisible(soum.getPdfFilename() != null && !soum.getPdfFilename().isEmpty());
    }

    @FXML
    private void handleDownloadSubmissionPdf() {
        if (selectedSoumission == null || selectedSoumission.getPdfFilename() == null) return;
        downloadPdf(selectedSoumission.getPdfFilename());
    }

    @FXML
    private void handleCancel() {
        navigateToList();
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Score score = new Score();
            score.setSoumissionId(selectedSoumission.getId());
            score.setNote(Double.parseDouble(noteField.getText().trim()));
            score.setNoteSur(Double.parseDouble(noteSurField.getText().trim()));
            score.setCommentaireEnseignant(commentaireArea.getText().trim());
            score.setDateCorrection(LocalDateTime.now());
            score.setStatutCorrection("corrigé");

            // Vérifier que la note ne dépasse pas la note maximale
            if (!score.isValidNote()) {
                showAlert(Alert.AlertType.ERROR, "Validation", "La note ne peut pas dépasser la note maximale.");
                return;
            }

            scoreService.add(score);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Correction enregistrée avec succès!");
            navigateToList();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La note doit être un nombre valide.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        // Validation soumission: obligatoire
        if (!ValidationUtils.validateNotNull(selectedSoumission, "La soumission")) {
            return false;
        }

        // Vérifier si la soumission est déjà corrigée
        Score existing = scoreService.getBySoumissionId(selectedSoumission.getId());
        if (existing != null) {
            ValidationUtils.showError("Cette soumission a déjà été corrigée.");
            return false;
        }

        // Vérifier que l'enseignant est propriétaire de l'évaluation
        Evaluation eval = evaluationService.getById(selectedSoumission.getEvaluationId());
        if (eval == null || !teacherId.equals(eval.getIdEnseignant())) {
            ValidationUtils.showError("Vous ne pouvez corriger que vos propres évaluations.");
            return false;
        }

        // Validation note: obligatoire, >= 0, <= note max
        String noteStr = noteField.getText();
        if (!ValidationUtils.validateRequired(noteStr, "La note")) {
            return false;
        }

        double noteMax = eval.getNoteMax();
        if (!ValidationUtils.validateDoubleRange(noteStr, 0, noteMax, "La note")) {
            return false;
        }

        // Validation commentaire: max 1000 caractères (optionnel)
        String commentaire = commentaireArea.getText();
        if (commentaire != null && !commentaire.isEmpty()) {
            if (!ValidationUtils.validateMaxLength(commentaire, 1000, "Le commentaire")) {
                return false;
            }
        }

        return true;
    }

    private void downloadPdf(String filename) {
        try {
            File pdfFile = Paths.get("uploads", filename).toFile();
            if (pdfFile.exists()) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Télécharger le PDF");
                fileChooser.setInitialFileName(filename);
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
                );

                Stage stage = (Stage) downloadPdfLink.getScene().getWindow();
                File targetFile = fileChooser.showSaveDialog(stage);

                if (targetFile != null) {
                    Files.copy(pdfFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "PDF téléchargé avec succès!");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier PDF n'existe pas.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de télécharger le PDF: " + e.getMessage());
        }
    }

    private void navigateToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-list.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la liste: " + e.getMessage());
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
