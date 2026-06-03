package com.nuvemite.cms.notifications.messaging;

import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EventPayloadMapper {

    public Map<String, String> toTemplateVariables(
            String eventType, NotificationEventPayload payload, NotificationEventPayload.RecipientPayload recipient) {
        Map<String, String> vars = new HashMap<>();
        if (payload.variables() != null) {
            payload.variables().forEach((k, v) -> vars.put(k, v != null ? v : ""));
        }
        vars.put("eventType", eventType);
        vars.put("eventName", eventName(eventType));
        vars.put("keycloakSub", valueOrEmpty(recipient.keycloakSub()));
        vars.put("recipientEmail", valueOrEmpty(recipient.email()));
        vars.put("recipientPhone", valueOrEmpty(recipient.phone()));
        vars.put("companyId", recipient.companyId() != null ? recipient.companyId().toString() : "");
        vars.put("recipientName", recipient.name() != null && !recipient.name().isBlank() ? recipient.name() : "there");
        vars.put("detailHtml", vars.getOrDefault("detailHtml", ""));
        vars.put("subjectLine", defaultSubject(eventType, vars));
        return vars;
    }

    private String defaultSubject(String eventType, Map<String, String> vars) {
        String configured = vars.get("subjectLine");
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        return eventName(eventType);
    }

    private String eventName(String eventType) {
        if (eventType == null || eventType.isBlank()) {
            return "Notification";
        }
        String normalized = eventType;
        if (normalized.startsWith("cms.")) {
            normalized = normalized.substring(4);
        }
        normalized = normalized.replaceAll("\\.v\\d+$", "").replace('.', ' ').replace('_', ' ');
        String[] words = normalized.split("\\s+");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!title.isEmpty()) {
                title.append(' ');
            }
            title.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                title.append(word.substring(1));
            }
        }
        return !title.isEmpty() ? title.toString() : "Notification";
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }
}
