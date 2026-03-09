package com.enscs.internship;

import com.enscs.internship.services.*;
import com.enscs.internship.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private DataManager dataManager;
    private AuthService authService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Initialize Services
        dataManager = new DataManager();
        authService = new AuthService(dataManager);

        // 2. Load UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // 3. Inject Service into Controller
        LoginController controller = loader.getController();
        controller.setAuthService(authService);

        // 4. Show Window
        primaryStage.setTitle("ENSCS Internship Management");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}