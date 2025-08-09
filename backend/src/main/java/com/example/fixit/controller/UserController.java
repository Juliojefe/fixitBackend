package com.example.fixit.controller;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<GetUserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public GetUserResponse getuserById(@PathVariable("id") int userId) {
        //  verbose response containing all user details
        return userService.getuserById(userId);
    }

    @GetMapping("/summary/{id}")
    public UserSummary getuserSummaryById(@PathVariable("id") int userId) {
        //  summary response containing only name and pfp
        return userService.getuserSummaryById(userId);
    }

    @GetMapping("/all-ids")
    public ResponseEntity<List<Integer>> getAllUserIds() {
        return userService.getAllUserIds();
    }

    @PatchMapping("/update-name/{id}/name")
    public boolean updateUserName(@RequestBody UpdateNameRequest request) {
        return userService.updateName(request);
    }

    @PatchMapping("/update-email/{id}/email")
    public boolean updateEmail(@RequestBody UpdateEmailRequest request) {
        return userService.updateEmail(request);
    }

    @PatchMapping("/update-password/{id}/password")
    public boolean updatePassword(@RequestBody UpdatePasswordRequest request) {
        return userService.updatePassword(request);
    }

    @PatchMapping("/make-admin/{id}")
    public boolean makeAdmin(@RequestBody int requestUserId) {
        return userService.makeAdmin(requestUserId);
    }

    @PatchMapping("/make-mechanic/{id}")
    public boolean makeMechanic(@RequestBody int requestUserId) {
        return userService.makeMechanic(requestUserId);
    }

    @PatchMapping("/make-regular-user/{id}")
    public boolean makeRegularUser(@RequestBody int requestUserId) {
        return userService.makeRegularUser(requestUserId);
    }

    @PatchMapping("/update-profile-pic/{id}/url-new-pic")
    public boolean updateProfilePic(@RequestBody UpdateProfilePicRequest request) {
        return userService.updateProfilePic(request);
    }

    @DeleteMapping("/delete-user/{id}")
    public boolean deleteUser(@RequestBody int requestUserId) {
        return userService.deleteUser(requestUserId);
    }
}