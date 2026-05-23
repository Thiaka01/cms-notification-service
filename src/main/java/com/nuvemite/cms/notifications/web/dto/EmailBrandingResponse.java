package com.nuvemite.cms.notifications.web.dto;

import com.nuvemite.cms.notifications.domain.EmailBranding;
import java.time.Instant;
import java.util.UUID;

public record EmailBrandingResponse(
        UUID id,
        String logoUrl,
        String headerTitle,
        String headerSubtitle,
        String headerHtml,
        String footerHtml,
        Instant updatedAt,
        String updatedBy) {

    public static EmailBrandingResponse from(EmailBranding branding) {
        return new EmailBrandingResponse(
                branding.getId(),
                branding.getLogoUrl(),
                branding.getHeaderTitle(),
                branding.getHeaderSubtitle(),
                branding.getHeaderHtml(),
                branding.getFooterHtml(),
                branding.getUpdatedAt(),
                branding.getUpdatedBy());
    }
}
