package org.example.exception;

public enum ErrorCode {
    AD_NOT_FOUND("Ad not found with id: %d"),
    USER_NOT_FOUND("User not found with id: %d"),
    AD_PLAN_NOT_FOUND("Ad plan not found with id: %d"),
    USER_LOGIN_NOT_FOUND("Email or password is not true"),
    USER_NOT_FOUND_BY_EMAIL("User not found with email: %s"),
    USER_ALREADY_REGISTERED("User already registered with email: %s"),
    PASSWORD_CHANGED_SUCCESSFULLY("Password changed successfully for email: %s"),
    PASSWORD_CHANGE_FAILED("Password change failed for email: %s"),
    PROFILE_IS_BLOCKED("Profile is blocked for email: %s"),
    OLD_PASSWORD_IS_INCORRECT("Old password is incorrect"),
    VERIFICATION_FAILED("Verification failed for email: %s"),
    TRY_AGAIN("Try again something went wrong");
    private final String message;


    ErrorCode(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
