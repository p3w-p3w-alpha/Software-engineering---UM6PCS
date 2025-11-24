# Week 4 Troubleshooting Guide

Common issues and their solutions when building the SAMS user management system.

---

## Database Issues

### Issue 1: Cannot Connect to PostgreSQL

**Error:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Symptoms:**
- Application fails to start
- Error mentions "connection refused" or "could not connect"

**Solutions:**

**✅ Solution 1: Check PostgreSQL is Running**
```bash
# Windows
# Check if PostgreSQL service is running in Services

# Mac/Linux
sudo systemctl status postgresql
# or
pg_isready
```

**✅ Solution 2: Verify Database Configuration**

Check `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

- Database name: `sams_db` must exist
- Username: `postgres` (or your username)
- Password: Correct password
- Port: `5432` (default PostgreSQL port)

**✅ Solution 3: Create Database if Missing**
```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE sams_db;

-- Exit
\q
```

---

### Issue 2: Table 'users' Doesn't Exist

**Error:**
```
org.postgresql.util.PSQLException: ERROR: relation "users" does not exist
```

**Symptoms:**
- Application starts but crashes when accessing /api/users
- Error mentions "relation does not exist"

**Solutions:**

**✅ Solution 1: Check Hibernate DDL Setting**

In `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

This tells Hibernate to automatically create tables.

**✅ Solution 2: Manually Create Table**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

**✅ Solution 3: Restart Application**

Sometimes tables aren't created until you restart:
```bash
# Stop the app (Ctrl+C)
# Start again
mvn spring-boot:run
```

---

### Issue 3: Unique Constraint Violation

**Error:**
```
org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "users_email_key"
```

**Symptoms:**
- Can't create user with existing email
- Error mentions "unique constraint"

**This is actually CORRECT behavior!**

**Why:**
- Your application is working as designed
- The database is enforcing email uniqueness
- Your service layer should catch this before it reaches the database

**✅ Verify Your Code:**
```java
// In UserService.createUser()
if (userRepository.existsByEmail(user.getEmail())) {
    throw new DuplicateEmailException(user.getEmail());  // Should happen BEFORE save
}
```

**If you want to clear data for testing:**
```sql
-- Delete all users
DELETE FROM users;

-- Reset ID sequence
ALTER SEQUENCE users_id_seq RESTART WITH 1;
```

---

## Application Startup Issues

### Issue 4: Port 8080 Already in Use

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solutions:**

**✅ Solution 1: Stop Other Application on Port 8080**
```bash
# Windows
netstat -ano | findstr :8080
# Note the PID, then:
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :8080
# Note the PID, then:
kill -9 <PID>
```

**✅ Solution 2: Change Port**

In `application.properties`:
```properties
server.port=8081
```

---

### Issue 5: Bean Creation Failed

**Error:**
```
Error creating bean with name 'userController'
Parameter 0 of constructor... required a bean of type 'UserService'
```

**Symptoms:**
- Application fails to start
- Error mentions "required a bean"

**Cause:**
Spring can't find the service to inject.

**Solutions:**

**✅ Solution 1: Check @Service Annotation**
```java
@Service  // Make sure this is present!
public class UserService {
    // ...
}
```

**✅ Solution 2: Check Package Structure**
```
com.sams
├── SamsApplication.java  ← Main class here
├── controller
│   └── UserController.java
├── service
│   └── UserService.java  ← Same root package
└── repository
    └── UserRepository.java
```

All classes must be under `com.sams` for component scanning to work.

**✅ Solution 3: Clean and Rebuild**
```bash
mvn clean install
```

---

## API/REST Issues

### Issue 6: 404 Not Found on Valid Endpoint

**Error:**
```
HTTP 404 - Not Found
```

**When:**
Trying to access `POST http://localhost:8080/api/users`

**Solutions:**

**✅ Solution 1: Check @RequestMapping**
```java
@RestController
@RequestMapping("/api/users")  // Make sure this is correct
public class UserController {
    // ...
}
```

