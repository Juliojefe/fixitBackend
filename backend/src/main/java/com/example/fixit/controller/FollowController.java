package com.example.fixit.controller;

import com.example.fixit.dto.response.MutualFollowResponse;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/mutual/{userBId}")
    public ResponseEntity<MutualFollowResponse> checkMutualFollow(@PathVariable int userBId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MutualFollowResponse());
        }
        return followService.checkMutualFollow(userOpt.get().getUserId(), userBId);
    }

    @DeleteMapping("/{unfollowUserId}")
    public ResponseEntity<Boolean> unfollow(@PathVariable int unfollowUserId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return followService.unfollow(userOpt.get().getUserId(), unfollowUserId);
    }

    @PostMapping("/{followUserId}")
    public ResponseEntity<Boolean> follow(@PathVariable int followUserId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return followService.follow(userOpt.get().getUserId(), followUserId);
    }

    @DeleteMapping("/remove-follower/{removeFollowerId}")
    public ResponseEntity<Boolean> removeFollower(@PathVariable int removeFollowerId, Principal principal) {
        Optional<User> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return followService.removeFollower(userOpt.get().getUserId(), removeFollowerId);
    }

    private Optional<User> getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(principal.getName());
    }
}