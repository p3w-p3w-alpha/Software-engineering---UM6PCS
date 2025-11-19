# SAMS Database Normalization Analysis

## Purpose
This document provides a detailed analysis of how the SAMS database schema conforms to database normalization forms (1NF, 2NF, 3NF), identifies any denormalization decisions, and justifies them.

---

## What is Database Normalization?

**Definition:** Database normalization is the process of organizing data to minimize redundancy and dependency by dividing large tables into smaller tables and defining relationships between them.

**Goals:**
1. Eliminate redundant data
2. Ensure data dependencies make sense
3. Reduce data anomalies (insertion, update, deletion)
4. Improve data integrity

---

## Normal Forms Overview

### First Normal Form (1NF)
**Requirements:**
- All columns contain atomic (indivisible) values
- Each column contains values of a single type
- Each column has a unique name
- The order of rows doesn't matter
- Each row is unique (has a primary key)

### Second Normal Form (2NF)
**Requirements:**
- Must be in 1NF
- All non-key attributes are fully dependent on the entire primary key
- No partial dependencies (applies to composite primary keys)

### Third Normal Form (3NF)
**Requirements:**
- Must be in 2NF
- No transitive dependencies (non-key attributes depend only on primary key, not on other non-key attributes)

---

## Table-by-Table Normalization Analysis

### 1. **users** Table

**Schema:**
```sql
users (
    user_id PK,
    username, email, password_hash, role,
    first_name, last_name, phone_number, date_of_birth,
    is_active, is_email_verified, last_login,
    created_at, updated_at
)
```

**1NF Compliance:** ✅
- All columns contain atomic values
- `user_id` is the primary key
- No repeating groups
- Each column has a unique name

**2NF Compliance:** ✅
- Primary key is single column (`user_id`), so no partial dependencies possible
- All non-key attributes depend on `user_id`

**3NF Compliance:** ✅
- No transitive dependencies
- All non-key attributes depend directly on `user_id`, not on each other
- Example: `first_name` depends on `user_id`, not on `username`

**Conclusion:** Fully normalized to 3NF

---

### 2. **students** Table

**Schema:**
```sql
students (
    student_id PK,FK,
    student_number, major, minor,
    admission_date, expected_graduation, actual_graduation,
    total_credits_completed, total_credits_required,
    cumulative_gpa, semester_gpa, academic_standing,
    emergency_contact_name, emergency_contact_phone, current_address,
    created_at, updated_at
)
```

**1NF Compliance:** ✅
- All columns are atomic
- Primary key: `student_id`
- No repeating groups

**2NF Compliance:** ✅
- Single-column primary key, no partial dependencies

**3NF Compliance:** ⚠️ **Calculated Fields Present**
- **Potential Issue:** `cumulative_gpa` and `semester_gpa` can be calculated from `grades` table
- **Design Decision:** Keep these fields for performance (denormalization for query speed)
- **Justification:**
  - SRS NFR-1.1 requires page loads < 3 seconds
  - Calculating GPA from grades on every dashboard load would be too slow
  - Triggers auto-update these fields when grades change
  - Trade-off: Storage space vs. query performance (performance wins)

**Other Attributes:**
- `total_credits_completed`: Could be calculated but stored for performance
- All other fields have no transitive dependencies

**Conclusion:** Intentionally denormalized for performance; otherwise 3NF

---

### 3. **faculty** Table

**Schema:**
```sql
faculty (
    faculty_id PK,FK,
    employee_number, department, title,
    office_location, office_hours,
    hire_date, contract_type, office_phone,
    created_at, updated_at
)
```

**1NF Compliance:** ✅
- All columns atomic
- Primary key: `faculty_id`

**2NF Compliance:** ✅
- Single-column PK

**3NF Compliance:** ✅
- No transitive dependencies
- `office_location` depends on `faculty_id`, not on `department` (faculty can be in different buildings)

**Conclusion:** Fully normalized to 3NF

---

### 4. **courses** Table

