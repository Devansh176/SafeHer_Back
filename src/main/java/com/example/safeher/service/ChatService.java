//package com.example.safeher.service;
//
//import com.example.safeher.model.ChatResponse;
//import com.theokanning.openai.completion.chat.ChatCompletionRequest;
//import com.theokanning.openai.completion.chat.ChatMessage;
//import com.theokanning.openai.completion.chat.ChatMessageRole;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ChatService {
//
//    private final OpenAiService openAiService;
//
//    public ChatService(@Value("${openai.api.key}") String apiKey) {
//        this.openAiService = new OpenAiService(apiKey);
//    }
//
//    public ChatResponse getResponse(String userMessage) {
//        ChatMessage systemPrompt = new ChatMessage(ChatMessageRole.SYSTEM.value(),
//                "You are SafeHer Assistant. Only answer questions related to the SafeHer app, its features, and safety routes using crime and traffic data. Reject anything else.");
//
//        ChatMessage userPrompt = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
//
//        ChatCompletionRequest request = ChatCompletionRequest.builder()
//                .model("gpt-3.5-turbo")
//                .messages(List.of(systemPrompt, userPrompt))
//                .temperature(0.7)
//                .maxTokens(300)
//                .build();
//
//        ChatCompletionResult result = openAiService.createChatCompletion(request);
//        String reply = result.getChoices().get(0).getMessage().getContent();
//
//        return new ChatResponse(reply);
//    }
//}