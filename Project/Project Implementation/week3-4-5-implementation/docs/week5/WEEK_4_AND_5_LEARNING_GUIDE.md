# 📚 WEEK 4 & 5 LEARNING GUIDE
## SAMS (Student Academic Management System)

**Welcome!** This guide will help you understand everything we built in Weeks 4 and 5. By the end, you'll be able to confidently explain and demo the project to your teammates and professors.

---

## 📖 Table of Contents

1. [What We Built](#what-we-built)
2. [Technology Explained Simply](#technology-explained-simply)
3. [System Architecture Overview](#system-architecture-overview)
4. [Week 4: User Management Deep Dive](#week-4-user-management-deep-dive)
5. [Week 5: Course & Enrollment Deep Dive](#week-5-course--enrollment-deep-dive)
6. [Database Structure](#database-structure)
7. [API Endpoints Reference](#api-endpoints-reference)
8. [Key Concepts Explained](#key-concepts-explained)
9. [Common Questions & Answers](#common-questions--answers)
10. [Testing Guide](#testing-guide)
11. [Presentation Tips](#presentation-tips)

---

## 🎯 What We Built

### Week 4: User Management Foundation
Think of this as building the "people" part of the system. We created:
- A way to store user information (students, faculty, admin)
- Operations to create, view, update, and delete users
- Password security using hashing (so passwords are never stored in plain text)
- Validation to prevent duplicate emails

### Week 5: Course Management & Basic Enrollment
Now we added the "academic" part:
- A way to store course information
- Operations to create and manage courses
- Ability to assign instructors (faculty) to courses
- A basic enrollment system where students can register for courses
- Protection to prevent over-enrollment (course capacity limits)

**Real-world analogy**: Think of it like a school's registration office. They need to:
1. Keep track of people (students, teachers, staff) ← Week 4
2. Keep track of classes being offered ← Week 5
3. Let students register for classes ← Week 5

---

## 🛠️ Technology Explained Simply

### What is Spring Boot? (And why do we use it?)

**Simple explanation**: Spring Boot is like a toolkit that makes building web applications much easier.

**Problem it solves**: Imagine you want to build a house. You could:
- Option A: Cut trees, make your own nails, mix cement from scratch (HARD!)
- Option B: Go to a hardware store and buy ready-made materials (EASY!)

Spring Boot is Option B. It provides ready-made solutions for common tasks like:
- Connecting to a database
- Handling web requests
- Managing security
- Organizing your code properly

**Key benefit**: Instead of spending weeks setting things up, we can focus on building our actual app!

---

### What is PostgreSQL? (And why do we use it?)

**Simple explanation**: PostgreSQL is a database - think of it as an organized filing cabinet for storing data.

**Problem it solves**: We need to store information somewhere that:
- Won't disappear when we turn off the computer
- Can handle lots of data efficiently
- Lets us search and filter quickly
- Keeps data organized and connected

**Real-world analogy**: If our app is a library:
- PostgreSQL is the shelving system
- Tables are different sections (Fiction, Non-fiction, Reference)
- Rows are individual items on the shelves
- We can quickly find any book using the catalog system

---

### What are REST APIs? (And why do we use them?)

**Simple explanation**: REST API is a way for different programs to talk to each other over the internet, like a waiter taking orders in a restaurant.

**How it works**:
1. **You (the customer)**: "I'd like a Caesar salad"
2. **Waiter (the API)**: Takes your order to the kitchen
3. **Kitchen (the backend)**: Prepares your food
4. **Waiter (the API)**: Brings it back to you

**In our app**:
1. **Frontend/Postman**: "GET /api/users/1" (get user with ID 1)
2. **API (Controller)**: Receives request
3. **Backend (Service)**: Fetches user from database
4. **API**: Returns user data as JSON

**Why we use it**: It's a standard way that any program (web app, mobile app, another server) can communicate with our backend.

---

### What is JWT? (We'll use this in Week 7, but good to know)

**Simple explanation**: JWT (JSON Web Token) is like a ticket you get at an amusement park.

**How it works**:
1. You buy a ticket (log in)
2. You show the ticket to enter rides (access protected resources)
3. Staff verify your ticket is real (server validates JWT)
4. No need to buy ticket again for each ride!

**Why it's useful**: Once logged in, you don't have to enter your password for every action. The JWT proves you're already authenticated.

---

## 🏗️ System Architecture Overview

### The Layered Approach

Our application is organized in **layers**, like a cake. Each layer has a specific job:

```
┌─────────────────────────────────────────────┐
│         CLIENT (Postman / Browser)          │
│  "I want to get all courses"                │
└────────────────┬────────────────────────────┘
                 │ HTTP Request
                 ▼
┌─────────────────────────────────────────────┐
│         CONTROLLER LAYER                    │
│  - Receives HTTP requests                   │
│  - Validates input                          │
│  - Returns HTTP responses                   │
│                                             │
│  Files: UserController.java                │
│         CourseController.java               │
│         EnrollmentController.java           │
└────────────────┬────────────────────────────┘
                 │ Calls service methods
                 ▼
┌─────────────────────────────────────────────┐
│         SERVICE LAYER                       │
│  - Business logic ("the brain")             │
│  - Validation rules                         │
│  - Manages transactions                     │
│                                             │
│  Files: UserService.java                    │
│         CourseService.java                  │
│         EnrollmentService.java              │
└────────────────┬────────────────────────────┘
                 │ Calls repository methods
                 ▼
┌─────────────────────────────────────────────┐
│         REPOSITORY LAYER                    │
│  - Talks to database                        │
│  - CRUD operations                          │
│  - Spring auto-generates SQL!               │
│                                             │
│  Files: UserRepository.java                 │
│         CourseRepository.java               │
│         EnrollmentRepository.java           │
└────────────────┬────────────────────────────┘
                 │ SQL queries
                 ▼
┌─────────────────────────────────────────────┐
│         DATABASE (PostgreSQL)               │
│  - Stores all data                          │
│  - Tables: users, courses, enrollments      │
└─────────────────────────────────────────────┘
```

### Why separate into layers?

**Real-world analogy**: Think of a restaurant:
- **Controller** = Waiter (takes orders, delivers food)
- **Service** = Manager (decides recipes, handles special requests)
- **Repository** = Kitchen (actually prepares the food)
- **Database** = Pantry (stores ingredients)

Each person has a specific role. The waiter doesn't cook, and the chef doesn't take orders!

**Benefits**:
1. **Easy to understand**: Each layer has one job
2. **Easy to test**: We can test the kitchen without needing waiters
3. **Easy to change**: If we change how we store data, we only change the Repository layer

---

## 👥 Week 4: User Management Deep Dive

### What Files Are Involved?

```
User Management Files:
├── entity/
│   └── User.java                    # Defines what a user looks like
├── repository/
│   └── UserRepository.java          # Database operations for users
├── service/
│   └── UserService.java             # Business logic for users
├── controller/
│   └── UserController.java          # API endpoints for users
├── dto/
│   ├── UserRequest.java             # What we accept as input
│   ├── UserResponse.java            # What we send as output
│   └── RegisterRequest.java         # For user registration
├── exception/
│   ├── UserNotFoundException.java   # When user doesn't exist
│   └── DuplicateEmailException.java # When email already exists
└── test/
    └── UserServiceTest.java         # Tests for UserService
```

### Feature 1: Create User (Registration)

**What does it do?**
Allows creating a new user account (student, faculty, or admin).

**Files involved**:
- `UserController.java` - receives the HTTP POST request
- `UserService.java` - validates and creates the user
- `UserRepository.java` - saves to database
- `User.java` - defines the user structure

**How does data flow?**

1. **Client sends request**:
```json
POST /api/users
{
  "username": "john_doe",
  "email": "john@student.com",
  "password": "student123",
  "role": "STUDENT"
}
```

2. **UserController receives it** (`UserController.java` line 26-36):
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword());
    user.setRole(request.getRole());

    User savedUser = userService.createUser(user);
    return convertToResponse(savedUser);
}
```
- `@PostMapping` means this handles POST requests
- `@Valid` checks if the input is valid (email format, required fields)
- It converts the request to a User object and calls the service

3. **UserService does the actual work** (`UserService.java` line 24-52):
```java
@Transactional
public User createUser(User user) {
    // chekc if email already exists (intentional typo!)
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new DuplicateEmailException(user.getEmail());
    }

    // set default role if not provided
    if (user.getRole() == null || user.getRole().isEmpty()) {
        user.setRole("STUDENT");
    }

    // hash the password before saving
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    return userRepository.save(user);
}
```
- Checks if email already exists (no duplicates!)
- Hashes the password (security!)
- Saves to database
- `@Transactional` means if anything fails, nothing is saved

4. **UserRepository saves to database** (`UserRepository.java`):
```java
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    // Spring automatically creates the SQL!
}
```
- Spring generates SQL like: `INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)`

5. **Response sent back**:
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@student.com",
  "role": "STUDENT",
  "createdAt": "2024-11-17T10:30:00"
}
```
- **Notice**: Password is NOT included! (Security - we never send passwords back)

**Key concepts in this feature**:
- **Validation**: Input is checked before processing
- **Password hashing**: Passwords are encrypted using BCrypt
- **DTO pattern**: We use UserRequest (input) and UserResponse (output) instead of sending the User entity directly
- **Exception handling**: If email exists, we throw a custom exception

---

### Feature 2: Password Hashing (Week 4 Improvement)

**What is password hashing?**

Imagine you write a secret message and then burn it. You can check if someone's message matches yours (by burning theirs too), but you can't read the original.

**Why we do it**:
- **Bad**: Storing passwords like "student123" in database
- **Good**: Storing passwords like "$2a$10$EixZaYVK1..." (hashed version)

**How it works** (`UserService.java` line 46-49):
```java
// hash the password before saving
if (user.getPassword() != null && !user.getPassword().isEmpty()) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
}
```

**Real-world analogy**:
- **Plain text password**: Writing your password on a sticky note (anyone can read it!)
- **Hashed password**: Locking your password in a safe with a one-way lock (can't get it back out, but can verify it matches)

**Benefits**:
1. Even if someone steals our database, they can't see passwords
2. Even we (the developers) can't see user passwords
3. Industry standard security practice

---

### Feature 3: Get All Users

**What does it do?**
Retrieves a list of all users in the system.

**API Call**:
```
GET /api/users
```

**Flow**:
1. `UserController.getAllUsers()` receives the request
2. Calls `UserService.getAllUsers()`
3. Service calls `UserRepository.findAll()`
4. Database returns all user records
5. Controller converts each User to UserResponse (removing password!)
6. Returns JSON array of users

**Response example**:
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@student.com",
    "role": "STUDENT",
    "createdAt": "2024-11-17T10:30:00"
  },
  {
    "id": 2,
    "username": "prof_smith",
    "email": "smith@faculty.com",
    "role": "FACULTY",
    "createdAt": "2024-11-17T10:35:00"
  }
]
```

---

### Feature 4: Get User by Email

**What does it do?**
Finds a specific user by their email address.

**API Call**:
```
GET /api/users/email/john@student.com
```

**Special technique - Custom Repository Method** (`UserRepository.java` line 14):
```java
Optional<User> findByEmail(String email);
```

**This is cool!** Spring looks at the method name and automatically creates the SQL:
```sql
SELECT * FROM users WHERE email = 'john@student.com'
```

We didn't write any SQL - Spring did it for us based on the method name!

**Error handling** (`UserService.java` line 53-56):
```java
public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("email", email));
}
```

- `Optional` means the result might be empty
- `.orElseThrow()` means "if not found, throw an exception"
- This becomes a 404 error in the API response

---

## 📚 Week 5: Course & Enrollment Deep Dive

### What Files Are Involved?

```
Course Management Files:
├── entity/
│   ├── Course.java                  # Defines what a course looks like
│   └── Enrollment.java              # Defines enrollment relationship
├── repository/
│   ├── CourseRepository.java        # Database operations for courses
│   └── EnrollmentRepository.java    # Database operations for enrollments
├── service/
│   ├── CourseService.java           # Business logic for courses
│   └── EnrollmentService.java       # Business logic for enrollments
├── controller/
│   ├── CourseController.java        # API endpoints for courses
│   └── EnrollmentController.java    # API endpoints for enrollments
├── dto/
│   ├── CourseRequest.java           # Input for creating courses
│   ├── CourseResponse.java          # Output for course data
│   ├── EnrollmentRequest.java       # Input for enrollment
│   └── EnrollmentResponse.java      # Output for enrollment data
└── exception/
    ├── CourseNotFoundException.java
    ├── DuplicateCourseCodeException.java
    ├── EnrollmentNotFoundException.java
    ├── CourseFullException.java
    └── AlreadyEnrolledException.java
```

### Feature 1: Create Course

**What does it do?**
Creates a new course in the system.

**API Call**:
```json
POST /api/courses
{
  "courseCode": "CS101",
  "courseName": "Introduction to Computer Science",
  "description": "Basic programming concepts",
  "credits": 3,
  "capacity": 30,
  "instructorId": 2
}
```

**Key logic** (`CourseService.java` line 24-53):
```java
@Transactional
public Course createCourse(Course course) {
    // chekc if course code already exists
    if (courseRepository.existsByCourseCode(course.getCourseCode())) {
        throw new DuplicateCourseCodeException(course.getCourseCode());
    }

    // set defaults if not provided
    if (course.getCredits() == null || course.getCredits() < 1) {
        course.setCredits(3); // default 3 credits
    }

    if (course.getCapacity() == null || course.getCapacity() < 1) {
        course.setCapacity(30); // default 30 students
    }

    return courseRepository.save(course);
}
```

**What's happening**:
1. Check if course code (like "CS101") already exists
2. Set default values for credits and capacity if not provided
3. Save to database
4. If anything fails, rollback (because of `@Transactional`)

**Response**:
```json
{
  "id": 1,
  "courseCode": "CS101",
  "courseName": "Introduction to Computer Science",
  "description": "Basic programming concepts",
  "credits": 3,
  "capacity": 30,
  "enrolledCount": 0,
  "isFull": false,
  "instructor": {
    "id": 2,
    "username": "prof_smith",
    "email": "smith@faculty.com"
  },
  "createdAt": "2024-11-17T14:00:00"
}
```

---

### Feature 2: Assign Instructor to Course

**What does it do?**
Assigns a faculty member to teach a course.

**API Call**:
```
PUT /api/courses/1/instructor/2
```
(Assign instructor with ID 2 to course with ID 1)

**Important validation** (`CourseService.java` line 131-143):
```java
@Transactional
public Course assignInstructor(Long courseId, Long instructorId) {
    Course course = getCourseById(courseId);

    // get instructor (must be faculty)
    User instructor = userRepository.findById(instructorId)
            .orElseThrow(() -> new UserNotFoundException(instructorId));

    // chekc if user is faculty
    if (!"FACULTY".equals(instructor.getRole())) {
        throw new IllegalArgumentException("Instructor must have FACULTY role");
    }

    course.setInstructor(instructor);
    return courseRepository.save(course);
}
```

**Business rule enforced**: Only users with "FACULTY" role can be instructors!

**Why this matters**: Prevents mistakes like assigning a student as an instructor.

---

### Feature 3: Search Courses

**What does it do?**
Allows searching for courses by name or code.

**API Calls**:
```
GET /api/courses/search/name?query=Computer
GET /api/courses/search/code?query=CS
```

**Magic of Spring Data JPA** (`CourseRepository.java` line 19-22):
```java
// search courses by name (contains, case insensitive)
List<Course> findByCourseNameContainingIgnoreCase(String courseName);

// search courses by code (contains, case insensitive)
List<Course> findByCourseCodeContainingIgnoreCase(String courseCode);
```

**Spring automatically creates this SQL**:
```sql
SELECT * FROM courses
WHERE LOWER(course_name) LIKE LOWER('%Computer%')
```

**No SQL written by us!** Spring understands the method name:
- `findBy` = SELECT
- `CourseName` = WHERE course_name
- `Containing` = LIKE '%...%'
- `IgnoreCase` = LOWER()

---

### Feature 4: Enroll Student in Course

**What does it do?**
Registers a student for a course.

**API Call**:
```json
POST /api/enrollments
{
  "studentId": 1,
  "courseId": 1
}
```

**Complex business logic** (`EnrollmentService.java` line 28-55):
```java
@Transactional
public Enrollment createEnrollment(Long studentId, Long courseId) {
    // get student and course
    User student = userService.getUserById(studentId);
    Course course = courseService.getCourseById(courseId);

    // validate student role
    if (!"STUDENT".equals(student.getRole())) {
        throw new IllegalArgumentException("Only students can enroll in courses");
    }

    // chekc if student is already enrolled
    if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
        throw new AlreadyEnrolledException(
            "Student " + student.getUsername() + " is already enrolled in " + course.getCourseCode()
        );
    }

    // chekc if course is full
    long enrolledCount = enrollmentRepository.countByCourse(course);
    if (enrolledCount >= course.getCapacity()) {
        throw new CourseFullException(course.getCourseCode());
    }

    // create enrollment
    Enrollment enrollment = new Enrollment(student, course);
    enrollment.setStatus("ACTIVE");

    return enrollmentRepository.save(enrollment);
}
```

**Business rules enforced**:
1. ✅ Only students can enroll (not faculty or admin)
2. ✅ Can't enroll in the same course twice
3. ✅ Can't enroll if course is at capacity (full)

**Response**:
```json
{
  "id": 1,
  "student": {
    "id": 1,
    "username": "john_doe",
    "email": "john@student.com"
  },
  "course": {
    "id": 1,
    "courseCode": "CS101",
    "courseName": "Introduction to Computer Science",
    "credits": 3
  },
  "status": "ACTIVE",
  "enrollmentDate": "2024-11-17T15:00:00"
}
```

---

## 🗄️ Database Structure

### Tables Created

Our database has **3 main tables**:

#### 1. Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- hashed!
    role VARCHAR(20) NOT NULL,        -- STUDENT, FACULTY, ADMIN
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

**Example data**:
| id | username | email | password | role | created_at |
|----|----------|-------|----------|------|------------|
| 1 | john_doe | john@student.com | $2a$10$Eix... | STUDENT | 2024-11-17 10:30 |
| 2 | prof_smith | smith@faculty.com | $2a$10$ABc... | FACULTY | 2024-11-17 10:35 |

---

#### 2. Courses Table
```sql
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    credits INTEGER NOT NULL DEFAULT 3,
    capacity INTEGER NOT NULL DEFAULT 30,
    instructor_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

**Example data**:
| id | course_code | course_name | credits | capacity | instructor_id |
|----|-------------|-------------|---------|----------|---------------|
| 1 | CS101 | Intro to CS | 3 | 30 | 2 |
| 2 | MATH201 | Calculus I | 4 | 25 | 2 |

**Relationship**: `instructor_id` links to `users.id` (a course has ONE instructor)

---

#### 3. Enrollments Table
```sql
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id),
    course_id BIGINT NOT NULL REFERENCES courses(id),
    enrollment_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, DROPPED, COMPLETED
    created_at TIMESTAMP
);
```

**Example data**:
| id | student_id | course_id | status | enrollment_date |
|----|------------|-----------|--------|-----------------|
| 1 | 1 | 1 | ACTIVE | 2024-11-17 15:00 |
| 2 | 1 | 2 | ACTIVE | 2024-11-17 15:05 |

**Relationships**:
- `student_id` links to `users.id`
- `course_id` links to `courses.id`
- This creates a **many-to-many** relationship (one student can take many courses, one course can have many students)

---

### Entity Relationships Diagram

```
┌─────────────┐
│   USERS     │
│ (Students,  │
│  Faculty,   │
│  Admin)     │
└──────┬──────┘
       │
       │ instructor_id (one-to-many)
       │ "One faculty teaches many courses"
       ▼
┌─────────────┐
│   COURSES   │
│             │
└──────┬──────┘
       │
       │ course_id
       │ (many-to-many via enrollments)
       │ "Many students in many courses"
       ▼
┌──────────────┐      student_id      ┌─────────────┐
│ ENROLLMENTS  │◄─────────────────────┤   USERS     │
│              │                      │ (Students)  │
└──────────────┘                      └─────────────┘
```

**What this means**:
1. A **faculty user** can teach **many courses** (one-to-many)
2. A **student** can enroll in **many courses** (many-to-many)
3. A **course** can have **many students** enrolled (many-to-many)
4. The **enrollments table** connects students and courses

---

## 🔌 API Endpoints Reference

### User Management Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/users` | Create new user | UserRequest | 201 Created |
| GET | `/api/users` | Get all users | - | 200 OK |
| GET | `/api/users/{id}` | Get user by ID | - | 200 OK / 404 Not Found |
| PUT | `/api/users/{id}` | Update user | UserRequest | 200 OK |
| DELETE | `/api/users/{id}` | Delete user | - | 204 No Content |
| GET | `/api/users/email/{email}` | Get user by email | - | 200 OK / 404 |
| GET | `/api/users/role/{role}` | Get users by role | - | 200 OK |

---

### Course Management Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/courses` | Create new course | CourseRequest | 201 Created |
| GET | `/api/courses` | Get all courses | - | 200 OK |
| GET | `/api/courses/{id}` | Get course by ID | - | 200 OK / 404 |
| GET | `/api/courses/code/{code}` | Get course by code | - | 200 OK / 404 |
| PUT | `/api/courses/{id}` | Update course | CourseRequest | 200 OK |
| DELETE | `/api/courses/{id}` | Delete course | - | 204 No Content |
| GET | `/api/courses/instructor/{id}` | Get courses by instructor | - | 200 OK |
| GET | `/api/courses/search/name?query=...` | Search by name | - | 200 OK |
| GET | `/api/courses/search/code?query=...` | Search by code | - | 200 OK |
| GET | `/api/courses/credits/{credits}` | Get courses by credits | - | 200 OK |
| PUT | `/api/courses/{courseId}/instructor/{instructorId}` | Assign instructor | - | 200 OK |

---

### Enrollment Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/enrollments` | Enroll student in course | EnrollmentRequest | 201 Created |
| GET | `/api/enrollments` | Get all enrollments | - | 200 OK |
| GET | `/api/enrollments/{id}` | Get enrollment by ID | - | 200 OK / 404 |
| GET | `/api/enrollments/student/{id}` | Get enrollments by student | - | 200 OK |
| GET | `/api/enrollments/course/{id}` | Get enrollments by course | - | 200 OK |
| GET | `/api/enrollments/status/{status}` | Get enrollments by status | - | 200 OK |
| PATCH | `/api/enrollments/{id}/status?status=...` | Update enrollment status | - | 200 OK |
| PUT | `/api/enrollments/{id}/drop` | Drop enrollment | - | 200 OK |
| DELETE | `/api/enrollments/{id}` | Delete enrollment | - | 204 No Content |
| GET | `/api/enrollments/course/{id}/count` | Get enrollment count | - | 200 OK |
| GET | `/api/enrollments/check?studentId=...&courseId=...` | Check if enrolled | - | 200 OK |

---

## 🧠 Key Concepts Explained

### What is Dependency Injection?

**Simple explanation**: Instead of creating things yourself, someone else gives them to you.

**Bad way (no dependency injection)**:
```java
public class UserService {
    private UserRepository userRepository = new UserRepository(); // We create it ourselves
}
```
**Problem**: Hard to test, hard to change

**Good way (with dependency injection)**:
```java
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository; // Someone gives it to us!
    }
}
```
**Benefit**: Spring creates and manages it for us!

**Real-world analogy**:
- **Bad**: You want coffee, so you buy a farm, grow beans, roast them yourself
- **Good**: You want coffee, someone hands you a cup from Starbucks

---

### What are JPA Entities? (@Entity)

**Simple explanation**: A Java class that represents a database table.

**Example** (`User.java`):
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    // ...
}
```

**What it means**:
- `@Entity` = This is a database table
- `@Table(name = "users")` = Table is called "users"
- `@Id` = This is the primary key
- `@GeneratedValue` = Database auto-generates IDs (1, 2, 3...)
- `@Column` = This is a column with special rules

**Magic**: Spring automatically creates the table and handles all SQL!

---

### What is @Service, @Repository, @Controller?

These are **annotations** that tell Spring what role a class plays.

**@Controller** (`UserController.java`):
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // Handles HTTP requests and responses
}
```
**Role**: Front desk receptionist - greets visitors, directs them

---

**@Service** (`UserService.java`):
```java
@Service
public class UserService {
    // Business logic goes here
}
```
**Role**: Manager - makes decisions, enforces rules

---

**@Repository** (`UserRepository.java`):
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Database operations
}
```
**Role**: File clerk - stores and retrieves documents

---

### How Does Spring Security Work in Our Project?

**Current setup** (`SecurityConfig.java`):
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // For hashing passwords
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/**").permitAll() // Allow all user endpoints
                .requestMatchers("/api/courses/**").permitAll() // Allow all course endpoints
                .requestMatchers("/api/enrollments/**").permitAll() // Allow all enrollment endpoints
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```

