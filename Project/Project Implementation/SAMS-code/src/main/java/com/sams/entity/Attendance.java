package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance entity - tracks daily attendance for students and faculty
 * can mark users as PRESENT, LATE, ABSENT, SICK, or EXCUSED
 *
 * unique constraint on user+date prevents duplicate entries
 * works fine but could use some refactoring later
 */
@Entity
@Table(name = "attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}),
       indexes = {
           @Index(name = "idx_attendance_user", columnList = "user_id"),
           @Index(name = "idx_attendance_date", columnList = "date"),
           @Index(name = "idx_attendance_course", columnList = "course_id"),
           @Index(name = "idx_attendance_status", columnList = "status")
       })
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the user whos attendance we're tracking
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // the date - combined with user_id forms unique constraint
    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    // attendance status - enum is cleaner than strings
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    // Optional: Track attendance for specific course/class instead of general
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(length = 500)
    private String notes;

    @Column(name = "marked_by")
    private Long markedBy; // ID of user who marked the attendance

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Attendance() {
    }

    public Attendance(User user, LocalDate date, AttendanceStatus status) {
        this.user = user;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(Long markedBy) {
        this.markedBy = markedBy;
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

    // Enum for Attendance Status
    public enum AttendanceStatus {
        PRESENT,
        LATE,
        ABSENT,
        SICK,
        EXCUSED
    }
}
