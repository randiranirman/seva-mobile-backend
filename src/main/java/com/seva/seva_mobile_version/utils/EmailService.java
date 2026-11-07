package com.seva.seva_mobile_version.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("emailTaskExecutor")
    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your email");
        message.setText("Your verification code is: " + code + "\n\nThis code expires in 15 minutes.");

        try {
            mailSender.send(message);
            log.info("Verification email sent to={}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send verification email to={}: {}", toEmail, ex.getMessage());
        }
    }
}
