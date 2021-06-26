package com.eventify.api.mail.utils;

import com.eventify.api.mail.templates.MailTemplateType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {
    public SimpleMailMessage getMessageTemplate(MailTemplateType templateKey) {
        String htmlTemplate = "<html><body><h1>Eventify</h1><p>%s</p></body></html>";
        SimpleMailMessage message = new SimpleMailMessage();

        switch (templateKey) {
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
}
