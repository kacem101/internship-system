package com.enscs.internship.controllers;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.enscs.internship.core.User;
import com.enscs.internship.exceptions.AuthException;
import com.enscs.internship.models.*;
import com.enscs.internship.services.AuthService;
import com.enscs.internship.services.DataManager;
import com.enscs.internship.services.ValidationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField     firstNameField, lastNameField, usernameField, emailField, extraInfoField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label         errorLabel;   // wire this up in register.fxml

    private DataManager dataManager;

    @FXML
    public void handleRegister() {
        // --- basic field checks ---
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String username  = usernameField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();
        String extra     = extraInfoField.getText().trim();
        String role      = roleComboBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty() || role == null || extra.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        // FIX: run validation BEFORE creating the user object
        try {
            ValidationService.validateEmail(email);
            ValidationService.validatePasswordStrength(password);
        } catch (AuthException e) {
            showError(e.getMessage());
            return;
        }

        // FIX: check for duplicate username before saving
        if (dataManager.getUserByUsername(username) != null) {
            showError("Username '" + username + "' is already taken.");
            return;
        }

        int newId = (int) (System.currentTimeMillis() % 100_000);

        // FIX: hash the raw password string — not the one already set on the object
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser;
        if ("Student".equals(role)) {
            newUser = new Student(newId, firstName, lastName, username, email, hashedPassword, extra);
        } else {
            newUser = new Supervisor(newId, firstName, lastName, username, email, hashedPassword, extra);
        }

        dataManager.addUser(newUser);
        goToLogin();
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            AuthService authService = new AuthService(this.dataManager);
            controller.setAuthService(authService);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        // Try the inline label first, fall back to Alert
        if (errorLabel != null) {
            errorLabel.setText(msg);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    public void setDataManager(Object dataManager) {
        this.dataManager = (DataManager) dataManager;
    }
}
