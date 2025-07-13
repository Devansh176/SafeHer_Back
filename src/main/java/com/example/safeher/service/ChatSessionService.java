package com.example.safeher.service;

import com.example.safeher.entity.ChatSession;
import com.example.safeher.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ChatSessionService {
    @Autowired
    ChatSessionRepository sessionRepo;

    public ChatSession createSession(String uid) {
        ChatSession s = ChatSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .uid(uid)
                .title("Chat " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .createdAt(LocalDateTime.now())
                .build();
        return sessionRepo.save(s);
    }

    public List<ChatSession> getSessions(String uid) {
        return sessionRepo.findByUidOrderByCreatedAtDesc(uid);
    }
}
