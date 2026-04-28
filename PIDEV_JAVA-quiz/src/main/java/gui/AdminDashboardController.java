package gui;

import entities.Administrateur;
import entities.Contenu;
import entities.Cours;
import entities.Enseignant;
import entities.Etudiant;
import entities.Module;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import services.AuthService;
import services.ContenuService;
import services.CoursService;
import services.ModuleService;
import services.UtilisateurService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Modifie seulement cette ligne si le nom ou le chemin de ton interface Forum change.
    private static final String FORUM_FXML_PATH = "/gui/admin_forum.fxml";

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
    private MenuButton notificationMenuButton;

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
    private VBox modulesSection;

    @FXML
    private VBox coursesSection;

    @FXML
    private VBox contenusSection;

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

    @FXML
    private TableView<Module> modulesTable;

    @FXML
    private TableColumn<Module, Integer> moduleIdColumn;

    @FXML
    private TableColumn<Module, String> moduleTitleColumn;

    @FXML
    private TableColumn<Module, Integer> moduleOrderColumn;

    @FXML
    private TableColumn<Module, Integer> moduleDurationColumn;

    @FXML
    private TableColumn<Module, String> moduleStatusColumn;

    @FXML
    private TableColumn<Module, Void> moduleActionsColumn;

    @FXML
    private VBox moduleBrowseSection;

    @FXML
    private VBox moduleDetailsPanel;

    @FXML
    private ListView<String> moduleDetailsList;

    @FXML
    private Label moduleMessageLabel;

    @FXML
    private VBox moduleEditorSection;

    @FXML
    private Label moduleFormTitleLabel;

    @FXML
    private TextField moduleTitreField;

    @FXML
    private TextArea moduleDescriptionArea;

    @FXML
    private TextField moduleOrdreField;

    @FXML
    private TextArea moduleObjectifsArea;

    @FXML
    private TextField moduleDureeField;

    @FXML
    private DatePicker moduleDatePublicationPicker;

    @FXML
    private ComboBox<String> moduleStatutCombo;

    @FXML
    private TextArea moduleRessourcesArea;

    @FXML
    private Label moduleFormStatusLabel;

    @FXML
    private Label moduleTitreErrorLabel;

    @FXML
    private Label moduleDescriptionErrorLabel;

    @FXML
    private Label moduleOrdreErrorLabel;

    @FXML
    private Label moduleObjectifsErrorLabel;

    @FXML
    private Label moduleDureeErrorLabel;

    @FXML
    private Label moduleDatePublicationErrorLabel;

    @FXML
    private Label moduleStatutErrorLabel;

    @FXML
    private Label moduleRessourcesErrorLabel;

    @FXML
    private Label currentModuleTitleLabel;

    @FXML
    private Label currentModuleSubtitleLabel;

    @FXML
    private TableView<Cours> coursTable;

    @FXML
    private TableColumn<Cours, Integer> coursIdColumn;

    @FXML
    private TableColumn<Cours, String> coursCodeColumn;

    @FXML
    private TableColumn<Cours, String> coursTitreColumn;

    @FXML
    private TableColumn<Cours, String> coursNiveauColumn;

    @FXML
    private TableColumn<Cours, Integer> coursCreditsColumn;

    @FXML
    private TableColumn<Cours, String> coursStatutColumn;

    @FXML
    private TableColumn<Cours, Void> coursActionsColumn;

    @FXML
    private VBox coursBrowseSection;

    @FXML
    private VBox coursDetailsPanel;

    @FXML
    private Label coursDetailCodeLabel;

    @FXML
    private Label coursDetailHeroTitleLabel;

    @FXML
    private Label coursDetailHeroMetaLabel;

    @FXML
    private Label coursDetailTitreLabel;

    @FXML
    private Label coursDetailDescriptionLabel;

    @FXML
    private Label coursDetailNiveauLabel;

    @FXML
    private Label coursDetailCreditsLabel;

    @FXML
    private Label coursDetailLangueLabel;

    @FXML
    private Label coursDetailDateDebutLabel;

    @FXML
    private Label coursDetailDateFinLabel;

    @FXML
    private Label coursDetailStatutLabel;

    @FXML
    private Label coursDetailImageLabel;

    @FXML
    private Label coursDetailPrerequisLabel;

    @FXML
    private Label coursMessageLabel;

    @FXML
    private VBox coursEditorSection;

    @FXML
    private Label coursFormTitleLabel;

    @FXML
    private TextField coursCodeField;

    @FXML
    private TextField coursTitreField;

    @FXML
    private TextArea coursDescriptionArea;

    @FXML
    private TextField coursNiveauField;

    @FXML
    private TextField coursCreditsField;

    @FXML
    private TextField coursLangueField;

    @FXML
    private DatePicker coursDateDebutPicker;

    @FXML
    private DatePicker coursDateFinPicker;

    @FXML
    private ComboBox<String> coursStatutCombo;

    @FXML
    private TextField coursImageField;

    @FXML
    private TextArea coursPrerequisArea;

    @FXML
    private Label coursFormStatusLabel;

    @FXML
    private Label coursCodeErrorLabel;

    @FXML
    private Label coursTitreErrorLabel;

    @FXML
    private Label coursDescriptionErrorLabel;

    @FXML
    private Label coursNiveauErrorLabel;

    @FXML
    private Label coursCreditsErrorLabel;

    @FXML
    private Label coursLangueErrorLabel;

    @FXML
    private Label coursDateDebutErrorLabel;

    @FXML
    private Label coursDateFinErrorLabel;

    @FXML
    private Label coursStatutErrorLabel;

    @FXML
    private Label coursImageErrorLabel;

    @FXML
    private Label coursPrerequisErrorLabel;

    @FXML
    private Label currentCoursTitleLabel;

    @FXML
    private Label currentCoursSubtitleLabel;

    @FXML
    private TableView<Contenu> contenusTable;

    @FXML
    private TableColumn<Contenu, Integer> contenuIdColumn;

    @FXML
    private TableColumn<Contenu, String> contenuTypeColumn;

    @FXML
    private TableColumn<Contenu, String> contenuTitreColumn;

    @FXML
    private TableColumn<Contenu, Integer> contenuOrdreColumn;

    @FXML
    private TableColumn<Contenu, Integer> contenuDureeColumn;

    @FXML
    private TableColumn<Contenu, Void> contenuActionsColumn;

    @FXML
    private VBox contenuBrowseSection;

    @FXML
    private VBox contenuDetailsPanel;

    @FXML
    private Label contenuHeroTitleLabel;

    @FXML
    private Label contenuHeroMetaLabel;

    @FXML
    private Label contenuDetailTypeLabel;

    @FXML
    private Label contenuDetailTitreLabel;

    @FXML
    private Label contenuDetailDescriptionLabel;

    @FXML
    private Label contenuDetailUrlLabel;

    @FXML
    private Label contenuDetailDureeLabel;

    @FXML
    private Label contenuDetailOrdreLabel;

    @FXML
    private Label contenuDetailPublicLabel;

    @FXML
    private Label contenuDetailDateAjoutLabel;

    @FXML
    private Label contenuDetailVuesLabel;

    @FXML
    private Label contenuDetailFormatLabel;

    @FXML
    private Label contenuDetailRessourcesLabel;

    @FXML
    private Label contenuMessageLabel;

    @FXML
    private VBox contenuEditorSection;

    @FXML
    private Label contenuFormTitleLabel;

    @FXML
    private CheckBox contenuVideoCheck;

    @FXML
    private CheckBox contenuPdfCheck;

    @FXML
    private CheckBox contenuPptCheck;

    @FXML
    private CheckBox contenuTexteCheck;

    @FXML
    private CheckBox contenuQuizCheck;

    @FXML
    private CheckBox contenuLienCheck;

    @FXML
    private TextField contenuTitreField;

    @FXML
    private TextField contenuUrlField;

    @FXML
    private TextArea contenuDescriptionArea;

    @FXML
    private TextField contenuDureeField;

    @FXML
    private TextField contenuOrdreField;

    @FXML
    private ComboBox<String> contenuPublicCombo;

    @FXML
    private DatePicker contenuDateAjoutPicker;

    @FXML
    private TextField contenuVuesField;

    @FXML
    private TextField contenuFormatField;

    @FXML
    private TextArea contenuRessourcesArea;

    @FXML
    private Label contenuFormStatusLabel;

    @FXML
    private VBox contenuPdfSection;

    @FXML
    private VBox contenuPptSection;

    @FXML
    private VBox contenuVideoSection;

    @FXML
    private VBox contenuLienSection;

    @FXML
    private VBox contenuQuizSection;

    @FXML
    private VBox contenuTexteSection;

    @FXML
    private TextField contenuPdfField;

    @FXML
    private TextField contenuPptField;

    @FXML
    private TextField contenuVideoField;

    @FXML
    private TextField contenuLienField;

    @FXML
    private TextField contenuQuizField;

    @FXML
    private Label contenuTypeErrorLabel;

    @FXML
    private Label contenuTitreErrorLabel;

    @FXML
    private Label contenuUrlErrorLabel;

    @FXML
    private Label contenuDescriptionErrorLabel;

    @FXML
    private Label contenuDureeErrorLabel;

    @FXML
    private Label contenuOrdreErrorLabel;

    @FXML
    private Label contenuPublicErrorLabel;

    @FXML
    private Label contenuDateAjoutErrorLabel;

    @FXML
    private Label contenuVuesErrorLabel;

    @FXML
    private Label contenuFormatErrorLabel;

    @FXML
    private Label contenuRessourcesErrorLabel;

    @FXML
    private Label contenuPdfErrorLabel;

    @FXML
    private Label contenuPptErrorLabel;

    @FXML
    private Label contenuVideoErrorLabel;

    @FXML
    private Label contenuLienErrorLabel;

    @FXML
    private Label contenuQuizErrorLabel;

    private final ObservableList<UtilisateurRow> masterRows = FXCollections.observableArrayList();
    private final ObservableList<String> activityItems = FXCollections.observableArrayList();
    private final ObservableList<StatRow> statRows = FXCollections.observableArrayList();
    private final ObservableList<String> statsInsights = FXCollections.observableArrayList();
    private final FilteredList<UtilisateurRow> filteredRows = new FilteredList<>(masterRows, row -> true);
    private final ObservableList<Module> moduleRows = FXCollections.observableArrayList();
    private final ObservableList<Cours> coursRows = FXCollections.observableArrayList();
    private final ObservableList<Contenu> contenuRows = FXCollections.observableArrayList();
    private final Map<Integer, List<Cours>> demoCoursByModuleId = new HashMap<>();
    private final Map<Integer, List<Contenu>> demoContenusByCoursId = new HashMap<>();
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final AuthService authService = new AuthService();
    private final ModuleService moduleService = new ModuleService();
    private final CoursService coursService = new CoursService();
    private final ContenuService contenuService = new ContenuService();
    private final gui.ModuleController moduleController = new gui.ModuleController();
    private final gui.CoursController coursController = new gui.CoursController();
    private final gui.ContenuController contenuController = new gui.ContenuController();
    private Utilisateur editingUtilisateur;
    private Module editingModule;
    private Module selectedModuleForCours;
    private Cours editingCours;
    private Cours selectedCoursForContenus;
    private Contenu editingContenu;
    private boolean moduleDemoMode;
    private boolean coursDemoMode;
    private boolean contenuDemoMode;

    @FXML
    private void initialize() {
        configureCurrentUserHeader();
        configureUserTable();
        configureStatisticsTable();
        configureCreateForm();
        configureModuleTable();
        configureCoursTable();
        configureContenuTable();
        
        if (notificationMenuButton != null) {
            utils.NotificationUIHelper.setupNotificationMenu(notificationMenuButton);
        }

        showUsersPage();
        loadDashboardData();
        loadModulesData();
    }

    @FXML
    private void showUsersPage() {
        setSectionVisibility(true, false, false, false, false, false);
        sectionTitleLabel.setText("User Registry");
        sectionSubtitleLabel.setText("Filter, inspect, edit, and remove platform users.");
    }

    @FXML
    private void showStatsPage() {
        setSectionVisibility(false, true, false, false, false, false);
        sectionTitleLabel.setText("Statistics");
        sectionSubtitleLabel.setText("Track role distribution and system-level user activity.");
    }

    @FXML
    private void showCreatePage() {
        setSectionVisibility(false, false, true, false, false, false);
        sectionTitleLabel.setText("Create User");
        sectionSubtitleLabel.setText("Add a new admin, student, or teacher using typed database-backed input controls.");
    }

    @FXML
    private void showModulesPage() {
        setSectionVisibility(false, false, false, true, false, false);
        sectionTitleLabel.setText("Modules");
        sectionSubtitleLabel.setText("Administrez le catalogue des modules et consultez leurs informations detaillees.");
        loadModulesData();
        showModuleBrowser();
    }

    @FXML
    private void openForumPage() {
        try {
            SceneManager.switchScene("/gui/admin_forum.fxml", "Forum");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation forum",
                    "Erreur réelle : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @FXML
    private void openMessagePage() {
        try {
            SceneManager.switchScene("/gui/admin_message.fxml", "Messagerie");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation messagerie",
                    "Erreur réelle : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @FXML
    private void showCoursPage() {
        if (selectedModuleForCours == null) {
            moduleMessageLabel.setText("Selectionnez un module pour voir ses cours.");
            showModulesPage();
            return;
        }
        setSectionVisibility(false, false, false, false, true, false);
        sectionTitleLabel.setText("Cours");
        sectionSubtitleLabel.setText("Gerez les cours rattaches au module selectionne.");
        currentModuleTitleLabel.setText(selectedModuleForCours.getTitreModule());
        currentModuleSubtitleLabel.setText("Module #" + selectedModuleForCours.getId() + " - " + safeValue(selectedModuleForCours.getStatut()));
        loadCoursData();
        showCoursBrowser();
    }

    @FXML
    private void showContenusPage() {
        if (selectedCoursForContenus == null) {
            coursMessageLabel.setText("Selectionnez un cours pour voir ses contenus.");
            showCoursPage();
            return;
        }
        setSectionVisibility(false, false, false, false, false, true);
        sectionTitleLabel.setText("Contenus");
        sectionSubtitleLabel.setText("Gerez les contenus rattaches au cours selectionne.");
        currentCoursTitleLabel.setText(selectedCoursForContenus.getTitre());
        currentCoursSubtitleLabel.setText(safeValue(selectedCoursForContenus.getCodeCours()) + "  |  " + safeValue(selectedCoursForContenus.getStatut()));
        loadContenusData();
        showContenuBrowser();
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
        loadModulesData();
        if (selectedModuleForCours != null) {
            loadCoursData();
        }
        if (selectedCoursForContenus != null) {
            loadContenusData();
        }
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
            Administrateur demoAdmin = buildDemoAdmin();
            currentUserNameLabel.setText(demoAdmin.getNomComplet());
            currentUserRoleLabel.setText(demoAdmin.getType());
            profileMenuButton.setText(buildInitials(demoAdmin));
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

    private void configureModuleTable() {
        moduleIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        moduleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("titreModule"));
        moduleOrderColumn.setCellValueFactory(new PropertyValueFactory<>("ordreAffichage"));
        moduleDurationColumn.setCellValueFactory(new PropertyValueFactory<>("dureeEstimeeHeures"));
        moduleStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        moduleActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.control.Button viewButton = new javafx.scene.control.Button("Voir");
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Modifier");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Supprimer");
            private final HBox box = new HBox(8, viewButton, editButton, deleteButton);

            {
                viewButton.getStyleClass().addAll("secondary-button", "table-action-button");
                editButton.getStyleClass().addAll("primary-button", "table-action-button");
                deleteButton.getStyleClass().addAll("danger-button", "table-action-button");

                viewButton.setOnAction(event -> {
                    Module module = getTableView().getItems().get(getIndex());
                    openModuleCours(module);
                });
                editButton.setOnAction(event -> editModule(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteModule(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        modulesTable.setItems(moduleRows);
        moduleDetailsList.setItems(FXCollections.observableArrayList());
        modulesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            setDetailPanelVisible(moduleDetailsPanel, newValue != null);
            updateModuleDetails(newValue);
        });
        moduleController.configureFormDefaults(moduleStatutCombo, moduleDatePublicationPicker);
        setDetailPanelVisible(moduleDetailsPanel, false);
        showModuleBrowser();
    }

    private void configureCoursTable() {
        coursIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        coursCodeColumn.setCellValueFactory(new PropertyValueFactory<>("codeCours"));
        coursTitreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        coursNiveauColumn.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        coursCreditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        coursStatutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        coursActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.control.Button viewButton = new javafx.scene.control.Button("Voir");
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Modifier");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Supprimer");
            private final HBox box = new HBox(8, viewButton, editButton, deleteButton);

            {
                viewButton.getStyleClass().addAll("secondary-button", "table-action-button");
                editButton.getStyleClass().addAll("primary-button", "table-action-button");
                deleteButton.getStyleClass().addAll("danger-button", "table-action-button");

                viewButton.setOnAction(event -> {
                    Cours cours = getTableView().getItems().get(getIndex());
                    openCoursContenus(cours);
                });
                editButton.setOnAction(event -> editCours(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteCours(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        coursTable.setItems(coursRows);
        coursTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            setDetailPanelVisible(coursDetailsPanel, newValue != null);
            updateCoursDetails(newValue);
        });
        coursController.configureFormDefaults(coursStatutCombo, coursDateDebutPicker, coursDateFinPicker);
        setDetailPanelVisible(coursDetailsPanel, false);
        showCoursBrowser();
    }

    private void configureContenuTable() {
        contenuIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contenuTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeContenu"));
        contenuTitreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        contenuOrdreColumn.setCellValueFactory(new PropertyValueFactory<>("ordreAffichage"));
        contenuDureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        contenuActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Modifier");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Supprimer");
            private final HBox box = new HBox(8, editButton, deleteButton);

            {
                editButton.getStyleClass().addAll("primary-button", "table-action-button");
                deleteButton.getStyleClass().addAll("danger-button", "table-action-button");

                editButton.setOnAction(event -> editContenu(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteContenu(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        contenusTable.setItems(contenuRows);
        contenusTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            setDetailPanelVisible(contenuDetailsPanel, newValue != null);
            updateContenuDetails(newValue);
        });
        contenuController.configureFormDefaults(
                contenuVideoCheck,
                contenuPdfCheck,
                contenuPptCheck,
                contenuTexteCheck,
                contenuQuizCheck,
                contenuLienCheck,
                contenuPublicCombo,
                contenuDateAjoutPicker
        );
        bindContenuTypeChecks();
        updateContenuTypeSections();
        setDetailPanelVisible(contenuDetailsPanel, false);
        showContenuBrowser();
    }

    private void configureCreateForm() {
        createRoleCombo.setItems(FXCollections.observableArrayList("administrateur", "enseignant", "etudiant"));
        createRoleCombo.setValue("administrateur");
        createRoleCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateCreateFormForRole(newValue));
        updateCreateFormForRole(createRoleCombo.getValue());
    }

    @FXML
    private void showModuleCreateForm() {
        editingModule = null;
        moduleFormTitleLabel.setText("Nouveau module");
        moduleController.clearForm(
                moduleTitreField,
                moduleDescriptionArea,
                moduleOrdreField,
                moduleObjectifsArea,
                moduleDureeField,
                moduleDatePublicationPicker,
                moduleStatutCombo,
                moduleRessourcesArea
        );
        moduleFormStatusLabel.setText("");
        clearModuleFieldErrors();
        showModuleEditor();
    }

    @FXML
    private void cancelModuleForm() {
        showModuleBrowser();
    }

    @FXML
    private void saveModule() {
        clearModuleFieldErrors();
        Map<String, String> validationErrors = moduleController.validateModuleForm(
                moduleTitreField,
                moduleDescriptionArea,
                moduleOrdreField,
                moduleObjectifsArea,
                moduleDureeField,
                moduleStatutCombo
        );
        if (!validationErrors.isEmpty()) {
            applyModuleFieldErrors(validationErrors);
            moduleFormStatusLabel.setText("");
            return;
        }

        try {
            Module module = moduleController.buildModule(
                    editingModule,
                    moduleTitreField,
                    moduleDescriptionArea,
                    moduleOrdreField,
                    moduleObjectifsArea,
                    moduleDureeField,
                    moduleDatePublicationPicker,
                    moduleStatutCombo,
                    moduleRessourcesArea
            );

            if (moduleDemoMode) {
                saveModuleInDemoMode(module);
            } else if (editingModule == null) {
                moduleService.create(module);
            } else {
                moduleService.update(module);
            }

            moduleMessageLabel.setText(editingModule == null
                    ? "Module cree avec succes."
                    : "Module modifie avec succes.");
            loadModulesData();
            showModuleBrowser();
        } catch (SQLException e) {
            moduleFormStatusLabel.setText("Erreur module: " + e.getMessage());
        }
    }

    @FXML
    private void backToModulesFromCours() {
        showModulesPage();
    }

    @FXML
    private void backToCoursFromContenus() {
        showCoursPage();
    }

    @FXML
    private void showCoursCreateForm() {
        if (selectedModuleForCours == null) {
            coursMessageLabel.setText("Selectionnez d'abord un module.");
            return;
        }
        editingCours = null;
        coursFormTitleLabel.setText("Nouveau Cours");
        coursController.clearForm(
                coursCodeField,
                coursTitreField,
                coursDescriptionArea,
                coursNiveauField,
                coursCreditsField,
                coursLangueField,
                coursDateDebutPicker,
                coursDateFinPicker,
                coursStatutCombo,
                coursImageField,
                coursPrerequisArea
        );
        coursFormStatusLabel.setText("");
        showCoursEditor();
    }

    @FXML
    private void cancelCoursForm() {
        showCoursBrowser();
    }

    @FXML
    private void saveCours() {
        if (selectedModuleForCours == null) {
            coursFormStatusLabel.setText("Aucun module selectionne.");
            return;
        }

        clearCoursFieldErrors();
        Map<String, String> validationErrors = coursController.validateCoursForm(
                coursCodeField,
                coursTitreField,
                coursDescriptionArea,
                coursNiveauField,
                coursCreditsField,
                coursLangueField,
                coursDateDebutPicker,
                coursDateFinPicker,
                coursStatutCombo,
                coursImageField,
                coursPrerequisArea
        );
        if (!validationErrors.isEmpty()) {
            applyCoursFieldErrors(validationErrors);
            coursFormStatusLabel.setText("");
            return;
        }

        try {
            Cours cours = coursController.buildCours(
                    editingCours,
                    selectedModuleForCours,
                    coursCodeField,
                    coursTitreField,
                    coursDescriptionArea,
                    coursNiveauField,
                    coursCreditsField,
                    coursLangueField,
                    coursDateDebutPicker,
                    coursDateFinPicker,
                    coursStatutCombo,
                    coursImageField,
                    coursPrerequisArea
            );

            if (coursDemoMode) {
                saveCoursInDemoMode(cours);
            } else if (editingCours == null) {
                coursService.create(cours);
            } else {
                coursService.update(cours);
            }

            coursMessageLabel.setText(editingCours == null
                    ? "Cours cree avec succes."
                    : "Cours modifie avec succes.");
            loadCoursData();
            showCoursBrowser();
        } catch (SQLException e) {
            coursFormStatusLabel.setText("Erreur cours: " + e.getMessage());
        }
    }

    @FXML
    private void showContenuCreateForm() {
        if (selectedCoursForContenus == null) {
            contenuMessageLabel.setText("Selectionnez d'abord un cours.");
            return;
        }
        editingContenu = null;
        contenuFormTitleLabel.setText("Nouveau Contenu");
        contenuController.clearForm(
                contenuVideoCheck,
                contenuPdfCheck,
                contenuPptCheck,
                contenuTexteCheck,
                contenuQuizCheck,
                contenuLienCheck,
                contenuTitreField,
                contenuUrlField,
                contenuDescriptionArea,
                contenuDureeField,
                contenuOrdreField,
                contenuPublicCombo,
                contenuDateAjoutPicker,
                contenuVuesField,
                contenuFormatField,
                contenuRessourcesArea,
                contenuPdfField,
                contenuPptField,
                contenuVideoField,
                contenuLienField,
                contenuQuizField
        );
        contenuFormStatusLabel.setText("");
        clearContenuFieldErrors();
        updateContenuTypeSections();
        showContenuEditor();
    }

    @FXML
    private void cancelContenuForm() {
        showContenuBrowser();
    }

    @FXML
    private void saveContenu() {
        if (selectedCoursForContenus == null) {
            contenuFormStatusLabel.setText("Aucun cours selectionne.");
            return;
        }

        clearContenuFieldErrors();
        Map<String, String> validationErrors = contenuController.validateContenuForm(
                getSelectedContenuTypes(),
                contenuTitreField,
                contenuDescriptionArea,
                contenuDureeField,
                contenuOrdreField,
                contenuPublicCombo,
                contenuVuesField,
                contenuFormatField,
                contenuRessourcesArea,
                contenuUrlField,
                contenuPdfField,
                contenuPptField,
                contenuVideoField,
                contenuLienField,
                contenuQuizField
        );
        if (!validationErrors.isEmpty()) {
            applyContenuFieldErrors(validationErrors);
            contenuFormStatusLabel.setText("");
            return;
        }

        try {
            Contenu contenu = contenuController.buildContenu(
                    editingContenu,
                    selectedCoursForContenus,
                    getSelectedContenuTypes(),
                    contenuTitreField,
                    contenuUrlField,
                    contenuDescriptionArea,
                    contenuDureeField,
                    contenuOrdreField,
                    contenuPublicCombo,
                    contenuDateAjoutPicker,
                    contenuVuesField,
                    contenuFormatField,
                    contenuRessourcesArea,
                    contenuPdfField,
                    contenuPptField,
                    contenuVideoField,
                    contenuLienField,
                    contenuQuizField
            );

            if (contenuDemoMode) {
                saveContenuInDemoMode(contenu);
            } else if (editingContenu == null) {
                contenuService.create(contenu);
            } else {
                contenuService.update(contenu);
            }

            contenuMessageLabel.setText(editingContenu == null
                    ? "Contenu cree avec succes."
                    : "Contenu modifie avec succes.");
            loadContenusData();
            showContenuBrowser();
        } catch (SQLException e) {
            contenuFormStatusLabel.setText("Erreur contenu: " + e.getMessage());
        }
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
            if (utilisateurs.isEmpty()) {
                utilisateurs = buildDemoUsers();
                activityItems.setAll("Demo mode enabled: no users found in database.");
            }
            masterRows.clear();
            statRows.clear();
            if (activityItems.isEmpty()) {
                activityItems.clear();
            }
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

            if (activityItems.isEmpty()) {
                activityItems.add("Live registry loaded from database");
                activityItems.add("Profile actions are available from the user circle");
                activityItems.add("Sidebar switches between users, statistics, and create page");
                activityItems.add("ObservableList keeps the admin workspace reactive");
            } else {
                activityItems.add("Create form stays available for desktop UI testing");
                activityItems.add("Table actions remain clickable in demo mode");
            }

            applyFilters();
            if (!usersTable.getItems().isEmpty()) {
                usersTable.getSelectionModel().select(0);
            } else {
                updateDetails(null);
            }
        } catch (SQLException e) {
            populateDemoDashboard("Demo mode enabled: " + e.getMessage());
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

    private void setSectionVisibility(boolean usersVisible, boolean statsVisible, boolean createVisible, boolean modulesVisible, boolean coursVisible, boolean contenusVisible) {
        usersSection.setVisible(usersVisible);
        usersSection.setManaged(usersVisible);
        statsSection.setVisible(statsVisible);
        statsSection.setManaged(statsVisible);
        createSection.setVisible(createVisible);
        createSection.setManaged(createVisible);
        modulesSection.setVisible(modulesVisible);
        modulesSection.setManaged(modulesVisible);
        coursesSection.setVisible(coursVisible);
        coursesSection.setManaged(coursVisible);
        contenusSection.setVisible(contenusVisible);
        contenusSection.setManaged(contenusVisible);
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

    private void loadModulesData() {
        try {
            List<Module> modules = moduleService.getAll();
            moduleDemoMode = modules.isEmpty();
            moduleRows.setAll(moduleDemoMode ? buildDemoModules() : modules);
            if (moduleDemoMode) {
                moduleMessageLabel.setText("Mode demo module actif.");
            } else {
                moduleMessageLabel.setText("");
            }
        } catch (SQLException e) {
            moduleDemoMode = true;
            moduleRows.setAll(buildDemoModules());
            moduleMessageLabel.setText("Mode demo module: " + e.getMessage());
        }

        modulesTable.getSelectionModel().clearSelection();
        setDetailPanelVisible(moduleDetailsPanel, false);
        updateModuleDetails(null);
    }

    private void loadCoursData() {
        if (selectedModuleForCours == null) {
            coursRows.clear();
            updateCoursDetails(null);
            return;
        }

        try {
            List<Cours> cours = coursService.getByModuleId(selectedModuleForCours.getId());
            coursDemoMode = cours.isEmpty();
            coursRows.setAll(coursDemoMode ? getOrCreateDemoCours(selectedModuleForCours) : cours);
            coursMessageLabel.setText(coursDemoMode ? "Mode demo cours actif." : "");
        } catch (SQLException e) {
            coursDemoMode = true;
            coursRows.setAll(getOrCreateDemoCours(selectedModuleForCours));
            coursMessageLabel.setText("Mode demo cours: " + e.getMessage());
        }

        coursTable.getSelectionModel().clearSelection();
        setDetailPanelVisible(coursDetailsPanel, false);
        updateCoursDetails(null);
    }

    private void loadContenusData() {
        if (selectedCoursForContenus == null) {
            contenuRows.clear();
            updateContenuDetails(null);
            return;
        }

        try {
            List<Contenu> contenus = contenuService.getByCoursId(selectedCoursForContenus.getId());
            contenuDemoMode = contenus.isEmpty();
            contenuRows.setAll(contenuDemoMode ? getOrCreateDemoContenus(selectedCoursForContenus) : contenus);
            contenuMessageLabel.setText(contenuDemoMode ? "Mode demo contenu actif." : "");
        } catch (SQLException e) {
            contenuDemoMode = true;
            contenuRows.setAll(getOrCreateDemoContenus(selectedCoursForContenus));
            contenuMessageLabel.setText("Mode demo contenu: " + e.getMessage());
        }

        contenusTable.getSelectionModel().clearSelection();
        setDetailPanelVisible(contenuDetailsPanel, false);
        updateContenuDetails(null);
    }

    private void updateModuleDetails(Module module) {
        if (module == null) {
            moduleDetailsList.getItems().setAll(
                    "Aucun module selectionne.",
                    "Selectionnez un module dans la liste pour consulter ses informations detaillees."
            );
            return;
        }
        moduleDetailsList.getItems().setAll(
                "Titre: " + module.getTitreModule(),
                "Description: " + safeValue(module.getDescription()),
                "Ordre: " + module.getOrdreAffichage(),
                "Objectifs: " + safeValue(module.getObjectifsApprentissage()),
                "Duree estimee: " + (module.getDureeEstimeeHeures() != null ? module.getDureeEstimeeHeures() + " h" : "-"),
                "Publication: " + formatDateTime(module.getDatePublication()),
                "Statut: " + safeValue(module.getStatut()),
                "Ressources: " + (module.getRessourcesComplementaires() == null || module.getRessourcesComplementaires().isEmpty()
                        ? "-"
                        : String.join(", ", module.getRessourcesComplementaires()))
        );
    }

    private void updateCoursDetails(Cours cours) {
        if (cours == null) {
            coursDetailHeroTitleLabel.setText("Aucun cours selectionne");
            coursDetailHeroMetaLabel.setText("La fiche detaillee du cours apparaitra ici apres selection dans la liste.");
            coursDetailCodeLabel.setText("-");
            coursDetailTitreLabel.setText("-");
            coursDetailDescriptionLabel.setText("-");
            coursDetailNiveauLabel.setText("-");
            coursDetailCreditsLabel.setText("-");
            coursDetailLangueLabel.setText("-");
            coursDetailDateDebutLabel.setText("-");
            coursDetailDateFinLabel.setText("-");
            coursDetailStatutLabel.setText("-");
            coursDetailImageLabel.setText("-");
            coursDetailPrerequisLabel.setText("-");
            return;
        }
        coursDetailHeroTitleLabel.setText(safeValue(cours.getTitre()));
        coursDetailHeroMetaLabel.setText(safeValue(cours.getCodeCours()) + "  |  " + safeValue(cours.getStatut()));
        coursDetailCodeLabel.setText(safeValue(cours.getCodeCours()));
        coursDetailTitreLabel.setText(safeValue(cours.getTitre()));
        coursDetailDescriptionLabel.setText(safeValue(cours.getDescription()));
        coursDetailNiveauLabel.setText(safeValue(cours.getNiveau()));
        coursDetailCreditsLabel.setText(cours.getCredits() != null ? String.valueOf(cours.getCredits()) : "-");
        coursDetailLangueLabel.setText(safeValue(cours.getLangue()));
        coursDetailDateDebutLabel.setText(cours.getDateDebut() != null ? cours.getDateDebut().toString() : "-");
        coursDetailDateFinLabel.setText(cours.getDateFin() != null ? cours.getDateFin().toString() : "-");
        coursDetailStatutLabel.setText(safeValue(cours.getStatut()));
        coursDetailImageLabel.setText(safeValue(cours.getImageCoursUrl()));
        coursDetailPrerequisLabel.setText(cours.getPrerequis() == null || cours.getPrerequis().isEmpty()
                ? "-"
                : String.join(", ", cours.getPrerequis()));
    }

    private void updateContenuDetails(Contenu contenu) {
        if (contenu == null) {
            contenuHeroTitleLabel.setText("Aucun contenu selectionne");
            contenuHeroMetaLabel.setText("La fiche detaillee du contenu apparaitra ici apres selection dans la liste.");
            contenuDetailTypeLabel.setText("-");
            contenuDetailTitreLabel.setText("-");
            contenuDetailDescriptionLabel.setText("-");
            contenuDetailUrlLabel.setText("-");
            contenuDetailDureeLabel.setText("-");
            contenuDetailOrdreLabel.setText("-");
            contenuDetailPublicLabel.setText("-");
            contenuDetailDateAjoutLabel.setText("-");
            contenuDetailVuesLabel.setText("-");
            contenuDetailFormatLabel.setText("-");
            contenuDetailRessourcesLabel.setText("-");
            return;
        }

        contenuHeroTitleLabel.setText(safeValue(contenu.getTitre()));
        contenuHeroMetaLabel.setText(safeValue(contenu.getTypeContenu()) + "  |  ordre " + contenu.getOrdreAffichage());
        contenuDetailTypeLabel.setText(safeValue(contenu.getTypeContenu()));
        contenuDetailTitreLabel.setText(safeValue(contenu.getTitre()));
        contenuDetailDescriptionLabel.setText(safeValue(contenu.getDescription()));
        contenuDetailUrlLabel.setText(safeValue(contenu.getUrlContenu()));
        contenuDetailDureeLabel.setText(contenu.getDuree() != null ? contenu.getDuree() + " min" : "-");
        contenuDetailOrdreLabel.setText(String.valueOf(contenu.getOrdreAffichage()));
        contenuDetailPublicLabel.setText(contenu.isEstPublic() ? "Oui" : "Non");
        contenuDetailDateAjoutLabel.setText(formatDateTime(contenu.getDateAjout()));
        contenuDetailVuesLabel.setText(String.valueOf(contenu.getNombreVues()));
        contenuDetailFormatLabel.setText(safeValue(contenu.getFormat()));
        contenuDetailRessourcesLabel.setText(contenu.getRessources() == null || contenu.getRessources().isEmpty()
                ? "-"
                : String.join(", ", contenu.getRessources()));
    }

    @FXML
    private void closeModuleDetails() {
        modulesTable.getSelectionModel().clearSelection();
        setDetailPanelVisible(moduleDetailsPanel, false);
        updateModuleDetails(null);
    }

    @FXML
    private void closeCoursDetails() {
        coursTable.getSelectionModel().clearSelection();
        setDetailPanelVisible(coursDetailsPanel, false);
        updateCoursDetails(null);
    }

    @FXML
    private void closeContenuDetails() {
        contenusTable.getSelectionModel().clearSelection();
        setDetailPanelVisible(contenuDetailsPanel, false);
        updateContenuDetails(null);
    }

    private void setDetailPanelVisible(VBox panel, boolean visible) {
        if (panel == null) {
            return;
        }
        panel.setVisible(visible);
        panel.setManaged(visible);
    }

    @FXML
    private void editSelectedModule() {
        Module selectedModule = modulesTable.getSelectionModel().getSelectedItem();
        if (selectedModule == null) {
            moduleMessageLabel.setText("Selectionnez un module a modifier.");
            return;
        }
        editModule(selectedModule);
    }

    @FXML
    private void deleteSelectedModule() {
        Module selectedModule = modulesTable.getSelectionModel().getSelectedItem();
        if (selectedModule == null) {
            moduleMessageLabel.setText("Selectionnez un module a supprimer.");
            return;
        }
        deleteModule(selectedModule);
    }

    private void populateDemoDashboard(String reason) {
        masterRows.clear();
        statRows.clear();
        activityItems.clear();
        statsInsights.clear();

        List<Utilisateur> utilisateurs = buildDemoUsers();
        long students = utilisateurs.stream().filter(user -> "etudiant".equalsIgnoreCase(user.getType())).count();
        long teachers = utilisateurs.stream().filter(user -> "enseignant".equalsIgnoreCase(user.getType())).count();
        long admins = utilisateurs.stream().filter(user -> "administrateur".equalsIgnoreCase(user.getType())).count();

        for (Utilisateur utilisateur : utilisateurs) {
            masterRows.add(new UtilisateurRow(utilisateur));
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
                new StatRow("Active profiles", "3")
        );

        statsInsights.add("Scene Builder preview data is loaded.");
        statsInsights.add("Admin table includes one admin, one teacher, and one student.");
        statsInsights.add("Create form can be tested without depending on MySQL.");
        statsInsights.add(reason);

        activityItems.add("Demo admin registry loaded");
        activityItems.add("Preview details are available for each role");
        activityItems.add("Search and role filters can be tested immediately");
        activityItems.add("Switch between pages to validate layout spacing");

        applyFilters();
        if (!usersTable.getItems().isEmpty()) {
            usersTable.getSelectionModel().select(0);
        } else {
            updateDetails(null);
        }
    }

    private List<Utilisateur> buildDemoUsers() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        utilisateurs.add(buildDemoAdmin());
        utilisateurs.add(buildDemoTeacher());
        utilisateurs.add(buildDemoStudent());
        return utilisateurs;
    }

    private Administrateur buildDemoAdmin() {
        Administrateur administrateur = new Administrateur();
        administrateur.setId(1001L);
        administrateur.setNom("Mansouri");
        administrateur.setPrenom("Salma");
        administrateur.setEmail("salma.mansouri@demo.tn");
        administrateur.setMotDePasse("DemoAdmin123");
        administrateur.setDepartement("Informatique");
        administrateur.setFonction("Responsable Pedagogique");
        administrateur.setTelephone("70111222");
        administrateur.setDateCreation(LocalDateTime.now().minusMonths(10));
        administrateur.setDateNomination(LocalDateTime.now().minusMonths(8));
        administrateur.setLastLogin(LocalDateTime.now().minusHours(2));
        administrateur.setActif(true);
        return administrateur;
    }

    private Enseignant buildDemoTeacher() {
        Enseignant enseignant = new Enseignant();
        enseignant.setId(2001L);
        enseignant.setNom("Ben Salem");
        enseignant.setPrenom("Yasmine");
        enseignant.setEmail("yasmine.bensalem@demo.tn");
        enseignant.setMotDePasse("DemoTeacher123");
        enseignant.setMatriculeEnseignant("ENS-JAVA-24");
        enseignant.setDiplome("Doctorat");
        enseignant.setSpecialite("Genie Logiciel");
        enseignant.setTypeContrat("CDI");
        enseignant.setAnneesExperience(8);
        enseignant.setDisponibilites("Lundi au jeudi 08:00-16:00");
        enseignant.setStatut("actif");
        enseignant.setDateCreation(LocalDateTime.now().minusMonths(6));
        enseignant.setLastLogin(LocalDateTime.now().minusDays(1));
        return enseignant;
    }

    private Etudiant buildDemoStudent() {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(3001L);
        etudiant.setNom("Trabelsi");
        etudiant.setPrenom("Amine");
        etudiant.setEmail("amine.trabelsi@demo.tn");
        etudiant.setMotDePasse("DemoStudent123");
        etudiant.setMatricule("ETU-GL-2401");
        etudiant.setNiveauEtude("Master 1");
        etudiant.setSpecialisation("Genie Logiciel");
        etudiant.setTelephone("22123456");
        etudiant.setAdresse("Tunis, Centre Urbain Nord");
        etudiant.setDateNaissance(LocalDate.of(2002, 5, 14));
        etudiant.setDateInscription(LocalDateTime.now().minusMonths(7));
        etudiant.setDateCreation(LocalDateTime.now().minusMonths(7));
        etudiant.setLastLogin(LocalDateTime.now().minusHours(6));
        etudiant.setStatut("actif");
        return etudiant;
    }

    private List<Module> buildDemoModules() {
        Module module1 = new Module();
        module1.setId(1);
        module1.setTitreModule("Introduction Java");
        module1.setDescription("Bases du langage Java, syntaxe, classes et objets.");
        module1.setOrdreAffichage(1);
        module1.setObjectifsApprentissage("Comprendre les fondamentaux de Java et manipuler les classes.");
        module1.setDureeEstimeeHeures(12);
        module1.setDatePublication(LocalDate.now().minusDays(12).atStartOfDay());
        module1.setStatut("publie");
        module1.setRessourcesComplementaires(List.of("support-java.pdf", "tp-introduction.zip"));

        Module module2 = new Module();
        module2.setId(2);
        module2.setTitreModule("UML et Analyse");
        module2.setDescription("Diagrammes UML, cas d'utilisation et modelisation des besoins.");
        module2.setOrdreAffichage(2);
        module2.setObjectifsApprentissage("Savoir modeliser une application avant implementation.");
        module2.setDureeEstimeeHeures(8);
        module2.setDatePublication(LocalDate.now().minusDays(5).atStartOfDay());
        module2.setStatut("brouillon");
        module2.setRessourcesComplementaires(List.of("uml-guide.pdf"));

        return List.of(module1, module2);
    }

    private List<Cours> buildDemoCoursForModule(Module module) {
        Cours cours1 = new Cours();
        cours1.setId(module.getId() * 100 + 1);
        cours1.setModule(module);
        cours1.setCodeCours("CRS-" + module.getId() + "-01");
        cours1.setTitre("Introduction du module");
        cours1.setDescription("Cours d'introduction et presentation generale du module.");
        cours1.setNiveau("Licence 3");
        cours1.setCredits(3);
        cours1.setLangue("Francais");
        cours1.setDateDebut(LocalDate.now().minusDays(3));
        cours1.setDateFin(LocalDate.now().plusDays(15));
        cours1.setStatut("publie");
        cours1.setImageCoursUrl("https://example.com/course-cover.png");
        cours1.setPrerequis(List.of("Bases generale", "Motivation"));

        Cours cours2 = new Cours();
        cours2.setId(module.getId() * 100 + 2);
        cours2.setModule(module);
        cours2.setCodeCours("CRS-" + module.getId() + "-02");
        cours2.setTitre("Atelier pratique");
        cours2.setDescription("Mise en pratique des concepts abordes dans le module.");
        cours2.setNiveau("Licence 3");
        cours2.setCredits(2);
        cours2.setLangue("Francais");
        cours2.setDateDebut(LocalDate.now().plusDays(7));
        cours2.setDateFin(LocalDate.now().plusDays(30));
        cours2.setStatut("brouillon");
        cours2.setPrerequis(List.of("Cours introductif"));

        return List.of(cours1, cours2);
    }

    private List<Contenu> buildDemoContenusForCours(Cours cours) {
        Contenu contenu1 = new Contenu();
        contenu1.setId(cours.getId() * 100 + 1);
        contenu1.setCours(cours);
        contenu1.setTypeContenu("video");
        contenu1.setTitre("Introduction video");
        contenu1.setUrlContenu("https://example.com/video-intro");
        contenu1.setDescription("Presentation video du cours.");
        contenu1.setDuree(12);
        contenu1.setOrdreAffichage(1);
        contenu1.setEstPublic(true);
        contenu1.setDateAjout(LocalDateTime.now().minusDays(2));
        contenu1.setNombreVues(31);
        contenu1.setFormat("mp4");
        contenu1.setRessources(List.of("slides-intro.pdf"));

        Contenu contenu2 = new Contenu();
        contenu2.setId(cours.getId() * 100 + 2);
        contenu2.setCours(cours);
        contenu2.setTypeContenu("pdf");
        contenu2.setTitre("Support de cours");
        contenu2.setUrlContenu("support-" + cours.getCodeCours() + ".pdf");
        contenu2.setDescription("Document PDF du cours.");
        contenu2.setOrdreAffichage(2);
        contenu2.setEstPublic(false);
        contenu2.setDateAjout(LocalDateTime.now().minusDays(1));
        contenu2.setNombreVues(8);
        contenu2.setFormat("pdf");
        contenu2.setRessources(List.of("annexe-1.pdf", "annexe-2.pdf"));

        return List.of(contenu1, contenu2);
    }

    private void showModuleBrowser() {
        editingModule = null;
        moduleBrowseSection.setVisible(true);
        moduleBrowseSection.setManaged(true);
        moduleEditorSection.setVisible(false);
        moduleEditorSection.setManaged(false);
        moduleFormTitleLabel.setText("Nouveau module");
        moduleController.clearForm(
                moduleTitreField,
                moduleDescriptionArea,
                moduleOrdreField,
                moduleObjectifsArea,
                moduleDureeField,
                moduleDatePublicationPicker,
                moduleStatutCombo,
                moduleRessourcesArea
        );
        moduleFormStatusLabel.setText("");
        clearModuleFieldErrors();
    }

    private void showModuleEditor() {
        moduleBrowseSection.setVisible(false);
        moduleBrowseSection.setManaged(false);
        moduleEditorSection.setVisible(true);
        moduleEditorSection.setManaged(true);
    }

    private void showCoursBrowser() {
        editingCours = null;
        coursBrowseSection.setVisible(true);
        coursBrowseSection.setManaged(true);
        coursEditorSection.setVisible(false);
        coursEditorSection.setManaged(false);
        coursFormTitleLabel.setText("Nouveau Cours");
        coursController.clearForm(
                coursCodeField,
                coursTitreField,
                coursDescriptionArea,
                coursNiveauField,
                coursCreditsField,
                coursLangueField,
                coursDateDebutPicker,
                coursDateFinPicker,
                coursStatutCombo,
                coursImageField,
                coursPrerequisArea
        );
        coursFormStatusLabel.setText("");
        clearCoursFieldErrors();
    }

    private void showCoursEditor() {
        coursBrowseSection.setVisible(false);
        coursBrowseSection.setManaged(false);
        coursEditorSection.setVisible(true);
        coursEditorSection.setManaged(true);
    }

    private void showContenuBrowser() {
        editingContenu = null;
        contenuBrowseSection.setVisible(true);
        contenuBrowseSection.setManaged(true);
        contenuEditorSection.setVisible(false);
        contenuEditorSection.setManaged(false);
        contenuFormTitleLabel.setText("Nouveau Contenu");
        contenuController.clearForm(
                contenuVideoCheck,
                contenuPdfCheck,
                contenuPptCheck,
                contenuTexteCheck,
                contenuQuizCheck,
                contenuLienCheck,
                contenuTitreField,
                contenuUrlField,
                contenuDescriptionArea,
                contenuDureeField,
                contenuOrdreField,
                contenuPublicCombo,
                contenuDateAjoutPicker,
                contenuVuesField,
                contenuFormatField,
                contenuRessourcesArea,
                contenuPdfField,
                contenuPptField,
                contenuVideoField,
                contenuLienField,
                contenuQuizField
        );
        contenuFormStatusLabel.setText("");
        clearContenuFieldErrors();
        updateContenuTypeSections();
    }

    private void showContenuEditor() {
        contenuBrowseSection.setVisible(false);
        contenuBrowseSection.setManaged(false);
        contenuEditorSection.setVisible(true);
        contenuEditorSection.setManaged(true);
    }

    private void saveModuleInDemoMode(Module module) {
        if (editingModule == null) {
            int nextId = moduleRows.stream()
                    .map(Module::getId)
                    .filter(id -> id != null)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
            module.setId(nextId);
            moduleRows.add(module);
        } else {
            modulesTable.refresh();
        }
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void editModule(Module selectedModule) {
        editingModule = selectedModule;
        moduleFormTitleLabel.setText("Modifier le module");
        moduleController.populateForm(
                selectedModule,
                moduleTitreField,
                moduleDescriptionArea,
                moduleOrdreField,
                moduleObjectifsArea,
                moduleDureeField,
                moduleDatePublicationPicker,
                moduleStatutCombo,
                moduleRessourcesArea
        );
        moduleFormStatusLabel.setText("");
        clearModuleFieldErrors();
        showModuleEditor();
    }

    private void openModuleCours(Module module) {
        modulesTable.getSelectionModel().select(module);
        selectedModuleForCours = module;
        showCoursPage();
    }

    private void openCoursContenus(Cours cours) {
        coursTable.getSelectionModel().select(cours);
        selectedCoursForContenus = cours;
        showContenusPage();
    }

    private void deleteModule(Module selectedModule) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer Module");
        confirmation.setHeaderText("Supprimer le module " + safeValue(selectedModule.getTitreModule()) + " ?");
        confirmation.setContentText("Cette action supprimera le module selectionne.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        if (moduleDemoMode) {
            moduleRows.remove(selectedModule);
            updateModuleDetails(modulesTable.getSelectionModel().getSelectedItem());
            moduleMessageLabel.setText("Module demo supprime.");
            return;
        }

        try {
            moduleService.delete(selectedModule.getId());
            loadModulesData();
            moduleMessageLabel.setText("Module supprime avec succes.");
        } catch (SQLException e) {
            moduleMessageLabel.setText("Suppression impossible: " + e.getMessage());
        }
    }

    @FXML
    private void editSelectedCours() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            coursMessageLabel.setText("Selectionnez un cours a modifier.");
            return;
        }
        editCours(selectedCours);
    }

    @FXML
    private void deleteSelectedCours() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            coursMessageLabel.setText("Selectionnez un cours a supprimer.");
            return;
        }
        deleteCours(selectedCours);
    }

    private void editCours(Cours cours) {
        editingCours = cours;
        coursFormTitleLabel.setText("Modifier Cours");
        coursController.populateForm(
                cours,
                coursCodeField,
                coursTitreField,
                coursDescriptionArea,
                coursNiveauField,
                coursCreditsField,
                coursLangueField,
                coursDateDebutPicker,
                coursDateFinPicker,
                coursStatutCombo,
                coursImageField,
                coursPrerequisArea
        );
        coursFormStatusLabel.setText("");
        clearCoursFieldErrors();
        showCoursEditor();
    }

    private void deleteCours(Cours cours) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer Cours");
        confirmation.setHeaderText("Supprimer le cours " + safeValue(cours.getTitre()) + " ?");
        confirmation.setContentText("Cette action supprimera le cours selectionne.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        if (coursDemoMode) {
            coursRows.remove(cours);
            updateCoursDetails(coursTable.getSelectionModel().getSelectedItem());
            coursMessageLabel.setText("Cours demo supprime.");
            return;
        }

        try {
            coursService.delete(cours.getId());
            loadCoursData();
            coursMessageLabel.setText("Cours supprime avec succes.");
        } catch (SQLException e) {
            coursMessageLabel.setText("Suppression impossible: " + e.getMessage());
        }
    }

    @FXML
    private void editSelectedContenu() {
        Contenu selectedContenu = contenusTable.getSelectionModel().getSelectedItem();
        if (selectedContenu == null) {
            contenuMessageLabel.setText("Selectionnez un contenu a modifier.");
            return;
        }
        editContenu(selectedContenu);
    }

    @FXML
    private void deleteSelectedContenu() {
        Contenu selectedContenu = contenusTable.getSelectionModel().getSelectedItem();
        if (selectedContenu == null) {
            contenuMessageLabel.setText("Selectionnez un contenu a supprimer.");
            return;
        }
        deleteContenu(selectedContenu);
    }

    private void editContenu(Contenu contenu) {
        editingContenu = contenu;
        contenuFormTitleLabel.setText("Modifier Contenu");
        contenuController.populateForm(
                contenu,
                contenuVideoCheck,
                contenuPdfCheck,
                contenuPptCheck,
                contenuTexteCheck,
                contenuQuizCheck,
                contenuLienCheck,
                contenuTitreField,
                contenuUrlField,
                contenuDescriptionArea,
                contenuDureeField,
                contenuOrdreField,
                contenuPublicCombo,
                contenuDateAjoutPicker,
                contenuVuesField,
                contenuFormatField,
                contenuRessourcesArea,
                contenuPdfField,
                contenuPptField,
                contenuVideoField,
                contenuLienField,
                contenuQuizField
        );
        contenuFormStatusLabel.setText("");
        clearContenuFieldErrors();
        updateContenuTypeSections();
        showContenuEditor();
    }

    private void deleteContenu(Contenu contenu) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer Contenu");
        confirmation.setHeaderText("Supprimer le contenu " + safeValue(contenu.getTitre()) + " ?");
        confirmation.setContentText("Cette action supprimera le contenu selectionne.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        if (contenuDemoMode) {
            List<Contenu> demoContenus = getOrCreateDemoContenus(selectedCoursForContenus);
            demoContenus.removeIf(item -> item.getId() != null && item.getId().equals(contenu.getId()));
            contenuRows.setAll(demoContenus);
            updateContenuDetails(contenusTable.getSelectionModel().getSelectedItem());
            contenuMessageLabel.setText("Contenu demo supprime.");
            return;
        }

        try {
            contenuService.delete(contenu.getId());
            loadContenusData();
            contenuMessageLabel.setText("Contenu supprime avec succes.");
        } catch (SQLException e) {
            contenuMessageLabel.setText("Suppression impossible: " + e.getMessage());
        }
    }

    private void saveCoursInDemoMode(Cours cours) {
        if (selectedModuleForCours == null) {
            return;
        }

        List<Cours> demoCours = getOrCreateDemoCours(selectedModuleForCours);
        if (editingCours == null) {
            int nextId = demoCours.stream()
                    .map(Cours::getId)
                    .filter(id -> id != null)
                    .max(Integer::compareTo)
                    .orElse((selectedModuleForCours != null ? selectedModuleForCours.getId() * 100 : 0)) + 1;
            cours.setId(nextId);
            demoCours.add(cours);
        } else {
            for (int i = 0; i < demoCours.size(); i++) {
                if (demoCours.get(i).getId() != null && demoCours.get(i).getId().equals(cours.getId())) {
                    demoCours.set(i, cours);
                    break;
                }
            }
            coursTable.refresh();
        }
    }

    private List<Cours> getOrCreateDemoCours(Module module) {
        return demoCoursByModuleId.computeIfAbsent(module.getId(), ignored -> new ArrayList<>(buildDemoCoursForModule(module)));
    }

    private List<Contenu> getOrCreateDemoContenus(Cours cours) {
        return demoContenusByCoursId.computeIfAbsent(cours.getId(), ignored -> new ArrayList<>(buildDemoContenusForCours(cours)));
    }

    private void clearCoursFieldErrors() {
        coursCodeErrorLabel.setText("");
        coursTitreErrorLabel.setText("");
        coursDescriptionErrorLabel.setText("");
        coursNiveauErrorLabel.setText("");
        coursCreditsErrorLabel.setText("");
        coursLangueErrorLabel.setText("");
        coursDateDebutErrorLabel.setText("");
        coursDateFinErrorLabel.setText("");
        coursStatutErrorLabel.setText("");
        coursImageErrorLabel.setText("");
        coursPrerequisErrorLabel.setText("");
    }

    private void clearModuleFieldErrors() {
        moduleTitreErrorLabel.setText("");
        moduleDescriptionErrorLabel.setText("");
        moduleOrdreErrorLabel.setText("");
        moduleObjectifsErrorLabel.setText("");
        moduleDureeErrorLabel.setText("");
        moduleDatePublicationErrorLabel.setText("");
        moduleStatutErrorLabel.setText("");
        moduleRessourcesErrorLabel.setText("");
    }

    private void clearContenuFieldErrors() {
        contenuTypeErrorLabel.setText("");
        contenuTitreErrorLabel.setText("");
        contenuUrlErrorLabel.setText("");
        contenuDescriptionErrorLabel.setText("");
        contenuDureeErrorLabel.setText("");
        contenuOrdreErrorLabel.setText("");
        contenuPublicErrorLabel.setText("");
        contenuDateAjoutErrorLabel.setText("");
        contenuVuesErrorLabel.setText("");
        contenuFormatErrorLabel.setText("");
        contenuRessourcesErrorLabel.setText("");
        contenuPdfErrorLabel.setText("");
        contenuPptErrorLabel.setText("");
        contenuVideoErrorLabel.setText("");
        contenuLienErrorLabel.setText("");
        contenuQuizErrorLabel.setText("");
    }

    private void applyCoursFieldErrors(Map<String, String> errors) {
        coursCodeErrorLabel.setText(errors.getOrDefault("code", ""));
        coursTitreErrorLabel.setText(errors.getOrDefault("titre", ""));
        coursDescriptionErrorLabel.setText(errors.getOrDefault("description", ""));
        coursNiveauErrorLabel.setText(errors.getOrDefault("niveau", ""));
        coursCreditsErrorLabel.setText(errors.getOrDefault("credits", ""));
        coursLangueErrorLabel.setText(errors.getOrDefault("langue", ""));
        coursDateDebutErrorLabel.setText(errors.getOrDefault("date_debut", ""));
        coursDateFinErrorLabel.setText(errors.getOrDefault("date_fin", ""));
        coursStatutErrorLabel.setText(errors.getOrDefault("statut", ""));
        coursImageErrorLabel.setText(errors.getOrDefault("image", ""));
        coursPrerequisErrorLabel.setText(errors.getOrDefault("prerequis", ""));
    }

    private void applyModuleFieldErrors(Map<String, String> errors) {
        moduleTitreErrorLabel.setText(errors.getOrDefault("titre", ""));
        moduleDescriptionErrorLabel.setText(errors.getOrDefault("description", ""));
        moduleOrdreErrorLabel.setText(errors.getOrDefault("ordre", ""));
        moduleObjectifsErrorLabel.setText(errors.getOrDefault("objectifs", ""));
        moduleDureeErrorLabel.setText(errors.getOrDefault("duree", ""));
        moduleDatePublicationErrorLabel.setText(errors.getOrDefault("date_publication", ""));
        moduleStatutErrorLabel.setText(errors.getOrDefault("statut", ""));
        moduleRessourcesErrorLabel.setText(errors.getOrDefault("ressources", ""));
    }

    private void applyContenuFieldErrors(Map<String, String> errors) {
        contenuTypeErrorLabel.setText(errors.getOrDefault("type", ""));
        contenuTitreErrorLabel.setText(errors.getOrDefault("titre", ""));
        contenuUrlErrorLabel.setText(errors.getOrDefault("url", ""));
        contenuDescriptionErrorLabel.setText(errors.getOrDefault("description", ""));
        contenuDureeErrorLabel.setText(errors.getOrDefault("duree", ""));
        contenuOrdreErrorLabel.setText(errors.getOrDefault("ordre", ""));
        contenuPublicErrorLabel.setText(errors.getOrDefault("public", ""));
        contenuDateAjoutErrorLabel.setText(errors.getOrDefault("date_ajout", ""));
        contenuVuesErrorLabel.setText(errors.getOrDefault("vues", ""));
        contenuFormatErrorLabel.setText(errors.getOrDefault("format", ""));
        contenuRessourcesErrorLabel.setText(errors.getOrDefault("ressources", ""));
        contenuPdfErrorLabel.setText(errors.getOrDefault("pdf", ""));
        contenuPptErrorLabel.setText(errors.getOrDefault("ppt", ""));
        contenuVideoErrorLabel.setText(errors.getOrDefault("video", ""));
        contenuLienErrorLabel.setText(errors.getOrDefault("lien", ""));
        contenuQuizErrorLabel.setText(errors.getOrDefault("quiz", ""));
    }

    private void bindContenuTypeChecks() {
        contenuVideoCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateContenuTypeSections());
        contenuPdfCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateContenuTypeSections());
        contenuPptCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateContenuTypeSections());
        contenuTexteCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateContenuTypeSections());
        contenuQuizCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateContenuTypeSections());
        contenuLienCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateContenuTypeSections());
    }

    private List<String> getSelectedContenuTypes() {
        List<String> selectedTypes = new ArrayList<>();
        if (contenuVideoCheck.isSelected()) {
            selectedTypes.add("video");
        }
        if (contenuPdfCheck.isSelected()) {
            selectedTypes.add("pdf");
        }
        if (contenuPptCheck.isSelected()) {
            selectedTypes.add("ppt");
        }
        if (contenuTexteCheck.isSelected()) {
            selectedTypes.add("texte");
        }
        if (contenuQuizCheck.isSelected()) {
            selectedTypes.add("quiz");
        }
        if (contenuLienCheck.isSelected()) {
            selectedTypes.add("lien");
        }
        return selectedTypes;
    }

    private void updateContenuTypeSections() {
        List<String> selectedTypes = getSelectedContenuTypes();
        toggleTypeSection(contenuPdfSection, selectedTypes.contains("pdf"));
        toggleTypeSection(contenuPptSection, selectedTypes.contains("ppt"));
        toggleTypeSection(contenuVideoSection, selectedTypes.contains("video"));
        toggleTypeSection(contenuLienSection, selectedTypes.contains("lien"));
        toggleTypeSection(contenuQuizSection, selectedTypes.contains("quiz"));
        toggleTypeSection(contenuTexteSection, selectedTypes.contains("texte"));
        clearUnusedContenuFields(selectedTypes);
    }

    private void clearUnusedContenuFields(List<String> selectedTypes) {
        if (!selectedTypes.contains("pdf")) {
            contenuPdfField.clear();
            contenuPdfErrorLabel.setText("");
        }
        if (!selectedTypes.contains("ppt")) {
            contenuPptField.clear();
            contenuPptErrorLabel.setText("");
        }
        if (!selectedTypes.contains("video")) {
            contenuVideoField.clear();
            contenuVideoErrorLabel.setText("");
        }
        if (!selectedTypes.contains("lien")) {
            contenuLienField.clear();
            contenuLienErrorLabel.setText("");
        }
        if (!selectedTypes.contains("quiz")) {
            contenuQuizField.clear();
            contenuQuizErrorLabel.setText("");
        }
        if (!selectedTypes.contains("texte")) {
            contenuDescriptionErrorLabel.setText("");
        }
    }

    private void toggleTypeSection(VBox section, boolean visible) {
        if (section == null) {
            return;
        }
        section.setVisible(visible);
        section.setManaged(visible);
    }

    @FXML
    private void browseContenuPdfFile() {
        chooseFileForField(contenuPdfField, "Selectionner un PDF", new FileChooser.ExtensionFilter("PDF", "*.pdf"));
    }

    @FXML
    private void browseContenuPptFile() {
        chooseFileForField(
                contenuPptField,
                "Selectionner un PowerPoint",
                new FileChooser.ExtensionFilter("PowerPoint", "*.ppt", "*.pptx")
        );
    }

    @FXML
    private void browseContenuVideoFile() {
        chooseFileForField(
                contenuVideoField,
                "Selectionner une video",
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov", "*.mkv", "*.wmv")
        );
    }

    private void chooseFileForField(TextField targetField, String title, FileChooser.ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(extensionFilter);
        if (contenuEditorSection.getScene() == null) {
            return;
        }
        java.io.File selectedFile = fileChooser.showOpenDialog(contenuEditorSection.getScene().getWindow());
        if (selectedFile != null) {
            targetField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void saveContenuInDemoMode(Contenu contenu) {
        if (selectedCoursForContenus == null) {
            return;
        }

        List<Contenu> demoContenus = getOrCreateDemoContenus(selectedCoursForContenus);
        if (editingContenu == null) {
            int nextId = demoContenus.stream()
                    .map(Contenu::getId)
                    .filter(id -> id != null)
                    .max(Integer::compareTo)
                    .orElse(selectedCoursForContenus.getId() * 100) + 1;
            contenu.setId(nextId);
            demoContenus.add(contenu);
        } else {
            for (int i = 0; i < demoContenus.size(); i++) {
                if (demoContenus.get(i).getId() != null && demoContenus.get(i).getId().equals(contenu.getId())) {
                    demoContenus.set(i, contenu);
                    break;
                }
            }
        }
    }
}
