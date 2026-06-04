package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.DeliveryAttempt;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, UUID> {

    Page<DeliveryAttempt> findByChannel(NotificationChannel channel, Pageable pageable);

    Page<DeliveryAttempt> findByStatus(DeliveryStatus status, Pageable pageable);

    Page<DeliveryAttempt> findByChannelAndStatus(NotificationChannel channel, DeliveryStatus status, Pageable pageable);

    @Query("""
            SELECT da FROM DeliveryAttempt da
            JOIN da.notification n
            WHERE (:channel IS NULL OR da.channel = :channel)
              AND (:status IS NULL OR da.status = :status)
              AND (:eventType IS NULL OR n.eventType = :eventType)
            ORDER BY da.attemptedAt DESC
            """)
    Page<DeliveryAttempt> findByCriteria(
            @Param("channel") NotificationChannel channel,
            @Param("status") DeliveryStatus status,
            @Param("eventType") String eventType,
            Pageable pageable);

    long countByChannel(NotificationChannel channel);

    long countByChannelAndStatus(NotificationChannel channel, DeliveryStatus status);

    @Query("""
            SELECT n.eventType FROM DeliveryAttempt da
            JOIN da.notification n
            WHERE da.channel = :channel
            GROUP BY n.eventType
            """)
    List<String> findDistinctEventTypesByChannel(@Param("channel") NotificationChannel channel);
}
