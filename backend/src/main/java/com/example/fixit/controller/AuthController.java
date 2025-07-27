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
import com.example.fixit.service.AuthService;
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
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/loginGoogle")
    public ResponseEntity<UserLoginResponse> loginGoogle(@RequestBody String googleId) {
        return authService.loginGoogle(googleId);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        return authService.refreshToken(refreshRequest);
    }
}
