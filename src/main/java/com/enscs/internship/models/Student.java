package com.enscs.internship.models;

import com.enscs.internship.core.User;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private static int id;
    private String major;
    private List<Application> applications; // Java Collections Framework requirement

    public Student(int id, String first_name, String last_name, String username, String email, String password, String major) {
        super(id, first_name, last_name, username, email, password);
        this.major = major;
        this.applications = new ArrayList<>();
        type = "STUDENT";
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    // Getters and Setters
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public List<Application> getApplications() { return applications; }
    
    public void addApplication(Application app) {
        this.applications.add(app);
    }
    public int getId() { return id; }
}