# Code Structure & Architecture Diagrams

Visual guide to understanding the SAMS project structure and request flow.

---

## Project Folder Structure

```
week3-development/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sams/
│   │   │           ├── SamsApplication.java          ⭐ Main entry point
│   │   │           │
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java       🔒 Security configuration
│   │   │           │
│   │   │           ├── entity/                       📦 Database models
│   │   │           │   └── User.java                 - User entity (JPA)
│   │   │           │
│   │   │           ├── repository/                   💾 Data access layer
│   │   │           │   └── UserRepository.java       - Auto-implemented by Spring
│   │   │           │
│   │   │           ├── service/                      🧠 Business logic layer
│   │   │           │   └── UserService.java          - Validation, rules, coordination
│   │   │           │
│   │   │           ├── controller/                   🌐 REST API layer
│   │   │           │   └── UserController.java       - HTTP request handling
│   │   │           │
│   │   │           ├── dto/                          📄 Data transfer objects
│   │   │           │   ├── UserRequest.java          - API request format
│   │   │           │   ├── UserResponse.java         - API response format
│   │   │           │   └── RegisterRequest.java      - Registration format
│   │   │           │
│   │   │           └── exception/                    ⚠️ Error handling
│   │   │               ├── UserNotFoundException.java
│   │   │               ├── DuplicateEmailException.java
│   │   │               └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources/
│   │       └── application.properties                ⚙️ Configuration (DB, port, etc.)
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── sams/
│                   └── service/
│                       └── UserServiceTest.java      ✅ Unit tests (8 tests)
│
├── postman/
│   └── SAMS_User_Management.postman_collection.json  📮 API test collection
│
├── documentation/
│   └── week4/                                        📚 Learning materials
│       ├── WEEK4_OVERVIEW.md
│       ├── REPOSITORY_PATTERN_EXPLAINED.md
│       ├── SERVICE_LAYER_GUIDE.md
│       ├── REST_API_BASICS.md
│       ├── API_ENDPOINTS_DOCUMENTATION.md
│       ├── TESTING_EXPLAINED.md
│       ├── POSTMAN_TESTING_GUIDE.md
│       ├── WEEK4_LEARNING_SUMMARY.md
│       ├── PRESENTATION_PREP_WEEK4.md
│       ├── TROUBLESHOOTING_WEEK4.md
│       ├── CODE_STRUCTURE_DIAGRAM.md (this file)
│       └── INDEX.md
│
└── pom.xml                                           📦 Maven dependencies
```

---

## Layer Responsibilities

### 🌐 Controller Layer (UserController.java)

**Responsibility:** Handle HTTP requests and responses

**What it does:**
- Receives HTTP requests from clients
- Validates request format (@Valid)
- Converts DTOs to entities
- Calls service layer methods
- Converts entities to DTOs
- Returns HTTP responses with proper status codes

**What it DOESN'T do:**
- ❌ Business logic
- ❌ Data validation (beyond format)
- ❌ Direct database access

---

### 🧠 Service Layer (UserService.java)

**Responsibility:** Implement business logic and rules

**What it does:**
- Validates business rules (email uniqueness, required fields)
- Coordinates repository calls
- Handles transactions (@Transactional)
- Throws custom exceptions
- Sets default values
- Enforces application logic

**What it DOESN'T do:**
- ❌ HTTP concerns (status codes, headers)
- ❌ Direct SQL queries
- ❌ DTO conversions

---

### 💾 Repository Layer (UserRepository.java)

**Responsibility:** Abstract data access

**What it does:**
- Provides methods to access database
- Auto-generates SQL from method names
- Handles database transactions
- Returns data as entities or Optional

**What it DOESN'T do:**
- ❌ Business logic
- ❌ Validation
- ❌ Exception translation (returns Optional instead)

---

## Request Flow Diagram

### Example: Creating a New User

