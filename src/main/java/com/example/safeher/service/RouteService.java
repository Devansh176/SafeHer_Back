// Updated RouteService.java
package com.example.safeher.service;

import com.example.safeher.crime.PolylineDecoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RouteService {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final SafetyScoringService scoringService;

    public RouteService(@Value("${GOOGLE_MAPS_API_KEY}") String apiKey,
                        SafetyScoringService scoringService) {
        this.apiKey = apiKey;
        this.scoringService = scoringService;
        this.webClient = WebClient.builder()
                .baseUrl("https://maps.googleapis.com/maps/api")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<String> getSafestRoute(String start, String end) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/directions/json")
                        .queryParam("origin", start)
                        .queryParam("destination", end)
                        .queryParam("alternatives", "true")
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode routes = root.path("routes");

                        JsonNode safest = null;
                        double bestScore = -1;

                        for (JsonNode route : routes) {
                            String polyline = route.path("overview_polyline").path("points").asText();
                            List<double[]> routePoints = PolylineDecoder.decode(polyline);
                            double score = scoringService.calculateSafetyScore(routePoints);

                            if (score > bestScore) {
                                bestScore = score;
                                safest = route;
                            }
                        }

                        if (safest != null) {
                            String duration = safest.path("legs").get(0).path("duration").path("text").asText();
                            String distance = safest.path("legs").get(0).path("distance").path("text").asText();
                            String summary = safest.path("summary").asText();

                            return Mono.just("The safest route from " + start + " to " + end +
                                    " takes approximately " + duration +
                                    ", covering about " + distance + ". Recommended via " + summary + ".");
                        } else {
                            return Mono.just("Unable to determine the safest route.");
                        }
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error processing Google Maps response", e));
                    }
                });
    }
}
