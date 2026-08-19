package com.tika.chatbot.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonQueryResponse(
        String answer,
        List<PythonSourceChunk> sources,
        @JsonProperty("response_time_ms") Integer responseTimeMs,
        @JsonProperty("tokens_used") Integer tokensUsed
) {}