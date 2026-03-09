package com.enscs.internship.exceptions;

public class InternshipException extends AppException {
    /**
     * Triggered for general business logic errors (e.g., applying twice).
     */
    public InternshipException(String message) {
        super(message);
    }
    public InternshipException(String message, Throwable cause) {
        super(message, cause);
    }
    public InternshipException(Throwable cause) {
        super(cause);
    }
    public InternshipException() {
        super();
    }
    @Override
    public String getMessage() {
        return "internship error: ";
    }
}