package com.tika.chatbot.chat.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        List<SourceDto> sources,
        Integer responseTimeMs,
        Integer tokensUsed
) {}