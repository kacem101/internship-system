package com.enscs.internship.services;

import com.enscs.internship.models.*;
import com.enscs.internship.exceptions.InternshipException;
import java.util.List;

public class InternshipService {
    private final DataManager dataManager;

    public InternshipService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Logic for a student applying to an offer.
     * Must check if offer is open and if student already applied.
     */
    public void applyForInternship(Student student, InternshipOffer offer) throws InternshipException {
        if (!offer.isOpen()) {
            throw new InternshipException("Cannot apply: Offer is closed.");
        }
        for (Application app : student.getApplications()) {
            if (app.getOfferId() == offer.getOfferId()) {
                throw new InternshipException("Cannot apply: Already applied to this offer.");
            }
        }
        Application newApp = new Application(Application.getNextApplicationId(), student.getId(), offer.getOfferId());
        student.addApplication(newApp);
        dataManager.addApplication(newApp);
    }


    /**
     * Logic for a supervisor to update application status.
     */
    public void updateApplicationStatus(Application app, ApplicationStatus newStatus) {
        app.setStatus(newStatus);
        dataManager.updateApplication(app);
    }

    /**
     * Returns a filtered list of open internships.
     */
    public List<InternshipOffer> getAvailableOffers() { return dataManager.getAllOffers(); }
}