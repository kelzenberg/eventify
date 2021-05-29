package com.eventify.api.mail.controllers;

import com.eventify.api.constants.AdminPaths;
import com.eventify.api.mail.services.MailService;
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
    private MailService mailService;

    @PostMapping(AdminPaths.MAIL + "/send")
    public ResponseEntity<String> send(@Valid @RequestBody MailSendRequest body) throws MessagingException {
        try {
            mailService.sendEmail(body.getRecipients(), body.getSubject(), body.getContent());

            return ResponseEntity.ok("Emails sent to " + Arrays.toString(body.getRecipients()));
        } catch (MessagingException e) {
            System.out.println("[DEBUG] Sending email failed: " + e.getMessage());
            throw e;
        }
    }
}