**Schema:**
```sql
courses (
    course_id PK,
    course_code, course_name, description, credits, department,
    instructor_id FK,
    semester, academic_year, schedule_days, schedule_time, room_location,
    max_capacity, current_enrollment, waitlist_capacity, current_waitlist,
    prerequisites, course_fee,
    is_active, registration_open,
    created_at, updated_at
)
```

**1NF Compliance:** ⚠️ **Minor Violation (Accepted)**
- **Issue:** `prerequisites` column stores comma-separated values ("CS101,CS102")
- **Violation:** Not truly atomic (contains list of values)
- **Justification:**
  - Simplifies design for simple prerequisite relationships
  - Most courses have 0-2 prerequisites
  - Alternative would be junction table `course_prerequisites (course_id, prerequisite_course_id)`
  - Current design is "good enough" for initial version
- **Future Enhancement:** Normalize to junction table if prerequisite logic becomes complex

**All other columns:** Atomic ✅

**2NF Compliance:** ✅
- Single-column PK

**3NF Compliance:** ⚠️ **Calculated Fields Present**
- **Issue:** `current_enrollment` and `current_waitlist` can be calculated from `enrollments` table
- **Design Decision:** Denormalized for real-time availability queries
- **Justification:**
  - SRS FR-2.2 requires instant course capacity checks
  - Counting enrollments for every course search would impact performance (NFR-1.1)
  - Triggers auto-update these counts when enrollments change
  - Critical for user experience during registration periods

**Conclusion:** Intentionally denormalized; prerequisites could be normalized

---

### 5. **enrollments** Table

**Schema:**
```sql
enrollments (
    enrollment_id PK,
    student_id FK, course_id FK,
    enrollment_date, status, dropped_date,
    semester, academic_year,
    payment_status, course_access_granted,
    waitlist_position,
    final_grade, grade_points,
    created_at, updated_at
)
```

**1NF Compliance:** ✅
- All columns atomic
- Primary key: `enrollment_id`

**2NF Compliance:** ✅
- Single-column PK

**3NF Compliance:** ✅
- No transitive dependencies
- `semester` and `academic_year` could be derived from `course_id` but stored here for historical accuracy (course might be deleted, but enrollment record persists)
- `final_grade` and `grade_points` are summary data but necessary for transcript generation

**Unique Constraint:**
```sql
UNIQUE(student_id, course_id, semester, academic_year)
```
- Prevents duplicate enrollments
- Ensures business rule: student can't enroll in same course twice in same semester

**Conclusion:** Fully normalized to 3NF

---

### 6. **grades** Table

**Schema:**
```sql
grades (
    grade_id PK,
    enrollment_id FK, student_id FK, course_id FK,
    grade_type, grade_name,
    grade_value, max_points, weight_percentage,
    grade_date, entered_by FK, comments,
    created_at, updated_at
)
```

**1NF Compliance:** ✅
- All columns atomic

**2NF Compliance:** ✅
- Single-column PK

**3NF Compliance:** ⚠️ **Intentional Denormalization**
- **Issue:** `student_id` and `course_id` can be derived from `enrollment_id`
- **Design Decision:** Include them for query performance
- **Justification:**
  - Student transcript queries: `SELECT * FROM grades WHERE student_id = ?`
  - Course grade analytics: `SELECT AVG(grade_value) FROM grades WHERE course_id = ?`
  - Without these fields, every query would require joining through `enrollments`
  - Trade-off: Redundancy vs. query performance (performance wins for academic data)

**All other fields:** No transitive dependencies ✅

**Conclusion:** Intentionally denormalized for query performance

---

### 7. **payments** Table

**Schema:**
```sql
payments (
    payment_id PK,
    student_id FK,
    amount, payment_method, transaction_reference,
    status, payment_date, validated_by FK, validated_date, rejection_reason,
    semester, academic_year, courses_covered,
    created_at, updated_at
)
```

