package com.example.fixit.service;

import com.example.fixit.component.JwtTokenProvider;
import com.example.fixit.dto.request.GoogleUserRegisterRequest;
import com.example.fixit.dto.request.RefreshRequest;
import com.example.fixit.dto.request.UserLoginRequest;
import com.example.fixit.dto.request.UserRegisterRequest;
import com.example.fixit.dto.response.AuthResponse;
import com.example.fixit.dto.response.RefreshResponse;
import com.example.fixit.exception.UnauthorizedException;
import com.example.fixit.model.*;
import com.example.fixit.repository.RefreshTokenRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public AuthResponse register(UserRegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";
        String confirmPassword = request.getConfirmPassword() != null ? request.getConfirmPassword().trim() : "";
        String name = request.getName() != null ? request.getName().trim() : "";
        String profilePic = request.getProfilePic() != null ? request.getProfilePic().trim() : getDefaultProfilePic();

        if (!password.equals(confirmPassword)) {
            throw new UnauthorizedException("Passwords do not match");
        }
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UnauthorizedException("Email already in use");
        }
        if (!isValidPassword(password)) {
            throw new UnauthorizedException("Invalid password:\n• At least 8 characters long\n• At least one uppercase letter\n• At least one lowercase letter\n• At least one number\n• At least one special character");
        }
        if (!email.contains("@") || email.length() < 4) {
            throw new UnauthorizedException("Invalid email");
        }
        String[] nameParts = name.split("\\s+");
        if (nameParts.length != 2 || nameParts[0].length() < 2 || nameParts[1].length() < 2) {
            throw new UnauthorizedException("Invalid first or last name");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProfilePic(profilePic);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setGoogleId(null);
        initializeUserCollections(newUser);
        userRepository.save(newUser);
        return createSuccessResponse(newUser, false);
    }

    public AuthResponse googleRegister(GoogleUserRegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String name = request.getName() != null ? request.getName().trim() : "";
        String googleId = request.getGoogleId() != null ? request.getGoogleId().trim() : "";
        String profilePic = request.getProfilePic() != null ? request.getProfilePic().trim() : getDefaultProfilePic();
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UnauthorizedException("Email already in use");
        }
        Optional<User> existingGoogleUser = userRepository.findByGoogleId(googleId);
        if (existingGoogleUser.isPresent()) {
            throw new UnauthorizedException("Google ID already registered");
        }
        if (!email.contains("@") || email.length() < 4) {
            throw new UnauthorizedException("Invalid email");
        }
        String[] nameParts = name.split("\\s+");
        if (nameParts.length != 2 || nameParts[0].length() < 2 || nameParts[1].length() < 2) {
            throw new UnauthorizedException("Invalid first or last name");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProfilePic(profilePic);
        newUser.setPassword(null);
        newUser.setGoogleId(googleId);
        initializeUserCollections(newUser);
        userRepository.save(newUser);
        return createSuccessResponse(newUser, true);
    }

    public AuthResponse login(UserLoginRequest loginRequest) {
        String email = loginRequest.getEmail() != null ? loginRequest.getEmail().trim() : "";
        String password = loginRequest.getPassword() != null ? loginRequest.getPassword().trim() : "";
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        User user = userOpt.get();
        return createSuccessResponse(user, false);
    }

    public AuthResponse googleLogin(String googleId) {
        // Ensures googleId is never null by replacing null with empty string, then trims whitespace
        googleId = googleId != null ? googleId.trim() : "";
        Optional<User> userOpt = userRepository.findByGoogleId(googleId);
        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("Google user not found, please register first");
        }
        User user = userOpt.get();
        return createSuccessResponse(user, true);
    }

    public RefreshResponse refreshToken(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isEmpty()) {
            throw new UnauthorizedException("Token not valid");
        }
        RefreshToken tokenEntity = tokenOpt.get();
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new UnauthorizedException("Token not valid");
        }
        User user = tokenEntity.getUser();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());
        return new RefreshResponse(newAccessToken);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        return password.matches(passwordRegex);
    }

    private String getDefaultProfilePic() {
        return "https://ui-avatars.com/api/?name=User&background=cccccc&color=222222&size=128";
    }

    private void initializeUserCollections(User user) {
        user.setChats(new HashSet<>());
        user.setFollowing(new HashSet<>());
        user.setFollowers(new HashSet<>());
        user.setSavedPosts(new HashSet<>());
        user.setLikedPosts(new HashSet<>());
        user.setOwnedPosts(new HashSet<>());
        UserRoles userRoles = new UserRoles();
        userRoles.setUser(user);
        userRoles.setIsAdmin(false);
        userRoles.setIsMechanic(false);
        user.setUserRoles(userRoles);
    }

    private AuthResponse createSuccessResponse(User user, boolean isGoogle) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getUserId());
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshTokenEntity);
        return new AuthResponse(user.getName(), user.getEmail(), user.getProfilePic(), isGoogle, accessToken, refreshToken);
    }
}