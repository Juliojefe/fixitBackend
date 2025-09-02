package com.example.fixit.controller;

import com.example.fixit.dto.MessageDTO;
import com.example.fixit.dto.MessageRequest;
import com.example.fixit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (principal == null) {
            return; // Handled by security config
        }
        MessageDTO savedMessage = messageService.saveMessage(chatId, request.getContent(), principal.getName());
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage);
    }
}
