package com.tika.chatbot.chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shared_chats")
public class SharedChat {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "target_user_email", nullable = false)
    private String targetUserEmail;

    // ADDED: Optional note
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "permission")
    private String permission = "READ_ONLY";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getTargetUserEmail() { return targetUserEmail; }
    public void setTargetUserEmail(String targetUserEmail) { this.targetUserEmail = targetUserEmail; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}