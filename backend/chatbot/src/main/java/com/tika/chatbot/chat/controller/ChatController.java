package com.tika.chatbot.chat.controller;

import com.tika.chatbot.auth.config.CustomUserDetails;
import com.tika.chatbot.chat.dto.ChatRequest;
import com.tika.chatbot.chat.dto.ChatResponse;
import com.tika.chatbot.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@Valid @RequestBody ChatRequest request,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        ChatResponse response = chatService.askQuestion(user.getId(), request.sessionId(), request.question());
        return ResponseEntity.ok(response);
    }
}