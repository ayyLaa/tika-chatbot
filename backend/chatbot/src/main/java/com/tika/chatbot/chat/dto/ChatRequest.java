package com.tika.chatbot.chat.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record ChatRequest(
        UUID sessionId,          // null = new session
        @NotBlank String question,
        Integer topK,
        List<ConversationTurnDto> conversationHistory
) {}