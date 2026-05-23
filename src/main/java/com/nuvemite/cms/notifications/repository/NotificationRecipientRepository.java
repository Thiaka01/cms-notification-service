package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.NotificationRecipient;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, UUID> {

    @Query(
            """
            SELECT r FROM NotificationRecipient r
            JOIN FETCH r.notification n
            WHERE r.keycloakSub = :sub
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRecipient> findInboxByKeycloakSub(@Param("sub") String keycloakSub, Pageable pageable);

    Optional<NotificationRecipient> findByIdAndKeycloakSub(UUID id, String keycloakSub);
}
