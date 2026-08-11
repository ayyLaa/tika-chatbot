package com.tika.chatbot.auth.exception;

public class InvalidEmailDomainException extends RuntimeException {
    public InvalidEmailDomainException() {

        super("Registration is possible just with a institution email address");
    }
}
