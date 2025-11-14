package com.example.fixit.controller;

import com.example.fixit.dto.request.GoogleUserRegisterRequest;
import com.example.fixit.dto.request.RefreshRequest;
import com.example.fixit.dto.request.UserLoginRequest;
import com.example.fixit.dto.request.UserRegisterRequest;
import com.example.fixit.dto.response.AuthResponse;
import com.example.fixit.dto.response.RefreshResponse;
import com.example.fixit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PatchMapping("/register/google/")
    public ResponseEntity<AuthResponse> googleRegister(@RequestBody GoogleUserRegisterRequest request) {
        return ResponseEntity.ok(authService.googleRegister(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/login/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody String googleId) {
        return ResponseEntity.ok(authService.googleLogin(googleId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshRequest));
    }
}