package com.nuvemite.cms.notifications.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationEventTemplateRequest(
        @NotNull NotificationChannel channel,
        @Size(max = 255) String subject,
        @JsonAlias("bodyTemplate") @NotBlank String templateContent,
        Boolean active) {}
