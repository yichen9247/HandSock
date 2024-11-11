package com.server.handsock.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import com.server.handsock.console.ConsolePrints;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;

@Service @Getter @Setter
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername);
            message.setTo(to);
            message.setText(text);
            message.setSubject(subject);
            mailSender.send(message);
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
        }
    }
}