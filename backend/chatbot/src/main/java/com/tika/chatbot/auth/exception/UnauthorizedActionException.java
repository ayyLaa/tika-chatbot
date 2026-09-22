package com.tika.chatbot.auth.exception;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException() {

      super("Bu işlemi gerçekleştirme yetkiniz yok.");
    }
}
