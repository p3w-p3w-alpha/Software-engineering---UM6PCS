package com.sams.service;

import com.sams.entity.Course;
import com.sams.entity.Enrollment;
import com.sams.entity.Grade;
import com.sams.entity.GradeHistory;
import com.sams.entity.User;
import com.sams.exception.EnrollmentNotFoundException;
import com.sams.exception.GradeFinalizedException;
import com.sams.repository.GradeRepository;
import com.sams.repository.GradeHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeHistoryRepository gradeHistoryRepository;
    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final CourseService courseService;

    // grade scale mapping - converts letter grades to grade points
    // this is the standard 4.0 scale used in most universities
    private static final Map<String, Double> GRADE_SCALE = new HashMap<>();

    static {
        // A grades
        GRADE_SCALE.put("A+", 4.0);
        GRADE_SCALE.put("A", 4.0);
        GRADE_SCALE.put("A-", 3.7);

        // B grades
        GRADE_SCALE.put("B+", 3.3);
        GRADE_SCALE.put("B", 3.0);
        GRADE_SCALE.put("B-", 2.7);

        // C grades
        GRADE_SCALE.put("C+", 2.3);
        GRADE_SCALE.put("C", 2.0);
        GRADE_SCALE.put("C-", 1.7);

        // D grades
        GRADE_SCALE.put("D+", 1.3);
        GRADE_SCALE.put("D", 1.0);
        GRADE_SCALE.put("D-", 0.7);

        // F grade
        GRADE_SCALE.put("F", 0.0);
    }

    // constructor injection
    public GradeService(GradeRepository gradeRepository,
                       GradeHistoryRepository gradeHistoryRepository,
                       EnrollmentService enrollmentService,
                       UserService userService,
                       CourseService courseService) {
        this.gradeRepository = gradeRepository;
        this.gradeHistoryRepository = gradeHistoryRepository;
        this.enrollmentService = enrollmentService;
        this.userService = userService;
        this.courseService = courseService;
    }

    // create or update grade for an enrollment with history tracking
    @Transactional
    public Grade assignGrade(Long enrollmentId, String gradeValue, Long modifiedBy) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);

        // validate that enrollment is completed
        if (!"COMPLETED".equals(enrollment.getStatus())) {
            throw new IllegalArgumentException("Can only assign grades to completed enrollments");
        }

        // validate grade value
        if (!GRADE_SCALE.containsKey(gradeValue)) {
            throw new IllegalArgumentException("Invalid grade value: " + gradeValue);
        }

        // get grade points from scale
        Double gradePoints = GRADE_SCALE.get(gradeValue);

        // check if grade already exists for this enrollment
        Optional<Grade> existingGrade = gradeRepository.findByEnrollment(enrollment);

        Grade grade;
        String actionType;
        String previousValue = null;
        Double previousPoints = null;

        if (existingGrade.isPresent()) {
            // update existing grade
            grade = existingGrade.get();

            // chekc if grade is finalized
            if (grade.getFinalized()) {
                throw new GradeFinalizedException(grade.getId());
            }

            // store previous values for history
            previousValue = grade.getGradeValue();
            previousPoints = grade.getGradePoints();
            actionType = "UPDATE";

            grade.setGradeValue(gradeValue);
            grade.setGradePoints(gradePoints);
        } else {
            // create new grade
            grade = new Grade();
            grade.setStudent(enrollment.getStudent());
            grade.setCourse(enrollment.getCourse());
            grade.setEnrollment(enrollment);
            grade.setGradeValue(gradeValue);
            grade.setGradePoints(gradePoints);
            actionType = "CREATE";
        }

        // save grade first
        grade = gradeRepository.save(grade);

        // create history entry
        User modifier = userService.getUserById(modifiedBy);
        createGradeHistory(grade, previousValue, gradeValue, previousPoints, gradePoints, modifier, actionType, null);

        return grade;
    }

    // get grade by id
    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + id));
    }

    // get all grades
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // get grades for a student
    public List<Grade> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    // get grades for a course
    public List<Grade> getGradesByCourse(Long courseId) {
        return gradeRepository.findByCourseId(courseId);
    }

    // get grade by enrollment
    public Optional<Grade> getGradeByEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
        return gradeRepository.findByEnrollment(enrollment);
    }

    // calcualte GPA for a student
    // GPA = sum of (grade points * credits) / sum of credits
    public Double calculateGPA(Long studentId) {
        User student = userService.getUserById(studentId);
        List<Grade> grades = gradeRepository.findByStudent(student);

        if (grades.isEmpty()) {
            return 0.0; // no grades yet
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Grade grade : grades) {
            Course course = grade.getCourse();
            totalPoints += grade.getGradePoints() * course.getCredits();
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        // round to 2 decimal places
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    // get total credits completed by student
    public Integer getTotalCreditsCompleted(Long studentId) {
        User student = userService.getUserById(studentId);
        List<Grade> grades = gradeRepository.findByStudent(student);

        return grades.stream()
                .mapToInt(grade -> grade.getCourse().getCredits())
                .sum();
    }

    // get GPA summary for a student
    public GPASummary getGPASummary(Long studentId) {
        Double gpa = calculateGPA(studentId);
        Integer totalCredits = getTotalCreditsCompleted(studentId);
        List<Grade> grades = getGradesByStudent(studentId);

        return new GPASummary(gpa, totalCredits, grades.size());
    }

    // update grade with history tracking
    @Transactional
    public Grade updateGrade(Long gradeId, String gradeValue, Long modifiedBy, String reason) {
        Grade grade = getGradeById(gradeId);

        // chekc if grade is finalized
        if (grade.getFinalized()) {
            throw new GradeFinalizedException(gradeId);
        }

        // validate grade value
        if (!GRADE_SCALE.containsKey(gradeValue)) {
            throw new IllegalArgumentException("Invalid grade value: " + gradeValue);
        }

        Double gradePoints = GRADE_SCALE.get(gradeValue);

        // store previous values for history
        String previousValue = grade.getGradeValue();
        Double previousPoints = grade.getGradePoints();

        grade.setGradeValue(gradeValue);
        grade.setGradePoints(gradePoints);

        grade = gradeRepository.save(grade);

        // create history entry
        User modifier = userService.getUserById(modifiedBy);
        createGradeHistory(grade, previousValue, gradeValue, previousPoints, gradePoints, modifier, "UPDATE", reason);

        return grade;
    }

    // delete grade
    @Transactional
    public void deleteGrade(Long gradeId) {
        Grade grade = getGradeById(gradeId);
        gradeRepository.delete(grade);
    }

    // helper method to get grade points for a letter grade
    public static Double getGradePoints(String gradeValue) {
        return GRADE_SCALE.getOrDefault(gradeValue, 0.0);
    }

    // helper method to get all valid grades
    public static Map<String, Double> getGradeScale() {
        return new HashMap<>(GRADE_SCALE);
    }

    // finalize a grade - prevents further modification
    @Transactional
    public Grade finalizeGrade(Long gradeId, Long finalizedBy) {
        Grade grade = getGradeById(gradeId);

        if (grade.getFinalized()) {
            throw new IllegalStateException("Grade is already finalized");
        }

        grade.finalizeGrade(finalizedBy);
        grade = gradeRepository.save(grade);

        // create history entry
        User modifier = userService.getUserById(finalizedBy);
        createGradeHistory(grade, grade.getGradeValue(), grade.getGradeValue(),
                         grade.getGradePoints(), grade.getGradePoints(),
                         modifier, "FINALIZE", "Grade finalized");

        return grade;
    }

    // unfinalize a grade (admin only)
    @Transactional
    public Grade unfinalizeGrade(Long gradeId, Long unfinalizedBy, String reason) {
        Grade grade = getGradeById(gradeId);

        if (!grade.getFinalized()) {
            throw new IllegalStateException("Grade is not finalized");
        }

        grade.unfinalizeGrade();
        grade = gradeRepository.save(grade);

        // create history entry
        User modifier = userService.getUserById(unfinalizedBy);
        createGradeHistory(grade, grade.getGradeValue(), grade.getGradeValue(),
                         grade.getGradePoints(), grade.getGradePoints(),
                         modifier, "UNFINALIZE", reason);

        return grade;
    }

    // finalize all grades for a course
    @Transactional
    public List<Grade> finalizeAllGradesForCourse(Long courseId, Long finalizedBy) {
        List<Grade> grades = getGradesByCourse(courseId);
        User modifier = userService.getUserById(finalizedBy);

        for (Grade grade : grades) {
            if (!grade.getFinalized()) {
                grade.finalizeGrade(finalizedBy);
                gradeRepository.save(grade);

                // create history entry
                createGradeHistory(grade, grade.getGradeValue(), grade.getGradeValue(),
                                 grade.getGradePoints(), grade.getGradePoints(),
                                 modifier, "FINALIZE", "Batch finalization for course");
            }
        }

        return grades;
    }

    // get grade history for a specific grade
    public List<GradeHistory> getGradeHistory(Long gradeId) {
        return gradeHistoryRepository.findByGradeIdOrderByModifiedAtDesc(gradeId);
    }

    // helper method to create grade history entry
    private void createGradeHistory(Grade grade, String previousValue, String newValue,
                                    Double previousPoints, Double newPoints,
                                    User modifiedBy, String actionType, String reason) {
        GradeHistory history = new GradeHistory();
        history.setGrade(grade);
        history.setPreviousValue(previousValue);
        history.setNewValue(newValue);
        history.setPreviousPoints(previousPoints);
        history.setNewPoints(newPoints);
        history.setModifiedBy(modifiedBy);
        history.setActionType(actionType);
        history.setReason(reason);

        gradeHistoryRepository.save(history);
    }

    // nested class for GPA summary
    public static class GPASummary {
        private Double gpa;
        private Integer totalCredits;
        private Integer totalCourses;

        public GPASummary(Double gpa, Integer totalCredits, Integer totalCourses) {
            this.gpa = gpa;
            this.totalCredits = totalCredits;
            this.totalCourses = totalCourses;
        }

        public Double getGpa() { return gpa; }
        public void setGpa(Double gpa) { this.gpa = gpa; }
        public Integer getTotalCredits() { return totalCredits; }
        public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }
        public Integer getTotalCourses() { return totalCourses; }
        public void setTotalCourses(Integer totalCourses) { this.totalCourses = totalCourses; }
    }
}
