package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public record NotificationEventRequest(
        @NotBlank @Size(max = 128) String eventCode,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1000) String description,
        @NotEmpty Set<NotificationChannel> supportedChannels,
        Boolean active,
        List<@Valid NotificationEventTemplateRequest> templates) {}
