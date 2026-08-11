package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.dto.*;
import com.tika.chatbot.auth.exception.*;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional
    public void register(SignupRequest request) {
        String generatedUsername = generateUsername(request.fullName());

        if (userRepository.existsByUsername(generatedUsername)) {
            throw new UsernameAlreadyExistsException(generatedUsername);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setUsername(generatedUsername);
        user.setEmail(request.email());
        user.setPhoneNumber(request.phone_number());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDepartment(request.department());
        user.setUserRole("user");
        user.setEmailVerified(false);
        user.setIsActive(true);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }
    private String normalizeToUsername(String fullName) {
        return fullName.toLowerCase()
                .replaceAll("[čćç]", "c")
                .replaceAll("[šş]", "s")
                .replaceAll("[ž]", "z")
                .replaceAll("[ğ]", "g")
                .replaceAll("[đ]", "dj")
                .replaceAll("[ı]", "i")
                .replaceAll("[ö]", "o")
                .replaceAll("[ü]", "u")
                .replaceAll("\\s+", "");

    }
    private String generateUsername(String fullName) {
        String base = normalizeToUsername(fullName);
        String candidate = base;
        int counter = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + counter;
            counter++;
        }
        return candidate;
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Nevažeći ili istekao token za verifikaciju."));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException();
        }
        if (!user.getIsActive()) {
            throw new UserDeactivatedException();
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getUserRole());
        return new AuthResponse(token, user.getFullName(), user.getEmail(), user.getUserRole());
    }

    @Transactional
    public void requestPasswordReset(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpires(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Nevažeći token za reset lozinke."));

        if (user.getResetTokenExpires().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token je istekao. Zatražite novi reset lozinke.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpires(null);
        userRepository.save(user);
    }
}