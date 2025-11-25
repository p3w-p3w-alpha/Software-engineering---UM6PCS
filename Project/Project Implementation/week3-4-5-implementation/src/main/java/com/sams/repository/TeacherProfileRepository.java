package com.sams.repository;

import com.sams.entity.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TeacherProfile entity
 * Provides data access methods for teacher profile management
 */
@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    // Basic queries
    Optional<TeacherProfile> findByUserId(Long userId);

    Optional<TeacherProfile> findByEmployeeId(String employeeId);

    // Find by status
    List<TeacherProfile> findByActiveTrue();

    List<TeacherProfile> findByActiveFalse();

    List<TeacherProfile> findByProfileCompletedTrue();

    List<TeacherProfile> findByProfileCompletedFalse();

    // Find by department and designation
    List<TeacherProfile> findByDepartment(String department);

    List<TeacherProfile> findByDesignation(String designation);

    List<TeacherProfile> findByDepartmentAndDesignation(String department, String designation);

    // Find by availability
    List<TeacherProfile> findByAvailableForConsultationTrue();

    List<TeacherProfile> findByAvailableForConsultationTrueAndActiveTrue();

    // Find by experience
    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.yearsOfExperience >= :minYears")
    List<TeacherProfile> findByMinimumExperience(@Param("minYears") Integer minYears);

    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.yearsOfExperience BETWEEN :minYears AND :maxYears")
    List<TeacherProfile> findByExperienceRange(
        @Param("minYears") Integer minYears,
        @Param("maxYears") Integer maxYears
    );

    // Find by qualifications
    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.qualification LIKE %:keyword%")
    List<TeacherProfile> findByQualificationContaining(@Param("keyword") String keyword);

    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.specialization LIKE %:keyword%")
    List<TeacherProfile> findBySpecializationContaining(@Param("keyword") String keyword);

    // Combined search query
    @Query("SELECT tp FROM TeacherProfile tp WHERE " +
           "(LOWER(tp.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tp.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tp.department) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tp.designation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tp.specialization) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tp.qualification) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<TeacherProfile> searchTeachers(@Param("searchTerm") String searchTerm);

    // Advanced search with filters
    @Query("SELECT tp FROM TeacherProfile tp WHERE " +
           "(:department IS NULL OR tp.department = :department) AND " +
           "(:designation IS NULL OR tp.designation = :designation) AND " +
           "(:active IS NULL OR tp.active = :active) AND " +
           "(:availableForConsultation IS NULL OR tp.availableForConsultation = :availableForConsultation)")
    List<TeacherProfile> findByFilters(
        @Param("department") String department,
        @Param("designation") String designation,
        @Param("active") Boolean active,
        @Param("availableForConsultation") Boolean availableForConsultation
    );

    // Statistics queries
    @Query("SELECT COUNT(tp) FROM TeacherProfile tp WHERE tp.active = true")
    long countActiveTeachers();

    @Query("SELECT COUNT(tp) FROM TeacherProfile tp WHERE tp.department = :department AND tp.active = true")
    long countByDepartmentAndActiveTrue(@Param("department") String department);

    @Query("SELECT tp.department, COUNT(tp) FROM TeacherProfile tp WHERE tp.active = true GROUP BY tp.department")
    List<Object[]> countTeachersByDepartment();

    @Query("SELECT tp.designation, COUNT(tp) FROM TeacherProfile tp WHERE tp.active = true GROUP BY tp.designation")
    List<Object[]> countTeachersByDesignation();

    // Top rated teachers
    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.averageRating IS NOT NULL AND tp.active = true ORDER BY tp.averageRating DESC")
    List<TeacherProfile> findTopRatedTeachers();

    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.averageRating >= :minRating AND tp.active = true ORDER BY tp.averageRating DESC")
    List<TeacherProfile> findByMinimumRating(@Param("minRating") Double minRating);

    // Most experienced teachers
    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.active = true ORDER BY tp.yearsOfExperience DESC")
    List<TeacherProfile> findMostExperiencedTeachers();

    // Teachers with most courses taught
    @Query("SELECT tp FROM TeacherProfile tp WHERE tp.active = true ORDER BY tp.totalCoursesTaught DESC")
    List<TeacherProfile> findTeachersByCoursesTeaught();

    // Check availability for courses
    @Query("SELECT tp FROM TeacherProfile tp WHERE " +
           "tp.active = true AND " +
           "tp.maxCoursesPerSemester > :currentCourseCount")
    List<TeacherProfile> findAvailableForNewCourse(@Param("currentCourseCount") Integer currentCourseCount);

    // Incomplete profiles
    @Query("SELECT tp FROM TeacherProfile tp WHERE " +
           "(tp.qualification IS NULL OR tp.specialization IS NULL OR " +
           "tp.department IS NULL OR tp.designation IS NULL OR tp.bio IS NULL) " +
           "AND tp.active = true")
    List<TeacherProfile> findIncompleteProfiles();

    // Delete operations
    boolean existsByUserId(Long userId);

    boolean existsByEmployeeId(String employeeId);

    void deleteByUserId(Long userId);
}
