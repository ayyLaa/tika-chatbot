package com.tika.chatbot.auth.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {

        super("User with " + userId + " not found");
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
