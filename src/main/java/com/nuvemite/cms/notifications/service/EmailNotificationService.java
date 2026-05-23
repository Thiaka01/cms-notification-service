package com.nuvemite.cms.notifications.service;

import com.nuvemite.cms.notifications.domain.DeliveryAttempt;
import com.nuvemite.cms.notifications.domain.Notification;
import com.nuvemite.cms.notifications.domain.NotificationChannel;
import com.nuvemite.cms.notifications.domain.NotificationTemplate;
import com.nuvemite.cms.notifications.email.EmailLayoutRenderer;
import com.nuvemite.cms.notifications.email.PlainTextEmailRenderer;
import com.nuvemite.cms.notifications.email.SmtpEmailSender;
import com.nuvemite.cms.notifications.email.TemplateRenderer;
import com.nuvemite.cms.notifications.repository.DeliveryAttemptRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailNotificationService {

    private final EmailLayoutRenderer layoutRenderer;
    private final SmtpEmailSender smtpEmailSender;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    public EmailNotificationService(
            EmailLayoutRenderer layoutRenderer,
            SmtpEmailSender smtpEmailSender,
            DeliveryAttemptRepository deliveryAttemptRepository) {
        this.layoutRenderer = layoutRenderer;
        this.smtpEmailSender = smtpEmailSender;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
    }

    @Transactional
    public void send(
            Notification notification,
            NotificationTemplate template,
            String recipientEmail,
            Map<String, String> variables) {
        DeliveryAttempt attempt = DeliveryAttempt.pending(notification, NotificationChannel.EMAIL);
        deliveryAttemptRepository.save(attempt);

        try {
            String bodyFragment = TemplateRenderer.render(template.getBodyTemplate(), variables);
            String subject = template.getSubject() != null
                    ? TemplateRenderer.render(template.getSubject(), variables)
                    : variables.getOrDefault("subjectLine", "Notification");
            String html = layoutRenderer.render(subject, bodyFragment);
            String plain = PlainTextEmailRenderer.toPlainText(bodyFragment);
            String messageId = smtpEmailSender.send(recipientEmail, subject, html, plain);
            attempt.markSent(messageId);
        } catch (Exception ex) {
            attempt.markFailed(ex.getMessage());
        }
        deliveryAttemptRepository.save(attempt);
    }
}
