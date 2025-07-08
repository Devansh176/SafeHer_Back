package com.example.safeher.controller;

import com.example.safeher.model.ChatRequest;
import com.example.safeher.model.ChatResponse;
import com.example.safeher.service.GeoRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatBotController {

    @Autowired
    private GeoRouteService geoRouteService;

    @Value("${geoapify.api.key}")
    private String apiKey;

    @PostMapping("/chatbot")
    public ChatResponse handleChat(@RequestBody ChatRequest request) {
        String message = request.getMessage().toLowerCase().trim();

        try {
            if (message.contains("route")) {
                String response = geoRouteService.extractRouteAndRespond(message);
                return new ChatResponse(response);
            } else if (message.contains("sos")) {
                return new ChatResponse("SOS triggers emergency call, alert sound, and location sharing.");
            } else if (message.contains("call")) {
                return new ChatResponse("SafeHer allows you to quickly call your emergency contacts.");
            } else if (message.contains("alert")) {
                return new ChatResponse("Alert plays a loud sound to deter threats.");
            }
        } catch (Exception e) {
            return new ChatResponse("Something went wrong while processing your request.");
        }

        return new ChatResponse("I can assist with SafeHer app features, SOS, safe routes, or calling contacts.");
    }
}
