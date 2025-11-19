# SAMS Data Flow Explanation

## Purpose
This document explains how data flows through the SAMS system for various operations, including read operations, write operations, and complex business processes.

---

## Data Flow Architecture Overview

### System Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  Vue.js Components → Vuex Store → Axios HTTP Client        │
└─────────────────────────────────────────────────────────────┘
                             ↓↑ HTTP/JSON
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
│  Controllers → Security → Services → Repositories           │
└─────────────────────────────────────────────────────────────┘
                             ↓↑ JDBC/SQL
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                            │
│  PostgreSQL Tables → Triggers → Views → Indexes            │
└─────────────────────────────────────────────────────────────┘
```

---

## 1. READ Operations Data Flow

### Example: View Student Grades

#### Step-by-Step Data Flow

**1. User Interaction (Browser)**
```
Student clicks "View My Grades" → Vue component method triggered
```

**2. Frontend Processing (Vue.js)**
```javascript
// GradesView.vue
async fetchGrades() {
  this.loading = true;
  try {
    const response = await api.get(`/api/v1/students/${this.studentId}/grades`);
    this.grades = response.data.data;  // Extract data from ApiResponse wrapper
  } catch (error) {
    this.handleError(error);
  } finally {
    this.loading = false;
  }
}
```

**Data State:**
```javascript
{
  loading: true,
  grades: [],
  error: null
}
```

**3. HTTP Request (Axios)**
```http
GET /api/v1/students/123/grades HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Accept: application/json
```

**4. Security Layer (Spring Security)**
```java
// JwtAuthenticationFilter
1. Extract JWT token from Authorization header
2. Validate token signature and expiration
3. Extract claims: userId=123, username="sarah.johnson", role="STUDENT"
4. Set SecurityContext with UserPrincipal
```

**Security Context:**
```java
{
  principal: UserPrincipal(userId=123, username="sarah.johnson", role=STUDENT),
  authorities: [ROLE_STUDENT]
}
```

**5. Controller Layer (Spring MVC)**
```java
// StudentController.java
@GetMapping("/students/{studentId}/grades")
@PreAuthorize("hasRole('ADMIN') or @studentService.isCurrentUser(#studentId)")
public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGrades(
    @PathVariable Integer studentId
) {
    List<GradeDTO> grades = gradeService.getStudentGrades(studentId);
    return ResponseEntity.ok(ApiResponse.success("Grades retrieved", grades));
}
```

**6. Service Layer (Business Logic)**
```java
// GradeService.java
public List<GradeDTO> getStudentGrades(Integer studentId) {
    // Fetch grades with related data (JOIN FETCH)
    List<Grade> grades = gradeRepository.findByStudentIdWithDetails(studentId);

    // Map entities to DTOs (hide internal fields)
    return grades.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
}
```

**7. Repository Layer (Spring Data JPA)**
```java
// GradeRepository.java
@Query("SELECT g FROM Grade g " +
       "JOIN FETCH g.course c " +
       "JOIN FETCH g.enteredByFaculty f " +
       "JOIN FETCH f.user u " +
       "WHERE g.studentId = :studentId " +
       "ORDER BY g.gradeDate DESC")
List<Grade> findByStudentIdWithDetails(@Param("studentId") Integer studentId);
```

**8. Database Query (PostgreSQL)**
```sql
SELECT
    g.grade_id, g.enrollment_id, g.student_id, g.course_id,
    g.grade_type, g.grade_name, g.grade_value, g.max_points,
    g.weight_percentage, g.grade_date, g.comments,
    c.course_code, c.course_name,
    u.first_name, u.last_name
