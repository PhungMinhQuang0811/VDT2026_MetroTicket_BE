package com.vdt.authservice.external.notification.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService implements IEmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending Email to: {} | Subject: {} | Body: {}", to, subject, body);
    }
}
