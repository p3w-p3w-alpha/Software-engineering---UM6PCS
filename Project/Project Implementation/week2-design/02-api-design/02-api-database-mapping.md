# API-to-Database Mapping

## Purpose
This document maps each API endpoint to its corresponding database queries, showing how the Spring Boot application interacts with PostgreSQL.

---

## Mapping Overview

For each major endpoint, we document:
1. **API Endpoint** - HTTP method and URL
2. **Database Tables** - Tables involved in the query
3. **SQL Query** - Representative SQL (what JPA generates)
4. **JPA Repository Method** - Spring Data JPA method used
5. **Performance Considerations** - Indexes, joins, optimization notes

---

## 1. Authentication Endpoints

### POST /api/v1/auth/login

**Purpose:** Authenticate user and return JWT token

**Tables Used:** `users`

**SQL Query:**
```sql
SELECT user_id, username, email, password_hash, role, first_name, last_name, is_active
FROM users
WHERE username = ?
  AND is_active = true;
```

**JPA Repository Method:**
```java
Optional<User> findByUsernameAndIsActiveTrue(String username);
```

**Spring Service Logic:**
```java
// UserService.java
public LoginResponse authenticateUser(LoginRequest request) {
    // Find user by username
    User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
        .orElseThrow(() -> new InvalidCredentialsException());

    // Verify password with BCrypt
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        throw new InvalidCredentialsException();
    }

    // Generate JWT token
    String token = jwtTokenProvider.createToken(user.getUserId(), user.getUsername(), user.getRole());

    // Update last_login timestamp
    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    return new LoginResponse(token, user);
}
```

