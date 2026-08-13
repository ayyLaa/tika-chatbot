// chat/repository/DocumentRepository.java
package com.tika.chatbot.chat.repository;

import com.tika.chatbot.chat.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    long countByDocStatus(String status);

    @Query(value = """
        SELECT d.file_name, d.doc_status, COUNT(c.id) as chunk_count, MAX(c.created_at) as last_sync
        FROM documents d
        LEFT JOIN chunks c ON c.document_id = d.id
        GROUP BY d.id, d.file_name, d.doc_status
        ORDER BY d.uploaded_at DESC
        """, nativeQuery = true)
    List<Object[]> findDocumentStatusSummary();
}