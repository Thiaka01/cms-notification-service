package com.nuvemite.cms.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    private UUID id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    private String subject;

    @Column(nullable = false)
    private String body;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Notification() {}

    public static Notification create(String eventType, String eventId, String subject, String body) {
        Notification notification = new Notification();
        notification.id = UUID.randomUUID();
        notification.eventType = eventType;
        notification.eventId = eventId;
        notification.subject = subject;
        notification.body = body;
        notification.createdAt = Instant.now();
        return notification;
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