FROM grades g
INNER JOIN courses c ON g.course_id = c.course_id
INNER JOIN faculty f ON g.entered_by = f.faculty_id
INNER JOIN users u ON f.faculty_id = u.user_id
WHERE g.student_id = 123
ORDER BY g.grade_date DESC;
```

**Query Execution Plan:**
```
Index Scan using idx_grades_student_id on grades  (cost=0.29..8.31 rows=12)
  Index Cond: (student_id = 123)
  -> Nested Loop  (cost=0.43..24.87 rows=12)
       -> Nested Loop  (cost=0.29..16.45 rows=12)
            -> Previous Index Scan
            -> Index Scan using courses_pkey on courses
       -> Index Scan using faculty_pkey on faculty
  -> Index Scan using users_pkey on users
```

**9. Result Mapping (Hibernate)**
```java
// Hibernate maps SQL result set to Grade entities
List<Grade> = [
    Grade(gradeId=1001, courseCode="CS301", gradeName="Homework 1", gradeValue=95.0, ...),
    Grade(gradeId=1002, courseCode="CS301", gradeName="Midterm", gradeValue=88.0, ...),
    ...
]
```

**10. DTO Mapping (Service Layer)**
```java
// Convert entities to DTOs (remove sensitive data, format for API)
List<GradeDTO> = [
    GradeDTO(
        gradeId=1001,
        courseCode="CS301",
        courseName="Data Structures",
        gradeType="ASSIGNMENT",
        gradeName="Homework 1",
        gradeValue=95.0,
        maxPoints=100.0,
        percentage=95.0,
        gradeDate="2024-09-10",
        enteredBy="Dr. Michael Smith"
    ),
    ...
]
```

**11. HTTP Response (Spring MVC)**
```json
{
  "success": true,
  "message": "Grades retrieved",
  "data": [
    {
      "gradeId": 1001,
      "courseCode": "CS301",
      "courseName": "Data Structures",
      "gradeType": "ASSIGNMENT",
      "gradeName": "Homework 1",
      "gradeValue": 95.0,
      "maxPoints": 100.0,
      "percentage": 95.0,
      "gradeDate": "2024-09-10",
      "enteredBy": "Dr. Michael Smith"
    }
  ]
}
```

**12. Frontend Update (Vue.js)**
```javascript
// Response received in Axios interceptor
this.grades = response.data.data;  // Array of grade objects

