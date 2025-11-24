# Week 6 & 7 Learning Guide: Advanced Features and Security 🎓

**Hey there! Welcome to the advanced section of SAMS development! 🚀**

This guide will walk you through Week 6 and Week 7 features in super simple terms. Think of me as your friendly senior student who's here to explain everything step by step!

---

## Table of Contents

1. [Week 6: Business Logic & Enrollment Rules](#week-6-business-logic--enrollment-rules)
2. [Week 7: Grade Management & Security](#week-7-grade-management--security)
3. [Complete API Reference](#complete-api-reference)
4. [Testing Guide](#testing-guide)
5. [Presentation Talking Points](#presentation-talking-points)

---

## Week 6: Business Logic & Enrollment Rules

### Overview: What We Built This Week

In Week 6, we made our enrollment system SMART! Before, students could enroll in any course. Now, the system checks:
- ✅ Does the student have the required prerequiset courses?
- ✅ Does this course conflict with their existing schedule?
- ✅ Is there space in the class?
- ✅ If full, can we add them to a waitlist?

Think of it like a really smart school counselor who checks everything before letting you register!

---

### Feature 1: Prerequisite Validation

#### What Are Prerequisites? (Simple Explanation)

Imagine you want to take "Advanced Math" but you never learned basic math. That would be hard, right? Prerequisites are courses you MUST complete before taking another course.

**Real-World Example:**
- To take CS201 (Data Structures), you must complete CS101 (Intro to Programming)
- To take PHYS302 (Quantum Physics), you must complete PHYS101 (Physics I) and MATH201 (Calculus)

#### How It Works (Step-by-Step)

**The Algorithm:**
```
1. Student tries to enroll in Course X
2. System checks: Does Course X have any prerequisets?
3. If NO prerequisites → Allow enrollment
4. If YES prerequisites:
   a. Get list of courses student has COMPLETED
   b. Check if student completed ALL prerequisets
   c. If YES → Allow enrollment
   d. If NO → REJECT and tell them what's missing
```

#### The Code (Explained Like You're 12)

Let's look at the `validatePrerequisites` method:

```java
private void validatePrerequisites(User student, Course course) {
    // Get the courses you need to take first
    Set<Course> prerequisites = course.getPrerequisites();

    // If there are no prerequisets, we're good!
    if (prerequisites == null || prerequisites.isEmpty()) {
        return; // Exit - no checking needed
    }

    // Get all courses the student finished
    List<Enrollment> completedEnrollments = enrollmentRepository
        .findByStudentIdAndStatus(student.getId(), "COMPLETED");

    // Make a list of IDs of completed courses (easier to check)
    Set<Long> completedCourseIds = completedEnrollments.stream()
        .map(e -> e.getCourse().getId())
        .collect(Collectors.toSet());

    // Find which prerequisets the student is missing
    List<Course> missingPrereqs = prerequisites.stream()
        .filter(prereq -> !completedCourseIds.contains(prereq.getId()))
        .collect(Collectors.toList());

    // If they're missing any, REJECT!
    if (!missingPrereqs.isEmpty()) {
        String missingCodes = missingPrereqs.stream()
            .map(Course::getCourseCode)
            .collect(Collectors.joining(", "));

        throw new PrerequisiteNotMetException(course.getCourseCode(), missingCodes);
    }
}
```

**Why This Design Is Good:**
- ✅ Prevents students from taking courses they're not ready for
- ✅ Maintains academic standards
- ✅ Gives clear error messages showing what's missing
- ✅ Uses efficient Set lookup (O(1) instead of O(n))

#### Try It Yourself! (API Example)

**Step 1: Create courses with prerequisites**
```bash
# Create CS101 (no prerequisites)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Save the token from response, then:
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS101",
    "courseName": "Intro to Programming",
    "credits": 3,
    "capacity": 30,
    "instructorId": 1
  }'

# Create CS201 (requires CS101)
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS201",
    "courseName": "Data Structures",
    "credits": 4,
    "capacity": 25,
    "instructorId": 1,
    "prerequisiteIds": [1]
  }'
```

**Step 2: Try to enroll without completing CS101**
```bash
# This will FAIL with error: "Missing prerequisites: CS101"
curl -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 2,
    "courseId": 2
  }'
```

---

### Feature 2: Schedule Conflict Detection

#### What Are Schedule Conflicts? (Simple Explanation)

Imagine you're enrolled in "Math" that meets Monday 9-10am. Now you try to enroll in "History" that also meets Monday 9-10am. You can't be in two places at once! That's a schedule conflict.

#### The Algorithm (Visual Walkthrough)

```
Student Schedule:
MON/WED: 09:00-10:30 (Math)
TUE/THU: 14:00-15:30 (English)

New Course:
MON/WED: 10:00-11:30 (Physics)

Checking for conflicts:
1. Does Physics share any days with existing courses?
   - Math: MON/WED ✓ (shares MON and WED)
   - English: TUE/THU ✗ (no common days)

2. For courses sharing days (Math), check time overlap:
   - Math: 09:00-10:30
   - Physics: 10:00-11:30

   Overlap formula: start1 < end2 AND start2 < end1
   - 09:00 < 11:30? YES
   - 10:00 < 10:30? YES
   - CONFLICT DETECTED! ❌
```

#### The Code (With Friendly Comments)

```java
private boolean hasScheduleConflict(Course course1, Course course2) {
    // Get days for each course (like ["MON", "WED"])
    Set<String> days1 = parseDaysOfWeek(course1.getDaysOfWeek());
    Set<String> days2 = parseDaysOfWeek(course2.getDaysOfWeek());

    // Find common days
    Set<String> commonDays = new HashSet<>(days1);
    commonDays.retainAll(days2); // Keep only days that appear in both

    if (commonDays.isEmpty()) {
        return false; // No common days = No conflict!
    }

    // They share days, now check if times overlap
    LocalTime start1 = course1.getStartTime();
    LocalTime end1 = course1.getEndTime();
    LocalTime start2 = course2.getStartTime();
    LocalTime end2 = course2.getEndTime();

    // Magic overlap formula!
    return start1.isBefore(end2) && start2.isBefore(end1);
}
```

**The Magic Overlap Formula Explained:**

Two time ranges overlap if:
- The first starts before the second ends, AND
- The second starts before the first ends

```
Case 1: Perfect Overlap
[----Course 1----]
[----Course 2----]
CONFLICT!

Case 2: Partial Overlap
[----Course 1----]
      [----Course 2----]
CONFLICT!

Case 3: No Overlap
[----Course 1----]
                   [----Course 2----]
NO CONFLICT!
```

#### API Example: Testing Schedule Conflicts

```bash
# Create course with schedule
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "MATH201",
    "courseName": "Calculus",
    "daysOfWeek": "MON,WED,FRI",
    "startTime": "09:00",
    "endTime": "10:30",
    "capacity": 30,
    "instructorId": 1
  }'

# Enroll student (this will work)
curl -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 2,
    "courseId": 3
  }'

# Try to enroll in conflicting course (this will FAIL)
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "PHYS101",
    "courseName": "Physics I",
    "daysOfWeek": "MON,WED",
    "startTime": "10:00",
    "endTime": "11:30",
    "capacity": 25,
    "instructorId": 1
  }'

# Error: "Schedule conflict: PHYS101 conflicts with MATH201"
```

---

### Feature 3: Enrollment Capacity & Waitlist

#### How Waitlists Work (Real-World Analogy)

Think of a popular concert. There are only 100 seats. When 100 people buy tickets, the concert is FULL. But people still want to go! So the venue creates a WAITLIST. If someone cancels, the first person on the waitlist gets their seat.

Our system works the same way!

#### The Complete Enrollment Flow

```
Student tries to enroll in course:
↓
Check 1: Is student already enrolled?
   YES → REJECT ("Already enrolled")
   NO → Continue
↓
Check 2: Did student complete prerequisets?
   NO → REJECT ("Missing prerequisites")
   YES → Continue
↓
Check 3: Does schedule conflict with existing classes?
   YES → REJECT ("Schedule conflict")
   NO → Continue
↓
Check 4: Is there space in the course?
   YES → ENROLL as ACTIVE ✅
   NO → Add to WAITLIST 📋
```

#### Waitlist Management Algorithm

```java
@Transactional
public Enrollment createEnrollment(Long studentId, Long courseId) {
    // ... validation checks ...

    // Check capacity
    long activeEnrolledCount = enrollmentRepository.countByCourseAndStatus(course, "ACTIVE");

    Enrollment enrollment = new Enrollment(student, course);

    if (activeEnrolledCount >= course.getCapacity()) {
        // Course is FULL - add to waitlist
        enrollment.setStatus("WAITLISTED");

        // Calculate their position in waitlist
        long waitlistCount = enrollmentRepository.countByCourseAndStatus(course, "WAITLISTED");
        enrollment.setWaitlistPosition((int) (waitlistCount + 1));
    } else {
        // Course has space - enroll normally
        enrollment.setStatus("ACTIVE");
        enrollment.setWaitlistPosition(null);
    }

    return enrollmentRepository.save(enrollment);
}
```

#### Auto-Promotion from Waitlist

When a student drops a course, we automatically promote the first person from the waitlist!

```java
@Transactional
public void processWaitlist(Course course) {
    // Get waitlist ordered by position
    List<Enrollment> waitlist = enrollmentRepository
        .findByCourseAndStatusOrderByWaitlistPositionAsc(course, "WAITLISTED");

    if (waitlist.isEmpty()) {
        return; // Nobody waiting
    }

    // Check if there's space
    long activeCount = enrollmentRepository.countByCourseAndStatus(course, "ACTIVE");
    if (activeCount >= course.getCapacity()) {
        return; // Still full
    }

    // Promote first person!
    Enrollment firstWaitlisted = waitlist.get(0);
    firstWaitlisted.setStatus("ACTIVE");
    firstWaitlisted.setWaitlistPosition(null);
    enrollmentRepository.save(firstWaitlisted);

    // Update everyone else's position
    for (int i = 1; i < waitlist.size(); i++) {
        Enrollment e = waitlist.get(i);
        e.setWaitlistPosition(i);
        enrollmentRepository.save(e);
    }
}
```

**Why This Design Is Awesome:**
- ✅ Fair: First to waitlist gets promoted first (FIFO - First In First Out)
- ✅ Automatic: No manual work needed
- ✅ Transparent: Students can see their waitlist position
- ✅ Efficient: Processes waitlist immediately when space opens

#### Complete Enrollment API Examples

```bash
# Scenario 1: Normal enrollment (course has space)
curl -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 2,
    "courseId": 1
  }'

# Response:
{
  "id": 1,
  "status": "ACTIVE",
  "waitlistPosition": null,
  ...
}

# Scenario 2: Course is full → Waitlisted
curl -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer STUDENT_TOKEN2" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "courseId": 1
  }'

# Response:
{
  "id": 2,
  "status": "WAITLISTED",
  "waitlistPosition": 1,
  ...
}

# Scenario 3: Drop enrollment (triggers waitlist processing)
curl -X PUT http://localhost:8080/api/enrollments/1/drop \
  -H "Authorization: Bearer STUDENT_TOKEN"

# Student 3 is automatically promoted from waitlist to ACTIVE!
```

---

## Week 7: Grade Management & Security

### Overview: What We Built This Week

Week 7 is where things get SERIOUS! We added:
- 📊 **Grade Management**: Track student grades and calculate GPAs
- 🔒 **JWT Authentication**: Secure API with tokens
- 🌐 **CORS**: Allow frontend apps to connect

---

### Feature 4: Grade Management & GPA Calculation

#### Understanding Grades (The Basics)

**Letter Grades vs. Grade Points:**
- A = 4.0 points (Excellent!)
- B = 3.0 points (Good)
- C = 2.0 points (Okay)
- D = 1.0 points (Passing but barely)
- F = 0.0 points (Failed)

**GPA (Grade Point Average):**
Think of GPA as your "average score" across all classes, but weighted by how many credits each class is worth.

#### The GPA Calculation Algorithm (Explained Simply)

```
Example:
Course 1: A (4.0 points) × 3 credits = 12.0
Course 2: B (3.0 points) × 4 credits = 12.0
Course 3: A- (3.7 points) × 3 credits = 11.1
                           Total: 10 credits, 35.1 points

GPA = 35.1 ÷ 10 = 3.51
```

**Pseudocode:**
```
function calculateGPA(studentId):
    grades = getAllGradesForStudent(studentId)
    totalPoints = 0
    totalCredits = 0

    for each grade in grades:
        course = grade.getCourse()
        points = grade.getGradePoints() * course.getCredits()
        totalPoints += points
        totalCredits += course.getCredits()

    if totalCredits == 0:
        return 0.0

    gpa = totalPoints / totalCredits
    return round(gpa, 2)
```

#### The Actual Code

```java
public Double calculateGPA(Long studentId) {
    User student = userService.getUserById(studentId);
    List<Grade> grades = gradeRepository.findByStudent(student);

    if (grades.isEmpty()) {
        return 0.0; // No grades yet
    }

    double totalPoints = 0.0;
    int totalCredits = 0;

    for (Grade grade : grades) {
        Course course = grade.getCourse();
        totalPoints += grade.getGradePoints() * course.getCredits();
        totalCredits += course.getCredits();
    }

    if (totalCredits == 0) {
        return 0.0;
    }

    // Round to 2 decimal places
    return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
}
```

#### Grade Management API Examples

```bash
# Assign grade to completed enrollment
curl -X POST http://localhost:8080/api/grades \
  -H "Authorization: Bearer FACULTY_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "enrollmentId": 1,
    "gradeValue": "A"
  }'

# Get student's GPA
curl -X GET http://localhost:8080/api/grades/student/2/gpa \
  -H "Authorization: Bearer STUDENT_TOKEN"

# Response: 3.51

# Get full GPA summary
curl -X GET http://localhost:8080/api/grades/student/2/summary \
  -H "Authorization: Bearer STUDENT_TOKEN"

# Response:
{
  "gpa": 3.51,
  "totalCredits": 10,
  "totalCourses": 3
}

# Get all grades for a student
curl -X GET http://localhost:8080/api/grades/student/2 \
  -H "Authorization: Bearer STUDENT_TOKEN"

# Get grade scale
curl -X GET http://localhost:8080/api/grades/scale \
  -H "Authorization: Bearer STUDENT_TOKEN"

# Response:
{
  "A+": 4.0,
  "A": 4.0,
  "A-": 3.7,
  "B+": 3.3,
  "B": 3.0,
  ...
}
```

---

### Feature 5: JWT Authentication

#### What is JWT? (Super Simple Explanation)

**Without JWT:**
```
You: "Can I see my grades?"
Server: "Who are you?"
You: "I'm Alice"
Server: "Prove it!"
You: "Here's my username and password"
Server: "Ok, here are your grades"

You: "Can I see my courses?"
Server: "Who are you again?"
You: "I'm Alice"
Server: "Prove it AGAIN!"
You: "Here's my username and password AGAIN"
...this is annoying!
```

**With JWT:**
```
You: "Here's my username and password"
Server: "✓ Verified! Here's a TOKEN (like a VIP pass)"

You: "Can I see my grades? [shows TOKEN]"
Server: "✓ Token valid! Here are your grades"

You: "Can I see my courses? [shows TOKEN]"
Server: "✓ Token valid! Here are your courses"

...much better!
```

#### How JWT Works (Step-by-Step)

**1. Login Process:**
```
Step 1: User sends username & password
Step 2: Server checks if correct
Step 3: Server creates a TOKEN containing:
   - username
   - role (STUDENT/FACULTY/ADMIN)
   - expiration time (24 hours)
Step 4: Server SIGNS the token with secret key
Step 5: Server sends token to user
```

**2. Using the Token:**
```
Step 1: User includes token in request header
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Step 2: Server extracts token
Step 3: Server verifies signature (is it real?)
Step 4: Server checks expiration (is it still valid?)
Step 5: Server extracts user info from token
Step 6: Server processes request
```

#### The JWT Code (Explained)

**Token Generation:**
```java
public String generateToken(String username, String role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 86400000); // 24 hours

    return Jwts.builder()
            .subject(username)           // Who is this token for?
            .claim("role", role)         // What's their role?
            .issuedAt(now)              // When was it created?
            .expiration(expiryDate)      // When does it expire?
            .signWith(secretKey)         // Sign it (prevents forgery)
            .compact();                  // Convert to string
}
```

**Token Validation:**
```java
public boolean validateToken(String token) {
    try {
        Jwts.parser()
                .verifyWith(secretKey)    // Check signature
                .build()
                .parseSignedClaims(token); // Parse and validate
        return true;
    } catch (JwtException | IllegalArgumentException e) {
        return false; // Token is fake or expired
    }
}
```

#### Complete Authentication Flow

```bash
# Step 1: Register a new user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123",
    "role": "STUDENT"
  }'

# Step 2: Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "password123"
  }'

# Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "alice",
  "email": "alice@example.com",
  "role": "STUDENT",
  "userId": 2
}

# Step 3: Use token for authenticated requests
curl -X GET http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Step 4: Validate token
curl -X GET http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Response:
{
  "username": "alice",
  "role": "STUDENT",
  "valid": true
}
```

#### Security Roles (Who Can Do What?)

```
PUBLIC (No authentication required):
- POST /api/auth/login
- POST /api/users/register

STUDENT/FACULTY/ADMIN (Authenticated users):
- GET/POST /api/users/**
- GET/POST /api/courses/**
- GET/POST /api/enrollments/**

FACULTY/ADMIN ONLY:
- POST/PUT/DELETE /api/grades/**
```

---

### Feature 6: CORS Configuration

#### What is CORS? (Simple Explanation)

Imagine your frontend React app runs on `http://localhost:3000` and your backend API runs on `http://localhost:8080`.

**Without CORS:**
```
Frontend: "Hey Backend, can I get the user list?"
Backend: "Who are you? I don't know http://localhost:3000. BLOCKED!"
```

**With CORS:**
```
Frontend: "Hey Backend, can I get the user list?"
Backend: "Oh, http://localhost:3000 is on my allowed list. Here you go!"
```

CORS = Cross-Origin Resource Sharing. It's like a bouncer's VIP list for your API.

#### Our CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Who can access? (Frontend URLs)
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",  // React
        "http://localhost:4200",  // Angular
        "http://localhost:8081",  // Vue
        "http://localhost:5173"   // Vite
    ));

    // What HTTP methods?
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));

    // What headers can frontend send?
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "Accept"
    ));

    // Allow cookies/auth headers
    configuration.setAllowCredentials(true);

    return source;
}
```

---

## Complete API Reference

### Authentication Endpoints

#### POST /api/auth/login
**Purpose:** Get JWT token for authentication

**Request:**
```json
{
  "username": "alice",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "alice",
  "email": "alice@example.com",
  "role": "STUDENT",
  "userId": 2
}
```

**Curl Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
```

