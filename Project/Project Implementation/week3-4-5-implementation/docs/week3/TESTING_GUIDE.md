# Testing Guide

Complete guide to understanding and running tests for the SAMS application.

---

## Why Testing Matters

**Without tests:**
- You manually test every change
- Bugs slip through to production
- Fear of refactoring (might break something)

**With tests:**
- Automated verification of functionality
- Catch bugs before they reach users
- Confidence to refactor and improve code
- Documentation of expected behavior

---

## Test Structure

### UserControllerTest.java

**Location:** `src/test/java/com/sams/UserControllerTest.java`

**What it tests:** User registration endpoint

**Test framework:** JUnit 5 (Jupiter)

**Test type:** Integration tests (tests full stack: Controller → Service → Repository → Database)

---

## Running Tests

### Method 1: Maven Command Line

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run specific test method
mvn test -Dtest=UserControllerTest#testSuccessfulUserRegistration
```

### Method 2: IntelliJ IDEA

**Run all tests:**
1. Right-click on `src/test/java` folder
2. Select "Run 'All Tests'"

**Run single test class:**
1. Open `UserControllerTest.java`
2. Click green ▶️ icon next to class name
3. Select "Run 'UserControllerTest'"

**Run single test method:**
1. Click green ▶️ icon next to test method
2. Select "Run 'testName()'"

### Expected Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.sams.UserControllerTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## Test Cases Explained

### 1. testSuccessfulUserRegistration()

**What it tests:**
- Valid user data is accepted
- Status code is 201 Created
- Response includes user data
- Password is NOT in response

**How it works:**
```java
RegisterRequest request = new RegisterRequest();
request.setUsername("testuser");
request.setEmail("test@example.com");
request.setPassword("password123");

mockMvc.perform(post("/api/users/register")...)
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.username").value("testuser"))
    .andExpect(jsonPath("$.password").doesNotExist());
```

**Why it matters:**
- Verifies the happy path works
- Ensures API returns correct data structure
- Confirms password security

---

### 2. testDuplicateUsername()

**What it tests:**
- Cannot register with existing username
- Returns 400 Bad Request
- Error message is clear

**How it works:**
1. Create a user "john"
2. Try to register another user "john"
3. Expect rejection with error message

**Why it matters:**
- Enforces unique username constraint
- Tests business logic validation
- Verifies error handling

---

### 3. testDuplicateEmail()

**What it tests:**
- Cannot register with existing email
- Returns 400 Bad Request
- Error message is clear

**How it works:**
1. Create user with email "john@example.com"
2. Try to register different user with same email
3. Expect rejection

**Why it matters:**
- Enforces unique email constraint
- Prevents duplicate accounts
- Tests database constraints

---

### 4. testValidationFailure_MissingUsername()

**What it tests:**
- Request without username is rejected
- Returns 400 Bad Request

**Why it matters:**
- Tests @NotBlank validation
- Ensures required fields are enforced
- Prevents incomplete data

---

### 5. testValidationFailure_InvalidEmail()

**What it tests:**
- Invalid email format is rejected
- Email must match pattern

**Example:**
```java
request.setEmail("not-an-email"); // Invalid
// Should reject
```

**Why it matters:**
- Tests @Email validation annotation
- Ensures data quality
- Prevents typos

---

### 6. testValidationFailure_ShortPassword()

**What it tests:**
- Password must be at least 6 characters
- Returns 400 Bad Request

**Example:**
```java
request.setPassword("12345"); // Only 5 chars
// Should reject
```

**Why it matters:**
- Tests @Size validation
- Enforces password strength rules
- Security requirement

---

### 7. testPasswordIsHashedInDatabase()

**What it tests:**
- Password is NOT stored as plain text
- Password is BCrypt hashed
- Hash starts with "$2a$"

**How it works:**
```java
// Register user with password "password123"
// Fetch from database
User savedUser = userRepository.findByUsername("testuser").orElseThrow();

// Verify NOT plain text
assert !savedUser.getPassword().equals("password123");

