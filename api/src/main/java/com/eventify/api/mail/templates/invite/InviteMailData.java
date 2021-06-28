package com.eventify.api.mail.templates.invite;

import com.eventify.api.mail.templates.MailData;
import lombok.Getter;

@Getter
public class InviteMailData extends MailData {
    String eventName;

    public InviteMailData(String toAddress, String eventName) {
        super(toAddress);
        this.eventName = eventName;
    }
}
