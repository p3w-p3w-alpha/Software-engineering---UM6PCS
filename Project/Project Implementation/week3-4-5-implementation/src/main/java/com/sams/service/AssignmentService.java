package com.sams.service;

import com.sams.entity.Assignment;
import com.sams.entity.Course;
import com.sams.entity.User;
import com.sams.entity.Enrollment;
import com.sams.exception.ResourceNotFoundException;
import com.sams.repository.AssignmentRepository;
import com.sams.repository.CourseRepository;
import com.sams.repository.UserRepository;
import com.sams.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// service for managing assignments
@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                            CourseRepository courseRepository,
                            UserRepository userRepository,
                            EnrollmentRepository enrollmentRepository,
                            NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notificationService = notificationService;
    }

    // ========== CRUD OPERATIONS ==========

    /**
     * Create a new assignment
     */
    @Transactional
    public Assignment createAssignment(Assignment assignment, Long courseId, Long facultyId) {
        // validate course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // validate faculty exists
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + facultyId));

        // validate faculty role
        if (!"FACULTY".equals(faculty.getRole())) {
            throw new IllegalArgumentException("Only faculty members can create assignments");
        }

        // validate due date is in the future
        if (assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date must be in the future");
        }

        assignment.setCourse(course);
        assignment.setCreatedBy(faculty);

        Assignment savedAssignment = assignmentRepository.save(assignment);

        // notify all enrolled students about the new assignment
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, "ACTIVE");
        for (Enrollment enrollment : enrollments) {
            notificationService.notifyAssignmentCreated(enrollment.getStudent(), course, savedAssignment);
        }

        return savedAssignment;
    }

    /**
     * Get assignment by ID
     */
    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
    }

    /**
     * Get all active assignments
     */
    public List<Assignment> getAllActiveAssignments() {
        return assignmentRepository.findByActiveTrue();
    }

    /**
     * Get all assignments for a course
     */
    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseIdAndActiveTrueOrderByDueDateAsc(courseId);
    }

    /**
     * Get all assignments created by a faculty member
     */
    public List<Assignment> getAssignmentsByFaculty(Long facultyId) {
        return assignmentRepository.findByCreatedByIdAndActiveTrueOrderByDueDateDesc(facultyId);
    }

    /**
     * Get upcoming assignments (due in the future)
     */
    public List<Assignment> getUpcomingAssignments() {
        return assignmentRepository.findUpcomingAssignments(LocalDateTime.now());
    }

    /**
     * Get overdue assignments
     */
    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now());
    }

    /**
     * Get assignments due between two dates
     */
    public List<Assignment> getAssignmentsDueBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return assignmentRepository.findAssignmentsDueBetween(startDate, endDate);
    }

    /**
     * Search assignments by title
     */
    public List<Assignment> searchAssignmentsByTitle(String title) {
        return assignmentRepository.findByTitleContainingIgnoreCaseAndActiveTrue(title);
    }

    /**
     * Update an assignment
     * Only the creator can update an assignment
     */
    @Transactional
    public Assignment updateAssignment(Long assignmentId, Assignment updatedAssignment, Long facultyId) {
        Assignment existing = getAssignmentById(assignmentId);

        // chekc if faculty is the creator
        if (!existing.getCreatedBy().getId().equals(facultyId)) {
            throw new IllegalStateException("Only the creator can update this assignment");
        }

        // update fields
        if (updatedAssignment.getTitle() != null && !updatedAssignment.getTitle().trim().isEmpty()) {
            existing.setTitle(updatedAssignment.getTitle());
        }

        if (updatedAssignment.getDescription() != null) {
            existing.setDescription(updatedAssignment.getDescription());
        }

        if (updatedAssignment.getDueDate() != null) {
            // validate new due date is in the future
            if (updatedAssignment.getDueDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Due date must be in the future");
            }
            existing.setDueDate(updatedAssignment.getDueDate());

            // notify students of due date change
            List<Enrollment> enrollments = enrollmentRepository
                    .findByCourseIdAndStatus(existing.getCourse().getId(), "ACTIVE");
            for (Enrollment enrollment : enrollments) {
                notificationService.notifyAssignmentUpdated(enrollment.getStudent(),
                        existing.getCourse(), existing);
            }
        }

        if (updatedAssignment.getMaxPoints() != null) {
            existing.setMaxPoints(updatedAssignment.getMaxPoints());
        }

        if (updatedAssignment.getAllowLateSubmissions() != null) {
            existing.setAllowLateSubmissions(updatedAssignment.getAllowLateSubmissions());
        }

        if (updatedAssignment.getLatePenaltyPerDay() != null) {
            existing.setLatePenaltyPerDay(updatedAssignment.getLatePenaltyPerDay());
        }

        if (updatedAssignment.getAllowedFileTypes() != null) {
            existing.setAllowedFileTypes(updatedAssignment.getAllowedFileTypes());
        }

        if (updatedAssignment.getMaxFileSizeMb() != null) {
            existing.setMaxFileSizeMb(updatedAssignment.getMaxFileSizeMb());
        }

        return assignmentRepository.save(existing);
    }

    /**
     * Delete (deactivate) an assignment
     * Only the creator can delete an assignment
     */
    @Transactional
    public void deleteAssignment(Long assignmentId, Long facultyId) {
        Assignment assignment = getAssignmentById(assignmentId);

        // chekc if faculty is the creator
        if (!assignment.getCreatedBy().getId().equals(facultyId)) {
            throw new IllegalStateException("Only the creator can delete this assignment");
        }

        assignment.setActive(false);
        assignmentRepository.save(assignment);
    }

    // ========== HELPER METHODS ==========

    /**
     * Get count of assignments for a course
     */
    public long getAssignmentCountByCourse(Long courseId) {
        return assignmentRepository.countByCourseIdAndActiveTrue(courseId);
    }

    /**
     * Chekc if an assignment is overdue
     */
    public boolean isAssignmentOverdue(Long assignmentId) {
        Assignment assignment = getAssignmentById(assignmentId);
        return assignment.isOverdue();
    }

    /**
     * Get assignments for a student (based on enrolled courses)
     */
    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsForStudent(Long studentId) {
        // get all courses the student is enrolled in
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, "ACTIVE");

        // use a custom query or stream to get all assignments for these courses
        return enrollments.stream()
                .flatMap(enrollment -> assignmentRepository
                        .findByCourseIdAndActiveTrueOrderByDueDateAsc(enrollment.getCourse().getId())
                        .stream())
                .distinct()
                .toList();
    }

    /**
     * Send reminder notifications for upcoming deadlines
     * This would typically be called by a scheduled job
     */
    @Transactional
    public void sendDeadlineReminders(int daysBeforeDeadline) {
        LocalDateTime startDate = LocalDateTime.now().plusDays(daysBeforeDeadline);
        LocalDateTime endDate = startDate.plusDays(1);

        List<Assignment> upcomingAssignments = getAssignmentsDueBetween(startDate, endDate);

        for (Assignment assignment : upcomingAssignments) {
            List<Enrollment> enrollments = enrollmentRepository
                    .findByCourseIdAndStatus(assignment.getCourse().getId(), "ACTIVE");

            for (Enrollment enrollment : enrollments) {
                notificationService.notifyAssignmentDeadlineApproaching(
                        enrollment.getStudent(),
                        assignment.getCourse(),
                        assignment,
                        daysBeforeDeadline
                );
            }
        }
    }
}
