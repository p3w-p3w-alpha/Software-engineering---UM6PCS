package com.sams.controller;

import com.sams.dto.dashboard.*;
import com.sams.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Dashboard Analytics
 * Provides endpoints for comprehensive dashboard data
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get complete dashboard with all analytics
     * GET /api/dashboard/complete
     */
    @GetMapping("/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CompleteDashboard> getCompleteDashboard() {
        CompleteDashboard dashboard = dashboardService.getCompleteDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get basic dashboard statistics
     * GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        DashboardStats stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get enrollment trends
     * GET /api/dashboard/enrollment-trends?months=6
     */
    @GetMapping("/enrollment-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EnrollmentTrend>> getEnrollmentTrends(
            @RequestParam(defaultValue = "6") int months) {
        List<EnrollmentTrend> trends = dashboardService.getEnrollmentTrends(months);
        return ResponseEntity.ok(trends);
    }

    /**
     * Get grade distribution
     * GET /api/dashboard/grade-distribution
     */
    @GetMapping("/grade-distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<List<GradeDistribution>> getGradeDistribution() {
        List<GradeDistribution> distribution = dashboardService.getGradeDistribution();
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get financial summary
     * GET /api/dashboard/financial-summary
     */
    @GetMapping("/financial-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FinancialSummary> getFinancialSummary() {
        FinancialSummary summary = dashboardService.getFinancialSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Get gender demographics
     * GET /api/dashboard/gender-demographics
     */
    @GetMapping("/gender-demographics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Long>> getGenderDemographics() {
        Map<String, Long> demographics = dashboardService.getGenderDemographics();
        return ResponseEntity.ok(demographics);
    }

    /**
     * Get recent activities
     * GET /api/dashboard/recent-activities?limit=10
     */
    @GetMapping("/recent-activities")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<RecentActivity>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        List<RecentActivity> activities = dashboardService.getRecentActivities(limit);
        return ResponseEntity.ok(activities);
    }
}
