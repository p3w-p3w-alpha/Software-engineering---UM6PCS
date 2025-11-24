package com.sams.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CourseRequest {

    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100)
    private String courseName;

    @Size(max = 500)
    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Long instructorId; // instructor user id

    // schedule information (optional)
    private String daysOfWeek; // e.g., "MON,WED,FRI"
    private String startTime;  // e.g., "09:00"
    private String endTime;    // e.g., "10:30"

    // list of prerequiset course IDs
    private List<Long> prerequisiteIds;

    public CourseRequest() {
    }

    public CourseRequest(String courseCode, String courseName, String description, Integer credits, Integer capacity, Long instructorId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.capacity = capacity;
        this.instructorId = instructorId;
    }

    // getters and setters
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

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<Long> getPrerequisiteIds() {
        return prerequisiteIds;
    }

    public void setPrerequisiteIds(List<Long> prerequisiteIds) {
        this.prerequisiteIds = prerequisiteIds;
    }
}
