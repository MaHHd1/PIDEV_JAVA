package gui;

import entities.Evenement;
import entities.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.EvenementService;
import utils.UserSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventManagementController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private TableView<Evenement> eventTable;

    @FXML
    private TableColumn<Evenement, String> idColumn;

    @FXML
    private TableColumn<Evenement, String> titreColumn;

    @FXML
    private TableColumn<Evenement, String> lieuColumn;

    @FXML
    private TableColumn<Evenement, String> typeColumn;

    @FXML
    private TableColumn<Evenement, String> dateDebutColumn;

    @FXML
    private TableColumn<Evenement, String> statutColumn;

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private TextField heureDebutField;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField heureFinField;

    @FXML
    private TextField lieuField;

    @FXML
    private TextField capaciteField;

    @FXML
    private ComboBox<String> statutCombo;

    @FXML
    private ComboBox<String> visibiliteCombo;

    @FXML
    private Label createurLabel;

    @FXML
    private Label formTitleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label detailTitleLabel;

    @FXML
    private Label detailMetaLabel;

    @FXML
    private Label detailDescriptionLabel;

    @FXML
    private Label detailCapacityLabel;

    @FXML
    private Label detailVisibilityLabel;

    private final EvenementService evenementService = new EvenementService();
    private final ObservableList<Evenement> events = FXCollections.observableArrayList();
    private Evenement editingEvenement;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId() != null ? String.valueOf(cell.getValue().getId()) : "-"));
        titreColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitre()));
        lieuColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLieu()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTypeEvenement()));
        dateDebutColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatDateTime(cell.getValue().getDateDebut())));
        statutColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));

        typeCombo.setItems(FXCollections.observableArrayList("Conference", "Workshop", "Competition", "Career", "Networking", "Cultural"));
        statutCombo.setItems(FXCollections.observableArrayList("En attente", "Planifie", "Ouvert", "Termine", "Annule"));
        visibiliteCombo.setItems(FXCollections.observableArrayList("Public", "Prive"));

        eventTable.setItems(events);
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> showEventDetails(newValue));

        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));
        heureDebutField.setText("09:00");
        heureFinField.setText("17:00");
        statutCombo.setValue("En attente");
        visibiliteCombo.setValue("Public");
        typeCombo.setValue("Conference");

        Utilisateur currentUser = UserSession.getCurrentUser();
        createurLabel.setText(currentUser != null ? currentUser.getNomComplet() : "Utilisateur inconnu");

        loadEvents();
        resetForm();
    }

    @FXML
    private void handleSaveEvent() {
        String validationError = validateForm();
        if (validationError != null) {
            setStatus(validationError, true);
            return;
        }

        try {
            Evenement evenement = buildEventFromForm();
            if (editingEvenement == null) {
                evenementService.create(evenement);
                setStatus("Evenement ajoute avec succes.", false);
            } else {
                evenementService.update(evenement);
                setStatus("Evenement mis a jour avec succes.", false);
            }
            loadEvents();
            resetForm();
        } catch (SQLException e) {
            setStatus("Enregistrement impossible: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleNewEvent() {
        resetForm();
    }

    @FXML
    private void handleEditSelected() {
        Evenement selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Selectionnez un evenement a modifier.", true);
            return;
        }
        editingEvenement = selected;
        formTitleLabel.setText("Modifier l'evenement");
        titreField.setText(selected.getTitre());
        descriptionArea.setText(defaultValue(selected.getDescription()));
        typeCombo.setValue(defaultValue(selected.getTypeEvenement()));
        if (selected.getDateDebut() != null) {
            dateDebutPicker.setValue(selected.getDateDebut().toLocalDate());
            heureDebutField.setText(selected.getDateDebut().toLocalTime().toString());
        }
        if (selected.getDateFin() != null) {
            dateFinPicker.setValue(selected.getDateFin().toLocalDate());
            heureFinField.setText(selected.getDateFin().toLocalTime().toString());
        }
        lieuField.setText(defaultValue(selected.getLieu()));
        capaciteField.setText(selected.getCapaciteMax() != null ? String.valueOf(selected.getCapaciteMax()) : "");
        statutCombo.setValue(defaultValue(selected.getStatut()));
        visibiliteCombo.setValue(defaultValue(selected.getVisibilite()));
        setStatus("Mode edition actif pour l'evenement selectionne.", false);
    }

    @FXML
    private void handleDeleteSelected() {
        Evenement selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            setStatus("Selectionnez un evenement a supprimer.", true);
            return;
        }

        try {
            evenementService.delete(selected.getId());
            loadEvents();
            resetForm();
            setStatus("Evenement supprime.", false);
        } catch (SQLException e) {
            setStatus("Suppression impossible: " + e.getMessage(), true);
        }
    }

    private void loadEvents() {
        try {
            List<Evenement> loadedEvents = evenementService.getAll();
            events.setAll(loadedEvents);
            if (!loadedEvents.isEmpty()) {
                eventTable.getSelectionModel().selectFirst();
            } else {
                showEventDetails(null);
            }
        } catch (SQLException e) {
            setStatus("Chargement impossible: " + e.getMessage(), true);
        }
    }

    private Evenement buildEventFromForm() {
        Evenement evenement = editingEvenement != null ? editingEvenement : new Evenement();
        Utilisateur currentUser = UserSession.getCurrentUser();
        evenement.setCreateurId(currentUser != null && currentUser.getId() != null ? currentUser.getId().intValue() : null);
        evenement.setTitre(titreField.getText().trim());
        evenement.setDescription(descriptionArea.getText().trim());
        evenement.setTypeEvenement(typeCombo.getValue());
        evenement.setDateDebut(LocalDateTime.of(dateDebutPicker.getValue(), LocalTime.parse(heureDebutField.getText().trim())));
        evenement.setDateFin(LocalDateTime.of(dateFinPicker.getValue(), LocalTime.parse(heureFinField.getText().trim())));
        evenement.setLieu(lieuField.getText().trim());
        evenement.setCapaciteMax(Integer.parseInt(capaciteField.getText().trim()));
        evenement.setStatut(statutCombo.getValue());
        evenement.setVisibilite(visibiliteCombo.getValue());
        return evenement;
    }

    private String validateForm() {
        if (titreField.getText() == null || titreField.getText().trim().length() < 3) {
            return "Le titre doit contenir au moins 3 caracteres.";
        }
        if (lieuField.getText() == null || lieuField.getText().trim().length() < 2) {
            return "Le lieu est obligatoire.";
        }
        if (typeCombo.getValue() == null || typeCombo.getValue().isBlank()) {
            return "Le type d'evenement est obligatoire.";
        }
        if (statutCombo.getValue() == null || statutCombo.getValue().isBlank()) {
            return "Le statut est obligatoire.";
        }
        if (visibiliteCombo.getValue() == null || visibiliteCombo.getValue().isBlank()) {
            return "La visibilite est obligatoire.";
        }
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            return "Les dates de debut et de fin sont obligatoires.";
        }
        try {
            LocalDateTime debut = LocalDateTime.of(dateDebutPicker.getValue(), LocalTime.parse(heureDebutField.getText().trim()));
            LocalDateTime fin = LocalDateTime.of(dateFinPicker.getValue(), LocalTime.parse(heureFinField.getText().trim()));
            if (!fin.isAfter(debut)) {
                return "La date de fin doit etre apres la date de debut.";
            }
        } catch (Exception e) {
            return "Les heures doivent etre au format HH:mm.";
        }
        try {
            int capacite = Integer.parseInt(capaciteField.getText().trim());
            if (capacite <= 0) {
                return "La capacite doit etre superieure a zero.";
            }
        } catch (NumberFormatException e) {
            return "La capacite doit etre un entier valide.";
        }
        return null;
    }

    private void resetForm() {
        editingEvenement = null;
        formTitleLabel.setText("Nouvel evenement");
        titreField.clear();
        descriptionArea.clear();
        typeCombo.setValue("Conference");
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));
        heureDebutField.setText("09:00");
        heureFinField.setText("17:00");
        lieuField.clear();
        capaciteField.setText("100");
        statutCombo.setValue("En attente");
        visibiliteCombo.setValue("Public");
    }

    private void showEventDetails(Evenement evenement) {
        if (evenement == null) {
            detailTitleLabel.setText("Aucun evenement selectionne");
            detailMetaLabel.setText("Selectionnez un evenement dans la liste.");
            detailDescriptionLabel.setText("-");
            detailCapacityLabel.setText("-");
            detailVisibilityLabel.setText("-");
            return;
        }

        detailTitleLabel.setText(evenement.getTitre());
        detailMetaLabel.setText(defaultValue(evenement.getTypeEvenement()) + " | " + defaultValue(evenement.getLieu()) + " | " + formatDateTime(evenement.getDateDebut()));
        detailDescriptionLabel.setText(defaultValue(evenement.getDescription()));
        detailCapacityLabel.setText("Capacite: " + (evenement.getCapaciteMax() != null ? evenement.getCapaciteMax() : "-") + " | Statut: " + defaultValue(evenement.getStatut()));
        detailVisibilityLabel.setText("Visibilite: " + defaultValue(evenement.getVisibilite()) + " | Fin: " + formatDateTime(evenement.getDateFin()));
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message != null ? message : "");
        statusLabel.getStyleClass().removeAll("error-status", "success-status", "neutral-status");
        statusLabel.getStyleClass().add(error ? "error-status" : "success-status");
    }

    private String formatDateTime(LocalDateTime value) {
        return value != null ? value.format(DATE_TIME_FORMATTER) : "-";
    }

    private String defaultValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
