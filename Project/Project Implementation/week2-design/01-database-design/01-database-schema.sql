-- ========================================
-- SAMS (Student Academic Management System)
-- Database Schema - PostgreSQL
-- ========================================
-- Created for: Week 2 Design Phase
-- Database: PostgreSQL 13+
-- Purpose: Complete schema for academic management
-- ========================================

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS study_group_members CASCADE;
DROP TABLE IF EXISTS study_groups CASCADE;
DROP TABLE IF EXISTS grades CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS faculty CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS degree_requirements CASCADE;

-- Drop custom types if they exist
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS enrollment_status CASCADE;
DROP TYPE IF EXISTS payment_status CASCADE;
DROP TYPE IF EXISTS payment_method CASCADE;
DROP TYPE IF EXISTS grade_type CASCADE;
DROP TYPE IF EXISTS notification_type CASCADE;
DROP TYPE IF EXISTS academic_standing CASCADE;

-- ========================================
-- CUSTOM ENUM TYPES
-- ========================================

-- user roles for the system (student, faculty, or admin)
CREATE TYPE user_role AS ENUM ('STUDENT', 'FACULTY', 'ADMIN');

-- enrollment status values
CREATE TYPE enrollment_status AS ENUM ('ENROLLED', 'WAITLISTED', 'DROPPED', 'COMPLETED');

-- payment status tracking
CREATE TYPE payment_status AS ENUM ('PENDING', 'SUBMITTED', 'VALIDATED', 'REJECTED');

-- payment methods accepted
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD', 'BANK_TRANSFER', 'CASH');

-- types of grades in the system
CREATE TYPE grade_type AS ENUM ('ASSIGNMENT', 'EXAM', 'MIDTERM', 'FINAL', 'QUIZ', 'PROJECT');

-- notification categories
CREATE TYPE notification_type AS ENUM (
    'GRADE_POSTED',
    'DEADLINE_REMINDER',
    'REGISTRATION_OPEN',
    'PAYMENT_DUE',
    'PAYMENT_CONFIRMED',
    'COURSE_FULL',
    'WAITLIST_MOVED',
    'ANNOUNCEMENT'
);

-- academic standing for students
CREATE TYPE academic_standing AS ENUM ('GOOD', 'PROBATION', 'SUSPENDED', 'GRADUATED');


-- ========================================
-- TABLE: users
-- ========================================
-- this is the main table for all user accounts
-- handles auth for students faculty and admins

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- bcrypt hashed password
    role user_role NOT NULL DEFAULT 'STUDENT',

    -- personal informations
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    date_of_birth DATE,

    -- account status
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,

    -- audit fields (who and when)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_username_length CHECK (LENGTH(username) >= 3),
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_phone_format CHECK (phone_number IS NULL OR phone_number ~ '^\+?[0-9]{10,15}$')
);

-- indexes for faster queries on users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

-- comment on table
COMMENT ON TABLE users IS 'Central user table for authentication and authorization';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password - never store plain text!';


-- ========================================
-- TABLE: students
-- ========================================
-- student-specific academic informations
-- linked to users table

CREATE TABLE students (
    student_id INTEGER PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    student_number VARCHAR(20) UNIQUE NOT NULL,  -- university ID like "S2024001"
    major VARCHAR(100) NOT NULL,
    minor VARCHAR(100),  -- optional minor

    -- academic tracking
    admission_date DATE NOT NULL,
    expected_graduation DATE,
    actual_graduation DATE,
    total_credits_completed DECIMAL(5,2) DEFAULT 0.00,
    total_credits_required DECIMAL(5,2) DEFAULT 120.00,  -- typical bachelor degree

    -- GPA calculations (updated automaticly)
    cumulative_gpa DECIMAL(3,2) DEFAULT 0.00,  -- range 0.00 to 4.00
    semester_gpa DECIMAL(3,2) DEFAULT 0.00,
    academic_standing academic_standing DEFAULT 'GOOD',

    -- contact and address
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    current_address TEXT,

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints for data integrity
    CONSTRAINT chk_gpa_range CHECK (cumulative_gpa >= 0.00 AND cumulative_gpa <= 4.00),
    CONSTRAINT chk_semester_gpa_range CHECK (semester_gpa >= 0.00 AND semester_gpa <= 4.00),
    CONSTRAINT chk_credits_positive CHECK (total_credits_completed >= 0),
    CONSTRAINT chk_graduation_after_admission CHECK (expected_graduation IS NULL OR expected_graduation > admission_date)
);

