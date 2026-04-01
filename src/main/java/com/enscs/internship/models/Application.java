package com.enscs.internship.models;

import java.time.LocalDate;
import java.util.UUID;

public class Application {
    private String applicationId; // Changed to String for UUID
    private String studentUsername; // Use Username as the unique link
    private int offerId;
    private LocalDate applicationDate;
    private ApplicationStatus status;

    public Application(String studentUsername, int offerId) {
        // Generates a unique ID like "550e8400-e29b-41d4-a716-446655440000"
        this.applicationId = UUID.randomUUID().toString().substring(0, 8); 
        this.studentUsername = studentUsername;
        this.offerId = offerId;
        this.applicationDate = LocalDate.now();
        this.status = ApplicationStatus.PENDING;
    }

    // Getters and Setters
    public String getApplicationId() { return applicationId; }
    public String getStudentUsername() { return studentUsername; }
    public int getOfferId() { return offerId; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status2) { this.status = status2; }
}