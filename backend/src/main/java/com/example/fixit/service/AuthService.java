package com.example.fixit.service;

import com.example.fixit.component.JwtTokenProvider;
import com.example.fixit.dto.RefreshRequest;
import com.example.fixit.dto.RefreshResponse;
import com.example.fixit.dto.UserLoginRequest;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.model.RefreshToken;
import com.example.fixit.model.User;
import com.example.fixit.repository.RefreshTokenRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
                return ResponseEntity.ok(new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), false, accessToken, refreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserLoginResponse(false, "", "", "", -1, false, "", ""));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<UserLoginResponse> loginGoogle(String googleId) {
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
                return ResponseEntity.ok(new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), true, accessToken, refreshToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserLoginResponse(false, "Google user not found, please register first", "", "", -1, false, "", ""));
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