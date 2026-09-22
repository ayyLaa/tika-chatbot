package com.tika.chatbot.auth.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }


    @Column(name = "full_name", nullable = false)
    private String fullName;
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @Column(name = "username", nullable = false, unique = true)
    private String username;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Column(unique = true, nullable = false)
    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone_number")
    private String phoneNumber;
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Column(name = "department")
    private String department;
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    @Column(name = "user_role")
    private String userRole = "user"; // default
    public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Column(name = "is_active")
    private boolean isActive = true;
    public boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Column(name = "email_verified")
    private boolean emailVerified = false;
    public boolean isEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "token_expiry_date")
    private LocalDateTime tokenExpiryDate;

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getTokenExpiryDate() {
        return tokenExpiryDate;
    }

    public void setTokenExpiryDate(LocalDateTime tokenExpiryDate) {
        this.tokenExpiryDate = tokenExpiryDate;
    }
    @Column(name = "reset_status")
    private String resetStatus; // null, "pending", "approved", "rejected"

    @Column(name = "reset_requested_at")
    private LocalDateTime resetRequestedAt;

    // getters/setters
    public String getResetStatus() { return resetStatus; }
    public void setResetStatus(String resetStatus) { this.resetStatus = resetStatus; }
    public LocalDateTime getResetRequestedAt() { return resetRequestedAt; }
    public void setResetRequestedAt(LocalDateTime resetRequestedAt) { this.resetRequestedAt = resetRequestedAt; }

}
