package com.nuvemite.cms.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "inbox_processed_event")
public class InboxProcessedEvent {

    @EmbeddedId
    private InboxProcessedEventId id;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxProcessedEvent() {}

    public static InboxProcessedEvent create(String eventId, String consumerGroup) {
        InboxProcessedEvent event = new InboxProcessedEvent();
        event.id = new InboxProcessedEventId(eventId, consumerGroup);
        event.processedAt = Instant.now();
        return event;
    }

    @Embeddable
    public static class InboxProcessedEventId implements Serializable {

        @Column(name = "event_id")
        private String eventId;

        @Column(name = "consumer_group")
        private String consumerGroup;

        protected InboxProcessedEventId() {}

        public InboxProcessedEventId(String eventId, String consumerGroup) {
            this.eventId = eventId;
            this.consumerGroup = consumerGroup;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof InboxProcessedEventId that)) {
                return false;
            }
            return Objects.equals(eventId, that.eventId)
                    && Objects.equals(consumerGroup, that.consumerGroup);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventId, consumerGroup);
        }
    }
}
