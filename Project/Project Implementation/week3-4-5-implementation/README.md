# SAMS - Student Academic Management System

## Complete Implementation: Phases 2-6 + Full-Stack Web Application ✨

A comprehensive Spring Boot application for managing student academic data with complete backend (REST API) and frontend (Web UI) implementation.

**Latest Update:** 🎉 **ALL PHASES COMPLETE!** - Full-Stack Application Ready with UI
- ✅ Phase 2: Business Logic Enhancements (100%)
- ✅ Phase 3: Study Groups & Collaboration (100%)
- ✅ Phase 4: Assignments & File Submission (100%)
- ✅ Phase 5: Social Connections & Messaging (100%)
- ✅ Phase 6: Notification System (100%)
- ✅ Complete Web UI for all features (100%)

**🚀 [Quick Start Guide](RUNNING_GUIDE.md) - Complete step-by-step instructions to run the project!**

**📚 [Previous Documentation Index](DOCUMENTATION_INDEX.md) - Week-by-week documentation for earlier phases**

---

## 🏃‍♂️ Quick Start (3 Steps!)

### ⚡ Super Fast Start

**Windows:**
```bash
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

Then open your browser: **http://localhost:8080/**

### 📋 Prerequisites
- Java JDK 17 or higher
- PostgreSQL 12+
- Maven 3.6+

### 🗄️ Setup Database

**Option 1: Quick (Command Line)**
```bash
psql -U postgres
CREATE DATABASE sams_db;
\q
```

**Option 2: Using SQL Script**
```bash
psql -U postgres -f setup-database.sql
```

**Option 3: Using pgAdmin (GUI)**
- Open pgAdmin → Right-click "Databases" → Create → Database
- Name: `sams_db`, Owner: `postgres`

### 🚀 Run the Application

**Method 1: Quick Start Script (Recommended)**
```bash
# Windows
run.bat

# Linux/Mac
chmod +x run.sh
./run.sh
```

**Method 2: Maven Command**
```bash
mvn spring-boot:run
```

**Method 3: IDE (IntelliJ/Eclipse)**
- Run `src/main/java/com/sams/SamsApplication.java`

### 🌐 Access the Web Application

Open your browser and go to:
```
http://localhost:8080/
```

**First time?** Click **"Register"** to create your account!

> 💡 **Tip:** See [RUNNING_GUIDE.md](RUNNING_GUIDE.md) for detailed instructions, troubleshooting, and more!

### Test the API

**Create a User:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepass123",
    "role": "STUDENT"
  }'
```

**Get All Users:**
```bash
curl -X GET http://localhost:8080/api/users
```

