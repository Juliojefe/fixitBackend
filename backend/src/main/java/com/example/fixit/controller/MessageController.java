package com.example.fixit.controller;

import com.example.fixit.dto.MessageDTO;
import com.example.fixit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.fixit.dto.MessageRequest;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // For broadcasting via WebSocket

    @PostMapping("/{chatId}")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable int chatId, @RequestBody MessageRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MessageDTO savedMessage = messageService.saveMessage(chatId, request.getContent(), principal.getName());
        // Broadcast via WebSocket
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByChat(
            @PathVariable int chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(messageService.getMessagesByChatId(chatId, page, size));
    }
}