package com.example.fixit.service;

import com.example.fixit.dto.request.UpdateEmailRequest;
import com.example.fixit.dto.request.UpdateNameRequest;
import com.example.fixit.dto.request.UpdatePasswordRequest;
import com.example.fixit.dto.request.UpdateProfilePicRequest;
import com.example.fixit.dto.response.GetUserProfilePrivateResponse;
import com.example.fixit.dto.response.GetUserProfilePublicResponse;
import com.example.fixit.dto.response.UserNameAndPfp;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //  For admins only
    public ResponseEntity<Page<GetUserProfilePrivateResponse>> getAllUsersPrivate(Pageable pageable) {
        try {
            Page<User> userPage = userRepository.findAll(pageable);
            List<GetUserProfilePrivateResponse> responseList = new ArrayList<>();
            for (User u : userPage.getContent()) {
                responseList.add(new GetUserProfilePrivateResponse(u));
            }
            return ResponseEntity.ok(new PageImpl<>(responseList, pageable, userPage.getTotalElements()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<GetUserProfilePrivateResponse> getUserProfilePrivateById(int userId) {
        try {
            Optional<User> OptUser = userRepository.findById(userId);
            if (OptUser.isPresent()) {
                User u = OptUser.get();
                return ResponseEntity.ok(new GetUserProfilePrivateResponse(u));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<UserNameAndPfp> getUserNameAndPfpById(int userId){
        try {
            Optional<User> OptUser = userRepository.findById(userId);
            if (OptUser.isPresent()) {
                User u = OptUser.get();
                return ResponseEntity.ok(new UserNameAndPfp(u));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<GetUserProfilePublicResponse> getUserProfileById(int userId) {
        try {
            Optional<User> u = userRepository.findById(userId);
            if (u.isPresent()) {
                return ResponseEntity.ok(new GetUserProfilePublicResponse(u.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Page<Integer>> getAllUserIds(Pageable pageable) {
        try {
            Page<User> allUsers = userRepository.findAll(pageable);
            List<Integer> ids = new ArrayList<>();
            for (User u : allUsers) {
                ids.add(u.getUserId());
            }
            Page<Integer> pageOfIds = new PageImpl<>(ids, pageable, allUsers.getTotalElements());
            return ResponseEntity.ok(pageOfIds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Boolean> updateName(UpdateNameRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.setName(request.getName().trim());
                userRepository.save(tempUser);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> updateEmail(UpdateEmailRequest request) {
        try {
            Optional<User> idUser = userRepository.findById(request.getUserId());
            Optional<User> emailUser = userRepository.findByEmail(request.getEmail().trim());
            // If both exist, but belong to different users → conflict (email already in use)
            if (idUser.isPresent() && emailUser.isPresent()) {
                User currentUser = idUser.get();
                User existingUser = emailUser.get();
                if (!Objects.equals(currentUser.getUserId(), existingUser.getUserId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(false); // 409 Conflict
                }
            }
            // If user not found by ID → 404
            if (idUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
            // Update email
            User userToUpdate = idUser.get();
            userToUpdate.setEmail(request.getEmail().trim());
            userRepository.save(userToUpdate);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> updatePassword(UpdatePasswordRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
            User tempUser = user.get();
            // Prevent password updates for Google-linked accounts
            if (tempUser.getPassword() == null && tempUser.getGoogleId() != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
            }
            String oldPassword = request.getOldPassword().trim();
            String storedHashedPassword = tempUser.getPassword().trim();
            String newPassword = request.getNewPassword().trim();
            if (!passwordEncoder.matches(oldPassword, storedHashedPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
            if (!isValidPassword(newPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            }
            tempUser.setPassword(passwordEncoder.encode(newPassword).trim());
            userRepository.save(tempUser);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> makeAdmin(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.getUserRoles().setIsAdmin(true);
                userRepository.save(tempUser);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> makeMechanic(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.getUserRoles().setIsMechanic(true);
                userRepository.save(tempUser);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> makeRegularUser(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.getUserRoles().setIsMechanic(false);
                tempUser.getUserRoles().setIsAdmin(false);
                userRepository.save(tempUser);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> updateProfilePic(UpdateProfilePicRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                User tempUser = user.get();
                tempUser.setProfilePic(request.getPictureUrl());
                userRepository.save(tempUser);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    public ResponseEntity<Boolean> deleteUser(int requestUserId) {
        try {
            Optional<User> user = userRepository.findById(requestUserId);
            if (user.isPresent()) {
                User tempUser = user.get();
                userRepository.delete(tempUser);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
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