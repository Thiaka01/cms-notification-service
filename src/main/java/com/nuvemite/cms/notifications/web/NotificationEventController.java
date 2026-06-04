package com.nuvemite.cms.notifications.web;

import com.nuvemite.cms.notifications.service.NotificationEventService;
import com.nuvemite.cms.notifications.web.dto.NotificationEventRequest;
import com.nuvemite.cms.notifications.web.dto.NotificationEventResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-events")
public class NotificationEventController {

    private final NotificationEventService eventService;

    public NotificationEventController(NotificationEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @PreAuthorize("hasRole('REGULATOR')")
    public List<NotificationEventResponse> listEvents() {
        return eventService.listEvents();
    }

    @GetMapping("/{eventCode}")
    @PreAuthorize("hasRole('REGULATOR')")
    public NotificationEventResponse getEvent(@PathVariable String eventCode) {
        return eventService.getEvent(eventCode);
    }

    @PostMapping
    @PreAuthorize("hasRole('REGULATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationEventResponse createEvent(@Valid @RequestBody NotificationEventRequest request) {
        return eventService.createEvent(request);
    }
}
