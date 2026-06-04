package com.nuvemite.cms.notifications.service;

import java.util.Map;
import java.util.UUID;

public record NotificationMessage(
        UUID notificationId,
        String eventCode,
        String eventId,
        String recipient,
        String subject,
        String content,
        Map<String, String> templateData,
        Map<String, String> metadata) {}