-- indexes to improve query performance
CREATE INDEX idx_students_major ON students(major);
CREATE INDEX idx_students_standing ON students(academic_standing);
CREATE INDEX idx_students_student_number ON students(student_number);

COMMENT ON TABLE students IS 'Student academic profile and progress tracking';


-- ========================================
-- TABLE: faculty
-- ========================================
-- faculty members who teach courses

CREATE TABLE faculty (
    faculty_id INTEGER PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    employee_number VARCHAR(20) UNIQUE NOT NULL,  -- like "FAC2024001"
    department VARCHAR(100) NOT NULL,
    title VARCHAR(50) NOT NULL,  -- Professor, Associate Professor, Assistant Professor, Lecturer

    -- office info
    office_location VARCHAR(100),
    office_hours TEXT,  -- can store like "MW 2-4pm, F 10-12pm"

    -- employment details
    hire_date DATE NOT NULL,
    contract_type VARCHAR(20) DEFAULT 'FULL_TIME',  -- FULL_TIME, PART_TIME, ADJUNCT

    -- contact
    office_phone VARCHAR(20),

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_title_valid CHECK (title IN ('Professor', 'Associate Professor', 'Assistant Professor', 'Lecturer', 'Teaching Assistant'))
);

-- index for department queries
CREATE INDEX idx_faculty_department ON faculty(department);
CREATE INDEX idx_faculty_title ON faculty(title);

COMMENT ON TABLE faculty IS 'Faculty member profiles and teaching information';


-- ========================================
-- TABLE: courses
-- ========================================
-- course offerings for each semester
-- this is where faculty creates the courses

CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL,  -- like "CS301", "MATH201"
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    credits DECIMAL(3,1) NOT NULL DEFAULT 3.0,
    department VARCHAR(100) NOT NULL,

    -- instructor assignment
    instructor_id INTEGER REFERENCES faculty(faculty_id) ON DELETE SET NULL,

    -- scheduling informations
    semester VARCHAR(20) NOT NULL,  -- "Fall", "Spring", "Summer"
    academic_year INTEGER NOT NULL,  -- 2024, 2025, etc.
    schedule_days VARCHAR(20),  -- "MWF", "TR", etc.
    schedule_time VARCHAR(50),  -- "09:00-09:50", "14:00-15:30"
    room_location VARCHAR(50),

    -- enrollment limits
    max_capacity INTEGER NOT NULL DEFAULT 30,
    current_enrollment INTEGER DEFAULT 0,
    waitlist_capacity INTEGER DEFAULT 10,
    current_waitlist INTEGER DEFAULT 0,

    -- prerequsites (can be comma-separated course codes or NULL)
    prerequisites TEXT,  -- like "CS101,CS102" or NULL

    -- financial
    course_fee DECIMAL(10,2) NOT NULL DEFAULT 500.00,

    -- course status
    is_active BOOLEAN DEFAULT TRUE,
    registration_open BOOLEAN DEFAULT FALSE,

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_credits_positive CHECK (credits > 0),
    CONSTRAINT chk_capacity_positive CHECK (max_capacity > 0),
    CONSTRAINT chk_enrollment_valid CHECK (current_enrollment >= 0 AND current_enrollment <= max_capacity),
    CONSTRAINT chk_waitlist_valid CHECK (current_waitlist >= 0 AND current_waitlist <= waitlist_capacity),
    CONSTRAINT chk_semester_valid CHECK (semester IN ('Fall', 'Spring', 'Summer', 'Winter')),
    CONSTRAINT chk_fee_positive CHECK (course_fee >= 0),
    CONSTRAINT uq_course_semester UNIQUE(course_code, semester, academic_year)  -- prevent duplicate offerings
);

