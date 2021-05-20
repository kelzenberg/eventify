package com.eventify.api.mail.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailSendRequest implements Serializable {

    @NotNull
    @NotEmpty
    private String[] recipients;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 64)
    private String subject;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 102400)
    private String content;
}
