package com.enscs.internship.controllers;

import com.enscs.internship.models.*;
import com.enscs.internship.services.InternshipService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

public class OfferListController {
    @FXML private TableView<InternshipOffer> table;
    @FXML private TableColumn<InternshipOffer, String> titleCol, companyCol;

    private InternshipService service;
    private Student currentStudent;

    public void initData(InternshipService service, Student student) {
        this.service = service;
        this.currentStudent = student;
        
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        companyCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCompanyName()));
        
        table.setItems(FXCollections.observableArrayList(service.getActiveOffers()));
    }

    @FXML
    private void handleApply() {
        InternshipOffer selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                service.apply(currentStudent, selected);
                new Alert(Alert.AlertType.INFORMATION, "Success!").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }
}