package com.example.fixit.controller;

import com.example.fixit.model.MessageImage;
import com.example.fixit.service.MessageImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/message-image")
public class MessageImageController {

    @Autowired
    private MessageImageService messageImageService;

    //  TODO
}