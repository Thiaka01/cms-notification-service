package com.nuvemite.cms.notifications.exception;

public class NotificationEventNotFoundException extends ResourceNotFoundException {

    public NotificationEventNotFoundException(String eventCode) {
        super("Notification event not found: " + eventCode);
    }
}
