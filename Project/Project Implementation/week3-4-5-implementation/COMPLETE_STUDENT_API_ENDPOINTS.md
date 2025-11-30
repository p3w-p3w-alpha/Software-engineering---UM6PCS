# Complete Student-Related API Endpoints Report

## Summary
This document contains all backend API endpoints specifically designed for student functionality across the SAMS (Student Attendance Management System) application.

---

## 1. ENROLLMENT ENDPOINTS
**Controller:** `EnrollmentController`
**Base Path:** `/api/enrollments`

### Endpoint: Create New Enrollment
- **Path:** `/api/enrollments`
- **HTTP Method:** `POST`
- **What it does:** Creates a new enrollment record for a student in a course
- **Parameters Required:**
  - `studentId` (body - Long)
  - `courseId` (body - Long)
- **Response:** `EnrollmentResponse` with enrollment details including student, course, status, date, and waitlist position
- **Student Use:** Students can enroll in courses

### Endpoint: Get All Enrollments
- **Path:** `/api/enrollments`
- **HTTP Method:** `GET`
- **What it does:** Retrieves all enrollment records in the system
- **Parameters Required:** None
- **Response:** List of `EnrollmentResponse` objects
- **Student Use:** View all enrollments (typically for admin purposes)

### Endpoint: Get Enrollment by ID
- **Path:** `/api/enrollments/{id}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves a specific enrollment record by ID
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** Single `EnrollmentResponse` object
- **Student Use:** View details of a specific enrollment

### Endpoint: Get Enrollments by Student
- **Path:** `/api/enrollments/student/{studentId}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves all enrollments for a specific student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** List of `EnrollmentResponse` objects for the student
- **Student Use:** View all courses the student is enrolled in

### Endpoint: Get Enrollments by Course
- **Path:** `/api/enrollments/course/{courseId}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves all enrollments for a specific course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** List of `EnrollmentResponse` objects
- **Student Use:** View who is enrolled in a course

### Endpoint: Get Enrollments by Status
- **Path:** `/api/enrollments/status/{status}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves enrollments filtered by status (e.g., ACTIVE, WAITLISTED, DROPPED)
- **Parameters Required:**
  - `status` (path - String)
- **Response:** List of `EnrollmentResponse` objects matching the status
- **Student Use:** Filter enrollments by their status

### Endpoint: Update Enrollment Status
- **Path:** `/api/enrollments/{id}/status`
- **HTTP Method:** `PATCH`
- **What it does:** Updates the status of an enrollment
- **Parameters Required:**
  - `id` (path - Long)
  - `status` (query - String)
- **Response:** Updated `EnrollmentResponse` object
- **Student Use:** Change enrollment status (e.g., from WAITLISTED to ACTIVE)

### Endpoint: Drop Enrollment
- **Path:** `/api/enrollments/{id}/drop`
- **HTTP Method:** `PUT`
- **What it does:** Marks an enrollment as dropped/withdrawn
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** Updated `EnrollmentResponse` object with status changed to DROPPED
- **Student Use:** Drop a course

### Endpoint: Delete Enrollment
- **Path:** `/api/enrollments/{id}`
- **HTTP Method:** `DELETE`
- **What it does:** Permanently deletes an enrollment record
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** HTTP 204 No Content
- **Student Use:** Administrative deletion of enrollment records

### Endpoint: Get Enrollment Count for Course
- **Path:** `/api/enrollments/course/{courseId}/count`
- **HTTP Method:** `GET`
- **What it does:** Gets the total number of enrolled students in a course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** Number of enrollments (Long)
- **Student Use:** View course enrollment numbers

### Endpoint: Check Student Enrollment
- **Path:** `/api/enrollments/check`
- **HTTP Method:** `GET`
- **What it does:** Checks if a student is enrolled in a specific course
- **Parameters Required:**
  - `studentId` (query - Long)
  - `courseId` (query - Long)
- **Response:** Boolean (true/false)
- **Student Use:** Verify if enrolled in a course

---

## 2. ASSIGNMENT ENDPOINTS
**Controller:** `AssignmentController`
**Base Path:** `/api/assignments`

### Endpoint: Create Assignment
- **Path:** `/api/assignments`
- **HTTP Method:** `POST`
- **What it does:** Creates a new assignment for a course
- **Parameters Required:**
  - `courseId` (query - Long)
  - `facultyId` (query - Long)
  - Assignment details in body (title, description, dueDate, maxPoints, etc.)
- **Response:** `AssignmentResponse` with full assignment details
- **Student Use:** Faculty creates assignments; students receive them

