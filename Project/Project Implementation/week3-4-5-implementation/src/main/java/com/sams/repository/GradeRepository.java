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

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // find grade by enrollment
    Optional<Grade> findByEnrollment(Enrollment enrollment);

    // find all grades for a student
    List<Grade> findByStudent(User student);

    // find all grades for a student by student id
    List<Grade> findByStudentId(Long studentId);

    // find all grades for a course
    List<Grade> findByCourse(Course course);

    // find all grades for a course by course id
    List<Grade> findByCourseId(Long courseId);

    // find grade by student and course
    Optional<Grade> findByStudentAndCourse(User student, Course course);

    // chekc if grade exists for enrollment
    boolean existsByEnrollment(Enrollment enrollment);

    // calcualte GPA for a student using custom query
    @Query("SELECT SUM(g.gradePoints * c.credits) / SUM(c.credits) " +
           "FROM Grade g JOIN g.course c WHERE g.student.id = :studentId")
    Double calculateGPA(@Param("studentId") Long studentId);

    // get total credits completed by student
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
}
