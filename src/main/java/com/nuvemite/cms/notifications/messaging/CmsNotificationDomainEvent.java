package com.nuvemite.cms.notifications.messaging;

import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;

public record CmsNotificationDomainEvent(
        String eventCode,
        String eventId,
        NotificationEventPayload payload) {}
