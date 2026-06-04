package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.NotificationEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {

    boolean existsByEventCode(String eventCode);

    Optional<NotificationEvent> findByEventCode(String eventCode);

    Optional<NotificationEvent> findByEventCodeAndActiveTrue(String eventCode);
}
