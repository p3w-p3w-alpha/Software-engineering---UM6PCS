package com.sams.dto;

import java.time.LocalDateTime;

public class EnrollmentResponse {

    private Long id;
    private StudentInfo student;
    private CourseInfo course;
    private String status;
    private LocalDateTime enrollmentDate;
    private Integer waitlistPosition; // null if not waitlisted

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(Long id, StudentInfo student, CourseInfo course, String status, LocalDateTime enrollmentDate) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    // nested class for student info
    public static class StudentInfo {
        private Long id;
        private String username;
        private String email;

        public StudentInfo() {
        }

        public StudentInfo(Long id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // nested class for course info
    public static class CourseInfo {
        private Long id;
        private String courseCode;
        private String courseName;
        private Integer credits;

        public CourseInfo() {
        }

        public CourseInfo(Long id, String courseCode, String courseName, Integer credits) {
            this.id = id;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.credits = credits;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public Integer getCredits() {
            return credits;
        }

        public void setCredits(Integer credits) {
            this.credits = credits;
        }
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentInfo getStudent() {
        return student;
    }

    public void setStudent(StudentInfo student) {
        this.student = student;
    }

    public CourseInfo getCourse() {
        return course;
    }

    public void setCourse(CourseInfo course) {
        this.course = course;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getWaitlistPosition() {
        return waitlistPosition;
    }

    public void setWaitlistPosition(Integer waitlistPosition) {
        this.waitlistPosition = waitlistPosition;
    }
}
