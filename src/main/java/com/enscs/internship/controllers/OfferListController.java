package com.enscs.internship.controllers;

import com.enscs.internship.models.*;
import com.enscs.internship.services.InternshipService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

public class OfferListController {

    @FXML private TableView<InternshipOffer>          table;
    @FXML private TableColumn<InternshipOffer, String> titleCol;
    @FXML private TableColumn<InternshipOffer, String> companyCol;
    @FXML private TableColumn<InternshipOffer, String> statusCol;  // FIX: now properly bound

    private InternshipService service;
    private Student           currentStudent;

    public void initData(InternshipService service, Student student) {
        this.service        = service;
        this.currentStudent = student;

        titleCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));
        companyCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCompanyName()));

        // FIX: bind the status column that was declared but never wired
        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isOpen() ? "Open" : "Closed"));

        table.setItems(FXCollections.observableArrayList(service.getActiveOffers()));
    }

    @FXML
    private void handleApply() {
        InternshipOffer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an offer first.").show();
            return;
        }
        try {
            service.apply(currentStudent, selected);
            new Alert(Alert.AlertType.INFORMATION, "Application submitted!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}
