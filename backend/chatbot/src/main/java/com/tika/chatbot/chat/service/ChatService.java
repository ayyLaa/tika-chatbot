package com.tika.chatbot.chat.service;

import com.tika.chatbot.auth.exception.UnauthorizedActionException;
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
    private final RiskKeywordChecker riskKeywordChecker;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Value("${ai.service.internal-key}")
    private String internalApiKey;

    public ChatService(RestTemplate restTemplate, ChatSessionRepository sessionRepository,
                       MessageRepository messageRepository, RiskKeywordChecker riskKeywordChecker) {
        this.restTemplate = restTemplate;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.riskKeywordChecker = riskKeywordChecker;
    }

    public ChatResponse askQuestion(UUID userId, String userEmail, UUID sessionId, String question) {
        UUID actualSessionId = sessionId;
        if (sessionId != null) {
            ChatSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Oturum mevcut değil.")); // Added () ->

            if (!session.getUserId().equals(userId)) {
                throw new RuntimeException("Bu görüşmeye devam etme izniniz yok.");
            }
        } else {
            // Create a new session if sessionId is null
            ChatSession session = new ChatSession();
            session.setUserId(userId);
            session.setTitle(question.length() > 50 ? question.substring(0, 50) : question);
            actualSessionId = sessionRepository.save(session).getId();
        }

        List<Message> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(actualSessionId);
        List<ConversationTurnDto> historyDto = new ArrayList<>();
        for (Message m : history) {
            historyDto.add(new ConversationTurnDto(m.getQuestion(), m.getAnswer()));
        }

        PythonQueryRequest pyRequest = new PythonQueryRequest(
                actualSessionId.toString(),   // sessionId
                question,                      // question
                5,                              // topK
                historyDto                      // conversationHistory
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Key", internalApiKey);
        HttpEntity<PythonQueryRequest> requestEntity = new HttpEntity<>(pyRequest, headers);

        PythonQueryResponse response = restTemplate.postForObject(
                aiServiceUrl + "/query", requestEntity, PythonQueryResponse.class
        );

        Message message = new Message();
        message.setSessionId(actualSessionId);
        message.setQuestion(question);
        message.setAnswer(response.answer());
        message.setResponseTimeMs(response.responseTimeMs());
        message.setTokensUsed(response.tokensUsed());
        message.setRiskStatus(riskKeywordChecker.isRisky(question) ? "flagged" : "none");
        messageRepository.save(message);

        List<SourceDto> sources = response.sources() != null
                ? response.sources().stream()
                .map(s -> new SourceDto(s.document(), s.page(), s.chunkId()))
                .toList()
                : List.of();

        return new ChatResponse(actualSessionId, message.getId(), response.answer(), sources, response.responseTimeMs(), response.tokensUsed());
    }

    public List<ChatSessionDTO> getUserChatHistory(UUID userId) {
        // 1. Fetch ALL sessions and ALL their messages in a SINGLE database query!
        List<ChatSession> sessions = sessionRepository.findByUserIdWithMessages(userId);

        // 2. Convert (map) the database entities into DTO objects for the frontend
        return sessions.stream().map(session -> {

            // Since we used JOIN FETCH, session.getMessages() doesn't trigger a new database query,
            // it uses the data that was already fetched.
            List<MessageDTO> messageDTOs = session.getMessages().stream()
                    .map(m -> new MessageDTO(
                            m.getId(),
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