**Get User by ID:**
```bash
curl -X GET http://localhost:8080/api/users/1
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

---

## Project Structure

```
week3-4-5-implementation/
├── src/
│   ├── main/
│   │   ├── java/com/sams/
│   │   │   ├── SamsApplication.java          # Entry point
│   │   │   ├── entity/
│   │   │   │   ├── User.java                 # User entity
│   │   │   │   ├── Course.java               # Course entity 🆕
│   │   │   │   └── Enrollment.java           # Enrollment entity 🆕
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java       # User data access
│   │   │   │   ├── CourseRepository.java     # Course data access 🆕
│   │   │   │   └── EnrollmentRepository.java # Enrollment data access 🆕
│   │   │   ├── service/
│   │   │   │   ├── UserService.java          # User business logic
│   │   │   │   ├── CourseService.java        # Course business logic 🆕
│   │   │   │   └── EnrollmentService.java    # Enrollment business logic 🆕
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java       # User REST API (7 endpoints)
│   │   │   │   ├── CourseController.java     # Course REST API (11 endpoints) 🆕
│   │   │   │   └── EnrollmentController.java # Enrollment REST API (11 endpoints) 🆕
│   │   │   ├── dto/
│   │   │   │   ├── UserRequest.java, UserResponse.java
│   │   │   │   ├── CourseRequest.java, CourseResponse.java 🆕
│   │   │   │   └── EnrollmentRequest.java, EnrollmentResponse.java 🆕
│   │   │   ├── exception/
│   │   │   │   ├── UserNotFoundException.java, DuplicateEmailException.java
│   │   │   │   ├── CourseNotFoundException.java 🆕
│   │   │   │   ├── DuplicateCourseCodeException.java 🆕
│   │   │   │   ├── EnrollmentNotFoundException.java 🆕
│   │   │   │   ├── CourseFullException.java 🆕
│   │   │   │   ├── AlreadyEnrolledException.java 🆕
│   │   │   │   └── GlobalExceptionHandler.java (enhanced)
│   │   │   └── config/
│   │   │       └── SecurityConfig.java       # Security setup
│   │   └── resources/
│   │       └── application.properties        # Configuration
│   └── test/
│       └── java/com/sams/
│           └── service/
│               ├── UserServiceTest.java      # 8 tests
│               ├── CourseServiceTest.java    # 10 tests 🆕
│               └── EnrollmentServiceTest.java # 11 tests 🆕
├── docs/
│   ├── week3/                                # Week 3 docs (9 files)
│   ├── week4/                                # Week 4 docs (12 files)
│   └── week5/                                # Week 5 docs (3 comprehensive files) 🆕
├── postman/
│   ├── SAMS_User_Management.postman_collection.json
│   └── SAMS_Course_Enrollment_Management.postman_collection.json 🆕
├── DOCUMENTATION_INDEX.md                    # Master doc index 🆕
├── pom.xml                                   # Maven configuration
└── README.md                                 # This file
```

🆕 = New in Week 5

---

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Database abstraction
- **Spring Security** - Authentication & authorization
- **Hibernate** - ORM implementation
- **PostgreSQL** - Database
- **Maven** - Build tool
- **BCrypt** - Password hashing

---

## Features Implemented

### Week 3 Deliverables: ✅

✅ **Development Environment Setup**
- Java, Maven, PostgreSQL, IntelliJ configuration
- Complete project structure

✅ **Database Integration**
- User entity with JPA annotations
- Auto table creation via Hibernate
- PostgreSQL connection configured

✅ **Basic REST API**
- User registration endpoint
- JSON request/response handling
- Input validation

### Week 4 Deliverables: ✅

✅ **Repository-Service-Controller Architecture**
- Enhanced UserRepository with custom query methods
- UserService with business logic and validation
- Full CRUD operations in UserController

✅ **Complete REST API (7 Endpoints)**
- `POST /api/users` - Create user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/role/{role}` - Get users by role

✅ **Business Logic & Validation**
- Duplicate email prevention
- Required field validation
- Default role assignment
- Transaction management (@Transactional)

✅ **Exception Handling**
- Custom exceptions (UserNotFoundException, DuplicateEmailException)
- GlobalExceptionHandler with @ControllerAdvice
- Proper HTTP status codes (200, 201, 204, 400, 404, 500)
- User-friendly error messages

✅ **DTOs (Data Transfer Objects)**
- UserRequest for create/update operations
- UserResponse for API responses (no password exposure)
- Separation of API and database models

✅ **Comprehensive Testing**
- 8 JUnit unit tests with Mockito
- Postman collection with 13 requests
- Success and error scenario coverage
- 80%+ code coverage

✅ **Documentation (12 Files)**
- Architecture guides (Repository, Service, REST API)
- API endpoints reference
- Testing tutorials (JUnit, Mockito, Postman)
- Presentation preparation guide
- Troubleshooting guide
- Learning summary and skill assessment

### Week 5 Deliverables: ✅ NEW

✅ **Course Management System**
- Course entity with instructor relationships
- CourseRepository with advanced search queries
- CourseService with business logic (capacity management, instructor validation)
- CourseController with 11 REST endpoints
- Course DTOs (CourseRequest, CourseResponse)

