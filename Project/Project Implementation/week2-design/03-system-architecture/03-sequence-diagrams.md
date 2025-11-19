# SAMS Sequence Diagrams - Key Workflows

## Purpose
This document provides detailed sequence diagrams for the major workflows in SAMS, showing the interactions between system components.

---

## 1. User Login Workflow

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Vue as Vue.js App
    participant Store as Vuex Store
    participant API as API Service
    participant Controller as AuthController
    participant Service as AuthService
    participant Repository as UserRepository
    participant DB as PostgreSQL
    participant JWT as JWTTokenProvider

    User->>Browser: Enter credentials and click Login
    Browser->>Vue: Submit login form
    Vue->>Store: dispatch('auth/login', credentials)
    Store->>API: authService.login(credentials)
    API->>Controller: POST /api/v1/auth/login
    Controller->>Controller: Validate request body
    Controller->>Service: authenticateUser(request)

    Service->>Repository: findByUsernameAndIsActiveTrue(username)
    Repository->>DB: SELECT * FROM users WHERE username = ? AND is_active = true
    DB-->>Repository: User record or null
    Repository-->>Service: Optional<User>

    alt User not found
        Service-->>Controller: throw InvalidCredentialsException
        Controller-->>API: 401 Unauthorized
        API-->>Store: Error response
        Store-->>Vue: Login failed
        Vue-->>Browser: Display error message
    end

    Service->>Service: passwordEncoder.matches(inputPassword, storedHash)

    alt Password incorrect
        Service-->>Controller: throw InvalidCredentialsException
        Controller-->>API: 401 Unauthorized
    end

    Service->>JWT: createToken(userId, username, role)
    JWT->>JWT: Build JWT with claims
    JWT-->>Service: JWT token string

    Service->>Repository: save(user) - update last_login
    Repository->>DB: UPDATE users SET last_login = NOW() WHERE user_id = ?

    Service->>Service: Build LoginResponse
    Service-->>Controller: LoginResponse (token, user info)
    Controller-->>API: 200 OK + JSON response
    API-->>Store: Login successful
    Store->>Store: commit('SET_TOKEN', token)
    Store->>Store: commit('SET_USER', user)
    Store->>Store: localStorage.setItem('accessToken', token)
    Store-->>Vue: Success
    Vue->>Vue: router.push(role-based dashboard)
    Vue-->>Browser: Redirect to dashboard
    Browser-->>User: Display dashboard
