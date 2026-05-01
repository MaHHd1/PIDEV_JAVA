package gui;

import entities.Cours;
import entities.Enseignant;
import entities.Etudiant;
import entities.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import services.CoursService;
import utils.UserSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeacherStudentsController implements MainControllerAware {

    @FXML private ComboBox<String> courseFilterCombo;
    @FXML private TableView<Etudiant> studentsTable;
    @FXML private TableColumn<Etudiant, String> matriculeColumn;
    @FXML private TableColumn<Etudiant, String> nameColumn;
    @FXML private TableColumn<Etudiant, String> emailColumn;
    @FXML private TableColumn<Etudiant, String> levelColumn;
    @FXML private TableColumn<Etudiant, String> specialiteColumn;

    private final CoursService coursService = new CoursService();
    private final Map<String, List<Etudiant>> studentMap = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        matriculeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMatricule()));
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomComplet()));
        emailColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        levelColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNiveauEtude()));
        specialiteColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSpecialisation()));

        loadStudents();
        courseFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter(newValue));
    }

    private void loadStudents() {
        Utilisateur utilisateur = UserSession.getCurrentUser();
        if (!(utilisateur instanceof Enseignant enseignant) || enseignant.getId() == null) {
            return;
        }
        try {
            List<Cours> courses = coursService.getByTeacherId(enseignant.getId());
            List<Etudiant> allStudents = new ArrayList<>();
            studentMap.clear();
            studentMap.put("Tous les cours", allStudents);
            for (Cours cours : courses) {
                if (cours.getId() == null) {
                    continue;
                }
                List<Etudiant> students = coursService.getStudentsByCoursId(cours.getId());
                studentMap.put(cours.getTitre(), students);
                for (Etudiant etudiant : students) {
                    if (etudiant.getId() != null && allStudents.stream().noneMatch(existing -> existing.getId().equals(etudiant.getId()))) {
                        allStudents.add(etudiant);
                    }
                }
            }
            courseFilterCombo.setItems(FXCollections.observableArrayList(studentMap.keySet()));
            if (!courseFilterCombo.getItems().isEmpty()) {
                courseFilterCombo.setValue(courseFilterCombo.getItems().get(0));
            }
        } catch (SQLException e) {
            studentsTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void applyFilter(String key) {
        studentsTable.setItems(FXCollections.observableArrayList(studentMap.getOrDefault(key, List.of())));
    }

    @Override
    public void setMainController(MainLayoutEnseignantController controller) {
    }
}