**1NF Compliance:** ⚠️ **Minor Violation (Accepted)**
- **Issue:** `courses_covered` stores JSON array like "[1,2,3]"
- **Violation:** Not atomic (contains multiple course IDs)
- **Justification:**
  - Simplified payment tracking (one payment covers multiple courses)
  - Alternative: Junction table `payment_courses (payment_id, course_id, amount)`
  - Current design acceptable for initial version
  - PostgreSQL supports JSON queries if needed

**2NF Compliance:** ✅
- Single-column PK

**3NF Compliance:** ✅
- `semester` and `academic_year` could be derived from courses but stored for historical record
- No other transitive dependencies

**Conclusion:** Minor 1NF violation accepted; otherwise normalized

---

### 8. **study_groups** Table

**Schema:**
```sql
study_groups (
    group_id PK,
    group_name, course_id FK, created_by FK,
    description, max_members, current_members,
    is_active, created_at, updated_at
)
```

**1NF Compliance:** ✅

**2NF Compliance:** ✅

**3NF Compliance:** ⚠️ **Calculated Field**
- **Issue:** `current_members` can be counted from `study_group_members`
- **Justification:** Performance (avoid counting on every group list query)
- **Auto-Updated:** Trigger updates count when members join/leave

**Conclusion:** Intentionally denormalized for performance

---

### 9. **study_group_members** Table

**Schema:**
```sql
study_group_members (
    membership_id PK,
    group_id FK, student_id FK,
    joined_date, is_active, left_date,
    created_at
)
```

**1NF Compliance:** ✅

**2NF Compliance:** ✅

**3NF Compliance:** ✅
- Junction table for many-to-many relationship
- Fully normalized

**Conclusion:** Fully normalized to 3NF

---

### 10. **notifications** Table

**Schema:**
```sql
notifications (
    notification_id PK,
    user_id FK,
    notification_type, title, message,
    is_read, read_at,
    related_entity_type, related_entity_id,
    priority, created_at
)
```

**1NF Compliance:** ✅

**2NF Compliance:** ✅

**3NF Compliance:** ✅
- **Note:** Polymorphic reference (`related_entity_type` + `related_entity_id`) is a denormalization pattern
- **Justification:** Flexibility to reference any entity (grades, courses, payments) without multiple FK columns
- **Acceptable:** Common pattern in notification systems

**Conclusion:** Normalized with acceptable polymorphic pattern

---

### 11. **degree_requirements** Table

**Schema:**
```sql
degree_requirements (
    requirement_id PK,
    major, requirement_category,
    required_credits, required_courses,
    description, created_at, updated_at
)
```

**1NF Compliance:** ⚠️ **Minor Violation**
- **Issue:** `required_courses` may store comma-separated or JSON list
- **Justification:** Similar to `prerequisites` - simplifies configuration

**2NF Compliance:** ✅

**3NF Compliance:** ✅

**Conclusion:** Minor 1NF violation for simplicity

---

## Summary of Normalization Status

| Table | 1NF | 2NF | 3NF | Denormalization Notes |
|-------|-----|-----|-----|----------------------|
| users | ✅ | ✅ | ✅ | Fully normalized |
| students | ✅ | ✅ | ⚠️ | GPA fields stored for performance |
| faculty | ✅ | ✅ | ✅ | Fully normalized |
| courses | ⚠️ | ✅ | ⚠️ | Prerequisites comma-separated; enrollment counts stored |
| enrollments | ✅ | ✅ | ✅ | Fully normalized |
| grades | ✅ | ✅ | ⚠️ | student_id, course_id denormalized for performance |
| payments | ⚠️ | ✅ | ✅ | courses_covered JSON array |
| study_groups | ✅ | ✅ | ⚠️ | current_members count stored |
| study_group_members | ✅ | ✅ | ✅ | Fully normalized |
| notifications | ✅ | ✅ | ✅ | Polymorphic reference pattern |
| degree_requirements | ⚠️ | ✅ | ✅ | required_courses list |

---

## Justified Denormalization Decisions

### 1. **GPA Storage in students Table**
- **What:** `cumulative_gpa` and `semester_gpa`
- **Why:** Performance (SRS NFR-1.1 - 3 second page loads)
- **Mitigation:** Triggers auto-update when grades change
- **Trade-off:** Minimal redundancy vs. significant performance gain

