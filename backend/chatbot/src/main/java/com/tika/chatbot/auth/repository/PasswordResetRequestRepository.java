package com.tika.chatbot.auth.repository;

import com.tika.chatbot.auth.model.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, UUID> {
    Optional<PasswordResetRequest> findByResetToken(String token);
    List<PasswordResetRequest> findByStatusOrderByRequestedAtDesc(String status);
}