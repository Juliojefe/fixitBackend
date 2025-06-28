package com.example.fixit.service;
import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class FollowService {

    @Autowired
    private UserRepository userRepository;

    public MutualFollowResponse checkMutualFollow(int userAId, int userBId) {
        Optional<User> optUserA = userRepository.findById(userAId);
        Optional<User> optUserB = userRepository.findById(userBId);
        if (optUserA.isPresent() && optUserB.isPresent()) {
            return new MutualFollowResponse(optUserA.get(), optUserB.get());
        } else {
            return new MutualFollowResponse();
        }
    }

}