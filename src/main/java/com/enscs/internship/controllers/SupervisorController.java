package com.enscs.internship.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.enscs.internship.models.*;
import com.enscs.internship.services.SessionManager;
import com.enscs.internship.services.InternshipService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SupervisorController implements Initializable {

    // Layout panels
    @FXML private VBox offersView;
    @FXML private VBox applicantsView;

    // Offers
    @FXML private ListView<InternshipOffer> offersListView;
    @FXML private TextField     searchField;
    @FXML private ComboBox<String> filterComboBox;

    // Applications
    @FXML private TableView<Application>            applicationsTable;
    @FXML private TableColumn<Application, String>  colStudent;
    @FXML private TableColumn<Application, String>  colOfferTitle;   // show title, not raw ID
    @FXML private TableColumn<Application, String>  colDate;
    @FXML private TableColumn<Application, String>  colStatus;

    private InternshipService internshipService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.internshipService = new InternshipService(SessionManager.getDataManager());

        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList("All Offers", "My Company Only"));
            filterComboBox.setValue("All Offers");
        }
        searchField.textProperty().addListener((obs, old, newValue) -> performSearch(newValue));

        setupApplicationsTable();
        refreshList();
        refreshApplications();
    }

    private void setupApplicationsTable() {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentUsername"));

        // FIX: resolve offer title from ID rather than showing a raw int
        colOfferTitle.setCellValueFactory(data -> {
            InternshipOffer offer = internshipService.getOfferById(data.getValue().getOfferId());
            return new SimpleStringProperty(offer != null ? offer.getTitle() : "Offer #" + data.getValue().getOfferId());
        });

        // FIX: applicationDate now has a getter — this works correctly
        colDate.setCellValueFactory(data -> {
            var date = data.getValue().getApplicationDate();
            return new SimpleStringProperty(date != null ? date.toString() : "—");
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    // --- Navigation ---
    @FXML public void showOffersView() {
        offersView.setVisible(true);
        applicantsView.setVisible(false);
        refreshList();
    }

    @FXML public void showApplicantsView() {
        offersView.setVisible(false);
        applicantsView.setVisible(true);
        refreshApplications();
    }

    // --- Applications ---
    private void refreshApplications() {
        Supervisor current = (Supervisor) SessionManager.getUser();
        if (current == null) return;

        List<Application> myApps = SessionManager.getDataManager().getApplications().stream()
                .filter(app -> {
                    InternshipOffer offer = SessionManager.getDataManager().getOfferById(app.getOfferId());
                    return offer != null && offer.getCompanyName().equals(current.getCompanyName());
                })
                .collect(Collectors.toList());

        applicationsTable.setItems(FXCollections.observableArrayList(myApps));
    }

    @FXML private void handleApprove() { updateStatus(ApplicationStatus.ACCEPTED); }
    @FXML private void handleReject()  { updateStatus(ApplicationStatus.REJECTED);  }

    private void updateStatus(ApplicationStatus status) {
        Application selected = applicationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an applicant from the table.");
            return;
        }
        selected.setStatus(status);
        SessionManager.getDataManager().saveData();
        applicationsTable.refresh();
        showInfo("Updated", "Status set to: " + status);
    }

    // --- Offers ---
    private void refreshList() {
        if (internshipService != null && offersListView != null) {
            offersListView.setItems(FXCollections.observableArrayList(internshipService.getActiveOffers()));
        }
    }

    private void performSearch(String query) {
        offersListView.setItems(FXCollections.observableArrayList(internshipService.searchOffers(query)));
    }

    @FXML private void handleFilterChange() {
        if ("My Company Only".equals(filterComboBox.getValue())) {
            viewMyOffersOnly();
        } else {
            refreshList();
        }
    }

    private void viewMyOffersOnly() {
        Supervisor current = (Supervisor) SessionManager.getUser();
        if (current != null) {
            offersListView.setItems(FXCollections.observableArrayList(
                    internshipService.getOffersBySupervisor(current.getCompanyName())));
        }
    }

    @FXML public void handleEditOffer() {
        InternshipOffer selected = offersListView.getSelectionModel().getSelectedItem();
        if (selected != null) showOfferDialog(selected);
    }

    @FXML public void showAddOfferDialog() { showOfferDialog(null); }

    private void showOfferDialog(InternshipOffer offerToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/post_offer.fxml"));
            Parent root = loader.load();
            PostOfferController controller = loader.getController();
            if (offerToEdit != null) {
                controller.initData(internshipService, offerToEdit);
            } else {
                controller.initData(internshipService, (Supervisor) SessionManager.getUser());
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setAuthService(SessionManager.getAuthService());
            Stage stage = (Stage) offersListView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- Alert helpers ---
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showInfo(String title, String content)  { showAlert(Alert.AlertType.INFORMATION, title, content); }
    private void showError(String title, String content) { showAlert(Alert.AlertType.ERROR, title, content); }
}