// Vue reactivity triggers re-render
// GradesTable component updates with new data
```

**13. UI Rendering (Browser)**
```
┌──────────────────────────────────────────────────────┐
│  My Grades                                   GPA: 3.75│
├──────────┬────────────┬──────────┬───────┬───────────┤
│ Course   │ Assignment │ Grade    │ Max   │ Percentage│
├──────────┼────────────┼──────────┼───────┼───────────┤
│ CS301    │ Homework 1 │ 95.0     │ 100.0 │ 95.0%     │
│ CS301    │ Midterm    │ 88.0     │ 100.0 │ 88.0%     │
└──────────┴────────────┴──────────┴───────┴───────────┘
```

### Read Operation Performance

**Total Time Breakdown:**
- Network latency (client → server): ~20ms
- Security processing (JWT validation): ~5ms
- Controller processing: <1ms
- Service layer logic: ~2ms
- Database query execution: ~15ms (with indexes)
- Result mapping (JPA): ~5ms
- DTO conversion: ~3ms
- Network response (server → client): ~20ms
- **Total: ~70ms** (well under 3-second SRS requirement)

---

## 2. WRITE Operations Data Flow

### Example: Enroll in Course

#### Step-by-Step Data Flow

**1. User Interaction**
```
Student clicks "Enroll" → Confirmation dialog → Confirm
```

**2. Frontend Request**
```javascript
// CourseRegistration.vue
async enrollInCourse(courseId) {
  const enrollmentData = {
    studentId: this.currentUser.studentId,
    courseId: courseId
  };

  const response = await api.post('/api/v1/enrollments', enrollmentData);

  if (response.data.success) {
    this.$message.success('Successfully enrolled');
    this.fetchEnrollments();  // Refresh list
  }
}
```

**3. HTTP POST Request**
```http
POST /api/v1/enrollments HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "studentId": 123,
  "courseId": 12
}
```

**4. Controller Processing**
```java
@PostMapping("/enrollments")
public ResponseEntity<ApiResponse<EnrollmentDTO>> enrollInCourse(
    @Valid @RequestBody EnrollmentRequest request
) {
    // @Valid triggers Bean Validation
    // - studentId: required, positive
    // - courseId: required, positive

    EnrollmentDTO enrollment = enrollmentService.registerForCourse(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Enrollment created", enrollment));
}
```

**5. Service Layer - Transaction Begins**
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public EnrollmentDTO registerForCourse(EnrollmentRequest request) {
    // Transaction started: isolation level SERIALIZABLE
    // Prevents phantom reads during concurrent enrollments

    // Validation steps...
    // Create enrollment...
    // Commit transaction
}
```

**6. Database Operations (Sequential in Transaction)**

**6.1 Lock Course Row**
```sql
-- Repository: courseRepository.findByIdForUpdate(courseId)
SELECT * FROM courses
WHERE course_id = 12
FOR UPDATE;  -- Acquires row-level exclusive lock
```

**Row Lock Acquired:**
```
Other transactions attempting to enroll in same course must wait
```

**6.2 Check Prerequisites**
```sql
-- Repository: gradeRepository.findCompletedCoursesByStudent(studentId)
SELECT DISTINCT c.course_code
FROM grades g
INNER JOIN enrollments e ON g.enrollment_id = e.enrollment_id
INNER JOIN courses c ON g.course_id = c.course_id
WHERE g.student_id = 123
  AND e.status = 'COMPLETED'
  AND g.final_grade >= 'D';
```

**Result:**
```
[CS201, CS202]  // Student has completed both prerequisites
```

**6.3 Check Existing Enrollment**
```sql
-- Repository: enrollmentRepository.findByStudentAndCourseAndSemester(...)
SELECT * FROM enrollments
WHERE student_id = 123
  AND course_id = 12
  AND semester = 'Fall'
  AND academic_year = 2024;
```

**Result:**
```
Empty (no existing enrollment) → Proceed
```

**6.4 Check Schedule Conflicts**
```sql
-- Repository: enrollmentRepository.findActiveEnrollmentsBySemester(...)
SELECT e.*, c.schedule_days, c.schedule_time
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = 123
  AND e.semester = 'Fall'
  AND e.academic_year = 2024
  AND e.status = 'ENROLLED';
```

**Application-Level Check:**
```java
// Compare schedule_days and schedule_time for overlaps
Course newCourse: Mon,Wed,Fri 10:00-11:15
Existing Course: Tue,Thu 14:00-15:50
→ No conflict detected
```

**6.5 Determine Enrollment Status**
```java
// Current course state (from locked row)
maxCapacity = 35
currentEnrollment = 28  // → 7 seats available
waitlistCapacity = 10
currentWaitlist = 0

if (currentEnrollment < maxCapacity) {
    status = ENROLLED;
    waitlistPosition = null;
} else if (currentWaitlist < waitlistCapacity) {
    status = WAITLISTED;
    waitlistPosition = currentWaitlist + 1;
} else {
    throw new BusinessRuleException("COURSE_FULL");
}

// Result: status = ENROLLED
```

**6.6 Insert Enrollment**
```sql
INSERT INTO enrollments (
    student_id, course_id, enrollment_date, status,
    semester, academic_year, payment_status, course_access_granted,
    waitlist_position, created_at, updated_at
) VALUES (
    123, 12, NOW(), 'ENROLLED',
    'Fall', 2024, 'PENDING', false,
    NULL, NOW(), NOW()
)
RETURNING enrollment_id;
```

**Database Returns:**
```
enrollment_id = 601
```

**6.7 Trigger Execution (Automatic)**
```sql
-- Trigger: update_course_enrollment_count
-- Fires AFTER INSERT on enrollments

UPDATE courses
SET current_enrollment = (
    SELECT COUNT(*) FROM enrollments
    WHERE course_id = 12 AND status = 'ENROLLED'
),
current_waitlist = (
    SELECT COUNT(*) FROM enrollments
    WHERE course_id = 12 AND status = 'WAITLISTED'
)
WHERE course_id = 12;
```

**Database State After Trigger:**
```
courses.current_enrollment: 28 → 29
courses.current_waitlist: 0 → 0
```

**7. Transaction Commit**
```
COMMIT;  // Release all locks, changes now visible to other transactions
```

**8. Post-Transaction Operations (Async)**

**8.1 Send Notification**
```java
// NotificationService.sendEnrollmentNotification(enrollment)
// Async operation - doesn't block HTTP response

INSERT INTO notifications (
    user_id, notification_type, title, message,
    related_entity_type, related_entity_id,
    is_read, priority, created_at
) VALUES (
    123, 'ENROLLMENT_SUCCESS', 'Course Enrollment Confirmed',
    'You have been successfully enrolled in CS301 - Data Structures',
    'ENROLLMENT', 601,
    false, 'MEDIUM', NOW()
);
```

**9. HTTP Response**
```json
{
  "success": true,
  "message": "Enrollment created successfully",
  "data": {
    "enrollmentId": 601,
    "studentId": 123,
    "courseId": 12,
    "courseCode": "CS301",
    "courseName": "Data Structures",
    "enrollmentDate": "2024-09-15T14:30:00Z",
    "status": "ENROLLED",
    "semester": "Fall",
    "academicYear": 2024,
    "paymentStatus": "PENDING",
    "courseAccessGranted": false
  }
}
```

**10. Frontend Update**
```javascript
// Success response received
this.enrollments.push(response.data.data);  // Add to local state
this.$message.success('Successfully enrolled in CS301');

// Optionally: Refetch enrollments to get latest state
await this.fetchEnrollments();
```

**11. UI Update**
```
┌────────────────────────────────────────────────────┐
│  My Enrollments - Fall 2024                        │
├──────────┬────────────────────────────┬────────────┤
│ Code     │ Course Name                │ Status     │
├──────────┼────────────────────────────┼────────────┤
│ CS301    │ Data Structures            │ ✓ Enrolled │  ← New
│ MATH210  │ Calculus II                │ ✓ Enrolled │
└──────────┴────────────────────────────┴────────────┘
```

### Write Operation Performance

**Total Time Breakdown:**
- Network latency (request): ~20ms
- Security/validation: ~5ms
- Service business logic: ~10ms
- **Database transaction**: ~40ms
  - Row lock acquisition: ~2ms
  - Prerequisite check: ~5ms
  - Schedule conflict check: ~8ms
  - Enrollment insert: ~3ms
  - Trigger execution: ~5ms
  - Commit: ~2ms
- DTO mapping: ~2ms
- Network latency (response): ~20ms
- **Total: ~97ms**

**Async Operations (non-blocking):**
- Notification insert: ~15ms (runs after response)

---

## 3. Complex Data Flow: GPA Calculation

### Triggered By: Grade Entry or Enrollment Completion

#### Data Flow Diagram

```
Grade Entry
     ↓
[GradeService.createGrade()]
     ↓
[Save Grade to DB]
     ↓
[Trigger: recalculateStudentGPA(studentId)] - ASYNC
     ↓
┌───────────────────────────────────────────┐
│  GPA Calculation Service                  │
├───────────────────────────────────────────┤
│ 1. Fetch all student's completed courses │
│ 2. Group by semester                      │
│ 3. Calculate semester GPAs                │
│ 4. Calculate cumulative GPA               │
│ 5. Update students table                  │
└───────────────────────────────────────────┘
     ↓
[UPDATE students SET cumulative_gpa = ?, semester_gpa = ?]
     ↓
[Database State Updated]
     ↓
[Student sees updated GPA on next dashboard load]
```

#### Calculation Logic

**1. Fetch Completed Grades**
```sql
SELECT
    e.enrollment_id,
    c.course_code,
    c.credits,
    e.final_grade,
    e.grade_points,
    e.semester,
    e.academic_year
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = 123
  AND e.status = 'COMPLETED'
  AND e.final_grade IS NOT NULL
ORDER BY e.academic_year DESC, e.semester DESC;
```

**Result Set:**
```
[
  {courseCode: "CS301", credits: 3, gradePoints: 4.0, semester: "Fall", year: 2024},
  {courseCode: "MATH210", credits: 4, gradePoints: 3.7, semester: "Fall", year: 2024},
  {courseCode: "ENG101", credits: 3, gradePoints: 3.3, semester: "Spring", year: 2024},
  ...
]
```

**2. Calculate Current Semester GPA**
```java
// Filter for current semester (Fall 2024)
List<Enrollment> currentSemester = enrollments.stream()
    .filter(e -> e.getSemester().equals("Fall") && e.getAcademicYear() == 2024)
    .collect(Collectors.toList());

// Weighted average
double totalPoints = 0.0;
double totalCredits = 0.0;

for (Enrollment e : currentSemester) {
    totalPoints += (e.getGradePoints() * e.getCourse().getCredits());
    totalCredits += e.getCourse().getCredits();
}

double semesterGpa = totalPoints / totalCredits;
// Result: (4.0*3 + 3.7*4) / (3+4) = 26.8 / 7 = 3.83
```

**3. Calculate Cumulative GPA**
```java
// All completed courses
double allPoints = 0.0;
double allCredits = 0.0;

for (Enrollment e : enrollments) {
    allPoints += (e.getGradePoints() * e.getCourse().getCredits());
    allCredits += e.getCourse().getCredits();
}

double cumulativeGpa = allPoints / allCredits;
// Example: 90.0 / 24 = 3.75
```

**4. Update Student Record**
```sql
UPDATE students
SET cumulative_gpa = 3.75,
    semester_gpa = 3.83,
    total_credits_completed = 24,
    updated_at = NOW()
WHERE student_id = 123;
```

**5. Determine Academic Standing**
```java
String academicStanding;
if (cumulativeGpa >= 3.5) {
    academicStanding = "DEAN_LIST";
} else if (cumulativeGpa >= 2.0) {
    academicStanding = "GOOD_STANDING";
} else if (cumulativeGpa >= 1.5) {
    academicStanding = "PROBATION";
} else {
    academicStanding = "ACADEMIC_SUSPENSION";
}

// Update: academicStanding = "DEAN_LIST" (GPA >= 3.5)
```

### Why GPA Stored vs Calculated On-Demand?

**Design Decision: Denormalization for Performance**

**Option A: Calculate on Every Request (Normalized)**
```
Pros:
- Always accurate (no sync issues)
- Truly 3NF

Cons:
- Expensive JOIN and SUM queries
- Slow dashboard load (calculates GPA every time)
- NFR-1.1 violated (page load > 3 seconds for large result sets)
```

**Option B: Store Calculated GPA (Denormalized) ← CHOSEN**
```
Pros:
- Fast retrieval (SELECT cumulative_gpa FROM students WHERE student_id = ?)
- Dashboard loads instantly
- Meets NFR-1.1 (< 3 second response)

Cons:
- Redundant data (can be calculated from grades)
- Must maintain consistency (triggers/async jobs)
```

**Consistency Maintained By:**
1. Async recalculation when grade entered
2. Batch recalculation job (nightly)
3. Validation checks on critical operations

---

## 4. Data Consistency Patterns

### Pattern 1: Optimistic Locking

**Use Case:** Update student profile

```java
@Entity
public class Student {
    @Version
    private Long version;  // Hibernate optimistic locking
}

// Service layer
public void updateStudent(Integer id, UpdateRequest request) {
    Student student = studentRepository.findById(id).orElseThrow();

    // Modify student
    student.setPhoneNumber(request.getPhoneNumber());

    try {
        studentRepository.save(student);
    } catch (OptimisticLockingFailureException ex) {
        throw new ConcurrentModificationException(
            "Student record was modified by another user. Please refresh and try again."
        );
    }
}
```

**Data Flow:**
1. Read: `SELECT *, version FROM students WHERE student_id = 123` (version = 5)
2. User modifies data in UI
3. Save: `UPDATE students SET phone = ?, version = 6 WHERE student_id = 123 AND version = 5`
4. If version mismatch (someone else updated): Rollback, show error
5. If successful: Commit, version now 6

---

### Pattern 2: Pessimistic Locking

**Use Case:** Course enrollment (critical section)

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM Course c WHERE c.courseId = :courseId")
Optional<Course> findByIdForUpdate(@Param("courseId") Integer courseId);
```

**Data Flow:**
1. Transaction starts
2. `SELECT * FROM courses WHERE course_id = 12 FOR UPDATE`
   - Row locked (exclusive lock)
   - Other transactions wait
3. Check capacity: `currentEnrollment < maxCapacity`
4. Insert enrollment
5. Trigger updates `currentEnrollment`
6. Commit (lock released)

**Without Lock (Race Condition):**
```
Time | Transaction A              | Transaction B
-----|---------------------------|---------------------------
T1   | Read: currentEnroll = 34  |
T2   |                           | Read: currentEnroll = 34
T3   | Check: 34 < 35 ✓          |
T4   |                           | Check: 34 < 35 ✓
T5   | Insert enrollment (35th)  |
T6   |                           | Insert enrollment (36th!) ← OVERBOOKING
```

**With Lock (Prevented):**
```
Time | Transaction A              | Transaction B
-----|---------------------------|---------------------------
T1   | Acquire lock              | Wait for lock...
T2   | Read: currentEnroll = 34  |
T3   | Insert enrollment (35th)  |
T4   | Commit, release lock      |
T5   |                           | Acquire lock
T6   |                           | Read: currentEnroll = 35
T7   |                           | Check: 35 < 35 ✗ FULL
T8   |                           | Add to waitlist
```

---

## 5. Caching Strategy Data Flow

### Application-Level Caching (Spring Cache)

**Cacheable Read:**
```java
@Cacheable(value = "courses", key = "#courseId")
public CourseDTO getCourseById(Integer courseId) {
    // Only executes if not in cache
    Course course = courseRepository.findById(courseId).orElseThrow();
    return mapToDTO(course);
}
```

**Data Flow:**
1. First Request: `getCourseById(12)`
   - Check cache: MISS
   - Query database: `SELECT * FROM courses WHERE course_id = 12`
   - Store in cache: `cache["courses::12"] = CourseDTO`
   - Return CourseDTO

2. Subsequent Requests: `getCourseById(12)`
   - Check cache: HIT
   - Return cached CourseDTO (no database query)

**Cache Invalidation:**
```java
@CacheEvict(value = "courses", key = "#courseId")
public void updateCourse(Integer courseId, UpdateRequest request) {
    // Update course...
    // Cache entry automatically removed
}
```

---

## Data Flow Summary

| Operation Type | Layers Traversed | Database Operations | Response Time (avg) |
|----------------|------------------|---------------------|---------------------|
| Simple Read | 6 (Vue → Controller → Service → Repo → DB) | 1 SELECT | ~70ms |
| Complex Read (JOIN) | 6 | 1 SELECT with JOINs | ~85ms |
| Simple Write | 6 + Async | 1 INSERT + Trigger | ~100ms |
| Complex Write (Transaction) | 6 + Async | Multiple SELECTs + INSERT + UPDATE | ~150ms |
| Cached Read | 4 (Vue → Controller → Service → Cache) | 0 (cache hit) | ~20ms |

**All operations meet SRS NFR-1.1 requirement: < 3 seconds**

---

**Document Status:** Complete
**Data Flow Patterns:** Read, Write, Complex Transaction, Caching, Consistency
**Next Step:** Technology choices justification
