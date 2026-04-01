package com.enscs.internship.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InternshipOffer {

    // ------------------------------------------------------------------ //
    //  FIX: Use a UUID-derived int instead of a static counter.           //
    //  A static counter resets to 1 every app launch, so loaded offers    //
    //  from JSON get IDs that collide with newly created ones.            //
    // ------------------------------------------------------------------ //
    private int offerId;
    private String title;
    private String description;
    private String companyName;
    private List<String> requirements;
    private List<String> tags;
    private boolean isOpen;

    /**
     * No-args constructor — required by Gson to deserialise from JSON.
     * Initialises all list fields so nothing is ever null after load.
     */
    public InternshipOffer() {
        this.requirements = new ArrayList<>();
        this.tags         = new ArrayList<>();
        this.isOpen       = true;
    }

    /**
     * Primary constructor used when a Supervisor creates a new offer.
     * Generates a collision-safe ID from a UUID hash so it stays unique
     * even after the application is restarted and old offers are loaded
     * from JSON.
     */
    public InternshipOffer(String title, String description, String companyName) {
        this();                                       // init lists + isOpen
        // Positive, bounded int derived from UUID — no static state needed
        this.offerId      = Math.abs(UUID.randomUUID().hashCode()) % 1_000_000 + 1;
        this.title        = title;
        this.description  = description;
        this.companyName  = companyName;
    }

    /**
     * Full constructor — title, description, company + requirements + tags.
     * Used by PostOfferController in both CREATE and EDIT modes.
     */
    public InternshipOffer(String title, String description, String companyName,
                           List<String> requirements, List<String> tags) {
        this(title, description, companyName);
        if (requirements != null) this.requirements = requirements;
        if (tags         != null) this.tags         = tags;
    }

    // ------------------------------------------------------------------ //
    //  Convenience helpers                                                 //
    // ------------------------------------------------------------------ //

    public void addRequirement(String req) {
        if (req != null && !req.trim().isEmpty()) requirements.add(req.trim());
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) tags.add(tag.trim());
    }

    public void toggleStatus() { this.isOpen = !this.isOpen; }

    // ------------------------------------------------------------------ //
    //  Getters & setters (JavaFX PropertyValueFactory needs these)        //
    // ------------------------------------------------------------------ //

    public int    getOfferId()    { return offerId; }
    public void   setOfferId(int offerId) { this.offerId = offerId; }

    public String getTitle()      { return title; }
    public void   setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }

    public String getCompanyName() { return companyName; }
    public void   setCompanyName(String companyName) { this.companyName = companyName; }

    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> requirements) {
        this.requirements = (requirements != null) ? requirements : new ArrayList<>();
    }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) {
        this.tags = (tags != null) ? tags : new ArrayList<>();
    }

    public boolean isOpen() { return isOpen; }
    public void    setOpen(boolean open) { isOpen = open; }

    @Override
    public String toString() {
        return String.format("[%d] %s @ %s (%s)", offerId, title, companyName, isOpen ? "Open" : "Closed");
    }
}
