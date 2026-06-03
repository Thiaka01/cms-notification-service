package com.nuvemite.cms.notifications.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.notifications.domain.InboxProcessedEvent;
import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import com.nuvemite.cms.notifications.repository.InboxProcessedEventRepository;
import com.nuvemite.cms.notifications.service.NotificationDispatchService;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DomainEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DomainEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final DomainEventNotificationPayloadMapper payloadMapper;
    private final InboxProcessedEventRepository inboxRepository;
    private final NotificationDispatchService dispatchService;

    public DomainEventConsumer(
            ObjectMapper objectMapper,
            DomainEventNotificationPayloadMapper payloadMapper,
            InboxProcessedEventRepository inboxRepository,
            NotificationDispatchService dispatchService) {
        this.objectMapper = objectMapper;
        this.payloadMapper = payloadMapper;
        this.inboxRepository = inboxRepository;
        this.dispatchService = dispatchService;
    }

    @KafkaListener(
            topicPattern = EventTypes.CMS_EVENT_TOPIC_PATTERN,
            groupId = EventTypes.CONSUMER_GROUP)
    @Transactional
    public void onDomainEvent(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        JsonNode root = objectMapper.readTree(record.value());
        String resolvedEventId = resolveEventId(record, eventId, root);
        if (isProcessed(resolvedEventId)) {
            return;
        }

        String eventCode = resolveEventCode(record, root);
        NotificationEventPayload payload = payloadMapper.toNotificationPayload(root, eventCode);

        dispatchService.dispatch(eventCode, resolvedEventId, payload);
        markProcessed(resolvedEventId);
        log.debug("Processed notification event {} code {} on topic {}", resolvedEventId, eventCode, record.topic());
    }

    @EventListener
    @Transactional
    public void onSpringDomainEvent(CmsNotificationDomainEvent event) {
        dispatchService.dispatch(event.eventCode(), event.eventId(), event.payload());
    }

    private boolean isProcessed(String eventId) {
        return inboxRepository.existsById(
                new InboxProcessedEvent.InboxProcessedEventId(eventId, EventTypes.CONSUMER_GROUP));
    }

    private void markProcessed(String eventId) {
        inboxRepository.save(InboxProcessedEvent.create(eventId, EventTypes.CONSUMER_GROUP));
    }

    private String header(ConsumerRecord<String, String> record, String name) {
        var header = record.headers().lastHeader(name);
        if (header == null) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    private String resolveEventId(ConsumerRecord<String, String> record, String headerEventId, JsonNode root) {
        if (headerEventId != null && !headerEventId.isBlank()) {
            return headerEventId;
        }
        String payloadEventId = text(root, "eventId");
        if (payloadEventId != null) {
            return payloadEventId;
        }
        String payloadId = text(root, "id");
        if (payloadId != null) {
            return payloadId;
        }
        return record.topic() + "-" + record.partition() + "-" + record.offset();
    }

    private String resolveEventCode(ConsumerRecord<String, String> record, JsonNode root) {
        String headerEventCode = header(record, "eventCode");
        if (headerEventCode != null && !headerEventCode.isBlank()) {
            return headerEventCode;
        }
        String payloadEventCode = text(root, "eventCode", "eventType");
        if (payloadEventCode != null) {
            return payloadEventCode;
        }
        return record.topic();
    }

    private String text(JsonNode root, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = root.path(fieldName);
            if (!value.isMissingNode() && !value.isNull() && !value.asText().isBlank()) {
                return value.asText();
            }
        }
        return null;
    }
}