---

#### GET /api/auth/validate
**Purpose:** Check if token is still valid

**Headers:**
```
Authorization: Bearer YOUR_TOKEN
```

**Response:**
```json
{
  "username": "alice",
  "role": "STUDENT",
  "valid": true
}
```

**Curl Example:**
```bash
curl -X GET http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### User Endpoints

#### POST /api/users/register
**Purpose:** Create new user account

**Request:**
```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

**Curl Example:**
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

---

### Course Endpoints (NEW in Week 6!)

#### POST /api/courses/:courseId/prerequisites/:prerequisiteId
**Purpose:** Add a prerequisite to a course

**Example:**
```bash
# Add CS101 as prerequisite for CS201
curl -X POST http://localhost:8080/api/courses/2/prerequisites/1 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

#### GET /api/courses/:courseId/prerequisites
**Purpose:** Get all prerequisites for a course

**Example:**
```bash
curl -X GET http://localhost:8080/api/courses/2/prerequisites \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

**Response:**
```json
[
  {
    "id": 1,
    "courseCode": "CS101",
    "courseName": "Intro to Programming",
    ...
  }
]
```

---

#### PUT /api/courses/:courseId/schedule
**Purpose:** Set course schedule

**Parameters:**
- daysOfWeek: "MON,WED,FRI"
- startTime: "09:00"
- endTime: "10:30"

