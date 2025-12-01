package com.sams.repository;

import com.sams.entity.Attendance;
import com.sams.entity.Attendance.AttendanceStatus;
import com.sams.entity.Course;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * repository for attendance data access
 * tracks student and faculty attendance records
 * needed this becuase we have complex attendance analytics requirements
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find attendance by user and date - for daily check-in
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);

    // Find all attendance records for a user
    List<Attendance> findByUserOrderByDateDesc(User user);

    // Find attendance records for a user within a date range
    List<Attendance> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);

    // Find attendance records for a specific date
    List<Attendance> findByDate(LocalDate date);

    // Find attendance records for a date range
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Find attendance by status
    List<Attendance> findByStatus(AttendanceStatus status);

    // Find attendance by user and status
    List<Attendance> findByUserAndStatus(User user, AttendanceStatus status);

    // custom query for finding attendance for a course - for faculty view
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.date = :date")
    List<Attendance> findByCourseAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);

    // custom query for getting attendance statistics for a user - needed this becuase JPA doesnt support GROUP BY with status
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.user.id = :userId " +
           "AND a.date BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Object[]> getAttendanceStatsByUser(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // custom query for getting attendance statistics for a date - for admin dashboard
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.date = :date GROUP BY a.status")
    List<Object[]> getAttendanceStatsByDate(@Param("date") LocalDate date);

    // custom query for getting attendance statistics for a date range
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.date BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Object[]> getAttendanceStatsByDateRange(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // custom query for getting attendance by user role
    @Query("SELECT a FROM Attendance a WHERE a.user.role = :role AND a.date = :date")
    List<Attendance> findByUserRoleAndDate(@Param("role") String role, @Param("date") LocalDate date);

    // custom query for getting attendance percentage for a user - might need to optimize
    // needed this becuase teh percentage calculation is complex
    @Query("SELECT CAST(COUNT(CASE WHEN a.status = 'PRESENT' OR a.status = 'LATE' THEN 1 END) AS double) / COUNT(*) * 100 " +
           "FROM Attendance a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate")
    Double getAttendancePercentage(@Param("userId") Long userId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    // custom query for counting attendance by status for a user - for reports
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.status = :status " +
           "AND a.date BETWEEN :startDate AND :endDate")
    Long countByUserAndStatusAndDateRange(@Param("userId") Long userId,
                                          @Param("status") AttendanceStatus status,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Find attendance records for a course within a date range
    List<Attendance> findByCourseAndDateBetween(Course course, LocalDate startDate, LocalDate endDate);
}
