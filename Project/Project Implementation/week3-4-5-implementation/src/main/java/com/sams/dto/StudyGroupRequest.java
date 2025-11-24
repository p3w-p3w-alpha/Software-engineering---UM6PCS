package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// request DTO for creating or updating a study group
public class StudyGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Long courseId; // optional - group can be course-specific or general

    private Integer maxMembers = 10;

    private Boolean isPrivate = false;

    // constructors
    public StudyGroupRequest() {
    }

    public StudyGroupRequest(String name, String description, Long courseId, Integer maxMembers, Boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.courseId = courseId;
        this.maxMembers = maxMembers;
        this.isPrivate = isPrivate;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
