package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import java.time.Instant;
import java.util.UUID;

public record NotificationTemplateResponse(
        UUID id,
        UUID eventId,
        String eventCode,
        NotificationChannel channel,
        String subject,
        String templateContent,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    public static NotificationTemplateResponse from(NotificationTemplate template) {
        return new NotificationTemplateResponse(
                template.getId(),
                template.getEvent().getId(),
                template.getEventType(),
                template.getChannel(),
                template.getSubject(),
                template.getBodyTemplate(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt());
    }
}
