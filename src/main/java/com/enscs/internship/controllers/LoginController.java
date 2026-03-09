package com.enscs.internship.controllers;

import com.enscs.internship.services.AuthService;
import com.enscs.internship.exceptions.AuthException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    public void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        try {
            authService.login(user, pass);
            // If successful, we would switch scenes here
            errorLabel.setText("Login Successful! Welcome " + authService.getCurrentUser().getFirstName());
            errorLabel.setStyle("-fx-text-fill: #2ecc71;");
        } catch (AuthException e) {
            // This catches your custom exception and shows the message to the user
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
}