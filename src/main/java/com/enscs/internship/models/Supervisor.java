package com.enscs.internship.models;

import com.enscs.internship.core.User;
import java.util.ArrayList;
import java.util.List;

public class Supervisor extends User {
    private String companyName;
    private List<InternshipOffer> postedOffers;

    public Supervisor(int id, String first_name, String last_name, String username, String email, String password, String companyName) {
        super(id, first_name, last_name, username, email, password);
        this.companyName = companyName;
        this.postedOffers = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "SUPERVISOR";
    }

    public String getCompanyName() { return companyName; }
    public List<InternshipOffer> getPostedOffers() { return postedOffers; }
}