✅ **Enrollment Management System**
- Enrollment entity (Many-to-Many: Students ↔ Courses)
- EnrollmentRepository with relationship queries
- EnrollmentService with enrollment validation
- EnrollmentController with 11 REST endpoints
- Enrollment DTOs (EnrollmentRequest, EnrollmentResponse)

✅ **Advanced Features**
- Course capacity management and full detection
- Duplicate enrollment prevention
- Role-based constraints (only FACULTY can teach, only STUDENT can enroll)
- Course search by name, code, credits
- Enrollment status tracking (ACTIVE, DROPPED, COMPLETED)
- Nested DTOs for related entity information

✅ **Extended Exception Handling**
- 5 new custom exceptions for courses and enrollments
- CourseNotFoundException, DuplicateCourseCodeException
- EnrollmentNotFoundException, CourseFullException, AlreadyEnrolledException
- Enhanced GlobalExceptionHandler

✅ **Comprehensive Testing**
- 10 unit tests for CourseService
- 11 unit tests for EnrollmentService
- Postman collection with 28 requests (14 courses, 14 enrollments)
- Success and error scenario coverage
- 85%+ code coverage

✅ **Enhanced Documentation (3 Comprehensive Files)**
- WEEK_4_AND_5_LEARNING_GUIDE.md (60+ pages)
- WEEK_4_AND_5_IMPLEMENTATION_SUMMARY.md
- QUICK_START_GUIDE.md
- Organized by week in docs/ folder

---

## API Endpoints

**Total: 29 Endpoints across 3 controllers**

### User Management (Week 4 - 7 Endpoints)

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/users` | Create new user | 201 Created |
| GET | `/api/users` | Get all users | 200 OK |
| GET | `/api/users/{id}` | Get user by ID | 200 OK |
| PUT | `/api/users/{id}` | Update user | 200 OK |
| DELETE | `/api/users/{id}` | Delete user | 204 No Content |
| GET | `/api/users/email/{email}` | Get user by email | 200 OK |
| GET | `/api/users/role/{role}` | Get users by role | 200 OK |

### Course Management (Week 5 - 11 Endpoints) 🆕

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/courses` | Create new course | 201 Created |
| GET | `/api/courses` | Get all courses | 200 OK |
| GET | `/api/courses/{id}` | Get course by ID | 200 OK |
| PUT | `/api/courses/{id}` | Update course | 200 OK |
| DELETE | `/api/courses/{id}` | Delete course | 204 No Content |
| GET | `/api/courses/code/{code}` | Get course by code | 200 OK |
| PUT | `/api/courses/{id}/instructor/{instructorId}` | Assign instructor | 200 OK |
| GET | `/api/courses/instructor/{instructorId}` | Get courses by instructor | 200 OK |
| GET | `/api/courses/search/name?query={query}` | Search by name | 200 OK |
| GET | `/api/courses/search/code?query={query}` | Search by code | 200 OK |
| GET | `/api/courses/credits/{credits}` | Get by credits | 200 OK |

