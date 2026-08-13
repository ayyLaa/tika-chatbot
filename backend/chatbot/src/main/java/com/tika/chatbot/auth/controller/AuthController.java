package com.tika.chatbot.auth.controller;

import com.tika.chatbot.auth.dto.*;
import com.tika.chatbot.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<Void> acceptInvite(@Valid @RequestBody AcceptInviteRequest request) {
        authService.acceptInvite(request.token(), request.fullName(), request.password(),
                request.phoneNumber(), request.department());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/invite-details")
    public ResponseEntity<InviteDetailsResponse> getInviteDetails(@RequestParam String token) {
        return ResponseEntity.ok(authService.getInviteDetails(token));
    }

}