-- indexes for common queries
CREATE INDEX idx_courses_code ON courses(course_code);
CREATE INDEX idx_courses_semester ON courses(semester, academic_year);
CREATE INDEX idx_courses_department ON courses(department);
CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_courses_active ON courses(is_active);

COMMENT ON TABLE courses IS 'Course offerings and scheduling information';
COMMENT ON COLUMN courses.prerequisites IS 'Comma-separated list of required course codes';


-- ========================================
-- TABLE: enrollments
-- ========================================
-- links students to courses
-- tracks enrollment status and payment

CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,

    -- enrollment tracking
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status enrollment_status DEFAULT 'ENROLLED',
    dropped_date TIMESTAMP,

    -- semester info
    semester VARCHAR(20) NOT NULL,
    academic_year INTEGER NOT NULL,

    -- payment tracking
    payment_status payment_status DEFAULT 'PENDING',
    course_access_granted BOOLEAN DEFAULT FALSE,  -- only true after payment validated

    -- waitlist position (if applicable)
    waitlist_position INTEGER,

    -- grade reference (will be filled when course completes)
    final_grade VARCHAR(2),  -- A, B+, B, C+, etc.
    grade_points DECIMAL(3,2),  -- 4.0, 3.7, 3.0, etc.

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT uq_student_course_semester UNIQUE(student_id, course_id, semester, academic_year),
    CONSTRAINT chk_waitlist_position_positive CHECK (waitlist_position IS NULL OR waitlist_position > 0),
    CONSTRAINT chk_dropped_after_enrollment CHECK (dropped_date IS NULL OR dropped_date >= enrollment_date)
);

-- indexes for faster lookups
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_semester ON enrollments(semester, academic_year);
CREATE INDEX idx_enrollments_payment ON enrollments(payment_status);

COMMENT ON TABLE enrollments IS 'Student course registrations and enrollment tracking';


-- ========================================
-- TABLE: grades
-- ========================================
-- stores grades for assignments exams and final grades
-- used for GPA calculation

CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    enrollment_id INTEGER NOT NULL REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    student_id INTEGER NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,

    -- grade details
    grade_type grade_type NOT NULL,
    grade_name VARCHAR(100),  -- "Assignment 1", "Midterm Exam", etc.

    -- scoring
    grade_value DECIMAL(5,2) NOT NULL,  -- actual score received
    max_points DECIMAL(5,2) NOT NULL,  -- maximum possible points
    weight_percentage DECIMAL(5,2),  -- what % of final grade (if specified)

    -- grade metadata
    grade_date DATE DEFAULT CURRENT_DATE,
    entered_by INTEGER REFERENCES faculty(faculty_id) ON DELETE SET NULL,
    comments TEXT,

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_grade_value_valid CHECK (grade_value >= 0 AND grade_value <= max_points),
    CONSTRAINT chk_max_points_positive CHECK (max_points > 0),
    CONSTRAINT chk_weight_valid CHECK (weight_percentage IS NULL OR (weight_percentage >= 0 AND weight_percentage <= 100))
);

-- indexes for grade queries
CREATE INDEX idx_grades_enrollment ON grades(enrollment_id);
CREATE INDEX idx_grades_student ON grades(student_id);
CREATE INDEX idx_grades_course ON grades(course_id);
CREATE INDEX idx_grades_type ON grades(grade_type);
CREATE INDEX idx_grades_date ON grades(grade_date);

COMMENT ON TABLE grades IS 'Individual grade records for assignments, exams, and finals';


-- ========================================
-- TABLE: payments
-- ========================================
-- tracks student payments for course fees

CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,

    -- payment amount and method
    amount DECIMAL(10,2) NOT NULL,
    payment_method payment_method NOT NULL,
    transaction_reference VARCHAR(100),  -- bank reference number or card transaction ID

    -- payment status and validation
    status payment_status DEFAULT 'PENDING',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validated_by INTEGER REFERENCES users(user_id) ON DELETE SET NULL,  -- admin who validated
    validated_date TIMESTAMP,
    rejection_reason TEXT,

    -- what this payment covers
    semester VARCHAR(20) NOT NULL,
    academic_year INTEGER NOT NULL,
    courses_covered TEXT,  -- JSON array of course_ids like "[1,2,3]"

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_validated_date_after_payment CHECK (validated_date IS NULL OR validated_date >= payment_date)
);

-- indexes
CREATE INDEX idx_payments_student ON payments(student_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_semester ON payments(semester, academic_year);
CREATE INDEX idx_payments_date ON payments(payment_date);

COMMENT ON TABLE payments IS 'Student payment records and validation tracking';


-- ========================================
-- TABLE: study_groups
-- ========================================
-- student collaboration groups for courses

CREATE TABLE study_groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    created_by INTEGER NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,

    -- group details
    description TEXT,
    max_members INTEGER DEFAULT 10,
    current_members INTEGER DEFAULT 1,  -- creator is first member

    -- group status
    is_active BOOLEAN DEFAULT TRUE,

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_max_members_positive CHECK (max_members > 0),
    CONSTRAINT chk_current_members_valid CHECK (current_members >= 0 AND current_members <= max_members)
);

-- indexes
CREATE INDEX idx_study_groups_course ON study_groups(course_id);
CREATE INDEX idx_study_groups_creator ON study_groups(created_by);
CREATE INDEX idx_study_groups_active ON study_groups(is_active);

COMMENT ON TABLE study_groups IS 'Student study groups for course collaboration';


-- ========================================
-- TABLE: study_group_members
-- ========================================
-- junction table for students and study groups (many-to-many)

CREATE TABLE study_group_members (
    membership_id SERIAL PRIMARY KEY,
    group_id INTEGER NOT NULL REFERENCES study_groups(group_id) ON DELETE CASCADE,
    student_id INTEGER NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,

    -- membership tracking
    joined_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    left_date TIMESTAMP,

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT uq_group_student UNIQUE(group_id, student_id),
    CONSTRAINT chk_left_after_joined CHECK (left_date IS NULL OR left_date >= joined_date)
);

-- indexes
CREATE INDEX idx_group_members_group ON study_group_members(group_id);
CREATE INDEX idx_group_members_student ON study_group_members(student_id);
CREATE INDEX idx_group_members_active ON study_group_members(is_active);

COMMENT ON TABLE study_group_members IS 'Study group membership junction table';


-- ========================================
-- TABLE: notifications
-- ========================================
-- system notifications for users

CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,

    -- notification content
    notification_type notification_type NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,

    -- notification status
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,

    -- related entity (polymorphic reference)
    related_entity_type VARCHAR(50),  -- 'COURSE', 'GRADE', 'ENROLLMENT', 'PAYMENT'
    related_entity_id INTEGER,

    -- notification priority
    priority VARCHAR(20) DEFAULT 'NORMAL',  -- HIGH, NORMAL, LOW

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_priority_valid CHECK (priority IN ('HIGH', 'NORMAL', 'LOW'))
);

-- indexes for notifications
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(notification_type);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_created ON notifications(created_at DESC);

COMMENT ON TABLE notifications IS 'User notifications and alerts';


-- ========================================
-- TABLE: degree_requirements (OPTIONAL)
-- ========================================
-- defines graduation requirements by major

CREATE TABLE degree_requirements (
    requirement_id SERIAL PRIMARY KEY,
    major VARCHAR(100) NOT NULL,
    requirement_category VARCHAR(50) NOT NULL,  -- 'CORE', 'ELECTIVE', 'GENERAL_ED', 'CAPSTONE'
    required_credits DECIMAL(5,2) NOT NULL,
    required_courses TEXT,  -- JSON array or comma-separated
    description TEXT,

    -- timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- constraints
    CONSTRAINT chk_required_credits_positive CHECK (required_credits > 0),
    CONSTRAINT uq_major_category UNIQUE(major, requirement_category)
);

