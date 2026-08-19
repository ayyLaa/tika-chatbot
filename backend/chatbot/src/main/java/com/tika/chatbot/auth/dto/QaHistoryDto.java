package com.tika.chatbot.auth.dto;

public record QaHistoryDto(String time, String user, String question, String source, String duration, Short feedback) {}
