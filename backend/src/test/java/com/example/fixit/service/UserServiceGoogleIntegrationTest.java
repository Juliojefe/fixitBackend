package com.example.fixit.service;

import com.example.fixit.dto.GoogleUserRegisterRequest;
import com.example.fixit.dto.UserLoginResponse;
import com.example.fixit.dto.UserRegisterRequest;
import com.example.fixit.dto.UserRegisterResponse;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceGoogleIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void registerUserWithGoogle_successfulRegistration() {
        GoogleUserRegisterRequest request = new GoogleUserRegisterRequest("google123", "john@example.com", "John Doe", "pic.jpg");
        UserRegisterResponse response = userService.registerUserWithGoogle(request);

        assertTrue(response.getSuccess(), "Google user registration should succeed");
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("pic.jpg", response.getProfilePic());
        assertTrue(response.isGoogle(), "Response should indicate a Google user");

        User savedUser = userRepository.findByEmail("john@example.com").orElseThrow();
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("pic.jpg", savedUser.getProfilePic());
        assertNull(savedUser.getPassword(), "Password should be null for Google users");
        assertEquals("google123", savedUser.getGoogleId());
        assertFalse(savedUser.getUserRoles().getIsAdmin(), "New user should not be admin");
        assertFalse(savedUser.getUserRoles().getIsMechanic(), "New user should not be mechanic");
    }

    @Test
    void registerUserWithGoogle_duplicateEmail() {
        // Register a regular user first
        UserRegisterRequest regularRequest = new UserRegisterRequest("Existing User", "john@example.com", "Password123!", "pic.jpg");
        UserRegisterResponse regularResponse = userService.registerUser(regularRequest);
        assertTrue(regularResponse.getSuccess(), "Regular user registration should succeed");

        // Attempt Google registration with the same email
        GoogleUserRegisterRequest googleRequest = new GoogleUserRegisterRequest("google123", "john@example.com", "John Doe", "pic.jpg");
        UserRegisterResponse googleResponse = userService.registerUserWithGoogle(googleRequest);
        assertFalse(googleResponse.getSuccess(), "Google registration with duplicate email should fail");
        assertEquals("email already exists", googleResponse.getName());
    }

    @Test
    void loginUserWithGoogle_successfulLogin() {
        // Register a Google user
        GoogleUserRegisterRequest registerRequest = new GoogleUserRegisterRequest("google123", "john@example.com", "John Doe", "pic.jpg");
        UserRegisterResponse registerResponse = userService.registerUserWithGoogle(registerRequest);
        assertTrue(registerResponse.getSuccess(), "Google user registration should succeed");

        // Login with the googleId
        UserLoginResponse loginResponse = userService.loginUserWithGoogle("google123");

        assertTrue(loginResponse.getSuccess(), "Google user login should succeed");
        assertEquals("John Doe", loginResponse.getName());
        assertEquals("john@example.com", loginResponse.getEmail());
        assertEquals("pic.jpg", loginResponse.getProfilePic());
        assertTrue(loginResponse.isGoogle(), "Response should indicate a Google user");
    }

    @Test
    void loginUserWithGoogle_nonExistentGoogleId() {
        UserLoginResponse response = userService.loginUserWithGoogle("nonExistentGoogleId");

        assertFalse(response.getSuccess(), "Login with non-existent googleId should fail");
        assertEquals("Google user not found, please register first", response.getName());
    }
}