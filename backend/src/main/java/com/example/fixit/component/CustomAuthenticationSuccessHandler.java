package com.example.fixit.component;

import com.example.fixit.service.AuthService;
import com.example.fixit.dto.request.GoogleUserRegisterRequest;
import com.example.fixit.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String profilePic = oauthUser.getAttribute("picture");

        if (email == null || name == null || googleId == null) {
            throw new IllegalArgumentException("Missing required attributes from Google: email, name, or sub");
        }

        ResponseEntity<AuthResponse> loginEntity = authService.googleLogin(googleId);
        if (loginEntity.getStatusCode().is2xxSuccessful()) {
            buildRedirect(response, loginEntity.getBody(), true);
        } else {
            // If login fails (user doesn't exist), attempt to register
            String finalProfilePic = (profilePic != null) ? profilePic : "";
            GoogleUserRegisterRequest registerRequest = new GoogleUserRegisterRequest(googleId, email, name, finalProfilePic);
            ResponseEntity<AuthResponse> registerEntity = authService.googleRegister(registerRequest);
            boolean isSuccess = registerEntity.getStatusCode().is2xxSuccessful();
            buildRedirect(response, registerEntity.getBody(), isSuccess);
        }
    }

    // Updated helper method to build the redirect URL using UriComponentsBuilder
    private void buildRedirect(HttpServletResponse response, AuthResponse authResponse, boolean isSuccess) throws IOException {
        String baseUrl = "http://localhost:3000/auth-callback";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("success", String.valueOf(isSuccess));

        if (isSuccess && authResponse != null && authResponse.getAccessToken() != null) {
            uriBuilder.queryParam("name", authResponse.getName());
            uriBuilder.queryParam("email", authResponse.getEmail());
            uriBuilder.queryParam("profilePic", authResponse.getProfilePic());
            uriBuilder.queryParam("isGoogle", authResponse.isGoogle());
            uriBuilder.queryParam("accessToken", authResponse.getAccessToken());
            uriBuilder.queryParam("refreshToken", authResponse.getRefreshToken());
        } else if (authResponse != null && authResponse.getMessage() != null) {
            uriBuilder.queryParam("message", authResponse.getMessage());
        } else {
            uriBuilder.queryParam("message", "An unexpected error occurred");
        }

        response.sendRedirect(uriBuilder.toUriString());
    }
}