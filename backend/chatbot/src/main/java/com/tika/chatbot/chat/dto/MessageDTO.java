package com.tika.chatbot.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDTO(
        UUID id,
        String question,
        String answer,
        LocalDateTime createdAt
) {}
