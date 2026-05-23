package com.nuvemite.cms.notifications.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.notifications.domain.InboxProcessedEvent;
import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import com.nuvemite.cms.notifications.repository.InboxProcessedEventRepository;
import com.nuvemite.cms.notifications.service.NotificationDispatchService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DomainEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DomainEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final InboxProcessedEventRepository inboxRepository;
    private final NotificationDispatchService dispatchService;

    public DomainEventConsumer(
            ObjectMapper objectMapper,
            InboxProcessedEventRepository inboxRepository,
            NotificationDispatchService dispatchService) {
        this.objectMapper = objectMapper;
        this.inboxRepository = inboxRepository;
        this.dispatchService = dispatchService;
    }

    @KafkaListener(
            topics = {
                EventTypes.PERMIT_SUBMITTED,
                EventTypes.PERMIT_APPROVED,
                EventTypes.LICENSE_GRANTED,
                EventTypes.LICENSE_INSPECTION_SCHEDULED,
                EventTypes.BATCH_CREATED,
                EventTypes.MOVEMENT_COMPLETED,
                EventTypes.PAYMENT_COMPLETED,
                EventTypes.VISIT_SCHEDULED
            },
            groupId = EventTypes.CONSUMER_GROUP)
    @Transactional
    public void onDomainEvent(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && isProcessed(eventId)) {
            return;
        }

        NotificationEventPayload payload = objectMapper.readValue(record.value(), NotificationEventPayload.class);
        String resolvedEventId = eventId != null
                ? eventId
                : payload.eventId() != null ? payload.eventId().toString() : record.offset() + "-" + record.partition();

        dispatchService.dispatch(record.topic(), resolvedEventId, payload);
        markProcessed(resolvedEventId);
        log.debug("Processed notification event {} on topic {}", resolvedEventId, record.topic());
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
        return new String(header.value());
    }
}
