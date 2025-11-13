package com.example.fixit.controller;

import com.example.fixit.dto.request.UpdateEmailRequest;
import com.example.fixit.dto.request.UpdateNameRequest;
import com.example.fixit.dto.request.UpdatePasswordRequest;
import com.example.fixit.dto.request.UpdateProfilePicRequest;
import com.example.fixit.dto.response.GetUserProfilePrivateResponse;
import com.example.fixit.dto.response.GetUserProfilePublicResponse;
import com.example.fixit.dto.response.UserNameAndPfp;
import com.example.fixit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    //  private response containing all users (pageable)
    @GetMapping("/getAll")
    public ResponseEntity<Page<GetUserProfilePrivateResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsersPrivate(PageRequest.of(page, size));
    }

    //  private verbose response containing all user details
    @GetMapping("/{id}/profile/private")
    public ResponseEntity<GetUserProfilePrivateResponse> getUserById(@PathVariable("id") int userId) {
        return userService.getUserProfilePrivateById(userId);
    }

    //  public response containing only name and pfp
    @GetMapping("/{id}/name-and-pfp")
    public ResponseEntity<UserNameAndPfp> getUserNameAndPfpById(@PathVariable("id") int userId) {
        return userService.getUserNameAndPfpById(userId);
    }

    //  public profile access
    @GetMapping("/{id}/profile/public")
    public ResponseEntity<GetUserProfilePublicResponse> getUserProfileById(@PathVariable("id") int userId) {
        return userService.getUserProfileById(userId);
    }

    @GetMapping("/all-ids")
    public ResponseEntity<Page<Integer>> getAllUserIds(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUserIds(PageRequest.of(page, size));
    }

    @PatchMapping("/update-name/{id}/name")
    public ResponseEntity<Boolean> updateUserName(@RequestBody UpdateNameRequest request) {
        return userService.updateName(request);
    }

    @PatchMapping("/update-email/{id}/email")
    public ResponseEntity<Boolean> updateEmail(@RequestBody UpdateEmailRequest request) {
        return userService.updateEmail(request);
    }

    @PatchMapping("/update-password/{id}/password")
    public ResponseEntity<Boolean> updatePassword(@RequestBody UpdatePasswordRequest request) {
        return userService.updatePassword(request);
    }

    @PatchMapping("/make-admin/{id}")
    public ResponseEntity<Boolean> makeAdmin(@RequestBody int requestUserId) {
        return userService.makeAdmin(requestUserId);
    }

    @PatchMapping("/make-mechanic/{id}")
    public ResponseEntity<Boolean> makeMechanic(@RequestBody int requestUserId) {
        return userService.makeMechanic(requestUserId);
    }

    @PatchMapping("/make-regular-user/{id}")
    public ResponseEntity<Boolean> makeRegularUser(@RequestBody int requestUserId) {
        return userService.makeRegularUser(requestUserId);
    }

    @PatchMapping("/update-profile-pic/{id}/url-new-pic")
    public ResponseEntity<Boolean> updateProfilePic(@RequestBody UpdateProfilePicRequest request) {
        return userService.updateProfilePic(request);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Boolean> deleteUser(@RequestBody int requestUserId) {
        return userService.deleteUser(requestUserId);
    }
}