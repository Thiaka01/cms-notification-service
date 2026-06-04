package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.DeliveryStatus;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.repository.DeliveryAttemptRepository;
import com.nuvemite.cms.notifications.repository.NotificationLogRepository;
import com.nuvemite.cms.notifications.repository.NotificationTemplateRepository;
import com.nuvemite.cms.notifications.web.dto.NotificationTemplateResponse;
import com.nuvemite.cms.notifications.web.dto.DeliveryAttemptResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationLogResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationStatsResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for querying notifications by channel (SMS, Email).
 * Provides filtering by event type, delivery status, and recipient.
 */
@Service
public class ChannelNotificationQueryService {

    private final NotificationLogRepository logRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final NotificationTemplateRepository templateRepository;

    public ChannelNotificationQueryService(
            NotificationLogRepository logRepository,
            DeliveryAttemptRepository deliveryAttemptRepository,
            NotificationTemplateRepository templateRepository) {
        this.logRepository = logRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.templateRepository = templateRepository;
    }

    // ── Notification Log Queries ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> getLogsByChannel(NotificationChannel channel, Pageable pageable) {
        return logRepository.findByChannel(channel, pageable).map(NotificationLogResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> getLogsByStatus(NotificationChannel channel, DeliveryStatus status, Pageable pageable) {
        return logRepository.findByChannelAndStatus(channel, status, pageable).map(NotificationLogResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> getLogsByEventType(NotificationChannel channel, String eventCode, Pageable pageable) {
        return logRepository.findByChannelAndEventCode(channel, eventCode, pageable).map(NotificationLogResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> getLogsByEventTypeAndStatus(
            NotificationChannel channel, String eventCode, DeliveryStatus status, Pageable pageable) {
        return logRepository.findByChannelAndEventCodeAndStatus(channel, eventCode, status, pageable)
                .map(NotificationLogResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> searchLogs(
            NotificationChannel channel,
            DeliveryStatus status,
            String eventCode,
            String recipient,
            Pageable pageable) {
        return logRepository.findByCriteria(channel, status, eventCode, recipient, pageable)
                .map(NotificationLogResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> getLogsByRecipient(
            NotificationChannel channel, String recipient, Pageable pageable) {
        return logRepository.findByChannelAndRecipientContainingIgnoreCase(channel, recipient, pageable)
                .map(NotificationLogResponse::from);
    }

    // ── Statistics ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public NotificationStatsResponse getStats(NotificationChannel channel) {
        long total = logRepository.countByChannel(channel);
        long sent = logRepository.countByChannelAndStatus(channel, DeliveryStatus.SENT);
        long failed = logRepository.countByChannelAndStatus(channel, DeliveryStatus.FAILED);
        long pending = logRepository.countByChannelAndStatus(channel, DeliveryStatus.PENDING);
        return NotificationStatsResponse.of(channel, total, sent, failed, pending);
    }

    @Transactional(readOnly = true)
    public NotificationStatsResponse getStatsByEventType(NotificationChannel channel, String eventCode) {
        long total = logRepository.countByEventCodeAndChannel(eventCode, channel);
        long sent = logRepository.countByEventCodeAndChannelAndStatus(eventCode, channel, DeliveryStatus.SENT);
        long failed = logRepository.countByEventCodeAndChannelAndStatus(eventCode, channel, DeliveryStatus.FAILED);
        long pending = logRepository.countByEventCodeAndChannelAndStatus(eventCode, channel, DeliveryStatus.PENDING);
        return NotificationStatsResponse.of(channel, total, sent, failed, pending);
    }

    // ── Event Types ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<String> getEventTypesByChannel(NotificationChannel channel) {
        return logRepository.findDistinctEventCodesByChannel(channel);
    }

    // ── Delivery Attempts ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<DeliveryAttemptResponse> getDeliveryAttempts(
            NotificationChannel channel, DeliveryStatus status, String eventType, Pageable pageable) {
        return deliveryAttemptRepository.findByCriteria(channel, status, eventType, pageable)
                .map(DeliveryAttemptResponse::from);
    }

    // ── Templates by Channel ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getTemplatesByChannel(NotificationChannel channel) {
        return templateRepository.findAll().stream()
                .filter(t -> t.getChannel() == channel)
                .map(NotificationTemplateResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getTemplatesByEventCodeAndChannel(
            String eventCode, NotificationChannel channel) {
        return templateRepository.findByEventEventCode(eventCode).stream()
                .filter(t -> t.getChannel() == channel)
                .map(NotificationTemplateResponse::from)
                .toList();
    }
}
