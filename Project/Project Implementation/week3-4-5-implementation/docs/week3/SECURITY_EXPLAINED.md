# Spring Security Explained

This guide demystifies Spring Security and explains how authentication, password hashing, and authorization work in our application.

## What is Spring Security?

**Spring Security** is a framework that provides:
- **Authentication** - Who are you? (Login)
- **Authorization** - What can you do? (Permissions)
- **Protection** - Against common attacks (CSRF, XSS, etc.)

**Think of it as:** A security guard for your application

---

## Why We Need Security

### The Danger of No Security

**Without Spring Security:**
```java
// Anyone can access any endpoint!
@GetMapping("/admin/delete-all-users")
public void deleteAll() {
    userRepository.deleteAll(); // Disaster!
}
```

**With Spring Security:**
```java
// Only authenticated admins can access
@GetMapping("/admin/delete-all-users")
@PreAuthorize("hasRole('ADMIN')")
public void deleteAll() {
    userRepository.deleteAll(); // Safe!
}
```

### Password Storage - NEVER Store Plain Text!

**WRONG - Never do this:**
```java
user.setPassword(request.getPassword()); // Storing "mypassword123"
// If database is hacked, all passwords are exposed!
```

**CORRECT - Hash passwords:**
```java
String hashed = passwordEncoder.encode(request.getPassword());
user.setPassword(hashed); // Storing "$2a$10$xYz..." (unrecognizable)
// Even if hacked, passwords can't be reverse-engineered
```

---

## Understanding SecurityConfig.java

Let's break down our security configuration:

### Class Declaration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
```

**@Configuration:**
- Marks this class as a source of bean definitions
- Spring will process this class at startup

**@EnableWebSecurity:**
- Activates Spring Security for the application
- Without this, Security features won't work

### Password Encoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**What is a @Bean?**
- An object managed by Spring
- Spring creates it once and reuses it everywhere
- You can @Autowire it in other classes

**What is BCrypt?**
- A password hashing algorithm
- Specifically designed for passwords (slow by design)
- Includes salt automatically (prevents rainbow table attacks)

**How it works:**
```java
String password = "mypassword123";
String hashed = passwordEncoder.encode(password);
// Result: "$2a$10$N9qo8uLOickgx2ZMRZoMye7I0iw3sxe8/vk3FT9cV6gW5S0zJ2x8e"

// Same password, different hash each time (salt is random):
String hash1 = passwordEncoder.encode("mypassword123");
String hash2 = passwordEncoder.encode("mypassword123");
// hash1 ≠ hash2, but both verify correctly!

boolean matches = passwordEncoder.matches("mypassword123", hashed);
// true - BCrypt can verify the password against its hash
```

**Why BCrypt is secure:**
1. **One-way function** - Can't reverse hash to get password
2. **Salted** - Same password produces different hashes
3. **Slow** - Makes brute-force attacks impractical
4. **Adaptive** - Can increase difficulty as computers get faster

### Security Filter Chain

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/register").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(basic -> {});

    return http.build();
}
```

This configures **how** Spring Security protects your app.

#### CSRF Protection

```java
.csrf(csrf -> csrf.disable())
```

**What is CSRF?**
- **Cross-Site Request Forgery**
- An attack where malicious sites make requests on user's behalf

**Why disabled?**
- We're building a REST API (stateless)
- CSRF protection is for form-based authentication with sessions
- For production APIs, use JWT tokens instead

**When to enable:**
- Server-side rendered apps (Thymeleaf, JSP)
- Cookie-based authentication

