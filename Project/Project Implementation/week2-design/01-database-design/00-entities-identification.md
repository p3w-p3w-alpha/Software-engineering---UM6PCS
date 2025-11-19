# SAMS Database Entities Identification

## Purpose
This document identifies all the main entities needed for the Student Academic Management System (SAMS) based on the SRS requirements and features breakdown.

---

## Core Entities Identified

### 1. **User**
**Purpose:** Central entity for all system users (students, faculty, admins)

**Justification:**
- SRS FR-1.1, FR-1.2 requires secure login with role-based access
- Supports three distinct user roles as per requirements
- Needed for authentication and authorization throughout the system

**Key Attributes:**
- user_id (PK)
- username, password, email
- role (STUDENT, FACULTY, ADMIN)
- first_name, last_name
- created_at, updated_at

**Relationships:**
- One user can be ONE student (1:1 for students)
- One user can be ONE faculty member (1:1 for faculty)
- Ties to enrollments, courses, study groups, etc.

---

### 2. **Student**
**Purpose:** Stores student-specific academic information

**Justification:**
- SRS FR-2.4 requires degree progress tracking
- FR-2.5 requires GPA calculation and transcript generation
- Students have unique academic data beyond basic user info

**Key Attributes:**
- student_id (PK, FK to User)
- major, student_number
- admission_date, expected_graduation
- total_credits_completed
- cumulative_gpa, semester_gpa
- academic_standing

**Relationships:**
- Links to User (1:1)
- Has many Enrollments
- Has many Grades
- Can join many StudyGroups

---

### 3. **Faculty**
**Purpose:** Stores faculty-specific information and teaching data

**Justification:**
- SRS FR-3.1 requires course offering creation by faculty
- FR-3.4 requires grade entry capabilities
- Faculty members have different data needs than students

**Key Attributes:**
- faculty_id (PK, FK to User)
- department, title (Professor, Associate Prof, etc.)
- office_location, office_hours
- hire_date

**Relationships:**
- Links to User (1:1)
- Teaches many Courses
- Enters many Grades

---

### 4. **Course**
**Purpose:** Defines course offerings for each semester

**Justification:**
- SRS FR-2.2 requires course registration and scheduling
- FR-3.1 requires faculty to create course offerings
- Central to the entire academic management system

**Key Attributes:**
- course_id (PK)
- course_code (e.g., "CS301")
- course_name (e.g., "Data Structures")
- description, credits
- department
- instructor_id (FK to Faculty)
- semester, academic_year
- schedule (days/times)
- max_capacity, current_enrollment
- prerequisites (can be another course_id or comma-separated)
- course_fee

**Relationships:**
- Taught by ONE Faculty member
- Has many Enrollments
- Has many Grades
- Can have Prerequisites (self-referencing)

---

### 5. **Enrollment**
**Purpose:** Links students to courses and tracks enrollment status

**Justification:**
- SRS FR-2.2 requires course registration with waitlist management
- FR-5.1 describes complete course registration workflow
- Need to track enrollment status (enrolled, waitlisted, dropped)

**Key Attributes:**
- enrollment_id (PK)
- student_id (FK to Student)
- course_id (FK to Course)
- enrollment_date
- status (ENROLLED, WAITLISTED, DROPPED, COMPLETED)
- semester, academic_year
- payment_status (PENDING, PAID, VALIDATED)

**Relationships:**
- Links ONE Student to ONE Course (many-to-many resolver)
- Connected to Payment
- Can have Grades

---

### 6. **Grade**
**Purpose:** Stores grades for assignments, exams, and final course grades

**Justification:**
- SRS FR-3.4 requires faculty grade entry
- FR-2.5 requires real-time GPA calculation
- Need to track both individual assignment grades and final grades

**Key Attributes:**
- grade_id (PK)
- enrollment_id (FK to Enrollment)
- student_id (FK to Student)
- course_id (FK to Course)
- grade_type (ASSIGNMENT, EXAM, FINAL)
- grade_value (numeric score or letter grade)
- max_points (for assignments/exams)
- grade_date
- entered_by (FK to Faculty)
- comments

**Relationships:**
- Belongs to ONE Enrollment
- Entered by ONE Faculty member
- Used for GPA calculations

---

### 7. **Payment**
**Purpose:** Tracks student payments for course enrollments

**Justification:**
- SRS FR-2.7 requires payment processing
- FR-4.4 requires admin payment validation
- FR-2.6 requires financial information display

**Key Attributes:**
- payment_id (PK)
- student_id (FK to Student)
- amount, payment_method (CREDIT_CARD, BANK_TRANSFER)
- payment_date
- transaction_reference
- status (SUBMITTED, VALIDATED, REJECTED)
- validated_by (FK to User - admin)
- validated_date
- semester, academic_year
- courses_covered (JSON array of course_ids)

