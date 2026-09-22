package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.dto.*;
import com.tika.chatbot.auth.exception.*;
import com.tika.chatbot.auth.model.LoginHistory;
import com.tika.chatbot.auth.model.PasswordResetRequest;
import com.tika.chatbot.auth.model.User;
import com.tika.chatbot.auth.model.UserInvite;
import com.tika.chatbot.auth.repository.LoginHistoryRepository;
import com.tika.chatbot.auth.repository.PasswordResetRequestRepository;
import com.tika.chatbot.auth.repository.UserInviteRepository;
import com.tika.chatbot.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tika.chatbot.auth.dto.InviteDetailsResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserInviteRepository inviteRepository;
    private final PasswordResetRequestRepository resetRequestRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, EmailService emailService, UserInviteRepository inviteRepository, PasswordResetRequestRepository resetRequestRepository, LoginHistoryRepository loginHistoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.inviteRepository = inviteRepository;
        this.resetRequestRepository = resetRequestRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    public void acceptInvite(String token, String fullName, String password, String phoneNumber, String department) {
        UserInvite invite = inviteRepository.findByInviteTokenAndStatus(token, "pending")
                .orElseThrow(() -> new InvalidTokenException("Geçersiz veya süresi dolmuş davet."));

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Davetin süresi doldu.");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setUsername(generateUsername(fullName));
        user.setEmail(invite.getEmail());
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setDepartment(department);
        user.setUserRole(invite.getInvitedRole());
        user.setEmailVerified(true);
        user.setIsActive(true);


        userRepository.save(user);

        invite.setStatus("accepted");
        inviteRepository.save(invite);
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

        LoginHistory history = new LoginHistory();
        history.setUserId(user.getId());
        history.setTypeLogin("SUCCESS");
        history.setDateTime(LocalDateTime.now());
        loginHistoryRepository.save(history);
        return new AuthResponse(token, user.getFullName(), user.getEmail(), user.getUserRole());
    }


    @Transactional
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            PasswordResetRequest req = new PasswordResetRequest();


            req.setUserId(user.getId());
            req.setStatus("pending");


            resetRequestRepository.save(req);

        });
    }

    @Transactional
    public void approvePasswordReset(UUID requestId, UUID adminId) throws Exception {
        PasswordResetRequest req = resetRequestRepository.findById(requestId).orElseThrow();

        String token = UUID.randomUUID().toString();
        req.setResetToken(token);
        req.setTokenExpires(LocalDateTime.now().plusHours(1));
        req.setStatus("approved");
        req.setReviewedBy(adminId);
        req.setReviewedAt(LocalDateTime.now());
        resetRequestRepository.save(req);

        User user = userRepository.findById(req.getUserId()).orElseThrow();
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetRequest req = resetRequestRepository.findByResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Şifre sıfırlama için geçersiz token."));

        if (!"approved".equals(req.getStatus())) {
            throw new InvalidTokenException("Token zaten kullanıldı veya onaylanmadı.");
        }

        if (req.getTokenExpires().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token'ın süresi doldu. Yeni bir şifre sıfırlama talep edin.");
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException(req.getUserId()));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        req.setStatus("used");
        resetRequestRepository.save(req);
    }

    public InviteDetailsResponse getInviteDetails(String token) {
        UserInvite invite = inviteRepository.findByInviteTokenAndStatus(token, "pending")
                .orElseThrow(() -> new InvalidTokenException("Geçersiz veya süresi dolmuş davet."));
        return new InviteDetailsResponse(invite.getEmail(), invite.getInvitedRole());
    }
}