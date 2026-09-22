package com.tika.chatbot.chat.service;

import com.tika.chatbot.auth.exception.UnauthorizedActionException;
import com.tika.chatbot.auth.exception.UserNotFoundException;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.repository.UserRepository;
import com.tika.chatbot.auth.service.EmailService;
import com.tika.chatbot.chat.model.ChatSession;
import com.tika.chatbot.chat.model.SharedChat;
import com.tika.chatbot.chat.model.SharedChat;
import com.tika.chatbot.chat.repository.ChatSessionRepository;
import com.tika.chatbot.chat.repository.ChatShareRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

// chat/service/ChatShareService.java
@Service
public class ChatShareService {
    private final ChatShareRepository shareRepository;
    private final ChatSessionRepository sessionRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public ChatShareService(ChatShareRepository shareRepository, ChatSessionRepository sessionRepository, EmailService emailService, UserRepository userRepository) {
        this.shareRepository = shareRepository;
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }


    public void shareChat(UUID sessionId, String senderEmail, String colleagueEmail, String note) {


        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Oturum mevcut değil."));

        User owner = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new RuntimeException("Oturum sahibi mevcut değil."));

        if (!owner.getEmail().equalsIgnoreCase(senderEmail)) {
            throw new RuntimeException("Görüşmeyi yalnızca sahibi paylaşabilir.");
        }

        User colleague = userRepository.findByEmail(colleagueEmail)
                .orElseThrow(() -> new UserNotFoundException("Meslektaş bulunamadı."));




        SharedChat share = new SharedChat();
        share.setSessionId(sessionId);
        share.setSenderEmail(senderEmail); // Store the sender's email
        share.setTargetUserEmail(colleague.getEmail()); // Store the recipient's email
        share.setNote(note);
        shareRepository.save(share);

        String chatLink = "http://localhost:5173/chat?session_id=" + sessionId;
        emailService.sendChatShareEmail(colleague.getEmail(), senderEmail, chatLink, note);
    }

    // access check — call this BEFORE returning the session's messages
    public boolean canAccess(UUID sessionId, String userEmail) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) return false;

        // If they are the session owner
        User owner = userRepository.findById(session.getUserId()).orElse(null);
        if (owner != null && owner.getEmail().equalsIgnoreCase(userEmail)) {
            return true;
        }

        // Or if the chat is shared with this email in the 'shared_chats' table
        return shareRepository.existsBySessionIdAndTargetUserEmail(sessionId, userEmail); // shared with them
    }

    public boolean isOwner(UUID sessionId, UUID userId) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        return session != null && session.getUserId().equals(userId);
    }

    public boolean isOwnerByEmail(UUID sessionId, String userEmail) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) return false;
        User owner = userRepository.findById(session.getUserId()).orElse(null);
        return owner != null && owner.getEmail().equalsIgnoreCase(userEmail);
    }
}