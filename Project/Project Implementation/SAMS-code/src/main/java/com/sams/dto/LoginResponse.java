package com.sams.dto;

import java.util.List;

/**
 * response object sent after successful login
 * contains JWT token and user info for teh frontend to store
 */
public class LoginResponse {

    // JWT authentication token
    private String token;

    // user's username
    private String username;

    // user's email address
    private String email;

    // user's role in teh system
    private String role;

    // unique user id
    private Long userId;

    // list of permissions granted to this user
    private List<String> permissions;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String email, String role, Long userId) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.userId = userId;
    }

    public LoginResponse(String token, String username, String email, String role, Long userId, List<String> permissions) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.permissions = permissions;
    }

    // getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
