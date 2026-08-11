package com.tika.chatbot.auth.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Please verify your email address");
    }
}
