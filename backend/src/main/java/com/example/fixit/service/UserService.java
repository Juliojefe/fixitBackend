package com.example.fixit.service;

import com.example.fixit.controller.UserController;
import com.example.fixit.dto.*;
import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //  For admins only
    public List<GetUserResponse> getAllUsers() {
        try {
            List<User> allUsers = userRepository.findAll();
            List<GetUserResponse> response = new ArrayList<>();
            for(User u : allUsers) {
                response.add(new GetUserResponse(u));
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<GetUserResponse> getuserById(int userId) {
        try {
            Optional<User> OptUser = userRepository.findById(userId);
            if (OptUser.isPresent()) {
                User u = OptUser.get();
                return ResponseEntity.ok(new GetUserResponse(u));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public UserSummary getuserSummaryById(int userId){
        try {
            Optional<User> OptUser = userRepository.findById(userId);
            if (OptUser.isPresent()) {
                User u = OptUser.get();
                return new UserSummary(u);
            } else {
                return new UserSummary();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<List<Integer>> getAllUserIds() {
        try {
            List<Integer> ids = new ArrayList<>();
            List<User> allUsers = userRepository.findAll();
            for (User u : allUsers) {
                ids.add(u.getUserId());
            }
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Set<UserSummary> followSummary(Set<User> follow) {
        try {
            Set<UserSummary> summary = new HashSet<>();
            for (User u : follow) {
                summary.add(new UserSummary(u.getName(), u.getProfilePic()));
            }
            return summary;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateName(UpdateNameRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.setName(request.getName().trim());
                userRepository.save(tempUser);
                logger.info("User name updated logged successfully");
                return true;
            }
            logger.warn("User with id:{} was not found", request.getUserId());
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateEmail(UpdateEmailRequest request) {
        try {
            Optional<User> idUser = userRepository.findById(request.getUserId());
            Optional<User> emailUser = userRepository.findByEmail(request.getEmail().trim());
            if (idUser.isPresent() && emailUser.isPresent()) {
                User numUser = idUser.get();
                User strUser = emailUser.get();
                if (!Objects.equals(numUser.getUserId(), strUser.getUserId())) {
                    logger.warn("email in use");
                    return false;
                }
                logger.info("no need to update, user is already using that email");
                return false;
            }
            if (idUser.isPresent()) {
                User tempUser = idUser.get();
                tempUser.setEmail(request.getEmail());
                userRepository.save(tempUser);
                logger.info("email update successfully");
                return true;
            }
            logger.warn("User with id:{} was not found", request.getUserId());
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updatePassword(UpdatePasswordRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                User tempUser = user.get();
                if (tempUser.getPassword() == null && tempUser.getGoogleId() != null) {
                    logger.warn("Password update failed: google users blocked");
                    return false;
                }
                String oldPassword = request.getOldPassword().trim();
                String storedHashedPassword = tempUser.getPassword().trim();
                String newPassword = request.getNewPassword().trim();
                if (passwordEncoder.matches(oldPassword, storedHashedPassword)) {
                    if (!isValidPassword(newPassword)) {
                        logger.warn("Password update failed: Invalid new password for user id {}", request.getUserId());
                        return false;
                    }
                    tempUser.setPassword(passwordEncoder.encode(newPassword).trim());
                    userRepository.save(tempUser);
                    logger.info("password update successfully");
                    return true;
                }
            }
            logger.warn("password mismatch or non-existent user with id:{}", request.getUserId());
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean makeAdmin(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.getUserRoles().setIsAdmin(true);
                userRepository.save(tempUser);
                return true;
            }
            logger.warn("User with id:{} was not found", requestUserId);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean makeMechanic(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.getUserRoles().setIsMechanic(true);
                userRepository.save(tempUser);
                return true;
            }
            logger.warn("User with id:{} was not found", requestUserId);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean makeRegularUser(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.getUserRoles().setIsMechanic(false);
                tempUser.getUserRoles().setIsAdmin(false);
                userRepository.save(tempUser);
                return true;
            }
            logger.warn("User with id:{} was not found", requestUserId);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateProfilePic(UpdateProfilePicRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.setProfilePic(request.getPictureUrl());
                userRepository.save(tempUser);
                return true;
            }
            logger.warn("User with id:{} was not found", request.getUserId());
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteUser(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                userRepository.delete(tempUser);
                return true;
            }
            logger.warn("User with id:{} was not found", requestUserId);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidPassword(String password) {
        try {
            if (password == null || password.length() < 8) {
                return false;
            }
            String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
            return password.matches(passwordRegex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}