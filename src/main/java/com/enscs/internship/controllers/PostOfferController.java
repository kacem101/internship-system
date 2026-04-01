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
    @FXML private TextArea  descriptionArea;
    @FXML private TextArea  requirementsArea;   // one requirement per line
    @FXML private TextField tagsField;          // comma-separated tags
    @FXML private Button    submitButton;

    private InternshipService internshipService;
    private InternshipOffer   existingOffer = null;

    // ------------------------------------------------------------------ //
    //  Init: CREATE mode                                                   //
    // ------------------------------------------------------------------ //
    public void initData(InternshipService service, Supervisor supervisor) {
        this.internshipService = service;
        if (supervisor != null) {
            companyField.setText(supervisor.getCompanyName());
            companyField.setEditable(false);
            companyField.setStyle("-fx-background-color: #eee;");
        }
    }

    // ------------------------------------------------------------------ //
    //  Init: EDIT mode                                                     //
    // ------------------------------------------------------------------ //
    public void initData(InternshipService service, InternshipOffer offer) {
        this.internshipService = service;
        this.existingOffer     = offer;

        titleField.setText(offer.getTitle());
        companyField.setText(offer.getCompanyName());
        descriptionArea.setText(offer.getDescription());

        // FIX: guard against null lists that Gson may leave on old records
        List<String> reqs = offer.getRequirements();
        if (reqs != null && !reqs.isEmpty()) {
            requirementsArea.setText(String.join("\n", reqs));
        }

        List<String> tags = offer.getTags();
        if (tags != null && !tags.isEmpty()) {
            tagsField.setText(String.join(", ", tags));
        }

        submitButton.setText("Update Offer");
    }

    // ------------------------------------------------------------------ //
    //  Submit                                                              //
    // ------------------------------------------------------------------ //
    @FXML
    public void handleSubmit() {
        String title       = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String company     = companyField.getText().trim();
        String rawReqs     = requirementsArea.getText().trim();
        String rawTags     = tagsField.getText().trim();

        if (title.isEmpty() || company.isEmpty()) {
            showError("Validation Error", "Title and Company are required.");
            return;
        }

        // Each non-empty line → one requirement
        List<String> requirementsList = Arrays.stream(rawReqs.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Comma-separated tags
        List<String> tagsList = Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (existingOffer != null) {
            // --- UPDATE ---
            existingOffer.setTitle(title);
            existingOffer.setDescription(description);
            existingOffer.setCompanyName(company);
            existingOffer.setRequirements(requirementsList);
            existingOffer.setTags(tagsList);
            SessionManager.getDataManager().saveData();
            showInfo("Success", "Offer updated successfully.");
        } else {
            // --- CREATE ---
            // Use the full constructor so both lists are properly assigned
            InternshipOffer newOffer = new InternshipOffer(title, description, company,
                                                           requirementsList, tagsList);
            internshipService.addOffer(newOffer);
            showInfo("Success", "New internship offer posted.");
        }

        handleCancel();
    }

    @FXML
    public void handleCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    // ------------------------------------------------------------------ //
    //  Helpers                                                             //
    // ------------------------------------------------------------------ //
    private void showInfo(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showError(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
