package com.nuvemite.cms.notifications.repository;

import com.nuvemite.cms.notifications.domain.InboxProcessedEvent;
import com.nuvemite.cms.notifications.domain.InboxProcessedEvent.InboxProcessedEventId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxProcessedEventRepository
        extends JpaRepository<InboxProcessedEvent, InboxProcessedEventId> {}
