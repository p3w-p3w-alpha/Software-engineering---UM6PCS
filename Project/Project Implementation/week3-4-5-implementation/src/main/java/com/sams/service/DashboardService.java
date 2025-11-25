package com.sams.service;

import com.sams.dto.dashboard.*;
import com.sams.entity.Payment;
import com.sams.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Growth rates (simplified - comparing current vs previous month)
        // For a real implementation, you'd need to track historical data
        stats.setStudentGrowthRate(0.0); // Placeholder
        stats.setFacultyGrowthRate(0.0); // Placeholder
        stats.setEnrollmentGrowthRate(0.0); // Placeholder

        return stats;
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

            // Count enrollments for this month
            // This is a simplified version - ideally you'd query by enrollment date
            long count = enrollmentRepository.count();

            trends.add(new EnrollmentTrend(period, count / months)); // Simplified distribution
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

        // Note: This requires gender field in User entity
        // For now, return mock data or implement after adding gender field
        demographics.put("MALE", 0L);
        demographics.put("FEMALE", 0L);
        demographics.put("OTHER", 0L);

        return demographics;
    }

    /**
     * Get recent activities
     */
    public List<RecentActivity> getRecentActivities(int limit) {
        List<RecentActivity> activities = new ArrayList<>();

        // This is a simplified version
        // In a real implementation, you'd have an Activity/AuditLog table

        // For now, return recent user creations
        userRepository.findAll().stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .limit(limit)
            .forEach(user -> {
                RecentActivity activity = new RecentActivity();
                activity.setActivityType("USER_CREATED");
                activity.setDescription("New user created: " + user.getUsername());
                activity.setPerformedBy("System");
                activity.setTimestamp(user.getCreatedAt());
                activity.setIcon("user-plus");
                activities.add(activity);
            });

        return activities;
    }
}
