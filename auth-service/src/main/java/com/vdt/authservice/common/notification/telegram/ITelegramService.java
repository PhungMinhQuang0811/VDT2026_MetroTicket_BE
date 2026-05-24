package com.vdt.authservice.common.notification.telegram;

public interface ITelegramService {
    void sendMessage(String chatId, String message);
}
