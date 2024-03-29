package com.eventify.api.mail.controllers;

import com.eventify.api.constants.AdminPaths;
import com.eventify.api.entities.Views;
import com.eventify.api.mail.services.MailService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Arrays;

@RestController
public class MailController {

    @Autowired
    private MailService service;

    @PostMapping(AdminPaths.MAIL + "/send")
    @JsonView(Views.PublicExtended.class)
    public ResponseEntity<String> send(@Valid @RequestBody MailSendRequest body) throws MessagingException {
        service.sendTestEmail(body.getRecipients(), body.getSubject(), body.getContent());
        return ResponseEntity.ok("Emails sent to " + Arrays.toString(body.getRecipients()));
    }
}