```
┌─────────────────────────────────────────────────────────────────┐
│                    CLIENT (Postman/Browser)                     │
│                                                                 │
│  POST /api/users                                                │
│  Content-Type: application/json                                │
│  {                                                              │
│    "username": "john_doe",                                      │
│    "email": "john@example.com",                                 │
│    "password": "password123",                                   │
│    "role": "STUDENT"                                            │
│  }                                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP POST Request
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                 SPRING BOOT WEB LAYER                           │
│  - Receives HTTP request                                        │
│  - Deserializes JSON → UserRequest object (Jackson)             │
│  - Applies @Valid validation                                    │
└────────────────────────────┬────────────────────────────────────┘
                             │ UserRequest object
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              CONTROLLER LAYER (UserController)                  │
│                                                                 │
│  @PostMapping                                                   │
│  public UserResponse createUser(@Valid @RequestBody            │
│                                  UserRequest request) {         │
│                                                                 │
│    1. Convert DTO to Entity:                                    │
│       User user = new User();                                   │
│       user.setUsername(request.getUsername());                  │
│       user.setEmail(request.getEmail());                        │
│       ...                                                       │
│                                                                 │
│    2. Call Service Layer:                                       │
│       User savedUser = userService.createUser(user);            │
│                                                                 │
│    3. Convert Entity to DTO:                                    │
│       UserResponse response = convertToResponse(savedUser);     │
│                                                                 │
│    4. Return response (Spring converts to JSON)                 │
│       return response;                                          │
│  }                                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │ User entity
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│               SERVICE LAYER (UserService)                       │
│                                                                 │
│  @Transactional                                                 │
│  public User createUser(User user) {                            │
│                                                                 │
│    1. Check for duplicate email:                                │
│       if (userRepository.existsByEmail(user.getEmail())) {      │
│           throw new DuplicateEmailException(...);               │
│       }                                                         │
│                                                                 │
│    2. Validate required fields:                                 │
│       if (user.getUsername() == null) {                         │
│           throw new IllegalArgumentException(...);              │
│       }                                                         │
│                                                                 │
│    3. Set default values:                                       │
│       if (user.getRole() == null) {                             │
│           user.setRole("STUDENT");                              │
│       }                                                         │
│                                                                 │
│    4. Save to database:                                         │
│       return userRepository.save(user);                         │
│  }                                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │ User entity
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│             REPOSITORY LAYER (UserRepository)                   │
│                                                                 │
│  public interface UserRepository extends                        │
│                  JpaRepository<User, Long> {                    │
│                                                                 │
│    // Spring auto-generates implementation:                     │
│    User save(User user);                                        │
│    // Becomes:                                                  │
│    // INSERT INTO users (username, email, password, role,       │
│    //                     created_at, updated_at)               │
│    // VALUES (?, ?, ?, ?, ?, ?)                                 │
│  }                                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │ SQL Query
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    DATABASE (PostgreSQL)                        │
│                                                                 │
│  Table: users                                                   │
│  ┌────┬──────────┬──────────────────┬──────────┬─────────┐     │
│  │ id │ username │ email            │ password │ role    │     │
│  ├────┼──────────┼──────────────────┼──────────┼─────────┤     │
│  │ 1  │ john_doe │ john@example.com │ pass123  │ STUDENT │     │
│  └────┴──────────┴──────────────────┴──────────┴─────────┘     │
│                                                                 │
│  Row inserted successfully!                                     │
└────────────────────────────┬────────────────────────────────────┘
                             │ User entity with ID
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              RESPONSE FLOWS BACK UP                             │
│                                                                 │
│  Repository → Service → Controller → Spring → Client            │
│                                                                 │
│  User object with generated ID:                                 │
│  User { id=1, username="john_doe", email="john@...", ... }      │
└────────────────────────────┬────────────────────────────────────┘
                             │ UserResponse (DTO)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT RECEIVES                          │
│                                                                 │
│  HTTP/1.1 201 Created                                           │
│  Content-Type: application/json                                │
│                                                                 │
│  {                                                              │
│    "id": 1,                                                     │
│    "username": "john_doe",                                      │
│    "email": "john@example.com",                                 │
│    "role": "STUDENT",                                           │
│    "createdAt": "2024-11-03T10:30:00"                           │
│  }                                                              │
│                                                                 │
│  ⚠️ Notice: No password in response!                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## Error Handling Flow

### Example: User Not Found

```
Client: GET /api/users/999

     ↓

Controller: getUserById(999)
     │
     └─→ Service: getUserById(999)
              │
              └─→ Repository: findById(999)
                       │
                       └─→ Database: SELECT * FROM users WHERE id = 999
                                │
                                └─→ No rows found
                       ↓
                  Returns: Optional.empty()
              ↓
         Throws: new UserNotFoundException(999)
     ↓
GlobalExceptionHandler catches it
     │
     └─→ @ExceptionHandler(UserNotFoundException.class)
              │
              └─→ Creates ErrorResponse:
                  {
                    "status": 404,
                    "message": "User not found with id: 999",
                    "timestamp": "2024-11-03T10:30:00"
                  }
     ↓

Client: Receives 404 Not Found with error message
```

---

## Class Relationships

```
┌────────────────────┐
│  SamsApplication   │  ← Main entry point (@SpringBootApplication)
└────────────────────┘

┌────────────────────┐
│  SecurityConfig    │  ← Configures security rules
└────────────────────┘

