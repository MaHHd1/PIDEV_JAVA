module Project3A2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;
    requires javafx.swing;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.desktop;
    requires okhttp3;
    requires org.json;

    opens entities to javafx.base;
    opens gui to javafx.fxml;
    exports main;
}
