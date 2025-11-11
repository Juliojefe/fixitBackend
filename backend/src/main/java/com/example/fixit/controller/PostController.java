package com.example.fixit.controller;

import com.example.fixit.dto.request.CreatePostRequestImages;
import com.example.fixit.dto.request.CreatePostRequestUrl;
import com.example.fixit.dto.response.PostSummary;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.PostService;
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

    @GetMapping("/all-ids")
    public ResponseEntity<Set<Integer>> getAllPostIds() {
        return postService.getAllPostIds();
    }

    @GetMapping("/following")
    public ResponseEntity<List<Integer>> getFollowingPostIds(Principal principal) {
	    //	postIds of post created by those who a user follows sorted
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return postService.getFollowingPostIds(userOpt.get());
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<Set<Integer>> getOwnedPostByUserId(@PathVariable("userId") int userId) {
        return postService.getOwnedPostByUserId(userId);
    }

    @GetMapping("/liked/{userId}")
    public ResponseEntity<Set<Integer>> getLikedPostByUserId(@PathVariable("userId") int userId) {
        return postService.getLikedPostByUserId(userId);
    }

    @GetMapping("/saved")
    public ResponseEntity<Set<Integer>> getSavedPostByUserId(Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return postService.getSavedPostsByUserId(userOpt.get().getUserId());
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

    @PostMapping("/create/urls")
    public ResponseEntity<PostSummary> createPost(@RequestBody CreatePostRequestUrl request, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return postService.createPost(request, userOpt.get().getUserId());
    }

    @PostMapping(value = "/create/images", consumes = { "multipart/form-data" })
    public ResponseEntity<PostSummary> createPost(
            @RequestParam("description") String description,
            @RequestParam("createdAt") String createdAt,
            @RequestParam(value = "requestImages", required = false) List<MultipartFile> images,
            Principal principal
    ) {
        List<MultipartFile> imageList = images != null ? images : new ArrayList<>();    //  empty?

        CreatePostRequestImages request = new CreatePostRequestImages(description, Instant.parse(createdAt), imageList);
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return postService.createPost(request, userOpt.get().getUserId());
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
