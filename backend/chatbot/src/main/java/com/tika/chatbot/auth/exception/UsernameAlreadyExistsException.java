package com.tika.chatbot.auth.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {

        super(username + " kullanıcı adına sahip bir kullanıcı zaten mevcut.");
    }
}
