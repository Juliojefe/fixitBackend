package com.example.fixit.controller;

import com.example.fixit.model.CommentImage;
import com.example.fixit.service.CommentImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comment-image")
public class CommentImageController {

    @Autowired
    private CommentImageService commentImageService;

    //  TODO
}