package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.DeliveryAttempt;
import com.nuvemite.cms.notifications.domain.Notification;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationEvent;
import com.nuvemite.cms.notifications.domain.NotificationLog;
import com.nuvemite.cms.notifications.domain.NotificationRecipient;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import com.nuvemite.cms.notifications.email.TemplateRenderer;
import com.nuvemite.cms.notifications.exception.NotificationDeliveryException;
import com.nuvemite.cms.notifications.messaging.EventPayloadMapper;
import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import com.nuvemite.cms.notifications.repository.DeliveryAttemptRepository;
import com.nuvemite.cms.notifications.repository.NotificationLogRepository;
import com.nuvemite.cms.notifications.repository.NotificationRecipientRepository;
import com.nuvemite.cms.notifications.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationDispatchService {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchService.class);
    private static final int MAX_DELIVERY_ATTEMPTS = 3;

    private final NotificationEventService eventService;
    private final NotificationTemplateService templateService;
    private final NotificationPreferenceService preferenceService;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final Map<NotificationChannel, NotificationService> notificationServices;
    private final EventPayloadMapper eventPayloadMapper;

    public NotificationDispatchService(
            NotificationEventService eventService,
            NotificationTemplateService templateService,
            NotificationPreferenceService preferenceService,
            NotificationRepository notificationRepository,
            NotificationRecipientRepository recipientRepository,
            DeliveryAttemptRepository deliveryAttemptRepository,
            NotificationLogRepository notificationLogRepository,
            List<NotificationService> notificationServices,
            EventPayloadMapper eventPayloadMapper) {
        this.eventService = eventService;
        this.templateService = templateService;
        this.preferenceService = preferenceService;
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.notificationServices = notificationServices.stream()
                .collect(Collectors.toUnmodifiableMap(NotificationService::channel, Function.identity()));
        this.eventPayloadMapper = eventPayloadMapper;
    }

    @Transactional
    public void dispatch(String eventCode, String eventId, NotificationEventPayload payload) {
        NotificationEvent event = eventService.findActiveEvent(eventCode)
                .orElseGet(() -> {
                    log.warn("Notification event {} is not registered or inactive, skipping", eventCode);
                    return null;
                });
        if (event == null) {
            return;
        }

        if (payload.recipients() == null || payload.recipients().isEmpty()) {
            log.warn("No recipients for event {} id {}", eventCode, eventId);
            return;
        }

        for (NotificationEventPayload.RecipientPayload recipient : payload.recipients()) {
            if (!hasAddressableRecipient(recipient)) {
                continue;
            }
            Map<String, String> variables =
                    eventPayloadMapper.toTemplateVariables(event.getEventCode(), payload, recipient);
            for (NotificationChannel channel : channelsFor(event, recipient)) {
                dispatchChannel(event, eventId, recipient, variables, channel);
            }
        }
    }

    private void dispatchChannel(
            NotificationEvent event,
            String eventId,
            NotificationEventPayload.RecipientPayload recipient,
            Map<String, String> variables,
            NotificationChannel channel) {
        if (!canDispatchToChannel(recipient, channel)) {
            return;
        }

        if (hasText(recipient.keycloakSub())
                && !preferenceService.isChannelEnabled(recipient.keycloakSub(), event.getEventCode(), channel)) {
            return;
        }

        NotificationTemplate template = templateService.findActiveTemplate(event.getEventCode(), channel)
                .orElseGet(() -> {
                    log.warn("No active template for event {} channel {}, skipping", event.getEventCode(), channel);
                    return null;
                });
        if (template == null) {
            return;
        }
        String body = TemplateRenderer.render(template.getBodyTemplate(), variables);
        String subject = template.getSubject() != null
                ? TemplateRenderer.render(template.getSubject(), variables)
                : variables.getOrDefault("subjectLine", "Notification");

        Notification notification = Notification.create(event.getEventCode(), eventId, subject, body);
        notificationRepository.save(notification);

        if (hasText(recipient.keycloakSub())) {
            NotificationRecipient notificationRecipient =
                    NotificationRecipient.create(notification, recipient.keycloakSub());
            recipientRepository.save(notificationRecipient);
        }

        String recipientAddress = recipientAddress(recipient, channel);
        if (channel == NotificationChannel.IN_APP) {
            notificationLogRepository.save(NotificationLog.sent(event.getEventCode(), recipientAddress, channel));
            return;
        }

        NotificationService notificationService = notificationServices.get(channel);
        if (notificationService == null) {
            String error = "No notification service registered for channel " + channel;
            log.warn(error);
            notificationLogRepository.save(NotificationLog.failed(event.getEventCode(), recipientAddress, channel, error));
            return;
        }

        NotificationMessage message = new NotificationMessage(
                notification.getId(),
                event.getEventCode(),
                eventId,
                recipientAddress,
                subject,
                body,
                Map.copyOf(variables),
                Map.of("channel", channel.name()));
        deliverWithRetry(notification, message, notificationService, recipientAddress, channel);
    }

    private boolean hasAddressableRecipient(NotificationEventPayload.RecipientPayload recipient) {
        return hasText(recipient.keycloakSub()) || hasText(recipient.email()) || hasText(recipient.phone());
    }

    private List<NotificationChannel> channelsFor(
            NotificationEvent event,
            NotificationEventPayload.RecipientPayload recipient) {
        List<NotificationChannel> configuredChannels = new ArrayList<>(event.getSupportedChannels());
        if (configuredChannels.isEmpty()) {
            return List.of();
        }
        if (recipient.channels() != null && !recipient.channels().isEmpty()) {
            configuredChannels.retainAll(recipient.channels());
        }
        return configuredChannels.stream()
                .filter(channel -> canDispatchToChannel(recipient, channel))
                .toList();
    }

    private void deliverWithRetry(
            Notification notification,
            NotificationMessage message,
            NotificationService notificationService,
            String recipientAddress,
            NotificationChannel channel) {
        NotificationDeliveryResult lastResult = null;
        for (int attemptNumber = 1; attemptNumber <= MAX_DELIVERY_ATTEMPTS; attemptNumber++) {
            DeliveryAttempt attempt = DeliveryAttempt.pending(notification, channel);
            deliveryAttemptRepository.save(attempt);
            try {
                lastResult = notificationService.sendForEvent(message.eventCode(), message);
                if (lastResult.delivered()) {
                    attempt.markSent(lastResult.providerReference());
                    deliveryAttemptRepository.save(attempt);
                    notificationLogRepository.save(NotificationLog.sent(message.eventCode(), recipientAddress, channel));
                    return;
                }
                attempt.markFailed(lastResult.errorMessage());
            } catch (NotificationDeliveryException ex) {
                lastResult = NotificationDeliveryResult.failed(rootMessage(ex));
                attempt.markFailed(lastResult.errorMessage());
            } catch (Exception ex) {
                lastResult = NotificationDeliveryResult.failed(ex.getMessage());
                attempt.markFailed(lastResult.errorMessage());
            }
            deliveryAttemptRepository.save(attempt);
            log.warn(
                    "Notification delivery attempt {}/{} failed for event {} channel {} recipient {}: {}",
                    attemptNumber,
                    MAX_DELIVERY_ATTEMPTS,
                    message.eventCode(),
                    channel,
                    recipientAddress,
                    lastResult.errorMessage());
        }
        String errorMessage = lastResult != null ? lastResult.errorMessage() : "Delivery failed";
        notificationLogRepository.save(NotificationLog.failed(message.eventCode(), recipientAddress, channel, errorMessage));
    }

    private boolean canDispatchToChannel(
            NotificationEventPayload.RecipientPayload recipient, NotificationChannel channel) {
        return switch (channel) {
            case IN_APP -> hasText(recipient.keycloakSub());
            case EMAIL -> hasText(recipient.email());
            case SMS -> hasText(recipient.phone());
        };
    }

    private String recipientAddress(NotificationEventPayload.RecipientPayload recipient, NotificationChannel channel) {
        return switch (channel) {
            case IN_APP -> recipient.keycloakSub();
            case EMAIL -> recipient.email();
            case SMS -> recipient.phone();
        };
    }

    private String rootMessage(Exception ex) {
        Throwable cause = ex.getCause();
        return cause != null && cause.getMessage() != null ? cause.getMessage() : ex.getMessage();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