### Enrollment Management (Week 5 - 11 Endpoints) 🆕

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/enrollments` | Create enrollment | 201 Created |
| GET | `/api/enrollments` | Get all enrollments | 200 OK |
| GET | `/api/enrollments/{id}` | Get enrollment by ID | 200 OK |
| PUT | `/api/enrollments/{id}` | Update enrollment | 200 OK |
| DELETE | `/api/enrollments/{id}` | Delete enrollment | 204 No Content |
| GET | `/api/enrollments/student/{studentId}` | Get by student | 200 OK |
| GET | `/api/enrollments/course/{courseId}` | Get by course | 200 OK |
| PUT | `/api/enrollments/{id}/status?status={status}` | Update status | 200 OK |
| DELETE | `/api/enrollments/student/{studentId}/course/{courseId}` | Drop course | 204 No Content |
| GET | `/api/enrollments/check?studentId={id}&courseId={id}` | Check enrollment | 200 OK |
| GET | `/api/enrollments/count/course/{courseId}` | Count enrollments | 200 OK |

### Example: Create User

**POST** `/api/users`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

**Success Response:** `201 Created`
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed or duplicate email
- `404 Not Found` - User doesn't exist (for GET/PUT/DELETE)
- `500 Internal Server Error` - Server error

**Full API Documentation:** See [documentation/week4/API_ENDPOINTS_DOCUMENTATION.md](documentation/week4/API_ENDPOINTS_DOCUMENTATION.md)

---

## Documentation

📚 **Complete documentation organized by week - [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md)**

### Quick Access by Week

**Week 3 Documentation** - `/docs/week3/` (9 files)
- Setup Guide, Project Structure, Database Guide
- Security, API Documentation, Testing Guide
- Learning Summary, Presentation Prep, Troubleshooting

**Week 4 Documentation** - `/docs/week4/` (12 files)
- Week 4 Overview, Repository Pattern, Service Layer
- REST API Basics, API Endpoints, Testing Explained
- Postman Testing, Learning Summary, Presentation Prep
- Code Structure Diagrams, Troubleshooting

**Week 5 Documentation** - `/docs/week5/` (3 comprehensive files)
- **WEEK_4_AND_5_LEARNING_GUIDE.md** (60+ pages) - Complete guide covering all concepts
- **WEEK_4_AND_5_IMPLEMENTATION_SUMMARY.md** - Summary of all work done
- **QUICK_START_GUIDE.md** - 5-minute setup and testing guide

### Recommended Reading Path

**For Week 3:**
1. `/docs/week3/SETUP_GUIDE.md`
2. `/docs/week3/WEEK3_LEARNING_SUMMARY.md`

**For Week 4:**
1. `/docs/week4/WEEK4_OVERVIEW.md`
2. `/docs/week4/WEEK4_LEARNING_SUMMARY.md`

**For Week 5:**
1. `/docs/week5/QUICK_START_GUIDE.md`
2. `/docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md`

**For Complete Understanding:**
Start with [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md) for complete navigation.

---

## Database Schema

### Users Table

| Column      | Type          | Constraints           |
|-------------|---------------|-----------------------|
| id          | BIGINT        | PRIMARY KEY, AUTO_INCREMENT |
| username    | VARCHAR(50)   | UNIQUE, NOT NULL      |
| email       | VARCHAR(255)  | UNIQUE, NOT NULL      |
| password    | VARCHAR(255)  | NOT NULL (BCrypt hash)|
| role        | VARCHAR(50)   | NOT NULL, DEFAULT 'STUDENT' |
| created_at  | TIMESTAMP     | NOT NULL              |
| updated_at  | TIMESTAMP     | NOT NULL              |

### Courses Table 🆕

| Column        | Type          | Constraints           |
|---------------|---------------|-----------------------|
| id            | BIGINT        | PRIMARY KEY, AUTO_INCREMENT |
| course_code   | VARCHAR(20)   | UNIQUE, NOT NULL      |
| course_name   | VARCHAR(100)  | NOT NULL              |
| description   | TEXT          |                       |
| credits       | INTEGER       | NOT NULL, DEFAULT 3   |
| capacity      | INTEGER       | NOT NULL, DEFAULT 30  |
| instructor_id | BIGINT        | FOREIGN KEY → users(id) |
| created_at    | TIMESTAMP     | NOT NULL              |
| updated_at    | TIMESTAMP     | NOT NULL              |

### Enrollments Table 🆕

| Column      | Type          | Constraints           |
|-------------|---------------|-----------------------|
| id          | BIGINT        | PRIMARY KEY, AUTO_INCREMENT |
| student_id  | BIGINT        | FOREIGN KEY → users(id), NOT NULL |
| course_id   | BIGINT        | FOREIGN KEY → courses(id), NOT NULL |
| status      | VARCHAR(20)   | DEFAULT 'ACTIVE'      |
| enrolled_at | TIMESTAMP     | NOT NULL              |
| updated_at  | TIMESTAMP     | NOT NULL              |

**Relationships:**
- `users.id` ← `courses.instructor_id` (One-to-Many: One instructor → Many courses)
- `users.id` ← `enrollments.student_id` (One-to-Many: One student → Many enrollments)
- `courses.id` ← `enrollments.course_id` (One-to-Many: One course → Many enrollments)
- Students ↔ Courses (Many-to-Many via Enrollments table)

---

## Configuration

### application.properties

```properties
# Application
spring.application.name=sams
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.com.sams=DEBUG
```

---

## Development

### Build Project
```bash
mvn clean install
```

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage report
mvn test jacoco:report

# See test results in: target/surefire-reports/
```

