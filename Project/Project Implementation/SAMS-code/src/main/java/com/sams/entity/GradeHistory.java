package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * GradeHistory entity - tracks all changes made to grades for audit purposes
 * provides complete history of grade modifications
 *
 * needed for compliance and handling grade disputes
 * captures who changed what when and why which is super important
 */
@Entity
@Table(name = "grade_history")
public class GradeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // reference to teh grade that was modified - many history entries per grade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    // the previous grade value before the change - null for CREATE actions
    @Column(name = "previous_value", length = 5)
    private String previousValue;

    // the new grade value after the change
    @Column(name = "new_value", nullable = false, length = 5)
    private String newValue;

    // the previous grade points before the change
    @Column(name = "previous_points")
    private Double previousPoints;

    // the new grade points after the change
    @Column(name = "new_points", nullable = false)
    private Double newPoints;

    // who made the modification (faculty or admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by", nullable = false)
    private User modifiedBy;

    // when the modification occured
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    // optional reason for the grade change
    @Column(length = 500)
    private String reason;

    // type of modification - CREATE, UPDATE, DELETE, FINALIZE
    @Column(name = "action_type", nullable = false, length = 20)
    private String actionType;

    @PrePersist
    protected void onCreate() {
        modifiedAt = LocalDateTime.now();
    }

    // constructors
    public GradeHistory() {
    }

    public GradeHistory(Grade grade, String previousValue, String newValue,
                       Double previousPoints, Double newPoints,
                       User modifiedBy, String actionType, String reason) {
        this.grade = grade;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.previousPoints = previousPoints;
        this.newPoints = newPoints;
        this.modifiedBy = modifiedBy;
        this.actionType = actionType;
        this.reason = reason;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
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

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
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
