package com.sams.dto.attendance;

import com.sams.entity.Attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * request object for marking attendance
 * teachers use this to mark students present/absent for courses
 */
public class AttendanceRequest {

    // which user (student or faculty) to mark
    @NotNull(message = "User ID is required")
    private Long userId;

    // date of attendance
    @NotNull(message = "Date is required")
    private LocalDate date;

    // attendance status (PRESENT, ABSENT, LATE, EXCUSED)
    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    // optional course id for course-specific attendance
    private Long courseId;

    // when they checked in
    private LocalDateTime checkInTime;

    // when they checked out
    private LocalDateTime checkOutTime;

    // any notes about teh attendance
    private String notes;

    // Constructors
    public AttendanceRequest() {
    }

    public AttendanceRequest(Long userId, LocalDate date, AttendanceStatus status) {
        this.userId = userId;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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
}
