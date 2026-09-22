package com.tika.chatbot.auth.repository;

import com.tika.chatbot.auth.model.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, UUID> {
    Optional<PasswordResetRequest> findByResetToken(String token);
    List<PasswordResetRequest> findByStatusOrderByRequestedAtDesc(String status);
    @Query(value =
            "SELECT r.id, u.full_name, u.email " +
                    "FROM password_reset_requests r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "WHERE r.status = 'pending'",
            nativeQuery = true)
    List<Object[]> findPendingWithUserInfo();
}