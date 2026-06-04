package com.nuvemite.cms.notifications.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DomainEventNotificationPayloadMapper {

    private final ObjectMapper objectMapper;

    public DomainEventNotificationPayloadMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationEventPayload toNotificationPayload(JsonNode root, String eventCode) {
        return new NotificationEventPayload(
                uuid(text(root, "eventId")),
                eventCode,
                recipients(root),
                variables(root));
    }

    private List<NotificationEventPayload.RecipientPayload> recipients(JsonNode root) {
        JsonNode recipients = root.path("recipients");
        if (recipients.isArray()) {
            List<NotificationEventPayload.RecipientPayload> mapped = new ArrayList<>();
            recipients.forEach(node -> {
                NotificationEventPayload.RecipientPayload recipient = recipient(node);
                if (hasAddress(recipient)) {
                    mapped.add(recipient);
                }
            });
            return mapped;
        }

        NotificationEventPayload.RecipientPayload recipient = recipient(root);
        return hasAddress(recipient) ? List.of(recipient) : List.of();
    }

    private NotificationEventPayload.RecipientPayload recipient(JsonNode node) {
        return new NotificationEventPayload.RecipientPayload(
                text(node, "keycloakSub", "userSub", "recipientUserSub", "reporterUserSub", "requestedBy"),
                text(node, "email", "recipientEmail", "toEmail", "contactEmail", "applicantEmail", "reporterEmail"),
                text(node, "name", "recipientName", "fullName", "contactName", "applicantName", "reporterName"),
                text(node, "phone", "recipientPhone", "mobile", "mobileNumber", "contactPhone", "applicantPhone", "reporterPhone"),
                uuid(text(node, "companyId")),
                channels(node));
    }

    private Map<String, String> variables(JsonNode root) {
        Map<String, String> variables = new LinkedHashMap<>();
        addVariables(variables, root);
        JsonNode explicitVariables = root.path("variables");
        if (explicitVariables.isObject()) {
            addVariables(variables, explicitVariables);
        }
        return variables;
    }

    private void addVariables(Map<String, String> variables, JsonNode node) {
        node.fields().forEachRemaining(entry -> {
            String name = entry.getKey();
            if ("recipients".equals(name) || "variables".equals(name)) {
                return;
            }

            JsonNode value = entry.getValue();
            if (value == null || value.isNull()) {
                variables.put(name, "");
            } else if (value.isValueNode()) {
                variables.put(name, value.asText());
            } else if (isScalarArray(value)) {
                variables.put(name, joinScalarArray(value));
            } else {
                variables.put(name, json(value));
            }
        });
    }

    private List<NotificationChannel> channels(JsonNode node) {
        JsonNode channelNode = firstNode(node, "channels", "notificationChannels", "channel");
        if (channelNode == null || channelNode.isMissingNode() || channelNode.isNull()) {
            return null;
        }

        LinkedHashSet<NotificationChannel> channels = new LinkedHashSet<>();
        if (channelNode.isArray()) {
            channelNode.forEach(value -> addChannel(channels, value.asText()));
        } else {
            for (String value : channelNode.asText().split(",")) {
                addChannel(channels, value);
            }
        }
        return channels.isEmpty() ? null : new ArrayList<>(channels);
    }

    private void addChannel(LinkedHashSet<NotificationChannel> channels, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        try {
            channels.add(NotificationChannel.valueOf(value.trim().replace('-', '_').toUpperCase()));
        } catch (IllegalArgumentException ignored) {
            // Unknown channel values should not stop processing the domain event.
        }
    }

    private JsonNode firstNode(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = node.path(fieldName);
            if (!value.isMissingNode() && !value.isNull()) {
                return value;
            }
        }
        return null;
    }

    private String text(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = node.path(fieldName);
            if (!value.isMissingNode() && !value.isNull() && !value.asText().isBlank()) {
                return value.asText();
            }
        }
        return null;
    }

    private UUID uuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private boolean hasAddress(NotificationEventPayload.RecipientPayload recipient) {
        return hasText(recipient.keycloakSub()) || hasText(recipient.email()) || hasText(recipient.phone());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isScalarArray(JsonNode value) {
        if (!value.isArray()) {
            return false;
        }
        for (JsonNode item : value) {
            if (!item.isValueNode()) {
                return false;
            }
        }
        return true;
    }

    private String joinScalarArray(JsonNode value) {
        List<String> parts = new ArrayList<>();
        value.forEach(item -> parts.add(item.asText()));
        return String.join(", ", parts);
    }

    private String json(JsonNode value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ignored) {
            return value.toString();
        }
    }
}
