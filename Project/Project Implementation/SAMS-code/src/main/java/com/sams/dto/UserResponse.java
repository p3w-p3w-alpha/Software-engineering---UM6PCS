package com.sams.dto;

import com.sams.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * response object for user information
 * used for displaying user profiles and account details accross teh app
 */
public class UserResponse {
    // user's unique identifier
    private Long id;

    // username for login
    private String username;

    // user's first name
    private String firstName;

    // user's last name
    private String lastName;

    // email address
    private String email;

    // role (STUDENT, FACULTY, ADMIN, etc)
    private String role;

    // list of permissions
    private List<String> permissions;

    // whether account is active
    private Boolean active;

    // account creation timestamp
    private LocalDateTime createdAt;

    // last update timestamp
    private LocalDateTime updatedAt;

    // phone number (optional)
    private String phone;

    // user biography/about section
    private String bio;

    // profile picture url
    private String profilePicture;

    // cover picture url
    private String coverPicture;

    // user's gender
    private String gender;

    // department they belong to
    private String department;

    // office location for faculty
    private String officeLocation;

    // list of user skills
    private List<String> skills;

    // list of user interests
    private List<String> interests;

    // linkedin profile url
    private String linkedinUrl;

    // github profile url
    private String githubUrl;

    // personal website url
    private String websiteUrl;

    public UserResponse() {
    }

    // Constructor for backward compatibility
    public UserResponse(Long id, String username, String email, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.permissions = new ArrayList<>();
        this.active = true;
    }

    // Constructor from User entity
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.getActive();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();

        // Profile fields
        this.phone = user.getPhone();
        this.bio = user.getBio();
        this.profilePicture = user.getProfilePicture();
        this.coverPicture = user.getCoverPicture();
        this.gender = user.getGender();
        this.department = user.getDepartment();
        this.officeLocation = user.getOfficeLocation();

        // Social links
        this.linkedinUrl = user.getLinkedinUrl();
        this.githubUrl = user.getGithubUrl();
        this.websiteUrl = user.getWebsiteUrl();

        // Parse permissions JSON string to List
        ObjectMapper mapper = new ObjectMapper();
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            try {
                this.permissions = mapper.readValue(user.getPermissions(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (JsonProcessingException e) {
                this.permissions = new ArrayList<>();
            }
        } else {
            this.permissions = new ArrayList<>();
        }

        // Parse skills JSON string to List
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            try {
                this.skills = mapper.readValue(user.getSkills(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (JsonProcessingException e) {
                this.skills = new ArrayList<>();
            }
        } else {
            this.skills = new ArrayList<>();
        }

        // Parse interests JSON string to List
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            try {
                this.interests = mapper.readValue(user.getInterests(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (JsonProcessingException e) {
                this.interests = new ArrayList<>();
            }
        } else {
            this.interests = new ArrayList<>();
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
