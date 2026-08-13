package com.tika.chatbot.auth.dto;

import java.util.List;

public record VectorStatusResponse(
        long indexed, long pending, long failed, long totalChunks,
        List<DocumentStatusDto> documents
) {}