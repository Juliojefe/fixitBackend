package com.example.fixit.controller;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @GetMapping("/getAll")
    public Page<GetUserProfilePrivateResponse> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(PageRequest.of(page, size));
    }


    //  verbose response containing all user details (protected)
    @GetMapping("/{id}/profile/private")
    public ResponseEntity<GetUserProfilePrivateResponse> getuserById(@PathVariable("id") int userId) {
        return userService.getuserById(userId);
    }

    //  response containing only name and pfp
    @GetMapping("/summary/{id}")
    public UserNameAndPfp getuserSummaryById(@PathVariable("id") int userId) {
        return userService.getuserSummaryById(userId);
    }

    //  public profile access
    @GetMapping("/{id}/profile/public")
    public ResponseEntity<GetUserProfilePublicResponse> getUserProfileById(@PathVariable("id") int userId) {
        return userService.getUserProfileById(userId);
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