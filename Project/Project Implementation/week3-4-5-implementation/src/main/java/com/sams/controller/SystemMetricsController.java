package com.sams.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for retrieving real-time system metrics and health status.
 * Provides endpoints for monitoring CPU, memory, storage, and system health.
 * Access restricted to ADMIN and SUPER_ADMIN roles only.
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://localhost:5173"})
public class SystemMetricsController {

    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;
    private final ThreadMXBean threadBean;
    private final RuntimeMXBean runtimeBean;
    private final long startTime;

    /**
     * Constructor initializes all MXBeans for system monitoring.
     * Captures start time for uptime calculations.
     */
    public SystemMetricsController() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * GET /api/system/metrics
     * Returns comprehensive system metrics including CPU, memory, storage, threads, and uptime.
     *
     * @return ResponseEntity containing system metrics data
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();

            // Get CPU metrics
            metrics.put("cpu", getCpuMetrics());

            // Get memory metrics
            metrics.put("memory", getMemoryMetrics());

            // Get storage metrics
            metrics.put("storage", getStorageMetrics());

            // Get thread metrics
            metrics.put("threads", getThreadMetrics());

            // Get system info and uptime
            metrics.put("system", getSystemInfo());

            // Timestamp
            metrics.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(metrics);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to retrieve system metrics");
            error.put("message", e.getMessage());
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/system/health
     * Returns system health status based on current metrics.
     * Evaluates CPU, memory, and storage to determine overall health.
     *
     * @return ResponseEntity containing health status
     */
    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();

            // Get current metrics
            Map<String, Object> cpuMetrics = getCpuMetrics();
            Map<String, Object> memoryMetrics = getMemoryMetrics();
            Map<String, Object> storageMetrics = getStorageMetrics();

            // Determine health status
            String status = "HEALTHY";
            StringBuilder issues = new StringBuilder();
            int issueCount = 0;

            // Check CPU load
            double systemLoad = (double) cpuMetrics.getOrDefault("systemLoadAverage", -1.0);
            int availableProcessors = (int) cpuMetrics.get("availableProcessors");
            if (systemLoad > availableProcessors * 0.8) {
                status = "WARNING";
                issues.append("High CPU load; ");
                issueCount++;
            }

            // Check memory usage
            double memoryUsagePercent = (double) memoryMetrics.get("usedPercentage");
            if (memoryUsagePercent > 85.0) {
                status = "CRITICAL";
                issues.append("Critical memory usage; ");
                issueCount++;
            } else if (memoryUsagePercent > 75.0) {
                if (!status.equals("CRITICAL")) {
                    status = "WARNING";
                }
                issues.append("High memory usage; ");
                issueCount++;
            }

            // Check storage usage
            double storageUsagePercent = (double) storageMetrics.get("usedPercentage");
            if (storageUsagePercent > 90.0) {
                status = "CRITICAL";
                issues.append("Critical storage usage; ");
                issueCount++;
            } else if (storageUsagePercent > 80.0) {
                if (!status.equals("CRITICAL")) {
                    status = "WARNING";
                }
                issues.append("High storage usage; ");
                issueCount++;
            }

