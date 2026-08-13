package com.tika.chatbot.chat.repository;

import com.tika.chatbot.chat.model.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, UUID> {

    @Query(value = """
        SELECT ROUND(100.0 * SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 0)
        FROM message_feedback
        """, nativeQuery = true)
    Double satisfactionPercentage();
}