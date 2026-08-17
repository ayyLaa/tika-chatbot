package com.tika.chatbot.chat.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonQueryRequest(
        String message,
        @JsonProperty("user_email") String userEmail,
        List<PythonChatMessage> history) {}