package com.tika.chatbot.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {

      super(email + " e-posta adresine sahip bir kullanıcı zaten mevcut.");
    }
}
