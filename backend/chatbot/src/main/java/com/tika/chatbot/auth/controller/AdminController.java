package com.tika.chatbot.auth.controller;

import com.tika.chatbot.auth.config.CustomUserDetails;
import com.tika.chatbot.auth.dto.*;
import com.tika.chatbot.auth.model.PasswordResetRequest;
import com.tika.chatbot.auth.repository.PasswordResetRequestRepository;
import com.tika.chatbot.auth.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final PasswordResetRequestRepository passwordResetRequestRepository;

    public AdminController(AdminService adminService, PasswordResetRequestRepository passwordResetRequestRepository) {
        this.adminService = adminService;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentAdmin) {

        adminService.deactivateUser(userId, currentAdmin.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteUser(@Valid @RequestBody InviteRequest request,
                                           @AuthenticationPrincipal CustomUserDetails admin) {
        adminService.inviteUser(request.email(), request.role(), admin.getId());
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/password-reset-requests")
    public ResponseEntity<List<PasswordResetRequest>> getPendingResets() {
        return ResponseEntity.ok(adminService.getPendingResetRequests());
    }

    @PostMapping("/password-reset-requests/{requestId}/approve")
    public ResponseEntity<Void> approveReset(@PathVariable UUID requestId,
                                             @AuthenticationPrincipal CustomUserDetails admin) {
        adminService.approvePasswordReset(requestId, admin.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectReset(@PathVariable UUID requestId,
                                            @AuthenticationPrincipal CustomUserDetails admin) {
        adminService.rejectPasswordReset(requestId, admin.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDto>> getAllUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(adminService.getAllUsers(role));
    }

    @GetMapping("/password-reset-requests/all")
    public ResponseEntity<List<PasswordResetRequest>> getAllResetRequests() {
        return ResponseEntity.ok(passwordResetRequestRepository.findAll());
    }

    @GetMapping("/vector-status")
    public ResponseEntity<VectorStatusResponse> getVectorStatus() {
        return ResponseEntity.ok(adminService.getVectorStatus());
    }

    @GetMapping("/qa-history")
    public ResponseEntity<List<QaHistoryDto>> getQaHistory() {
        return ResponseEntity.ok(adminService.getQaHistory());
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(adminService.getAnalytics());
    }
}
