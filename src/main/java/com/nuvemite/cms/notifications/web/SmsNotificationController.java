package com.nuvemite.cms.notifications.web;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.service.ChannelNotificationQueryService;
import com.nuvemite.cms.notifications.web.dto.NotificationTemplateResponse;
import com.nuvemite.cms.notifications.web.dto.DeliveryAttemptResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationLogResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationStatsResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for querying SMS notification events, logs,
 * delivery attempts, stats, and templates.
 */
@RestController
@RequestMapping("/api/v1/notifications/sms")
public class SmsNotificationController {

    private final ChannelNotificationQueryService queryService;

    public SmsNotificationController(ChannelNotificationQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/logs")
    public Page<NotificationLogResponse> getLogs(Pageable pageable) {
        return queryService.getLogsByChannel(NotificationChannel.SMS, pageable);
    }

    @GetMapping("/logs/sent")
    public Page<NotificationLogResponse> getSentLogs(Pageable pageable) {
        return queryService.getLogsByStatus(NotificationChannel.SMS, DeliveryStatus.SENT, pageable);
    }

    @GetMapping("/logs/failed")
    public Page<NotificationLogResponse> getFailedLogs(Pageable pageable) {
        return queryService.getLogsByStatus(NotificationChannel.SMS, DeliveryStatus.FAILED, pageable);
    }

    @GetMapping("/logs/pending")
    public Page<NotificationLogResponse> getPendingLogs(Pageable pageable) {
        return queryService.getLogsByStatus(NotificationChannel.SMS, DeliveryStatus.PENDING, pageable);
    }

    @GetMapping("/events")
    public List<String> getEventTypes() {
        return queryService.getEventTypesByChannel(NotificationChannel.SMS);
    }

    @GetMapping("/events/{eventCode}/logs")
    public Page<NotificationLogResponse> getLogsByEventType(
            @PathVariable String eventCode,
            Pageable pageable) {
        return queryService.getLogsByEventType(NotificationChannel.SMS, eventCode, pageable);
    }

    @GetMapping("/events/{eventCode}/stats")
    public NotificationStatsResponse getStatsByEventType(
            @PathVariable String eventCode) {
        return queryService.getStatsByEventType(NotificationChannel.SMS, eventCode);
    }

    @GetMapping("/stats")
    public NotificationStatsResponse getStats() {
        return queryService.getStats(NotificationChannel.SMS);
    }

    @GetMapping("/search")
    public Page<NotificationLogResponse> searchLogs(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) String eventCode,
            @RequestParam(required = false) String recipient,
            Pageable pageable) {
        return queryService.searchLogs(NotificationChannel.SMS, status, eventCode, recipient, pageable);
    }

    @GetMapping("/delivery-attempts")
    public Page<DeliveryAttemptResponse> getDeliveryAttempts(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) String eventType,
            Pageable pageable) {
        return queryService.getDeliveryAttempts(NotificationChannel.SMS, status, eventType, pageable);
    }

    @GetMapping("/templates")
    public List<NotificationTemplateResponse> getTemplates() {
        return queryService.getTemplatesByChannel(NotificationChannel.SMS);
    }

    @GetMapping("/templates/{eventCode}")
    public List<NotificationTemplateResponse> getTemplatesByEventCode(
            @PathVariable String eventCode) {
        return queryService.getTemplatesByEventCodeAndChannel(eventCode, NotificationChannel.SMS);
    }
}
