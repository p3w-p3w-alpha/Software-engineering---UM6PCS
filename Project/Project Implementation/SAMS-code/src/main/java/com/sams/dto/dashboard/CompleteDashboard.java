package com.sams.dto.dashboard;

import java.util.List;
import java.util.Map;

/**
 * DTO that aggregates all dashboard data
 */
public class CompleteDashboard {

    private DashboardStats stats;
    private List<EnrollmentTrend> enrollmentTrends;
    private List<GradeDistribution> gradeDistribution;
    private FinancialSummary financialSummary;
    private Map<String, Long> genderDemographics; // MALE, FEMALE, OTHER -> count
    private List<RecentActivity> recentActivities;

    // Constructors
    public CompleteDashboard() {
    }

    // Getters and Setters
    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public List<EnrollmentTrend> getEnrollmentTrends() {
        return enrollmentTrends;
    }

    public void setEnrollmentTrends(List<EnrollmentTrend> enrollmentTrends) {
        this.enrollmentTrends = enrollmentTrends;
    }

    public List<GradeDistribution> getGradeDistribution() {
        return gradeDistribution;
    }

    public void setGradeDistribution(List<GradeDistribution> gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }

    public FinancialSummary getFinancialSummary() {
        return financialSummary;
    }

    public void setFinancialSummary(FinancialSummary financialSummary) {
        this.financialSummary = financialSummary;
    }

    public Map<String, Long> getGenderDemographics() {
        return genderDemographics;
    }

    public void setGenderDemographics(Map<String, Long> genderDemographics) {
        this.genderDemographics = genderDemographics;
    }

    public List<RecentActivity> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<RecentActivity> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
