package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.exception.NotificationDeliveryException;
import com.nuvemite.cms.notifications.messaging.EventTypes;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    private final Set<String> supportedEvents;

    public SmsNotificationService() {
        this.supportedEvents = EventTypes.systemDefinitions().stream()
                .filter(def -> def.supportedChannels().contains(NotificationChannel.SMS))
                .map(EventTypes.NotificationEventDefinition::eventCode)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    public NotificationDeliveryResult send(NotificationMessage message) {
        if (message.recipient() == null || message.recipient().isBlank()) {
            throw new NotificationDeliveryException("SMS recipient phone is required");
        }
        log.info(
                "SMS notification queued for {} event {} id {} metadata {}: {}",
                message.recipient(),
                message.eventCode(),
                message.eventId(),
                message.metadata(),
                message.content());
        return NotificationDeliveryResult.sent("local-sms-" + message.notificationId());
    }

    @Override
    public Set<String> supportedEventTypes() {
        return supportedEvents;
    }

    @Override
    public boolean supportsEventType(String eventCode) {
        return supportedEvents.contains(eventCode);
    }

    @Override
    public NotificationDeliveryResult sendForEvent(String eventCode, NotificationMessage message) {
        if (!supportsEventType(eventCode)) {
            throw new NotificationDeliveryException(
                    "SMS channel is not supported for event type: " + eventCode);
        }
        log.debug("Sending SMS notification for event type: {}", eventCode);
        return send(message);
    }
}