            health.put("status", status);
            health.put("issueCount", issueCount);
            health.put("issues", issues.length() > 0 ? issues.toString().trim() : "None");
            health.put("cpuHealth", getCpuHealth(systemLoad, availableProcessors));
            health.put("memoryHealth", getMemoryHealth(memoryUsagePercent));
            health.put("storageHealth", getStorageHealth(storageUsagePercent));
            health.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(health);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("error", "Failed to retrieve system health");
            error.put("message", e.getMessage());
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves CPU metrics including load average and processor count.
     *
     * @return Map containing CPU metrics
     */
    private Map<String, Object> getCpuMetrics() {
        Map<String, Object> cpu = new HashMap<>();

        // Get system load average (1-minute load average)
        double loadAverage = osBean.getSystemLoadAverage();
        cpu.put("systemLoadAverage", loadAverage);

        // Get available processors
        int processors = osBean.getAvailableProcessors();
        cpu.put("availableProcessors", processors);

        // Calculate CPU usage percentage (load average / processors * 100)
        // Note: Load average can exceed 100% if load > processors
        double cpuUsagePercent = loadAverage >= 0 ? (loadAverage / processors) * 100 : -1.0;
        cpu.put("usagePercentage", cpuUsagePercent >= 0 ? Math.round(cpuUsagePercent * 100.0) / 100.0 : -1.0);

        // Get system CPU load (if available via com.sun.management.OperatingSystemMXBean)
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean =
                (com.sun.management.OperatingSystemMXBean) osBean;

            // System CPU load (0.0 to 1.0)
            double systemCpuLoad = sunOsBean.getCpuLoad();
            cpu.put("systemCpuLoad", systemCpuLoad >= 0 ? Math.round(systemCpuLoad * 10000.0) / 100.0 : -1.0);

            // Process CPU load (0.0 to 1.0)
            double processCpuLoad = sunOsBean.getProcessCpuLoad();
            cpu.put("processCpuLoad", processCpuLoad >= 0 ? Math.round(processCpuLoad * 10000.0) / 100.0 : -1.0);
        }

        cpu.put("architecture", osBean.getArch());
        cpu.put("operatingSystem", osBean.getName() + " " + osBean.getVersion());