**✅ Solution 2: Verify Application Started Successfully**
Check console for:
```
Started SamsApplication in X.XXX seconds
```

**✅ Solution 3: Check URL**
- ✅ Correct: `http://localhost:8080/api/users`
- ❌ Wrong: `http://localhost:8080/users` (missing /api)
- ❌ Wrong: `http://localhost:8080/api/user` (missing s)

---

### Issue 7: 400 Bad Request with No Error Message

**Error:**
```
HTTP 400 - Bad Request
```

**When:**
Sending POST/PUT requests

**Solutions:**

**✅ Solution 1: Check Content-Type Header**
```
Content-Type: application/json
```

Must be set in Postman/client!

**✅ Solution 2: Validate JSON Syntax**
```json
{
  "username": "test",
  "email": "test@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

Common mistakes:
- ❌ Trailing comma: `"role": "STUDENT",}`
- ❌ Missing quotes: `{username: "test"}`
- ❌ Wrong quotes: `{"username": 'test'}`

**✅ Solution 3: Check Field Names Match**

DTO field names must match JSON keys exactly (case-sensitive):
```java
// UserRequest.java
private String username;  // Must match "username" in JSON
private String email;     // Must match "email" in JSON
```

---

### Issue 8: Password Not Being Saved

**Error:**
User created but password is null in database

**Cause:**
Password might not be included in DTO or conversion.

**Solutions:**

**✅ Solution 1: Check UserRequest DTO**
```java
public class UserRequest {
    private String username;
    private String email;
    private String password;  // Make sure this exists!
    private String role;
    // ... getters/setters
}
```

**✅ Solution 2: Check Conversion in Controller**
```java
User user = new User();
user.setUsername(request.getUsername());
user.setEmail(request.getEmail());
user.setPassword(request.getPassword());  // Don't forget this!
user.setRole(request.getRole());
```

---

## Testing Issues

### Issue 9: Tests Fail with NullPointerException

**Error:**
```
java.lang.NullPointerException
    at UserServiceTest.testCreateUser_Success
```

**Cause:**
Mock not configured or not injected.

**Solutions:**

**✅ Solution 1: Check @ExtendWith Annotation**
```java
@ExtendWith(MockitoExtension.class)  // Required for Mockito!
class UserServiceTest {
    // ...
}
```

**✅ Solution 2: Check @Mock and @InjectMocks**
```java
@Mock
private UserRepository userRepository;  // Creates mock

@InjectMocks
private UserService userService;  // Injects mock into this
```

**✅ Solution 3: Configure Mock Behavior**
```java
@Test
void testCreateUser() {
    // Must tell mock what to return!
    when(userRepository.save(any())).thenReturn(testUser);

    User result = userService.createUser(testUser);
}
```

---

### Issue 10: Tests Pass But Real Application Fails

**Why:**
Tests use mocks, not the real database.

**Solutions:**

**✅ Use Integration Tests Too**

Create a test that uses real database:
```java
@SpringBootTest  // Loads full application context
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserWithRealDatabase() {
        User user = new User("test", "test@example.com", "pass", "STUDENT");
        User saved = userService.createUser(user);
        assertNotNull(saved.getId());
    }
}
```

**✅ Test with Postman**

Always test the running application manually.

---

## JSON Serialization Issues

### Issue 11: Infinite Recursion Error

**Error:**
```
com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion
```

**Cause:**
Circular references in entities (e.g., User → Course → User).

**Solution:**

**✅ Use DTOs Instead of Entities**
```java
// Don't return entities from controllers
@GetMapping("/{id}")
public UserResponse getUser(@PathVariable Long id) {  // Return DTO, not User
    User user = userService.getUserById(id);
    return convertToDTO(user);
}
```

---

### Issue 12: Dates Not Formatting Correctly

**Error:**
Date appears as array instead of string:
```json
{
  "createdAt": [2024, 11, 3, 10, 30, 0]
}
```

**Solution:**

**✅ Add Jackson Dependency** (Usually already included):
```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

