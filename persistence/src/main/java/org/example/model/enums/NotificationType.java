package org.example.model.enums;

public enum NotificationType {

    USER_REGISTERED_NOTIFICATION("Welcome %s %s!\n" +
                    "Your registration was successful.\n" +
                    "Your profile is now active. You can browse properties, connect with our real estate agents, and publish your own property listings."),
    MANAGER_REGISTERED_NOTIFICATION("Welcome %s %s!\n" + "Your registration was successful.\n" +
                    "Your profile is now active. You can now manage properties, assist customers, and oversee real estate transactions."),
    PROFILE_REMOVED_NOTIFICATION("Dear %s %s,\n" +
                    "Your profile has been deleted successfully.\n" +
                    "All associated data and property listings have been removed from our platform."),
    PROFILE_UPDATE_NOTIFICATION("Dear %s %s,\n" +
                    "Your profile has been updated successfully.\n" +
                    "Your changes have been saved and your profile information is now up to date."),
    PROFILE_BLOCKED_NOTIFICATION("Dear %s %s,\n" +
                    "Your profile has been blocked.\n" +
                    "Please contact our support team for more information."),
    PROFILE_STILL_NOT_BLOCKED_NOTIFICATION("Dear %s %s,\n" +
                    "Please be aware that if your rating becomes negative again, your account may be blocked."),
    PROFILE_UNBLOCKED_NOTIFICATION("Dear %s %s,\n" +
                    "Your profile has been unblocked.\n" +
                    "You can now access your account and continue using our services."),
    PROFILE_PASSWORD_CHANGED_NOTIFICATION("Dear %s %s,\n" +
                    "Your password has been changed successfully.\n" +
                    "If you did not request this change, please contact our support team immediately."),
    PROFILE_PASSWORD_RESET_NOTIFICATION("Dear %s %s,\n" +
            "Your password has been reset successfully.\n"),


    PROPERTY_APPROVED_NOTIFICATION("Congratulations, %s %s!\n" +
                    "Your property listing has been approved and is now visible to potential buyers and renters."),
    PROPERTY_UPDATE_NOTIFICATION("Dear %s %s,\n" +
                    "Your property listing has been updated successfully."),
    PROPERTY_REMOVED_NOTIFICATION("Dear %s %s,\n" +
                    "Your property listing has been removed from our platform."),
    PROPERTY_REJECTED_NOTIFICATION("Dear %s %s,\n" +
                    "Unfortunately, your property listing has been rejected.\n" +
                    "Please review our listing guidelines and resubmit after making the necessary changes."),
    PROPERTY_SOLD_NOTIFICATION("Congratulations %s %s!\n" +
                    "Your property has been marked as sold and is no longer visible on our platform."),
    PROPERTY_RENTED_NOTIFICATION("Congratulations %s %s!\n" +
                    "Your property has been marked as rented and is no longer visible on our platform."),


    AD_APPROVED_NOTIFICATION("Congratulations %s %s!\n" +
                    "Your advertisement has been approved and is now visible on our platform."),
    AD_REJECTED_NOTIFICATION("Dear %s %s,\n" +
                    "Your advertisement has been rejected.\n" +
                    "Please review our advertising guidelines and resubmit it after making the necessary changes."),
    AD_REMOVED_NOTIFICATION("Dear %s %s,\n" +
                    "Your advertisement has been removed from our platform."),
    AD_PUBLISHED_NOTIFICATION("Congratulations %s %s!\n" +
                    "Your advertisement has been published and is now visible to potential customers."),


    PAYMENT_FOR_AD_NOTIFICATION("Payment successful.\n" +
                    "Dear %s %s, your advertisement has been published and is now visible to potential customers."),
    PAYMENT_SUCCESS_NOTIFICATION("Payment successful.\n" +
                    "Dear %s %s, thank you for using our platform.");



    private final String notification;

    NotificationType(String notification) {
        this.notification = notification;
    }

    public String format(Object... args) {
        return String.format(notification, args);
    }

}