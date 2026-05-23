package com.nuvemite.cms.notifications.email;

import com.nuvemite.cms.notifications.domain.EmailBranding;
import com.nuvemite.cms.notifications.exception.ResourceNotFoundException;
import com.nuvemite.cms.notifications.repository.EmailBrandingRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailLayoutRenderer {

    private final TemplateEngine templateEngine;
    private final EmailBrandingRepository brandingRepository;

    public EmailLayoutRenderer(TemplateEngine templateEngine, EmailBrandingRepository brandingRepository) {
        this.templateEngine = templateEngine;
        this.brandingRepository = brandingRepository;
    }

    public String render(String subject, String bodyHtml) {
        EmailBranding branding = brandingRepository
                .findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Active email branding not configured"));

        Context context = new Context();
        context.setVariable("emailSubject", subject);
        context.setVariable("logoUrl", branding.getLogoUrl());
        context.setVariable("headerTitle", branding.getHeaderTitle());
        context.setVariable("headerSubtitle", branding.getHeaderSubtitle());
        context.setVariable("headerHtml", branding.getHeaderHtml());
        context.setVariable("footerHtml", branding.getFooterHtml());
        context.setVariable("bodyHtml", bodyHtml);

        return templateEngine.process("email/layout", context);
    }

    public Map<String, String> brandingVariables() {
        EmailBranding branding = brandingRepository
                .findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Active email branding not configured"));
        Map<String, String> vars = new HashMap<>();
        vars.put("headerTitle", branding.getHeaderTitle());
        vars.put("headerSubtitle", branding.getHeaderSubtitle() != null ? branding.getHeaderSubtitle() : "");
        vars.put("footerHtml", branding.getFooterHtml());
        return vars;
    }
}
