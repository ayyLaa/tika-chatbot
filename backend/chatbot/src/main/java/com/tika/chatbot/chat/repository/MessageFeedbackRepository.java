package com.tika.chatbot.chat.repository;

import com.tika.chatbot.chat.model.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, UUID> {

    @Query(value = """
        SELECT ROUND(100.0 * SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 0)
        FROM message_feedback
        """, nativeQuery = true)
    Double satisfactionPercentage();

    @Query(value =
            "SELECT mf.id, mf.created_at, u.email, m.answer, mf.comment, mf.rating " +
                    "FROM message_feedback mf " +
                    "JOIN messages m ON mf.message_id = m.id " +
                    "JOIN chat_sessions cs ON m.session_id = cs.id " +
                    "JOIN users u ON cs.user_id = u.id " +
                    "WHERE mf.rating = 0 " + // <--- Uslov za negativne je vraćen
                    "ORDER BY mf.created_at DESC",
            nativeQuery = true)
    List<Object[]> findAllDislikeFeedback();

    @Query(value =
            "SELECT mf.id, mf.created_at, u.email, m.answer, mf.comment, mf.rating " +
                    "FROM message_feedback mf " +
                    "JOIN messages m ON mf.message_id = m.id " +
                    "JOIN chat_sessions cs ON m.session_id = cs.id " +
                    "JOIN users u ON cs.user_id = u.id " +
                    "ORDER BY mf.created_at DESC",
            nativeQuery = true)
    List<Object[]> findAllFeedbacksWithDetails();
}