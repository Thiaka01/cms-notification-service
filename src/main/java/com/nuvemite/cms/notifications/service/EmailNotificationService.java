package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.email.EmailLayoutRenderer;
import com.nuvemite.cms.notifications.email.PlainTextEmailRenderer;
import com.nuvemite.cms.notifications.email.SmtpEmailSender;
import com.nuvemite.cms.notifications.exception.NotificationDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final EmailLayoutRenderer layoutRenderer;
    private final SmtpEmailSender smtpEmailSender;

    public EmailNotificationService(
            EmailLayoutRenderer layoutRenderer,
            SmtpEmailSender smtpEmailSender) {
        this.layoutRenderer = layoutRenderer;
        this.smtpEmailSender = smtpEmailSender;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public NotificationDeliveryResult send(NotificationMessage message) {
        if (message.recipient() == null || message.recipient().isBlank()) {
            throw new NotificationDeliveryException("Email recipient is required");
        }
        try {
            log.debug(
                    "Sending email notification for event {} id {} to {} using {} variables",
                    message.eventCode(),
                    message.eventId(),
                    message.recipient(),
                    message.templateData().size());
            String html = layoutRenderer.render(message.subject(), message.content());
            String plain = PlainTextEmailRenderer.toPlainText(message.content());
            String messageId = smtpEmailSender.send(message.recipient(), message.subject(), html, plain);
            return NotificationDeliveryResult.sent(messageId);
        } catch (Exception ex) {
            throw new NotificationDeliveryException("Failed to send email notification", ex);
        }
    }
}
