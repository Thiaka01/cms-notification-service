package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.NotificationPreference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    List<NotificationPreference> findByKeycloakSub(String keycloakSub);

    Optional<NotificationPreference> findByKeycloakSubAndChannelAndEventType(
            String keycloakSub, com.nuvemite.cms.notifications.domain.NotificationChannel channel, String eventType);
}
