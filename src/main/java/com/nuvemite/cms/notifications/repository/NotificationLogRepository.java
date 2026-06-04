package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    Page<NotificationLog> findByChannel(NotificationChannel channel, Pageable pageable);

    Page<NotificationLog> findByStatus(DeliveryStatus status, Pageable pageable);

    Page<NotificationLog> findByChannelAndStatus(NotificationChannel channel, DeliveryStatus status, Pageable pageable);

    Page<NotificationLog> findByEventCode(String eventCode, Pageable pageable);

    Page<NotificationLog> findByChannelAndEventCode(NotificationChannel channel, String eventCode, Pageable pageable);

    Page<NotificationLog> findByEventCodeAndStatus(String eventCode, DeliveryStatus status, Pageable pageable);

    Page<NotificationLog> findByChannelAndEventCodeAndStatus(
            NotificationChannel channel, String eventCode, DeliveryStatus status, Pageable pageable);

    Page<NotificationLog> findByRecipientContainingIgnoreCase(String recipient, Pageable pageable);

    Page<NotificationLog> findByChannelAndRecipientContainingIgnoreCase(
            NotificationChannel channel, String recipient, Pageable pageable);

    @Query("""
            SELECT l FROM NotificationLog l
            WHERE (:channel IS NULL OR l.channel = :channel)
              AND (:status IS NULL OR l.status = :status)
              AND (:eventCode IS NULL OR l.eventCode = :eventCode)
              AND (:recipient IS NULL OR LOWER(l.recipient) LIKE LOWER(CONCAT('%', :recipient, '%')))
            ORDER BY l.sentAt DESC
            """)
    Page<NotificationLog> findByCriteria(
            @Param("channel") NotificationChannel channel,
            @Param("status") DeliveryStatus status,
            @Param("eventCode") String eventCode,
            @Param("recipient") String recipient,
            Pageable pageable);

    long countByChannel(NotificationChannel channel);

    long countByChannelAndStatus(NotificationChannel channel, DeliveryStatus status);

    long countByEventCodeAndChannel(String eventCode, NotificationChannel channel);

    long countByEventCodeAndChannelAndStatus(String eventCode, NotificationChannel channel, DeliveryStatus status);

    @Query("""
            SELECT l.eventCode FROM NotificationLog l
            WHERE l.channel = :channel
            GROUP BY l.eventCode
            """)
    List<String> findDistinctEventCodesByChannel(@Param("channel") NotificationChannel channel);
}
