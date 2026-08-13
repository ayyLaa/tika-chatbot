package com.tika.chatbot.chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_feedback")
public class MessageFeedback {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "message_id")
    private UUID messageId;

    private Short rating;
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // getteri/setteri
    public UUID getId() { return id; }
    public UUID getMessageId() { return messageId; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }
    public Short getRating() { return rating; }
    public void setRating(Short rating) { this.rating = rating; }
}