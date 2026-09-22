package com.tika.chatbot.auth.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Lütfen e-posta adresinizi doğrulayın.");
    }
}
