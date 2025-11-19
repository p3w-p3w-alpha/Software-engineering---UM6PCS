# SAMS Database Design Rationale

## Purpose
This document explains the reasoning behind every major design decision in the SAMS database schema. It traces each decision back to SRS requirements and best practices in database design.

---

## 1. Overall Architecture Decisions

### 1.1 PostgreSQL as Database Choice

**Decision:** Use PostgreSQL as the relational database management system

**Rationale:**
- **SRS Constraint:** Technical constraints specify PostgreSQL as the mandated RDBMS
- **Feature Support:** PostgreSQL supports advanced features like ENUM types, JSON columns, triggers, and views
- **Data Integrity:** Strong ACID compliance ensures reliable academic data management
- **Performance:** Efficient indexing and query optimization for 100+ concurrent users (NFR-1.2)
- **Spring Boot Integration:** Excellent support with Spring Data JPA and Hibernate

**Alternatives Considered:**
- MySQL: Good but less feature-rich than PostgreSQL
- MongoDB: NoSQL not suitable for transactional academic data with complex relationships

**SRS Requirements Satisfied:** Technical Constraints, NFR-3.2 (Fault Tolerance), NFR-3.3 (Data Backup)

---

### 1.2 Normalized Design (3NF)

**Decision:** Design database schema to Third Normal Form (3NF)

**Rationale:**
- **Data Integrity:** Eliminates redundancy and update anomalies
- **SRS FR-2.5:** GPA calculations require accurate, non-duplicated grade data
- **Maintenance:** Easier to maintain and update normalized data
- **Scalability:** Better long-term scalability as system grows beyond 100 students (NFR-1.4)

**Strategic Denormalization:**
- Added `current_enrollment` to `courses` table (can be calculated) for real-time availability checks
- Added `student_id` and `course_id` to `grades` table (derivable from enrollment) for faster queries

**SRS Requirements Satisfied:** NFR-5.1 (Code Quality), NFR-5.4 (Modularity), NFR-1.4 (Scalability)

---

### 1.3 Use of ENUM Types

**Decision:** Define custom ENUM types for status fields (user_role, enrollment_status, payment_status, etc.)

**Rationale:**
- **Data Integrity:** Prevents invalid values in status columns
- **Database-Level Enforcement:** Ensures consistency without application-level validation
- **Performance:** More efficient than CHECK constraints with VARCHAR
- **Self-Documenting:** Schema clearly shows allowed values

**Example:**
```sql
CREATE TYPE enrollment_status AS ENUM ('ENROLLED', 'WAITLISTED', 'DROPPED', 'COMPLETED');
```

**SRS Requirements Satisfied:** NFR-3.2 (Fault Tolerance), NFR-5.1 (Code Quality)

---

## 2. Entity-Specific Design Decisions

### 2.1 Users Table - Single Table for All User Types

**Decision:** Use one `users` table with a `role` column instead of separate tables for each user type

**Rationale:**
- **Authentication:** FR-1.1 requires unified login system for all user types
- **Simplicity:** Single authentication logic in Spring Security
- **Role-Based Access Control:** FR-1.2 requires role-based redirects after login
- **Flexibility:** Easy to add new roles (e.g., ADVISOR, DEAN) in future

**Alternative Considered:**
- Separate tables (students_auth, faculty_auth, admin_auth): Would complicate authentication

**Implementation:**
```sql
role user_role NOT NULL DEFAULT 'STUDENT'
```

**SRS Requirements Satisfied:** FR-1.1, FR-1.2, FR-1.3 (User Authentication and Authorization)

---

### 2.2 Student and Faculty Tables - Separate from Users

**Decision:** Create dedicated `students` and `faculty` tables with 1:1 relationship to `users`

**Rationale:**
- **Data Organization:** Students and faculty have completely different attributes
- **Avoid NULL Columns:** Prevents having columns like `student_number` NULL for faculty users
- **SRS Requirements:** FR-2.x (Student Portal) and FR-3.x (Faculty Portal) have distinct data needs
- **Maintainability:** Easier to modify student-specific or faculty-specific fields

**Relationship Design:**
```sql
student_id INTEGER PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE
```
- `student_id` is both PK and FK (enforces 1:1 relationship)
- `ON DELETE CASCADE` ensures data consistency

**SRS Requirements Satisfied:** FR-2.1 (Academic Dashboard), FR-3.1 (Course Offering Creation)

