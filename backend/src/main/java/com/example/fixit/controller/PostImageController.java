package com.example.fixit.controller;

import com.example.fixit.model.PostImage;
//import com.example.backend.services.PostImageService;
import com.example.fixit.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/post-image")
public class PostImageController {

    @Autowired
    private PostImageService postImageService;

}