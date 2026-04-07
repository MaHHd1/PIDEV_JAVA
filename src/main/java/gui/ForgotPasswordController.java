package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import services.AuthService;
import utils.SceneManager;

import java.io.IOException;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label feedbackLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleResetRequest() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        if (email.isEmpty()) {
            feedbackLabel.setText("Enter your email first.");
            return;
        }

        try {
            boolean updated = authService.requestPasswordReset(email);
            if (updated) {
                feedbackLabel.setText("Reset request saved in database. Token expires in 30 minutes.");
            } else {
                feedbackLabel.setText("No account found for this email.");
            }
        } catch (SQLException e) {
            feedbackLabel.setText("Reset request failed: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        try {
            SceneManager.switchScene("/gui/login.fxml", "Campus Access");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to return to login.");
        }
    }
}
