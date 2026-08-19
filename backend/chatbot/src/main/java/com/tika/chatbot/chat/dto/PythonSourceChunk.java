package com.tika.chatbot.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PythonSourceChunk(
        String document,
        Integer page, @JsonProperty("chunk_id") String chunkId) {}