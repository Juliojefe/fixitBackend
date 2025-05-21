package com.example.fixit.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    //  TODO
}