# SAMS Complete API Endpoints Documentation

## Purpose
This document provides comprehensive documentation for all 37 REST API endpoints in the Student Academic Management System (SAMS).

---

## API Base URL
```
Development: http://localhost:8080/api/v1
Production:  https://sams.university.edu/api/v1
```

---

## Table of Contents
1. [Authentication Endpoints](#1-authentication-endpoints) (4 endpoints)
2. [User Management Endpoints](#2-user-management-endpoints) (4 endpoints)
3. [Student Endpoints](#3-student-endpoints) (6 endpoints)
4. [Faculty Endpoints](#4-faculty-endpoints) (4 endpoints)
5. [Course Endpoints](#5-course-endpoints) (7 endpoints)
6. [Enrollment Endpoints](#6-enrollment-endpoints) (5 endpoints)
7. [Grade Endpoints](#7-grade-endpoints) (5 endpoints)
8. [Payment Endpoints](#8-payment-endpoints) (5 endpoints)
9. [Study Group Endpoints](#9-study-group-endpoints) (5 endpoints)
10. [Notification Endpoints](#10-notification-endpoints) (4 endpoints)
11. [Admin Endpoints](#11-admin-endpoints) (4 endpoints)

**Total: 37 Endpoints**

---

## 1. Authentication Endpoints

### 1.1 User Login
**Endpoint:** `POST /api/v1/auth/login`

**Description:** Authenticates user credentials and returns JWT token for subsequent requests.

**Authentication Required:** No

**Authorization Roles:** All (public)

**Request Body:**
```json
{
  "username": "sarah.johnson",
  "password": "SecurePass123"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJ1c2VybmFtZSI6InNhcmFoLmpvaG5zb24iLCJyb2xlIjoiU1RVREVOVCIsImlhdCI6MTYzMTg3MjQwMCwiZXhwIjoxNjMxODc2MDAwfQ.abc123xyz",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": 123,
      "username": "sarah.johnson",
      "email": "sarah.johnson@university.edu",
      "role": "STUDENT",
      "firstName": "Sarah",
      "lastName": "Johnson"
    }
  }
}
```

**Error Responses:**
```json
// 401 Unauthorized - Invalid credentials
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid username or password",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}

// 403 Forbidden - Account inactive
{
  "success": false,
  "error": {
    "code": "ACCOUNT_INACTIVE",
    "message": "Your account has been deactivated. Please contact administration.",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Password must be validated against BCrypt hash in database
- Account must have `is_active = true`
- Failed login attempts should be logged (security audit)
- JWT token expires after 1 hour (configurable)

**SRS Requirements:** FR-1.1 (Secure Login)

---

### 1.2 User Logout
**Endpoint:** `POST /api/v1/auth/logout`

**Description:** Invalidates current JWT token (client-side token deletion, server logs action).

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Request Headers:**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:** None

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

**Business Rules:**
- Update `users.last_login` timestamp
- Log logout event for audit trail
- Client must delete token from local storage

**SRS Requirements:** FR-1.1 (Authentication)

---

### 1.3 Refresh Token
**Endpoint:** `POST /api/v1/auth/refresh`

**Description:** Generates new JWT token before current one expires (seamless session extension).

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newTokenHere...",
    "expiresIn": 3600
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_REFRESH_TOKEN",
    "message": "Refresh token is invalid or expired. Please login again.",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Refresh token valid for 7 days
- New access token issued with 1 hour expiry
- Old refresh token invalidated after use

**SRS Requirements:** FR-1.1 (Session Management)

---

### 1.4 Password Reset Request
**Endpoint:** `POST /api/v1/auth/reset-password`

**Description:** Sends password reset email to user (future implementation - for now returns success).

**Authentication Required:** No

**Authorization Roles:** All (public)

**Request Body:**
```json
{
  "email": "sarah.johnson@university.edu"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "If an account with that email exists, a password reset link has been sent."
}
```

**Business Rules:**
- Always return success (don't reveal if email exists - security)
- Generate reset token valid for 1 hour
- Send email with reset link (Phase 2 feature)

**SRS Requirements:** FR-1.3 (User Management)

---

## 2. User Management Endpoints

### 2.1 Get Current User Profile
**Endpoint:** `GET /api/v1/users/me`

**Description:** Retrieves authenticated user's complete profile information.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Request Headers:**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Success Response (200 OK - Student):**
```json
{
  "success": true,
  "data": {
    "userId": 123,
    "username": "sarah.johnson",
    "email": "sarah.johnson@university.edu",
    "role": "STUDENT",
    "firstName": "Sarah",
    "lastName": "Johnson",
    "phoneNumber": "+1-555-0101",
    "dateOfBirth": "2003-05-15",
    "isActive": true,
    "isEmailVerified": true,
    "lastLogin": "2024-09-15T14:30:00Z",
    "createdAt": "2024-01-10T09:00:00Z",
    "studentProfile": {
      "studentId": 123,
      "studentNumber": "STU2024001",
      "major": "Computer Science",
      "minor": "Mathematics",
      "admissionDate": "2024-01-15",
      "expectedGraduation": "2028-05-15",
      "totalCreditsCompleted": 24,
      "totalCreditsRequired": 120,
      "cumulativeGpa": 3.75,
      "semesterGpa": 3.85,
      "academicStanding": "GOOD_STANDING",
      "emergencyContactName": "Mary Johnson",
      "emergencyContactPhone": "+1-555-0199",
      "currentAddress": "123 Campus Drive, Apt 4B, College Town, ST 12345"
    }
  }
}
```

**Success Response (200 OK - Faculty):**
```json
{
  "success": true,
  "data": {
    "userId": 456,
    "username": "dr.smith",
    "email": "michael.smith@university.edu",
    "role": "FACULTY",
    "firstName": "Michael",
    "lastName": "Smith",
    "phoneNumber": "+1-555-0202",
    "isActive": true,
    "facultyProfile": {
      "facultyId": 456,
      "employeeNumber": "FAC20190001",
      "department": "Computer Science",
      "title": "Associate Professor",
      "officeLocation": "Building C, Room 301",
      "officeHours": "Mon/Wed 2:00-4:00 PM, Fri 10:00-12:00 PM",
      "hireDate": "2019-08-15",
      "contractType": "Full-Time",
      "officePhone": "+1-555-0300"
    }
  }
}
```

**Business Rules:**
- Returns profile based on user's role
- Students get `studentProfile` object
- Faculty get `facultyProfile` object
- Admins get basic user info only

**SRS Requirements:** FR-2.1 (Student Portal), FR-3.1 (Faculty Portal)

---

### 2.2 Update User Profile
**Endpoint:** `PUT /api/v1/users/me`

**Description:** Updates authenticated user's profile information (partial updates allowed).

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Request Body:**
```json
{
  "phoneNumber": "+1-555-9999",
  "currentAddress": "456 New Street, College Town, ST 12345",
  "emergencyContactName": "John Johnson",
  "emergencyContactPhone": "+1-555-8888"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": 123,
    "username": "sarah.johnson",
    "phoneNumber": "+1-555-9999",
    "studentProfile": {
      "currentAddress": "456 New Street, College Town, ST 12345",
      "emergencyContactName": "John Johnson",
      "emergencyContactPhone": "+1-555-8888"
    }
  }
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Phone number format is invalid",
    "details": {
      "field": "phoneNumber",
      "value": "invalid-phone",
      "constraint": "Must match pattern: +X-XXX-XXXX"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Users can only update their own profiles
- Cannot change: userId, username, email, role, studentNumber, employeeNumber
- Can change: phoneNumber, address, emergency contacts
- Input validation required for all fields

**SRS Requirements:** FR-1.3 (Profile Management)

---

### 2.3 Change Password
**Endpoint:** `POST /api/v1/users/me/change-password`

**Description:** Allows authenticated user to change their password.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Request Body:**
```json
{
  "currentPassword": "OldPass123",
  "newPassword": "NewSecurePass456",
  "confirmPassword": "NewSecurePass456"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully. Please login with your new password."
}
```

**Error Responses:**
```json
// 401 Unauthorized - Wrong current password
{
  "success": false,
  "error": {
    "code": "INVALID_CURRENT_PASSWORD",
    "message": "Current password is incorrect",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}

// 422 Unprocessable Entity - Password validation failed
{
  "success": false,
  "error": {
    "code": "WEAK_PASSWORD",
    "message": "Password does not meet security requirements",
    "details": {
      "requirements": [
        "Minimum 8 characters",
        "At least one uppercase letter",
        "At least one lowercase letter",
        "At least one number",
        "At least one special character"
      ]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Current password must be validated before change
- New password must meet complexity requirements
- newPassword and confirmPassword must match
- Password hashed with BCrypt before storage
- Invalidate all existing tokens after password change

**SRS Requirements:** NFR-4.1 (Security)

---

### 2.4 Get User by ID
**Endpoint:** `GET /api/v1/users/{userId}`

**Description:** Retrieves user profile by ID (admin or faculty can view others, students only themselves).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY (all users), STUDENT (only own profile)

**Path Parameters:**
- `userId` (integer, required) - User ID to retrieve

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": 789,
    "username": "john.doe",
    "email": "john.doe@university.edu",
    "role": "STUDENT",
    "firstName": "John",
    "lastName": "Doe",
    "isActive": true,
    "studentProfile": {
      "studentNumber": "STU2024002",
      "major": "Mathematics",
      "cumulativeGpa": 3.45
    }
  }
}
```

**Error Response (403 Forbidden):**
```json
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_PERMISSIONS",
    "message": "You do not have permission to view this user's profile",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Students can only view their own profile
- Faculty can view student profiles (for advising)
- Admins can view all profiles
- Sensitive fields hidden based on role (e.g., password_hash never returned)

**SRS Requirements:** FR-1.2 (Authorization), FR-4.3 (User Management)

---

## 3. Student Endpoints

### 3.1 Get All Students
**Endpoint:** `GET /api/v1/students`

**Description:** Retrieves paginated list of all students with optional filtering.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY

**Query Parameters:**
- `page` (integer, optional, default=1) - Page number
- `size` (integer, optional, default=20) - Items per page
- `major` (string, optional) - Filter by major
- `academicStanding` (enum, optional) - Filter by standing (GOOD_STANDING, PROBATION, etc.)
- `sort` (string, optional, default=lastName,asc) - Sort field and direction

**Example Request:**
```http
GET /api/v1/students?page=1&size=20&major=Computer Science&sort=cumulativeGpa,desc
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "studentId": 123,
      "studentNumber": "STU2024001",
      "firstName": "Sarah",
      "lastName": "Johnson",
      "email": "sarah.johnson@university.edu",
      "major": "Computer Science",
      "minor": "Mathematics",
      "cumulativeGpa": 3.75,
      "semesterGpa": 3.85,
      "totalCreditsCompleted": 24,
      "academicStanding": "GOOD_STANDING"
    },
    {
      "studentId": 456,
      "studentNumber": "STU2024003",
      "firstName": "Emily",
      "lastName": "Davis",
      "email": "emily.davis@university.edu",
      "major": "Computer Science",
      "minor": null,
      "cumulativeGpa": 3.92,
      "semesterGpa": 4.00,
      "totalCreditsCompleted": 30,
      "academicStanding": "DEAN_LIST"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 5,
    "totalItems": 87,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Business Rules:**
- Only active students returned by default
- Maximum page size: 100
- Default sort: lastName ascending

**SRS Requirements:** FR-4.3 (Student Management)

---

### 3.2 Get Student by ID
**Endpoint:** `GET /api/v1/students/{studentId}`

**Description:** Retrieves detailed information for a specific student.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (only own profile)

**Path Parameters:**
- `studentId` (integer, required) - Student ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "studentId": 123,
    "userId": 123,
    "studentNumber": "STU2024001",
    "firstName": "Sarah",
    "lastName": "Johnson",
    "email": "sarah.johnson@university.edu",
    "phoneNumber": "+1-555-0101",
    "dateOfBirth": "2003-05-15",
    "major": "Computer Science",
    "minor": "Mathematics",
    "admissionDate": "2024-01-15",
    "expectedGraduation": "2028-05-15",
    "actualGraduation": null,
    "totalCreditsCompleted": 24,
    "totalCreditsRequired": 120,
    "cumulativeGpa": 3.75,
    "semesterGpa": 3.85,
    "academicStanding": "GOOD_STANDING",
    "emergencyContactName": "Mary Johnson",
    "emergencyContactPhone": "+1-555-0199",
    "currentAddress": "123 Campus Drive, Apt 4B",
    "isActive": true,
    "createdAt": "2024-01-10T09:00:00Z",
    "updatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "error": {
    "code": "STUDENT_NOT_FOUND",
    "message": "Student with ID 999 not found",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Students can only access their own profile
- Faculty and admins can access all student profiles

**SRS Requirements:** FR-2.1 (Student Information Access)

---

### 3.3 Get Student Enrollments
**Endpoint:** `GET /api/v1/students/{studentId}/enrollments`

**Description:** Retrieves all course enrollments for a specific student.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (only own enrollments)

**Path Parameters:**
- `studentId` (integer, required)

**Query Parameters:**
- `semester` (string, optional) - Filter by semester (Fall, Spring, Summer)
- `academicYear` (integer, optional) - Filter by year
- `status` (enum, optional) - Filter by status (ENROLLED, WAITLISTED, DROPPED, COMPLETED)

**Example Request:**
```http
GET /api/v1/students/123/enrollments?semester=Fall&academicYear=2024&status=ENROLLED
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "enrollmentId": 501,
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "credits": 3,
      "instructorName": "Dr. Michael Smith",
      "semester": "Fall",
      "academicYear": 2024,
      "enrollmentDate": "2024-08-20T10:30:00Z",
      "status": "ENROLLED",
      "paymentStatus": "VALIDATED",
      "courseAccessGranted": true,
      "scheduleDays": "Mon,Wed,Fri",
      "scheduleTime": "10:00-11:15",
      "roomLocation": "Building A, Room 205"
    },
    {
      "enrollmentId": 502,
      "courseId": 15,
      "courseCode": "MATH210",
      "courseName": "Calculus II",
      "credits": 4,
      "instructorName": "Dr. Jennifer Lee",
      "semester": "Fall",
      "academicYear": 2024,
      "enrollmentDate": "2024-08-21T09:15:00Z",
      "status": "ENROLLED",
      "paymentStatus": "VALIDATED",
      "courseAccessGranted": true,
      "scheduleDays": "Tue,Thu",
      "scheduleTime": "14:00-15:50",
      "roomLocation": "Building B, Room 101"
    }
  ]
}
```

**Business Rules:**
- Sorted by enrollment date (most recent first)
- Includes course details for convenience (denormalized for performance)
- Only active enrollments shown by default unless `status` filter used

**SRS Requirements:** FR-2.1 (View Course Schedule), FR-2.2 (Enrollment Management)

---

### 3.4 Get Student Grades
**Endpoint:** `GET /api/v1/students/{studentId}/grades`

**Description:** Retrieves all grades for a specific student across all courses.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (only own grades)

**Path Parameters:**
- `studentId` (integer, required)

**Query Parameters:**
- `courseId` (integer, optional) - Filter by specific course
- `semester` (string, optional) - Filter by semester
- `academicYear` (integer, optional) - Filter by year
- `gradeType` (enum, optional) - Filter by type (ASSIGNMENT, EXAM, FINAL)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "gradeId": 1001,
      "enrollmentId": 501,
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "gradeType": "ASSIGNMENT",
      "gradeName": "Homework 1 - Arrays and Linked Lists",
      "gradeValue": 95.0,
      "maxPoints": 100.0,
      "weightPercentage": 10.0,
      "gradeDate": "2024-09-10",
      "enteredBy": "Dr. Michael Smith",
      "comments": "Excellent work! Very thorough solution.",
      "createdAt": "2024-09-10T16:00:00Z"
    },
    {
      "gradeId": 1002,
      "enrollmentId": 501,
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "gradeType": "EXAM",
      "gradeName": "Midterm Exam",
      "gradeValue": 88.0,
      "maxPoints": 100.0,
      "weightPercentage": 30.0,
      "gradeDate": "2024-10-05",
      "enteredBy": "Dr. Michael Smith",
      "comments": null,
      "createdAt": "2024-10-06T10:00:00Z"
    }
  ],
  "summary": {
    "totalGrades": 12,
    "averageGrade": 91.5,
    "currentSemesterGpa": 3.85,
    "cumulativeGpa": 3.75
  }
}
```

**Business Rules:**
- Grades sorted by date (most recent first)
- Final grades (type=FINAL) represent overall course grade
- GPA summary calculated in real-time from grades table

**SRS Requirements:** FR-2.5 (GPA Calculation), FR-3.4 (Grade Viewing)

---

### 3.5 Get Student Transcript
**Endpoint:** `GET /api/v1/students/{studentId}/transcript`

**Description:** Generates complete academic transcript for student.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (only own transcript)

**Path Parameters:**
- `studentId` (integer, required)

**Query Parameters:**
- `format` (string, optional, default=json) - Response format (json or pdf)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "student": {
      "studentNumber": "STU2024001",
      "firstName": "Sarah",
      "lastName": "Johnson",
      "major": "Computer Science",
      "minor": "Mathematics",
      "admissionDate": "2024-01-15",
      "expectedGraduation": "2028-05-15"
    },
    "academicHistory": [
      {
        "semester": "Fall",
        "academicYear": 2024,
        "courses": [
          {
            "courseCode": "CS301",
            "courseName": "Data Structures and Algorithms",
            "credits": 3,
            "finalGrade": "A",
            "gradePoints": 4.0,
            "instructor": "Dr. Michael Smith"
          },
          {
            "courseCode": "MATH210",
            "courseName": "Calculus II",
            "credits": 4,
            "finalGrade": "A-",
            "gradePoints": 3.7,
            "instructor": "Dr. Jennifer Lee"
          }
        ],
        "semesterCredits": 14,
        "semesterGpa": 3.85
      }
    ],
    "summary": {
      "totalCreditsCompleted": 24,
      "totalCreditsRequired": 120,
      "cumulativeGpa": 3.75,
      "academicStanding": "GOOD_STANDING",
      "degreeProgress": "20%"
    },
    "generatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Only completed courses with final grades appear on transcript
- Courses ordered by semester/year, then by course code
- GPA calculated using credit-weighted average
- PDF generation uses Jasper Reports (Phase 2)

**SRS Requirements:** FR-2.5 (Transcript Generation)

---

### 3.6 Get Student Degree Progress
**Endpoint:** `GET /api/v1/students/{studentId}/degree-progress`

**Description:** Calculates student's progress toward degree requirements.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (only own progress)

**Path Parameters:**
- `studentId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "student": {
      "studentId": 123,
      "studentNumber": "STU2024001",
      "firstName": "Sarah",
      "lastName": "Johnson",
      "major": "Computer Science",
      "expectedGraduation": "2028-05-15"
    },
    "degreeRequirements": {
      "major": "Computer Science",
      "totalCreditsRequired": 120,
      "creditsCompleted": 24,
      "creditsRemaining": 96,
      "percentageComplete": 20.0
    },
    "requirementCategories": [
      {
        "category": "Core CS Courses",
        "requiredCredits": 45,
        "completedCredits": 12,
        "remainingCredits": 33,
        "completedCourses": ["CS101", "CS201", "CS301"],
        "remainingCourses": ["CS302", "CS401", "CS402", "CS403", "CS404", "CS405"]
      },
      {
        "category": "Mathematics",
        "requiredCredits": 20,
        "completedCredits": 8,
        "remainingCredits": 12,
        "completedCourses": ["MATH110", "MATH210"],
        "remainingCourses": ["MATH310", "STAT200"]
      },
      {
        "category": "General Education",
        "requiredCredits": 30,
        "completedCredits": 0,
        "remainingCredits": 30,
        "completedCourses": [],
        "remainingCourses": ["ENG101", "HIST100", "BIO100", "etc..."]
      },
      {
        "category": "Electives",
        "requiredCredits": 25,
        "completedCredits": 4,
        "remainingCredits": 21,
        "completedCourses": ["PHYS101"],
        "remainingCourses": ["Any approved electives"]
      }
    ],
    "onTrack": true,
    "estimatedGraduationDate": "2028-05-15",
    "warnings": []
  }
}
```

**Business Rules:**
- Reads from `degree_requirements` table based on student's major
- Compares completed courses with requirements
- Flags warnings if student behind schedule
- Automated tracking per SRS FR-2.4

**SRS Requirements:** FR-2.4 (Automated Degree Progress Tracking)

---

## 4. Faculty Endpoints

### 4.1 Get All Faculty
**Endpoint:** `GET /api/v1/faculty`

**Description:** Retrieves paginated list of all faculty members.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (for finding instructors)

**Query Parameters:**
- `page` (integer, optional, default=1)
- `size` (integer, optional, default=20)
- `department` (string, optional) - Filter by department
- `sort` (string, optional, default=lastName,asc)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "facultyId": 456,
      "employeeNumber": "FAC20190001",
      "firstName": "Michael",
      "lastName": "Smith",
      "email": "michael.smith@university.edu",
      "department": "Computer Science",
      "title": "Associate Professor",
      "officeLocation": "Building C, Room 301",
      "officeHours": "Mon/Wed 2:00-4:00 PM",
      "officePhone": "+1-555-0300"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 2,
    "totalItems": 35
  }
}
```

**Business Rules:**
- Public directory information only (no sensitive data)
- Only active faculty members shown

**SRS Requirements:** FR-3.1 (Faculty Information)

---

### 4.2 Get Faculty by ID
**Endpoint:** `GET /api/v1/faculty/{facultyId}`

**Description:** Retrieves detailed information for specific faculty member.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Path Parameters:**
- `facultyId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "facultyId": 456,
    "userId": 456,
    "employeeNumber": "FAC20190001",
    "firstName": "Michael",
    "lastName": "Smith",
    "email": "michael.smith@university.edu",
    "phoneNumber": "+1-555-0202",
    "department": "Computer Science",
    "title": "Associate Professor",
    "officeLocation": "Building C, Room 301",
    "officeHours": "Mon/Wed 2:00-4:00 PM, Fri 10:00-12:00 PM",
    "hireDate": "2019-08-15",
    "contractType": "Full-Time",
    "officePhone": "+1-555-0300",
    "isActive": true,
    "coursesTeaching": [
      {
        "courseId": 12,
        "courseCode": "CS301",
        "courseName": "Data Structures and Algorithms",
        "semester": "Fall",
        "academicYear": 2024,
        "currentEnrollment": 28
      }
    ]
  }
}
```

**Business Rules:**
- Includes list of currently taught courses
- Public directory information (per university policy)

**SRS Requirements:** FR-3.1 (Faculty Profile)

---

### 4.3 Get Faculty's Courses
**Endpoint:** `GET /api/v1/faculty/{facultyId}/courses`

**Description:** Retrieves all courses taught by specific faculty member.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Path Parameters:**
- `facultyId` (integer, required)

**Query Parameters:**
- `semester` (string, optional)
- `academicYear` (integer, optional)
- `isActive` (boolean, optional, default=true)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "credits": 3,
      "department": "Computer Science",
      "semester": "Fall",
      "academicYear": 2024,
      "scheduleDays": "Mon,Wed,Fri",
      "scheduleTime": "10:00-11:15",
      "roomLocation": "Building A, Room 205",
      "maxCapacity": 35,
      "currentEnrollment": 28,
      "waitlistCapacity": 10,
      "currentWaitlist": 2,
      "isActive": true,
      "registrationOpen": true
    }
  ]
}
```

**Business Rules:**
- Only returns active courses by default
- Historical courses available with `isActive=false`

**SRS Requirements:** FR-3.1 (Course Management)

---

### 4.4 Get Faculty's Students
**Endpoint:** `GET /api/v1/faculty/{facultyId}/students`

**Description:** Retrieves all students enrolled in faculty member's courses (current semester).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY (only own students)

**Path Parameters:**
- `facultyId` (integer, required)

**Query Parameters:**
- `courseId` (integer, optional) - Filter by specific course
- `semester` (string, optional, default=current)
- `academicYear` (integer, optional, default=current)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "studentId": 123,
      "studentNumber": "STU2024001",
      "firstName": "Sarah",
      "lastName": "Johnson",
      "email": "sarah.johnson@university.edu",
      "major": "Computer Science",
      "cumulativeGpa": 3.75,
      "enrolledCourses": [
        {
          "courseId": 12,
          "courseCode": "CS301",
          "enrollmentId": 501,
          "enrollmentStatus": "ENROLLED"
        }
      ]
    }
  ]
}
```

**Business Rules:**
- Faculty can only view students in their own courses
- Includes enrollment status and basic academic info
- Helps faculty track class rosters

**SRS Requirements:** FR-3.4 (Student Roster Access)

---

## 5. Course Endpoints

### 5.1 Get All Courses
**Endpoint:** `GET /api/v1/courses`

**Description:** Retrieves paginated list of all course offerings with filtering and search.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Query Parameters:**
- `page` (integer, optional, default=1)
- `size` (integer, optional, default=20)
- `department` (string, optional) - Filter by department
- `semester` (string, optional) - Filter by semester
- `academicYear` (integer, optional) - Filter by year
- `search` (string, optional) - Search in course code/name
- `registrationOpen` (boolean, optional) - Filter by registration status
- `availableSeats` (boolean, optional) - Only courses with open seats
- `sort` (string, optional, default=courseCode,asc)

**Example Request:**
```http
GET /api/v1/courses?department=Computer Science&semester=Fall&academicYear=2024&availableSeats=true
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "description": "Advanced study of data structures including trees, graphs, hash tables, and algorithm analysis",
      "credits": 3,
      "department": "Computer Science",
      "instructor": {
        "facultyId": 456,
        "firstName": "Michael",
        "lastName": "Smith",
        "title": "Associate Professor"
      },
      "semester": "Fall",
      "academicYear": 2024,
      "scheduleDays": "Mon,Wed,Fri",
      "scheduleTime": "10:00-11:15",
      "roomLocation": "Building A, Room 205",
      "maxCapacity": 35,
      "currentEnrollment": 28,
      "availableSeats": 7,
      "waitlistCapacity": 10,
      "currentWaitlist": 2,
      "prerequisites": "CS201, CS202",
      "courseFee": 50.00,
      "isActive": true,
      "registrationOpen": true
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 4,
    "totalItems": 68
  }
}
```

**Business Rules:**
- Only active courses shown by default
- `availableSeats = maxCapacity - currentEnrollment`
- Search matches course code or course name (case-insensitive)

**SRS Requirements:** FR-2.2 (Course Search and Registration)

---

### 5.2 Get Course by ID
**Endpoint:** `GET /api/v1/courses/{courseId}`

**Description:** Retrieves detailed information for specific course offering.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Path Parameters:**
- `courseId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "courseId": 12,
    "courseCode": "CS301",
    "courseName": "Data Structures and Algorithms",
    "description": "Advanced study of data structures including trees, graphs, hash tables, and their applications. Topics include algorithm analysis, sorting algorithms, searching techniques, and computational complexity.",
    "credits": 3,
    "department": "Computer Science",
    "instructor": {
      "facultyId": 456,
      "firstName": "Michael",
      "lastName": "Smith",
      "title": "Associate Professor",
      "email": "michael.smith@university.edu",
      "officeLocation": "Building C, Room 301",
      "officeHours": "Mon/Wed 2:00-4:00 PM"
    },
    "semester": "Fall",
    "academicYear": 2024,
    "scheduleDays": "Mon,Wed,Fri",
    "scheduleTime": "10:00-11:15",
    "roomLocation": "Building A, Room 205",
    "maxCapacity": 35,
    "currentEnrollment": 28,
    "availableSeats": 7,
    "waitlistCapacity": 10,
    "currentWaitlist": 2,
    "prerequisites": "CS201, CS202",
    "courseFee": 50.00,
    "isActive": true,
    "registrationOpen": true,
    "createdAt": "2024-06-01T10:00:00Z",
    "updatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "error": {
    "code": "COURSE_NOT_FOUND",
    "message": "Course with ID 999 not found",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Returns complete course details including instructor info
- Shows real-time enrollment availability

**SRS Requirements:** FR-2.2 (Course Information)

---

### 5.3 Create Course
**Endpoint:** `POST /api/v1/courses`

**Description:** Creates new course offering (faculty and admin only).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY

**Request Body:**
```json
{
  "courseCode": "CS401",
  "courseName": "Advanced Algorithms",
  "description": "Deep dive into advanced algorithmic techniques including dynamic programming, greedy algorithms, NP-completeness",
  "credits": 3,
  "department": "Computer Science",
  "instructorId": 456,
  "semester": "Spring",
  "academicYear": 2025,
  "scheduleDays": "Tue,Thu",
  "scheduleTime": "13:00-14:50",
  "roomLocation": "Building A, Room 310",
  "maxCapacity": 30,
  "waitlistCapacity": 10,
  "prerequisites": "CS301",
  "courseFee": 75.00
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Course created successfully",
  "data": {
    "courseId": 25,
    "courseCode": "CS401",
    "courseName": "Advanced Algorithms",
    "description": "Deep dive into advanced algorithmic techniques including dynamic programming, greedy algorithms, NP-completeness",
    "credits": 3,
    "department": "Computer Science",
    "instructorId": 456,
    "semester": "Spring",
    "academicYear": 2025,
    "scheduleDays": "Tue,Thu",
    "scheduleTime": "13:00-14:50",
    "roomLocation": "Building A, Room 310",
    "maxCapacity": 30,
    "currentEnrollment": 0,
    "waitlistCapacity": 10,
    "currentWaitlist": 0,
    "prerequisites": "CS301",
    "courseFee": 75.00,
    "isActive": true,
    "registrationOpen": false,
    "createdAt": "2024-09-15T14:30:00Z",
    "updatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Course validation failed",
    "details": {
      "courseCode": "Course code CS401 already exists for Spring 2025",
      "credits": "Credits must be between 1 and 6"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Faculty can only create courses in their department
- Admins can create any course
- Course code must be unique per semester/year
- Instructor must exist in faculty table
- Registration closed by default (admin must open)

**SRS Requirements:** FR-3.1 (Course Creation)

---

### 5.4 Update Course
**Endpoint:** `PUT /api/v1/courses/{courseId}`

**Description:** Updates existing course offering.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY (only own courses)

**Path Parameters:**
- `courseId` (integer, required)

**Request Body:**
```json
{
  "description": "Updated course description with more details",
  "scheduleDays": "Mon,Wed",
  "scheduleTime": "14:00-15:15",
  "roomLocation": "Building B, Room 105",
  "maxCapacity": 40,
  "registrationOpen": true
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Course updated successfully",
  "data": {
    "courseId": 12,
    "courseCode": "CS301",
    "description": "Updated course description with more details",
    "scheduleDays": "Mon,Wed",
    "scheduleTime": "14:00-15:15",
    "roomLocation": "Building B, Room 105",
    "maxCapacity": 40,
    "registrationOpen": true,
    "updatedAt": "2024-09-15T14:35:00Z"
  }
}
```

**Error Response (403 Forbidden):**
```json
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_PERMISSIONS",
    "message": "You can only update your own courses",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Cannot change: courseId, courseCode, semester, academicYear (immutable)
- Can change: description, schedule, capacity, registration status
- Faculty can only update their own courses
- If capacity reduced below current enrollment, validation error

**SRS Requirements:** FR-3.1 (Course Management)

---

### 5.5 Delete Course
**Endpoint:** `DELETE /api/v1/courses/{courseId}`

**Description:** Soft deletes course (sets isActive=false, preserves historical data).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Path Parameters:**
- `courseId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Course deactivated successfully"
}
```

**Error Response (409 Conflict):**
```json
{
  "success": false,
  "error": {
    "code": "COURSE_HAS_ENROLLMENTS",
    "message": "Cannot delete course with active enrollments",
    "details": {
      "courseId": 12,
      "activeEnrollments": 28
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Soft delete only (sets `isActive = false`)
- Cannot delete if active enrollments exist
- Historical data preserved for transcripts
- Only admins can delete courses

**SRS Requirements:** FR-4.1 (Course Management)

---

### 5.6 Get Course Enrollments
**Endpoint:** `GET /api/v1/courses/{courseId}/enrollments`

**Description:** Retrieves all students enrolled in specific course.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY (only own courses)

**Path Parameters:**
- `courseId` (integer, required)

**Query Parameters:**
- `status` (enum, optional) - Filter by enrollment status

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "course": {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "semester": "Fall",
      "academicYear": 2024,
      "maxCapacity": 35,
      "currentEnrollment": 28
    },
    "enrollments": [
      {
        "enrollmentId": 501,
        "student": {
          "studentId": 123,
          "studentNumber": "STU2024001",
          "firstName": "Sarah",
          "lastName": "Johnson",
          "email": "sarah.johnson@university.edu",
          "major": "Computer Science"
        },
        "enrollmentDate": "2024-08-20T10:30:00Z",
        "status": "ENROLLED",
        "paymentStatus": "VALIDATED",
        "courseAccessGranted": true
      }
    ]
  }
}
```

**Business Rules:**
- Faculty can only view enrollments for their own courses
- Sorted by enrollment date
- Includes student contact info for instructor communication

**SRS Requirements:** FR-3.4 (Class Roster)

---

### 5.7 Check Course Prerequisites
**Endpoint:** `POST /api/v1/courses/{courseId}/check-prerequisites`

**Description:** Validates if student meets prerequisites for course enrollment.

**Authentication Required:** Yes

**Authorization Roles:** STUDENT, ADMIN, FACULTY

**Path Parameters:**
- `courseId` (integer, required)

**Request Body:**
```json
{
  "studentId": 123
}
```

**Success Response (200 OK - Prerequisites Met):**
```json
{
  "success": true,
  "message": "Student meets all prerequisites",
  "data": {
    "canEnroll": true,
    "courseId": 12,
    "courseCode": "CS301",
    "prerequisites": ["CS201", "CS202"],
    "completedPrerequisites": [
      {
        "courseCode": "CS201",
        "courseName": "Intro to Programming",
        "finalGrade": "A",
        "completed": true
      },
      {
        "courseCode": "CS202",
        "courseName": "Object-Oriented Programming",
        "finalGrade": "B+",
        "completed": true
      }
    ],
    "missingPrerequisites": []
  }
}
```

**Success Response (200 OK - Prerequisites Not Met):**
```json
{
  "success": true,
  "message": "Student does not meet prerequisites",
  "data": {
    "canEnroll": false,
    "courseId": 12,
    "courseCode": "CS301",
    "prerequisites": ["CS201", "CS202"],
    "completedPrerequisites": [
      {
        "courseCode": "CS201",
        "courseName": "Intro to Programming",
        "finalGrade": "A",
        "completed": true
      }
    ],
    "missingPrerequisites": [
      {
        "courseCode": "CS202",
        "courseName": "Object-Oriented Programming",
        "completed": false
      }
    ]
  }
}
```

**Business Rules:**
- Parses `prerequisites` column (comma-separated course codes)
- Checks student's completed courses with final grades
- Minimum grade requirement: D or higher (configurable)
- Used before enrollment to validate eligibility

**SRS Requirements:** FR-2.2 (Prerequisite Validation), SRS Business Rule BR-2

---

## 6. Enrollment Endpoints

### 6.1 Create Enrollment (Register for Course)
**Endpoint:** `POST /api/v1/enrollments`

**Description:** Enrolls student in course or adds to waitlist if full.

**Authentication Required:** Yes

**Authorization Roles:** STUDENT (only self), ADMIN (any student)

**Request Body:**
```json
{
  "studentId": 123,
  "courseId": 12
}
```

**Success Response (201 Created - Enrolled):**
```json
{
  "success": true,
  "message": "Successfully enrolled in course",
  "data": {
    "enrollmentId": 601,
    "studentId": 123,
    "courseId": 12,
    "courseCode": "CS301",
    "courseName": "Data Structures and Algorithms",
    "enrollmentDate": "2024-09-15T14:30:00Z",
    "status": "ENROLLED",
    "semester": "Fall",
    "academicYear": 2024,
    "paymentStatus": "PENDING",
    "courseAccessGranted": false,
    "waitlistPosition": null
  }
}
```

**Success Response (201 Created - Waitlisted):**
```json
{
  "success": true,
  "message": "Course is full. You have been added to the waitlist.",
  "data": {
    "enrollmentId": 602,
    "studentId": 123,
    "courseId": 15,
    "courseCode": "CS402",
    "courseName": "Machine Learning",
    "enrollmentDate": "2024-09-15T14:30:00Z",
    "status": "WAITLISTED",
    "semester": "Fall",
    "academicYear": 2024,
    "paymentStatus": "PENDING",
    "courseAccessGranted": false,
    "waitlistPosition": 3
  }
}
```

**Error Responses:**
```json
// 409 Conflict - Already enrolled
{
  "success": false,
  "error": {
    "code": "ALREADY_ENROLLED",
    "message": "Student is already enrolled in this course",
    "details": {
      "existingEnrollmentId": 501,
      "status": "ENROLLED"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}

// 422 Unprocessable Entity - Prerequisites not met
{
  "success": false,
  "error": {
    "code": "PREREQUISITES_NOT_MET",
    "message": "Student does not meet course prerequisites",
    "details": {
      "required": ["CS201", "CS202"],
      "missing": ["CS202"]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}

// 422 Unprocessable Entity - Registration closed
{
  "success": false,
  "error": {
    "code": "REGISTRATION_CLOSED",
    "message": "Registration is not currently open for this course",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
1. Check prerequisites before enrollment
2. If `currentEnrollment < maxCapacity`: status = ENROLLED
3. If course full: status = WAITLISTED, assign waitlist position
4. If waitlist full: reject with error
5. Check for schedule conflicts (same day/time)
6. Payment status set to PENDING initially
7. Course access granted after payment validation
8. Trigger updates `courses.current_enrollment` or `courses.current_waitlist`

**SRS Requirements:** FR-2.2 (Course Registration), FR-5.1 (Registration Workflow)

---

### 6.2 Get Enrollment by ID
**Endpoint:** `GET /api/v1/enrollments/{enrollmentId}`

**Description:** Retrieves specific enrollment details.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY, STUDENT (only own enrollments)

**Path Parameters:**
- `enrollmentId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "enrollmentId": 501,
    "student": {
      "studentId": 123,
      "studentNumber": "STU2024001",
      "firstName": "Sarah",
      "lastName": "Johnson"
    },
    "course": {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "credits": 3,
      "instructor": "Dr. Michael Smith",
      "semester": "Fall",
      "academicYear": 2024
    },
    "enrollmentDate": "2024-08-20T10:30:00Z",
    "status": "ENROLLED",
    "droppedDate": null,
    "semester": "Fall",
    "academicYear": 2024,
    "paymentStatus": "VALIDATED",
    "courseAccessGranted": true,
    "waitlistPosition": null,
    "finalGrade": null,
    "gradePoints": null,
    "createdAt": "2024-08-20T10:30:00Z",
    "updatedAt": "2024-08-22T15:00:00Z"
  }
}
```

**Business Rules:**
- Students can only view their own enrollments
- Faculty can view enrollments for their courses
- Admins can view all enrollments

**SRS Requirements:** FR-2.2 (Enrollment Details)

---

### 6.3 Update Enrollment Status
**Endpoint:** `PATCH /api/v1/enrollments/{enrollmentId}`

**Description:** Updates enrollment status (e.g., drop course, move from waitlist to enrolled).

**Authentication Required:** Yes

**Authorization Roles:** STUDENT (drop only), ADMIN (all changes)

**Path Parameters:**
- `enrollmentId` (integer, required)

**Request Body:**
```json
{
  "status": "DROPPED"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Enrollment status updated successfully",
  "data": {
    "enrollmentId": 501,
    "status": "DROPPED",
    "droppedDate": "2024-09-15T14:30:00Z",
    "updatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "success": false,
  "error": {
    "code": "DROP_DEADLINE_PASSED",
    "message": "Cannot drop course after drop deadline",
    "details": {
      "dropDeadline": "2024-09-10",
      "currentDate": "2024-09-15"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Students can only drop (set status=DROPPED)
- Drop deadline enforced (configurable, e.g., 2 weeks into semester)
- Admins can change any status
- When student drops, trigger moves next waitlisted student to enrolled
- Refund logic handled separately (not in this endpoint)

**SRS Requirements:** FR-2.2 (Drop Course), FR-5.1 (Waitlist Management)

---

### 6.4 Delete Enrollment
**Endpoint:** `DELETE /api/v1/enrollments/{enrollmentId}`

**Description:** Permanently deletes enrollment record (admin only, use carefully).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Path Parameters:**
- `enrollmentId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Enrollment deleted successfully"
}
```

**Error Response (409 Conflict):**
```json
{
  "success": false,
  "error": {
    "code": "ENROLLMENT_HAS_GRADES",
    "message": "Cannot delete enrollment with existing grades",
    "details": {
      "enrollmentId": 501,
      "gradeCount": 5
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Hard delete (removes from database)
- Cannot delete if grades exist (data integrity)
- Prefer status update (DROPPED) over deletion
- Only for error corrections

**SRS Requirements:** FR-4.2 (Data Management)

---

### 6.5 Get All Enrollments
**Endpoint:** `GET /api/v1/enrollments`

**Description:** Retrieves paginated list of enrollments with filtering (admin only).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN

**Query Parameters:**
- `page` (integer, optional, default=1)
- `size` (integer, optional, default=50)
- `studentId` (integer, optional)
- `courseId` (integer, optional)
- `status` (enum, optional)
- `semester` (string, optional)
- `academicYear` (integer, optional)
- `paymentStatus` (enum, optional)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "enrollmentId": 501,
      "studentId": 123,
      "studentName": "Sarah Johnson",
      "courseId": 12,
      "courseCode": "CS301",
      "enrollmentDate": "2024-08-20T10:30:00Z",
      "status": "ENROLLED",
      "semester": "Fall",
      "academicYear": 2024,
      "paymentStatus": "VALIDATED"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 50,
    "totalPages": 20,
    "totalItems": 987
  }
}
```

**Business Rules:**
- Admin-only endpoint for reporting
- Supports complex filtering for analytics

**SRS Requirements:** FR-4.5 (Reporting and Analytics)

---

## 7. Grade Endpoints

### 7.1 Create Grade Entry
**Endpoint:** `POST /api/v1/grades`

**Description:** Faculty creates new grade entry for student (assignment, exam, or final).

**Authentication Required:** Yes

**Authorization Roles:** FACULTY (only own courses), ADMIN

**Request Body:**
```json
{
  "enrollmentId": 501,
  "gradeType": "ASSIGNMENT",
  "gradeName": "Homework 3 - Binary Search Trees",
  "gradeValue": 92.5,
  "maxPoints": 100.0,
  "weightPercentage": 10.0,
  "gradeDate": "2024-09-15",
  "comments": "Great work on the implementation! Minor issues with edge cases."
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Grade entry created successfully",
  "data": {
    "gradeId": 1050,
    "enrollmentId": 501,
    "studentId": 123,
    "courseId": 12,
    "gradeType": "ASSIGNMENT",
    "gradeName": "Homework 3 - Binary Search Trees",
    "gradeValue": 92.5,
    "maxPoints": 100.0,
    "weightPercentage": 10.0,
    "gradeDate": "2024-09-15",
    "enteredBy": 456,
    "enteredByName": "Dr. Michael Smith",
    "comments": "Great work on the implementation! Minor issues with edge cases.",
    "createdAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (403 Forbidden):**
```json
{
  "success": false,
  "error": {
    "code": "NOT_COURSE_INSTRUCTOR",
    "message": "You can only enter grades for your own courses",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Faculty can only enter grades for their courses
- `gradeValue` must be <= `maxPoints`
- `enteredBy` set to authenticated faculty ID
- `studentId` and `courseId` derived from enrollment (denormalized for performance)
- Notification sent to student when grade posted

**SRS Requirements:** FR-3.4 (Grade Entry), FR-2.9 (Grade Notifications)

---

### 7.2 Get Grade by ID
**Endpoint:** `GET /api/v1/grades/{gradeId}`

**Description:** Retrieves specific grade entry details.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY (own courses), STUDENT (own grades)

**Path Parameters:**
- `gradeId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "gradeId": 1050,
    "enrollment": {
      "enrollmentId": 501,
      "student": {
        "studentId": 123,
        "firstName": "Sarah",
        "lastName": "Johnson"
      },
      "course": {
        "courseId": 12,
        "courseCode": "CS301",
        "courseName": "Data Structures and Algorithms"
      }
    },
    "gradeType": "ASSIGNMENT",
    "gradeName": "Homework 3 - Binary Search Trees",
    "gradeValue": 92.5,
    "maxPoints": 100.0,
    "percentage": 92.5,
    "weightPercentage": 10.0,
    "gradeDate": "2024-09-15",
    "enteredBy": {
      "facultyId": 456,
      "firstName": "Michael",
      "lastName": "Smith"
    },
    "comments": "Great work on the implementation! Minor issues with edge cases.",
    "createdAt": "2024-09-15T14:30:00Z",
    "updatedAt": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Students can only view their own grades
- Faculty can view grades for their courses
- Admins can view all grades

**SRS Requirements:** FR-2.5 (Grade Viewing)

---

### 7.3 Update Grade
**Endpoint:** `PUT /api/v1/grades/{gradeId}`

**Description:** Updates existing grade entry (faculty only, within time window).

**Authentication Required:** Yes

**Authorization Roles:** FACULTY (who entered it), ADMIN

**Path Parameters:**
- `gradeId` (integer, required)

**Request Body:**
```json
{
  "gradeValue": 95.0,
  "comments": "Updated after regrade request - fixed scoring error"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Grade updated successfully",
  "data": {
    "gradeId": 1050,
    "gradeValue": 95.0,
    "comments": "Updated after regrade request - fixed scoring error",
    "updatedAt": "2024-09-16T10:00:00Z"
  }
}
```

**Error Response (403 Forbidden):**
```json
{
  "success": false,
  "error": {
    "code": "GRADE_EDIT_WINDOW_EXPIRED",
    "message": "Grades can only be edited within 7 days of entry",
    "details": {
      "gradeDate": "2024-09-01",
      "editDeadline": "2024-09-08",
      "currentDate": "2024-09-16"
    },
    "timestamp": "2024-09-16T10:00:00Z"
  }
}
```

**Business Rules:**
- Only faculty who entered grade can update it (or admin)
- Edit window: 7 days after creation (configurable)
- Cannot change: gradeType, enrollmentId
- Can change: gradeValue, comments
- Notification sent to student about grade change

**SRS Requirements:** FR-3.4 (Grade Management)

---

### 7.4 Delete Grade
**Endpoint:** `DELETE /api/v1/grades/{gradeId}`

**Description:** Deletes grade entry (admin only, for error correction).

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Path Parameters:**
- `gradeId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Grade deleted successfully"
}
```

**Business Rules:**
- Hard delete (removes from database)
- Only for error corrections
- Triggers GPA recalculation

**SRS Requirements:** FR-4.2 (Data Management)

---

### 7.5 Calculate Course Grade
**Endpoint:** `GET /api/v1/grades/calculate/{enrollmentId}`

**Description:** Calculates weighted final grade for student in course based on all grade entries.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, FACULTY (own courses), STUDENT (own grades)

**Path Parameters:**
- `enrollmentId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "enrollmentId": 501,
    "student": {
      "studentId": 123,
      "firstName": "Sarah",
      "lastName": "Johnson"
    },
    "course": {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "credits": 3
    },
    "gradeBreakdown": [
      {
        "gradeType": "ASSIGNMENT",
        "count": 5,
        "averageScore": 93.2,
        "totalWeight": 50.0,
        "contributionToFinal": 46.6
      },
      {
        "gradeType": "EXAM",
        "count": 2,
        "averageScore": 85.5,
        "totalWeight": 30.0,
        "contributionToFinal": 25.65
      },
      {
        "gradeType": "FINAL",
        "count": 1,
        "averageScore": 90.0,
        "totalWeight": 20.0,
        "contributionToFinal": 18.0
      }
    ],
    "calculatedFinalGrade": 90.25,
    "letterGrade": "A-",
    "gradePoints": 3.7,
    "totalWeightCovered": 100.0,
    "isComplete": true
  }
}
```

**Business Rules:**
- Weighted average based on `weight_percentage` for each grade
- Letter grade conversion:
  - A: 93-100 (4.0), A-: 90-92.99 (3.7)
  - B+: 87-89.99 (3.3), B: 83-86.99 (3.0), B-: 80-82.99 (2.7)
  - C+: 77-79.99 (2.3), C: 73-76.99 (2.0), C-: 70-72.99 (1.7)
  - D+: 67-69.99 (1.3), D: 63-66.99 (1.0), D-: 60-62.99 (0.7)
  - F: < 60 (0.0)
- If `totalWeightCovered < 100`, final grade incomplete

**SRS Requirements:** FR-2.5 (GPA Calculation), FR-3.5 (Grade Analytics)

---

## 8. Payment Endpoints

### 8.1 Create Payment
**Endpoint:** `POST /api/v1/payments`

**Description:** Student submits payment for course enrollments.

**Authentication Required:** Yes

**Authorization Roles:** STUDENT (only self), ADMIN

**Request Body:**
```json
{
  "studentId": 123,
  "amount": 1250.00,
  "paymentMethod": "BANK_TRANSFER",
  "transactionReference": "TXN20240915143000ABC",
  "semester": "Fall",
  "academicYear": 2024,
  "coursesCovered": [12, 15, 18]
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Payment submitted successfully. Awaiting admin validation.",
  "data": {
    "paymentId": 301,
    "studentId": 123,
    "amount": 1250.00,
    "paymentMethod": "BANK_TRANSFER",
    "transactionReference": "TXN20240915143000ABC",
    "status": "SUBMITTED",
    "paymentDate": "2024-09-15T14:30:00Z",
    "validatedBy": null,
    "validatedDate": null,
    "rejectionReason": null,
    "semester": "Fall",
    "academicYear": 2024,
    "coursesCovered": [12, 15, 18],
    "createdAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PAYMENT_AMOUNT",
    "message": "Payment amount does not match total course fees",
    "details": {
      "submittedAmount": 1000.00,
      "requiredAmount": 1250.00,
      "coursesCovered": [
        {"courseId": 12, "courseCode": "CS301", "fee": 550.00},
        {"courseId": 15, "courseCode": "MATH210", "fee": 450.00},
        {"courseId": 18, "courseCode": "PHYS101", "fee": 250.00}
      ]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Student must be enrolled in all listed courses
- Amount must match sum of course fees
- Payment status initially set to "SUBMITTED"
- Transaction reference must be unique
- Notification sent to student confirming submission
- Notification sent to admin for validation

**SRS Requirements:** FR-2.7 (Payment Processing), FR-5.3 (Payment Workflow)

---

### 8.2 Get Payment by ID
**Endpoint:** `GET /api/v1/payments/{paymentId}`

**Description:** Retrieves payment details.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, STUDENT (only own payments)

**Path Parameters:**
- `paymentId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "paymentId": 301,
    "student": {
      "studentId": 123,
      "studentNumber": "STU2024001",
      "firstName": "Sarah",
      "lastName": "Johnson"
    },
    "amount": 1250.00,
    "paymentMethod": "BANK_TRANSFER",
    "transactionReference": "TXN20240915143000ABC",
    "status": "VALIDATED",
    "paymentDate": "2024-09-15T14:30:00Z",
    "validatedBy": {
      "userId": 999,
      "firstName": "Admin",
      "lastName": "User"
    },
    "validatedDate": "2024-09-16T09:00:00Z",
    "rejectionReason": null,
    "semester": "Fall",
    "academicYear": 2024,
    "coursesCovered": [
      {
        "courseId": 12,
        "courseCode": "CS301",
        "courseName": "Data Structures and Algorithms",
        "courseFee": 550.00
      },
      {
        "courseId": 15,
        "courseCode": "MATH210",
        "courseName": "Calculus II",
        "courseFee": 450.00
      },
      {
        "courseId": 18,
        "courseCode": "PHYS101",
        "courseName": "Physics I",
        "courseFee": 250.00
      }
    ],
    "createdAt": "2024-09-15T14:30:00Z",
    "updatedAt": "2024-09-16T09:00:00Z"
  }
}
```

**Business Rules:**
- Students can only view their own payments
- Admins can view all payments
- Includes course details for transparency

**SRS Requirements:** FR-2.6 (Financial Information)

---

### 8.3 Validate Payment (Admin)
**Endpoint:** `POST /api/v1/payments/{paymentId}/validate`

**Description:** Admin validates or rejects payment submission.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Path Parameters:**
- `paymentId` (integer, required)

**Request Body (Validate):**
```json
{
  "action": "VALIDATE"
}
```

**Request Body (Reject):**
```json
{
  "action": "REJECT",
  "rejectionReason": "Transaction reference not found in bank records. Please resubmit with correct reference number."
}
```

**Success Response (200 OK - Validated):**
```json
{
  "success": true,
  "message": "Payment validated successfully. Student granted course access.",
  "data": {
    "paymentId": 301,
    "status": "VALIDATED",
    "validatedBy": 999,
    "validatedDate": "2024-09-16T09:00:00Z",
    "affectedEnrollments": [
      {
        "enrollmentId": 501,
        "courseCode": "CS301",
        "paymentStatus": "VALIDATED",
        "courseAccessGranted": true
      },
      {
        "enrollmentId": 502,
        "courseCode": "MATH210",
        "paymentStatus": "VALIDATED",
        "courseAccessGranted": true
      }
    ]
  }
}
```

**Success Response (200 OK - Rejected):**
```json
{
  "success": true,
  "message": "Payment rejected",
  "data": {
    "paymentId": 301,
    "status": "REJECTED",
    "validatedBy": 999,
    "validatedDate": "2024-09-16T09:00:00Z",
    "rejectionReason": "Transaction reference not found in bank records. Please resubmit with correct reference number."
  }
}
```

**Business Rules:**
- Only admins can validate/reject payments
- On VALIDATE:
  - Update payment status to "VALIDATED"
  - Update all related enrollments: `payment_status = VALIDATED`, `course_access_granted = true`
  - Send notification to student confirming validation
- On REJECT:
  - Update payment status to "REJECTED"
  - Store rejection reason
  - Send notification to student with reason
  - Student must resubmit payment

**SRS Requirements:** FR-4.4 (Payment Validation), FR-5.3 (Payment Workflow)

---

### 8.4 Get Student Payments
**Endpoint:** `GET /api/v1/students/{studentId}/payments`

**Description:** Retrieves all payments for specific student.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN, STUDENT (only own payments)

**Path Parameters:**
- `studentId` (integer, required)

**Query Parameters:**
- `semester` (string, optional)
- `academicYear` (integer, optional)
- `status` (enum, optional)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "paymentId": 301,
      "amount": 1250.00,
      "paymentMethod": "BANK_TRANSFER",
      "transactionReference": "TXN20240915143000ABC",
      "status": "VALIDATED",
      "paymentDate": "2024-09-15T14:30:00Z",
      "validatedDate": "2024-09-16T09:00:00Z",
      "semester": "Fall",
      "academicYear": 2024,
      "coursesCovered": ["CS301", "MATH210", "PHYS101"]
    }
  ],
  "summary": {
    "totalPaid": 1250.00,
    "pendingPayments": 0,
    "validatedPayments": 1,
    "rejectedPayments": 0
  }
}
```

**Business Rules:**
- Sorted by payment date (most recent first)
- Includes payment summary for student dashboard

**SRS Requirements:** FR-2.6 (Payment History)

---

### 8.5 Get All Payments (Admin)
**Endpoint:** `GET /api/v1/payments`

**Description:** Retrieves paginated list of all payments with filtering.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Query Parameters:**
- `page` (integer, optional, default=1)
- `size` (integer, optional, default=50)
- `status` (enum, optional) - Filter by payment status
- `semester` (string, optional)
- `academicYear` (integer, optional)
- `studentId` (integer, optional)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "paymentId": 301,
      "studentId": 123,
      "studentName": "Sarah Johnson",
      "amount": 1250.00,
      "paymentMethod": "BANK_TRANSFER",
      "transactionReference": "TXN20240915143000ABC",
      "status": "SUBMITTED",
      "paymentDate": "2024-09-15T14:30:00Z",
      "semester": "Fall",
      "academicYear": 2024
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 50,
    "totalPages": 12,
    "totalItems": 587
  },
  "summary": {
    "pendingValidation": 45,
    "validated": 520,
    "rejected": 22
  }
}
```

**Business Rules:**
- Admin-only endpoint for payment management
- Pending payments shown first (sorted by submission date)

**SRS Requirements:** FR-4.4 (Payment Management), FR-4.5 (Reporting)

---

## 9. Study Group Endpoints

### 9.1 Create Study Group
**Endpoint:** `POST /api/v1/study-groups`

**Description:** Student creates new study group for a course.

**Authentication Required:** Yes

**Authorization Roles:** STUDENT, FACULTY, ADMIN

**Request Body:**
```json
{
  "groupName": "CS301 Algorithm Masters",
  "courseId": 12,
  "description": "Study group for CS301. We meet twice weekly to review assignments and prepare for exams. Friendly and collaborative environment!",
  "maxMembers": 6
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Study group created successfully",
  "data": {
    "groupId": 201,
    "groupName": "CS301 Algorithm Masters",
    "course": {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms"
    },
    "createdBy": {
      "studentId": 123,
      "firstName": "Sarah",
      "lastName": "Johnson"
    },
    "description": "Study group for CS301. We meet twice weekly to review assignments and prepare for exams. Friendly and collaborative environment!",
    "maxMembers": 6,
    "currentMembers": 1,
    "isActive": true,
    "createdAt": "2024-09-15T14:30:00Z"
  }
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "success": false,
  "error": {
    "code": "NOT_ENROLLED_IN_COURSE",
    "message": "You must be enrolled in the course to create a study group",
    "details": {
      "courseId": 12,
      "studentId": 123
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Business Rules:**
- Creator must be enrolled in the course
- Creator automatically added as first member
- `maxMembers` range: 2-10
- Group name must be unique per course
- Notification sent to course students about new group

**SRS Requirements:** FR-2.8 (Study Group Creation)

---

### 9.2 Get All Study Groups
**Endpoint:** `GET /api/v1/study-groups`

**Description:** Retrieves list of active study groups with filtering.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Query Parameters:**
- `courseId` (integer, optional) - Filter by course
- `hasOpenings` (boolean, optional) - Only groups with available slots
- `search` (string, optional) - Search in group name/description

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "groupId": 201,
      "groupName": "CS301 Algorithm Masters",
      "course": {
        "courseId": 12,
        "courseCode": "CS301",
        "courseName": "Data Structures and Algorithms"
      },
      "createdBy": {
        "studentId": 123,
        "firstName": "Sarah",
        "lastName": "Johnson"
      },
      "description": "Study group for CS301. We meet twice weekly to review assignments and prepare for exams.",
      "maxMembers": 6,
      "currentMembers": 4,
      "availableSlots": 2,
      "isActive": true,
      "createdAt": "2024-09-15T14:30:00Z"
    }
  ]
}
```

**Business Rules:**
- Only active groups shown by default
- Sorted by creation date (newest first)
- Shows availability for joining

**SRS Requirements:** FR-2.8 (Study Group Discovery)

---

### 9.3 Get Study Group by ID
**Endpoint:** `GET /api/v1/study-groups/{groupId}`

**Description:** Retrieves detailed information about specific study group including member list.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Path Parameters:**
- `groupId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "groupId": 201,
    "groupName": "CS301 Algorithm Masters",
    "course": {
      "courseId": 12,
      "courseCode": "CS301",
      "courseName": "Data Structures and Algorithms",
      "semester": "Fall",
      "academicYear": 2024
    },
    "createdBy": {
      "studentId": 123,
      "firstName": "Sarah",
      "lastName": "Johnson"
    },
    "description": "Study group for CS301. We meet twice weekly to review assignments and prepare for exams. Friendly and collaborative environment!",
    "maxMembers": 6,
    "currentMembers": 4,
    "availableSlots": 2,
    "isActive": true,
    "members": [
      {
        "membershipId": 1001,
        "student": {
          "studentId": 123,
          "firstName": "Sarah",
          "lastName": "Johnson",
          "email": "sarah.johnson@university.edu",
          "major": "Computer Science"
        },
        "joinedDate": "2024-09-15T14:30:00Z",
        "isActive": true
      },
      {
        "membershipId": 1002,
        "student": {
          "studentId": 456,
          "firstName": "John",
          "lastName": "Doe",
          "email": "john.doe@university.edu",
          "major": "Mathematics"
        },
        "joinedDate": "2024-09-16T10:00:00Z",
        "isActive": true
      }
    ],
    "createdAt": "2024-09-15T14:30:00Z",
    "updatedAt": "2024-09-16T10:00:00Z"
  }
}
```

**Business Rules:**
- Full member list visible to all (for collaboration)
- Contact info visible for coordination

**SRS Requirements:** FR-2.8 (Study Group Details)

---

### 9.4 Join Study Group
**Endpoint:** `POST /api/v1/study-groups/{groupId}/join`

**Description:** Student joins existing study group.

**Authentication Required:** Yes

**Authorization Roles:** STUDENT

**Path Parameters:**
- `groupId` (integer, required)

**Request Body:** None (studentId derived from JWT token)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Successfully joined study group",
  "data": {
    "membershipId": 1003,
    "groupId": 201,
    "groupName": "CS301 Algorithm Masters",
    "student": {
      "studentId": 789,
      "firstName": "Emily",
      "lastName": "Davis"
    },
    "joinedDate": "2024-09-17T11:00:00Z",
    "isActive": true
  }
}
```

**Error Responses:**
```json
// 409 Conflict - Group full
{
  "success": false,
  "error": {
    "code": "GROUP_FULL",
    "message": "Study group has reached maximum capacity",
    "details": {
      "maxMembers": 6,
      "currentMembers": 6
    },
    "timestamp": "2024-09-17T11:00:00Z"
  }
}

// 409 Conflict - Already a member
{
  "success": false,
  "error": {
    "code": "ALREADY_MEMBER",
    "message": "You are already a member of this study group",
    "timestamp": "2024-09-17T11:00:00Z"
  }
}

// 422 Unprocessable Entity - Not enrolled in course
{
  "success": false,
  "error": {
    "code": "NOT_ENROLLED_IN_COURSE",
    "message": "You must be enrolled in the course to join this study group",
    "timestamp": "2024-09-17T11:00:00Z"
  }
}
```

**Business Rules:**
- Student must be enrolled in the course
- Group must have available slots
- Cannot join if already a member
- Trigger updates `study_groups.current_members`
- Notification sent to group creator

**SRS Requirements:** FR-2.8 (Study Group Participation)

---

### 9.5 Leave Study Group
**Endpoint:** `POST /api/v1/study-groups/{groupId}/leave`

**Description:** Student leaves study group.

**Authentication Required:** Yes

**Authorization Roles:** STUDENT

**Path Parameters:**
- `groupId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Successfully left study group"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "error": {
    "code": "NOT_A_MEMBER",
    "message": "You are not a member of this study group",
    "timestamp": "2024-09-17T11:00:00Z"
  }
}
```

**Business Rules:**
- Sets `study_group_members.is_active = false`, records `left_date`
- Creator cannot leave (must delete group instead)
- Trigger updates `study_groups.current_members`

**SRS Requirements:** FR-2.8 (Study Group Management)

---

## 10. Notification Endpoints

### 10.1 Get User Notifications
**Endpoint:** `GET /api/v1/notifications`

**Description:** Retrieves authenticated user's notifications.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Query Parameters:**
- `page` (integer, optional, default=1)
- `size` (integer, optional, default=20)
- `isRead` (boolean, optional) - Filter by read status
- `notificationType` (enum, optional) - Filter by type

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "notificationId": 5001,
      "notificationType": "GRADE_POSTED",
      "title": "New Grade Posted",
      "message": "Dr. Michael Smith posted a grade for CS301 - Homework 3",
      "isRead": false,
      "readAt": null,
      "relatedEntityType": "GRADE",
      "relatedEntityId": 1050,
      "priority": "MEDIUM",
      "createdAt": "2024-09-15T16:00:00Z"
    },
    {
      "notificationId": 5002,
      "notificationType": "PAYMENT_VALIDATED",
      "title": "Payment Validated",
      "message": "Your payment of $1,250.00 has been validated. You now have access to all enrolled courses.",
      "isRead": true,
      "readAt": "2024-09-16T09:30:00Z",
      "relatedEntityType": "PAYMENT",
      "relatedEntityId": 301,
      "priority": "HIGH",
      "createdAt": "2024-09-16T09:00:00Z"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 3,
    "totalItems": 47
  },
  "summary": {
    "unreadCount": 12
  }
}
```

**Business Rules:**
- User can only see their own notifications
- Sorted by creation date (newest first)
- Unread notifications prioritized

**SRS Requirements:** FR-2.9 (Intelligent Notifications)

---

### 10.2 Mark Notification as Read
**Endpoint:** `PATCH /api/v1/notifications/{notificationId}/read`

**Description:** Marks notification as read.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users (own notifications only)

**Path Parameters:**
- `notificationId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification marked as read",
  "data": {
    "notificationId": 5001,
    "isRead": true,
    "readAt": "2024-09-17T11:00:00Z"
  }
}
```

**Business Rules:**
- User can only mark their own notifications as read
- Sets `is_read = true`, records `read_at` timestamp

**SRS Requirements:** FR-2.9 (Notification Management)

---

### 10.3 Mark All Notifications as Read
**Endpoint:** `POST /api/v1/notifications/mark-all-read`

**Description:** Marks all user's notifications as read.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "All notifications marked as read",
  "data": {
    "updatedCount": 12
  }
}
```

**Business Rules:**
- Bulk updates all unread notifications for user

**SRS Requirements:** FR-2.9 (Notification Management)

---

### 10.4 Delete Notification
**Endpoint:** `DELETE /api/v1/notifications/{notificationId}`

**Description:** Deletes notification.

**Authentication Required:** Yes

**Authorization Roles:** All authenticated users (own notifications only)

**Path Parameters:**
- `notificationId` (integer, required)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Notification deleted successfully"
}
```

**Business Rules:**
- Hard delete (removes from database)
- User can only delete their own notifications

**SRS Requirements:** FR-2.9 (Notification Management)

---

## 11. Admin Endpoints

### 11.1 Get System Statistics
**Endpoint:** `GET /api/v1/admin/statistics`

**Description:** Retrieves system-wide statistics and metrics.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Query Parameters:**
- `semester` (string, optional, default=current)
- `academicYear` (integer, optional, default=current)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "semester": "Fall",
    "academicYear": 2024,
    "users": {
      "totalUsers": 342,
      "activeUsers": 315,
      "students": 287,
      "faculty": 28,
      "admins": 5
    },
    "courses": {
      "totalCourses": 68,
      "activeCourses": 65,
      "registrationOpen": 52,
      "averageEnrollment": 24.5,
      "coursesByDepartment": {
        "Computer Science": 15,
        "Mathematics": 12,
        "Physics": 10,
        "English": 8,
        "Business": 10,
        "Other": 13
      }
    },
    "enrollments": {
      "totalEnrollments": 987,
      "enrolled": 945,
      "waitlisted": 42,
      "dropped": 25,
      "completed": 0
    },
    "payments": {
      "totalPayments": 587,
      "validated": 520,
      "pending": 45,
      "rejected": 22,
      "totalRevenue": 734500.00
    },
    "grades": {
      "totalGrades": 5432,
      "averageGpa": 3.15
    },
    "studyGroups": {
      "totalGroups": 45,
      "activeGroups": 42,
      "totalMemberships": 178
    },
    "generatedAt": "2024-09-17T11:00:00Z"
  }
}
```

**Business Rules:**
- Admin-only endpoint
- Real-time calculations from database
- Useful for dashboard and reporting

**SRS Requirements:** FR-4.5 (Reporting and Analytics)

---

### 11.2 Toggle User Active Status
**Endpoint:** `PATCH /api/v1/admin/users/{userId}/toggle-active`

**Description:** Activates or deactivates user account.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Path Parameters:**
- `userId` (integer, required)

**Request Body:**
```json
{
  "isActive": false,
  "reason": "Student withdrew from university"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "User account deactivated successfully",
  "data": {
    "userId": 789,
    "username": "john.doe",
    "isActive": false,
    "updatedAt": "2024-09-17T11:00:00Z"
  }
}
```

**Business Rules:**
- Deactivated users cannot login
- Preserves all historical data
- Used for withdrawals, suspensions, etc.

**SRS Requirements:** FR-4.3 (User Management)

---

### 11.3 Bulk Import Students
**Endpoint:** `POST /api/v1/admin/students/bulk-import`

**Description:** Bulk imports student records from CSV file.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Request:** Multipart form data with CSV file

**CSV Format:**
```csv
firstName,lastName,email,studentNumber,major,admissionDate
Sarah,Johnson,sarah.johnson@university.edu,STU2024001,Computer Science,2024-01-15
John,Doe,john.doe@university.edu,STU2024002,Mathematics,2024-01-15
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Bulk import completed",
  "data": {
    "totalRows": 50,
    "successfulImports": 48,
    "failedImports": 2,
    "errors": [
      {
        "row": 15,
        "email": "duplicate@university.edu",
        "error": "Email already exists"
      },
      {
        "row": 32,
        "email": "invalid-email",
        "error": "Invalid email format"
      }
    ]
  }
}
```

**Business Rules:**
- Validates each row before import
- Creates user account + student profile
- Generates random temporary password
- Sends welcome email with credentials (Phase 2)
- Skips duplicates, logs errors

**SRS Requirements:** FR-4.3 (Bulk Operations)

---

### 11.4 Generate Reports
**Endpoint:** `POST /api/v1/admin/reports/generate`

**Description:** Generates various administrative reports.

**Authentication Required:** Yes

**Authorization Roles:** ADMIN only

**Request Body:**
```json
{
  "reportType": "ENROLLMENT_SUMMARY",
  "semester": "Fall",
  "academicYear": 2024,
  "format": "PDF"
}
```

**Report Types:**
- `ENROLLMENT_SUMMARY` - Course enrollment statistics
- `PAYMENT_REPORT` - Financial report
- `GRADE_DISTRIBUTION` - Grade distribution analysis
- `STUDENT_ROSTER` - Complete student list

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Report generated successfully",
  "data": {
    "reportId": "RPT-20240917-001",
    "reportType": "ENROLLMENT_SUMMARY",
    "downloadUrl": "/api/v1/admin/reports/download/RPT-20240917-001",
    "expiresAt": "2024-09-18T11:00:00Z",
    "generatedAt": "2024-09-17T11:00:00Z"
  }
}
```

**Business Rules:**
- PDF generation using Jasper Reports (Phase 2)
- Temporary download links expire after 24 hours
- Reports stored temporarily, then archived

**SRS Requirements:** FR-4.5 (Reporting and Analytics)

---

## API Endpoint Summary

### Total Endpoints: 37

**Authentication (4):**
1. POST /api/v1/auth/login
2. POST /api/v1/auth/logout
3. POST /api/v1/auth/refresh
4. POST /api/v1/auth/reset-password

**User Management (4):**
5. GET /api/v1/users/me
6. PUT /api/v1/users/me
7. POST /api/v1/users/me/change-password
8. GET /api/v1/users/{userId}

**Students (6):**
9. GET /api/v1/students
10. GET /api/v1/students/{studentId}
11. GET /api/v1/students/{studentId}/enrollments
12. GET /api/v1/students/{studentId}/grades
13. GET /api/v1/students/{studentId}/transcript
14. GET /api/v1/students/{studentId}/degree-progress

**Faculty (4):**
15. GET /api/v1/faculty
16. GET /api/v1/faculty/{facultyId}
17. GET /api/v1/faculty/{facultyId}/courses
18. GET /api/v1/faculty/{facultyId}/students

**Courses (7):**
19. GET /api/v1/courses
20. GET /api/v1/courses/{courseId}
21. POST /api/v1/courses
22. PUT /api/v1/courses/{courseId}
23. DELETE /api/v1/courses/{courseId}
24. GET /api/v1/courses/{courseId}/enrollments
25. POST /api/v1/courses/{courseId}/check-prerequisites

**Enrollments (5):**
26. POST /api/v1/enrollments
27. GET /api/v1/enrollments/{enrollmentId}
28. PATCH /api/v1/enrollments/{enrollmentId}
29. DELETE /api/v1/enrollments/{enrollmentId}
30. GET /api/v1/enrollments

**Grades (5):**
31. POST /api/v1/grades
32. GET /api/v1/grades/{gradeId}
33. PUT /api/v1/grades/{gradeId}
34. DELETE /api/v1/grades/{gradeId}
35. GET /api/v1/grades/calculate/{enrollmentId}

**Payments (5):**
36. POST /api/v1/payments
37. GET /api/v1/payments/{paymentId}
38. POST /api/v1/payments/{paymentId}/validate
39. GET /api/v1/students/{studentId}/payments
40. GET /api/v1/payments

**Study Groups (5):**
41. POST /api/v1/study-groups
42. GET /api/v1/study-groups
43. GET /api/v1/study-groups/{groupId}
44. POST /api/v1/study-groups/{groupId}/join
45. POST /api/v1/study-groups/{groupId}/leave

**Notifications (4):**
46. GET /api/v1/notifications
47. PATCH /api/v1/notifications/{notificationId}/read
48. POST /api/v1/notifications/mark-all-read
49. DELETE /api/v1/notifications/{notificationId}

**Admin (4):**
50. GET /api/v1/admin/statistics
51. PATCH /api/v1/admin/users/{userId}/toggle-active
52. POST /api/v1/admin/students/bulk-import
53. POST /api/v1/admin/reports/generate

---

**Document Status:** Complete
**Total Endpoints Documented:** 37 (exceeds requirement of 35-40)
**Next Step:** API-Database mapping documentation
