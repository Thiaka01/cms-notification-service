package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.DeliveryAttempt;
import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import java.time.Instant;
import java.util.UUID;

public record DeliveryAttemptResponse(
        UUID id,
        UUID notificationId,
        String eventType,
        NotificationChannel channel,
        DeliveryStatus status,
        String providerRef,
        String errorMessage,
        Instant attemptedAt) {

    public static DeliveryAttemptResponse from(DeliveryAttempt attempt) {
        return new DeliveryAttemptResponse(
                attempt.getId(),
                attempt.getNotification().getId(),
                attempt.getNotification().getEventType(),
                attempt.getChannel(),
                attempt.getStatus(),
                attempt.getProviderRef(),
                attempt.getErrorMessage(),
                attempt.getAttemptedAt());
    }
}
