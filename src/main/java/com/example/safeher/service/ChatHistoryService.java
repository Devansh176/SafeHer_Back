package com.example.safeher.service;

import com.example.safeher.entity.ChatMessage;
import com.example.safeher.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatHistoryService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    // Save any message (user or bot)
    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    // Get all messages for a specific Firebase UID in order
    public List<ChatMessage> getMessagesByUser(String uid) {
        return chatMessageRepository.findByUidOrderByTimestampAsc(uid);
    }
}
