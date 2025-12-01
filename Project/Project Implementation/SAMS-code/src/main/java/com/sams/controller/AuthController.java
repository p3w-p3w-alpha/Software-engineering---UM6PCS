package com.sams.controller;

import com.sams.dto.LoginRequest;
import com.sams.dto.LoginResponse;
import com.sams.dto.RegisterRequest;
import com.sams.entity.User;
import com.sams.repository.UserRepository;
import com.sams.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * handles all authentication endpoints - login, token validation
 * used by frontend for user auth flow
 * NOTE: public registration is disabled, only admins can create accounts
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // constructor injection
    public AuthController(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // handles user login - returns JWT token on success
    // frontend calls this when user submits login form
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // chekc password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // parse permissions
        List<String> permissions = new ArrayList<>();
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                permissions = mapper.readValue(user.getPermissions(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception e) {
                permissions = new ArrayList<>();
            }
        }

        // create response
        LoginResponse response = new LoginResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getId(),
            permissions
        );

        return ResponseEntity.ok(response);
    }

    // REMOVED: Public registration is disabled
    // Only admins can create user accounts via /api/admin/users endpoint

    // validates JWT token - used to check if user session is still valid
    // make sure to include Bearer prefix in teh header
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            return ResponseEntity.ok(new TokenInfo(username, role, true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }

    // nested class for token info response
    public static class TokenInfo {
        private String username;
        private String role;
        private boolean valid;

        public TokenInfo(String username, String role, boolean valid) {
            this.username = username;
            this.role = role;
            this.valid = valid;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
    }
}
