package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.DeliveryAttempt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, UUID> {}
