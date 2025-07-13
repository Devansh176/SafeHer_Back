package com.example.safeher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;

    // 🧠 Memory-based conversation storage
    private final List<Map<String, Object>> chatHistory;

    // ✨ SYSTEM PROMPT: SafeHer App HelpBot Instruction
    private static final String systemPrompt = """
        You are SafeHer, a helpful assistant for the SafeHer women's safety app.
        
        Your job is to answer user queries about how to use the app, explain the features, and guide users to stay safe using the app tools.

        💡 App Features:
        1. 🚨 Emergency Alert Button – Plays a loud siren to attract attention in danger.
        2. 📞 Quick Call – Call emergency services or trusted personal contacts.
        3. 📍 Share Location – Send your GPS location via SMS to saved contacts.
        4. 🗺️ Safe Route Suggestion – Uses OpenRouteService to suggest safe driving routes.
        5. 👥 Add Emergency Contacts – Add people to receive alerts or calls.
        6. The SOS button triggers a loud alert sound, sends your live location to all emergency contacts, and instantly calls the first contact — all in one tap.
        7. 💬 ChatBot (You) – Answer app-related questions & fetch safe routes.

        🛑 For unrelated queries (e.g., jokes, news, weather):
        "I'm here to help with the SafeHer app only. Please ask about app features or safety guidance."
        
        You can sometimes talk with them if they want to make themselves light-mood. Be like their buddy who listens to everything, but the main motive is to guide them. 
        You must always provide safety tips, guidance, and assist them in understanding how to use the SafeHer app effectively.
        Don't give any messages in bold or with stars. Refer to GoogleMaps for the route. If someone asks for the route, give them straightaway,
        don't take much time giving useless responses.
        """;

    public GeminiService(
            @Value("${GEMINI_API_URL}") String apiUrl,
            @Value("${GEMINI_API_KEY}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();

        // Initialize chat history with system prompt
        this.chatHistory = new ArrayList<>();
        this.chatHistory.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", systemPrompt))
        ));
    }

    public Mono<String> generateResponse(String userPrompt) {
        // Add new user message to chat history
        chatHistory.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userPrompt))
        ));

        // Build request
        Map<String, Object> request = Map.of(
                "contents", chatHistory
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Gemini API error: " + errorBody);
                                    return Mono.error(new RuntimeException("Gemini API returned error: " + errorBody));
                                })
                )
                .bodyToMono(Map.class)
                .map(response -> {
                    try {
                        Map candidate = ((List<Map>) response.get("candidates")).getFirst();
                        Map content = (Map) candidate.get("content");
                        List<Map> parts = (List<Map>) content.get("parts");
                        String botResponse = parts.getFirst().get("text").toString();

                        // Add bot response to history
                        chatHistory.add(Map.of(
                                "role", "model",
                                "parts", List.of(Map.of("text", botResponse))
                        ));

                        // Optional: Keep history size in check
                        if (chatHistory.size() > 20) {
                            chatHistory.subList(2, 6).clear(); // keep system prompt + recent messages
                        }

                        return botResponse;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Could not parse Gemini response.";
                    }
                });
    }
}
