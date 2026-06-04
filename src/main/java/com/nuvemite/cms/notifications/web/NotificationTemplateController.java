package com.nuvemite.cms.notifications.web;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.service.NotificationTemplateService;
import com.nuvemite.cms.notifications.web.dto.NotificationTemplateRequest;
import com.nuvemite.cms.notifications.web.dto.NotificationTemplateResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/templates")
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;

    public NotificationTemplateController(NotificationTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    @PreAuthorize("hasRole('REGULATOR')")
    public List<NotificationTemplateResponse> listTemplates() {
        return templateService.listTemplates();
    }

    @GetMapping("/{eventCode}/{channel}")
    @PreAuthorize("hasRole('REGULATOR')")
    public NotificationTemplateResponse getTemplate(
            @PathVariable String eventCode,
            @PathVariable NotificationChannel channel) {
        return NotificationTemplateResponse.from(templateService.requireTemplate(eventCode, channel));
    }

    @PostMapping
    @PreAuthorize("hasRole('REGULATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationTemplateResponse createTemplate(@Valid @RequestBody NotificationTemplateRequest request) {
        return templateService.createTemplate(request);
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("hasRole('REGULATOR')")
    public NotificationTemplateResponse activate(@PathVariable UUID id) {
        return templateService.setActive(id, true);
    }

    @PutMapping("/{id}/inactive")
    @PreAuthorize("hasRole('REGULATOR')")
    public NotificationTemplateResponse deactivate(@PathVariable UUID id) {
        return templateService.setActive(id, false);
    }
}
