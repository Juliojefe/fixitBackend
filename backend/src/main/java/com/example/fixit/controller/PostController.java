package com.example.fixit.controller;

import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public PostSummary getuserById(@PathVariable("id") int postId) {
        return postService.getPostSummaryById(postId);
    }
}