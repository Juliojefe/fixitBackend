package com.example.fixit.controller;

import com.example.fixit.component.JwtTokenProvider;
import com.example.fixit.dto.request.GoogleUserRegisterRequest;
import com.example.fixit.dto.request.RefreshRequest;
import com.example.fixit.dto.request.UserLoginRequest;
import com.example.fixit.dto.request.UserRegisterRequest;
import com.example.fixit.dto.response.RefreshResponse;
import com.example.fixit.dto.response.UserLoginResponse;
import com.example.fixit.dto.response.UserRegisterResponse;
import com.example.fixit.repository.RefreshTokenRepository;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest request) {
        return authService.register(request);
    }

    @PatchMapping("/register/google/")
    public ResponseEntity<UserRegisterResponse> googleRegister(@RequestBody GoogleUserRegisterRequest request) {
        return authService.googleRegister(request);
    }

        @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/login/google")
    public ResponseEntity<UserLoginResponse> googleLogin(@RequestBody String googleId) {
        return authService.googleLogin(googleId);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        return authService.refreshToken(refreshRequest);
    }
}