**Relationships:**
- Made by ONE Student
- Validated by ONE Administrator
- Covers multiple Course enrollments

---

### 8. **StudyGroup**
**Purpose:** Enables student collaboration for specific courses

**Justification:**
- SRS FR-2.8 requires study group creation and management
- Students need to coordinate study sessions

**Key Attributes:**
- group_id (PK)
- group_name
- course_id (FK to Course)
- created_by (FK to Student)
- created_date
- description
- max_members

**Relationships:**
- Associated with ONE Course
- Created by ONE Student
- Has many Student members (many-to-many)

---

### 9. **StudyGroupMember** (Junction Table)
**Purpose:** Resolves many-to-many relationship between Students and StudyGroups

**Justification:**
- Students can join multiple study groups
- Study groups can have multiple students

**Key Attributes:**
- membership_id (PK)
- group_id (FK to StudyGroup)
- student_id (FK to Student)
- joined_date
- is_active

---

### 10. **Notification**
**Purpose:** Stores system notifications for users

**Justification:**
- SRS FR-2.9 requires intelligent notifications
- Need to track notification history and read status

**Key Attributes:**
- notification_id (PK)
- user_id (FK to User)
- notification_type (GRADE_POSTED, DEADLINE_REMINDER, REGISTRATION_OPEN, etc.)
- title, message
- is_read
- created_at
- related_entity_type (COURSE, GRADE, ENROLLMENT, etc.)
- related_entity_id

**Relationships:**
- Sent to ONE User
- Can relate to various entities (polymorphic)

---

### 11. **DegreeRequirement** (Optional - for advanced tracking)
**Purpose:** Defines graduation requirements for different majors

**Justification:**
- SRS FR-2.4 requires automated degree progress tracking
- Need to know what courses/credits are required for each major

**Key Attributes:**
- requirement_id (PK)
- major
- requirement_type (CORE, ELECTIVE, GENERAL_ED)
- required_credits
- specific_courses (JSON array or separate table)
- description

---

## Entity Relationship Summary

```
User (1) ──────── (1) Student
User (1) ──────── (1) Faculty

Student (1) ────── (*) Enrollment
Course (1) ────── (*) Enrollment
Enrollment (*) ────── (1) Payment

Faculty (1) ────── (*) Course (teaches)
Course (1) ────── (*) Grade
Student (1) ────── (*) Grade
Enrollment (1) ────── (*) Grade

Course (1) ────── (*) StudyGroup
StudyGroup (*) ────── (*) Student (through StudyGroupMember)

User (1) ────── (*) Notification
```

---

## Rationale for 8+ Tables

The initial design includes 10-11 core tables, which satisfies the "8+ tables" requirement:

1. **User** - Authentication and base user data
2. **Student** - Student-specific academic info
3. **Faculty** - Faculty-specific info
4. **Course** - Course offerings and scheduling
5. **Enrollment** - Student-course registration
6. **Grade** - Academic performance tracking
7. **Payment** - Financial transactions
8. **StudyGroup** - Collaborative learning
9. **StudyGroupMember** - Group membership junction
10. **Notification** - System alerts
11. **DegreeRequirement** (optional) - Graduation tracking

This design is **normalized to 3NF** (no transitive dependencies), supports all SRS functional requirements, and provides a solid foundation for the Spring Boot backend implementation.

---

## Traceability to SRS Requirements

| Entity | SRS Requirements Satisfied |
|--------|---------------------------|
| User | FR-1.1, FR-1.2, FR-1.3 (Authentication & Authorization) |
| Student | FR-2.1, FR-2.4, FR-2.5 (Student Portal, Progress Tracking, GPA) |
| Faculty | FR-3.1, FR-3.4, FR-3.5 (Faculty Portal, Grade Management) |
| Course | FR-2.2, FR-3.1, FR-5.1 (Course Registration, Course Creation) |
| Enrollment | FR-2.2, FR-5.1 (Registration Workflow, Waitlists) |
| Grade | FR-2.5, FR-3.4, FR-5.2 (GPA Calculation, Grade Entry) |
| Payment | FR-2.6, FR-2.7, FR-4.4, FR-5.3 (Financial Management, Payment Validation) |
| StudyGroup | FR-2.8 (Collaboration Tools) |
| Notification | FR-2.9 (Intelligent Notifications) |

---

**Document Status:** Complete
**Last Updated:** Week 2 - Database Design Phase
**Next Step:** Create detailed database schema (01-database-schema.sql)
