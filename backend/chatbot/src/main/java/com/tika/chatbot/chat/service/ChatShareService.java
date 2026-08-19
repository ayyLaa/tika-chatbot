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
                .orElseThrow(() -> new RuntimeException("Sesija ne postoji."));

        User owner = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new RuntimeException("Vlasnik sesije ne postoji."));

        if (!owner.getEmail().equalsIgnoreCase(senderEmail)) {
            throw new RuntimeException("Samo vlasnik razgovora može ga podijeliti.");
        }

        User colleague = userRepository.findByEmail(colleagueEmail)
                .orElseThrow(() -> new UserNotFoundException("Kolega nije pronađen."));




        SharedChat share = new SharedChat();
        share.setSessionId(sessionId);
        share.setSenderEmail(senderEmail); // Upisujemo email pošiljaoca
        share.setTargetUserEmail(colleague.getEmail()); // Upisujemo email primaoca
        share.setNote(note);
        shareRepository.save(share);

        String chatLink = "http://localhost:5173/chat?session_id=" + sessionId;
        emailService.sendChatShareEmail(colleague.getEmail(), senderEmail, chatLink, note);
    }

    // provjera pristupa — ovo pozivaš PRIJE nego vratiš poruke sesije
    public boolean canAccess(UUID sessionId, String userEmail) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) return false;

        // Ako je vlasnik sesije
        User owner = userRepository.findById(session.getUserId()).orElse(null);
        if (owner != null && owner.getEmail().equalsIgnoreCase(userEmail)) {
            return true;
        }

        // Ili ako je chat podijeljen sa ovim emailom u 'shared_chats' tabeli
        return shareRepository.existsBySessionIdAndTargetUserEmail(sessionId, userEmail); // dijeljeno s njim
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