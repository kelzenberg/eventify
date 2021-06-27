package com.eventify.api.mail.templates;

import com.eventify.api.mail.constants.MailTemplateType;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public interface MailTemplate {
    MailTemplateType getTemplateType();
    MimeMessage getMessage() throws MessagingException;
}