-- index
CREATE INDEX idx_degree_requirements_major ON degree_requirements(major);

COMMENT ON TABLE degree_requirements IS 'Degree requirements for automated progress tracking';


-- ========================================
-- TRIGGERS FOR AUTOMATIC UPDATES
-- ========================================

-- trigger function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- apply trigger to all tables with updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_students_updated_at BEFORE UPDATE ON students
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_faculty_updated_at BEFORE UPDATE ON faculty
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_courses_updated_at BEFORE UPDATE ON courses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_enrollments_updated_at BEFORE UPDATE ON enrollments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_grades_updated_at BEFORE UPDATE ON grades
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_study_groups_updated_at BEFORE UPDATE ON study_groups
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- ========================================
-- TRIGGER: Update course enrollment counts
-- ========================================

CREATE OR REPLACE FUNCTION update_course_enrollment_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        -- update enrollment count for the course
        UPDATE courses
        SET current_enrollment = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = NEW.course_id AND status = 'ENROLLED'
        ),
        current_waitlist = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = NEW.course_id AND status = 'WAITLISTED'
        )
        WHERE course_id = NEW.course_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE courses
        SET current_enrollment = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = OLD.course_id AND status = 'ENROLLED'
        ),
        current_waitlist = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = OLD.course_id AND status = 'WAITLISTED'
        )
        WHERE course_id = OLD.course_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_course_enrollment
AFTER INSERT OR UPDATE OR DELETE ON enrollments
FOR EACH ROW EXECUTE FUNCTION update_course_enrollment_count();


-- ========================================
-- SAMPLE DATA FOR TESTING
-- ========================================

-- Insert sample users (passwords would be hashed in real system)
INSERT INTO users (username, email, password_hash, role, first_name, last_name, phone_number) VALUES
('admin', 'admin@sams.edu', '$2a$10$dummyhash1', 'ADMIN', 'System', 'Administrator', '+1234567890'),
('jdoe', 'john.doe@student.edu', '$2a$10$dummyhash2', 'STUDENT', 'John', 'Doe', '+1234567891'),
('msmith', 'mary.smith@student.edu', '$2a$10$dummyhash3', 'STUDENT', 'Mary', 'Smith', '+1234567892'),
('martinez', 'prof.martinez@faculty.edu', '$2a$10$dummyhash4', 'FACULTY', 'Carlos', 'Martinez', '+1234567893'),
('johnson', 'prof.johnson@faculty.edu', '$2a$10$dummyhash5', 'FACULTY', 'Sarah', 'Johnson', '+1234567894');

-- Insert sample students
INSERT INTO students (student_id, student_number, major, admission_date, expected_graduation, total_credits_completed, cumulative_gpa) VALUES
(2, 'S2024001', 'Computer Science', '2024-09-01', '2028-05-31', 30.0, 3.5),
(3, 'S2024002', 'Computer Science', '2024-09-01', '2028-05-31', 45.0, 3.8);

-- Insert sample faculty
INSERT INTO faculty (faculty_id, employee_number, department, title, hire_date, office_location, office_hours) VALUES
(4, 'FAC2020001', 'Computer Science', 'Professor', '2020-08-15', 'Building A, Room 301', 'MWF 2-4pm'),
(5, 'FAC2021002', 'Computer Science', 'Associate Professor', '2021-08-20', 'Building A, Room 305', 'TR 10am-12pm');

