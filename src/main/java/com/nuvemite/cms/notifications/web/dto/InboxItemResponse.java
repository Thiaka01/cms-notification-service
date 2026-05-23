package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationRecipient;
import java.time.Instant;
import java.util.UUID;

public record InboxItemResponse(
        UUID recipientId,
        UUID notificationId,
        String eventType,
        String subject,
        String body,
        Instant createdAt,
        Instant readAt) {

    public static InboxItemResponse from(NotificationRecipient recipient) {
        var notification = recipient.getNotification();
        return new InboxItemResponse(
                recipient.getId(),
                notification.getId(),
                notification.getEventType(),
                notification.getSubject(),
                notification.getBody(),
                notification.getCreatedAt(),
                recipient.getReadAt());
    }
}
