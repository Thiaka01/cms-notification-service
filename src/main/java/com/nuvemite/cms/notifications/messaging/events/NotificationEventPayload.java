package com.nuvemite.cms.notifications.messaging.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationEventPayload(
        UUID eventId,
        List<RecipientPayload> recipients,
        Map<String, String> variables) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecipientPayload(String keycloakSub, String email, String name) {}
}
