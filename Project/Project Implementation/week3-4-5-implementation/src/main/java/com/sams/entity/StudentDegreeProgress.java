package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity tracking a student's progress toward completing their degree
 * Links student to their degree program and tracks completion status
 */
@Entity
@Table(name = "student_degree_progress")
public class StudentDegreeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // the degree program the student is pursuing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_program_id", nullable = false)
    private DegreeProgram degreeProgram;

    // when the student started this program
    @Column(name = "start_date")
    private LocalDate startDate;

    // expected graduation date
    @Column(name = "expected_graduation_date")
    private LocalDate expectedGraduationDate;

    // actual graduation date (null if not graduated)
    @Column(name = "actual_graduation_date")
    private LocalDate actualGraduationDate;

    // status: ACTIVE, ON_HOLD, COMPLETED, WITHDRAWN
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    // total credits completed
    @Column(name = "credits_completed")
    private Integer creditsCompleted = 0;

    // total credits remaining
    @Column(name = "credits_remaining")
    private Integer creditsRemaining;

    // current GPA
    @Column(name = "current_gpa")
    private Double currentGpa = 0.0;

    // percentage of degree completed (0-100)
    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    // is student on track to graduate on time?
    @Column(name = "on_track")
    private Boolean onTrack = true;

    // notes or comments about student's progress
    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // calculate initial expected graduation date based on program duration
        if (startDate != null && expectedGraduationDate == null && degreeProgram != null) {
            int durationSemesters = degreeProgram.getTypicalDurationSemesters();
            // assuming each semester is ~6 months
            expectedGraduationDate = startDate.plusMonths(durationSemesters * 6L);
        }

        // calculate initial credits remaining
        if (degreeProgram != null && creditsRemaining == null) {
            creditsRemaining = degreeProgram.getTotalCreditsRequired() - creditsCompleted;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // recalculate credits remaining
        if (degreeProgram != null) {
            creditsRemaining = degreeProgram.getTotalCreditsRequired() - creditsCompleted;
        }

        // recalculate completion percentage
        if (degreeProgram != null && degreeProgram.getTotalCreditsRequired() > 0) {
            completionPercentage = (creditsCompleted.doubleValue() / degreeProgram.getTotalCreditsRequired()) * 100.0;
            completionPercentage = Math.round(completionPercentage * 100.0) / 100.0; // round to 2 decimals
        }
    }

    // constructors
    public StudentDegreeProgress() {
    }

    public StudentDegreeProgress(User student, DegreeProgram degreeProgram, LocalDate startDate) {
        this.student = student;
        this.degreeProgram = degreeProgram;
        this.startDate = startDate;
        this.status = "ACTIVE";
    }

    // helper methods

    /**
     * Check if student has met degree requirements
     */
    public boolean meetsGraduationRequirements() {
        if (degreeProgram == null) {
            return false;
        }

        // check if student has completed required credits
        boolean hasRequiredCredits = creditsCompleted >= degreeProgram.getTotalCreditsRequired();

        // check if student meets minimum GPA requirement
        boolean meetsGpaRequirement = currentGpa >= degreeProgram.getMinimumGpa();

        return hasRequiredCredits && meetsGpaRequirement;
    }

    /**
     * Update progress based on student's grades
     */
    public void updateProgress(Integer totalCredits, Double gpa) {
        this.creditsCompleted = totalCredits;
        this.currentGpa = gpa;

        if (degreeProgram != null) {
            this.creditsRemaining = degreeProgram.getTotalCreditsRequired() - totalCredits;
            this.completionPercentage = (totalCredits.doubleValue() / degreeProgram.getTotalCreditsRequired()) * 100.0;
            this.completionPercentage = Math.round(completionPercentage * 100.0) / 100.0;
        }
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedGraduationDate() {
        return expectedGraduationDate;
    }

    public void setExpectedGraduationDate(LocalDate expectedGraduationDate) {
        this.expectedGraduationDate = expectedGraduationDate;
    }

    public LocalDate getActualGraduationDate() {
        return actualGraduationDate;
    }

    public void setActualGraduationDate(LocalDate actualGraduationDate) {
        this.actualGraduationDate = actualGraduationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreditsCompleted() {
        return creditsCompleted;
    }

    public void setCreditsCompleted(Integer creditsCompleted) {
        this.creditsCompleted = creditsCompleted;
    }

    public Integer getCreditsRemaining() {
        return creditsRemaining;
    }

    public void setCreditsRemaining(Integer creditsRemaining) {
        this.creditsRemaining = creditsRemaining;
    }

    public Double getCurrentGpa() {
        return currentGpa;
    }

    public void setCurrentGpa(Double currentGpa) {
        this.currentGpa = currentGpa;
    }

    public Double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Boolean getOnTrack() {
        return onTrack;
    }

    public void setOnTrack(Boolean onTrack) {
        this.onTrack = onTrack;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
}