---

### 2.3 GPA Fields in Students Table

**Decision:** Store `cumulative_gpa` and `semester_gpa` directly in `students` table

**Rationale:**
- **SRS FR-2.5:** Real-time GPA display on student dashboard
- **Performance:** NFR-1.1 requires page loads within 3 seconds - calculating GPA from grades on every query would be too slow
- **Calculated Fields:** These are auto-updated by triggers when grades are entered
- **Transcript Generation:** Quick access to GPA without complex joins and aggregations

**Update Mechanism:**
- Trigger on `grades` table insertion/update recalculates GPA
- Alternative: Calculate on-the-fly (rejected due to performance concerns for 100 students)

**SRS Requirements Satisfied:** FR-2.5 (GPA & Transcript Access), NFR-1.1 (Response Time)

---

### 2.4 Courses Table - Comprehensive Scheduling

**Decision:** Include scheduling details directly in `courses` table (days, times, room, etc.)

**Rationale:**
- **SRS FR-2.2:** Course registration requires viewing class schedules
- **SRS FR-5.1:** System must detect schedule conflicts during registration
- **Single Source of Truth:** All course information in one place
- **Simplicity:** Avoids complex normalization with separate scheduling tables

**Fields Included:**
```sql
schedule_days VARCHAR(20),  -- "MWF", "TR"
schedule_time VARCHAR(50),  -- "09:00-09:50"
room_location VARCHAR(50)
```

**Conflict Detection:**
- Application logic (Spring Boot) parses schedule strings to detect overlaps
- Could be enhanced with time range types in future iterations

**SRS Requirements Satisfied:** FR-2.2 (Course Registration), FR-2.3 (Class Schedule Management)

---

### 2.5 Enrollment Capacity Tracking

**Decision:** Store `current_enrollment` and `current_waitlist` in `courses` table, auto-updated by triggers

**Rationale:**
- **SRS FR-2.2:** System prevents registration when courses are at capacity
- **Real-Time Updates:** NFR-1.1 requires instant seat availability
- **Performance:** Counting enrollments on every course search would be inefficient
- **Automatic Updates:** Triggers ensure counts are always accurate

**Trigger Implementation:**
```sql
CREATE TRIGGER trigger_update_course_enrollment
AFTER INSERT OR UPDATE OR DELETE ON enrollments
FOR EACH ROW EXECUTE FUNCTION update_course_enrollment_count();
```

**Alternative Considered:**
- Calculate capacity on-the-fly: Rejected due to performance impact

**SRS Requirements Satisfied:** FR-2.2 (Course Registration), NFR-1.1 (Response Time)

---

### 2.6 Prerequisites as TEXT Field

**Decision:** Store course prerequisites as comma-separated course codes in a TEXT column

**Rationale:**
- **Simplicity:** Most courses have 0-2 prerequisites
- **SRS FR-2.2:** System must verify prerequisite completion before registration
- **Implementation:** Easy to parse in Java (Spring Boot) application logic

**Example:**
```sql
prerequisites TEXT  -- "CS101,CS102" or NULL
```

**Alternative Considered:**
- Separate `course_prerequisites` junction table: More normalized but overkill for simple prerequisites

**Future Enhancement:**
- Could normalize if prerequisite logic becomes more complex (AND/OR conditions, minimum grades, etc.)

**SRS Requirements Satisfied:** FR-2.2 (Course Registration - Prerequisite Verification)

---

### 2.7 Enrollments Table - Central Integration Point

**Decision:** Create `enrollments` as the central table linking students, courses, payments, and grades

**Rationale:**
- **SRS FR-5.1:** Complete course registration workflow requires tracking enrollment status
- **Payment Integration:** FR-5.3 requires payment before course access
- **Waitlist Management:** FR-2.2 requires managing waitlists when courses are full
- **Grade Association:** Grades are linked to specific enrollment instances

**Key Fields:**
```sql
status enrollment_status DEFAULT 'ENROLLED',  -- ENROLLED, WAITLISTED, DROPPED, COMPLETED
payment_status payment_status DEFAULT 'PENDING',
course_access_granted BOOLEAN DEFAULT FALSE
```

**Workflow Support:**
1. Student registers → enrollment created with status='ENROLLED' or 'WAITLISTED'
2. Student pays → payment_status='VALIDATED'
3. Admin validates → course_access_granted=TRUE
4. Semester ends → status='COMPLETED', final_grade entered

