package com.example.fixit.controller;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping("/mutual/{userAId}/{userBId}")
    public MutualFollowResponse checkMutualFollow(@PathVariable int userAId, @PathVariable int userBId) {
        return followService.checkMutualFollow(userAId, userBId);
    }

    @DeleteMapping("/{activeUserId}/unfollow/{unfollowUserId}")
    public boolean unfollow(@PathVariable int activeUserId, @PathVariable int unfollowUserId) {
        return followService.unfollow(activeUserId, unfollowUserId);
    }

    @PostMapping("/{activeUserId}/follow/{followUserId}")
    public boolean follow(@PathVariable int activeUserId, @PathVariable int followUserId) {
        return followService.follow(activeUserId, followUserId);
    }

    @DeleteMapping("/{activeUserId}/remove-follower/{removeFollowerId}")
    public boolean removeFollower(@PathVariable int activeUserId, @PathVariable int removeFollowerId) {
        return followService.removeFollower(activeUserId, removeFollowerId);
    }
}