package com.sams.service;

import com.sams.entity.User;
import com.sams.exception.DuplicateEmailException;
import com.sams.exception.UserNotFoundException;
import com.sams.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("STUDENT");
    }

    @Test
    void testCreateUser_Success() {
        // arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // act
        User savedUser = userService.createUser(testUser);

        // assert
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("STUDENT", savedUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // act & assert
        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser(testUser);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Found() {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // act
        User foundUser = userService.getUserById(1L);

        // assert
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("testuser", foundUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        // arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void testUpdateUser_Success() {
        // arrange
        User updatedDetails = new User();
        updatedDetails.setUsername("updateduser");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setPassword("newpassword");
        updatedDetails.setRole("FACULTY");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // act
        User result = userService.updateUser(1L, updatedDetails);

        // assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeleteUser_Success() {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // act
        userService.deleteUser(1L);

        // assert
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testGetAllUsers() {
        // arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> userList = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // act
        List<User> result = userService.getAllUsers();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUsersByRole() {
        // arrange
        List<User> students = Arrays.asList(testUser);
        when(userRepository.findByRole("STUDENT")).thenReturn(students);

        // act
        List<User> result = userService.getUsersByRole("STUDENT");

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("STUDENT", result.get(0).getRole());
        verify(userRepository, times(1)).findByRole("STUDENT");
    }
}
