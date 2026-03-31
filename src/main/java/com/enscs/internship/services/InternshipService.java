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

    public void apply(Student student, InternshipOffer offer) throws InternshipException {
        if (!offer.isOpen()) throw new InternshipException("Offer is closed.");
        
        boolean duplicate = student.getApplications().stream()
                .anyMatch(a -> a.getOfferId() == offer.getOfferId());
        
        if (duplicate) throw new InternshipException("Already applied!");

        Application app = new Application(Application.getNextApplicationId(), student.getAppId(), offer.getOfferId());
        student.addApplication(app);
        dataManager.addApplication(app);
        dataManager.saveData();
    }

    public List<InternshipOffer> getActiveOffers() {
        return dataManager.getOffers().stream().filter(InternshipOffer::isOpen).collect(Collectors.toList());
    }
    public boolean saveOffer(InternshipOffer offer) {
        dataManager.addOffer(offer);
        dataManager.saveData();
        return true;
    }
}