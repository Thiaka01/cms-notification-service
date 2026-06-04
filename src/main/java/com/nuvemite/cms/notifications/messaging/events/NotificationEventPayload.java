package com.nuvemite.cms.notifications.messaging.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationEventPayload(
        UUID eventId,
        String eventCode,
        List<RecipientPayload> recipients,
        Map<String, String> variables) {

    public NotificationEventPayload(
            UUID eventId,
            List<RecipientPayload> recipients,
            Map<String, String> variables) {
        this(eventId, null, recipients, variables);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecipientPayload(
            String keycloakSub,
            String email,
            String name,
            String phone,
            UUID companyId,
            List<NotificationChannel> channels) {

        public RecipientPayload(String keycloakSub, String email, String name) {
            this(keycloakSub, email, name, null, null, null);
        }
    }
}
