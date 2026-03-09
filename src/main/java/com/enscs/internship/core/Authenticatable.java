package com.enscs.internship.core;

public interface Authenticatable {
    boolean login(String username, String password);
    void logout();
}