┌────────────────────────────────────────────────────────┐
│                    USER MANAGEMENT                     │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌─────────────────┐                                  │
│  │ UserController  │  Has-a: UserService              │
│  └────────┬────────┘                                  │
│           │                                            │
│           │ uses                                       │
│           ▼                                            │
│  ┌─────────────────┐                                  │
│  │  UserService    │  Has-a: UserRepository           │
│  └────────┬────────┘                                  │
│           │                                            │
│           │ uses                                       │
│           ▼                                            │
│  ┌─────────────────┐                                  │
│  │ UserRepository  │  Manages: User entity            │
│  └────────┬────────┘                                  │
│           │                                            │
│           │ operates on                                │
│           ▼                                            │
│  ┌─────────────────┐                                  │
│  │  User (Entity)  │  Maps to: users table            │
│  └─────────────────┘                                  │
│                                                        │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│                        DTOs                            │
├────────────────────────────────────────────────────────┤
│                                                        │
│  UserRequest   ──→  Controller  ──→  Convert to Entity │
│                                                        │
│  Entity  ──→  Controller  ──→  Convert to UserResponse │
│                                                        │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│                  EXCEPTION HANDLING                    │
├────────────────────────────────────────────────────────┤
│                                                        │
│  Any Exception  ──→  GlobalExceptionHandler           │
│                           │                            │
│                           ├─→ UserNotFoundException    │
│                           ├─→ DuplicateEmailException  │
│                           └─→ Other exceptions         │
│                                                        │
└────────────────────────────────────────────────────────┘
```

---

## Dependency Injection Flow

```
Spring Container Starts
     │
     ├─→ Scans for @Component, @Service, @Repository, @Controller
     │
     ├─→ Creates UserRepository (Spring Data generates impl)
     │
     ├─→ Creates UserService
     │   │
     │   └─→ Needs UserRepository
     │       └─→ Spring injects it via constructor
     │
     └─→ Creates UserController
         │
         └─→ Needs UserService
             └─→ Spring injects it via constructor

Result: Fully wired application ready to handle requests!
```

---

## Testing Architecture

```
┌─────────────────────────────────────────────────┐
│             UserServiceTest                     │
│                                                 │
│  @Mock                                          │
│  UserRepository mockRepo;  ← Fake repository    │
│                                                 │
│  @InjectMocks                                   │
│  UserService service;  ← Real service with mock │
│                                                 │
│  @Test                                          │
│  void testCreateUser() {                        │
│    when(mockRepo.save(any()))                   │
│        .thenReturn(testUser);                   │
│                                                 │
│    User result = service.createUser(testUser);  │
│                                                 │
│    verify(mockRepo).save(testUser);             │
│  }                                              │
└─────────────────────────────────────────────────┘
                      │
                      │ No database involved!
                      ▼
              Tests run in milliseconds
```

---

## Data Flow: Entity vs DTO

```
DATABASE LAYER (Entity)
┌─────────────────────────┐
│      User Entity        │
│  - id                   │
│  - username             │
│  - email                │
│  - password  ⚠️         │
│  - role                 │
│  - createdAt            │
│  - updatedAt            │
└────────┬────────────────┘
         │
         │ Used internally
         │
         ▼
    SERVICE LAYER
    (Works with entities)
         │
         │ Converts
         ▼
API LAYER (DTOs)
┌─────────────────────────┐      ┌─────────────────────────┐
│    UserRequest          │      │    UserResponse         │
│  - username             │      │  - id                   │
│  - email                │      │  - username             │
│  - password             │      │  - email                │
│  - role                 │      │  - role                 │
└─────────────────────────┘      │  - createdAt            │
                                 │                         │
    ↑ From Client                │  ⭐ No password!        │
                                 └─────────────────────────┘
                                     ↓ To Client
```

**Why separate?**
- Security: Don't expose password
- Flexibility: API can differ from database
- Versioning: Change API without changing database

---

## Summary

### Key Architectural Principles

1. **Layered Architecture**: Each layer has one responsibility
2. **Dependency Injection**: Spring wires everything together
3. **DTO Pattern**: Control what data is exposed
4. **Repository Pattern**: Abstract data access
5. **Global Exception Handling**: Centralized error responses
6. **Test Isolation**: Mock dependencies for fast tests

### Request Flow Summary

```
Client → Controller → Service → Repository → Database
                ↓
            Response flows back with DTOs
```

### Remember

- **Controllers** handle HTTP
- **Services** handle logic
- **Repositories** handle data
- **DTOs** handle API format
- **Entities** handle database format
- **Exceptions** handle errors
