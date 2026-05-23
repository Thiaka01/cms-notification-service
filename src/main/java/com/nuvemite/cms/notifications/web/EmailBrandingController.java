package com.nuvemite.cms.notifications.web;

import com.nuvemite.cms.notifications.service.EmailBrandingService;
import com.nuvemite.cms.notifications.web.dto.EmailBrandingResponse;
import com.nuvemite.cms.notifications.web.dto.EmailBrandingUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/branding")
public class EmailBrandingController {

    private final EmailBrandingService brandingService;

    public EmailBrandingController(EmailBrandingService brandingService) {
        this.brandingService = brandingService;
    }

    @GetMapping
    public EmailBrandingResponse getBranding() {
        return brandingService.getActive();
    }

    @PutMapping
    @PreAuthorize("hasRole('REGULATOR')")
    public EmailBrandingResponse updateBranding(@Valid @RequestBody EmailBrandingUpdateRequest request) {
        return brandingService.update(request);
    }
}
