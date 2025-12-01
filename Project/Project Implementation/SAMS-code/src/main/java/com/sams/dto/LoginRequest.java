package com.sams.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * request DTO for user login
 * frontend sends this to authenticate users
 */
public class LoginRequest {

    // username for login
    @NotBlank(message = "Username is required")
    private String username;

    // user password (gets hashed on backend)
    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
