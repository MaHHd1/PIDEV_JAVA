package gui;

import entities.Message;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.MessageService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MessageController {

    @FXML
    private TextField tfExpediteurId;

    @FXML
    private TextField tfDestinataireId;

    @FXML
    private TextField tfObjet;

    @FXML
    private TextArea taContenu;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private ComboBox<String> cbPriorite;

    @FXML
    private TextField tfPieceJointeUrl;

    @FXML
    private TextField tfParentId;

    @FXML
    private ComboBox<String> cbCategorie;

    private MessageService messageService;

    public void setConnection(Connection connection) {
        this.messageService = new MessageService(connection);
    }

    @FXML
    public void goBack() {
        try {
            entities.Utilisateur user = utils.UserSession.getCurrentUser();
            if (user != null) {
                switch (user.getType().toLowerCase()) {
                    case "administrateur": utils.SceneManager.switchScene("/gui/admin-dashboard.fxml", "Admin Dashboard"); break;
                    case "enseignant": utils.SceneManager.switchScene("/gui/teacher-dashboard.fxml", "Teacher Dashboard"); break;
                    case "etudiant": utils.SceneManager.switchScene("/gui/student-dashboard.fxml", "Student Dashboard"); break;
                    default: utils.SceneManager.switchScene("/gui/admin-dashboard.fxml", "Admin Dashboard");
                }
            } else {
                utils.SceneManager.switchScene("/gui/admin-dashboard.fxml", "Admin Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyMessageTable(Connection connection) {
        try {
            try (java.sql.Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS priorite VARCHAR(50) DEFAULT 'normal'");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS piece_jointe_url VARCHAR(255) NULL");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS parent_id BIGINT NULL");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS categorie VARCHAR(50) DEFAULT 'personnel'");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS est_archive_expediteur BOOLEAN DEFAULT FALSE");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS est_archive_destinataire BOOLEAN DEFAULT FALSE");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS est_supprime_expediteur BOOLEAN DEFAULT FALSE");
                stmt.execute("ALTER TABLE message ADD COLUMN IF NOT EXISTS est_supprime_destinataire BOOLEAN DEFAULT FALSE");
            }
        } catch (Exception e) {
            System.err.println("Note migration message: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        if (this.messageService == null) {
            Connection cnx = utils.DBConnection.getInstance().getConnection();
            verifyMessageTable(cnx);
            this.messageService = new MessageService(cnx);
        }
        cbStatut.setItems(FXCollections.observableArrayList("envoye", "lu"));
        cbPriorite.setItems(FXCollections.observableArrayList("basse", "normal", "haute"));
        cbCategorie.setItems(FXCollections.observableArrayList("personnel", "important", "administratif"));

        cbStatut.setValue("envoye");
        cbPriorite.setValue("normal");
        cbCategorie.setValue("personnel");
    }

    @FXML
    public void ajouterMessage() {
        try {
            String erreurs = validerSaisie();

            if (!erreurs.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreurs de saisie", erreurs);
                return;
            }

            Message message = new Message();
            message.setExpediteurId(Long.parseLong(tfExpediteurId.getText().trim()));
            message.setDestinataireId(Long.parseLong(tfDestinataireId.getText().trim()));
            message.setObjet(tfObjet.getText().trim());
            message.setContenu(taContenu.getText().trim());
            message.setDateEnvoi(LocalDateTime.now());
            message.setStatut(cbStatut.getValue());
            message.setPriorite(cbPriorite.getValue());
            message.setCategorie(cbCategorie.getValue());

            String pieceJointe = tfPieceJointeUrl.getText().trim();
            if (!pieceJointe.isEmpty()) {
                message.setPieceJointeUrl(pieceJointe);
            }

            String parentId = tfParentId.getText().trim();
            if (!parentId.isEmpty()) {
                message.setParentId(Long.parseLong(parentId));
            }

            messageService.ajouter(message);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Message ajouté avec succès.");
            viderChamps();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    public void modifierMessage() {
        try {
            String erreurs = validerSaisie();

            if (!erreurs.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreurs de saisie", erreurs);
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Modification");
            dialog.setHeaderText("Modifier un message");
            dialog.setContentText("Entrer l'ID du message à modifier :");

            dialog.showAndWait().ifPresent(idStr -> {
                try {
                    long id = Long.parseLong(idStr.trim());

                    Message message = new Message();
                    message.setId(id);
                    message.setExpediteurId(Long.parseLong(tfExpediteurId.getText().trim()));
                    message.setDestinataireId(Long.parseLong(tfDestinataireId.getText().trim()));
                    message.setObjet(tfObjet.getText().trim());
                    message.setContenu(taContenu.getText().trim());
                    message.setDateEnvoi(LocalDateTime.now());
                    message.setStatut(cbStatut.getValue());
                    message.setPriorite(cbPriorite.getValue());
                    message.setCategorie(cbCategorie.getValue());

                    String pieceJointe = tfPieceJointeUrl.getText().trim();
                    if (!pieceJointe.isEmpty()) {
                        message.setPieceJointeUrl(pieceJointe);
                    }

                    String parentId = tfParentId.getText().trim();
                    if (!parentId.isEmpty()) {
                        message.setParentId(Long.parseLong(parentId));
                    }

                    messageService.modifier(message);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Message modifié avec succès.");
                    viderChamps();

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    public void supprimerMessage() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Suppression");
        dialog.setHeaderText("Supprimer un message");
        dialog.setContentText("Entrer l'ID du message à supprimer :");

        dialog.showAndWait().ifPresent(idStr -> {
            try {
                if (idStr.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID est obligatoire.");
                    return;
                }

                long id = Long.parseLong(idStr.trim());
                messageService.supprimer(id);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Message supprimé avec succès.");

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être numérique.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
            }
        });
    }

    private String validerSaisie() {
        StringBuilder erreurs = new StringBuilder();

        String expediteur = tfExpediteurId.getText().trim();
        String destinataire = tfDestinataireId.getText().trim();
        String objet = tfObjet.getText().trim();
        String contenu = taContenu.getText().trim();
        String parentId = tfParentId.getText().trim();
        String pieceJointe = tfPieceJointeUrl.getText().trim();

        if (expediteur.isEmpty()) {
            erreurs.append("- L'ID de l'expéditeur est obligatoire.\n");
        } else if (!expediteur.matches("\\d+")) {
            erreurs.append("- L'ID de l'expéditeur doit être numérique.\n");
        }

        if (destinataire.isEmpty()) {
            erreurs.append("- L'ID du destinataire est obligatoire.\n");
        } else if (!destinataire.matches("\\d+")) {
            erreurs.append("- L'ID du destinataire doit être numérique.\n");
        }

        if (expediteur.matches("\\d+") && destinataire.matches("\\d+")) {
            if (Long.parseLong(expediteur) == Long.parseLong(destinataire)) {
                erreurs.append("- L'expéditeur et le destinataire ne doivent pas être identiques.\n");
            }
        }

        if (objet.isEmpty()) {
            erreurs.append("- L'objet est obligatoire.\n");
        } else if (objet.length() < 3) {
            erreurs.append("- L'objet doit contenir au moins 3 caractères.\n");
        } else if (objet.length() > 255) {
            erreurs.append("- L'objet ne doit pas dépasser 255 caractères.\n");
        }

        if (contenu.isEmpty()) {
            erreurs.append("- Le contenu est obligatoire.\n");
        } else if (contenu.length() < 5) {
            erreurs.append("- Le contenu doit contenir au moins 5 caractères.\n");
        }

        if (cbStatut.getValue() == null || cbStatut.getValue().trim().isEmpty()) {
            erreurs.append("- Le statut est obligatoire.\n");
        }

        if (cbPriorite.getValue() == null || cbPriorite.getValue().trim().isEmpty()) {
            erreurs.append("- La priorité est obligatoire.\n");
        }

        if (cbCategorie.getValue() == null || cbCategorie.getValue().trim().isEmpty()) {
            erreurs.append("- La catégorie est obligatoire.\n");
        }

        if (!parentId.isEmpty() && !parentId.matches("\\d+")) {
            erreurs.append("- Le parentId doit être numérique.\n");
        }

        if (!pieceJointe.isEmpty() && pieceJointe.length() > 255) {
            erreurs.append("- L'URL de la pièce jointe ne doit pas dépasser 255 caractères.\n");
        }

        return erreurs.toString();
    }

    private void viderChamps() {
        tfExpediteurId.clear();
        tfDestinataireId.clear();
        tfObjet.clear();
        taContenu.clear();
        tfPieceJointeUrl.clear();
        tfParentId.clear();

        cbStatut.setValue("envoye");
        cbPriorite.setValue("normal");
        cbCategorie.setValue("personnel");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}