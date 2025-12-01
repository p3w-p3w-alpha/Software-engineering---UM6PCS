package com.sams.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * request object for updating user information
 * used when modifying existing user details
 */
public class UserRequest {

    // username must be between 3-50 chars
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    // email for teh user account
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    // password needs to be at least 6 characters
    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    // user role like STUDENT, FACULTY, etc
    private String role;

    public UserRequest() {
    }

    public UserRequest(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
