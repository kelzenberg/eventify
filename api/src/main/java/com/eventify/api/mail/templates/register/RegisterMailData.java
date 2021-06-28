package com.eventify.api.mail.templates.register;

import com.eventify.api.mail.templates.MailData;
import lombok.Getter;

import java.util.Date;

@Getter
public class RegisterMailData extends MailData {
    Date createdAt;
    String verificationHash;

    public RegisterMailData(String toAddress, Date createdAt, String verificationHash) {
        super(toAddress);
        this.createdAt = createdAt;
        this.verificationHash = verificationHash;
    }
}
