package com.tika.chatbot.auth.repository;

import com.tika.chatbot.auth.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {


    Optional<LoginHistory> findFirstByUserIdOrderByDateTimeDesc(UUID userId);
}
