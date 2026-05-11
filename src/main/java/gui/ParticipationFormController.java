package gui;

import entities.Evenement;
import entities.ParticipationEvenement;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ParticipationEvenementService;
import services.WeatherService;
import utils.GroqService;
import entities.Etudiant;
import entities.Utilisateur;
import utils.UserSession;
import java.time.LocalDateTime;

public class ParticipationFormController {
    @FXML private Label lblEventInfo;
    @FXML private TextArea descriptionField;
    @FXML private Label weatherLabel;

    private final ParticipationEvenementService ps = new ParticipationEvenementService();
    private final WeatherService weatherService = new WeatherService();
    private final GroqService groqService = new GroqService();
    private Evenement selectedEvent;

    public void setEvent(Evenement event) {
        this.selectedEvent = event;
        lblEventInfo.setText("Événement: " + event.getTitre() + " à " + event.getLieu());
        updateWeather();
        handleAI(null); // Auto-generate AI description
    }

    private void updateWeather() {
        if (selectedEvent != null) {
            new Thread(() -> {
                String forecast = weatherService.getWeatherForecast(selectedEvent.getLieu(), selectedEvent.getDate_debut());
                Platform.runLater(() -> weatherLabel.setText(forecast));
            }).start();
        }
    }

    @FXML
    void handleAI(ActionEvent event) {
        Utilisateur user = UserSession.getCurrentUser();
        if (user == null) {
            descriptionField.setText("Connectez-vous pour générer une motivation personnalisée.");
            return;
        }

        String nom = user.getNom();
        String prenom = user.getPrenom();
        String annee = (user instanceof Etudiant) ? ((Etudiant) user).getNiveauEtude() : "";

        descriptionField.setText("Génération de votre motivation personnalisée...");
        new Thread(() -> {
            try {
                String description = GroqService.generateDescription(nom, prenom, annee, selectedEvent.getTitre(), selectedEvent.getType_evenement());
                Platform.runLater(() -> descriptionField.setText(description));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && errorMsg.contains("401")) {
                        descriptionField.setText("Erreur : Clé API invalide ou expirée. Veuillez vérifier votre configuration.");
                    } else if (errorMsg != null && errorMsg.contains("429")) {
                        descriptionField.setText("Erreur : Trop de requêtes (Rate limit). Réessayez dans quelques instants.");
                    } else {
                        descriptionField.setText("L'IA est momentanément indisponible. Erreur : " + (errorMsg != null ? errorMsg : "Inconnue"));
                    }
                    System.err.println("Erreur IA: " + errorMsg);
                });
            }
        }).start();
    }

    @FXML
    void handleSave(ActionEvent event) {
        Utilisateur user = UserSession.getCurrentUser();
        if (user == null) {
            showAlert("Erreur", "Vous devez être connecté pour participer.");
            return;
        }

        ParticipationEvenement p = new ParticipationEvenement();
        p.setNom(user.getNom());
        p.setPrenom(user.getPrenom());
        p.setEmail(user.getEmail());
        
        if (user instanceof Etudiant) {
            p.setTelephone(((Etudiant) user).getTelephone());
            p.setAnneeScolaire(((Etudiant) user).getNiveauEtude());
        } else {
            p.setTelephone("-");
            p.setAnneeScolaire("-");
        }

        p.setDescriptionParticipant(descriptionField.getText());
        p.setEvenement_id(selectedEvent.getId());
        p.setUtilisateur_id(user.getId().intValue());
        p.setStatut("Confirmé");
        p.setDate_inscription(LocalDateTime.now());

        try {
            ps.create(p);
            
            String weather = weatherLabel.getText().toLowerCase();
            if (weather.contains("pluie") || weather.contains("rain") || weather.contains("orage") || weather.contains("storm")) {
                showAlert("Inscription Confirmée", "Votre inscription est enregistrée ! ⚠️ Conseil météo : Mauvais temps prévu, n'oubliez pas vos vêtements adaptés !");
            } else {
                showAlert("Succès", "Votre participation à '" + selectedEvent.getTitre() + "' a été enregistrée !");
            }

            handleCancel(null);
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("unique_participation"))) {
                showAlert("Déjà inscrit", "Vous êtes déjà inscrit à cet événement !");
            } else {
                showAlert("Erreur", "Impossible de s'inscrire : " + e.getMessage());
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        ((Stage) descriptionField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