```

**Key Points:**
- BCrypt password verification (secure, slow by design)
- JWT token contains userId, username, role
- Token stored in localStorage (accessible across sessions)
- last_login updated asynchronously
- Role-based redirect (Student/Faculty/Admin dashboard)

---

## 2. Course Registration Workflow

```mermaid
sequenceDiagram
    actor Student
    participant Vue as Vue Component
    participant Store as Vuex Store
    participant API as API Service
    participant Controller as EnrollmentController
    participant Service as EnrollmentService
    participant CourseRepo as CourseRepository
    participant EnrollRepo as EnrollmentRepository
    participant GradeRepo as GradeRepository
    participant DB as PostgreSQL
    participant NotifService as NotificationService
    participant Trigger as DB Trigger

    Student->>Vue: Click "Enroll" on course
    Vue->>Vue: Show confirmation dialog
    Student->>Vue: Confirm enrollment

    Vue->>API: enrollmentService.create({studentId, courseId})
    API->>Controller: POST /api/v1/enrollments
    Controller->>Controller: Extract JWT, validate auth
    Controller->>Controller: Validate request body
    Controller->>Service: registerForCourse(request)

    Note over Service: Start SERIALIZABLE transaction

    Service->>CourseRepo: findByIdForUpdate(courseId)
    CourseRepo->>DB: SELECT * FROM courses WHERE course_id = ? FOR UPDATE
    Note over DB: Row-level lock acquired
    DB-->>CourseRepo: Course entity (locked)
    CourseRepo-->>Service: Course

    alt Course not found or inactive
        Service-->>Controller: throw ResourceNotFoundException
        Controller-->>API: 404 Not Found
        API-->>Vue: Error response
        Vue->>Vue: Display error message
    end

    Service->>EnrollRepo: findByStudentAndCourseAndSemester(...)
    EnrollRepo->>DB: SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?
    DB-->>EnrollRepo: Existing enrollment or null
    EnrollRepo-->>Service: Optional<Enrollment>

    alt Already enrolled
        Service-->>Controller: throw BusinessRuleException("ALREADY_ENROLLED")
        Controller-->>API: 409 Conflict
    end

    alt Course has prerequisites
        Service->>Service: Parse prerequisites (comma-separated)
        Service->>GradeRepo: findCompletedCoursesByStudent(studentId)
        GradeRepo->>DB: SELECT course_code FROM grades WHERE student_id = ? AND status = 'COMPLETED'
        DB-->>GradeRepo: List of completed courses
        GradeRepo-->>Service: List<String>
        Service->>Service: Check if all prerequisites met

        alt Prerequisites not met
            Service-->>Controller: throw BusinessRuleException("PREREQUISITES_NOT_MET")
            Controller-->>API: 422 Unprocessable Entity
        end
    end

    Service->>EnrollRepo: findActiveEnrollmentsBySemester(studentId, semester, year)
    EnrollRepo->>DB: SELECT * FROM enrollments WHERE student_id = ? AND status = 'ENROLLED'
    DB-->>EnrollRepo: List of active enrollments
    EnrollRepo-->>Service: List<Enrollment>
    Service->>Service: Check for schedule conflicts

    alt Schedule conflict detected
        Service-->>Controller: throw BusinessRuleException("SCHEDULE_CONFLICT")
        Controller-->>API: 409 Conflict
    end

    Service->>Service: Determine enrollment status
    alt Course has available seats
        Service->>Service: status = ENROLLED
    else Course full but waitlist available
        Service->>Service: status = WAITLISTED
        Service->>Service: waitlistPosition = currentWaitlist + 1
    else Both full
        Service-->>Controller: throw BusinessRuleException("COURSE_FULL")
        Controller-->>API: 409 Conflict
    end

    Service->>EnrollRepo: save(enrollment)
    EnrollRepo->>DB: INSERT INTO enrollments (student_id, course_id, status, ...)
    DB->>Trigger: INSERT trigger fires
    Trigger->>DB: UPDATE courses SET current_enrollment = ... WHERE course_id = ?
    Note over Trigger: Auto-updates course capacity counts
    DB-->>EnrollRepo: Saved enrollment entity
    EnrollRepo-->>Service: Enrollment

    Note over Service: Commit transaction

    Service->>NotifService: sendEnrollmentNotification(enrollment)
    NotifService->>DB: INSERT INTO notifications (user_id, message, ...)
    Note over NotifService: Async operation

    Service->>Service: Map to DTO
    Service-->>Controller: EnrollmentDTO
    Controller-->>API: 201 Created + JSON response
    API-->>Store: Enrollment successful
    Store->>Store: Add enrollment to state
    Store-->>Vue: Success
    Vue->>Vue: Refresh enrollments list
    Vue->>Vue: Show success notification
    Vue-->>Student: Display confirmation
