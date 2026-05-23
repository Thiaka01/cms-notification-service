package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationRecipient;
import com.nuvemite.cms.notifications.exception.ResourceNotFoundException;
import com.nuvemite.cms.notifications.repository.NotificationRecipientRepository;
import com.nuvemite.cms.notifications.security.SecurityUtils;
import com.nuvemite.cms.notifications.web.dto.InboxItemResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboxService {

    private final NotificationRecipientRepository recipientRepository;

    public InboxService(NotificationRecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    public Page<InboxItemResponse> getInbox(Pageable pageable) {
        String sub = SecurityUtils.currentSubject();
        return recipientRepository.findInboxByKeycloakSub(sub, pageable).map(InboxItemResponse::from);
    }

    @Transactional
    public void markRead(UUID recipientId) {
        String sub = SecurityUtils.currentSubject();
        NotificationRecipient recipient = recipientRepository
                .findByIdAndKeycloakSub(recipientId, sub)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        recipient.markRead();
        recipientRepository.save(recipient);
    }
}
