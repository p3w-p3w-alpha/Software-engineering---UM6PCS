package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Enrollment entity - links students to courses
 * also tracks payment status, waitlist position, etc
 *
 * status flow: PENDING_PAYMENT -> ACTIVE (after payment approved)
 *              or -> WAITLISTED if course is full
 *              then -> COMPLETED or DROPPED
 */
@Entity
@Table(name = "enrollments", indexes = {
    // lots of queries filter by student or status so we index those
    @Index(name = "idx_enrollment_student", columnList = "student_id"),
    @Index(name = "idx_enrollment_course", columnList = "course_id"),
    @Index(name = "idx_enrollment_status", columnList = "status"),
    @Index(name = "idx_enrollment_student_status", columnList = "student_id, status")
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which student is enrolled
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // which course they enrolled in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // when they enrolled - auto set on creation
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    // status can be: PENDING_PAYMENT, ACTIVE, DROPPED, COMPLETED, WAITLISTED
    // starts as PENDING_PAYMENT until they pay for the course
    @Column(length = 20)
    private String status = "PENDING_PAYMENT";

    // if course is full, they go on waitlist - this tracks thier position
    @Column(name = "waitlist_position")
    private Integer waitlistPosition;

    // link to the payment record (if they paid for this enrollment)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // soft delete fields - for keeping historical data instead of hard deletes
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy; // id of user who deleted this record

    @PrePersist
    protected void onCreate() {
        enrollmentDate = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }

    // soft delete method - marks record as deleted without removing from database
    public void softDelete(Long deletedByUserId) {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUserId;
    }

    // constructors
    public Enrollment() {
    }

    public Enrollment(User student, Course course) {
        this.student = student;
        this.course = course;
        this.status = "PENDING_PAYMENT"; // default status, changes to ACTIVE after payment approval
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

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getWaitlistPosition() {
        return waitlistPosition;
    }

    public void setWaitlistPosition(Integer waitlistPosition) {
        this.waitlistPosition = waitlistPosition;
    }

    // helper method to chekc if enrollment is waitlisted
    public boolean isWaitlisted() {
        return "WAITLISTED".equals(status);
    }

    // helper method to chekc if enrollment is active
    public boolean isActiveStatus() {
        return "ACTIVE".equals(status);
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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
