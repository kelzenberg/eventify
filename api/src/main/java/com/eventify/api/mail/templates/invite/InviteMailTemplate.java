package com.eventify.api.mail.templates.invite;

import com.eventify.api.mail.constants.TemplateConstants;
import com.eventify.api.mail.templates.MailTemplate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class InviteMailTemplate extends MailTemplate {
    private final String eventName;

    public InviteMailTemplate(InviteMailData data) {
        super(
                String.format("You were invited to %s", TemplateConstants.STATIC_PUBLIC_NAME),
                data.getToAddress()
        );

        this.eventName = data.getEventName();
    }

    @Override
    public String getTemplate() {
        String registerURL = TemplateConstants.getFrontendURL() + "/register";
        String template = "You (" +
                getToAddress() +
                ") were invited to join the <b><i>" +
                eventName +
                "</i></b> Event on " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                ".<br><br>"
                + "<a href=\"" +
                registerURL +
                "\" alt=\"Register on " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "\">Create a new account now</a> and let your people know that you joined " +
                TemplateConstants.STATIC_PUBLIC_NAME +
                "!";

        String baseTemplate = TemplateConstants.getBaseTemplate();
        return String.format(baseTemplate, template);
    }
}
