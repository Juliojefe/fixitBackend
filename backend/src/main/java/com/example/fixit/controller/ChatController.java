package com.example.fixit.controller;

import com.example.fixit.dto.ChatSummary;
import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Chat;
import com.example.fixit.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/{id}")
    public ChatSummary getChatById(@PathVariable("id") int chatId) {
        return chatService.getChatSummaryById(chatId);
    }

    @GetMapping("/")
    public Set<ChatSummary> getAllChats() {
        return chatService.getAllChats();
    }
}