package com.enscs.internship.core;

import org.mindrot.jbcrypt.BCrypt;

public abstract class User implements Authenticatable {
    private final int app_id;
    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String password;

    public User(int id, String first_name, String last_name, String username, String email, String password) {
        this.app_id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

 
    public abstract String getRole();

    @Override
    public boolean login(String username, String password) {
        return this.username.equals(username) && BCrypt.checkpw(password, this.password);
    }

    @Override
    public void logout() {
        System.out.println(username + " logged out.");
    }

    public String getFirstName() { return first_name; }
    public String getLastName() { return last_name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getAppId() { return app_id; }
    
    public void setFirstName(String first_name) { this.first_name = first_name; }
    public void setLastName(String last_name) { this.last_name = last_name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}