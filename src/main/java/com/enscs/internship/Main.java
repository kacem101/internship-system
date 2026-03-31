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
public void start(Stage stage) throws Exception {
    dataManager = new DataManager();
    authService = new AuthService(dataManager);

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
    Parent root = loader.load();

    // Set a modern aspect ratio: 1024 width, 700 height
    Scene scene = new Scene(root, 1024, 700);
    
    // Inject service into Login controller
    LoginController controller = loader.getController();
    controller.setAuthService(authService);

    stage.setTitle("ENSCS Internship Management System");
    stage.setScene(scene);
    
    // Prevent the app from being resized too small
    stage.setMinWidth(900);
    stage.setMinHeight(650);
    
    stage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}