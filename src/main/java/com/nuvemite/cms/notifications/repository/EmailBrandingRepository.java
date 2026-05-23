package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.EmailBranding;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmailBrandingRepository extends JpaRepository<EmailBranding, UUID> {

    Optional<EmailBranding> findByActiveTrue();

    @Modifying
    @Query("UPDATE EmailBranding b SET b.active = false WHERE b.active = true")
    void deactivateAllActive();
}
