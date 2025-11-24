# Week 4 Overview - User Management Foundation

## What We Accomplished This Week

Week 4 was all about building the **foundational user management system** using proper architectural patterns. We implemented complete CRUD (Create, Read, Update, Delete) operations for users following industry-standard practices.

## Key Deliverables

### 1. **Repository Layer** (Monday)
- Enhanced `UserRepository` with custom query methods
- Added methods: `findByRole()`, `findByEmailAndRole()`
- Learned how Spring Data JPA auto-implements these methods

### 2. **Service Layer** (Tuesday)
- Created `UserService` with complete business logic
- Implemented validation and error handling
- Used `@Transactional` for data integrity
- Created custom exceptions: `UserNotFoundException`, `DuplicateEmailException`

### 3. **Controller Layer** (Wednesday)
- Built `UserController` with 7 REST endpoints
- Implemented all CRUD operations
- Created DTOs: `UserRequest`, `UserResponse`
- Added `GlobalExceptionHandler` for unified error responses
- Used proper HTTP status codes

### 4. **Testing** (Thursday & Friday)
- Wrote 8 unit tests for `UserService` using JUnit & Mockito
- Created comprehensive Postman collection with 13 requests
- Tested both success and error scenarios

## Architecture Pattern: Repository-Service-Controller

```
┌─────────────┐
│   Client    │ (Postman, Browser, Mobile App)
└──────┬──────┘
       │ HTTP Request (JSON)
       ▼
┌─────────────────────────────────────────┐
│        CONTROLLER LAYER                 │
│  - Handles HTTP requests/responses      │
│  - Validates input (@Valid)             │
│  - Returns DTOs (not entities)          │
│  - Maps endpoints to service methods    │
└──────┬──────────────────────────────────┘
       │ Calls service methods
       ▼
┌─────────────────────────────────────────┐
│        SERVICE LAYER                    │
│  - Business logic & validation          │
│  - Transaction management               │
│  - Throws custom exceptions             │
│  - Orchestrates repository calls        │
└──────┬──────────────────────────────────┘
       │ Calls repository methods
       ▼
┌─────────────────────────────────────────┐
│        REPOSITORY LAYER                 │
│  - Data access abstraction              │
│  - Auto-implemented by Spring Data JPA  │
│  - Direct database interaction          │
└──────┬──────────────────────────────────┘
       │ SQL queries
       ▼
┌─────────────────────────────────────────┐
│        DATABASE (PostgreSQL)            │
│  - Stores user data                     │
└─────────────────────────────────────────┘
```

## API Endpoints Implemented

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/users` | Create new user | 201 Created |
| GET | `/api/users` | Get all users | 200 OK |
| GET | `/api/users/{id}` | Get user by ID | 200 OK / 404 Not Found |
| PUT | `/api/users/{id}` | Update user | 200 OK / 404 Not Found |
| DELETE | `/api/users/{id}` | Delete user | 204 No Content / 404 Not Found |
| GET | `/api/users/email/{email}` | Get user by email | 200 OK / 404 Not Found |
| GET | `/api/users/role/{role}` | Get users by role | 200 OK |

## Key Concepts Learned

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Dependency Injection**: Using constructor injection for loose coupling
3. **DTOs vs Entities**: Never expose entities directly in API responses
4. **Exception Handling**: Custom exceptions with global handler
5. **REST Principles**: Proper use of HTTP methods and status codes
6. **Testing**: Unit tests with mocking vs integration tests with Postman

## Project Structure

```
src/
├── main/
│   ├── java/com/sams/
│   │   ├── entity/
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   └── UserRepository.java ⭐ Enhanced
│   │   ├── service/
│   │   │   └── UserService.java ⭐ NEW
│   │   ├── controller/
│   │   │   └── UserController.java ⭐ Refactored
│   │   ├── dto/
│   │   │   ├── UserRequest.java ⭐ NEW
│   │   │   └── UserResponse.java
│   │   ├── exception/
│   │   │   ├── UserNotFoundException.java ⭐ NEW
│   │   │   ├── DuplicateEmailException.java ⭐ NEW
│   │   │   └── GlobalExceptionHandler.java ⭐ NEW
│   │   └── config/
│   │       └── SecurityConfig.java (Updated)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/sams/
        └── service/
            └── UserServiceTest.java ⭐ NEW
```

## What's Next?

Week 5 will likely focus on:
- Advanced authentication (JWT tokens)
- Role-based authorization
- More complex business logic
- Additional entities and relationships

## Time Invested

- Monday: 2.5 hours (Repository layer)
- Tuesday: 2 hours (Service layer)
- Wednesday: 2 hours (Controller layer)
- Thursday: 2 hours (Unit testing)
- Friday: 1 hour (Postman testing)
- Weekend: 0.5 hours (Code review & docs)

**Total: ~10 hours**

## Success Metrics

✅ All CRUD operations working
✅ Proper error handling implemented
✅ 8 unit tests passing
✅ 13 Postman requests tested
✅ Clean separation of layers
✅ No authentication required (for Week 4 testing)
✅ Ready for demo and presentation
