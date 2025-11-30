# System Metrics API Documentation

## Overview
The SystemMetricsController provides real-time system monitoring capabilities for administrators. It uses Java's Management APIs (MXBeans) to collect actual system metrics rather than simulated data.

## Endpoints

### 1. GET /api/system/metrics
Retrieves comprehensive system metrics including CPU, memory, storage, threads, and uptime.

**Authentication Required:** Yes (ADMIN or SUPER_ADMIN role)

**Response Example:**
```json
{
  "cpu": {
    "systemLoadAverage": 2.45,
    "availableProcessors": 8,
    "usagePercentage": 30.63,
    "systemCpuLoad": 25.42,
    "processCpuLoad": 15.23,
    "architecture": "amd64",
    "operatingSystem": "Linux 6.6.87.2-microsoft-standard-WSL2"
  },
  "memory": {
    "heap": {
      "initMB": 256.0,
      "usedMB": 150.45,
      "committedMB": 512.0,
      "maxMB": 4096.0,
      "usedPercentage": 3.67
    },
    "nonHeap": {
      "initMB": 7.5,
      "usedMB": 50.23,
      "committedMB": 60.0,
      "maxMB": -1
    },
    "totalUsedMB": 200.68,
    "totalCommittedMB": 572.0,
    "totalMaxMB": 4095.0,
    "usedPercentage": 4.9,
    "physical": {
      "totalMB": 16384.0,
      "freeMB": 8192.0,
      "usedMB": 8192.0,
      "usedPercentage": 50.0
    }
  },
  "storage": {
    "totalGB": 500.0,
    "freeGB": 250.0,
    "usableGB": 245.0,
    "usedGB": 250.0,
    "usedPercentage": 50.0
  },
  "threads": {
    "activeCount": 25,
    "peakCount": 30,
    "daemonCount": 18,
    "totalStartedCount": 45
  },
  "system": {
    "uptimeMillis": 3600000,
    "uptimeFormatted": "1h 0m 0s",
    "jvmName": "OpenJDK 64-Bit Server VM",
    "jvmVersion": "17.0.2+8",
    "jvmVendor": "Oracle Corporation",
    "javaVersion": "17.0.2",
    "javaHome": "/usr/lib/jvm/java-17-openjdk"
  },
  "timestamp": 1732786574123
}
```

**Metrics Explanation:**

#### CPU Metrics
- `systemLoadAverage`: 1-minute load average (number of processes waiting for CPU)
- `availableProcessors`: Number of CPU cores/threads available
- `usagePercentage`: CPU usage calculated as (load / processors * 100)
- `systemCpuLoad`: System-wide CPU load (0-100%)
- `processCpuLoad`: JVM process CPU load (0-100%)
- `architecture`: CPU architecture (e.g., amd64, arm64)
- `operatingSystem`: Operating system name and version

#### Memory Metrics
- **Heap Memory**: Memory used by Java objects
  - `initMB`: Initial heap size
  - `usedMB`: Currently used heap memory
  - `committedMB`: Memory committed for JVM use
  - `maxMB`: Maximum heap size (-1 if unlimited)
  - `usedPercentage`: Percentage of max heap used

- **Non-Heap Memory**: Memory used by JVM internals (class metadata, code cache)

- **Physical Memory**: Total system RAM
  - `totalMB`: Total physical RAM
  - `freeMB`: Free RAM available
  - `usedMB`: RAM currently in use
  - `usedPercentage`: Percentage of RAM used

#### Storage Metrics
- `totalGB`: Total disk space
- `freeGB`: Free disk space
- `usableGB`: Usable disk space (may be less than free due to reserved space)
- `usedGB`: Used disk space
- `usedPercentage`: Percentage of disk used

#### Thread Metrics
- `activeCount`: Number of currently active threads
- `peakCount`: Peak number of threads since JVM started
- `daemonCount`: Number of daemon threads
- `totalStartedCount`: Total threads started since JVM started

