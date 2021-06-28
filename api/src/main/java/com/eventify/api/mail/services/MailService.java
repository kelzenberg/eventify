package com.eventify.api.mail.services;

import com.eventify.api.mail.constants.TemplateConstants;
import com.eventify.api.mail.templates.delete.DeleteMailData;
import com.eventify.api.mail.templates.delete.DeleteMailTemplate;
import com.eventify.api.mail.templates.invite.InviteMailData;
import com.eventify.api.mail.templates.invite.InviteMailTemplate;
import com.eventify.api.mail.templates.register.RegisterMailData;
import com.eventify.api.mail.templates.register.RegisterMailTemplate;
import com.eventify.api.mail.templates.reminder.ReminderMailData;
import com.eventify.api.mail.templates.reminder.ReminderMailTemplate;
import com.eventify.api.mail.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailService {

    @Autowired
    public MailUtil util;

    public void sendRegisterMail(RegisterMailData data) throws MessagingException {
        List<MimeMessage> messages = util.generateMimeMessages(
                List.of(new RegisterMailTemplate(data))
        );
        util.sendMessagesInBatches(messages);
    }

    public void sendReminderMailBulk(List<ReminderMailData> data) throws MessagingException {
        List<MimeMessage> messages = util.generateMimeMessages(
                data.stream().map(ReminderMailTemplate::new).collect(Collectors.toList())
        );
        util.sendMessagesInBatches(messages);
    }

    public void sendInviteMail(InviteMailData data) throws MessagingException {
        List<MimeMessage> messages = util.generateMimeMessages(
                List.of(new InviteMailTemplate(data))
        );
        util.sendMessagesInBatches(messages);
    }

    public void sendDeleteMailBulk(List<DeleteMailData> data) throws MessagingException {
        List<MimeMessage> messages = util.generateMimeMessages(
                data.stream().map(DeleteMailTemplate::new).collect(Collectors.toList())
        );
        util.sendMessagesInBatches(messages);
    }

    public void sendTestEmail(String[] to, String subject, String htmlTemplate) throws MessagingException {
        MimeMessageHelper helper = util.getMessageHelper();
        helper.setFrom(TemplateConstants.SENDER_MAIL_ADDRESS);
        helper.setSubject(subject);
        helper.setText(String.format(TemplateConstants.getBaseTemplate(), htmlTemplate), true);
        helper.setTo(to);

        util.sendMessagesInBatches(List.of(helper.getMimeMessage()));
    }
}