**Performance Notes:**
- Index on `username` (already created as UNIQUE constraint)
- BCrypt password verification is CPU-intensive (expected)
- last_login update is asynchronous (doesn't block response)

**SRS Requirements:** FR-1.1

---

## 2. User Management Endpoints

### GET /api/v1/users/me

**Purpose:** Get current user's complete profile

**Tables Used:** `users`, `students` OR `faculty` (depending on role)

**SQL Query (Student):**
```sql
SELECT u.*, s.*
FROM users u
LEFT JOIN students s ON u.user_id = s.student_id
WHERE u.user_id = ?;
```

**SQL Query (Faculty):**
```sql
SELECT u.*, f.*
FROM users u
LEFT JOIN faculty f ON u.user_id = f.faculty_id
WHERE u.user_id = ?;
```

**JPA Repository Method:**
```java
// UserRepository.java
Optional<User> findById(Integer userId);

// StudentRepository.java
Optional<Student> findByStudentId(Integer studentId);

// FacultyRepository.java
Optional<Faculty> findByFacultyId(Integer facultyId);
```

**Spring Service Logic:**
```java
// UserService.java
public UserProfileResponse getCurrentUserProfile(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));

    UserProfileResponse response = new UserProfileResponse(user);

    // Fetch role-specific profile
    if (user.getRole() == UserRole.STUDENT) {
        Student student = studentRepository.findByStudentId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Student", userId));
        response.setStudentProfile(student);
    } else if (user.getRole() == UserRole.FACULTY) {
        Faculty faculty = facultyRepository.findByFacultyId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Faculty", userId));
        response.setFacultyProfile(faculty);
    }

    return response;
}
```

**Performance Notes:**
- Primary key lookups are very fast (indexed)
- LEFT JOIN ensures user record always returned even if profile missing
- Alternative: Use `@OneToOne(fetch = FetchType.LAZY)` in User entity

**SRS Requirements:** FR-2.1, FR-3.1

---

## 3. Student Endpoints

### GET /api/v1/students/{studentId}/enrollments

**Purpose:** Get all enrollments for a student

**Tables Used:** `enrollments`, `courses`, `users` (for instructor name)

**SQL Query:**
```sql
SELECT
    e.enrollment_id, e.student_id, e.course_id, e.enrollment_date,
    e.status, e.payment_status, e.course_access_granted,
    e.semester, e.academic_year,
    c.course_code, c.course_name, c.credits,
    c.schedule_days, c.schedule_time, c.room_location,
    CONCAT(u.first_name, ' ', u.last_name) AS instructor_name
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
INNER JOIN faculty f ON c.instructor_id = f.faculty_id
INNER JOIN users u ON f.faculty_id = u.user_id
WHERE e.student_id = ?
  AND e.status IN ('ENROLLED', 'WAITLISTED')
ORDER BY e.enrollment_date DESC;
```

**JPA Repository Method:**
```java
// EnrollmentRepository.java
@Query("SELECT e FROM Enrollment e " +
       "JOIN FETCH e.course c " +
       "JOIN FETCH c.instructor i " +
       "JOIN FETCH i.user u " +
       "WHERE e.studentId = :studentId " +
       "AND e.status IN :statuses " +
       "ORDER BY e.enrollmentDate DESC")
List<Enrollment> findByStudentIdAndStatusIn(
    @Param("studentId") Integer studentId,
    @Param("statuses") List<EnrollmentStatus> statuses
);
```

**Performance Notes:**
- Index on `enrollments.student_id` (created as FK index)
- JOIN FETCH prevents N+1 query problem (fetches related data in one query)
- Filter by status reduces result set

**SRS Requirements:** FR-2.1, FR-2.2

---

### GET /api/v1/students/{studentId}/grades

**Purpose:** Get all grades for a student

**Tables Used:** `grades`, `enrollments`, `courses`, `users` (faculty name)

**SQL Query:**
```sql
SELECT
    g.grade_id, g.enrollment_id, g.student_id, g.course_id,
    g.grade_type, g.grade_name, g.grade_value, g.max_points,
    g.weight_percentage, g.grade_date, g.comments,
    c.course_code, c.course_name,
    CONCAT(u.first_name, ' ', u.last_name) AS entered_by_name
FROM grades g
INNER JOIN courses c ON g.course_id = c.course_id
INNER JOIN faculty f ON g.entered_by = f.faculty_id
INNER JOIN users u ON f.faculty_id = u.user_id
WHERE g.student_id = ?
ORDER BY g.grade_date DESC, g.created_at DESC;
```

**JPA Repository Method:**
```java
// GradeRepository.java
@Query("SELECT g FROM Grade g " +
       "JOIN FETCH g.course c " +
       "JOIN FETCH g.enteredByFaculty f " +
       "JOIN FETCH f.user u " +
       "WHERE g.studentId = :studentId " +
       "ORDER BY g.gradeDate DESC, g.createdAt DESC")
List<Grade> findByStudentIdWithDetails(@Param("studentId") Integer studentId);
```

**Performance Notes:**
- Denormalized `student_id` and `course_id` in grades table eliminates extra JOIN to enrollments
- Index on `grades.student_id` for fast filtering
- JOIN FETCH loads related data efficiently

**SRS Requirements:** FR-2.5

---

### GET /api/v1/students/{studentId}/transcript

**Purpose:** Generate academic transcript

**Tables Used:** `students`, `enrollments`, `courses`, `grades`

**SQL Query (Complex Multi-Step):**
```sql
-- Step 1: Get student info
SELECT s.*, u.first_name, u.last_name, u.email
FROM students s
INNER JOIN users u ON s.student_id = u.user_id
WHERE s.student_id = ?;

-- Step 2: Get completed courses grouped by semester
SELECT
    e.semester,
    e.academic_year,
    c.course_code,
    c.course_name,
    c.credits,
    e.final_grade,
    e.grade_points,
    CONCAT(uf.first_name, ' ', uf.last_name) AS instructor_name
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
INNER JOIN faculty f ON c.instructor_id = f.faculty_id
INNER JOIN users uf ON f.faculty_id = uf.user_id
WHERE e.student_id = ?
  AND e.status = 'COMPLETED'
  AND e.final_grade IS NOT NULL
ORDER BY
    e.academic_year ASC,
    CASE e.semester
        WHEN 'Spring' THEN 1
        WHEN 'Summer' THEN 2
        WHEN 'Fall' THEN 3
    END,
    c.course_code ASC;

-- Step 3: Calculate semester GPAs
SELECT
    e.semester,
    e.academic_year,
    SUM(c.credits) AS semester_credits,
    SUM(e.grade_points * c.credits) / SUM(c.credits) AS semester_gpa
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = ?
  AND e.status = 'COMPLETED'
  AND e.final_grade IS NOT NULL
GROUP BY e.semester, e.academic_year
ORDER BY e.academic_year, e.semester;
```

**JPA Repository Methods:**
```java
// StudentRepository.java
@Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.studentId = :studentId")
Optional<Student> findByIdWithUser(@Param("studentId") Integer studentId);

// EnrollmentRepository.java
@Query("SELECT e FROM Enrollment e " +
       "JOIN FETCH e.course c " +
       "JOIN FETCH c.instructor i " +
       "JOIN FETCH i.user u " +
       "WHERE e.studentId = :studentId " +
       "AND e.status = 'COMPLETED' " +
       "AND e.finalGrade IS NOT NULL " +
       "ORDER BY e.academicYear ASC, e.semester ASC, c.courseCode ASC")
List<Enrollment> findCompletedEnrollmentsByStudentId(@Param("studentId") Integer studentId);
```

**Spring Service Logic:**
```java
// TranscriptService.java
public TranscriptResponse generateTranscript(Integer studentId) {
    // Get student info
    Student student = studentRepository.findByIdWithUser(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

    // Get completed enrollments
    List<Enrollment> completedEnrollments =
        enrollmentRepository.findCompletedEnrollmentsByStudentId(studentId);

    // Group by semester
    Map<String, List<Enrollment>> bySemester = completedEnrollments.stream()
        .collect(Collectors.groupingBy(e -> e.getSemester() + " " + e.getAcademicYear()));

    // Calculate semester GPAs
    List<SemesterRecord> semesterRecords = bySemester.entrySet().stream()
        .map(entry -> calculateSemesterGpa(entry.getValue()))
        .collect(Collectors.toList());

    return new TranscriptResponse(student, semesterRecords);
}
```

**Performance Notes:**
- Complex query broken into smaller JPA queries (easier to maintain)
- Application-level grouping and calculation (flexible logic)
- Alternative: Create database VIEW `student_transcript` for pre-computed results
- Caching recommended for transcripts (rarely changes, frequently accessed)

**SRS Requirements:** FR-2.5

---

## 4. Course Endpoints

### GET /api/v1/courses (with filters)

**Purpose:** Search and filter courses

**Tables Used:** `courses`, `faculty`, `users`

**SQL Query (with all filters):**
```sql
SELECT
    c.*,
    CONCAT(u.first_name, ' ', u.last_name) AS instructor_name,
    (c.max_capacity - c.current_enrollment) AS available_seats
FROM courses c
LEFT JOIN faculty f ON c.instructor_id = f.faculty_id
LEFT JOIN users u ON f.faculty_id = u.user_id
WHERE c.is_active = true
  AND c.department = ? -- filter: department
  AND c.semester = ?   -- filter: semester
  AND c.academic_year = ? -- filter: academicYear
  AND (c.course_code ILIKE ? OR c.course_name ILIKE ?) -- filter: search
  AND c.registration_open = ? -- filter: registrationOpen
  AND (c.max_capacity - c.current_enrollment) > 0 -- filter: availableSeats
ORDER BY c.course_code ASC
LIMIT 20 OFFSET 0; -- pagination
```

**JPA Repository Method (Spring Data JPA Specification):**
```java
// CourseRepository.java
public interface CourseRepository extends JpaRepository<Course, Integer>,
                                          JpaSpecificationExecutor<Course> {
    // Dynamic queries built using Specifications
}

// CourseSpecifications.java (helper class)
public class CourseSpecifications {
    public static Specification<Course> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }

    public static Specification<Course> hasDepartment(String department) {
        return (root, query, cb) ->
            department == null ? null : cb.equal(root.get("department"), department);
    }

    public static Specification<Course> hasSemester(String semester) {
        return (root, query, cb) ->
            semester == null ? null : cb.equal(root.get("semester"), semester);
    }

    public static Specification<Course> hasAvailableSeats() {
        return (root, query, cb) ->
            cb.greaterThan(
                cb.diff(root.get("maxCapacity"), root.get("currentEnrollment")),
                0
            );
    }

    public static Specification<Course> searchByKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("courseCode")), pattern),
                cb.like(cb.lower(root.get("courseName")), pattern)
            );
        };
    }
}

// CourseService.java
public Page<Course> searchCourses(CourseSearchRequest request, Pageable pageable) {
    Specification<Course> spec = Specification.where(CourseSpecifications.isActive())
        .and(CourseSpecifications.hasDepartment(request.getDepartment()))
        .and(CourseSpecifications.hasSemester(request.getSemester()))
        .and(CourseSpecifications.hasAvailableSeats())
        .and(CourseSpecifications.searchByKeyword(request.getSearch()));

    return courseRepository.findAll(spec, pageable);
}
```

**Performance Notes:**
- Indexes on: `department`, `semester`, `academic_year`, `is_active`, `registration_open`
- Composite index on `(semester, academic_year, is_active)` for common query pattern
- Denormalized `current_enrollment` eliminates COUNT(*) subquery
- Pagination limits result set (performance critical with 1000+ courses)

**SRS Requirements:** FR-2.2

---

### POST /api/v1/courses

**Purpose:** Create new course offering

**Tables Used:** `courses`

**SQL Query:**
```sql
INSERT INTO courses (
    course_code, course_name, description, credits, department,
    instructor_id, semester, academic_year, schedule_days, schedule_time,
    room_location, max_capacity, current_enrollment, waitlist_capacity,
    current_waitlist, prerequisites, course_fee, is_active, registration_open,
    created_at, updated_at
) VALUES (
    ?, ?, ?, ?, ?,
    ?, ?, ?, ?, ?,
    ?, ?, 0, ?,
    0, ?, ?, true, false,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
)
RETURNING course_id;
```

**JPA Repository Method:**
```java
// CourseRepository.java
// Standard JPA save method (auto-generated)
Course save(Course course);
```

**Spring Service Logic:**
```java
// CourseService.java
@Transactional
public CourseResponse createCourse(CreateCourseRequest request, Integer authenticatedUserId) {
    // Authorization check: faculty can only create courses in their department
    if (authenticatedUserRole == UserRole.FACULTY) {
        Faculty faculty = facultyRepository.findByFacultyId(authenticatedUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Faculty", authenticatedUserId));

        if (!faculty.getDepartment().equals(request.getDepartment())) {
            throw new InsufficientPermissionsException("Can only create courses in your department");
        }
    }

    // Verify instructor exists
    Faculty instructor = facultyRepository.findByFacultyId(request.getInstructorId())
        .orElseThrow(() -> new ResourceNotFoundException("Instructor", request.getInstructorId()));

    // Check for duplicate course code in same semester
    boolean exists = courseRepository.existsByCourseCodeAndSemesterAndAcademicYear(
        request.getCourseCode(), request.getSemester(), request.getAcademicYear()
    );
    if (exists) {
        throw new DuplicateResourceException("Course code already exists for this semester");
    }

    // Create course entity
    Course course = new Course();
    course.setCourseCode(request.getCourseCode());
    course.setCourseName(request.getCourseName());
    // ... set other fields
    course.setCurrentEnrollment(0);
    course.setCurrentWaitlist(0);
    course.setIsActive(true);
    course.setRegistrationOpen(false); // admin must manually open registration

    // Save to database
    Course savedCourse = courseRepository.save(course);

    return new CourseResponse(savedCourse);
}
```

**Performance Notes:**
- UNIQUE constraint on `(course_code, semester, academic_year)` prevents duplicates
- Auto-increment primary key (`course_id SERIAL`)
- Default values set for `current_enrollment`, `current_waitlist`

**SRS Requirements:** FR-3.1

---

## 5. Enrollment Endpoints

### POST /api/v1/enrollments (Course Registration)

**Purpose:** Enroll student in course (complex business logic)

**Tables Used:** `enrollments`, `courses`, `students`, `grades` (for prerequisite check)

**SQL Queries (Multi-Step Transaction):**
```sql
-- Step 1: Verify student exists
SELECT student_id FROM students WHERE student_id = ?;

-- Step 2: Verify course exists and is active
SELECT course_id, max_capacity, current_enrollment, waitlist_capacity,
       current_waitlist, prerequisites, registration_open
FROM courses
WHERE course_id = ? AND is_active = true
FOR UPDATE; -- lock row for concurrent enrollment handling

-- Step 3: Check for existing enrollment
SELECT enrollment_id, status
FROM enrollments
WHERE student_id = ? AND course_id = ?
  AND semester = ? AND academic_year = ?;

-- Step 4: Verify prerequisites (if any)
SELECT c.course_code, e.final_grade
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = ?
  AND c.course_code IN ('CS201', 'CS202') -- parsed from prerequisites field
  AND e.status = 'COMPLETED'
  AND e.final_grade >= 'D'; -- minimum passing grade

-- Step 5: Check for schedule conflicts
SELECT e.enrollment_id, c.course_code, c.schedule_days, c.schedule_time
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = ?
  AND e.semester = ?
  AND e.academic_year = ?
  AND e.status = 'ENROLLED'
  AND c.schedule_days IS NOT NULL
  AND c.schedule_time IS NOT NULL;
-- Application logic checks for day/time overlaps

-- Step 6: Insert enrollment
INSERT INTO enrollments (
    student_id, course_id, enrollment_date, status,
    semester, academic_year, payment_status, course_access_granted,
    waitlist_position, created_at, updated_at
) VALUES (
    ?, ?, CURRENT_TIMESTAMP, ?, -- status: ENROLLED or WAITLISTED
    ?, ?, 'PENDING', false,
    ?, -- waitlist_position: NULL if enrolled, position number if waitlisted
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
)
RETURNING enrollment_id;

-- Step 7: Trigger automatically updates courses.current_enrollment or current_waitlist
-- (handled by database trigger update_course_enrollment_count)
```

**JPA Repository Methods:**
```java
// EnrollmentRepository.java
@Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId " +
       "AND e.courseId = :courseId AND e.semester = :semester AND e.academicYear = :year")
Optional<Enrollment> findByStudentAndCourseAndSemester(
    @Param("studentId") Integer studentId,
    @Param("courseId") Integer courseId,
    @Param("semester") String semester,
    @Param("year") Integer year
);

@Query("SELECT e FROM Enrollment e JOIN FETCH e.course c " +
       "WHERE e.studentId = :studentId AND e.semester = :semester " +
       "AND e.academicYear = :year AND e.status = 'ENROLLED'")
List<Enrollment> findActiveEnrollmentsBySemester(
    @Param("studentId") Integer studentId,
    @Param("semester") String semester,
    @Param("year") Integer year
);

// CourseRepository.java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM Course c WHERE c.courseId = :courseId")
Optional<Course> findByIdForUpdate(@Param("courseId") Integer courseId);

// GradeRepository.java
@Query("SELECT g.course.courseCode, g.finalGrade FROM Grade g " +
       "WHERE g.studentId = :studentId AND g.course.courseCode IN :courseCodes " +
       "AND g.enrollment.status = 'COMPLETED'")
List<Object[]> findCompletedCourseGrades(
    @Param("studentId") Integer studentId,
    @Param("courseCodes") List<String> courseCodes
);
```

**Spring Service Logic:**
```java
// EnrollmentService.java
@Transactional(isolation = Isolation.SERIALIZABLE)
public EnrollmentResponse registerForCourse(EnrollmentRequest request) {
    // 1. Verify student exists
    Student student = studentRepository.findById(request.getStudentId())
        .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

    // 2. Lock course row and verify it exists
    Course course = courseRepository.findByIdForUpdate(request.getCourseId())
        .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

    if (!course.getIsActive() || !course.getRegistrationOpen()) {
        throw new BusinessRuleException("REGISTRATION_CLOSED", "Registration is not open for this course");
    }

    // 3. Check for duplicate enrollment
    Optional<Enrollment> existing = enrollmentRepository.findByStudentAndCourseAndSemester(
        request.getStudentId(), request.getCourseId(), course.getSemester(), course.getAcademicYear()
    );
    if (existing.isPresent()) {
        throw new BusinessRuleException("ALREADY_ENROLLED", "Student already enrolled in this course");
    }

    // 4. Verify prerequisites
    if (course.getPrerequisites() != null && !course.getPrerequisites().isEmpty()) {
        List<String> requiredCourses = Arrays.asList(course.getPrerequisites().split(","));
        List<Object[]> completedGrades = gradeRepository.findCompletedCourseGrades(
            request.getStudentId(), requiredCourses
        );

        if (completedGrades.size() < requiredCourses.size()) {
            List<String> missing = new ArrayList<>(requiredCourses);
            completedGrades.forEach(g -> missing.remove((String) g[0]));
            throw new BusinessRuleException("PREREQUISITES_NOT_MET",
                "Missing prerequisites: " + String.join(", ", missing));
        }
    }

    // 5. Check schedule conflicts
    List<Enrollment> activeEnrollments = enrollmentRepository.findActiveEnrollmentsBySemester(
        request.getStudentId(), course.getSemester(), course.getAcademicYear()
    );

    for (Enrollment e : activeEnrollments) {
        if (hasScheduleConflict(course, e.getCourse())) {
            throw new BusinessRuleException("SCHEDULE_CONFLICT",
                "Schedule conflict with " + e.getCourse().getCourseCode());
        }
    }

    // 6. Determine enrollment status (ENROLLED vs WAITLISTED)
    EnrollmentStatus status;
    Integer waitlistPosition = null;

    if (course.getCurrentEnrollment() < course.getMaxCapacity()) {
        status = EnrollmentStatus.ENROLLED;
    } else if (course.getCurrentWaitlist() < course.getWaitlistCapacity()) {
        status = EnrollmentStatus.WAITLISTED;
        waitlistPosition = course.getCurrentWaitlist() + 1;
    } else {
        throw new BusinessRuleException("COURSE_FULL", "Course and waitlist are both full");
    }

    // 7. Create enrollment
    Enrollment enrollment = new Enrollment();
    enrollment.setStudentId(request.getStudentId());
    enrollment.setCourseId(request.getCourseId());
    enrollment.setEnrollmentDate(LocalDateTime.now());
    enrollment.setStatus(status);
    enrollment.setSemester(course.getSemester());
    enrollment.setAcademicYear(course.getAcademicYear());
    enrollment.setPaymentStatus(PaymentStatus.PENDING);
    enrollment.setCourseAccessGranted(false);
    enrollment.setWaitlistPosition(waitlistPosition);

    Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

    // 8. Database trigger updates course.current_enrollment or course.current_waitlist

    // 9. Send notification to student
    notificationService.sendEnrollmentNotification(savedEnrollment);

    return new EnrollmentResponse(savedEnrollment);
}

private boolean hasScheduleConflict(Course newCourse, Course existingCourse) {
    // Logic to check if schedule days/times overlap
    // (implementation details omitted for brevity)
    return false; // placeholder
}
```

**Performance Notes:**
- **CRITICAL:** Row-level lock on `courses` table (`FOR UPDATE`) prevents race conditions during concurrent enrollment
- Transaction isolation level SERIALIZABLE ensures consistency
- Denormalized `current_enrollment` and `current_waitlist` maintained by trigger
- Prerequisites stored as comma-separated string (acceptable for simple cases)
  - Alternative: Normalize to `course_prerequisites` junction table if complex AND/OR logic needed
- Schedule conflict check done in application layer (flexible logic)

**SRS Requirements:** FR-2.2, FR-5.1, Business Rules BR-1, BR-2, BR-3

---

## 6. Grade Endpoints

### GET /api/v1/grades/calculate/{enrollmentId}

**Purpose:** Calculate weighted final grade for a course

**Tables Used:** `grades`, `enrollments`, `courses`

**SQL Query:**
```sql
-- Get all grades for enrollment
SELECT
    grade_type,
    grade_value,
    max_points,
    weight_percentage
FROM grades
WHERE enrollment_id = ?;

-- Get enrollment and course info
SELECT
    e.enrollment_id,
    e.student_id,
    c.course_id,
    c.course_code,
    c.course_name,
    c.credits
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.enrollment_id = ?;
```

**JPA Repository Method:**
```java
// GradeRepository.java
List<Grade> findByEnrollmentId(Integer enrollmentId);

// EnrollmentRepository.java
@Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.enrollmentId = :enrollmentId")
Optional<Enrollment> findByIdWithCourse(@Param("enrollmentId") Integer enrollmentId);
```

**Spring Service Logic:**
```java
// GradeCalculationService.java
public CourseGradeCalculationResponse calculateFinalGrade(Integer enrollmentId) {
    // Get enrollment with course info
    Enrollment enrollment = enrollmentRepository.findByIdWithCourse(enrollmentId)
        .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

    // Get all grades for this enrollment
    List<Grade> grades = gradeRepository.findByEnrollmentId(enrollmentId);

    if (grades.isEmpty()) {
        throw new BusinessRuleException("NO_GRADES", "No grades found for this enrollment");
    }

    // Group by grade type
    Map<GradeType, List<Grade>> byType = grades.stream()
        .collect(Collectors.groupingBy(Grade::getGradeType));

    // Calculate weighted average for each type
    List<GradeTypeBreakdown> breakdowns = new ArrayList<>();
    double totalContribution = 0.0;
    double totalWeightCovered = 0.0;

    for (Map.Entry<GradeType, List<Grade>> entry : byType.entrySet()) {
        List<Grade> typeGrades = entry.getValue();

        // Calculate average score for this type
        double totalScore = 0.0;
        double totalMaxPoints = 0.0;
        double totalWeight = 0.0;

        for (Grade g : typeGrades) {
            totalScore += g.getGradeValue();
            totalMaxPoints += g.getMaxPoints();
            totalWeight += g.getWeightPercentage();
        }

        double averagePercentage = (totalScore / totalMaxPoints) * 100;
        double contribution = (averagePercentage * totalWeight) / 100;

        breakdowns.add(new GradeTypeBreakdown(
            entry.getKey(),
            typeGrades.size(),
            averagePercentage,
            totalWeight,
            contribution
        ));

        totalContribution += contribution;
        totalWeightCovered += totalWeight;
    }

    // Convert numeric grade to letter grade
    String letterGrade = convertToLetterGrade(totalContribution);
    double gradePoints = convertToGradePoints(letterGrade);

    boolean isComplete = (totalWeightCovered >= 99.0); // allow for rounding errors

    return new CourseGradeCalculationResponse(
        enrollment,
        breakdowns,
        totalContribution,
        letterGrade,
        gradePoints,
        totalWeightCovered,
        isComplete
    );
}

private String convertToLetterGrade(double numericGrade) {
    if (numericGrade >= 93.0) return "A";
    if (numericGrade >= 90.0) return "A-";
    if (numericGrade >= 87.0) return "B+";
    if (numericGrade >= 83.0) return "B";
    if (numericGrade >= 80.0) return "B-";
    if (numericGrade >= 77.0) return "C+";
    if (numericGrade >= 73.0) return "C";
    if (numericGrade >= 70.0) return "C-";
    if (numericGrade >= 67.0) return "D+";
    if (numericGrade >= 63.0) return "D";
    if (numericGrade >= 60.0) return "D-";
    return "F";
}

private double convertToGradePoints(String letterGrade) {
    switch (letterGrade) {
        case "A": return 4.0;
        case "A-": return 3.7;
        case "B+": return 3.3;
        case "B": return 3.0;
        case "B-": return 2.7;
        case "C+": return 2.3;
        case "C": return 2.0;
        case "C-": return 1.7;
        case "D+": return 1.3;
        case "D": return 1.0;
        case "D-": return 0.7;
        default: return 0.0; // F
    }
}
```

**Performance Notes:**
- Calculation done in application layer (flexible, maintainable)
- Relatively small dataset per enrollment (10-20 grades typical)
- Result can be cached (invalidate when new grade posted)
- Alternative: Create PostgreSQL function for grade calculation (better performance, less flexible)

**SRS Requirements:** FR-2.5, FR-3.5

---

## 7. Payment Endpoints

### POST /api/v1/payments/{paymentId}/validate

**Purpose:** Admin validates payment and grants course access

**Tables Used:** `payments`, `enrollments`

**SQL Queries (Transaction):**
```sql
-- Step 1: Update payment status
UPDATE payments
SET status = 'VALIDATED',
    validated_by = ?,
    validated_date = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE payment_id = ?
RETURNING *;

-- Step 2: Update all related enrollments
UPDATE enrollments
SET payment_status = 'VALIDATED',
    course_access_granted = true,
    updated_at = CURRENT_TIMESTAMP
WHERE student_id = (SELECT student_id FROM payments WHERE payment_id = ?)
  AND course_id = ANY(
      SELECT UNNEST(string_to_array(courses_covered, ',')::int[])
      FROM payments
      WHERE payment_id = ?
  )
  AND semester = (SELECT semester FROM payments WHERE payment_id = ?)
  AND academic_year = (SELECT academic_year FROM payments WHERE payment_id = ?);
```

**Note:** The above SQL uses PostgreSQL-specific functions. In JPA, we handle this differently:

**JPA Repository Methods:**
```java
// PaymentRepository.java
@Modifying
@Query("UPDATE Payment p SET p.status = :status, p.validatedBy = :validatedBy, " +
       "p.validatedDate = CURRENT_TIMESTAMP WHERE p.paymentId = :paymentId")
int updatePaymentStatus(
    @Param("paymentId") Integer paymentId,
    @Param("status") PaymentStatus status,
    @Param("validatedBy") Integer validatedBy
);

// EnrollmentRepository.java
@Modifying
@Query("UPDATE Enrollment e SET e.paymentStatus = 'VALIDATED', " +
       "e.courseAccessGranted = true WHERE e.studentId = :studentId " +
       "AND e.courseId IN :courseIds AND e.semester = :semester AND e.academicYear = :year")
int grantCourseAccessForPayment(
    @Param("studentId") Integer studentId,
    @Param("courseIds") List<Integer> courseIds,
    @Param("semester") String semester,
    @Param("year") Integer year
);
```

**Spring Service Logic:**
```java
// PaymentService.java
@Transactional
public PaymentValidationResponse validatePayment(Integer paymentId, Integer adminUserId) {
    // Get payment
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

    if (payment.getStatus() != PaymentStatus.SUBMITTED) {
        throw new BusinessRuleException("INVALID_STATUS",
            "Only submitted payments can be validated");
    }

    // Update payment status
    payment.setStatus(PaymentStatus.VALIDATED);
    payment.setValidatedBy(adminUserId);
    payment.setValidatedDate(LocalDateTime.now());
    paymentRepository.save(payment);

    // Parse courses covered (stored as JSON array string: "[12,15,18]")
    List<Integer> courseIds = parseCoursesCovered(payment.getCoursesCovered());

    // Update enrollments to grant access
    int updatedCount = enrollmentRepository.grantCourseAccessForPayment(
        payment.getStudentId(),
        courseIds,
        payment.getSemester(),
        payment.getAcademicYear()
    );

    // Get affected enrollments for response
    List<Enrollment> affectedEnrollments = enrollmentRepository.findByStudentIdAndCourseIdIn(
        payment.getStudentId(), courseIds
    );

    // Send notification to student
    notificationService.sendPaymentValidatedNotification(payment, affectedEnrollments);

    return new PaymentValidationResponse(payment, affectedEnrollments);
}

private List<Integer> parseCoursesCovered(String coursesCoveredJson) {
    // Parse "[12,15,18]" to List<Integer>
    try {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(coursesCoveredJson, new TypeReference<List<Integer>>(){});
    } catch (Exception e) {
        throw new DataIntegrityException("Invalid courses_covered format");
    }
}
```

**Performance Notes:**
- Batch update of enrollments more efficient than individual updates
- JSON parsing in application layer (acceptable for small arrays, 1-6 courses typical)
- Alternative: Normalize with `payment_courses` junction table (cleaner but more complex)
- Transaction ensures atomicity (payment and enrollments updated together)

**SRS Requirements:** FR-4.4, FR-5.3

---

## 8. Optimization Strategies

### Index Strategy Summary

**Primary Indexes (Auto-Created):**
- All primary keys (PK)
- All unique constraints (UK)
- All foreign keys (FK)

**Custom Performance Indexes:**
```sql
-- Frequently filtered columns
CREATE INDEX idx_courses_department ON courses(department);
CREATE INDEX idx_courses_semester_year ON courses(semester, academic_year);
CREATE INDEX idx_courses_active_registration ON courses(is_active, registration_open);

-- Student queries
CREATE INDEX idx_enrollments_student_semester ON enrollments(student_id, semester, academic_year);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_grades_student ON grades(student_id);

-- Search indexes
CREATE INDEX idx_courses_search ON courses USING gin(to_tsvector('english', course_name || ' ' || course_code));
CREATE INDEX idx_users_email_lower ON users(LOWER(email)); -- case-insensitive email lookup
```

### Query Performance Monitoring

**Slow Query Logging:**
```properties
# application.properties
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=debug

# Log slow queries (> 1 second)
spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=1000
```

**N+1 Query Prevention:**
```java
// BAD: N+1 problem
List<Enrollment> enrollments = enrollmentRepository.findByStudentId(123);
for (Enrollment e : enrollments) {
    System.out.println(e.getCourse().getCourseName()); // Extra query for each course!
}

// GOOD: JOIN FETCH
@Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.studentId = :studentId")
List<Enrollment> findByStudentIdWithCourse(@Param("studentId") Integer studentId);
```

### Caching Strategy

**Spring Cache Configuration:**
```java
// CacheConfig.java
@EnableCaching
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "courses",       // Cache course details (rarely change)
            "students",      // Cache student profiles
            "transcripts",   // Cache generated transcripts
            "degreeProgress" // Cache degree progress calculations
        );
    }
}

// CourseService.java
@Cacheable(value = "courses", key = "#courseId")
public Course getCourseById(Integer courseId) {
    return courseRepository.findById(courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
}

@CacheEvict(value = "courses", key = "#courseId")
public void updateCourse(Integer courseId, UpdateCourseRequest request) {
    // Update course... cache evicted automatically
}
```

---

## 9. Database Connection Configuration

**Spring Boot application.properties:**
```properties
# PostgreSQL connection
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=sams_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP connection pooling (default in Spring Boot)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA/Hibernate settings
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate # never use 'update' in production!
spring.jpa.show-sql=false # set to true for debugging
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

---

**Document Status:** Complete
**Next Step:** Authentication flow documentation (03-authentication-flow.md)
