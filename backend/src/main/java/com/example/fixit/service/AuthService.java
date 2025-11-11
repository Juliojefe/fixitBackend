package com.example.fixit.service;

import com.example.fixit.component.JwtTokenProvider;
import com.example.fixit.dto.request.GoogleUserRegisterRequest;
import com.example.fixit.dto.request.RefreshRequest;
import com.example.fixit.dto.request.UserLoginRequest;
import com.example.fixit.dto.request.UserRegisterRequest;
import com.example.fixit.dto.response.RefreshResponse;
import com.example.fixit.dto.response.UserLoginResponse;
import com.example.fixit.dto.response.UserRegisterResponse;
import com.example.fixit.model.*;
import com.example.fixit.repository.RefreshTokenRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<UserRegisterResponse> register(UserRegisterRequest request) {
        try {
            Optional<User> tempUser = userRepository.findByEmail(request.getEmail().trim());
            if (tempUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Email already in use", "", "", false, "", ""));
            }
            if (!isValidPassword(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse(
                                "Invalid password:\n• At least 8 characters long\n• At least one uppercase letter\n• At least one lowercase letter\n• At least one number\n• At least one special character",
                                "", "",  false, "", ""));
            }
            if (!request.getEmail().trim().contains("@") || request.getEmail().trim().length() < 4) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Invalid email", "", "",  false,  "", ""));
            }
            String[] name = request.getName().trim().split("\\s+");
            if (name.length != 2 || name[0].length() < 2 || name[1].length() < 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Invalid first or last name", "", "",  false, "", ""));
            }
            User newUser = new User();
            newUser.setEmail(request.getEmail().trim());
            if (request.getProfilePic().isEmpty() || request.getProfilePic() == null) {
                newUser.setProfilePic("https://ui-avatars.com/api/?name=User&background=cccccc&color=222222&size=128");
            } else {
                newUser.setProfilePic(request.getProfilePic().trim());
            }
            newUser.setName(request.getName().trim());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()).trim());
            newUser.setGoogleId(null);
            newUser.setChats(new HashSet<Chat>());
            newUser.setFollowing(new HashSet<User>());
            newUser.setFollowers(new HashSet<User>());
            newUser.setSavedPosts(new HashSet<Post>());
            newUser.setLikedPosts(new HashSet<Post>());
            newUser.setOwnedPosts(new HashSet<Post>());
            UserRoles userRoles = new UserRoles();
            userRoles.setUser(newUser);
            userRoles.setIsAdmin(false);
            userRoles.setIsMechanic(false);
            newUser.setUserRoles(userRoles);
            userRepository.save(newUser);
            String accessToken = jwtTokenProvider.createAccessToken(newUser.getEmail(), newUser.getUserId());
            String refreshToken = jwtTokenProvider.createRefreshToken(newUser.getEmail(), newUser.getUserId());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setUser(newUser);
            refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
            refreshTokenRepository.save(refreshTokenEntity);
            return ResponseEntity.ok(new UserRegisterResponse(newUser.getName(), newUser.getEmail(), newUser.getProfilePic(), false, accessToken, refreshToken));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<UserRegisterResponse> googleRegister(GoogleUserRegisterRequest request) {
        try {
            Optional<User> tempUser = userRepository.findByEmail(request.getEmail().trim());
            if (tempUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Email already in use", "", "", false, "", ""));
            }

            Optional<User> googleUser = userRepository.findByGoogleId(request.getGoogleId().trim());
            if (googleUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Google ID already registered", "", "",  false, "", ""));
            }

            if (!request.getEmail().trim().contains("@") || request.getEmail().trim().length() < 4) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Invalid email", "", "", false, "", ""));
            }

            String[] name = request.getName().trim().split("\\s+");
            if (name.length != 2 || name[0].length() < 2 || name[1].length() < 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserRegisterResponse( "Invalid first or last name", "", "", false, "", ""));
            }
            User newUser = new User();
            newUser.setEmail(request.getEmail().trim());
            if (request.getProfilePic().isEmpty() || request.getProfilePic() == null) {
                newUser.setProfilePic("https://ui-avatars.com/api/?name=User&background=cccccc&color=222222&size=128");
            } else {
                newUser.setProfilePic(request.getProfilePic().trim());
            }
            newUser.setName(request.getName().trim());
            newUser.setPassword(null); // No password for Google users
            newUser.setGoogleId(request.getGoogleId().trim());
            newUser.setChats(new HashSet<Chat>());
            newUser.setFollowing(new HashSet<User>());
            newUser.setFollowers(new HashSet<User>());
            newUser.setSavedPosts(new HashSet<Post>());
            newUser.setLikedPosts(new HashSet<Post>());
            newUser.setOwnedPosts(new HashSet<Post>());
            UserRoles userRoles = new UserRoles();
            userRoles.setUser(newUser);
            userRoles.setIsAdmin(false);
            userRoles.setIsMechanic(false);
            newUser.setUserRoles(userRoles);
            userRepository.save(newUser);
            String accessToken = jwtTokenProvider.createAccessToken(newUser.getEmail(), newUser.getUserId());
            String refreshToken = jwtTokenProvider.createRefreshToken(newUser.getEmail(), newUser.getUserId());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setUser(newUser);
            refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
            refreshTokenRepository.save(refreshTokenEntity);
            return ResponseEntity.ok(new UserRegisterResponse(newUser.getName(), newUser.getEmail(), newUser.getProfilePic(), false, accessToken, refreshToken));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {
        try {
            Optional<User> tempUser = userRepository.findByEmail(loginRequest.getEmail().trim());
            if (tempUser.isPresent() && passwordEncoder.matches(loginRequest.getPassword().trim(), tempUser.get().getPassword())) {
                User user = tempUser.get();
                String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getUserId());
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setToken(refreshToken);
                refreshTokenEntity.setUser(user);
                refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
                refreshTokenRepository.save(refreshTokenEntity);
                return ResponseEntity.ok(new UserLoginResponse(user.getName(), user.getEmail(), user.getProfilePic(), false, accessToken, refreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserLoginResponse("", "", "", false, "", ""));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<UserLoginResponse> googleLogin(String googleId) {
        try {
            Optional<User> tempUser = userRepository.findByGoogleId(googleId.trim());
            if (tempUser.isPresent()) {
                User user = tempUser.get();
                String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getUserId());
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setToken(refreshToken);
                refreshTokenEntity.setUser(user);
                refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
                refreshTokenRepository.save(refreshTokenEntity);
                return ResponseEntity.ok(new UserLoginResponse(user.getName(), user.getEmail(), user.getProfilePic(), true, accessToken, refreshToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserLoginResponse("Google user not found, please register first", "", "", false, "", ""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<RefreshResponse> refreshToken(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        // Validate refresh token
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }
        User user = tokenEntity.getUser();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());

        return ResponseEntity.ok(new RefreshResponse(newAccessToken));
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