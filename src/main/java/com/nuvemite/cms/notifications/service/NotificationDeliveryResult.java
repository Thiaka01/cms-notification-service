package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;

public record NotificationDeliveryResult(
        DeliveryStatus status,
        String providerReference,
        String errorMessage) {

    public static NotificationDeliveryResult sent(String providerReference) {
        return new NotificationDeliveryResult(DeliveryStatus.SENT, providerReference, null);
    }

    public static NotificationDeliveryResult failed(String errorMessage) {
        return new NotificationDeliveryResult(DeliveryStatus.FAILED, null, errorMessage);
    }

    public boolean delivered() {
        return status == DeliveryStatus.SENT;
    }
}
