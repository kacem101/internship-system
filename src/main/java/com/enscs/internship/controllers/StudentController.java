package com.enscs.internship.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.enscs.internship.models.Application;
import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.models.Student;
import com.enscs.internship.services.SessionManager;
import com.enscs.internship.services.InternshipService;
import com.enscs.internship.exceptions.InternshipException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class StudentController implements Initializable {
    @FXML private Label welcomeLabel;
    
    // Main Offers Table
    @FXML private TableView<InternshipOffer> offersTable;
    @FXML private TableColumn<InternshipOffer, String> colTitle;
    @FXML private TableColumn<InternshipOffer, String> colCompany;
    @FXML private TableColumn<InternshipOffer, String> colDeadline;

    // Applications Status Table
    @FXML private TableView<Application> statusTable;
    @FXML private TableColumn<Application, Integer> colAppOfferId;
    @FXML private TableColumn<Application, String> colAppStatus;

    private InternshipService internshipService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.internshipService = new InternshipService(SessionManager.getDataManager());

        // 1. Set Welcome Message
        if (SessionManager.getUser() != null) {
            welcomeLabel.setText("Welcome back, " + SessionManager.getUser().getFirstName() + "!");
        }

        // 2. Setup Offers Table
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        // Deadline field needs to exist in InternshipOffer model
        // colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline")); 

        // 3. Setup Status Table (If present in FXML)
        if (statusTable != null) {
            colAppOfferId.setCellValueFactory(new PropertyValueFactory<>("offerId"));
            colAppStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        refreshTable();
        loadApplicationStatuses();
    }

    private void refreshTable() {
        offersTable.setItems(FXCollections.observableArrayList(internshipService.getActiveOffers()));
    }

    @FXML
private void handleApply() {
    // 1. Get the selected offer from your UI (ListView or TableView)
    InternshipOffer selectedOffer = offersTable.getSelectionModel().getSelectedItem();

    if (selectedOffer == null) {
        showError("No Selection", "Please select an internship offer first.");
        return;
    }

    // 2. Get current student (assuming you have a SessionManager)
    Student currentStudent = (Student) SessionManager.getUser();

    // 3. Create the Application object
    // Assuming Application constructor: (String studentUsername, int offerId)
    Application newApplication = new Application(
        currentStudent.getUsername(), 
        selectedOffer.getOfferId()
    );

    // 4. Check if already applied (Optional but recommended)
    boolean alreadyApplied = SessionManager.getDataManager().getApplications().stream()
            .anyMatch(a -> a.getStudentUsername().equals(currentStudent.getUsername()) 
                      && a.getOfferId() == selectedOffer.getOfferId());

    if (alreadyApplied) {
        showError("Already Applied", "You have already submitted an application for this position.");
        return;
    }

    // 5. Save the application
    SessionManager.getDataManager().getApplications().add(newApplication);
    SessionManager.getDataManager().saveData(); // Persist to JSON/File

    showInfo("Success", "Your application for " + selectedOffer.getTitle() + " has been sent!");
}

    private void showInfo(String string, String string2) {
		showAlert(Alert.AlertType.INFORMATION, string, string2);
	}

	private void showError(String string, String string2) {
		showAlert(Alert.AlertType.ERROR, string, string2);
	}

	public void loadApplicationStatuses() {
        Student current = (Student) SessionManager.getUser();
        if (current != null && statusTable != null) {
            List<Application> myApps = internshipService.getStudentApplications(current.getUsername());
            statusTable.setItems(FXCollections.observableArrayList(myApps));
        }
    }

    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("ENSCS Internship System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}