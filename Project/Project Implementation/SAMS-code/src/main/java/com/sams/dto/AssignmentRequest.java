package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * request object for creating or updating assignments
 * teachers use this to create new assignments for thier courses
 */
public class AssignmentRequest {

    // assignment title
    @NotBlank(message = "Assignment title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    // detailed description of what students need to do
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    // deadline for submission
    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    // maximum points student can earn
    @Positive(message = "Max points must be positive")
    private Double maxPoints = 100.0;

    // whether to accept late submissions
    private Boolean allowLateSubmissions = false;

    // penalty per day for late submissions (percentage)
    private Double latePenaltyPerDay = 10.0;

    // comma-seperated list of allowed file types (pdf,docx,zip)
    private String allowedFileTypes;

    // max file size in MB
    private Integer maxFileSizeMb = 10;

    // constructors
    public AssignmentRequest() {
    }

    // getters and setters
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
}
