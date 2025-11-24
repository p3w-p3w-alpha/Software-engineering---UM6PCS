# REST API Basics

## What is an API?

**API** = Application Programming Interface

It's a way for different software systems to talk to each other. Think of it like a waiter in a restaurant:
- **You (client)**: Order food
- **Waiter (API)**: Takes your order to the kitchen
- **Kitchen (server)**: Prepares the food
- **Waiter (API)**: Brings food back to you

## What is REST?

**REST** = Representational State Transfer

It's a set of rules for building APIs using HTTP. A **RESTful API** follows these rules:

1. **Uses HTTP methods** (GET, POST, PUT, DELETE)
2. **Works with resources** (users, courses, grades)
3. **Stateless** (each request is independent)
4. **Uses standard formats** (usually JSON)

## HTTP Methods (Verbs)

Think of HTTP methods as actions you can perform on resources:

| HTTP Method | Purpose | Database Equivalent | Example |
|-------------|---------|---------------------|---------|
| **GET** | Retrieve data | SELECT | Get list of users |
| **POST** | Create new data | INSERT | Create new user |
| **PUT** | Update existing data | UPDATE | Update user details |
| **DELETE** | Remove data | DELETE | Delete a user |

### Real-World Analogy

Imagine managing a library:

```
GET /books          → "Show me all books" (browse catalog)
GET /books/123      → "Show me book #123" (look at specific book)
POST /books         → "Add a new book" (donate a book)
PUT /books/123      → "Update book #123 details" (fix book info)
DELETE /books/123   → "Remove book #123" (discard a book)
```

## HTTP Status Codes

Status codes tell you what happened with your request:

### Success Codes (2xx) ✅

| Code | Name | Meaning | When to Use |
|------|------|---------|-------------|
| **200** | OK | Request succeeded | GET, PUT successful |
| **201** | Created | New resource created | POST successful |
| **204** | No Content | Successful, no data to return | DELETE successful |

### Client Error Codes (4xx) ⚠️

| Code | Name | Meaning | When to Use |
|------|------|---------|-------------|
| **400** | Bad Request | Invalid data sent | Validation failed |
| **404** | Not Found | Resource doesn't exist | User ID not found |
| **409** | Conflict | Resource conflict | Duplicate email |

### Server Error Codes (5xx) ❌

| Code | Name | Meaning | When to Use |
|------|------|---------|-------------|
| **500** | Internal Server Error | Something broke | Unexpected error |

## JSON Format

**JSON** = JavaScript Object Notation

It's how data is sent between client and server in REST APIs.

### Example: User Object
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

### Example: List of Users
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "STUDENT"
  },
  {
    "id": 2,
    "username": "jane_smith",
    "email": "jane@example.com",
    "role": "FACULTY"
  }
]
```

## How Spring Converts JSON ↔ Java

This is **magical** in Spring Boot! You don't need to do anything special.

### Request: JSON → Java Object

**Client sends:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

**Spring automatically converts to:**
```java
UserRequest request = new UserRequest();
request.setUsername("john_doe");
request.setEmail("john@example.com");
request.setPassword("password123");
request.setRole("STUDENT");
```

**How?**
- Spring uses **Jackson library** (included by default)
- Matches JSON fields to Java object properties
- Calls setters automatically
- You just use `@RequestBody` annotation!

```java
@PostMapping("/users")
public UserResponse createUser(@RequestBody UserRequest request) {
    // request is already a Java object!
    System.out.println(request.getUsername());  // "john_doe"
}
```

### Response: Java Object → JSON

**Java object:**
```java
UserResponse response = new UserResponse(
    1L,
    "john_doe",
    "john@example.com",
    "STUDENT",
    LocalDateTime.now()
);
return response;  // Just return it!
```

**Spring automatically converts to:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

**How?**
- Spring calls all the getters
- Converts to JSON automatically
- You just use `@RestController` annotation!

## What are DTOs and Why Use Them?

**DTO** = Data Transfer Object

A DTO is a simple object that carries data between client and server.

### Entity vs DTO

**Entity (User.java)** - Database representation:
```java
@Entity
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;  // ⚠️ Sensitive!
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // ... getters/setters
}
```

**DTO (UserResponse.java)** - API representation:
```java
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    // NO PASSWORD! ✅
    private String role;
    private LocalDateTime createdAt;
    // ... getters/setters
}
```

### Why NOT Send Entities Directly?

**❌ Bad: Sending Entity**
```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUserById(id);
    // Returns password, updatedAt, and other internal fields!
}
```

**Response includes password:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "password": "$2a$10$hashed...",  ← SECURITY RISK!
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00",
  "updatedAt": "2024-11-03T11:15:00"
}
```

