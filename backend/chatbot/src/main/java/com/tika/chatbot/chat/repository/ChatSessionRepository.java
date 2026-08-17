package com.tika.chatbot.chat.repository;

import com.tika.chatbot.chat.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUserIdOrderByCreatedAtDesc(UUID userId);
    @Query("SELECT DISTINCT s FROM ChatSession s LEFT JOIN FETCH s.messages m WHERE s.userId = :userId ORDER BY s.createdAt DESC")
    List<ChatSession> findByUserIdWithMessages(@Param("userId") UUID userId);
}