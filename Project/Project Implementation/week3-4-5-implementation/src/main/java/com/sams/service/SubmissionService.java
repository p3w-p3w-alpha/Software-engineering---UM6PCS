package com.sams.service;

import com.sams.entity.Assignment;
import com.sams.entity.Submission;
import com.sams.entity.User;
import com.sams.exception.ResourceNotFoundException;
import com.sams.repository.SubmissionRepository;
import com.sams.repository.AssignmentRepository;
import com.sams.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// service for managing assignment submissions
@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public SubmissionService(SubmissionRepository submissionRepository,
                            AssignmentRepository assignmentRepository,
                            UserRepository userRepository,
                            FileStorageService fileStorageService,
                            NotificationService notificationService) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    // ========== SUBMISSION OPERATIONS ==========

    /**
     * Submit an assignment file
     */
    @Transactional
    public Submission submitAssignment(Long assignmentId, Long studentId, MultipartFile file) throws IOException {
        // validate assignment exists
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        // validate student exists
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // validate student role
        if (!"STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("Only students can submit assignments");
        }

        // chekc if student has already submitted
        if (submissionRepository.existsByAssignmentIdAndStudentIdAndActiveTrue(assignmentId, studentId)) {
            throw new IllegalStateException("You have already submitted this assignment. " +
                    "Delete previous submission before resubmitting.");
        }

        // chekc if assignment accepts late submissions
        boolean isLate = LocalDateTime.now().isAfter(assignment.getDueDate());
        if (isLate && !assignment.getAllowLateSubmissions()) {
            throw new IllegalStateException("Assignment deadline has passed and late submissions are not allowed");
        }

        // validate file type if assignment specifies allowed types
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!assignment.isFileTypeAllowed(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed for this assignment. " +
                    "Allowed types: " + assignment.getAllowedFileTypes());
        }

        // store file using FileStorageService
        String filePath = fileStorageService.storeFile(file, studentId, assignmentId);

        // create submission record
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setFilePath(filePath);
        submission.setFileName(file.getOriginalFilename());
        submission.setFileSizeBytes(file.getSize());
        submission.setFileType(fileExtension);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setIsLate(isLate);

        // calculate late penalty if applicable
        if (isLate) {
            double penalty = assignment.calculateLatePenalty(LocalDateTime.now());
            submission.setLatePenaltyPercent(penalty);
        }

        Submission savedSubmission = submissionRepository.save(submission);

        // notify faculty about new submission
        notificationService.notifySubmissionReceived(assignment.getCreatedBy(),
                assignment.getCourse(), assignment, student);

        // notify student of sucessful submission
        notificationService.notifySubmissionConfirmed(student, assignment.getCourse(), assignment);

        return savedSubmission;
    }

    /**
     * Resubmit an assignment (delete old submission and create new one)
     */
    @Transactional
    public Submission resubmitAssignment(Long assignmentId, Long studentId, MultipartFile file) throws IOException {
        // delete existing submission if it exists
        Optional<Submission> existingSubmission = submissionRepository
                .findByAssignmentIdAndStudentIdAndActiveTrue(assignmentId, studentId);

        if (existingSubmission.isPresent()) {
            deleteSubmission(existingSubmission.get().getId(), studentId);
        }

        // submit new file
        return submitAssignment(assignmentId, studentId, file);
    }

    /**
     * Grade a submission
     */
    @Transactional
    public Submission gradeSubmission(Long submissionId, Double points, String feedback, Long facultyId) {
        Submission submission = getSubmissionById(submissionId);
        Assignment assignment = submission.getAssignment();

        // validate faculty
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + facultyId));

        if (!"FACULTY".equals(faculty.getRole())) {
            throw new IllegalArgumentException("Only faculty members can grade submissions");
        }

        // validate points
        if (points < 0 || points > assignment.getMaxPoints()) {
            throw new IllegalArgumentException("Points must be between 0 and " + assignment.getMaxPoints());
        }

        // grade the submission
        submission.grade(points, feedback, faculty);
        Submission gradedSubmission = submissionRepository.save(submission);

        // notify student of graded submission
        notificationService.notifySubmissionGraded(submission.getStudent(),
                assignment.getCourse(), assignment, submission.getFinalScore());

        return gradedSubmission;
    }

    /**
     * Return graded submission to student
     */
    @Transactional
    public Submission returnSubmission(Long submissionId, Long facultyId) {
        Submission submission = getSubmissionById(submissionId);

        // validate faculty
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + facultyId));

        if (!"FACULTY".equals(faculty.getRole())) {
            throw new IllegalArgumentException("Only faculty members can return submissions");
        }

        submission.returnToStudent();
        Submission returnedSubmission = submissionRepository.save(submission);

        // notify student that submission has been returned
        notificationService.notifySubmissionReturned(submission.getStudent(),
                submission.getAssignment().getCourse(), submission.getAssignment());

        return returnedSubmission;
    }

    /**
     * Get submission by ID
     */
    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
    }

    /**
     * Get all submissions for an assignment
     */
    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdAndActiveTrueOrderBySubmittedAtDesc(assignmentId);
    }

    /**
     * Get all submissions by a student
     */
    public List<Submission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentIdAndActiveTrueOrderBySubmittedAtDesc(studentId);
    }

    /**
     * Get submission for a specific student and assignment
     */
    public Optional<Submission> getSubmissionByStudentAndAssignment(Long studentId, Long assignmentId) {
        return submissionRepository.findByAssignmentIdAndStudentIdAndActiveTrue(assignmentId, studentId);
    }

    /**
     * Get all late submissions for an assignment
     */
    public List<Submission> getLateSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignmentIdAndIsLateTrueAndActiveTrue(assignmentId);
    }

    /**
     * Get ungraded submissions for a course
     */
    public List<Submission> getUngradedSubmissionsByCourse(Long courseId) {
        return submissionRepository.findUngradedSubmissionsByCourse(courseId);
    }

    /**
     * Get submissions by status
     */
    public List<Submission> getSubmissionsByStatus(String status) {
        return submissionRepository.findByStatusAndActiveTrue(status);
    }

    /**
     * Delete a submission
     * Students can delete their own submissions before grading
     * Faculty can delete any submission
     */
    @Transactional
    public void deleteSubmission(Long submissionId, Long userId) throws IOException {
        Submission submission = getSubmissionById(submissionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // chekc permissions
        boolean isOwner = submission.getStudent().getId().equals(userId);
        boolean isFaculty = "FACULTY".equals(user.getRole());

        if (!isOwner && !isFaculty) {
            throw new IllegalStateException("You don't have permission to delete this submission");
        }

        // students can only delete ungraded submissions
        if (isOwner && submission.isGraded()) {
            throw new IllegalStateException("Cannot delete a graded submission");
        }

        // delete the file from file system
        try {
            fileStorageService.deleteFile(submission.getFilePath());
        } catch (IOException e) {
            // log error but continue with database deletion
            System.err.println("Failed to delete file: " + submission.getFilePath());
        }

        // soft delete the submission
        submission.setActive(false);
        submissionRepository.save(submission);
    }

    /**
     * Download a submission file
     */
    public Resource downloadSubmission(Long submissionId, Long userId) throws IOException {
        Submission submission = getSubmissionById(submissionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // chekc permissions - student can download their own, faculty can download any
        boolean isOwner = submission.getStudent().getId().equals(userId);
        boolean isFaculty = "FACULTY".equals(user.getRole());

        if (!isOwner && !isFaculty) {
            throw new IllegalStateException("You don't have permission to download this submission");
        }

        return fileStorageService.loadFileAsResource(submission.getFilePath());
    }

    // ========== HELPER METHODS ==========

    /**
     * Get count of submissions for an assignment
     */
    public long getSubmissionCountByAssignment(Long assignmentId) {
        return submissionRepository.countByAssignmentIdAndActiveTrue(assignmentId);
    }

    /**
     * Get count of ungraded submissions for an assignment
     */
    public long getUngradedSubmissionCount(Long assignmentId) {
        return submissionRepository.countByAssignmentIdAndStatusAndActiveTrue(assignmentId, "SUBMITTED");
    }

    /**
     * Get submissions by student for a specific course
     */
    public List<Submission> getSubmissionsByStudentAndCourse(Long studentId, Long courseId) {
        return submissionRepository.findSubmissionsByStudentAndCourse(studentId, courseId);
    }

    /**
     * Chekc if student has submitted an assignment
     */
    public boolean hasStudentSubmitted(Long studentId, Long assignmentId) {
        return submissionRepository.existsByAssignmentIdAndStudentIdAndActiveTrue(assignmentId, studentId);
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
