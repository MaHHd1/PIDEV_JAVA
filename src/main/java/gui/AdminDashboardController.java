package gui;

import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.UtilisateurService;
import utils.SceneManager;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label studentsCountLabel;

    @FXML
    private Label teachersCountLabel;

    @FXML
    private Label adminsCountLabel;

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
    private ListView<String> activityList;

    private final ObservableList<UtilisateurRow> userRows = FXCollections.observableArrayList();
    private final ObservableList<String> activityItems = FXCollections.observableArrayList();
    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        usersTable.setItems(userRows);
        activityList.setItems(activityItems);

        Utilisateur currentUser = UserSession.getCurrentUser();
        welcomeLabel.setText(currentUser != null ? "Welcome back, " + currentUser.getPrenom() : "Welcome back");

        loadDashboardData();
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
    }

    @FXML
    private void logout() throws IOException {
        UserSession.clear();
        SceneManager.switchScene("/gui/login.fxml", "Campus Access");
    }

    private void loadDashboardData() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
            userRows.clear();
            activityItems.clear();

            long students = 0;
            long teachers = 0;
            long admins = 0;

            for (Utilisateur utilisateur : utilisateurs) {
                userRows.add(new UtilisateurRow(
                        utilisateur.getId(),
                        utilisateur.getNomComplet(),
                        utilisateur.getEmail(),
                        utilisateur.getType()
                ));

                if ("etudiant".equalsIgnoreCase(utilisateur.getType())) {
                    students++;
                } else if ("enseignant".equalsIgnoreCase(utilisateur.getType())) {
                    teachers++;
                } else if ("administrateur".equalsIgnoreCase(utilisateur.getType())) {
                    admins++;
                }
            }

            totalUsersLabel.setText(String.valueOf(utilisateurs.size()));
            studentsCountLabel.setText(String.valueOf(students));
            teachersCountLabel.setText(String.valueOf(teachers));
            adminsCountLabel.setText(String.valueOf(admins));

            activityItems.add("Live registry loaded from database");
            activityItems.add("Users are grouped by role and bound through ObservableList");
            activityItems.add("Refresh keeps the dashboard synchronized");
            activityItems.add("Desktop login is now role-based");
        } catch (SQLException e) {
            activityItems.setAll("Failed to load admin dashboard: " + e.getMessage());
        }
    }
}
