package gui;

import entities.Question;
import entities.Reponse;
import entities.Quiz;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.QuestionService;
import services.QuizService;
import services.ReponseService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    @FXML private TextField  quizIdCoursField;
    @FXML private Label      quizIdCoursErrorLabel;
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
    @FXML private Spinner<Integer> questionPointsSpinner;
    @FXML private Label     questionPointsErrorLabel;
    @FXML private Spinner<Integer> questionOrdreSpinner;
    @FXML private TextArea  questionExplicationArea;
    @FXML private Label     questionExplicationErrorLabel;
    @FXML private Label     questionFormStatusLabel;

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

    // ── État interne ──────────────────────────────────────────────
    private Quiz     selectedQuiz     = null;
    private Quiz     editingQuiz      = null;
    private Quiz     currentQuiz      = null;   // quiz dont on gère les questions
    private Question selectedQuestion = null;
    private Question editingQuestion  = null;
    private Reponse  editingReponse   = null;

    private final QuizService     quizService     = new QuizService();
    private final QuestionService questionService = new QuestionService();
    private final ReponseService  reponseService  = new ReponseService();

    private final ObservableList<Quiz>     quizList     = FXCollections.observableArrayList();
    private final ObservableList<Question> questionList = FXCollections.observableArrayList();
    private final ObservableList<Reponse>  reponseList  = FXCollections.observableArrayList();

    // ═════════════════════════════════════════════════════════════
    // INITIALISATION
    // ═════════════════════════════════════════════════════════════
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {

        // ── ComboBox quiz ──
        if (quizTypeCombo != null)
            quizTypeCombo.setItems(FXCollections.observableArrayList(
                    "QCM", "Vrai/Faux", "Texte libre", "Mixte"));
        if (quizCorrectionApresCombo != null)
            quizCorrectionApresCombo.setItems(FXCollections.observableArrayList(
                    "Immédiate", "Après soumission", "Manuelle"));

        // ── ComboBox question ──
        if (questionTypeCombo != null) {
            questionTypeCombo.setItems(FXCollections.observableArrayList(
                    "choix_unique", "choix_multiple", "vrai_faux", "texte_libre"));
            questionTypeCombo.valueProperty().addListener((obs, old, nv) -> updateTypeHint(nv));
        }

        // ── TableView quiz ──
        if (quizTable != null) {
            setupColumn(quizTitreColumn,    "titre");
            setupColumn(quizTypeColumn,     "typeQuiz");
            setupColumn(quizDureeColumn,    "dureeMinutes");
            setupColumn(quizDispoColumn,    "dateFinDisponibilite");

            if (quizActionsColumn != null) {
                ((TableColumn<Quiz, Void>) quizActionsColumn).setCellFactory(col -> new TableCell<>() {
                    private final Button btnEdit  = new Button("✏ Modifier");
                    private final Button btnQuest = new Button("❓ Questions");
                    private final Button btnDel   = new Button("🗑 Supprimer");
                    private final HBox   box      = new HBox(6, btnEdit, btnQuest, btnDel);
                    {
                        btnEdit .getStyleClass().addAll("secondary-button", "table-action-button");
                        btnQuest.getStyleClass().addAll("primary-button",   "table-action-button");
                        btnDel  .getStyleClass().addAll("danger-button",    "table-action-button");
                        btnEdit .setOnAction(e -> startEditQuiz(getTableView().getItems().get(getIndex())));
                        btnQuest.setOnAction(e -> openQuestionsPage(getTableView().getItems().get(getIndex())));
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

        // ── TableView questions ──
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

        // ── TableView réponses ──
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

        // ── Listeners dates quiz ──
        if (quizDateDebutPicker != null)
            quizDateDebutPicker.valueProperty().addListener((obs, ov, nv) -> updateDateRangeInfo());
        if (quizDateFinPicker != null)
            quizDateFinPicker.valueProperty().addListener((obs, ov, nv) -> updateDateRangeInfo());

        // ── Profil ──
        Utilisateur u = UserSession.getCurrentUser();
        if (u != null) setCurrentUser(u.getNomComplet(), u.getType());

        // ── Page initiale ──
        showOnly(quizListSection);
        setText(sectionTitleLabel,    "Gestion des Quiz");
        setText(sectionSubtitleLabel, "Créez, modifiez et organisez vos quiz et leurs questions.");
        loadQuizList();
    }

    // ═════════════════════════════════════════════════════════════
    // NAVIGATION — toutes les sections du même FXML
    // ═════════════════════════════════════════════════════════════
    private void showOnly(VBox target) {
        for (VBox s : new VBox[]{
                quizListSection, quizFormSection,
                questionsSection, questionFormSection, reponsesSection })
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

    @FXML
    private void closeQuizDetails() {
        if (quizDetailsPanel != null) {
            quizDetailsPanel.setVisible(false);
            quizDetailsPanel.setManaged(false);
        }
        selectedQuiz = null;
        if (quizTable != null) quizTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void editSelectedQuiz() {
        if (selectedQuiz == null) {
            setStatus(quizListMessageLabel, "Sélectionnez un quiz à modifier.", true);
            return;
        }
        startEditQuiz(selectedQuiz);
    }

    @FXML
    private void deleteSelectedQuiz() {
        if (selectedQuiz == null) {
            setStatus(quizListMessageLabel, "Sélectionnez un quiz à supprimer.", true);
            return;
        }
        confirmDeleteQuiz(selectedQuiz);
    }

    @FXML
    private void manageQuestionsOfSelectedQuiz() {
        if (selectedQuiz == null) {
            setStatus(quizListMessageLabel, "Sélectionnez d'abord un quiz.", true);
            return;
        }
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
        clearQuizForm();
    }

    @FXML
    private void cancelQuizForm() {
        editingQuiz = null;
        showQuizListPage();
    }

    @FXML
    private void saveQuiz() {
        if (!validateQuizForm()) return;
        try {
            Quiz quiz = (editingQuiz != null) ? editingQuiz : new Quiz();
            quiz.setTitre(quizTitreField.getText().trim());
            if (quizTypeCombo.getValue() != null)
                quiz.setTypeQuiz(quizTypeCombo.getValue());
            if (quizCorrectionApresCombo.getValue() != null)
                quiz.setAfficherCorrectionApres(quizCorrectionApresCombo.getValue());
            if (quizDureeSpinner.getValue() != null)
                quiz.setDureeMinutes(quizDureeSpinner.getValue());
            if (quizTentativesSpinner.getValue() != null)
                quiz.setNombreTentativesAutorisees(quizTentativesSpinner.getValue());
            if (quizDateDebutPicker.getValue() != null)
                quiz.setDateDebutDisponibilite(quizDateDebutPicker.getValue().atStartOfDay());
            if (quizDateFinPicker.getValue() != null)
                quiz.setDateFinDisponibilite(quizDateFinPicker.getValue().atTime(23, 59));
            if (quizIdCoursField != null && !quizIdCoursField.getText().trim().isEmpty()) {
                try { quiz.setIdCours(Integer.parseInt(quizIdCoursField.getText().trim())); }
                catch (NumberFormatException ignored) {}
            }
            if (quizInstructionsArea != null)
                quiz.setInstructions(quizInstructionsArea.getText().trim());
            if (quizDescriptionArea != null)
                quiz.setDescription(quizDescriptionArea.getText().trim());

            Utilisateur u = UserSession.getCurrentUser();
            if (u != null && quiz.getCreateur() == null)
                quiz.setCreateur(u);

            if (editingQuiz == null) {
                quizService.create(quiz);
                setStatus(quizFormStatusLabel, "Quiz créé avec succès ✔", false);
            } else {
                quizService.update(quiz);
                setStatus(quizFormStatusLabel, "Quiz modifié avec succès ✔", false);
            }

            editingQuiz = null;
            loadQuizList();
            showOnly(quizListSection);
            setText(sectionTitleLabel,    "Gestion des Quiz");
            setText(sectionSubtitleLabel, "Créez, modifiez et organisez vos quiz et leurs questions.");

        } catch (Exception e) {
            setStatus(quizFormStatusLabel,
                    "Erreur (" + e.getClass().getSimpleName() + ") : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 3 — LISTE QUESTIONS
    // ═════════════════════════════════════════════════════════════

    /**
     * Navigation vers la page questions pour un quiz donné.
     * Appelée depuis le bouton table "❓ Questions" et depuis le panneau détail.
     */
    private void openQuestionsPage(Quiz quiz) {
        currentQuiz = quiz;
        selectedQuestion = null;
        showOnly(questionsSection);
        setText(sectionTitleLabel,             "Questions — " + quiz.getTitre());
        setText(sectionSubtitleLabel,          "Gérez les questions de ce quiz.");
        setText(questionsSectionTitleLabel,    quiz.getTitre());
        setText(questionsSectionSubtitleLabel, "Ajoutez, modifiez ou supprimez les questions.");
        if (questionDetailsPanel != null) {
            questionDetailsPanel.setVisible(false);
            questionDetailsPanel.setManaged(false);
        }
        loadQuestions();
    }

    @FXML
    private void closeQuestionDetails() {
        if (questionDetailsPanel != null) {
            questionDetailsPanel.setVisible(false);
            questionDetailsPanel.setManaged(false);
        }
        selectedQuestion = null;
        if (questionsTable != null) questionsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void editSelectedQuestion() {
        if (selectedQuestion == null) {
            setStatus(questionsMessageLabel, "Sélectionnez une question à modifier.", true);
            return;
        }
        startEditQuestion(selectedQuestion);
    }

    @FXML
    private void deleteSelectedQuestion() {
        if (selectedQuestion == null) {
            setStatus(questionsMessageLabel, "Sélectionnez une question à supprimer.", true);
            return;
        }
        confirmDeleteQuestion(selectedQuestion);
    }

    @FXML
    private void manageReponsesOfSelectedQuestion() {
        if (selectedQuestion == null) {
            setStatus(questionsMessageLabel, "Sélectionnez une question d'abord.", true);
            return;
        }
        openReponsesFor(selectedQuestion);
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 4 — FORMULAIRE QUESTION
    // ═════════════════════════════════════════════════════════════

    /**
     * CORRECTION PRINCIPALE : méthode référencée dans teacher-quiz.fxml ligne 341
     * via onAction="#showQuestionCreateForm". Elle doit être dans QuizController
     * puisque teacher-quiz.fxml déclare fx:controller="gui.QuizController".
     */
    @FXML
    public void showQuestionCreateForm() {
        if (currentQuiz == null) {
            setStatus(questionsMessageLabel,
                    "Erreur : aucun quiz sélectionné. Revenez à la liste.", true);
            return;
        }
        editingQuestion = null;
        showOnly(questionFormSection);
        setText(sectionTitleLabel,      "Nouvelle question — " + currentQuiz.getTitre());
        setText(questionFormTitleLabel, "Nouvelle question");
        clearQuestionForm();
    }

    @FXML
    private void cancelQuestionForm() {
        editingQuestion = null;
        if (currentQuiz != null) openQuestionsPage(currentQuiz);
        else showQuizListPage();
    }

    @FXML
    private void saveQuestion() {
        if (currentQuiz == null) {
            setStatus(questionFormStatusLabel,
                    "Erreur : aucun quiz associé. Revenez à la liste.", true);
            return;
        }
        if (!validateQuestionForm()) return;
        try {
            Question question = (editingQuestion != null) ? editingQuestion : new Question();
            question.setTexte(questionTexteArea.getText().trim());
            if (questionTypeCombo.getValue() != null)
                question.setTypeQuestion(questionTypeCombo.getValue());
            if (questionPointsSpinner.getValue() != null)
                question.setPoints(questionPointsSpinner.getValue());
            if (questionOrdreSpinner != null && questionOrdreSpinner.getValue() != null)
                question.setOrdreAffichage(questionOrdreSpinner.getValue());
            if (questionExplicationArea != null)
                question.setExplicationReponse(questionExplicationArea.getText().trim());

            question.setQuiz(currentQuiz);

            Utilisateur u = UserSession.getCurrentUser();
            if (u != null && question.getCreateur() == null)
                question.setCreateur(u);

            if (editingQuestion == null) {
                questionService.create(question);
            } else {
                questionService.update(question);
            }

            editingQuestion = null;
            openQuestionsPage(currentQuiz);

        } catch (Exception e) {
            setStatus(questionFormStatusLabel,
                    "Erreur (" + e.getClass().getSimpleName() + ") : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ═════════════════════════════════════════════════════════════
    // PAGE 5 — RÉPONSES
    // ═════════════════════════════════════════════════════════════
    @FXML
    private void backToQuestionsFromReponses() {
        if (currentQuiz != null) openQuestionsPage(currentQuiz);
        else showQuizListPage();
    }

    @FXML
    private void showReponseCreateForm() {
        editingReponse = null;
        setText(reponseFormTitleLabel, "Nouvelle réponse");
        clearReponseForm();
        showPanel(reponseFormPanel);
    }

    @FXML
    private void closeReponseForm() {
        editingReponse = null;
        hidePanel(reponseFormPanel);
    }

    @FXML
    private void saveReponse() {
        if (!validateReponseForm()) return;
        try {
            Reponse reponse = (editingReponse != null) ? editingReponse : new Reponse();
            reponse.setTexteReponse(reponseTexteArea.getText().trim());
            reponse.setEstCorrecte(reponseCorrecteCheck != null && reponseCorrecteCheck.isSelected());
            if (reponseOrdreSpinner != null && reponseOrdreSpinner.getValue() != null)
                reponse.setOrdreAffichage(reponseOrdreSpinner.getValue());
            if (reponseFeedbackArea != null)
                reponse.setFeedbackSpecifique(reponseFeedbackArea.getText().trim());
            if (reponseMediaUrlField != null)
                reponse.setMediaUrl(reponseMediaUrlField.getText().trim());

            reponse.setQuestion(selectedQuestion);

            if (editingReponse == null) {
                reponseService.create(reponse);
                setStatus(reponseFormStatusLabel, "Réponse créée ✔", false);
            } else {
                reponseService.update(reponse);
                setStatus(reponseFormStatusLabel, "Réponse modifiée ✔", false);
            }

            editingReponse = null;
            hidePanel(reponseFormPanel);
            loadReponses(selectedQuestion.getId());

        } catch (Exception e) {
            setStatus(reponseFormStatusLabel,
                    "Erreur (" + e.getClass().getSimpleName() + ") : " + e.getMessage(), true);
            e.printStackTrace();
        }
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
            if (list.isEmpty())
                setText(quizListMessageLabel, "Aucun quiz créé pour le moment.");
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
            if (list.isEmpty())
                setText(questionsMessageLabel, "Aucune question pour ce quiz.");
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
            if (list.isEmpty())
                setText(reponsesMessageLabel, "Aucune réponse pour cette question.");
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
        setText(detailQuizDureeLabel,      quiz.getDureeMinutes() != null
                ? quiz.getDureeMinutes() + " min" : "-");
        setText(detailQuizTentativesLabel, quiz.getNombreTentativesAutorisees() != null
                ? String.valueOf(quiz.getNombreTentativesAutorisees()) : "-");
        setText(detailQuizCorrectionLabel, safe(quiz.getAfficherCorrectionApres()));
        setText(detailQuizDispoLabel,      quiz.getDateFinDisponibilite() != null
                ? quiz.getDateFinDisponibilite().toLocalDate().toString() : "-");
        setText(detailQuizDescLabel,       safe(quiz.getDescription()));
    }

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

        // Masquer la case "correcte" pour texte_libre
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
                    selectedQuestion = null;
                    closeQuestionDetails();
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
                    lbl.getStyleClass().add(Boolean.TRUE.equals(r.getEstCorrecte())
                            ? "detail-value" : "detail-key");
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
    }

    private void populateReponseForm(Reponse r) {
        clearReponseForm();
        if (reponseTexteArea     != null) reponseTexteArea.setText(safe(r.getTexteReponse()));
        if (reponseCorrecteCheck != null) reponseCorrecteCheck.setSelected(
                Boolean.TRUE.equals(r.getEstCorrecte()));
        if (reponseOrdreSpinner  != null && r.getOrdreAffichage() != null)
            reponseOrdreSpinner.getValueFactory().setValue(r.getOrdreAffichage());
        if (reponseFeedbackArea  != null) reponseFeedbackArea.setText(safe(r.getFeedbackSpecifique()));
        if (reponseMediaUrlField != null) reponseMediaUrlField.setText(safe(r.getMediaUrl()));
    }

    // ═════════════════════════════════════════════════════════════
    // HINTS TYPE QUESTION
    // ═════════════════════════════════════════════════════════════
    private void updateTypeHint(String type) {
        if (type == null) { setText(questionFormStatusLabel, ""); return; }
        switch (type) {
            case "choix_unique"   -> setStatus(questionFormStatusLabel,
                    "Choix unique : une seule réponse correcte. Marquez exactement une réponse.", false);
            case "choix_multiple" -> setStatus(questionFormStatusLabel,
                    "Choix multiple : plusieurs bonnes réponses. Marquez toutes les correctes.", false);
            case "vrai_faux"      -> setStatus(questionFormStatusLabel,
                    "Vrai / Faux : ajoutez « Vrai » et « Faux », marquez la correcte.", false);
            case "texte_libre"    -> setStatus(questionFormStatusLabel,
                    "Texte libre : aucune réponse à prédéfinir — correction manuelle.", false);
            default               -> setText(questionFormStatusLabel, "");
        }
    }

    private void updateReponsesHint(String type) {
        if (type == null || reponsesMessageLabel == null) return;
        switch (type) {
            case "choix_unique"   -> setStatus(reponsesMessageLabel,
                    "ℹ Choix unique : marquez exactement une réponse comme correcte.", false);
            case "choix_multiple" -> setStatus(reponsesMessageLabel,
                    "ℹ Choix multiple : marquez toutes les réponses correctes.", false);
            case "vrai_faux"      -> setStatus(reponsesMessageLabel,
                    "ℹ Vrai / Faux : créez « Vrai » et « Faux », marquez la correcte.", false);
            case "texte_libre"    -> setStatus(reponsesMessageLabel,
                    "ℹ Texte libre : aucune réponse à marquer — correction manuelle.", false);
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
            LocalDate d = quizDateDebutPicker.getValue();
            LocalDate f = quizDateFinPicker.getValue();
            if (d != null && f != null && !f.isAfter(d)) {
                setText(quizDateFinErrorLabel,
                        "La date de clôture doit être après la date d'ouverture."); ok = false;
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
        if (questionTypeCombo != null && questionTypeCombo.getValue() == null) {
            setText(questionTypeErrorLabel, "Veuillez choisir un type."); ok = false;
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

    // ── Nettoyage erreurs ──
    private void clearQuizFormErrors() {
        setText(quizTitreErrorLabel, "");        setText(quizTypeErrorLabel, "");
        setText(quizCorrectionErrorLabel, "");   setText(quizDureeErrorLabel, "");
        setText(quizTentativesErrorLabel, "");   setText(quizDateDebutErrorLabel, "");
        setText(quizDateFinErrorLabel, "");      setText(quizIdCoursErrorLabel, "");
        setText(quizInstructionsErrorLabel, ""); setText(quizDescriptionErrorLabel, "");
        setText(quizFormStatusLabel, "");
    }

    private void clearQuestionFormErrors() {
        setText(questionTexteErrorLabel,       "");
        setText(questionTypeErrorLabel,        "");
        setText(questionPointsErrorLabel,      "");
        setText(questionExplicationErrorLabel, "");
        setText(questionFormStatusLabel,       "");
    }

    // ── Nettoyage formulaires ──
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