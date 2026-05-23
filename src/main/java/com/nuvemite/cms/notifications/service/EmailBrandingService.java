package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.EmailBranding;
import com.nuvemite.cms.notifications.exception.ResourceNotFoundException;
import com.nuvemite.cms.notifications.repository.EmailBrandingRepository;
import com.nuvemite.cms.notifications.security.SecurityUtils;
import com.nuvemite.cms.notifications.web.dto.EmailBrandingResponse;
import com.nuvemite.cms.notifications.web.dto.EmailBrandingUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailBrandingService {

    private final EmailBrandingRepository brandingRepository;

    public EmailBrandingService(EmailBrandingRepository brandingRepository) {
        this.brandingRepository = brandingRepository;
    }

    public EmailBrandingResponse getActive() {
        return brandingRepository
                .findByActiveTrue()
                .map(EmailBrandingResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Active email branding not configured"));
    }

    @Transactional
    public EmailBrandingResponse update(EmailBrandingUpdateRequest request) {
        brandingRepository.deactivateAllActive();
        EmailBranding branding = EmailBranding.create(
                request.logoUrl(),
                request.headerTitle(),
                request.headerSubtitle(),
                request.headerHtml(),
                request.footerHtml(),
                SecurityUtils.currentSubject());
        brandingRepository.save(branding);
        return EmailBrandingResponse.from(branding);
    }
}
