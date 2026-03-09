package com.enscs.internship.services;

import com.enscs.internship.models.*;
import com.enscs.internship.core.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private List<User> users;
    private List<InternshipOffer> offers;
    private List<Application> allApplications;
    private final String USER_FILE = "src/main/resources/data/users.json";
    private final String OFFER_FILE = "src/main/resources/data/offers.json";
    private final String APPLICATION_FILE = "src/main/resources/data/applications.json";
    private final Gson gson;

    public DataManager() {
        // GsonBuilder with setPrettyPrinting makes the JSON files readable for your report
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.users = new ArrayList<>();
        this.offers = new ArrayList<>();
        loadData();
    }

    // --- Persistence Logic ---

    public void saveData() {
        try (Writer userWriter = new FileWriter(USER_FILE);
             Writer offerWriter = new FileWriter(OFFER_FILE);
             Writer applicationWriter = new FileWriter(APPLICATION_FILE)) {
            
            gson.toJson(users, userWriter);
            gson.toJson(offers, offerWriter);
            gson.toJson(allApplications, applicationWriter);
            System.out.println("Data saved successfully to JSON.");
            
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        try (Reader userReader = new FileReader(USER_FILE)) {
            // Note: Gson needs a TypeToken to handle List<User> correctly
            users = gson.fromJson(userReader, new TypeToken<List<User>>(){}.getType());
            if (users == null) users = new ArrayList<>();
        } catch (FileNotFoundException e) {
            System.out.println("No existing user data found. Starting fresh.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader offerReader = new FileReader(OFFER_FILE)) {
            offers = gson.fromJson(offerReader, new TypeToken<List<InternshipOffer>>(){}.getType());
            if (offers == null) offers = new ArrayList<>();
        } catch (FileNotFoundException e) {
            System.out.println("No existing offer data found. Starting fresh.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader applicationReader = new FileReader(APPLICATION_FILE)) {
            allApplications = gson.fromJson(applicationReader, new TypeToken<List<Application>>(){}.getType());
            if (allApplications == null) allApplications = new ArrayList<>();
        } catch (FileNotFoundException e) {
            System.out.println("No existing application data found. Starting fresh.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Collections Logic ---

    public void addUser(User user) {
        users.add(user);
        saveData(); // Auto-save on change
    }

    public List<InternshipOffer> getAllOffers() {
        return offers;
    }

    public User getUserByUsername(String username) {
        return users.stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(username))
                    .findFirst()
                    .orElse(null);
    }
    public void addOffer(InternshipOffer offer) {
        offers.add(offer);
        saveData();
    }
    public void addApplication(Application app) {
        allApplications.add(app);
        saveData();
    }
    public List<Application> getAllApplications() {
        return allApplications;
    }
    public Application getApplicationById(int appId) {
        return allApplications.stream()
                .filter(app -> app.getApplicationId() == appId)
                .findFirst()
                .orElse(null);
    }
    public void updateApplication(Application app) {
        Application existingApp = getApplicationById(app.getApplicationId());
        if (existingApp != null) {
            existingApp.setStatus(app.getStatus());
            saveData();
        }
    }
}