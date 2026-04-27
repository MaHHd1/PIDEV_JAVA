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
import java.nio.file.Paths;

public class ScoreDetailController {

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private Label evaluationTitleLabel;

    @FXML
    private Label evaluationTypeLabel;

    @FXML
    private Label courseLabel;

    @FXML
    private Label studentLabel;

    @FXML
    private Label submissionDateLabel;

    @FXML
    private Label submissionStatusLabel;

    @FXML
    private Label noteLabel;

    @FXML
    private Label pourcentageLabel;

    @FXML
    private Label resultatLabel;

    @FXML
    private Label correctionStatusLabel;

    @FXML
    private Label correctionDateLabel;

    @FXML
    private Label mentionLabel;

    @FXML
    private VBox teacherCommentBox;

    @FXML
    private Label teacherCommentLabel;

    @FXML
    private VBox studentCommentBox;

    @FXML
    private Label studentCommentLabel;

    @FXML
    private Hyperlink evalPdfLink;

    @FXML
    private Hyperlink submissionPdfLink;

    @FXML
    private Button viewSubmissionButton;

    private Score score;
    private Soumission soumission;
    private Evaluation evaluation;

    private final ScoreService scoreService = new ScoreService();
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    public void setScore(Score score) {
        this.score = score;
        loadData();
        displayDetails();
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

    private void displayDetails() {
        if (score == null || soumission == null) return;

        // Évaluation
        if (evaluation != null) {
            evaluationTitleLabel.setText(evaluation.getTitre());
            evaluationTypeLabel.setText(evaluation.getTypeEvaluation());
            courseLabel.setText(evaluation.getCoursId() != null ? "Cours #" + evaluation.getCoursId() : "-");
            evalPdfLink.setVisible(evaluation.getPdfFilename() != null && !evaluation.getPdfFilename().isEmpty());
        } else {
            evaluationTitleLabel.setText("Évaluation #" + soumission.getEvaluationId());
            evaluationTypeLabel.setText("-");
            courseLabel.setText("-");
            evalPdfLink.setVisible(false);
        }

        // Étudiant
        studentLabel.setText(soumission.getIdEtudiant());

        // Soumission
        submissionDateLabel.setText(soumission.getDateSoumission().toLocalDate().toString());
        submissionStatusLabel.setText(soumission.getStatut());
        submissionPdfLink.setVisible(soumission.getPdfFilename() != null && !soumission.getPdfFilename().isEmpty());

        // Notation
        noteLabel.setText(score.getNote() + "/" + score.getNoteSur());
        if (score.getPourcentage() != null) {
            pourcentageLabel.setText(score.getPourcentage() + "%");
        }

        // Résultat
        if (score.isReussi()) {
            resultatLabel.setText("Réussi");
            resultatLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            resultatLabel.setText("Échoué");
            resultatLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        // Statut et mention
        correctionStatusLabel.setText(score.getStatutCorrection());
        correctionDateLabel.setText(score.getDateCorrection().toLocalDate().toString());
        mentionLabel.setText(score.getMention());

        // Commentaires
        if (score.getCommentaireEnseignant() != null && !score.getCommentaireEnseignant().isEmpty()) {
            teacherCommentBox.setVisible(true);
            teacherCommentLabel.setText(score.getCommentaireEnseignant());
        } else {
            teacherCommentBox.setVisible(false);
        }

        if (soumission.getCommentaireEtudiant() != null && !soumission.getCommentaireEtudiant().isEmpty()) {
            studentCommentBox.setVisible(true);
            studentCommentLabel.setText(soumission.getCommentaireEtudiant());
        } else {
            studentCommentBox.setVisible(false);
        }
    }

    @FXML
    private void handleEdit() {
        if (score == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/score-edit.fxml"));
            Parent root = loader.load();
            ScoreEditController controller = loader.getController();
            controller.setScore(score);
            Stage stage = (Stage) editButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire d'édition: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (score == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la correction");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette correction ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                scoreService.delete(score);
                navigateToList();
            }
        });
    }

    @FXML
    private void handleBack() {
        navigateToList();
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
    private void handleViewSubmission() {
        if (soumission == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/soumission-detail.fxml"));
            Parent root = loader.load();
            SoumissionDetailController controller = loader.getController();
            controller.setSoumission(soumission);
            Stage stage = (Stage) viewSubmissionButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les détails de la soumission: " + e.getMessage());
        }
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

                Stage stage = (Stage) backButton.getScene().getWindow();
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
            Stage stage = (Stage) backButton.getScene().getWindow();
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
