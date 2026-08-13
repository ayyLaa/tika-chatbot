package com.tika.chatbot.chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "doc_type", nullable = false)
    private String docType;

    @Column(name = "doc_path", nullable = false)
    private String docPath;

    @Column(name = "doc_status")
    private String docStatus = "pending";

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();


    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }
    public String getDocPath() { return docPath; }
    public void setDocPath(String docPath) { this.docPath = docPath; }
    public String getDocStatus() { return docStatus; }
    public void setDocStatus(String docStatus) { this.docStatus = docStatus; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}