package com.eventify.api.mail.services;

import com.eventify.api.mail.utils.MailTemplate;
import com.eventify.api.mail.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.UUID;

@Service
public class MailService {

    @Autowired
    public MailUtil util;

    @Autowired
    private JavaMailSender sender;

    public void sendEmail(String[] to, String subject, String text) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = util.getMessageHelper(message);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setTo(to);

        sender.send(message);
    }

    public void sendInvite(String to, UUID eventId) throws MessagingException {
        SimpleMailMessage template = util.getMessageTemplate(MailTemplate.INVITE);
        String subject = String.format(Objects.requireNonNull(template.getSubject()), to, eventId);
        String text = String.format(Objects.requireNonNull(template.getText()), to, eventId);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = util.getMessageHelper(message);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setTo(to);

        sender.send(message);
    }
}