### 2. **Enrollment Counts in courses Table**
- **What:** `current_enrollment` and `current_waitlist`
- **Why:** Real-time availability checks (SRS FR-2.2)
- **Mitigation:** Triggers auto-update on enrollment changes
- **Trade-off:** 2 redundant integers vs. COUNT(*) on every search

### 3. **student_id and course_id in grades Table**
- **What:** Storing FKs that could be derived from enrollment_id
- **Why:** Fast student transcripts and course analytics
- **Mitigation:** Foreign key constraints ensure consistency
- **Trade-off:** 2 redundant FKs per grade vs. extra JOIN on every query

### 4. **Comma-Separated / JSON Lists**
- **What:** prerequisites, courses_covered, required_courses
- **Why:** Simplicity for lists with few items
- **Future:** Can normalize to junction tables if complexity increases

---

## Benefits of This Design

### 1. **Data Integrity**
- Foreign key constraints prevent orphaned records
- CHECK constraints validate data ranges
- UNIQUE constraints prevent duplicates
- ENUM types enforce valid status values

### 2. **Query Performance**
- Strategic denormalization speeds up common queries
- Indexes on all FKs and frequently searched columns
- Views encapsulate complex joins

### 3. **Maintainability**
- Normalized structure makes schema easy to understand
- Clear entity relationships
- Triggers maintain denormalized data automatically

### 4. **Scalability**
- Design supports growth beyond 100 students
- Partition-ready (semester/year fields in transactional tables)
- No hard-coded limits

---

## Anomalies Prevented by Normalization

### **Update Anomaly**
- **Without normalization:** If student's major stored in multiple tables, updating major requires multiple updates
- **With normalization:** Major stored once in `students` table

### **Insertion Anomaly**
- **Without normalization:** Can't add course without instructor
- **With normalization:** Instructor FK allows NULL, course can exist before assignment

### **Deletion Anomaly**
- **Without normalization:** Deleting last student in a major might lose major information
- **With normalization:** Major is attribute of student, not separate entity (in this design)

---

## Normalization Trade-offs

### **Advantages of Our Design:**
1. Minimal redundancy
2. High data integrity
3. Flexible structure for future changes
4. Easy to maintain

### **Performance Considerations:**
1. Some queries require joins (mitigated with indexes and views)
2. GPA calculation would be slow without denormalization
3. Enrollment counts would require COUNT(*) without storage

### **Decision:** Normalize where possible, denormalize strategically for performance

---

## Compliance with SRS Requirements

| Normalization Aspect | SRS Requirement Satisfied |
|----------------------|--------------------------|
| 3NF Design | NFR-5.1 (Code Quality), NFR-5.4 (Modularity) |
| Data Integrity | NFR-3.2 (Fault Tolerance) |
| Performance Optimizations | NFR-1.1 (Response Time < 3s) |
| Scalable Structure | NFR-1.4 (Scalability) |
| Consistent Data | FR-2.5 (Accurate GPA Calculations) |

---

## Recommendations for Future Normalization

### **If System Grows:**

1. **Normalize Prerequisites**
   - Create `course_prerequisites (course_id, prerequisite_id, min_grade)`
   - Supports complex AND/OR prerequisite logic
   - Allows minimum grade requirements

2. **Normalize Payment-Course Relationship**
   - Create `payment_courses (payment_id, course_id, amount_allocated)`
   - Supports partial payments
   - Allows per-course payment tracking

3. **Partition Large Tables**
   - Partition `enrollments`, `grades`, `payments` by semester
   - Improves query performance at scale
   - Simplifies archiving old semesters

4. **Separate Audit Tables**
   - Create `audit_log` table for all data changes
   - Track who changed what and when
   - Supports compliance requirements

---

**Document Status:** Complete
**Overall Assessment:** Database is in 3NF with justified strategic denormalization for performance
**Next Step:** API Design Phase
