package com.nuvemite.cms.notifications.messaging;

import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import java.util.HashMap;
import java.util.Map;

public final class EventPayloadMapper {

    private EventPayloadMapper() {}

    public static Map<String, String> toTemplateVariables(
            String eventType, NotificationEventPayload payload, NotificationEventPayload.RecipientPayload recipient) {
        Map<String, String> vars = new HashMap<>();
        if (payload.variables() != null) {
            payload.variables().forEach((k, v) -> vars.put(k, v != null ? v : ""));
        }
        vars.put("recipientName", recipient.name() != null && !recipient.name().isBlank() ? recipient.name() : "there");
        vars.put("detailHtml", vars.getOrDefault("detailHtml", ""));
        vars.put("subjectLine", defaultSubject(eventType, vars));
        return vars;
    }

    private static String defaultSubject(String eventType, Map<String, String> vars) {
        return switch (eventType) {
            case EventTypes.LICENSE_GRANTED -> "License " + vars.getOrDefault("licenseNumber", "") + " granted";
            case EventTypes.PERMIT_APPROVED -> "Permit " + vars.getOrDefault("permitNumber", "") + " approved";
            case EventTypes.PAYMENT_COMPLETED -> "Payment " + vars.getOrDefault("reference", "") + " received";
            default -> "Notification";
        };
    }
}
