package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import jakarta.validation.constraints.NotNull;

public record NotificationPreferenceRequest(
        @NotNull NotificationChannel channel, String eventType, boolean enabled) {}
