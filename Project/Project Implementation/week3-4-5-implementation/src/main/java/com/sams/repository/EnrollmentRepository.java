package com.sams.repository;

import com.sams.entity.Course;
import com.sams.entity.Enrollment;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // find all enrollments for a student
    List<Enrollment> findByStudent(User student);

    // find all enrollments for a student by id
    List<Enrollment> findByStudentId(Long studentId);

    // find all enrollments for a course
    List<Enrollment> findByCourse(Course course);

    // find all enrollments for a course by id
    List<Enrollment> findByCourseId(Long courseId);

    // find enrollments by course id and status
    List<Enrollment> findByCourseIdAndStatus(Long courseId, String status);

    // check if student is already enrolled in a course
    boolean existsByStudentAndCourse(User student, Course course);

    // find enrollment by student and course
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    // find enrollments by status
    List<Enrollment> findByStatus(String status);

    // count enrollments for a course
    long countByCourse(Course course);

    // count active enrollments for a course (for capacity checking)
    long countByCourseAndStatus(Course course, String status);

    // find active enrollments for a student (for schedule conflict checking)
    List<Enrollment> findByStudentAndStatus(User student, String status);

    // find completed enrollments for a student (for prerequesite checking)
    List<Enrollment> findByStudentIdAndStatus(Long studentId, String status);

    // find waitlisted enrollments for a course (ordered by position)
    List<Enrollment> findByCourseAndStatusOrderByWaitlistPositionAsc(Course course, String status);

    // soft delete support - find only active enrollments
    List<Enrollment> findByActiveTrue();

    Optional<Enrollment> findByIdAndActiveTrue(Long id);

    List<Enrollment> findByStudentIdAndActiveTrue(Long studentId);

    List<Enrollment> findByCourseIdAndActiveTrue(Long courseId);

    List<Enrollment> findByStatusAndActiveTrue(String status);

    List<Enrollment> findByStudentAndStatusAndActiveTrue(User student, String status);

    long countByCourseAndStatusAndActiveTrue(Course course, String status);

    // find enrollments by payment
    List<Enrollment> findByPaymentId(Long paymentId);
}
