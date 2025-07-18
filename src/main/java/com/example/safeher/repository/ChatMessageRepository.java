package com.example.safeher.repository;

import com.example.safeher.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByUidAndSessionIdOrderByTimestampAsc(String uid,String sessionId);
}
