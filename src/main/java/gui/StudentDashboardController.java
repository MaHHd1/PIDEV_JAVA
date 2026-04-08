package gui;

import entities.Contenu;
import entities.Cours;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import services.ContenuService;
import services.CoursService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class StudentDashboardController {

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
    @FXML private VBox myCoursesSection;
    @FXML private VBox allCoursesSection;
    @FXML private VBox contenusSection;

    @FXML private Label enrolledCoursesCountLabel;
    @FXML private Label availableCoursesCountLabel;
    @FXML private Label completedContentsCountLabel;

    @FXML private TableView<KeyValueRow> profileTable;
    @FXML private TableColumn<KeyValueRow, String> profileFieldColumn;
    @FXML private TableColumn<KeyValueRow, String> profileValueColumn;
    @FXML private TableView<KeyValueRow> focusTable;
    @FXML private TableColumn<KeyValueRow, String> focusTitleColumn;
    @FXML private TableColumn<KeyValueRow, String> focusDescriptionColumn;

    @FXML private Label myCoursesContextLabel;
    @FXML private Label myCoursesMessageLabel;
    @FXML private TableView<Cours> myCoursesTable;
    @FXML private TableColumn<Cours, String> myCourseCodeColumn;
    @FXML private TableColumn<Cours, String> myCourseTitleColumn;
    @FXML private TableColumn<Cours, String> myCourseModuleColumn;
    @FXML private TableColumn<Cours, String> myCourseLevelColumn;
    @FXML private TableColumn<Cours, String> myCourseStatusColumn;
    @FXML private TableColumn<Cours, Integer> myCourseContentsColumn;
    @FXML private TableColumn<Cours, Void> myCourseActionsColumn;
    @FXML private VBox myCourseDetailsPanel;
    @FXML private Label myCourseDetailHeroTitleLabel;
    @FXML private Label myCourseDetailHeroMetaLabel;
    @FXML private Label myCourseDetailCodeLabel;
    @FXML private Label myCourseDetailTitleLabel;
    @FXML private Label myCourseDetailModuleLabel;
    @FXML private Label myCourseDetailDescriptionLabel;
    @FXML private Label myCourseDetailLevelLabel;
    @FXML private Label myCourseDetailCreditsLabel;
    @FXML private Label myCourseDetailLanguageLabel;
    @FXML private Label myCourseDetailDatesLabel;
    @FXML private Label myCourseDetailStatusLabel;
    @FXML private Label myCourseDetailContentsLabel;
    @FXML private Label myCourseDetailImageLabel;
    @FXML private Label myCourseDetailPrerequisLabel;

    @FXML private Label allCoursesContextLabel;
    @FXML private Label allCoursesMessageLabel;
    @FXML private TableView<Cours> allCoursesTable;
    @FXML private TableColumn<Cours, String> allCourseCodeColumn;
    @FXML private TableColumn<Cours, String> allCourseTitleColumn;
    @FXML private TableColumn<Cours, String> allCourseModuleColumn;
    @FXML private TableColumn<Cours, String> allCourseLevelColumn;
    @FXML private TableColumn<Cours, String> allCourseStatusColumn;
    @FXML private TableColumn<Cours, Integer> allCourseContentsColumn;
    @FXML private TableColumn<Cours, Void> allCourseActionsColumn;
    @FXML private VBox allCourseDetailsPanel;
    @FXML private Label allCourseDetailHeroTitleLabel;
    @FXML private Label allCourseDetailHeroMetaLabel;
    @FXML private Label allCourseDetailCodeLabel;
    @FXML private Label allCourseDetailTitleLabel;
    @FXML private Label allCourseDetailModuleLabel;
    @FXML private Label allCourseDetailDescriptionLabel;
    @FXML private Label allCourseDetailLevelLabel;
    @FXML private Label allCourseDetailCreditsLabel;
    @FXML private Label allCourseDetailLanguageLabel;
    @FXML private Label allCourseDetailDatesLabel;
    @FXML private Label allCourseDetailStatusLabel;
    @FXML private Label allCourseDetailContentsLabel;
    @FXML private Label allCourseDetailImageLabel;
    @FXML private Label allCourseDetailPrerequisLabel;

    @FXML private Label currentCoursTitleLabel;
    @FXML private Label currentCoursSubtitleLabel;
    @FXML private Label contenusMessageLabel;
    @FXML private Label contenuCountLabel;
    @FXML private VBox contenuCardsContainer;
    @FXML private VBox contenuDetailsPanel;
    @FXML private Label contenuViewerSectionLabel;
    @FXML private Label contenuHeroTitleLabel;
    @FXML private Label contenuHeroMetaLabel;
    @FXML private Label contenuViewerDescriptionLabel;
    @FXML private VBox contenuTextPanel;
    @FXML private Label contenuTextBodyLabel;
    @FXML private Label contenuViewerInfoLabel;
    @FXML private Label contenuViewerLinkLabel;
    @FXML private StackPane contenuMediaContainer;
    @FXML private MediaView contenuMediaView;
    @FXML private Label contenuMediaPlaceholderLabel;
    @FXML private VBox contenuResourcePanel;
    @FXML private Button contenuPrimaryActionButton;
    @FXML private Button contenuSecondaryActionButton;

    private final ObservableList<KeyValueRow> profileRows = FXCollections.observableArrayList();
    private final ObservableList<KeyValueRow> focusRows = FXCollections.observableArrayList();
    private final ObservableList<Cours> myCourses = FXCollections.observableArrayList();
    private final ObservableList<Cours> availableCourses = FXCollections.observableArrayList();
    private final ObservableList<Contenu> contenus = FXCollections.observableArrayList();
    private final CoursService coursService = new CoursService();
    private final ContenuService contenuService = new ContenuService();

    private Etudiant currentStudent;
    private Cours selectedMyCourse;
    private Cours selectedAvailableCourse;
    private Cours selectedCoursForContenus;
    private Contenu selectedContenu;
    private MediaPlayer contenuMediaPlayer;

    @FXML
    private void initialize() {
        currentStudent = resolveStudent();
        configureHeader();
        configureOverviewTables();
        configureMyCoursesTable();
        configureAllCoursesTable();
        configureContenusTable();
        refreshStudentData();
        showOverviewPage();
    }

    @FXML
    private void showOverviewPage() {
        setPageVisibility(true, false, false, false);
        sectionTitleLabel.setText("Espace etudiant");
        sectionSubtitleLabel.setText("Consultez vos cours, explorez le catalogue et accedez aux contenus de formation.");
    }

    @FXML
    private void showMyCoursesPage() {
        setPageVisibility(false, true, false, false);
        sectionTitleLabel.setText("Mes cours");
        sectionSubtitleLabel.setText("Consultez les cours auxquels vous etes deja inscrit et accedez a leurs contenus.");
    }

    @FXML
    private void showAllCoursesPage() {
        setPageVisibility(false, false, true, false);
        sectionTitleLabel.setText("Tous les cours");
        sectionSubtitleLabel.setText("Explorez le catalogue disponible et inscrivez-vous aux cours qui vous interessent.");
    }

    @FXML
    private void showContenusPage() {
        if (selectedCoursForContenus == null) {
            myCoursesMessageLabel.setText("Selectionnez un cours pour consulter ses contenus.");
            showMyCoursesPage();
            return;
        }
        setPageVisibility(false, false, false, true);
        sectionTitleLabel.setText("Contenus");
        sectionSubtitleLabel.setText("Consultez les contenus disponibles pour le cours selectionne.");
        currentCoursTitleLabel.setText(safe(selectedCoursForContenus.getTitre()));
        currentCoursSubtitleLabel.setText(safe(selectedCoursForContenus.getCodeCours()) + " | " + safe(selectedCoursForContenus.getStatut()));
        loadContenusData();
    }

    @FXML
    private void showContenusForSelectedMyCourse() {
        if (selectedMyCourse == null) {
            myCoursesMessageLabel.setText("Selectionnez un cours pour consulter ses contenus.");
            return;
        }
        openCoursContenus(selectedMyCourse);
    }

    @FXML
    private void enrollSelectedCourse() {
        if (selectedAvailableCourse == null) {
            allCoursesMessageLabel.setText("Selectionnez un cours pour vous inscrire.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Inscription au cours");
        confirmation.setHeaderText("S'inscrire au cours " + safe(selectedAvailableCourse.getTitre()) + " ?");
        confirmation.setContentText("Votre inscription sera ajoutee a votre espace etudiant.");
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            coursService.enrollStudent(selectedAvailableCourse.getId(), currentStudent.getId());
            allCoursesMessageLabel.setText("Inscription enregistree avec succes.");
            refreshStudentData();
            showMyCoursesPage();
        } catch (SQLException e) {
            allCoursesMessageLabel.setText("Inscription impossible: " + e.getMessage());
        }
    }

    @FXML
    private void backToMyCoursesFromContenus() {
        showMyCoursesPage();
    }

    @FXML
    private void closeMyCourseDetails() {
        myCoursesTable.getSelectionModel().clearSelection();
        selectedMyCourse = null;
        updateMyCourseDetails(null);
    }

    @FXML
    private void closeAllCourseDetails() {
        allCoursesTable.getSelectionModel().clearSelection();
        selectedAvailableCourse = null;
        updateAvailableCourseDetails(null);
    }

    @FXML
    private void closeContenuDetails() {
        selectedContenu = null;
        stopCurrentMedia();
        updateContenuDetails(null);
        renderContenuCards();
    }

    @FXML
    private void handleContenuPrimaryAction() {
        if (selectedContenu == null) {
            return;
        }
        openContenuResource(selectedContenu);
    }

    @FXML
    private void handleContenuSecondaryAction() {
        if (selectedContenu == null) {
            return;
        }
        downloadContenuResource(selectedContenu);
    }

    @FXML
    private void handleProfileLogout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    private void refreshStudentData() {
        loadMyCourses();
        loadAvailableCourses();
        rebuildOverview();
        if (selectedCoursForContenus != null) {
            myCourses.stream()
                    .filter(cours -> Objects.equals(cours.getId(), selectedCoursForContenus.getId()))
                    .findFirst()
                    .ifPresentOrElse(cours -> {
                        selectedCoursForContenus = cours;
                        loadContenusData();
                    }, () -> {
                        selectedCoursForContenus = null;
                        contenus.clear();
                        updateContenuDetails(null);
                    });
        }
    }

    private void configureHeader() {
        welcomeLabel.setText("Bienvenue " + safe(currentStudent.getPrenom()));
        roleLabel.setText("Retrouvez vos cours, vos contenus et les nouvelles inscriptions disponibles.");
        currentUserNameLabel.setText(currentStudent.getNomComplet());
        currentUserRoleLabel.setText(currentStudent.getType());
        profileMenuButton.setText(buildInitials(currentStudent));
        myCoursesContextLabel.setText("Parcours de " + currentStudent.getNomComplet());
        allCoursesContextLabel.setText("Catalogue disponible pour " + currentStudent.getNiveauEtudeAbrege());
    }

    private void configureOverviewTables() {
        profileFieldColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().key()));
        profileValueColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().value()));
        focusTitleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().key()));
        focusDescriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().value()));
        profileTable.setItems(profileRows);
        focusTable.setItems(focusRows);
    }

    private void configureMyCoursesTable() {
        myCourseCodeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getCodeCours())));
        myCourseTitleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getTitre())));
        myCourseModuleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(moduleLabel(data.getValue().getModule())));
        myCourseLevelColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getNiveau())));
        myCourseStatusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getStatut())));
        myCourseContentsColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(contentCountForCourse(data.getValue())));
        myCourseActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewButton = new Button("Voir");
            private final HBox actions = new HBox(8.0, viewButton);

            {
                viewButton.getStyleClass().addAll("secondary-button", "table-action-button");
                viewButton.setOnAction(event -> openCoursContenus(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });
        myCoursesTable.setItems(myCourses);
        myCoursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedMyCourse = newValue;
            updateMyCourseDetails(newValue);
        });
    }

    private void configureAllCoursesTable() {
        allCourseCodeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getCodeCours())));
        allCourseTitleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getTitre())));
        allCourseModuleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(moduleLabel(data.getValue().getModule())));
        allCourseLevelColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getNiveau())));
        allCourseStatusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(safe(data.getValue().getStatut())));
        allCourseContentsColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(contentCountForCourse(data.getValue())));
        allCourseActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button enrollButton = new Button("S'inscrire");
            private final HBox actions = new HBox(8.0, enrollButton);

            {
                enrollButton.getStyleClass().addAll("primary-button", "table-action-button");
                enrollButton.setOnAction(event -> {
                    Cours cours = getTableView().getItems().get(getIndex());
                    allCoursesTable.getSelectionModel().select(cours);
                    selectedAvailableCourse = cours;
                    enrollSelectedCourse();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });
        allCoursesTable.setItems(availableCourses);
        allCoursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedAvailableCourse = newValue;
            updateAvailableCourseDetails(newValue);
        });
    }

    private void configureContenusTable() {
        contenuCardsContainer.getChildren().clear();
        contenuCountLabel.setText("0 contenu");
    }

    private void loadMyCourses() {
        myCourses.clear();
        myCoursesMessageLabel.setText("");
        try {
            myCourses.setAll(coursService.getByStudentId(currentStudent.getId()));
        } catch (SQLException e) {
            myCoursesMessageLabel.setText("Chargement de vos cours impossible: " + e.getMessage());
        }
        if (myCourses.isEmpty() && myCoursesMessageLabel.getText().isBlank()) {
            myCoursesMessageLabel.setText("Vous n'etes inscrit a aucun cours pour le moment.");
        }
    }

    private void loadAvailableCourses() {
        availableCourses.clear();
        allCoursesMessageLabel.setText("");
        try {
            availableCourses.setAll(coursService.getAvailableForStudentId(currentStudent.getId()));
        } catch (SQLException e) {
            allCoursesMessageLabel.setText("Chargement du catalogue impossible: " + e.getMessage());
        }
        if (availableCourses.isEmpty() && allCoursesMessageLabel.getText().isBlank()) {
            allCoursesMessageLabel.setText("Aucun nouveau cours disponible pour le moment.");
        }
    }

    private void loadContenusData() {
        contenus.clear();
        contenusMessageLabel.setText("");
        selectedContenu = null;
        stopCurrentMedia();
        updateContenuDetails(null);
        if (selectedCoursForContenus == null || selectedCoursForContenus.getId() == null) {
            renderContenuCards();
            return;
        }
        try {
            contenus.setAll(contenuService.getByCoursId(selectedCoursForContenus.getId()));
        } catch (SQLException e) {
            contenusMessageLabel.setText("Chargement des contenus impossible: " + e.getMessage());
        }
        renderContenuCards();
        if (!contenus.isEmpty()) {
            selectedContenu = contenus.get(0);
            updateContenuDetails(selectedContenu);
            renderContenuCards();
        }
        if (contenus.isEmpty() && contenusMessageLabel.getText().isBlank()) {
            contenusMessageLabel.setText("Aucun contenu n'est disponible pour ce cours.");
        }
    }

    private void rebuildOverview() {
        int totalContenus = myCourses.stream()
                .mapToInt(this::contentCountForCourse)
                .sum();

        enrolledCoursesCountLabel.setText(String.valueOf(myCourses.size()));
        availableCoursesCountLabel.setText(String.valueOf(availableCourses.size()));
        completedContentsCountLabel.setText(String.valueOf(totalContenus));

        profileRows.setAll(
                new KeyValueRow("Nom complet", currentStudent.getNomComplet()),
                new KeyValueRow("Email", safe(currentStudent.getEmail())),
                new KeyValueRow("Matricule", safe(currentStudent.getMatricule())),
                new KeyValueRow("Niveau", safe(currentStudent.getNiveauEtude())),
                new KeyValueRow("Specialisation", safe(currentStudent.getSpecialisation())),
                new KeyValueRow("Telephone", safe(currentStudent.getTelephone())),
                new KeyValueRow("Statut", safe(currentStudent.getStatut())),
                new KeyValueRow("Inscription", formatDateTime(currentStudent.getDateInscription()))
        );

        focusRows.setAll(
                new KeyValueRow("Mes cours", myCourses.isEmpty()
                        ? "Vous n'avez encore aucun cours actif."
                        : "Vous avez " + myCourses.size() + " cours deja rattaches a votre espace."),
                new KeyValueRow("Catalogue", availableCourses.isEmpty()
                        ? "Aucun nouveau cours n'est disponible actuellement."
                        : availableCourses.size() + " cours sont disponibles pour une nouvelle inscription."),
                new KeyValueRow("Contenus", totalContenus + " contenus sont consultables sur vos cours actuels."),
                new KeyValueRow("Action conseillee", myCourses.isEmpty()
                        ? "Parcourez le catalogue et inscrivez-vous a un premier cours."
                        : "Ouvrez un cours de votre liste pour consulter ses contenus.")
        );
    }

    private void renderContenuCards() {
        detachContenuDetailsPanel();
        contenuCardsContainer.getChildren().clear();
        contenuCountLabel.setText(contenus.size() + (contenus.size() > 1 ? " contenus" : " contenu"));

        if (contenus.isEmpty()) {
            VBox emptyState = new VBox(6.0);
            emptyState.getStyleClass().add("student-content-empty");
            Label title = new Label("Aucun contenu disponible");
            title.getStyleClass().add("student-content-empty-title");
            Label subtitle = new Label("Les ressources du cours apparaitront ici lorsqu'elles seront publiees.");
            subtitle.setWrapText(true);
            subtitle.getStyleClass().add("student-content-empty-copy");
            emptyState.getChildren().addAll(title, subtitle);
            contenuCardsContainer.getChildren().add(emptyState);
            return;
        }

        for (Contenu contenu : contenus) {
            HBox card = new HBox(16.0);
            card.getStyleClass().add("student-content-card");
            if (selectedContenu != null && Objects.equals(selectedContenu.getId(), contenu.getId())) {
                card.getStyleClass().add("student-content-card-active");
            }

            StackPane badge = new StackPane();
            badge.getStyleClass().add("student-content-badge");
            Label badgeLabel = new Label(contentBadgeLabel(contenu));
            badgeLabel.getStyleClass().add("student-content-badge-text");
            badge.getChildren().add(badgeLabel);

            VBox body = new VBox(6.0);
            body.setFillWidth(true);
            Label title = new Label(safe(contenu.getTitre()));
            title.getStyleClass().add("student-content-title");
            title.setWrapText(true);

            Label description = new Label(buildContenuSummary(contenu));
            description.getStyleClass().add("student-content-description");
            description.setWrapText(true);

            HBox metaRow = new HBox(10.0);
            Label metaType = new Label(safe(contenu.getTypeContenu()));
            metaType.getStyleClass().add("student-content-chip");
            Label metaOrder = new Label("Chapitre " + contenu.getOrdreAffichage());
            metaOrder.getStyleClass().add("student-content-chip");
            Label metaDuration = new Label(contenu.getDuree() == null ? "Duree libre" : contenu.getDuree() + " min");
            metaDuration.getStyleClass().add("student-content-chip");
            metaRow.getChildren().addAll(metaType, metaOrder, metaDuration);

            body.getChildren().addAll(title, description, metaRow);
            HBox.setHgrow(body, javafx.scene.layout.Priority.ALWAYS);

            Label arrow = new Label("v");
            arrow.getStyleClass().add("student-content-arrow");

            card.getChildren().addAll(badge, body, arrow);
            card.setOnMouseClicked(event -> {
                if (selectedContenu != null && Objects.equals(selectedContenu.getId(), contenu.getId())) {
                    selectedContenu = null;
                    stopCurrentMedia();
                    updateContenuDetails(null);
                } else {
                    selectedContenu = contenu;
                    updateContenuDetails(contenu);
                }
                renderContenuCards();
            });
            contenuCardsContainer.getChildren().add(card);
            if (selectedContenu != null && Objects.equals(selectedContenu.getId(), contenu.getId())) {
                contenuCardsContainer.getChildren().add(contenuDetailsPanel);
            }
        }
    }

    private void updateMyCourseDetails(Cours cours) {
        boolean visible = cours != null;
        myCourseDetailsPanel.setVisible(visible);
        myCourseDetailsPanel.setManaged(visible);
        if (!visible) {
            myCourseDetailHeroTitleLabel.setText("Aucun cours selectionne");
            myCourseDetailHeroMetaLabel.setText("La fiche detaillee apparaitra ici apres selection dans la liste.");
            myCourseDetailCodeLabel.setText("-");
            myCourseDetailTitleLabel.setText("-");
            myCourseDetailModuleLabel.setText("-");
            myCourseDetailDescriptionLabel.setText("-");
            myCourseDetailLevelLabel.setText("-");
            myCourseDetailCreditsLabel.setText("-");
            myCourseDetailLanguageLabel.setText("-");
            myCourseDetailDatesLabel.setText("-");
            myCourseDetailStatusLabel.setText("-");
            myCourseDetailContentsLabel.setText("-");
            myCourseDetailImageLabel.setText("-");
            myCourseDetailPrerequisLabel.setText("-");
            return;
        }

        myCourseDetailHeroTitleLabel.setText(safe(cours.getTitre()));
        myCourseDetailHeroMetaLabel.setText(safe(cours.getCodeCours()) + " | " + safe(cours.getStatut()));
        myCourseDetailCodeLabel.setText(safe(cours.getCodeCours()));
        myCourseDetailTitleLabel.setText(safe(cours.getTitre()));
        myCourseDetailModuleLabel.setText(moduleLabel(cours.getModule()));
        myCourseDetailDescriptionLabel.setText(safe(cours.getDescription()));
        myCourseDetailLevelLabel.setText(safe(cours.getNiveau()));
        myCourseDetailCreditsLabel.setText(cours.getCredits() == null ? "-" : String.valueOf(cours.getCredits()));
        myCourseDetailLanguageLabel.setText(safe(cours.getLangue()));
        myCourseDetailDatesLabel.setText(formatDateRange(cours.getDateDebut(), cours.getDateFin()));
        myCourseDetailStatusLabel.setText(safe(cours.getStatut()));
        myCourseDetailContentsLabel.setText(String.valueOf(contentCountForCourse(cours)));
        myCourseDetailImageLabel.setText(safe(cours.getImageCoursUrl()));
        myCourseDetailPrerequisLabel.setText(formatPrerequis(cours));
    }

    private void updateAvailableCourseDetails(Cours cours) {
        boolean visible = cours != null;
        allCourseDetailsPanel.setVisible(visible);
        allCourseDetailsPanel.setManaged(visible);
        if (!visible) {
            allCourseDetailHeroTitleLabel.setText("Aucun cours selectionne");
            allCourseDetailHeroMetaLabel.setText("La fiche detaillee apparaitra ici apres selection dans la liste.");
            allCourseDetailCodeLabel.setText("-");
            allCourseDetailTitleLabel.setText("-");
            allCourseDetailModuleLabel.setText("-");
            allCourseDetailDescriptionLabel.setText("-");
            allCourseDetailLevelLabel.setText("-");
            allCourseDetailCreditsLabel.setText("-");
            allCourseDetailLanguageLabel.setText("-");
            allCourseDetailDatesLabel.setText("-");
            allCourseDetailStatusLabel.setText("-");
            allCourseDetailContentsLabel.setText("-");
            allCourseDetailImageLabel.setText("-");
            allCourseDetailPrerequisLabel.setText("-");
            return;
        }

        allCourseDetailHeroTitleLabel.setText(safe(cours.getTitre()));
        allCourseDetailHeroMetaLabel.setText(safe(cours.getCodeCours()) + " | " + safe(cours.getStatut()));
        allCourseDetailCodeLabel.setText(safe(cours.getCodeCours()));
        allCourseDetailTitleLabel.setText(safe(cours.getTitre()));
        allCourseDetailModuleLabel.setText(moduleLabel(cours.getModule()));
        allCourseDetailDescriptionLabel.setText(safe(cours.getDescription()));
        allCourseDetailLevelLabel.setText(safe(cours.getNiveau()));
        allCourseDetailCreditsLabel.setText(cours.getCredits() == null ? "-" : String.valueOf(cours.getCredits()));
        allCourseDetailLanguageLabel.setText(safe(cours.getLangue()));
        allCourseDetailDatesLabel.setText(formatDateRange(cours.getDateDebut(), cours.getDateFin()));
        allCourseDetailStatusLabel.setText(safe(cours.getStatut()));
        allCourseDetailContentsLabel.setText(String.valueOf(contentCountForCourse(cours)));
        allCourseDetailImageLabel.setText(safe(cours.getImageCoursUrl()));
        allCourseDetailPrerequisLabel.setText(formatPrerequis(cours));
    }

    private void updateContenuDetails(Contenu contenu) {
        boolean visible = contenu != null;
        contenuDetailsPanel.setVisible(visible);
        contenuDetailsPanel.setManaged(visible);
        if (!visible) {
            contenuViewerSectionLabel.setText("Consultation");
            contenuHeroTitleLabel.setText("Selectionnez une ressource");
            contenuHeroMetaLabel.setText("Choisissez un contenu ci-dessous pour le consulter.");
            contenuViewerDescriptionLabel.setText("Le contenu selectionne s'affiche ici directement sous le titre du cours.");
            contenuTextPanel.setVisible(false);
            contenuTextPanel.setManaged(false);
            contenuTextBodyLabel.setText("");
            contenuMediaContainer.setVisible(false);
            contenuMediaContainer.setManaged(false);
            contenuViewerInfoLabel.setText("-");
            contenuViewerLinkLabel.setText("-");
            contenuMediaPlaceholderLabel.setText("Apercu du contenu");
            contenuMediaPlaceholderLabel.setVisible(true);
            contenuMediaPlaceholderLabel.setManaged(true);
            contenuMediaView.setVisible(false);
            contenuResourcePanel.setVisible(false);
            contenuResourcePanel.setManaged(false);
            contenuPrimaryActionButton.setVisible(false);
            contenuPrimaryActionButton.setManaged(false);
            contenuSecondaryActionButton.setVisible(false);
            contenuSecondaryActionButton.setManaged(false);
            return;
        }

        selectedContenu = contenu;
        stopCurrentMedia();
        boolean text = isTextContent(contenu);
        boolean video = isVideoContent(contenu);
        boolean downloadable = isDownloadableContent(contenu);
        boolean externalResource = !text;
        contenuHeroTitleLabel.setText(safe(contenu.getTitre()));
        contenuHeroMetaLabel.setText(safe(contenu.getTypeContenu()) + " | " + (contenu.getDuree() == null ? "Consultation libre" : contenu.getDuree() + " min"));
        contenuViewerDescriptionLabel.setText(buildContenuViewerDescription(contenu));
        contenuViewerInfoLabel.setText("Ordre " + contenu.getOrdreAffichage()
                + " | " + (contenu.isEstPublic() ? "Public" : "Prive")
                + " | " + formatDateTime(contenu.getDateAjout()));
        contenuViewerLinkLabel.setText(safe(contenu.getUrlContenu()));

        contenuViewerSectionLabel.setText(text ? "Lecture du contenu" : video ? "Lecteur video" : "Support de cours");
        contenuTextPanel.setVisible(text);
        contenuTextPanel.setManaged(text);
        contenuTextBodyLabel.setText(buildTextContent(contenu));
        contenuResourcePanel.setVisible(externalResource);
        contenuResourcePanel.setManaged(externalResource);
        contenuPrimaryActionButton.setVisible(externalResource);
        contenuPrimaryActionButton.setManaged(externalResource);
        contenuPrimaryActionButton.setText(video ? "Ouvrir la video" : "Ouvrir le support");
        contenuSecondaryActionButton.setVisible(externalResource && downloadable);
        contenuSecondaryActionButton.setManaged(externalResource && downloadable);
        contenuSecondaryActionButton.setText("Telecharger");

        if (video && startVideoPlayback(contenu)) {
            contenuMediaContainer.setVisible(true);
            contenuMediaContainer.setManaged(true);
            contenuMediaView.setVisible(true);
            contenuMediaPlaceholderLabel.setVisible(false);
            contenuMediaPlaceholderLabel.setManaged(false);
        } else {
            contenuMediaView.setVisible(false);
            contenuMediaContainer.setVisible(video);
            contenuMediaContainer.setManaged(video);
            contenuMediaView.setVisible(false);
            contenuMediaPlaceholderLabel.setVisible(true);
            contenuMediaPlaceholderLabel.setManaged(true);
            contenuMediaPlaceholderLabel.setText(video
                    ? "Lecture integree indisponible. Utilisez le bouton pour ouvrir la video."
                    : contentPlaceholderText(contenu));
        }
    }

    private void detachContenuDetailsPanel() {
        if (contenuDetailsPanel.getParent() instanceof Pane pane) {
            pane.getChildren().remove(contenuDetailsPanel);
        }
    }

    private void setPageVisibility(boolean overviewVisible, boolean myCoursesVisible, boolean allCoursesVisible, boolean contenusVisible) {
        overviewSection.setVisible(overviewVisible);
        overviewSection.setManaged(overviewVisible);
        myCoursesSection.setVisible(myCoursesVisible);
        myCoursesSection.setManaged(myCoursesVisible);
        allCoursesSection.setVisible(allCoursesVisible);
        allCoursesSection.setManaged(allCoursesVisible);
        contenusSection.setVisible(contenusVisible);
        contenusSection.setManaged(contenusVisible);
    }

    private void openCoursContenus(Cours cours) {
        myCoursesTable.getSelectionModel().select(cours);
        selectedMyCourse = cours;
        selectedCoursForContenus = cours;
        showContenusPage();
    }

    private int contentCountForCourse(Cours cours) {
        if (cours == null || cours.getId() == null) {
            return 0;
        }
        try {
            return contenuService.getByCoursId(cours.getId()).size();
        } catch (SQLException e) {
            return 0;
        }
    }

    private Etudiant resolveStudent() {
        Utilisateur utilisateur = UserSession.getCurrentUser();
        return utilisateur instanceof Etudiant etudiant ? etudiant : buildDemoStudent();
    }

    private String buildInitials(Utilisateur utilisateur) {
        String prenom = utilisateur.getPrenom() != null && !utilisateur.getPrenom().isBlank()
                ? utilisateur.getPrenom().substring(0, 1).toUpperCase() : "";
        String nom = utilisateur.getNom() != null && !utilisateur.getNom().isBlank()
                ? utilisateur.getNom().substring(0, 1).toUpperCase() : "";
        return prenom + nom;
    }

    private String moduleLabel(Module module) {
        if (module == null) {
            return "-";
        }
        if (module.getTitreModule() != null && !module.getTitreModule().isBlank()) {
            return module.getTitreModule();
        }
        return module.getId() == null ? "-" : "Module #" + module.getId();
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

    private String formatPrerequis(Cours cours) {
        return cours.getPrerequis() == null || cours.getPrerequis().isEmpty() ? "-" : String.join(", ", cours.getPrerequis());
    }

    private String buildContenuViewerDescription(Contenu contenu) {
        if (contenu.getDescription() != null && !contenu.getDescription().isBlank()) {
            if (isTextContent(contenu)) {
                return "Le contenu textuel est affiche directement dans la page de consultation.";
            }
            return contenu.getDescription();
        }
        if (isVideoContent(contenu)) {
            return "Ce contenu video peut etre lu directement dans l'espace etudiant lorsqu'il est compatible.";
        }
        if (isTextContent(contenu)) {
            return "Le contenu textuel est disponible en lecture directe dans cette page.";
        }
        if (hasType(contenu, "pdf")) {
            return "Ce support PDF peut etre ouvert dans une visionneuse externe ou telecharge selon votre navigateur.";
        }
        if (hasType(contenu, "ppt")) {
            return "Ce support de presentation peut etre ouvert ou telecharge pour consultation hors ligne.";
        }
        if (hasType(contenu, "lien")) {
            return "Cette ressource externe s'ouvre dans votre navigateur par defaut.";
        }
        return "Selectionnez une action pour consulter cette ressource.";
    }

    private String contentPlaceholderText(Contenu contenu) {
        if (hasType(contenu, "pdf")) {
            return "Document PDF pret a etre ouvert ou telecharge.";
        }
        if (hasType(contenu, "ppt")) {
            return "Presentation disponible a l'ouverture ou au telechargement.";
        }
        if (hasType(contenu, "lien")) {
            return "Lien externe pret a etre consulte.";
        }
        if (hasType(contenu, "quiz")) {
            return "Ressource interactive disponible a l'ouverture.";
        }
        return "Ressource disponible a la consultation.";
    }

    private String buildTextContent(Contenu contenu) {
        if (contenu.getDescription() != null && !contenu.getDescription().isBlank()) {
            return contenu.getDescription().trim();
        }
        if (contenu.getUrlContenu() != null && !contenu.getUrlContenu().isBlank()) {
            return contenu.getUrlContenu().trim();
        }
        return "Aucun texte n'est disponible pour cette ressource.";
    }

    private boolean isTextContent(Contenu contenu) {
        return hasType(contenu, "texte");
    }

    private boolean isVideoContent(Contenu contenu) {
        return hasType(contenu, "video");
    }

    private boolean isDownloadableContent(Contenu contenu) {
        return hasType(contenu, "pdf") || hasType(contenu, "ppt") || hasType(contenu, "video");
    }

    private boolean hasType(Contenu contenu, String type) {
        return contenu.getTypeContenuList().stream().anyMatch(item -> type.equalsIgnoreCase(item.trim()));
    }

    private boolean startVideoPlayback(Contenu contenu) {
        String source = contenu.getUrlContenu();
        if (source == null || source.isBlank()) {
            return false;
        }
        try {
            String mediaSource = toMediaSource(source);
            Media media = new Media(mediaSource);
            contenuMediaPlayer = new MediaPlayer(media);
            contenuMediaPlayer.setAutoPlay(false);
            contenuMediaView.setMediaPlayer(contenuMediaPlayer);
            return true;
        } catch (MediaException | IllegalArgumentException e) {
            return false;
        }
    }

    private void stopCurrentMedia() {
        if (contenuMediaPlayer != null) {
            contenuMediaPlayer.stop();
            contenuMediaPlayer.dispose();
            contenuMediaPlayer = null;
        }
        contenuMediaView.setMediaPlayer(null);
    }

    private String toMediaSource(String source) {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            return source;
        }
        return Path.of(source).toUri().toString();
    }

    private void openContenuResource(Contenu contenu) {
        try {
            if (contenu.getUrlContenu() == null || contenu.getUrlContenu().isBlank()) {
                contenusMessageLabel.setText("Aucune ressource principale n'est definie pour ce contenu.");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if (isWebUrl(contenu.getUrlContenu())) {
                desktop.browse(URI.create(contenu.getUrlContenu()));
            } else {
                desktop.open(new File(contenu.getUrlContenu()));
            }
        } catch (Exception e) {
            contenusMessageLabel.setText("Ouverture impossible: " + e.getMessage());
        }
    }

    private void downloadContenuResource(Contenu contenu) {
        try {
            String source = contenu.getUrlContenu();
            if (source == null || source.isBlank()) {
                contenusMessageLabel.setText("Aucun fichier a telecharger.");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if (isWebUrl(source)) {
                desktop.browse(URI.create(source));
            } else {
                Path file = Path.of(source);
                if (Files.exists(file)) {
                    desktop.open(file.toFile());
                } else {
                    contenusMessageLabel.setText("Fichier introuvable: " + source);
                }
            }
        } catch (Exception e) {
            contenusMessageLabel.setText("Telechargement impossible: " + e.getMessage());
        }
    }

    private boolean isWebUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private String contentBadgeLabel(Contenu contenu) {
        List<String> types = contenu.getTypeContenuList();
        if (types.isEmpty()) {
            return "TXT";
        }
        String first = types.get(0);
        return switch (first) {
            case "video" -> "VID";
            case "pdf" -> "PDF";
            case "ppt" -> "PPT";
            case "quiz" -> "QZ";
            case "lien" -> "URL";
            default -> "TXT";
        };
    }

    private String buildContenuSummary(Contenu contenu) {
        if (contenu.getDescription() != null && !contenu.getDescription().isBlank()) {
            String description = contenu.getDescription().trim();
            return description.length() > 180 ? description.substring(0, 177) + "..." : description;
        }
        if (contenu.getUrlContenu() != null && !contenu.getUrlContenu().isBlank()) {
            return contenu.getUrlContenu();
        }
        return "Cliquez pour consulter les details de cette ressource.";
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private Etudiant buildDemoStudent() {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(3001L);
        etudiant.setNom("Trabelsi");
        etudiant.setPrenom("Amine");
        etudiant.setEmail("amine.trabelsi@demo.tn");
        etudiant.setMatricule("ETU-GL-2401");
        etudiant.setNiveauEtude("Master 1");
        etudiant.setSpecialisation("Genie Logiciel");
        etudiant.setDateNaissance(LocalDate.of(2002, 5, 14));
        etudiant.setTelephone("22123456");
        etudiant.setAdresse("Tunis, Centre Urbain Nord");
        etudiant.setDateInscription(LocalDateTime.now().minusMonths(7));
        etudiant.setStatut("actif");
        return etudiant;
    }

    private record KeyValueRow(String key, String value) {
    }
}
