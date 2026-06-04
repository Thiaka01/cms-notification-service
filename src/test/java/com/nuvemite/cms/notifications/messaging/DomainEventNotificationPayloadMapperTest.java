package com.nuvemite.cms.notifications.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainEventNotificationPayloadMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DomainEventNotificationPayloadMapper mapper =
            new DomainEventNotificationPayloadMapper(objectMapper);

    @Test
    void mapsExplicitNotificationRecipients() throws Exception {
        UUID eventId = UUID.randomUUID();
        JsonNode root = objectMapper.readTree("""
                {
                  "eventId": "%s",
                  "eventCode": "cms.license.granted.v1",
                  "recipients": [
                    {
                      "keycloakSub": "user-sub",
                      "email": "user@test.local",
                      "name": "Jane",
                      "phone": "+254700000001",
                      "channels": ["EMAIL", "SMS"]
                    }
                  ],
                  "variables": {
                    "licenseNumber": "LIC-001",
                    "chemicalName": "Acetone"
                  }
                }
                """.formatted(eventId));

        var payload = mapper.toNotificationPayload(root, "cms.license.granted.v1");

        assertThat(payload.eventId()).isEqualTo(eventId);
        assertThat(payload.variables())
                .containsEntry("licenseNumber", "LIC-001")
                .containsEntry("chemicalName", "Acetone");
        assertThat(payload.recipients()).hasSize(1);
        var recipient = payload.recipients().get(0);
        assertThat(recipient.keycloakSub()).isEqualTo("user-sub");
        assertThat(recipient.email()).isEqualTo("user@test.local");
        assertThat(recipient.phone()).isEqualTo("+254700000001");
        assertThat(recipient.channels()).containsExactly(NotificationChannel.EMAIL, NotificationChannel.SMS);
    }

    @Test
    void mapsGenericCmsEventContactFields() throws Exception {
        UUID eventId = UUID.randomUUID();
        JsonNode root = objectMapper.readTree("""
                {
                  "eventId": "%s",
                  "eventType": "cms.complaint.submitted.v1",
                  "recipientEmail": "reporter@test.local",
                  "recipientPhone": "+254700000002",
                  "recipientName": "Amina",
                  "channels": "EMAIL,SMS",
                  "referenceNumber": "CMP-001",
                  "submittedAt": "2026-06-03T08:00:00Z"
                }
                """.formatted(eventId));

        var payload = mapper.toNotificationPayload(root, "cms.complaint.submitted.v1");

        assertThat(payload.eventId()).isEqualTo(eventId);
        assertThat(payload.variables())
                .containsEntry("referenceNumber", "CMP-001")
                .containsEntry("submittedAt", "2026-06-03T08:00:00Z");
        assertThat(payload.recipients()).hasSize(1);
        var recipient = payload.recipients().get(0);
        assertThat(recipient.email()).isEqualTo("reporter@test.local");
        assertThat(recipient.phone()).isEqualTo("+254700000002");
        assertThat(recipient.name()).isEqualTo("Amina");
        assertThat(recipient.channels()).containsExactly(NotificationChannel.EMAIL, NotificationChannel.SMS);
    }
}
