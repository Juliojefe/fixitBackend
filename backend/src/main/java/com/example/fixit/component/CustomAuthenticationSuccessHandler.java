package com.example.fixit.component;

import com.example.fixit.dto.GoogleUserRegisterRequest;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.dto.UserRegisterResponse;
import com.example.fixit.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Cast to OAuth2AuthenticationToken to get Google user details
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        // Extract user details from Google's ID token
        String googleId = oauthUser.getAttribute("sub"); // This is the googleId
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String profilePic = oauthUser.getAttribute("picture");

        if (email == null || name == null || googleId == null) {
            throw new IllegalArgumentException("Missing required attributes: email and/or name");
        }

        // Check if the user exists by googleId
        UserLoginResponse loginResponse = userService.loginUserWithGoogle(googleId);
        if (loginResponse.isSuccess()) {
            // User exists, log them in
            writeResponse(response, loginResponse);
        } else {
            // User doesn’t exist, register them
            GoogleUserRegisterRequest registerRequest = new GoogleUserRegisterRequest(googleId, email, name, profilePic);
            UserRegisterResponse registerResponse = userService.registerUserWithGoogle(registerRequest);
            writeResponse(response, registerResponse);
        }
    }

    private void writeResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), data);
    }
}