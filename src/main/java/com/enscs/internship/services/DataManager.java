package com.enscs.internship.services;

import com.enscs.internship.core.User;
import com.enscs.internship.models.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {
    private List<User> users = new ArrayList<>();
    private List<InternshipOffer> offers = new ArrayList<>();
    private List<Application> applications = new ArrayList<>();
    
    private final String DATA_PATH = "src/main/resources/data/";
    private final Gson gson;

    public DataManager() {
        // 1. Create the Directory if it doesn't exist
        File directory = new File(DATA_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. Configure GSON with Adapters
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            // Fix for LocalDate (InaccessibleObjectException)
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            // Support for Polymorphism (Student vs Supervisor)
            .registerTypeAdapter(User.class, new UserAdapter()) 
            .create();

        loadData();
    }

    public void loadData() {
        users = loadFile(DATA_PATH + "users.json", new TypeToken<List<User>>(){}.getType());
        offers = loadFile(DATA_PATH + "offers.json", new TypeToken<List<InternshipOffer>>(){}.getType());
        applications = loadFile(DATA_PATH + "applications.json", new TypeToken<List<Application>>(){}.getType());
    }

    private <T> List<T> loadFile(String path, java.lang.reflect.Type type) {
    File file = new File(path);

    // FIX 1: If the file doesn't exist OR is empty, don't let Gson touch it!
    if (!file.exists() || file.length() == 0) {
        System.out.println("File is empty or missing: " + path + ". Initializing empty list.");
        return new ArrayList<>();
    }

    try (Reader reader = new FileReader(file)) {
        // FIX 2: Parse the file
        List<T> list = gson.fromJson(reader, type);
        
        // Ensure we return a real list even if the file had "null" written in it
        return list != null ? list : new ArrayList<>();
    } catch (IOException | JsonSyntaxException e) {
        System.err.println("Error reading " + path + ": " + e.getMessage());
        return new ArrayList<>();
    }
}

    public void saveData() {
        saveToFile(DATA_PATH + "users.json", users);
        saveToFile(DATA_PATH + "offers.json", offers);
        saveToFile(DATA_PATH + "applications.json", applications);
    }

    private void saveToFile(String path, Object data) {
    try (Writer writer = new FileWriter(path)) {
        // Specify the type explicitly to force GSON to use the UserAdapter
        java.lang.reflect.Type listType = new TypeToken<List<User>>(){}.getType();
        
        if (path.contains("users.json")) {
            gson.toJson(data, listType, writer); // Force the User type
        } else {
            gson.toJson(data, writer);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    // --- Accessors ---
    public List<User> getUsers() { return users; }
    public List<InternshipOffer> getOffers() { return offers; }
    public List<Application> getApplications() { return applications; }

    public void addUser(User u) { 
        if (getUserByUsername(u.getUsername()) == null) {
            users.add(u); 
            saveData(); 
        }
    }

    public void addOffer(InternshipOffer o) { offers.add(o); saveData(); }
    public void addApplication(Application a) { applications.add(a); saveData(); }
    
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Internal helper to tell GSON how to distinguish between Student and Supervisor
     */
    private static class UserAdapter implements JsonSerializer<User>, JsonDeserializer<User> {
    
    // This runs when you call saveData()
    @Override
    public JsonElement serialize(User src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
    // Force GSON to use the actual class (Student/Supervisor) instead of the 'User' interface
    JsonObject result = context.serialize(src, src.getClass()).getAsJsonObject();
    
    // Add the discriminator field
    result.addProperty("type", src.getClass().getSimpleName());
    
    return result;
}

    // This runs when you call loadData()
    @Override
    public User deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // 3. Look for the "type" field we saved earlier
        JsonElement typeElement = jsonObject.get("type");
        
        // Safety Check: If type is missing, we can't crash! 
        // We default to Student for your NSCS project.
        if (typeElement == null || typeElement.isJsonNull()) {
            return context.deserialize(json, Student.class);
        }

        String type = typeElement.getAsString();
        
        // 4. Use the type to recreate the correct object
        if ("Student".equalsIgnoreCase(type)) {
            return context.deserialize(json, Student.class);
        } else if ("Supervisor".equalsIgnoreCase(type)) {
            return context.deserialize(json, Supervisor.class);
        } else {
            throw new JsonParseException("Unknown User type: " + type);
        }
    }
    }

    public InternshipOffer getOfferById(int offerId) {
        return offers.stream()
                .filter(o -> o.getOfferId() == offerId)
                .findFirst()
                .orElse(null);
    }
}