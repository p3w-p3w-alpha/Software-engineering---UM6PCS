package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for grade information
 * used for showing student grades in courses - seperate from assignment grades
 */
public class GradeResponse {

    // grade record id
    private Long id;

    // student info
    private StudentInfo student;

    // course info
    private CourseInfo course;

    // letter grade (A, B+, C, etc)
    private String gradeValue;

    // numeric point value (4.0, 3.7, etc)
    private Double gradePoints;

    // when grade was created
    private LocalDateTime createdAt;

    // whether grade has been finalized
    private Boolean finalized;

    // when it was finalized
    private LocalDateTime finalizedAt;

    public GradeResponse() {
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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
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

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }

    public Double getGradePoints() {
        return gradePoints;
    }

    public void setGradePoints(Double gradePoints) {
        this.gradePoints = gradePoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getFinalized() {
        return finalized;
    }

    public void setFinalized(Boolean finalized) {
        this.finalized = finalized;
    }

    public LocalDateTime getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
}
