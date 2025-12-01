package com.sams.controller;

import com.sams.dto.*;
import com.sams.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * handles teacher profile management - profiles, office hours, statistics
 * frontend uses this for teacher directory and scheduling
 * supports search by department and specialization
 */
@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    // ==================== Teacher Profile Endpoints ====================

    /**
     * Create a new teacher profile
     */
    @PostMapping("/profiles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createTeacherProfile(@Valid @RequestBody TeacherProfileRequest request) {
        try {
            TeacherProfileResponse response = teacherService.createTeacherProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating teacher profile: " + e.getMessage());
        }
    }

    /**
     * Update an existing teacher profile
     */
    @PutMapping("/profiles/{profileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @teacherProfileSecurity.isProfileOwner(#profileId)")
    public ResponseEntity<?> updateTeacherProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody TeacherProfileRequest request) {
        try {
            TeacherProfileResponse response = teacherService.updateTeacherProfile(profileId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating teacher profile: " + e.getMessage());
        }
    }

    /**
     * Get teacher profile by ID
     */
    @GetMapping("/profiles/{profileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getTeacherProfile(@PathVariable Long profileId) {
        try {
            TeacherProfileResponse response = teacherService.getTeacherProfile(profileId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teacher profile: " + e.getMessage());
        }
    }

    /**
     * Get teacher profile by user ID
     */
    @GetMapping("/profiles/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getTeacherProfileByUserId(@PathVariable Long userId) {
        try {
            TeacherProfileResponse response = teacherService.getTeacherProfileByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teacher profile: " + e.getMessage());
        }
    }

    /**
     * Get all teacher profiles
     */
    @GetMapping("/profiles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getAllTeacherProfiles() {
        try {
            List<TeacherProfileResponse> profiles = teacherService.getAllTeacherProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teacher profiles: " + e.getMessage());
        }
    }

    /**
     * Get active teacher profiles
     */
    @GetMapping("/profiles/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getActiveTeacherProfiles() {
        try {
            List<TeacherProfileResponse> profiles = teacherService.getActiveTeacherProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching active teacher profiles: " + e.getMessage());
        }
    }

    /**
     * Search teachers by keyword
     */
    @GetMapping("/profiles/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> searchTeachers(@RequestParam String query) {
        try {
            List<TeacherProfileResponse> profiles = teacherService.searchTeachers(query);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error searching teachers: " + e.getMessage());
        }
    }

    /**
     * Get teachers by department
     */
    @GetMapping("/profiles/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getTeachersByDepartment(@PathVariable String department) {
        try {
            List<TeacherProfileResponse> profiles = teacherService.getTeachersByDepartment(department);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teachers by department: " + e.getMessage());
        }
    }

    /**
     * Get teachers by filters
     */
    @GetMapping("/profiles/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<?> getTeachersByFilters(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean availableForConsultation) {
        try {
            List<TeacherProfileResponse> profiles = teacherService.getTeachersByFilters(
                department, designation, active, availableForConsultation);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teachers: " + e.getMessage());
        }
    }

    /**
     * Get top-rated teachers
     */
    @GetMapping("/profiles/top-rated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getTopRatedTeachers() {
        try {
            List<TeacherProfileResponse> profiles = teacherService.getTopRatedTeachers();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching top-rated teachers: " + e.getMessage());
        }
    }

    /**
     * Toggle teacher profile active status
     */
    @PatchMapping("/profiles/{profileId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> toggleTeacherProfileStatus(@PathVariable Long profileId) {
        try {
            TeacherProfileResponse response = teacherService.toggleTeacherProfileStatus(profileId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error toggling teacher status: " + e.getMessage());
        }
    }

    /**
     * Delete teacher profile
     */
    @DeleteMapping("/profiles/{profileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteTeacherProfile(@PathVariable Long profileId) {
        try {
            teacherService.deleteTeacherProfile(profileId);
            return ResponseEntity.ok("Teacher profile deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting teacher profile: " + e.getMessage());
        }
    }

    // ==================== Office Hours Endpoints ====================

    /**
     * Create office hours
     */
    @PostMapping("/office-hours")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<?> createOfficeHours(@Valid @RequestBody OfficeHoursRequest request) {
        try {
            OfficeHoursResponse response = teacherService.createOfficeHours(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating office hours: " + e.getMessage());
        }
    }

    /**
     * Update office hours
     */
    @PutMapping("/office-hours/{officeHoursId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @officeHoursSecurity.isOwner(#officeHoursId)")
    public ResponseEntity<?> updateOfficeHours(
            @PathVariable Long officeHoursId,
            @Valid @RequestBody OfficeHoursRequest request) {
        try {
            OfficeHoursResponse response = teacherService.updateOfficeHours(officeHoursId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating office hours: " + e.getMessage());
        }
    }

    /**
     * Get office hours by ID
     */
    @GetMapping("/office-hours/{officeHoursId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getOfficeHours(@PathVariable Long officeHoursId) {
        try {
            OfficeHoursResponse response = teacherService.getOfficeHours(officeHoursId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching office hours: " + e.getMessage());
        }
    }

    /**
     * Get all office hours for a teacher
     */
    @GetMapping("/office-hours/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getTeacherOfficeHours(@PathVariable Long teacherId) {
        try {
            List<OfficeHoursResponse> officeHours = teacherService.getTeacherOfficeHours(teacherId);
            return ResponseEntity.ok(officeHours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teacher office hours: " + e.getMessage());
        }
    }

    /**
     * Get active office hours for a teacher
     */
    @GetMapping("/office-hours/teacher/{teacherId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getActiveTeacherOfficeHours(@PathVariable Long teacherId) {
        try {
            List<OfficeHoursResponse> officeHours = teacherService.getActiveTeacherOfficeHours(teacherId);
            return ResponseEntity.ok(officeHours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching active office hours: " + e.getMessage());
        }
    }

    /**
     * Get office hours available on a specific date
     */
    @GetMapping("/office-hours/teacher/{teacherId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getAvailableOfficeHoursForDate(
            @PathVariable Long teacherId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<OfficeHoursResponse> officeHours = teacherService.getAvailableOfficeHoursForDate(teacherId, date);
            return ResponseEntity.ok(officeHours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching office hours for date: " + e.getMessage());
        }
    }

    /**
     * Cancel office hours
     */
    @PatchMapping("/office-hours/{officeHoursId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<?> cancelOfficeHours(
            @PathVariable Long officeHoursId,
            @RequestParam String reason,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate specificDate) {
        try {
            OfficeHoursResponse response = teacherService.cancelOfficeHours(officeHoursId, reason, specificDate);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error cancelling office hours: " + e.getMessage());
        }
    }

    /**
     * Reactivate cancelled office hours
     */
    @PatchMapping("/office-hours/{officeHoursId}/reactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<?> reactivateOfficeHours(@PathVariable Long officeHoursId) {
        try {
            OfficeHoursResponse response = teacherService.reactivateOfficeHours(officeHoursId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error reactivating office hours: " + e.getMessage());
        }
    }

    /**
     * Delete office hours
     */
    @DeleteMapping("/office-hours/{officeHoursId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @officeHoursSecurity.isOwner(#officeHoursId)")
    public ResponseEntity<?> deleteOfficeHours(@PathVariable Long officeHoursId) {
        try {
            teacherService.deleteOfficeHours(officeHoursId);
            return ResponseEntity.ok("Office hours deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting office hours: " + e.getMessage());
        }
    }

    // ==================== Statistics and Analytics Endpoints ====================

    /**
     * Get teacher statistics
     */
    @GetMapping("/statistics/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<?> getTeacherStatistics(@PathVariable Long teacherId) {
        try {
            TeacherStatisticsResponse stats = teacherService.getTeacherStatistics(teacherId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teacher statistics: " + e.getMessage());
        }
    }

    /**
     * Get teacher weekly schedule
     */
    @GetMapping("/schedule/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<?> getTeacherSchedule(@PathVariable Long teacherId) {
        try {
            TeacherScheduleResponse schedule = teacherService.getTeacherSchedule(teacherId);
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching teacher schedule: " + e.getMessage());
        }
    }
}
