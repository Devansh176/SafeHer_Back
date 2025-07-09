package com.example.safeher.controller;

import com.example.safeher.model.ChatRequest;
import com.example.safeher.model.ChatResponse;
import com.example.safeher.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
public class ChatBotController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chatbot")
    public ChatResponse handleChat(@RequestBody ChatRequest request) {
        String response = chatService.askGPT(request.getMessage());
        return new ChatResponse(response);
    }
}
