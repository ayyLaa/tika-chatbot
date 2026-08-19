package com.tika.chatbot.auth.controller;

import com.tika.chatbot.auth.config.CustomUserDetails;
import com.tika.chatbot.auth.dto.UserSummaryDto;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/colleagues")
    public ResponseEntity<List<UserSummaryDto>> getColleagues(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User currentUser = userRepository.findById(principal.getId()).orElse(null);

        if (currentUser == null || currentUser.getDepartment() == null) {
            return ResponseEntity.ok(List.of());
        }

        String myDept = currentUser.getDepartment().trim();

        List<UserSummaryDto> colleagues = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> u.getDepartment() != null && u.getDepartment().trim().equalsIgnoreCase(myDept))
                .map(u -> new UserSummaryDto(
                        u.getId(), u.getFullName(), u.getUsername(), u.getEmail(),
                        u.getDepartment(), u.getUserRole(), u.getIsActive(), null
                ))
                .toList();

        return ResponseEntity.ok(colleagues);
    }
}