package com.example.fixit.controller;

import com.example.fixit.dto.CreatePostRequestImages;
import com.example.fixit.dto.CreatePostRequestUrl;
import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostSummary> getPostById(@PathVariable("id") int postId) {
        return postService.getPostSummaryById(postId);
    }

    @GetMapping("/")
    public Set<PostSummary> getALlPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/all-ids")
    public ResponseEntity<Set<Integer>> getAllPostIds() {
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

    @PostMapping("/like-post/{postId}/{userId}")
    public ResponseEntity<Boolean> likePost(@PathVariable("postId") int postId, @PathVariable("userId") int userId) {
        return postService.likePost(postId, userId);
    }

    @PostMapping("/save-post/{postId}/{userId}")
    public boolean savePost(@PathVariable("postId") int postId, @PathVariable("userId") int userId) {
        return postService.savePost(postId, userId);
    }

    @DeleteMapping("/unlike-post/{postId}/{userId}")
    public boolean unlikePost(@PathVariable("postId") int postId, @PathVariable("userId") int userId) {
        return postService.unlikePost(postId, userId);
    }

    @DeleteMapping("/unSave-post/{postId}/{userId}")
    public boolean unSavePost(@PathVariable("postId") int postId, @PathVariable("userId") int userId) {
        return postService.unSavePost(postId, userId);
    }

    @PostMapping("/create-post-urls")
    public PostSummary createPost(@RequestBody CreatePostRequestUrl request) {
        return postService.createPost(request);
    }

    @PostMapping(value = "/create-post-images", consumes = { "multipart/form-data" })
    public PostSummary createPost(
            @RequestParam("description") String description,
            @RequestParam("user_id") int userId,
            @RequestParam("createdAt") String createdAt,
            @RequestParam(value = "requestImages", required = false) List<MultipartFile> images
    ) {
        List<MultipartFile> imageList = images != null ? images : new ArrayList<>();    //  empty?

        CreatePostRequestImages request = new CreatePostRequestImages(
                description,
                userId,
                Instant.parse(createdAt),
                imageList
        );
        return postService.createPost(request);
    }

}