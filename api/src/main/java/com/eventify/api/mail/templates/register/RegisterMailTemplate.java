package com.eventify.api.mail.templates.register;

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
public class RegisterMailTemplate extends MailTemplate {
    private final Date createdAt;
    private final String verificationHash;

    public RegisterMailTemplate(RegisterMailData data) {
        super(
                String.format("Welcome to %s", TemplateConstants.STATIC_PUBLIC_NAME),
                data.getToAddress()
        );

        this.createdAt = data.getCreatedAt();
        this.verificationHash = data.getVerificationHash();
    }

    @Override
    public String getTemplate() {
        String verifyURL = TemplateConstants.getFrontendURL() + "/register?verify=" + verificationHash;
        String template = "Your new account " +
                "<i>" + super.getToAddress() + "</i> " +
                "on " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                " is almost ready.<br>" +
                "Please verify your account by clicking " +
                "<a href=\"" + verifyURL + "\" alt=\"Verify your email address for " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "\">this link</a> " +
                "or copying and opening the following URL into your Browser:<br><br>" +
                "<i>" + verifyURL + "</i><br><br>" +
                "The link is valid for " +
                ApplicationSecurityConfig.ACCOUNT_VERIFICATION_TIME_HRS +
                " hours (until " +
                TemplateConstants.HUMAN_DATE_FORMAT.format(
                        new Date(createdAt.getTime() +
                                (long) ApplicationSecurityConfig.ACCOUNT_VERIFICATION_TIME_HRS * (60 - 1) * 60 * 1000) // = Hours to Milliseconds, minus 1 minute
                ) +
                ").<br>" +
                "Otherwise your account will be permanently deleted afterwards.<br><br>" +
                "<b>Welcome to " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "!</b>";

        String baseTemplate = TemplateConstants.getBaseTemplate();
        return String.format(baseTemplate, template);
    }
}
