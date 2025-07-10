package com.example.safeher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;

    public GeminiService(
            @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}") String apiUrl,
            @Value("${gemini.api.key:${GEMINI_API_KEY}}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<String> generateResponse(String prompt) {
        Map<String, Object> request = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    try {
                        Map<?, ?> candidate = ((Map<?, ?>) ((java.util.List<?>) response.get("candidates")).getFirst());
                        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                        java.util.List<?> parts = (java.util.List<?>) content.get("parts");
                        Map<?, ?> firstPart = (Map<?, ?>) parts.getFirst();
                        return firstPart.get("text").toString();
                    } catch (Exception e) {
                        return "Could not parse Gemini response.";
                    }
                });
    }
}
