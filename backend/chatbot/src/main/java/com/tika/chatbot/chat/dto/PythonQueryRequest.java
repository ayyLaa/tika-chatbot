package com.tika.chatbot.chat.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonQueryRequest(
        @JsonProperty("session_id") String sessionId,
        String question,
        @JsonProperty("top_k")  Integer topK,
        @JsonProperty("conversation_history") List<ConversationTurnDto> conversationHistory) {}