package com.tika.chatbot.auth.exception;

public class InvalidEmailDomainException extends RuntimeException {
    public InvalidEmailDomainException() {

        super("Kayıt işlemi yalnızca kurumsal e-posta adresi ile mümkündür.");
    }
}
