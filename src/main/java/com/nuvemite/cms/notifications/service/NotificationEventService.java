package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationEvent;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import com.nuvemite.cms.notifications.exception.NotificationEventAlreadyExistsException;
import com.nuvemite.cms.notifications.exception.NotificationEventNotFoundException;
import com.nuvemite.cms.notifications.messaging.EventTypes;
import com.nuvemite.cms.notifications.repository.NotificationEventRepository;
import com.nuvemite.cms.notifications.repository.NotificationTemplateRepository;
import com.nuvemite.cms.notifications.web.dto.NotificationEventRequest;
import com.nuvemite.cms.notifications.web.dto.NotificationEventResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationEventTemplateRequest;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationEventService {

    private final NotificationEventRepository eventRepository;
    private final NotificationTemplateRepository templateRepository;

    public NotificationEventService(
            NotificationEventRepository eventRepository,
            NotificationTemplateRepository templateRepository) {
        this.eventRepository = eventRepository;
        this.templateRepository = templateRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationEventResponse> listEvents() {
        return eventRepository.findAll().stream()
                .map(event -> NotificationEventResponse.from(
                        event,
                        templateRepository.findByEventEventCode(event.getEventCode())))
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationEvent requireEvent(String eventCode) {
        return eventRepository
                .findByEventCode(normalize(eventCode))
                .orElseThrow(() -> new NotificationEventNotFoundException(eventCode));
    }

    @Transactional(readOnly = true)
    public NotificationEventResponse getEvent(String eventCode) {
        NotificationEvent event = requireEvent(eventCode);
        return NotificationEventResponse.from(
                event,
                templateRepository.findByEventEventCode(event.getEventCode()));
    }

    @Transactional(readOnly = true)
    public Optional<NotificationEvent> findActiveEvent(String eventCode) {
        return eventRepository.findByEventCodeAndActiveTrue(normalize(eventCode));
    }

    @Transactional
    public NotificationEventResponse createEvent(NotificationEventRequest request) {
        String eventCode = normalize(request.eventCode());
        if (eventRepository.existsByEventCode(eventCode)) {
            throw new NotificationEventAlreadyExistsException(eventCode);
        }

        Set<NotificationChannel> channels = new LinkedHashSet<>(request.supportedChannels());
        NotificationEvent event = NotificationEvent.create(
                eventCode,
                request.name().trim(),
                request.description(),
                channels,
                request.active() == null || request.active());
        NotificationEvent savedEvent = eventRepository.save(event);

        List<NotificationTemplate> templates = createTemplates(savedEvent, request.templates());
        return NotificationEventResponse.from(savedEvent, templates);
    }

    @Transactional
    public void synchronizeSystemDefinitions() {
        for (EventTypes.NotificationEventDefinition definition : EventTypes.systemDefinitions()) {
            NotificationEvent event = eventRepository
                    .findByEventCode(definition.eventCode())
                    .orElseGet(() -> NotificationEvent.create(
                            definition.eventCode(),
                            definition.name(),
                            definition.description(),
                            definition.supportedChannels(),
                            true));
            event.synchronizeDefinition(
                    definition.name(),
                    definition.description(),
                    definition.supportedChannels());
            NotificationEvent savedEvent = eventRepository.save(event);

            for (EventTypes.TemplateDefinition templateDefinition : definition.templates()) {
                if (!templateRepository.existsByEventEventCodeAndChannel(
                        savedEvent.getEventCode(), templateDefinition.channel())) {
                    templateRepository.save(NotificationTemplate.create(
                            savedEvent,
                            templateDefinition.channel(),
                            templateDefinition.subject(),
                            templateDefinition.templateContent(),
                            true));
                }
            }
        }
    }

    private List<NotificationTemplate> createTemplates(
            NotificationEvent event,
            List<NotificationEventTemplateRequest> templateRequests) {
        if (templateRequests == null || templateRequests.isEmpty()) {
            return List.of();
        }
        return templateRequests.stream()
                .map(templateRequest -> createTemplate(event, templateRequest))
                .toList();
    }

    private NotificationTemplate createTemplate(
            NotificationEvent event,
            NotificationEventTemplateRequest request) {
        if (!event.getSupportedChannels().contains(request.channel())) {
            throw new IllegalArgumentException(
                    "Template channel " + request.channel() + " is not supported by event " + event.getEventCode());
        }
        return templateRepository.save(NotificationTemplate.create(
                event,
                request.channel(),
                request.subject(),
                request.templateContent(),
                request.active() == null || request.active()));
    }

    private String normalize(String eventCode) {
        if (eventCode == null || eventCode.isBlank()) {
            throw new IllegalArgumentException("Event code is required");
        }
        return eventCode.trim();
    }
}
