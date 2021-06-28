package com.eventify.api.mail.templates.delete;

import com.eventify.api.auth.ApplicationSecurityConfig;
import com.eventify.api.mail.constants.TemplateConstants;
import com.eventify.api.mail.templates.MailTemplate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DeleteMailTemplate extends MailTemplate {
    public DeleteMailTemplate(DeleteMailData data) {
        super(
                String.format("Your %s Account was deleted", TemplateConstants.STATIC_PUBLIC_NAME),
                data.getToAddress()
        );
    }

    @Override
    public String getTemplate() {
        String registerURL = TemplateConstants.getFrontendURL() + "/register";
        String template = "Your account was deleted due to not being verified in the last " +
                ApplicationSecurityConfig.ACCOUNT_VERIFICATION_TIME_HRS +
                " hours.<br>" +
                "If you wish to continue using " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                ", you may " +
                "<a href=\"" +
                registerURL +
                "\" alt=\"Register a new account on " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "\">register</a> " +
                " with the same email again.<br><br>" +
                "Thanks for being with us at " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "!";

        String baseTemplate = TemplateConstants.getBaseTemplate();
        return String.format(baseTemplate, template);
    }
}