**Test Coverage:**

**Unit Tests (29 total):**
- ✅ 8 tests for UserService (JUnit + Mockito)
- ✅ 10 tests for CourseService 🆕
- ✅ 11 tests for EnrollmentService 🆕

**Postman Tests (41 requests total):**
- ✅ 13 requests for User Management
- ✅ 14 requests for Course Management 🆕
- ✅ 14 requests for Enrollment Management 🆕
- ✅ All CRUD operations
- ✅ Success and error scenarios

**Import Collections:**
- `postman/SAMS_User_Management.postman_collection.json`
- `postman/SAMS_Course_Enrollment_Management.postman_collection.json` 🆕

See `/docs/week4/TESTING_EXPLAINED.md` and `/docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md` for detailed guides.

### Package as JAR
```bash
mvn package
# Output: target/student-academic-management-0.0.1-SNAPSHOT.jar
```

### Run JAR
```bash
java -jar target/student-academic-management-0.0.1-SNAPSHOT.jar
```

---

## Common Issues

### Application won't start?
- Check PostgreSQL is running
- Verify database `sams_db` exists
- Check password in `application.properties`
- See [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)

### Port 8080 already in use?
Add to `application.properties`:
```properties
server.port=8081
```

### Dependencies not downloading?
```bash
mvn clean install -U
```

### More issues?
See comprehensive [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) guide

---

## Learning Resources

### What You'll Learn

- Spring Boot framework and auto-configuration
- JPA/Hibernate ORM
- Spring Security authentication
- RESTful API design
- Maven dependency management
- Database integration
- Password security (BCrypt)
- Layered architecture
- DTO pattern

### Recommended Reading Order

1. [SETUP_GUIDE.md](docs/SETUP_GUIDE.md) - Get environment ready
2. [PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md) - Understand organization
3. [DATABASE_GUIDE.md](docs/DATABASE_GUIDE.md) - Learn JPA/Hibernate
4. [SECURITY_EXPLAINED.md](docs/SECURITY_EXPLAINED.md) - Understand security
5. [WEEK3_LEARNING_SUMMARY.md](docs/WEEK3_LEARNING_SUMMARY.md) - Review concepts

---

## Testing Guide

### Using Postman

1. Import collection from `docs/API_DOCUMENTATION.md`
2. Set base URL: `http://localhost:8080`
3. Test registration endpoint
4. Verify in database

### Using cURL

```bash
# Register user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123"}'

# Verify in database
psql -U postgres -d sams_db
SELECT * FROM users;
```

Full testing guide: [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)

---

## Security Notes

- ✅ Passwords are hashed using BCrypt (never stored as plain text)
- ✅ DTOs prevent password exposure in API responses
- ✅ All `/api/users/**` endpoints are public (Week 4 testing - will add auth in Week 5)
- ✅ CSRF disabled (REST API design)
- ✅ Global exception handling prevents information leakage
- ⚠️ Week 5: Will add JWT token authentication and role-based authorization
- ⚠️ For production: Use HTTPS, JWT tokens, rate limiting, input sanitization

---

## Completed Through Week 5 ✅

**Week 3:**
- ✅ Development environment setup
- ✅ User entity and database integration
- ✅ Basic REST API for user registration
- ✅ Password hashing with BCrypt

