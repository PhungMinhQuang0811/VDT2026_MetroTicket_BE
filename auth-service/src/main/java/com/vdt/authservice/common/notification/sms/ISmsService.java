package com.vdt.authservice.common.notification.sms;

public interface ISmsService {
    void sendSms(String phoneNumber, String message);
}
