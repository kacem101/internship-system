package com.enscs.internship.controllers;

import com.enscs.internship.services.AuthService;

import java.io.IOException;

import com.enscs.internship.core.User;
import com.enscs.internship.exceptions.AuthException;
import com.enscs.internship.models.Student;

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
    String user = usernameField.getText();
    String pass = passwordField.getText();

    try {
        // 1. Perform login (this throws AuthException if it fails)
        authService.login(user, pass);
        
        // 2. Get the logged-in user to see their role
        User loggedUser = authService.getCurrentUser();
        
        // 3. Determine which dashboard to load
        String fxmlPath = (loggedUser instanceof Student) 
            ? "/fxml/student_dashboard.fxml" 
            : "/fxml/supervisor_dashboard.fxml";

        // 4. Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // 5. Switch the stage
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("ENSCS — Dashboard (" + loggedUser.getFirstName() + ")");
        stage.centerOnScreen(); // Good practice after changing root

    } catch (AuthException e) {
        errorLabel.setText(e.getMessage());
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
    } catch (IOException e) {
        errorLabel.setText("Error loading Dashboard UI.");
        e.printStackTrace();
    }
    }
    @FXML
    public void handleGoToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();

            // Pass the AuthService/DataManager to the RegisterController if needed
            RegisterController controller = loader.getController();
            controller.setDataManager(authService.getDataManager()); 

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("ENSCS — Create Account");
        } catch (IOException e) {
            errorLabel.setText("Error loading registration page.");
            e.printStackTrace();
        }
    }
}