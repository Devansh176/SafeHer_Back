package com.example.safeher.service;

import com.example.safeher.entity.ChatMessage;
import com.example.safeher.entity.ChatSession;
import com.example.safeher.repository.ChatMessageRepository;
import com.example.safeher.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatHistoryService {

    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;

    public ChatHistoryService(ChatSessionRepository sessionRepo, ChatMessageRepository messageRepo) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
    }

    /**
     * Creates a new chat session for the given user UID.
     */
    public ChatSession createSession(String uid) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = ChatSession.builder()
                .sessionId(sessionId)
                .uid(uid)
                .createdAt(LocalDateTime.now())
                .title("Chat on " + LocalDateTime.now().toLocalDate())
                .build();
        sessionRepo.save(session);
        return session;
    }

    /**
     * Saves a chat message under a given session ID.
     */
    public void saveMessage(String uid, String sessionId, String text, boolean isUser) {
        ChatMessage message = ChatMessage.builder()
                .uid(uid)
                .sessionId(sessionId)
                .text(text)
                .isUser(isUser)
                .timestamp(LocalDateTime.now())
                .build();
        messageRepo.save(message);
    }

    /**
     * Lists all chat sessions for the user (newest first).
     */
    public List<ChatSession> listSessions(String uid) {
        return sessionRepo.findByUidOrderByCreatedAtDesc(uid);
    }

    /**
     * Retrieves messages for a specific session.
     */
    public List<ChatMessage> getMessages(String uid, String sessionId) {
        return messageRepo.findByUidAndSessionIdOrderByTimestampAsc(uid, sessionId);
    }
}
