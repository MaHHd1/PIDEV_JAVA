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
import java.util.Map;
import java.util.HashMap;
import services.ForumDiscussionService;
import services.UtilisateurService;

public class ForumDiscussionController {

    @FXML private TextField tfRechercheForum;
    @FXML private TableView<ForumDiscussion> tvForums;
    @FXML private TableColumn<ForumDiscussion, Long> colForumId;
    @FXML private TableColumn<ForumDiscussion, String> colForumTitre;
    @FXML private TableColumn<ForumDiscussion, String> colForumCreateur;
    @FXML private TableColumn<ForumDiscussion, String> colForumType;
    @FXML private TableColumn<ForumDiscussion, String> colForumStatut;
    private javafx.collections.ObservableList<ForumDiscussion> forumsList = FXCollections.observableArrayList();

    @FXML private javafx.scene.layout.VBox paneList;
    @FXML private javafx.scene.layout.VBox paneForm;

    private services.NotificationService notificationService = new services.NotificationService();

    @FXML
    private TextField tfTitre;

    @FXML
    private TextArea taDescription;

    @FXML
    private ComboBox<String> cbCreateur;

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
    private UtilisateurService utilisateurService;
    private Map<Long, String> idToNameMap = new HashMap<>();
    private Map<String, Long> nameToIdMap = new HashMap<>();

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
        if (this.utilisateurService == null) {
            this.utilisateurService = new UtilisateurService();
        }

