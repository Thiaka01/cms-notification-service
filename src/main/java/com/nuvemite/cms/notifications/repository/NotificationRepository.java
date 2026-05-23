package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {}
