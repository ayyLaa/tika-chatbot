package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.dto.UserSummaryDto;
import com.tika.chatbot.auth.exception.EmailAlreadyExistsException;
import com.tika.chatbot.auth.exception.InviteAlreadyExistsException;
import com.tika.chatbot.auth.exception.UserNotFoundException;
import com.tika.chatbot.auth.model.PasswordResetRequest;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.model.UserInvite;
import com.tika.chatbot.auth.repository.PasswordResetRequestRepository;
import com.tika.chatbot.auth.repository.UserInviteRepository;
import com.tika.chatbot.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;
    private final UserInviteRepository inviteRepository;
    private final PasswordResetRequestRepository resetRequestRepository;


    public AdminService(UserRepository userRepository, AuditLogService auditLogService, EmailService emailService, UserInviteRepository inviteRepository, PasswordResetRequestRepository resetRequestRepository) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.emailService = emailService;
        this.inviteRepository = inviteRepository;
        this.resetRequestRepository = resetRequestRepository;
    }

    @Transactional
    public void deactivateUser(UUID targetUserId, UUID adminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        user.setIsActive(false);
        userRepository.save(user);

        auditLogService.logAction(adminId, "user_removed", targetUserId);
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
        invite.setInvitedRole(role);
        invite.setInviteToken(token);
        invite.setInvitedBy(adminId);
        invite.setExpiresAt(LocalDateTime.now().plusDays(7));

        inviteRepository.save(invite);
        emailService.sendInviteEmail(email, token);
    }

    public void approvePasswordReset(UUID requestId, UUID adminId) {
        PasswordResetRequest req = resetRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Zahtjev ne postoji."));

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

    public List<PasswordResetRequest> getPendingResetRequests() {
        return resetRequestRepository.findByStatusOrderByRequestedAtDesc("pending");
    }

    public List<UserSummaryDto> getAllUsers(String roleFilter) {
        var stream = userRepository.findAll().stream();
        if (roleFilter != null) {
            stream = stream.filter(u -> u.getUserRole().equalsIgnoreCase(roleFilter));
        }
        return stream.map(u -> new UserSummaryDto(
                u.getId(), u.getFullName(), u.getUsername(), u.getEmail(),
                u.getDepartment(), u.getUserRole(), u.getIsActive(), null
        )).toList();
    }
}
