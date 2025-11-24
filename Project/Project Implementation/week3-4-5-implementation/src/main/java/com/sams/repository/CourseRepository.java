package com.sams.repository;

import com.sams.entity.Course;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // find course by course code
    Optional<Course> findByCourseCode(String courseCode);

    // check if course code exists
    boolean existsByCourseCode(String courseCode);

    // find all courses by instructor
    List<Course> findByInstructor(User instructor);

    // find courses by instructor id
    List<Course> findByInstructorId(Long instructorId);

    // search courses by name (contains, case insensitive)
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    // search courses by code (contains, case insensitive)
    List<Course> findByCourseCodeContainingIgnoreCase(String courseCode);

    // find courses by credits
    List<Course> findByCredits(Integer credits);

    // soft delete support - find only active courses
    List<Course> findByActiveTrue();

    Optional<Course> findByIdAndActiveTrue(Long id);

    Optional<Course> findByCourseCodeAndActiveTrue(String courseCode);

    List<Course> findByInstructorIdAndActiveTrue(Long instructorId);

    List<Course> findByCourseNameContainingIgnoreCaseAndActiveTrue(String courseName);

    List<Course> findByCreditsAndActiveTrue(Integer credits);

    boolean existsByCourseCodeAndActiveTrue(String courseCode);

    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.courseCode = :code AND c.active = true AND c.id != :excludeId")
    boolean existsByCourseCodeAndActiveTrueExcludingId(@Param("code") String code, @Param("excludeId") Long excludeId);
}