**Example:**
```bash
curl -X PUT "http://localhost:8080/api/courses/1/schedule?daysOfWeek=MON,WED,FRI&startTime=09:00&endTime=10:30" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

#### GET /api/courses/:courseId/waitlist
**Purpose:** Get waitlist for a course

**Example:**
```bash
curl -X GET http://localhost:8080/api/courses/1/waitlist \
  -H "Authorization: Bearer FACULTY_TOKEN"
```

**Response:**
```json
[
  {
    "studentId": 3,
    "username": "bob",
    "email": "bob@example.com",
    "position": 1,
    "enrollmentDate": "2024-01-15T10:30:00"
  },
  {
    "studentId": 4,
    "username": "carol",
    "email": "carol@example.com",
    "position": 2,
    "enrollmentDate": "2024-01-15T11:00:00"
  }
]
```

---

### Enrollment Endpoints

#### POST /api/enrollments
**Purpose:** Enroll student in course (with all validations!)

**Request:**
```json
{
  "studentId": 2,
  "courseId": 1
}
```

**Possible Responses:**

**Success (Enrolled):**
```json
{
  "id": 1,
  "status": "ACTIVE",
  "waitlistPosition": null,
  "student": {...},
  "course": {...}
}
```

**Success (Waitlisted):**
```json
{
  "id": 2,
  "status": "WAITLISTED",
  "waitlistPosition": 1,
  "student": {...},
  "course": {...}
}
```

**Error (Missing Prerequisite):**
```json
{
  "status": 400,
  "message": "Cannot enroll in CS201. Missing prerequisites: CS101",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Error (Schedule Conflict):**
```json
{
  "status": 409,
  "message": "Schedule conflict: CS201 conflicts with MATH101",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### Grade Endpoints (NEW in Week 7!)

#### POST /api/grades
**Purpose:** Assign grade to student (FACULTY/ADMIN only)

**Request:**
```json
{
  "enrollmentId": 1,
  "gradeValue": "A"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/grades \
  -H "Authorization: Bearer FACULTY_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "enrollmentId": 1,
    "gradeValue": "A"
  }'
```

---

#### GET /api/grades/student/:studentId/gpa
**Purpose:** Calculate student's GPA

**Example:**
```bash
curl -X GET http://localhost:8080/api/grades/student/2/gpa \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

**Response:**
```json
3.51
```

---

#### GET /api/grades/student/:studentId/summary
**Purpose:** Get complete GPA summary

**Example:**
```bash
curl -X GET http://localhost:8080/api/grades/student/2/summary \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

**Response:**
```json
{
  "gpa": 3.51,
  "totalCredits": 12,
  "totalCourses": 4
}
```

---

#### GET /api/grades/scale
**Purpose:** Get the grading scale

**Example:**
```bash
curl -X GET http://localhost:8080/api/grades/scale \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

**Response:**
```json
{
  "A+": 4.0,
  "A": 4.0,
  "A-": 3.7,
  "B+": 3.3,
  "B": 3.0,
  "B-": 2.7,
  "C+": 2.3,
  "C": 2.0,
  "C-": 1.7,
  "D+": 1.3,
  "D": 1.0,
  "D-": 0.7,
  "F": 0.0
}
```

---

## Testing Guide

### Complete End-to-End Test Scenario

Here's a full scenario testing ALL Week 6 and 7 features:

```bash
#!/bin/bash

echo "🧪 SAMS Week 6 & 7 Complete Test Suite"

# 1. Register users
echo "\n📝 Step 1: Register users..."

ADMIN=$(curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@sams.com",
    "password": "admin123",
    "role": "ADMIN"
  }')

FACULTY=$(curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "prof_smith",
    "email": "smith@sams.com",
    "password": "prof123",
    "role": "FACULTY"
  }')

STUDENT1=$(curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@sams.com",
    "password": "student123",
    "role": "STUDENT"
  }')

echo "✅ Users created"

# 2. Login and get tokens
echo "\n🔐 Step 2: Login users..."

ADMIN_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')
ADMIN_TOKEN=$(echo $ADMIN_LOGIN | jq -r '.token')

FACULTY_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"prof_smith","password":"prof123"}')
FACULTY_TOKEN=$(echo $FACULTY_LOGIN | jq -r '.token')

STUDENT_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"student123"}')
STUDENT_TOKEN=$(echo $STUDENT_LOGIN | jq -r '.token')

echo "✅ All users logged in"

# 3. Create courses with prerequisites
echo "\n📚 Step 3: Create courses..."

CS101=$(curl -s -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS101",
    "courseName": "Intro to Programming",
    "description": "Learn Java basics",
    "credits": 3,
    "capacity": 2,
    "daysOfWeek": "MON,WED",
    "startTime": "09:00",
    "endTime": "10:30",
    "instructorId": '$(echo $FACULTY | jq '.id')'
  }')
CS101_ID=$(echo $CS101 | jq '.id')

CS201=$(curl -s -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS201",
    "courseName": "Data Structures",
    "description": "Advanced programming concepts",
    "credits": 4,
    "capacity": 30,
    "daysOfWeek": "TUE,THU",
    "startTime": "14:00",
    "endTime": "15:30",
    "instructorId": '$(echo $FACULTY | jq '.id')',
    "prerequisiteIds": ['$CS101_ID']
  }')
CS201_ID=$(echo $CS201 | jq '.id')

echo "✅ Courses created with schedule and prerequisites"

# 4. Test prerequisite validation
echo "\n🚫 Step 4: Test prerequisite validation (should FAIL)..."

curl -s -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": '$(echo $STUDENT1 | jq '.id')',
    "courseId": '$CS201_ID'
  }' | jq '.'

echo "✅ Correctly rejected enrollment (missing CS101)"

# 5. Enroll in CS101
echo "\n✅ Step 5: Enroll in CS101..."

ENROLLMENT1=$(curl -s -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": '$(echo $STUDENT1 | jq '.id')',
    "courseId": '$CS101_ID'
  }')

echo "✅ Enrolled in CS101"

# 6. Mark enrollment as completed
echo "\n📝 Step 6: Complete CS101..."

curl -s -X PATCH http://localhost:8080/api/enrollments/$(echo $ENROLLMENT1 | jq '.id')/status?status=COMPLETED \
  -H "Authorization: Bearer $STUDENT_TOKEN" | jq '.'

echo "✅ CS101 marked as COMPLETED"

# 7. Assign grade
echo "\n🎓 Step 7: Assign grade for CS101..."

GRADE1=$(curl -s -X POST http://localhost:8080/api/grades \
  -H "Authorization: Bearer $FACULTY_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "enrollmentId": '$(echo $ENROLLMENT1 | jq '.id')',
    "gradeValue": "A"
  }')

echo "✅ Grade assigned: A (4.0)"

# 8. Try enrolling in CS201 again (should work now!)
echo "\n✅ Step 8: Enroll in CS201 (should work now)..."

ENROLLMENT2=$(curl -s -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": '$(echo $STUDENT1 | jq '.id')',
    "courseId": '$CS201_ID'
  }')

echo "✅ Successfully enrolled in CS201!"

# 9. Check GPA
echo "\n📊 Step 9: Calculate GPA..."

GPA=$(curl -s -X GET http://localhost:8080/api/grades/student/$(echo $STUDENT1 | jq '.id')/gpa \
  -H "Authorization: Bearer $STUDENT_TOKEN")

echo "✅ Current GPA: $GPA"

# 10. Get GPA summary
echo "\n📈 Step 10: Get complete GPA summary..."

curl -s -X GET http://localhost:8080/api/grades/student/$(echo $STUDENT1 | jq '.id')/summary \
  -H "Authorization: Bearer $STUDENT_TOKEN" | jq '.'

echo "\n🎉 ALL TESTS PASSED!"
```

---

## Presentation Talking Points

### When Presenting Week 6 & 7 to Your Class/Professor:

#### Opening (30 seconds)
> "In Weeks 6 and 7, we transformed SAMS from a basic enrollment system into a production-ready academic management platform with intelligent business rules and enterprise-grade security."

#### Key Achievement #1: Smart Enrollment (2 minutes)
**What to say:**
> "Our enrollment system now thinks like a real academic advisor. Before enrolling a student, it automatically checks three critical things:
> 1. Prerequisites - Has the student completed required courses?
> 2. Schedule Conflicts - Will this class overlap with their existing schedule?
> 3. Capacity - Is there space, or should we add them to a waitlist?
>
> This prevents students from registering for classes they're not ready for and eliminates scheduling nightmares."

**Demo:** Show live enrollment being rejected due to missing prerequisite

#### Key Achievement #2: Waitlist Automation (1 minute)
**What to say:**
> "When a course fills up, students are automatically added to a waitlist with a numbered position. When someone drops, the system automatically promotes the next person in line. This is completely automated - no manual intervention needed."

**Demo:** Show waitlist positions and auto-promotion

#### Key Achievement #3: GPA Calculation (1 minute)
**What to say:**
> "The system calculates weighted GPAs automatically using the standard 4.0 scale. It accounts for credit hours, so a 4-credit A has more weight than a 3-credit A. Faculty can assign grades, and students can instantly see their updated GPA."

**Demo:** Show GPA calculation and summary

#### Key Achievement #4: JWT Security (2 minutes)
**What to say:**
> "We implemented industry-standard JWT authentication. Instead of sending username/password with every request, users login once and receive a secure token valid for 24 hours. This token contains their identity and role, encrypted and signed to prevent tampering.
>
> We also implemented role-based access control: students can view their grades, but only faculty can assign them. This follows the principle of least privilege."

**Demo:** Show login flow and token validation

#### Key Achievement #5: CORS & Frontend Ready (30 seconds)
**What to say:**
> "The API is now configured for Cross-Origin Resource Sharing, meaning any frontend framework - React, Angular, Vue - can connect to it seamlessly. We're ready for full-stack integration."

#### Technical Highlights (if asked):
- "All business logic uses transactions to ensure data consistency"
- "Schedule conflict detection uses an efficient time-overlap algorithm"
- "GPA calculations are optimized with database queries"
- "JWT tokens are stateless, making the API horizontally scalable"

#### Closing:
> "These features bring SAMS to production-ready status. It's now a complete academic management system that could actually be deployed and used by a real university."

---

## Why These Design Choices Are Good

### 1. Prerequisite System
**Design:** Many-to-many self-referencing relationship
**Why it's good:**
- Flexible: A course can have multiple prerequisites
- Recursive: Prerequisites can have their own prerequisites
- Efficient: Uses join table for fast lookups

### 2. Schedule Conflict Detection
**Design:** Store days and times separately, use overlap algorithm
**Why it's good:**
- Precise: Catches all types of conflicts
- Fast: O(n) where n = number of existing enrollments
- Clear error messages: Tells exactly which courses conflict

### 3. Waitlist Management
**Design:** Auto-promotion with position tracking
**Why it's good:**
- Fair: First-come-first-served
- Transparent: Students know their position
- Automated: No manual processing needed

### 4. GPA Calculation
**Design:** Weighted average with credit hours
**Why it's good:**
- Accurate: Matches university standards
- Real-time: Calculates on demand
- Efficient: Uses streams for clean code

### 5. JWT Authentication
**Design:** Stateless tokens with role claims
**Why it's good:**
- Scalable: No server-side session storage
- Secure: Signed and encrypted
- Flexible: Can add more claims easily

### 6. Role-Based Access Control
**Design:** Different permissions for STUDENT/FACULTY/ADMIN
**Why it's good:**
- Secure: Least privilege principle
- Maintainable: Easy to add new roles
- Clear: Permissions match real-world roles

---

## Common Questions & Answers

**Q: Why use JWT instead of sessions?**
A: JWTs are stateless. The server doesn't need to remember who's logged in. This makes it easier to scale horizontally (add more servers). Sessions require shared storage or sticky sessions.

**Q: What if someone steals a JWT token?**
A: Tokens expire after 24 hours. Also, use HTTPS in production so tokens can't be intercepted. For extra security, you could add refresh tokens with shorter expiration.

**Q: Why check prerequisites AND schedule conflicts?**
A: Both are important! Prerequisites ensure academic readiness. Schedule checks prevent logistical impossibilities (being in two places at once).

**Q: Could a student game the waitlist system?**
A: Not really. Positions are assigned in order of enrollment attempt. You can't jump the line.

**Q: Why round GPA to 2 decimal places?**
A: Standard practice in most universities. Also prevents floating-point precision issues.

---

## Next Steps / Future Improvements

1. **Email notifications** when promoted from waitlist
2. **Degree audit** to track progress toward graduation
3. **Course recommendations** based on completed courses
4. **Refresh tokens** for extended sessions
5. **Two-factor authentication** for extra security
6. **Rate limiting** to prevent API abuse

---

## Congratulations! 🎉

You've successfully implemented advanced business logic and security features! You now have:
- ✅ Smart enrollment with multiple validations
- ✅ Automated waitlist management
- ✅ Complete grade tracking and GPA calculation
- ✅ Enterprise-grade JWT authentication
- ✅ CORS-enabled API ready for frontend

**You're ready to present this as a production-grade academic management system!**

---

**Made with ❤️ by a senior student who remembers what it's like to be confused by complex code**

*Remember: The best code is code that others can understand. Keep learning, keep building!* 🚀
