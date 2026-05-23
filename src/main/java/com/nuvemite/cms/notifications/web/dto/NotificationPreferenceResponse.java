package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationPreference;
import java.util.UUID;

public record NotificationPreferenceResponse(
        UUID id, NotificationChannel channel, String eventType, boolean enabled) {

    public static NotificationPreferenceResponse from(NotificationPreference preference) {
        return new NotificationPreferenceResponse(
                preference.getId(),
                preference.getChannel(),
                preference.getEventType(),
                preference.isEnabled());
    }
}
