package com.example.fixit.service;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import com.example.fixit.dto.UserRegisterRequest;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // --- registerUser Tests ---
    @Test
    void registerUser_successfulRegistration() {
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "pic.jpg");
        UserRegisterResponse response = userService.registerUser(request);
        assertTrue(response.getSuccess());
        User savedUser = userRepository.findByEmail("john@example.com").orElseThrow();
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("pic.jpg", savedUser.getProfilePic());
        assertTrue(passwordEncoder.matches("Password123!", savedUser.getPassword()));
    }

    @Test
    void registerUser_invalidPassword() {
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password", "pic.jpg");
        UserRegisterResponse response = userService.registerUser(request);
        assertFalse(response.getSuccess(), "Registration with invalid password should fail");
    }

    @Test
    void registerUser_duplicateEmail() {
        UserRegisterRequest firstRequest = new UserRegisterRequest("Existing User", "john@example.com", "Password123!", "");
        UserRegisterResponse firstResponse = userService.registerUser(firstRequest);
        assertTrue(firstResponse.getSuccess());

        // Try to register second user with the same email
        UserRegisterRequest secondRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "pic.jpg");
        UserRegisterResponse secondResponse = userService.registerUser(secondRequest);
        assertFalse(secondResponse.getSuccess());
    }

    // --- loginUser Tests ---
    @Test
    void loginUser_successfulLogin() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "img.jpg");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Login with the registered user
        UserLoginRequest loginRequest = new UserLoginRequest("john@example.com", "Password123!");
        UserLoginResponse loginResponse = userService.loginUser(loginRequest);
        assertTrue(loginResponse.getSuccess());
