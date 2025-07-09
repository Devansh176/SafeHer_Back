package com.example.safeher.controller;

import com.example.safeher.model.ChatRequest;
import com.example.safeher.model.ChatResponse;
import com.example.safeher.service.GeoRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatBotController {

    @Autowired
    private GeoRouteService geoRouteService;

    @PostMapping("/chatbot")
    public ChatResponse handleChat(@RequestBody ChatRequest request) {
        String message = request.getMessage().toLowerCase();

        if (message.contains("route") && message.contains("to")) {
            try {
                String[] parts = message.split("to");
                String from = parts[0].replace("route", "").trim();
                String to = parts[1].trim();

                String route = geoRouteService.extractRouteAndRespond(from, to);
                return new ChatResponse(route);
            } catch (Exception e) {
                return new ChatResponse("Sorry, I couldn’t understand the locations.");
            }
        }

        if (message.contains("sos")) {
            return new ChatResponse("SOS triggers emergency call, alert sound, and location sharing.");
        } else if (message.contains("call")) {
            return new ChatResponse("SafeHer allows you to quickly call your emergency contacts.");
        } else if (message.contains("alert")) {
            return new ChatResponse("Alert plays a loud sound to deter threats.");
        }

        return new ChatResponse("I can assist with SafeHer app features, SOS, route safety, or calling contacts.");
    }
}
