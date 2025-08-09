package com.example.fixit.service;
import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


@Service
public class FollowService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<MutualFollowResponse> checkMutualFollow(int userAId, int userBId) {
        try {
            Optional<User> optUserA = userRepository.findById(userAId);
            Optional<User> optUserB = userRepository.findById(userBId);
            if (optUserA.isPresent() && optUserB.isPresent()) {
                return ResponseEntity.ok(new MutualFollowResponse(optUserA.get(), optUserB.get()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MutualFollowResponse());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Boolean> unfollow(int activeUserId, int unfollowUserId) {
        try {
            Optional<User> optActive = userRepository.findById(activeUserId);
            Optional<User> optionalUnfollow = userRepository.findById(unfollowUserId);
            if (optActive.isPresent() && optionalUnfollow.isPresent()) {
                User active = optActive.get();
                User unfollow = optionalUnfollow.get();
                if (active.getFollowing().contains(unfollow)) {
                    active.getFollowing().remove(unfollow);
                    unfollow.getFollowers().remove(active);
                    userRepository.save(active);
                    userRepository.save(unfollow);
                    return ResponseEntity.ok(true);    //  true: both exist, and active followed the other
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);   //  false: never followed
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);   //  false: one or both don't exist
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Boolean> follow(int activeUserId, int followUserId) {
        try {
            Optional<User> optActive = userRepository.findById(activeUserId);
            Optional<User> optionalFollow = userRepository.findById(followUserId);
            if (optActive.isPresent() && optionalFollow.isPresent()) {
                User active = optActive.get();
                User follow = optionalFollow.get();
                if (!active.getFollowing().contains(follow)) {
                    active.getFollowing().add(follow);
                    follow.getFollowers().add(active);
                    userRepository.save(active);
                    userRepository.save(follow);
                    return ResponseEntity.ok(true);    //  true: both exist, active don't follow the other
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);   //  false: active already follows
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);   //  false: one or both don't exist
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Boolean> removeFollower(int activeUserId, int removeFollowerId) {
        try {
            Optional<User> optActive = userRepository.findById(activeUserId);
            Optional<User> optionalRemoveFollow = userRepository.findById(removeFollowerId);
            if (optActive.isPresent() && optionalRemoveFollow.isPresent()) {
                User active = optActive.get();
                User removeFollow = optionalRemoveFollow.get();
                if (active.getFollowers().contains(removeFollow)) {
                    active.getFollowers().remove(removeFollow);
                    removeFollow.getFollowing().remove(active);
                    userRepository.save(active);
                    userRepository.save(removeFollow);
                    return ResponseEntity.ok(true);    //  true: both exist, active is followed by the other
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);   //  false: active is not followed by the other
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);   //  false: one or both don't exist
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}