package com.tika.chatbot.chat.dto;

import java.util.List;
import java.util.UUID;

public record ChatResponse(
        UUID sessionId,
        String answer,
        List<SourceDto> sources,
        Integer responseTimeMs,
        Integer tokensUsed
) {

}