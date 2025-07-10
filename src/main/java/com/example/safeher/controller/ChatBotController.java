package com.example.safeher.controller;

import com.example.safeher.service.GeminiService;
import com.example.safeher.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatBotController {

    private final GeminiService geminiService;
    private final RouteService routeService;

    public ChatBotController(GeminiService geminiService, RouteService routeService) {
        this.geminiService = geminiService;
        this.routeService = routeService;
    }

    // 🧠 Gemini chatbot endpoint
    @PostMapping("/chat")
    public Mono<ResponseEntity<Map<String, String>>> chat(@RequestBody Map<String, String> request) {
        String prompt = request.get("message");
        return geminiService.generateResponse(prompt)
                .map(response -> ResponseEntity.ok(Map.of("response", response)));
    }

    // 🚗 OpenRouteService route endpoint
    @GetMapping("/route")
    public Mono<ResponseEntity<String>> getRoute(@RequestParam String start, @RequestParam String end) {
        return routeService.getRoute(start, end)
                .map(ResponseEntity::ok);
    }
}