**SRS Requirements Satisfied:** FR-2.2, FR-5.1, FR-5.3 (Registration, Payment, Access Workflows)

---

### 2.8 Grades Table - Granular Tracking

**Decision:** Store individual assignment/exam grades separately, not just final course grades

**Rationale:**
- **SRS FR-3.4:** Faculty must enter grades for assignments, exams, and finals separately
- **SRS FR-2.5:** Students need to view detailed grade breakdown, not just final letter grade
- **GPA Calculation:** Enables weighted GPA calculations if needed
- **Academic Analytics:** Supports trend analysis and performance insights

**Denormalization Decision:**
```sql
student_id INTEGER NOT NULL,
course_id INTEGER NOT NULL
```
- These can be derived from `enrollment_id` but stored for faster queries
- Enables quick student transcript queries without joining through enrollments

**SRS Requirements Satisfied:** FR-3.4 (Grade Entry), FR-2.5 (Grade Viewing), FR-5.2 (Grade Submission Workflow)

---

### 2.9 Payments Table - Manual Validation Workflow

**Decision:** Implement multi-stage payment status with admin validation

**Rationale:**
- **SRS FR-2.7:** Payment processing requires manual validation, not automated gateway (initial version)
- **SRS FR-4.4:** Administrators must validate payments before granting access
- **SRS FR-5.3:** Complete payment and access workflow defined
- **Audit Trail:** Tracks who validated payment and when

**Payment States:**
1. **PENDING:** Student hasn't submitted payment yet
2. **SUBMITTED:** Student uploaded payment proof
3. **VALIDATED:** Admin confirmed payment
4. **REJECTED:** Admin rejected payment (with reason)

**Integration with Enrollments:**
```sql
courses_covered TEXT  -- JSON array like "[1,2,3]"
```
- Stores which course enrollments this payment covers
- Supports paying for multiple courses in one transaction

**SRS Requirements Satisfied:** FR-2.7, FR-4.4, FR-4.5, FR-5.3 (Payment Processing & Validation)

---

### 2.10 Study Groups - Many-to-Many with Junction Table

**Decision:** Use `study_groups` and `study_group_members` junction table for many-to-many relationship

**Rationale:**
- **SRS FR-2.8:** Students can create and join multiple study groups
- **Flexibility:** Each student can be in multiple groups; each group has multiple students
- **Membership Tracking:** Junction table tracks join/leave dates and active status
- **Group Limits:** `max_members` in study_groups table enforces size limits

**Schema:**
```sql
study_groups (group_id, course_id, created_by, max_members)
study_group_members (membership_id, group_id, student_id, joined_date, is_active)
```

**SRS Requirements Satisfied:** FR-2.8 (Study Group & Collaboration Tools)

---

### 2.11 Notifications Table - Polymorphic References

**Decision:** Use polymorphic reference pattern for notifications (related_entity_type + related_entity_id)

**Rationale:**
- **SRS FR-2.9:** Notifications relate to various entities (grades, courses, payments, deadlines)
- **Flexibility:** One table can reference any entity type
- **Simplicity:** Avoids creating separate notification tables for each entity type

**Implementation:**
```sql
related_entity_type VARCHAR(50),  -- 'COURSE', 'GRADE', 'ENROLLMENT', 'PAYMENT'
related_entity_id INTEGER
```

**Notification Types Supported:**
- GRADE_POSTED → related to Grade
- DEADLINE_REMINDER → related to Course
- PAYMENT_DUE → related to Enrollment
- PAYMENT_CONFIRMED → related to Payment

**SRS Requirements Satisfied:** FR-2.9 (Intelligent Notifications & Analytics)

---

## 3. Data Integrity Decisions

### 3.1 Foreign Key Constraints with CASCADE/SET NULL

**Decision:** Use appropriate ON DELETE behaviors for each foreign key

**Rationale:**
- **Data Consistency:** Prevents orphaned records
- **SRS NFR-3.2:** Fault tolerance requires preventing data corruption

