package com.tika.chatbot.auth.dto;

public record DocumentStatusDto(String fileName, String status, long chunkCount, String lastSync) {}