//package com.example.safeher.controller;
//
//import com.example.safeher.model.ChatRequest;
//import com.example.safeher.model.ChatResponse;
//import com.example.safeher.service.ChatService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/chat")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*") // for Flutter
//public class ChatController {
//
//    private final ChatService chatService;
//
//    @PostMapping
//    public ChatResponse getChatResponse(@RequestBody ChatRequest request) {
//        return chatService.getResponse(request.getMessage());
//    }
//}
