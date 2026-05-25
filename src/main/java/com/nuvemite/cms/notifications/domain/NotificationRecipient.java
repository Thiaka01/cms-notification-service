package com.nuvemite.cms.notifications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_recipient")
public class NotificationRecipient {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "keycloak_sub", nullable = false)
    private String keycloakSub;

    @Column(name = "read_at")
    private Instant readAt;


    public static NotificationRecipient create(Notification notification, String keycloakSub) {
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.id = UUID.randomUUID();
        recipient.notification = notification;
        recipient.keycloakSub = keycloakSub;
        return recipient;
    }

    public void markRead() {
        this.readAt = Instant.now();
    }




}
