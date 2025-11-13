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
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ChatSummary> getChatSummaryById(@PathVariable("id") int chatId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ChatSummary());
        }
        Optional<User> optUser = userRepository.findByEmail(principal.getName());
        if (!optUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ChatSummary());
        }
        User currentUser = optUser.get();
        return chatService.getChatSummaryById(chatId, currentUser);
    }

    @PostMapping("/create")
    public ResponseEntity<ChatSummary> createChat(@RequestBody ChatCreateRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> optUser = userRepository.findByEmail(principal.getName());
        if (!optUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ChatSummary());
        }
        User currentUser = optUser.get();
        return chatService.createChat(request.getName(), currentUser, request.getUserIds());
    }

    @GetMapping("/user")
    public ResponseEntity<Set<ChatSummary>> getUserChats(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> optUser = userRepository.findByEmail(principal.getName());
        if (!optUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User currentUser = optUser.get();
        return chatService.getChatsForUser(currentUser);
    }
}
