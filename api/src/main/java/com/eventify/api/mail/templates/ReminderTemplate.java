package com.eventify.api.mail.templates;

import com.eventify.api.mail.constants.BaseMailTemplate;
import com.eventify.api.mail.constants.MailTemplateType;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;

public class ReminderTemplate implements MailTemplate {
    private final MailTemplateType type = MailTemplateType.DELETE;
    private final String subject = String.format("Please verify your %s Account", BaseMailTemplate.PUBLIC_NAME_STATIC);

    private final MimeMessage message;
    private final String toAddress;
//    private final Date createdAt;
    private final String verificationHash;

    public ReminderTemplate(MimeMessage message, HashMap<String, String> data) {
        this.message = message;
        this.toAddress = data.get("toAddress");
//        this.createdAt = new Date(data.get("createdAt")); // TODO: improve Date parsing
        this.verificationHash = data.get("verificationHash");
    }

    private String getTemplate() {
        String verifyURL = BaseMailTemplate.getBaseURL() + "/register?verify=" + this.verificationHash;
        String template = "Please verify your account by clicking " +
                "<a href=\"" + verifyURL + "\" alt=\"Verify your email address for " +
                BaseMailTemplate.PUBLIC_NAME_STATIC +
                "\">this link</a> " +
                "or copying and opening the following URL into your Browser:<br><br>" +
                "<i>" + verifyURL + "</i><br><br>" +
                "The link is valid until " +
//                BaseMailTemplate.DATE_FORMAT.format(new Date(
//                        this.createdAt.getTime() +
//                                (long) ApplicationSecurityConfig.ACCOUNT_VERIFICATION_TIME_HRS * (60 - 1) * 60 * 1000) // = Hours to Milliseconds, minus 1 minute
//                ) +
                ".<br>" +
                "Your account will be <b>permanently deleted</b> afterwards.";

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
