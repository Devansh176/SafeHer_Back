package com.example.safeher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private boolean isUser;
    private String sessionId;
    private String uid;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime timestamp;
}
