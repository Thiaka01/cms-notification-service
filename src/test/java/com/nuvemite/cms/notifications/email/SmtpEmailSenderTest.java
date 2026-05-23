package com.nuvemite.cms.notifications.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.nuvemite.cms.notifications.config.NotificationsMailProperties;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;

class SmtpEmailSenderTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Test
    void sendsMultipartEmail() throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(greenMail.getSmtp().getPort());

        NotificationsMailProperties props =
                new NotificationsMailProperties(true, "noreply@test.local", "CMS", "");
        SmtpEmailSender sender = new SmtpEmailSender(mailSender, props);

        sender.send("user@test.local", "Subject", "<p>HTML</p>", "Plain");

        assertThat(greenMail.getReceivedMessages()).hasSize(1);
        var message = greenMail.getReceivedMessages()[0];
        assertThat(message.getSubject()).isEqualTo("Subject");
        assertThat(message.getContent()).isInstanceOf(MimeMultipart.class);
        assertThat(message.getContentType()).contains("multipart");
    }
}
