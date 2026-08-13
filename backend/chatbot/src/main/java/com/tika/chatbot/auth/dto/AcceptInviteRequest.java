package com.tika.chatbot.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcceptInviteRequest(
        @NotBlank String token,
        @NotBlank String fullName,
        @NotBlank @Size(min = 8) String password,
        String phoneNumber,
        String department
) {
}
