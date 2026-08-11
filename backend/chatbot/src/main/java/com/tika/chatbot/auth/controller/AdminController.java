package com.tika.chatbot.auth.controller;

import com.tika.chatbot.auth.config.CustomUserDetails;
import com.tika.chatbot.auth.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("admin/")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentAdmin) {

        adminService.deactivateUser(userId, currentAdmin.getId());
        return ResponseEntity.noContent().build();
    }
}
