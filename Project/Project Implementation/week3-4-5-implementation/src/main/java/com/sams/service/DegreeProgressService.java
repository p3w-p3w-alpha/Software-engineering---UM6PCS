package com.sams.service;

import com.sams.entity.*;
import com.sams.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing degree programs and tracking student progress toward graduation
 */
@Service
public class DegreeProgressService {

    private final DegreeProgramRepository degreeProgramRepository;
    private final DegreeRequirementRepository requirementRepository;
    private final StudentDegreeProgressRepository progressRepository;
    private final GradeService gradeService;
    private final UserService userService;

    public DegreeProgressService(DegreeProgramRepository degreeProgramRepository,
                                DegreeRequirementRepository requirementRepository,
                                StudentDegreeProgressRepository progressRepository,
                                GradeService gradeService,
                                UserService userService) {
        this.degreeProgramRepository = degreeProgramRepository;
        this.requirementRepository = requirementRepository;
        this.progressRepository = progressRepository;
        this.gradeService = gradeService;
        this.userService = userService;
    }

    // ========== DEGREE PROGRAM MANAGEMENT ==========

    /**
     * Create a new degree program
     */
    @Transactional
    public DegreeProgram createDegreeProgram(DegreeProgram degreeProgram) {
        // validate uniqueness of code
        if (degreeProgramRepository.findByCode(degreeProgram.getCode()).isPresent()) {
            throw new IllegalArgumentException("Degree program with code " + degreeProgram.getCode() + " already exists");
        }

        return degreeProgramRepository.save(degreeProgram);
    }

    /**
     * Update degree program
     */
    @Transactional
    public DegreeProgram updateDegreeProgram(Long id, DegreeProgram updates) {
        DegreeProgram existing = getDegreeProgramById(id);

        // update fields
        existing.setName(updates.getName());
        existing.setDescription(updates.getDescription());
        existing.setTotalCreditsRequired(updates.getTotalCreditsRequired());
        existing.setMinimumGpa(updates.getMinimumGpa());
        existing.setDepartment(updates.getDepartment());
        existing.setTypicalDurationSemesters(updates.getTypicalDurationSemesters());
        existing.setActive(updates.getActive());

        return degreeProgramRepository.save(existing);
    }

    /**
     * Get degree program by ID
     */
    public DegreeProgram getDegreeProgramById(Long id) {
        return degreeProgramRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Degree program not found with id: " + id));
    }

    /**
     * Get all degree programs
     */
    public List<DegreeProgram> getAllDegreePrograms() {
        return degreeProgramRepository.findAll();
    }

    /**
     * Get all active degree programs
     */
    public List<DegreeProgram> getActiveDegreePrograms() {
        return degreeProgramRepository.findByActiveTrue();
    }

    /**
     * Get degree program by code
     */
    public Optional<DegreeProgram> getDegreeProgramByCode(String code) {
        return degreeProgramRepository.findByCode(code);
    }

    // ========== DEGREE REQUIREMENTS MANAGEMENT ==========

    /**
     * Add requirement to degree program
     */
    @Transactional
    public DegreeRequirement addRequirement(Long degreeProgramId, DegreeRequirement requirement) {
        DegreeProgram program = getDegreeProgramById(degreeProgramId);
        requirement.setDegreeProgram(program);
        return requirementRepository.save(requirement);
    }

    /**
     * Update requirement
     */
    @Transactional
    public DegreeRequirement updateRequirement(Long requirementId, DegreeRequirement updates) {
        DegreeRequirement existing = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Degree requirement not found with id: " + requirementId));

        existing.setName(updates.getName());
        existing.setDescription(updates.getDescription());
        existing.setType(updates.getType());
        existing.setCreditsRequired(updates.getCreditsRequired());
        existing.setSpecificCourse(updates.getSpecificCourse());
        existing.setMandatory(updates.getMandatory());
        existing.setDisplayOrder(updates.getDisplayOrder());
        existing.setActive(updates.getActive());

        return requirementRepository.save(existing);
    }

    /**
     * Get all requirements for a degree program
     */
    public List<DegreeRequirement> getRequirementsForProgram(Long degreeProgramId) {
        return requirementRepository.findByDegreeProgramIdOrderByDisplayOrderAsc(degreeProgramId);
    }

    /**
     * Delete requirement
     */
    @Transactional
    public void deleteRequirement(Long requirementId) {
        requirementRepository.deleteById(requirementId);
    }

    // ========== STUDENT PROGRESS TRACKING ==========

    /**
     * Enroll student in a degree program
     */
    @Transactional
    public StudentDegreeProgress enrollStudentInProgram(Long studentId, Long degreeProgramId, LocalDate startDate) {
        User student = userService.getUserById(studentId);

        if (!"STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("Only students can be enrolled in degree programs");
        }

        // check if student is already enrolled
        if (progressRepository.existsByStudentId(studentId)) {
            throw new IllegalStateException("Student is already enrolled in a degree program");
        }

        DegreeProgram program = getDegreeProgramById(degreeProgramId);

        StudentDegreeProgress progress = new StudentDegreeProgress(student, program, startDate);
        return progressRepository.save(progress);
    }

    /**
     * Get student's degree progress
     */
    public StudentDegreeProgress getStudentProgress(Long studentId) {
        return progressRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student is not enrolled in any degree program"));
    }