//        assertEquals("John Doe", loginResponse.getName());
    }

    @Test
    void loginUser_wrongPassword() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Try to login with wrong password
        UserLoginRequest loginRequest = new UserLoginRequest("john@example.com", "wrongpassword-Password123!");
        UserLoginResponse loginResponse = userService.loginUser(loginRequest);
        assertFalse(loginResponse.getSuccess());
    }

    @Test
    void loginUser_nonExistentEmail() {
        UserLoginRequest request = new UserLoginRequest("john@example.com", "Password123!");
        UserLoginResponse response = userService.loginUser(request);
        assertFalse(response.getSuccess());
    }

    // --- getAllUsers Tests ---
    @Test
    void getAllUsers_withUsers() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Get all users
        List<User> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    void getAllUsers_noUsers() {
        // Ensure database is empty
        userRepository.deleteAll();
        List<User> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void updateName_success() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("Old Name", "old@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Update name
        UpdateNameRequest updateRequest = new UpdateNameRequest(registerResponse.getUserId(), "New Name");
        boolean result = userService.updateName(updateRequest);
        assertTrue(result);

        // Verify update
        User updatedUser = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        assertEquals("New Name", updatedUser.getName());
    }

    @Test
    void updateName_userNotFound() {
        UpdateNameRequest request = new UpdateNameRequest(999, "New Name");
        boolean result = userService.updateName(request);
        assertFalse(result);
    }

    // --- updateEmail Tests ---
    @Test
    void updateEmail_successNewEmail() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "old@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Update email
        UpdateEmailRequest updateRequest = new UpdateEmailRequest(registerResponse.getUserId(), "new@example.com");
        boolean result = userService.updateEmail(updateRequest);
        assertTrue(result);

        // Verify update
        User updatedUser = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        assertEquals("new@example.com", updatedUser.getEmail());
    }

    @Test
    void updateEmail_emailInUseByAnotherUser() {
        // Register first user
        UserRegisterRequest firstRequest = new UserRegisterRequest("User1", "user1@example.com", "Password123!", "");
        UserRegisterResponse firstResponse = userService.registerUser(firstRequest);
        assertTrue(firstResponse.getSuccess());

        // Register second user
        UserRegisterRequest secondRequest = new UserRegisterRequest("User2", "user2@example.com", "Password123!", "");
        UserRegisterResponse secondResponse = userService.registerUser(secondRequest);
        assertTrue(secondResponse.getSuccess());

        // Try to update first user's email to second user's email
        UpdateEmailRequest updateRequest = new UpdateEmailRequest(firstResponse.getUserId(), "user2@example.com");
        boolean result = userService.updateEmail(updateRequest);
        assertFalse(result);
    }

    // --- updatePassword Tests ---
    @Test
    void updatePassword_success() {
        // Register user
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Update password
        UpdatePasswordRequest updateRequest = new UpdatePasswordRequest(registerResponse.getUserId(), "Password123!", "Password321!");
        boolean result = userService.updatePassword(updateRequest);
        assertTrue(result);

        // Verify update
        User updatedUser = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        assertTrue(passwordEncoder.matches("Password321!", updatedUser.getPassword()));
    }

    @Test
    void updatePassword_wrongOldPassword() {
        // Register user
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "oldPass-Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Try to update with wrong old password
        UpdatePasswordRequest updateRequest = new UpdatePasswordRequest(registerResponse.getUserId(), "wrongPass-Password123!", "newPass-Password123!");
        boolean result = userService.updatePassword(updateRequest);
        assertFalse(result);
    }

    // --- makeAdmin Tests ---
    @Test
    void makeAdmin_noExistingRoles() {
        UserRegisterRequest urr = new UserRegisterRequest("Test User", "test@example.com", "Password123!", "");
        UserRegisterResponse userRegisterResponse = userService.registerUser(urr);
        assertTrue(userRegisterResponse.getSuccess());

        Optional<User> userOptional = userRepository.findById(userRegisterResponse.getUserId());
        assertTrue(userOptional.isPresent());

        User user = userOptional.get();
        boolean result = userService.makeAdmin(user.getUserId());
        assertTrue(result);

        Optional<User> updatedUserOptional = userRepository.findById(user.getUserId());
        assertTrue(updatedUserOptional.isPresent());

        User updatedUser = updatedUserOptional.get();
        assertTrue(updatedUser.getUserRoles().getIsAdmin());
    }

    @Test
    void makeAdmin_userNotFound() {
        boolean result = userService.makeAdmin(999);
        assertFalse(result);
    }

    // --- makeMechanic Tests ---
    @Test
    public void makeMechanic_existingRoles() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("Test User", "test@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Make the user a mechanic
        boolean result = userService.makeMechanic(registerResponse.getUserId());
        assertTrue(result);

        // Verify update
        User updatedUser = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        assertNotNull(updatedUser.getUserRoles());
        assertTrue(updatedUser.getUserRoles().getIsMechanic());
    }

    // --- makeRegularUser Tests ---
    @Test
    public void makeRegularUser_withRoles() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("Test User", "test@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Set roles to true
        User user = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        UserRoles roles = user.getUserRoles();
        roles.setIsAdmin(true);
        roles.setIsMechanic(true);
        userRolesRepository.save(roles);

        // Make the user a regular user
        boolean result = userService.makeRegularUser(user.getUserId());
        assertTrue(result);

        // Verify update
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertNotNull(updatedUser.getUserRoles());
        assertFalse(updatedUser.getUserRoles().getIsAdmin());
        assertFalse(updatedUser.getUserRoles().getIsMechanic());
    }

    // --- updateProfilePic Tests ---
    @Test
    void updateProfilePic_success() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "oldpic.jpg");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Update profile picture
        UpdateProfilePicRequest updateRequest = new UpdateProfilePicRequest(registerResponse.getUserId(), "newpic.jpg");
        boolean result = userService.updateProfilePic(updateRequest);
        assertTrue(result);

        // Verify update
        User updatedUser = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        assertEquals("newpic.jpg", updatedUser.getProfilePic());
    }

    // --- deleteUser Tests ---
    @Test
    void deleteUser_success() {
        UserRegisterRequest registerRequest = new UserRegisterRequest("John Doe", "john@example.com", "Password123!", "");
        UserRegisterResponse registerResponse = userService.registerUser(registerRequest);
        assertTrue(registerResponse.getSuccess());

        // Delete user
        boolean result = userService.deleteUser(registerResponse.getUserId());
        assertTrue(result);
        assertTrue(userRepository.findById(registerResponse.getUserId()).isEmpty());
    }

    @Test
    void deleteUser_notFound() {
        boolean result = userService.deleteUser(999);
        assertFalse(result);
    }

    @Test
    public void testIsValidPassword_validPassword() {
        assertTrue(userService.isValidPassword("Password123!"), "Valid password should pass");
    }

    @Test
    public void testIsValidPassword_missingUppercase() {
        assertFalse(userService.isValidPassword("password123!"), "Password without uppercase should fail");
    }

    @Test
    public void testIsValidPassword_missingLowercase() {
        assertFalse(userService.isValidPassword("PASSWORD123!"), "Password without lowercase should fail");
    }

    @Test
    public void testIsValidPassword_missingDigit() {
        assertFalse(userService.isValidPassword("Password!abc"), "Password without digit should fail");
    }

    @Test
    public void testIsValidPassword_missingSpecialChar() {
        assertFalse(userService.isValidPassword("Password123"), "Password without special character should fail");
    }

    @Test
    public void testIsValidPassword_tooShort() {
        assertFalse(userService.isValidPassword("Pass1!"), "Password shorter than 8 characters should fail");
    }

    @Test
    public void testIsValidPassword_nullPassword() {
        assertFalse(userService.isValidPassword(null), "Null password should fail");
    }
}