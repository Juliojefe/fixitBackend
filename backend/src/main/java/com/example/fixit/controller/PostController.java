package com.example.fixit.controller;

import com.example.fixit.dto.CreatePostRequestImages;
import com.example.fixit.dto.CreatePostRequestUrl;
import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> likePost(@PathVariable int postId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return postService.likePost(postId, userOpt.get().getUserId());
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Boolean> unlikePost(@PathVariable int postId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return postService.unlikePost(postId, userOpt.get().getUserId());
    }

    @PostMapping("/{postId}/save")
    public ResponseEntity<Boolean> savePost(@PathVariable int postId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return postService.savePost(postId, userOpt.get().getUserId());
    }

    @DeleteMapping("/{postId}/save")
    public ResponseEntity<Boolean> unSavePost(@PathVariable int postId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return postService.unSavePost(postId, userOpt.get().getUserId());
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

    //  Helper method to safely extract the user ID from the Authentication object.
    private int getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated.");
        }
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();
            return jwt.getClaim("userId");
        }
        throw new IllegalArgumentException("Cannot determine user ID from authentication token.");
    }

    private Optional<User> getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(principal.getName());
    }

}