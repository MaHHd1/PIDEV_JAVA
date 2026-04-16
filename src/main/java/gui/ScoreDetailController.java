package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
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

public class ScoreDetailController implements MainControllerAware {

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
        displayDetails();
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

    private void displayDetails() {
        if (score == null || soumission == null) {
            return;
        }

        if (evaluation != null) {
            evaluationTitleLabel.setText(evaluation.getTitre());
            evaluationTypeLabel.setText(evaluation.getTypeEvaluation());
            courseLabel.setText(evaluation.getCoursId() != null ? "Cours #" + evaluation.getCoursId() : "-");
            evalPdfLink.setVisible(evaluation.getPdfFilename() != null && !evaluation.getPdfFilename().isBlank());
        } else {
            evaluationTitleLabel.setText("Evaluation #" + soumission.getEvaluationId());
            evaluationTypeLabel.setText("-");
            courseLabel.setText("-");
            evalPdfLink.setVisible(false);
        }

        studentLabel.setText(soumission.getIdEtudiant());
        submissionDateLabel.setText(soumission.getDateSoumission().toLocalDate().toString());
        submissionStatusLabel.setText(soumission.getStatut());
        submissionPdfLink.setVisible(soumission.getPdfFilename() != null && !soumission.getPdfFilename().isBlank());

        noteLabel.setText(score.getNote() + "/" + score.getNoteSur());
        pourcentageLabel.setText(score.getPourcentage() != null ? score.getPourcentage() + "%" : "-");
        resultatLabel.setText(score.isReussi() ? "Reussi" : "Echoue");
        correctionStatusLabel.setText(score.getStatutCorrection());
        correctionDateLabel.setText(score.getDateCorrection() != null ? score.getDateCorrection().toLocalDate().toString() : "-");
        mentionLabel.setText(score.getMention() != null ? score.getMention() : "-");

        teacherCommentBox.setVisible(score.getCommentaireEnseignant() != null && !score.getCommentaireEnseignant().isBlank());
        teacherCommentLabel.setText(score.getCommentaireEnseignant());
        studentCommentBox.setVisible(soumission.getCommentaireEtudiant() != null && !soumission.getCommentaireEtudiant().isBlank());
        studentCommentLabel.setText(soumission.getCommentaireEtudiant());
    }

    @FXML
    private void handleEdit() {
        if (score != null && mainController != null) {
            mainController.showScoreEdit(score);
        }
    }

    @FXML
    private void handleDelete() {
        if (score == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la correction");
        alert.setContentText("Voulez-vous supprimer cette correction ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                scoreService.delete(score);
                handleBack();
            }
        });
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showMesCorrections();
        }
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Le detail complet de la soumission reste accessible dans l'espace etudiant.");
        alert.showAndWait();
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
            Stage stage = (Stage) backButton.getScene().getWindow();
            File targetFile = fileChooser.showSaveDialog(stage);
            if (targetFile != null) {
                Files.copy(pdfFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        }
    }
}