// Verify is BCrypt hash
assert savedUser.getPassword().startsWith("$2a$");
```

**Why it matters:**
- CRITICAL security test
- Ensures passwords are protected
- Verifies BCrypt encoder works

---

### 8. testDefaultRoleIsStudent()

**What it tests:**
- If role not specified, defaults to "STUDENT"
- Business logic works correctly

**How it works:**
```java
RegisterRequest request = new RegisterRequest();
// Don't set role
request.setUsername("testuser");
request.setEmail("test@example.com");
request.setPassword("password123");

// Expect STUDENT role in response
.andExpect(jsonPath("$.role").value("STUDENT"));
```

**Why it matters:**
- Tests default value logic
- Ensures consistent user creation
- Verifies business rules

---

## Understanding Test Annotations

### @SpringBootTest
```java
@SpringBootTest
```

**What it does:**
- Starts the full Spring application context
- Loads all beans and configurations
- Uses real database (H2 in-memory by default for tests)

**Think of it as:** Starting the entire application for testing

---

### @AutoConfigureMockMvc
```java
@AutoConfigureMockMvc
```

**What it does:**
- Configures MockMvc for testing controllers
- Allows simulating HTTP requests without starting server

**Think of it as:** A test client that can call your endpoints

---

### @BeforeEach
```java
@BeforeEach
void setUp() {
    userRepository.deleteAll();
}
```

**What it does:**
- Runs before EACH test method
- Cleans up database
- Ensures tests are independent

**Why it matters:**
- Tests don't interfere with each other
- Clean slate for every test
- Reliable results

---

### @Test
```java
@Test
void testSuccessfulUserRegistration() throws Exception {
    // test code
}
```

**What it does:**
- Marks method as a test
- JUnit will execute it
- Reports results (pass/fail)

---

## MockMvc Explained

### What is MockMvc?

**MockMvc** allows you to test Spring MVC controllers without starting a web server.

**Example:**
```java
mockMvc.perform(post("/api/users/register")
    .contentType(MediaType.APPLICATION_JSON)
    .content(json))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.username").value("testuser"));
```

**Breaking it down:**

1. **`.perform(post(...))`** → Simulate POST request
2. **`.contentType(...)`** → Set Content-Type header
3. **`.content(...)`** → Request body (JSON)
4. **`.andExpect(status()...)`** → Verify response status
5. **`.andExpect(jsonPath()...)`** → Verify JSON response

---

## JSONPath Expressions

**JSONPath** allows you to query JSON responses:

```java
// Check field exists
.andExpect(jsonPath("$.id").exists())

// Check field value
.andExpect(jsonPath("$.username").value("testuser"))

// Check field does NOT exist
.andExpect(jsonPath("$.password").doesNotExist())

