package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationLog;
import java.time.Instant;
import java.util.UUID;

public record NotificationLogResponse(
        UUID id,
        String eventCode,
        String recipient,
        NotificationChannel channel,
        DeliveryStatus status,
        String errorMessage,
        Instant sentAt) {

    public static NotificationLogResponse from(NotificationLog log) {
        return new NotificationLogResponse(
                log.getId(),
                log.getEventCode(),
                log.getRecipient(),
                log.getChannel(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getSentAt());
    }
}
