package com.nuvemite.cms.notifications.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_event")
public class NotificationEvent {

    @Id
    private UUID id;

    @Column(name = "event_code", nullable = false, unique = true, length = 128)
    private String eventCode;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notification_event_channel", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private Set<NotificationChannel> supportedChannels = new LinkedHashSet<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static NotificationEvent create(
            String eventCode,
            String name,
            String description,
            Set<NotificationChannel> supportedChannels,
            boolean active) {
        NotificationEvent event = new NotificationEvent();
        event.id = UUID.randomUUID();
        event.eventCode = eventCode;
        event.name = name;
        event.description = description;
        event.supportedChannels = new LinkedHashSet<>(supportedChannels);
        event.active = active;
        Instant now = Instant.now();
        event.createdAt = now;
        event.updatedAt = now;
        return event;
    }

    public void synchronizeDefinition(
            String name,
            String description,
            Set<NotificationChannel> supportedChannels) {
        this.name = name;
        this.description = description;
        this.supportedChannels = new LinkedHashSet<>(supportedChannels);
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
