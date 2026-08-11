package com.tika.chatbot.auth.exception;

public class UserDeactivatedException extends RuntimeException {
    public UserDeactivatedException() {

        super("Your account has been deactivated. Contact your administrator.");
    }
}
