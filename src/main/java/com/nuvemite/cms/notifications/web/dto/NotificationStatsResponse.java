package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;

public record NotificationStatsResponse(
        NotificationChannel channel,
        long total,
        long sent,
        long failed,
        long pending) {

    public static NotificationStatsResponse of(
            NotificationChannel channel,
            long total,
            long sent,
            long failed,
            long pending) {
        return new NotificationStatsResponse(channel, total, sent, failed, pending);
    }
}
