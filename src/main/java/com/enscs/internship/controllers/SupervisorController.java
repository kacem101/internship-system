package com.enscs.internship.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class SupervisorController {
    @FXML private ListView<?> offersListView;

    @FXML
    public void showAddOfferDialog() {
        System.out.println("Opening add offer dialog");
    }

    @FXML
    public void handleLogout() {
        System.out.println("Logging out");
    }
}