```

**Critical Aspects:**
- **SERIALIZABLE transaction**: Prevents race conditions during concurrent enrollment
- **Row-level lock (FOR UPDATE)**: Ensures course capacity check is atomic
- **Business rule validation**:
  1. Prerequisites check
  2. Schedule conflict detection
  3. Capacity verification
- **Database trigger**: Auto-updates `current_enrollment` count
- **Notification**: Async operation doesn't block response

---

## 3. Grade Entry Workflow (Faculty)

```mermaid
sequenceDiagram
    actor Faculty
    participant Vue as Vue Component
    participant API as API Service
    participant Controller as GradeController
    participant Service as GradeService
    participant EnrollRepo as EnrollmentRepository
    participant GradeRepo as GradeRepository
    participant DB as PostgreSQL
    participant NotifService as NotificationService

    Faculty->>Vue: Select student and enter grade
    Vue->>Vue: Validate form input
    Faculty->>Vue: Click "Submit Grade"

    Vue->>API: gradeService.create(gradeData)
    API->>Controller: POST /api/v1/grades + JWT token
    Controller->>Controller: Extract userId from JWT
    Controller->>Controller: @PreAuthorize("hasRole('FACULTY')")
    Controller->>Controller: Validate request body
    Controller->>Service: createGrade(request)

    Service->>EnrollRepo: findById(enrollmentId)
    EnrollRepo->>DB: SELECT e.*, c.* FROM enrollments e JOIN courses c WHERE enrollment_id = ?
    DB-->>EnrollRepo: Enrollment with Course
    EnrollRepo-->>Service: Enrollment

    alt Enrollment not found
        Service-->>Controller: throw ResourceNotFoundException
        Controller-->>API: 404 Not Found
    end

    Service->>Service: validateFacultyPermission(course, facultyId)
    Note over Service: Check if authenticated faculty teaches this course

    alt Not course instructor
        Service-->>Controller: throw InsufficientPermissionsException
        Controller-->>API: 403 Forbidden
    end

    Service->>Service: Validate grade value <= max points

    alt Invalid grade value
        Service-->>Controller: throw ValidationException
        Controller-->>API: 422 Unprocessable Entity
    end

    Service->>Service: Build Grade entity
    Service->>GradeRepo: save(grade)
    GradeRepo->>DB: INSERT INTO grades (enrollment_id, student_id, course_id, grade_value, ...)
    DB-->>GradeRepo: Saved grade
    GradeRepo-->>Service: Grade entity

    Service->>Service: recalculateStudentGPA(studentId) - async
    Note over Service: Background job calculates new GPA

    Service->>NotifService: sendGradePostedNotification(grade)
    NotifService->>DB: INSERT INTO notifications (user_id, notification_type, message, ...)
    Note over NotifService: Student receives notification

    Service->>Service: Map to DTO
    Service-->>Controller: GradeDTO
    Controller-->>API: 201 Created + JSON response
    API-->>Vue: Grade created successfully
    Vue->>Vue: Refresh grades list
    Vue->>Vue: Show success message
    Vue-->>Faculty: Confirmation displayed
```

**Key Validation Points:**
- **Authorization**: Only course instructor can enter grades for that course
- **Input validation**: Grade value must not exceed max points
- **Denormalized FKs**: `student_id` and `course_id` stored in grades table for query performance
- **GPA recalculation**: Async job triggered after grade entry
- **Student notification**: Student notified of new grade

---

## 4. Payment Validation Workflow (Admin)

```mermaid
sequenceDiagram
    actor Admin
    participant Vue as Vue Component
    participant API as API Service
    participant Controller as PaymentController
    participant Service as PaymentService
    participant PaymentRepo as PaymentRepository
    participant EnrollRepo as EnrollmentRepository
    participant DB as PostgreSQL
    participant NotifService as NotificationService

    Admin->>Vue: View pending payments list
    Vue->>API: paymentService.getPendingPayments()
    API->>Controller: GET /api/v1/payments?status=SUBMITTED
    Controller->>Service: getAllPayments(status=SUBMITTED)
    Service->>PaymentRepo: findByStatus(SUBMITTED)
    PaymentRepo->>DB: SELECT * FROM payments WHERE status = 'SUBMITTED'
    DB-->>PaymentRepo: List of pending payments
    PaymentRepo-->>Service: List<Payment>
    Service-->>Controller: List<PaymentDTO>
    Controller-->>API: 200 OK + JSON
    API-->>Vue: Pending payments data
    Vue-->>Admin: Display payments table

    Admin->>Vue: Click "Validate" on payment
    Vue->>Vue: Show payment details modal
    Admin->>Vue: Review transaction reference and amount
    Admin->>Vue: Confirm validation

    Vue->>API: paymentService.validate(paymentId)
    API->>Controller: POST /api/v1/payments/{id}/validate
    Controller->>Controller: @PreAuthorize("hasRole('ADMIN')")
    Controller->>Service: validatePayment(paymentId, adminUserId)

    Note over Service: Start transaction

    Service->>PaymentRepo: findById(paymentId)
    PaymentRepo->>DB: SELECT * FROM payments WHERE payment_id = ?
    DB-->>PaymentRepo: Payment entity
    PaymentRepo-->>Service: Payment

    alt Payment not found
        Service-->>Controller: throw ResourceNotFoundException
        Controller-->>API: 404 Not Found
    end

    alt Payment status != SUBMITTED
        Service-->>Controller: throw BusinessRuleException("INVALID_STATUS")
        Controller-->>API: 400 Bad Request
    end

    Service->>Service: Update payment status
    Service->>Service: payment.setStatus(VALIDATED)
    Service->>Service: payment.setValidatedBy(adminUserId)
    Service->>Service: payment.setValidatedDate(NOW())
    Service->>PaymentRepo: save(payment)
    PaymentRepo->>DB: UPDATE payments SET status = 'VALIDATED', validated_by = ?, validated_date = NOW()
    DB-->>PaymentRepo: Updated payment

    Service->>Service: Parse courses_covered JSON array
    Note over Service: Extract course IDs: [12, 15, 18]

    Service->>EnrollRepo: grantCourseAccessForPayment(studentId, courseIds, semester, year)
    EnrollRepo->>DB: UPDATE enrollments SET payment_status = 'VALIDATED', course_access_granted = true WHERE student_id = ? AND course_id IN (...)
    DB-->>EnrollRepo: Updated count (number of enrollments affected)
    EnrollRepo-->>Service: int updatedCount

    Service->>EnrollRepo: findByStudentIdAndCourseIdIn(studentId, courseIds)
    EnrollRepo->>DB: SELECT * FROM enrollments WHERE student_id = ? AND course_id IN (...)
    DB-->>EnrollRepo: List of affected enrollments
    EnrollRepo-->>Service: List<Enrollment>

    Note over Service: Commit transaction

    Service->>NotifService: sendPaymentValidatedNotification(payment, enrollments)
    NotifService->>DB: INSERT INTO notifications (user_id, notification_type, title, message, ...)
    Note over NotifService: Student receives notification with course access confirmation

    Service->>Service: Build response
    Service-->>Controller: PaymentValidationResponse
    Controller-->>API: 200 OK + JSON response
    API-->>Vue: Validation successful
    Vue->>Vue: Refresh payments list
    Vue->>Vue: Show success notification
    Vue-->>Admin: Confirmation displayed
