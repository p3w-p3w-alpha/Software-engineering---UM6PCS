package com.sams.dto.attendance;

public class AttendanceStatistics {

    private Long totalCount;
    private Long presentCount;
    private Long lateCount;
    private Long absentCount;
    private Long sickCount;
    private Long excusedCount;
    private Double attendancePercentage;
    private String period; // e.g., "2024-01-01 to 2024-01-31"

    // Constructors
    public AttendanceStatistics() {
    }

    public AttendanceStatistics(Long totalCount, Long presentCount, Long lateCount,
                               Long absentCount, Long sickCount, Long excusedCount) {
        this.totalCount = totalCount;
        this.presentCount = presentCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.sickCount = sickCount;
        this.excusedCount = excusedCount;
        this.attendancePercentage = calculatePercentage();
    }

    // Calculate attendance percentage (Present + Late as attended)
    private Double calculatePercentage() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        long attended = (presentCount != null ? presentCount : 0) + (lateCount != null ? lateCount : 0);
        return (double) attended / totalCount * 100;
    }

    // Getters and Setters
    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        this.attendancePercentage = calculatePercentage();
    }

    public Long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(Long presentCount) {
        this.presentCount = presentCount;
        this.attendancePercentage = calculatePercentage();
    }

    public Long getLateCount() {
        return lateCount;
    }

    public void setLateCount(Long lateCount) {
        this.lateCount = lateCount;
        this.attendancePercentage = calculatePercentage();
    }

    public Long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(Long absentCount) {
        this.absentCount = absentCount;
    }

    public Long getSickCount() {
        return sickCount;
    }

    public void setSickCount(Long sickCount) {
        this.sickCount = sickCount;
    }

    public Long getExcusedCount() {
        return excusedCount;
    }

    public void setExcusedCount(Long excusedCount) {
        this.excusedCount = excusedCount;
    }

    public Double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(Double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
