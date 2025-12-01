package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for assignment data
 * pretty straightforward - returns all assignment details to frontend
 */
public class AssignmentResponse {

    // assignment unique id
    private Long id;

    // assignment title
    private String title;

    // what the assignment is about
    private String description;

    // which course this belongs to
    private Long courseId;

    // course name for display
    private String courseName;

    // nested course info for frontend compatability
    private CourseInfo course;

    // teacher who created it
    private Long createdById;

    // teacher's name
    private String createdByName;

    // when it's due
    private LocalDateTime dueDate;

    // max points possible
    private Double maxPoints;

    // alias for maxPoints (frontend uses this)
    private Double maxGrade;

    // whether late submissions are allowed
    private Boolean allowLateSubmissions;

    // late penalty percentage per day
    private Double latePenaltyPerDay;

    // allowed file types
    private String allowedFileTypes;

    // max file size in MB
    private Integer maxFileSizeMb;

    // whether assignment is still active
    private Boolean active;

    // whether deadline has passed
    private Boolean isOverdue;

    // number of submissions recieved
    private Long submissionCount;

    // when assignment was created
    private LocalDateTime createdAt;

    // last update time
    private LocalDateTime updatedAt;

    // type of assignment ('assignment' or 'exam')
    private String type;

    // Nested class for course info
    public static class CourseInfo {
        private Long id;
        private String courseCode;
        private String courseName;
        private String code; // alias for courseCode
        private String name; // alias for courseName

        public CourseInfo() {}

        public CourseInfo(Long id, String courseCode, String courseName) {
            this.id = id;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.code = courseCode;
            this.name = courseName;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; this.code = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; this.name = courseName; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // constructors
    public AssignmentResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Double getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Double maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Boolean getAllowLateSubmissions() {
        return allowLateSubmissions;
    }

    public void setAllowLateSubmissions(Boolean allowLateSubmissions) {
        this.allowLateSubmissions = allowLateSubmissions;
    }

    public Double getLatePenaltyPerDay() {
        return latePenaltyPerDay;
    }

    public void setLatePenaltyPerDay(Double latePenaltyPerDay) {
        this.latePenaltyPerDay = latePenaltyPerDay;
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public Integer getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public void setMaxFileSizeMb(Integer maxFileSizeMb) {
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Long getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(Long submissionCount) {
        this.submissionCount = submissionCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CourseInfo getCourse() {
        return course;
    }

    public void setCourse(CourseInfo course) {
        this.course = course;
    }

    public Double getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(Double maxGrade) {
        this.maxGrade = maxGrade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
