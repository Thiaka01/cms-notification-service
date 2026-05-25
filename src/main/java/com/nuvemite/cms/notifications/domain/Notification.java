package com.nuvemite.cms.notifications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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






}
