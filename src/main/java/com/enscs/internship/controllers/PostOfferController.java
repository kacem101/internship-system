package com.enscs.internship.controllers;

import com.enscs.internship.services.InternshipService;
import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.models.Supervisor;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PostOfferController {
    @FXML private TextField titleField;
    @FXML private TextField companyField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea requirementsArea;
    @FXML private Button submitButton;
    
    private InternshipService internshipService;
    private Supervisor currentSupervisor;

    /**
     * Updated to accept both the service and the current supervisor context.
     * This ensures the offer can be linked to the poster's record.
     */
    public void initData(InternshipService service, Supervisor supervisor) {  
        this.internshipService = service; 
        this.currentSupervisor = supervisor;
        
        // Pre-fill company name if the supervisor has one defined
        if (currentSupervisor != null && currentSupervisor.getCompanyName() != null) {
            companyField.setText(currentSupervisor.getCompanyName());
        }
    }

    /**
     * Reads the form, creates an InternshipOffer, and saves it through the service.
     */
    @FXML
public void handleSubmit() {
    String title = titleField.getText().trim();
    String description = descriptionArea.getText().trim();
    String company = companyField.getText().trim();
    String[] requirements = requirementsArea.getText().split("\n");

    // 1. Create the object using your existing constructor
    InternshipOffer newOffer = new InternshipOffer(title, description, company, requirements);

    // 2. Save it via the service
    boolean success = internshipService.saveOffer(newOffer);

    if (success) {
        showInfo("Success", "Offer saved to offers.json!");
        clearForm();
    }
    }
    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        requirementsArea.clear();
        // We keep the company name as it's likely the same for the next post
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