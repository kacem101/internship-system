package com.enscs.internship.controllers;

import com.enscs.internship.core.User;
import com.enscs.internship.models.Student;
import com.enscs.internship.models.Supervisor;
import com.enscs.internship.services.AuthService;
import com.enscs.internship.services.InternshipService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button postOfferButton;
    @FXML private StackPane contentArea;

    private AuthService authService;
    private InternshipService internshipService;

    /**
     * Initializes the dashboard with necessary services and user context.
     */
    public void initData(AuthService authService, InternshipService internshipService) {
        this.authService = authService;
        this.internshipService = internshipService;
        
        User user = authService.getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName());

        // Role-based UI management
        boolean isSupervisor = (user instanceof Supervisor);
        postOfferButton.setVisible(isSupervisor);
        postOfferButton.setManaged(isSupervisor);
    }

    /**
     * Loads FXML from the /fxml/ directory and manages content switching.
     */
    private FXMLLoader loadView(String fxmlName) {
        try {
            // Path corrected to match your resources/fxml/ structure
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlName));
            Parent view = loader.load();
            
            contentArea.getChildren().setAll(view);
            return loader;
        } catch (IOException e) {
            showError("Navigation Error", "Could not load: " + fxmlName, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void handleViewOffers() {
        FXMLLoader loader = loadView("offers_list.fxml");
        if (loader != null && authService.getCurrentUser() instanceof Student) {
            OfferListController controller = loader.getController();
            controller.initData(internshipService, (Student) authService.getCurrentUser());
        }
    }

    @FXML
    public void handlePostOffer() {
        FXMLLoader loader = loadView("post_offer_form.fxml");
        if (loader != null && authService.getCurrentUser() instanceof Supervisor) {
            PostOfferController controller = loader.getController();
            // Pass both the service and the specific supervisor model
            controller.initData(internshipService, (Supervisor) authService.getCurrentUser());
        }
    }

    @FXML
    public void handleViewApplications() {
        loadView("applications_list.fxml");
        // Logic for ApplicationsController goes here once implemented
    }

    @FXML
    public void handleLogout() {
        try {
            authService.logout();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginView = loader.load();

            // Setup the login controller with the service again
            LoginController controller = loader.getController();
            controller.setAuthService(authService);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.setTitle("ENSCS — Login");
            stage.show();

        } catch (IOException e) {
            showError("Logout Error", "Failed to return to login screen.", e.getMessage());
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}