```

**Transaction Boundaries:**
- Payment status update and enrollment access grant must be atomic
- If enrollment update fails, payment status rollback
- Ensures data consistency

**Business Impact:**
- Student cannot access course content until payment validated
- Enrollment record tracks payment status separately
- Admin audit trail (who validated, when)

---

## 5. View Student Transcript Workflow

```mermaid
sequenceDiagram
    actor Student
    participant Vue as Vue Component
    participant API as API Service
    participant Controller as StudentController
    participant Service as StudentService
    participant StudentRepo as StudentRepository
    participant EnrollRepo as EnrollmentRepository
    participant DB as PostgreSQL

    Student->>Vue: Navigate to "My Transcript"
    Vue->>Vue: mounted() lifecycle hook
    Vue->>API: studentService.getTranscript(studentId)
    API->>Controller: GET /api/v1/students/{id}/transcript + JWT
    Controller->>Controller: Extract userId from JWT
    Controller->>Controller: @PreAuthorize("@studentService.isCurrentUser(#studentId)")
    Controller->>Service: generateTranscript(studentId)

    Service->>StudentRepo: findByIdWithUser(studentId)
    StudentRepo->>DB: SELECT s.*, u.* FROM students s JOIN users u WHERE student_id = ?
    DB-->>StudentRepo: Student with User info
    StudentRepo-->>Service: Student entity

    Service->>EnrollRepo: findCompletedEnrollmentsByStudentId(studentId)
    EnrollRepo->>DB: SELECT e.*, c.*, f.*, u.* FROM enrollments e JOIN courses c JOIN faculty f JOIN users u WHERE e.student_id = ? AND e.status = 'COMPLETED' AND e.final_grade IS NOT NULL ORDER BY academic_year, semester, course_code
    DB-->>EnrollRepo: List of completed enrollments
    EnrollRepo-->>Service: List<Enrollment>

    Service->>Service: Group enrollments by semester/year
    Note over Service: Map<String, List<Enrollment>> bySemester

    loop For each semester
        Service->>Service: Calculate semester credits
        Service->>Service: Calculate semester GPA
        Note over Service: Weighted average: SUM(grade_points * credits) / SUM(credits)
    end

    Service->>Service: Calculate overall statistics
    Service->>Service: Build TranscriptResponse
    Service-->>Controller: TranscriptResponse (student info, academic history, GPA summary)
    Controller-->>API: 200 OK + JSON response
    API-->>Vue: Transcript data

    Vue->>Vue: Process data for display
    Vue->>Vue: Render transcript table
    Vue-->>Student: Display formatted transcript
