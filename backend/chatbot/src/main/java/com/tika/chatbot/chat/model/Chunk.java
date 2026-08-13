package com.tika.chatbot.chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chunks")
public class Chunk {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @Column(name = "chunk_text", columnDefinition = "TEXT")
    private String chunkText;

    @Column(name = "source_page")
    private Integer sourcePage;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public UUID getDocumentId() { return documentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}