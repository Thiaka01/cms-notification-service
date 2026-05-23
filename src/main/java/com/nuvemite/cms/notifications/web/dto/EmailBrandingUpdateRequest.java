package com.nuvemite.cms.notifications.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmailBrandingUpdateRequest(
        @Size(max = 512)
                @Pattern(
                        regexp = "^(|https://.+)",
                        message = "logoUrl must be empty or an absolute HTTPS URL")
                String logoUrl,
        @NotBlank @Size(max = 255) String headerTitle,
        @Size(max = 512) String headerSubtitle,
        String headerHtml,
        @NotBlank String footerHtml) {}
