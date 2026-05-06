package com.vdt.authservice.external.notification.email;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
}
