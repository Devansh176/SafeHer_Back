package com.example.safeher.controller;

import com.example.safeher.model.ChatRequest;
import com.example.safeher.model.ChatResponse;
import com.example.safeher.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // <-- Important: This prefix MUST match frontend URL
@CrossOrigin(origins = "*")
public class ChatBotController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chatbot") // Final URL becomes: /api/chatbot
    public ChatResponse handleChat(@RequestBody ChatRequest request) throws Exception {
        String response = chatService.askGPT(request.getMessage());
        return new ChatResponse(response);
    }
}
