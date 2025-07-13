package com.example.safeher.controller;

import com.example.safeher.entity.ChatSession;
import com.example.safeher.entity.ChatMessage;
import com.example.safeher.service.ChatHistoryService;
import com.example.safeher.service.GeminiService;
import com.example.safeher.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatBotController {

    private final GeminiService geminiService;
    private final RouteService routeService;
    private final ChatHistoryService chatHistoryService;

    public ChatBotController(GeminiService geminiService, RouteService routeService, ChatHistoryService chatHistoryService) {
        this.geminiService = geminiService;
        this.routeService = routeService;
        this.chatHistoryService = chatHistoryService;
    }

    /**
     * Creates a new chat session and returns the session ID.
     */
    @PostMapping("/session")
    public ResponseEntity<Map<String, String>> createSession(@RequestHeader("X-USER-UID") String uid) {
        ChatSession session = chatHistoryService.createSession(uid);
        return ResponseEntity.ok(Map.of("sessionId", session.getSessionId()));
    }

    /**
     * Sends a message in a session and returns bot response.
     */
    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> sendMessage(
            @RequestHeader("X-USER-UID") String uid,
            @RequestBody Map<String, String> body
    ) {
        String sessionId = body.get("sessionId");
        String message = body.get("message");

        if (sessionId == null || sessionId.isBlank()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing sessionId")));
        }

        if (message == null || message.isBlank()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty")));
        }

        // Save user message
        chatHistoryService.saveMessage(uid, sessionId, message, true);

        return geminiService.generateResponse(message)
                .map(botReply -> {
                    chatHistoryService.saveMessage(uid, sessionId, botReply, false);
                    return ResponseEntity.ok(Map.of("response", botReply));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
                        .body(Map.of("response", "Error: " + e.getMessage()))));
    }

    /**
     * Fetches chat sessions for a user.
     */
    @GetMapping("/history/sessions")
    public ResponseEntity<List<ChatSession>> getChatSessions(@RequestHeader("X-USER-UID") String uid) {
        return ResponseEntity.ok(chatHistoryService.listSessions(uid));
    }

    /**
     * Fetches messages for a specific session.
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getSessionMessages(
            @RequestHeader("X-USER-UID") String uid,
            @PathVariable String sessionId
    ) {
        return ResponseEntity.ok(chatHistoryService.getMessages(uid, sessionId));
    }

    /**
     * Fetches safe route information (moved to /api/route endpoint).
     */
    @GetMapping("/route")
    public Mono<ResponseEntity<String>> getRoute(@RequestParam String start, @RequestParam String end) {
        return routeService.getRoute(start, end).map(ResponseEntity::ok);
    }
}