    /**
     * Update student's progress based on their grades
     * This should be called automatically when grades are posted
     */
    @Transactional
    public StudentDegreeProgress updateStudentProgress(Long studentId) {
        StudentDegreeProgress progress = getStudentProgress(studentId);

        // get student's GPA and total credits
        Double gpa = gradeService.calculateGPA(studentId);
        Integer totalCredits = gradeService.getTotalCreditsCompleted(studentId);

        // update progress
        progress.updateProgress(totalCredits, gpa);

        // check if student is on track
        boolean onTrack = calculateIfOnTrack(progress);
        progress.setOnTrack(onTrack);

        return progressRepository.save(progress);
    }

    /**
     * Calculate if student is on track for graduation
     */
    private boolean calculateIfOnTrack(StudentDegreeProgress progress) {
        if (progress.getExpectedGraduationDate() == null) {
            return true; // can't determine if no expected date
        }

        LocalDate now = LocalDate.now();
        LocalDate expected = progress.getExpectedGraduationDate();

        // if past expected graduation date and not completed, student is behind
        if (now.isAfter(expected) && progress.getCreditsRemaining() > 0) {
            return false;
        }

        // calculate expected progress percentage based on time elapsed
        LocalDate startDate = progress.getStartDate();
        if (startDate == null) {
            return true;
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, expected);
        long elapsedDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, now);

        if (totalDays <= 0) {
            return true;
        }

        double expectedPercentage = (elapsedDays / (double) totalDays) * 100.0;

        // student should be within 10% of expected progress
        return progress.getCompletionPercentage() >= (expectedPercentage - 10.0);
    }

    /**
     * Check if student meets graduation requirements
     */
    public boolean meetsGraduationRequirements(Long studentId) {
        StudentDegreeProgress progress = getStudentProgress(studentId);
        return progress.meetsGraduationRequirements();
    }

    /**
     * Mark student as graduated
     */
    @Transactional
    public StudentDegreeProgress markAsGraduated(Long studentId, LocalDate graduationDate) {
        StudentDegreeProgress progress = getStudentProgress(studentId);

        if (!progress.meetsGraduationRequirements()) {
            throw new IllegalStateException("Student does not meet graduation requirements");
        }

        progress.setStatus("COMPLETED");
        progress.setActualGraduationDate(graduationDate);
        progress.setCompletionPercentage(100.0);

        return progressRepository.save(progress);
    }

    /**
     * Get all students in a degree program
     */
    public List<StudentDegreeProgress> getStudentsInProgram(Long degreeProgramId) {
        return progressRepository.findByDegreeProgramId(degreeProgramId);
    }

    /**
     * Get students eligible for graduation
     */
    public List<StudentDegreeProgress> getEligibleForGraduation() {
        return progressRepository.findEligibleForGraduation();
    }

    /**
     * Get students at risk (low GPA or behind schedule)
     */
    public List<StudentDegreeProgress> getStudentsAtRisk() {
        return progressRepository.findStudentsAtRisk();
    }

    /**
     * Update student progress status
     */
    @Transactional
    public StudentDegreeProgress updateProgressStatus(Long studentId, String status) {
        StudentDegreeProgress progress = getStudentProgress(studentId);

        // validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Must be ACTIVE, ON_HOLD, COMPLETED, or WITHDRAWN");
        }

        progress.setStatus(status);
        return progressRepository.save(progress);
    }

    /**
     * Validate status
     */
    private boolean isValidStatus(String status) {
        return "ACTIVE".equals(status) || "ON_HOLD".equals(status) ||
               "COMPLETED".equals(status) || "WITHDRAWN".equals(status);
    }

    /**
     * Get detailed progress report for a student
     * Returns information about completed requirements vs remaining
     */
    public ProgressReport getProgressReport(Long studentId) {
        StudentDegreeProgress progress = getStudentProgress(studentId);
        List<DegreeRequirement> requirements = getRequirementsForProgram(progress.getDegreeProgram().getId());

        // In a full implementation, this would analyze which requirements are met
        // For now, we'll return basic information

        ProgressReport report = new ProgressReport();
        report.setStudentProgress(progress);
        report.setAllRequirements(requirements);
        report.setTotalRequirements(requirements.size());
        report.setCompletedRequirements(0); // TODO: Calculate based on student's completed courses
        report.setRemainingRequirements(requirements.size()); // TODO: Calculate properly

        return report;
    }

    /**
     * Inner class for progress report
     */
    public static class ProgressReport {
        private StudentDegreeProgress studentProgress;
        private List<DegreeRequirement> allRequirements;
        private int totalRequirements;
        private int completedRequirements;
        private int remainingRequirements;

        // getters and setters
        public StudentDegreeProgress getStudentProgress() {
            return studentProgress;
        }

        public void setStudentProgress(StudentDegreeProgress studentProgress) {
            this.studentProgress = studentProgress;
        }

        public List<DegreeRequirement> getAllRequirements() {
            return allRequirements;
        }

        public void setAllRequirements(List<DegreeRequirement> allRequirements) {
            this.allRequirements = allRequirements;
        }

        public int getTotalRequirements() {
            return totalRequirements;
        }

        public void setTotalRequirements(int totalRequirements) {
            this.totalRequirements = totalRequirements;
        }

        public int getCompletedRequirements() {
            return completedRequirements;
        }

        public void setCompletedRequirements(int completedRequirements) {
            this.completedRequirements = completedRequirements;
        }

        public int getRemainingRequirements() {
            return remainingRequirements;
        }

        public void setRemainingRequirements(int remainingRequirements) {
            this.remainingRequirements = remainingRequirements;
        }
    }
}
