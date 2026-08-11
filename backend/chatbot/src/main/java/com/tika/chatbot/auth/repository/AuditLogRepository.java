package com.tika.chatbot.auth.repository;

import com.tika.chatbot.auth.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
