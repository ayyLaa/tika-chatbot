package com.tika.chatbot.auth.dto;

import java.util.UUID;

public record UserSummaryDto(
        UUID id, String fullName, String username, String email,
        String department, String userRole, boolean isActive, String lastLogin
) {}
