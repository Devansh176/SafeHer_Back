package com.example.safeher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final List<Map<String, Object>> chatHistory;

    private static final String systemPrompt = """
        You are SafeHer, a helpful assistant for the SafeHer women's safety app.

        Your job is to answer user queries about how to use the app, explain the features, and guide users to stay safe using the app tools.

        💡 App Features:
        1. 🚨 Emergency Alert Button – Plays a loud siren to attract attention in danger.
        2. 📞 Quick Call – Call emergency services or trusted personal contacts.
        3. 📍 Share Location – Send your GPS location via SMS to saved contacts.
        4. 🗺️ Safe Route Suggestion – Uses Google Maps to suggest safe driving routes.
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

    private final RouteService routeService;

    public GeminiService(
            @Value("${GEMINI_API_URL}") String apiUrl,
            @Value("${GEMINI_API_KEY}") String apiKey, RouteService routeService) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.routeService = routeService;

        this.chatHistory = new ArrayList<>();
        this.chatHistory.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", systemPrompt))
        ));
    }

    public Mono<String> generateResponse(String userPrompt) {
        if (userPrompt.toLowerCase().contains("route from") && userPrompt.toLowerCase().contains("to")) {
            try {
                String cleaned = userPrompt.toLowerCase().replace("give me the safest route from", "").trim();
                String[] parts = cleaned.split("to");
                if (parts.length < 2) {
                    return Mono.just("Please provide a valid query like: 'Give me the safest route from A to B'");
                }

                String start = parts[0].trim();
                String end = parts[1].trim();

                return routeService.getSafestRoute(start, end)
                        .map(json -> {
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode root = mapper.readTree(json);
                                JsonNode route = root.path("routes").get(0);
                                JsonNode leg = route.path("legs").get(0);

                                String distance = leg.path("distance").path("text").asText("");
                                String duration = leg.path("duration").path("text").asText("");
                                String summary = route.path("summary").asText("");

                                if (distance.isEmpty() || duration.isEmpty()) {
                                    return "Could not extract route information. Please check your input or try again.";
                                }

                                return "🚣️ The safest route from " + start + " to " + end + " takes approximately " +
                                        duration + ", covering about " + distance +
                                        ". Recommended via " + (summary.isEmpty() ? "the optimal route" : summary) + ".";
                            } catch (Exception e) {
                                e.printStackTrace();
                                return "I couldn't process the route correctly. Please try again.";
                            }
                        });
            } catch (Exception e) {
                return Mono.just("Please provide a valid query like: 'Give me the safest route from A to B'");
            }
        }

        chatHistory.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userPrompt))
        ));

        Map<String, Object> request = Map.of("contents", chatHistory);

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
                        Map candidate = ((List<Map>) response.get("candidates")).get(0);
                        Map content = (Map) candidate.get("content");
                        List<Map> parts = (List<Map>) content.get("parts");
                        String botResponse = parts.get(0).get("text").toString();

                        chatHistory.add(Map.of(
                                "role", "model",
                                "parts", List.of(Map.of("text", botResponse))
                        ));

                        if (chatHistory.size() > 20) {
                            chatHistory.subList(2, 6).clear();
                        }

                        return botResponse;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Could not parse Gemini response.";
                    }
                });
    }
}