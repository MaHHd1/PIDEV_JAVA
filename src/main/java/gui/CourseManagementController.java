package gui;

import entities.Contenu;
import entities.Cours;
import entities.Enseignant;
import entities.Module;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ContenuService;
import services.CoursService;
import services.ModuleService;
import utils.UserSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CourseManagementController implements MainControllerAware {

    @FXML
    private TableView<Module> moduleTable;
    @FXML
    private TableColumn<Module, String> moduleTitleColumn;
    @FXML
    private TableColumn<Module, String> moduleStatusColumn;
    @FXML
    private TableColumn<Module, Integer> moduleOrderColumn;
    @FXML
    private TextField moduleTitleField;
    @FXML
    private TextArea moduleDescriptionArea;
    @FXML
    private TextField moduleOrderField;
    @FXML
    private TextArea moduleObjectivesArea;
    @FXML
    private TextField moduleDurationField;
    @FXML
    private DatePicker modulePublicationDatePicker;
    @FXML
    private ComboBox<String> moduleStatusCombo;
    @FXML
    private TextArea moduleResourcesArea;
    @FXML
    private Label moduleStatusLabel;

    @FXML
    private TableView<Cours> courseTable;
    @FXML
    private TableColumn<Cours, String> courseCodeColumn;
    @FXML
    private TableColumn<Cours, String> courseTitleColumn;
    @FXML
    private TableColumn<Cours, String> courseModuleColumn;
    @FXML
    private TableColumn<Cours, String> courseStatusColumn;
    @FXML
    private TextField courseCodeField;
    @FXML
    private TextField courseTitleField;
    @FXML
    private TextArea courseDescriptionArea;
    @FXML
    private ComboBox<Module> courseModuleCombo;
    @FXML
    private TextField courseLevelField;
    @FXML
    private TextField courseCreditsField;
    @FXML
    private TextField courseLanguageField;
    @FXML
    private DatePicker courseStartDatePicker;
    @FXML
    private DatePicker courseEndDatePicker;
    @FXML
    private ComboBox<String> courseStatusCombo;
    @FXML
    private TextField courseImageUrlField;
    @FXML
    private TextArea coursePrerequisitesArea;
    @FXML
    private Label courseStatusLabel;

    @FXML
    private TableView<Contenu> contentTable;
    @FXML
    private TableColumn<Contenu, String> contentTitleColumn;
    @FXML
    private TableColumn<Contenu, String> contentCourseColumn;
    @FXML
    private TableColumn<Contenu, String> contentTypeColumn;
    @FXML
    private TableColumn<Contenu, Integer> contentOrderColumn;
    @FXML
    private ComboBox<Cours> contentCourseCombo;
    @FXML
    private CheckBox contentVideoCheck;
    @FXML
    private CheckBox contentPdfCheck;
    @FXML
    private CheckBox contentPptCheck;
    @FXML
    private CheckBox contentTextCheck;
    @FXML
    private CheckBox contentQuizCheck;
    @FXML
    private CheckBox contentLinkCheck;
    @FXML
    private TextField contentTitleField;
    @FXML
    private TextField contentUrlField;
    @FXML
    private TextArea contentDescriptionArea;
    @FXML
    private TextField contentDurationField;
    @FXML
    private TextField contentOrderField;
    @FXML
    private ComboBox<String> contentPublicCombo;
    @FXML
    private DatePicker contentAddedDatePicker;
    @FXML
    private TextField contentViewsField;
    @FXML
    private TextField contentFormatField;
    @FXML
    private TextArea contentResourcesArea;
    @FXML
    private TextField contentPdfField;
    @FXML
    private TextField contentPptField;
    @FXML
    private TextField contentVideoField;
    @FXML
    private TextField contentLinkField;
    @FXML
    private TextField contentQuizField;
    @FXML
    private Label contentStatusLabel;

    private final ModuleService moduleService = new ModuleService();
    private final CoursService coursService = new CoursService();
    private final ContenuService contenuService = new ContenuService();

    private final ModuleController moduleFormHelper = new ModuleController();
    private final CoursController coursFormHelper = new CoursController();
    private final ContenuController contenuFormHelper = new ContenuController();

    private final ObservableList<Module> modules = FXCollections.observableArrayList();
    private final ObservableList<Cours> courses = FXCollections.observableArrayList();
    private final ObservableList<Contenu> contents = FXCollections.observableArrayList();

    private Module editingModule;
    private Cours editingCours;
    private Contenu editingContenu;
    private Long currentTeacherId;

    private MainLayoutEnseignantController mainController;

    @FXML
    public void initialize() {
        Utilisateur currentUser = UserSession.getCurrentUser();
        if (currentUser instanceof Enseignant enseignant && enseignant.getId() != null) {
            currentTeacherId = enseignant.getId();
        }

        setupModuleTable();
        setupCourseTable();
        setupContentTable();
        setupCombos();
        resetModuleForm();
        resetCourseForm();
        resetContentForm();
        loadAllData();
    }

    @Override
    public void setMainController(MainLayoutEnseignantController controller) {
        this.mainController = controller;
    }

    private void setupModuleTable() {
        moduleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("titreModule"));
        moduleStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        moduleOrderColumn.setCellValueFactory(new PropertyValueFactory<>("ordreAffichage"));
        moduleTable.setItems(modules);
        moduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            editingModule = newValue;
            if (newValue != null) {
                moduleFormHelper.populateForm(
                        newValue,
                        moduleTitleField,
                        moduleDescriptionArea,
                        moduleOrderField,
                        moduleObjectivesArea,
                        moduleDurationField,
                        modulePublicationDatePicker,
                        moduleStatusCombo,
                        moduleResourcesArea
                );
                moduleStatusLabel.setText("Edition du module #" + newValue.getId());
            }
        });
    }

    private void setupCourseTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("codeCours"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        courseModuleColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getModule() != null ? cell.getValue().getModule().getTitreModule() : "-"
        ));
        courseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        courseTable.setItems(courses);
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            editingCours = newValue;
            if (newValue != null) {
                coursFormHelper.populateForm(
                        newValue,
                        courseCodeField,
                        courseTitleField,
                        courseDescriptionArea,
                        courseLevelField,
                        courseCreditsField,
                        courseLanguageField,
                        courseStartDatePicker,
                        courseEndDatePicker,
                        courseStatusCombo,
                        courseImageUrlField,
                        coursePrerequisitesArea
                );
                selectModuleForCourse(newValue);
                courseStatusLabel.setText("Edition du cours #" + newValue.getId());
            }
        });
    }

    private void setupContentTable() {
        contentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        contentCourseColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getCours() != null ? cell.getValue().getCours().getTitre() : "-"
        ));
        contentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeContenu"));
        contentOrderColumn.setCellValueFactory(new PropertyValueFactory<>("ordreAffichage"));
        contentTable.setItems(contents);
        contentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            editingContenu = newValue;
            if (newValue != null) {
                contenuFormHelper.populateForm(
                        newValue,
                        contentVideoCheck,
                        contentPdfCheck,
                        contentPptCheck,
                        contentTextCheck,
                        contentQuizCheck,
                        contentLinkCheck,
                        contentTitleField,
                        contentUrlField,
                        contentDescriptionArea,
                        contentDurationField,
                        contentOrderField,
                        contentPublicCombo,
                        contentAddedDatePicker,
                        contentViewsField,
                        contentFormatField,
                        contentResourcesArea,
                        contentPdfField,
                        contentPptField,
                        contentVideoField,
                        contentLinkField,
                        contentQuizField
                );
                selectCourseForContent(newValue);
                contentStatusLabel.setText("Edition du contenu #" + newValue.getId());
            }
        });
    }

    private void setupCombos() {
        moduleFormHelper.configureFormDefaults(moduleStatusCombo, modulePublicationDatePicker);
        coursFormHelper.configureFormDefaults(courseStatusCombo, courseStartDatePicker, courseEndDatePicker);
        contenuFormHelper.configureFormDefaults(
                contentVideoCheck,
                contentPdfCheck,
                contentPptCheck,
                contentTextCheck,
                contentQuizCheck,
                contentLinkCheck,
                contentPublicCombo,
                contentAddedDatePicker
        );

        courseModuleCombo.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Module item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitreModule());
            }
        });
        courseModuleCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Module item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Choisir un module" : item.getTitreModule());
            }
        });

        contentCourseCombo.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Cours item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitre());
            }
        });
        contentCourseCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Cours item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Choisir un cours" : item.getTitre());
            }
        });
    }

    private void loadAllData() {
        loadModules();
        loadCourses();
        loadContents();
    }

    private void loadModules() {
        try {
            modules.setAll(moduleService.getAll());
            courseModuleCombo.setItems(FXCollections.observableArrayList(modules));
        } catch (SQLException e) {
            showError("Chargement modules impossible: " + e.getMessage());
        }
    }

    private void loadCourses() {
        try {
            List<Cours> loadedCourses;
            if (currentTeacherId != null) {
                loadedCourses = coursService.getByTeacherId(currentTeacherId);
                if (loadedCourses.isEmpty()) {
                    loadedCourses = coursService.getAll();
                }
            } else {
                loadedCourses = coursService.getAll();
            }
            courses.setAll(loadedCourses);
            contentCourseCombo.setItems(FXCollections.observableArrayList(courses));
        } catch (SQLException e) {
            showError("Chargement cours impossible: " + e.getMessage());
        }
    }

    private void loadContents() {
        try {
            List<Contenu> loadedContents = new ArrayList<>();
            for (Cours cours : courses) {
                if (cours.getId() != null) {
                    loadedContents.addAll(contenuService.getByCoursId(cours.getId()));
                }
            }
            contents.setAll(loadedContents);
        } catch (SQLException e) {
            showError("Chargement contenus impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewModule() {
        resetModuleForm();
    }

    @FXML
    private void handleSaveModule() {
        Map<String, String> errors = moduleFormHelper.validateModuleForm(
                moduleTitleField,
                moduleDescriptionArea,
                moduleOrderField,
                moduleObjectivesArea,
                moduleDurationField,
                moduleStatusCombo
        );
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors.values()));
            return;
        }

        try {
            Module module = moduleFormHelper.buildModule(
                    editingModule,
                    moduleTitleField,
                    moduleDescriptionArea,
                    moduleOrderField,
                    moduleObjectivesArea,
                    moduleDurationField,
                    modulePublicationDatePicker,
                    moduleStatusCombo,
                    moduleResourcesArea
            );
            if (editingModule == null) {
                moduleService.create(module);
                moduleStatusLabel.setText("Module cree avec succes.");
            } else {
                moduleService.update(module);
                moduleStatusLabel.setText("Module mis a jour.");
            }
            loadModules();
            resetModuleForm();
        } catch (SQLException e) {
            showError("Enregistrement module impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteModule() {
        if (editingModule == null || editingModule.getId() == null) {
            showError("Selectionnez un module a supprimer.");
            return;
        }
        try {
            moduleService.delete(editingModule.getId());
            moduleStatusLabel.setText("Module supprime.");
            loadAllData();
            resetModuleForm();
        } catch (SQLException e) {
            showError("Suppression module impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewCourse() {
        resetCourseForm();
    }

    @FXML
    private void handleSaveCourse() {
        Map<String, String> errors = coursFormHelper.validateCoursForm(
                courseCodeField,
                courseTitleField,
                courseDescriptionArea,
                courseLevelField,
                courseCreditsField,
                courseLanguageField,
                courseStartDatePicker,
                courseEndDatePicker,
                courseStatusCombo,
                courseImageUrlField,
                coursePrerequisitesArea
        );
        if (courseModuleCombo.getValue() == null) {
            errors.put("module", "Choisissez un module pour le cours.");
        }
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors.values()));
            return;
        }

        try {
            Cours cours = coursFormHelper.buildCours(
                    editingCours,
                    courseModuleCombo.getValue(),
                    courseCodeField,
                    courseTitleField,
                    courseDescriptionArea,
                    courseLevelField,
                    courseCreditsField,
                    courseLanguageField,
                    courseStartDatePicker,
                    courseEndDatePicker,
                    courseStatusCombo,
                    courseImageUrlField,
                    coursePrerequisitesArea
            );
            if (editingCours == null) {
                if (currentTeacherId != null) {
                    coursService.createForTeacher(cours, currentTeacherId);
                } else {
                    coursService.create(cours);
                }
                courseStatusLabel.setText("Cours cree avec succes.");
            } else {
                if (currentTeacherId != null) {
                    coursService.updateForTeacher(cours, currentTeacherId);
                } else {
                    coursService.update(cours);
                }
                courseStatusLabel.setText("Cours mis a jour.");
            }
            loadCourses();
            loadContents();
            resetCourseForm();
        } catch (SQLException e) {
            showError("Enregistrement cours impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCourse() {
        if (editingCours == null || editingCours.getId() == null) {
            showError("Selectionnez un cours a supprimer.");
            return;
        }
        try {
            coursService.delete(editingCours.getId());
            courseStatusLabel.setText("Cours supprime.");
            loadCourses();
            loadContents();
            resetCourseForm();
        } catch (SQLException e) {
            showError("Suppression cours impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewContent() {
        resetContentForm();
    }

    @FXML
    private void handleSaveContent() {
        List<String> selectedTypes = selectedContentTypes();
        Map<String, String> errors = contenuFormHelper.validateContenuForm(
                selectedTypes,
                contentTitleField,
                contentDescriptionArea,
                contentDurationField,
                contentOrderField,
                contentPublicCombo,
                contentViewsField,
                contentFormatField,
                contentResourcesArea,
                contentUrlField,
                contentPdfField,
                contentPptField,
                contentVideoField,
                contentLinkField,
                contentQuizField
        );
        if (contentCourseCombo.getValue() == null) {
            errors.put("cours", "Choisissez un cours pour le contenu.");
        }
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors.values()));
            return;
        }

        try {
            Contenu contenu = contenuFormHelper.buildContenu(
                    editingContenu,
                    contentCourseCombo.getValue(),
                    selectedTypes,
                    contentTitleField,
                    contentUrlField,
                    contentDescriptionArea,
                    contentDurationField,
                    contentOrderField,
                    contentPublicCombo,
                    contentAddedDatePicker,
                    contentViewsField,
                    contentFormatField,
                    contentResourcesArea,
                    contentPdfField,
                    contentPptField,
                    contentVideoField,
                    contentLinkField,
                    contentQuizField
            );
            if (editingContenu == null) {
                contenuService.create(contenu);
                contentStatusLabel.setText("Contenu cree avec succes.");
            } else {
                contenuService.update(contenu);
                contentStatusLabel.setText("Contenu mis a jour.");
            }
            loadContents();
            resetContentForm();
        } catch (SQLException e) {
            showError("Enregistrement contenu impossible: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteContent() {
        if (editingContenu == null || editingContenu.getId() == null) {
            showError("Selectionnez un contenu a supprimer.");
            return;
        }
        try {
            contenuService.delete(editingContenu.getId());
            contentStatusLabel.setText("Contenu supprime.");
            loadContents();
            resetContentForm();
        } catch (SQLException e) {
            showError("Suppression contenu impossible: " + e.getMessage());
        }
    }

    private List<String> selectedContentTypes() {
        List<String> types = new ArrayList<>();
        if (contentVideoCheck.isSelected()) {
            types.add("video");
        }
        if (contentPdfCheck.isSelected()) {
            types.add("pdf");
        }
        if (contentPptCheck.isSelected()) {
            types.add("ppt");
        }
        if (contentTextCheck.isSelected()) {
            types.add("texte");
        }
        if (contentQuizCheck.isSelected()) {
            types.add("quiz");
        }
        if (contentLinkCheck.isSelected()) {
            types.add("lien");
        }
        return types;
    }

    private void selectModuleForCourse(Cours cours) {
        if (cours.getModule() == null || cours.getModule().getId() == null) {
            courseModuleCombo.setValue(null);
            return;
        }
        courseModuleCombo.getItems().stream()
                .filter(module -> Objects.equals(module.getId(), cours.getModule().getId()))
                .findFirst()
                .ifPresent(courseModuleCombo::setValue);
    }

    private void selectCourseForContent(Contenu contenu) {
        if (contenu.getCours() == null || contenu.getCours().getId() == null) {
            contentCourseCombo.setValue(null);
            return;
        }
        contentCourseCombo.getItems().stream()
                .filter(cours -> Objects.equals(cours.getId(), contenu.getCours().getId()))
                .findFirst()
                .ifPresent(contentCourseCombo::setValue);
    }

    private void resetModuleForm() {
        editingModule = null;
        moduleTable.getSelectionModel().clearSelection();
        moduleFormHelper.clearForm(
                moduleTitleField,
                moduleDescriptionArea,
                moduleOrderField,
                moduleObjectivesArea,
                moduleDurationField,
                modulePublicationDatePicker,
                moduleStatusCombo,
                moduleResourcesArea
        );
        moduleStatusLabel.setText("Nouveau module.");
    }

    private void resetCourseForm() {
        editingCours = null;
        courseTable.getSelectionModel().clearSelection();
        coursFormHelper.clearForm(
                courseCodeField,
                courseTitleField,
                courseDescriptionArea,
                courseLevelField,
                courseCreditsField,
                courseLanguageField,
                courseStartDatePicker,
                courseEndDatePicker,
                courseStatusCombo,
                courseImageUrlField,
                coursePrerequisitesArea
        );
        courseModuleCombo.setValue(null);
        courseStatusLabel.setText("Nouveau cours.");
    }

    private void resetContentForm() {
        editingContenu = null;
        contentTable.getSelectionModel().clearSelection();
        contenuFormHelper.clearForm(
                contentVideoCheck,
                contentPdfCheck,
                contentPptCheck,
                contentTextCheck,
                contentQuizCheck,
                contentLinkCheck,
                contentTitleField,
                contentUrlField,
                contentDescriptionArea,
                contentDurationField,
                contentOrderField,
                contentPublicCombo,
                contentAddedDatePicker,
                contentViewsField,
                contentFormatField,
                contentResourcesArea,
                contentPdfField,
                contentPptField,
                contentVideoField,
                contentLinkField,
                contentQuizField
        );
        contentCourseCombo.setValue(null);
        contentStatusLabel.setText("Nouveau contenu.");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