#### System Information
- `uptimeMillis`: JVM uptime in milliseconds
- `uptimeFormatted`: Human-readable uptime (e.g., "2d 3h 45m 12s")
- `jvmName`: JVM implementation name
- `jvmVersion`: JVM version
- `jvmVendor`: JVM vendor
- `javaVersion`: Java version
- `javaHome`: Java installation directory

---

### 2. GET /api/system/health
Returns system health status based on current metrics with intelligent health evaluation.

**Authentication Required:** Yes (ADMIN or SUPER_ADMIN role)

**Response Example:**
```json
{
  "status": "WARNING",
  "issueCount": 2,
  "issues": "High memory usage; High storage usage;",
  "cpuHealth": "HEALTHY",
  "memoryHealth": "WARNING",
  "storageHealth": "WARNING",
  "timestamp": 1732786574123
}
```

**Health Status Values:**
- `HEALTHY`: All systems operating normally
- `WARNING`: One or more metrics approaching critical levels
- `CRITICAL`: One or more metrics at critical levels
- `ERROR`: Unable to retrieve health status

**Health Evaluation Criteria:**

#### CPU Health
- `HEALTHY`: Load < 60% of available processors
- `WARNING`: Load 60-80% of available processors
- `CRITICAL`: Load > 80% of available processors

#### Memory Health
- `HEALTHY`: Usage < 75%
- `WARNING`: Usage 75-85%
- `CRITICAL`: Usage > 85%

#### Storage Health
- `HEALTHY`: Usage < 80%
- `WARNING`: Usage 80-90%
- `CRITICAL`: Usage > 90%

---

## Usage Examples

### Using cURL

#### Get System Metrics
```bash
curl -X GET "http://localhost:8080/api/system/metrics" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get System Health
```bash
curl -X GET "http://localhost:8080/api/system/health" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Using JavaScript/Axios

```javascript
// Get system metrics
const getMetrics = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/system/metrics', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log('Metrics:', response.data);
  } catch (error) {
    console.error('Error fetching metrics:', error);
  }
};

// Get system health
const getHealth = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/system/health', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log('Health:', response.data);
  } catch (error) {
    console.error('Error fetching health:', error);
  }
};
```

---

## Security

### Authentication
Both endpoints require JWT authentication with ADMIN or SUPER_ADMIN role.

### Authorization
The `@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")` annotation ensures only authorized users can access system metrics.

### CORS
The controller includes CORS configuration for the following origins:
- http://localhost:8080 (Spring Boot)
- http://localhost:3000 (React)
- http://localhost:5173 (Vite)

---

## Error Handling

### Error Response Format
```json
{
  "error": "Failed to retrieve system metrics",
  "message": "Detailed error message here",
  "timestamp": 1732786574123
}
```

### HTTP Status Codes
- `200 OK`: Successful request
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: User does not have ADMIN or SUPER_ADMIN role
- `500 Internal Server Error`: Server-side error retrieving metrics

---

## Implementation Details

### MXBeans Used
1. **OperatingSystemMXBean**: CPU load, processors, OS info
2. **MemoryMXBean**: Heap and non-heap memory usage
3. **ThreadMXBean**: Thread counts and statistics
4. **RuntimeMXBean**: JVM uptime and version info
5. **com.sun.management.OperatingSystemMXBean**: Extended metrics (CPU load, physical memory)

### Real-Time Data
All metrics are collected in real-time when the endpoint is called. There is no caching or simulation of data.

### Platform Compatibility
- **Linux/Unix**: Full support for all metrics
- **Windows**: Full support for all metrics
- **macOS**: Full support for all metrics
- **Load Average**: Returns -1 on Windows (not available)

### Performance
- Metric collection is lightweight and fast (< 50ms typical)
- No database queries required
- Direct JVM and OS API calls
- Safe for frequent polling (e.g., every 5-10 seconds)

---

## Frontend Integration Example

