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

public class ScoreEditController {

    @FXML
    private Label studentLabel;

    @FXML
    private Label evaluationTitleLabel;

    @FXML
    private Label evaluationTypeLabel;

    @FXML
    private Label studentIdLabel;

    @FXML
    private Label submissionDateLabel;

    @FXML
    private Hyperlink evalPdfLink;

    @FXML
    private Hyperlink submissionPdfLink;

    @FXML
    private VBox studentCommentBox;

    @FXML
    private Label studentCommentLabel;

    @FXML
    private Label currentScoreLabel;

    @FXML
    private Label currentPercentageLabel;

    @FXML
    private Label currentDateLabel;

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

    private Score score;
    private Soumission soumission;
    private Evaluation evaluation;

    private final ScoreService scoreService = new ScoreService();
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    public void setScore(Score score) {
        this.score = score;
        loadData();
        populateFields();
    }

    private void loadData() {
        if (score == null) return;
        soumission = soumissionService.getById(score.getSoumissionId());
        if (soumission != null) {
            evaluation = evaluationService.getById(soumission.getEvaluationId());
        }
    }

    @FXML
    public void initialize() {
    }

    private void populateFields() {
        if (score == null || soumission == null) return;

        // Étudiant
        studentLabel.setText(soumission.getIdEtudiant());
        studentIdLabel.setText(soumission.getIdEtudiant());

        // Évaluation
        if (evaluation != null) {
            evaluationTitleLabel.setText(evaluation.getTitre());
            evaluationTypeLabel.setText(evaluation.getTypeEvaluation());
            evalPdfLink.setVisible(evaluation.getPdfFilename() != null && !evaluation.getPdfFilename().isEmpty());
        } else {
            evaluationTitleLabel.setText("Évaluation #" + soumission.getEvaluationId());
            evaluationTypeLabel.setText("-");
            evalPdfLink.setVisible(false);
        }

        // Soumission
        submissionDateLabel.setText(soumission.getDateSoumission().toLocalDate().toString());
        submissionPdfLink.setVisible(soumission.getPdfFilename() != null && !soumission.getPdfFilename().isEmpty());

        // Commentaire étudiant
        if (soumission.getCommentaireEtudiant() != null && !soumission.getCommentaireEtudiant().isEmpty()) {
            studentCommentBox.setVisible(true);
            studentCommentLabel.setText(soumission.getCommentaireEtudiant());
        } else {
            studentCommentBox.setVisible(false);
        }

        // Note actuelle
        currentScoreLabel.setText("Note: " + score.getNote() + "/" + score.getNoteSur());
        if (score.getPourcentage() != null) {
            currentPercentageLabel.setText("Pourcentage: " + score.getPourcentage() + "%");
        }
        currentDateLabel.setText("Corrigé le: " + score.getDateCorrection().toLocalDate().toString());

        // Formulaire
        noteField.setText(String.valueOf(score.getNote()));
        noteSurField.setText(String.valueOf(score.getNoteSur()));
        commentaireArea.setText(score.getCommentaireEnseignant());
    }

    @FXML
    private void handleDownloadEvalPdf() {
        if (evaluation != null && evaluation.getPdfFilename() != null) {
            downloadPdf(evaluation.getPdfFilename());
        }
    }

    @FXML
    private void handleDownloadSubmissionPdf() {
        if (soumission != null && soumission.getPdfFilename() != null) {
            downloadPdf(soumission.getPdfFilename());
        }
    }

    @FXML
    private void handleCancel() {
        navigateToDetail();
    }

    @FXML
    private void handleSave() {
        if (score == null) return;

        if (!validateInput()) {
            return;
        }

        try {
            double newNote = Double.parseDouble(noteField.getText().trim());
            score.setNote(newNote);
            score.setCommentaireEnseignant(commentaireArea.getText().trim());
            score.setDateCorrection(LocalDateTime.now()); // Mettre à jour la date

            // Vérifier que la note ne dépasse pas la note maximale
            if (!score.isValidNote()) {
                ValidationUtils.showError("La note ne peut pas dépasser la note maximale.");
                return;
            }

            scoreService.update(score);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Correction mise à jour avec succès!");
            navigateToDetail();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La note doit être un nombre valide.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        // Validation note: obligatoire, >= 0, <= note max
        String noteStr = noteField.getText();
        if (!ValidationUtils.validateRequired(noteStr, "La note")) {
            return false;
        }

        double noteMax = score.getNoteSur();
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

                Stage stage = (Stage) cancelButton.getScene().getWindow();
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

    private void navigateToDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-detail.fxml"));
            Parent root = loader.load();
            ScoreDetailController controller = loader.getController();
            controller.setScore(score);
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner: " + e.getMessage());
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