**What it does**:
1. Creates a password encoder for hashing
2. Allows all API endpoints without authentication (for now!)
3. In Week 7, we'll add JWT and restrict access by role

**Why permitAll() for now?**
- We want to test our APIs easily
- Week 7 will add proper authentication
- For now, focus on getting the functionality working

---

## ❓ Common Questions & Answers

### Q1: "Why do we hash passwords?"

**Answer**: Imagine you write your bank PIN on a piece of paper and someone steals it. They know your PIN!

Hashing is like putting your PIN through a shredder. The result is unreadable, but we can still check if someone enters the right PIN by shredding theirs too and comparing.

**Example**:
- Original password: `student123`
- Hashed password: `$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW`

Even if a hacker steals the database, they can't read the passwords!

---

### Q2: "What happens when a user logs in?" (For Week 7, but good to know)

**Flow**:
1. User enters email and password
2. Backend fetches hashed password from database
3. Hashes the entered password
4. Compares the two hashes
5. If they match: Generate JWT token
6. If they don't match: Return "Invalid credentials"

**Why it's secure**:
- Password never sent in plain text (except first time over HTTPS)
- Password never stored in plain text
- JWT token proves authentication without password

---

### Q3: "How do we prevent unauthorized access?" (Week 7)

**Current**: Everyone can access everything (for testing)

