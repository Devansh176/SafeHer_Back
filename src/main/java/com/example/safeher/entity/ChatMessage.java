package com.example.safeher.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid; // Firebase UID of the user

    private boolean isUser;

    private String text; // The message content (user or bot)

    private LocalDateTime timestamp;
}
