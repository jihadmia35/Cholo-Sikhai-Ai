package com.cholosikhai.cholosikhaiai.controllers;

import com.cholosikhai.cholosikhaiai.service.ChatService;
import io.opencensus.resource.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Flux<String>> chat(@RequestParam String query) {

        return ResponseEntity.ok(chatService.getResponse(query));
    }

}
