package com.enscs.internship.controllers;

import com.enscs.internship.services.InternshipService;
import com.enscs.internship.services.SessionManager;
import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.models.Supervisor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PostOfferController {
    @FXML private TextField titleField;
    @FXML private TextField companyField;
    @FXML private TextArea descriptionArea;
    // Note: Ensure your FXML fx:id matches this (requirementsArea)
    @FXML private TextArea requirementsArea; 
    
    private InternshipService internshipService;
    private Supervisor currentSupervisor;

    public void initData(InternshipService service, Supervisor supervisor) {  
        this.internshipService = service; 
        this.currentSupervisor = supervisor;
        
        if (currentSupervisor != null && currentSupervisor.getCompanyName() != null) {
            companyField.setText(currentSupervisor.getCompanyName());
        }
    }

    @FXML
    public void handleSubmit() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String company = companyField.getText().trim();
        String rawRequirements = requirementsArea.getText().trim();

        if (title.isEmpty() || company.isEmpty()) {
            showError("Input Error", "Title and Company Name are required fields.");
            return;
        }

        List<String> requirementsList = Arrays.stream(rawRequirements.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        // Create the object
        InternshipOffer newOffer = new InternshipOffer(title, description, company, requirementsList);
        
        // Use the service to save (it should handle the DataManager calls)
        if (internshipService != null) {
            // Assuming your service.addOffer handles saving to JSON
            internshipService.addOffer(newOffer); 
            
            showInfo("Success", "Internship Offer #" + newOffer.getOfferId() + " has been posted.");
            
            // Close the window after success
            handleCancel(); 
        } else {
            showError("Dependency Error", "InternshipService was not initialized correctly.");
        }
    }

    /**
     * This method fixes the "Location is not set" or "Namespace" error 
     * by providing the target for onAction="#handleCancel"
     */
    @FXML
    public void handleCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}