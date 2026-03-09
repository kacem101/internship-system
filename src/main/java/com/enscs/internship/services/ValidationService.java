package com.enscs.internship.services;

import com.enscs.internship.exceptions.AuthException;

public class ValidationService {

    /**
     * Validates email format using Regex.
     * @throws AppException if format is invalid.
     */
    public static void validateEmail(String email) throws AuthException {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new AuthException("Invalid email format");
        }
    }

    /**
     * Enforces ENSCS password standards (e.g., length, special chars).
     */
    public static void validatePasswordStrength(String password) throws AuthException {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+).{8,}$";
        if (!password.matches(passwordRegex)) {
            throw new AuthException("Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character");
        }
    }

    /**
     * Sanitizes input to prevent basic injection attempts in form fields.
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.replaceAll("[<>&;()#|'\"]", "").trim();
    }
}