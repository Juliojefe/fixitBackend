package com.example.fixit.service;

import com.example.fixit.dto.*;
import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
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

    //  Emails are unique to a single user.
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        try {
            Optional<User> tempUser = userRepository.findByEmail(request.getEmail().trim());
            if (tempUser.isPresent()) {
                return new UserRegisterResponse(false, "Email already in use", "", "", -1, false);
            }

            if (!isValidPassword(request.getPassword())) {
                return new UserRegisterResponse(
                        false,
                        "Invalid password:\n• At least 8 characters long\n• At least one uppercase letter\n• At least one lowercase letter\n• At least one number\n• At least one special character",
                        "",
                        "",
                        -1,
                        false
                );
            }

            if (!request.getEmail().trim().contains("@") || request.getEmail().trim().length() < 4) {
                return new UserRegisterResponse(false, "Invalid email", "", "", -1, false);
            }

            String[] name = request.getName().trim().split("\\s+");
            if (name.length != 2 || name[0].length() < 2 || name[1].length() < 2) {
                return new UserRegisterResponse(false, "Invalid first or last name", "", "", -1, false);
            }
            
            User newUser = new User();
            newUser.setEmail(request.getEmail().trim());
            newUser.setProfilePic(request.getProfilePic().trim());
            newUser.setName(request.getName().trim());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()).trim());
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserRegisterResponse registerUserWithGoogle(GoogleUserRegisterRequest request) {
        try {
            Optional<User> tempUser = userRepository.findByEmail(request.getEmail().trim());
            if (tempUser.isPresent()) {
                logger.warn("Registration failed: User with email {} already exists", request.getEmail());
                return new UserRegisterResponse(false, "Email already in use", "", "", -1, false);
            }

            if (!request.getEmail().trim().contains("@") || request.getEmail().trim().length() < 4) {
                return new UserRegisterResponse(false, "Invalid email", "", "", -1, false);
            }

            String[] name = request.getName().trim().split("\\s+");
            if (name.length != 2 || name[0].length() < 2 || name[1].length() < 2) {
                return new UserRegisterResponse(false, "Invalid first or last name", "", "", -1, false);
            }

            User newUser = new User();
            newUser.setEmail(request.getEmail().trim());
            newUser.setProfilePic(request.getProfilePic().trim());
            newUser.setName(request.getName().trim());
            newUser.setPassword(null); // No password for Google users
            newUser.setGoogleId(request.getGoogleId().trim());

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

    public UserLoginResponse loginUser(UserLoginRequest request) {
        try {
            Optional<User> tempUser = userRepository.findByEmail(request.getEmail().trim());
            if (tempUser.isPresent() && passwordEncoder.matches(request.getPassword(), tempUser.get().getPassword())) {
                logger.info("User with email {} logged in successfully", request.getEmail());
                User user = tempUser.get();
                return new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), false);
            } else {
                logger.warn("Authentication failed for email {}: Incorrect email or password", request.getEmail());
                return new UserLoginResponse(false, "", "", "", -1, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserLoginResponse loginUserWithGoogle(String googleId) {
        try {
            Optional<User> tempUser = userRepository.findByGoogleId(googleId.trim());
            if (tempUser.isPresent()) {
                User user = tempUser.get();
                return new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), true);
            }
            return new UserLoginResponse(false, "Google user not found, please register first", "", "", -1, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    public GetUserResponse getuserById(int userId) {
        try {
            Optional<User> OptUser = userRepository.findById(userId);
            if (OptUser.isPresent()) {
                User u = OptUser.get();
                return new GetUserResponse(u);
            } else {
                return new GetUserResponse();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
}