package com.example.safeher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RouteService {

    private final WebClient webClient;

    public RouteService(
            @Value("${openrouteservice.api-key:${ORS_API_KEY}}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeader("Authorization", apiKey)
                .build();
    }

    public Mono<String> getRoute(String start, String end) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/directions/driving-car")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
