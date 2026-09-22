package com.tika.chatbot.auth.dto;

public record PasswordResetRequestDto(String requestId, String fullName, String email) {}
