package com.tika.chatbot.auth.repository;

import com.tika.chatbot.auth.model.UserInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserInviteRepository extends JpaRepository<UserInvite, UUID> {
    Optional<UserInvite> findByInviteTokenAndStatus(String token, String status);
    boolean existsByEmailAndStatus(String email, String status);
}