**✅ Or Format in DTO:**
```java
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserResponse {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
```

---

## Security Issues

### Issue 13: All Endpoints Return 401 Unauthorized

**Error:**
```
HTTP 401 - Unauthorized
```

**Cause:**
Spring Security is blocking requests.

**Solution:**

**✅ Check SecurityConfig**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/**").permitAll()  // Allow all user endpoints
            .anyRequest().authenticated()
        );
    return http.build();
}
```

---

## Postman Issues

### Issue 14: Postman Can't Connect

**Error:**
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```

**Solutions:**

**✅ Solution 1: Check Application is Running**
Look for this in console:
```
Started SamsApplication in X.XXX seconds
```

**✅ Solution 2: Disable Proxy in Postman**
Settings → Proxy → Disable proxy

**✅ Solution 3: Use 127.0.0.1 Instead of localhost**
```
http://127.0.0.1:8080/api/users
```

---

### Issue 15: Body Not Being Sent

**Symptoms:**
- Request shows 400 Bad Request
- Server receives empty body

**Solutions:**

**✅ Check "raw" is Selected**
- Click "Body" tab
- Select "raw"
- Select "JSON" from dropdown

**✅ Verify Content-Type Header**
Headers tab must have:
```
Content-Type: application/json
```

---

## Validation Issues

### Issue 16: Validation Not Working

**Symptoms:**
- Can create user with invalid email
- Can create user with empty fields

**Solutions:**

**✅ Solution 1: Add Validation Dependency**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**✅ Solution 2: Add @Valid Annotation**
```java
@PostMapping
public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    //                           ^^^^^^ Don't forget this!
}
```

**✅ Solution 3: Check DTO Annotations**
```java
public class UserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
```

---

## General Debugging Tips

### Tip 1: Check Application Logs

Always look at the console output:
```
Caused by: ...
    at UserService.createUser(UserService.java:45)
```

The line number tells you exactly where the error is!

---

### Tip 2: Enable Debug Logging

In `application.properties`:
```properties
logging.level.com.sams=DEBUG
logging.level.org.springframework.web=DEBUG
```

---

### Tip 3: Use Debugger

Set breakpoints in IntelliJ/Eclipse:
1. Click left margin next to line number
2. Run in Debug mode
3. Step through code to see values

---

### Tip 4: Test One Thing at a Time

Don't change multiple things at once:
1. Make one small change
2. Test
3. If it works, continue
4. If it breaks, undo and try differently

---

## Quick Reference: Common Error Codes

| Code | Meaning | Likely Cause |
|------|---------|--------------|
| 400 | Bad Request | Invalid JSON, missing fields, validation failed |
| 404 | Not Found | Wrong URL, user doesn't exist |
| 405 | Method Not Allowed | Using GET instead of POST, etc. |
| 500 | Internal Server Error | Bug in your code, check logs |
| 401 | Unauthorized | Security blocking request |

---

## Getting Help

### Before Asking for Help

1. ✅ Read the error message carefully
2. ✅ Check application logs
3. ✅ Search the error on Google/Stack Overflow
4. ✅ Try the solutions in this guide
5. ✅ Simplify the problem (test one endpoint at a time)

### When Asking for Help

Provide:
- Exact error message
- What you were trying to do
- What you expected to happen
- What actually happened
- Code snippets (relevant parts only)
- Application logs

---

## Summary

Most issues fall into these categories:
1. **Database not running/configured** → Check PostgreSQL
2. **Beans not found** → Check @Service, @Repository annotations
3. **404 errors** → Check URLs and @RequestMapping
4. **400 errors** → Check JSON syntax and Content-Type header
5. **Test failures** → Check mock configuration
6. **Validation not working** → Check @Valid and validation annotations

**When stuck, go back to basics and test each layer independently!**
