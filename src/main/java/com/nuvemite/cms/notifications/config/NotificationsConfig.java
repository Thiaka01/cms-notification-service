package com.nuvemite.cms.notifications.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NotificationsMailProperties.class)
public class NotificationsConfig {}
