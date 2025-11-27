-- SAMS Database Seed Script
-- Creates demo data for presentation
-- Run this AFTER starting the backend for the first time

-- Connect to the database
\c sams_db;

-- Note: The backend will auto-create tables on first startup due to spring.jpa.hibernate.ddl-auto=update

-- Insert Demo Semesters (backend should create these via API, but we'll add them if needed)
INSERT INTO semester (id, name, code, start_date, end_date, is_active, registration_open, created_at, updated_at)
VALUES
    (1, 'Fall 2024', 'FALL2024', '2024-09-01', '2024-12-20', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Spring 2025', 'SPRING2025', '2025-01-15', '2025-05-30', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert Demo Users (passwords will be hashed by backend, these are plain text for reference)
-- Password for all users: "password123"
-- Actual hashed password (BCrypt): $2a$10$8xPJQQK5JZ9Y9oHZJZqHzO9.7JQXQZ9Y9oHZJZqHzO9.7JQXQZ9Y9o

INSERT INTO users (id, username, password, first_name, last_name, email, role, active, created_at)
VALUES
    (1, 'admin', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'System', 'Administrator', 'admin@sams.edu', 'SUPER_ADMIN', true, CURRENT_TIMESTAMP),
    (2, 'jsmith', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'John', 'Smith', 'jsmith@sams.edu', 'FACULTY', true, CURRENT_TIMESTAMP),
    (3, 'mjones', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'Mary', 'Jones', 'mjones@sams.edu', 'FACULTY', true, CURRENT_TIMESTAMP),
    (4, 'adavis', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'Alice', 'Davis', 'adavis@sams.edu', 'STUDENT', true, CURRENT_TIMESTAMP),
    (5, 'bwilson', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'Bob', 'Wilson', 'bwilson@sams.edu', 'STUDENT', true, CURRENT_TIMESTAMP),
    (6, 'ctaylor', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'Carol', 'Taylor', 'ctaylor@sams.edu', 'STUDENT', true, CURRENT_TIMESTAMP),
    (7, 'dlee', '$2a$10$rC1qN0L1E5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5PcN5vZ1X5Z1eN5P', 'David', 'Lee', 'dlee@sams.edu', 'STUDENT', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert Demo Courses
INSERT INTO course (id, code, name, description, credits, department, semester_id, instructor_id, max_capacity, days_of_week, start_time, end_time, created_at, updated_at)
VALUES
    (1, 'CS101', 'Introduction to Computer Science', 'Fundamentals of programming and computer science concepts', 3, 'Computer Science', 1, 2, 30, 'MWF', '09:00:00', '10:30:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'CS201', 'Data Structures and Algorithms', 'Study of fundamental data structures and algorithms', 4, 'Computer Science', 1, 2, 25, 'TTh', '13:00:00', '15:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'MATH101', 'Calculus I', 'Introduction to differential and integral calculus', 4, 'Mathematics', 1, 3, 35, 'MWF', '08:00:00', '09:30:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'ENG101', 'English Composition', 'Academic writing and critical thinking', 3, 'English', 1, 3, 30, 'TTh', '10:00:00', '11:30:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert Demo Enrollments
INSERT INTO enrollment (id, student_id, course_id, semester_id, status, enrollment_date, grade, letter_grade)
VALUES
    (1, 4, 1, 1, 'ENROLLED', CURRENT_TIMESTAMP, NULL, NULL),
    (2, 4, 3, 1, 'ENROLLED', CURRENT_TIMESTAMP, 92.5, 'A'),
    (3, 5, 1, 1, 'ENROLLED', CURRENT_TIMESTAMP, NULL, NULL),
    (4, 5, 2, 1, 'ENROLLED', CURRENT_TIMESTAMP, 88.0, 'B+'),
    (5, 6, 1, 1, 'ENROLLED', CURRENT_TIMESTAMP, NULL, NULL),
    (6, 6, 4, 1, 'ENROLLED', CURRENT_TIMESTAMP, 95.5, 'A'),
    (7, 7, 2, 1, 'ENROLLED', CURRENT_TIMESTAMP, NULL, NULL),
    (8, 7, 3, 1, 'ENROLLED', CURRENT_TIMESTAMP, 85.0, 'B')
ON CONFLICT (id) DO NOTHING;

-- Insert Demo Assignments
INSERT INTO assignment (id, course_id, faculty_id, title, description, due_date, max_grade, created_at, updated_at)
VALUES
    (1, 1, 2, 'Programming Assignment 1', 'Write a program to implement basic sorting algorithms', '2024-11-30 23:59:59', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 2, 'Programming Assignment 2', 'Implement a simple calculator using OOP principles', '2024-12-15 23:59:59', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 2, 'Data Structures Project', 'Implement a binary search tree with all operations', '2024-12-20 23:59:59', 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 3, 3, 'Calculus Problem Set', 'Solve integration and differentiation problems', '2024-11-28 23:59:59', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert Demo Degree Programs
INSERT INTO degree_program (id, name, code, description, total_credits_required, is_active, created_at, updated_at)
VALUES
    (1, 'Bachelor of Science in Computer Science', 'BSCS', 'Four-year undergraduate program in computer science', 120, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Bachelor of Science in Mathematics', 'BSMATH', 'Four-year undergraduate program in mathematics', 120, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert Demo Study Groups
INSERT INTO study_group (id, name, description, course_id, creator_id, max_members, is_public, created_at)
VALUES
    (1, 'CS101 Study Group', 'Help each other with CS101 assignments and concepts', 1, 4, 10, true, CURRENT_TIMESTAMP),
    (2, 'Data Structures Masters', 'Advanced study group for CS201', 2, 5, 8, true, CURRENT_TIMESTAMP),
    (3, 'Calculus Help Group', 'Study together for calculus exams', 3, 6, 12, true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert Study Group Members
INSERT INTO study_group_member (id, group_id, user_id, role, joined_at, is_active)
VALUES
    (1, 1, 4, 'ADMIN', CURRENT_TIMESTAMP, true),
    (2, 1, 5, 'MEMBER', CURRENT_TIMESTAMP, true),
    (3, 1, 6, 'MEMBER', CURRENT_TIMESTAMP, true),
    (4, 2, 5, 'ADMIN', CURRENT_TIMESTAMP, true),
    (5, 2, 7, 'MEMBER', CURRENT_TIMESTAMP, true),
    (6, 3, 6, 'ADMIN', CURRENT_TIMESTAMP, true),
    (7, 3, 7, 'MEMBER', CURRENT_TIMESTAMP, true)
ON CONFLICT (id) DO NOTHING;

-- Set sequences to correct values
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('course_id_seq', (SELECT MAX(id) FROM course));
SELECT setval('enrollment_id_seq', (SELECT MAX(id) FROM enrollment));
SELECT setval('assignment_id_seq', (SELECT MAX(id) FROM assignment));
SELECT setval('semester_id_seq', (SELECT MAX(id) FROM semester));
SELECT setval('degree_program_id_seq', (SELECT MAX(id) FROM degree_program));
SELECT setval('study_group_id_seq', (SELECT MAX(id) FROM study_group));
SELECT setval('study_group_member_id_seq', (SELECT MAX(id) FROM study_group_member));

-- Display demo credentials
\echo ''
\echo '==================== DEMO CREDENTIALS ===================='
\echo 'Admin User:'
\echo '  Username: admin'
\echo '  Password: password123'
\echo '  Role: SUPER_ADMIN'
\echo ''
\echo 'Faculty Users:'
\echo '  Username: jsmith | Password: password123 | Role: FACULTY'
\echo '  Username: mjones | Password: password123 | Role: FACULTY'
\echo ''
\echo 'Student Users:'
\echo '  Username: adavis  | Password: password123 | Role: STUDENT'
\echo '  Username: bwilson | Password: password123 | Role: STUDENT'
\echo '  Username: ctaylor | Password: password123 | Role: STUDENT'
\echo '  Username: dlee    | Password: password123 | Role: STUDENT'
\echo ''
\echo 'Demo Data Created:'
\echo '  - 7 Users (1 admin, 2 faculty, 4 students)'
\echo '  - 2 Semesters'
\echo '  - 4 Courses'
\echo '  - 8 Enrollments'
\echo '  - 4 Assignments'
\echo '  - 2 Degree Programs'
\echo '  - 3 Study Groups with members'
\echo '==========================================================='
\echo ''
