package org.example.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.entity.Evaluation;
import org.example.entity.Soumission;
import org.example.service.EvaluationService;
import org.example.service.SoumissionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SoumissionDetailController implements MainControllerAwareEtudiant {

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private Label idLabel;

    @FXML
    private Label evaluationLabel;

    @FXML
    private Label studentIdLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label pdfFileLabel;

    @FXML
    private Hyperlink downloadPdfLink;

    @FXML
    private VBox commentBox;

    @FXML
    private Label commentLabel;

    @FXML
    private Label evalDescriptionLabel;

    @FXML
    private Hyperlink evalPdfLink;

    private Soumission soumission;
    private String studentId = "ETU001";
    private final SoumissionService soumissionService = new SoumissionService();
    private final EvaluationService evaluationService = new EvaluationService();

    private MainLayoutEtudiantController mainController;

    public void setSoumission(Soumission soum) {
        this.soumission = soum;
        displaySoumissionDetails();
    }

    @Override
    public void setMainController(MainLayoutEtudiantController controller) {
        this.mainController = controller;
    }

    public void setStudentId(String id) {
        this.studentId = id;
    }

    @FXML
    public void initialize() {
    }

    private void displaySoumissionDetails() {
        if (soumission == null) return;

        idLabel.setText(String.valueOf(soumission.getId()));
        studentIdLabel.setText(soumission.getIdEtudiant());
        dateLabel.setText(soumission.getDateSoumission().toLocalDate().toString());

        // Statut avec style
        String statut = soumission.getStatut();
        statusLabel.setText(statut);
        switch (statut.toLowerCase()) {
            case "soumise":
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                break;
            case "en_retard":
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                break;
            case "non_soumise":
                statusLabel.setStyle("-fx-text-fill: red;");
                break;
            default:
                statusLabel.setStyle("");
        }

        // Informations de l'évaluation
        Evaluation eval = evaluationService.getById(soumission.getEvaluationId());
        if (eval != null) {
            evaluationLabel.setText(eval.getTitre());
            evalDescriptionLabel.setText(eval.getDescription() != null ? eval.getDescription() : "Aucune description");
            evalPdfLink.setVisible(eval.getPdfFilename() != null && !eval.getPdfFilename().isEmpty());
        } else {
            evaluationLabel.setText("Évaluation #" + soumission.getEvaluationId());
            evalDescriptionLabel.setText("-");
            evalPdfLink.setVisible(false);
        }

        // PDF soumis
        if (soumission.getPdfFilename() != null && !soumission.getPdfFilename().isEmpty()) {
            pdfFileLabel.setText(soumission.getPdfFilename());
            downloadPdfLink.setVisible(true);
        } else {
            pdfFileLabel.setText("Aucun PDF");
            downloadPdfLink.setVisible(false);
        }

        // Commentaire
        if (soumission.getCommentaireEtudiant() != null && !soumission.getCommentaireEtudiant().isEmpty()) {
            commentBox.setVisible(true);
            commentLabel.setText(soumission.getCommentaireEtudiant());
        } else {
            commentBox.setVisible(false);
        }
    }

    @FXML
    private void handleEdit() {
        if (soumission == null || mainController == null) return;

        // Vérifier si l'évaluation est fermée
        Evaluation eval = evaluationService.getById(soumission.getEvaluationId());
        if (eval != null && "fermee".equals(eval.getStatut())) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Cette évaluation est fermée. La modification est interdite.");
            return;
        }

        mainController.showSoumissionEdit(soumission);
    }

    @FXML
    private void handleDelete() {
        if (soumission == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la soumission");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette soumission ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                soumissionService.delete(soumission);
                navigateToList();
            }
        });
    }

    @FXML
    private void handleBack() {
        navigateToList();
    }

    @FXML
    private void handleDownloadPdf() {
        if (soumission == null || soumission.getPdfFilename() == null) return;
        downloadPdf(soumission.getPdfFilename());
    }

    @FXML
    private void handleDownloadEvalPdf() {
        Evaluation eval = evaluationService.getById(soumission.getEvaluationId());
        if (eval != null && eval.getPdfFilename() != null) {
            downloadPdf(eval.getPdfFilename());
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

                Stage stage = (Stage) downloadPdfLink.getScene().getWindow();
                File targetFile = fileChooser.showSaveDialog(stage);

                if (targetFile != null) {
                    Files.copy(pdfFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
        if (mainController != null) {
            mainController.showMesSoumissions();
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