**✅ Good: Using DTO**
```java
@GetMapping("/users/{id}")
public UserResponse getUser(@PathVariable Long id) {
    User user = userService.getUserById(id);
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getRole(),
        user.getCreatedAt()
    );  // No password!
}
```

**Response (safe):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

### Benefits of DTOs

1. **Security**: Don't expose sensitive fields (passwords, internal IDs)
2. **Flexibility**: API can differ from database structure
3. **Versioning**: Can change API without changing database
4. **Control**: Choose exactly what to send/receive

## Understanding Controller Annotations

### @RestController
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ...
}
```

**What it does:**
- Combines `@Controller` + `@ResponseBody`
- All methods return data (not HTML pages)
- Responses automatically converted to JSON

### @RequestMapping
```java
@RequestMapping("/api/users")  // Class level - applies to all methods
public class UserController {

    @GetMapping("/{id}")  // Method level - adds to class path
    public UserResponse getUser(@PathVariable Long id) {
        // Full path: /api/users/{id}
    }
}
```

### HTTP Method Annotations

```java
@GetMapping           // GET /api/users
@PostMapping          // POST /api/users
@PutMapping("/{id}")  // PUT /api/users/{id}
@DeleteMapping("/{id}") // DELETE /api/users/{id}
```

### @PathVariable
```java
@GetMapping("/users/{id}")
public UserResponse getUser(@PathVariable Long id) {
    // URL: /api/users/123
    // id = 123
}
```

Extracts value from URL path.

### @RequestParam
```java
@GetMapping("/users")
public List<UserResponse> searchUsers(@RequestParam String role) {
    // URL: /api/users?role=STUDENT
    // role = "STUDENT"
}
```

Extracts value from query string.

### @RequestBody
```java
@PostMapping("/users")
public UserResponse createUser(@RequestBody UserRequest request) {
    // Request body (JSON) is converted to UserRequest object
}
```

Converts JSON request body to Java object.

### @ResponseStatus
```java
@PostMapping("/users")
@ResponseStatus(HttpStatus.CREATED)  // Returns 201
public UserResponse createUser(@RequestBody UserRequest request) {
    // ...
}
```

Sets HTTP status code for successful responses.

## Complete SAMS Example

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE: POST /api/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // 201
    public UserResponse createUser(@RequestBody UserRequest request) {
        User user = convertToEntity(request);
        User savedUser = userService.createUser(user);
        return convertToResponse(savedUser);
    }

    // READ ALL: GET /api/users
    @GetMapping
    public List<UserResponse> getAllUsers() {  // 200 OK by default
        return userService.getAllUsers()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    // READ ONE: GET /api/users/{id}
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        // If not found, service throws exception → 404
        return convertToResponse(user);
    }

    // UPDATE: PUT /api/users/{id}
    @PutMapping("/{id}")
    public UserResponse updateUser(
        @PathVariable Long id,
        @RequestBody UserRequest request
    ) {
        User userDetails = convertToEntity(request);
        User updated = userService.updateUser(id, userDetails);
        return convertToResponse(updated);
    }

    // DELETE: DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // 204
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        // No return value for 204
    }

    // Helper methods
    private User convertToEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        return user;
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
```

## Request/Response Flow

### Creating a User

**1. Client sends request:**
```http
POST /api/users HTTP/1.1
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

**2. Spring converts JSON to UserRequest**

**3. Controller calls service:**
```java
User savedUser = userService.createUser(user);
```

**4. Service validates and saves to database**

**5. Controller converts User to UserResponse**

**6. Spring converts UserResponse to JSON**

**7. Client receives response:**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

## Summary

| Concept | What It Means |
|---------|---------------|
| **REST API** | Web service following REST principles |
| **HTTP Method** | Action to perform (GET, POST, PUT, DELETE) |
| **Status Code** | Result of request (200 OK, 404 Not Found, etc.) |
| **JSON** | Data format for requests/responses |
| **DTO** | Object for transferring data (hides sensitive fields) |
| **@RestController** | Marks class as REST API controller |
| **@RequestBody** | Converts JSON to Java object |
| **@PathVariable** | Extracts value from URL path |
| **@RequestParam** | Extracts value from query string |

## Key Principles

1. **Use correct HTTP methods**: GET for read, POST for create, PUT for update, DELETE for remove
2. **Use correct status codes**: 200 OK, 201 Created, 404 Not Found, etc.
3. **Never expose entities**: Always use DTOs in API responses
4. **Keep controllers thin**: Business logic belongs in services
5. **Be consistent**: Follow REST conventions for predictable APIs
