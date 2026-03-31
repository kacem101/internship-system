package com.enscs.internship.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.models.Supervisor;
import com.enscs.internship.services.SessionManager;
import com.enscs.internship.services.InternshipService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SupervisorController implements Initializable {
    @FXML private ListView<InternshipOffer> offersListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;

    private InternshipService internshipService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize service using the SessionManager's data
        this.internshipService = new InternshipService(SessionManager.getDataManager());
        
        // Setup Filter ComboBox
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList("All Offers", "My Company Only"));
            filterComboBox.setValue("All Offers");
        }
        
        refreshList();
    }

    private void refreshList() {
        if (internshipService != null && offersListView != null) {
            offersListView.setItems(FXCollections.observableArrayList(
                internshipService.getActiveOffers()
            ));
        }
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String query = searchField.getText();
        List<InternshipOffer> filtered = internshipService.searchOffers(query);
        offersListView.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleFilterChange() {
        String selected = filterComboBox.getValue();
        if ("My Company Only".equals(selected)) {
            viewMyOffersOnly();
        } else {
            refreshList();
        }
    }

    private void viewMyOffersOnly() {
        Supervisor current = (Supervisor) SessionManager.getUser();
        if (current != null) {
            List<InternshipOffer> myOffers = internshipService.getOffersBySupervisor(current.getCompanyName());
            offersListView.setItems(FXCollections.observableArrayList(myOffers));
        }
    }

    @FXML
public void handleLogout() {
    try {
        // 1. Create the loader manually
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // 2. Get the NEW controller instance and inject the service
        LoginController controller = loader.getController();
        
        // Use the service stored in your SessionManager
        controller.setAuthService(SessionManager.getAuthService());

        // 3. Switch the scene
        Stage stage = (Stage) searchField.getScene().getWindow(); // or any @FXML field
        stage.getScene().setRoot(root);
        stage.setTitle("ENSCS Internship System - Login");
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}
@FXML
public void showAddOfferDialog() {
    try {
        // Ensure the path starts with / and matches your folder structure exactly
        URL fxmlLocation = getClass().getResource("/fxml/post_offer.fxml");
        
        if (fxmlLocation == null) {
            System.err.println("Error: Could not find /fxml/post_offer.fxml. Check your file name!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        PostOfferController controller = loader.getController();
        controller.initData(this.internshipService, (Supervisor) SessionManager.getUser());

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Post New Internship");
        stage.initModality(Modality.APPLICATION_MODAL); // Stops user from clicking dashboard until closed
        stage.showAndWait();
        
        refreshList();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}