-- Insert sample courses
INSERT INTO courses (course_code, course_name, description, credits, department, instructor_id, semester, academic_year, schedule_days, schedule_time, max_capacity, course_fee) VALUES
('CS101', 'Introduction to Programming', 'Fundamentals of programming using Java', 3.0, 'Computer Science', 4, 'Fall', 2024, 'MWF', '09:00-09:50', 30, 500.00),
('CS201', 'Data Structures', 'Arrays, linked lists, trees, graphs, and algorithms', 3.0, 'Computer Science', 4, 'Fall', 2024, 'TR', '14:00-15:30', 25, 500.00),
('CS301', 'Database Systems', 'Relational databases, SQL, and database design', 3.0, 'Computer Science', 5, 'Fall', 2024, 'MWF', '10:00-10:50', 30, 500.00);

-- Insert sample enrollments
INSERT INTO enrollments (student_id, course_id, semester, academic_year, status, payment_status) VALUES
(2, 1, 'Fall', 2024, 'ENROLLED', 'VALIDATED'),
(2, 2, 'Fall', 2024, 'ENROLLED', 'VALIDATED'),
(3, 1, 'Fall', 2024, 'ENROLLED', 'PENDING');

-- Insert sample grades
INSERT INTO grades (enrollment_id, student_id, course_id, grade_type, grade_name, grade_value, max_points, entered_by) VALUES
(1, 2, 1, 'ASSIGNMENT', 'Assignment 1', 95.0, 100.0, 4),
(1, 2, 1, 'EXAM', 'Midterm Exam', 88.0, 100.0, 4);

-- Insert sample payment
INSERT INTO payments (student_id, amount, payment_method, transaction_reference, status, semester, academic_year, courses_covered, validated_by) VALUES
(2, 1000.00, 'CREDIT_CARD', 'TXN123456789', 'VALIDATED', 'Fall', 2024, '[1,2]', 1);

-- Insert sample study group
INSERT INTO study_groups (group_name, course_id, created_by, description, max_members) VALUES
('CS101 Study Group', 1, 2, 'Weekly study sessions for CS101 students', 8);

-- Insert study group member
INSERT INTO study_group_members (group_id, student_id) VALUES
(1, 2);

-- Insert sample notification
INSERT INTO notifications (user_id, notification_type, title, message, related_entity_type, related_entity_id) VALUES
(2, 'GRADE_POSTED', 'New Grade Posted', 'Your grade for Assignment 1 in CS101 has been posted.', 'GRADE', 1);


-- ========================================
-- USEFUL VIEWS FOR COMMON QUERIES
-- ========================================

-- view for student course schedule
CREATE OR REPLACE VIEW student_schedule AS
SELECT
    s.student_id,
    u.first_name,
    u.last_name,
    c.course_code,
    c.course_name,
    c.schedule_days,
    c.schedule_time,
    c.room_location,
    f.first_name AS instructor_first_name,
    f.last_name AS instructor_last_name,
    e.status,
    e.semester,
    e.academic_year
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN users u ON s.student_id = u.user_id
JOIN courses c ON e.course_id = c.course_id
LEFT JOIN faculty fac ON c.instructor_id = fac.faculty_id
LEFT JOIN users f ON fac.faculty_id = f.user_id
WHERE e.status IN ('ENROLLED', 'WAITLISTED');

-- view for course enrollment summary
CREATE OR REPLACE VIEW course_enrollment_summary AS
SELECT
    c.course_id,
    c.course_code,
    c.course_name,
    c.semester,
    c.academic_year,
    c.max_capacity,
    c.current_enrollment,
    c.current_waitlist,
    (c.max_capacity - c.current_enrollment) AS available_seats,
    CONCAT(u.first_name, ' ', u.last_name) AS instructor_name
FROM courses c
LEFT JOIN faculty f ON c.instructor_id = f.faculty_id
LEFT JOIN users u ON f.faculty_id = u.user_id;


-- ========================================
-- END OF SCHEMA
-- ========================================

-- display success message
DO $$
BEGIN
    RAISE NOTICE 'SAMS database schema created successfully!';
    RAISE NOTICE 'Total tables: 11';
    RAISE NOTICE 'Total views: 2';
    RAISE NOTICE 'Sample data inserted for testing';
END $$;
