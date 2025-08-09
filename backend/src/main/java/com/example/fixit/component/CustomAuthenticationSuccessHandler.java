package com.example.fixit.component;

import com.example.fixit.service.AuthService;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.dto.GoogleUserRegisterRequest;
import com.example.fixit.dto.UserRegisterResponse;
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

        ResponseEntity<UserLoginResponse> loginEntity = authService.googleLogin(googleId);
        UserLoginResponse loginResponse = loginEntity.getBody();

        if (loginEntity.getStatusCode().is2xxSuccessful() && loginResponse != null) {
            // If login is successful, build the redirect URL with the login response
            buildRedirect(response, loginResponse);
        } else {
            // If login fails (user doesn't exist), register them and build the redirect URL
            String finalProfilePic = (profilePic != null) ? profilePic : "";
            GoogleUserRegisterRequest registerRequest = new GoogleUserRegisterRequest(googleId, email, name, finalProfilePic);
            ResponseEntity<UserRegisterResponse> registerEntity = authService.googleRegister(registerRequest);
            buildRedirect(response, registerEntity.getBody());
        }
    }

    // A helper method to build the redirect URL using UriComponentsBuilder
    private void buildRedirect(HttpServletResponse response, Object data) throws IOException {
        String baseUrl = "http://localhost:3000/auth-callback";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("success", "true");

        if (data instanceof UserLoginResponse) {
            UserLoginResponse res = (UserLoginResponse) data;
            uriBuilder.queryParam("name", res.getName());
            uriBuilder.queryParam("email", res.getEmail());
            uriBuilder.queryParam("profilePic", res.getProfilePic());
            uriBuilder.queryParam("isGoogle", res.isGoogle());
            uriBuilder.queryParam("accessToken", res.getAccessToken());
            uriBuilder.queryParam("refreshToken", res.getRefreshToken());
        } else if (data instanceof UserRegisterResponse) {
            UserRegisterResponse res = (UserRegisterResponse) data;
            uriBuilder.queryParam("name", res.getName());
            uriBuilder.queryParam("email", res.getEmail());
            uriBuilder.queryParam("profilePic", res.getProfilePic());
            uriBuilder.queryParam("isGoogle", res.isGoogle());
            uriBuilder.queryParam("accessToken", res.getAccessToken());
            uriBuilder.queryParam("refreshToken", res.getRefreshToken());
        } else {
            throw new IllegalArgumentException("Unsupported response type for redirect");
        }
        response.sendRedirect(uriBuilder.toUriString());
    }
}