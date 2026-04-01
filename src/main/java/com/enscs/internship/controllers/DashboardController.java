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

    @FXML private Label     welcomeLabel;
    @FXML private Button    postOfferButton;
    @FXML private StackPane contentArea;

    private AuthService       authService;
    private InternshipService internshipService;

    public void initData(AuthService authService, InternshipService internshipService) {
        this.authService       = authService;
        this.internshipService = internshipService;

        User user = authService.getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName());

        boolean isSupervisor = (user instanceof Supervisor);
        postOfferButton.setVisible(isSupervisor);
        postOfferButton.setManaged(isSupervisor);
    }

    private FXMLLoader loadView(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlName));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            return loader;
        } catch (IOException e) {
            showError("Navigation Error", "Could not load: " + fxmlName, e.getMessage());
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
        FXMLLoader loader = loadView("post_offer.fxml");
        if (loader != null && authService.getCurrentUser() instanceof Supervisor) {
            PostOfferController controller = loader.getController();
            controller.initData(internshipService, (Supervisor) authService.getCurrentUser());
        }
    }

    /**
     * FIX: was loading a non-existent "applications_list.fxml".
     * Now routes by role:
     *  - Student  → loads the offers_list panel (student sees their own apps via StudentDashboard)
     *  - Supervisor → loads the ApplicationsController view
     */
    @FXML
    public void handleViewApplications() {
        User user = authService.getCurrentUser();
        if (user instanceof Supervisor) {
            // Supervisor: show the full applications management panel
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/applications_list.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().setAll(view);
            } catch (IOException e) {
                showError("UI Error", "Could not load applications panel.", e.getMessage());
            }
        } else {
            // Student: re-use the offers list (they track apps in StudentDashboard's second tab)
            handleViewOffers();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            authService.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginView = loader.load();
            LoginController controller = loader.getController();
            controller.setAuthService(authService);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.setTitle("ENSCS — Login");
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
