package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Grade entity - stores final grades for students
 * linked to enrollment so we know which course/student combo
 *
 * grades can be finalized - once finalized, faculty cant change them
 * only admin can unfinalize (in case of errors)
 *
 * GPA calcualtion uses the gradePoints field
 */
@Entity
@Table(name = "grades", indexes = {
    @Index(name = "idx_grade_student", columnList = "student_id"),
    @Index(name = "idx_grade_course", columnList = "course_id"),
    @Index(name = "idx_grade_enrollment", columnList = "enrollment_id")
})
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the student who recieved this grade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // the course this grade is for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // link back to enrollment - one enrollment = one grade
    // made this unique so we cant have duplicate grades for same enrollment
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    // the actual letter grade - A, A-, B+, B, B-, etc
    @NotNull
    @Column(nullable = false, length = 5)
    private String gradeValue;

    // numeric value for GPA calcualtion
    // A = 4.0, A- = 3.7, B+ = 3.3, etc
    @NotNull
    @Column(nullable = false)
    private Double gradePoints;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // grade finalization fields - once finalized, grades cant be changed easily
    @Column(nullable = false)
    private Boolean finalized = false;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @Column(name = "finalized_by")
    private Long finalizedBy; // user id who finalized the grade

    // soft delete fields - for keeping historical data instead of hard deletes
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy; // id of user who deleted this record

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // soft delete method - marks record as deleted without removing from database
    public void softDelete(Long deletedByUserId) {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUserId;
    }

    // constructors
    public Grade() {
    }

    public Grade(User student, Course course, Enrollment enrollment, String gradeValue, Double gradePoints) {
        this.student = student;
        this.course = course;
        this.enrollment = enrollment;
        this.gradeValue = gradeValue;
        this.gradePoints = gradePoints;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
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

    public Long getFinalizedBy() {
        return finalizedBy;
    }

    public void setFinalizedBy(Long finalizedBy) {
        this.finalizedBy = finalizedBy;
    }

    // helper method to finalize a grade
    public void finalizeGrade(Long finalizedByUserId) {
        this.finalized = true;
        this.finalizedAt = LocalDateTime.now();
        this.finalizedBy = finalizedByUserId;
    }

    // helper method to unfinalize (admin only)
    public void unfinalizeGrade() {
        this.finalized = false;
        this.finalizedAt = null;
        this.finalizedBy = null;
    }
}
