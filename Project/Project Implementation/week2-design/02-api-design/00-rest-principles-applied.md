# REST Principles Applied to SAMS

## Purpose
This document explains how RESTful architectural principles are applied to the Student Academic Management System (SAMS) API design.

---

## What is REST?

**REST (Representational State Transfer)** is an architectural style for designing networked applications. It relies on stateless, client-server communication using standard HTTP methods.

**Key Benefits:**
- Scalability through stateless interactions
- Simple and intuitive API structure
- Platform-independent communication
- Cacheable responses for performance
- Separation of concerns (client/server)

---

## REST Principles Applied to SAMS

### 1. **Client-Server Separation**

**Principle:** The client (UI) and server (API) are independent and communicate only through the API interface.

**SAMS Implementation:**
- **Client:** Vue.js frontend runs in browser
- **Server:** Spring Boot REST API runs on application server
- **Communication:** HTTP requests/responses with JSON payload
- **Benefit:** Frontend and backend teams can work independently

**Example:**
```
Vue.js App (Port 3000) ──HTTP/JSON──> Spring Boot API (Port 8080) ──JDBC──> PostgreSQL (Port 5432)
```

**SRS Compliance:** NFR-5.4 (Modularity and Maintainability)

---

### 2. **Statelessness**

**Principle:** Each request from client to server must contain all information needed to understand the request. Server does not store session state.

**SAMS Implementation:**
- **Authentication:** JWT tokens sent with every request in `Authorization` header
- **No Server-Side Sessions:** Server doesn't maintain session data
- **Self-Contained Requests:** Each API call includes all necessary data

**Example Request:**
```http
GET /api/v1/students/123/grades
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Benefits:**
- Server can scale horizontally (any server can handle any request)
- No session synchronization needed
- Simplified server architecture

**SRS Compliance:** NFR-1.4 (Scalability), NFR-3.2 (Fault Tolerance)

---

### 3. **Resource-Based URLs**

**Principle:** URLs represent resources (nouns), not actions (verbs). HTTP methods define the action.

**SAMS Implementation:**

**✅ CORRECT (Resource-Based):**
```
GET    /api/v1/courses           - Get all courses
POST   /api/v1/courses           - Create new course
GET    /api/v1/courses/5         - Get course with ID 5
PUT    /api/v1/courses/5         - Update course 5
DELETE /api/v1/courses/5         - Delete course 5
```

**❌ INCORRECT (Action-Based - RPC Style):**
```
GET  /api/v1/getAllCourses
POST /api/v1/createCourse
GET  /api/v1/getCourseById?id=5
POST /api/v1/updateCourse
POST /api/v1/deleteCourse
```

**SAMS Resources Identified:**
- `/users` - User accounts
- `/students` - Student profiles
- `/faculty` - Faculty profiles
- `/courses` - Course offerings
- `/enrollments` - Course enrollments
- `/grades` - Student grades
- `/payments` - Payment transactions
- `/study-groups` - Study groups
- `/notifications` - User notifications

**SRS Compliance:** NFR-5.1 (Code Quality and Best Practices)

---

### 4. **HTTP Methods (Verbs)**

**Principle:** Use standard HTTP methods to perform operations on resources.

**SAMS HTTP Method Usage:**

| HTTP Method | Operation | Idempotent? | Safe? | SAMS Examples |
|-------------|-----------|-------------|-------|---------------|
| **GET** | Retrieve resource(s) | Yes | Yes | Get student profile, List courses |
| **POST** | Create new resource | No | No | Register student, Submit payment |
| **PUT** | Update entire resource | Yes | No | Update course details |
| **PATCH** | Partial update | No | No | Update enrollment status only |
| **DELETE** | Remove resource | Yes | No | Drop course, Remove study group |

**Idempotent:** Multiple identical requests have same effect as single request
**Safe:** Does not modify server state

**Example Operations:**

```http
# Get all active courses (Safe + Idempotent)
GET /api/v1/courses?status=active

# Enroll student in course (Not idempotent - creates new enrollment each time)
POST /api/v1/enrollments
{
  "studentId": 123,
  "courseId": 456
}

# Update student's major (Idempotent - same result if called multiple times)
PUT /api/v1/students/123
{
  "major": "Computer Science",
  "minor": "Mathematics"
}

