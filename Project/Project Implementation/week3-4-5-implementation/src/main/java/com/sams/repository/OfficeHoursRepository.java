package com.sams.repository;

import com.sams.entity.OfficeHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository interface for OfficeHours entity
 * Provides data access methods for teacher office hours and consultation scheduling
 */
@Repository
public interface OfficeHoursRepository extends JpaRepository<OfficeHours, Long> {

    // Basic queries by teacher
    List<OfficeHours> findByTeacherId(Long teacherId);

    List<OfficeHours> findByTeacherIdAndActiveTrue(Long teacherId);

    List<OfficeHours> findByTeacherIdAndActiveTrueAndIsCancelledFalse(Long teacherId);

    // Find by day of week
    List<OfficeHours> findByTeacherIdAndDayOfWeek(Long teacherId, DayOfWeek dayOfWeek);

    List<OfficeHours> findByTeacherIdAndDayOfWeekAndActiveTrue(
        Long teacherId,
        DayOfWeek dayOfWeek
    );

    List<OfficeHours> findByDayOfWeekAndActiveTrue(DayOfWeek dayOfWeek);

    // Find by consultation type
    List<OfficeHours> findByTeacherIdAndConsultationType(Long teacherId, String consultationType);

    List<OfficeHours> findByConsultationTypeAndActiveTrue(String consultationType);

    // Find by location
    List<OfficeHours> findByLocation(String location);

    List<OfficeHours> findByTeacherIdAndLocation(Long teacherId, String location);

    // Find by recurring status
    List<OfficeHours> findByTeacherIdAndRecurringTrue(Long teacherId);

    List<OfficeHours> findByTeacherIdAndRecurringFalse(Long teacherId);

