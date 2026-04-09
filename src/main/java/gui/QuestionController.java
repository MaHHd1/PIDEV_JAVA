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
import services.ReponseService;
import utils.UserSession;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QuestionController implements Initializable {

    // ── TOPBAR ────────────────────────────────────────────────────
    @FXML private Label      sectionTitleLabel;
    @FXML private Label      sectionSubtitleLabel;
    @FXML private Label      currentUserNameLabel;
    @FXML private Label      currentUserRoleLabel;
    @FXML private MenuButton profileMenuButton;

    // ── PAGE 1 : LISTE QUESTIONS ──────────────────────────────────
    @FXML private VBox  questionsSection;
    @FXML private Label questionsSectionTitleLabel;
    @FXML private Label questionsSectionSubtitleLabel;
    @FXML private Label questionsMessageLabel;

    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<?,?>   qOrdreColumn;
    @FXML private TableColumn<?,?>   qTexteColumn;
    @FXML private TableColumn<?,?>   qTypeColumn;
    @FXML private TableColumn<?,?>   qPointsColumn;
    @FXML private TableColumn<?,?>   qActionsColumn;

    // Panneau détail question
    @FXML private VBox  questionDetailsPanel;
    @FXML private Label detailQuestionTexteLabel;
    @FXML private Label detailQuestionMetaLabel;
    @FXML private VBox  detailReponsesContainer;
    @FXML private Label detailQuestionExplicationLabel;

    // ── PAGE 2 : FORMULAIRE QUESTION ─────────────────────────────
    @FXML private VBox      questionFormSection;
    @FXML private Label     questionFormTitleLabel;
    @FXML private TextArea  questionTexteArea;
    @FXML private Label     questionTexteErrorLabel;
    @FXML private ComboBox<String> questionTypeCombo;
    @FXML private Label     questionTypeErrorLabel;
    @FXML private Label     questionTypeHintLabel;      // ← hint visible dans le panneau Paramètres
    @FXML private Spinner<Integer> questionPointsSpinner;
    @FXML private Label     questionPointsErrorLabel;
    @FXML private Spinner<Integer> questionOrdreSpinner;
    @FXML private TextArea  questionExplicationArea;
    @FXML private Label     questionExplicationErrorLabel;
    @FXML private Label     questionFormStatusLabel;    // ← réservé aux erreurs de sauvegarde

    // ── PAGE 3 : LISTE RÉPONSES ───────────────────────────────────
    @FXML private VBox  reponsesSection;
    @FXML private Label reponsesSectionTitleLabel;
    @FXML private Label reponsesSectionSubtitleLabel;
    @FXML private Label reponsesMessageLabel;

    @FXML private TableView<Reponse> reponsesTable;
    @FXML private TableColumn<?,?>  rOrdreColumn;
    @FXML private TableColumn<?,?>  rTexteColumn;
    @FXML private TableColumn<?,?>  rCorrecteColumn;
    @FXML private TableColumn<?,?>  rFeedbackColumn;
    @FXML private TableColumn<?,?>  rActionsColumn;

    // Formulaire réponse inline
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
    private Quiz     currentQuiz      = null;
    private Question selectedQuestion = null;
    private Question editingQuestion  = null;
    private Reponse  editingReponse   = null;

    private final QuestionService questionService = new QuestionService();
    private final ReponseService  reponseService  = new ReponseService();

    private final ObservableList<Question> questionList = FXCollections.observableArrayList();
    private final ObservableList<Reponse>  reponseList  = FXCollections.observableArrayList();

    // ── INITIALISATION ────────────────────────────────────────────
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {

        // ComboBox types de question
        if (questionTypeCombo != null) {
            questionTypeCombo.setItems(FXCollections.observableArrayList(
                    "choix_unique", "choix_multiple", "vrai_faux", "texte_libre"));

            // Hint contextuel affiché dans le panneau Paramètres via questionTypeHintLabel
            questionTypeCombo.valueProperty().addListener((obs, old, nv) -> updateTypeHint(nv));
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

        // Profil utilisateur
        Utilisateur u = UserSession.getCurrentUser();
        if (u != null) setCurrentUser(u.getNomComplet(), u.getType());
    }

    /**
     * Appelé par QuizController pour injecter le quiz parent.
     */
    public void initWithQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
        setText(sectionTitleLabel,             "Questions — " + quiz.getTitre());
        setText(sectionSubtitleLabel,          "Gérez les questions de ce quiz.");
        setText(questionsSectionTitleLabel,    quiz.getTitre());
        setText(questionsSectionSubtitleLabel, "Ajoutez, modifiez ou supprimez les questions.");
        showOnly(questionsSection);
        loadQuestions();
    }

    // ── NAVIGATION ────────────────────────────────────────────────
    private void showOnly(VBox target) {
        for (VBox s : new VBox[]{ questionsSection, questionFormSection, reponsesSection })
            if (s != null) { s.setVisible(false); s.setManaged(false); }
        if (target != null) { target.setVisible(true); target.setManaged(true); }
    }

    // ── PAGE 1 : QUESTIONS ────────────────────────────────────────
    @FXML
    public void showQuizListPage() {
        if (questionsSection != null)
            questionsSection.getScene().getWindow().hide();
    }

    @FXML
    public void showQuestionCreateForm() {
        if (currentQuiz == null) {
            setStatus(questionsMessageLabel,
                    "Erreur : aucun quiz associé. Fermez et réouvrez la fenêtre.", true);
            return;
        }
        editingQuestion = null;
        showOnly(questionFormSection);
        setText(sectionTitleLabel,      "Nouvelle question");
        setText(questionFormTitleLabel, "Nouvelle question");
        clearQuestionForm();
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

    // ── PAGE 2 : FORMULAIRE QUESTION ─────────────────────────────
    @FXML
    private void cancelQuestionForm() {
        editingQuestion = null;
        showOnly(questionsSection);
        setText(sectionTitleLabel, "Questions — " + (currentQuiz != null ? currentQuiz.getTitre() : ""));
    }

    @FXML
    private void saveQuestion() {
        if (currentQuiz == null) {
            setStatus(questionFormStatusLabel,
                    "Erreur critique : aucun quiz associé. Fermez et réouvrez la fenêtre.", true);
            return;
        }

        if (!validateQuestionForm()) return;

        try {
            boolean isNew = (editingQuestion == null);
            Question question = isNew ? new Question() : editingQuestion;

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

            if (isNew) {
                questionService.create(question);
                // Génération automatique des réponses selon le type
                autoCreateReponses(question);
            } else {
                questionService.update(question);
                setStatus(questionFormStatusLabel, "Question modifiée avec succès ✔", false);
                editingQuestion = null;
                loadQuestions();
                showOnly(questionsSection);
                setText(sectionTitleLabel, "Questions — " + currentQuiz.getTitre());
            }

        } catch (Exception e) {
            setStatus(questionFormStatusLabel,
                    "Erreur (" + e.getClass().getSimpleName() + ") : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ── GÉNÉRATION AUTOMATIQUE DES RÉPONSES ──────────────────────

    /**
     * Génère automatiquement les réponses selon le type de la question
     * qui vient d'être créée, puis propose d'ouvrir la page des réponses.
     */
    private void autoCreateReponses(Question question) {
        String type = question.getTypeQuestion();

        // Retour à la liste dans tous les cas
        editingQuestion = null;
        loadQuestions();
        showOnly(questionsSection);
        setText(sectionTitleLabel, "Questions — " + currentQuiz.getTitre());

        if (type == null) {
            setStatus(questionsMessageLabel, "Question créée ✔", false);
            return;
        }

        switch (type) {

            case "vrai_faux" -> {
                try {
                    Reponse vrai = new Reponse();
                    vrai.setTexteReponse("Vrai");
                    vrai.setEstCorrecte(true);
                    vrai.setOrdreAffichage(1);
                    vrai.setQuestion(question);
                    reponseService.create(vrai);

                    Reponse faux = new Reponse();
                    faux.setTexteReponse("Faux");
                    faux.setEstCorrecte(false);
                    faux.setOrdreAffichage(2);
                    faux.setQuestion(question);
                    reponseService.create(faux);

                    setStatus(questionsMessageLabel,
                            "Question créée ✔ · Réponses « Vrai » et « Faux » générées automatiquement.", false);

                    askOpenReponses(question,
                            "Les réponses « Vrai » et « Faux » ont été créées.\n"
                                    + "Voulez-vous vérifier et ajuster la bonne réponse ?");

                } catch (Exception e) {
                    setStatus(questionsMessageLabel,
                            "Question créée mais erreur à la génération des réponses : " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }

            case "choix_unique", "choix_multiple" -> {
                ChoiceDialog<Integer> dialog = new ChoiceDialog<>(4, 2, 3, 4, 5, 6);
                dialog.setTitle("Réponses automatiques");
                dialog.setHeaderText("Question de type « " + type + " »");
                dialog.setContentText("Combien de réponses vides souhaitez-vous créer ?");

                dialog.showAndWait().ifPresent(count -> {
                    try {
                        for (int i = 1; i <= count; i++) {
                            Reponse r = new Reponse();
                            r.setTexteReponse("Réponse " + i);
                            r.setEstCorrecte(false);
                            r.setOrdreAffichage(i);
                            r.setQuestion(question);
                            reponseService.create(r);
                        }

                        setStatus(questionsMessageLabel,
                                "Question créée ✔ · " + count + " réponse(s) vide(s) générée(s).", false);

                        askOpenReponses(question,
                                count + " réponses vides ont été créées.\n"
                                        + "Voulez-vous les compléter et marquer la/les correcte(s) maintenant ?");

                    } catch (Exception e) {
                        setStatus(questionsMessageLabel,
                                "Question créée mais erreur à la génération : " + e.getMessage(), true);
                        e.printStackTrace();
                    }
                });
            }

            case "texte_libre" ->
                    setStatus(questionsMessageLabel,
                            "Question « texte libre » créée ✔ · Aucune réponse prédéfinie (correction manuelle).", false);

            default -> setStatus(questionsMessageLabel, "Question créée ✔", false);
        }
    }

    /**
     * Propose d'ouvrir la page des réponses pour la question qui vient d'être créée.
     */
    private void askOpenReponses(Question question, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Éditer les réponses ?");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) openReponsesFor(question);
        });
    }

    // ── PAGE 3 : RÉPONSES ─────────────────────────────────────────
    @FXML
    private void backToQuestionsFromReponses() {
        showOnly(questionsSection);
        setText(sectionTitleLabel, "Questions — " + (currentQuiz != null ? currentQuiz.getTitre() : ""));
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

    // ── CHARGEMENT ────────────────────────────────────────────────
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
            setStatus(questionsMessageLabel,
                    "Chargement impossible : " + e.getMessage(), true);
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
            setStatus(reponsesMessageLabel,
                    "Chargement impossible : " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // ── HELPERS PRIVÉS ────────────────────────────────────────────
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
        setText(sectionTitleLabel,            "Réponses");
        setText(reponsesSectionTitleLabel,    q.getTexte());
        setText(reponsesSectionSubtitleLabel, "Type : " + safe(q.getTypeQuestion())
                + " · " + safe(String.valueOf(q.getPoints())) + " pts");
        hidePanel(reponseFormPanel);

        // Masquer la case "correcte" pour le type texte_libre
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
                setStatus(questionsMessageLabel,
                        "Suppression impossible : " + e.getMessage(), true);
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
                setStatus(reponsesMessageLabel,
                        "Suppression impossible : " + e.getMessage(), true);
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
                    lbl.getStyleClass().add(
                            Boolean.TRUE.equals(r.getEstCorrecte()) ? "detail-value" : "detail-key");
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
        // Le listener sur questionTypeCombo déclenche updateTypeHint automatiquement
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

    // ── HINTS SELON LE TYPE ───────────────────────────────────────

    /**
     * Affiche le hint directement dans le panneau Paramètres (questionTypeHintLabel),
     * juste sous le ComboBox — visible sans scroller, dès la sélection du type.
     */
    private void updateTypeHint(String type) {
        if (type == null) {
            setText(questionTypeHintLabel, "");
            return;
        }
        switch (type) {
            case "choix_unique" ->
                    setStatus(questionTypeHintLabel,
                            "ℹ  Choix unique : à la sauvegarde, vous choisirez le nombre de réponses vides à générer. "
                                    + "Marquez ensuite exactement une réponse comme correcte.", false);
            case "choix_multiple" ->
                    setStatus(questionTypeHintLabel,
                            "ℹ  Choix multiple : à la sauvegarde, vous choisirez le nombre de réponses vides à générer. "
                                    + "Marquez toutes les réponses correctes.", false);
            case "vrai_faux" ->
                    setStatus(questionTypeHintLabel,
                            "ℹ  Vrai / Faux : les réponses « Vrai » et « Faux » seront créées automatiquement à la sauvegarde.", false);
            case "texte_libre" ->
                    setStatus(questionTypeHintLabel,
                            "ℹ  Texte libre : aucune réponse prédéfinie — la correction sera manuelle.", false);
            default -> setText(questionTypeHintLabel, "");
        }
    }

    /**
     * Affiche un hint dans la page des réponses selon le type de la question parente.
     */
    private void updateReponsesHint(String type) {
        if (type == null || reponsesMessageLabel == null) return;
        switch (type) {
            case "choix_unique" ->
                    setStatus(reponsesMessageLabel,
                            "ℹ  Choix unique : marquez exactement une réponse comme correcte.", false);
            case "choix_multiple" ->
                    setStatus(reponsesMessageLabel,
                            "ℹ  Choix multiple : marquez toutes les réponses correctes.", false);
            case "vrai_faux" ->
                    setStatus(reponsesMessageLabel,
                            "ℹ  Vrai / Faux : vérifiez que la bonne réponse est bien marquée correcte.", false);
            case "texte_libre" ->
                    setStatus(reponsesMessageLabel,
                            "ℹ  Texte libre : aucune réponse à marquer — correction manuelle.", false);
            default -> setText(reponsesMessageLabel, "");
        }
    }

    // ── VALIDATION ────────────────────────────────────────────────
    private boolean validateQuestionForm() {
        boolean ok = true;
        clearQuestionFormErrors();
        if (questionTexteArea != null && questionTexteArea.getText().trim().isEmpty()) {
            setText(questionTexteErrorLabel, "L'énoncé est obligatoire.");
            ok = false;
        }
        if (questionTypeCombo != null && questionTypeCombo.getValue() == null) {
            setText(questionTypeErrorLabel, "Veuillez choisir un type.");
            ok = false;
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

    private void clearQuestionFormErrors() {
        setText(questionTexteErrorLabel,       "");
        setText(questionTypeErrorLabel,        "");
        setText(questionTypeHintLabel,         "");  // reset du hint
        setText(questionPointsErrorLabel,      "");
        setText(questionExplicationErrorLabel, "");
        setText(questionFormStatusLabel,       "");
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

    // ── UTILITAIRES ───────────────────────────────────────────────
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