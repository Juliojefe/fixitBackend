package com.example.fixit.controller;

import com.example.fixit.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/post-image")
public class PostImageController {

    @Autowired
    private PostImageService postImageService;

    //  TODO
}