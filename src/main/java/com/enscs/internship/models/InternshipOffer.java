package com.enscs.internship.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InternshipOffer {
    private static int OfferIdGenerator = 1; // Start IDs at 1
    private int offerId;
    private String title;
    private String description;
    private String companyName;
    private List<String> requirements;
    private boolean isOpen;
    private List<String> tags;
    /**
     * 1. NO-ARGS CONSTRUCTOR
     * Required by Gson to reconstruct objects from JSON without crashing.
     */
    public InternshipOffer() {
        this.requirements = new ArrayList<>();
        this.isOpen = true;
    }

    /**
     * 2. PRIMARY CONSTRUCTOR (For manual creation in Supervisor Dashboard)
     * Automatically assigns an ID using the static generator.
     */
    public InternshipOffer(String title, String description, String companyName) {
        this(); // Calls the no-args constructor to init list/status
        this.offerId = OfferIdGenerator++;
        this.title = title;
        this.description = description;
        this.companyName = companyName;
    }
    public InternshipOffer(String title, String description, String company, 
                           List<String> requirements, List<String> tags) {
        this.offerId = new Random().nextInt(10000);
        this.title = title;
        this.description = description;
        this.companyName = company;
        this.requirements = requirements;
        this.tags = tags;
    }

    // Add Getters and Setters for tags
    public List<String> getTags() { return tags; }
    /**
     * 3. FULL CONSTRUCTOR (With requirements)
     * Useful if you want to pass a pre-made list of strings.
     */
    public InternshipOffer(String title, String description, String companyName, List<String> requirements) {
        this(title, description, companyName);
        this.requirements = (requirements != null) ? requirements : new ArrayList<>();
    }

    // Methods to manage requirements
    public void addRequirement(String req) {
        if (req != null && !req.trim().isEmpty()) {
            this.requirements.add(req.trim());
        }
    }

    // Logic Methods
    public void toggleStatus() {
        this.isOpen = !this.isOpen;
    }

    // --- Standard Getters and Setters (Required for JavaFX PropertyValueFactory) ---
    
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    @Override
    public String toString() {
        return String.format("[%d] %s at %s (%s)", 
            offerId, title, companyName, isOpen ? "Open" : "Closed");
    }

    public void setTags(List<String> tagsList) {
        this.tags = tagsList;
    }
}