package com.tika.chatbot.auth.exception;

public class UserDeactivatedException extends RuntimeException {
    public UserDeactivatedException() {

        super("Hesabınız devre dışı bırakıldı. Lütfen yöneticinizle iletişime geçin.");
    }
}
