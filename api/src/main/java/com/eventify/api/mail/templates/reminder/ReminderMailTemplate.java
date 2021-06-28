package com.eventify.api.mail.templates.reminder;

import com.eventify.api.auth.ApplicationSecurityConfig;
import com.eventify.api.mail.constants.TemplateConstants;
import com.eventify.api.mail.templates.MailTemplate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ReminderMailTemplate extends MailTemplate {
    private final Date createdAt;
    private final String verificationHash;

    public ReminderMailTemplate(ReminderMailData data) {
        super(
                String.format("Please verify your %s Account", TemplateConstants.STATIC_PUBLIC_NAME),
                data.getToAddress()
        );

        this.createdAt = data.getCreatedAt();
        this.verificationHash = data.getVerificationHash();
    }

    @Override
    public String getTemplate() {
        String verifyURL = TemplateConstants.getFrontendURL() + "/register?verify=" + this.verificationHash;
        String template = "Please verify your account by clicking " +
                "<a href=\"" + verifyURL + "\" alt=\"Verify your email address for " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "\">this link</a> " +
                "or copying and opening the following URL into your Browser:<br><br>" +
                "<i>" + verifyURL + "</i><br><br>" +
                "The link is valid until " +
                TemplateConstants.HUMAN_DATE_FORMAT.format(new Date(
                        this.createdAt.getTime() +
                                (long) ApplicationSecurityConfig.ACCOUNT_VERIFICATION_TIME_HRS * (60 - 1) * 60 * 1000) // = Hours to Milliseconds, minus 1 minute
                ) +
                ".<br>" +
                "Your account will be <b>permanently deleted</b> afterwards.";

        String baseTemplate = TemplateConstants.getBaseTemplate();
        return String.format(baseTemplate, template);
    }
}
