package com.nuvemite.cms.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.messaging.events.NotificationEventPayload;
import com.nuvemite.cms.notifications.repository.DeliveryAttemptRepository;
import com.nuvemite.cms.notifications.repository.NotificationRecipientRepository;
import com.nuvemite.cms.notifications.repository.NotificationRepository;
import com.nuvemite.cms.notifications.support.IntegrationTestBase;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationDispatchIntegrationTest extends IntegrationTestBase {

    @Autowired
    private NotificationDispatchService dispatchService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationRecipientRepository recipientRepository;

    @Autowired
    private DeliveryAttemptRepository deliveryAttemptRepository;

    @Test
    void dispatchesInAppAndEmail() {
        NotificationEventPayload payload = new NotificationEventPayload(
                UUID.randomUUID(),
                List.of(new NotificationEventPayload.RecipientPayload(
                        "user-sub", "user@test.local", "Jane")),
                Map.of(
                        "licenseNumber", "LIC-001",
                        "chemicalName", "Acetone",
                        "premiseName", "Plant A"));

        dispatchService.dispatch("cms.license.granted.v1", "evt-1", payload);

        assertThat(notificationRepository.count()).isEqualTo(2);
        assertThat(recipientRepository.count()).isEqualTo(2);
        assertThat(deliveryAttemptRepository.findAll())
                .filteredOn(a -> a.getChannel() == NotificationChannel.EMAIL)
                .extracting(a -> a.getStatus())
                .contains(DeliveryStatus.SENT);
    }
}
