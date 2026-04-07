package gui;

import entities.Administrateur;
import entities.Enseignant;
import entities.Etudiant;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private Label sectionTitleLabel;

    @FXML
    private Label sectionSubtitleLabel;

    @FXML
    private Label currentUserNameLabel;

    @FXML
    private Label currentUserRoleLabel;

    @FXML
    private MenuButton profileMenuButton;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label studentsCountLabel;

    @FXML
    private Label teachersCountLabel;

    @FXML
    private Label adminsCountLabel;

    @FXML
    private VBox usersSection;

    @FXML
    private VBox statsSection;

    @FXML
    private VBox createSection;

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

    @FXML
    private TableView<StatRow> statisticsTable;

    @FXML
    private TableColumn<StatRow, String> metricColumn;

    @FXML
    private TableColumn<StatRow, String> valueColumn;

    @FXML
    private ListView<String> statsInsightsList;

    @FXML
    private ComboBox<String> createRoleCombo;

    @FXML
    private TextField createNomField;

    @FXML
    private TextField createPrenomField;

    @FXML
    private TextField createEmailField;

    @FXML
    private TextField createPasswordField;

    @FXML
    private Label createIdentifierLabel;

    @FXML
    private TextField createIdentifierField;

    @FXML
    private Label createFieldOneLabel;

    @FXML
    private TextField createFieldOneField;

    @FXML
    private Label createFieldTwoLabel;

    @FXML
    private TextField createFieldTwoField;

    @FXML
    private Label createFieldThreeLabel;

    @FXML
    private TextField createFieldThreeField;

    @FXML
    private Label createFieldFourLabel;

    @FXML
    private TextField createFieldFourField;

    @FXML
    private Label createDateLabel;

    @FXML
    private DatePicker createDatePicker;

    @FXML
    private Label createAreaLabel;

    @FXML
    private TextArea createAreaField;

    @FXML
    private Label createStatusLabel;

    @FXML
    private javafx.scene.control.Button createSubmitButton;

    private final ObservableList<UtilisateurRow> masterRows = FXCollections.observableArrayList();
    private final ObservableList<String> activityItems = FXCollections.observableArrayList();
    private final ObservableList<StatRow> statRows = FXCollections.observableArrayList();
    private final ObservableList<String> statsInsights = FXCollections.observableArrayList();
    private final FilteredList<UtilisateurRow> filteredRows = new FilteredList<>(masterRows, row -> true);
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final AuthService authService = new AuthService();
    private Utilisateur editingUtilisateur;

    @FXML
    private void initialize() {
        configureCurrentUserHeader();
        configureUserTable();
        configureStatisticsTable();
        configureCreateForm();
        showUsersPage();
        loadDashboardData();
    }

    @FXML
    private void showUsersPage() {
        setSectionVisibility(true, false, false);
        sectionTitleLabel.setText("User Registry");
        sectionSubtitleLabel.setText("Filter, inspect, edit, and remove platform users.");
    }

    @FXML
    private void showStatsPage() {
        setSectionVisibility(false, true, false);
        sectionTitleLabel.setText("Statistics");
        sectionSubtitleLabel.setText("Track role distribution and system-level user activity.");
    }

    @FXML
    private void showCreatePage() {
        setSectionVisibility(false, false, true);
        sectionTitleLabel.setText("Create User");
        sectionSubtitleLabel.setText("Add a new admin, student, or teacher using typed database-backed input controls.");
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
    }

    @FXML
    private void handleProfileLogout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    @FXML
    private void createUser() {
        String role = createRoleCombo.getValue();
        if (role == null || createNomField.getText().trim().isEmpty() || createPrenomField.getText().trim().isEmpty()
                || createEmailField.getText().trim().isEmpty() || createPasswordField.getText().trim().isEmpty()) {
            if (editingUtilisateur == null || createPasswordField.getText().trim().isEmpty()) {
                if (editingUtilisateur != null && !createPasswordField.getText().trim().isEmpty()) {
                    createStatusLabel.setText("Fill the required fields before saving.");
                }
            }
        }

        if (role == null || createNomField.getText().trim().isEmpty() || createPrenomField.getText().trim().isEmpty()
                || createEmailField.getText().trim().isEmpty()
                || (editingUtilisateur == null && createPasswordField.getText().trim().isEmpty())) {
            createStatusLabel.setText(editingUtilisateur == null
                    ? "Fill the required fields before creating a user."
                    : "Fill the required fields before saving.");
            return;
        }

        try {
            if (editingUtilisateur == null) {
                String hashedPassword = authService.hashPassword(createPasswordField.getText().trim());
                Utilisateur utilisateur = buildUtilisateurFromCreateForm(role, hashedPassword);
                utilisateurService.createUtilisateur(utilisateur);
                createStatusLabel.setText("User created successfully: " + utilisateur.getNomComplet());
            } else {
                applyCreateFormToExisting(editingUtilisateur);
                utilisateurService.updateUtilisateur(editingUtilisateur);
                createStatusLabel.setText("User updated successfully: " + editingUtilisateur.getNomComplet());
            }
            clearCreateForm();
            loadDashboardData();
            showUsersPage();
        } catch (IOException | SQLException e) {
            createStatusLabel.setText((editingUtilisateur == null ? "Create failed: " : "Update failed: ") + e.getMessage());
        }
    }

    private void configureCurrentUserHeader() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            currentUserNameLabel.setText("Guest");
            currentUserRoleLabel.setText("-");
            profileMenuButton.setText("G");
            return;
        }

        currentUserNameLabel.setText(currentUser.getNomComplet());
        currentUserRoleLabel.setText(currentUser.getType());
        profileMenuButton.setText(buildInitials(currentUser));
    }

    private void configureUserTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        lastLoginColumn.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));

        roleFilterCombo.setItems(FXCollections.observableArrayList("All", "administrateur", "enseignant", "etudiant"));
        roleFilterCombo.setValue("All");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        roleFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        SortedList<UtilisateurRow> sortedRows = new SortedList<>(filteredRows);
        sortedRows.comparatorProperty().bind(usersTable.comparatorProperty());
        usersTable.setItems(sortedRows);
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateDetails(newValue));

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.control.Button viewButton = new javafx.scene.control.Button("View");
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Modify");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Delete");
            private final HBox box = new HBox(8, viewButton, editButton, deleteButton);

            {
                viewButton.getStyleClass().addAll("secondary-button", "table-action-button");
                editButton.getStyleClass().addAll("primary-button", "table-action-button");
                deleteButton.getStyleClass().addAll("danger-button", "table-action-button");

                viewButton.setOnAction(event -> showUserDetailsDialog(getTableView().getItems().get(getIndex())));
                editButton.setOnAction(event -> editUser(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        activityList.setItems(activityItems);
    }

    private void configureStatisticsTable() {
        metricColumn.setCellValueFactory(new PropertyValueFactory<>("metric"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        statisticsTable.setItems(statRows);
        statsInsightsList.setItems(statsInsights);
    }

    private void configureCreateForm() {
        createRoleCombo.setItems(FXCollections.observableArrayList("administrateur", "enseignant", "etudiant"));
        createRoleCombo.setValue("administrateur");
        createRoleCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateCreateFormForRole(newValue));
        updateCreateFormForRole(createRoleCombo.getValue());
    }

    private void updateCreateFormForRole(String role) {
        if ("etudiant".equals(role)) {
            createIdentifierLabel.setText("Matricule");
            createFieldOneLabel.setText("Niveau");
            createFieldTwoLabel.setText("Specialisation");
            createFieldThreeLabel.setText("Telephone");
            createFieldFourLabel.setText("Statut");
            createDateLabel.setText("Date naissance");
            createAreaLabel.setText("Adresse");
            createFieldFourLabel.setVisible(true);
            createFieldFourField.setVisible(true);
            createDateLabel.setVisible(true);
            createDatePicker.setVisible(true);
            createAreaLabel.setVisible(true);
            createAreaField.setVisible(true);
            createFieldFourField.setText("actif");
            createDatePicker.setValue(LocalDate.of(2000, 1, 1));
            createAreaField.setText("Desktop created student");
            return;
        }

        if ("enseignant".equals(role)) {
            createIdentifierLabel.setText("Teacher ID");
            createFieldOneLabel.setText("Diploma");
            createFieldTwoLabel.setText("Speciality");
            createFieldThreeLabel.setText("Contract");
            createFieldFourLabel.setText("Experience");
            createDateLabel.setText("Unused");
            createAreaLabel.setText("Disponibilites");
            createFieldFourLabel.setVisible(true);
            createFieldFourField.setVisible(true);
            createDateLabel.setVisible(false);
            createDatePicker.setVisible(false);
            createAreaLabel.setVisible(true);
            createAreaField.setVisible(true);
            createFieldFourField.setText("0");
            createAreaField.setText("");
            return;
        }

        createIdentifierLabel.setText("Departement");
        createFieldOneLabel.setText("Fonction");
        createFieldTwoLabel.setText("Telephone");
        createFieldThreeLabel.setText("Actif");
        createFieldFourLabel.setVisible(false);
        createFieldFourField.setVisible(false);
        createDateLabel.setVisible(false);
        createDatePicker.setVisible(false);
        createAreaLabel.setVisible(false);
        createAreaField.setVisible(false);
        createFieldThreeField.setText("true");
    }

    private Utilisateur buildUtilisateurFromCreateForm(String role, String hashedPassword) {
        if ("etudiant".equals(role)) {
            Etudiant etudiant = new Etudiant();
            fillBase(etudiant, hashedPassword);
            etudiant.setMatricule(createIdentifierField.getText().trim());
            etudiant.setNiveauEtude(createFieldOneField.getText().trim());
            etudiant.setSpecialisation(createFieldTwoField.getText().trim());
            etudiant.setTelephone(createFieldThreeField.getText().trim());
            etudiant.setStatut(defaultIfBlank(createFieldFourField.getText(), "actif"));
            etudiant.setDateNaissance(createDatePicker.getValue() != null ? createDatePicker.getValue() : LocalDate.of(2000, 1, 1));
            etudiant.setAdresse(defaultIfBlank(createAreaField.getText(), "Desktop created student"));
            etudiant.setDateInscription(LocalDateTime.now());
            return etudiant;
        }

        if ("enseignant".equals(role)) {
            Enseignant enseignant = new Enseignant();
            fillBase(enseignant, hashedPassword);
            enseignant.setMatriculeEnseignant(createIdentifierField.getText().trim());
            enseignant.setDiplome(createFieldOneField.getText().trim());
            enseignant.setSpecialite(createFieldTwoField.getText().trim());
            enseignant.setTypeContrat(createFieldThreeField.getText().trim());
            enseignant.setAnneesExperience(parseInteger(createFieldFourField.getText(), 0));
            enseignant.setDisponibilites(defaultIfBlank(createAreaField.getText(), ""));
            enseignant.setStatut("actif");
            return enseignant;
        }

        Administrateur administrateur = new Administrateur();
        fillBase(administrateur, hashedPassword);
        administrateur.setDepartement(createIdentifierField.getText().trim());
        administrateur.setFonction(createFieldOneField.getText().trim());
        administrateur.setTelephone(createFieldTwoField.getText().trim());
        administrateur.setActif(Boolean.parseBoolean(defaultIfBlank(createFieldThreeField.getText(), "true")));
        administrateur.setDateNomination(LocalDateTime.now());
        return administrateur;
    }

    private void fillBase(Utilisateur utilisateur, String hashedPassword) {
        utilisateur.setNom(createNomField.getText().trim());
        utilisateur.setPrenom(createPrenomField.getText().trim());
        utilisateur.setEmail(createEmailField.getText().trim());
        utilisateur.setMotDePasse(hashedPassword);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateur.setLastLogin(null);
        utilisateur.setResetToken(null);
        utilisateur.setResetTokenExpiresAt(null);
    }

    private void clearCreateForm() {
        editingUtilisateur = null;
        createNomField.clear();
        createPrenomField.clear();
        createEmailField.clear();
        createPasswordField.clear();
        createIdentifierField.clear();
        createFieldOneField.clear();
        createFieldTwoField.clear();
        createFieldThreeField.clear();
        createFieldFourField.clear();
        createAreaField.clear();
        createDatePicker.setValue(null);
        createRoleCombo.setDisable(false);
        createSubmitButton.setText("Create User");
        updateCreateFormForRole(createRoleCombo.getValue());
    }

    private void applyFilters() {
        String search = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String roleFilter = roleFilterCombo.getValue();

        filteredRows.setPredicate(row -> {
            boolean matchesRole = roleFilter == null || "All".equalsIgnoreCase(roleFilter) || row.getType().equalsIgnoreCase(roleFilter);
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
            statRows.clear();
            activityItems.clear();
            statsInsights.clear();

            long students = 0;
            long teachers = 0;
            long admins = 0;
            long activeCount = 0;

            for (Utilisateur utilisateur : utilisateurs) {
                masterRows.add(new UtilisateurRow(utilisateur));

                if ("etudiant".equalsIgnoreCase(utilisateur.getType())) {
                    students++;
                    if (((Etudiant) utilisateur).isActif()) {
                        activeCount++;
                    }
                } else if ("enseignant".equalsIgnoreCase(utilisateur.getType())) {
                    teachers++;
                    if (((Enseignant) utilisateur).isActif()) {
                        activeCount++;
                    }
                } else if ("administrateur".equalsIgnoreCase(utilisateur.getType())) {
                    admins++;
                    if (((Administrateur) utilisateur).isActif()) {
                        activeCount++;
                    }
                }
            }

            totalUsersLabel.setText(String.valueOf(utilisateurs.size()));
            studentsCountLabel.setText(String.valueOf(students));
            teachersCountLabel.setText(String.valueOf(teachers));
            adminsCountLabel.setText(String.valueOf(admins));

            statRows.addAll(
                    new StatRow("Total users", String.valueOf(utilisateurs.size())),
                    new StatRow("Administrators", String.valueOf(admins)),
                    new StatRow("Teachers", String.valueOf(teachers)),
                    new StatRow("Students", String.valueOf(students)),
                    new StatRow("Active profiles", String.valueOf(activeCount))
            );

            statsInsights.add("User registry is synchronized with MySQL.");
            statsInsights.add("Filtering supports role and free-text search.");
            statsInsights.add("Admin workspace includes create, edit, view, and delete flows.");
            statsInsights.add("Profile menu is available from the top bar.");

            activityItems.add("Live registry loaded from database");
            activityItems.add("Profile actions are available from the user circle");
            activityItems.add("Sidebar switches between users, statistics, and create page");
            activityItems.add("ObservableList keeps the admin workspace reactive");

            applyFilters();
            if (!usersTable.getItems().isEmpty()) {
                usersTable.getSelectionModel().select(0);
            } else {
                updateDetails(null);
            }
        } catch (SQLException e) {
            activityItems.setAll("Failed to load admin dashboard: " + e.getMessage());
            statsInsights.setAll("Statistics unavailable: " + e.getMessage());
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

    private void editUser(UtilisateurRow row) {
        editingUtilisateur = row.getUtilisateur();
        populateCreateFormForEdit(editingUtilisateur);
        createRoleCombo.setDisable(true);
        createSubmitButton.setText("Save Changes");
        createStatusLabel.setText("Editing " + editingUtilisateur.getNomComplet() + ". Leave password empty to keep it unchanged.");
        showCreatePage();
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

    private void setSectionVisibility(boolean usersVisible, boolean statsVisible, boolean createVisible) {
        usersSection.setVisible(usersVisible);
        usersSection.setManaged(usersVisible);
        statsSection.setVisible(statsVisible);
        statsSection.setManaged(statsVisible);
        createSection.setVisible(createVisible);
        createSection.setManaged(createVisible);
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

    private int parseInteger(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String buildInitials(Utilisateur utilisateur) {
        String prenom = utilisateur.getPrenom() != null && !utilisateur.getPrenom().isBlank()
                ? utilisateur.getPrenom().substring(0, 1).toUpperCase() : "";
        String nom = utilisateur.getNom() != null && !utilisateur.getNom().isBlank()
                ? utilisateur.getNom().substring(0, 1).toUpperCase() : "";
        return prenom + nom;
    }

    private void populateCreateFormForEdit(Utilisateur utilisateur) {
        createNomField.setText(utilisateur.getNom());
        createPrenomField.setText(utilisateur.getPrenom());
        createEmailField.setText(utilisateur.getEmail());
        createPasswordField.clear();
        createRoleCombo.setValue(utilisateur.getType());
        updateCreateFormForRole(utilisateur.getType());

        if (utilisateur instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) utilisateur;
            createIdentifierField.setText(etudiant.getMatricule());
            createFieldOneField.setText(etudiant.getNiveauEtude());
            createFieldTwoField.setText(etudiant.getSpecialisation());
            createFieldThreeField.setText(etudiant.getTelephone());
            createFieldFourField.setText(etudiant.getStatut());
            createDatePicker.setValue(etudiant.getDateNaissance());
            createAreaField.setText(etudiant.getAdresse());
            return;
        }

        if (utilisateur instanceof Enseignant) {
            Enseignant enseignant = (Enseignant) utilisateur;
            createIdentifierField.setText(enseignant.getMatriculeEnseignant());
            createFieldOneField.setText(enseignant.getDiplome());
            createFieldTwoField.setText(enseignant.getSpecialite());
            createFieldThreeField.setText(enseignant.getTypeContrat());
            createFieldFourField.setText(String.valueOf(enseignant.getAnneesExperience() != null ? enseignant.getAnneesExperience() : 0));
            createAreaField.setText(enseignant.getDisponibilites());
            return;
        }

        Administrateur administrateur = (Administrateur) utilisateur;
        createIdentifierField.setText(administrateur.getDepartement());
        createFieldOneField.setText(administrateur.getFonction());
        createFieldTwoField.setText(administrateur.getTelephone());
        createFieldThreeField.setText(administrateur.isActif() ? "true" : "false");
    }

    private void applyCreateFormToExisting(Utilisateur utilisateur) throws IOException {
        utilisateur.setNom(createNomField.getText().trim());
        utilisateur.setPrenom(createPrenomField.getText().trim());
        utilisateur.setEmail(createEmailField.getText().trim());
        if (!createPasswordField.getText().trim().isEmpty()) {
            utilisateur.setMotDePasse(authService.hashPassword(createPasswordField.getText().trim()));
        }

        if (utilisateur instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) utilisateur;
            etudiant.setMatricule(createIdentifierField.getText().trim());
            etudiant.setNiveauEtude(createFieldOneField.getText().trim());
            etudiant.setSpecialisation(createFieldTwoField.getText().trim());
            etudiant.setTelephone(createFieldThreeField.getText().trim());
            etudiant.setStatut(defaultIfBlank(createFieldFourField.getText(), etudiant.getStatut()));
            if (createDatePicker.getValue() != null) {
                etudiant.setDateNaissance(createDatePicker.getValue());
            }
            etudiant.setAdresse(defaultIfBlank(createAreaField.getText(), etudiant.getAdresse()));
            if (etudiant.getDateInscription() == null) {
                etudiant.setDateInscription(LocalDateTime.now());
            }
            return;
        }

        if (utilisateur instanceof Enseignant) {
            Enseignant enseignant = (Enseignant) utilisateur;
            enseignant.setMatriculeEnseignant(createIdentifierField.getText().trim());
            enseignant.setDiplome(createFieldOneField.getText().trim());
            enseignant.setSpecialite(createFieldTwoField.getText().trim());
            enseignant.setTypeContrat(createFieldThreeField.getText().trim());
            enseignant.setAnneesExperience(parseInteger(createFieldFourField.getText(), enseignant.getAnneesExperience() != null ? enseignant.getAnneesExperience() : 0));
            enseignant.setDisponibilites(defaultIfBlank(createAreaField.getText(), enseignant.getDisponibilites()));
            if (enseignant.getStatut() == null || enseignant.getStatut().isBlank()) {
                enseignant.setStatut("actif");
            }
            return;
        }

        Administrateur administrateur = (Administrateur) utilisateur;
        administrateur.setDepartement(createIdentifierField.getText().trim());
        administrateur.setFonction(createFieldOneField.getText().trim());
        administrateur.setTelephone(createFieldTwoField.getText().trim());
        administrateur.setActif(Boolean.parseBoolean(defaultIfBlank(createFieldThreeField.getText(), administrateur.isActif() ? "true" : "false")));
        if (administrateur.getDateNomination() == null) {
            administrateur.setDateNomination(LocalDateTime.now());
        }
    }
}
