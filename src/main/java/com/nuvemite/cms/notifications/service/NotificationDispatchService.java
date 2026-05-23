package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.Notification;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationRecipient;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import com.nuvemite.cms.notifications.email.TemplateRenderer;
import com.nuvemite.cms.notifications.messaging.EventPayloadMapper;
import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import com.nuvemite.cms.notifications.repository.NotificationRecipientRepository;
import com.nuvemite.cms.notifications.repository.NotificationRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationDispatchService {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchService.class);

    private final NotificationTemplateService templateService;
    private final NotificationPreferenceService preferenceService;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final EmailNotificationService emailNotificationService;

    public NotificationDispatchService(
            NotificationTemplateService templateService,
            NotificationPreferenceService preferenceService,
            NotificationRepository notificationRepository,
            NotificationRecipientRepository recipientRepository,
            EmailNotificationService emailNotificationService) {
        this.templateService = templateService;
        this.preferenceService = preferenceService;
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional
    public void dispatch(String eventType, String eventId, NotificationEventPayload payload) {
        if (payload.recipients() == null || payload.recipients().isEmpty()) {
            log.warn("No recipients for event {} id {}", eventType, eventId);
            return;
        }

        for (NotificationEventPayload.RecipientPayload recipient : payload.recipients()) {
            if (recipient.keycloakSub() == null || recipient.keycloakSub().isBlank()) {
                continue;
            }
            Map<String, String> variables =
                    EventPayloadMapper.toTemplateVariables(eventType, payload, recipient);
            dispatchChannel(eventType, eventId, recipient, variables, NotificationChannel.IN_APP);
            if (recipient.email() != null && !recipient.email().isBlank()) {
                dispatchChannel(eventType, eventId, recipient, variables, NotificationChannel.EMAIL);
            }
        }
    }

    private void dispatchChannel(
            String eventType,
            String eventId,
            NotificationEventPayload.RecipientPayload recipient,
            Map<String, String> variables,
            NotificationChannel channel) {
        if (!preferenceService.isChannelEnabled(recipient.keycloakSub(), eventType, channel)) {
            return;
        }

        NotificationTemplate template = templateService.requireTemplate(eventType, channel);
        String body = TemplateRenderer.render(template.getBodyTemplate(), variables);
        String subject = template.getSubject() != null
                ? TemplateRenderer.render(template.getSubject(), variables)
                : variables.getOrDefault("subjectLine", "Notification");

        Notification notification = Notification.create(eventType, eventId, subject, body);
        notificationRepository.save(notification);

        NotificationRecipient notificationRecipient =
                NotificationRecipient.create(notification, recipient.keycloakSub());
        recipientRepository.save(notificationRecipient);

        if (channel == NotificationChannel.EMAIL) {
            emailNotificationService.send(notification, template, recipient.email(), variables);
        }
    }
}
