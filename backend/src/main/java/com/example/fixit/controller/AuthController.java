package com.example.fixit.controller;

import com.example.fixit.dto.request.GoogleUserRegisterRequest;
import com.example.fixit.dto.request.RefreshRequest;
import com.example.fixit.dto.request.UserLoginRequest;
import com.example.fixit.dto.request.UserRegisterRequest;
import com.example.fixit.dto.response.AuthResponse;
import com.example.fixit.dto.response.RefreshResponse;
import com.example.fixit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest request) {
        AuthResponse resp = authService.register(request);
        if (resp.getAccessToken() != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest loginRequest) {
        AuthResponse resp = authService.login(loginRequest);
        if (resp.getAccessToken() != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }

    @PostMapping("/google/register")
    public ResponseEntity<AuthResponse> googleRegister(@RequestBody GoogleUserRegisterRequest request) {
        AuthResponse resp = authService.googleRegister(request);
        if (resp.getAccessToken() != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }

    @PostMapping("/google/login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> request) {
        AuthResponse resp = authService.googleLogin(request.get("googleId"));
        if (resp.getAccessToken() != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }
}