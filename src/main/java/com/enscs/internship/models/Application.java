package com.enscs.internship.models;

import java.time.LocalDate;

public class Application {
    private static int serialNumber = 0; // For generating unique application IDs
    private int applicationId;
    private int studentId;
    private int offerId;
    private LocalDate applicationDate;
    private ApplicationStatus status;

    public Application(int applicationId, int studentId, int offerId) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.offerId = offerId;
        this.applicationDate = LocalDate.now();
        this.status = ApplicationStatus.PENDING;
    }

    // Getters and Setters
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public int getOfferId() { return offerId; }
    public int getStudentId() { return studentId; }
    public int getApplicationId() { return applicationId; }
    public void incrementSerial() { serialNumber++; }
    public static int getNextApplicationId() { return ++serialNumber; }
}