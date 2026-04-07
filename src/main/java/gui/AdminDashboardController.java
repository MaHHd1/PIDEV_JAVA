package gui;

import entities.Administrateur;
import entities.Enseignant;
import entities.Etudiant;
import entities.Utilisateur;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import services.AuthService;
import services.UtilisateurService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label studentsCountLabel;

    @FXML
    private Label teachersCountLabel;

    @FXML
    private Label adminsCountLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> roleFilterCombo;

    @FXML
    private TableView<UtilisateurRow> usersTable;

    @FXML
    private TableColumn<UtilisateurRow, Long> idColumn;

    @FXML
    private TableColumn<UtilisateurRow, String> fullNameColumn;

    @FXML
    private TableColumn<UtilisateurRow, String> emailColumn;

    @FXML
    private TableColumn<UtilisateurRow, String> roleColumn;

    @FXML
    private TableColumn<UtilisateurRow, String> identifierColumn;

    @FXML
    private TableColumn<UtilisateurRow, String> statusColumn;

    @FXML
    private TableColumn<UtilisateurRow, String> lastLoginColumn;

    @FXML
    private TableColumn<UtilisateurRow, Void> actionsColumn;

    @FXML
    private Label detailNameLabel;

    @FXML
    private Label detailEmailLabel;

    @FXML
    private Label detailRoleLabel;

    @FXML
    private Label detailIdentifierLabel;

    @FXML
    private Label detailStatusLabel;

    @FXML
    private Label detailInfoLabel;

    @FXML
    private Label detailCreatedLabel;

    @FXML
    private Label detailLastLoginLabel;

    @FXML
    private ListView<String> activityList;

    private final ObservableList<UtilisateurRow> masterRows = FXCollections.observableArrayList();
    private final ObservableList<String> activityItems = FXCollections.observableArrayList();
    private final FilteredList<UtilisateurRow> filteredRows = new FilteredList<>(masterRows, row -> true);
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        lastLoginColumn.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));

        configureFilters();
        configureActionsColumn();
        configureTableBinding();

        activityList.setItems(activityItems);

        Utilisateur currentUser = UserSession.getCurrentUser();
        welcomeLabel.setText(currentUser != null ? "Welcome back, " + currentUser.getPrenom() : "Welcome back");

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> updateDetails(newRow));
        loadDashboardData();
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
    }

    @FXML
    private void createUser() {
        try {
            UserDialogData dialogData = buildDialogData(null);
            Optional<ButtonType> result = dialogData.dialog.showAndWait();
            if (result.isPresent() && result.get() == dialogData.saveButton) {
                Utilisateur utilisateur = dialogData.form.toNewUtilisateur();
                utilisateurService.createUtilisateur(utilisateur);
                activityItems.setAll("User created: " + utilisateur.getNomComplet());
                loadDashboardData();
            }
        } catch (IOException | SQLException e) {
            showError("Create failed", e.getMessage());
        }
    }

    @FXML
    private void logout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    private void configureFilters() {
        roleFilterCombo.setItems(FXCollections.observableArrayList("All", "administrateur", "enseignant", "etudiant"));
        roleFilterCombo.setValue("All");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        roleFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void configureTableBinding() {
        SortedList<UtilisateurRow> sortedRows = new SortedList<>(filteredRows);
        sortedRows.comparatorProperty().bind(usersTable.comparatorProperty());
        usersTable.setItems(sortedRows);
    }

    private void configureActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Modify");
            private final Button deleteButton = new Button("Delete");
            private final HBox box = new HBox(8, viewButton, editButton, deleteButton);

            {
                viewButton.getStyleClass().addAll("secondary-button", "table-action-button");
                editButton.getStyleClass().addAll("primary-button", "table-action-button");
                deleteButton.getStyleClass().addAll("danger-button", "table-action-button");

                viewButton.setOnAction(event -> showUserDetailsDialog(getCurrentRow()));
                editButton.setOnAction(event -> editUser(getCurrentRow()));
                deleteButton.setOnAction(event -> deleteUser(getCurrentRow()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }

            private UtilisateurRow getCurrentRow() {
                return getTableView().getItems().get(getIndex());
            }
        });
    }

    private void applyFilters() {
        String search = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String roleFilter = roleFilterCombo.getValue();

        filteredRows.setPredicate(row -> {
            boolean matchesRole = roleFilter == null
                    || "All".equalsIgnoreCase(roleFilter)
                    || row.getType().equalsIgnoreCase(roleFilter);

            boolean matchesSearch = search.isEmpty()
                    || String.valueOf(row.getId()).contains(search)
                    || row.getFullName().toLowerCase().contains(search)
                    || row.getEmail().toLowerCase().contains(search)
                    || row.getType().toLowerCase().contains(search)
                    || row.getIdentifier().toLowerCase().contains(search)
                    || row.getStatus().toLowerCase().contains(search)
                    || row.getExtraInfo().toLowerCase().contains(search);

            return matchesRole && matchesSearch;
        });
    }

    private void loadDashboardData() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
            masterRows.clear();
            activityItems.clear();

            long students = 0;
            long teachers = 0;
            long admins = 0;

            for (Utilisateur utilisateur : utilisateurs) {
                masterRows.add(new UtilisateurRow(utilisateur));
                if ("etudiant".equalsIgnoreCase(utilisateur.getType())) {
                    students++;
                } else if ("enseignant".equalsIgnoreCase(utilisateur.getType())) {
                    teachers++;
                } else if ("administrateur".equalsIgnoreCase(utilisateur.getType())) {
                    admins++;
                }
            }

            totalUsersLabel.setText(String.valueOf(utilisateurs.size()));
            studentsCountLabel.setText(String.valueOf(students));
            teachersCountLabel.setText(String.valueOf(teachers));
            adminsCountLabel.setText(String.valueOf(admins));

            activityItems.add("Live registry loaded from database");
            activityItems.add("Dynamic search and role filtering are active");
            activityItems.add("Create, view, modify, and delete actions are available");
            activityItems.add("ObservableList drives the table and admin activity feed");

            applyFilters();
            if (!usersTable.getItems().isEmpty()) {
                usersTable.getSelectionModel().select(0);
            } else {
                updateDetails(null);
            }
        } catch (SQLException e) {
            activityItems.setAll("Failed to load admin dashboard: " + e.getMessage());
        }
    }

    private void updateDetails(UtilisateurRow row) {
        if (row == null) {
            detailNameLabel.setText("-");
            detailEmailLabel.setText("-");
            detailRoleLabel.setText("-");
            detailIdentifierLabel.setText("-");
            detailStatusLabel.setText("-");
            detailInfoLabel.setText("-");
            detailCreatedLabel.setText("-");
            detailLastLoginLabel.setText("-");
            return;
        }

        detailNameLabel.setText(row.getFullName());
        detailEmailLabel.setText(row.getEmail());
        detailRoleLabel.setText(row.getType());
        detailIdentifierLabel.setText(row.getIdentifier());
        detailStatusLabel.setText(row.getStatus());
        detailInfoLabel.setText(row.getExtraInfo());
        detailCreatedLabel.setText(row.getDateCreation());
        detailLastLoginLabel.setText(row.getLastLogin());
    }

    private void showUserDetailsDialog(UtilisateurRow row) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText(row.getFullName());
        alert.setContentText(buildDetailedText(row.getUtilisateur()));
        alert.showAndWait();
    }

    private String buildDetailedText(Utilisateur utilisateur) {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(utilisateur.getId()).append('\n');
        builder.append("Name: ").append(utilisateur.getNomComplet()).append('\n');
        builder.append("Email: ").append(utilisateur.getEmail()).append('\n');
        builder.append("Role: ").append(utilisateur.getType()).append('\n');
        builder.append("Created: ").append(formatDateTime(utilisateur.getDateCreation())).append('\n');
        builder.append("Last login: ").append(formatDateTime(utilisateur.getLastLogin())).append('\n');

        if (utilisateur instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) utilisateur;
            builder.append("Matricule: ").append(etudiant.getMatricule()).append('\n');
            builder.append("Level: ").append(etudiant.getNiveauEtude()).append('\n');
            builder.append("Specialisation: ").append(etudiant.getSpecialisation()).append('\n');
            builder.append("Phone: ").append(etudiant.getTelephone()).append('\n');
            builder.append("Address: ").append(etudiant.getAdresse()).append('\n');
            builder.append("Status: ").append(etudiant.getStatut());
        } else if (utilisateur instanceof Enseignant) {
            Enseignant enseignant = (Enseignant) utilisateur;
            builder.append("Teacher ID: ").append(enseignant.getMatriculeEnseignant()).append('\n');
            builder.append("Diploma: ").append(enseignant.getDiplome()).append('\n');
            builder.append("Speciality: ").append(enseignant.getSpecialite()).append('\n');
            builder.append("Experience: ").append(enseignant.getAnneesExperience()).append('\n');
            builder.append("Contract: ").append(enseignant.getTypeContrat()).append('\n');
            builder.append("Status: ").append(enseignant.getStatut());
        } else if (utilisateur instanceof Administrateur) {
            Administrateur administrateur = (Administrateur) utilisateur;
            builder.append("Department: ").append(administrateur.getDepartement()).append('\n');
            builder.append("Function: ").append(administrateur.getFonction()).append('\n');
            builder.append("Phone: ").append(administrateur.getTelephone()).append('\n');
            builder.append("Active: ").append(administrateur.isActif() ? "Yes" : "No");
        }

        return builder.toString();
    }

    private void editUser(UtilisateurRow row) {
        try {
            UserDialogData dialogData = buildDialogData(row.getUtilisateur());
            Optional<ButtonType> result = dialogData.dialog.showAndWait();
            if (result.isPresent() && result.get() == dialogData.saveButton) {
                dialogData.form.applyToExisting();
                utilisateurService.updateUtilisateur(dialogData.form.existingUtilisateur);
                activityItems.setAll("User updated: " + dialogData.form.existingUtilisateur.getNomComplet());
                loadDashboardData();
            }
        } catch (IOException | SQLException e) {
            showError("Update failed", e.getMessage());
        }
    }

    private void deleteUser(UtilisateurRow row) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete User");
        confirmation.setHeaderText("Delete " + row.getFullName() + "?");
        confirmation.setContentText("This action removes the user from the base table and the role-specific table.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                utilisateurService.deleteUtilisateur((int) row.getId());
                activityItems.setAll("User deleted: " + row.getFullName());
                loadDashboardData();
            } catch (SQLException e) {
                showError("Delete failed", e.getMessage());
            }
        }
    }

    private UserDialogData buildDialogData(Utilisateur utilisateur) throws IOException {
        boolean creating = utilisateur == null;
        UserForm form = new UserForm(utilisateur);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(creating ? "Create User" : "Modify User");
        dialog.setHeaderText(creating ? "Create a new platform user" : "Update " + utilisateur.getNomComplet());

        ButtonType saveButton = new ButtonType(creating ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));

        int rowIndex = 0;
        addField(grid, "Nom", form.nomField, rowIndex++);
        addField(grid, "Prenom", form.prenomField, rowIndex++);
        addField(grid, "Email", form.emailField, rowIndex++);
        if (creating) {
            addField(grid, "Role", form.roleCombo, rowIndex++);
        }
        addField(grid, creating ? "Password" : "New password", form.passwordField, rowIndex++);

        int dynamicStartRow = rowIndex;
        Runnable renderFields = () -> {
            grid.getChildren().removeIf(node -> {
                Integer row = GridPane.getRowIndex(node);
                return row != null && row >= dynamicStartRow;
            });
            form.renderTypeFields(grid, dynamicStartRow);
        };

        if (creating) {
            form.roleCombo.valueProperty().addListener((obs, oldValue, newValue) -> renderFields.run());
        }
        renderFields.run();

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().lookupButton(saveButton).disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> form.nomField.getText().trim().isEmpty()
                                || form.prenomField.getText().trim().isEmpty()
                                || form.emailField.getText().trim().isEmpty()
                                || (creating && form.passwordField.getText().trim().isEmpty()),
                        form.nomField.textProperty(),
                        form.prenomField.textProperty(),
                        form.emailField.textProperty(),
                        form.passwordField.textProperty()
                )
        );

        return new UserDialogData(dialog, saveButton, form);
    }

    private void addField(GridPane grid, String label, javafx.scene.Node node, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(node, 1, row);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String formatDateTime(LocalDateTime value) {
        return value != null ? value.format(DATE_TIME_FORMATTER) : "-";
    }

    private final class UserForm {
        private final Utilisateur existingUtilisateur;
        private final TextField nomField;
        private final TextField prenomField;
        private final TextField emailField;
        private final ComboBox<String> roleCombo;
        private final TextField passwordField;

        private final TextField identifierField = new TextField();
        private final TextField infoFieldOne = new TextField();
        private final TextField infoFieldTwo = new TextField();
        private final TextField infoFieldThree = new TextField();
        private final DatePicker datePicker = new DatePicker();

        private UserForm(Utilisateur utilisateur) {
            this.existingUtilisateur = utilisateur;
            this.nomField = new TextField(utilisateur != null ? utilisateur.getNom() : "");
            this.prenomField = new TextField(utilisateur != null ? utilisateur.getPrenom() : "");
            this.emailField = new TextField(utilisateur != null ? utilisateur.getEmail() : "");
            this.roleCombo = new ComboBox<>(FXCollections.observableArrayList("administrateur", "enseignant", "etudiant"));
            this.passwordField = new TextField();

            if (utilisateur != null) {
                roleCombo.setValue(utilisateur.getType());
                preloadExistingValues(utilisateur);
            } else {
                roleCombo.setValue("administrateur");
                preloadDefaults("administrateur");
            }
        }

        private void preloadExistingValues(Utilisateur utilisateur) {
            if (utilisateur instanceof Etudiant) {
                Etudiant etudiant = (Etudiant) utilisateur;
                identifierField.setText(etudiant.getMatricule());
                infoFieldOne.setText(etudiant.getNiveauEtude());
                infoFieldTwo.setText(etudiant.getSpecialisation());
                infoFieldThree.setText(etudiant.getTelephone());
                datePicker.setValue(etudiant.getDateNaissance());
            } else if (utilisateur instanceof Enseignant) {
                Enseignant enseignant = (Enseignant) utilisateur;
                identifierField.setText(enseignant.getMatriculeEnseignant());
                infoFieldOne.setText(enseignant.getDiplome());
                infoFieldTwo.setText(enseignant.getSpecialite());
                infoFieldThree.setText(enseignant.getTypeContrat());
            } else if (utilisateur instanceof Administrateur) {
                Administrateur administrateur = (Administrateur) utilisateur;
                identifierField.setText(administrateur.getDepartement());
                infoFieldOne.setText(administrateur.getFonction());
                infoFieldTwo.setText(administrateur.getTelephone());
                infoFieldThree.setText(administrateur.isActif() ? "true" : "false");
            }
        }

        private void preloadDefaults(String role) {
            if ("etudiant".equals(role)) {
                datePicker.setValue(LocalDate.of(2000, 1, 1));
            }
        }

        private void renderTypeFields(GridPane grid, int rowIndex) {
            String role = roleCombo.getValue();
            if ("etudiant".equals(role)) {
                addField(grid, "Matricule", identifierField, rowIndex++);
                addField(grid, "Niveau", infoFieldOne, rowIndex++);
                addField(grid, "Specialisation", infoFieldTwo, rowIndex++);
                addField(grid, "Telephone", infoFieldThree, rowIndex++);
                addField(grid, "Date naissance", datePicker, rowIndex);
                return;
            }
            if ("enseignant".equals(role)) {
                addField(grid, "Teacher ID", identifierField, rowIndex++);
                addField(grid, "Diploma", infoFieldOne, rowIndex++);
                addField(grid, "Speciality", infoFieldTwo, rowIndex++);
                addField(grid, "Contract", infoFieldThree, rowIndex);
                return;
            }
            addField(grid, "Departement", identifierField, rowIndex++);
            addField(grid, "Fonction", infoFieldOne, rowIndex++);
            addField(grid, "Telephone", infoFieldTwo, rowIndex++);
            addField(grid, "Actif", infoFieldThree, rowIndex);
        }

        private Utilisateur toNewUtilisateur() throws IOException {
            String hashedPassword = authService.hashPassword(passwordField.getText().trim());
            String role = roleCombo.getValue();

            if ("etudiant".equals(role)) {
                Etudiant etudiant = new Etudiant();
                fillBase(etudiant, hashedPassword);
                etudiant.setMatricule(identifierField.getText().trim());
                etudiant.setNiveauEtude(infoFieldOne.getText().trim());
                etudiant.setSpecialisation(infoFieldTwo.getText().trim());
                etudiant.setTelephone(infoFieldThree.getText().trim());
                etudiant.setAdresse("Desktop created user");
                etudiant.setDateNaissance(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.of(2000, 1, 1));
                etudiant.setDateInscription(LocalDateTime.now());
                etudiant.setStatut("actif");
                return etudiant;
            }

            if ("enseignant".equals(role)) {
                Enseignant enseignant = new Enseignant();
                fillBase(enseignant, hashedPassword);
                enseignant.setMatriculeEnseignant(identifierField.getText().trim());
                enseignant.setDiplome(infoFieldOne.getText().trim());
                enseignant.setSpecialite(infoFieldTwo.getText().trim());
                enseignant.setTypeContrat(infoFieldThree.getText().trim());
                enseignant.setAnneesExperience(0);
                enseignant.setStatut("actif");
                return enseignant;
            }

            Administrateur administrateur = new Administrateur();
            fillBase(administrateur, hashedPassword);
            administrateur.setDepartement(identifierField.getText().trim());
            administrateur.setFonction(infoFieldOne.getText().trim());
            administrateur.setTelephone(infoFieldTwo.getText().trim());
            administrateur.setActif(Boolean.parseBoolean(infoFieldThree.getText().trim().isBlank() ? "true" : infoFieldThree.getText().trim()));
            administrateur.setDateNomination(LocalDateTime.now());
            return administrateur;
        }

        private void applyToExisting() throws IOException {
            existingUtilisateur.setNom(nomField.getText().trim());
            existingUtilisateur.setPrenom(prenomField.getText().trim());
            existingUtilisateur.setEmail(emailField.getText().trim());

            if (!passwordField.getText().trim().isEmpty()) {
                existingUtilisateur.setMotDePasse(authService.hashPassword(passwordField.getText().trim()));
            }

            if (existingUtilisateur instanceof Etudiant) {
                Etudiant etudiant = (Etudiant) existingUtilisateur;
                etudiant.setMatricule(identifierField.getText().trim());
                etudiant.setNiveauEtude(infoFieldOne.getText().trim());
                etudiant.setSpecialisation(infoFieldTwo.getText().trim());
                etudiant.setTelephone(infoFieldThree.getText().trim());
                etudiant.setDateNaissance(datePicker.getValue() != null ? datePicker.getValue() : etudiant.getDateNaissance());
                if (etudiant.getAdresse() == null || etudiant.getAdresse().isBlank()) {
                    etudiant.setAdresse("Desktop updated user");
                }
                if (etudiant.getDateInscription() == null) {
                    etudiant.setDateInscription(LocalDateTime.now());
                }
                return;
            }

            if (existingUtilisateur instanceof Enseignant) {
                Enseignant enseignant = (Enseignant) existingUtilisateur;
                enseignant.setMatriculeEnseignant(identifierField.getText().trim());
                enseignant.setDiplome(infoFieldOne.getText().trim());
                enseignant.setSpecialite(infoFieldTwo.getText().trim());
                enseignant.setTypeContrat(infoFieldThree.getText().trim());
                if (enseignant.getAnneesExperience() == null) {
                    enseignant.setAnneesExperience(0);
                }
                return;
            }

            Administrateur administrateur = (Administrateur) existingUtilisateur;
            administrateur.setDepartement(identifierField.getText().trim());
            administrateur.setFonction(infoFieldOne.getText().trim());
            administrateur.setTelephone(infoFieldTwo.getText().trim());
            administrateur.setActif(Boolean.parseBoolean(infoFieldThree.getText().trim().isBlank() ? "true" : infoFieldThree.getText().trim()));
            if (administrateur.getDateNomination() == null) {
                administrateur.setDateNomination(LocalDateTime.now());
            }
        }

        private void fillBase(Utilisateur utilisateur, String hashedPassword) {
            utilisateur.setNom(nomField.getText().trim());
            utilisateur.setPrenom(prenomField.getText().trim());
            utilisateur.setEmail(emailField.getText().trim());
            utilisateur.setMotDePasse(hashedPassword);
            utilisateur.setDateCreation(LocalDateTime.now());
            utilisateur.setResetToken(null);
            utilisateur.setResetTokenExpiresAt(null);
            utilisateur.setLastLogin(null);
        }
    }

    private static final class UserDialogData {
        private final Dialog<ButtonType> dialog;
        private final ButtonType saveButton;
        private final UserForm form;

        private UserDialogData(Dialog<ButtonType> dialog, ButtonType saveButton, UserForm form) {
            this.dialog = dialog;
            this.saveButton = saveButton;
            this.form = form;
        }
    }
}
