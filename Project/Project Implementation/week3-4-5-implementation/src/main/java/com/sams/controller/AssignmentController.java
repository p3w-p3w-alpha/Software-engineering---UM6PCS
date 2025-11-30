package com.sams.controller;

import com.sams.dto.AssignmentRequest;
import com.sams.dto.AssignmentResponse;
import com.sams.entity.Assignment;
import com.sams.service.AssignmentService;
import com.sams.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// REST controller for assignment operations
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public AssignmentController(AssignmentService assignmentService,
                               SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    // ========== CRUD OPERATIONS ==========

    /**
     * Create a new assignment
     * POST /api/assignments?courseId=1&facultyId=2
     */
    @PostMapping
    public ResponseEntity<AssignmentResponse> createAssignment(
            @Valid @RequestBody AssignmentRequest request,
            @RequestParam Long courseId,
            @RequestParam Long facultyId) {

        Assignment assignment = convertToEntity(request);
        Assignment created = assignmentService.createAssignment(assignment, courseId, facultyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(created));
    }

    /**
     * Get assignment by ID
     * GET /api/assignments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable Long id) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(convertToResponse(assignment));
    }

    /**
     * Get all active assignments
     * GET /api/assignments
     */
    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAllActiveAssignments() {
        List<Assignment> assignments = assignmentService.getAllActiveAssignments();
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all assignments for a course
     * GET /api/assignments/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByCourse(@PathVariable Long courseId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourse(courseId);
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all assignments created by a faculty member
     * GET /api/assignments/faculty/{facultyId}
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByFaculty(@PathVariable Long facultyId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByFaculty(facultyId);
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get assignments for a student (based on enrolled courses)
     * GET /api/assignments/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsForStudent(@PathVariable Long studentId) {
        List<Assignment> assignments = assignmentService.getAssignmentsForStudent(studentId);
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get upcoming assignments (due in the future)
     * GET /api/assignments/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<AssignmentResponse>> getUpcomingAssignments() {
        List<Assignment> assignments = assignmentService.getUpcomingAssignments();
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get overdue assignments
     * GET /api/assignments/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<AssignmentResponse>> getOverdueAssignments() {
        List<Assignment> assignments = assignmentService.getOverdueAssignments();
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get assignments due between two dates
     * GET /api/assignments/due-between?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
     */
    @GetMapping("/due-between")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsDueBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Assignment> assignments = assignmentService.getAssignmentsDueBetween(startDate, endDate);
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Search assignments by title
     * GET /api/assignments/search?title=homework
     */
    @GetMapping("/search")
    public ResponseEntity<List<AssignmentResponse>> searchAssignmentsByTitle(@RequestParam String title) {
        List<Assignment> assignments = assignmentService.searchAssignmentsByTitle(title);
        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Update an assignment
     * PUT /api/assignments/{id}?facultyId=2
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request,
            @RequestParam Long facultyId) {

        Assignment updatedAssignment = convertToEntity(request);
        Assignment result = assignmentService.updateAssignment(id, updatedAssignment, facultyId);

        return ResponseEntity.ok(convertToResponse(result));
    }

    /**
     * Delete an assignment
     * DELETE /api/assignments/{id}?facultyId=2
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAssignment(
            @PathVariable Long id,
            @RequestParam Long facultyId) {

        assignmentService.deleteAssignment(id, facultyId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Assignment deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ========== HELPER METHODS FOR DTO CONVERSION ==========

    // convert AssignmentRequest to Assignment entity
    private Assignment convertToEntity(AssignmentRequest request) {
        Assignment assignment = new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxPoints(request.getMaxPoints());
        assignment.setAllowLateSubmissions(request.getAllowLateSubmissions());
        assignment.setLatePenaltyPerDay(request.getLatePenaltyPerDay());
        assignment.setAllowedFileTypes(request.getAllowedFileTypes());
        assignment.setMaxFileSizeMb(request.getMaxFileSizeMb());

        return assignment;
    }

    // convert Assignment entity to AssignmentResponse
    private AssignmentResponse convertToResponse(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setMaxPoints(assignment.getMaxPoints());
        response.setMaxGrade(assignment.getMaxPoints()); // alias for frontend
        response.setAllowLateSubmissions(assignment.getAllowLateSubmissions());
        response.setLatePenaltyPerDay(assignment.getLatePenaltyPerDay());
        response.setAllowedFileTypes(assignment.getAllowedFileTypes());
        response.setMaxFileSizeMb(assignment.getMaxFileSizeMb());
        response.setActive(assignment.getActive());
        response.setIsOverdue(assignment.isOverdue());
        response.setCreatedAt(assignment.getCreatedAt());
        response.setUpdatedAt(assignment.getUpdatedAt());
        response.setType("assignment"); // default type

        // set course info - both flat fields and nested object
        if (assignment.getCourse() != null) {
            response.setCourseId(assignment.getCourse().getId());
            response.setCourseName(assignment.getCourse().getCourseCode() + " - " +
                    assignment.getCourse().getCourseName());
            // Create nested course object for frontend compatibility
            AssignmentResponse.CourseInfo courseInfo = new AssignmentResponse.CourseInfo(
                assignment.getCourse().getId(),
                assignment.getCourse().getCourseCode(),
                assignment.getCourse().getCourseName()
            );
            response.setCourse(courseInfo);
        }

        // set creator info
        if (assignment.getCreatedBy() != null) {
            response.setCreatedById(assignment.getCreatedBy().getId());
            response.setCreatedByName(assignment.getCreatedBy().getFirstName() + " " +
                    assignment.getCreatedBy().getLastName());
        }

        // set submission count
        long submissionCount = submissionService.getSubmissionCountByAssignment(assignment.getId());
        response.setSubmissionCount(submissionCount);

        return response;
    }
}
