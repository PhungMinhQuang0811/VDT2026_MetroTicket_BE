package com.vdt.authservice.external.notification.sms;

public interface ISmsService {
    void sendSms(String phoneNumber, String message);
}
