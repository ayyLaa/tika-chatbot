package com.tika.chatbot.chat.dto;

import java.util.List;
import java.util.UUID;

public record ChatSessionDTO(
        UUID sessionId,
        String title,
        List<MessageDTO> messages
) {}