package com.sams.repository;

import com.sams.entity.Course;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * repository for course data access
 * has some custom queries for search and filtering
 * needed this becuase teh course management needs flexible querying
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // find course by course code - used in course lookup
    Optional<Course> findByCourseCode(String courseCode);

    // check if course code exists - for validation during creation
    boolean existsByCourseCode(String courseCode);

    // find all courses by instructor - used in faculty dashboard
    List<Course> findByInstructor(User instructor);

    // find courses by instructor id - alternative method for performace
    List<Course> findByInstructorId(Long instructorId);

    // search courses by name (contains, case insensitive) - for student search
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    // search courses by code (contains, case insensitive) - alternative search
    List<Course> findByCourseCodeContainingIgnoreCase(String courseCode);

    // find courses by credits - used in degree planning
    List<Course> findByCredits(Integer credits);

    // soft delete support - find only active courses
    List<Course> findByActiveTrue();

    // find active course by id - might need to optimize this later
    Optional<Course> findByIdAndActiveTrue(Long id);

    // find active course by code
    Optional<Course> findByCourseCodeAndActiveTrue(String courseCode);

    // find active courses by instructor
    List<Course> findByInstructorIdAndActiveTrue(Long instructorId);

    // search active courses by name
    List<Course> findByCourseNameContainingIgnoreCaseAndActiveTrue(String courseName);

    // find active courses by credits
    List<Course> findByCreditsAndActiveTrue(Integer credits);

    // check if active course code exists
    boolean existsByCourseCodeAndActiveTrue(String courseCode);

    // custom query for checking duplicate course codes excluding current course
    // needed this becuase JPA doesnt have built-in exclude functionality
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.courseCode = :code AND c.active = true AND c.id != :excludeId")
    boolean existsByCourseCodeAndActiveTrueExcludingId(@Param("code") String code, @Param("excludeId") Long excludeId);
}
