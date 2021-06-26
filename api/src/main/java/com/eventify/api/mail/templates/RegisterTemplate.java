package com.eventify.api.mail.templates;

import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class RegisterTemplate implements MailTemplate {
    private final MailTemplateType type = MailTemplateType.REGISTER;
    private final String subject = "Welcome to Eventify";

    private final MimeMessage message;
    private final String toAddress;
    private final Date createdAt;
    private final String verificationHash;

    public RegisterTemplate(MimeMessage message, String toAddress, Date createdAt, String verificationHash) {
        this.message = message;
        this.toAddress = toAddress;
        this.createdAt = createdAt;
        this.verificationHash = verificationHash;
    }

    private String getTemplate() {
        String verifyURL = BaseMailTemplate.getBaseURL() + "/register?verify=" + this.verificationHash;
        String template = "Your new account " +
                "<i>" + this.toAddress + "</i> " +
                "on Eventify is almost ready.<br>" +
                "Please verify your account by clicking " +
                "<a href=\"" + verifyURL + "\" alt=\"Verify your email address for Eventify\">this link</a> " +
                "or copying and opening the following URL into your Browser:<br><br>" +
                "<i>" + verifyURL + "</i><br><br>" +
                "The link is valid for 48 hours (until " +
                BaseMailTemplate.DATE_FORMAT.format(new Date(this.createdAt.getTime() + 48 * (60 - 1) * 60 * 1000)) + // = 2 days minus 1 minute
                ").<br>" +
                "After this period your account will be <b>permanently deleted</b>.<br><br>" +
                "Welcome to Eventify!";

        String baseTemplate = BaseMailTemplate.getBaseTemplate();
        return String.format(baseTemplate, template);
    }

    @Override
    public MailTemplateType getTemplateType() {
        return this.type;
    }

    @Override
    public MimeMessage getMessage() throws MessagingException {
        MimeMessage message = this.message;
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(BaseMailTemplate.SENDER_MAIL_ADDRESS);
        helper.setSubject(this.subject);
        helper.setText(this.getTemplate(), true);
        helper.setTo(this.toAddress);
        return message;
    }
}