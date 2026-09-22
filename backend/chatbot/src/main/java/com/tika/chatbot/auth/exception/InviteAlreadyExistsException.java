package com.tika.chatbot.auth.exception;

public class InviteAlreadyExistsException extends RuntimeException {
    public InviteAlreadyExistsException(String email) {
        super(email + " için zaten aktif bir davet bulunmaktadır.");
    }
}