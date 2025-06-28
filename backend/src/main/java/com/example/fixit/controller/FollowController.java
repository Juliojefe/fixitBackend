package com.example.fixit.controller;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping("/mutual/{userAId}/{userBId}")
    public MutualFollowResponse checkMutualFollow(@PathVariable int userAId, @PathVariable int userBId) {
        return followService.checkMutualFollow(userAId, userBId);
    }
}