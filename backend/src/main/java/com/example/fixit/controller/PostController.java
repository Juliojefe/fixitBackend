package com.example.fixit.controller;

import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.service.PostService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public PostSummary getPostById(@PathVariable("id") int postId) {
        return postService.getPostSummaryById(postId);
    }

    @GetMapping("/")
    public Set<PostSummary> getALlPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/all-ids")
    public Set<Integer> getAllPostIds() {
        return postService.getAllPostIds();
    }

    @GetMapping("/owned-post-by-userId/{userId}")
    public Set<Integer> getOwnedPostByUserId(@PathVariable("userId") int userId) {
        return postService.getOwnedPostByUserId(userId);
    }

    @GetMapping("/liked-post-by-userId/{userId}")
    public Set<Integer> getLikedPostByUserId(@PathVariable("userId") int userId) {
        return postService.getLikedPostByUserId(userId);
    }

    @GetMapping("/saved-post-by-userId/{userId}")
    public Set<Integer> getSavedPostByUserId(@PathVariable("userId") int userId) {
        return postService.getSavedPostsByUserId(userId);
    }
}