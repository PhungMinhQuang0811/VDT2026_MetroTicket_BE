package com.vdt.authservice.external.notification.telegram;

public interface ITelegramService {
    void sendMessage(String chatId, String message);
}
