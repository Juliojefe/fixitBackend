package com.example.fixit.controller;

import com.example.fixit.dto.ImageRequest;
import com.example.fixit.model.MessageImage;
import com.example.fixit.service.MessageImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/message-image")
public class MessageImageController {

    @Autowired
    private MessageImageService messageImageService;

    @PostMapping("/{messageId}")
    public ResponseEntity<MessageImage> addImageToMessage(@PathVariable int messageId, @RequestBody ImageRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Optional: Verify user owns the message
        MessageImage image = messageImageService.addImage(messageId, request.getImageUrl());
        return ResponseEntity.ok(image);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<List<MessageImage>> getImagesByMessage(@PathVariable int messageId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(messageImageService.getImagesByMessageId(messageId));
    }
}