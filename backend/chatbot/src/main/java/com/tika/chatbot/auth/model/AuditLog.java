package com.tika.chatbot.auth.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(name = "admin_action", nullable = false)
    private String adminAction;

    @Column(name = "target_user_id")
    private UUID targetUserId;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    public AuditLog() {}

    public AuditLog(UUID adminId, String adminAction, UUID targetUserId) {
        this.adminId = adminId;
        this.adminAction = adminAction;
        this.targetUserId = targetUserId;
        this.dateTime = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getAdminId() { return adminId; }
    public void setAdminId(UUID adminId) { this.adminId = adminId; }
    public String getAdminAction() { return adminAction; }
    public void setAdminAction(String adminAction) { this.adminAction = adminAction; }
    public UUID getTargetUserId() { return targetUserId; }
    public void setTargetUserId(UUID targetUserId) { this.targetUserId = targetUserId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}
