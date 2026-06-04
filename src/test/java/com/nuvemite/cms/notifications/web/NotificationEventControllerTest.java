package com.nuvemite.cms.notifications.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.exception.NotificationEventAlreadyExistsException;
import com.nuvemite.cms.notifications.security.CmsJwtAuthenticationConverter;
import com.nuvemite.cms.notifications.service.NotificationEventService;
import com.nuvemite.cms.notifications.support.TestJwt;
import com.nuvemite.cms.notifications.web.dto.NotificationEventResponse;
import com.nuvemite.cms.notifications.web.dto.NotificationEventTemplateResponse;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationEventController.class)
@Import({CmsJwtAuthenticationConverter.class, GlobalExceptionHandler.class})
@EnableMethodSecurity
@EnableWebSecurity
class NotificationEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationEventService eventService;

    @Test
    void regulatorCanCreateNotificationEvent() throws Exception {
        when(eventService.createEvent(any()))
                .thenReturn(response("PAYROLL_APPROVED"));

        mockMvc.perform(post("/api/v1/notification-events")
                        .with(jwt().jwt(TestJwt.regulator())
                                .authorities(new SimpleGrantedAuthority("ROLE_REGULATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "eventCode": "PAYROLL_APPROVED",
                                  "name": "Payroll approved",
                                  "description": "Payroll approval notification",
                                  "supportedChannels": ["EMAIL", "SMS"],
                                  "templates": [
                                    {
                                      "channel": "EMAIL",
                                      "subject": "Payroll approved",
                                      "templateContent": "Hello {{employeeName}}"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventCode").value("PAYROLL_APPROVED"))
                .andExpect(jsonPath("$.templates[0].channel").value("EMAIL"));
    }

    @Test
    void duplicateEventReturnsConflict() throws Exception {
        when(eventService.createEvent(any()))
                .thenThrow(new NotificationEventAlreadyExistsException("PAYROLL_APPROVED"));

        mockMvc.perform(post("/api/v1/notification-events")
                        .with(jwt().jwt(TestJwt.regulator())
                                .authorities(new SimpleGrantedAuthority("ROLE_REGULATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "eventCode": "PAYROLL_APPROVED",
                                  "name": "Payroll approved",
                                  "supportedChannels": ["EMAIL"]
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void regulatorCanReadNotificationEvent() throws Exception {
        when(eventService.getEvent("PAYROLL_APPROVED"))
                .thenReturn(response("PAYROLL_APPROVED"));

        mockMvc.perform(get("/api/v1/notification-events/PAYROLL_APPROVED")
                        .with(jwt().jwt(TestJwt.regulator())
                                .authorities(new SimpleGrantedAuthority("ROLE_REGULATOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventCode").value("PAYROLL_APPROVED"));
    }

    private NotificationEventResponse response(String eventCode) {
        Instant now = Instant.now();
        return new NotificationEventResponse(
                UUID.randomUUID(),
                eventCode,
                "Payroll approved",
                "Payroll approval notification",
                true,
                Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS),
                List.of(new NotificationEventTemplateResponse(
                        UUID.randomUUID(),
                        NotificationChannel.EMAIL,
                        "Payroll approved",
                        "Hello {{employeeName}}",
                        true,
                        now,
                        now)),
                now,
                now);
    }
}
