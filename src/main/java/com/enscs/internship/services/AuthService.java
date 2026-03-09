package com.enscs.internship.services;

import com.enscs.internship.core.User;
import com.enscs.internship.exceptions.AuthException;

public class AuthService {
    private ThreadLocal<User> currentUser = new ThreadLocal<>();
    private final DataManager dataManager;

    public AuthService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Validates credentials against dataManager records.
     * @throws AuthException if user not found or password incorrect.
     */
    public void login(String username, String password) throws AuthException {
        if (username == null || password == null) {
            throw new AuthException("Username and password are required");
        }
        try {
            User user = dataManager.getUserByUsername(username);
            if (user == null) {
                throw new AuthException("User not found");
            }
            if (!user.login(username, password)) {
                throw new AuthException("Invalid password");
            }
            currentUser.set(user);
        } catch (Exception e) {
            throw new AuthException("Authentication failed due to system error", e);
        }
    }

    public void logout() {
        User user = currentUser.get();
        if (user != null) {
            user.logout();
            currentUser.remove();
        }
    }

    public User getCurrentUser() { return currentUser.get(); }

    public boolean isAuthenticated() { return currentUser.get() != null; }
}