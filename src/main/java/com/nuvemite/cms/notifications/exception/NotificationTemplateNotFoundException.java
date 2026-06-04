package com.nuvemite.cms.notifications.exception;

import com.nuvemite.cms.notifications.domain.NotificationChannel;

public class NotificationTemplateNotFoundException extends ResourceNotFoundException {

    public NotificationTemplateNotFoundException(String eventCode, NotificationChannel channel) {
        super("No active template for event " + eventCode + " channel " + channel);
    }
}
