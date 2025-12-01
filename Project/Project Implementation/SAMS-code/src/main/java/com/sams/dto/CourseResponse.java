package com.sams.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * response object for sending course data to frontend
 * includes enrollment stats and instructor info
 */
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credits;
    // max capacity for the course
    private Integer capacity;
    // how many students currently enrolled
    private Integer enrolledCount;
    // number of students on waitlist
    private Integer waitlistCount;
    // true if course is at capacity
    private boolean isFull;
    // instructor information (nested object)
    private InstructorInfo instructor;
    private LocalDateTime createdAt;

    // schedule info - when teh class meets
    private String daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // prerequiset course codes (courses needed before this one)
    private List<String> prerequisiteCourses;

    public CourseResponse() {
    }

    public CourseResponse(Long id, String courseCode, String courseName, String description,
                          Integer credits, Integer capacity, Integer enrolledCount,
                          boolean isFull, InstructorInfo instructor, LocalDateTime createdAt) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
        this.isFull = isFull;
        this.instructor = instructor;
        this.createdAt = createdAt;
    }

    // nested class for instructor info
    public static class InstructorInfo {
        private Long id;
        private String username;
        private String email;

        public InstructorInfo() {
        }

        public InstructorInfo(Long id, String username, String email) {
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

    // getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(Integer enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public InstructorInfo getInstructor() {
        return instructor;
    }

    public void setInstructor(InstructorInfo instructor) {
        this.instructor = instructor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getWaitlistCount() {
        return waitlistCount;
    }

    public void setWaitlistCount(Integer waitlistCount) {
        this.waitlistCount = waitlistCount;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<String> getPrerequisiteCourses() {
        return prerequisiteCourses;
    }

    public void setPrerequisiteCourses(List<String> prerequisiteCourses) {
        this.prerequisiteCourses = prerequisiteCourses;
    }
}
