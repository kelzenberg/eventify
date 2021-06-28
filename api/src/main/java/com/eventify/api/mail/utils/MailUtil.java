package com.eventify.api.mail.utils;

import com.eventify.api.mail.constants.TemplateConstants;
import com.eventify.api.mail.templates.MailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MailUtil {

    @Autowired
    private JavaMailSender sender;

    public MimeMessageHelper getMessageHelper() throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(TemplateConstants.SENDER_MAIL_ADDRESS);
        return helper;
    }

    public List<MimeMessage> generateMimeMessages(List<MailTemplate> templates) throws MessagingException {
        List<MessagingException> exceptions = new ArrayList<>();

        List<MimeMessage> messages = templates.stream()
                .map(template -> {
                    try {
                        MimeMessageHelper helper = getMessageHelper();
                        helper.setSubject(template.getSubject());
                        helper.setText(template.getTemplate(), true);
                        helper.setTo(template.getToAddress());
                        return helper.getMimeMessage();
                    } catch (MessagingException e) {
                        exceptions.add(e);
                        return null;
                    }
                })
                .collect(Collectors.toList());

        for (MessagingException exception : exceptions) {
            throw exception;
        }

        return messages;
    }

    public void sendMessagesInBatches(List<MimeMessage> messages) {
        int batchLimit = 50;
        int amountOfBatches = (int) Math.ceil(messages.size() / (double) batchLimit); // limit the sending to 50 mails at once

        for (int batchIdx = 0; batchIdx < amountOfBatches; batchIdx++) {
            int fromIndex = Math.min(batchIdx * batchLimit, messages.size());
            int toIndex = Math.min((batchIdx + 1) * batchLimit, messages.size());

            if (fromIndex == toIndex) {
                break;
            }

            List<MimeMessage> messageBatch = messages.subList(fromIndex, toIndex);
            sender.send(messageBatch.toArray(MimeMessage[]::new));
        }
    }

}
