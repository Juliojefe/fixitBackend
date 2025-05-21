package com.example.fixit.controller;

import com.example.fixit.model.Chat;
import com.example.fixit.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    //  TODO
}