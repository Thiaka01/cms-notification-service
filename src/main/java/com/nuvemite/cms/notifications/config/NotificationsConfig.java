package com.nuvemite.cms.notifications.config;

import com.nuvemite.cms.notifications.service.NotificationEventService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NotificationsMailProperties.class)
public class NotificationsConfig {

    @Bean
    ApplicationRunner notificationEventDefinitionSynchronizer(NotificationEventService eventService) {
        return args -> eventService.synchronizeSystemDefinitions();
    }
}
