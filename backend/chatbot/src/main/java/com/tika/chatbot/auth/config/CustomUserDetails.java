package com.tika.chatbot.auth.config;

import java.util.UUID;

public class CustomUserDetails {
    private final UUID id;
    private final String email;
    private final String role;

    public CustomUserDetails(UUID id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}