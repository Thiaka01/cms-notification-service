package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import java.time.Instant;
import java.util.UUID;

public record NotificationEventTemplateResponse(
        UUID id,
        NotificationChannel channel,
        String subject,
        String templateContent,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    public static NotificationEventTemplateResponse from(NotificationTemplate template) {
        return new NotificationEventTemplateResponse(
                template.getId(),
                template.getChannel(),
                template.getSubject(),
                template.getBodyTemplate(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt());
    }
}
