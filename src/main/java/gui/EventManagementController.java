package gui;

import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import services.EvenementService;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class EventManagementController {

    @FXML
    private TextField titreField;
    @FXML
    private TextField lieuField;
    @FXML
    private TextField typeField;
    @FXML
    private TableView<Evenement> eventTable;
    @FXML
    private TableColumn<Evenement, String> colTitre;
    @FXML
    private TableColumn<Evenement, String> colLieu;
    @FXML
    private TableColumn<Evenement, String> colType;

    private EvenementService es = new EvenementService();
    private ObservableList<Evenement> eventList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type_evenement"));

        loadEvents();
    }

    private void loadEvents() {
        try {
            eventList.setAll(es.readAll());
            eventTable.setItems(eventList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        Evenement ev = new Evenement(
                1, // createur_id par défaut
                titreField.getText(),
                "Description automatique",
                typeField.getText(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                lieuField.getText(),
                100,
                "En attente",
                "Public"
        );

        try {
            es.create(ev);
            loadEvents();
            handleVider(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleVider(ActionEvent event) {
        titreField.clear();
        lieuField.clear();
        typeField.clear();
    }
}