        return cpu;
    }

    /**
     * Retrieves memory metrics including heap and non-heap memory usage.
     *
     * @return Map containing memory metrics
     */
    private Map<String, Object> getMemoryMetrics() {
        Map<String, Object> memory = new HashMap<>();

        // Heap memory (used by Java objects)
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        Map<String, Object> heap = new HashMap<>();
        heap.put("initMB", bytesToMB(heapMemory.getInit()));
        heap.put("usedMB", bytesToMB(heapMemory.getUsed()));
        heap.put("committedMB", bytesToMB(heapMemory.getCommitted()));
        heap.put("maxMB", bytesToMB(heapMemory.getMax()));
        heap.put("usedPercentage", calculatePercentage(heapMemory.getUsed(), heapMemory.getMax()));
        memory.put("heap", heap);

        // Non-heap memory (used by JVM internals, class metadata, etc.)
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        Map<String, Object> nonHeap = new HashMap<>();
        nonHeap.put("initMB", bytesToMB(nonHeapMemory.getInit()));
        nonHeap.put("usedMB", bytesToMB(nonHeapMemory.getUsed()));
        nonHeap.put("committedMB", bytesToMB(nonHeapMemory.getCommitted()));
        nonHeap.put("maxMB", bytesToMB(nonHeapMemory.getMax()));
        memory.put("nonHeap", nonHeap);

        // Total memory (heap + non-heap)
        long totalUsed = heapMemory.getUsed() + nonHeapMemory.getUsed();
        long totalCommitted = heapMemory.getCommitted() + nonHeapMemory.getCommitted();
        long totalMax = heapMemory.getMax() + nonHeapMemory.getMax();

        memory.put("totalUsedMB", bytesToMB(totalUsed));
        memory.put("totalCommittedMB", bytesToMB(totalCommitted));
        memory.put("totalMaxMB", bytesToMB(totalMax));
        memory.put("usedPercentage", calculatePercentage(totalUsed, totalMax));

        // Physical memory (if available via com.sun.management.OperatingSystemMXBean)
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean =
                (com.sun.management.OperatingSystemMXBean) osBean;

            long totalPhysicalMemory = sunOsBean.getTotalMemorySize();
            long freePhysicalMemory = sunOsBean.getFreeMemorySize();
            long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;

            Map<String, Object> physical = new HashMap<>();
            physical.put("totalMB", bytesToMB(totalPhysicalMemory));
            physical.put("freeMB", bytesToMB(freePhysicalMemory));
            physical.put("usedMB", bytesToMB(usedPhysicalMemory));
            physical.put("usedPercentage", calculatePercentage(usedPhysicalMemory, totalPhysicalMemory));
            memory.put("physical", physical);
        }

        return memory;
    }

    /**
     * Retrieves storage metrics for the root filesystem.
     *
     * @return Map containing storage metrics
     */
    private Map<String, Object> getStorageMetrics() {
        Map<String, Object> storage = new HashMap<>();

        // Get root filesystem
        File root = new File("/");

        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usableSpace = root.getUsableSpace();
        long usedSpace = totalSpace - freeSpace;

        storage.put("totalGB", bytesToGB(totalSpace));
        storage.put("freeGB", bytesToGB(freeSpace));
        storage.put("usableGB", bytesToGB(usableSpace));
        storage.put("usedGB", bytesToGB(usedSpace));
        storage.put("usedPercentage", calculatePercentage(usedSpace, totalSpace));

        return storage;
    }

    /**
     * Retrieves thread metrics including active and peak thread counts.
     *
     * @return Map containing thread metrics
     */
    private Map<String, Object> getThreadMetrics() {
        Map<String, Object> threads = new HashMap<>();

        threads.put("activeCount", threadBean.getThreadCount());
        threads.put("peakCount", threadBean.getPeakThreadCount());
        threads.put("daemonCount", threadBean.getDaemonThreadCount());
        threads.put("totalStartedCount", threadBean.getTotalStartedThreadCount());

        return threads;
    }

    /**
     * Retrieves system information including uptime and JVM details.
     *
     * @return Map containing system information
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> system = new HashMap<>();

        // JVM uptime
        long uptimeMillis = runtimeBean.getUptime();
        system.put("uptimeMillis", uptimeMillis);
        system.put("uptimeFormatted", formatUptime(uptimeMillis));

        // JVM info
        system.put("jvmName", runtimeBean.getVmName());
        system.put("jvmVersion", runtimeBean.getVmVersion());
        system.put("jvmVendor", runtimeBean.getVmVendor());

        // Java version
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("javaHome", System.getProperty("java.home"));

        return system;
    }

    /**
     * Determines CPU health status based on load and processor count.
     *
     * @param loadAverage System load average
     * @param processors Number of available processors
     * @return Health status string
     */
    private String getCpuHealth(double loadAverage, int processors) {
        if (loadAverage < 0) return "UNKNOWN";
        double loadPercentage = (loadAverage / processors) * 100;
        if (loadPercentage > 80) return "CRITICAL";
        if (loadPercentage > 60) return "WARNING";
        return "HEALTHY";
    }

    /**
     * Determines memory health status based on usage percentage.
     *
     * @param usagePercent Memory usage percentage
     * @return Health status string
     */
    private String getMemoryHealth(double usagePercent) {
        if (usagePercent > 85) return "CRITICAL";
        if (usagePercent > 75) return "WARNING";
        return "HEALTHY";
    }

    /**
     * Determines storage health status based on usage percentage.
     *
     * @param usagePercent Storage usage percentage
     * @return Health status string
     */
    private String getStorageHealth(double usagePercent) {
        if (usagePercent > 90) return "CRITICAL";
        if (usagePercent > 80) return "WARNING";
        return "HEALTHY";
    }

    /**
     * Converts bytes to megabytes.
     *
     * @param bytes Number of bytes
     * @return Megabytes rounded to 2 decimal places
     */
    private double bytesToMB(long bytes) {
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }

    /**
     * Converts bytes to gigabytes.
     *
     * @param bytes Number of bytes
     * @return Gigabytes rounded to 2 decimal places
     */
    private double bytesToGB(long bytes) {
        return Math.round((bytes / (1024.0 * 1024.0 * 1024.0)) * 100.0) / 100.0;
    }

    /**
     * Calculates percentage of used vs total.
     *
     * @param used Used amount
     * @param total Total amount
     * @return Percentage rounded to 2 decimal places
     */
    private double calculatePercentage(long used, long total) {
        if (total <= 0) return 0.0;
        return Math.round((used / (double) total * 100.0) * 100.0) / 100.0;
    }

    /**
     * Formats uptime in milliseconds to human-readable format.
     *
     * @param uptimeMillis Uptime in milliseconds
     * @return Formatted string (e.g., "2d 3h 45m 12s")
     */
    private String formatUptime(long uptimeMillis) {
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        hours = hours % 24;
        minutes = minutes % 60;
        seconds = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString();
    }
}
