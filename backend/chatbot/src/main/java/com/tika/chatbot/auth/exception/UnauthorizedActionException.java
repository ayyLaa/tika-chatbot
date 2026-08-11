package com.tika.chatbot.auth.exception;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException() {

      super("You do not have permission to perform this action");
    }
}
