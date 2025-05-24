package com.example.fixit.service;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //  Emails are unique to a single user.
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        Optional<User> tempUser = userRepository.findByEmail(request.getEmail());
        if (tempUser.isPresent()) {
            logger.warn("Registration failed: User with email {} already exists", request.getEmail());
            return new UserRegisterResponse(false, "", "", "", -1);
        }
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setProfilePic(request.getProfilePic());
        newUser.setName(request.getName());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);
        logger.info("User with email {} registered successfully", request.getEmail());
        return new UserRegisterResponse(true, newUser.getName(),
                newUser.getEmail(), newUser.getProfilePic(), newUser.getUserId());
    }

    public UserLoginResponse loginUser(UserLoginRequest request) {
        Optional<User> tempUser = userRepository.findByEmail(request.getEmail());
        if (tempUser.isPresent() && passwordEncoder.matches(request.getPassword(), tempUser.get().getPassword())) {
            logger.info("User with email {} logged in successfully", request.getEmail());
            User user = tempUser.get();
            return new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId());
        } else {
            logger.warn("Authentication failed for email {}: Incorrect email or password", request.getEmail());
            return new UserLoginResponse(false, "", "", "", -1);
        }
    }

    //  For admins only
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean updateName(UpdateNameRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isPresent()) {
            User tempUser = user.get();
            tempUser.setName(request.getName());
            userRepository.save(tempUser);
            logger.info("User name updated logged successfully");
            return true;
        }
        logger.warn("User with id:{} was not found", request.getUserId());
        return false;
    }

    public boolean updateEmail(UpdateEmailRequest request) {
        Optional<User> idUser = userRepository.findById(request.getUserId());
        Optional<User> emailUser = userRepository.findByEmail(request.getEmail());
        if (idUser.isPresent() && emailUser.isPresent()) {
            User numUser = idUser.get();
            User strUser = emailUser.get();
            if (numUser.getUserId() != strUser.getUserId()) {
                logger.warn("email in use");
                return false;
            }
            numUser.setEmail(request.getEmail());
            userRepository.save(numUser);
            logger.info("email update successfully");
            return true;
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
    }

    public boolean updatePassword(UpdatePasswordRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isPresent()) {
            User tempUser = user.get();
            String oldPassword = request.getOldPassword();
            String storedHashedPassword = tempUser.getPassword();
            String newPassword = request.getNewPassword();
            if (passwordEncoder.matches(oldPassword, storedHashedPassword)) {
                tempUser.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(tempUser);
                logger.info("password update successfully");
                return true;
            }
        }
        logger.warn("password mismatch or non-existent user with id:{}", request.getUserId());
        return false;
    }

    @Transactional
    public boolean makeAdmin(int requestUserId) {
        try {
            Optional<User> userOptional = userRepository.findById(requestUserId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserRoles userRoles = user.getUserRoles();
                if (userRoles == null) {    // Create a new UserRoles if none exists
                    userRoles = new UserRoles(user.getUserId(), true, false);
                    user.setUserRoles(userRoles);
                } else {
                    userRoles.setIsAdmin(true); // Update existing UserRoles
                }
                userRepository.save(user); // Cascades to UserRoles
                return true;
            }
            logger.warn("User with id:{} was not found. Current user: {}", requestUserId);
            return false;
        } catch (Exception e) {
            logger.error("Failed to make user {} an admin: {}", requestUserId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public boolean makeMechanic(int requestUserId) {
        try {
            Optional<User> userOptional = userRepository.findById(requestUserId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserRoles userRoles = user.getUserRoles();
                if (userRoles == null) {    // Create a new UserRoles if none exists
                    userRoles = new UserRoles(user.getUserId(), false, true);
                    user.setUserRoles(userRoles);
                } else {
                    userRoles.setIsMechanic(true); // Update existing UserRoles
                }
                userRepository.save(user); // Cascades to UserRoles
                return true;
            }
            logger.warn("User with id:{} was not found. Current user: {}", requestUserId);
            return false;
        } catch (Exception e) {
            logger.error("Failed to make user {} an admin: {}", requestUserId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public boolean makeRegularUser(int requestUserId) {
        try {
            Optional<User> userOptional = userRepository.findById(requestUserId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserRoles userRoles = user.getUserRoles();
                if (userRoles == null) {    // Create a new UserRoles if none exists
                    userRoles = new UserRoles(user.getUserId(), false, false);
                    user.setUserRoles(userRoles);
                } else {
                    userRoles.setIsAdmin(false); // Update existing UserRoles
                    userRoles.setIsMechanic(false);
                }
                userRepository.save(user); // Cascades to UserRoles
                return true;
            }
            logger.warn("User with id:{} was not found. Current user: {}", requestUserId);
            return false;
        } catch (Exception e) {
            logger.error("Failed to make user {} an admin: {}", requestUserId, e.getMessage(), e);
            return false;
        }
    }

    public boolean updateProfilePic(UpdateProfilePicRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isPresent()) {
            User tempUser = user.get();
            tempUser.setProfilePic(request.getPictureUrl());
            userRepository.save(tempUser);
            return true;
        }
        logger.warn("User with id:{} was not found", request.getUserId());
        return false;
    }

    public boolean deleteUser(int requestUserId) {
        Optional<User> user = userRepository.findById(requestUserId);
        if (user.isPresent()) {
            User tempUser = user.get();
            userRepository.delete(tempUser);
            return true;
        }
        logger.warn("User with id:{} was not found", requestUserId);
        return false;
    }

}