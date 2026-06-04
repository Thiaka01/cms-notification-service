package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationEvent;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import com.nuvemite.cms.notifications.exception.ConflictException;
import com.nuvemite.cms.notifications.exception.NotificationEventNotFoundException;
import com.nuvemite.cms.notifications.exception.NotificationTemplateNotFoundException;
import com.nuvemite.cms.notifications.exception.ResourceNotFoundException;
import com.nuvemite.cms.notifications.repository.NotificationEventRepository;
import com.nuvemite.cms.notifications.repository.NotificationTemplateRepository;
import com.nuvemite.cms.notifications.web.dto.NotificationTemplateRequest;
import com.nuvemite.cms.notifications.web.dto.NotificationTemplateResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationEventRepository eventRepository;

    public NotificationTemplateService(
            NotificationTemplateRepository templateRepository,
            NotificationEventRepository eventRepository) {
        this.templateRepository = templateRepository;
        this.eventRepository = eventRepository;
    }


    public NotificationTemplate requireTemplate(String eventCode, NotificationChannel channel) {
        return templateRepository
                .findByEventEventCodeAndChannelAndActiveTrue(eventCode, channel)
                .or(() -> templateRepository.findByEventTypeAndChannelAndActiveTrue(eventCode, channel))
                .orElseThrow(() -> new NotificationTemplateNotFoundException(eventCode, channel));
    }

    public Optional<NotificationTemplate> findActiveTemplate(String eventCode, NotificationChannel channel) {
        return templateRepository
                .findByEventEventCodeAndChannelAndActiveTrue(eventCode, channel)
                .or(() -> templateRepository.findByEventTypeAndChannelAndActiveTrue(eventCode, channel));
    }

    public List<NotificationTemplateResponse> listTemplates() {
        return templateRepository.findAll().stream()
                .map(NotificationTemplateResponse::from)
                .toList();
    }

    @Transactional
    public NotificationTemplateResponse createTemplate(NotificationTemplateRequest request) {
        NotificationEvent event = eventRepository
                .findByEventCode(request.eventCode())
                .orElseThrow(() -> new NotificationEventNotFoundException(request.eventCode()));
        if (!event.getSupportedChannels().contains(request.channel())) {
            throw new IllegalArgumentException(
                    "Template channel " + request.channel() + " is not supported by event " + request.eventCode());
        }

        templateRepository.findByEventEventCodeAndChannel(request.eventCode(), request.channel())
                .ifPresent(existing -> {
                    throw new ConflictException("Template already exists for event "
                            + request.eventCode() + " channel " + request.channel());
                });

        NotificationTemplate template = NotificationTemplate.create(
                event,
                request.channel(),
                request.subject(),
                request.templateContent(),
                request.active() == null || request.active());
        return NotificationTemplateResponse.from(templateRepository.save(template));
    }

    @Transactional
    public NotificationTemplateResponse setActive(UUID id, boolean active) {
        NotificationTemplate template = templateRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found: " + id));
        template.setActive(active);
        return NotificationTemplateResponse.from(templateRepository.save(template));
    }
}
