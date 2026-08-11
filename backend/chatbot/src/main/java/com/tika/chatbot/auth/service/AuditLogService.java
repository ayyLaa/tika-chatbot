package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.model.AuditLog;
import com.tika.chatbot.auth.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(UUID adminId, String action, UUID targetUserId) {
        AuditLog log = new AuditLog(adminId, action, targetUserId);
        auditLogRepository.save(log);
    }
}
