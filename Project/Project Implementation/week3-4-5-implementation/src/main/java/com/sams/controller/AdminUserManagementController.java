package com.sams.controller;

import com.sams.dto.CreateUserRequest;
import com.sams.dto.UserResponse;
import com.sams.entity.User;
import com.sams.repository.UserRepository;
import com.sams.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminUserManagementController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public AdminUserManagementController(UserRepository userRepository,
                                         PasswordEncoder passwordEncoder,
                                         JwtUtil jwtUtil,
                                         ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    // GET all users - GET /api/admin/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET user by ID - GET /api/admin/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return ResponseEntity.ok(new UserResponse(user));
    }

    // CREATE user - POST /api/admin/users
    // Only ADMIN or SUPER_ADMIN can create users
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request,
                                        @RequestHeader("Authorization") String authHeader) {

        // Extract current user from token
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.getUsernameFromToken(token);
        String currentUserRole = jwtUtil.getRoleFromToken(token);

        // Check if trying to create SUPER_ADMIN (only SUPER_ADMIN can do this)
        if ("SUPER_ADMIN".equals(request.getRole()) && !"SUPER_ADMIN".equals(currentUserRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only SUPER_ADMIN can create SUPER_ADMIN accounts");
        }

        // Check if trying to create ADMIN with permissions (only SUPER_ADMIN can do this)
        if ("ADMIN".equals(request.getRole()) && !"SUPER_ADMIN".equals(currentUserRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only SUPER_ADMIN can create ADMIN accounts");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        // Set permissions if provided (JSON array string)
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            try {
                String permissionsJson = objectMapper.writeValueAsString(request.getPermissions());
                user.setPermissions(permissionsJson);
            } catch (Exception e) {
                user.setPermissions("[]");
            }
        }

        // Save user
        user = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
    }

    // UPDATE user - PUT /api/admin/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                       @Valid @RequestBody CreateUserRequest request,
                                       @RequestHeader("Authorization") String authHeader) {

        // Extract current user from token
        String token = authHeader.substring(7);
        String currentUserRole = jwtUtil.getRoleFromToken(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if trying to modify SUPER_ADMIN (only SUPER_ADMIN can do this)
        if ("SUPER_ADMIN".equals(user.getRole()) && !"SUPER_ADMIN".equals(currentUserRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only SUPER_ADMIN can modify SUPER_ADMIN accounts");
        }

        // Update fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update role (with permission check)
        if ("SUPER_ADMIN".equals(request.getRole()) && !"SUPER_ADMIN".equals(currentUserRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only SUPER_ADMIN can assign SUPER_ADMIN role");
        }
        user.setRole(request.getRole());

        // Update permissions
        if (request.getPermissions() != null) {
            try {
                String permissionsJson = objectMapper.writeValueAsString(request.getPermissions());
                user.setPermissions(permissionsJson);
            } catch (Exception e) {
                user.setPermissions("[]");
            }
        }

        user = userRepository.save(user);

        return ResponseEntity.ok(new UserResponse(user));
    }

    // DELETE user (soft delete) - DELETE /api/admin/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id,
                                        @RequestHeader("Authorization") String authHeader) {

        // Extract current user from token
        String token = authHeader.substring(7);
        String currentUserRole = jwtUtil.getRoleFromToken(token);
        String currentUsername = jwtUtil.getUsernameFromToken(token);

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent deleting yourself
        if (user.getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot delete your own account");
        }

        // SUPER_ADMIN accounts cannot be deleted by anyone (system protection)
        if ("SUPER_ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("SUPER_ADMIN accounts cannot be deleted for system integrity");
        }

        // Only SUPER_ADMIN can delete ADMIN accounts
        if ("ADMIN".equals(user.getRole()) && !"SUPER_ADMIN".equals(currentUserRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only SUPER_ADMIN can delete ADMIN accounts");
        }

        // Soft delete
        user.softDelete(currentUser.getId());
        userRepository.save(user);

        return ResponseEntity.ok().body("User deleted successfully");
    }

    // TOGGLE user active status - PATCH /api/admin/users/{id}/toggle-active
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActiveStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setActive(!user.getActive());
        user = userRepository.save(user);

        return ResponseEntity.ok(new UserResponse(user));
    }

    // GET available permissions list - GET /api/admin/users/permissions
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<String>> getAvailablePermissions() {
        // Define all available permissions
        List<String> permissions = List.of(
            "USER_CREATE",
            "USER_READ",
            "USER_UPDATE",
            "USER_DELETE",
            "COURSE_CREATE",
            "COURSE_UPDATE",
            "COURSE_DELETE",
            "ENROLLMENT_MANAGE",
            "GRADE_MANAGE",
            "ASSIGNMENT_CREATE",
            "ASSIGNMENT_MANAGE",
            "NOTIFICATION_SEND",
            "REPORT_VIEW",
            "SYSTEM_SETTINGS"
        );
        return ResponseEntity.ok(permissions);
    }
}