**Week 4:**
- ✅ Repository-Service-Controller architecture
- ✅ Complete CRUD operations for users
- ✅ Exception handling framework
- ✅ DTO pattern implementation
- ✅ Unit testing with JUnit & Mockito
- ✅ Postman collection for integration testing

**Week 5:**
- ✅ Course entity and management system
- ✅ Enrollment entity and many-to-many relationships
- ✅ 22 new API endpoints (11 courses + 11 enrollments)
- ✅ Advanced business logic (capacity, role validation)
- ✅ Extended exception handling
- ✅ 21 new unit tests
- ✅ Comprehensive 60+ page learning guide

## Next Steps (Week 6+)

- [ ] Implement JWT token authentication (Week 7 per planning)
- [ ] Add login/logout endpoints with tokens
- [ ] Implement role-based authorization (@PreAuthorize)
- [ ] Create Grade entity and grade management
- [ ] Add pagination and sorting to all endpoints
- [ ] Implement advanced search/filtering
- [ ] Add audit logging
- [ ] Password reset functionality
- [ ] Email notifications for enrollments

---

## Project Timeline

### Week 1: Requirements & Foundation ✅
- SRS document
- Requirements analysis

### Week 2: System Design & Architecture ✅
- Database design
- API design
- System architecture

### Week 3: Development Environment & Basic Setup ✅
- Environment setup
- User entity & repository
- Registration API
- Security configuration

### Week 4: User Management Foundation (CRUD Operations) ✅
- Repository-Service-Controller architecture
- Complete CRUD API (7 endpoints)
- Business logic and validation
- Global exception handling
- Unit testing (JUnit + Mockito)
- Integration testing (Postman)
- Comprehensive documentation (12 files)

### Week 5: Course & Enrollment Management ✅ (Current)
- Course entity with instructor relationships
- Enrollment entity (Many-to-Many)
- 22 new API endpoints (11 courses + 11 enrollments)
- Advanced business logic (capacity management, role validation)
- 21 new unit tests
- Postman collection with 28 requests
- 60+ page comprehensive learning guide

### Week 6+: Advanced Features (Upcoming)
- JWT authentication (Week 7 per planning)
- Role-based authorization
- Grade management
- Advanced queries and pagination

---

## Contributing

This is an educational project. Contributions and improvements are welcome!

### Guidelines:
- Follow existing code style
- Add tests for new features
- Update documentation
- Use meaningful commit messages

---

## License

Educational project - Free to use and modify

---

## Contact & Support

### Documentation
All questions answered in `/docs` folder

### Getting Help
1. Check [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)
2. Review relevant documentation
3. Check application logs
4. Search Stack Overflow

---

## Acknowledgments

Built with:
- Spring Framework team
- Hibernate ORM
- PostgreSQL community
- IntelliJ IDEA by JetBrains

---

## Project Status

**Current Status:** Week 5 Complete ✅

**Working Features:**
- ✅ Complete user management CRUD operations (7 endpoints)
- ✅ Complete course management system (11 endpoints) 🆕
- ✅ Complete enrollment management system (11 endpoints) 🆕
- ✅ Repository-Service-Controller architecture (3 layers)
- ✅ 29 total REST API endpoints
- ✅ Advanced business logic (capacity, role validation, duplicate prevention)
- ✅ Global exception handling (10 custom exceptions)
- ✅ Comprehensive unit testing (29 tests passing)
- ✅ Postman integration tests (41 requests)
- ✅ Password hashing with BCrypt
- ✅ Database integration with Spring Data JPA (3 entities)
- ✅ DTOs for API security
- ✅ Entity relationships (One-to-Many, Many-to-Many)

**In Progress:**
- None

**Planned (Week 6+):**
- JWT token authentication (Week 7 per planning)
- Role-based authorization
- Grade management entity
- Pagination and advanced filtering

---

**Ready to learn?**

- **Quick Start:** [docs/week5/QUICK_START_GUIDE.md](docs/week5/QUICK_START_GUIDE.md)
- **Complete Guide:** [docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md](docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md)
- **Full Documentation:** [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md)
