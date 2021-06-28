package com.eventify.api.mail.templates;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public abstract class MailTemplate {
    private final String subject;
    private final String toAddress;
    public abstract String getTemplate();
}
