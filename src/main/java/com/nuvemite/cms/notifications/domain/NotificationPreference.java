package com.nuvemite.cms.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "notification_preference")
public class NotificationPreference {

    @Id
    private UUID id;

    @Column(name = "keycloak_sub", nullable = false)
    private String keycloakSub;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(name = "event_type")
    private String eventType;

    @Column(nullable = false)
    private boolean enabled = true;

    protected NotificationPreference() {}

    public static NotificationPreference create(
            String keycloakSub, NotificationChannel channel, String eventType, boolean enabled) {
        NotificationPreference preference = new NotificationPreference();
        preference.id = UUID.randomUUID();
        preference.keycloakSub = keycloakSub;
        preference.channel = channel;
        preference.eventType = eventType;
        preference.enabled = enabled;
        return preference;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UUID getId() {
        return id;
    }

    public String getKeycloakSub() {
        return keycloakSub;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
