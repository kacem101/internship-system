package com.enscs.internship.models;

import java.time.LocalDate;
import java.util.UUID;

public class Application {
    private String applicationId;
    private String studentUsername;
    private int offerId;
    private LocalDate applicationDate;
    private ApplicationStatus status;

    /** Required by Gson */
    public Application() {}

    public Application(String studentUsername, int offerId) {
        this.applicationId = UUID.randomUUID().toString().substring(0, 8);
        this.studentUsername = studentUsername;
        this.offerId = offerId;
        this.applicationDate = LocalDate.now();
        this.status = ApplicationStatus.PENDING;
    }

    // Getters
    public String getApplicationId()   { return applicationId; }
    public String getStudentUsername() { return studentUsername; }
    public int    getOfferId()         { return offerId; }
    public ApplicationStatus getStatus() { return status; }

    /** Exposed so TableView PropertyValueFactory("applicationDate") works */
    public LocalDate getApplicationDate() { return applicationDate; }

    public void setStatus(ApplicationStatus status) { this.status = status; }
}
