package com.tika.chatbot.chat.controller;

import com.tika.chatbot.auth.config.CustomUserDetails;
import com.tika.chatbot.chat.model.Message;
import com.tika.chatbot.chat.repository.MessageRepository;
import com.tika.chatbot.chat.service.ChatShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class SharedChatController {

    private final ChatShareService chatShareService;
    private final MessageRepository messageRepository;

    // Injecting ChatShareService instead of the repository directly
    public SharedChatController(ChatShareService chatShareService, MessageRepository messageRepository) {
        this.chatShareService = chatShareService;
        this.messageRepository = messageRepository;
    }

    // 1. ENDPOINT FOR SENDING THE CHAT (Creates a link and sends an email via the service)
    @PostMapping("/share-in-app")
    public ResponseEntity<?> shareChat(@RequestBody Map<String, String> request, @AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        UUID sessionId = UUID.fromString(request.get("session_id"));
        String recipientEmail = request.get("recipient_email");
        String note = request.get("note");

        // Calling the service we defined
        chatShareService.shareChat(sessionId, user.getEmail(), recipientEmail, note);

        return ResponseEntity.ok(Map.of("status", "success"));
    }

    // 2. ENDPOINT FOR OPENING SOMEONE ELSE'S CHAT (Security check + returning messages)
    @GetMapping("/shared/{sessionId}")
    public ResponseEntity<?> getSharedChat(@PathVariable UUID sessionId,
                                           @AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            return ResponseEntity.status(401).build();  // must log in first
        }

        boolean isOwner = chatShareService.isOwnerByEmail(sessionId, user.getEmail());
        boolean hasAccess = isOwner || chatShareService.canAccess(sessionId, user.getEmail());

        if (!hasAccess) {
            return ResponseEntity.status(403).body(Map.of("message", "Bu sohbete erişim izniniz yok."));
        }

        List<Message> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        List<Map<String, Object>> formattedMessages = new ArrayList<>();
        for (Message m : history) {
            formattedMessages.add(Map.of("sender", "user", "text", m.getQuestion()));
            if (m.getAnswer() != null) {
                formattedMessages.add(Map.of("sender", "ai", "text", m.getAnswer()));
            }
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "messages", formattedMessages,
                "readOnly", !isOwner   // the frontend uses this to hide the input field
        ));
    }
}