// Check nested field
.andExpect(jsonPath("$.user.email").value("test@example.com"))
```

**Syntax:**
- `$` = Root
- `.field` = Access field
- `[0]` = Array index

---

## Common Test Patterns

### Testing Success
```java
@Test
void testSuccess() throws Exception {
    // Arrange - prepare test data
    RegisterRequest request = new RegisterRequest();
    request.setUsername("testuser");

    // Act & Assert - perform action and verify
    mockMvc.perform(post("/api/users/register")
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
}
```

### Testing Failure
```java
@Test
void testFailure() throws Exception {
    // Arrange - create condition for failure
    User existing = new User("john", "john@example.com", "pass", "STUDENT");
    userRepository.save(existing);

    // Act - try duplicate
    RegisterRequest request = new RegisterRequest();
    request.setUsername("john");

    // Assert - verify rejection
    mockMvc.perform(post("/api/users/register")
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
}
```

---

## Test Coverage

**What we're testing:**

✅ Happy path (successful registration)
✅ Duplicate username validation
✅ Duplicate email validation
✅ Missing required fields
✅ Invalid email format
✅ Password length validation
✅ Password hashing
✅ Default role assignment

**What we're NOT testing (yet):**
- Authentication (Week 4)
- Multiple concurrent registrations
- SQL injection attempts
- Very long input strings
- Special characters in username

---

## Debugging Failed Tests

### Test Fails - What to do?

1. **Read the error message:**
   ```
   Expected status:<201> but was:<400>
   ```
   This tells you exactly what went wrong.

2. **Check the stack trace:**
   - Find the line number where it failed
   - Look at what the test expected vs what it got

3. **Add debug logging:**
   ```java
   @Test
   void testSomething() throws Exception {
       // Add this to see response
       mockMvc.perform(post(...))
           .andDo(print())  // Prints full request/response
           .andExpect(status().isCreated());
   }
   ```

4. **Run test in debug mode:**
   - Set breakpoint in test
   - Run test in debug mode (🐛 icon in IntelliJ)
   - Step through execution

---

## Best Practices

### 1. Test Independence
✅ **Do:** Clean up database before each test
```java
@BeforeEach
void setUp() {
    userRepository.deleteAll();
}
```

✗ **Don't:** Depend on test execution order

---

### 2. Clear Test Names
✅ **Do:** Descriptive names
```java
testDuplicateUsernameReturnsError()
```

✗ **Don't:** Vague names
```java
test1()
```

---

### 3. One Assertion per Test (guideline, not strict)
✅ **Do:** Focus on one scenario
```java
@Test
void testDuplicateUsername() {
    // Test only duplicate username logic
}
```

✗ **Don't:** Test everything in one method

---

### 4. Use Meaningful Test Data
✅ **Do:** Clear test data
```java
request.setUsername("testuser");
request.setEmail("test@example.com");
```

✗ **Don't:** Cryptic data
```java
request.setUsername("asdf123");
```

---

## Running Tests in CI/CD

Tests are designed to run automatically in CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Run Tests
  run: mvn test

# Only deploy if tests pass
- name: Deploy
  if: success()
  run: ...
```

---

## Test Database Configuration

**Production database:** PostgreSQL
**Test database:** H2 in-memory (automatic)

**Why different?**
- Tests run faster with in-memory DB
- Don't pollute production data
- Each test gets fresh database

**Configuration** (automatic):
Spring Boot automatically uses H2 for tests if it's in dependencies.

---

## Extending Tests

### Add More Test Cases:

```java
@Test
void testUsernameWithSpecialCharacters() {
    // Test username with spaces, symbols, etc.
}

@Test
void testEmailCaseInsensitivity() {
    // Test john@EXAMPLE.com vs john@example.com
}

@Test
void testMaximumPasswordLength() {
    // Test very long password
}
```

---

## Common Issues

### Issue: Tests fail but manual testing works

**Cause:** Database state from previous test

**Solution:**
```java
@BeforeEach
void setUp() {
    userRepository.deleteAll();
}
```

---

### Issue: "Cannot autowire MockMvc"

**Cause:** Missing @AutoConfigureMockMvc

**Solution:**
```java
@SpringBootTest
@AutoConfigureMockMvc  // Add this
public class UserControllerTest {
```

---

### Issue: Tests are slow

**Cause:** Starting Spring context for each test

**Solution:** Use @SpringBootTest at class level (already done)
Spring reuses context across tests in same class.

---

## Interview Questions

**Q: What's the difference between unit tests and integration tests?**
A: "Unit tests test individual components in isolation, often with mocked dependencies. Integration tests, like ours, test the full stack including database, to ensure components work together correctly."

**Q: Why clean database before each test?**
A: "To ensure test independence. If tests share database state, they can interfere with each other and produce inconsistent results. Each test should run in a clean, predictable environment."

**Q: What is MockMvc?**
A: "MockMvc is a Spring test utility that simulates HTTP requests to controllers without starting a web server. It allows testing the full MVC stack including request mapping, validation, and response formatting."

---

## Next Steps

**Week 4 testing:**
- Test login/logout endpoints
- Test JWT token generation
- Test authorization (role-based access)
- Mock external services

**Learn more:**
- JUnit 5 User Guide
- Spring Boot Testing documentation
- AssertJ for fluent assertions
- Mockito for mocking

---

**Remember: Tests are not just about finding bugs - they're living documentation of how your code should behave!**