### Endpoint: Get Assignment by ID
- **Path:** `/api/assignments/{id}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves a specific assignment by ID
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** `AssignmentResponse` object
- **Student Use:** View assignment details

### Endpoint: Get All Active Assignments
- **Path:** `/api/assignments`
- **HTTP Method:** `GET`
- **What it does:** Retrieves all active assignments in the system
- **Parameters Required:** None
- **Response:** List of `AssignmentResponse` objects
- **Student Use:** View all available assignments

### Endpoint: Get Assignments by Course
- **Path:** `/api/assignments/course/{courseId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all assignments for a specific course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** List of `AssignmentResponse` objects for the course
- **Student Use:** View assignments in a course student is enrolled in

### Endpoint: Get Assignments by Faculty
- **Path:** `/api/assignments/faculty/{facultyId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all assignments created by a specific faculty member
- **Parameters Required:**
  - `facultyId` (path - Long)
- **Response:** List of `AssignmentResponse` objects
- **Student Use:** View assignments from a specific instructor

### Endpoint: Get Assignments for Student
- **Path:** `/api/assignments/student/{studentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets assignments for courses a student is enrolled in
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** List of `AssignmentResponse` objects
- **Student Use:** View all assignments assigned to the student

### Endpoint: Get Upcoming Assignments
- **Path:** `/api/assignments/upcoming`
- **HTTP Method:** `GET`
- **What it does:** Gets assignments with future due dates
- **Parameters Required:** None
- **Response:** List of `AssignmentResponse` objects ordered by due date
- **Student Use:** See upcoming assignments to plan work

### Endpoint: Get Overdue Assignments
- **Path:** `/api/assignments/overdue`
- **HTTP Method:** `GET`
- **What it does:** Gets assignments that have passed their due date
- **Parameters Required:** None
- **Response:** List of `AssignmentResponse` objects
- **Student Use:** See overdue assignments that need attention

### Endpoint: Get Assignments Due Between Dates
- **Path:** `/api/assignments/due-between`
- **HTTP Method:** `GET`
- **What it does:** Gets assignments with due dates in a specified date range
- **Parameters Required:**
  - `startDate` (query - LocalDateTime format: 2024-01-01T00:00:00)
  - `endDate` (query - LocalDateTime format: 2024-12-31T23:59:59)
- **Response:** List of `AssignmentResponse` objects in date range
- **Student Use:** Filter assignments by due date range

### Endpoint: Search Assignments by Title
- **Path:** `/api/assignments/search`
- **HTTP Method:** `GET`
- **What it does:** Searches assignments by title keyword
- **Parameters Required:**
  - `title` (query - String)
- **Response:** List of `AssignmentResponse` objects matching search
- **Student Use:** Find specific assignments by name

### Endpoint: Update Assignment
- **Path:** `/api/assignments/{id}`
- **HTTP Method:** `PUT`
- **What it does:** Updates an existing assignment (faculty only)
- **Parameters Required:**
  - `id` (path - Long)
  - `facultyId` (query - Long)
  - Updated assignment details in body
- **Response:** Updated `AssignmentResponse` object
- **Student Use:** Faculty updates assignments; students see changes

### Endpoint: Delete Assignment
- **Path:** `/api/assignments/{id}`
- **HTTP Method:** `DELETE`
- **What it does:** Deletes an assignment (faculty only)
- **Parameters Required:**
  - `id` (path - Long)
  - `facultyId` (query - Long)
- **Response:** Success message
- **Student Use:** Faculty deletes assignments

---

## 3. SUBMISSION ENDPOINTS
**Controller:** `SubmissionController`
**Base Path:** `/api/submissions`

### Endpoint: Submit Assignment
- **Path:** `/api/submissions/submit`
- **HTTP Method:** `POST`
- **What it does:** Submits an assignment file by a student
- **Parameters Required:**
  - `assignmentId` (query - Long)
  - `studentId` (query - Long)
  - `file` (multipart form data - MultipartFile)
- **Response:** `SubmissionResponse` with submission details
- **Student Use:** Submit assignment work

### Endpoint: Resubmit Assignment
- **Path:** `/api/submissions/resubmit`
- **HTTP Method:** `POST`
- **What it does:** Resubmits an assignment, replacing the previous submission
- **Parameters Required:**
  - `assignmentId` (query - Long)
  - `studentId` (query - Long)
  - `file` (multipart form data - MultipartFile)
- **Response:** `SubmissionResponse` with updated submission details
- **Student Use:** Update assignment submission before due date

### Endpoint: Grade Submission
- **Path:** `/api/submissions/{id}/grade`
- **HTTP Method:** `POST`
- **What it does:** Grades a student submission (faculty only)
- **Parameters Required:**
  - `id` (path - Long)
  - `facultyId` (query - Long)
  - `pointsEarned` (body - Double)
  - `feedback` (body - String)
- **Response:** Updated `SubmissionResponse` with grade and feedback
- **Student Use:** Faculty grades student submissions; students can view results

### Endpoint: Return Graded Submission
- **Path:** `/api/submissions/{id}/return`
- **HTTP Method:** `POST`
- **What it does:** Returns a graded submission to the student
- **Parameters Required:**
  - `id` (path - Long)
  - `facultyId` (query - Long)
- **Response:** `SubmissionResponse` with status changed to returned
- **Student Use:** Faculty returns graded submissions

### Endpoint: Download Submission File
- **Path:** `/api/submissions/{id}/download`
- **HTTP Method:** `GET`
- **What it does:** Downloads a submitted assignment file
- **Parameters Required:**
  - `id` (path - Long)
  - `userId` (query - Long) - Person downloading must be authenticated
- **Response:** File resource with appropriate content type
- **Student Use:** Download submitted assignment or faculty downloads student submission

### Endpoint: Delete Submission
- **Path:** `/api/submissions/{id}`
- **HTTP Method:** `DELETE`
- **What it does:** Deletes a submission record
- **Parameters Required:**
  - `id` (path - Long)
  - `userId` (query - Long) - Student can delete their own, faculty/admin can delete others
- **Response:** Success message
- **Student Use:** Remove submitted assignment

### Endpoint: Get Submission by ID
- **Path:** `/api/submissions/{id}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves a specific submission by ID
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** `SubmissionResponse` object
- **Student Use:** View submission details

### Endpoint: Get Submissions by Assignment
- **Path:** `/api/submissions/assignment/{assignmentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all submissions for a specific assignment
- **Parameters Required:**
  - `assignmentId` (path - Long)
- **Response:** List of `SubmissionResponse` objects
- **Student Use:** View all submissions for an assignment

### Endpoint: Get Submissions by Student
- **Path:** `/api/submissions/student/{studentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all submissions from a specific student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** List of `SubmissionResponse` objects
- **Student Use:** View all of student's submissions

### Endpoint: Get Submission by Student and Assignment
- **Path:** `/api/submissions/student/{studentId}/assignment/{assignmentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets a student's submission for a specific assignment
- **Parameters Required:**
  - `studentId` (path - Long)
  - `assignmentId` (path - Long)
- **Response:** Single `SubmissionResponse` object or 404 if not found
- **Student Use:** Check if submitted and view submission details

### Endpoint: Get Late Submissions for Assignment
- **Path:** `/api/submissions/assignment/{assignmentId}/late`
- **HTTP Method:** `GET`
- **What it does:** Gets all late submissions for an assignment
- **Parameters Required:**
  - `assignmentId` (path - Long)
- **Response:** List of late `SubmissionResponse` objects
- **Student Use:** Faculty identifies late submissions

### Endpoint: Get Ungraded Submissions by Course
- **Path:** `/api/submissions/course/{courseId}/ungraded`
- **HTTP Method:** `GET`
- **What it does:** Gets all ungraded submissions in a course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** List of ungraded `SubmissionResponse` objects
- **Student Use:** Faculty identifies submissions needing grading

### Endpoint: Get Submissions by Status
- **Path:** `/api/submissions/status/{status}`
- **HTTP Method:** `GET`
- **What it does:** Gets submissions filtered by status (SUBMITTED, GRADED, RETURNED, etc.)
- **Parameters Required:**
  - `status` (path - String)
- **Response:** List of `SubmissionResponse` objects with the specified status
- **Student Use:** Filter submissions by status

### Endpoint: Get Submissions by Student and Course
- **Path:** `/api/submissions/student/{studentId}/course/{courseId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all submissions from a student in a specific course
- **Parameters Required:**
  - `studentId` (path - Long)
  - `courseId` (path - Long)
- **Response:** List of `SubmissionResponse` objects
- **Student Use:** View submissions in a specific course

### Endpoint: Check if Student Submitted
- **Path:** `/api/submissions/check`
- **HTTP Method:** `GET`
- **What it does:** Checks if a student has submitted a specific assignment
- **Parameters Required:**
  - `studentId` (query - Long)
  - `assignmentId` (query - Long)
- **Response:** JSON object with `hasSubmitted` boolean
- **Student Use:** Verify submission status

### Endpoint: Get Submission Counts
- **Path:** `/api/submissions/assignment/{assignmentId}/count`
- **HTTP Method:** `GET`
- **What it does:** Gets submission statistics for an assignment
- **Parameters Required:**
  - `assignmentId` (path - Long)
- **Response:** JSON with total, ungraded, and graded counts
- **Student Use:** Faculty sees submission statistics

---

## 4. GRADE ENDPOINTS
**Controller:** `GradeController`
**Base Path:** `/api/grades`

### Endpoint: Assign Grade
- **Path:** `/api/grades`
- **HTTP Method:** `POST`
- **What it does:** Assigns a grade to a student's enrollment in a course
- **Parameters Required:**
  - `enrollmentId` (body - Long)
  - `gradeValue` (body - String, e.g., "A", "B+", "C")
- **Response:** `GradeResponse` with grade details
- **Student Use:** Faculty assigns grades; students can view their grades

### Endpoint: Get All Grades
- **Path:** `/api/grades`
- **HTTP Method:** `GET`
- **What it does:** Retrieves all grades in the system
- **Parameters Required:** None
- **Response:** List of `GradeResponse` objects
- **Student Use:** Administrative view of all grades

### Endpoint: Get Grade by ID
- **Path:** `/api/grades/{id}`
- **HTTP Method:** `GET`
- **What it does:** Retrieves a specific grade record by ID
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** Single `GradeResponse` object
- **Student Use:** View specific grade details

### Endpoint: Get Grades by Student
- **Path:** `/api/grades/student/{studentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all grades for a specific student across all courses
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** List of `GradeResponse` objects for the student
- **Student Use:** View transcript of all grades

### Endpoint: Get Grades by Course
- **Path:** `/api/grades/course/{courseId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all grades assigned in a specific course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** List of `GradeResponse` objects for the course
- **Student Use:** View grade distribution in a course

### Endpoint: Calculate Student GPA
- **Path:** `/api/grades/student/{studentId}/gpa`
- **HTTP Method:** `GET`
- **What it does:** Calculates cumulative GPA for a student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** Double (GPA value, e.g., 3.5)
- **Student Use:** View cumulative GPA

### Endpoint: Get GPA Summary
- **Path:** `/api/grades/student/{studentId}/summary`
- **HTTP Method:** `GET`
- **What it does:** Gets detailed GPA summary including semester GPA and trends
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** `GPASummary` object with GPA details and statistics
- **Student Use:** View comprehensive GPA summary

### Endpoint: Update Grade
- **Path:** `/api/grades/{id}`
- **HTTP Method:** `PUT`
- **What it does:** Updates an assigned grade
- **Parameters Required:**
  - `id` (path - Long)
  - `gradeValue` (query - String)
- **Response:** Updated `GradeResponse` object
- **Student Use:** Faculty updates grades

### Endpoint: Delete Grade
- **Path:** `/api/grades/{id}`
- **HTTP Method:** `DELETE`
- **What it does:** Deletes a grade record
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** HTTP 204 No Content
- **Student Use:** Administrative deletion of grades

### Endpoint: Get Grade Scale
- **Path:** `/api/grades/scale`
- **HTTP Method:** `GET`
- **What it does:** Returns the grading scale used by the institution
- **Parameters Required:** None
- **Response:** JSON map of letter grades to numeric values (e.g., A=4.0, A-=3.7, B+=3.3, etc.)
- **Student Use:** Understand grading scale

---

## 5. ATTENDANCE ENDPOINTS
**Controller:** `AttendanceController`
**Base Path:** `/api/attendance`

### Endpoint: Mark Attendance
- **Path:** `/api/attendance/mark`
- **HTTP Method:** `POST`
- **What it does:** Marks attendance for a single student (faculty/admin only)
- **Parameters Required:**
  - `userId` (body - Long)
  - `status` (body - String, e.g., "PRESENT", "ABSENT", "LATE")
  - `date` (body - LocalDate)
  - Additional context fields
- **Response:** `AttendanceResponse` with attendance record
- **Student Use:** Faculty marks attendance; students see their attendance

### Endpoint: Mark Bulk Attendance
- **Path:** `/api/attendance/mark-bulk`
- **HTTP Method:** `POST`
- **What it does:** Marks attendance for multiple students at once (faculty/admin only)
- **Parameters Required:**
  - List of attendance records in body
- **Response:** List of `AttendanceResponse` objects
- **Student Use:** Faculty marks attendance for entire class

### Endpoint: Get Attendance by Date
- **Path:** `/api/attendance/date/{date}`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance records for all users on a specific date
- **Parameters Required:**
  - `date` (path - LocalDate format: 2024-01-01)
- **Response:** List of `AttendanceResponse` objects for that date
- **Student Use:** Faculty views class attendance by date

### Endpoint: Get Attendance by Date Range
- **Path:** `/api/attendance/range`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance records for a date range
- **Parameters Required:**
  - `startDate` (query - LocalDate)
  - `endDate` (query - LocalDate)
- **Response:** List of `AttendanceResponse` objects in the range
- **Student Use:** View attendance over a period

### Endpoint: Get User Attendance
- **Path:** `/api/attendance/user/{userId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all attendance records for a specific user
- **Parameters Required:**
  - `userId` (path - Long)
- **Response:** List of `AttendanceResponse` objects
- **Student Use:** Student views their attendance history

### Endpoint: Get User Attendance by Date Range
- **Path:** `/api/attendance/user/{userId}/range`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance records for a user within a date range
- **Parameters Required:**
  - `userId` (path - Long)
  - `startDate` (query - LocalDate)
  - `endDate` (query - LocalDate)
- **Response:** List of `AttendanceResponse` objects
- **Student Use:** Student views attendance for specific period

### Endpoint: Get Attendance Statistics by Date
- **Path:** `/api/attendance/statistics/date/{date}`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance statistics for all users on a date (admin only)
- **Parameters Required:**
  - `date` (path - LocalDate)
- **Response:** `AttendanceStatistics` object with aggregate data
- **Student Use:** Admin views attendance statistics

### Endpoint: Get Attendance Statistics by Date Range
- **Path:** `/api/attendance/statistics/range`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance statistics for a date range (admin only)
- **Parameters Required:**
  - `startDate` (query - LocalDate)
  - `endDate` (query - LocalDate)
- **Response:** `AttendanceStatistics` object with aggregate data
- **Student Use:** Admin views attendance trends

### Endpoint: Get User Attendance Statistics
- **Path:** `/api/attendance/statistics/user/{userId}`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance statistics for a specific user
- **Parameters Required:**
  - `userId` (path - Long)
  - `startDate` (query - LocalDate)
  - `endDate` (query - LocalDate)
- **Response:** `AttendanceStatistics` object
- **Student Use:** View personal attendance statistics

### Endpoint: Get Attendance Statistics by Role
- **Path:** `/api/attendance/statistics/by-role`
- **HTTP Method:** `GET`
- **What it does:** Gets attendance statistics grouped by role (admin only)
- **Parameters Required:**
  - `startDate` (query - LocalDate)
  - `endDate` (query - LocalDate)
- **Response:** Map of role to `AttendanceStatistics`
- **Student Use:** Admin views attendance by role

### Endpoint: Get Attendance Percentage
- **Path:** `/api/attendance/percentage/{userId}`
- **HTTP Method:** `GET`
- **What it does:** Calculates attendance percentage for a user
- **Parameters Required:**
  - `userId` (path - Long)
  - `startDate` (query - LocalDate)
  - `endDate` (query - LocalDate)
- **Response:** Double (percentage, 0-100)
- **Student Use:** View attendance percentage

### Endpoint: Delete Attendance Record
- **Path:** `/api/attendance/{id}`
- **HTTP Method:** `DELETE`
- **What it does:** Deletes an attendance record (admin only)
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** HTTP 204 No Content
- **Student Use:** Admin removes attendance records

---

## 6. DEGREE PROGRESS ENDPOINTS
**Controller:** `DegreeProgressController`
**Base Path:** `/api/degree-progress`

### Endpoint: Enroll Student in Degree Program
- **Path:** `/api/degree-progress/students/{studentId}/enroll`
- **HTTP Method:** `POST`
- **What it does:** Enrolls a student in a degree program (admin only)
- **Parameters Required:**
  - `studentId` (path - Long)
  - `degreeProgramId` (query - Long)
  - `startDate` (query - LocalDate)
- **Response:** `StudentDegreeProgress` object
- **Student Use:** Admin enrolls students in degree programs

### Endpoint: Get Student Progress
- **Path:** `/api/degree-progress/students/{studentId}/progress`
- **HTTP Method:** `GET`
- **What it does:** Gets degree progress information for a student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** `StudentDegreeProgress` object with completed/pending requirements
- **Student Use:** Student views their degree progress toward graduation

### Endpoint: Update Student Progress
- **Path:** `/api/degree-progress/students/{studentId}/update-progress`
- **HTTP Method:** `POST`
- **What it does:** Recalculates student progress based on current grades (admin only)
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** Updated `StudentDegreeProgress` object
- **Student Use:** Admin updates progress calculations

### Endpoint: Check Graduation Eligibility
- **Path:** `/api/degree-progress/students/{studentId}/graduation-eligible`
- **HTTP Method:** `GET`
- **What it does:** Checks if student meets all graduation requirements
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** Boolean (true = eligible, false = not eligible)
- **Student Use:** Student checks if ready to graduate

### Endpoint: Mark Student as Graduated
- **Path:** `/api/degree-progress/students/{studentId}/graduate`
- **HTTP Method:** `POST`
- **What it does:** Marks a student as graduated (admin only)
- **Parameters Required:**
  - `studentId` (path - Long)
  - `graduationDate` (query - LocalDate)
- **Response:** Updated `StudentDegreeProgress` object with graduation date
- **Student Use:** Admin marks graduation

### Endpoint: Get Students in Degree Program
- **Path:** `/api/degree-progress/programs/{programId}/students`
- **HTTP Method:** `GET`
- **What it does:** Gets all students enrolled in a degree program (admin only)
- **Parameters Required:**
  - `programId` (path - Long)
- **Response:** List of `StudentDegreeProgress` objects
- **Student Use:** Admin views students in program

### Endpoint: Get Eligible for Graduation
- **Path:** `/api/degree-progress/students/graduation-eligible`
- **HTTP Method:** `GET`
- **What it does:** Gets all students eligible to graduate (admin only)
- **Parameters Required:** None
- **Response:** List of `StudentDegreeProgress` objects
- **Student Use:** Admin identifies graduating students

### Endpoint: Get Students at Risk
- **Path:** `/api/degree-progress/students/at-risk`
- **HTTP Method:** `GET`
- **What it does:** Gets students at risk of not graduating (faculty/admin)
- **Parameters Required:** None
- **Response:** List of `StudentDegreeProgress` objects
- **Student Use:** Faculty/admin identifies at-risk students

### Endpoint: Update Progress Status
- **Path:** `/api/degree-progress/students/{studentId}/status`
- **HTTP Method:** `PATCH`
- **What it does:** Updates student progress status (admin only)
- **Parameters Required:**
  - `studentId` (path - Long)
  - `status` (query - String)
- **Response:** Updated `StudentDegreeProgress` object
- **Student Use:** Admin changes progress status

### Endpoint: Get Progress Report
- **Path:** `/api/degree-progress/students/{studentId}/report`
- **HTTP Method:** `GET`
- **What it does:** Gets detailed progress report for a student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** `ProgressReport` object with detailed progress information
- **Student Use:** Student/faculty/admin views comprehensive progress report

### Endpoint: Get Degree Programs
- **Path:** `/api/degree-progress/programs`
- **HTTP Method:** `GET`
- **What it does:** Gets all available degree programs
- **Parameters Required:** None
- **Response:** List of `DegreeProgram` objects
- **Student Use:** View available programs

### Endpoint: Get Active Degree Programs
- **Path:** `/api/degree-progress/programs/active`
- **HTTP Method:** `GET`
- **What it does:** Gets only active degree programs
- **Parameters Required:** None
- **Response:** List of active `DegreeProgram` objects
- **Student Use:** View programs currently accepting students

### Endpoint: Get Degree Program by ID
- **Path:** `/api/degree-progress/programs/{id}`
- **HTTP Method:** `GET`
- **What it does:** Gets details of a specific degree program
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** Single `DegreeProgram` object
- **Student Use:** View program details

### Endpoint: Get Program Requirements
- **Path:** `/api/degree-progress/programs/{programId}/requirements`
- **HTTP Method:** `GET`
- **What it does:** Gets all degree requirements for a program
- **Parameters Required:**
  - `programId` (path - Long)
- **Response:** List of `DegreeRequirement` objects
- **Student Use:** See what's required for the degree

---

## 7. PAYMENT & BILLING ENDPOINTS
**Controller:** `PaymentController`
**Base Path:** `/api/payments`

### Endpoint: Create Payment
- **Path:** `/api/payments/create`
- **HTTP Method:** `POST`
- **What it does:** Creates or updates payment for a student's semester enrollments
- **Parameters Required:**
  - `studentId` (body - Long)
  - `semesterId` (body - Long)
  - Authorization header with JWT token
- **Response:** `PaymentResponse` with payment details
- **Student Use:** Student creates payment for semester fees

### Endpoint: Submit Payment
- **Path:** `/api/payments/{id}/submit`
- **HTTP Method:** `POST`
- **What it does:** Submits/marks a payment as paid by student
- **Parameters Required:**
  - `id` (path - Long)
  - `paidAmount` (body - BigDecimal)
  - `paymentMethod` (body - String, e.g., "CREDIT_CARD", "BANK_TRANSFER")
  - `transactionReference` (body - String)
  - Authorization header
- **Response:** Updated `PaymentResponse`
- **Student Use:** Student submits payment proof

### Endpoint: Approve Payment
- **Path:** `/api/payments/{id}/approve`
- **HTTP Method:** `POST`
- **What it does:** Approves a payment (admin only)
- **Parameters Required:**
  - `id` (path - Long)
  - Authorization header
- **Response:** `PaymentResponse` with status APPROVED
- **Student Use:** Admin approves student payment

### Endpoint: Reject Payment
- **Path:** `/api/payments/{id}/reject`
- **HTTP Method:** `POST`
- **What it does:** Rejects a payment with reason (admin only)
- **Parameters Required:**
  - `id` (path - Long)
  - `reason` (query - String)
  - Authorization header
- **Response:** `PaymentResponse` with status REJECTED
- **Student Use:** Admin rejects payment with explanation

### Endpoint: Get Payment by ID
- **Path:** `/api/payments/{id}`
- **HTTP Method:** `GET`
- **What it does:** Gets details of a specific payment
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** `PaymentResponse` object
- **Student Use:** Student views payment details

### Endpoint: Get All Payments
- **Path:** `/api/payments`
- **HTTP Method:** `GET`
- **What it does:** Gets all payments (admin only)
- **Parameters Required:** None
- **Response:** List of `PaymentResponse` objects
- **Student Use:** Admin views all payments

### Endpoint: Get Payments by Student
- **Path:** `/api/payments/student/{studentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all payments for a specific student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** List of `PaymentResponse` objects
- **Student Use:** Student views their payment history

### Endpoint: Get Payments by Semester
- **Path:** `/api/payments/semester/{semesterId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all payments for a semester (admin only)
- **Parameters Required:**
  - `semesterId` (path - Long)
- **Response:** List of `PaymentResponse` objects
- **Student Use:** Admin views semester payments

### Endpoint: Get Payments Pending Approval
- **Path:** `/api/payments/pending-approval`
- **HTTP Method:** `GET`
- **What it does:** Gets all payments awaiting approval (admin only)
- **Parameters Required:** None
- **Response:** List of `PaymentResponse` objects with status PENDING
- **Student Use:** Admin reviews pending payments

### Endpoint: Get Payment for Student in Semester
- **Path:** `/api/payments/student/{studentId}/semester/{semesterId}`
- **HTTP Method:** `GET`
- **What it does:** Gets the payment for a student in a specific semester
- **Parameters Required:**
  - `studentId` (path - Long)
  - `semesterId` (path - Long)
- **Response:** `PaymentResponse` object or 404 if not found
- **Student Use:** Check payment status for specific semester

### Endpoint: Check Approved Payment
- **Path:** `/api/payments/student/{studentId}/semester/{semesterId}/approved`
- **HTTP Method:** `GET`
- **What it does:** Checks if student has approved payment for semester
- **Parameters Required:**
  - `studentId` (path - Long)
  - `semesterId` (path - Long)
- **Response:** Boolean (true = has approved payment, false = no)
- **Student Use:** Verify payment approval status

### Endpoint: Get Payment History
- **Path:** `/api/payments/{id}/history`
- **HTTP Method:** `GET`
- **What it does:** Gets history/audit trail of a payment
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** List of `PaymentHistory` objects with status changes
- **Student Use:** View payment status changes over time

### Endpoint: Get Overdue Payments
- **Path:** `/api/payments/overdue`
- **HTTP Method:** `GET`
- **What it does:** Gets all overdue payments (admin only)
- **Parameters Required:** None
- **Response:** List of `PaymentResponse` objects
- **Student Use:** Admin identifies overdue student payments

### Endpoint: Delete Payment
- **Path:** `/api/payments/{id}`
- **HTTP Method:** `DELETE`
- **What it does:** Deletes a payment (super admin only)
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** HTTP 204 No Content
- **Student Use:** Super admin removes payment records

---

## 8. FEE MANAGEMENT ENDPOINTS
**Controller:** `FeeController`
**Base Path:** `/api/fees`

### Endpoint: Get Fee Structures for Program
- **Path:** `/api/fees/structures/program/{programId}`
- **HTTP Method:** `GET`
- **What it does:** Gets fee structures for a specific degree program
- **Parameters Required:**
  - `programId` (path - Long)
  - `semester` (query - Integer, optional)
- **Response:** List of `FeeStructureResponse` objects
- **Student Use:** View applicable fees for program

### Endpoint: Get Active Fee Structures
- **Path:** `/api/fees/structures/active`
- **HTTP Method:** `GET`
- **What it does:** Gets all currently active fee structures
- **Parameters Required:** None
- **Response:** List of `FeeStructureResponse` objects
- **Student Use:** View current fee information

### Endpoint: Get Fee Structure by ID
- **Path:** `/api/fees/structures/{id}`
- **HTTP Method:** `GET`
- **What it does:** Gets details of a specific fee structure
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** `FeeStructureResponse` object
- **Student Use:** View fee structure details

### Endpoint: Get Fee Items by Student
- **Path:** `/api/fees/items/student/{studentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets all fee items for a student
- **Parameters Required:**
  - `studentId` (path - Long)
- **Response:** List of `FeeItemResponse` objects
- **Student Use:** View all fees owed

### Endpoint: Get Fee Items by Student and Semester
- **Path:** `/api/fees/items/student/{studentId}/semester/{semesterId}`
- **HTTP Method:** `GET`
- **What it does:** Gets fee items for a student in a specific semester
- **Parameters Required:**
  - `studentId` (path - Long)
  - `semesterId` (path - Long)
- **Response:** List of `FeeItemResponse` objects
- **Student Use:** View semester-specific fees

### Endpoint: Get Fee Items by Payment
- **Path:** `/api/fees/items/payment/{paymentId}`
- **HTTP Method:** `GET`
- **What it does:** Gets fee items associated with a payment
- **Parameters Required:**
  - `paymentId` (path - Long)
- **Response:** List of `FeeItemResponse` objects
- **Student Use:** View fees in a payment

### Endpoint: Get Fee Breakdown
- **Path:** `/api/fees/breakdown/student/{studentId}/semester/{semesterId}`
- **HTTP Method:** `GET`
- **What it does:** Gets detailed fee breakdown for student in semester
- **Parameters Required:**
  - `studentId` (path - Long)
  - `semesterId` (path - Long)
- **Response:** `FeeBreakdownResponse` with tuition, fees, discounts, total
- **Student Use:** Student views itemized fee breakdown

### Endpoint: Apply Discount to Fee Item
- **Path:** `/api/fees/items/{id}/discount`
- **HTTP Method:** `POST`
- **What it does:** Applies discount to a fee item (admin only)
- **Parameters Required:**
  - `id` (path - Long)
  - `amount` (query - BigDecimal)
  - `reason` (query - String)
- **Response:** Updated `FeeItemResponse`
- **Student Use:** Admin provides discount

### Endpoint: Waive Fee Item
- **Path:** `/api/fees/items/{id}/waive`
- **HTTP Method:** `POST`
- **What it does:** Waives a fee completely (admin only)
- **Parameters Required:**
  - `id` (path - Long)
  - `reason` (query - String)
- **Response:** Updated `FeeItemResponse` with waived status
- **Student Use:** Admin waives fee

---

## 9. COURSE ENDPOINTS (Student-Related)
**Controller:** `CourseController`
**Base Path:** `/api/courses`

### Endpoint: Get All Courses
- **Path:** `/api/courses`
- **HTTP Method:** `GET`
- **What it does:** Gets all available courses
- **Parameters Required:** None
- **Response:** List of `CourseResponse` objects
- **Student Use:** Browse available courses for enrollment

### Endpoint: Get Course by ID
- **Path:** `/api/courses/{id}`
- **HTTP Method:** `GET`
- **What it does:** Gets details of a specific course
- **Parameters Required:**
  - `id` (path - Long)
- **Response:** `CourseResponse` object with full details
- **Student Use:** View course details before enrollment

### Endpoint: Get Course by Course Code
- **Path:** `/api/courses/code/{courseCode}`
- **HTTP Method:** `GET`
- **What it does:** Gets course by course code
- **Parameters Required:**
  - `courseCode` (path - String)
- **Response:** `CourseResponse` object
- **Student Use:** Look up course by code

### Endpoint: Search Courses by Name or Code
- **Path:** `/api/courses/search`
- **HTTP Method:** `GET`
- **What it does:** Searches courses in both name and code fields
- **Parameters Required:**
  - `query` (query - String)
- **Response:** List of `CourseResponse` objects matching search
- **Student Use:** Find courses by search term

### Endpoint: Search Courses by Name
- **Path:** `/api/courses/search/name`
- **HTTP Method:** `GET`
- **What it does:** Searches courses by name only
- **Parameters Required:**
  - `query` (query - String)
- **Response:** List of `CourseResponse` objects
- **Student Use:** Search by course name

### Endpoint: Search Courses by Code
- **Path:** `/api/courses/search/code`
- **HTTP Method:** `GET`
- **What it does:** Searches courses by code only
- **Parameters Required:**
  - `query` (query - String)
- **Response:** List of `CourseResponse` objects
- **Student Use:** Search by course code

### Endpoint: Get Courses by Credits
- **Path:** `/api/courses/credits/{credits}`
- **HTTP Method:** `GET`
- **What it does:** Gets courses by credit hours
- **Parameters Required:**
  - `credits` (path - Integer)
- **Response:** List of `CourseResponse` objects
- **Student Use:** Filter courses by credit value

### Endpoint: Get Course Prerequisites
- **Path:** `/api/courses/{courseId}/prerequisites`
- **HTTP Method:** `GET`
- **What it does:** Gets all prerequisite courses for a course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** List of `CourseResponse` objects (prerequisites)
- **Student Use:** Check what courses are required before enrolling

### Endpoint: Get Course Waitlist
- **Path:** `/api/courses/{courseId}/waitlist`
- **HTTP Method:** `GET`
- **What it does:** Gets students on waitlist for a course
- **Parameters Required:**
  - `courseId` (path - Long)
- **Response:** List of `WaitlistInfo` objects with student info and position
- **Student Use:** Check waitlist position and status

---

## Summary Statistics

### Total Student-Related Endpoints: 81

**By Controller:**
- EnrollmentController: 11 endpoints
- AssignmentController: 12 endpoints
- SubmissionController: 14 endpoints
- GradeController: 10 endpoints
- AttendanceController: 12 endpoints
- DegreeProgressController: 13 endpoints
- PaymentController: 13 endpoints
- FeeController: 8 endpoints
- CourseController: 9 endpoints

### Authentication & Authorization
Most endpoints require authentication via JWT token in Authorization header.
Permission levels vary:
- Student: Can access own data and shared resources
- Faculty: Can access assigned courses and students
- Admin/SuperAdmin: Full access to all resources

### Data Formats
- **Dates:** LocalDate format (YYYY-MM-DD)
- **DateTimes:** LocalDateTime format (YYYY-MM-DDTHH:MM:SS)
- **File Uploads:** Multipart form data
- **Large Numbers:** Use Long for IDs, BigDecimal for currency

### Common Response Codes
- 200: OK - Successful GET/PUT/PATCH
- 201: Created - Successful POST
- 204: No Content - Successful DELETE
- 400: Bad Request - Invalid parameters
- 401: Unauthorized - Missing/invalid authentication
- 403: Forbidden - Insufficient permissions
- 404: Not Found - Resource doesn't exist
- 409: Conflict - Business logic conflict
- 500: Server Error

