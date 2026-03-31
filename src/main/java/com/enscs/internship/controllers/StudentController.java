package com.enscs.internship.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class StudentController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<?> offersTable;
    @FXML private TableColumn<?, ?> colTitle;
    @FXML private TableColumn<?, ?> colCompany;
    @FXML private TableColumn<?, ?> colDeadline;

    @FXML
    public void handleApply() {
        System.out.println("Apply button clicked");
    }

    @FXML
    public void handleLogout() {
        System.out.println("Logout button clicked");
    }
}