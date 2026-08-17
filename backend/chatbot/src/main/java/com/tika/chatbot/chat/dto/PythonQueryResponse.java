package com.tika.chatbot.chat.dto;

import java.util.List;

public record PythonQueryResponse(String reply, List<String> sources, String status) {}