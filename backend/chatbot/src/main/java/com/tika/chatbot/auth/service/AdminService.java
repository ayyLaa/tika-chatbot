package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.dto.*;
import com.tika.chatbot.auth.exception.EmailAlreadyExistsException;
import com.tika.chatbot.auth.exception.InviteAlreadyExistsException;
import com.tika.chatbot.auth.exception.ResourceNotFoundException;
import com.tika.chatbot.auth.exception.UserNotFoundException;
import com.tika.chatbot.auth.model.PasswordResetRequest;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.model.UserInvite;
import com.tika.chatbot.auth.repository.LoginHistoryRepository;
import com.tika.chatbot.chat.dto.RiskFlagDto;
import com.tika.chatbot.chat.model.Message;
import com.tika.chatbot.chat.repository.*;
import com.tika.chatbot.auth.repository.PasswordResetRequestRepository;
import com.tika.chatbot.auth.repository.UserInviteRepository;
import com.tika.chatbot.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;
    private final UserInviteRepository inviteRepository;
    private final PasswordResetRequestRepository resetRequestRepository;
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final MessageRepository messageRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    private final MessageFeedbackRepository messageFeedbackRepository;

    public AdminService(UserRepository userRepository, AuditLogService auditLogService, EmailService emailService, UserInviteRepository inviteRepository, PasswordResetRequestRepository resetRequestRepository, DocumentRepository documentRepository, ChunkRepository chunkRepository, MessageRepository messageRepository, LoginHistoryRepository loginHistoryRepository, MessageFeedbackRepository messageFeedbackRepository) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.emailService = emailService;
        this.inviteRepository = inviteRepository;
        this.resetRequestRepository = resetRequestRepository;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.messageRepository = messageRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.messageFeedbackRepository = messageFeedbackRepository;
    }

    @Transactional
    public void deactivateUser(UUID targetUserId, UUID adminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));


        user.setIsActive(!user.getIsActive());
        userRepository.save(user);


        String actionString = user.getIsActive() ? "user_activated" : "user_deactivated";
        auditLogService.logAction(adminId, actionString, targetUserId);
    }

    public void inviteUser(String email, String role, UUID adminId) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        if (inviteRepository.existsByEmailAndStatus(email, "pending")) {
            throw new InviteAlreadyExistsException(email);
        }

        String token = UUID.randomUUID().toString();
        UserInvite invite = new UserInvite();
        invite.setEmail(email);
        invite.setInvitedRole(role.toLowerCase());
        invite.setInviteToken(token);
        invite.setInvitedBy(adminId);
        invite.setExpiresAt(LocalDateTime.now().plusDays(7));

        inviteRepository.save(invite);
        emailService.sendInviteEmail(email, token);
    }

    public void approvePasswordReset(UUID requestId, UUID adminId) {
        PasswordResetRequest req = resetRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Talep mevcut değil."));

        String token = UUID.randomUUID().toString();
        req.setStatus("approved");
        req.setReviewedBy(adminId);
        req.setReviewedAt(LocalDateTime.now());
        req.setResetToken(token);
        req.setTokenExpires(LocalDateTime.now().plusHours(1));
        resetRequestRepository.save(req);

        User user = userRepository.findById(req.getUserId()).orElseThrow();
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void rejectPasswordReset(UUID requestId, UUID adminId) {
        PasswordResetRequest req = resetRequestRepository.findById(requestId).orElseThrow();
        req.setStatus("rejected");
        req.setReviewedBy(adminId);
        req.setReviewedAt(LocalDateTime.now());
        resetRequestRepository.save(req);
    }

    public List<PasswordResetRequestDto> getPendingPasswordRequests() {
        return resetRequestRepository.findPendingWithUserInfo().stream()
                .map(row -> new PasswordResetRequestDto(row[0].toString(), (String) row[1], (String) row[2]))
                .toList();
    }

    public List<UserSummaryDto> getAllUsers(String roleFilter) {
        var stream = userRepository.findAll().stream();
        if (roleFilter != null) {
            stream = stream.filter(u -> u.getUserRole().equalsIgnoreCase(roleFilter));
        }

        return stream.map(u -> {
            // Look up the last login for this user
            String lastLoginTime = loginHistoryRepository
                    .findFirstByUserIdOrderByDateTimeDesc(u.getId())
                    .map(login -> login.getDateTime().toString())
                    .orElse("Belirsiz"); // If they never logged in

            return new UserSummaryDto(
                    u.getId(), u.getFullName(), u.getUsername(), u.getEmail(),
                    u.getDepartment(), u.getUserRole(), u.getIsActive(),
                    lastLoginTime // <-- Sending that date to Vue.js
            );
        }).toList();
    }

    public VectorStatusResponse getVectorStatus() {
        long indexed = documentRepository.countByDocStatus("ready");
        long pending = documentRepository.countByDocStatus("pending");
        long failed = documentRepository.countByDocStatus("failed");
        long totalChunks = chunkRepository.count();

        List<DocumentStatusDto> docs = documentRepository.findDocumentStatusSummary().stream()
                .map(row -> new DocumentStatusDto(
                        (String) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        row[3] != null ? row[3].toString() : "—"
                ))
                .toList();

        return new VectorStatusResponse(indexed, pending, failed, totalChunks, docs);
    }

    public AnalyticsResponse getAnalytics() {
        Long tokens = messageRepository.sumTokensLast30Days();
        Double avgTime = messageRepository.avgResponseTimeLast30Days();
        long activeUsers = messageRepository.countActiveUsersLast30Days();
        Double satisfaction = messageFeedbackRepository.satisfactionPercentage();

        Double finalSatisfaction = satisfaction != null ? satisfaction : 0.0;

        List<DailyUsageDto> daily = messageRepository.dailyMessageCountsLast14Days().stream()
                .map(row -> new DailyUsageDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        Double avgTimeSeconds = avgTime != null ? avgTime / 1000.0 : null;

        return new AnalyticsResponse(tokens, avgTimeSeconds, activeUsers, finalSatisfaction, daily);
    }

    public List<QaHistoryDto> getQaHistory() {
        return messageRepository.findRecentQaHistory().stream()
                .map(row -> new QaHistoryDto(
                        row[0].toString(), (String) row[1], (String) row[2],
                        (String) row[3], row[4] != null ? row[4].toString() : "-",
                        row[5] != null ? ((Number) row[5]).shortValue() : null
                ))
                .toList();
    }

    public List<RiskFlagDto> getFlaggedMessages() {
        return messageRepository.findFlaggedMessages().stream()
                .map(row -> new RiskFlagDto(
                        row[0].toString(), row[1].toString(), (String) row[2], (String) row[3], (String) row[4]
                ))
                .toList();
    }

    @Transactional
    public void reviewRiskFlag(UUID messageId, boolean isRisky, UUID adminId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesaj bulunamadı"));
        message.setRiskStatus(isRisky ? "confirmed" : "clean");
        message.setRiskReviewedBy(adminId);
        message.setRiskReviewedAt(LocalDateTime.now());
        messageRepository.save(message);
    }
}
