package com.tika.chatbot.chat.dto;

public record RiskFlagDto(String messageId, String time, String userEmail, String question, String riskStatus) {}