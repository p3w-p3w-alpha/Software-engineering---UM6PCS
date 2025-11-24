# Service Layer Guide

## What is the Service Layer?

The **Service Layer** is where your **business logic** lives. It sits between the Controller (which handles HTTP requests) and the Repository (which handles database access).

### Simple Analogy

Think of building a house:
- **Controller**: The receptionist who takes your order
- **Service**: The project manager who makes decisions, coordinates work, ensures quality
- **Repository**: The construction workers who actually build

## Why Have a Separate Service Layer?

### Without Service Layer ❌
```java
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        // Validation in controller? Bad!
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email exists");
        }
        // Business logic in controller? Bad!
        if (user.getRole() == null) {
            user.setRole("STUDENT");
        }
        return userRepository.save(user);
    }
}
```

**Problems:**
- Controller is doing too much
- Can't reuse this logic elsewhere
- Hard to test
- Messy and difficult to maintain

### With Service Layer ✅
```java
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserResponse createUser(@RequestBody UserRequest request) {
        User user = userService.createUser(convertToEntity(request));
        return convertToResponse(user);
    }
}

@Service
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        // All validation and business logic here!
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }
        if (user.getRole() == null) {
            user.setRole("STUDENT");
        }
        return userRepository.save(user);
    }
}
```

**Benefits:**
- Each layer has one job
- Business logic is reusable
- Easy to test
- Clean and maintainable

## Business Logic vs Data Access Logic

### Data Access Logic (Repository)
**What**: Technical operations for getting/storing data

Examples:
- `findById(Long id)` - Get from database
- `save(User user)` - Write to database
- `delete(User user)` - Remove from database

### Business Logic (Service)
**Why**: The rules and decisions of your application

Examples:
- "A student cannot enroll in more than 5 courses"
- "Only admins can delete faculty accounts"
- "Email must be unique"
- "Default role is STUDENT"
- "Cannot update a deleted user"

## Anatomy of UserService

```java
@Service  // Marks this as a Spring service component
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection (recommended)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional  // Database transaction
    public User createUser(User user) {
        // Business logic here
        validateUser(user);
        checkDuplicates(user);
        setDefaults(user);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    // More methods...
}
```

## Understanding Annotations

### @Service
```java
@Service
public class UserService {
    // ...
}
```

**What it does:**
- Tells Spring: "This is a service component"
- Spring will create ONE instance of this class (singleton)
- Spring will manage its lifecycle
- Makes it available for dependency injection

### @Transactional
```java
@Transactional
public User createUser(User user) {
    // Multiple database operations
    userRepository.save(user);
    logRepository.save(new Log("User created"));
    // If ANY operation fails, ALL are rolled back
}
```

**What it does:**
- Wraps method in a database transaction
- Either ALL operations succeed, or NONE do (atomicity)
- Automatically commits on success
- Automatically rolls back on exception

**When to use:**
- Methods that modify data (create, update, delete)
- Methods that perform multiple database operations
- When you need data consistency

**When NOT to use:**
- Read-only methods (findById, getAllUsers)
- Methods that don't touch the database

## Constructor Injection vs @Autowired

### Option 1: Constructor Injection ✅ (Recommended)
```java
@Service
public class UserService {
    private final UserRepository userRepository;  // Can be final!

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

**Advantages:**
- Dependencies are `final` (immutable, thread-safe)
- Required dependencies are obvious
- Easier to test (you can pass mock objects)
- Recommended by Spring team

### Option 2: Field Injection ⚠️ (Not recommended)
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Cannot be final
}
```

**Disadvantages:**
- Cannot make field `final`
- Hidden dependencies
- Harder to test
- Considered bad practice

