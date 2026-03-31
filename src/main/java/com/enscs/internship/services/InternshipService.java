package com.enscs.internship.services;

import com.enscs.internship.models.*;
import com.enscs.internship.exceptions.InternshipException;
import java.util.List;
import java.util.stream.Collectors;

public class InternshipService {
    private final DataManager dataManager;

    public InternshipService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // --- OFFER MANAGEMENT ---

    /**
     * Saves a new offer and persists to the JSON database.
     */
    public boolean saveOffer(InternshipOffer offer) {
        dataManager.addOffer(offer);
        dataManager.saveData();
        return true;
    }

    /**
     * Returns only offers that are currently open for applications.
     */
    public List<InternshipOffer> getActiveOffers() {
        return dataManager.getOffers().stream()
                .filter(InternshipOffer::isOpen)
                .collect(Collectors.toList());
    }

    /**
     * For Supervisors: Returns only the offers they have posted for their company.
     */
    public List<InternshipOffer> getOffersBySupervisor(String companyName) {
        return dataManager.getOffers().stream()
                .filter(o -> o.getCompanyName().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    /**
     * Search feature: Filters by Title or Company.
     */
    public List<InternshipOffer> searchOffers(String query) {
        if (query == null || query.trim().isEmpty()) return getActiveOffers();
        
        String lowerQuery = query.toLowerCase().trim();
        return dataManager.getOffers().stream()
                .filter(o -> o.getTitle().toLowerCase().contains(lowerQuery) || 
                             o.getCompanyName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    // --- APPLICATION LOGIC (ID-FREE STRATEGY) ---

    /**
     * Processes a new application.
     * Uses student.getUsername() to identify the applicant, solving the "missing school ID" issue.
     */
    public void apply(Student student, InternshipOffer offer) throws InternshipException {
        if (!offer.isOpen()) {
            throw new InternshipException("This internship position is no longer accepting applications.");
        }
        
        // Check for duplicate applications using Username + Offer ID
        // This is safe because usernames are unique across the whole system.
        boolean duplicate = dataManager.getApplications().stream()
                .anyMatch(a -> a.getOfferId() == offer.getOfferId() && 
                               a.getStudentUsername().equals(student.getUsername()));
        
        if (duplicate) {
            throw new InternshipException("You have already submitted an application for this role.");
        }

        // Create the application using the new constructor that handles its own UUID/ID
        Application app = new Application(student.getUsername(), offer.getOfferId());
        
        dataManager.addApplication(app);
        dataManager.saveData();
    }

    /**
     * For Students: View their own dashboard of applications.
     * Filtered by username.
     */
    public List<Application> getStudentApplications(String username) {
        return dataManager.getApplications().stream()
                .filter(a -> a.getStudentUsername().equals(username))
                .collect(Collectors.toList());
    }

    /**
     * For Supervisors: See everyone who applied to a specific offer.
     */
    public List<Application> getApplicationsForOffer(int offerId) {
        return dataManager.getApplications().stream()
                .filter(a -> a.getOfferId() == offerId)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status (ACCEPTED, REJECTED, etc.) of an existing application.
     */
    public void updateApplicationStatus(Application app, ApplicationStatus newStatus) {
        app.setStatus(newStatus);
        dataManager.saveData();
    }

    // --- HELPERS ---
    // Find an offer by its ID
public InternshipOffer getOfferById(int id) {
    return dataManager.getOffers().stream()
            .filter(o -> o.getOfferId() == id)
            .findFirst()
            .orElse(null);
}

// Get applications for a specific supervisor's company
public List<Application> getApplicationsForSupervisor(String companyName) {
    return dataManager.getApplications().stream()
            .filter(app -> {
                InternshipOffer offer = getOfferById(app.getOfferId());
                return offer != null && offer.getCompanyName().equals(companyName);
            })
            .collect(Collectors.toList());
}
public void addOffer(InternshipOffer offer) {
    // 1. Add it to the local list in DataManager
    dataManager.addOffer(offer);
    
    // 2. Ensure it's persisted to the JSON file
    dataManager.saveData();
    
    System.out.println("Service: Offer " + offer.getOfferId() + " added and saved.");
}
}