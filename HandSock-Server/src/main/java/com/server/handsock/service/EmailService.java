package com.server.handsock.service;

import jakarta.mail.internet.InternetAddress;
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
    private final ConsolePrints consolePrints = new ConsolePrints();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${handsock.name}")
    private String appName;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(new InternetAddress(mailUsername, appName, "UTF-8").toString());
            message.setTo(to);
            message.setText(text);
            message.setSubject(subject);
            mailSender.send(message);
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
    }
}