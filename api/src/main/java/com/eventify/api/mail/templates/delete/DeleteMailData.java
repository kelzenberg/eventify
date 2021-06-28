package com.eventify.api.mail.templates.delete;

import com.eventify.api.mail.templates.MailData;
import lombok.Getter;

@Getter
public class DeleteMailData extends MailData {
    public DeleteMailData(String toAddress) {
        super(toAddress);
    }
}
