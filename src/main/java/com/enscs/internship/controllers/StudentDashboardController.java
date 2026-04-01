package com.enscs.internship.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.enscs.internship.core.User;
import com.enscs.internship.models.Application;
import com.enscs.internship.models.InternshipOffer;
import com.enscs.internship.services.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StudentDashboardController implements Initializable {

    @FXML private ListView<InternshipOffer> offersListView;
    @FXML private TextField searchField;

    // Detail panel
    @FXML private Label detailTitle;
    @FXML private Label detailCompany;
    @FXML private Label detailDescription;
    @FXML private Label detailRequirements;
    @FXML private Label detailTags;        // shown in student_dashboard.fxml (see FXML fix)
    @FXML private Button applyButton;

    // Applications tab
    @FXML private TableView<Application>         applicationsTable;
    @FXML private TableColumn<Application, String> colOfferTitle;
    @FXML private TableColumn<Application, String> colAppDate;
    @FXML private TableColumn<Application, String> colStatus;

    private final ObservableList<InternshipOffer> allOffers = FXCollections.observableArrayList();
    private FilteredList<InternshipOffer> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (SessionManager.getDataManager() == null) return;

        // 1. Load offers
        allOffers.addAll(SessionManager.getDataManager().getOffers());
        filteredData = new FilteredList<>(allOffers, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        offersListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(InternshipOffer item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle() + " @ " + item.getCompanyName());
            }
        });

        offersListView.setItems(filteredData);
        offersListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> { if (newVal != null) displayOfferDetails(newVal); });

        // 2. Applications table
        setupApplicationsTable();
        refreshApplications();
    }

    private void setupApplicationsTable() {
        // Offer title — look it up from allOffers by ID
        colOfferTitle.setCellValueFactory(data -> {
            int id = data.getValue().getOfferId();
            return new SimpleStringProperty(allOffers.stream()
                    .filter(o -> o.getOfferId() == id)
                    .map(InternshipOffer::getTitle)
                    .findFirst().orElse("Offer #" + id));
        });

        // FIX: was showing applicationId; now shows the actual date
        colAppDate.setCellValueFactory(data -> {
            var date = data.getValue().getApplicationDate();
            return new SimpleStringProperty(date != null ? date.toString() : "—");
        });

        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().toString()));
    }

    @FXML
    public void refreshApplications() {
        User user = SessionManager.getUser();
        if (user == null) return;

        List<Application> myApps = SessionManager.getDataManager().getApplications().stream()
                .filter(a -> a.getStudentUsername().equals(user.getUsername()))
                .collect(Collectors.toList());

        applicationsTable.setItems(FXCollections.observableArrayList(myApps));
    }

    private void displayOfferDetails(InternshipOffer offer) {
        detailTitle.setText(offer.getTitle());
        detailCompany.setText("🏢 " + offer.getCompanyName());
        detailDescription.setText(offer.getDescription() != null ? offer.getDescription() : "—");
        detailRequirements.setText(
                offer.getRequirements() != null && !offer.getRequirements().isEmpty()
                        ? "• " + String.join("\n• ", offer.getRequirements())
                        : "None listed.");

        // FIX: show tags in the detail panel
        if (detailTags != null) {
            List<String> tags = offer.getTags();
            detailTags.setText(
                    tags != null && !tags.isEmpty()
                            ? String.join("  |  ", tags)
                            : "No tags.");
        }

        checkIfAlreadyApplied(offer);
    }

    private boolean hasAlreadyApplied(String username, int offerId) {
        return SessionManager.getDataManager().getApplications().stream()
                .anyMatch(a -> a.getStudentUsername().equals(username) && a.getOfferId() == offerId);
    }

    private void checkIfAlreadyApplied(InternshipOffer offer) {
        User user = SessionManager.getUser();
        if (user == null) return;

        if (hasAlreadyApplied(user.getUsername(), offer.getOfferId())) {
            applyButton.setDisable(true);
            applyButton.setText("Already Applied");
            applyButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        } else {
            applyButton.setDisable(false);
            applyButton.setText("Apply Now");
            applyButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        }
    }

    @FXML
    private void handleApply() {
        InternshipOffer selected = offersListView.getSelectionModel().getSelectedItem();
        User user = SessionManager.getUser();
        if (selected == null || user == null) return;

        if (hasAlreadyApplied(user.getUsername(), selected.getOfferId())) {
            showAlert(Alert.AlertType.ERROR, "Error", "You have already applied for this position.");
            checkIfAlreadyApplied(selected);
            return;
        }

        Application newApp = new Application(user.getUsername(), selected.getOfferId());
        SessionManager.getDataManager().getApplications().add(newApp);
        SessionManager.getDataManager().saveData();

        showAlert(Alert.AlertType.INFORMATION, "Success", "Application submitted!");
        checkIfAlreadyApplied(selected);
        refreshApplications();
    }

    private void updateFilter() {
        String q = searchField.getText().toLowerCase();
        filteredData.setPredicate(offer ->
                q.isEmpty()
                || offer.getTitle().toLowerCase().contains(q)
                || offer.getCompanyName().toLowerCase().contains(q)
                // FIX: also search by tag
                || (offer.getTags() != null && offer.getTags().stream()
                        .anyMatch(t -> t.toLowerCase().contains(q)))
        );
    }

    @FXML
    private void handleLogout() {
        ((Stage) offersListView.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
