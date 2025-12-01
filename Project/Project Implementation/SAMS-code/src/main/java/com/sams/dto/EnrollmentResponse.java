package com.sams.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * response object for course enrollments
 * used for showing which courses students are registered in
 */
public class EnrollmentResponse {

    // enrollment record id
    private Long id;

    // student information
    private StudentInfo student;

    // course information
    private CourseInfo course;

    // enrollment status (ENROLLED, WAITLISTED, DROPPED, etc)
    private String status;

    // when student enrolled
    private LocalDateTime enrollmentDate;

    // position in waitlist (null if not waitlisted)
    private Integer waitlistPosition;

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

    // nested class for instructor info
    public static class InstructorInfo {
        private Long id;
        private String username;
        private String email;
        private String name;
        private String department;

        public InstructorInfo() {
        }

        public InstructorInfo(Long id, String username, String email, String name, String department) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.name = name;
            this.department = department;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }

    // nested class for course info
    public static class CourseInfo {
        private Long id;
        private String courseCode;
        private String courseName;
        private Integer credits;
        private String daysOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private String description;
        private InstructorInfo instructor;
        private Long instructorId;
        private java.math.BigDecimal courseFee;
        private Integer capacity;
        private String location;

        public CourseInfo() {
        }

        public CourseInfo(Long id, String courseCode, String courseName, Integer credits) {
            this.id = id;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.credits = credits;
        }

        public CourseInfo(Long id, String courseCode, String courseName, Integer credits,
                         String daysOfWeek, LocalTime startTime, LocalTime endTime, String description) {
            this.id = id;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.credits = credits;
            this.daysOfWeek = daysOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
            this.description = description;
        }

        public CourseInfo(Long id, String courseCode, String courseName, Integer credits,
                         String daysOfWeek, LocalTime startTime, LocalTime endTime, String description,
                         InstructorInfo instructor, Long instructorId, java.math.BigDecimal courseFee,
                         Integer capacity, String location) {
            this.id = id;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.credits = credits;
            this.daysOfWeek = daysOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
            this.description = description;
            this.instructor = instructor;
            this.instructorId = instructorId;
            this.courseFee = courseFee;
            this.capacity = capacity;
            this.location = location;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        // Alias for frontend compatibility
        public Integer getCreditHours() {
            return credits;
        }

        public InstructorInfo getInstructor() {
            return instructor;
        }

        public void setInstructor(InstructorInfo instructor) {
            this.instructor = instructor;
        }

        public Long getInstructorId() {
            return instructorId;
        }

        public void setInstructorId(Long instructorId) {
            this.instructorId = instructorId;
        }

        public java.math.BigDecimal getCourseFee() {
            return courseFee;
        }

        public void setCourseFee(java.math.BigDecimal courseFee) {
            this.courseFee = courseFee;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
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
