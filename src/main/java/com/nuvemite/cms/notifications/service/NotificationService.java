package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import java.util.Set;

/**
 * Channel-specific notification service interface.
 * Implemented by SMS and Email services to dispatch notifications
 * when an event from {@link com.nuvemite.cms.notifications.messaging.EventTypes} is fired.
 */
public interface NotificationService {

    /**
     * Returns the notification channel this service handles.
     */
    NotificationChannel channel();

    /**
     * Sends a notification message through this channel.
     */
    NotificationDeliveryResult send(NotificationMessage message);

    /**
     * Returns the set of event type codes this service supports.
     * Derived from the event definitions that include this channel.
     */
    Set<String> supportedEventTypes();

    /**
     * Whether this service can handle the given event type code.
     */
    boolean supportsEventType(String eventCode);

    /**
     * Sends a notification for a specific event type through this channel.
     * Validates that the event is supported before sending.
     */
    NotificationDeliveryResult sendForEvent(String eventCode, NotificationMessage message);
}
