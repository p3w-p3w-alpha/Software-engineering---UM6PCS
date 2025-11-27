<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">System Health Monitor</h1>
          <p class="mt-2 text-sm text-gray-600">
            Real-time system performance and health metrics
          </p>
        </div>
        <div class="flex items-center space-x-2">
          <div class="flex items-center px-3 py-2 bg-green-100 rounded-lg">
            <div class="h-3 w-3 bg-green-500 rounded-full animate-pulse mr-2"></div>
            <span class="text-sm font-medium text-green-800">System Operational</span>
          </div>
          <button
            @click="refreshMetrics"
            class="p-2 border border-gray-300 rounded-lg hover:bg-gray-50"
            title="Refresh"
          >
            <svg class="h-5 w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </button>
        </div>
      </div>

      <!-- System Status Overview -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <p class="text-sm font-medium text-gray-600">Server Uptime</p>
            <div class="p-2 bg-green-100 rounded-lg">
              <svg class="h-5 w-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
              </svg>
            </div>
          </div>
          <p class="text-2xl font-bold text-gray-900">{{ metrics.uptime }}</p>
          <p class="text-sm text-green-600 mt-1">99.9% availability</p>
        </div>

        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <p class="text-sm font-medium text-gray-600">CPU Usage</p>
            <div class="p-2 bg-blue-100 rounded-lg">
              <svg class="h-5 w-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
              </svg>
            </div>
          </div>
          <p class="text-2xl font-bold text-gray-900">{{ metrics.cpuUsage }}%</p>
          <div class="mt-2">
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="h-2 rounded-full"
                :class="metrics.cpuUsage > 80 ? 'bg-red-600' : metrics.cpuUsage > 60 ? 'bg-yellow-600' : 'bg-green-600'"
                :style="{ width: `${metrics.cpuUsage}%` }"
              ></div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <p class="text-sm font-medium text-gray-600">Memory Usage</p>
            <div class="p-2 bg-purple-100 rounded-lg">
              <svg class="h-5 w-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
              </svg>
            </div>
          </div>
          <p class="text-2xl font-bold text-gray-900">{{ metrics.memoryUsage }}%</p>
          <p class="text-sm text-gray-600 mt-1">{{ metrics.memoryUsed }}GB / {{ metrics.memoryTotal }}GB</p>
        </div>

        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <p class="text-sm font-medium text-gray-600">Active Users</p>
            <div class="p-2 bg-yellow-100 rounded-lg">
              <svg class="h-5 w-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
          </div>
          <p class="text-2xl font-bold text-gray-900">{{ metrics.activeUsers }}</p>
          <p class="text-sm text-gray-600 mt-1">{{ metrics.peakUsers }} peak today</p>
        </div>
      </div>

      <!-- Database Performance -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Database Performance</h3>
          <div class="space-y-4">
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Query Response Time</p>
                <p class="text-2xl font-bold text-gray-900">{{ database.queryTime }}ms</p>
              </div>
              <div class="text-sm text-green-600">Optimal</div>
            </div>
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Active Connections</p>
                <p class="text-2xl font-bold text-gray-900">{{ database.activeConnections }}</p>
              </div>
              <div class="text-sm text-gray-600">/ {{ database.maxConnections }}</div>
            </div>
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Database Size</p>
                <p class="text-2xl font-bold text-gray-900">{{ database.size }}GB</p>
              </div>
              <div class="text-sm text-gray-600">{{ database.freeSpace }}GB free</div>
            </div>
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Transactions/sec</p>
                <p class="text-2xl font-bold text-gray-900">{{ database.transactionsPerSec }}</p>
              </div>
              <div class="text-sm text-green-600">Normal</div>
            </div>
          </div>
        </div>

        <!-- API Performance -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">API Performance</h3>
          <div class="space-y-4">
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Avg Response Time</p>
                <p class="text-2xl font-bold text-gray-900">{{ api.avgResponseTime }}ms</p>
              </div>
              <div class="text-sm text-green-600">Fast</div>
            </div>
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Requests/min</p>
                <p class="text-2xl font-bold text-gray-900">{{ api.requestsPerMin }}</p>
              </div>
              <div class="text-sm text-gray-600">Normal load</div>
            </div>
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Error Rate</p>
                <p class="text-2xl font-bold text-gray-900">{{ api.errorRate }}%</p>
              </div>
              <div class="text-sm" :class="api.errorRate < 1 ? 'text-green-600' : 'text-red-600'">
                {{ api.errorRate < 1 ? 'Excellent' : 'High' }}
              </div>
            </div>
            <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p class="text-sm text-gray-600">Success Rate</p>
                <p class="text-2xl font-bold text-gray-900">{{ api.successRate }}%</p>
              </div>
              <div class="text-sm text-green-600">Excellent</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Errors/Warnings -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">Recent Events</h3>
          <div class="flex items-center space-x-2">
            <button
              @click="filterEvents('all')"
              :class="eventFilter === 'all' ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-700'"
              class="px-3 py-1 rounded text-sm"
            >
              All
            </button>
            <button
              @click="filterEvents('errors')"
              :class="eventFilter === 'errors' ? 'bg-red-600 text-white' : 'bg-gray-100 text-gray-700'"
              class="px-3 py-1 rounded text-sm"
            >
              Errors
            </button>
            <button
              @click="filterEvents('warnings')"
              :class="eventFilter === 'warnings' ? 'bg-yellow-600 text-white' : 'bg-gray-100 text-gray-700'"
              class="px-3 py-1 rounded text-sm"
            >
              Warnings
            </button>
          </div>
        </div>

        <div class="space-y-3">
          <div
            v-for="event in filteredEvents"
            :key="event.id"
            class="flex items-start p-4 rounded-lg"
            :class="event.type === 'error' ? 'bg-red-50 border border-red-200' : event.type === 'warning' ? 'bg-yellow-50 border border-yellow-200' : 'bg-blue-50 border border-blue-200'"
          >
            <div class="flex-shrink-0 mr-3">
              <svg
                v-if="event.type === 'error'"
                class="h-5 w-5 text-red-600"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
              <svg
                v-else-if="event.type === 'warning'"
                class="h-5 w-5 text-yellow-600"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
              <svg
                v-else
                class="h-5 w-5 text-blue-600"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="flex-1">
              <p class="text-sm font-medium text-gray-900">{{ event.message }}</p>
              <p class="text-xs text-gray-500 mt-1">{{ event.timestamp }} • {{ event.source }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Service Status -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Service Status</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="service in services"
            :key="service.name"
            class="flex items-center justify-between p-4 border border-gray-200 rounded-lg"
          >
            <div class="flex items-center space-x-3">
              <div
                class="h-3 w-3 rounded-full"
                :class="service.status === 'operational' ? 'bg-green-500' : service.status === 'degraded' ? 'bg-yellow-500' : 'bg-red-500'"
              ></div>
              <div>
                <p class="text-sm font-medium text-gray-900">{{ service.name }}</p>
                <p class="text-xs text-gray-500">{{ service.uptime }}% uptime</p>
              </div>
            </div>
            <span
              class="text-xs font-medium px-2 py-1 rounded"
              :class="service.status === 'operational' ? 'bg-green-100 text-green-800' : service.status === 'degraded' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'"
            >
              {{ service.status }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const metrics = ref({
  uptime: '45d 12h 35m',
  cpuUsage: 42,
  memoryUsage: 68,
  memoryUsed: 10.8,
  memoryTotal: 16,
  activeUsers: 234,
  peakUsers: 412
})

const database = ref({
  queryTime: 12,
  activeConnections: 45,
  maxConnections: 200,
  size: 8.5,
  freeSpace: 23.5,
  transactionsPerSec: 1250
})

const api = ref({
  avgResponseTime: 85,
  requestsPerMin: 3420,
  errorRate: 0.3,
  successRate: 99.7
})

const eventFilter = ref('all')

const events = ref([
  { id: 1, type: 'info', message: 'Backup completed successfully', timestamp: '2 minutes ago', source: 'Backup Service' },
  { id: 2, type: 'warning', message: 'High memory usage detected (75%)', timestamp: '15 minutes ago', source: 'Monitoring System' },
  { id: 3, type: 'error', message: 'Failed to send notification email to 3 users', timestamp: '1 hour ago', source: 'Email Service' },
  { id: 4, type: 'info', message: 'Database optimization completed', timestamp: '2 hours ago', source: 'Database Service' },
  { id: 5, type: 'warning', message: 'API response time exceeded threshold (200ms)', timestamp: '3 hours ago', source: 'API Gateway' }
])

const services = ref([
  { name: 'Web Server', status: 'operational', uptime: 99.9 },
  { name: 'Database', status: 'operational', uptime: 99.8 },
  { name: 'API Gateway', status: 'operational', uptime: 99.7 },
  { name: 'WebSocket', status: 'operational', uptime: 99.9 },
  { name: 'Email Service', status: 'degraded', uptime: 98.2 },
  { name: 'File Storage', status: 'operational', uptime: 99.9 }
])

const filteredEvents = computed(() => {
  if (eventFilter.value === 'all') return events.value
  return events.value.filter(e => e.type === eventFilter.value.slice(0, -1))
})

let refreshInterval

onMounted(() => {
  // Simulate real-time updates
  refreshInterval = setInterval(updateMetrics, 5000)
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})

function updateMetrics() {
  // Simulate metric changes
  metrics.value.cpuUsage = Math.min(100, Math.max(0, metrics.value.cpuUsage + (Math.random() - 0.5) * 10))
  metrics.value.activeUsers = Math.floor(Math.random() * 100) + 200
  api.value.requestsPerMin = Math.floor(Math.random() * 1000) + 3000
}

function refreshMetrics() {
  updateMetrics()
  alert('Metrics refreshed!')
}

function filterEvents(filter) {
  eventFilter.value = filter
}
</script>
