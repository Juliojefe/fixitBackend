package com.example.fixit.controller;

import com.example.fixit.dto.request.CreatePostRequestImages;
import com.example.fixit.dto.request.CreatePostRequestUrl;
import com.example.fixit.dto.response.PostSummary;
import com.example.fixit.exception.UnauthorizedException;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<PostSummary> getPostById(@PathVariable("id") int postId) {
        return ResponseEntity.ok(postService.getPostSummaryById(postId));
    }

    @GetMapping("/all-ids")
    public ResponseEntity<Set<Integer>> getAllPostIds() {
        return ResponseEntity.ok(postService.getAllPostIds());
    }

    @GetMapping("/following")
    public ResponseEntity<List<Integer>> getFollowingPostIds(Principal principal) {
        // postIds of post created by those who a user follows sorted
        User user = getUserFromPrincipalOrThrow(principal);
        return ResponseEntity.ok(postService.getFollowingPostIds(user));
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<Set<Integer>> getOwnedPostByUserId(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(postService.getOwnedPostByUserId(userId));
    }

    @GetMapping("/liked/{userId}")
    public ResponseEntity<Set<Integer>> getLikedPostByUserId(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(postService.getLikedPostByUserId(userId));
    }

    @GetMapping("/saved")
    public ResponseEntity<Set<Integer>> getSavedPostByUserId(Principal principal) {
        User user = getUserFromPrincipalOrThrow(principal);
        return ResponseEntity.ok(postService.getSavedPostsByUserId(user.getUserId()));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> likePost(@PathVariable int postId, Principal principal) {
        User user = getUserFromPrincipalOrThrow(principal);
        postService.likePost(postId, user.getUserId());
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Boolean> unlikePost(@PathVariable int postId, Principal principal) {
        User user = getUserFromPrincipalOrThrow(principal);
        postService.unlikePost(postId, user.getUserId());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/{postId}/save")
    public ResponseEntity<Boolean> savePost(@PathVariable int postId, Principal principal) {
        User user = getUserFromPrincipalOrThrow(principal);
        postService.savePost(postId, user.getUserId());
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{postId}/save")
    public ResponseEntity<Boolean> unSavePost(@PathVariable int postId, Principal principal) {
        User user = getUserFromPrincipalOrThrow(principal);
        postService.unSavePost(postId, user.getUserId());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/create/urls")
    public ResponseEntity<PostSummary> createPost(@RequestBody CreatePostRequestUrl request, Principal principal) {
        User user = getUserFromPrincipalOrThrow(principal);
        return ResponseEntity.ok(postService.createPost(request, user.getUserId()));
    }

    @PostMapping(value = "/create/images", consumes = { "multipart/form-data" })
    public ResponseEntity<PostSummary> createPost(
            @RequestParam("description") String description,
            @RequestParam("createdAt") String createdAt,
            @RequestParam(value = "requestImages", required = false) List<MultipartFile> images,
            Principal principal
    ) throws IOException {
        List<MultipartFile> imageList = images != null ? images : new ArrayList<>();    //  empty?

        CreatePostRequestImages request = new CreatePostRequestImages(description, Instant.parse(createdAt), imageList);
        User user = getUserFromPrincipalOrThrow(principal);
        return ResponseEntity.ok(postService.createPost(request, user.getUserId()));
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

    private User getUserFromPrincipalOrThrow(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

}