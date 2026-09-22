package com.tika.chatbot.chat.controller;

import com.tika.chatbot.auth.config.CustomUserDetails;
import com.tika.chatbot.auth.exception.ResourceNotFoundException;
import com.tika.chatbot.auth.exception.UnauthorizedActionException;
import com.tika.chatbot.chat.dto.ChatRequest;
import com.tika.chatbot.chat.dto.ChatResponse;
import com.tika.chatbot.chat.dto.ChatSessionDTO;
import com.tika.chatbot.chat.dto.FeedbackDto;
import com.tika.chatbot.chat.model.ChatSession;
import com.tika.chatbot.chat.model.Message;
import com.tika.chatbot.chat.model.MessageFeedback;
import com.tika.chatbot.chat.repository.ChatSessionRepository;
import com.tika.chatbot.chat.repository.MessageFeedbackRepository;
import com.tika.chatbot.chat.repository.MessageRepository;
import com.tika.chatbot.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.repository.UserRepository;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;

    public ChatController(ChatService chatService, UserRepository userRepository, MessageRepository messageRepository, ChatSessionRepository chatSessionRepository, MessageFeedbackRepository messageFeedbackRepository) {

        this.chatService = chatService;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.messageFeedbackRepository = messageFeedbackRepository;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@Valid @RequestBody ChatRequest request,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        ChatResponse response = chatService.askQuestion(user.getId(), user.getEmail(), request.sessionId(), request.question());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatSessionDTO>> getHistory(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(chatService.getUserChatHistory(user.getId()));
    }



    public record FeedbackRequest(java.util.UUID messageId, Short rating, String comment) {}

    @PostMapping("/feedback")
    public ResponseEntity<Void> receiveFeedback(@RequestBody FeedbackRequest request,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Feedback vermek için giriş yapmalısınız.");
        }

        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesaj bulunamadı"));

        ChatSession session = chatSessionRepository.findById(message.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Oturum bulunamadı"));

        if (!session.getUserId().equals(userDetails.getId())) {
            throw new RuntimeException("Bu mesaja geri bildirim verme yetkiniz yok.");
        }

        MessageFeedback feedback = new MessageFeedback();
        feedback.setMessageId(request.messageId());
        feedback.setRating(request.rating());
        feedback.setComment(request.comment());
        messageFeedbackRepository.save(feedback);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<List<FeedbackDto>> getAllFeedbacks() {

        List<FeedbackDto> result = messageFeedbackRepository.findAllDislikeFeedback().stream()
                .map(row -> new FeedbackDto(
                        row[0].toString(),
                        row[1] != null ? row[1].toString() : "",
                        (String) row[2],
                        (String) row[3],
                        row[4] != null ? (String) row[4] : "Açıklama girilmedi (Sadece \uD83D\uDC4E verildi)",
                        ((Number) row[5]).shortValue()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<String> suggestions = new ArrayList<>();

        if (userDetails == null) {
            suggestions.add("How do I search TİKA internal data programs and databases?");
            suggestions.add("What are the cybersecurity guidelines for internal reporting?");
            return ResponseEntity.ok(suggestions);
        }

        User user = userRepository.findById(userDetails.getId()).orElse(null);
        String dept = (user != null && user.getDepartment() != null) ? user.getDepartment().toLowerCase() : "general";

        // Department-specific suggestions
        if (dept.contains("it") || dept.contains("bilgi")) {
            suggestions.add("How do I request server access for IT infrastructure?");
            suggestions.add("Where can I find system maintenance and backup schedules?");
        } else if (dept.contains("dis") || dept.contains("ilişkiler")) {
            suggestions.add("What programs does TİKA run in Africa?");
            suggestions.add("How do we coordinate with local partner agencies?");
        } else {
            suggestions.add("How do I search TİKA internal data programs?");
            suggestions.add("What are the cybersecurity guidelines?");
        }

        // Complete the list with fixed support suggestions
        suggestions.add("How do I request software access or IT technical support?");
        suggestions.add("Where can I find operational guidelines and reporting templates?");

        return ResponseEntity.ok(suggestions.stream().distinct().limit(4).collect(Collectors.toList()));
    }
}