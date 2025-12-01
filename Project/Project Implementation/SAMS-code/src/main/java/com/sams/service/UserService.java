package com.sams.service;

import com.sams.entity.User;
import com.sams.exception.DuplicateEmailException;
import com.sams.exception.UserNotFoundException;
import com.sams.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * handles user management - creating users, updating profiles, password changes
 * uses BCrypt for password hashing (dont store plain text passwords!)
 *
 * supports profile updates with validation and duplicate email checking
 * also handles skills/interests as JSON arrays
 *
 * works pretty well, might need to add more validation later
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // constructor injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // create new user
    @Transactional
    public User createUser(User user) {
        // chekc if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }

        // validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }

        // hash the password before saving
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    // get user by id
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    // get user by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));
    }

    // get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // get users by role
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // update user
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        // check if email is being changed and if new email already exists
        if (!user.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new DuplicateEmailException(userDetails.getEmail());
            }
        }

        // update fields
        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }

        // hash password if it's being updated
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        return userRepository.save(user);
    }

    // delete user
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // check if email exists
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // simple save user (for profile updates without password change)
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // change user password with verification of current password
    @Transactional
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    // update user profile (firstName, lastName, bio, etc. - not role or password)
    // TODO: this method is kinda long, should probably refactor it
    @Transactional
    public User updateProfile(Long id, java.util.Map<String, Object> profileData) {
        User user = getUserById(id);

        if (profileData.containsKey("firstName")) {
            Object value = profileData.get("firstName");
            user.setFirstName(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("lastName")) {
            Object value = profileData.get("lastName");
            user.setLastName(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("email")) {
            Object value = profileData.get("email");
            if (value != null) {
                String email = String.valueOf(value);
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new IllegalArgumentException("Invalid email format");
                }
                // Check for duplicate email
                if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
                    throw new DuplicateEmailException(email);
                }
                user.setEmail(email);
            }
        }
        if (profileData.containsKey("phone")) {
            Object value = profileData.get("phone");
            user.setPhone(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("bio")) {
            Object value = profileData.get("bio");
            user.setBio(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("profilePicture")) {
            Object value = profileData.get("profilePicture");
            user.setProfilePicture(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("coverPicture")) {
            Object value = profileData.get("coverPicture");
            user.setCoverPicture(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("gender")) {
            Object value = profileData.get("gender");
            user.setGender(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("department")) {
            Object value = profileData.get("department");
            user.setDepartment(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("officeLocation")) {
            Object value = profileData.get("officeLocation");
            user.setOfficeLocation(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("linkedinUrl")) {
            Object value = profileData.get("linkedinUrl");
            user.setLinkedinUrl(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("githubUrl")) {
            Object value = profileData.get("githubUrl");
            user.setGithubUrl(value != null ? String.valueOf(value) : null);
        }
        if (profileData.containsKey("websiteUrl")) {
            Object value = profileData.get("websiteUrl");
            user.setWebsiteUrl(value != null ? String.valueOf(value) : null);
        }
        // Handle skills as JSON array
        if (profileData.containsKey("skills")) {
            Object value = profileData.get("skills");
            if (value != null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    if (value instanceof java.util.List) {
                        user.setSkills(mapper.writeValueAsString(value));
                    } else if (value instanceof String) {
                        user.setSkills((String) value);
                    }
                } catch (Exception e) {
                    user.setSkills(null);
                }
            } else {
                user.setSkills(null);
            }
        }
        // Handle interests as JSON array
        if (profileData.containsKey("interests")) {
            Object value = profileData.get("interests");
            if (value != null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    if (value instanceof java.util.List) {
                        user.setInterests(mapper.writeValueAsString(value));
                    } else if (value instanceof String) {
                        user.setInterests((String) value);
                    }
                } catch (Exception e) {
                    user.setInterests(null);
                }
            } else {
                user.setInterests(null);
            }
        }

        return userRepository.save(user);
    }
}