    // Find available office hours (active and not cancelled)
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.active = true AND " +
           "oh.isCancelled = false AND " +
           "(oh.effectiveFrom IS NULL OR oh.effectiveFrom <= :date) AND " +
           "(oh.effectiveUntil IS NULL OR oh.effectiveUntil >= :date)")
    List<OfficeHours> findAvailableForDate(
        @Param("teacherId") Long teacherId,
        @Param("date") LocalDate date
    );

    // Find office hours for a specific date
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.dayOfWeek = :dayOfWeek AND " +
           "oh.active = true AND " +
           "oh.isCancelled = false AND " +
           "(oh.effectiveFrom IS NULL OR oh.effectiveFrom <= :date) AND " +
           "(oh.effectiveUntil IS NULL OR oh.effectiveUntil >= :date) AND " +
           "(oh.cancelledOnDate IS NULL OR oh.cancelledOnDate != :date)")
    List<OfficeHours> findEffectiveOfficeHoursForDate(
        @Param("teacherId") Long teacherId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("date") LocalDate date
    );

    // Find overlapping office hours
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.dayOfWeek = :dayOfWeek AND " +
           "oh.active = true AND " +
           "oh.id != :excludeId AND " +
           "NOT (oh.endTime <= :startTime OR oh.startTime >= :endTime)")
    List<OfficeHours> findOverlappingOfficeHours(
        @Param("teacherId") Long teacherId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime,
        @Param("excludeId") Long excludeId
    );

    // Simpler version for creation (no excludeId needed)
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.dayOfWeek = :dayOfWeek AND " +
           "oh.active = true AND " +
           "NOT (oh.endTime <= :startTime OR oh.startTime >= :endTime)")
    List<OfficeHours> findOverlappingOfficeHoursForCreation(
        @Param("teacherId") Long teacherId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    // Find office hours within validity period
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.active = true AND " +
           "(oh.effectiveFrom IS NULL OR oh.effectiveFrom <= :currentDate) AND " +
           "(oh.effectiveUntil IS NULL OR oh.effectiveUntil >= :currentDate)")
    List<OfficeHours> findCurrentlyValidOfficeHours(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.active = true AND " +
           "(oh.effectiveFrom IS NULL OR oh.effectiveFrom <= :currentDate) AND " +
           "(oh.effectiveUntil IS NULL OR oh.effectiveUntil >= :currentDate)")
    List<OfficeHours> findCurrentlyValidOfficeHoursByTeacher(
        @Param("teacherId") Long teacherId,
        @Param("currentDate") LocalDate currentDate
    );

    // Find office hours by booking requirements
    List<OfficeHours> findByTeacherIdAndBookingRequiredTrue(Long teacherId);

    List<OfficeHours> findByTeacherIdAndBookingRequiredFalse(Long teacherId);

    // Find cancelled office hours
    List<OfficeHours> findByTeacherIdAndIsCancelledTrue(Long teacherId);

    List<OfficeHours> findByCancelledOnDate(LocalDate date);

    List<OfficeHours> findByTeacherIdAndCancelledOnDate(Long teacherId, LocalDate date);

    // Find by time range
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.startTime >= :startTime AND " +
           "oh.endTime <= :endTime")
    List<OfficeHours> findByTeacherAndTimeRange(
        @Param("teacherId") Long teacherId,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    // Statistics queries
    @Query("SELECT COUNT(oh) FROM OfficeHours oh WHERE oh.teacher.id = :teacherId AND oh.active = true")
    long countActiveOfficeHoursByTeacher(@Param("teacherId") Long teacherId);

    @Query("SELECT oh.dayOfWeek, COUNT(oh) FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND oh.active = true GROUP BY oh.dayOfWeek")
    List<Object[]> countOfficeHoursByDayOfWeek(@Param("teacherId") Long teacherId);

    @Query("SELECT oh.consultationType, COUNT(oh) FROM OfficeHours oh WHERE " +
           "oh.active = true GROUP BY oh.consultationType")
    List<Object[]> countOfficeHoursByConsultationType();

    // Find all teachers with office hours on a specific day
    @Query("SELECT DISTINCT oh.teacher.id FROM OfficeHours oh WHERE " +
           "oh.dayOfWeek = :dayOfWeek AND " +
           "oh.active = true AND " +
           "oh.isCancelled = false")
    List<Long> findTeacherIdsWithOfficeHoursOnDay(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Find upcoming office hours (within date range)
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.active = true AND " +
           "oh.isCancelled = false AND " +
           "(oh.effectiveFrom IS NULL OR oh.effectiveFrom <= :endDate) AND " +
           "(oh.effectiveUntil IS NULL OR oh.effectiveUntil >= :startDate)")
    List<OfficeHours> findUpcomingOfficeHours(
        @Param("teacherId") Long teacherId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Search by multiple filters
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "(:teacherId IS NULL OR oh.teacher.id = :teacherId) AND " +
           "(:dayOfWeek IS NULL OR oh.dayOfWeek = :dayOfWeek) AND " +
           "(:consultationType IS NULL OR oh.consultationType = :consultationType) AND " +
           "(:active IS NULL OR oh.active = :active) AND " +
           "(:bookingRequired IS NULL OR oh.bookingRequired = :bookingRequired)")
    List<OfficeHours> findByFilters(
        @Param("teacherId") Long teacherId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("consultationType") String consultationType,
        @Param("active") Boolean active,
        @Param("bookingRequired") Boolean bookingRequired
    );

    // Find office hours expiring soon
    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.active = true AND " +
           "oh.effectiveUntil IS NOT NULL AND " +
           "oh.effectiveUntil BETWEEN :startDate AND :endDate")
    List<OfficeHours> findExpiringSoon(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT oh FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND " +
           "oh.active = true AND " +
           "oh.effectiveUntil IS NOT NULL AND " +
           "oh.effectiveUntil BETWEEN :startDate AND :endDate")
    List<OfficeHours> findExpiringSoonByTeacher(
        @Param("teacherId") Long teacherId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Calculate total available slots
    @Query("SELECT SUM(CAST((EXTRACT(HOUR FROM oh.endTime) * 60 + EXTRACT(MINUTE FROM oh.endTime) - " +
           "EXTRACT(HOUR FROM oh.startTime) * 60 - EXTRACT(MINUTE FROM oh.startTime)) / " +
           "COALESCE(oh.slotDurationMinutes, 30) AS integer)) " +
           "FROM OfficeHours oh WHERE " +
           "oh.teacher.id = :teacherId AND oh.active = true AND oh.isCancelled = false")
    Long calculateTotalAvailableSlots(@Param("teacherId") Long teacherId);

    // Delete operations
    void deleteByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndDayOfWeekAndStartTimeAndEndTime(
        Long teacherId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
    );
}
