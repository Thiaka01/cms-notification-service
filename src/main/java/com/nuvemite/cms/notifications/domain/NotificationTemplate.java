package com.nuvemite.cms.notifications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_template")
public class NotificationTemplate {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private NotificationEvent event;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    private String subject;

    @Column(name = "template_content", nullable = false)
    private String bodyTemplate;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static NotificationTemplate create(
            NotificationEvent event,
            NotificationChannel channel,
            String subject,
            String bodyTemplate,
            boolean active) {
        NotificationTemplate template = new NotificationTemplate();
        template.id = UUID.randomUUID();
        template.setEvent(event);
        template.channel = channel;
        template.subject = subject;
        template.bodyTemplate = bodyTemplate;
        template.active = active;
        Instant now = Instant.now();
        template.createdAt = now;
        template.updatedAt = now;
        return template;
    }

    public void setEvent(NotificationEvent event) {
        this.event = event;
        this.eventType = event != null ? event.getEventCode() : null;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (event != null) {
            eventType = event.getEventCode();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        if (event != null) {
            eventType = event.getEventCode();
        }
        updatedAt = Instant.now();
    }





    public boolean isActive() {
        return active;
    }
}
