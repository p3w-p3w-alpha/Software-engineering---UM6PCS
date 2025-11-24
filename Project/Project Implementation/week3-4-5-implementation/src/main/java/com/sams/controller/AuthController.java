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

    // login endpoint - POST /api/auth/login
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

        // create response
        LoginResponse response = new LoginResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getId()
        );

        return ResponseEntity.ok(response);
    }

    // register endpoint - POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }

        // check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists");
        }

        // create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "STUDENT");
        user.setActive(true);

        // save user
        user = userRepository.save(user);

        // generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // create response
        LoginResponse response = new LoginResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // endpoint to validate token - GET /api/auth/validate
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
