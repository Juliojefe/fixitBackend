package com.example.fixit.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.PostImageRepository;

@Service
public class PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;
}