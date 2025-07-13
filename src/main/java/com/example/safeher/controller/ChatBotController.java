package com.example.safeher.controller;

import com.example.safeher.entity.ChatMessage;
import com.example.safeher.service.ChatHistoryService;
import com.example.safeher.service.GeminiService;
import com.example.safeher.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatBotController {

    private final GeminiService geminiService;
    private final RouteService routeService;
    private final ChatHistoryService chatHistoryService;

    public ChatBotController(GeminiService geminiService,
                             RouteService routeService,
                             ChatHistoryService chatHistoryService) {
        this.geminiService = geminiService;
        this.routeService = routeService;
        this.chatHistoryService = chatHistoryService;
    }

    // 💬 Chat endpoint: Stores both user and bot messages for a user
    @PostMapping("/chat")
    public Mono<ResponseEntity<Map<String, String>>> chat(
            @RequestHeader("X-USER-UID") String uid,
            @RequestBody Map<String, String> request) {

        String userMessage = request.get("message");

        // Save user's message
        chatHistoryService.saveMessage(ChatMessage.builder()
                .uid(uid)
                .text(userMessage)
                .isUser(true)
                .timestamp(LocalDateTime.now())
                .build());

        // Generate bot response and save it
        return geminiService.generateResponse(userMessage)
                .map(botResponse -> {
                    chatHistoryService.saveMessage(ChatMessage.builder()
                            .uid(uid)
                            .text(botResponse)
                            .isUser(false)
                            .timestamp(LocalDateTime.now())
                            .build());

                    return ResponseEntity.ok(Map.of("response", botResponse));
                })
                .onErrorResume(ex -> Mono.just(ResponseEntity
                        .status(500)
                        .body(Map.of("response", "An error occurred while generating a response."))));
    }

    // 📜 Endpoint to fetch chat history for a user by Firebase UID
    @GetMapping("/chat/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestHeader("X-USER-UID") String uid) {
        List<ChatMessage> history = chatHistoryService.getMessagesByUser(uid);
        return ResponseEntity.ok(history);
    }

    // 🗺️ Safe route suggestion
    @GetMapping("/route")
    public Mono<ResponseEntity<String>> getRoute(
            @RequestParam String start,
            @RequestParam String end) {
        return routeService.getRoute(start, end).map(ResponseEntity::ok);
    }
}
