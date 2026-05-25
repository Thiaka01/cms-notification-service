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
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "delivery_attempt")
public class DeliveryAttempt {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "provider_ref")
    private String providerRef;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;


    public static DeliveryAttempt pending(Notification notification, NotificationChannel channel) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.id = UUID.randomUUID();
        attempt.notification = notification;
        attempt.channel = channel;
        attempt.status = DeliveryStatus.PENDING;
        attempt.attemptedAt = Instant.now();
        return attempt;
    }

    public void markSent(String providerRef) {
        this.status = DeliveryStatus.SENT;
        this.providerRef = providerRef;
    }

    public void markFailed(String errorMessage) {
        this.status = DeliveryStatus.FAILED;
        this.errorMessage = errorMessage;
    }




}
