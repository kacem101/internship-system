package com.enscs.internship.controllers;

import com.enscs.internship.services.AuthService;
import com.enscs.internship.services.SessionManager;
import com.enscs.internship.services.DataManager; // Ensure this is imported
import com.enscs.internship.core.User;
import com.enscs.internship.exceptions.AuthException;
import com.enscs.internship.models.Student;
import com.enscs.internship.models.Supervisor;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    if (this.authService == null) {
        this.authService = SessionManager.getAuthService();
    }
    try {
        authService.login(usernameField.getText(), passwordField.getText());
        User loggedUser = authService.getCurrentUser();

        // 1. UPDATE SESSION FIRST
        SessionManager.setUser(loggedUser);
        
        // 2. CHOOSE PATH
        String fxml = (loggedUser instanceof Supervisor) 
                      ? "/fxml/supervisor_dashboard.fxml" 
                      : "/fxml/student_dashboard.fxml";

        // 3. LOAD
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.getScene().setRoot(root);

    } catch (AuthException e) {
        errorLabel.setText(e.getMessage());
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
private void handleGoToRegister() {
    try {
        // Safety net: if local authService is null, pull from global session
        if (this.authService == null) {
            this.authService = SessionManager.getAuthService();
        }

        if (this.authService == null) {
            System.err.println("Critical Error: AuthService is null in SessionManager!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Parent root = loader.load();

        RegisterController regController = loader.getController();
        // Use the dataManager from the service
        regController.setDataManager(authService.getDataManager());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.getScene().setRoot(root);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}