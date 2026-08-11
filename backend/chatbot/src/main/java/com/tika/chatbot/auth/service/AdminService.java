package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.exception.UserNotFoundException;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public AdminService(UserRepository userRepository, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void deactivateUser(UUID targetUserId, UUID adminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        user.setIsActive(false);
        userRepository.save(user);

        auditLogService.logAction(adminId, "user_removed", targetUserId);
    }
}
