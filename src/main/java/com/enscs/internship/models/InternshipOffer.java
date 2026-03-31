package com.enscs.internship.models;

import java.util.ArrayList;
import java.util.List;

public class InternshipOffer {
    private static int OfferIdGenerator = 0;
    private int offerId;
    private String title;
    private String description;
    private String companyName;
    private List<String> requirements;
    private boolean isOpen;

    public InternshipOffer(int offerId, String title, String description, String companyName) {
        this.offerId = offerId;
        this.title = title;
        this.description = description;
        this.companyName = companyName;
        this.requirements = new ArrayList<>();
        this.isOpen = true; // Default to open upon creation
    }
    public InternshipOffer(String title, String description, String companyName, String[] requirements) {
        this.offerId = OfferIdGenerator++;
        this.title = title;
        this.description = description;
        this.companyName = companyName;
        this.requirements = new ArrayList<>();
        for (String req : requirements) {
            this.requirements.add(req.trim());
        }
        this.isOpen = true; // Default to open upon creation
    }
    // Methods to manage requirements
    public void addRequirement(String req) {
        this.requirements.add(req);
    }

    // Standard Getters and Setters
    public int getOfferId() { return offerId; }
    public String getTitle() { return title; }
    public String getCompanyName() { return companyName; }
    public boolean isOpen() { return isOpen; }
    public List<String> getRequirements() { return requirements; }
    public void setOpen(boolean open) { isOpen = open; }

    @Override
    public String toString() {
        return String.format("[%d] %s at %s", offerId, title, companyName);
    }
    /**
 * Toggles the status of the internship.
 * Useful for the Supervisor Dashboard.
 */
public void toggleStatus() {
    this.isOpen = !this.isOpen;
}

// Add these getters/setters if missing for GSON serialization
public void setDescription(String description) { this.description = description; }
public String getDescription() { return description; }
}