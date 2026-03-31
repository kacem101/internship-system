package com.enscs.internship.services;

import com.enscs.internship.core.User;

public class SessionManager {
    private static User currentUser;
    private static DataManager dataManager;
    private static AuthService authService; // Add this

    public static void setUser(User user) { currentUser = user; }
    public static User getUser() { return currentUser; }

    public static void setDataManager(DataManager dm) { dataManager = dm; }
    public static DataManager getDataManager() { return dataManager; }

    public static void setAuthService(AuthService auth) { authService = auth; }
    public static AuthService getAuthService() { return authService; }
}