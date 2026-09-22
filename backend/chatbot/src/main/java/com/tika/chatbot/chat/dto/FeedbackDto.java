package com.tika.chatbot.chat.dto;

public record FeedbackDto(String feedbackId, String time, String user, String answerText, String text, Short rating) {}
