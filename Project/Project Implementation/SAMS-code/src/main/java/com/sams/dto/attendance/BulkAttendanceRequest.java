package com.sams.dto.attendance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO for marking attendance for multiple users at once
 */
public class BulkAttendanceRequest {

    @NotEmpty(message = "Attendance records list cannot be empty")
    @Valid
    private List<AttendanceRequest> attendanceRecords;

    // Constructors
    public BulkAttendanceRequest() {
    }

    public BulkAttendanceRequest(List<AttendanceRequest> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }

    // Getters and Setters
    public List<AttendanceRequest> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(List<AttendanceRequest> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }
}
