package com.sams.service;

import com.sams.dto.dashboard.*;
import com.sams.entity.ActivityLog;
import com.sams.entity.Payment;
import com.sams.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    /**
     * Get complete dashboard with all analytics
     */
    public CompleteDashboard getCompleteDashboard() {
        CompleteDashboard dashboard = new CompleteDashboard();

        dashboard.setStats(getDashboardStats());
        dashboard.setEnrollmentTrends(getEnrollmentTrends(6)); // last 6 months
        dashboard.setGradeDistribution(getGradeDistribution());
        dashboard.setFinancialSummary(getFinancialSummary());
        dashboard.setGenderDemographics(getGenderDemographics());
        dashboard.setRecentActivities(getRecentActivities(10)); // last 10 activities

        return dashboard;
    }

    /**
     * Get basic dashboard statistics
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // Total counts
        long totalStudents = userRepository.countByRole("STUDENT");
        long totalFaculty = userRepository.countByRole("FACULTY");
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        stats.setTotalStudents(totalStudents);
        stats.setTotalFaculty(totalFaculty);
        stats.setTotalCourses(totalCourses);
        stats.setTotalEnrollments(totalEnrollments);

        // Active/Inactive counts
        stats.setActiveStudents(userRepository.countByRoleAndActive("STUDENT", true));
        stats.setInactiveStudents(userRepository.countByRoleAndActive("STUDENT", false));
        stats.setActiveFaculty(userRepository.countByRoleAndActive("FACULTY", true));
        stats.setInactiveFaculty(userRepository.countByRoleAndActive("FACULTY", false));

        // Frontend-expected fields
        long totalUsers = userRepository.count();
        stats.setTotalUsers(totalUsers);
        stats.setActiveCourses(totalCourses); // All courses are considered active

        // Count pending payments using repository method (avoid loading all payments)
        long pendingPayments = paymentRepository.countByStatus("PENDING");
        stats.setPendingPayments(pendingPayments);

        // Growth rates - compare current month vs previous month
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
        LocalDateTime startOfTwoMonthsAgo = startOfCurrentMonth.minusMonths(2);

        // Student growth
        long studentsThisMonth = userRepository.countByRoleAndCreatedAtBetween("STUDENT", startOfCurrentMonth, now);
        long studentsLastMonth = userRepository.countByRoleAndCreatedAtBetween("STUDENT", startOfPreviousMonth, startOfCurrentMonth);
        stats.setStudentGrowthRate(calculateGrowthRate(studentsLastMonth, studentsThisMonth));

        // Faculty growth
        long facultyThisMonth = userRepository.countByRoleAndCreatedAtBetween("FACULTY", startOfCurrentMonth, now);
        long facultyLastMonth = userRepository.countByRoleAndCreatedAtBetween("FACULTY", startOfPreviousMonth, startOfCurrentMonth);
        stats.setFacultyGrowthRate(calculateGrowthRate(facultyLastMonth, facultyThisMonth));

        // Enrollment growth
        long enrollmentsThisMonth = enrollmentRepository.countByEnrollmentDateBetween(startOfCurrentMonth, now);
        long enrollmentsLastMonth = enrollmentRepository.countByEnrollmentDateBetween(startOfPreviousMonth, startOfCurrentMonth);
        stats.setEnrollmentGrowthRate(calculateGrowthRate(enrollmentsLastMonth, enrollmentsThisMonth));

        return stats;
    }

    /**
     * Calculate growth rate between two periods
     */
    private Double calculateGrowthRate(long previous, long current) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double)(current - previous) / previous) * 100.0;
    }

    /**
     * Get enrollment trends over the last N months
     */
    public List<EnrollmentTrend> getEnrollmentTrends(int months) {
        List<EnrollmentTrend> trends = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            String period = monthDate.format(formatter);

            // Get actual enrollment count for this month
            LocalDateTime startOfMonth = monthDate.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = monthDate.plusMonths(1).withDayOfMonth(1).atStartOfDay();

            long count = enrollmentRepository.countByEnrollmentDateBetween(startOfMonth, endOfMonth);
            trends.add(new EnrollmentTrend(period, count));
        }

        return trends;
    }

    /**
     * Get grade distribution
     */
    public List<GradeDistribution> getGradeDistribution() {
        List<Object[]> results = gradeRepository.getGradeDistribution();
        List<GradeDistribution> distribution = new ArrayList<>();

        long total = results.stream().mapToLong(r -> (Long) r[1]).sum();

        for (Object[] result : results) {
            String grade = (String) result[0];
            Long count = (Long) result[1];
            Double percentage = total > 0 ? (count * 100.0 / total) : 0.0;

            distribution.add(new GradeDistribution(grade, count, percentage));
        }

        return distribution;
    }

    /**
     * Get financial summary
     */
    public FinancialSummary getFinancialSummary() {
        FinancialSummary summary = new FinancialSummary();

        List<Payment> allPayments = paymentRepository.findAll();

        // Calculate totals by status
        for (Payment payment : allPayments) {
            summary.setTotalPaymentCount(summary.getTotalPaymentCount() + 1);

            String status = payment.getStatus();
            if ("PENDING".equals(status)) {
                summary.setPendingPaymentCount(summary.getPendingPaymentCount() + 1);
                summary.setPendingPayments(summary.getPendingPayments().add(payment.getAmount()));
            } else if ("APPROVED".equals(status) || "PAID".equals(status)) {
                summary.setCompletedPaymentCount(summary.getCompletedPaymentCount() + 1);
                summary.setCompletedPayments(summary.getCompletedPayments().add(payment.getAmount()));
            } else if ("REJECTED".equals(status)) {
                summary.setRejectedPaymentCount(summary.getRejectedPaymentCount() + 1);
                summary.setRejectedPayments(summary.getRejectedPayments().add(payment.getAmount()));
            }
        }

        // Calculate total revenue and collection rate
        summary.setTotalRevenue(summary.getCompletedPayments());

        if (summary.getTotalPaymentCount() > 0) {
            double collectionRate = (summary.getCompletedPaymentCount() * 100.0) / summary.getTotalPaymentCount();
            summary.setCollectionRate(collectionRate);
        }

        return summary;
    }

    /**
     * Get gender demographics
     */
    public Map<String, Long> getGenderDemographics() {
        Map<String, Long> demographics = new HashMap<>();

        // Count users by gender
        demographics.put("MALE", userRepository.countByGenderAndActiveTrue("MALE"));
        demographics.put("FEMALE", userRepository.countByGenderAndActiveTrue("FEMALE"));
        demographics.put("OTHER", userRepository.countByGenderAndActiveTrue("OTHER"));
        demographics.put("PREFER_NOT_TO_SAY", userRepository.countByGenderAndActiveTrue("PREFER_NOT_TO_SAY"));

        // Count users without gender set (null) - use count instead of loading all users
        long totalActive = userRepository.countByActiveTrue();
        long withGender = demographics.values().stream().mapToLong(Long::longValue).sum();
        demographics.put("UNSPECIFIED", totalActive - withGender);

        return demographics;
    }

    /**
     * Get recent activities
     */
    public List<RecentActivity> getRecentActivities(int limit) {
        List<RecentActivity> activities = new ArrayList<>();

        // First, try to get from ActivityLog table
        List<ActivityLog> logs = activityLogRepository.findRecentActivities(PageRequest.of(0, limit));

        if (!logs.isEmpty()) {
            // Use activity logs
            for (ActivityLog log : logs) {
                RecentActivity activity = new RecentActivity();
                activity.setId(log.getId());
                activity.setActivityType(log.getActivityType());
                activity.setType(getActivityTypeForFrontend(log.getActivityType()));
                activity.setDescription(log.getDescription());
                activity.setMessage(log.getDescription());
                activity.setPerformedBy(log.getPerformedByUsername());
                activity.setTimestamp(log.getCreatedAt());
                activity.setIcon(getIconForActivityType(log.getActivityType()));
                activities.add(activity);
            }
        } else {
            // Fallback: Generate activities from recent user creations
            // Use paginated query to avoid loading all users (N+1 fix)
            final long[] counter = {1};
            userRepository.findRecentUsers(PageRequest.of(0, limit))
                .forEach(user -> {
                    RecentActivity activity = new RecentActivity();
                    activity.setId(counter[0]++);
                    activity.setActivityType("USER_CREATED");
                    activity.setType("user");
                    activity.setDescription("New user created: " + user.getUsername());
                    activity.setMessage("New user created: " + user.getUsername());
                    activity.setPerformedBy("System");
                    activity.setTimestamp(user.getCreatedAt());
                    activity.setIcon("user-plus");
                    activities.add(activity);
                });
        }

        return activities;
    }

    /**
     * Get frontend-friendly type from activity type
     */
    private String getActivityTypeForFrontend(String activityType) {
        if (activityType == null) return "info";
        switch (activityType) {
            case "USER_CREATED":
            case "USER_UPDATED":
            case "USER_DELETED":
                return "user";
            case "ENROLLMENT_CREATED":
            case "ENROLLMENT_DROPPED":
                return "enrollment";
            case "GRADE_ASSIGNED":
            case "GRADE_UPDATED":
                return "grade";
            case "PAYMENT_SUBMITTED":
            case "PAYMENT_APPROVED":
            case "PAYMENT_REJECTED":
                return "payment";
            case "COURSE_CREATED":
            case "COURSE_UPDATED":
                return "course";
            default:
                return "info";
        }
    }

    /**
     * Get icon for activity type
     */
    private String getIconForActivityType(String activityType) {
        if (activityType == null) return "info-circle";
        switch (activityType) {
            case "USER_CREATED":
                return "user-plus";
            case "USER_UPDATED":
                return "user-edit";
            case "USER_DELETED":
                return "user-minus";
            case "ENROLLMENT_CREATED":
                return "book-open";
            case "ENROLLMENT_DROPPED":
                return "book-dead";
            case "GRADE_ASSIGNED":
            case "GRADE_UPDATED":
                return "graduation-cap";
            case "PAYMENT_SUBMITTED":
                return "credit-card";
            case "PAYMENT_APPROVED":
                return "check-circle";
            case "PAYMENT_REJECTED":
                return "times-circle";
            case "COURSE_CREATED":
            case "COURSE_UPDATED":
                return "chalkboard-teacher";
            default:
                return "info-circle";
        }
    }
}
