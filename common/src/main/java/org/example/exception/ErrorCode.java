package org.example.exception;

public enum ErrorCode {
    USER_NOT_FOUND("User not found with id: %d"),
    USER_LOGIN_NOT_FOUND("Email or password is not true"),
    USER_NOT_FOUND_BY_EMAIL("User not found with email: %s"),
    USER_ALREADY_REGISTERED("User already registered with email: %s"),
    PASSWORD_CHANGED_SUCCESSFULLY("Password changed successfully for email: %s"),
    PASSWORD_CHANGE_FAILED("Password change failed for email: %s"),
    PROFILE_IS_BLOCKED("Profile is blocked for email: %s"),
    OLD_PASSWORD_IS_INCORRECT("Old password is incorrect"),
    VERIFICATION_FAILED("Verification failed for email: %s"),
    TRY_AGAIN("Try again something went wrong"),
    PASSWORDS_DO_NOT_MATCH("Passwords do not match with email: %s"),
    NOTIFICATION_NOT_FOUND("Notification not found with id: %d"),
    USER_NOT_AUTHENTICATED("User not authenticated"),
    CANNOT_DELETE_OWN_ACCOUNT("Cannot delete own account with id: %d"),
    CANNOT_UPDATE_OWN_ACCOUNT("Cannot update own account with id: %d"),
    VERIFICATION_SUCCESSFUL("Verification successful for email: %s"),
    CONVERSATION_NOT_FOUND("Conversation not found with id: %d"),
    CONVERSATION_ACCESS_DENIED("You are not allowed to access this conversation"),
    CANNOT_MESSAGE_SELF("You cannot start a conversation with yourself"),
    NO_MANAGER_AVAILABLE("No support manager is available"),
    PROPERTY_NOT_FOUND("Property not found with id: %d"),
    LOCATION_NOT_FOUND("Location not found with id: %d"),
    LOCATION_NAME_NOT_FOUND("Location name not found with id: %d"),
    INVALID_REQUEST_BODY("Request body is invalid"),
    PROFILE_EDIT_NOT_ALLOWED("You are not allowed to edit this user profile"),
    URGENT_PLAN_NOT_FOUND("No active urgent sell plan found"),
    FAVORITES_NOT_FOUND("No favorites found with id: %d"),
    FAVORITES_NOT_FOUND_WHIT_USER_ID_OR_PROPERTY_ID("No favorites found with user_id: %d or property_id: %d");


    private final String message;


    ErrorCode(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
