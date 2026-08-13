package com.tika.chatbot.chat.repository;

import com.tika.chatbot.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);

    @Query(value = "SELECT SUM(tokens_used) FROM messages WHERE created_at > now() - interval '30 days'", nativeQuery = true)
    Long sumTokensLast30Days();

    @Query(value = "SELECT AVG(response_time_ms) FROM messages WHERE created_at > now() - interval '30 days'", nativeQuery = true)
    Double avgResponseTimeLast30Days();

    @Query(value = """
        SELECT COUNT(DISTINCT cs.user_id) FROM chat_sessions cs
        JOIN messages m ON m.session_id = cs.id
        WHERE m.created_at > now() - interval '30 days'
        """, nativeQuery = true)
    long countActiveUsersLast30Days();

    @Query(value = """
        SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as day, COUNT(*) as msg_count
        FROM messages
        WHERE created_at > now() - interval '14 days'
        GROUP BY TO_CHAR(created_at, 'YYYY-MM-DD')
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> dailyMessageCountsLast14Days();
}