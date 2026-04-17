package gui;

import entities.ForumDiscussion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ForumDiscussionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForumDiscussionController {

    @FXML
    private TextField tfTitre;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField tfCreateurId;

    @FXML
    private TextField tfIdCours;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private TextField tfTags;

    @FXML
    private TextArea taReglesModeration;

    @FXML
    private TextField tfImageCouvertureUrl;

    @FXML
    private TextField tfPieceJointeUrl;

    private ForumDiscussionService forumService;

    public void setConnection(Connection connection) {
        this.forumService = new ForumDiscussionService(connection);
    }

    private void verifyForumTable(Connection connection) {
        try {
            try (java.sql.Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS derniere_activite TIMESTAMP NULL");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS tags VARCHAR(255) DEFAULT ''");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS regles_moderation TEXT NULL");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS image_couverture_url VARCHAR(255) NULL");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS piece_jointe_url VARCHAR(255) NULL");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS likes INT DEFAULT 0");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS dislikes INT DEFAULT 0");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS signalements INT DEFAULT 0");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS est_modifie BOOLEAN DEFAULT FALSE");
                stmt.execute("ALTER TABLE forum_discussion ADD COLUMN IF NOT EXISTS date_modification TIMESTAMP NULL");
            }
        } catch (Exception e) {
            System.err.println("Note migration: " + e.getMessage());
        }
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

    @FXML
    public void initialize() {
        if (this.forumService == null) {
            Connection cnx = utils.DBConnection.getInstance().getConnection();
            verifyForumTable(cnx);
            this.forumService = new ForumDiscussionService(cnx);
        }
        cbType.setItems(FXCollections.observableArrayList("public", "prive"));
        cbStatut.setItems(FXCollections.observableArrayList("ouvert", "ferme"));

        cbType.setValue("public");
        cbStatut.setValue("ouvert");
    }

    @FXML
    public void ajouterForum() {
        try {
            String erreurs = validerSaisie();

            if (!erreurs.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreurs de saisie", erreurs);
                return;
            }

            ForumDiscussion forum = new ForumDiscussion();
            forum.setTitre(tfTitre.getText().trim());
            forum.setDescription(taDescription.getText().trim());
            forum.setCreateurId(Long.parseLong(tfCreateurId.getText().trim()));
            forum.setDateCreation(LocalDateTime.now());
            forum.setDerniereActivite(LocalDateTime.now());
            forum.setType(cbType.getValue());
            forum.setStatut(cbStatut.getValue());

            String idCours = tfIdCours.getText().trim();
            if (!idCours.isEmpty()) {
                forum.setIdCours(Integer.parseInt(idCours));
            }

            String tagsTexte = tfTags.getText().trim();
            if (!tagsTexte.isEmpty()) {
                List<String> tags = Arrays.stream(tagsTexte.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                forum.setTags(tags);
            }

            String regles = taReglesModeration.getText().trim();
            if (!regles.isEmpty()) {
                forum.setReglesModeration(regles);
            }

            String image = tfImageCouvertureUrl.getText().trim();
            if (!image.isEmpty()) {
                forum.setImageCouvertureUrl(image);
            }

            String pieceJointe = tfPieceJointeUrl.getText().trim();
            if (!pieceJointe.isEmpty()) {
                forum.setPieceJointeUrl(pieceJointe);
            }

            forumService.ajouter(forum);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum ajouté avec succès.");
            viderChamps();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    public void modifierForum() {
        try {
            String erreurs = validerSaisie();

            if (!erreurs.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreurs de saisie", erreurs);
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Modification");
            dialog.setHeaderText("Modifier un forum");
            dialog.setContentText("Entrer l'ID du forum à modifier :");

            dialog.showAndWait().ifPresent(idStr -> {
                try {
                    long id = Long.parseLong(idStr.trim());

                    ForumDiscussion forum = new ForumDiscussion();
                    forum.setId(id);
                    forum.setTitre(tfTitre.getText().trim());
                    forum.setDescription(taDescription.getText().trim());
                    forum.setCreateurId(Long.parseLong(tfCreateurId.getText().trim()));
                    forum.setDateCreation(LocalDateTime.now());
                    forum.setDerniereActivite(LocalDateTime.now());
                    forum.setType(cbType.getValue());
                    forum.setStatut(cbStatut.getValue());

                    String idCours = tfIdCours.getText().trim();
                    if (!idCours.isEmpty()) {
                        forum.setIdCours(Integer.parseInt(idCours));
                    }

                    String tagsTexte = tfTags.getText().trim();
                    if (!tagsTexte.isEmpty()) {
                        List<String> tags = Arrays.stream(tagsTexte.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());
                        forum.setTags(tags);
                    }

                    String regles = taReglesModeration.getText().trim();
                    if (!regles.isEmpty()) {
                        forum.setReglesModeration(regles);
                    }

                    String image = tfImageCouvertureUrl.getText().trim();
                    if (!image.isEmpty()) {
                        forum.setImageCouvertureUrl(image);
                    }

                    String pieceJointe = tfPieceJointeUrl.getText().trim();
                    if (!pieceJointe.isEmpty()) {
                        forum.setPieceJointeUrl(pieceJointe);
                    }

                    forum.setEstModifie(true);
                    forum.setDateModification(LocalDateTime.now());

                    forumService.modifier(forum);

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum modifié avec succès.");
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
    public void supprimerForum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Suppression");
        dialog.setHeaderText("Supprimer un forum");
        dialog.setContentText("Entrer l'ID du forum à supprimer :");

        dialog.showAndWait().ifPresent(idStr -> {
            try {
                if (idStr.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID est obligatoire.");
                    return;
                }

                long id = Long.parseLong(idStr.trim());
                forumService.supprimer(id);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum supprimé avec succès.");

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être numérique.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
            }
        });
    }

    private String validerSaisie() {
        StringBuilder erreurs = new StringBuilder();

        String titre = tfTitre.getText().trim();
        String description = taDescription.getText().trim();
        String createurId = tfCreateurId.getText().trim();
        String idCours = tfIdCours.getText().trim();
        String image = tfImageCouvertureUrl.getText().trim();
        String pieceJointe = tfPieceJointeUrl.getText().trim();

        if (titre.isEmpty()) {
            erreurs.append("- Le titre est obligatoire.\n");
        } else if (titre.length() < 3) {
            erreurs.append("- Le titre doit contenir au moins 3 caractères.\n");
        } else if (titre.length() > 255) {
            erreurs.append("- Le titre ne doit pas dépasser 255 caractères.\n");
        }

        if (description.isEmpty()) {
            erreurs.append("- La description est obligatoire.\n");
        } else if (description.length() < 10) {
            erreurs.append("- La description doit contenir au moins 10 caractères.\n");
        }

        if (createurId.isEmpty()) {
            erreurs.append("- L'ID du créateur est obligatoire.\n");
        } else if (!createurId.matches("\\d+")) {
            erreurs.append("- L'ID du créateur doit être numérique.\n");
        }

        if (!idCours.isEmpty() && !idCours.matches("\\d+")) {
            erreurs.append("- L'ID du cours doit être numérique.\n");
        }

        if (cbType.getValue() == null || cbType.getValue().trim().isEmpty()) {
            erreurs.append("- Le type est obligatoire.\n");
        }

        if (cbStatut.getValue() == null || cbStatut.getValue().trim().isEmpty()) {
            erreurs.append("- Le statut est obligatoire.\n");
        }

        if (!image.isEmpty() && image.length() > 255) {
            erreurs.append("- L'URL de l'image ne doit pas dépasser 255 caractères.\n");
        }

        if (!pieceJointe.isEmpty() && pieceJointe.length() > 255) {
            erreurs.append("- L'URL de la pièce jointe ne doit pas dépasser 255 caractères.\n");
        }

        return erreurs.toString();
    }

    private void viderChamps() {
        tfTitre.clear();
        taDescription.clear();
        tfCreateurId.clear();
        tfIdCours.clear();
        tfTags.clear();
        taReglesModeration.clear();
        tfImageCouvertureUrl.clear();
        tfPieceJointeUrl.clear();

        cbType.setValue("public");
        cbStatut.setValue("ouvert");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}