# Partially update payment status (Not idempotent)
PATCH /api/v1/payments/789
{
  "status": "VALIDATED"
}

# Drop course enrollment (Idempotent)
DELETE /api/v1/enrollments/101
```

**SRS Compliance:** FR-2.2 (Course Registration), FR-2.7 (Payment Processing)

---

### 5. **Representation (JSON Format)**

**Principle:** Resources can have multiple representations. SAMS uses JSON for data exchange.

**SAMS JSON Standards:**

**Request Body (POST /api/v1/courses):**
```json
{
  "courseCode": "CS301",
  "courseName": "Data Structures and Algorithms",
  "description": "Advanced study of data structures including trees, graphs, and hash tables",
  "credits": 3,
  "department": "Computer Science",
  "instructorId": 5,
  "semester": "Fall",
  "academicYear": 2024,
  "scheduleDays": "Mon,Wed,Fri",
  "scheduleTime": "10:00-11:15",
  "roomLocation": "Building A, Room 205",
  "maxCapacity": 35,
  "prerequisites": "CS201,CS202"
}
```

**Response Body (200 OK):**
```json
{
  "success": true,
  "message": "Course created successfully",
  "data": {
    "courseId": 12,
    "courseCode": "CS301",
    "courseName": "Data Structures and Algorithms",
    "description": "Advanced study of data structures including trees, graphs, and hash tables",
    "credits": 3,
    "department": "Computer Science",
    "instructorId": 5,
    "semester": "Fall",
    "academicYear": 2024,
    "scheduleDays": "Mon,Wed,Fri",
    "scheduleTime": "10:00-11:15",
    "roomLocation": "Building A, Room 205",
    "maxCapacity": 35,
    "currentEnrollment": 0,
    "waitlistCapacity": 10,
    "currentWaitlist": 0,
    "prerequisites": "CS201,CS202",
    "isActive": true,
    "registrationOpen": false,
    "createdAt": "2024-09-15T14:30:00Z",
    "updatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Naming Conventions:**
- **camelCase** for JSON field names (matches JavaScript conventions)
- **Consistent field names** across all endpoints
- **ISO 8601 format** for timestamps (`2024-09-15T14:30:00Z`)

**SRS Compliance:** NFR-2.1 (Cross-Platform Compatibility)

---

### 6. **HTTP Status Codes**

**Principle:** Use appropriate HTTP status codes to indicate request outcome.

**SAMS Status Code Usage:**

| Code | Meaning | SAMS Use Case |
|------|---------|---------------|
| **200 OK** | Success (GET, PUT, PATCH) | Successfully retrieved student profile |
| **201 Created** | Resource created (POST) | New enrollment created successfully |
| **204 No Content** | Success, no body (DELETE) | Course dropped successfully |
| **400 Bad Request** | Invalid input | Missing required field in registration |
| **401 Unauthorized** | Authentication failed | Invalid or missing JWT token |
| **403 Forbidden** | Insufficient permissions | Student trying to access faculty endpoint |
| **404 Not Found** | Resource doesn't exist | Course ID 999 not found |
| **409 Conflict** | Business rule violation | Student already enrolled in course |
| **422 Unprocessable Entity** | Validation failed | GPA value outside 0.0-4.0 range |
| **500 Internal Server Error** | Server error | Database connection failure |

**Example Error Response (409 Conflict):**
```json
{
  "success": false,
  "error": {
    "code": "ENROLLMENT_CONFLICT",
    "message": "Student is already enrolled in this course",
    "details": {
      "studentId": 123,
      "courseId": 456,
      "existingEnrollmentId": 789
    },
    "timestamp": "2024-09-15T14:35:22Z"
  }
}
```

**SRS Compliance:** NFR-4.2 (Error Handling and Logging)

---

### 7. **Versioning**

**Principle:** API should support versioning for backward compatibility.

**SAMS Versioning Strategy:**

**URL Path Versioning (Chosen Approach):**
```
https://sams.university.edu/api/v1/courses
https://sams.university.edu/api/v1/students
```

**Rationale:**
- Clear and visible in URL
- Easy to route in Spring Boot
- Simple for frontend developers to understand
- Version applies to entire API

**Future Versions:**
- v1: Initial release (Week 2-11)
- v2: Future enhancements (if breaking changes needed)

**Version Migration Example:**
```
# Current version (v1)
GET /api/v1/grades?studentId=123

# Future version (v2) - different response structure
GET /api/v2/grades?studentId=123
```

**SRS Compliance:** NFR-5.3 (Extensibility)

---

### 8. **HATEOAS (Hypermedia As The Engine Of Application State)**

**Principle:** API responses include links to related resources.

**SAMS Implementation (Partial HATEOAS):**

**Example: GET /api/v1/students/123**
```json
{
  "success": true,
  "data": {
    "studentId": 123,
    "studentNumber": "STU2024001",
    "firstName": "Sarah",
    "lastName": "Johnson",
    "major": "Computer Science",
    "cumulativeGpa": 3.75,
    "_links": {
      "self": {
        "href": "/api/v1/students/123"
      },
      "enrollments": {
        "href": "/api/v1/students/123/enrollments"
      },
      "grades": {
        "href": "/api/v1/students/123/grades"
      },
      "transcript": {
        "href": "/api/v1/students/123/transcript"
      },
      "payments": {
        "href": "/api/v1/students/123/payments"
      }
    }
  }
}
```

**Benefits:**
- API is self-documenting
- Client doesn't need to construct URLs
- Easier API navigation

**Note:** Full HATEOAS implementation is optional for SAMS v1 (simplicity prioritized for initial version).

---

### 9. **Pagination, Filtering, and Sorting**

**Principle:** Large collections should support pagination and filtering.

**SAMS Query Parameter Standards:**

**Pagination:**
```http
GET /api/v1/courses?page=2&size=20
```

**Response with Pagination Metadata:**
```json
{
  "success": true,
  "data": [ /* array of 20 courses */ ],
  "pagination": {
    "currentPage": 2,
    "pageSize": 20,
    "totalPages": 8,
    "totalItems": 152,
    "hasNext": true,
    "hasPrevious": true
  }
}
```

**Filtering:**
```http
GET /api/v1/courses?department=Computer Science&semester=Fall&year=2024
```

**Sorting:**
```http
GET /api/v1/students?sort=cumulativeGpa,desc&sort=lastName,asc
```

**Combined Example:**
```http
GET /api/v1/enrollments?status=ENROLLED&semester=Fall&page=1&size=50&sort=enrollmentDate,desc
```

**SRS Compliance:** NFR-1.1 (Response Time), FR-2.2 (Course Search)

---

### 10. **Caching**

**Principle:** Use HTTP caching headers to improve performance.

**SAMS Caching Strategy:**

**Cache-Control Headers:**
```http
# Static data (rarely changes)
GET /api/v1/courses/123
Cache-Control: max-age=3600, public

# Dynamic data (changes frequently)
GET /api/v1/notifications
Cache-Control: no-cache, must-revalidate

# User-specific data
GET /api/v1/students/123/grades
Cache-Control: private, max-age=600
```

**ETag for Conditional Requests:**
```http
# Initial request
GET /api/v1/students/123
Response:
  ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"

# Subsequent request
GET /api/v1/students/123
If-None-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"
Response: 304 Not Modified (if unchanged)
```

**SRS Compliance:** NFR-1.1 (Performance - 3 second page loads)

---

## RESTful URL Design Patterns in SAMS

### Pattern 1: Collection and Item
```
GET    /api/v1/courses              - Collection (all courses)
GET    /api/v1/courses/5            - Item (specific course)
```

### Pattern 2: Nested Resources
```
GET    /api/v1/students/123/enrollments     - Student's enrollments
GET    /api/v1/courses/5/enrollments        - Course's enrollments
GET    /api/v1/students/123/grades          - Student's all grades
```

### Pattern 3: Action Endpoints (When REST is Insufficient)
```
POST   /api/v1/enrollments/789/drop         - Drop a course (complex business logic)
POST   /api/v1/payments/456/validate        - Admin validates payment
POST   /api/v1/auth/login                   - User login (not a CRUD operation)
POST   /api/v1/students/123/calculate-gpa   - Trigger GPA recalculation
```

**Note:** Action endpoints are acceptable when operation doesn't map cleanly to CRUD.

---

## REST vs. GraphQL vs. SOAP - Why REST for SAMS?

| Feature | REST | GraphQL | SOAP |
|---------|------|---------|------|
| **Complexity** | Simple | Medium | Complex |
| **Overhead** | Low | Medium | High |
| **Tooling** | Excellent | Good | Good |
| **Caching** | Built-in (HTTP) | Manual | Manual |
| **Learning Curve** | Easy | Medium | Steep |
| **Best For** | CRUD operations | Complex queries | Enterprise systems |

**Decision:** REST chosen for SAMS because:
1. Simple CRUD operations dominate the API (SRS functional requirements)
2. Team familiar with REST and Spring Boot
3. HTTP caching improves performance (NFR-1.1)
4. Excellent tooling and documentation available
5. Easier for frontend developers to consume

**SRS Compliance:** NFR-5.1 (Code Quality), NFR-5.4 (Maintainability)

---

## API Security Principles

### 1. **HTTPS Only**
All API requests must use HTTPS in production (HTTP allowed in development only).

### 2. **JWT Authentication**
```http
POST /api/v1/auth/login
{
  "username": "sarah.johnson",
  "password": "SecurePass123"
}

Response:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "user": {
      "userId": 123,
      "username": "sarah.johnson",
      "role": "STUDENT"
    }
  }
}
```

### 3. **Role-Based Access Control (RBAC)**
```
# Student can only access their own data
GET /api/v1/students/123/grades
Authorization: Bearer <student-token>

# Faculty can access any student's data for their courses
GET /api/v1/students/456/grades?courseId=12
Authorization: Bearer <faculty-token>

# Admin can access all data
GET /api/v1/payments?status=PENDING
Authorization: Bearer <admin-token>
```

### 4. **Input Validation**
All inputs validated on server-side (never trust client).

**SRS Compliance:** NFR-4.1 (Security and Data Protection), FR-1.2 (Authorization)

---

## API Documentation Standards

**Each endpoint documented with:**
1. **HTTP Method and URL**
2. **Description**
3. **Authentication Required** (Yes/No)
4. **Authorization Roles** (STUDENT, FACULTY, ADMIN)
5. **Request Parameters** (path, query, body)
6. **Request Example**
7. **Response Example** (success and error)
8. **Status Codes**
9. **Business Rules**
10. **SRS Requirement Traceability**

**Documentation Format:** Markdown files in `02-api-design/` folder

---

## Benefits of RESTful Design for SAMS

### 1. **Simplicity**
- Easy for frontend developers to understand and consume
- Standard HTTP methods reduce learning curve
- Clear resource-based URL structure

### 2. **Scalability**
- Stateless design allows horizontal scaling
- Each request independent (load balancer friendly)
- Caching improves performance

### 3. **Flexibility**
- Client and server evolve independently
- Multiple clients (web, mobile app) use same API
- Easy to test with tools like Postman

### 4. **Maintainability**
- Consistent patterns across all endpoints
- Self-documenting URLs
- Standard error handling

### 5. **Performance**
- HTTP caching reduces server load
- JSON is lightweight compared to XML (SOAP)
- Efficient data transfer

---

## REST Best Practices Applied to SAMS

1. **Use nouns for resources, verbs for actions** ✅
2. **Consistent naming conventions (camelCase)** ✅
3. **Proper HTTP status codes** ✅
4. **Stateless authentication (JWT)** ✅
5. **Versioning for API evolution** ✅
6. **Pagination for large collections** ✅
7. **Meaningful error messages** ✅
8. **Input validation and sanitization** ✅
9. **HTTPS for security** ✅
10. **Comprehensive documentation** ✅

---

## Compliance with SRS Requirements

| REST Principle | SRS Requirement Satisfied |
|----------------|---------------------------|
| Statelessness | NFR-1.4 (Scalability), NFR-3.2 (Fault Tolerance) |
| HTTP Caching | NFR-1.1 (Performance - 3s page loads) |
| Standard Methods | NFR-5.1 (Code Quality and Best Practices) |
| JSON Format | NFR-2.1 (Cross-Platform Compatibility) |
| Versioning | NFR-5.3 (Extensibility) |
| HTTPS + JWT | NFR-4.1 (Security and Data Protection) |
| Error Handling | NFR-4.2 (Error Handling and Logging) |
| Resource-Based URLs | NFR-5.4 (Modularity and Maintainability) |

---

**Document Status:** Complete
**RESTful Maturity Level:** Level 2 (HTTP Verbs + Status Codes) with partial Level 3 (HATEOAS)
**Next Step:** Complete endpoint documentation (01-api-endpoints-complete.md)
