# Testing Explained - JUnit & Mockito

## What is Unit Testing?

**Unit Testing** means testing individual "units" of code in isolation.

### Real-World Analogy

Building a car:
- **Unit Test**: Test each part separately (engine works, brakes work, lights work)
- **Integration Test**: Test how parts work together (engine + transmission)
- **End-to-End Test**: Test the whole car driving

### Why Write Tests?

1. **Catch bugs early**: Find problems before users do
2. **Confidence**: Know your code works when you change it
3. **Documentation**: Tests show how code should be used
4. **Save time**: Automated tests faster than manual testing

## JUnit - The Testing Framework

**JUnit** is the most popular Java testing framework.

### Basic Test Structure

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void testAddition() {
        // Arrange - Set up test data
        Calculator calc = new Calculator();

        // Act - Execute the code being tested
        int result = calc.add(2, 3);

        // Assert - Verify the result
        assertEquals(5, result);
    }
}
```

### Common Assertions

```java
// Check equality
assertEquals(expected, actual);
assertEquals(5, result);

// Check not null
assertNotNull(user);

// Check true/false
assertTrue(user.isActive());
assertFalse(user.isDeleted());

// Check exceptions
assertThrows(UserNotFoundException.class, () -> {
    userService.getUserById(999L);
});

// Check arrays/lists
assertEquals(3, users.size());
```

## Mockito - The Mocking Framework

**Mockito** lets you create "fake" objects for testing.

### Why Mock?

**Problem**: UserService needs UserRepository to work. But in unit tests:
- We don't want to connect to a real database
- Database is slow
- We want to test service logic, not database

**Solution**: Create a "fake" repository that pretends to be real!

### Real vs Mock

**Without Mocking (Integration Test):**
```java
@Test
void testCreateUser() {
    UserRepository repo = new UserRepository();  // Real repository
    UserService service = new UserService(repo);

    User user = service.createUser(new User(...));

    // Actually saves to database!
    // Slow, needs DB running, data persists
}
```

**With Mocking (Unit Test):**
```java
@Test
void testCreateUser() {
    UserRepository mockRepo = mock(UserRepository.class);  // Fake!
    UserService service = new UserService(mockRepo);

    // Tell mock what to return
    when(mockRepo.save(any())).thenReturn(new User(...));

    User user = service.createUser(new User(...));

    // Never touches database!
    // Fast, no DB needed, isolated
}
```

## Understanding @Mock and @InjectMocks

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;  // Create a fake repository

    @InjectMocks
    private UserService userService;  // Create real service with fake repo injected

    @Test
    void testSomething() {
        // userService now uses the mock userRepository
    }
}
```

### What Happens?

1. `@Mock` creates a fake UserRepository
2. `@InjectMocks` creates a real UserService
3. Mockito automatically injects the mock repository into the service
4. Now you can test service logic without touching the database!

## when().thenReturn() - Stubbing

**Stubbing** means telling mocks what to return.

```java
// When this method is called...
when(userRepository.findById(1L))
    // ...return this value
    .thenReturn(Optional.of(testUser));

// Now when the service calls findById(1L), it gets testUser
User user = userService.getUserById(1L);
assertEquals("john_doe", user.getUsername());
```

### More Examples

```java
// Return specific value
when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

// Return different values on multiple calls
when(userRepository.findAll())
    .thenReturn(Arrays.asList(user1, user2));

// Throw exception
when(userRepository.findById(999L))
    .thenThrow(new UserNotFoundException(999L));

// Match any argument
when(userRepository.save(any(User.class)))
    .thenReturn(testUser);
```

## verify() - Checking Method Calls

**verify()** checks if a method was called.

```java
@Test
void testCreateUser() {
    // Arrange
    when(userRepository.save(any())).thenReturn(testUser);

    // Act
    userService.createUser(testUser);

    // Assert - Verify save was called
    verify(userRepository).save(testUser);
}
```

### More verify() Examples

```java
// Verify method was called once
verify(userRepository, times(1)).save(testUser);

// Verify method was never called
verify(userRepository, never()).delete(any());

// Verify method was called with specific argument
verify(userRepository).findById(1L);

// Verify method was called at least once
verify(userRepository, atLeastOnce()).findAll();
```

## The AAA Pattern

Every good test follows **AAA**:

1. **Arrange**: Set up test data
2. **Act**: Execute the code being tested
3. **Assert**: Verify the result

```java
@Test
void testCreateUser_Success() {
    // ===== ARRANGE =====
    // Create test user
    User testUser = new User();
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");

    // Set up mock behavior
    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // ===== ACT =====
    // Call the method being tested
    User result = userService.createUser(testUser);

    // ===== ASSERT =====
    // Verify the results
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    verify(userRepository, times(1)).save(testUser);
}
```

