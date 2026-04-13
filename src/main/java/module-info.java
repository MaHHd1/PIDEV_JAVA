module Project3A2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens entities to javafx.base;
    opens gui to javafx.fxml;
    exports main;
}
