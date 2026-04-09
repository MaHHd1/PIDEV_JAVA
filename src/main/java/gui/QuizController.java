package gui;

import entities.Quiz;
import entities.Utilisateur;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import services.QuizService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ResourceBundle;

public class QuizController implements Initializable {

    // ── TOPBAR ────────────────────────────────────────────────────
    @FXML private Label      sectionTitleLabel;
    @FXML private Label      sectionSubtitleLabel;
    @FXML private Label      currentUserNameLabel;
    @FXML private Label      currentUserRoleLabel;
    @FXML private MenuButton profileMenuButton;

    // ── STUDENT — liste ───────────────────────────────────────────
    @FXML private VBox  quizListSection;
    @FXML private Label quizListSubtitleLabel;
    @FXML private Label quizCountLabel;
    @FXML private VBox  quizCardsContainer;
    @FXML private Label quizListMessageLabel;

    // ── STUDENT — détail ──────────────────────────────────────────
    @FXML private VBox   quizDetailSection;
    @FXML private Label  detailQuizTitleLabel;
    @FXML private Label  detailQuizTypeLabel;
    @FXML private Label  detailQuizDescLabel;
    @FXML private Label  detailQuizDureeLabel;
    @FXML private Label  detailQuizNbQuestionsLabel;
    @FXML private Label  detailQuizTentativesLabel;
    @FXML private Label  detailQuizDispoLabel;
    @FXML private Label  detailQuizInstructionsLabel;
    @FXML private Label  detailQuizMessageLabel;
    @FXML private Button startQuizButton;
    @FXML private Label  myTentativesLabel;
    @FXML private Label  myBestScoreLabel;
    @FXML private Label  quizMoyenneLabel;

    // ── STUDENT — passage ─────────────────────────────────────────
    @FXML private VBox        quizPassageSection;
    @FXML private Label       passageQuizTitleLabel;
    @FXML private Label       questionIndexLabel;
    @FXML private ProgressBar quizProgressBar;
    @FXML private Label       progressPercentLabel;
    @FXML private Label       timerLabel;
    @FXML private Label       questionPointsLabel;
    @FXML private Label       questionTypeLabel;
    @FXML private Label       questionTexteLabel;
    @FXML private VBox        choixMultiplePanel;
    @FXML private ToggleGroup reponseToggleGroup;
    @FXML private VBox        reponsesContainer;
    @FXML private VBox        texteLibrePanel;
    @FXML private TextArea    texteLibreArea;
    @FXML private Label       texteLibreErrorLabel;
    @FXML private Label       passageMessageLabel;
    @FXML private Button      previousQuestionButton;
    @FXML private Button      nextQuestionButton;
    @FXML private Button      submitQuizButton;
    @FXML private VBox        quizPlanContainer;

    // ── STUDENT — résultat ────────────────────────────────────────
    @FXML private VBox        quizResultatSection;
    @FXML private Label       resultatQuizTitleLabel;
    @FXML private Label       resultatScoreLabel;
    @FXML private Label       resultatPointsLabel;
    @FXML private Label       resultatMentionLabel;
    @FXML private ProgressBar resultatProgressBar;
    @FXML private VBox        correctionContainer;

    // ── STUDENT — historique ──────────────────────────────────────
    @FXML private VBox              myResultsSection;
    @FXML private Label             totalTentativesLabel;
    @FXML private TableView<Quiz>   resultatsTable;
    @FXML private TableColumn<?,?> resQuizTitleColumn;
    @FXML private TableColumn<?,?> resScoreColumn;
    @FXML private TableColumn<?,?> resPointsColumn;
    @FXML private TableColumn<?,?> resDateColumn;
    @FXML private TableColumn<?,?> resMentionColumn;
    @FXML private Label             myResultsMessageLabel;

    // ── TEACHER — liste ───────────────────────────────────────────
    @FXML private TableView<Quiz>   quizTable;
    @FXML private TableColumn<?,?> quizTitreColumn;
    @FXML private TableColumn<?,?> quizTypeColumn;
    @FXML private TableColumn<?,?> quizDureeColumn;
    @FXML private TableColumn<?,?> quizQuestionsColumn;
    @FXML private TableColumn<?,?> quizDispoColumn;
    @FXML private TableColumn<?,?> quizActionsColumn;
    @FXML private VBox  quizDetailsPanel;
    @FXML private Label detailQuizMetaLabel;
    @FXML private Label detailQuizCorrectionLabel;

