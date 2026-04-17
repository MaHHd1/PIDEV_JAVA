package gui;

import entities.Cours;
import entities.Question;
import entities.Reponse;
import entities.Quiz;
import entities.ResultatQuiz;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import services.CoursService;
import services.QuestionService;
import services.QuizService;
import services.ReponseService;
import services.ResultatQuizService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class QuizController implements Initializable {

    // ── TOPBAR ────────────────────────────────────────────────────
    @FXML private Label      sectionTitleLabel;
    @FXML private Label      sectionSubtitleLabel;
    @FXML private Label      currentUserNameLabel;
    @FXML private Label      currentUserRoleLabel;
    @FXML private MenuButton profileMenuButton;

    // ── PAGE 1 : LISTE QUIZ ───────────────────────────────────────
    @FXML private VBox  quizListSection;
    @FXML private Label quizListSubtitleLabel;
    @FXML private Label quizListMessageLabel;

    @FXML private TableView<Quiz>   quizTable;
    @FXML private TableColumn<?,?> quizTitreColumn;
    @FXML private TableColumn<?,?> quizTypeColumn;
    @FXML private TableColumn<?,?> quizDureeColumn;
    @FXML private TableColumn<?,?> quizQuestionsColumn;
    @FXML private TableColumn<?,?> quizDispoColumn;
    @FXML private TableColumn<?,?> quizActionsColumn;

    @FXML private VBox  quizDetailsPanel;
    @FXML private Label detailQuizTitleLabel;
    @FXML private Label detailQuizMetaLabel;
    @FXML private Label detailQuizTypeLabel;
    @FXML private Label detailQuizDureeLabel;
    @FXML private Label detailQuizTentativesLabel;
    @FXML private Label detailQuizCorrectionLabel;
    @FXML private Label detailQuizDispoLabel;
    @FXML private Label detailQuizDescLabel;
    @FXML private Label detailQuizInstructionsLabel;
    @FXML private Label detailQuizCoursLabel;

    // ── PAGE 2 : FORMULAIRE QUIZ ──────────────────────────────────
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

    @FXML private ComboBox<Cours> quizCoursCombo;
    @FXML private Label           quizCoursErrorLabel;

    @FXML private TextArea   quizInstructionsArea;
    @FXML private Label      quizInstructionsErrorLabel;
    @FXML private TextArea   quizDescriptionArea;
    @FXML private Label      quizDescriptionErrorLabel;
    @FXML private Label      quizFormStatusLabel;
    @FXML private Label      quizDateRangeInfoLabel;

    // ── PAGE 3 : LISTE QUESTIONS ──────────────────────────────────
    @FXML private VBox  questionsSection;
    @FXML private Label questionsSectionTitleLabel;
    @FXML private Label questionsSectionSubtitleLabel;
    @FXML private Label questionsMessageLabel;

    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<?,?>   qOrdreColumn;
    @FXML private TableColumn<?,?>   qTexteColumn;
    @FXML private TableColumn<?,?>   qTypeColumn;
    @FXML private TableColumn<?,?>   qPointsColumn;
    @FXML private TableColumn<?,?>   qReponsesColumn;
    @FXML private TableColumn<?,?>   qActionsColumn;

    @FXML private VBox  questionDetailsPanel;
    @FXML private Label detailQuestionTexteLabel;
    @FXML private Label detailQuestionMetaLabel;
    @FXML private VBox  detailReponsesContainer;
    @FXML private Label detailQuestionExplicationLabel;

    // ── PAGE 4 : FORMULAIRE QUESTION ─────────────────────────────
    @FXML private VBox      questionFormSection;
    @FXML private Label     questionFormTitleLabel;
    @FXML private TextArea  questionTexteArea;
    @FXML private Label     questionTexteErrorLabel;
    @FXML private ComboBox<String> questionTypeCombo;
    @FXML private Label     questionTypeErrorLabel;
    @FXML private Label     questionTypeHintLabel;
    @FXML private Spinner<Integer> questionPointsSpinner;
    @FXML private Label     questionPointsErrorLabel;
    @FXML private Spinner<Integer> questionOrdreSpinner;
    @FXML private TextArea  questionExplicationArea;
    @FXML private Label     questionExplicationErrorLabel;
    @FXML private Label     questionFormStatusLabel;

    // ── Panels dynamiques réponses (PAGE 4) ──────────────────────
    @FXML private VBox        reponsesVraiFauxPanel;
    @FXML private RadioButton vraiFauxVraiRadio;
    @FXML private RadioButton vraiFauxFauxRadio;
    @FXML private Label       vraiFauxErrorLabel;

    @FXML private VBox  reponsesChoixUniquePanel;
    @FXML private VBox  choixUniqueReponsesContainer;
    @FXML private Label choixUniqueErrorLabel;

    @FXML private VBox  reponsesChoixMultiplePanel;
    @FXML private VBox  choixMultipleReponsesContainer;
    @FXML private Label choixMultipleErrorLabel;

    @FXML private VBox     reponsesTexteLibrePanel;
    @FXML private TextArea texteLibreReponseArea;
    @FXML private Label    texteLibreErrorLabel;

    // ── PAGE 5 : RÉPONSES ─────────────────────────────────────────
    @FXML private VBox  reponsesSection;
    @FXML private Label reponsesSectionTitleLabel;
    @FXML private Label reponsesSectionSubtitleLabel;
    @FXML private Label reponsesMessageLabel;

    @FXML private TableView<Reponse>  reponsesTable;
    @FXML private TableColumn<?,?>   rOrdreColumn;
    @FXML private TableColumn<?,?>   rTexteColumn;
    @FXML private TableColumn<?,?>   rCorrecteColumn;
    @FXML private TableColumn<?,?>   rFeedbackColumn;
    @FXML private TableColumn<?,?>   rActionsColumn;

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

    // ── PAGE 6 : RÉSULTATS ────────────────────────────────────────
    @FXML private VBox  resultatsSection;
    @FXML private Label resultatsSectionTitleLabel;
    @FXML private Label resultatsSectionSubtitleLabel;
    @FXML private Label resultatsMessageLabel;

    @FXML private Label statsMoyenneLabel;
    @FXML private Label statsMeilleurLabel;
    @FXML private Label statsPlusBas;
    @FXML private Label statsTotalLabel;

    @FXML private TableView<Object[]>  resultatsTable;
    @FXML private TableColumn<Object[], String>  resNomColumn;
    @FXML private TableColumn<Object[], String>  resPrenomColumn;
    @FXML private TableColumn<Object[], String>  resMatriculeColumn;
    @FXML private TableColumn<Object[], Double>  resScoreColumn;
    @FXML private TableColumn<Object[], String>  resPointsColumn;
    @FXML private TableColumn<Object[], String>  resDateColumn;
    @FXML private TableColumn<Object[], String>  resMentionColumn;

    // ── État interne ──────────────────────────────────────────────
    private Quiz     selectedQuiz     = null;
    private Quiz     editingQuiz      = null;
    private Quiz     currentQuiz      = null;
    private Question selectedQuestion = null;
    private Question editingQuestion  = null;
    private Reponse  editingReponse   = null;

    private final ToggleGroup choixUniqueGroup = new ToggleGroup();

    private final QuizService          quizService          = new QuizService();
    private final QuestionService      questionService      = new QuestionService();
    private final ReponseService       reponseService       = new ReponseService();
    private final CoursService         coursService         = new CoursService();
    private final ResultatQuizService  resultatQuizService  = new ResultatQuizService();

    private final ObservableList<Quiz>     quizList     = FXCollections.observableArrayList();
    private final ObservableList<Question> questionList = FXCollections.observableArrayList();
    private final ObservableList<Reponse>  reponseList  = FXCollections.observableArrayList();
    private final ObservableList<Object[]> resultatList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ═════════════════════════════════════════════════════════════
    // INITIALISATION
    // ═════════════════════════════════════════════════════════════
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {

        // ComboBox quiz
        if (quizTypeCombo != null)
            quizTypeCombo.setItems(FXCollections.observableArrayList(
                    "QCM", "Vrai/Faux", "Texte libre", "Mixte"));
        if (quizCorrectionApresCombo != null)
            quizCorrectionApresCombo.setItems(FXCollections.observableArrayList(
                    "Immédiate", "Après soumission", "Manuelle"));

        // ComboBox cours
        if (quizCoursCombo != null) {
            quizCoursCombo.setConverter(new StringConverter<Cours>() {
                @Override
                public String toString(Cours c) {
                    if (c == null) return "— Aucun cours —";
                    String code = (c.getCodeCours() != null && !c.getCodeCours().isEmpty())
                            ? "[" + c.getCodeCours() + "] " : "";
                    return code + c.getTitre();
                }
                @Override public Cours fromString(String s) { return null; }
            });
            loadCoursList();
        }

        // ComboBox question
        if (questionTypeCombo != null) {
            questionTypeCombo.setItems(FXCollections.observableArrayList(
                    "choix_unique", "choix_multiple", "vrai_faux", "texte_libre"));
            questionTypeCombo.valueProperty().addListener((obs, old, nv) -> onTypeChanged(nv));
        }

        // TableView quiz
        if (quizTable != null) {
            setupColumn(quizTitreColumn, "titre");
            setupColumn(quizTypeColumn,  "typeQuiz");
            setupColumn(quizDureeColumn, "dureeMinutes");
            setupColumn(quizDispoColumn, "dateFinDisponibilite");

            if (quizActionsColumn != null) {
                ((TableColumn<Quiz, Void>) quizActionsColumn).setCellFactory(col -> new TableCell<>() {
                    private final Button btnEdit  = new Button("✏ Modifier");
                    private final Button btnQuest = new Button("❓ Questions");
                    private final Button btnRes   = new Button("📊 Résultats");
                    private final Button btnDel   = new Button("🗑 Supprimer");
                    private final HBox   box      = new HBox(6, btnEdit, btnQuest, btnRes, btnDel);
                    {
                        btnEdit .getStyleClass().addAll("secondary-button", "table-action-button");
                        btnQuest.getStyleClass().addAll("primary-button",   "table-action-button");
                        btnRes  .getStyleClass().addAll("secondary-button", "table-action-button");
                        btnDel  .getStyleClass().addAll("danger-button",    "table-action-button");
                        btnEdit .setOnAction(e -> startEditQuiz(getTableView().getItems().get(getIndex())));
                        btnQuest.setOnAction(e -> openQuestionsPage(getTableView().getItems().get(getIndex())));
                        btnRes  .setOnAction(e -> openResultatsPage(getTableView().getItems().get(getIndex())));
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

        // TableView questions
        if (questionsTable != null) {
            setupColumn(qOrdreColumn,  "ordreAffichage");
            setupColumn(qTexteColumn,  "texte");
            setupColumn(qTypeColumn,   "typeQuestion");
            setupColumn(qPointsColumn, "points");

            if (qActionsColumn != null) {
                ((TableColumn<Question, Void>) qActionsColumn).setCellFactory(col -> new TableCell<>() {
                    private final Button btnEdit = new Button("✏ Modifier");
                    private final Button btnRep  = new Button("💬 Réponses");
                    private final Button btnDel  = new Button("🗑 Supprimer");
                    private final HBox   box     = new HBox(6, btnEdit, btnRep, btnDel);
                    {
                        btnEdit.getStyleClass().addAll("secondary-button", "table-action-button");
                        btnRep .getStyleClass().addAll("primary-button",   "table-action-button");
                        btnDel .getStyleClass().addAll("danger-button",    "table-action-button");
                        btnEdit.setOnAction(e -> startEditQuestion(getTableView().getItems().get(getIndex())));
                        btnRep .setOnAction(e -> openReponsesFor(getTableView().getItems().get(getIndex())));
                        btnDel .setOnAction(e -> confirmDeleteQuestion(getTableView().getItems().get(getIndex())));
                    }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : box);
                    }
                });
            }
            questionsTable.setItems(questionList);
            questionsTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, nw) -> { selectedQuestion = nw; updateQuestionDetailPanel(nw); });
        }

        // TableView réponses
        if (reponsesTable != null) {
            setupColumn(rOrdreColumn,    "ordreAffichage");
            setupColumn(rTexteColumn,    "texteReponse");
            setupColumn(rCorrecteColumn, "estCorrecte");
            setupColumn(rFeedbackColumn, "feedbackSpecifique");

            if (rActionsColumn != null) {
                ((TableColumn<Reponse, Void>) rActionsColumn).setCellFactory(col -> new TableCell<>() {
                    private final Button btnEdit = new Button("✏");
                    private final Button btnDel  = new Button("🗑");
                    private final HBox   box     = new HBox(6, btnEdit, btnDel);
                    {
                        btnEdit.getStyleClass().addAll("secondary-button", "table-action-button");
                        btnDel .getStyleClass().addAll("danger-button",    "table-action-button");
                        btnEdit.setOnAction(e -> startEditReponse(getTableView().getItems().get(getIndex())));
                        btnDel .setOnAction(e -> confirmDeleteReponse(getTableView().getItems().get(getIndex())));
                    }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : box);
                    }
                });
            }
            reponsesTable.setItems(reponseList);
        }

        // TableView résultats
        setupResultatsTable();

        // Listeners dates quiz
        if (quizDateDebutPicker != null)
            quizDateDebutPicker.valueProperty().addListener((obs, ov, nv) -> updateDateRangeInfo());
        if (quizDateFinPicker != null)
            quizDateFinPicker.valueProperty().addListener((obs, ov, nv) -> updateDateRangeInfo());

        // Profil
        Utilisateur u = UserSession.getCurrentUser();
        if (u != null) setCurrentUser(u.getNomComplet(), u.getType());

        // Page initiale
        showOnly(quizListSection);
        setText(sectionTitleLabel,    "Gestion des Quiz");
        setText(sectionSubtitleLabel, "Créez, modifiez et organisez vos quiz et leurs questions.");
        loadQuizList();
    }

    // ═════════════════════════════════════════════════════════════
    // CHARGEMENT DES COURS
    // ═════════════════════════════════════════════════════════════
    private void loadCoursList() {
        if (quizCoursCombo == null) return;
        try {
            List<Cours> cours = coursService.getAll();
            ObservableList<Cours> items = FXCollections.observableArrayList();
            items.add(null);
            items.addAll(cours);
            quizCoursCombo.setItems(items);
        } catch (Exception e) {
            System.err.println("Impossible de charger les cours : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ═════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═════════════════════════════════════════════════════════════
    private void showOnly(VBox target) {
        for (VBox s : new VBox[]{
                quizListSection, quizFormSection,
                questionsSection, questionFormSection,
                reponsesSection, resultatsSection })
            if (s != null) { s.setVisible(false); s.setManaged(false); }
        if (target != null) { target.setVisible(true); target.setManaged(true); }
    }

    // ═════════════════════════════════════════════════════════════
    // TOPBAR
    // ═════════════════════════════════════════════════════════════
    @FXML
    private void handleLogout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 1 — LISTE QUIZ
    // ═════════════════════════════════════════════════════════════
    @FXML
    public void showQuizListPage() {
        currentQuiz = null;
        showOnly(quizListSection);
        setText(sectionTitleLabel,    "Gestion des Quiz");
        setText(sectionSubtitleLabel, "Créez, modifiez et organisez vos quiz et leurs questions.");
        loadQuizList();
    }

    @FXML private void closeQuizDetails() {
        if (quizDetailsPanel != null) { quizDetailsPanel.setVisible(false); quizDetailsPanel.setManaged(false); }
        selectedQuiz = null;
        if (quizTable != null) quizTable.getSelectionModel().clearSelection();
    }

    @FXML private void editSelectedQuiz() {
        if (selectedQuiz == null) { setStatus(quizListMessageLabel, "Sélectionnez un quiz à modifier.", true); return; }
        startEditQuiz(selectedQuiz);
    }

    @FXML private void deleteSelectedQuiz() {
        if (selectedQuiz == null) { setStatus(quizListMessageLabel, "Sélectionnez un quiz à supprimer.", true); return; }
        confirmDeleteQuiz(selectedQuiz);
    }

    @FXML private void manageQuestionsOfSelectedQuiz() {
        if (selectedQuiz == null) { setStatus(quizListMessageLabel, "Sélectionnez d'abord un quiz.", true); return; }
        openQuestionsPage(selectedQuiz);
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 2 — FORMULAIRE QUIZ
    // ═════════════════════════════════════════════════════════════
    @FXML
    public void showQuizCreateForm() {
        editingQuiz = null;
        showOnly(quizFormSection);
        setText(sectionTitleLabel,  "Nouveau quiz");
        setText(quizFormTitleLabel, "Nouveau quiz");
        loadCoursList();
        clearQuizForm();
    }

    @FXML private void cancelQuizForm() {
        editingQuiz = null;
        showQuizListPage();
    }

    @FXML private void saveQuiz() {
        if (!validateQuizForm()) return;
        try {
            Quiz quiz = (editingQuiz != null) ? editingQuiz : new Quiz();
            quiz.setTitre(quizTitreField.getText().trim());
            if (quizTypeCombo.getValue() != null)            quiz.setTypeQuiz(quizTypeCombo.getValue());
            if (quizCorrectionApresCombo.getValue() != null) quiz.setAfficherCorrectionApres(quizCorrectionApresCombo.getValue());
            if (quizDureeSpinner.getValue() != null)         quiz.setDureeMinutes(quizDureeSpinner.getValue());
            if (quizTentativesSpinner.getValue() != null)    quiz.setNombreTentativesAutorisees(quizTentativesSpinner.getValue());
            if (quizDateDebutPicker.getValue() != null)      quiz.setDateDebutDisponibilite(quizDateDebutPicker.getValue().atStartOfDay());
            if (quizDateFinPicker.getValue() != null)        quiz.setDateFinDisponibilite(quizDateFinPicker.getValue().atTime(23, 59));

            if (quizCoursCombo != null) {
                Cours coursSelectionne = quizCoursCombo.getValue();
                quiz.setIdCours(coursSelectionne != null ? coursSelectionne.getId() : null);
            }

            if (quizInstructionsArea != null) quiz.setInstructions(quizInstructionsArea.getText().trim());
            if (quizDescriptionArea  != null) quiz.setDescription(quizDescriptionArea.getText().trim());

            Utilisateur u = UserSession.getCurrentUser();
            if (u != null && quiz.getCreateur() == null) quiz.setCreateur(u);

            if (editingQuiz == null) { quizService.create(quiz); setStatus(quizFormStatusLabel, "Quiz créé ✔", false); }
            else                     { quizService.update(quiz); setStatus(quizFormStatusLabel, "Quiz modifié ✔", false); }

            editingQuiz = null;
            loadQuizList();
            showOnly(quizListSection);
            setText(sectionTitleLabel,    "Gestion des Quiz");
            setText(sectionSubtitleLabel, "Créez, modifiez et organisez vos quiz et leurs questions.");

        } catch (Exception e) {
            setStatus(quizFormStatusLabel, "Erreur (" + e.getClass().getSimpleName() + ") : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 3 — LISTE QUESTIONS
    // ═════════════════════════════════════════════════════════════
    private void openQuestionsPage(Quiz quiz) {
        currentQuiz = quiz;
        selectedQuestion = null;
        showOnly(questionsSection);
        setText(sectionTitleLabel,             "Questions — " + quiz.getTitre());
        setText(sectionSubtitleLabel,          "Gérez les questions de ce quiz.");
        setText(questionsSectionTitleLabel,    quiz.getTitre());
        setText(questionsSectionSubtitleLabel, "Ajoutez, modifiez ou supprimez les questions.");
        if (questionDetailsPanel != null) { questionDetailsPanel.setVisible(false); questionDetailsPanel.setManaged(false); }
        loadQuestions();
    }

    @FXML private void closeQuestionDetails() {
        if (questionDetailsPanel != null) { questionDetailsPanel.setVisible(false); questionDetailsPanel.setManaged(false); }
        selectedQuestion = null;
        if (questionsTable != null) questionsTable.getSelectionModel().clearSelection();
    }

    @FXML private void editSelectedQuestion() {
        if (selectedQuestion == null) { setStatus(questionsMessageLabel, "Sélectionnez une question.", true); return; }
        startEditQuestion(selectedQuestion);
    }

    @FXML private void deleteSelectedQuestion() {
        if (selectedQuestion == null) { setStatus(questionsMessageLabel, "Sélectionnez une question.", true); return; }
        confirmDeleteQuestion(selectedQuestion);
    }

    @FXML private void manageReponsesOfSelectedQuestion() {
        if (selectedQuestion == null) { setStatus(questionsMessageLabel, "Sélectionnez une question.", true); return; }
        openReponsesFor(selectedQuestion);
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 4 — FORMULAIRE QUESTION
    // ═════════════════════════════════════════════════════════════
    @FXML
    public void showQuestionCreateForm() {
        if (currentQuiz == null) {
            setStatus(questionsMessageLabel, "Erreur : aucun quiz sélectionné.", true);
            return;
        }
        editingQuestion = null;
        showOnly(questionFormSection);
        setText(sectionTitleLabel,      "Nouvelle question — " + currentQuiz.getTitre());
        setText(questionFormTitleLabel, "Nouvelle question");
        clearQuestionForm();
    }

    @FXML private void cancelQuestionForm() {
        editingQuestion = null;
        if (currentQuiz != null) openQuestionsPage(currentQuiz);
        else showQuizListPage();
    }

    @FXML private void saveQuestion() {
        if (currentQuiz == null) {
            setStatus(questionFormStatusLabel, "Erreur : aucun quiz associé.", true);
            return;
        }
        if (!validateQuestionForm()) return;
        try {
            boolean isNew = (editingQuestion == null);
            Question question = isNew ? new Question() : editingQuestion;

            question.setTexte(questionTexteArea.getText().trim());
            String type = questionTypeCombo.getValue();
            question.setTypeQuestion(type);
            if (questionPointsSpinner.getValue() != null)
                question.setPoints(questionPointsSpinner.getValue());
            if (questionOrdreSpinner != null && questionOrdreSpinner.getValue() != null)
                question.setOrdreAffichage(questionOrdreSpinner.getValue());
            if (questionExplicationArea != null)
                question.setExplicationReponse(questionExplicationArea.getText().trim());
            question.setQuiz(currentQuiz);

            Utilisateur u = UserSession.getCurrentUser();
            if (u != null && question.getCreateur() == null) question.setCreateur(u);

            if (isNew) {
                questionService.create(question);
                saveReponsesInline(question);
            } else {
                questionService.update(question);
                List<Reponse> old = reponseService.getByQuestion(question.getId());
                for (Reponse r : old) reponseService.delete(r.getId().intValue());
                saveReponsesInline(question);
            }

            editingQuestion = null;
            openQuestionsPage(currentQuiz);

        } catch (Exception e) {
            setStatus(questionFormStatusLabel,
                    "Erreur (" + e.getClass().getSimpleName() + ") : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ── CHANGEMENT DE TYPE → affiche le bon panel de réponses ────
    private void onTypeChanged(String type) {
        hidePanel(reponsesVraiFauxPanel);
        hidePanel(reponsesChoixUniquePanel);
        hidePanel(reponsesChoixMultiplePanel);
        hidePanel(reponsesTexteLibrePanel);

        if (type == null) { setText(questionTypeHintLabel, ""); return; }

        switch (type) {
            case "vrai_faux" -> {
                showPanel(reponsesVraiFauxPanel);
                setStatus(questionTypeHintLabel, "ℹ  Sélectionnez la bonne réponse ci-dessous.", false);
            }
            case "choix_unique" -> {
                showPanel(reponsesChoixUniquePanel);
                if (choixUniqueReponsesContainer != null
                        && choixUniqueReponsesContainer.getChildren().isEmpty()) {
                    addChoixUniqueRow(); addChoixUniqueRow();
                }
                setStatus(questionTypeHintLabel, "ℹ  Saisissez les réponses et sélectionnez la correcte (●).", false);
            }
            case "choix_multiple" -> {
                showPanel(reponsesChoixMultiplePanel);
                if (choixMultipleReponsesContainer != null
                        && choixMultipleReponsesContainer.getChildren().isEmpty()) {
                    addChoixMultipleRow(); addChoixMultipleRow();
                }
                setStatus(questionTypeHintLabel, "ℹ  Saisissez les réponses et cochez les correctes (☑).", false);
            }
            case "texte_libre" -> {
                showPanel(reponsesTexteLibrePanel);
                setStatus(questionTypeHintLabel, "ℹ  Saisissez la réponse correcte de référence.", false);
            }
            default -> setText(questionTypeHintLabel, "");
        }
    }

    @FXML private void addChoixUniqueRow() {
        if (choixUniqueReponsesContainer == null) return;
        int index = choixUniqueReponsesContainer.getChildren().size() + 1;

        RadioButton radio = new RadioButton();
        radio.setToggleGroup(choixUniqueGroup);

        TextField field = new TextField();
        field.setPromptText("Réponse " + index + "…");
        field.getStyleClass().add("input-field");
        HBox.setHgrow(field, javafx.scene.layout.Priority.ALWAYS);

        Button btnDel = new Button("🗑");
        btnDel.getStyleClass().addAll("danger-button", "compact-button");

        HBox row = new HBox(10, radio, field, btnDel);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btnDel.setOnAction(e -> choixUniqueReponsesContainer.getChildren().remove(row));

        choixUniqueReponsesContainer.getChildren().add(row);
    }

    @FXML private void addChoixMultipleRow() {
        if (choixMultipleReponsesContainer == null) return;
        int index = choixMultipleReponsesContainer.getChildren().size() + 1;

        CheckBox check = new CheckBox();

        TextField field = new TextField();
        field.setPromptText("Réponse " + index + "…");
        field.getStyleClass().add("input-field");
        HBox.setHgrow(field, javafx.scene.layout.Priority.ALWAYS);

        Button btnDel = new Button("🗑");
        btnDel.getStyleClass().addAll("danger-button", "compact-button");

        HBox row = new HBox(10, check, field, btnDel);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btnDel.setOnAction(e -> choixMultipleReponsesContainer.getChildren().remove(row));

        choixMultipleReponsesContainer.getChildren().add(row);
    }

    private void saveReponsesInline(Question question) throws Exception {
        String type = question.getTypeQuestion();
        if (type == null) return;

        switch (type) {
            case "vrai_faux" -> {
                boolean vraiCorrect = vraiFauxVraiRadio != null && vraiFauxVraiRadio.isSelected();
                Reponse vrai = new Reponse();
                vrai.setTexteReponse("Vrai"); vrai.setEstCorrecte(vraiCorrect);
                vrai.setOrdreAffichage(1);    vrai.setQuestion(question);
                reponseService.create(vrai);

                Reponse faux = new Reponse();
                faux.setTexteReponse("Faux"); faux.setEstCorrecte(!vraiCorrect);
                faux.setOrdreAffichage(2);    faux.setQuestion(question);
                reponseService.create(faux);
            }
            case "choix_unique" -> {
                if (choixUniqueReponsesContainer == null) return;
                int ordre = 1;
                for (var node : choixUniqueReponsesContainer.getChildren()) {
                    if (!(node instanceof HBox row)) continue;
                    RadioButton radio = (RadioButton) row.getChildren().get(0);
                    TextField   field = (TextField)   row.getChildren().get(1);
                    String texte = field.getText().trim();
                    if (texte.isEmpty()) continue;
                    Reponse r = new Reponse();
                    r.setTexteReponse(texte); r.setEstCorrecte(radio.isSelected());
                    r.setOrdreAffichage(ordre++); r.setQuestion(question);
                    reponseService.create(r);
                }
            }
            case "choix_multiple" -> {
                if (choixMultipleReponsesContainer == null) return;
                int ordre = 1;
                for (var node : choixMultipleReponsesContainer.getChildren()) {
                    if (!(node instanceof HBox row)) continue;
                    CheckBox  check = (CheckBox)  row.getChildren().get(0);
                    TextField field = (TextField) row.getChildren().get(1);
                    String texte = field.getText().trim();
                    if (texte.isEmpty()) continue;
                    Reponse r = new Reponse();
                    r.setTexteReponse(texte); r.setEstCorrecte(check.isSelected());
                    r.setOrdreAffichage(ordre++); r.setQuestion(question);
                    reponseService.create(r);
                }
            }
            case "texte_libre" -> {
                String texte = texteLibreReponseArea != null
                        ? texteLibreReponseArea.getText().trim() : "";
                if (!texte.isEmpty()) {
                    Reponse r = new Reponse();
                    r.setTexteReponse(texte); r.setEstCorrecte(true);
                    r.setOrdreAffichage(1);   r.setQuestion(question);
                    reponseService.create(r);
                }
            }
        }
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 5 — RÉPONSES
    // ═════════════════════════════════════════════════════════════
    @FXML private void backToQuestionsFromReponses() {
        if (currentQuiz != null) openQuestionsPage(currentQuiz);
        else showQuizListPage();
    }

    @FXML private void showReponseCreateForm() {
        editingReponse = null;
        setText(reponseFormTitleLabel, "Nouvelle réponse");
        clearReponseForm();
        showPanel(reponseFormPanel);
    }

    @FXML private void closeReponseForm() {
        editingReponse = null;
        hidePanel(reponseFormPanel);
    }

    @FXML private void saveReponse() {
        if (!validateReponseForm()) return;
        try {
            Reponse reponse = (editingReponse != null) ? editingReponse : new Reponse();
            reponse.setTexteReponse(reponseTexteArea.getText().trim());
            reponse.setEstCorrecte(reponseCorrecteCheck != null && reponseCorrecteCheck.isSelected());
            if (reponseOrdreSpinner != null && reponseOrdreSpinner.getValue() != null)
                reponse.setOrdreAffichage(reponseOrdreSpinner.getValue());
            if (reponseFeedbackArea  != null) reponse.setFeedbackSpecifique(reponseFeedbackArea.getText().trim());
            if (reponseMediaUrlField != null) reponse.setMediaUrl(reponseMediaUrlField.getText().trim());
            reponse.setQuestion(selectedQuestion);

            if (editingReponse == null) reponseService.create(reponse);
            else                        reponseService.update(reponse);

            setStatus(reponseFormStatusLabel,
                    editingReponse == null ? "Réponse créée ✔" : "Réponse modifiée ✔", false);
            editingReponse = null;
            hidePanel(reponseFormPanel);
            loadReponses(selectedQuestion.getId());

        } catch (Exception e) {
            setStatus(reponseFormStatusLabel, "Erreur : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 6 — RÉSULTATS
    // ═════════════════════════════════════════════════════════════

    /** Configure les colonnes de la TableView résultats avec CellValueFactory personnalisées. */
    private void setupResultatsTable() {
        if (resultatsTable == null) return;

        resNomColumn.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(
                        cd.getValue()[1] != null ? (String) cd.getValue()[1] : "—"));

        resPrenomColumn.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(
                        cd.getValue()[2] != null ? (String) cd.getValue()[2] : "—"));

        resMatriculeColumn.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(
                        cd.getValue()[3] != null ? (String) cd.getValue()[3] : "—"));

        resScoreColumn.setCellValueFactory(cd -> {
            ResultatQuiz r = (ResultatQuiz) cd.getValue()[0];
            return new javafx.beans.property.SimpleObjectProperty<>(r.getScore());
        });
        resScoreColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null); setStyle("");
                } else {
                    setText(String.format("%.1f %%", score));
                    if (score >= 75)
                        setStyle("-fx-text-fill:#16a34a; -fx-font-weight:700;");
                    else if (score >= 60)
                        setStyle("-fx-text-fill:#b45309; -fx-font-weight:700;");
                    else
                        setStyle("-fx-text-fill:#dc2626; -fx-font-weight:700;");
                }
            }
        });

        resPointsColumn.setCellValueFactory(cd -> {
            ResultatQuiz r = (ResultatQuiz) cd.getValue()[0];
            return new javafx.beans.property.SimpleStringProperty(
                    r.getEarnedPoints() + " / " + r.getTotalPoints());
        });

        resDateColumn.setCellValueFactory(cd -> {
            ResultatQuiz r = (ResultatQuiz) cd.getValue()[0];
            String date = r.getDatePassation() != null
                    ? r.getDatePassation().format(DT_FMT) : "—";
            return new javafx.beans.property.SimpleStringProperty(date);
        });

        resMentionColumn.setCellValueFactory(cd -> {
            ResultatQuiz r = (ResultatQuiz) cd.getValue()[0];
            return new javafx.beans.property.SimpleStringProperty(mention(r.getScore()));
        });

        resultatsTable.setItems(resultatList);
    }

    /** Ouvre la page résultats pour un quiz donné. */
    private void openResultatsPage(Quiz quiz) {
        currentQuiz = quiz;
        showOnly(resultatsSection);
        setText(sectionTitleLabel,             "Résultats — " + quiz.getTitre());
        setText(sectionSubtitleLabel,          "Notes et performances des étudiants.");
        setText(resultatsSectionTitleLabel,    quiz.getTitre());
        setText(resultatsSectionSubtitleLabel,
                "Durée : " + (quiz.getDureeMinutes() != null ? quiz.getDureeMinutes() + " min" : "—")
                        + " · Tentatives max : "
                        + (quiz.getNombreTentativesAutorisees() != null
                        ? quiz.getNombreTentativesAutorisees() : "—"));
        loadResultats(quiz);
    }

    /** Action du bouton "← Retour quiz" depuis la page résultats. */
    @FXML
    private void backToQuizListFromResultats() {
        currentQuiz = null;
        showQuizListPage();
    }

    /** Charge les résultats depuis la BD et met à jour la TableView + cartes stats. */
    private void loadResultats(Quiz quiz) {
        resultatList.clear();
        setText(resultatsMessageLabel, "");
        resetStatsLabels();

        try {
            List<Object[]> rows = resultatQuizService.getByQuizWithEtudiant(quiz.getId());

            if (rows.isEmpty()) {
                setText(resultatsMessageLabel, "Aucun étudiant n'a encore passé ce quiz.");
                return;
            }

            resultatList.setAll(rows);

            // Statistiques agrégées
            double[] stats = resultatQuizService.getStatsQuiz(quiz.getId());
            setText(statsMoyenneLabel,  String.format("%.1f %%", stats[0]));
            setText(statsMeilleurLabel, String.format("%.1f %%", stats[1]));
            setText(statsPlusBas,       String.format("%.1f %%", stats[2]));
            setText(statsTotalLabel,    String.valueOf((int) stats[3]));

        } catch (Exception e) {
            setStatus(resultatsMessageLabel, "Chargement impossible : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void resetStatsLabels() {
        setText(statsMoyenneLabel,  "—");
        setText(statsMeilleurLabel, "—");
        setText(statsPlusBas,       "—");
        setText(statsTotalLabel,    "—");
    }

    private static String mention(double score) {
        if (score >= 90) return "🏆 Excellent";
        if (score >= 75) return "✅ Bien";
        if (score >= 60) return "📘 Passable";
        return "❌ Insuffisant";
    }

    // ═════════════════════════════════════════════════════════════
    // CHARGEMENT
    // ═════════════════════════════════════════════════════════════
    private void loadQuizList() {
        if (quizTable == null) return;
        quizList.clear();
        setText(quizListMessageLabel, "");
        try {
            Utilisateur u = UserSession.getCurrentUser();
            List<Quiz> list = (u != null)
                    ? quizService.getByCreateur(u.getId().intValue())
                    : quizService.getAll();
            quizList.setAll(list);
            if (list.isEmpty()) setText(quizListMessageLabel, "Aucun quiz créé pour le moment.");
        } catch (Exception e) {
            setStatus(quizListMessageLabel, "Chargement impossible : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        questionList.clear();
        setText(questionsMessageLabel, "");
        if (currentQuiz == null) return;
        try {
            List<Question> list = questionService.getByQuiz(currentQuiz.getId());
            questionList.setAll(list);
            if (list.isEmpty()) setText(questionsMessageLabel, "Aucune question pour ce quiz.");
        } catch (Exception e) {
            setStatus(questionsMessageLabel, "Chargement impossible : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void loadReponses(long questionId) {
        reponseList.clear();
        setText(reponsesMessageLabel, "");
        try {
            List<Reponse> list = reponseService.getByQuestion(questionId);
            reponseList.setAll(list);
            if (list.isEmpty()) setText(reponsesMessageLabel, "Aucune réponse pour cette question.");
        } catch (Exception e) {
            setStatus(reponsesMessageLabel, "Chargement impossible : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ═════════════════════════════════════════════════════════════
    // HELPERS — QUIZ
    // ═════════════════════════════════════════════════════════════
    private void startEditQuiz(Quiz quiz) {
        editingQuiz = quiz;
        showOnly(quizFormSection);
        setText(sectionTitleLabel,  "Modifier le quiz");
        setText(quizFormTitleLabel, "Modifier le quiz");
        loadCoursList();
        populateQuizForm(quiz);
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
                loadQuizList();
            } catch (Exception e) {
                setStatus(quizListMessageLabel, "Suppression impossible : " + e.getMessage(), true);
            }
        });
    }

    private void updateQuizDetailPanel(Quiz quiz) {
        if (quizDetailsPanel == null) return;
        boolean v = quiz != null;
        quizDetailsPanel.setVisible(v);
        quizDetailsPanel.setManaged(v);
        if (!v) return;
        setText(detailQuizTitleLabel,      quiz.getTitre());
        setText(detailQuizMetaLabel,       safe(quiz.getTypeQuiz()) + " · "
                + (quiz.getDureeMinutes() != null ? quiz.getDureeMinutes() + " min" : "-"));
        setText(detailQuizTypeLabel,       safe(quiz.getTypeQuiz()));
        setText(detailQuizDureeLabel,      quiz.getDureeMinutes() != null ? quiz.getDureeMinutes() + " min" : "-");
        setText(detailQuizTentativesLabel, quiz.getNombreTentativesAutorisees() != null
                ? String.valueOf(quiz.getNombreTentativesAutorisees()) : "-");
        setText(detailQuizCorrectionLabel, safe(quiz.getAfficherCorrectionApres()));
        setText(detailQuizDispoLabel,      quiz.getDateFinDisponibilite() != null
                ? quiz.getDateFinDisponibilite().toLocalDate().toString() : "-");
        setText(detailQuizDescLabel,       safe(quiz.getDescription()));

        if (detailQuizCoursLabel != null) {
            if (quiz.getIdCours() != null) {
                try {
                    Cours cours = coursService.getById(quiz.getIdCours());
                    if (cours != null) {
                        String code = (cours.getCodeCours() != null && !cours.getCodeCours().isEmpty())
                                ? "[" + cours.getCodeCours() + "] " : "";
                        setText(detailQuizCoursLabel, code + cours.getTitre());
                    } else {
                        setText(detailQuizCoursLabel, "ID " + quiz.getIdCours());
                    }
                } catch (Exception e) {
                    setText(detailQuizCoursLabel, "ID " + quiz.getIdCours());
                }
            } else {
                setText(detailQuizCoursLabel, "—");
            }
        }
    }

    private void populateQuizForm(Quiz quiz) {
        clearQuizForm();
        if (quizTitreField           != null) quizTitreField.setText(safe(quiz.getTitre()));
        if (quizTypeCombo            != null) quizTypeCombo.setValue(quiz.getTypeQuiz());
        if (quizCorrectionApresCombo != null) quizCorrectionApresCombo.setValue(quiz.getAfficherCorrectionApres());
        if (quizDureeSpinner         != null && quiz.getDureeMinutes() != null)
            quizDureeSpinner.getValueFactory().setValue(quiz.getDureeMinutes());
        if (quizTentativesSpinner    != null && quiz.getNombreTentativesAutorisees() != null)
            quizTentativesSpinner.getValueFactory().setValue(quiz.getNombreTentativesAutorisees());
        if (quizDateDebutPicker      != null && quiz.getDateDebutDisponibilite() != null)
            quizDateDebutPicker.setValue(quiz.getDateDebutDisponibilite().toLocalDate());
        if (quizDateFinPicker        != null && quiz.getDateFinDisponibilite() != null)
            quizDateFinPicker.setValue(quiz.getDateFinDisponibilite().toLocalDate());
        if (quizInstructionsArea     != null) quizInstructionsArea.setText(safe(quiz.getInstructions()));
        if (quizDescriptionArea      != null) quizDescriptionArea.setText(safe(quiz.getDescription()));

        if (quizCoursCombo != null && quiz.getIdCours() != null) {
            quizCoursCombo.getItems().stream()
                    .filter(c -> c != null && c.getId().equals(quiz.getIdCours()))
                    .findFirst()
                    .ifPresent(c -> quizCoursCombo.setValue(c));
        }
    }

    private void updateDateRangeInfo() {
        if (quizDateRangeInfoLabel == null) return;
        java.time.LocalDate d = quizDateDebutPicker != null ? quizDateDebutPicker.getValue() : null;
        java.time.LocalDate f = quizDateFinPicker   != null ? quizDateFinPicker.getValue()   : null;
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
            long days = d.until(f).getDays()
                    + (long) d.until(f).getMonths() * 30
                    + (long) d.until(f).getYears()  * 365;
            setText(quizDateRangeInfoLabel,
                    "✅ Quiz disponible du " + d + " au " + f + " (" + days + " jours).");
            quizDateRangeInfoLabel.setStyle("-fx-text-fill:#16a34a; -fx-font-weight:700;");
        }
    }

    // ═════════════════════════════════════════════════════════════
    // HELPERS — QUESTIONS
    // ═════════════════════════════════════════════════════════════
    private void startEditQuestion(Question q) {
        editingQuestion = q;
        showOnly(questionFormSection);
        setText(sectionTitleLabel,      "Modifier la question");
        setText(questionFormTitleLabel, "Modifier la question");
        populateQuestionForm(q);
    }

    private void openReponsesFor(Question q) {
        selectedQuestion = q;
        showOnly(reponsesSection);
        setText(sectionTitleLabel,            "Réponses — " + safe(q.getTexte()));
        setText(reponsesSectionTitleLabel,    q.getTexte());
        setText(reponsesSectionSubtitleLabel, "Type : " + safe(q.getTypeQuestion())
                + " · " + (q.getPoints() != null ? q.getPoints() + " pts" : "-"));
        hidePanel(reponseFormPanel);
        boolean isTextLibre = "texte_libre".equals(q.getTypeQuestion());
        if (reponseCorrecteCheck != null) {
            reponseCorrecteCheck.setVisible(!isTextLibre);
            reponseCorrecteCheck.setManaged(!isTextLibre);
        }
        updateReponsesHint(q.getTypeQuestion());
        loadReponses(q.getId());
    }

    private void confirmDeleteQuestion(Question q) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer cette question ?\n« " + q.getTexte() + " »",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        alert.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                questionService.delete(q.getId().intValue());
                if (selectedQuestion != null && selectedQuestion.getId().equals(q.getId())) {
                    selectedQuestion = null; closeQuestionDetails();
                }
                setStatus(questionsMessageLabel, "Question supprimée.", false);
                loadQuestions();
            } catch (Exception e) {
                setStatus(questionsMessageLabel, "Suppression impossible : " + e.getMessage(), true);
            }
        });
    }

    private void startEditReponse(Reponse r) {
        editingReponse = r;
        setText(reponseFormTitleLabel, "Modifier la réponse");
        populateReponseForm(r);
        showPanel(reponseFormPanel);
    }

    private void confirmDeleteReponse(Reponse r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer cette réponse ?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        alert.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                reponseService.delete(r.getId().intValue());
                setStatus(reponsesMessageLabel, "Réponse supprimée.", false);
                loadReponses(selectedQuestion.getId());
            } catch (Exception e) {
                setStatus(reponsesMessageLabel, "Suppression impossible : " + e.getMessage(), true);
            }
        });
    }

    private void updateQuestionDetailPanel(Question q) {
        if (questionDetailsPanel == null) return;
        boolean v = q != null;
        questionDetailsPanel.setVisible(v);
        questionDetailsPanel.setManaged(v);
        if (!v) return;
        setText(detailQuestionTexteLabel,       q.getTexte());
        setText(detailQuestionMetaLabel,        safe(q.getTypeQuestion())
                + " · " + (q.getPoints() != null ? q.getPoints() + " pts" : "-"));
        setText(detailQuestionExplicationLabel, safe(q.getExplicationReponse()));
        if (detailReponsesContainer != null) {
            detailReponsesContainer.getChildren().clear();
            try {
                List<Reponse> reponses = reponseService.getByQuestion(q.getId());
                for (Reponse r : reponses) {
                    String icone = Boolean.TRUE.equals(r.getEstCorrecte()) ? "✅ " : "❌ ";
                    Label lbl = new Label(icone + safe(r.getTexteReponse()));
                    lbl.setWrapText(true);
                    lbl.getStyleClass().add(Boolean.TRUE.equals(r.getEstCorrecte()) ? "detail-value" : "detail-key");
                    detailReponsesContainer.getChildren().add(lbl);
                }
                if (reponses.isEmpty()) {
                    Label empty = new Label("Aucune réponse définie.");
                    empty.getStyleClass().add("dashboard-subsection");
                    detailReponsesContainer.getChildren().add(empty);
                }
            } catch (Exception e) {
                Label err = new Label("Chargement réponses impossible.");
                err.getStyleClass().add("field-error-label");
                detailReponsesContainer.getChildren().add(err);
            }
        }
    }

    private void populateQuestionForm(Question q) {
        clearQuestionForm();
        if (questionTexteArea       != null) questionTexteArea.setText(safe(q.getTexte()));
        if (questionTypeCombo       != null) questionTypeCombo.setValue(q.getTypeQuestion());
        if (questionPointsSpinner   != null && q.getPoints() != null)
            questionPointsSpinner.getValueFactory().setValue(q.getPoints());
        if (questionOrdreSpinner    != null && q.getOrdreAffichage() != null)
            questionOrdreSpinner.getValueFactory().setValue(q.getOrdreAffichage());
        if (questionExplicationArea != null)
            questionExplicationArea.setText(safe(q.getExplicationReponse()));

        try {
            List<Reponse> reponses = reponseService.getByQuestion(q.getId());
            String type = q.getTypeQuestion();
            if (type == null) return;
            switch (type) {
                case "vrai_faux" -> {
                    for (Reponse r : reponses) {
                        if ("Vrai".equalsIgnoreCase(r.getTexteReponse()) && Boolean.TRUE.equals(r.getEstCorrecte()))
                            if (vraiFauxVraiRadio != null) vraiFauxVraiRadio.setSelected(true);
                        if ("Faux".equalsIgnoreCase(r.getTexteReponse()) && Boolean.TRUE.equals(r.getEstCorrecte()))
                            if (vraiFauxFauxRadio != null) vraiFauxFauxRadio.setSelected(true);
                    }
                }
                case "choix_unique" -> {
                    if (choixUniqueReponsesContainer != null)
                        choixUniqueReponsesContainer.getChildren().clear();
                    choixUniqueGroup.getToggles().clear();
                    for (Reponse r : reponses) {
                        addChoixUniqueRow();
                        HBox row = (HBox) choixUniqueReponsesContainer.getChildren()
                                .get(choixUniqueReponsesContainer.getChildren().size() - 1);
                        ((RadioButton) row.getChildren().get(0)).setSelected(Boolean.TRUE.equals(r.getEstCorrecte()));
                        ((TextField)   row.getChildren().get(1)).setText(safe(r.getTexteReponse()));
                    }
                }
                case "choix_multiple" -> {
                    if (choixMultipleReponsesContainer != null)
                        choixMultipleReponsesContainer.getChildren().clear();
                    for (Reponse r : reponses) {
                        addChoixMultipleRow();
                        HBox row = (HBox) choixMultipleReponsesContainer.getChildren()
                                .get(choixMultipleReponsesContainer.getChildren().size() - 1);
                        ((CheckBox)  row.getChildren().get(0)).setSelected(Boolean.TRUE.equals(r.getEstCorrecte()));
                        ((TextField) row.getChildren().get(1)).setText(safe(r.getTexteReponse()));
                    }
                }
                case "texte_libre" -> {
                    if (!reponses.isEmpty() && texteLibreReponseArea != null)
                        texteLibreReponseArea.setText(safe(reponses.get(0).getTexteReponse()));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void populateReponseForm(Reponse r) {
        clearReponseForm();
        if (reponseTexteArea     != null) reponseTexteArea.setText(safe(r.getTexteReponse()));
        if (reponseCorrecteCheck != null) reponseCorrecteCheck.setSelected(Boolean.TRUE.equals(r.getEstCorrecte()));
        if (reponseOrdreSpinner  != null && r.getOrdreAffichage() != null)
            reponseOrdreSpinner.getValueFactory().setValue(r.getOrdreAffichage());
        if (reponseFeedbackArea  != null) reponseFeedbackArea.setText(safe(r.getFeedbackSpecifique()));
        if (reponseMediaUrlField != null) reponseMediaUrlField.setText(safe(r.getMediaUrl()));
    }

    // ═════════════════════════════════════════════════════════════
    // HINTS
    // ═════════════════════════════════════════════════════════════
    private void updateReponsesHint(String type) {
        if (type == null || reponsesMessageLabel == null) return;
        switch (type) {
            case "choix_unique"   -> setStatus(reponsesMessageLabel, "ℹ  Une seule réponse correcte.", false);
            case "choix_multiple" -> setStatus(reponsesMessageLabel, "ℹ  Plusieurs réponses correctes possibles.", false);
            case "vrai_faux"      -> setStatus(reponsesMessageLabel, "ℹ  Vérifiez la bonne réponse.", false);
            case "texte_libre"    -> setStatus(reponsesMessageLabel, "ℹ  Correction manuelle.", false);
            default               -> setText(reponsesMessageLabel, "");
        }
    }

    // ═════════════════════════════════════════════════════════════
    // VALIDATION
    // ═════════════════════════════════════════════════════════════
    private boolean validateQuizForm() {
        boolean ok = true;
        clearQuizFormErrors();
        if (quizTitreField != null && quizTitreField.getText().trim().isEmpty()) {
            setText(quizTitreErrorLabel, "Le titre est obligatoire."); ok = false;
        }
        if (quizTypeCombo != null && quizTypeCombo.getValue() == null) {
            setText(quizTypeErrorLabel, "Veuillez choisir un type."); ok = false;
        }
        if (quizCorrectionApresCombo != null && quizCorrectionApresCombo.getValue() == null) {
            setText(quizCorrectionErrorLabel, "Choisissez le moment de correction."); ok = false;
        }
        if (quizDateDebutPicker != null && quizDateDebutPicker.getValue() == null) {
            setText(quizDateDebutErrorLabel, "La date d'ouverture est obligatoire."); ok = false;
        }
        if (quizDateFinPicker != null && quizDateFinPicker.getValue() == null) {
            setText(quizDateFinErrorLabel, "La date de clôture est obligatoire."); ok = false;
        }
        if (quizDateDebutPicker != null && quizDateFinPicker != null) {
            java.time.LocalDate d = quizDateDebutPicker.getValue();
            java.time.LocalDate f = quizDateFinPicker.getValue();
            if (d != null && f != null && !f.isAfter(d)) {
                setText(quizDateFinErrorLabel, "La date de clôture doit être après la date d'ouverture."); ok = false;
            }
        }
        return ok;
    }

    private boolean validateQuestionForm() {
        boolean ok = true;
        clearQuestionFormErrors();
        if (questionTexteArea != null && questionTexteArea.getText().trim().isEmpty()) {
            setText(questionTexteErrorLabel, "L'énoncé est obligatoire."); ok = false;
        }
        String type = questionTypeCombo != null ? questionTypeCombo.getValue() : null;
        if (type == null) {
            setText(questionTypeErrorLabel, "Veuillez choisir un type."); return false;
        }
        switch (type) {
            case "vrai_faux" -> {
                boolean aucun = (vraiFauxVraiRadio == null || !vraiFauxVraiRadio.isSelected())
                        && (vraiFauxFauxRadio == null || !vraiFauxFauxRadio.isSelected());
                if (aucun) { setText(vraiFauxErrorLabel, "Sélectionnez Vrai ou Faux."); ok = false; }
            }
            case "choix_unique" -> {
                if (choixUniqueReponsesContainer == null || choixUniqueReponsesContainer.getChildren().isEmpty()) {
                    setText(choixUniqueErrorLabel, "Ajoutez au moins une réponse."); ok = false;
                } else {
                    boolean hasCorrect = choixUniqueReponsesContainer.getChildren().stream()
                            .filter(n -> n instanceof HBox)
                            .anyMatch(n -> ((RadioButton) ((HBox) n).getChildren().get(0)).isSelected());
                    if (!hasCorrect) { setText(choixUniqueErrorLabel, "Sélectionnez la bonne réponse (●)."); ok = false; }
                }
            }
            case "choix_multiple" -> {
                if (choixMultipleReponsesContainer == null || choixMultipleReponsesContainer.getChildren().isEmpty()) {
                    setText(choixMultipleErrorLabel, "Ajoutez au moins une réponse."); ok = false;
                } else {
                    boolean hasCorrect = choixMultipleReponsesContainer.getChildren().stream()
                            .filter(n -> n instanceof HBox)
                            .anyMatch(n -> ((CheckBox) ((HBox) n).getChildren().get(0)).isSelected());
                    if (!hasCorrect) { setText(choixMultipleErrorLabel, "Cochez au moins une réponse correcte."); ok = false; }
                }
            }
        }
        return ok;
    }

    private boolean validateReponseForm() {
        setText(reponseTexteErrorLabel, "");
        if (reponseTexteArea != null && reponseTexteArea.getText().trim().isEmpty()) {
            setText(reponseTexteErrorLabel, "Le texte de la réponse est obligatoire.");
            return false;
        }
        return true;
    }

    private void clearQuizFormErrors() {
        setText(quizTitreErrorLabel, "");       setText(quizTypeErrorLabel, "");
        setText(quizCorrectionErrorLabel, "");  setText(quizDureeErrorLabel, "");
        setText(quizTentativesErrorLabel, "");  setText(quizDateDebutErrorLabel, "");
        setText(quizDateFinErrorLabel, "");     setText(quizCoursErrorLabel, "");
        setText(quizInstructionsErrorLabel, "");setText(quizDescriptionErrorLabel, "");
        setText(quizFormStatusLabel, "");
    }

    private void clearQuestionFormErrors() {
        setText(questionTexteErrorLabel,       "");
        setText(questionTypeErrorLabel,        "");
        setText(questionTypeHintLabel,         "");
        setText(questionPointsErrorLabel,      "");
        setText(questionExplicationErrorLabel, "");
        setText(questionFormStatusLabel,       "");
        setText(vraiFauxErrorLabel,            "");
        setText(choixUniqueErrorLabel,         "");
        setText(choixMultipleErrorLabel,       "");
        setText(texteLibreErrorLabel,          "");
    }

    private void clearQuizForm() {
        clearQuizFormErrors();
        if (quizTitreField           != null) quizTitreField.clear();
        if (quizTypeCombo            != null) quizTypeCombo.setValue(null);
        if (quizCorrectionApresCombo != null) quizCorrectionApresCombo.setValue(null);
        if (quizDateDebutPicker      != null) quizDateDebutPicker.setValue(null);
        if (quizDateFinPicker        != null) quizDateFinPicker.setValue(null);
        if (quizCoursCombo           != null) quizCoursCombo.setValue(null);
        if (quizInstructionsArea     != null) quizInstructionsArea.clear();
        if (quizDescriptionArea      != null) quizDescriptionArea.clear();
        if (quizDateRangeInfoLabel   != null)
            setText(quizDateRangeInfoLabel, "Sélectionnez les deux dates pour voir la période.");
    }

    private void clearQuestionForm() {
        clearQuestionFormErrors();
        if (questionTexteArea       != null) questionTexteArea.clear();
        if (questionTypeCombo       != null) questionTypeCombo.setValue(null);
        if (questionExplicationArea != null) questionExplicationArea.clear();
        if (questionPointsSpinner   != null) questionPointsSpinner.getValueFactory().setValue(1);
        if (questionOrdreSpinner    != null) questionOrdreSpinner.getValueFactory().setValue(1);

        if (vraiFauxVraiRadio              != null) vraiFauxVraiRadio.setSelected(false);
        if (vraiFauxFauxRadio              != null) vraiFauxFauxRadio.setSelected(false);
        if (choixUniqueReponsesContainer   != null) choixUniqueReponsesContainer.getChildren().clear();
        if (choixMultipleReponsesContainer != null) choixMultipleReponsesContainer.getChildren().clear();
        if (texteLibreReponseArea          != null) texteLibreReponseArea.clear();

        hidePanel(reponsesVraiFauxPanel);
        hidePanel(reponsesChoixUniquePanel);
        hidePanel(reponsesChoixMultiplePanel);
        hidePanel(reponsesTexteLibrePanel);
    }

    private void clearReponseForm() {
        setText(reponseTexteErrorLabel,    "");
        setText(reponseOrdreErrorLabel,    "");
        setText(reponseFeedbackErrorLabel, "");
        setText(reponseFormStatusLabel,    "");
        if (reponseTexteArea     != null) reponseTexteArea.clear();
        if (reponseCorrecteCheck != null) reponseCorrecteCheck.setSelected(false);
        if (reponseFeedbackArea  != null) reponseFeedbackArea.clear();
        if (reponseMediaUrlField != null) reponseMediaUrlField.clear();
        if (reponseOrdreSpinner  != null) reponseOrdreSpinner.getValueFactory().setValue(1);
    }

    // ═════════════════════════════════════════════════════════════
    // UTILITAIRES
    // ═════════════════════════════════════════════════════════════
    private void setText(Label l, String t)  { if (l != null) l.setText(t != null ? t : ""); }
    private void showPanel(VBox p)           { if (p != null) { p.setVisible(true);  p.setManaged(true); } }
    private void hidePanel(VBox p)           { if (p != null) { p.setVisible(false); p.setManaged(false); } }
    private String safe(String v)            { return v == null ? "" : v; }

    private void setStatus(Label l, String msg, boolean error) {
        if (l == null) return;
        l.setText(msg);
        l.getStyleClass().removeAll("error-status", "neutral-status", "success-status");
        l.getStyleClass().add(error ? "error-status" : "success-status");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setupColumn(TableColumn col, String prop) {
        if (col != null) col.setCellValueFactory(new PropertyValueFactory<>(prop));
    }

    public void setCurrentUser(String nom, String role) {
        setText(currentUserNameLabel, nom);
        setText(currentUserRoleLabel, role);
        if (profileMenuButton != null)
            profileMenuButton.setText(nom.length() >= 2
                    ? nom.substring(0, 2).toUpperCase() : nom.toUpperCase());
    }
}