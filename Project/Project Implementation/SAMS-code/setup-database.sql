-- ============================================
-- SAMS Database Setup Script
-- ============================================
--
-- How to run this script:
--
-- Option 1: Using psql command line
--   psql -U postgres -f setup-database.sql
--
-- Option 2: Using pgAdmin
--   1. Open pgAdmin
--   2. Connect to PostgreSQL server
--   3. Open Query Tool
--   4. Copy and paste this entire file
--   5. Click Execute (F5)
--
-- ============================================

-- Create database if it doesn't exist
-- Note: You might need to disconnect from sams_db first if it exists
SELECT 'Creating database...' AS status;

-- Drop existing database (CAUTION: This will delete all data!)
-- Uncomment the line below only if you want to start fresh
-- DROP DATABASE IF EXISTS sams_db;

-- Create the database
CREATE DATABASE sams_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Connect to the database
\c sams_db

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE sams_db TO postgres;

-- Create extensions (optional but useful)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

SELECT 'Database setup complete!' AS status;
SELECT 'Database name: sams_db' AS info;
SELECT 'Owner: postgres' AS info;
SELECT 'Now you can run the Spring Boot application!' AS info;

-- ============================================
-- Optional: Insert Sample Data
-- ============================================
--
-- Uncomment the sections below if you want to
-- pre-populate the database with sample data
--
-- Note: Tables will be created automatically by
-- Hibernate when you run the Spring Boot app
-- for the first time.
--
-- ============================================

/*
-- Wait for Spring Boot to create tables first!
-- Then run these inserts:

-- Insert Sample Semester
INSERT INTO semesters (name, start_date, end_date, enrollment_start, enrollment_end, drop_deadline, active, created_at, updated_at)
VALUES
('Fall 2024', '2024-09-01', '2024-12-31', '2024-08-01', '2024-09-15', '2024-10-15', true, NOW(), NOW()),
('Spring 2025', '2025-01-15', '2025-05-31', '2024-12-01', '2025-01-30', '2025-02-28', false, NOW(), NOW());

-- Insert Sample Courses (you'll need to update semester_id after semesters are created)
INSERT INTO courses (course_code, course_name, description, credit_hours, capacity, active, created_at, updated_at)
VALUES
('CS101', 'Introduction to Computer Science', 'Basic programming concepts and problem-solving using Python', 3, 30, true, NOW(), NOW()),
('CS102', 'Data Structures', 'Fundamental data structures and algorithms', 4, 25, true, NOW(), NOW()),
('MATH201', 'Calculus I', 'Differential and integral calculus', 4, 35, true, NOW(), NOW()),
('MATH202', 'Linear Algebra', 'Vector spaces, matrices, and linear transformations', 3, 30, true, NOW(), NOW()),
('ENG101', 'Technical Writing', 'Professional and technical communication skills', 3, 40, true, NOW(), NOW()),
('PHYS101', 'Physics I', 'Classical mechanics and thermodynamics', 4, 25, true, NOW(), NOW());

-- Note: User accounts should be created through the registration page
-- for proper password hashing
*/
