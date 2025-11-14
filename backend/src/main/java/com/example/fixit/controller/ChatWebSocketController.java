package com.example.fixit.controller;

import com.example.fixit.dto.response.MessageDTO;
import com.example.fixit.dto.request.MessageRequest;
import com.example.fixit.exception.UnauthorizedException;
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

        if (principal == null) {
            sendError(principal, "Unauthorized");
            messagingTemplate.convertAndSend("/topic/errors", "Unauthorized access attempt");
            return;
        }
        try {
            MessageDTO message = messageService.saveMessage(
                    chatId,
                    request.getContent(),
                    principal.getName(),
                    request.getImageUrls()
            );
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
        } catch (UnauthorizedException e) {
            sendError(principal, "Access denied: " + e.getMessage());
        } catch (Exception e) {
            sendError(principal, "Failed to send message");
        }
    }

    private void sendError(Principal principal, String message) {
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                message
        );
    }
}