**Cascade Strategy:**
| Relationship | ON DELETE | Reason |
|--------------|-----------|--------|
| User → Student/Faculty | CASCADE | Student/faculty profile meaningless without user account |
| Student → Enrollment | CASCADE | Enrollments belong to student |
| Course → Enrollment | CASCADE | Enrollments invalid if course deleted |
| Enrollment → Grade | CASCADE | Grades belong to specific enrollment |
| Course → StudyGroup | CASCADE | Study groups are course-specific |
| Faculty → Course | SET NULL | Course can be reassigned to another faculty |
| Faculty → Grade | SET NULL | Preserve grade even if faculty leaves |

**SRS Requirements Satisfied:** NFR-3.2 (Fault Tolerance), NFR-5.1 (Code Quality)

---

### 3.2 CHECK Constraints for Data Validation

**Decision:** Implement database-level validation with CHECK constraints

**Rationale:**
- **SRS NFR-3.2:** Prevents invalid data at database level (defense in depth)
- **Examples:**
  - GPA must be between 0.00 and 4.00
  - Credits must be positive
  - Email must match valid format
  - Graduation date must be after admission date

**Sample Constraints:**
```sql
CONSTRAINT chk_gpa_range CHECK (cumulative_gpa >= 0.00 AND cumulative_gpa <= 4.00),
CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
```

**SRS Requirements Satisfied:** NFR-3.2 (Fault Tolerance), Data Validation (Part of FR-5.x workflows)

---

### 3.3 Unique Constraints for Business Rules

**Decision:** Enforce uniqueness at database level where business rules require it

**Rationale:**
- **Data Integrity:** Prevents duplicate registrations, usernames, emails
- **SRS FR-1.1:** Usernames and emails must be unique for authentication

**Unique Constraints:**
```sql
username VARCHAR(50) UNIQUE NOT NULL
email VARCHAR(100) UNIQUE NOT NULL
student_number VARCHAR(20) UNIQUE NOT NULL
CONSTRAINT uq_student_course_semester UNIQUE(student_id, course_id, semester, academic_year)
CONSTRAINT uq_course_semester UNIQUE(course_code, semester, academic_year)
```

**SRS Requirements Satisfied:** FR-1.1 (User Login), FR-2.2 (Course Registration - prevent duplicate enrollments)

---

## 4. Performance Optimization Decisions

### 4.1 Strategic Indexing

**Decision:** Create indexes on frequently queried columns

**Rationale:**
- **SRS NFR-1.1:** Page loads must complete within 3 seconds
- **SRS NFR-1.2:** System must support 100 concurrent users

**Index Strategy:**
- All primary keys (automatic)
- All foreign keys (for joins)
- Frequently searched columns (email, username, course_code)
- Status columns (enrollment.status, payment.status)
- Date columns for range queries (enrollment_date, payment_date)

**Examples:**
```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_courses_semester ON courses(semester, academic_year);
CREATE INDEX idx_enrollments_status ON enrollments(status);
```

**SRS Requirements Satisfied:** NFR-1.1 (Response Time), NFR-1.2 (Throughput)

---

### 4.2 Materialized Data (GPA, Enrollment Counts)

**Decision:** Store calculated values (GPA, enrollment counts) rather than computing on every query

**Rationale:**
- **SRS NFR-1.1:** Real-time display requirements
- **SRS FR-2.5:** GPA must update immediately when grades are entered but display quickly
- **Trade-off:** Storage space vs. query performance (performance wins for academic data)

**Auto-Update Mechanisms:**
- Triggers recalculate when source data changes
- Application logic can also trigger updates

**SRS Requirements Satisfied:** NFR-1.1 (Response Time), NFR-1.4 (GPA updates within 1 second)

---

### 4.3 Database Views for Complex Queries

**Decision:** Create views for commonly needed complex queries

**Rationale:**
- **Simplification:** Encapsulates complex joins for application layer
- **Consistency:** Ensures same query logic used everywhere
- **Performance:** PostgreSQL can optimize view queries

**Views Created:**
```sql
student_schedule  -- Student course schedules with instructor info
course_enrollment_summary  -- Course capacity and availability
```

**SRS Requirements Satisfied:** NFR-1.1 (Response Time), NFR-5.1 (Code Quality)

---

## 5. Audit and Compliance Decisions

### 5.1 Timestamp Tracking (created_at, updated_at)

**Decision:** Add `created_at` and `updated_at` to all core tables

**Rationale:**
- **Audit Trail:** Track when records were created and last modified
- **SRS NFR-2.5:** Security requirements include audit logging
- **Debugging:** Helps troubleshoot data issues
- **Compliance:** Academic institutions often have data retention policies

