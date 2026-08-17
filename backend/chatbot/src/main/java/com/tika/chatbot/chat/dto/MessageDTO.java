package com.tika.chatbot.chat.dto;

import java.time.LocalDateTime;

public record MessageDTO(
        String question,
        String answer,
        LocalDateTime createdAt
) {}
