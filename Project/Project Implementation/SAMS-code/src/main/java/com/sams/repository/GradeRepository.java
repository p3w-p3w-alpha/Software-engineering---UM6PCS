package com.sams.repository;

import com.sams.entity.Course;
import com.sams.entity.Enrollment;
import com.sams.entity.Grade;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * repository for grade data access
 * handles student course grades and GPA calculations
 * might need to optimize teh GPA calculation query for performace
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // find grade by enrollment - used in grade view
    Optional<Grade> findByEnrollment(Enrollment enrollment);

    // find all grades for a student
    List<Grade> findByStudent(User student);

    // find all grades for a student by student id
    List<Grade> findByStudentId(Long studentId);

    // find all grades for a course
    List<Grade> findByCourse(Course course);

    // find all grades for a course by course id
    List<Grade> findByCourseId(Long courseId);

    // find grade by student and course - for quick lookup
    Optional<Grade> findByStudentAndCourse(User student, Course course);

    // check if grade exists for enrollment - for validation
    boolean existsByEnrollment(Enrollment enrollment);

    // custom query for calculating GPA for a student - needed this becuase JPA doesnt support weighted averages
    @Query("SELECT SUM(g.gradePoints * c.credits) / SUM(c.credits) " +
           "FROM Grade g JOIN g.course c WHERE g.student.id = :studentId")
    Double calculateGPA(@Param("studentId") Long studentId);

    // custom query for getting total credits completed by student - used in degree progress
    @Query("SELECT SUM(c.credits) FROM Grade g JOIN g.course c WHERE g.student.id = :studentId")
    Integer getTotalCreditsCompleted(@Param("studentId") Long studentId);

    // soft delete support - find only active grades
    List<Grade> findByActiveTrue();

    Optional<Grade> findByIdAndActiveTrue(Long id);

    List<Grade> findByStudentIdAndActiveTrue(Long studentId);

    List<Grade> findByCourseIdAndActiveTrue(Long courseId);

    Optional<Grade> findByEnrollmentIdAndActiveTrue(Long enrollmentId);

    // calculate GPA only for active grades
    @Query("SELECT SUM(g.gradePoints * c.credits) / SUM(c.credits) " +
           "FROM Grade g JOIN g.course c WHERE g.student.id = :studentId AND g.active = true")
    Double calculateActiveGPA(@Param("studentId") Long studentId);

    // get total credits for active grades only
    @Query("SELECT SUM(c.credits) FROM Grade g JOIN g.course c WHERE g.student.id = :studentId AND g.active = true")
    Integer getTotalActiveCreditsCompleted(@Param("studentId") Long studentId);

    // Dashboard - Get grade distribution
    @Query("SELECT g.gradeValue, COUNT(g) FROM Grade g WHERE g.active = true GROUP BY g.gradeValue")
    List<Object[]> getGradeDistribution();
}
