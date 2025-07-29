package com.example.fixit.component;

import com.example.fixit.dto.GoogleUserRegisterRequest;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.dto.UserRegisterResponse;
import com.example.fixit.service.AuthService;
import com.example.fixit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.server.ExportException;

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
            throw new IllegalArgumentException("Missing required attributes: email and/or name");
        }

        ResponseEntity<UserLoginResponse> loginEntity = authService.googleLogin(googleId);
        UserLoginResponse loginResponse = loginEntity.getBody();

        if (loginEntity.getStatusCode().is2xxSuccessful() && loginResponse != null && loginResponse.isSuccess()) {
            writeResponse(response, loginResponse);
        } else {
            GoogleUserRegisterRequest registerRequest = new GoogleUserRegisterRequest(googleId, email, name, profilePic);
            ResponseEntity<UserRegisterResponse> registerEntity = authService.googleRegister(registerRequest);
            UserRegisterResponse registerResponse = registerEntity.getBody();
            writeResponse(response, registerResponse);
        }

    }

    private void writeResponse(HttpServletResponse response, Object data) throws IOException {
        String baseUrl = "http://localhost:3000/auth-callback";
        String redirectUrl;

        if (data instanceof UserLoginResponse) {
            UserLoginResponse loginResponse = (UserLoginResponse) data;
            redirectUrl = String.format("%s?success=%b&userId=%d&name=%s&email=%s&profilePic=%s&isGoogle=%b&accessToken=%s&refreshToken=%s",
                    baseUrl,
                    loginResponse.isSuccess(),
                    loginResponse.getUserId(),
                    URLEncoder.encode(loginResponse.getName(), StandardCharsets.UTF_8),
                    URLEncoder.encode(loginResponse.getEmail(), StandardCharsets.UTF_8),
                    URLEncoder.encode(loginResponse.getProfilePic() != null ? loginResponse.getProfilePic() : "", StandardCharsets.UTF_8),
                    loginResponse.isGoogle(),
                    URLEncoder.encode(loginResponse.getAccessToken() != null ? loginResponse.getAccessToken() : "", StandardCharsets.UTF_8),
                    URLEncoder.encode(loginResponse.getRefreshToken() != null ? loginResponse.getRefreshToken() : "", StandardCharsets.UTF_8));
        } else if (data instanceof UserRegisterResponse) {
            UserRegisterResponse registerResponse = (UserRegisterResponse) data;
            redirectUrl = String.format("%s?success=%b&userId=%d&name=%s&email=%s&profilePic=%s&isGoogle=%b&accessToken=%s&refreshToken=%s",
                    baseUrl,
                    registerResponse.isSuccess(),
                    registerResponse.getUserId(),
                    URLEncoder.encode(registerResponse.getName(), StandardCharsets.UTF_8),
                    URLEncoder.encode(registerResponse.getEmail(), StandardCharsets.UTF_8),
                    URLEncoder.encode(registerResponse.getProfilePic() != null ? registerResponse.getProfilePic() : "", StandardCharsets.UTF_8),
                    registerResponse.isGoogle(),
                    URLEncoder.encode(registerResponse.getAccessToken() != null ? registerResponse.getAccessToken() : "", StandardCharsets.UTF_8),
                    URLEncoder.encode(registerResponse.getRefreshToken() != null ? registerResponse.getRefreshToken() : "", StandardCharsets.UTF_8));
        } else {
            throw new IllegalArgumentException("Unsupported response type");        }
        response.sendRedirect(redirectUrl);
    }
}