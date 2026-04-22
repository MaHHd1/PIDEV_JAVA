package gui;

import entities.Message;
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
import javafx.util.StringConverter;
import services.MessageService;
import services.UtilisateurService;
import utils.UserSession;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageDashboardController implements MainControllerAware, MainControllerAwareEtudiant {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubtitleLabel;
    @FXML private Label roleBadgeLabel;
    @FXML private Label senderInfoLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<Message> messageTable;
    @FXML private TableColumn<Message, Long> idColumn;
    @FXML private TableColumn<Message, String> directionColumn;
    @FXML private TableColumn<Message, String> contactColumn;
    @FXML private TableColumn<Message, String> subjectColumn;
    @FXML private TableColumn<Message, String> priorityColumn;
    @FXML private TableColumn<Message, String> statutColumn;
    @FXML private TableColumn<Message, String> dateColumn;
    @FXML private Label detailSubjectLabel;
    @FXML private Label detailMetaLabel;
    @FXML private Label detailAttachmentLabel;
    @FXML private Label detailParentLabel;
    @FXML private TextField subjectField;
    @FXML private TextArea contentArea;
    @FXML private ComboBox<Utilisateur> recipientCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField attachmentField;
    @FXML private TextField parentIdField;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button markReadButton;

    private final MessageService messageService = new MessageService();
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final ObservableList<Utilisateur> users = FXCollections.observableArrayList();
    private final Map<Long, Utilisateur> usersById = new HashMap<>();

    private Utilisateur currentUser;
    private Message editingMessage;

    @FXML
    public void initialize() {
        currentUser = UserSession.getCurrentUser();
        configureCombos();
        configureTable();
        configureRoleUi();
        loadUsers();
        refreshMessages();
        clearForm();
        clearDetail();
    }

    @Override
    public void setMainController(MainLayoutEnseignantController controller) {
    }

    @Override
    public void setMainController(MainLayoutEtudiantController controller) {
    }

    @FXML
    private void handleRefresh() {
        refreshMessages();
    }

    @FXML
    private void handleSaveMessage() {
        if (currentUser == null) {
            showError("Aucun utilisateur connecte.");
            return;
        }
        if (recipientCombo.getValue() == null) {
            showError("Choisissez un destinataire.");
            return;
        }
        if (subjectField.getText() == null || subjectField.getText().trim().length() < 3) {
            showError("L'objet doit contenir au moins 3 caracteres.");
            return;
        }
        if (contentArea.getText() == null || contentArea.getText().trim().length() < 5) {
            showError("Le contenu doit contenir au moins 5 caracteres.");
            return;
        }

        boolean updating = editingMessage != null;
        if (updating && !canEditMessage(editingMessage)) {
            showError("Vous ne pouvez pas modifier ce message.");
            return;
        }

        try {
            Message message = updating ? editingMessage : new Message();
            message.setExpediteurId(updating ? message.getExpediteurId() : currentUser.getId());
            message.setDestinataireId(recipientCombo.getValue().getId());
            message.setObjet(subjectField.getText().trim());
            message.setContenu(contentArea.getText().trim());
            message.setPriorite(priorityCombo.getValue());
            message.setCategorie(categoryCombo.getValue());
            message.setStatut(statutCombo.getValue());
            message.setPieceJointeUrl(blankToNull(attachmentField.getText()));
            message.setParentId(parseLong(parentIdField.getText()));

            if (updating) {
                messageService.modifier(message);
                statusLabel.setText("Message mis a jour.");
            } else {
                message.setDateEnvoi(LocalDateTime.now());
                messageService.ajouter(message);
                statusLabel.setText("Message envoye.");
            }

            refreshMessages();
            clearForm();
        } catch (SQLException e) {
            showError("Erreur base de donnees: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteMessage() {
        if (editingMessage == null) {
            showError("Selectionnez un message a supprimer.");
            return;
        }
        if (!canDeleteMessage(editingMessage)) {
            showError("Seul l'administrateur peut supprimer un message.");
            return;
        }

        try {
            messageService.supprimer(editingMessage.getId());
            statusLabel.setText("Message supprime.");
            refreshMessages();
            clearForm();
            clearDetail();
        } catch (SQLException e) {
            showError("Suppression impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleMarkRead() {
        if (editingMessage == null) {
            showError("Selectionnez un message a marquer comme lu.");
            return;
        }
        if (currentUser == null || !currentUser.getId().equals(editingMessage.getDestinataireId())) {
            showError("Seul le destinataire peut marquer ce message comme lu.");
            return;
        }

        try {
            messageService.marquerCommeLu(editingMessage.getId());
            statusLabel.setText("Message marque comme lu.");
            refreshMessages();
        } catch (SQLException e) {
            showError("Mise a jour impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    private void configureCombos() {
        priorityCombo.setItems(FXCollections.observableArrayList("basse", "normal", "haute"));
        categoryCombo.setItems(FXCollections.observableArrayList("personnel", "important", "administratif"));
        statutCombo.setItems(FXCollections.observableArrayList("envoye", "lu"));
        recipientCombo.setItems(users);
        recipientCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Utilisateur utilisateur) {
                return utilisateur == null ? "" : utilisateur.getNomComplet() + " (" + utilisateur.getType() + ")";
            }

            @Override
            public Utilisateur fromString(String string) {
                return null;
            }
        });
    }

    private void configureTable() {
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        directionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(resolveDirection(data.getValue())));
        contactColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(resolveContact(data.getValue())));
        subjectColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getObjet()));
        priorityColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPriorite()));
        statutColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatut()));
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDate(data.getValue().getDateEnvoi())));
        messageTable.setItems(messages);
        messageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> populateSelection(newValue));
    }

    private void configureRoleUi() {
        String role = currentUser == null ? "invite" : currentUser.getType();
        roleBadgeLabel.setText(role.toUpperCase());
        senderInfoLabel.setText(currentUser == null ? "-" : currentUser.getNomComplet() + " (#" + currentUser.getId() + ")");

        if ("administrateur".equalsIgnoreCase(role)) {
            pageTitleLabel.setText("Messagerie admin");
            pageSubtitleLabel.setText("Vue globale des messages importes depuis dev-ahmed avec edition et suppression admin.");
        } else if ("enseignant".equalsIgnoreCase(role)) {
            pageTitleLabel.setText("Messagerie enseignant");
            pageSubtitleLabel.setText("Envoyez des messages et mettez a jour ceux que vous avez deja envoyes.");
        } else {
            pageTitleLabel.setText("Messagerie etudiant");
            pageSubtitleLabel.setText("Envoyez des messages depuis le meme dashboard, sans retour aux anciennes pages.");
        }

        deleteButton.setVisible(canDeleteCurrentRole());
        deleteButton.setManaged(canDeleteCurrentRole());
    }

    private void loadUsers() {
        try {
            List<Utilisateur> allUsers = utilisateurService.getAllUtilisateurs();
            users.setAll(allUsers.stream()
                    .filter(user -> currentUser == null || !user.getId().equals(currentUser.getId()))
                    .toList());
            for (Utilisateur user : allUsers) {
                usersById.put(user.getId(), user);
            }
        } catch (SQLException e) {
            statusLabel.setText("Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private void refreshMessages() {
        try {
            Map<Long, Message> merged = new LinkedHashMap<>();
            if (currentUser != null && "administrateur".equalsIgnoreCase(currentUser.getType())) {
                for (Message message : messageService.getAll()) {
                    merged.put(message.getId(), message);
                }
            } else if (currentUser != null) {
                for (Message message : messageService.getMessagesRecus(currentUser.getId())) {
                    merged.put(message.getId(), message);
                }
                for (Message message : messageService.getMessagesEnvoyes(currentUser.getId())) {
                    merged.put(message.getId(), message);
                }
            }
            messages.setAll(merged.values());
        } catch (SQLException e) {
            showError("Chargement des messages impossible: " + e.getMessage());
        }
    }

    private void populateSelection(Message message) {
        editingMessage = message;
        if (message == null) {
            clearDetail();
            return;
        }

        detailSubjectLabel.setText(message.getObjet());
        detailMetaLabel.setText(resolveDirection(message) + " | " + resolveContact(message) + " | " + formatDate(message.getDateEnvoi()));
        detailAttachmentLabel.setText(message.getPieceJointeUrl() == null ? "Aucune piece jointe" : message.getPieceJointeUrl());
        detailParentLabel.setText(message.getParentId() == null ? "Aucun fil parent" : "Reponse au message #" + message.getParentId());

        subjectField.setText(message.getObjet());
        contentArea.setText(message.getContenu());
        recipientCombo.setValue(usersById.get(message.getDestinataireId()));
        priorityCombo.setValue(message.getPriorite());
        categoryCombo.setValue(message.getCategorie());
        statutCombo.setValue(message.getStatut());
        attachmentField.setText(nullToEmpty(message.getPieceJointeUrl()));
        parentIdField.setText(message.getParentId() == null ? "" : String.valueOf(message.getParentId()));

        markReadButton.setDisable(currentUser == null
                || !currentUser.getId().equals(message.getDestinataireId())
                || message.isLu());
        statusLabel.setText("Message #" + message.getId() + " selectionne.");
    }

    private void clearForm() {
        editingMessage = null;
        messageTable.getSelectionModel().clearSelection();
        recipientCombo.getSelectionModel().clearSelection();
        subjectField.clear();
        contentArea.clear();
        priorityCombo.setValue("normal");
        categoryCombo.setValue("personnel");
        statutCombo.setValue("envoye");
        attachmentField.clear();
        parentIdField.clear();
        markReadButton.setDisable(true);
    }

    private void clearDetail() {
        detailSubjectLabel.setText("Selectionnez un message");
        detailMetaLabel.setText("-");
        detailAttachmentLabel.setText("Aucune piece jointe");
        detailParentLabel.setText("Aucun fil parent");
    }

    private boolean canEditMessage(Message message) {
        return currentUser != null && ("administrateur".equalsIgnoreCase(currentUser.getType())
                || ("enseignant".equalsIgnoreCase(currentUser.getType()) && currentUser.getId().equals(message.getExpediteurId())));
    }

    private boolean canDeleteMessage(Message message) {
        return canDeleteCurrentRole() && message != null;
    }

    private boolean canDeleteCurrentRole() {
        return currentUser != null && "administrateur".equalsIgnoreCase(currentUser.getType());
    }

    private String resolveDirection(Message message) {
        if (currentUser != null && currentUser.getId().equals(message.getDestinataireId())) {
            return "Recu";
        }
        if (currentUser != null && currentUser.getId().equals(message.getExpediteurId())) {
            return "Envoye";
        }
        return "Global";
    }

    private String resolveContact(Message message) {
        Long contactId = currentUser != null && currentUser.getId().equals(message.getDestinataireId())
                ? message.getExpediteurId()
                : message.getDestinataireId();
        Utilisateur user = usersById.get(contactId);
        return user == null ? "Utilisateur #" + contactId : user.getNomComplet();
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATE_FORMATTER);
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        if (!value.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Le parent ID doit etre numerique.");
        }
        return Long.parseLong(value.trim());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Messagerie");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
