package org.example.exception;

public enum ErrorCode {
    AD_NOT_FOUND("Ad not found with id: %d"),
    USER_NOT_FOUND("User not found with id: %d"),
    AD_PLAN_NOT_FOUND("Ad plan not found with id: %d");



    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
