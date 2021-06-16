package com.eventify.api.mail.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailUtil {

    private static final String senderEmail = "noreply@eventify.com";
    private static final String htmlTemplate = "<html><body><h1>Eventify</h1><p>%s</p></body></html>";

    public SimpleMailMessage getMessageTemplate(MailTemplate templateKey) {
        SimpleMailMessage message = new SimpleMailMessage();

        switch (templateKey) {
            case REGISTER:
                message.setSubject("Welcome to Eventify");
                message.setText(String.format(htmlTemplate, "You (%s) registered on Eventify."));
                break;
            case INVITE:
                message.setSubject("You were invited to Eventify");
                message.setText(String.format(htmlTemplate,
                        "You (%s) were invited to join the <b><i>%s</i></b> Event.<br><br>"
                                + "<a href=\"http://localhost:8081/register\" alt=\"Register on Eventify\">Create a new account!</a>"));
                break;
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
