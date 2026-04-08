package gui;

import entities.Contenu;
import entities.Cours;
import entities.Enseignant;
import entities.Etudiant;
import entities.Module;
import entities.Utilisateur;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import services.ContenuService;
import services.CoursService;
import services.ModuleService;
import utils.SceneManager;
import utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeacherDashboardController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label sectionTitleLabel;
    @FXML private Label sectionSubtitleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label currentUserNameLabel;
    @FXML private Label currentUserRoleLabel;
    @FXML private MenuButton profileMenuButton;
    @FXML private VBox overviewSection;
    @FXML private VBox coursesSection;
    @FXML private VBox coursesBrowseSection;
    @FXML private VBox courseEditorSection;
    @FXML private VBox contenusSection;
    @FXML private VBox contenuBrowseSection;
    @FXML private VBox contenuDetailsPanel;
    @FXML private VBox contenuEditorSection;
    @FXML private VBox studentsSection;
    @FXML private Label coursesCountLabel;
    @FXML private Label studentsCountLabel;
    @FXML private Label publishedCoursesLabel;
    @FXML private TableView<KeyValueRow> profileTable;
    @FXML private TableColumn<KeyValueRow, String> profileFieldColumn;
    @FXML private TableColumn<KeyValueRow, String> profileValueColumn;
    @FXML private TableView<KeyValueRow> focusTable;
    @FXML private TableColumn<KeyValueRow, String> focusTitleColumn;
    @FXML private TableColumn<KeyValueRow, String> focusDescriptionColumn;
    @FXML private Label teacherCourseContextLabel;
    @FXML private Label coursesMessageLabel;
    @FXML private TableView<Cours> coursesTable;
    @FXML private TableColumn<Cours, String> courseCodeColumn;
    @FXML private TableColumn<Cours, String> courseTitleColumn;
    @FXML private TableColumn<Cours, String> courseModuleColumn;
    @FXML private TableColumn<Cours, String> courseLevelColumn;
    @FXML private TableColumn<Cours, String> courseStatusColumn;
    @FXML private TableColumn<Cours, Integer> courseStudentsColumn;
    @FXML private TableColumn<Cours, Void> courseActionsColumn;
    @FXML private VBox courseDetailsPanel;
    @FXML private Label courseDetailHeroTitleLabel;
    @FXML private Label courseDetailHeroMetaLabel;
    @FXML private Label courseDetailCodeLabel;
    @FXML private Label courseDetailTitleLabel;
    @FXML private Label courseDetailModuleLabel;
    @FXML private Label courseDetailDescriptionLabel;
    @FXML private Label courseDetailLevelLabel;
    @FXML private Label courseDetailCreditsLabel;
    @FXML private Label courseDetailLanguageLabel;
    @FXML private Label courseDetailDatesLabel;
    @FXML private Label courseDetailStatusLabel;
    @FXML private Label courseDetailStudentsLabel;
    @FXML private Label courseDetailImageLabel;
    @FXML private Label courseDetailPrerequisLabel;
    @FXML private Label courseFormTitleLabel;
    @FXML private ComboBox<Module> courseModuleCombo;
    @FXML private Label courseModuleErrorLabel;
    @FXML private TextField courseCodeField;
    @FXML private Label courseCodeErrorLabel;
    @FXML private TextField courseTitleField;
    @FXML private Label courseTitleErrorLabel;
    @FXML private TextArea courseDescriptionArea;
    @FXML private Label courseDescriptionErrorLabel;
    @FXML private TextField courseLevelField;
    @FXML private Label courseLevelErrorLabel;
    @FXML private TextField courseCreditsField;
    @FXML private Label courseCreditsErrorLabel;
    @FXML private TextField courseLanguageField;
    @FXML private Label courseLanguageErrorLabel;
    @FXML private ComboBox<String> courseStatusCombo;
    @FXML private Label courseStatusErrorLabel;
    @FXML private DatePicker courseDateDebutPicker;
    @FXML private Label courseDateDebutErrorLabel;
    @FXML private DatePicker courseDateFinPicker;
    @FXML private Label courseDateFinErrorLabel;
    @FXML private TextField courseImageField;
    @FXML private Label courseImageErrorLabel;
    @FXML private TextArea coursePrerequisArea;
    @FXML private Label coursePrerequisErrorLabel;
    @FXML private Label courseFormStatusLabel;
    @FXML private Label currentCoursTitleLabel;
    @FXML private Label currentCoursSubtitleLabel;
    @FXML private Label contenuMessageLabel;
    @FXML private TableView<Contenu> contenusTable;
    @FXML private TableColumn<Contenu, Integer> contenuIdColumn;
    @FXML private TableColumn<Contenu, String> contenuTypeColumn;
    @FXML private TableColumn<Contenu, String> contenuTitreColumn;
    @FXML private TableColumn<Contenu, Integer> contenuOrdreColumn;
    @FXML private TableColumn<Contenu, Integer> contenuDureeColumn;
    @FXML private TableColumn<Contenu, Void> contenuActionsColumn;
    @FXML private Label contenuHeroTitleLabel;
    @FXML private Label contenuHeroMetaLabel;
    @FXML private Label contenuDetailTypeLabel;
    @FXML private Label contenuDetailTitreLabel;
    @FXML private Label contenuDetailDescriptionLabel;
    @FXML private Label contenuDetailUrlLabel;
    @FXML private Label contenuDetailDureeLabel;
    @FXML private Label contenuDetailOrdreLabel;
    @FXML private Label contenuDetailPublicLabel;
    @FXML private Label contenuDetailDateAjoutLabel;
    @FXML private Label contenuDetailVuesLabel;
    @FXML private Label contenuDetailFormatLabel;
    @FXML private Label contenuDetailRessourcesLabel;
    @FXML private Label contenuFormTitleLabel;
    @FXML private CheckBox contenuVideoCheck;
    @FXML private CheckBox contenuPdfCheck;
    @FXML private CheckBox contenuPptCheck;
    @FXML private CheckBox contenuTexteCheck;
    @FXML private CheckBox contenuQuizCheck;
    @FXML private CheckBox contenuLienCheck;
    @FXML private TextField contenuTitreField;
    @FXML private TextField contenuUrlField;
    @FXML private TextArea contenuDescriptionArea;
    @FXML private TextField contenuDureeField;
    @FXML private TextField contenuOrdreField;
    @FXML private ComboBox<String> contenuPublicCombo;
    @FXML private DatePicker contenuDateAjoutPicker;
    @FXML private TextField contenuVuesField;
    @FXML private TextField contenuFormatField;
    @FXML private TextArea contenuRessourcesArea;
    @FXML private Label contenuFormStatusLabel;
    @FXML private VBox contenuPdfSection;
    @FXML private VBox contenuPptSection;
    @FXML private VBox contenuVideoSection;
    @FXML private VBox contenuLienSection;
    @FXML private VBox contenuQuizSection;
    @FXML private VBox contenuTexteSection;
    @FXML private TextField contenuPdfField;
    @FXML private TextField contenuPptField;
    @FXML private TextField contenuVideoField;
    @FXML private TextField contenuLienField;
    @FXML private TextField contenuQuizField;
    @FXML private Label contenuTypeErrorLabel;
    @FXML private Label contenuTitreErrorLabel;
    @FXML private Label contenuUrlErrorLabel;
    @FXML private Label contenuDescriptionErrorLabel;
    @FXML private Label contenuDureeErrorLabel;
    @FXML private Label contenuOrdreErrorLabel;
    @FXML private Label contenuPublicErrorLabel;
    @FXML private Label contenuDateAjoutErrorLabel;
    @FXML private Label contenuVuesErrorLabel;
    @FXML private Label contenuFormatErrorLabel;
    @FXML private Label contenuRessourcesErrorLabel;
    @FXML private Label contenuPdfErrorLabel;
    @FXML private Label contenuPptErrorLabel;
    @FXML private Label contenuVideoErrorLabel;
    @FXML private Label contenuLienErrorLabel;
    @FXML private Label contenuQuizErrorLabel;
    @FXML private Label studentsHeroTitleLabel;
    @FXML private Label studentsHeroSubtitleLabel;
    @FXML private ComboBox<CourseFilterOption> studentsCourseFilterCombo;
    @FXML private TableView<Etudiant> studentsTable;
    @FXML private TableColumn<Etudiant, String> studentMatriculeColumn;
    @FXML private TableColumn<Etudiant, String> studentNameColumn;
    @FXML private TableColumn<Etudiant, String> studentEmailColumn;
    @FXML private TableColumn<Etudiant, String> studentLevelColumn;
    @FXML private TableColumn<Etudiant, String> studentSpecialiteColumn;
    @FXML private VBox studentDetailsPanel;
    @FXML private Label studentDetailHeroTitleLabel;
    @FXML private Label studentDetailHeroMetaLabel;
    @FXML private Label studentDetailMatriculeLabel;
    @FXML private Label studentDetailEmailLabel;
    @FXML private Label studentDetailLevelLabel;
    @FXML private Label studentDetailSpecialiteLabel;
    @FXML private Label studentDetailPhoneLabel;
    @FXML private Label studentDetailAddressLabel;

    private final ObservableList<KeyValueRow> profileRows = FXCollections.observableArrayList();
    private final ObservableList<KeyValueRow> focusRows = FXCollections.observableArrayList();
    private final ObservableList<Cours> teacherCourses = FXCollections.observableArrayList();
    private final ObservableList<Contenu> contenuRows = FXCollections.observableArrayList();
    private final ObservableList<Etudiant> teacherStudents = FXCollections.observableArrayList();
    private final ObservableList<Module> availableModules = FXCollections.observableArrayList();
    private final ObservableList<CourseFilterOption> courseFilterOptions = FXCollections.observableArrayList();
    private final Map<Integer, Integer> studentCountByCourseId = new LinkedHashMap<>();
    private final CoursService coursService = new CoursService();
    private final ModuleService moduleService = new ModuleService();
    private final ContenuService contenuService = new ContenuService();
    private final CoursController coursController = new CoursController();
    private final ContenuController contenuController = new ContenuController();

    private Enseignant currentTeacher;
    private Cours selectedCourse;
    private Cours selectedCoursForContenus;
    private Cours editingCourse;
    private Contenu editingContenu;

    @FXML
    private void initialize() {
        currentTeacher = resolveTeacher();
        configureHeader();
        configureOverviewTables();
        configureCoursesTable();
        configureContenuTable();
        configureStudentsTable();
        configureCourseForm();
        loadModules();
        refreshTeacherData();
        showOverviewPage();
    }

    @FXML
    private void showOverviewPage() {
        setPageVisibility(true, false, false, false);
        sectionTitleLabel.setText("Espace enseignant");
        sectionSubtitleLabel.setText("Consultez vos cours, vos etudiants et votre activite d'enseignement.");
    }

    @FXML
    private void showCoursesPage() {
        setPageVisibility(false, true, false, false);
        showCourseBrowser();
        sectionTitleLabel.setText("Mes cours");
        sectionSubtitleLabel.setText("Consultez, ajoutez et mettez a jour les cours rattaches a votre compte enseignant.");
    }

    @FXML
    private void showContenusPage() {
        if (selectedCoursForContenus == null) {
            coursesMessageLabel.setText("Selectionnez un cours pour voir ses contenus.");
            showCoursesPage();
            return;
        }
        setPageVisibility(false, false, true, false);
        sectionTitleLabel.setText("Contenus");
        sectionSubtitleLabel.setText("Gerez les contenus rattaches au cours selectionne.");
        currentCoursTitleLabel.setText(safe(selectedCoursForContenus.getTitre()));
        currentCoursSubtitleLabel.setText(safe(selectedCoursForContenus.getCodeCours()) + " | " + safe(selectedCoursForContenus.getStatut()));
        loadContenusData();
        showContenuBrowser();
    }

    @FXML
    private void showStudentsPage() {
        setPageVisibility(false, false, false, true);
        sectionTitleLabel.setText("Mes etudiants");
        sectionSubtitleLabel.setText("Consultez les etudiants inscrits a vos cours avec filtre par cours.");
        updateStudentsHero();
        applyStudentsFilter();
    }

    @FXML
    private void showCourseCreateForm() {
        editingCourse = null;
        selectedCourse = null;
        courseFormTitleLabel.setText("Nouveau cours");
        courseFormStatusLabel.setText("");
        clearCourseFieldErrors();
        coursController.clearForm(
                courseCodeField, courseTitleField, courseDescriptionArea, courseLevelField, courseCreditsField,
                courseLanguageField, courseDateDebutPicker, courseDateFinPicker, courseStatusCombo,
                courseImageField, coursePrerequisArea
        );
        courseModuleCombo.setValue(availableModules.isEmpty() ? null : availableModules.get(0));
        showCoursesPage();
        showCourseEditor();
    }

    @FXML
    private void cancelCourseForm() {
        showCoursesPage();
        showCourseBrowser();
    }

    @FXML
    private void saveCourse() {
        clearCourseFieldErrors();
        Module selectedModule = courseModuleCombo.getValue();
        if (selectedModule == null) {
            courseModuleErrorLabel.setText("Veuillez choisir un module.");
            return;
        }

        Map<String, String> errors = coursController.validateCoursForm(
                courseCodeField, courseTitleField, courseDescriptionArea, courseLevelField, courseCreditsField,
                courseLanguageField, courseDateDebutPicker, courseDateFinPicker, courseStatusCombo,
                courseImageField, coursePrerequisArea
        );
        applyCourseFieldErrors(errors);
        if (!errors.isEmpty()) {
            courseFormStatusLabel.setText("Corrigez les champs signales avant d'enregistrer.");
            return;
        }

        Cours target = coursController.buildCours(
                editingCourse, selectedModule, courseCodeField, courseTitleField, courseDescriptionArea,
                courseLevelField, courseCreditsField, courseLanguageField, courseDateDebutPicker,
                courseDateFinPicker, courseStatusCombo, courseImageField, coursePrerequisArea
        );

        try {
            if (editingCourse == null) {
                coursService.createForTeacher(target, currentTeacher.getId());
                coursesMessageLabel.setText("Cours ajoute avec succes.");
            } else {
                coursService.updateForTeacher(target, currentTeacher.getId());
                coursesMessageLabel.setText("Cours modifie avec succes.");
            }
            courseFormStatusLabel.setText("");
            refreshTeacherData();
            selectCourseById(target.getId());
            showCoursesPage();
            showCourseBrowser();
        } catch (SQLException e) {
            courseFormStatusLabel.setText("Enregistrement impossible: " + e.getMessage());
        }
    }

    @FXML
    private void editSelectedCourse() {
        if (selectedCourse == null) {
            coursesMessageLabel.setText("Selectionnez un cours a modifier.");
            return;
        }
        editCourse(selectedCourse);
    }

    @FXML
    private void deleteSelectedCourse() {
        if (selectedCourse == null) {
            coursesMessageLabel.setText("Selectionnez un cours a supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer cours");
        confirmation.setHeaderText("Supprimer le cours " + safe(selectedCourse.getTitre()) + " ?");
        confirmation.setContentText("Cette action supprimera le cours selectionne.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            coursService.delete(selectedCourse.getId());
            if (selectedCoursForContenus != null && Objects.equals(selectedCoursForContenus.getId(), selectedCourse.getId())) {
                selectedCoursForContenus = null;
            }
            coursesMessageLabel.setText("Cours supprime avec succes.");
            refreshTeacherData();
            selectedCourse = null;
            updateCourseDetails(null);
        } catch (SQLException e) {
            coursesMessageLabel.setText("Suppression impossible: " + e.getMessage());
        }
    }

    @FXML
    private void showStudentsPageForSelectedCourse() {
        if (selectedCourse == null) {
            coursesMessageLabel.setText("Selectionnez un cours pour voir ses etudiants.");
            return;
        }
        courseFilterOptions.stream()
                .filter(option -> option.matches(selectedCourse))
                .findFirst()
                .ifPresent(studentsCourseFilterCombo::setValue);
        showStudentsPage();
    }

    @FXML
    private void showContenusForSelectedCourse() {
        if (selectedCourse == null) {
            coursesMessageLabel.setText("Selectionnez un cours pour voir ses contenus.");
            return;
        }
        openCoursContenus(selectedCourse);
    }

    @FXML
    private void closeCourseDetails() {
        coursesTable.getSelectionModel().clearSelection();
        selectedCourse = null;
        updateCourseDetails(null);
    }

    @FXML
    private void backToCoursesFromContenus() {
        showCoursesPage();
    }

    @FXML
    private void showContenuCreateForm() {
        if (selectedCoursForContenus == null) {
            contenuMessageLabel.setText("Selectionnez d'abord un cours.");
            return;
        }
        editingContenu = null;
        contenuFormTitleLabel.setText("Nouveau contenu");
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

            if (editingContenu == null) {
                contenuService.create(contenu);
                contenuMessageLabel.setText("Contenu cree avec succes.");
            } else {
                contenuService.update(contenu);
                contenuMessageLabel.setText("Contenu modifie avec succes.");
            }
            loadContenusData();
            showContenuBrowser();
        } catch (SQLException e) {
            contenuFormStatusLabel.setText("Erreur contenu: " + e.getMessage());
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

    @FXML
    private void closeContenuDetails() {
        contenusTable.getSelectionModel().clearSelection();
        updateContenuDetails(null);
    }

    @FXML
    private void closeStudentDetails() {
        studentsTable.getSelectionModel().clearSelection();
        updateStudentDetails(null);
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

    @FXML
    private void handleProfileLogout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    private void refreshTeacherData() {
        loadTeacherCourses();
        rebuildOverview();
        rebuildCourseFilter();
        if (selectedCoursForContenus != null) {
            teacherCourses.stream()
                    .filter(cours -> Objects.equals(cours.getId(), selectedCoursForContenus.getId()))
                    .findFirst()
                    .ifPresentOrElse(cours -> {
                        selectedCoursForContenus = cours;
                        loadContenusData();
                    }, () -> {
                        selectedCoursForContenus = null;
                        contenuRows.clear();
                        updateContenuDetails(null);
                    });
        }
    }

    private void configureHeader() {
        welcomeLabel.setText("Bienvenue " + safe(currentTeacher.getPrenom()));
        roleLabel.setText("Pilotez vos cours, vos inscriptions et les mises a jour de votre catalogue pedagogique.");
        currentUserNameLabel.setText(currentTeacher.getNomComplet());
        currentUserRoleLabel.setText(currentTeacher.getType());
        profileMenuButton.setText(buildInitials(currentTeacher));
        teacherCourseContextLabel.setText("Catalogue enseignant de " + currentTeacher.getNomComplet());
    }

    private void configureOverviewTables() {
        profileFieldColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().key()));
        profileValueColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().value()));
        focusTitleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().key()));
        focusDescriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().value()));
        profileTable.setItems(profileRows);
        focusTable.setItems(focusRows);
    }

    private void configureCoursesTable() {
        courseCodeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getCodeCours())));
        courseTitleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getTitre())));
        courseModuleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(moduleLabel(data.getValue().getModule())));
        courseLevelColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getNiveau())));
        courseStatusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getStatut())));
        courseStudentsColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(studentCountForCourse(data.getValue())));
        courseActionsColumn.setCellFactory(column -> new CourseActionsCell());
        coursesTable.setItems(teacherCourses);
        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedCourse = newValue;
            updateCourseDetails(newValue);
        });
    }

    private void configureContenuTable() {
        contenuIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contenuTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeContenu"));
        contenuTitreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        contenuOrdreColumn.setCellValueFactory(new PropertyValueFactory<>("ordreAffichage"));
        contenuDureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        contenuActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox box = new HBox(8.0, editButton, deleteButton);

            {
                editButton.getStyleClass().addAll("secondary-button", "table-action-button");
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
        contenusTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateContenuDetails(newValue));
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
        updateContenuDetails(null);
        showContenuBrowser();
    }

    private void configureStudentsTable() {
        studentMatriculeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getMatricule())));
        studentNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNomComplet()));
        studentEmailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getEmail())));
        studentLevelColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getNiveauEtude())));
        studentSpecialiteColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getSpecialisation())));
        studentsTable.setItems(teacherStudents);
        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateStudentDetails(newValue));
        studentsCourseFilterCombo.setItems(courseFilterOptions);
        studentsCourseFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyStudentsFilter());
    }

    private void configureCourseForm() {
        coursController.configureFormDefaults(courseStatusCombo, courseDateDebutPicker, courseDateFinPicker);
        courseModuleCombo.setItems(availableModules);
        courseModuleCombo.setCellFactory(listView -> new ModuleCell());
        courseModuleCombo.setButtonCell(new ModuleCell());
    }

    private void loadModules() {
        availableModules.clear();
        try {
            availableModules.addAll(moduleService.getAll());
        } catch (SQLException e) {
            courseFormStatusLabel.setText("Chargement des modules impossible: " + e.getMessage());
        }
    }

    private void loadTeacherCourses() {
        teacherCourses.clear();
        studentCountByCourseId.clear();
        coursesMessageLabel.setText("");
        try {
            teacherCourses.addAll(coursService.getByTeacherId(currentTeacher.getId()));
            for (Cours cours : teacherCourses) {
                studentCountByCourseId.put(cours.getId(), coursService.getStudentsByCoursId(cours.getId()).size());
            }
        } catch (SQLException e) {
            coursesMessageLabel.setText("Chargement des cours impossible: " + e.getMessage());
        }
        if (teacherCourses.isEmpty() && coursesMessageLabel.getText().isBlank()) {
            coursesMessageLabel.setText("Aucun cours rattache a cet enseignant pour le moment.");
        }
    }

    private void loadContenusData() {
        contenuRows.clear();
        updateContenuDetails(null);
        contenuMessageLabel.setText("");
        if (selectedCoursForContenus == null || selectedCoursForContenus.getId() == null) {
            return;
        }
        try {
            contenuRows.setAll(contenuService.getByCoursId(selectedCoursForContenus.getId()));
        } catch (SQLException e) {
            contenuMessageLabel.setText("Chargement des contenus impossible: " + e.getMessage());
        }
        if (contenuRows.isEmpty() && contenuMessageLabel.getText().isBlank()) {
            contenuMessageLabel.setText("Aucun contenu rattache a ce cours pour le moment.");
        }
    }

    private void rebuildOverview() {
        int publishedCount = (int) teacherCourses.stream()
                .filter(cours -> "publie".equalsIgnoreCase(cours.getStatut()))
                .count();
        int totalStudents = teacherCourses.stream()
                .flatMap(cours -> studentsOfCourse(cours).stream())
                .collect(Collectors.toMap(Etudiant::getId, etudiant -> etudiant, (left, right) -> left))
                .size();

        coursesCountLabel.setText(String.valueOf(teacherCourses.size()));
        studentsCountLabel.setText(String.valueOf(totalStudents));
        publishedCoursesLabel.setText(String.valueOf(publishedCount));

        profileRows.setAll(
                new KeyValueRow("Nom complet", currentTeacher.getNomComplet()),
                new KeyValueRow("Email", safe(currentTeacher.getEmail())),
                new KeyValueRow("Matricule", safe(currentTeacher.getMatriculeEnseignant())),
                new KeyValueRow("Diplome", safe(currentTeacher.getDiplome())),
                new KeyValueRow("Specialite", safe(currentTeacher.getSpecialite())),
                new KeyValueRow("Contrat", safe(currentTeacher.getTypeContrat())),
                new KeyValueRow("Experience", formatExperience(currentTeacher.getAnneesExperience())),
                new KeyValueRow("Taux horaire", formatRate(currentTeacher.getTauxHoraire())),
                new KeyValueRow("Disponibilites", safe(currentTeacher.getDisponibilites())),
                new KeyValueRow("Statut", safe(currentTeacher.getStatut()))
        );

        focusRows.setAll(
                new KeyValueRow("Priorite", teacherCourses.isEmpty()
                        ? "Aucun cours n'est encore rattache a votre profil."
                        : "Mettez a jour la fiche de vos cours et suivez les inscriptions."),
                new KeyValueRow("Cours publies", publishedCount + " cours sont actuellement publies."),
                new KeyValueRow("Charge etudiante", totalStudents + " etudiants distincts sont inscrits a vos cours."),
                new KeyValueRow("Catalogue", "Vous pouvez ajouter, modifier ou supprimer vos cours et leurs contenus depuis l'onglet Mes cours.")
        );
    }

    private void rebuildCourseFilter() {
        CourseFilterOption previous = studentsCourseFilterCombo.getValue();
        courseFilterOptions.setAll(new CourseFilterOption("Tous les cours", null));
        teacherCourses.forEach(cours -> courseFilterOptions.add(new CourseFilterOption(cours.getTitre(), cours)));

        if (previous != null) {
            CourseFilterOption restored = courseFilterOptions.stream()
                    .filter(option -> option.matches(previous.getCours()))
                    .findFirst()
                    .orElse(courseFilterOptions.get(0));
            studentsCourseFilterCombo.setValue(restored);
        } else if (!courseFilterOptions.isEmpty()) {
            studentsCourseFilterCombo.setValue(courseFilterOptions.get(0));
        }
    }

    private void applyStudentsFilter() {
        teacherStudents.clear();
        updateStudentDetails(null);
        CourseFilterOption filter = studentsCourseFilterCombo.getValue();
        if (filter == null || filter.getCours() == null) {
            Map<Long, Etudiant> uniqueStudents = new LinkedHashMap<>();
            teacherCourses.forEach(cours -> studentsOfCourse(cours).forEach(etudiant -> uniqueStudents.put(etudiant.getId(), etudiant)));
            teacherStudents.setAll(uniqueStudents.values());
        } else {
            teacherStudents.setAll(studentsOfCourse(filter.getCours()));
        }
        updateStudentsHero();
    }

    private void updateStudentsHero() {
        CourseFilterOption filter = studentsCourseFilterCombo.getValue();
        if (filter != null && filter.getCours() != null) {
            studentsHeroTitleLabel.setText("Etudiants inscrits au cours " + safe(filter.getCours().getTitre()));
            studentsHeroSubtitleLabel.setText("Vue filtree sur un seul cours de votre catalogue enseignant.");
        } else {
            studentsHeroTitleLabel.setText("Etudiants inscrits a vos cours");
            studentsHeroSubtitleLabel.setText("Consultez la liste consolidee des etudiants lies a l'ensemble de vos cours.");
        }
    }

    private void editCourse(Cours cours) {
        editingCourse = cours;
        courseFormTitleLabel.setText("Modifier le cours");
        courseFormStatusLabel.setText("");
        clearCourseFieldErrors();
        courseModuleCombo.setValue(findModule(cours.getModule()));
        coursController.populateForm(
                cours, courseCodeField, courseTitleField, courseDescriptionArea, courseLevelField,
                courseCreditsField, courseLanguageField, courseDateDebutPicker, courseDateFinPicker,
                courseStatusCombo, courseImageField, coursePrerequisArea
        );
        showCoursesPage();
        showCourseEditor();
    }

    private void editContenu(Contenu contenu) {
        editingContenu = contenu;
        contenuFormTitleLabel.setText("Modifier contenu");
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
        confirmation.setTitle("Supprimer contenu");
        confirmation.setHeaderText("Supprimer le contenu " + safe(contenu.getTitre()) + " ?");
        confirmation.setContentText("Cette action supprimera le contenu selectionne.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            contenuService.delete(contenu.getId());
            contenuMessageLabel.setText("Contenu supprime avec succes.");
            loadContenusData();
        } catch (SQLException e) {
            contenuMessageLabel.setText("Suppression impossible: " + e.getMessage());
        }
    }

    private void updateCourseDetails(Cours cours) {
        boolean visible = cours != null;
        courseDetailsPanel.setVisible(visible);
        courseDetailsPanel.setManaged(visible);
        if (!visible) {
            courseDetailHeroTitleLabel.setText("Aucun cours selectionne");
            courseDetailHeroMetaLabel.setText("La fiche detaillee du cours apparaitra ici apres selection dans la liste.");
            courseDetailCodeLabel.setText("-");
            courseDetailTitleLabel.setText("-");
            courseDetailModuleLabel.setText("-");
            courseDetailDescriptionLabel.setText("-");
            courseDetailLevelLabel.setText("-");
            courseDetailCreditsLabel.setText("-");
            courseDetailLanguageLabel.setText("-");
            courseDetailDatesLabel.setText("-");
            courseDetailStatusLabel.setText("-");
            courseDetailStudentsLabel.setText("-");
            courseDetailImageLabel.setText("-");
            courseDetailPrerequisLabel.setText("-");
            return;
        }

        courseDetailHeroTitleLabel.setText(safe(cours.getTitre()));
        courseDetailHeroMetaLabel.setText(safe(cours.getCodeCours()) + " | " + safe(cours.getStatut()));
        courseDetailCodeLabel.setText(safe(cours.getCodeCours()));
        courseDetailTitleLabel.setText(safe(cours.getTitre()));
        courseDetailModuleLabel.setText(moduleLabel(cours.getModule()));
        courseDetailDescriptionLabel.setText(safe(cours.getDescription()));
        courseDetailLevelLabel.setText(safe(cours.getNiveau()));
        courseDetailCreditsLabel.setText(cours.getCredits() == null ? "-" : String.valueOf(cours.getCredits()));
        courseDetailLanguageLabel.setText(safe(cours.getLangue()));
        courseDetailDatesLabel.setText(formatDateRange(cours.getDateDebut(), cours.getDateFin()));
        courseDetailStatusLabel.setText(safe(cours.getStatut()));
        courseDetailStudentsLabel.setText(String.valueOf(studentCountForCourse(cours)));
        courseDetailImageLabel.setText(safe(cours.getImageCoursUrl()));
        courseDetailPrerequisLabel.setText(cours.getPrerequis() == null || cours.getPrerequis().isEmpty() ? "-" : String.join(", ", cours.getPrerequis()));
    }

    private void updateContenuDetails(Contenu contenu) {
        boolean visible = contenu != null;
        contenuDetailsPanel.setVisible(visible);
        contenuDetailsPanel.setManaged(visible);
        if (!visible) {
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

        contenuHeroTitleLabel.setText(safe(contenu.getTitre()));
        contenuHeroMetaLabel.setText(safe(contenu.getTypeContenu()) + " | " + safe(contenu.getFormat()));
        contenuDetailTypeLabel.setText(safe(contenu.getTypeContenu()));
        contenuDetailTitreLabel.setText(safe(contenu.getTitre()));
        contenuDetailDescriptionLabel.setText(safe(contenu.getDescription()));
        contenuDetailUrlLabel.setText(safe(contenu.getUrlContenu()));
        contenuDetailDureeLabel.setText(contenu.getDuree() == null ? "-" : contenu.getDuree() + " min");
        contenuDetailOrdreLabel.setText(String.valueOf(contenu.getOrdreAffichage()));
        contenuDetailPublicLabel.setText(contenu.isEstPublic() ? "Oui" : "Non");
        contenuDetailDateAjoutLabel.setText(formatDateTime(contenu.getDateAjout()));
        contenuDetailVuesLabel.setText(String.valueOf(contenu.getNombreVues()));
        contenuDetailFormatLabel.setText(safe(contenu.getFormat()));
        contenuDetailRessourcesLabel.setText(contenu.getRessources() == null || contenu.getRessources().isEmpty()
                ? "-"
                : String.join(", ", contenu.getRessources()));
    }

    private void updateStudentDetails(Etudiant etudiant) {
        boolean visible = etudiant != null;
        studentDetailsPanel.setVisible(visible);
        studentDetailsPanel.setManaged(visible);
        if (!visible) {
            studentDetailHeroTitleLabel.setText("Aucun etudiant selectionne");
            studentDetailHeroMetaLabel.setText("La fiche detaillee de l'etudiant apparaitra ici apres selection dans la liste.");
            studentDetailMatriculeLabel.setText("-");
            studentDetailEmailLabel.setText("-");
            studentDetailLevelLabel.setText("-");
            studentDetailSpecialiteLabel.setText("-");
            studentDetailPhoneLabel.setText("-");
            studentDetailAddressLabel.setText("-");
            return;
        }

        studentDetailHeroTitleLabel.setText(etudiant.getNomComplet());
        studentDetailHeroMetaLabel.setText(safe(etudiant.getSpecialisation()) + " | " + safe(etudiant.getNiveauEtude()));
        studentDetailMatriculeLabel.setText(safe(etudiant.getMatricule()));
        studentDetailEmailLabel.setText(safe(etudiant.getEmail()));
        studentDetailLevelLabel.setText(safe(etudiant.getNiveauEtude()));
        studentDetailSpecialiteLabel.setText(safe(etudiant.getSpecialisation()));
        studentDetailPhoneLabel.setText(safe(etudiant.getTelephone()));
        studentDetailAddressLabel.setText(safe(etudiant.getAdresse()));
    }

    private void showCourseBrowser() {
        coursesBrowseSection.setVisible(true);
        coursesBrowseSection.setManaged(true);
        courseEditorSection.setVisible(false);
        courseEditorSection.setManaged(false);
    }

    private void showCourseEditor() {
        coursesBrowseSection.setVisible(false);
        coursesBrowseSection.setManaged(false);
        courseEditorSection.setVisible(true);
        courseEditorSection.setManaged(true);
    }

    private void showContenuBrowser() {
        contenuBrowseSection.setVisible(true);
        contenuBrowseSection.setManaged(true);
        contenuEditorSection.setVisible(false);
        contenuEditorSection.setManaged(false);
    }

    private void showContenuEditor() {
        contenuBrowseSection.setVisible(false);
        contenuBrowseSection.setManaged(false);
        contenuEditorSection.setVisible(true);
        contenuEditorSection.setManaged(true);
    }

    private void setPageVisibility(boolean overviewVisible, boolean coursesVisible, boolean contenusVisible, boolean studentsVisible) {
        overviewSection.setVisible(overviewVisible);
        overviewSection.setManaged(overviewVisible);
        coursesSection.setVisible(coursesVisible);
        coursesSection.setManaged(coursesVisible);
        contenusSection.setVisible(contenusVisible);
        contenusSection.setManaged(contenusVisible);
        studentsSection.setVisible(studentsVisible);
        studentsSection.setManaged(studentsVisible);
    }

    private void clearCourseFieldErrors() {
        courseModuleErrorLabel.setText("");
        courseCodeErrorLabel.setText("");
        courseTitleErrorLabel.setText("");
        courseDescriptionErrorLabel.setText("");
        courseLevelErrorLabel.setText("");
        courseCreditsErrorLabel.setText("");
        courseLanguageErrorLabel.setText("");
        courseStatusErrorLabel.setText("");
        courseDateDebutErrorLabel.setText("");
        courseDateFinErrorLabel.setText("");
        courseImageErrorLabel.setText("");
        coursePrerequisErrorLabel.setText("");
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

    private void applyCourseFieldErrors(Map<String, String> errors) {
        courseCodeErrorLabel.setText(errors.getOrDefault("code", ""));
        courseTitleErrorLabel.setText(errors.getOrDefault("titre", ""));
        courseDescriptionErrorLabel.setText(errors.getOrDefault("description", ""));
        courseLevelErrorLabel.setText(errors.getOrDefault("niveau", ""));
        courseCreditsErrorLabel.setText(errors.getOrDefault("credits", ""));
        courseLanguageErrorLabel.setText(errors.getOrDefault("langue", ""));
        courseDateDebutErrorLabel.setText(errors.getOrDefault("date_debut", ""));
        courseDateFinErrorLabel.setText(errors.getOrDefault("date_fin", ""));
        courseStatusErrorLabel.setText(errors.getOrDefault("statut", ""));
        courseImageErrorLabel.setText(errors.getOrDefault("image", ""));
        coursePrerequisErrorLabel.setText(errors.getOrDefault("prerequis", ""));
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

    private void chooseFileForField(TextField targetField, String title, FileChooser.ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(extensionFilter);
        if (contenuEditorSection.getScene() == null) {
            return;
        }
        File selectedFile = fileChooser.showOpenDialog(contenuEditorSection.getScene().getWindow());
        if (selectedFile != null) {
            targetField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void selectCourseById(Integer courseId) {
        if (courseId == null) {
            return;
        }
        teacherCourses.stream()
                .filter(cours -> Objects.equals(cours.getId(), courseId))
                .findFirst()
                .ifPresent(cours -> {
                    coursesTable.getSelectionModel().select(cours);
                    selectedCourse = cours;
                    updateCourseDetails(cours);
                });
    }

    private void openCoursContenus(Cours cours) {
        coursesTable.getSelectionModel().select(cours);
        selectedCourse = cours;
        selectedCoursForContenus = cours;
        contenuMessageLabel.setText("");
        showContenusPage();
    }

    private List<Etudiant> studentsOfCourse(Cours cours) {
        if (cours == null || cours.getId() == null) {
            return List.of();
        }
        try {
            return coursService.getStudentsByCoursId(cours.getId());
        } catch (SQLException e) {
            return List.of();
        }
    }

    private int studentCountForCourse(Cours cours) {
        if (cours == null || cours.getId() == null) {
            return 0;
        }
        return studentCountByCourseId.getOrDefault(cours.getId(), 0);
    }

    private Module findModule(Module module) {
        if (module == null || module.getId() == null) {
            return null;
        }
        return availableModules.stream()
                .filter(item -> Objects.equals(item.getId(), module.getId()))
                .findFirst()
                .orElse(module);
    }

    private Enseignant resolveTeacher() {
        Utilisateur utilisateur = UserSession.getCurrentUser();
        return utilisateur instanceof Enseignant enseignant ? enseignant : buildDemoTeacher();
    }

    private String buildInitials(Utilisateur utilisateur) {
        String prenom = utilisateur.getPrenom() != null && !utilisateur.getPrenom().isBlank()
                ? utilisateur.getPrenom().substring(0, 1).toUpperCase() : "";
        String nom = utilisateur.getNom() != null && !utilisateur.getNom().isBlank()
                ? utilisateur.getNom().substring(0, 1).toUpperCase() : "";
        return prenom + nom;
    }

    private String moduleLabel(Module module) {
        return module == null ? "-" : safe(module.getTitreModule());
    }

    private String formatDateRange(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return "-";
        }
        if (start == null) {
            return "Jusqu'au " + end.format(DATE_FORMATTER);
        }
        if (end == null) {
            return "A partir du " + start.format(DATE_FORMATTER);
        }
        return start.format(DATE_FORMATTER) + " -> " + end.format(DATE_FORMATTER);
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FORMATTER);
    }

    private String formatExperience(Integer years) {
        return years == null ? "-" : years + " ans";
    }

    private String formatRate(BigDecimal rate) {
        return rate == null ? "-" : rate + " TND";
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private Enseignant buildDemoTeacher() {
        Enseignant enseignant = new Enseignant();
        enseignant.setId(2001L);
        enseignant.setNom("Ben Salem");
        enseignant.setPrenom("Yasmine");
        enseignant.setEmail("yasmine.bensalem@demo.tn");
        enseignant.setMatriculeEnseignant("ENS-JAVA-24");
        enseignant.setDiplome("Doctorat");
        enseignant.setSpecialite("Genie Logiciel");
        enseignant.setTypeContrat("CDI");
        enseignant.setAnneesExperience(8);
        enseignant.setTauxHoraire(new BigDecimal("95.00"));
        enseignant.setDisponibilites("Lundi au jeudi 08:00-16:00");
        enseignant.setStatut("actif");
        return enseignant;
    }

    private record KeyValueRow(String key, String value) {
    }

    private static final class CourseFilterOption {
        private final String label;
        private final Cours cours;

        private CourseFilterOption(String label, Cours cours) {
            this.label = label;
            this.cours = cours;
        }

        public Cours getCours() {
            return cours;
        }

        private boolean matches(Cours otherCourse) {
            if (cours == null && otherCourse == null) {
                return true;
            }
            return cours != null && otherCourse != null && Objects.equals(cours.getId(), otherCourse.getId());
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final class CourseActionsCell extends TableCell<Cours, Void> {
        private final Button viewButton = new Button("Voir");
        private final Button editButton = new Button("Modifier");
        private final Button deleteButton = new Button("Supprimer");
        private final HBox actions = new HBox(8.0, viewButton, editButton, deleteButton);

        private CourseActionsCell() {
            viewButton.getStyleClass().addAll("secondary-button", "table-action-button");
            editButton.getStyleClass().addAll("secondary-button", "table-action-button");
            deleteButton.getStyleClass().addAll("danger-button", "table-action-button");
            viewButton.setOnAction(event -> openCoursContenus(getTableView().getItems().get(getIndex())));
            editButton.setOnAction(event -> editCourse(getTableView().getItems().get(getIndex())));
            deleteButton.setOnAction(event -> {
                Cours cours = getTableView().getItems().get(getIndex());
                coursesTable.getSelectionModel().select(cours);
                selectedCourse = cours;
                deleteSelectedCourse();
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : actions);
        }
    }

    private static final class ModuleCell extends ListCell<Module> {
        @Override
        protected void updateItem(Module item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getTitreModule());
        }
    }
}
