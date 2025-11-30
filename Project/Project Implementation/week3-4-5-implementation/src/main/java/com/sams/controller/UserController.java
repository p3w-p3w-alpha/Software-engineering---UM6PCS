package com.sams.controller;

import com.sams.dto.UserRequest;
import com.sams.dto.UserResponse;
import com.sams.entity.User;
import com.sams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create new user - POST /api/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        User savedUser = userService.createUser(user);
        return convertToResponse(savedUser);
    }

    // get all users - GET /api/users
    @GetMapping
    public List<UserResponse> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get user by id - GET /api/users/{id}
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return convertToResponse(user);
    }

    // update user - PUT /api/users/{id}
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        User userDetails = new User();
        userDetails.setUsername(request.getUsername());
        userDetails.setEmail(request.getEmail());
        userDetails.setPassword(request.getPassword());
        userDetails.setRole(request.getRole());

        User updatedUser = userService.updateUser(id, userDetails);
        return convertToResponse(updatedUser);
    }

    // delete user - DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // get user by email - GET /api/users/email/{email}
    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return convertToResponse(user);
    }

    // get users by role - GET /api/users/role/{role}
    @GetMapping("/role/{role}")
    public List<UserResponse> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // search users by username or email - GET /api/users/search?query=...
    @GetMapping("/search")
    public List<UserResponse> searchUsers(@RequestParam String query) {
        List<User> allUsers = userService.getAllUsers();
        return allUsers.stream()
                .filter(user ->
                    user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(query.toLowerCase())) ||
                    (user.getLastName() != null && user.getLastName().toLowerCase().contains(query.toLowerCase()))
                )
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // change user password - PUT /api/users/{id}/password
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody java.util.Map<String, String> passwordData) {
        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            if (currentPassword == null || currentPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Current password is required"));
            }
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "New password is required"));
            }
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "New password must be at least 6 characters"));
            }

            boolean success = userService.changePassword(id, currentPassword, newPassword);
            if (success) {
                return ResponseEntity.ok(java.util.Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Current password is incorrect"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to change password: " + e.getMessage()));
        }
    }

    // update user profile (for authenticated user updating their own profile)
    // PUT /api/users/{id}/profile
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id, @RequestBody java.util.Map<String, Object> profileData) {
        User user = userService.getUserById(id);

        // Update only profile fields (not role or password) with proper type validation
        if (profileData.containsKey("firstName")) {
            Object value = profileData.get("firstName");
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("firstName must be a string");
            }
            user.setFirstName(value != null ? (String) value : null);
        }
        if (profileData.containsKey("lastName")) {
            Object value = profileData.get("lastName");
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("lastName must be a string");
            }
            user.setLastName(value != null ? (String) value : null);
        }
        if (profileData.containsKey("email")) {
            Object value = profileData.get("email");
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("email must be a string");
            }
            String email = value != null ? (String) value : null;
            if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            user.setEmail(email);
        }
        if (profileData.containsKey("phone")) {
            Object value = profileData.get("phone");
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("phone must be a string");
            }
            user.setPhone(value != null ? (String) value : null);
        }
        if (profileData.containsKey("bio")) {
            Object value = profileData.get("bio");
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("bio must be a string");
            }
            user.setBio(value != null ? (String) value : null);
        }
        if (profileData.containsKey("profilePicture")) {
            Object value = profileData.get("profilePicture");
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("profilePicture must be a string");
            }
            user.setProfilePicture(value != null ? (String) value : null);
        }

        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(convertToResponse(updatedUser));
    }

    // helper method to convert User to UserResponse
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