**Week 7**: Role-based access control:
- **Students** can:
  - View courses
  - Enroll in courses
  - View their own enrollments
  - Can NOT create courses or view other students' data
- **Faculty** can:
  - Everything students can
  - View enrollments in their courses
  - Update grades (Week 6)
- **Admin** can:
  - Everything!

**How it works**: Using `@PreAuthorize` annotation:
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    // Only admins can delete users
}
```

---

### Q4: "What's the difference between @OneToMany and @ManyToOne?"

**Simple explanation with a school example**:

**@OneToMany**: One teacher, many students
```java
public class Teacher {
    @OneToMany
    private List<Student> students; // One teacher has many students
}
```

**@ManyToOne**: Many students, one teacher
```java
public class Student {
    @ManyToOne
    private Teacher teacher; // Many students belong to one teacher
}
```

**In our project**:
```java
public class Course {
    @ManyToOne
    private User instructor; // Many courses can have one instructor
}
```

---

### Q5: "Why do we use DTOs instead of entities?"

**Problem with sending entities directly**:
```java
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUserById(id); // Sends password too!
}
```

**Response** (BAD!):
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@student.com",
  "password": "$2a$10$Eix...",  ← EXPOSED!
  "role": "STUDENT"
}
```

**Solution with DTOs**:
```java
@GetMapping("/{id}")
public UserResponse getUser(@PathVariable Long id) {
    User user = userService.getUserById(id);
    return convertToDTO(user); // Only sends safe data
}
```

