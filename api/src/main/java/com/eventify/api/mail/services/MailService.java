package com.eventify.api.mail.services;

import com.eventify.api.mail.constants.MailTemplateType;
import com.eventify.api.mail.templates.RegisterTemplate;
import com.eventify.api.mail.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Objects;

@Service
public class MailService {

    @Autowired
    public MailUtil util;

    @Autowired
    private JavaMailSender sender;

    public void sendEmail(String[] to, String subject, String text) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("noreply@eventify.com");
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setTo(to);

        sender.send(message);
    }

    // TODO: choose MailTemplate automatically based on mapped function name
    public void sendInviteMail(String to, String eventName) throws MessagingException {
        SimpleMailMessage template = util.getMessageTemplate(MailTemplateType.INVITE);
        String subject = Objects.requireNonNull(template.getSubject());
        String text = String.format(Objects.requireNonNull(template.getText()), to, eventName);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("noreply@eventify.com");
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setTo(to);

        sender.send(message);
    }

    public void sendRegisterMail(String toAddress, Date createdAt, String verificationHash) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        RegisterTemplate template = new RegisterTemplate(message, toAddress, createdAt, verificationHash);
        sender.send(template.getMessage());
    }
}
