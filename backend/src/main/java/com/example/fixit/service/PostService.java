package com.example.fixit.service;

import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.PostRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostSummary getPostSummaryById(int postId) {
        try {
            Optional<Post> OptPost = postRepository.findById(postId);
            if (OptPost.isPresent()) {
                Post p = OptPost.get();
                return new PostSummary(p);
            } else {
                return new PostSummary();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}