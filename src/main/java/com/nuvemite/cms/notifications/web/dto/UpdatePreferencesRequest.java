package com.nuvemite.cms.notifications.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdatePreferencesRequest(@NotEmpty @Valid List<NotificationPreferenceRequest> preferences) {}
