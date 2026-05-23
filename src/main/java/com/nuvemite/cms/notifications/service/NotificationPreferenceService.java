package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationPreference;
import com.nuvemite.cms.notifications.repository.NotificationPreferenceRepository;
import com.nuvemite.cms.notifications.security.SecurityUtils;
import com.nuvemite.cms.notifications.web.dto.NotificationPreferenceRequest;
import com.nuvemite.cms.notifications.web.dto.NotificationPreferenceResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationPreferenceService(NotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public List<NotificationPreferenceResponse> getPreferences() {
        String sub = SecurityUtils.currentSubject();
        return preferenceRepository.findByKeycloakSub(sub).stream()
                .map(NotificationPreferenceResponse::from)
                .toList();
    }

    @Transactional
    public List<NotificationPreferenceResponse> updatePreferences(List<NotificationPreferenceRequest> requests) {
        String sub = SecurityUtils.currentSubject();
        for (NotificationPreferenceRequest request : requests) {
            NotificationPreference preference = preferenceRepository
                    .findByKeycloakSubAndChannelAndEventType(sub, request.channel(), request.eventType())
                    .orElseGet(() -> NotificationPreference.create(
                            sub, request.channel(), request.eventType(), request.enabled()));
            preference.setEnabled(request.enabled());
            preferenceRepository.save(preference);
        }
        return getPreferences();
    }

    public boolean isChannelEnabled(String keycloakSub, String eventType, NotificationChannel channel) {
        return preferenceRepository
                .findByKeycloakSubAndChannelAndEventType(keycloakSub, channel, eventType)
                .map(NotificationPreference::isEnabled)
                .orElse(true);
    }
}
