package com.example.fixit.controller;

import com.example.fixit.dto.response.MessageDTO;
import com.example.fixit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.fixit.dto.request.MessageRequest;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // For broadcasting via WebSocket

    /**
     * Handles sending a message to a specific chat via HTTP.
     * <p>
     * This route is primarily for fallback or testing purposes. In most cases,
     * messages should be sent through ChatWebSocketController's sendMessage
     * which uses WebSocket communication and internally calls messageService.saveMessage().
     *
     * @param chatId    the ID of the chat to send the message to
     * @param request   the message content and optional image URLs
     * @param principal the authenticated user sending the message
     * @return          an HTTP status code representing the result of the operation
     */

    @PostMapping("/{chatId}")
    public ResponseEntity<Void> sendMessage(@PathVariable int chatId, @RequestBody MessageRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseEntity<MessageDTO> savedMessage = messageService.saveMessage(chatId, request.getContent(), principal.getName(), request.getImageUrls());
        if (savedMessage.getStatusCode() == HttpStatus.OK && savedMessage.getBody() != null) {
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage.getBody());
        }
        return ResponseEntity.status(savedMessage.getStatusCode()).build();
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
        return messageService.getMessagesByChatId(chatId, page, size);
    }
}