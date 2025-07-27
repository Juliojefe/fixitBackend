package com.example.fixit.controller;

import com.example.fixit.component.JwtTokenProvider;
import com.example.fixit.dto.RefreshRequest;
import com.example.fixit.dto.RefreshResponse;
import com.example.fixit.dto.UserLoginRequest;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.model.RefreshToken;
import com.example.fixit.model.User;
import com.example.fixit.repository.RefreshTokenRepository;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        UserLoginResponse ulr = userService.loginUser(loginRequest);

        String accessToken = jwtTokenProvider.createAccessToken(ulr.getEmail(), ulr.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(ulr.getEmail(), ulr.getUserId());

        ulr.setAccessToken(jwtTokenProvider.createAccessToken(ulr.getEmail(), ulr.getUserId()));
        ulr.setRefreshToken(jwtTokenProvider.createRefreshToken(ulr.getEmail(), ulr.getUserId()));

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshTokenEntity);

        Boolean isGoogle = (user.getGoogleId() != null);
        return ResponseEntity.ok(new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), isGoogle, accessToken, refreshToken));

    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody UserLoginRequest loginRequest) {
//        User user = userRepository.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        String token = jwtTokenProvider.createAccessToken(loginRequest.getEmail(), user.getUserId());
//        return ResponseEntity.ok(token); // Return just access token for now
//    }

//    @PostMapping("/login")
//    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
//
//        String email = (String) auth.getPrincipal();
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
//        int userId = user.getUserId();
//
//        // Generate tokens
//        String accessToken = jwtTokenProvider.createAccessToken(email, userId);
//        String refreshToken = jwtTokenProvider.createRefreshToken(email, userId);
//
//        // Save refresh token
//        RefreshToken refreshTokenEntity = new RefreshToken();
//        refreshTokenEntity.setToken(refreshToken);
//        refreshTokenEntity.setUser(user);
//        refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
//        refreshTokenRepository.save(refreshTokenEntity);
//
//        Boolean isGoogle = (user.getGoogleId() != null);
//        return ResponseEntity.ok(new UserLoginResponse(true, user.getName(), user.getEmail(), user.getProfilePic(), user.getUserId(), isGoogle, accessToken, refreshToken));
//    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
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
}
