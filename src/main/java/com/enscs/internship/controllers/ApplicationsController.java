package com.enscs.internship.controllers;

import com.enscs.internship.models.Application;
import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.models.Supervisor;
import com.enscs.internship.models.ApplicationStatus;
import com.enscs.internship.services.InternshipService;
import com.enscs.internship.services.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ApplicationsController {
    @FXML private TableView<Application> appTable;
    @FXML private TableColumn<Application, String> studentCol;
    @FXML private TableColumn<Application, String> offerCol;
    @FXML private TableColumn<Application, String> statusCol;

    private InternshipService service;

    public void initialize() {
        this.service = new InternshipService(SessionManager.getDataManager());
        Supervisor current = (Supervisor) SessionManager.getUser();

        studentCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStudentUsername()));
        
        offerCol.setCellValueFactory(data -> {
            int id = data.getValue().getOfferId();
            InternshipOffer offer = service.getOfferById(id);
            return new SimpleStringProperty(offer != null ? offer.getTitle() : "Unknown ID: " + id);
        });
        
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));

        if (current != null) {
            loadApplications(current.getCompanyName());
        }
    }

    private void loadApplications(String companyName) {
        appTable.setItems(FXCollections.observableArrayList(
            service.getApplicationsForSupervisor(companyName)
        ));
    }

    @FXML
    private void handleAccept() {
        updateStatus(ApplicationStatus.ACCEPTED);
    }

    @FXML
    private void handleReject() {
        updateStatus(ApplicationStatus.REJECTED);
    }

    private void updateStatus(ApplicationStatus status) {
        Application selected = appTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus(status);
            SessionManager.getDataManager().saveData(); 
            appTable.refresh();
        }
    }
}