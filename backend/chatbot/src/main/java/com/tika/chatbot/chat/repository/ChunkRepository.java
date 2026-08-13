package com.tika.chatbot.chat.repository;

import com.tika.chatbot.chat.model.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
}