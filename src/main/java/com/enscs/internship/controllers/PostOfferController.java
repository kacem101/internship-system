package com.enscs.internship.controllers;

import com.enscs.internship.services.InternshipService;
import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.models.Supervisor;
import com.enscs.internship.services.SessionManager;
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
    @FXML private TextArea requirementsArea; 
    @FXML private TextField tagsField;
    @FXML private Button submitButton;

    private InternshipService internshipService;
    private InternshipOffer existingOffer = null; // Track if we are editing

    /**
     * Initializes the dialog for a NEW offer
     */
    public void initData(InternshipService service, Supervisor supervisor) {  
        this.internshipService = service; 
        if (supervisor != null) {
            companyField.setText(supervisor.getCompanyName());
            companyField.setEditable(false); // Supervisors usually shouldn't change company name
        }
    }

    /**
     * Overloaded method to initialize the dialog for EDITING an existing offer
     */
    public void initData(InternshipService service, InternshipOffer offer) {
        this.internshipService = service;
        this.existingOffer = offer;

        // Pre-fill fields
        titleField.setText(offer.getTitle());
        companyField.setText(offer.getCompanyName());
        descriptionArea.setText(offer.getDescription());
        
        if (offer.getRequirements() != null) {
            requirementsArea.setText(String.join("\n", offer.getRequirements()));
        }
        
        if (offer.getTags() != null) {
            tagsField.setText(String.join(", ", offer.getTags()));
        }

        submitButton.setText("Update Offer");
    }

    @FXML
    public void handleSubmit() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String company = companyField.getText().trim();
        String rawRequirements = requirementsArea.getText().trim();
        String rawTags = tagsField.getText().trim();

        if (title.isEmpty() || company.isEmpty()) {
            showError("Input Error", "Title and Company Name are required fields.");
            return;
        }

        List<String> requirementsList = Arrays.stream(rawRequirements.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        List<String> tagsList = Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());

        if (existingOffer != null) {
            // UPDATE MODE
            existingOffer.setTitle(title);
            existingOffer.setDescription(description);
            existingOffer.setCompanyName(company);
            existingOffer.setRequirements(requirementsList);
            // Ensure your InternshipOffer model has a setTags method
            // existingOffer.setTags(tagsList); 
            
            SessionManager.getDataManager().saveData(); // Persist changes
            showInfo("Success", "Offer updated successfully.");
        } else {
            // CREATE MODE
            InternshipOffer newOffer = new InternshipOffer(title, description, company, requirementsList, tagsList);
            internshipService.addOffer(newOffer);
            showInfo("Success", "New offer posted.");
        }
        handleCancel();
    }

    @FXML
    public void handleCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}