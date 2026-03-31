package com.enscs.internship.controllers;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.enscs.internship.core.User;
import com.enscs.internship.models.*;
import com.enscs.internship.services.AuthService;
import com.enscs.internship.services.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField firstNameField, lastNameField, usernameField, emailField, extraInfoField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;

    private DataManager dataManager; // Pass this from Main or AuthService

    @FXML
    public void handleRegister() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return;
        }

        int newId = (int) (System.currentTimeMillis() % 10000); // Simple ID generation
        User newUser;

        if ("Student".equals(roleComboBox.getValue())) {
            newUser = new Student(newId, firstNameField.getText(), lastNameField.getText(), 
                                 usernameField.getText(), emailField.getText(), 
                                 passwordField.getText(), extraInfoField.getText());
        } else {
            newUser = new Supervisor(newId, firstNameField.getText(), lastNameField.getText(), 
                                    usernameField.getText(), emailField.getText(), 
                                    passwordField.getText(), extraInfoField.getText());
        }
        newUser.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
        dataManager.addUser(newUser);
        goToLogin();
    }

    @FXML
public void goToLogin() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // 1. Get the controller for the Login page
        LoginController controller = loader.getController();
        
        // 2. IMPORTANT: Re-inject the AuthService (which holds your DataManager)
        // Note: You might need to pass this from your RegisterController's constructor/setter
        AuthService authService = new AuthService(this.dataManager); 
        controller.setAuthService(authService);

        // 3. Set the scene
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.getScene().setRoot(root);
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    public void setDataManager(Object dataManager2) {
        this.dataManager = (DataManager) dataManager2;
    }
}