    // ── TEACHER — formulaire quiz ─────────────────────────────────
    @FXML private VBox      quizFormSection;
    @FXML private Label     quizFormTitleLabel;
    @FXML private TextField quizTitreField;
    @FXML private Label     quizTitreErrorLabel;
    @FXML private ComboBox<String> quizTypeCombo;
    @FXML private Label     quizTypeErrorLabel;
    @FXML private ComboBox<String> quizCorrectionApresCombo;
    @FXML private Label     quizCorrectionErrorLabel;
    @FXML private Spinner<Integer> quizDureeSpinner;
    @FXML private Label     quizDureeErrorLabel;
    @FXML private Spinner<Integer> quizTentativesSpinner;
    @FXML private Label     quizTentativesErrorLabel;
    @FXML private DatePicker quizDateDebutPicker;
    @FXML private Label      quizDateDebutErrorLabel;
    @FXML private DatePicker quizDateFinPicker;
    @FXML private Label      quizDateFinErrorLabel;
    @FXML private TextField  quizIdCoursField;
    @FXML private Label      quizIdCoursErrorLabel;
    @FXML private TextArea   quizInstructionsArea;
    @FXML private Label      quizInstructionsErrorLabel;
    @FXML private TextArea   quizDescriptionArea;
    @FXML private Label      quizDescriptionErrorLabel;
    @FXML private Label      quizFormStatusLabel;
    @FXML private Label      quizDateRangeInfoLabel;

    // ── TEACHER — questions ───────────────────────────────────────
    @FXML private VBox              questionsSection;
    @FXML private Label             questionsSectionTitleLabel;
    @FXML private Label             questionsSectionSubtitleLabel;
    @FXML private Label             questionsMessageLabel;
    @FXML private TableView<?>      questionsTable;
    @FXML private TableColumn<?,?> qOrdreColumn;
    @FXML private TableColumn<?,?> qTexteColumn;
    @FXML private TableColumn<?,?> qTypeColumn;
    @FXML private TableColumn<?,?> qPointsColumn;
    @FXML private TableColumn<?,?> qReponsesColumn;
    @FXML private TableColumn<?,?> qActionsColumn;
    @FXML private VBox  questionDetailsPanel;
    @FXML private Label detailQuestionTexteLabel;
    @FXML private Label detailQuestionMetaLabel;
    @FXML private VBox  detailReponsesContainer;
    @FXML private Label detailQuestionExplicationLabel;

    // ── TEACHER — formulaire question ─────────────────────────────
    @FXML private VBox      questionFormSection;
    @FXML private Label     questionFormTitleLabel;
    @FXML private TextArea  questionTexteArea;
    @FXML private Label     questionTexteErrorLabel;
    @FXML private ComboBox<String> questionTypeCombo;
    @FXML private Label     questionTypeErrorLabel;
    @FXML private Spinner<Integer> questionPointsSpinner;
    @FXML private Label     questionPointsErrorLabel;
    @FXML private Spinner<Integer> questionOrdreSpinner;
    @FXML private TextArea  questionExplicationArea;
    @FXML private Label     questionExplicationErrorLabel;
    @FXML private Label     questionFormStatusLabel;

    // ── TEACHER — réponses ────────────────────────────────────────
    @FXML private VBox              reponsesSection;
    @FXML private Label             reponsesSectionTitleLabel;
    @FXML private Label             reponsesSectionSubtitleLabel;
    @FXML private Label             reponsesMessageLabel;
    @FXML private TableView<?>      reponsesTable;
    @FXML private TableColumn<?,?> rOrdreColumn;
    @FXML private TableColumn<?,?> rTexteColumn;
    @FXML private TableColumn<?,?> rCorrecteColumn;
    @FXML private TableColumn<?,?> rFeedbackColumn;
    @FXML private TableColumn<?,?> rActionsColumn;
    @FXML private VBox      reponseFormPanel;
    @FXML private Label     reponseFormTitleLabel;
    @FXML private TextArea  reponseTexteArea;
    @FXML private Label     reponseTexteErrorLabel;
    @FXML private CheckBox  reponseCorrecteCheck;
    @FXML private Spinner<Integer> reponseOrdreSpinner;
    @FXML private Label     reponseOrdreErrorLabel;
    @FXML private TextArea  reponseFeedbackArea;
    @FXML private Label     reponseFeedbackErrorLabel;
    @FXML private TextField reponseMediaUrlField;
    @FXML private Label     reponseFormStatusLabel;

    // ── État interne ──────────────────────────────────────────────
    private boolean isTeacher = false;
    private Timeline quizTimer;
    private int remainingSeconds    = 0;
    private int currentQuestionIndex = 0;
    private int totalQuestions       = 0;
    private int currentQuizId        = -1;
    private int currentQuestionId    = -1;

