package com.nuvemite.cms.notifications.web;

import com.nuvemite.cms.notifications.service.InboxService;
import com.nuvemite.cms.notifications.service.NotificationPreferenceService;
import com.nuvemite.cms.notifications.web.dto.InboxItemResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationPreferenceResponse;
import com.nuvemite.cms.notifications.web.dto.UpdatePreferencesRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final InboxService inboxService;
    private final NotificationPreferenceService preferenceService;

    public NotificationController(InboxService inboxService, NotificationPreferenceService preferenceService) {
        this.inboxService = inboxService;
        this.preferenceService = preferenceService;
    }

    @GetMapping("/inbox")
    public Page<InboxItemResponse> inbox(Pageable pageable) {
        return inboxService.getInbox(pageable);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable UUID id) {
        inboxService.markRead(id);
    }

    @GetMapping("/preferences")
    public List<NotificationPreferenceResponse> getPreferences() {
        return preferenceService.getPreferences();
    }

    @PutMapping("/preferences")
    public List<NotificationPreferenceResponse> updatePreferences(
            @Valid @RequestBody UpdatePreferencesRequest request) {
        return preferenceService.updatePreferences(request.preferences());
    }
}
