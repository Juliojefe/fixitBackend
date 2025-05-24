package com.example.fixit;

import com.example.fixit.dto.*;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import com.example.fixit.repository.UserRepository;
import com.example.fixit.repository.UserRolesRepository;
import com.example.fixit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // --- registerUser Tests ---
    @Test
    void registerUser_successfulRegistration() {
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password123", "pic.jpg");

        UserRegisterResponse response = userService.registerUser(request);

        assertTrue(response.getSuccess());
        User savedUser = userRepository.findByEmail("john@example.com").orElseThrow();
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("pic.jpg", savedUser.getProfilePic());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    void registerUser_duplicateEmail() {
        // Arrange: Create a user with the same email
        User existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("john@example.com");
        existingUser.setPassword("password");
        userRepository.save(existingUser);

        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password123", "pic.jpg");

        UserRegisterResponse response = userService.registerUser(request);

        assertFalse(response.getSuccess());
    }

    // --- loginUser Tests ---
    @Test
    void loginUser_successfulLogin() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest("john@example.com", "password123");

        UserLoginResponse response = userService.loginUser(request);

        assertTrue(response.getSuccess());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void loginUser_wrongPassword() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest("john@example.com", "wrongpassword");

        UserLoginResponse response = userService.loginUser(request);

        assertFalse(response.getSuccess());
    }

    @Test
    void loginUser_nonExistentEmail() {
        UserLoginRequest request = new UserLoginRequest("john@example.com", "password123");

        UserLoginResponse response = userService.loginUser(request);

        assertFalse(response.getSuccess());
    }

    // --- getAllUsers Tests ---
    @Test
    void getAllUsers_withUsers() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        userRepository.save(user);

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
    }

    @Test
    void getAllUsers_noUsers() {
        // Assuming the database is empty or we clear it for this test
        userRepository.deleteAll(); // Clear the database (will be rolled back)

        List<User> users = userService.getAllUsers();

        assertTrue(users.isEmpty());
    }

    // --- updateName Tests ---
    @Test
    void updateName_success() {
        // Arrange: Create a user
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        UpdateNameRequest request = new UpdateNameRequest(user.getUserId(), "New Name");

        boolean result = userService.updateName(request);

        assertTrue(result);
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
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
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("old@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        UpdateEmailRequest request = new UpdateEmailRequest(user.getUserId(), "new@example.com");

        boolean result = userService.updateEmail(request);

        assertTrue(result);
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals("new@example.com", updatedUser.getEmail());
    }

    @Test
    void updateEmail_emailInUseByAnotherUser() {
        // Arrange: Create two users
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        userRepository.save(user2);

        UpdateEmailRequest request = new UpdateEmailRequest(user1.getUserId(), "user2@example.com");

        boolean result = userService.updateEmail(request);

        assertFalse(result);
    }

    // --- updatePassword Tests ---
    @Test
    void updatePassword_success() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("oldPass"));
        user = userRepository.save(user);

        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getUserId(), "oldPass", "newPass");

        boolean result = userService.updatePassword(request);

        assertTrue(result);
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPass", updatedUser.getPassword()));
    }

    @Test
    void updatePassword_wrongOldPassword() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("oldPass"));
        userRepository.save(user);

        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getUserId(), "wrongPass", "newPass");

        boolean result = userService.updatePassword(request);

        assertFalse(result);
    }

    // --- makeAdmin Tests ---
    @Test
    @Transactional
    public void makeAdmin_noExistingRoles() {
        // Arrange: Create a user with no roles
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userRepository.save(user); // Persist the user

        // Act: Make the user an admin
        boolean result = userService.makeAdmin(user.getUserId());
        assertTrue(result);

        // Assert: Fetch the updated user from the database
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertNotNull(updatedUser.getUserRoles());
        assertTrue(updatedUser.getUserRoles().getIsAdmin());
    }

    @Test
    void makeAdmin_userNotFound() {
        boolean result = userService.makeAdmin(999);

        assertFalse(result);
    }

    // --- makeMechanic Tests ---
    @Test
    @Transactional
    public void makeMechanic_existingRoles() {
        // Arrange: Create a user with existing roles
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userRepository.save(user); // Persist the user

        UserRoles roles = new UserRoles(user.getUserId(), false, false);
        roles.setUser(user); // Set the back-reference
        user.setUserRoles(roles); // Set the forward-reference
        userRepository.save(user); // Persist the user with roles

        // Act: Make the user a mechanic
        boolean result = userService.makeMechanic(user.getUserId());
        assertTrue(result);

        // Assert: Fetch the updated user from the database
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertNotNull(updatedUser.getUserRoles());
        assertTrue(updatedUser.getUserRoles().getIsMechanic());
    }

    // --- makeRegularUser Tests ---
    @Test
    @Transactional
    public void makeRegularUser_withRoles() {
        // Arrange: Create a user with existing roles
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userRepository.save(user); // Persist the user

        UserRoles roles = new UserRoles(user.getUserId(), true, true); // Some existing roles
        roles.setUser(user); // Set the back-reference
        user.setUserRoles(roles); // Set the forward-reference
        userRepository.save(user); // Persist the user with roles

        // Act: Make the user a regular user (e.g., remove special roles)
        boolean result = userService.makeRegularUser(user.getUserId());
        assertTrue(result);

        // Assert: Fetch the updated user from the database
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertNotNull(updatedUser.getUserRoles());
        assertFalse(updatedUser.getUserRoles().getIsAdmin());
        assertFalse(updatedUser.getUserRoles().getIsMechanic());
    }

    // --- updateProfilePic Tests ---
    @Test
    void updateProfilePic_success() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        UpdateProfilePicRequest request = new UpdateProfilePicRequest(user.getUserId(), "newpic.jpg");

        boolean result = userService.updateProfilePic(request);

        assertTrue(result);
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals("newpic.jpg", updatedUser.getProfilePic());
    }

    // --- deleteUser Tests ---
    @Test
    void deleteUser_success() {
        // Arrange: Create a user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        boolean result = userService.deleteUser(user.getUserId());

        assertTrue(result);
        assertTrue(userRepository.findById(user.getUserId()).isEmpty());
    }

    @Test
    void deleteUser_notFound() {
        boolean result = userService.deleteUser(999);

        assertFalse(result);
    }
}