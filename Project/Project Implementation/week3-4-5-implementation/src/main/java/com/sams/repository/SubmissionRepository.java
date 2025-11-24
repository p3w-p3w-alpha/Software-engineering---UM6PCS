package com.sams.repository;

import com.sams.entity.Submission;
import com.sams.entity.Assignment;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // find all submissions for a specific assignment
    List<Submission> findByAssignmentIdAndActiveTrueOrderBySubmittedAtDesc(Long assignmentId);

    // find all submissions by a specific student
    List<Submission> findByStudentIdAndActiveTrueOrderBySubmittedAtDesc(Long studentId);

    // find submission for a specific student and assignment
    Optional<Submission> findByAssignmentIdAndStudentIdAndActiveTrue(Long assignmentId, Long studentId);

    // chekc if student has already submitted for an assignment
    boolean existsByAssignmentIdAndStudentIdAndActiveTrue(Long assignmentId, Long studentId);

    // find all late submissions for an assignment
    List<Submission> findByAssignmentIdAndIsLateTrueAndActiveTrue(Long assignmentId);

    // find all submissions by status
    List<Submission> findByStatusAndActiveTrue(String status);

    // find all submissions for an assignment by status
    List<Submission> findByAssignmentIdAndStatusAndActiveTrueOrderBySubmittedAtDesc(Long assignmentId, String status);

    // find all ungraded submissions for a course
    @Query("SELECT s FROM Submission s WHERE s.active = true AND s.status = 'SUBMITTED' " +
           "AND s.assignment.course.id = :courseId ORDER BY s.submittedAt ASC")
    List<Submission> findUngradedSubmissionsByCourse(@Param("courseId") Long courseId);

    // find all submissions graded by a specific faculty
    List<Submission> findByGradedByIdAndActiveTrueOrderByGradedAtDesc(Long facultyId);

    // count submissions for an assignment
    long countByAssignmentIdAndActiveTrue(Long assignmentId);

    // count ungraded submissions for an assignment
    long countByAssignmentIdAndStatusAndActiveTrue(Long assignmentId, String status);

    // find all submissions for assignments in a course
    @Query("SELECT s FROM Submission s WHERE s.active = true AND s.assignment.course.id = :courseId " +
           "ORDER BY s.submittedAt DESC")
    List<Submission> findSubmissionsByCourse(@Param("courseId") Long courseId);

    // find submissions by student for a specific course
    @Query("SELECT s FROM Submission s WHERE s.active = true AND s.student.id = :studentId " +
           "AND s.assignment.course.id = :courseId ORDER BY s.submittedAt DESC")
    List<Submission> findSubmissionsByStudentAndCourse(@Param("studentId") Long studentId,
                                                       @Param("courseId") Long courseId);
}