        try {
            List<entities.Utilisateur> users = utilisateurService.getAllUtilisateurs();
            for (entities.Utilisateur u : users) {
                String fullname = u.getNom() + " " + u.getPrenom() + " (" + u.getEmail() + ")";
                idToNameMap.put(u.getId(), fullname);
                nameToIdMap.put(fullname, u.getId());
                if (cbCreateur != null) cbCreateur.getItems().add(fullname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cbType.setItems(FXCollections.observableArrayList("public", "prive"));
        cbStatut.setItems(FXCollections.observableArrayList("ouvert", "ferme"));

        cbType.setValue("public");
        cbStatut.setValue("ouvert");

        entities.Utilisateur currentUser = utils.UserSession.getCurrentUser();
        if (currentUser != null && cbCreateur != null) {
            String currentFullName = currentUser.getNom() + " " + currentUser.getPrenom() + " (" + currentUser.getEmail() + ")";
            cbCreateur.setValue(currentFullName);
            if (!currentUser.getType().equalsIgnoreCase("administrateur")) {
                cbCreateur.setDisable(true);
            }
        }

        if (tvForums != null) {
            colForumId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
            colForumTitre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titre"));
            if (colForumCreateur != null) {
                colForumCreateur.setCellValueFactory(cellData -> {
                    Long createurId = cellData.getValue().getCreateurId();
                    String nom = idToNameMap.getOrDefault(createurId, "Inconnu (" + createurId + ")");
                    return new javafx.beans.property.SimpleStringProperty(nom);
                });
            }
            colForumType.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
            colForumStatut.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("statut"));

            loadForums();

            if (tfRechercheForum != null) {
                javafx.collections.transformation.FilteredList<ForumDiscussion> filteredData = new javafx.collections.transformation.FilteredList<>(forumsList, b -> true);
                tfRechercheForum.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(forum -> {
                        if (newValue == null || newValue.isEmpty()) return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        if (forum.getTitre() != null && forum.getTitre().toLowerCase().contains(lowerCaseFilter)) return true;
                        if (forum.getDescription() != null && forum.getDescription().toLowerCase().contains(lowerCaseFilter)) return true;
                        return false;
                    });
                });
                javafx.collections.transformation.SortedList<ForumDiscussion> sortedData = new javafx.collections.transformation.SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(tvForums.comparatorProperty());
                tvForums.setItems(sortedData);
            }

            tvForums.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateForm(newSelection);
                }
            });
        }
    }

    private void loadForums() {
        try {
            forumsList.clear();
            forumsList.addAll(forumService.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateForm(ForumDiscussion forum) {
        tfTitre.setText(forum.getTitre());
        taDescription.setText(forum.getDescription());
        if (cbCreateur != null) {
            String name = idToNameMap.get(forum.getCreateurId());
            if (name != null) cbCreateur.setValue(name);
        }
        if (forum.getIdCours() != null) tfIdCours.setText(String.valueOf(forum.getIdCours()));
        else tfIdCours.clear();
        cbType.setValue(forum.getType());
        cbStatut.setValue(forum.getStatut());
        if (forum.getTags() != null) tfTags.setText(String.join(", ", forum.getTags()));
        else tfTags.clear();
        if (forum.getReglesModeration() != null) taReglesModeration.setText(forum.getReglesModeration());
        else taReglesModeration.clear();
        if (forum.getImageCouvertureUrl() != null) tfImageCouvertureUrl.setText(forum.getImageCouvertureUrl());
        else tfImageCouvertureUrl.clear();
        if (forum.getPieceJointeUrl() != null) tfPieceJointeUrl.setText(forum.getPieceJointeUrl());
        else tfPieceJointeUrl.clear();
    }

    @FXML
    public void showFormCreate() {
        viderChamps();
        tvForums.getSelectionModel().clearSelection();
        if (paneList != null) paneList.setVisible(false);
        if (paneForm != null) paneForm.setVisible(true);
    }

    @FXML
    public void showFormEdit() {
        ForumDiscussion selected = tvForums.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un forum à modifier.");
            return;
        }
        populateForm(selected);
        if (paneList != null) paneList.setVisible(false);
        if (paneForm != null) paneForm.setVisible(true);
    }

    @FXML
    public void hideForm() {
        if (paneForm != null) paneForm.setVisible(false);
        if (paneList != null) paneList.setVisible(true);
        tvForums.getSelectionModel().clearSelection();
        viderChamps();
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
            
            Long idCreateur = utils.UserSession.getCurrentUser().getId();
            if (cbCreateur != null && cbCreateur.getValue() != null) {
                Long mappedId = nameToIdMap.get(cbCreateur.getValue());
                if (mappedId != null) idCreateur = mappedId;
            }
            forum.setCreateurId(idCreateur);
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

            // Notification pour le créateur
            entities.Utilisateur currentUser = utils.UserSession.getCurrentUser();
            if (currentUser != null) {
                entities.Notification notif = new entities.Notification(
                    currentUser.getId(),
                    "Forum Créé",
                    "Votre forum '" + forum.getTitre() + "' a été publié avec succès."
                );
                notificationService.ajouter(notif);
            }

            if (tvForums != null) loadForums();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum ajouté avec succès.");
            hideForm();

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

            boolean isSelected = false;
            long idToModify = -1;
            if (tvForums != null && tvForums.getSelectionModel().getSelectedItem() != null) {
                isSelected = true;
                idToModify = tvForums.getSelectionModel().getSelectedItem().getId();
            }

            if (!isSelected) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun forum n'est sélectionné.");
                return;
            }

            if (isSelected) {
                try {
                    long id = idToModify;

                    entities.Utilisateur currentUser = utils.UserSession.getCurrentUser();
                    boolean isAdmin = currentUser != null && currentUser.getType().equalsIgnoreCase("administrateur");

                    ForumDiscussion fCheck = forumsList.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
                    if (fCheck != null && !isAdmin && fCheck.getCreateurId() != currentUser.getId()) {
                        showAlert(Alert.AlertType.ERROR, "Accès refusé", "Vous ne pouvez modifier que vos propres forums.");
                        return;
                    }

                    ForumDiscussion forum = new ForumDiscussion();
                    forum.setId(id);
                    forum.setTitre(tfTitre.getText().trim());
                    forum.setDescription(taDescription.getText().trim());
                    
                    Long idCreateur = fCheck != null ? fCheck.getCreateurId() : currentUser.getId();
                    if (isAdmin && cbCreateur != null && cbCreateur.getValue() != null) {
                        Long mappedId = nameToIdMap.get(cbCreateur.getValue());
                        if (mappedId != null) idCreateur = mappedId;
                    }
                    forum.setCreateurId(idCreateur);
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

                    if (tvForums != null) loadForums();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum modifié avec succès.");
                    hideForm();

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    public void supprimerForum() {
        boolean isSelected = false;
        long idToDelete = -1;
        if (tvForums != null && tvForums.getSelectionModel().getSelectedItem() != null) {
            isSelected = true;
            idToDelete = tvForums.getSelectionModel().getSelectedItem().getId();
        }

        if (!isSelected) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Suppression");
            dialog.setHeaderText("Supprimer un forum");
            dialog.setContentText("Entrer l'ID du forum à supprimer :");
            java.util.Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                try {
                    idToDelete = Long.parseLong(result.get().trim());
                    isSelected = true;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être numérique.");
                }
            } else if (result.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID est obligatoire.");
            }
        }

        if (isSelected) {
            try {
                long finalId = idToDelete;
                entities.Utilisateur currentUser = utils.UserSession.getCurrentUser();
                boolean isAdmin = currentUser != null && currentUser.getType().equalsIgnoreCase("administrateur");

                ForumDiscussion fCheck = forumsList.stream().filter(f -> f.getId() == finalId).findFirst().orElse(null);
                if (fCheck != null && !isAdmin && fCheck.getCreateurId() != currentUser.getId()) {
                    showAlert(Alert.AlertType.ERROR, "Accès refusé", "Vous ne pouvez supprimer que vos propres forums.");
                    return;
                }

                forumService.supprimer(idToDelete);
                if (tvForums != null) loadForums();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum supprimé avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
            }
        }
    }

    private String validerSaisie() {
        StringBuilder erreurs = new StringBuilder();

        String titre = tfTitre.getText().trim();
        String description = taDescription.getText().trim();
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
        tfIdCours.clear();
        tfTags.clear();
        taReglesModeration.clear();
        tfImageCouvertureUrl.clear();
        tfPieceJointeUrl.clear();

        cbType.setValue("public");
        cbStatut.setValue("ouvert");

        entities.Utilisateur currentUser = utils.UserSession.getCurrentUser();
        if (currentUser != null && cbCreateur != null) {
            String currentFullName = currentUser.getNom() + " " + currentUser.getPrenom() + " (" + currentUser.getEmail() + ")";
            cbCreateur.setValue(currentFullName);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}