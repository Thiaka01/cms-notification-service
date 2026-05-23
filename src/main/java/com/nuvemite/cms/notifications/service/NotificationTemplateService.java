package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import com.nuvemite.cms.notifications.exception.ResourceNotFoundException;
import com.nuvemite.cms.notifications.repository.NotificationTemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;

    public NotificationTemplateService(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public NotificationTemplate requireTemplate(String eventType, NotificationChannel channel) {
        return templateRepository
                .findByEventTypeAndChannelAndActiveTrue(eventType, channel)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active template for event " + eventType + " channel " + channel));
    }
}
