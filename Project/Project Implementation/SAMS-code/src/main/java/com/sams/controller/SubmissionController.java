package com.sams.controller;

import com.sams.dto.GradeSubmissionRequest;
import com.sams.dto.SubmissionResponse;
import com.sams.entity.Submission;
import com.sams.service.SubmissionService;
import com.sams.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * handles assignment submission operations - submit, grade, download
 * frontend calls this when students submit thier work
 * supports both file upload and pre-uploaded file paths
 */
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final FileStorageService fileStorageService;

    public SubmissionController(SubmissionService submissionService,
                               FileStorageService fileStorageService) {
        this.submissionService = submissionService;
        this.fileStorageService = fileStorageService;
    }

    // ========== SUBMISSION OPERATIONS ==========

    /**
     * Submit an assignment file
     * POST /api/submissions/submit?assignmentId=1&studentId=2
     * Content-Type: multipart/form-data
     */
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @RequestParam Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam("file") MultipartFile file) {

        try {
            Submission submission = submissionService.submitAssignment(assignmentId, studentId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(submission));
        } catch (IOException e) {
            throw new RuntimeException("Failed to submit assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Submit an assignment with pre-uploaded file paths (JSON body)
     * POST /api/submissions/submit-with-files
     * Content-Type: application/json
     * Used when files are uploaded separately via /api/files/upload/assignment
     */
    @PostMapping("/submit-with-files")
    public ResponseEntity<SubmissionResponse> submitAssignmentWithFiles(
            @RequestBody Map<String, Object> submissionData) {

        Long assignmentId = Long.valueOf(submissionData.get("assignmentId").toString());
        Long studentId = Long.valueOf(submissionData.get("studentId").toString());

        // Get file paths - can be single string or array
        String filePath;
        Object filePathsObj = submissionData.get("filePaths");
        if (filePathsObj instanceof List) {
            List<?> paths = (List<?>) filePathsObj;
            filePath = paths.isEmpty() ? "" : paths.get(0).toString();
        } else if (filePathsObj != null) {
            filePath = filePathsObj.toString();
        } else {
            filePath = "";
        }

        String notes = submissionData.get("notes") != null ? submissionData.get("notes").toString() : "";

        Submission submission = submissionService.submitAssignmentWithFilePaths(
                assignmentId, studentId, filePath, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(submission));
    }

    /**
     * Resubmit an assignment (replaces previous submission)
     * POST /api/submissions/resubmit?assignmentId=1&studentId=2
     * Content-Type: multipart/form-data
     */
    @PostMapping("/resubmit")
    public ResponseEntity<SubmissionResponse> resubmitAssignment(
            @RequestParam Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam("file") MultipartFile file) {

        try {
            Submission submission = submissionService.resubmitAssignment(assignmentId, studentId, file);
            return ResponseEntity.ok(convertToResponse(submission));
        } catch (IOException e) {
            throw new RuntimeException("Failed to resubmit assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Grade a submission
     * POST /api/submissions/{id}/grade?facultyId=3
     */
    @PostMapping("/{id}/grade")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            @PathVariable Long id,
            @RequestParam Long facultyId,
            @Valid @RequestBody GradeSubmissionRequest request) {

        Submission graded = submissionService.gradeSubmission(
                id,
                request.getPointsEarned(),
                request.getFeedback(),
                facultyId
        );

        return ResponseEntity.ok(convertToResponse(graded));
    }

    /**
     * Return graded submission to student
     * POST /api/submissions/{id}/return?facultyId=3
     */
    @PostMapping("/{id}/return")
    public ResponseEntity<SubmissionResponse> returnSubmission(
            @PathVariable Long id,
            @RequestParam Long facultyId) {

        Submission returned = submissionService.returnSubmission(id, facultyId);
        return ResponseEntity.ok(convertToResponse(returned));
    }

    /**
     * Download a submission file
     * GET /api/submissions/{id}/download?userId=2
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadSubmission(
            @PathVariable Long id,
            @RequestParam Long userId) {

        try {
            Submission submission = submissionService.getSubmissionById(id);
            Resource resource = submissionService.downloadSubmission(id, userId);

            // set content type
            String contentType = "application/octet-stream";
            String fileExtension = submission.getFileType();

            // set common content types
            if ("pdf".equals(fileExtension)) {
                contentType = "application/pdf";
            } else if ("docx".equals(fileExtension) || "doc".equals(fileExtension)) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if ("txt".equals(fileExtension)) {
                contentType = "text/plain";
            } else if ("zip".equals(fileExtension)) {
                contentType = "application/zip";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + submission.getFileName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Failed to download submission: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a submission
     * DELETE /api/submissions/{id}?userId=2
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSubmission(
            @PathVariable Long id,
            @RequestParam Long userId) {

        try {
            submissionService.deleteSubmission(id, userId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Submission deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete submission: " + e.getMessage(), e);
        }
    }

    // ========== QUERY OPERATIONS ==========

    /**
     * Get submission by ID
     * GET /api/submissions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getSubmissionById(@PathVariable Long id) {
        Submission submission = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(convertToResponse(submission));
    }

    /**
     * Get all submissions for an assignment
     * GET /api/submissions/assignment/{assignmentId}
     */
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignmentId);
        List<SubmissionResponse> responses = submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all submissions by a student
     * GET /api/submissions/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByStudent(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudent(studentId);
        List<SubmissionResponse> responses = submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get submission for a specific student and assignment
     * GET /api/submissions/student/{studentId}/assignment/{assignmentId}
     */
    @GetMapping("/student/{studentId}/assignment/{assignmentId}")
    public ResponseEntity<SubmissionResponse> getSubmissionByStudentAndAssignment(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId) {

        Optional<Submission> submission = submissionService
                .getSubmissionByStudentAndAssignment(studentId, assignmentId);

        if (submission.isPresent()) {
            return ResponseEntity.ok(convertToResponse(submission.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all late submissions for an assignment
     * GET /api/submissions/assignment/{assignmentId}/late
     */
    @GetMapping("/assignment/{assignmentId}/late")
    public ResponseEntity<List<SubmissionResponse>> getLateSubmissions(@PathVariable Long assignmentId) {
        List<Submission> submissions = submissionService.getLateSubmissions(assignmentId);
        List<SubmissionResponse> responses = submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get ungraded submissions for a course
     * GET /api/submissions/course/{courseId}/ungraded
     */
    @GetMapping("/course/{courseId}/ungraded")
    public ResponseEntity<List<SubmissionResponse>> getUngradedSubmissionsByCourse(@PathVariable Long courseId) {
        List<Submission> submissions = submissionService.getUngradedSubmissionsByCourse(courseId);
        List<SubmissionResponse> responses = submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get submissions by status
     * GET /api/submissions/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByStatus(@PathVariable String status) {
        List<Submission> submissions = submissionService.getSubmissionsByStatus(status);
        List<SubmissionResponse> responses = submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get submissions by student for a specific course
     * GET /api/submissions/student/{studentId}/course/{courseId}
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        List<Submission> submissions = submissionService.getSubmissionsByStudentAndCourse(studentId, courseId);
        List<SubmissionResponse> responses = submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * checks if student already submitted - prevents duplicate submissions
     * frontend calls this before showing submit button
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> hasStudentSubmitted(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId) {

        boolean hasSubmitted = submissionService.hasStudentSubmitted(studentId, assignmentId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasSubmitted", hasSubmitted);
        return ResponseEntity.ok(response);
    }

    /**
     * Get submission counts for an assignment
     * GET /api/submissions/assignment/{assignmentId}/count
     */
    @GetMapping("/assignment/{assignmentId}/count")
    public ResponseEntity<Map<String, Long>> getSubmissionCounts(@PathVariable Long assignmentId) {
        long totalCount = submissionService.getSubmissionCountByAssignment(assignmentId);
        long ungradedCount = submissionService.getUngradedSubmissionCount(assignmentId);

        Map<String, Long> counts = new HashMap<>();
        counts.put("total", totalCount);
        counts.put("ungraded", ungradedCount);
        counts.put("graded", totalCount - ungradedCount);

        return ResponseEntity.ok(counts);
    }

    // ========== HELPER METHODS FOR DTO CONVERSION ==========

    // convert Submission entity to SubmissionResponse
    private SubmissionResponse convertToResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setIsLate(submission.getIsLate());
        response.setLatePenaltyPercent(submission.getLatePenaltyPercent());
        response.setFileName(submission.getFileName());
        response.setFileSizeBytes(submission.getFileSizeBytes());
        response.setFileType(submission.getFileType());
        response.setPointsEarned(submission.getPointsEarned());
        response.setFinalScore(submission.getFinalScore());
        response.setFeedback(submission.getFeedback());
        response.setGradedAt(submission.getGradedAt());
        response.setStatus(submission.getStatus());
        response.setActive(submission.getActive());

        // set assignment info
        if (submission.getAssignment() != null) {
            response.setAssignmentId(submission.getAssignment().getId());
            response.setAssignmentTitle(submission.getAssignment().getTitle());
            response.setMaxPoints(submission.getAssignment().getMaxPoints());
        }

        // set student info
        if (submission.getStudent() != null) {
            response.setStudentId(submission.getStudent().getId());
            response.setStudentName(submission.getStudent().getFirstName() + " " +
                    submission.getStudent().getLastName());
        }

        // set grader info
        if (submission.getGradedBy() != null) {
            response.setGradedById(submission.getGradedBy().getId());
            response.setGradedByName(submission.getGradedBy().getFirstName() + " " +
                    submission.getGradedBy().getLastName());
        }

        return response;
    }
}
