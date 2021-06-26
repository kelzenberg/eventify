package com.eventify.api.mail.templates;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public interface MailTemplate {
    MailTemplateType getTemplateType();
    MimeMessage getMessage() throws MessagingException;
}
