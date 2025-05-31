package com.example.fixit.service;

import com.example.fixit.dto.*;
import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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

    //  Emails are unique to a single user.
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        Optional<User> tempUser = userRepository.findByEmail(request.getEmail());
        if (tempUser.isPresent()) {
            logger.warn("Registration failed: User with email {} already exists", request.getEmail());
            return new UserRegisterResponse(false, "email already exists", "", "", -1, false);
        }

        if (!isValidPassword(request.getPassword())) {
            logger.warn("Registration failed: Invalid password for email {}", request.getEmail());
            return new UserRegisterResponse(false, "invalid password", "", "", -1, false);
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setProfilePic(request.getProfilePic());
        newUser.setName(request.getName());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setGoogleId(null);

        // Initialize relational fields as empty sets
        newUser.setChats(new HashSet<Chat>());
        newUser.setFollowing(new HashSet<User>());
        newUser.setFollowers(new HashSet<User>());
        newUser.setSavedPosts(new HashSet<Post>());
        newUser.setLikedPosts(new HashSet<Post>());
        newUser.setOwnedPosts(new HashSet<Post>());

        // Create and configure UserRoles
        UserRoles userRoles = new UserRoles();
        userRoles.setUser(newUser); // Link UserRoles to User
        userRoles.setIsAdmin(false); // Default to false
        userRoles.setIsMechanic(false); // Default to false
        newUser.setUserRoles(userRoles); // Link User to UserRoles

        // Save the user (cascades to UserRoles)
        userRepository.save(newUser);

        logger.info("User with email {} registered successfully", request.getEmail());
        return new UserRegisterResponse(true, newUser.getName(),
                newUser.getEmail(), newUser.getProfilePic(), newUser.getUserId(), false);
    }

    @Transactional
    public UserRegisterResponse registerUserWithGoogle(GoogleUserRegisterRequest request) {
        Optional<User> tempUser = userRepository.findByEmail(request.getEmail());
        if (tempUser.isPresent()) {
            logger.warn("Registration failed: User with email {} already exists", request.getEmail());
            return new UserRegisterResponse(false, "email already exists", "", "", -1, false);
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setProfilePic(request.getProfilePic());
        newUser.setName(request.getName());
        newUser.setPassword(null); // No password for Google users
        newUser.setGoogleId(request.getGoogleId());

        // Initialize relational fields as empty sets
        newUser.setChats(new HashSet<Chat>());
        newUser.setFollowing(new HashSet<User>());
        newUser.setFollowers(new HashSet<User>());
        newUser.setSavedPosts(new HashSet<Post>());
        newUser.setLikedPosts(new HashSet<Post>());
        newUser.setOwnedPosts(new HashSet<Post>());

        // Create and configure UserRoles
        UserRoles userRoles = new UserRoles();
        userRoles.setUser(newUser);
        userRoles.setIsAdmin(false);
        userRoles.setIsMechanic(false);
        newUser.setUserRoles(userRoles);

        // Save the user (cascades to UserRoles)
        userRepository.save(newUser);
        logger.info("User with email {} registered successfully via Google", request.getEmail());
        return new UserRegisterResponse(true, newUser.getName(), newUser.getEmail(),
                newUser.getProfilePic(), newUser.getUserId(), true);
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        return password.matches(passwordRegex);
    }

    public UserLoginResponse loginUser(UserLoginRequest request) {
        Optional<User> tempUser = userRepository.findByEmail(request.getEmail());
        if (tempUser.isPresent() && passwordEncoder.matches(request.getPassword(), tempUser.get().getPassword())) {
            logger.info("User with email {} logged in successfully", request.getEmail());
            User user = tempUser.get();
            return new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), false);
        } else {
            logger.warn("Authentication failed for email {}: Incorrect email or password", request.getEmail());
            return new UserLoginResponse(false, "", "", "", -1, false);
        }
    }

    public UserLoginResponse loginUserWithGoogle(String googleId)  {
        Optional<User> tempUser = userRepository.findByGoogleId(googleId);
        if (tempUser.isPresent()) {
            User user = tempUser.get();
            return new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), true);
        }
        return new UserLoginResponse(false, "Google user not found, please register first", "", "", -1, false);
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
            if (!Objects.equals(numUser.getUserId(), strUser.getUserId())) {
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
            if (tempUser.getPassword() == null && tempUser.getGoogleId() != null) {
                logger.warn("Password update failed: google users blocked");
                return false;
            }
            String oldPassword = request.getOldPassword();
            String storedHashedPassword = tempUser.getPassword();
            String newPassword = request.getNewPassword();
            if (passwordEncoder.matches(oldPassword, storedHashedPassword)) {
                if (!isValidPassword(newPassword)) {
                    logger.warn("Password update failed: Invalid new password for user id {}", request.getUserId());
                    return false;
                }
                tempUser.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(tempUser);
                logger.info("password update successfully");
                return true;
            }
        }
        logger.warn("password mismatch or non-existent user with id:{}", request.getUserId());
        return false;
    }

    public boolean makeAdmin(int requestUserId) {
        Optional<User> user = userRepository.findById(requestUserId);
        if (user.isPresent()) {
            User tempUser = user.get();
            tempUser.getUserRoles().setIsAdmin(true);
            userRepository.save(tempUser);
            return true;
        }
        logger.warn("User with id:{} was not found", requestUserId);
        return false;
    }

    public boolean makeMechanic(int requestUserId) {
        Optional<User> user = userRepository.findById(requestUserId);
        if (user.isPresent()) {
            User tempUser = user.get();
            tempUser.getUserRoles().setIsMechanic(true);
            userRepository.save(tempUser);
            return true;
        }
        logger.warn("User with id:{} was not found", requestUserId);
        return false;
    }

    public boolean makeRegularUser(int requestUserId) {
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