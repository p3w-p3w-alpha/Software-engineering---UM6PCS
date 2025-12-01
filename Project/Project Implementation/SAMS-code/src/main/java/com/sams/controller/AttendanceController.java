package com.sams.controller;

import com.sams.dto.attendance.*;
import com.sams.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Attendance Management
 * Provides endpoints for marking, viewing, and analyzing attendance
 */
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Mark attendance for a single user
     * POST /api/attendance/mark
     */
    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        try {
            AttendanceResponse response = attendanceService.markAttendance(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Mark attendance for multiple users (bulk operation)
     * POST /api/attendance/mark-bulk
     */
    @PostMapping("/mark-bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<List<AttendanceResponse>> markBulkAttendance(@Valid @RequestBody BulkAttendanceRequest request) {
        try {
            List<AttendanceResponse> responses = attendanceService.markBulkAttendance(request);
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Get attendance records for a specific date
     * GET /api/attendance/date/{date}
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceResponse> responses = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get attendance records for a date range
     * GET /api/attendance/range?startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceResponse> responses = attendanceService.getAttendanceByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get attendance records for a specific user
     * GET /api/attendance/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByUser(@PathVariable Long userId) {
        try {
            List<AttendanceResponse> responses = attendanceService.getAttendanceByUser(userId);
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Get attendance records for a user within a date range
     * GET /api/attendance/user/{userId}/range?startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<AttendanceResponse> responses = attendanceService.getAttendanceByUserAndDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Get attendance statistics for a specific date
     * GET /api/attendance/statistics/date/{date}
     */
    @GetMapping("/statistics/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AttendanceStatistics> getStatisticsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AttendanceStatistics statistics = attendanceService.getAttendanceStatisticsByDate(date);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get attendance statistics for a date range
     * GET /api/attendance/statistics/range?startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/statistics/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AttendanceStatistics> getStatisticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AttendanceStatistics statistics = attendanceService.getAttendanceStatisticsByDateRange(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get attendance statistics for a specific user
     * GET /api/attendance/statistics/user/{userId}?startDate={startDate}&endDate={endDate}
     * startDate and endDate are optional - defaults to last 90 days
     */
    @GetMapping("/statistics/user/{userId}")
    public ResponseEntity<AttendanceStatistics> getStatisticsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Default to last 90 days if not specified
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            if (startDate == null) {
                startDate = endDate.minusDays(90);
            }
            AttendanceStatistics statistics = attendanceService.getAttendanceStatisticsByUser(userId, startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Get attendance statistics by role (STUDENT, FACULTY)
     * GET /api/attendance/statistics/by-role?startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/statistics/by-role")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, AttendanceStatistics>> getStatisticsByRole(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, AttendanceStatistics> statistics = attendanceService.getAttendanceStatisticsByRole(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get attendance percentage for a user
     * GET /api/attendance/percentage/{userId}?startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/percentage/{userId}")
    public ResponseEntity<Double> getAttendancePercentage(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Double percentage = attendanceService.getAttendancePercentage(userId, startDate, endDate);
            return ResponseEntity.ok(percentage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Delete attendance record
     * DELETE /api/attendance/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        try {
            attendanceService.deleteAttendance(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get attendance statistics for a specific course
     * GET /api/attendance/course/{courseId}/statistics?startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/course/{courseId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<AttendanceStatistics> getStatisticsByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Default to last 90 days if not specified
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            if (startDate == null) {
                startDate = endDate.minusDays(90);
            }
            AttendanceStatistics statistics = attendanceService.getAttendanceStatisticsByCourse(courseId, startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Get attendance records for a specific course
     * GET /api/attendance/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            if (startDate == null) {
                startDate = endDate.minusDays(30);
            }
            List<AttendanceResponse> responses = attendanceService.getAttendanceByCourse(courseId, startDate, endDate);
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
