package com.nuvemite.cms.notifications.exception;

public class NotificationEventAlreadyExistsException extends ConflictException {

    public NotificationEventAlreadyExistsException(String eventCode) {
        super("Notification event already exists: " + eventCode);
    }
}
