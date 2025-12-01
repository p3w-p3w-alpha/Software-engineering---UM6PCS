package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity - represents all users in the system (students, faculty, admins)
 * this is probaly the most important entity in the whole app
 *
 * we use soft deletes here so we dont lose user data when "deleting" accounts
 * the active field is what we check, not actually removing rows from db
 */
@Entity
@Table(name = "users", indexes = {
    // indexes for common queries - makes lookups faster
    @Index(name = "idx_user_role", columnList = "role"),
    @Index(name = "idx_user_active", columnList = "active"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_username", columnList = "username")
})
public class User {

    // auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username for login - must be unique
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // email also unique - used for password reset n stuff
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    // password is hashed with bcrypt - never store plaintext!!
    @NotBlank(message = "Password is required")
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    // role determins what user can access - STUDENT, FACULTY, ADMIN, SUPER_ADMIN
    @Column(nullable = false)
    private String role = "STUDENT"; // default to student

    // permissions stored as JSON - bit of a hack but works
    // TODO: maybe use a seperate permissions table in future?
    @Column(columnDefinition = "TEXT")
    private String permissions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // soft delete fields - for keeping historical data instead of hard deletes
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy; // id of user who deleted this record

    // Profile fields
    @Column(name = "phone")
    private String phone;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_picture")
    private String profilePicture;

    // Gender field for demographics (MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY)
    @Column(name = "gender")
    private String gender;

    // Enhanced profile fields
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills; // JSON array of skills ["Java", "Python", "SQL"]

    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests; // JSON array of interests ["Machine Learning", "Web Dev"]

    @Column(name = "office_location")
    private String officeLocation; // For faculty: "Building A, Room 301"

    @Column(name = "department")
    private String department; // Computer Science, Mathematics, etc.

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "cover_picture")
    private String coverPicture; // Cover image for profile page

    // JPA lifecycle hooks - automatically set timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        // update timestamp on every change
        updatedAt = LocalDateTime.now();
    }

    /**
     * soft delete - we dont actually remove the user from db
     * just mark them as inactive and track who deleted them
     * this way we can restore accounts if needed
     */
    public void softDelete(Long deletedByUserId) {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUserId;
    }

    // default constructor - JPA needs this
    public User() {
    }

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // getters and setters
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