**Auto-Update Trigger:**
```sql
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

**SRS Requirements Satisfied:** NFR-2.5 (Audit Trail), NFR-3.3 (Data Backup)

---

### 5.2 Soft Deletes vs. Hard Deletes

**Decision:** Use hard deletes with CASCADE but preserve audit data where needed

**Rationale:**
- **Enrollment History:** Completed enrollments remain in database
- **Grade History:** Never delete grades (academic records must be permanent)
- **User Accounts:** Set `is_active=FALSE` instead of deleting (soft delete pattern)
- **Payments:** Never delete payments (financial audit requirements)

**Implementation:**
```sql
is_active BOOLEAN DEFAULT TRUE  -- in users, study_groups
```

**SRS Requirements Satisfied:** NFR-2.5 (Audit Trail), FR-2.5 (Transcript Generation - historical data)

---

## 6. Security-Related Design Decisions

### 6.1 Password Storage

**Decision:** Store `password_hash` (BCrypt hashed), never plain text passwords

**Rationale:**
- **SRS NFR-2.1:** Secure password storage using encryption
- **Best Practice:** Industry standard for password security
- **Spring Security Integration:** BCrypt is default hashing algorithm

**Schema:**
```sql
password_hash VARCHAR(255) NOT NULL  -- BCrypt hashes are 60 chars
```

**SRS Requirements Satisfied:** NFR-2.1 (Authentication Security), NFR-2.3 (Data Protection)

---

### 6.2 Role-Based Access via Database Role Column

**Decision:** Implement RBAC with `role` ENUM in `users` table

**Rationale:**
- **SRS FR-1.2:** Role-based access control required
- **Simplicity:** Single column determines portal access
- **Spring Security Integration:** Easy to configure role-based endpoint security

**SRS Requirements Satisfied:** FR-1.2 (Role-Based Access Control), NFR-2.2 (Authorization)

---

## 7. Scalability Considerations

### 7.1 Design for Growth Beyond 100 Students

**Decision:** Schema supports unlimited students, courses, and semesters

**Rationale:**
- **SRS NFR-1.4:** Architecture must accommodate future growth
- **No Hard Limits:** No table design limits system to 100 students
- **Partitioning Ready:** Tables can be partitioned by semester/year if needed in future

**SRS Requirements Satisfied:** NFR-1.4 (Scalability), NFR-1.3 (Capacity)

---

### 7.2 Semester-Based Data Partitioning Readiness

**Decision:** Include `semester` and `academic_year` in enrollments, payments, and grades

**Rationale:**
- **Future Partitioning:** Can partition tables by semester for better performance at scale
- **Archiving:** Can archive old semesters to separate tables
- **Queries:** Most queries filter by current semester

**SRS Requirements Satisfied:** NFR-1.4 (Scalability), NFR-3.3 (Data Backup - selective semester backups)

---

## 8. Traceability to SRS Requirements

| Design Decision | SRS Requirements Satisfied |
|-----------------|---------------------------|
| PostgreSQL Choice | Technical Constraints, NFR-3.2, NFR-3.3 |
| 3NF Normalization | NFR-5.1, NFR-5.4, NFR-1.4 |
| ENUM Types | NFR-3.2, NFR-5.1 |
| Unified Users Table | FR-1.1, FR-1.2, FR-1.3 |
| Separate Student/Faculty Tables | FR-2.x, FR-3.x |
| GPA Storage | FR-2.5, NFR-1.1, NFR-1.4 |
| Enrollment Tracking | FR-2.2, FR-5.1, FR-5.3 |
| Granular Grades | FR-3.4, FR-2.5, FR-5.2 |
| Payment Validation | FR-2.7, FR-4.4, FR-5.3 |
| Study Groups M:N | FR-2.8 |
| Polymorphic Notifications | FR-2.9 |
| Indexes | NFR-1.1, NFR-1.2 |
| Triggers | NFR-1.1, NFR-1.4 |
| Audit Fields | NFR-2.5, NFR-3.3 |
| Password Hashing | NFR-2.1, NFR-2.3 |

---

**Document Status:** Complete
**Design Philosophy:** Balance normalization with performance, enforce integrity at database level, support SRS workflows
**Next Step:** Normalization analysis documentation
