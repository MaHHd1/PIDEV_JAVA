package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import services.AuthService;
import utils.SceneManager;

import java.io.IOException;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label feedbackLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleResetRequest() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String newPassword = newPasswordField.getText() != null ? newPasswordField.getText() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";

        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            feedbackLabel.setText("Fill email, new password, and confirmation.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            feedbackLabel.setText("Password confirmation does not match.");
            return;
        }

        if (newPassword.length() < 8) {
            feedbackLabel.setText("Password must contain at least 8 characters.");
            return;
        }

        try {
            boolean updated = authService.resetPassword(email, newPassword);
            if (updated) {
                feedbackLabel.setText("Password updated. Reset token data was also refreshed for later implementation.");
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                feedbackLabel.setText("No account found for this email.");
            }
        } catch (SQLException | IOException e) {
            feedbackLabel.setText("Password reset failed: " + e.getMessage());
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
