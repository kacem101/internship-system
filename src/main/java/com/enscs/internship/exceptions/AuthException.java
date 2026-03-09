package com.enscs.internship.exceptions;

public class AuthException extends AppException {
         
    public AuthException(String message) {
        super("Authentication failed: " + message);
    }
    public AuthException(String message, Throwable cause) {
        super("Authentication failed: " + message, cause);
    }
    public AuthException(Throwable cause) {
        super("Authentication failed", cause);
    }
    public AuthException() {
        super("Authentication failed");
    }
    @Override
    public String getMessage() {
        return "Authentication failed" ;
    }
}
