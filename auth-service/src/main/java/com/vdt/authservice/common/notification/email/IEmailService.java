package com.vdt.authservice.common.notification.email;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
    void sendVerificationEmail(String to, String token);
    void sendResetPasswordEmail(String to, String token);
}
