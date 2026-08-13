package com.tika.chatbot.auth.exception;

public class InviteAlreadyExistsException extends RuntimeException {
    public InviteAlreadyExistsException(String email) {
        super("There is already an active email invitation for " + email);
    }
}