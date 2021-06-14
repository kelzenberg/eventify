package com.eventify.api.mail.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailUtil {

    private static final String senderEmail = "noreply@eventify.com";
    private static final String htmlTemplate = "<html><body>%s</body></html>";

    public SimpleMailMessage getMessageTemplate(String templateKey) {
        SimpleMailMessage message = new SimpleMailMessage();

        switch (templateKey) {
            case "INVITE":
                message.setSubject("Invited to Eventify");
                message.setText(String.format(htmlTemplate, "Invite Email: %s\nEventId: %s"));
                break;
            case "REGISTER":
                message.setSubject("Registered to Eventify");
                message.setText(String.format(htmlTemplate, "Register: %s"));
            default:
                message.setSubject("%s");
                message.setText(String.format(htmlTemplate, "%s"));
                break;
        }

        return message;
    }

    public MimeMessageHelper getMessageHelper(MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(senderEmail);
        return helper;
    }
}
