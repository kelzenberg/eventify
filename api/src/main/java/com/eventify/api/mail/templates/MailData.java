package com.eventify.api.mail.templates;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class MailData {
    protected final String toAddress;
}