```

**Transcript Structure:**
```json
{
  "student": {
    "studentNumber": "STU2024001",
    "firstName": "Sarah",
    "lastName": "Johnson",
    "major": "Computer Science"
  },
  "academicHistory": [
    {
      "semester": "Fall",
      "academicYear": 2024,
      "courses": [
        {
          "courseCode": "CS301",
          "courseName": "Data Structures",
          "credits": 3,
          "finalGrade": "A",
          "gradePoints": 4.0,
          "instructor": "Dr. Michael Smith"
        }
      ],
      "semesterCredits": 14,
      "semesterGpa": 3.85
    }
  ],
  "summary": {
    "totalCreditsCompleted": 24,
    "cumulativeGpa": 3.75,
    "academicStanding": "GOOD_STANDING"
  }
}
```

**Performance Optimization:**
- Single query with JOINs (not N+1 queries)
- Application-level GPA calculation (flexible logic)
- Can be cached (transcripts don't change frequently)

---

## 6. Dashboard Data Loading Workflow

```mermaid
sequenceDiagram
    actor Student
    participant Vue as Dashboard.vue
    participant Store as Vuex Store
    participant API as API Service
    participant Spring as Spring Boot
    participant DB as PostgreSQL

    Student->>Vue: Navigate to dashboard
    Vue->>Vue: mounted() lifecycle hook

    Note over Vue: Parallel API calls for performance

    par Fetch student profile
        Vue->>API: studentService.getProfile(studentId)
        API->>Spring: GET /api/v1/students/me
        Spring->>DB: SELECT FROM students WHERE student_id = ?
        DB-->>Spring: Student data
        Spring-->>API: 200 OK + JSON
        API-->>Vue: Student profile
        Vue->>Store: commit('student/SET_PROFILE', profile)
    and Fetch enrollments
        Vue->>API: studentService.getEnrollments(studentId)
        API->>Spring: GET /api/v1/students/{id}/enrollments?semester=Fall&year=2024
        Spring->>DB: SELECT FROM enrollments JOIN courses WHERE student_id = ?
        DB-->>Spring: Enrollment list
        Spring-->>API: 200 OK + JSON
        API-->>Vue: Enrollments
        Vue->>Store: commit('enrollment/SET_LIST', enrollments)
    and Fetch recent grades
        Vue->>API: studentService.getGrades(studentId, {limit: 5})
        API->>Spring: GET /api/v1/students/{id}/grades?limit=5
        Spring->>DB: SELECT FROM grades WHERE student_id = ? ORDER BY created_at DESC LIMIT 5
        DB-->>Spring: Recent grades
        Spring-->>API: 200 OK + JSON
        API-->>Vue: Recent grades
        Vue->>Store: commit('grade/SET_RECENT', grades)
    and Fetch notifications
        Vue->>API: notificationService.getUnread()
        API->>Spring: GET /api/v1/notifications?isRead=false
        Spring->>DB: SELECT FROM notifications WHERE user_id = ? AND is_read = false
        DB-->>Spring: Unread notifications
        Spring-->>API: 200 OK + JSON
        API-->>Vue: Notifications
        Vue->>Store: commit('notification/SET_UNREAD', notifications)
    end

    Vue->>Vue: All data loaded
    Vue->>Vue: loading = false
    Vue-->>Student: Display complete dashboard
```

**Performance Strategy:**
- **Parallel API calls**: Reduces total load time
- **Pagination/Limiting**: Fetch only needed data (recent 5 grades)
- **Lazy loading**: Additional data loaded on-demand (e.g., full grade history)
- **Caching**: Vuex store caches data across page navigations

---

## Workflow Summary

| Workflow | Key Components | Transaction Type | Critical Validation |
|----------|----------------|------------------|---------------------|
| **Login** | AuthService, JwtTokenProvider | Read-only | Password verification, account active |
| **Course Registration** | EnrollmentService, Triggers | SERIALIZABLE | Prerequisites, capacity, schedule |
| **Grade Entry** | GradeService, NotificationService | Standard | Faculty authorization, value range |
| **Payment Validation** | PaymentService, EnrollmentRepository | Standard (multi-table update) | Payment status, course matching |
| **Transcript Generation** | StudentService, EnrollmentRepository | Read-only | Student authorization only |
| **Dashboard Load** | Multiple services | Read-only (parallel) | JWT validation per request |

---

**Document Status:** Complete
**Workflows Documented:** 6 critical business processes
**Diagram Type:** Mermaid sequence diagrams
**Next Step:** Data flow explanation documentation
