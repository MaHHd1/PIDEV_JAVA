package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ScoreEditController implements MainControllerAware {

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

    private final ScoreService scoreService = new ScoreService();
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    private Score score;
    private Soumission soumission;
    private Evaluation evaluation;
    private MainLayoutEnseignantController mainController;

    @Override
    public void setMainController(MainLayoutEnseignantController controller) {
        mainController = controller;
    }

    public void setScore(Score value) {
        score = value;
        loadData();
        populateFields();
    }

    private void loadData() {
        if (score == null) {
            return;
        }
        soumission = soumissionService.getById(score.getSoumissionId());
        if (soumission != null) {
            evaluation = evaluationService.getById(soumission.getEvaluationId());
        }
    }

    private void populateFields() {
        if (score == null || soumission == null) {
            return;
        }

        studentLabel.setText(soumission.getIdEtudiant());
        studentIdLabel.setText(soumission.getIdEtudiant());
        submissionDateLabel.setText(soumission.getDateSoumission().toLocalDate().toString());
        studentCommentBox.setVisible(soumission.getCommentaireEtudiant() != null && !soumission.getCommentaireEtudiant().isBlank());
        studentCommentLabel.setText(soumission.getCommentaireEtudiant());
        submissionPdfLink.setVisible(soumission.getPdfFilename() != null && !soumission.getPdfFilename().isBlank());

        if (evaluation != null) {
            evaluationTitleLabel.setText(evaluation.getTitre());
            evaluationTypeLabel.setText(evaluation.getTypeEvaluation());
            evalPdfLink.setVisible(evaluation.getPdfFilename() != null && !evaluation.getPdfFilename().isBlank());
        } else {
            evaluationTitleLabel.setText("Evaluation #" + soumission.getEvaluationId());
            evaluationTypeLabel.setText("-");
            evalPdfLink.setVisible(false);
        }

        currentScoreLabel.setText("Note: " + score.getNote() + "/" + score.getNoteSur());
        currentPercentageLabel.setText("Pourcentage: " + (score.getPourcentage() != null ? score.getPourcentage() + "%" : "-"));
        currentDateLabel.setText("Corrige le: " + (score.getDateCorrection() != null ? score.getDateCorrection().toLocalDate() : "-"));
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
        if (mainController != null) {
            mainController.showScoreDetail(score);
        }
    }

    @FXML
    private void handleSave() {
        if (score == null || !ValidationUtils.validateRequired(noteField.getText(), "La note")) {
            return;
        }

        try {
            if (!ValidationUtils.validateDoubleRange(noteField.getText(), 0, score.getNoteSur(), "La note")) {
                return;
            }
            score.setNote(Double.parseDouble(noteField.getText().trim()));
            score.setCommentaireEnseignant(commentaireArea.getText().trim());
            score.setDateCorrection(LocalDateTime.now());
            scoreService.update(score);
            handleCancel();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La note doit etre un nombre valide.");
            alert.showAndWait();
        }
    }

    private void downloadPdf(String filename) {
        try {
            File pdfFile = Paths.get("uploads", filename).toFile();
            if (!pdfFile.exists()) {
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Telecharger le PDF");
            fileChooser.setInitialFileName(filename);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            File targetFile = fileChooser.showSaveDialog(stage);
            if (targetFile != null) {
                Files.copy(pdfFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        }
    }
}

