package com.example.fixit.controller;

import com.example.fixit.dto.request.ChatCreateRequest;
import com.example.fixit.dto.response.ChatSummary;
import com.example.fixit.model.Chat;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ChatSummary> getChatById(@PathVariable("id") int chatId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Optional: Check if user is in the chat
        return ResponseEntity.ok(chatService.getChatSummaryById(chatId));
    }

    @PostMapping("/create")
    public ResponseEntity<ChatSummary> createChat(@RequestBody ChatCreateRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Chat newChat = chatService.createChat(request.getName(), currentUser, request.getUserIds());
        return ResponseEntity.ok(new ChatSummary(newChat));
    }

    @GetMapping("/user")
    public ResponseEntity<Set<ChatSummary>> getUserChats(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(chatService.getChatsForUser(principal.getName()));
    }
}