    /** Quiz sélectionné dans le tableau enseignant. */
    private Quiz selectedQuiz  = null;
    /** null = création, non-null = modification. */
    private Quiz editingQuiz   = null;

    private final QuizService quizService = new QuizService();
    private final ObservableList<Quiz> quizList = FXCollections.observableArrayList();

    // ── INITIALISATION ────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isTeacher = (quizTable != null);
        if (isTeacher) initTeacher();
        else           initStudent();
    }

    @SuppressWarnings("unchecked")
    private void initTeacher() {
        setText(sectionTitleLabel,    "Gestion des Quiz");
        setText(sectionSubtitleLabel, "Créez, modifiez et organisez vos quiz et leurs questions.");

        if (quizTypeCombo != null)
            quizTypeCombo.setItems(FXCollections.observableArrayList("QCM", "Vrai/Faux", "Texte libre", "Mixte"));
        if (quizCorrectionApresCombo != null)
            quizCorrectionApresCombo.setItems(FXCollections.observableArrayList("Immédiate", "Après soumission", "Manuelle"));
        if (questionTypeCombo != null)
            questionTypeCombo.setItems(FXCollections.observableArrayList("Choix unique", "Choix multiple", "Vrai/Faux", "Texte libre"));

        if (quizTable != null) {
            setupColumn(quizTitreColumn, "titre");
            setupColumn(quizTypeColumn,  "typeQuiz");
            setupColumn(quizDureeColumn, "dureeMinutes");
            setupColumn(quizDispoColumn, "dateFinDisponibilite");

            // Colonne actions
            if (quizActionsColumn != null) {
                ((TableColumn<Quiz, Void>) quizActionsColumn).setCellFactory(col -> new TableCell<>() {
                    private final Button btnEdit   = new Button("✏ Modifier");
                    private final Button btnDel    = new Button("🗑 Supprimer");
                    private final Button btnQuest  = new Button("❓ Questions");
                    private final HBox   box       = new HBox(6, btnEdit, btnQuest, btnDel);
                    {
                        btnEdit .getStyleClass().addAll("secondary-button", "table-action-button");
                        btnQuest.getStyleClass().addAll("primary-button",   "table-action-button");
                        btnDel  .getStyleClass().addAll("danger-button",    "table-action-button");
                        btnEdit .setOnAction(e -> startEditQuiz(getTableView().getItems().get(getIndex())));
                        btnQuest.setOnAction(e -> openQuestionsForQuiz(getTableView().getItems().get(getIndex())));
                        btnDel  .setOnAction(e -> confirmDeleteQuiz(getTableView().getItems().get(getIndex())));
                    }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : box);
                    }
                });
            }

            quizTable.setItems(quizList);
            quizTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, nw) -> { selectedQuiz = nw; updateQuizDetailPanel(nw); });
        }

        // Listener pour afficher le résumé de la période
        if (quizDateDebutPicker != null && quizDateFinPicker != null) {
            quizDateDebutPicker.valueProperty().addListener((obs, ov, nv) -> updateDateRangeInfo());
            quizDateFinPicker.valueProperty().addListener((obs, ov, nv) -> updateDateRangeInfo());
        }

        loadQuizListTeacher();
    }

    private void initStudent() {
        setText(sectionTitleLabel,    "Espace Quiz");
        setText(sectionSubtitleLabel, "Choisissez un quiz et répondez aux questions.");
        if (resultatsTable != null) {
            setupColumn(resQuizTitleColumn, "titre");
            setupColumn(resDateColumn,      "dateCreation");
        }
        showQuizListPage();
    }

    // ── NAVIGATION ────────────────────────────────────────────────
    private void showOnly(VBox target) {
        VBox[] all = { quizListSection, quizDetailSection, quizPassageSection,
                quizResultatSection, myResultsSection, quizFormSection,
                questionsSection, questionFormSection, reponsesSection };
        for (VBox s : all)
            if (s != null) { s.setVisible(false); s.setManaged(false); }
        if (target != null) { target.setVisible(true); target.setManaged(true); }
    }

    // ── ACTIONS COMMUNES ──────────────────────────────────────────
    @FXML
    private void handleLogout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    // ── ACTIONS STUDENT ───────────────────────────────────────────
    @FXML
    public void showQuizListPage() {
        stopTimer();
        showOnly(quizListSection);
        setText(sectionTitleLabel,    "Espace Quiz");
        setText(sectionSubtitleLabel, "Choisissez un quiz et répondez aux questions.");
        if (!isTeacher) loadAvailableQuizzes();
        else            loadQuizListTeacher();
    }

    @FXML
    private void showMyResultsPage() {
        showOnly(myResultsSection);
        setText(sectionTitleLabel, "Mes résultats");
        loadMyResults();
    }

    @FXML
    private void startQuiz() {
        showOnly(quizPassageSection);
        setText(sectionTitleLabel, "Quiz en cours");
        currentQuestionIndex = 0;
        loadQuestion(0);
        startTimer(30 * 60);
    }

    @FXML private void nextQuestion()     { saveCurrentAnswer(); if (currentQuestionIndex < totalQuestions - 1) loadQuestion(++currentQuestionIndex); }
    @FXML private void previousQuestion() { saveCurrentAnswer(); if (currentQuestionIndex > 0) loadQuestion(--currentQuestionIndex); }

    @FXML
    private void submitQuiz() {
        stopTimer();
        showOnly(quizResultatSection);
        setText(sectionTitleLabel, "Résultat du quiz");
        displayResult(75, 15, 20, "Bien");
        // TODO : calculer le vrai score + ResultatService.save(...)
    }

    // ── ACTIONS TEACHER — Quiz CRUD ───────────────────────────────

    @FXML
    public void showQuizCreateForm() {
        editingQuiz = null;
        showOnly(quizFormSection);
        setText(sectionTitleLabel,  "Nouveau quiz");
        setText(quizFormTitleLabel, "Nouveau quiz");
        clearQuizForm();
    }

    private void startEditQuiz(Quiz quiz) {
        editingQuiz = quiz;
        showOnly(quizFormSection);
        setText(sectionTitleLabel,  "Modifier le quiz");
        setText(quizFormTitleLabel, "Modifier le quiz");
        populateQuizForm(quiz);
    }

    @FXML
    private void editSelectedQuiz() {
        if (selectedQuiz == null) { setStatus(quizListMessageLabel, "Sélectionnez un quiz à modifier.", true); return; }
        startEditQuiz(selectedQuiz);
    }

    @FXML
    private void deleteSelectedQuiz() {
        if (selectedQuiz == null) { setStatus(quizListMessageLabel, "Sélectionnez un quiz à supprimer.", true); return; }
        confirmDeleteQuiz(selectedQuiz);
    }

    private void confirmDeleteQuiz(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le quiz « " + quiz.getTitre() + " » ?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        alert.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                quizService.delete(quiz.getId().intValue());
                selectedQuiz = null;
                updateQuizDetailPanel(null);
                setStatus(quizListMessageLabel, "Quiz supprimé.", false);
                loadQuizListTeacher();
            } catch (Exception e) {
                setStatus(quizListMessageLabel, "Suppression impossible : " + e.getMessage(), true);
            }
        });
    }

    @FXML
    private void closeQuizDetails() {
        if (quizDetailsPanel != null) { quizDetailsPanel.setVisible(false); quizDetailsPanel.setManaged(false); }
        selectedQuiz = null;
        if (quizTable != null) quizTable.getSelectionModel().clearSelection();
    }

    /**
     * SAUVEGARDE DU QUIZ EN BASE.
     * Lit le formulaire, construit l'entité Quiz, appelle quizService.create() ou update().
     */
    @FXML
    private void saveQuiz() {
        if (!validateQuizForm()) return;

        try {
            // Réutiliser l'entité existante (update) ou en créer une nouvelle (create)
            Quiz quiz = (editingQuiz != null) ? editingQuiz : new Quiz();

            // ── Lecture du formulaire ──────────────────────────────
            quiz.setTitre(quizTitreField.getText().trim());

            if (quizTypeCombo != null && quizTypeCombo.getValue() != null)
                quiz.setTypeQuiz(quizTypeCombo.getValue());

            if (quizCorrectionApresCombo != null && quizCorrectionApresCombo.getValue() != null)
                quiz.setAfficherCorrectionApres(quizCorrectionApresCombo.getValue());

            if (quizDureeSpinner != null && quizDureeSpinner.getValue() != null)
                quiz.setDureeMinutes(quizDureeSpinner.getValue());

            if (quizTentativesSpinner != null && quizTentativesSpinner.getValue() != null)
                quiz.setNombreTentativesAutorisees(quizTentativesSpinner.getValue());

            if (quizDateDebutPicker != null && quizDateDebutPicker.getValue() != null)
                quiz.setDateDebutDisponibilite(quizDateDebutPicker.getValue().atStartOfDay());

            if (quizDateFinPicker != null && quizDateFinPicker.getValue() != null)
                quiz.setDateFinDisponibilite(quizDateFinPicker.getValue().atTime(23, 59));

            if (quizIdCoursField != null && !quizIdCoursField.getText().trim().isEmpty()) {
                try { quiz.setIdCours(Integer.parseInt(quizIdCoursField.getText().trim())); }
                catch (NumberFormatException ignored) { /* champ optionnel */ }
            }

            if (quizInstructionsArea != null)
                quiz.setInstructions(quizInstructionsArea.getText().trim());

            if (quizDescriptionArea != null)
                quiz.setDescription(quizDescriptionArea.getText().trim());

            // Lier le créateur si c'est une création
            Utilisateur currentUser = UserSession.getCurrentUser();
            if (currentUser != null && quiz.getCreateur() == null)
                quiz.setCreateur(currentUser);

            // ── Appel service ─────────────────────────────────────
            if (editingQuiz == null) {
                quizService.create(quiz);                    // INSERT en base
                setStatus(quizFormStatusLabel, "Quiz créé avec succès ✔", false);
            } else {
                quizService.update(quiz);                    // UPDATE en base
                setStatus(quizFormStatusLabel, "Quiz modifié avec succès ✔", false);
            }

            editingQuiz = null;
            loadQuizListTeacher();
            showOnly(quizListSection);

        } catch (Exception e) {
            setStatus(quizFormStatusLabel, "Erreur lors de l'enregistrement : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelQuizForm() {
        editingQuiz = null;
        showQuizListPage();
    }

    // ── ACTIONS TEACHER — Questions ───────────────────────────────
    @FXML
    private void manageQuestionsOfSelectedQuiz() {
        if (selectedQuiz == null) { setStatus(quizListMessageLabel, "Sélectionnez un quiz d'abord.", true); return; }
        openQuestionsForQuiz(selectedQuiz);
    }

    private void openQuestionsForQuiz(Quiz quiz) {
        currentQuizId = quiz.getId().intValue();
        showOnly(questionsSection);
        setText(sectionTitleLabel,          "Questions du quiz");
        setText(questionsSectionTitleLabel, quiz.getTitre());
        loadQuestionsOfQuiz(currentQuizId);
    }

    @FXML private void showQuestionCreateForm() {
        showOnly(questionFormSection);
        setText(sectionTitleLabel,      "Nouvelle question");
        setText(questionFormTitleLabel, "Nouvelle question");
        clearQuestionForm();
    }

    @FXML private void editSelectedQuestion()   { showOnly(questionFormSection); setText(questionFormTitleLabel, "Modifier la question"); }
    @FXML private void deleteSelectedQuestion() { setStatus(questionsMessageLabel, "Question supprimée.", false); loadQuestionsOfQuiz(currentQuizId); }
    @FXML private void closeQuestionDetails()   { if (questionDetailsPanel != null) { questionDetailsPanel.setVisible(false); questionDetailsPanel.setManaged(false); } }

    @FXML
    private void saveQuestion() {
        if (!validateQuestionForm()) return;
        // TODO : QuestionService.create/update
        setStatus(questionFormStatusLabel, "Question enregistrée.", false);
        showOnly(questionsSection);
        loadQuestionsOfQuiz(currentQuizId);
    }

    @FXML private void cancelQuestionForm() { showOnly(questionsSection); }

    // ── ACTIONS TEACHER — Réponses ────────────────────────────────
    @FXML private void manageReponsesOfSelectedQuestion()  { showOnly(reponsesSection); loadReponsesOfQuestion(currentQuestionId); }
    @FXML private void backToQuestionsFromReponses()       { showOnly(questionsSection); loadQuestionsOfQuiz(currentQuizId); }
    @FXML private void showReponseCreateForm()             { setText(reponseFormTitleLabel, "Nouvelle réponse"); clearReponseForm(); showPanel(reponseFormPanel); }
    @FXML private void closeReponseForm()                  { hidePanel(reponseFormPanel); }

    @FXML
    private void saveReponse() {
        if (!validateReponseForm()) return;
        // TODO : ReponseService.create/update
        setStatus(reponseFormStatusLabel, "Réponse enregistrée.", false);
        hidePanel(reponseFormPanel);
        loadReponsesOfQuestion(currentQuestionId);
    }

    // ── CHARGEMENT DES DONNÉES ────────────────────────────────────
    private void loadAvailableQuizzes() {
        if (quizCardsContainer != null) quizCardsContainer.getChildren().clear();
        try {
            List<Quiz> list = quizService.getQuizzesDisponibles();
            setText(quizCountLabel, String.valueOf(list.size()));
            setText(quizListMessageLabel, list.isEmpty() ? "Aucun quiz disponible." : "");
            // TODO : construire les cards et les ajouter dans quizCardsContainer
        } catch (Exception e) {
            setText(quizListMessageLabel, "Chargement impossible : " + e.getMessage());
        }
    }

    /** Recharge la liste des quiz de l'enseignant connecté depuis la base. */
    private void loadQuizListTeacher() {
        if (quizTable == null) return;
        quizList.clear();
        setText(quizListMessageLabel, "");
        try {
            Utilisateur currentUser = UserSession.getCurrentUser();
            List<Quiz> list = (currentUser != null)
                    ? quizService.getByCreateur(currentUser.getId().intValue())
                    : quizService.getAll();
            quizList.setAll(list);
            if (list.isEmpty()) setText(quizListMessageLabel, "Aucun quiz créé pour le moment.");
        } catch (Exception e) {
            setText(quizListMessageLabel, "Chargement impossible : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMyResults()                { setText(totalTentativesLabel, "0"); setText(myResultsMessageLabel, ""); }
    private void loadQuestionsOfQuiz(int qzId)  { if (questionsTable != null) questionsTable.getItems().clear(); setText(questionsMessageLabel, ""); }
    private void loadReponsesOfQuestion(int qId){ if (reponsesTable  != null) reponsesTable.getItems().clear();  setText(reponsesMessageLabel,  ""); }

    private void loadQuestion(int index) {
        setText(questionIndexLabel, "Question " + (index + 1) + " / " + totalQuestions);
        double progress = totalQuestions > 0 ? (double)(index + 1) / totalQuestions : 0;
        if (quizProgressBar      != null) quizProgressBar.setProgress(progress);
        if (progressPercentLabel != null) progressPercentLabel.setText(String.format("%.0f%%", progress * 100));
        if (previousQuestionButton != null) { previousQuestionButton.setVisible(index > 0); previousQuestionButton.setManaged(index > 0); }
        boolean isLast = (index == totalQuestions - 1);
        if (nextQuestionButton != null) { nextQuestionButton.setVisible(!isLast); nextQuestionButton.setManaged(!isLast); }
        if (submitQuizButton   != null) { submitQuizButton.setVisible(isLast);   submitQuizButton.setManaged(isLast); }
    }

    private void saveCurrentAnswer() { /* TODO */ }

    private void displayResult(double pct, int obtenus, int total, String mention) {
        setText(resultatScoreLabel,   String.format("%.0f%%", pct));
        setText(resultatPointsLabel,  obtenus + " / " + total);
        setText(resultatMentionLabel, mention);
        if (resultatProgressBar != null) resultatProgressBar.setProgress(pct / 100.0);
    }

    /** Pré-remplit le formulaire en mode édition. */
    private void populateQuizForm(Quiz quiz) {
        clearQuizForm();
        if (quizTitreField           != null) quizTitreField.setText(safe(quiz.getTitre()));
        if (quizTypeCombo            != null) quizTypeCombo.setValue(quiz.getTypeQuiz());
        if (quizCorrectionApresCombo != null) quizCorrectionApresCombo.setValue(quiz.getAfficherCorrectionApres());
        if (quizDureeSpinner      != null && quiz.getDureeMinutes() != null)
            quizDureeSpinner.getValueFactory().setValue(quiz.getDureeMinutes());
        if (quizTentativesSpinner != null && quiz.getNombreTentativesAutorisees() != null)
            quizTentativesSpinner.getValueFactory().setValue(quiz.getNombreTentativesAutorisees());
        if (quizDateDebutPicker   != null && quiz.getDateDebutDisponibilite() != null)
            quizDateDebutPicker.setValue(quiz.getDateDebutDisponibilite().toLocalDate());
        if (quizDateFinPicker     != null && quiz.getDateFinDisponibilite() != null)
            quizDateFinPicker.setValue(quiz.getDateFinDisponibilite().toLocalDate());
        if (quizIdCoursField      != null && quiz.getIdCours() != null)
            quizIdCoursField.setText(String.valueOf(quiz.getIdCours()));
        if (quizInstructionsArea  != null) quizInstructionsArea.setText(safe(quiz.getInstructions()));
        if (quizDescriptionArea   != null) quizDescriptionArea.setText(safe(quiz.getDescription()));
    }

    /** Affiche les infos du quiz sélectionné dans le panneau latéral. */
    private void updateQuizDetailPanel(Quiz quiz) {
        if (quizDetailsPanel == null) return;
        boolean v = quiz != null;
        quizDetailsPanel.setVisible(v); quizDetailsPanel.setManaged(v);
        if (!v) return;
        setText(detailQuizTitleLabel,        quiz.getTitre());
        setText(detailQuizTypeLabel,         safe(quiz.getTypeQuiz()));
        setText(detailQuizDescLabel,         safe(quiz.getDescription()));
        setText(detailQuizDureeLabel,        quiz.getDureeMinutes() != null ? quiz.getDureeMinutes() + " min" : "-");
        setText(detailQuizTentativesLabel,   quiz.getNombreTentativesAutorisees() != null ? String.valueOf(quiz.getNombreTentativesAutorisees()) : "-");
        setText(detailQuizCorrectionLabel,   safe(quiz.getAfficherCorrectionApres()));
        setText(detailQuizInstructionsLabel, safe(quiz.getInstructions()));
        setText(detailQuizDispoLabel,        quiz.getDateFinDisponibilite() != null
                ? quiz.getDateFinDisponibilite().toLocalDate().toString() : "-");
    }

    /** Met à jour le label de résumé de la période de disponibilité. */
    private void updateDateRangeInfo() {
        if (quizDateRangeInfoLabel == null) return;
        LocalDate d = quizDateDebutPicker != null ? quizDateDebutPicker.getValue() : null;
        LocalDate f = quizDateFinPicker   != null ? quizDateFinPicker.getValue()   : null;
        if (d == null && f == null) {
            setText(quizDateRangeInfoLabel, "Sélectionnez les deux dates pour voir la période.");
            quizDateRangeInfoLabel.setStyle("-fx-text-fill:#64748b;");
        } else if (d != null && f == null) {
            setText(quizDateRangeInfoLabel, "Ouverture le " + d + " — date de clôture manquante.");
            quizDateRangeInfoLabel.setStyle("-fx-text-fill:#b45309;");
        } else if (d == null) {
            setText(quizDateRangeInfoLabel, "Clôture le " + f + " — date d'ouverture manquante.");
            quizDateRangeInfoLabel.setStyle("-fx-text-fill:#b45309;");
        } else if (!f.isAfter(d)) {
            setText(quizDateRangeInfoLabel, "⚠ La date de clôture doit être après la date d'ouverture !");
            quizDateRangeInfoLabel.setStyle("-fx-text-fill:#dc2626; -fx-font-weight:700;");
        } else {
            long days = d.until(f).getDays() + d.until(f).getMonths() * 30L + d.until(f).getYears() * 365L;
            setText(quizDateRangeInfoLabel, "✅ Quiz disponible du " + d + " au " + f + " (" + days + " jours).");
            quizDateRangeInfoLabel.setStyle("-fx-text-fill:#16a34a; -fx-font-weight:700;");
        }
    }

    // ── TIMER ─────────────────────────────────────────────────────
    private void startTimer(int seconds) {
        stopTimer();
        remainingSeconds = seconds;
        updateTimerLabel();
        quizTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) { stopTimer(); submitQuiz(); }
        }));
        quizTimer.setCycleCount(Timeline.INDEFINITE);
        quizTimer.play();
    }

    private void stopTimer() { if (quizTimer != null) { quizTimer.stop(); quizTimer = null; } }
    private void updateTimerLabel() {
        if (timerLabel != null)
            timerLabel.setText(String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60));
    }

    // ── VALIDATION ────────────────────────────────────────────────
    private boolean validateQuizForm() {
        boolean ok = true;
        clearQuizFormErrors();

        // Titre obligatoire
        if (quizTitreField != null && quizTitreField.getText().trim().isEmpty()) {
            setText(quizTitreErrorLabel, "Le titre est obligatoire."); ok = false;
        }
        // Type obligatoire
        if (quizTypeCombo != null && quizTypeCombo.getValue() == null) {
            setText(quizTypeErrorLabel, "Veuillez choisir un type."); ok = false;
        }
        // Correction obligatoire
        if (quizCorrectionApresCombo != null && quizCorrectionApresCombo.getValue() == null) {
            setText(quizCorrectionErrorLabel, "Choisissez le moment de correction."); ok = false;
        }
        // Date début obligatoire
        if (quizDateDebutPicker != null && quizDateDebutPicker.getValue() == null) {
            setText(quizDateDebutErrorLabel, "La date d'ouverture est obligatoire."); ok = false;
        }
        // Date fin obligatoire
        if (quizDateFinPicker != null && quizDateFinPicker.getValue() == null) {
            setText(quizDateFinErrorLabel, "La date de clôture est obligatoire."); ok = false;
        }
        // Date fin > date début
        if (quizDateDebutPicker != null && quizDateFinPicker != null) {
            LocalDate d = quizDateDebutPicker.getValue();
            LocalDate f = quizDateFinPicker.getValue();
            if (d != null && f != null) {
                if (!f.isAfter(d)) {
                    setText(quizDateFinErrorLabel, "La date de clôture doit être strictement après la date d'ouverture."); ok = false;
                }
            }
        }
        return ok;
    }

    private boolean validateQuestionForm() {
        boolean ok = true;
        clearQuestionFormErrors();
        if (questionTexteArea != null && questionTexteArea.getText().trim().isEmpty()) { setText(questionTexteErrorLabel, "L'énoncé est obligatoire."); ok = false; }
        if (questionTypeCombo != null && questionTypeCombo.getValue() == null)         { setText(questionTypeErrorLabel,  "Veuillez choisir un type."); ok = false; }
        return ok;
    }

    private boolean validateReponseForm() {
        setText(reponseTexteErrorLabel, "");
        if (reponseTexteArea != null && reponseTexteArea.getText().trim().isEmpty()) {
            setText(reponseTexteErrorLabel, "Le texte de la réponse est obligatoire."); return false;
        }
        return true;
    }

    private void clearQuizFormErrors() {
        setText(quizTitreErrorLabel,""); setText(quizTypeErrorLabel,""); setText(quizCorrectionErrorLabel,"");
        setText(quizDureeErrorLabel,""); setText(quizTentativesErrorLabel,""); setText(quizDateDebutErrorLabel,"");
        setText(quizDateFinErrorLabel,""); setText(quizIdCoursErrorLabel,""); setText(quizInstructionsErrorLabel,"");
        setText(quizDescriptionErrorLabel,""); setText(quizFormStatusLabel,"");
    }

    private void clearQuizForm() {
        clearQuizFormErrors();
        if (quizTitreField           != null) quizTitreField.clear();
        if (quizTypeCombo            != null) quizTypeCombo.setValue(null);
        if (quizCorrectionApresCombo != null) quizCorrectionApresCombo.setValue(null);
        if (quizDateDebutPicker      != null) quizDateDebutPicker.setValue(null);
        if (quizDateFinPicker        != null) quizDateFinPicker.setValue(null);
        if (quizIdCoursField         != null) quizIdCoursField.clear();
        if (quizInstructionsArea     != null) quizInstructionsArea.clear();
        if (quizDescriptionArea      != null) quizDescriptionArea.clear();
        if (quizDateRangeInfoLabel   != null) setText(quizDateRangeInfoLabel, "Sélectionnez les deux dates pour voir la période.");
    }

    private void clearQuestionFormErrors() {
        setText(questionTexteErrorLabel,""); setText(questionTypeErrorLabel,"");
        setText(questionPointsErrorLabel,""); setText(questionExplicationErrorLabel,""); setText(questionFormStatusLabel,"");
    }

    private void clearQuestionForm() {
        clearQuestionFormErrors();
        if (questionTexteArea       != null) questionTexteArea.clear();
        if (questionTypeCombo       != null) questionTypeCombo.setValue(null);
        if (questionExplicationArea != null) questionExplicationArea.clear();
    }

    private void clearReponseForm() {
        setText(reponseTexteErrorLabel,""); setText(reponseOrdreErrorLabel,"");
        setText(reponseFeedbackErrorLabel,""); setText(reponseFormStatusLabel,"");
        if (reponseTexteArea     != null) reponseTexteArea.clear();
        if (reponseCorrecteCheck != null) reponseCorrecteCheck.setSelected(false);
        if (reponseFeedbackArea  != null) reponseFeedbackArea.clear();
        if (reponseMediaUrlField != null) reponseMediaUrlField.clear();
    }

    // ── UTILITAIRES ───────────────────────────────────────────────
    private void setText(Label l, String t)     { if (l != null) l.setText(t != null ? t : ""); }
    private void showPanel(VBox p)              { if (p != null) { p.setVisible(true);  p.setManaged(true); } }
    private void hidePanel(VBox p)              { if (p != null) { p.setVisible(false); p.setManaged(false); } }
    private String safe(String v)               { return v == null ? "" : v; }

    private void setStatus(Label l, String msg, boolean error) {
        if (l == null) return;
        l.setText(msg);
        l.getStyleClass().removeAll("error-status","neutral-status","success-status");
        l.getStyleClass().add(error ? "error-status" : "success-status");
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void setupColumn(TableColumn col, String prop) {
        if (col != null) col.setCellValueFactory(new PropertyValueFactory<>(prop));
    }

    // ── SETTER public ─────────────────────────────────────────────
    public void setCurrentUser(String nom, String role) {
        setText(currentUserNameLabel, nom);
        setText(currentUserRoleLabel, role);
        if (profileMenuButton != null)
            profileMenuButton.setText(nom.length() >= 2 ? nom.substring(0,2).toUpperCase() : nom.toUpperCase());
        isTeacher = "enseignant".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role);
    }
}