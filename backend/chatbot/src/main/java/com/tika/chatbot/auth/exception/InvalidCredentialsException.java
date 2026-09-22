package com.tika.chatbot.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Geçersiz kimlik bilgileri.");
    }
}