#### Authorization Rules

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/users/register").permitAll()
    .anyRequest().authenticated()
)
```

**Breaking it down:**

1. **`.requestMatchers("/api/users/register").permitAll()`**
   - This URL is public (no authentication needed)
   - Anyone can register
   - Must be before `.anyRequest()`

2. **`.anyRequest().authenticated()`**
   - Everything else requires authentication
   - User must be logged in

**Order matters!** Spring processes rules top to bottom.

**More examples:**
```java
.requestMatchers("/api/public/**").permitAll()
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/student/**").hasAnyRole("STUDENT", "PROFESSOR")
.anyRequest().authenticated()
```

#### HTTP Basic Authentication

```java
.httpBasic(basic -> {})
```

**What is HTTP Basic?**
- Simple authentication method
- Client sends username:password in header (Base64 encoded)
- Good for testing, not great for production

**How it works:**
```
Request Header:
Authorization: Basic am9objpteXBhc3N3b3JkMTIz
                     ↑ Base64 of "john:mypassword123"
```

**For production:**
- Use JWT (JSON Web Tokens) instead
- Or OAuth2 for third-party login

---

## Authentication Flow

### When User Registers

```
1. User sends POST /api/users/register
   Body: { "username": "john", "password": "pass123" }

2. UserController receives request
   ↓
3. Check if username/email already exists
   ↓
4. Hash the password:
   passwordEncoder.encode("pass123")
   → "$2a$10$xYz..." (stored in database)
   ↓
5. Save user to database
   ↓
6. Return success (without password in response!)
```

### When User Logs In (Future Implementation)

```
1. User sends credentials
   Authorization: Basic username:password
   ↓
2. Spring Security intercepts request
   ↓
3. Loads user from database
   ↓
4. Compares provided password with stored hash:
   passwordEncoder.matches(providedPassword, storedHash)
   ↓
5. If match → User authenticated ✓
   If no match → 401 Unauthorized ✗
```

---

## Security in Our Code

### In UserController

```java
@PostMapping("/register")
public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {

    // Validation 1: Check username not taken
    if (userRepository.existsByUsername(request.getUsername())) {
        return ResponseEntity.badRequest()
            .body("Error: Username is already taken");
    }

    // Validation 2: Check email not taken
    if (userRepository.existsByEmail(request.getEmail())) {
        return ResponseEntity.badRequest()
            .body("Error: Email is already in use");
    }

    // Security: Hash password before storing
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    // Best practice: Don't return password in response
    UserResponse response = new UserResponse(...);
    // No password field!
}
```

**Security measures:**
1. **Validation** - Prevent duplicate usernames/emails
2. **Hashing** - Never store plain passwords
3. **DTO pattern** - Don't expose password in response

### In RegisterRequest (DTO)

```java
@NotBlank(message = "Password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String password;
```

**Validation annotations:**
- Checked before reaching database
- Prevent invalid data
- User-friendly error messages

**For production, add more validation:**
```java
@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
         message = "Password must contain uppercase, lowercase, and number")
@Size(min = 8)
private String password;
```

---

## Common Security Concepts

### Authentication vs Authorization

**Authentication** - Identity verification
- **Question:** "Who are you?"
- **Example:** Login with username/password
- **Spring:** Uses `UserDetailsService` to load user

**Authorization** - Permission checking
- **Question:** "What can you do?"
- **Example:** Only admins can delete users
- **Spring:** Uses roles (ROLE_ADMIN, ROLE_USER)

### Roles in Our System

Currently, we have one role field:
```java
private String role = "STUDENT"; // Default
```

**Future roles:**
- STUDENT
- PROFESSOR
- ADMIN
- TA (Teaching Assistant)

**How to use roles:**
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/users/{id}")
public void deleteUser(@PathVariable Long id) {
    // Only admins can delete
}
```

### What Happens When Unauthenticated User Tries Protected Endpoint?

```
Request: GET /api/protected-resource
Without: Authorization header

Spring Security Response:
HTTP 401 Unauthorized
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required"
}
```

---

## Testing Security

### Test Public Endpoint (Registration)

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "securepass123"
  }'

# Should succeed with 201 Created
```

### Test Protected Endpoint (Future)

```bash
# Without authentication - Should fail
curl http://localhost:8080/api/protected
# Response: 401 Unauthorized

# With authentication - Should succeed
curl -u john:securepass123 http://localhost:8080/api/protected
# Response: 200 OK with data
```

---

## Common Mistakes to Avoid

### ❌ Storing Plain Passwords
```java
user.setPassword(request.getPassword()); // NEVER DO THIS!
```

### ✅ Always Hash
```java
user.setPassword(passwordEncoder.encode(request.getPassword()));
```

---

### ❌ Returning Password in Response
```java
return ResponseEntity.ok(user); // Exposes password field!
```

### ✅ Use DTOs Without Password
```java
UserResponse response = new UserResponse(user.getId(), user.getUsername(), ...);
return ResponseEntity.ok(response); // Safe!
```

---

### ❌ Putting Security Logic in Controller
```java
@GetMapping("/admin")
public String adminPanel() {
    if (!currentUser.getRole().equals("ADMIN")) {
        throw new ForbiddenException();
    }
    // Business logic
}
```

### ✅ Use Spring Security Annotations
```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String adminPanel() {
    // Business logic
    // Security handled by Spring
}
```

---

## Password Security Best Practices

1. **Minimum length:** 8 characters (we use 6 for demo)
2. **Complexity:** Mix of upper, lower, numbers, symbols
3. **No common passwords:** Check against common password lists
4. **No personal info:** Don't allow username in password
5. **Password expiry:** Force change after X days (enterprise apps)
6. **Login attempt limits:** Lock account after 5 failed attempts

**Example enhanced validation:**
```java
@NotBlank
@Size(min = 8, max = 100)
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
         message = "Password must contain uppercase, lowercase, number, and special character")
private String password;
```

---

## What's Next? (Week 4+)

Our current security is basic. Future enhancements:

### JWT (JSON Web Tokens)
```
Instead of: Authorization: Basic username:password (every request)
Use: Authorization: Bearer eyJhbGciOiJIUzI1... (token)

Benefits:
- Stateless (no server-side sessions)
- Can include user data (claims)
- Expires automatically
- Industry standard for REST APIs
```

### Method-Level Security
```java
@PreAuthorize("hasRole('PROFESSOR')")
public void createCourse() { }

@PreAuthorize("#username == authentication.principal.username")
public User getProfile(String username) { }
// Users can only view their own profile
```

### OAuth2 / Social Login
```
Login with Google
Login with GitHub
```

---

## Common Questions

**Q: Why use Spring Security instead of writing our own?**
A: Security is hard. Spring Security is battle-tested, maintained by experts, and protects against vulnerabilities you might not know about.

**Q: Can I disable Spring Security during development?**
A: Yes, but don't! Instead, use permitAll() for endpoints you're testing.

**Q: What if I forget the password?**
A: Implement "forgot password" flow (email reset link with temporary token).

**Q: How do I test endpoints that require authentication?**
A: Use Postman's Authorization tab, or curl with -u flag.

**Q: Is HTTP Basic secure?**
A: Only over HTTPS! Otherwise, credentials are visible in network traffic.

---

**Next:** Read [API_DOCUMENTATION.md](API_DOCUMENTATION.md) to test the registration endpoint!
