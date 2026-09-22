package com.tika.chatbot.auth.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {

        super(userId + " kimliğine sahip kullanıcı bulunamadı.");
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
