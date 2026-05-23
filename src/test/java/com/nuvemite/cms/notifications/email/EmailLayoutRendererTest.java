package com.nuvemite.cms.notifications.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.nuvemite.cms.notifications.domain.EmailBranding;
import com.nuvemite.cms.notifications.repository.EmailBrandingRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@ExtendWith(MockitoExtension.class)
class EmailLayoutRendererTest {

    @Mock
    private EmailBrandingRepository brandingRepository;

    private EmailLayoutRenderer layoutRenderer;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        layoutRenderer = new EmailLayoutRenderer(engine, brandingRepository);

        EmailBranding branding = EmailBranding.create(
                "https://example.com/logo.png",
                "Chemical Management System",
                "Regulatory notifications",
                null,
                "<p>Footer</p>",
                "test");
        when(brandingRepository.findByActiveTrue()).thenReturn(Optional.of(branding));
    }

    @Test
    void rendersTableLayoutWithBranding() {
        String html = layoutRenderer.render("Test subject", "<p>Hello world</p>");
        assertThat(html).contains("Chemical Management System");
        assertThat(html).contains("<table");
        assertThat(html).contains("Hello world");
        assertThat(html).contains("#0d4f4f");
        assertThat(html).doesNotContain("<link ");
    }
}
