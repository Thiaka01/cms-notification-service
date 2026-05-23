package com.nuvemite.cms.notifications.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nuvemite.cms.notifications.service.EmailBrandingService;
import com.nuvemite.cms.notifications.support.TestJwt;
import com.nuvemite.cms.notifications.web.dto.EmailBrandingResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.nuvemite.cms.notifications.security.CmsJwtAuthenticationConverter;

@WebMvcTest(EmailBrandingController.class)
@Import({CmsJwtAuthenticationConverter.class, GlobalExceptionHandler.class})
@EnableMethodSecurity
@EnableWebSecurity
class EmailBrandingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailBrandingService brandingService;

    @Test
    void regulatorCanUpdateBranding() throws Exception {
        when(brandingService.update(any()))
                .thenReturn(new EmailBrandingResponse(
                        UUID.randomUUID(),
                        "https://example.com/logo.png",
                        "CMS Updated",
                        "Alerts",
                        null,
                        "<p>Footer</p>",
                        Instant.now(),
                        "regulator-sub"));

        mockMvc.perform(put("/api/v1/notifications/branding")
                        .with(jwt().jwt(TestJwt.regulator())
                                .authorities(new SimpleGrantedAuthority("ROLE_REGULATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "logoUrl": "https://example.com/logo.png",
                                  "headerTitle": "CMS Updated",
                                  "headerSubtitle": "Alerts",
                                  "footerHtml": "<p>Footer</p>"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerTitle").value("CMS Updated"));
    }

    @Test
    void companyUserCannotUpdateBranding() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/branding")
                        .with(jwt().jwt(TestJwt.companyUser())
                                .authorities(new SimpleGrantedAuthority("ROLE_COMPANY_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "headerTitle": "CMS",
                                  "footerHtml": "<p>Footer</p>"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanReadBranding() throws Exception {
        when(brandingService.getActive())
                .thenReturn(new EmailBrandingResponse(
                        UUID.randomUUID(),
                        null,
                        "Chemical Management System",
                        null,
                        null,
                        "<p>Footer</p>",
                        Instant.now(),
                        "system"));

        mockMvc.perform(get("/api/v1/notifications/branding")
                        .with(jwt().jwt(TestJwt.companyUser())
                                .authorities(new SimpleGrantedAuthority("ROLE_COMPANY_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerTitle").value("Chemical Management System"));
    }
}
