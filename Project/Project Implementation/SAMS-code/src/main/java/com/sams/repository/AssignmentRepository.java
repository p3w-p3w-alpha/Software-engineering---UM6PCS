package com.sams.repository;

import com.sams.entity.Assignment;
import com.sams.entity.Course;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * repository for assignment data access
 * handles course assignments and deadlines
 * needed this becuase we need custom queries for date filtering
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // find all active assignments - for admin view
    List<Assignment> findByActiveTrue();

    // find all assignments for a specific course
    List<Assignment> findByCourseIdAndActiveTrueOrderByDueDateAsc(Long courseId);

    // find all assignments created by a specific faculty member
    List<Assignment> findByCreatedByIdAndActiveTrueOrderByDueDateDesc(Long facultyId);

    // custom query for finding upcoming assignments (due in the future) - used in student dashboard
    @Query("SELECT a FROM Assignment a WHERE a.active = true AND a.dueDate > :now ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignments(@Param("now") LocalDateTime now);

    // custom query for finding overdue assignments - might need to optimize
    @Query("SELECT a FROM Assignment a WHERE a.active = true AND a.dueDate < :now ORDER BY a.dueDate DESC")
    List<Assignment> findOverdueAssignments(@Param("now") LocalDateTime now);

    // find assignments by course and faculty
    List<Assignment> findByCourseIdAndCreatedByIdAndActiveTrue(Long courseId, Long facultyId);

    // find assignments due between two dates
    @Query("SELECT a FROM Assignment a WHERE a.active = true AND a.dueDate BETWEEN :startDate AND :endDate ORDER BY a.dueDate ASC")
    List<Assignment> findAssignmentsDueBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // count assignments for a course
    long countByCourseIdAndActiveTrue(Long courseId);

    // search assignments by title (case insensitive)
    List<Assignment> findByTitleContainingIgnoreCaseAndActiveTrue(String title);

    // find assignments for courses taught by a faculty member
    @Query("SELECT a FROM Assignment a WHERE a.active = true AND a.course.instructor.id = :facultyId " +
           "ORDER BY a.dueDate ASC")
    List<Assignment> findAssignmentsByFaculty(@Param("facultyId") Long facultyId);
}
