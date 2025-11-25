package com.sams.controller;

import com.sams.entity.*;
import com.sams.service.DegreeProgressService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for degree programs and student progress tracking
 * Handles CRUD operations for degree programs, requirements, and progress monitoring
 */
@RestController
@RequestMapping("/api/degree-progress")
public class DegreeProgressController {

    private final DegreeProgressService progressService;

    public DegreeProgressController(DegreeProgressService progressService) {
        this.progressService = progressService;
    }

    // ========== DEGREE PROGRAM ENDPOINTS ==========

    /**
     * Create new degree program - Only ADMIN/SUPER_ADMIN
     */
    @PostMapping("/programs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<DegreeProgram> createDegreeProgram(@Valid @RequestBody DegreeProgram degreeProgram) {
        DegreeProgram created = progressService.createDegreeProgram(degreeProgram);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update degree program - Only ADMIN/SUPER_ADMIN
     */
    @PutMapping("/programs/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<DegreeProgram> updateDegreeProgram(@PathVariable Long id,
                                                             @Valid @RequestBody DegreeProgram degreeProgram) {
        DegreeProgram updated = progressService.updateDegreeProgram(id, degreeProgram);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get all degree programs - Accessible to all authenticated users
     */
    @GetMapping("/programs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DegreeProgram>> getAllDegreePrograms() {
        List<DegreeProgram> programs = progressService.getAllDegreePrograms();
        return ResponseEntity.ok(programs);
    }

    /**
     * Get active degree programs - Accessible to all authenticated users
     */
    @GetMapping("/programs/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DegreeProgram>> getActiveDegreePrograms() {
        List<DegreeProgram> programs = progressService.getActiveDegreePrograms();
        return ResponseEntity.ok(programs);
    }

    /**
     * Get degree program by ID - Accessible to all authenticated users
     */
    @GetMapping("/programs/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DegreeProgram> getDegreeProgramById(@PathVariable Long id) {
        DegreeProgram program = progressService.getDegreeProgramById(id);
        return ResponseEntity.ok(program);
    }

    // ========== DEGREE REQUIREMENTS ENDPOINTS ==========

    /**
     * Add requirement to degree program - Only ADMIN/SUPER_ADMIN
     */
    @PostMapping("/programs/{programId}/requirements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<DegreeRequirement> addRequirement(@PathVariable Long programId,
                                                            @Valid @RequestBody DegreeRequirement requirement) {
        DegreeRequirement created = progressService.addRequirement(programId, requirement);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update requirement - Only ADMIN/SUPER_ADMIN
     */
    @PutMapping("/requirements/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<DegreeRequirement> updateRequirement(@PathVariable Long id,
                                                               @Valid @RequestBody DegreeRequirement requirement) {
        DegreeRequirement updated = progressService.updateRequirement(id, requirement);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get all requirements for a degree program - Accessible to all authenticated users
     */
    @GetMapping("/programs/{programId}/requirements")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DegreeRequirement>> getRequirementsForProgram(@PathVariable Long programId) {
        List<DegreeRequirement> requirements = progressService.getRequirementsForProgram(programId);
        return ResponseEntity.ok(requirements);
    }

    /**
     * Delete requirement - Only SUPER_ADMIN
     */
    @DeleteMapping("/requirements/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequirement(@PathVariable Long id) {
        progressService.deleteRequirement(id);
    }

    // ========== STUDENT PROGRESS ENDPOINTS ==========

    /**
     * Enroll student in degree program - Only ADMIN/SUPER_ADMIN
     */
    @PostMapping("/students/{studentId}/enroll")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<StudentDegreeProgress> enrollStudent(@PathVariable Long studentId,
                                                               @RequestParam Long degreeProgramId,
                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        StudentDegreeProgress progress = progressService.enrollStudentInProgram(studentId, degreeProgramId, startDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(progress);
    }

    /**
     * Get student's progress - STUDENT (own), FACULTY (enrolled students), ADMIN/SUPER_ADMIN (all)
     */
    @GetMapping("/students/{studentId}/progress")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<StudentDegreeProgress> getStudentProgress(@PathVariable Long studentId) {
        StudentDegreeProgress progress = progressService.getStudentProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Update student progress (recalculate based on grades) - Called automatically or by ADMIN
     */
    @PostMapping("/students/{studentId}/update-progress")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<StudentDegreeProgress> updateStudentProgress(@PathVariable Long studentId) {
        StudentDegreeProgress progress = progressService.updateStudentProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Check if student meets graduation requirements
     */
    @GetMapping("/students/{studentId}/graduation-eligible")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Boolean> meetsGraduationRequirements(@PathVariable Long studentId) {
        boolean eligible = progressService.meetsGraduationRequirements(studentId);
        return ResponseEntity.ok(eligible);
    }

    /**
     * Mark student as graduated - Only ADMIN/SUPER_ADMIN
     */
    @PostMapping("/students/{studentId}/graduate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<StudentDegreeProgress> markAsGraduated(@PathVariable Long studentId,
                                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate graduationDate) {
        StudentDegreeProgress progress = progressService.markAsGraduated(studentId, graduationDate);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get all students in a degree program - Only ADMIN/SUPER_ADMIN
     */
    @GetMapping("/programs/{programId}/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<StudentDegreeProgress>> getStudentsInProgram(@PathVariable Long programId) {
        List<StudentDegreeProgress> students = progressService.getStudentsInProgram(programId);
        return ResponseEntity.ok(students);
    }

    /**
     * Get students eligible for graduation - Only ADMIN/SUPER_ADMIN
     */
    @GetMapping("/students/graduation-eligible")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<StudentDegreeProgress>> getEligibleForGraduation() {
        List<StudentDegreeProgress> students = progressService.getEligibleForGraduation();
        return ResponseEntity.ok(students);
    }

    /**
     * Get students at risk - Only ADMIN/SUPER_ADMIN and FACULTY
     */
    @GetMapping("/students/at-risk")
    @PreAuthorize("hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<StudentDegreeProgress>> getStudentsAtRisk() {
        List<StudentDegreeProgress> students = progressService.getStudentsAtRisk();
        return ResponseEntity.ok(students);
    }

    /**
     * Update student progress status - Only ADMIN/SUPER_ADMIN
     */
    @PatchMapping("/students/{studentId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<StudentDegreeProgress> updateProgressStatus(@PathVariable Long studentId,
                                                                       @RequestParam String status) {
        StudentDegreeProgress progress = progressService.updateProgressStatus(studentId, status);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get detailed progress report for student
     */
    @GetMapping("/students/{studentId}/report")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<DegreeProgressService.ProgressReport> getProgressReport(@PathVariable Long studentId) {
        DegreeProgressService.ProgressReport report = progressService.getProgressReport(studentId);
        return ResponseEntity.ok(report);
    }
}
