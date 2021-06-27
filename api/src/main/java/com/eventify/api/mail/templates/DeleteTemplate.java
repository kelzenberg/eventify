package com.eventify.api.mail.templates;

import com.eventify.api.auth.ApplicationSecurityConfig;
import com.eventify.api.mail.constants.BaseMailTemplate;
import com.eventify.api.mail.constants.MailTemplateType;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class DeleteTemplate implements MailTemplate {
    private final MailTemplateType type = MailTemplateType.DELETE;
    private final String subject = String.format("Your %s Account was deleted", BaseMailTemplate.PUBLIC_NAME_STATIC);

    private final MimeMessage message;
    private final String[] toAddresses;

    public DeleteTemplate(MimeMessage message, String[] toAddresses) {
        this.message = message;
        this.toAddresses = toAddresses;
    }

    private String getTemplate() {
        String template = "Your account was deleted due to not being verified in the last " +
                ApplicationSecurityConfig.ACCOUNT_VERIFICATION_TIME_HRS +
                " hours.<br>" +
                "If you wish to continue using " +
                BaseMailTemplate.PUBLIC_NAME_STATIC +
                ", you may " +
                "<a href=\"" + BaseMailTemplate.getBaseURL() + "/register" + "\" alt=\"Register a new account on " +
                BaseMailTemplate.PUBLIC_NAME_STATIC +
                "\">register</a> " +
                " with the same email again.<br><br>" +
                "Thanks for being with us at " +
                BaseMailTemplate.PUBLIC_NAME_STATIC +
                "!";

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
        helper.setBcc(this.toAddresses);
        return message;
    }
}
