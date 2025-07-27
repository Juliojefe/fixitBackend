package com.example.fixit.service;

import com.example.fixit.component.JwtTokenProvider;
import com.example.fixit.dto.RefreshRequest;
import com.example.fixit.dto.RefreshResponse;
import com.example.fixit.dto.UserLoginRequest;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.repository.RefreshTokenRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {
        //  TODO
    }

    public ResponseEntity<UserLoginResponse> loginGoogle(UserLoginRequest loginRequest) {
        //  TODO
    }

    public ResponseEntity<RefreshResponse> refreshToken(RefreshRequest refreshRequest) {
        //  TODO
    }



}