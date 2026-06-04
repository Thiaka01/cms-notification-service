package com.nuvemite.cms.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    private UUID id;

    @Column(name = "event_code", nullable = false, length = 128)
    private String eventCode;

    @Column(nullable = false, length = 512)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    public static NotificationLog sent(String eventCode, String recipient, NotificationChannel channel) {
        return create(eventCode, recipient, channel, DeliveryStatus.SENT, null);
    }

    public static NotificationLog failed(
            String eventCode,
            String recipient,
            NotificationChannel channel,
            String errorMessage) {
        return create(eventCode, recipient, channel, DeliveryStatus.FAILED, errorMessage);
    }

    private static NotificationLog create(
            String eventCode,
            String recipient,
            NotificationChannel channel,
            DeliveryStatus status,
            String errorMessage) {
        NotificationLog log = new NotificationLog();
        log.id = UUID.randomUUID();
        log.eventCode = eventCode;
        log.recipient = recipient;
        log.channel = channel;
        log.status = status;
        log.errorMessage = errorMessage;
        log.sentAt = Instant.now();
        return log;
    }
}
