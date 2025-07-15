package com.example.fixit.controller;

import com.example.fixit.model.Comment;
import com.example.fixit.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    //  TODO
}