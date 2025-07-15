package com.example.fixit.controller;

import com.example.fixit.model.Message;
import com.example.fixit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;


    //  TODO
}