## Testing Happy Paths vs Error Paths

### Happy Path
Test when everything works correctly:

```java
@Test
void testGetUserById_Found() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    // Act
    User result = userService.getUserById(1L);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
}
```

### Error Path
Test when something goes wrong:

```java
@Test
void testGetUserById_NotFound_ThrowsException() {
    // Arrange
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> {
        userService.getUserById(999L);
    });
}
```

## Complete Example: UserServiceTest

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Runs before each test
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("STUDENT");
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User savedUser = userService.createUser(testUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("STUDENT", savedUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser(testUser);
        });

        // Verify save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("testuser", foundUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        User updatedDetails = new User();
        updatedDetails.setUsername("updateduser");
        updatedDetails.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<User> userList = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUsersByRole() {
        // Arrange
        List<User> students = Arrays.asList(testUser);
        when(userRepository.findByRole("STUDENT")).thenReturn(students);

        // Act
        List<User> result = userService.getUsersByRole("STUDENT");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("STUDENT", result.get(0).getRole());
        verify(userRepository, times(1)).findByRole("STUDENT");
    }
}
```

## Running Tests

### In IDE (IntelliJ/Eclipse)
- Right-click on test class → Run
- Green checkmarks = tests passed ✅
- Red X = tests failed ❌

### With Maven
```bash
mvn test
```

### What Good Test Output Looks Like
```
UserServiceTest
  ✅ testCreateUser_Success
  ✅ testCreateUser_DuplicateEmail_ThrowsException
  ✅ testGetUserById_Found
  ✅ testGetUserById_NotFound_ThrowsException
  ✅ testUpdateUser_Success
  ✅ testDeleteUser_Success
  ✅ testGetAllUsers
  ✅ testGetUsersByRole

Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

## Common Testing Mistakes

### ❌ Not Using AAA Pattern
```java
@Test
void testCreateUser() {
    User user = new User();
    when(userRepository.save(any())).thenReturn(user);
    user.setEmail("test@example.com");
    User result = userService.createUser(user);
    assertEquals("test@example.com", result.getEmail());
    // Confusing! Hard to read
}
```

### ✅ Using AAA Pattern
```java
@Test
void testCreateUser_Success() {
    // Arrange
    User user = new User();
    user.setEmail("test@example.com");
    when(userRepository.save(any())).thenReturn(user);

    // Act
    User result = userService.createUser(user);

    // Assert
    assertEquals("test@example.com", result.getEmail());
}
```

### ❌ Testing Too Much in One Test
```java
@Test
void testEverything() {
    // Creating user
    userService.createUser(user1);
    // Updating user
    userService.updateUser(1L, user2);
    // Deleting user
    userService.deleteUser(1L);
    // Too much! Hard to debug if it fails
}
```

### ✅ One Test, One Thing
```java
@Test
void testCreateUser_Success() {
    // Only test creating user
}

@Test
void testUpdateUser_Success() {
    // Only test updating user
}

@Test
void testDeleteUser_Success() {
    // Only test deleting user
}
```

## Test Coverage

**Code Coverage** shows what percentage of your code is tested.

### Good Coverage Goals
- **Service Layer**: Aim for 80-90% coverage
- **Controllers**: 70-80% coverage
- **Entities/DTOs**: Often skip (simple POJOs)

### How to Check Coverage in IntelliJ
1. Right-click test class
2. Select "Run tests with Coverage"
3. See coverage report

## Summary

| Concept | What It Means |
|---------|---------------|
| **Unit Test** | Test individual code units in isolation |
| **JUnit** | Java testing framework |
| **Mockito** | Framework for creating mock objects |
| **@Mock** | Creates a fake object |
| **@InjectMocks** | Creates real object with mocks injected |
| **when().thenReturn()** | Tell mock what to return |
| **verify()** | Check if method was called |
| **AAA Pattern** | Arrange, Act, Assert structure |
| **Happy Path** | Test when everything works |
| **Error Path** | Test when things go wrong |

## Key Principles

1. **Test behavior, not implementation**: Focus on what code does, not how
2. **One assertion per test**: Keep tests focused
3. **Use descriptive names**: `testCreateUser_DuplicateEmail_ThrowsException`
4. **Test both happy and error paths**: Don't just test success
5. **Keep tests independent**: Each test should run alone
6. **Fast tests**: Unit tests should run in milliseconds

## Benefits of Testing

- **Confidence**: Know your code works
- **Refactoring**: Change code safely
- **Documentation**: Tests show how to use code
- **Bug Detection**: Catch errors early
- **Professional**: Shows you write quality code
