package com.tika.chatbot.chat.service;

import com.tika.chatbot.chat.dto.*;
import com.tika.chatbot.chat.model.ChatSession;
import com.tika.chatbot.chat.model.Message;
import com.tika.chatbot.chat.repository.ChatSessionRepository;
import com.tika.chatbot.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final RestTemplate restTemplate;
    private final ChatSessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public ChatService(RestTemplate restTemplate, ChatSessionRepository sessionRepository,
                       MessageRepository messageRepository) {
        this.restTemplate = restTemplate;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    public ChatResponse askQuestion(UUID userId, UUID sessionId, String question) {
        // 1. nova sesija ako ne postoji
        UUID actualSessionId = sessionId;
        if (actualSessionId == null) {
            ChatSession session = new ChatSession();
            session.setUserId(userId);
            session.setTitle(question.length() > 50 ? question.substring(0, 50) : question);
            actualSessionId = sessionRepository.save(session).getId();
        }

        // 2. povuci historiju
        List<Message> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(actualSessionId);
        List<ConversationTurnDto> historyDto = history.stream()
                .map(m -> new ConversationTurnDto(m.getQuestion(), m.getAnswer()))
                .collect(Collectors.toList());

        // 3. pozovi Python /query
        PythonQueryRequest pyRequest = new PythonQueryRequest(
                actualSessionId.toString(), question, 5, historyDto
        );
        PythonQueryResponse response = restTemplate.postForObject(
                aiServiceUrl + "/query", pyRequest, PythonQueryResponse.class
        );

        // 4. sačuvaj poruku
        Message message = new Message();
        message.setSessionId(actualSessionId);
        message.setQuestion(question);
        message.setAnswer(response.answer());
        message.setResponseTimeMs(response.responseTimeMs());
        message.setTokensUsed(response.tokensUsed());
        messageRepository.save(message);

        List<SourceDto> sources = response.sources().stream()
                .map(s -> new SourceDto(s.document(), s.page(), s.chunkId()))
                .collect(Collectors.toList());

        return new ChatResponse(response.answer(), sources, response.responseTimeMs(), response.tokensUsed());
    }
}