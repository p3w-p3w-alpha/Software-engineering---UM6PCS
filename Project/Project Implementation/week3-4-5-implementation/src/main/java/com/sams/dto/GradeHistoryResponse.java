package com.sams.dto;

import java.time.LocalDateTime;

// DTO for returning grade history information
public class GradeHistoryResponse {

    private Long id;
    private Long gradeId;
    private String previousValue;
    private String newValue;
    private Double previousPoints;
    private Double newPoints;
    private Long modifiedBy;
    private String modifiedByUsername;
    private LocalDateTime modifiedAt;
    private String reason;
    private String actionType;

    // constructors
    public GradeHistoryResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Double getPreviousPoints() {
        return previousPoints;
    }

    public void setPreviousPoints(Double previousPoints) {
        this.previousPoints = previousPoints;
    }

    public Double getNewPoints() {
        return newPoints;
    }

    public void setNewPoints(Double newPoints) {
        this.newPoints = newPoints;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedByUsername() {
        return modifiedByUsername;
    }

    public void setModifiedByUsername(String modifiedByUsername) {
        this.modifiedByUsername = modifiedByUsername;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
