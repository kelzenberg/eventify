package com.eventify.api.mail.templates.reminder;

import com.eventify.api.mail.templates.MailData;
import lombok.Getter;

import java.util.Date;

@Getter
public class ReminderMailData extends MailData {
    Date createdAt;
    String verificationHash;

    public ReminderMailData(String toAddress, Date createdAt, String verificationHash) {
        super(toAddress);
        this.createdAt = createdAt;
        this.verificationHash = verificationHash;
    }
}
