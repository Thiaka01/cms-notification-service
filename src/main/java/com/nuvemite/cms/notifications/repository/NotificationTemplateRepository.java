package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByEventTypeAndChannelAndActiveTrue(
            String eventType, NotificationChannel channel);

    Optional<NotificationTemplate> findByEventEventCodeAndChannelAndActiveTrue(
            String eventCode, NotificationChannel channel);

    Optional<NotificationTemplate> findByEventEventCodeAndChannel(String eventCode, NotificationChannel channel);

    boolean existsByEventEventCodeAndChannel(String eventCode, NotificationChannel channel);

    List<NotificationTemplate> findByEventEventCode(String eventCode);
}
