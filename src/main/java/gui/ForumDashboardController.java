package gui;

import entities.ForumDiscussion;
import entities.Utilisateur;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.ForumDiscussionService;
import services.UtilisateurService;
import utils.UserSession;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForumDashboardController implements MainControllerAware, MainControllerAwareEtudiant {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubtitleLabel;
    @FXML private Label roleBadgeLabel;
    @FXML private Label creatorInfoLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<ForumDiscussion> forumTable;
    @FXML private TableColumn<ForumDiscussion, Long> idColumn;
    @FXML private TableColumn<ForumDiscussion, String> titreColumn;
    @FXML private TableColumn<ForumDiscussion, String> auteurColumn;
    @FXML private TableColumn<ForumDiscussion, String> typeColumn;
    @FXML private TableColumn<ForumDiscussion, String> statutColumn;
    @FXML private TableColumn<ForumDiscussion, String> activiteColumn;
    @FXML private TableColumn<ForumDiscussion, String> tagsColumn;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField courseIdField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField tagsField;
    @FXML private TextArea moderationArea;
    @FXML private TextField imageField;
    @FXML private TextField attachmentField;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;

    private final ForumDiscussionService forumService = new ForumDiscussionService();
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ObservableList<ForumDiscussion> forums = FXCollections.observableArrayList();
    private final Map<Long, String> userNames = new HashMap<>();

    private Utilisateur currentUser;
    private ForumDiscussion editingForum;

    @FXML
    public void initialize() {
        currentUser = UserSession.getCurrentUser();
        typeCombo.setItems(FXCollections.observableArrayList("public", "prive"));
        statutCombo.setItems(FXCollections.observableArrayList("ouvert", "ferme"));
        configureTable();
        configureRoleUi();
        loadUsers();
        refreshForums();
        clearForm();
    }

    @Override
    public void setMainController(MainLayoutEnseignantController controller) {
    }

    @Override
    public void setMainController(MainLayoutEtudiantController controller) {
    }

    @FXML
    private void handleRefresh() {
        refreshForums();
    }

    @FXML
    private void handleSaveForum() {
        if (currentUser == null) {
            showError("Aucun utilisateur connecte.");
            return;
        }

        String validationError = validateForm();
        if (validationError != null) {
            showError(validationError);
            return;
        }

        boolean updating = editingForum != null;
        if (updating && !canModify()) {
            showError("Votre role ne peut pas modifier une discussion existante.");
            return;
        }

        try {
            ForumDiscussion forum = updating ? editingForum : new ForumDiscussion();
            forum.setTitre(titreField.getText().trim());
            forum.setDescription(descriptionArea.getText().trim());
            forum.setCreateurId(updating ? forum.getCreateurId() : currentUser.getId());
            forum.setType(typeCombo.getValue());
            forum.setStatut(statutCombo.getValue());
            forum.setDerniereActivite(LocalDateTime.now());
            forum.setReglesModeration(blankToNull(moderationArea.getText()));
            forum.setImageCouvertureUrl(blankToNull(imageField.getText()));
            forum.setPieceJointeUrl(blankToNull(attachmentField.getText()));
            forum.setTags(parseTags(tagsField.getText()));

            String courseValue = courseIdField.getText() == null ? "" : courseIdField.getText().trim();
            forum.setIdCours(courseValue.isEmpty() ? null : Integer.parseInt(courseValue));

            if (updating) {
                forum.setEstModifie(true);
                forum.setDateModification(LocalDateTime.now());
                forumService.modifier(forum);
                statusLabel.setText("Discussion mise a jour.");
            } else {
                forum.setDateCreation(LocalDateTime.now());
                forumService.ajouter(forum);
                statusLabel.setText("Discussion publiee.");
            }

            refreshForums();
            clearForm();
        } catch (SQLException e) {
            showError("Erreur base de donnees: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteForum() {
        if (editingForum == null) {
            showError("Selectionnez une discussion a supprimer.");
            return;
        }
        if (!canDelete()) {
            showError("Seul l'administrateur peut supprimer une discussion.");
            return;
        }

        try {
            forumService.supprimer(editingForum.getId());
            statusLabel.setText("Discussion supprimee.");
            refreshForums();
            clearForm();
        } catch (SQLException e) {
            showError("Suppression impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        titreColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitre()));
        auteurColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(resolveUserName(data.getValue().getCreateurId())));
        typeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType()));
        statutColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatut()));
        activiteColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDate(data.getValue().getDerniereActivite())));
        tagsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.join(", ", data.getValue().getTags())));
        forumTable.setItems(forums);
        forumTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> populateForm(newValue));
    }

    private void configureRoleUi() {
        String role = currentUser == null ? "invite" : currentUser.getType();
        roleBadgeLabel.setText(role.toUpperCase());
        creatorInfoLabel.setText(currentUser == null ? "-" : currentUser.getNomComplet() + " (#" + currentUser.getId() + ")");

        if ("administrateur".equalsIgnoreCase(role)) {
            pageTitleLabel.setText("Forums collaboratifs");
            pageSubtitleLabel.setText("Vue admin: creation, modification et suppression des discussions importees depuis dev-ahmed.");
        } else if ("enseignant".equalsIgnoreCase(role)) {
            pageTitleLabel.setText("Forums enseignant");
            pageSubtitleLabel.setText("Publiez ou mettez a jour des discussions sans casser votre dashboard actuel.");
        } else {
            pageTitleLabel.setText("Forums etudiant");
            pageSubtitleLabel.setText("Consultez les discussions et publiez de nouveaux sujets dans le meme style que vos autres pages.");
        }

        deleteButton.setVisible(canDelete());
        deleteButton.setManaged(canDelete());
        saveButton.setText(canModify() ? "Publier / Mettre a jour" : "Publier");
    }

    private void loadUsers() {
        try {
            for (Utilisateur utilisateur : utilisateurService.getAllUtilisateurs()) {
                userNames.put(utilisateur.getId(), utilisateur.getNomComplet());
            }
        } catch (SQLException e) {
            statusLabel.setText("Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private void refreshForums() {
        try {
            forums.setAll(forumService.getAll());
        } catch (SQLException e) {
            showError("Chargement des discussions impossible: " + e.getMessage());
        }
    }

    private void populateForm(ForumDiscussion forum) {
        editingForum = forum;
        if (forum == null) {
            return;
        }

        titreField.setText(forum.getTitre());
        descriptionArea.setText(forum.getDescription());
        courseIdField.setText(forum.getIdCours() == null ? "" : String.valueOf(forum.getIdCours()));
        typeCombo.setValue(forum.getType());
        statutCombo.setValue(forum.getStatut());
        tagsField.setText(String.join(", ", forum.getTags()));
        moderationArea.setText(nullToEmpty(forum.getReglesModeration()));
        imageField.setText(nullToEmpty(forum.getImageCouvertureUrl()));
        attachmentField.setText(nullToEmpty(forum.getPieceJointeUrl()));
        statusLabel.setText("Discussion #" + forum.getId() + " selectionnee.");
    }

    private void clearForm() {
        editingForum = null;
        forumTable.getSelectionModel().clearSelection();
        titreField.clear();
        descriptionArea.clear();
        courseIdField.clear();
        typeCombo.setValue("public");
        statutCombo.setValue("ouvert");
        tagsField.clear();
        moderationArea.clear();
        imageField.clear();
        attachmentField.clear();
    }

    private String validateForm() {
        if (titreField.getText() == null || titreField.getText().trim().length() < 3) {
            return "Le titre doit contenir au moins 3 caracteres.";
        }
        if (descriptionArea.getText() == null || descriptionArea.getText().trim().length() < 10) {
            return "La description doit contenir au moins 10 caracteres.";
        }
        String courseValue = courseIdField.getText() == null ? "" : courseIdField.getText().trim();
        if (!courseValue.isEmpty() && !courseValue.matches("\\d+")) {
            return "L'ID du cours doit etre numerique.";
        }
        return null;
    }

    private boolean canModify() {
        return currentUser != null && ("administrateur".equalsIgnoreCase(currentUser.getType())
                || "enseignant".equalsIgnoreCase(currentUser.getType()));
    }

    private boolean canDelete() {
        return currentUser != null && "administrateur".equalsIgnoreCase(currentUser.getType());
    }

    private String resolveUserName(Long userId) {
        if (userId == null) {
            return "-";
        }
        return userNames.getOrDefault(userId, "Utilisateur #" + userId);
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATE_FORMATTER);
    }

    private List<String> parseTags(String rawTags) {
        if (rawTags == null || rawTags.isBlank()) {
            return List.of();
        }
        return List.of(rawTags.split(",")).stream()
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Forum");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
