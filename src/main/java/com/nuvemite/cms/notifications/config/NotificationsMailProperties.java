package com.nuvemite.cms.notifications.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cms.notifications.mail")
public record NotificationsMailProperties(
        boolean enabled, String from, String fromName, String replyTo) {}