```vue
<template>
  <div class="system-metrics">
    <div class="metric-card">
      <h3>CPU Usage</h3>
      <div class="metric-value">{{ metrics.cpu?.usagePercentage || 0 }}%</div>
      <div class="metric-health" :class="health.cpuHealth?.toLowerCase()">
        {{ health.cpuHealth }}
      </div>
    </div>

    <div class="metric-card">
      <h3>Memory Usage</h3>
      <div class="metric-value">{{ metrics.memory?.usedPercentage || 0 }}%</div>
      <div class="metric-health" :class="health.memoryHealth?.toLowerCase()">
        {{ health.memoryHealth }}
      </div>
    </div>

    <div class="metric-card">
      <h3>Storage Usage</h3>
      <div class="metric-value">{{ metrics.storage?.usedPercentage || 0 }}%</div>
      <div class="metric-health" :class="health.storageHealth?.toLowerCase()">
        {{ health.storageHealth }}
      </div>
    </div>

    <div class="metric-card">
      <h3>System Health</h3>
      <div class="metric-value">{{ health.status }}</div>
      <div class="metric-issues">{{ health.issues }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/services/api';

const metrics = ref({});
const health = ref({});

const fetchMetrics = async () => {
  try {
    const response = await api.get('/api/system/metrics');
    metrics.value = response.data;
  } catch (error) {
    console.error('Error fetching metrics:', error);
  }
};

const fetchHealth = async () => {
  try {
    const response = await api.get('/api/system/health');
    health.value = response.data;
  } catch (error) {
    console.error('Error fetching health:', error);
  }
};

onMounted(() => {
  fetchMetrics();
  fetchHealth();

  // Refresh every 10 seconds
  setInterval(() => {
    fetchMetrics();
    fetchHealth();
  }, 10000);
});
</script>

<style scoped>
.system-metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.metric-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 0.5rem 0;
}

.metric-health.healthy {
  color: #28a745;
}

.metric-health.warning {
  color: #ffc107;
}

.metric-health.critical {
  color: #dc3545;
}
</style>
```

---

## Testing

### Manual Testing
1. Start the Spring Boot application
2. Login as an ADMIN or SUPER_ADMIN user to get JWT token
3. Use the token to call the endpoints
4. Verify real-time metrics are returned

### Test Script
```bash
#!/bin/bash

# Login and get token
TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"

# Get system metrics
echo -e "\n=== System Metrics ==="
curl -s -X GET "http://localhost:8080/api/system/metrics" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.'

# Get system health
echo -e "\n=== System Health ==="
curl -s -X GET "http://localhost:8080/api/system/health" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.'
```

---

## Monitoring Best Practices

1. **Polling Interval**: Poll every 5-10 seconds for real-time monitoring
2. **Alerting**: Set up alerts for WARNING and CRITICAL health statuses
3. **Trend Analysis**: Store metrics over time to identify trends
4. **Dashboard**: Display metrics on admin dashboard for easy monitoring
5. **Resource Planning**: Use metrics to plan for scaling and upgrades

---

## Troubleshooting

### Metrics Showing -1
Some metrics may return -1 if they're not available on the platform:
- `systemLoadAverage`: Not available on Windows
- `maxMB` for non-heap: May be unlimited (-1)

### High Memory Usage
If memory usage is consistently high:
1. Check for memory leaks
2. Adjust JVM heap size (-Xmx parameter)
3. Enable garbage collection logging
4. Use profiling tools

### High CPU Usage
If CPU usage is consistently high:
1. Check for infinite loops or busy waiting
2. Review database query performance
3. Optimize algorithms
4. Consider horizontal scaling

---

## Related Files

- **Controller**: `/src/main/java/com/sams/controller/SystemMetricsController.java`
- **Security Config**: `/src/main/java/com/sams/config/SecurityConfig.java`
- **This Documentation**: `/SYSTEM_METRICS_API.md`

---

## License
Part of the SAMS (Student Academic Management System) project.