### Option 3: Setter Injection (Rarely used)
```java
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

**When to use:** Optional dependencies (rare)

## Handling Optional<T> in Service Layer

```java
public User getUserById(Long id) {
    // Repository returns Optional<User>
    Optional<User> userOptional = userRepository.findById(id);

    // Transform Optional to User or throw exception
    return userOptional.orElseThrow(
        () -> new UserNotFoundException(id)
    );
}
```

**Why throw exception here?**
- Controller shouldn't deal with Optional
- Service enforces: "User MUST exist or fail"
- GlobalExceptionHandler converts exception to HTTP 404

## Common Service Layer Patterns

### 1. Validation
```java
public User createUser(User user) {
    // Validate required fields
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        throw new IllegalArgumentException("Email is required");
    }

    // Check business rules
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new DuplicateEmailException(user.getEmail());
    }

    return userRepository.save(user);
}
```

### 2. Setting Defaults
```java
public User createUser(User user) {
    // Set default role if not provided
    if (user.getRole() == null || user.getRole().isEmpty()) {
        user.setRole("STUDENT");
    }

    return userRepository.save(user);
}
```

### 3. Update Pattern
```java
public User updateUser(Long id, User userDetails) {
    // First, get existing user (or throw exception)
    User existingUser = getUserById(id);

    // Check if email is changing
    if (!existingUser.getEmail().equals(userDetails.getEmail())) {
        // Validate new email doesn't exist
        if (userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateEmailException(userDetails.getEmail());
        }
    }

    // Update fields
    existingUser.setEmail(userDetails.getEmail());
    existingUser.setUsername(userDetails.getUsername());

    // Save and return
    return userRepository.save(existingUser);
}
```

### 4. Delete Pattern
```java
public void deleteUser(Long id) {
    // Verify user exists first
    User user = getUserById(id);

    // Business rule: Check if user can be deleted
    if (user.getRole().equals("ADMIN")) {
        throw new IllegalStateException("Cannot delete admin users");
    }

    // Delete
    userRepository.delete(user);
}
```

## Real Example: SAMS UserService

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE - with validation
    @Transactional
    public User createUser(User user) {
        // Business rule: Email must be unique
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }

        // Business rule: Required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        // Business rule: Default role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }

        return userRepository.save(user);
    }

    // READ - with error handling
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // UPDATE - with validation
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        // Check email uniqueness if changing
        if (!user.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new DuplicateEmailException(userDetails.getEmail());
            }
        }

        // Update fields
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getRole());

        return userRepository.save(user);
    }

    // DELETE - with existence check
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // Additional business methods
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
```

## Testing Services (Why Constructor Injection Helps)

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;  // Fake repository

    @InjectMocks
    private UserService userService;  // Real service with fake repo

    @Test
    void testCreateUser_Success() {
        // Arrange
        User user = new User("test", "test@example.com", "pass", "STUDENT");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(user);
    }
}
```

Because we use constructor injection, Mockito can easily inject the mock repository!

## Summary

| Concept | What It Means |
|---------|---------------|
| **Service Layer** | Business logic layer between Controller and Repository |
| **Business Logic** | Application rules and decisions (what should happen) |
| **Data Access Logic** | Technical database operations (how to get data) |
| **@Service** | Marks class as Spring service component |
| **@Transactional** | Wraps method in database transaction |
| **Constructor Injection** | Best practice for dependency injection (use `final` fields) |
| **@Autowired** | Field injection (not recommended, but works) |

## Key Principles

1. **Single Responsibility**: Service handles business logic, repository handles data access
2. **Don't Repeat Yourself (DRY)**: Reuse service methods across controllers
3. **Fail Fast**: Validate and throw exceptions early
4. **Transaction Boundaries**: Use @Transactional for write operations
5. **Immutability**: Use `final` fields with constructor injection

## Common Interview Questions

**Q: Why not put business logic in the controller?**
A: Controllers should only handle HTTP concerns. Business logic should be reusable and testable independently.

**Q: When do you use @Transactional?**
A: On methods that modify data (create, update, delete) or perform multiple database operations that must succeed together.

**Q: What's the difference between Service and Repository?**
A: Service contains business logic (rules), Repository contains data access logic (SQL).

**Q: Why use constructor injection?**
A: Makes dependencies explicit, allows final fields, easier to test, recommended best practice.
