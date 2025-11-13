package com.example.fixit.controller;

import com.example.fixit.dto.response.MessageDTO;
import com.example.fixit.dto.request.MessageRequest;
import com.example.fixit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send/{chatId}")
    public void sendMessage(
            @DestinationVariable int chatId,
            @Payload MessageRequest request,
            Principal principal) {
        if (principal == null) {    //  send error message to user
            messagingTemplate.convertAndSendToUser("admin", "/queue/errors", "Unauthorized access");
            return;
        }
        ResponseEntity<MessageDTO> savedMessage = messageService.saveMessage(chatId, request.getContent(), principal.getName(), request.getImageUrls());
        if (savedMessage.getStatusCode() == HttpStatus.OK && savedMessage.getBody() != null) {
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage.getBody());
        } else { // send back an error
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", "Failed to send message: " + savedMessage.getStatusCode());
        }
    }
}
