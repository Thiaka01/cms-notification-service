package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationEvent;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record NotificationEventResponse(
        UUID id,
        String eventCode,
        String name,
        String description,
        boolean active,
        Set<NotificationChannel> supportedChannels,
        List<NotificationEventTemplateResponse> templates,
        Instant createdAt,
        Instant updatedAt) {

    public static NotificationEventResponse from(
            NotificationEvent event,
            List<NotificationTemplate> templates) {
        return new NotificationEventResponse(
                event.getId(),
                event.getEventCode(),
                event.getName(),
                event.getDescription(),
                event.isActive(),
                event.getSupportedChannels(),
                templates.stream().map(NotificationEventTemplateResponse::from).toList(),
                event.getCreatedAt(),
                event.getUpdatedAt());
    }
}
