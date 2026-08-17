package com.tika.chatbot.chat.service;

import com.tika.chatbot.chat.dto.*;
import com.tika.chatbot.chat.model.ChatSession;
import com.tika.chatbot.chat.model.Message;
import com.tika.chatbot.chat.repository.ChatSessionRepository;
import com.tika.chatbot.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final RestTemplate restTemplate;
    private final ChatSessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Value("${ai.service.internal-key}")
    private String internalApiKey;

    public ChatService(RestTemplate restTemplate, ChatSessionRepository sessionRepository,
                       MessageRepository messageRepository) {
        this.restTemplate = restTemplate;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    public ChatResponse askQuestion(UUID userId, String userEmail, UUID sessionId, String question) {
        UUID actualSessionId = sessionId;
        if (actualSessionId == null) {
            ChatSession session = new ChatSession();
            session.setUserId(userId);
            session.setTitle(question.length() > 50 ? question.substring(0, 50) : question);
            actualSessionId = sessionRepository.save(session).getId();
        }

        List<Message> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(actualSessionId);
        List<PythonChatMessage> historyDto = new ArrayList<>();
        for (Message m : history) {
            historyDto.add(new PythonChatMessage("user", m.getQuestion()));
            historyDto.add(new PythonChatMessage("assistant", m.getAnswer()));
        }

        PythonQueryRequest pyRequest = new PythonQueryRequest(question, userEmail, historyDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Key", internalApiKey);
        HttpEntity<PythonQueryRequest> requestEntity = new HttpEntity<>(pyRequest, headers);

        PythonQueryResponse response = restTemplate.postForObject(
                aiServiceUrl + "/api/chat", requestEntity, PythonQueryResponse.class
        );

        Message message = new Message();
        message.setSessionId(actualSessionId);
        message.setQuestion(question);
        message.setAnswer(response.reply());
        messageRepository.save(message);

        List<String> sources = response.sources() != null ? response.sources() : List.of();

        return new ChatResponse(actualSessionId, response.reply(), sources);
    }

    public List<ChatSessionDTO> getUserChatHistory(UUID userId) {
        // 1. Povuci SVE sesije i SVE njihove poruke u SAMO JEDNOM upitu bazi!
        List<ChatSession> sessions = sessionRepository.findByUserIdWithMessages(userId);

        // 2. Pretvori (mapiraj) bazu podataka u DTO objekte za frontend
        return sessions.stream().map(session -> {

            // Pošto smo koristili JOIN FETCH, session.getMessages() ne pravi novi upit u bazu,
            // već koristi podatke koje je već povukao.
            List<MessageDTO> messageDTOs = session.getMessages().stream()
                    .map(m -> new MessageDTO(
                            m.getQuestion(),
                            m.getAnswer(),
                            m.getCreatedAt()
                    ))
                    .toList();

            return new ChatSessionDTO(
                    session.getId(),
                    session.getTitle(),
                    messageDTOs
            );

        }).toList();
    }
}