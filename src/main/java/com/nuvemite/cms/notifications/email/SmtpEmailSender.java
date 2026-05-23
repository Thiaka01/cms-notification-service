package com.nuvemite.cms.notifications.email;

import com.nuvemite.cms.notifications.config.NotificationsMailProperties;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final JavaMailSender mailSender;
    private final NotificationsMailProperties mailProperties;

    public SmtpEmailSender(JavaMailSender mailSender, NotificationsMailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    public String send(String to, String subject, String htmlBody, String plainBody) {
        if (!mailProperties.enabled()) {
            log.info("Mail disabled — would send to {} subject: {}", to, subject);
            return "logged-stub";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String from = mailProperties.fromName() != null && !mailProperties.fromName().isBlank()
                    ? "%s <%s>".formatted(mailProperties.fromName(), mailProperties.from())
                    : mailProperties.from();
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainBody, htmlBody);
            if (mailProperties.replyTo() != null && !mailProperties.replyTo().isBlank()) {
                helper.setReplyTo(mailProperties.replyTo());
            }
            mailSender.send(message);
            return message.getMessageID();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send email: " + ex.getMessage(), ex);
        }
    }
}