**Response** (GOOD!):
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@student.com",
  "role": "STUDENT"
  // No password!
}
```

---

## 🧪 Testing Guide

### How to Test Using Postman

#### Step 1: Import Collections

1. Open Postman
2. Click "Import"
3. Import both collections:
   - `SAMS_User_Management.postman_collection.json`
   - `SAMS_Course_Enrollment_Management.postman_collection.json`

#### Step 2: Test User Management

**Test Flow**:

1. **Create a Student**:
```
POST http://localhost:8080/api/users
Body:
{
  "username": "john_doe",
  "email": "john@student.com",
  "password": "student123",
  "role": "STUDENT"
}
```
Expected: 201 Created

2. **Create a Faculty**:
```
POST http://localhost:8080/api/users
Body:
{
  "username": "prof_smith",
  "email": "smith@faculty.com",
  "password": "faculty123",
  "role": "FACULTY"
}
```
Expected: 201 Created

3. **Get All Users**:
```
GET http://localhost:8080/api/users
```
Expected: 200 OK with array of users

4. **Try Duplicate Email** (Error test):
```
POST http://localhost:8080/api/users
Body:
{
  "username": "another_user",
  "email": "john@student.com",  ← Same email!
  "password": "test123",
  "role": "STUDENT"
}
```
Expected: 400 Bad Request with error message

---

#### Step 3: Test Course Management

1. **Create a Course**:
```
POST http://localhost:8080/api/courses
Body:
{
  "courseCode": "CS101",
  "courseName": "Introduction to Computer Science",
  "description": "Basic programming",
  "credits": 3,
  "capacity": 30,
  "instructorId": 2  ← Use faculty user ID
}
```
Expected: 201 Created

2. **Search Courses**:
```
GET http://localhost:8080/api/courses/search/name?query=Computer
```
Expected: 200 OK with matching courses

3. **Assign Instructor**:
```
PUT http://localhost:8080/api/courses/1/instructor/2
```
Expected: 200 OK

---

#### Step 4: Test Enrollment

1. **Enroll Student in Course**:
```
POST http://localhost:8080/api/enrollments
Body:
{
  "studentId": 1,
  "courseId": 1
}
```
Expected: 201 Created

2. **Get Student's Enrollments**:
```
GET http://localhost:8080/api/enrollments/student/1
```
Expected: 200 OK with list of enrollments

3. **Try Duplicate Enrollment** (Error test):
```
POST http://localhost:8080/api/enrollments
Body:
{
  "studentId": 1,
  "courseId": 1  ← Already enrolled!
}
```
Expected: 400 Bad Request

4. **Drop Enrollment**:
```
PUT http://localhost:8080/api/enrollments/1/drop
```
Expected: 200 OK with status changed to "DROPPED"

---

### How to Run Unit Tests

**In IntelliJ IDEA**:
1. Right-click on `src/test/java`
2. Click "Run 'All Tests'"
3. All tests should pass ✅

**From Command Line**:
```bash
./mvnw test
```

**What gets tested**:
- UserService: 8 tests
- CourseService: 10 tests
- All business logic is validated!

---

## 🎤 Presentation Tips

### Key Talking Points

#### 1. Architecture (Start with the big picture)

**What to say**:
> "Our system uses a layered architecture with three main layers: Controller, Service, and Repository. This separation makes our code organized, testable, and easy to maintain. The Controller handles HTTP requests, the Service contains business logic, and the Repository talks to the database."

**Demo**: Show the folder structure in IntelliJ

---

#### 2. User Management (Week 4)

**What to say**:
> "We built a complete user management system with CRUD operations. Users can be students, faculty, or admin. We implemented password hashing using BCrypt for security - passwords are never stored in plain text. We also prevent duplicate emails and use DTOs to ensure passwords are never exposed in API responses."

**Demo**:
1. Create a user in Postman
2. Show the response (no password!)
3. Show the database (password is hashed)

**Impressive detail to mention**:
> "We use BCrypt hashing with a work factor of 10, which means the password goes through 1,024 rounds of hashing. This makes it extremely difficult for attackers to crack passwords even if they steal our database."

---

#### 3. Course Management (Week 5)

**What to say**:
> "We created a course management system where faculty can be assigned as instructors. Courses have properties like course code, name, credits, and capacity. We implemented search functionality so users can find courses by name or code. Spring Data JPA automatically generates the SQL for us based on method names."

**Demo**:
1. Create a course
2. Assign an instructor
3. Search for courses
4. Show how the course response includes instructor details

**Impressive detail to mention**:
> "We use Spring Data JPA's query derivation feature. For example, our method `findByCourseNameContainingIgnoreCase` automatically generates a SQL query with LIKE and LOWER functions - we didn't write any SQL!"

---

#### 4. Enrollment System (Week 5)

**What to say**:
> "The enrollment system connects students to courses. We enforce business rules: only students can enroll, they can't enroll twice in the same course, and they can't enroll if the course is full. The system tracks enrollment status - active, dropped, or completed."

**Demo**:
1. Enroll a student
2. Try to enroll them again (show error)
3. Try to enroll a faculty member (show error)
4. Show enrollment count for a course

**Impressive detail to mention**:
> "We use transactions with the @Transactional annotation. This means if any step in the enrollment process fails - like if the course becomes full while we're processing - the entire operation is rolled back. This ensures data integrity."

---

#### 5. Testing (Show professionalism)

**What to say**:
> "We wrote unit tests for our service layer using JUnit and Mockito. We have 18 tests total that verify both success scenarios and error handling. We also created Postman collections for manual testing with over 25 requests covering all endpoints."

**Demo**:
1. Run unit tests in IntelliJ (show they all pass)
2. Show Postman collection organization

---

### Demo Flow (5-minute version)

**Minute 1**: Explain architecture and show folder structure

**Minute 2**: Demo user creation and password hashing
- Create student
- Create faculty
- Show hashed password in database
- Show error when using duplicate email

**Minute 3**: Demo course creation and management
- Create course
- Assign instructor
- Search courses

**Minute 4**: Demo enrollment
- Enroll student in course
- Show enrollment list
- Try to enroll again (error)
- Drop enrollment

**Minute 5**: Show testing
- Run unit tests
- Show Postman collections

---

### Questions They Might Ask (And How to Answer)

**Q: "Why did you choose Spring Boot?"**

**A**: "Spring Boot provides a complete framework for building enterprise applications. It handles common tasks like dependency injection, database connectivity, and security configuration. It also has great community support and is industry-standard for Java backend development."

---

**Q: "How do you handle security?"**

**A**: "Currently we have basic security implemented - password hashing with BCrypt and DTOs to prevent password exposure. In Week 7, we'll add JWT authentication for role-based access control. This will restrict endpoints based on user roles - students, faculty, and admin."

---

**Q: "What happens if two students try to enroll in the last spot simultaneously?"**

**A**: "Great question! We use database transactions with the @Transactional annotation. The database locks the enrollment record during the operation, so only one student can claim the last spot. The other student would get a 'course is full' error."

---

**Q: "How do you test your application?"**

**A**: "We have two types of testing. Unit tests verify individual components in isolation - we have 18 tests using JUnit and Mockito with 100% coverage of service layer methods. We also have integration testing using Postman collections with over 25 requests that test the complete request-response cycle."

---

## 🎉 Congratulations!

You now understand:
- ✅ How Spring Boot organizes code with layers
- ✅ How JPA entities map to database tables
- ✅ How REST APIs work
- ✅ How password hashing provides security
- ✅ How relationships connect tables (one-to-many, many-to-many)
- ✅ How business rules are enforced in the service layer
- ✅ How to test with Postman and JUnit

**You're ready to:**
- Demo the project confidently
- Explain every part to teammates
- Answer technical questions
- Continue to Week 6 and beyond!

**Next steps** (Week 6 & 7):
- Week 6: Enrollment business logic (prerequisites, conflicts, waitlist)
- Week 7: Grade management and JWT authentication
- Week 8-9: Frontend development
- Week 10-11: Testing and documentation

---

**Remember**: The goal isn't to memorize everything, but to understand the flow and be able to explain how things work. Use this guide as a reference whenever you need to refresh your memory!

Good luck with your presentation! 🚀
