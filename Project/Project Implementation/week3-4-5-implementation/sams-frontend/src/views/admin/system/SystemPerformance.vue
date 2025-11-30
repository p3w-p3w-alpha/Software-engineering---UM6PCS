<template>
  <div class="system-performance min-h-screen bg-gray-50 p-6">
    <!-- Header -->
    <div class="mb-6">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-3">
            <i class="pi pi-server text-blue-600"></i>
            System Performance Monitor
          </h1>
          <p class="text-gray-600 mt-2">Real-time system metrics and health monitoring</p>
        </div>

        <!-- Control Buttons -->
        <div class="flex items-center gap-3">
          <Button
            v-if="!isAutoRefresh"
            @click="startAutoRefresh"
            icon="pi pi-play"
            label="Start Auto-Refresh"
            class="p-button-success"
            :disabled="isLoading"
          />
          <Button
            v-else
            @click="stopAutoRefresh"
            icon="pi pi-pause"
            label="Stop Auto-Refresh"
            class="p-button-warning"
          />
          <Button
            @click="fetchAllData"
            icon="pi pi-refresh"
            label="Refresh Now"
            class="p-button-primary"
            :disabled="isLoading"
            :loading="isLoading"
          />
        </div>
      </div>

      <!-- Last Updated -->
      <div class="mt-4 flex items-center gap-2 text-sm text-gray-500">
        <i class="pi pi-clock"></i>
        <span>Last updated: {{ lastUpdated || 'Never' }}</span>
        <span v-if="isAutoRefresh" class="text-green-600 font-semibold ml-2">
          (Auto-refresh active: every 10 seconds)
        </span>
      </div>
    </div>

    <!-- Loading Overlay -->
    <div v-if="isLoading && !metrics" class="flex items-center justify-center py-20">
      <ProgressSpinner />
    </div>

    <!-- Error Message -->
    <Message v-if="error" severity="error" :closable="true" @close="error = null">
      <div class="flex items-center gap-2">
        <i class="pi pi-exclamation-triangle"></i>
        <span>{{ error }}</span>
      </div>
    </Message>

    <!-- Main Content -->
    <div v-if="metrics && health" class="space-y-6">
      <!-- System Health Status -->
      <Card class="shadow-lg">
        <template #title>
          <div class="flex items-center gap-3">
            <i class="pi pi-heart text-2xl"></i>
            System Health Status
          </div>
        </template>
        <template #content>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <!-- Overall Status -->
            <div class="flex flex-col items-center p-6 rounded-lg" :class="healthStatusClass">
              <i class="pi text-5xl mb-3" :class="healthStatusIcon"></i>
              <h3 class="text-2xl font-bold mb-2">{{ health.status }}</h3>
              <p class="text-sm text-center">Overall System Status</p>
            </div>

            <!-- Components Status -->
            <div class="col-span-1 md:col-span-2 space-y-4">
              <h4 class="text-lg font-semibold text-gray-700 mb-4">Component Health</h4>
              <div v-for="(component, name) in health.components" :key="name"
                   class="flex items-center justify-between p-4 rounded-lg border"
                   :class="getComponentClass(component.status)">
                <div class="flex items-center gap-3">
                  <i class="pi text-xl" :class="getComponentIcon(component.status)"></i>
                  <div>
                    <h5 class="font-semibold capitalize">{{ formatComponentName(name) }}</h5>
                    <p class="text-sm text-gray-600" v-if="component.details">
                      {{ formatComponentDetails(component.details) }}
                    </p>
                  </div>
                </div>
                <Tag :value="component.status" :severity="getComponentSeverity(component.status)" />
              </div>
            </div>
          </div>
        </template>
      </Card>

      <!-- Quick Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <!-- CPU Usage -->
        <Card class="shadow-lg hover:shadow-xl transition-shadow">
          <template #content>
            <div class="flex items-center justify-between mb-4">
              <div>
                <p class="text-gray-600 text-sm mb-1">CPU Usage</p>
                <h3 class="text-3xl font-bold text-blue-600">
                  {{ formatPercentage(metrics.cpu.usage) }}
                </h3>
              </div>
              <div class="w-16 h-16 rounded-full bg-blue-100 flex items-center justify-center">
                <i class="pi pi-microchip text-3xl text-blue-600"></i>
              </div>
            </div>
            <div class="space-y-2 text-sm text-gray-600">
              <div class="flex justify-between">
                <span>Processors:</span>
                <span class="font-semibold">{{ metrics.cpu.availableProcessors }}</span>
              </div>
              <div class="flex justify-between">
                <span>Load Avg:</span>
                <span class="font-semibold">{{ metrics.cpu.loadAverage.toFixed(2) }}</span>
              </div>
            </div>
          </template>
        </Card>

        <!-- Memory Usage -->
        <Card class="shadow-lg hover:shadow-xl transition-shadow">
          <template #content>
            <div class="flex items-center justify-between mb-4">
              <div>
                <p class="text-gray-600 text-sm mb-1">Memory Usage</p>
                <h3 class="text-3xl font-bold text-green-600">
                  {{ formatPercentage(metrics.memory.heap.usagePercentage) }}
                </h3>
              </div>
              <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
                <i class="pi pi-database text-3xl text-green-600"></i>
              </div>
            </div>
            <div class="space-y-2 text-sm text-gray-600">
              <div class="flex justify-between">
                <span>Used:</span>
                <span class="font-semibold">{{ formatBytes(metrics.memory.heap.used) }}</span>
              </div>
              <div class="flex justify-between">
                <span>Max:</span>
                <span class="font-semibold">{{ formatBytes(metrics.memory.heap.max) }}</span>
              </div>
            </div>
          </template>
        </Card>

        <!-- Storage Usage -->
        <Card class="shadow-lg hover:shadow-xl transition-shadow">
          <template #content>
            <div class="flex items-center justify-between mb-4">
              <div>
                <p class="text-gray-600 text-sm mb-1">Storage Usage</p>
                <h3 class="text-3xl font-bold text-purple-600">
                  {{ formatPercentage(metrics.storage.usagePercentage) }}
                </h3>
              </div>
              <div class="w-16 h-16 rounded-full bg-purple-100 flex items-center justify-center">
                <i class="pi pi-chart-pie text-3xl text-purple-600"></i>
              </div>
            </div>
            <div class="space-y-2 text-sm text-gray-600">
              <div class="flex justify-between">
                <span>Used:</span>
                <span class="font-semibold">{{ formatGB(metrics.storage.usedGB) }} GB</span>
              </div>
              <div class="flex justify-between">
                <span>Total:</span>
                <span class="font-semibold">{{ formatGB(metrics.storage.totalGB) }} GB</span>
              </div>
            </div>
          </template>
        </Card>

        <!-- System Uptime -->
        <Card class="shadow-lg hover:shadow-xl transition-shadow">
          <template #content>
            <div class="flex items-center justify-between mb-4">
              <div>
                <p class="text-gray-600 text-sm mb-1">System Uptime</p>
                <h3 class="text-2xl font-bold text-orange-600">
                  {{ formatUptime(metrics.system.uptime) }}
                </h3>
              </div>
              <div class="w-16 h-16 rounded-full bg-orange-100 flex items-center justify-center">
                <i class="pi pi-clock text-3xl text-orange-600"></i>
              </div>
            </div>
            <div class="space-y-2 text-sm text-gray-600">
              <div class="flex justify-between">
                <span>Threads:</span>
                <span class="font-semibold">{{ metrics.threads.active }}</span>
              </div>
              <div class="flex justify-between">
                <span>Peak:</span>
                <span class="font-semibold">{{ metrics.threads.peak }}</span>
              </div>
            </div>
          </template>
        </Card>
      </div>

      <!-- Detailed Metrics -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- CPU Details with Chart -->
        <Card class="shadow-lg">
          <template #title>
            <div class="flex items-center gap-3">
              <i class="pi pi-microchip text-blue-600"></i>
              CPU Performance
            </div>
          </template>
          <template #content>
            <div class="space-y-4">
              <div class="h-64">
                <Doughnut :data="cpuChartData" :options="chartOptions" />
              </div>
              <Divider />
              <div class="space-y-3">
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">CPU Usage</span>
                  <div class="flex items-center gap-3">
                    <ProgressBar :value="metrics.cpu.usage" class="w-32" :showValue="false" />
                    <span class="font-bold text-blue-600">{{ formatPercentage(metrics.cpu.usage) }}</span>
                  </div>
                </div>
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">Available Processors</span>
                  <span class="font-bold">{{ metrics.cpu.availableProcessors }}</span>
                </div>
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">System Load Average</span>
                  <span class="font-bold">{{ metrics.cpu.loadAverage.toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </template>
        </Card>

        <!-- Memory Details with Chart -->
        <Card class="shadow-lg">
          <template #title>
            <div class="flex items-center gap-3">
              <i class="pi pi-database text-green-600"></i>
              Memory Performance
            </div>
          </template>
          <template #content>
            <div class="space-y-4">
              <div class="h-64">
                <Bar :data="memoryChartData" :options="memoryChartOptions" />
              </div>
              <Divider />
              <div class="space-y-3">
                <div class="p-3 bg-gray-50 rounded-lg">
                  <h4 class="font-semibold text-gray-700 mb-2">Heap Memory</h4>
                  <div class="space-y-2">
                    <div class="flex justify-between text-sm">
                      <span>Used:</span>
                      <span class="font-semibold">{{ formatBytes(metrics.memory.heap.used) }}</span>
                    </div>
                    <ProgressBar :value="metrics.memory.heap.usagePercentage" :showValue="false" />
                    <div class="flex justify-between text-sm">
                      <span>Max:</span>
                      <span class="font-semibold">{{ formatBytes(metrics.memory.heap.max) }}</span>
                    </div>
                  </div>
                </div>
                <div class="p-3 bg-gray-50 rounded-lg">
                  <h4 class="font-semibold text-gray-700 mb-2">Physical Memory</h4>
                  <div class="space-y-2">
                    <div class="flex justify-between text-sm">
                      <span>Total:</span>
                      <span class="font-semibold">{{ formatBytes(metrics.memory.physical.total) }}</span>
                    </div>
                    <div class="flex justify-between text-sm">
                      <span>Free:</span>
                      <span class="font-semibold">{{ formatBytes(metrics.memory.physical.free) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </Card>

        <!-- Storage Details -->
        <Card class="shadow-lg">
          <template #title>
            <div class="flex items-center gap-3">
              <i class="pi pi-chart-pie text-purple-600"></i>
              Storage Performance
            </div>
          </template>
          <template #content>
            <div class="space-y-4">
              <div class="h-64">
                <Doughnut :data="storageChartData" :options="chartOptions" />
              </div>
              <Divider />
              <div class="space-y-3">
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">Total Space</span>
                  <span class="font-bold">{{ formatGB(metrics.storage.totalGB) }} GB</span>
                </div>
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">Used Space</span>
                  <div class="flex items-center gap-3">
                    <ProgressBar :value="metrics.storage.usagePercentage" class="w-32" :showValue="false" />
                    <span class="font-bold text-purple-600">{{ formatGB(metrics.storage.usedGB) }} GB</span>
                  </div>
                </div>
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">Free Space</span>
                  <span class="font-bold text-green-600">{{ formatGB(metrics.storage.freeGB) }} GB</span>
                </div>
                <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span class="text-gray-700">Usage Percentage</span>
                  <span class="font-bold text-purple-600">{{ formatPercentage(metrics.storage.usagePercentage) }}</span>
                </div>
              </div>
            </div>
          </template>
        </Card>

        <!-- System Information -->
        <Card class="shadow-lg">
          <template #title>
            <div class="flex items-center gap-3">
              <i class="pi pi-info-circle text-orange-600"></i>
              System Information
            </div>
          </template>
          <template #content>
            <div class="space-y-3">
              <div class="p-3 bg-gray-50 rounded-lg">
                <div class="flex items-center gap-2 mb-2">
                  <i class="pi pi-clock text-orange-600"></i>
                  <h4 class="font-semibold text-gray-700">Uptime</h4>
                </div>
                <p class="text-2xl font-bold text-orange-600">{{ formatUptime(metrics.system.uptime) }}</p>
                <p class="text-sm text-gray-600 mt-1">{{ formatUptimeDetailed(metrics.system.uptime) }}</p>
              </div>

              <div class="p-3 bg-gray-50 rounded-lg">
                <div class="flex items-center gap-2 mb-2">
                  <i class="pi pi-box text-blue-600"></i>
                  <h4 class="font-semibold text-gray-700">JVM Information</h4>
                </div>
                <div class="space-y-2 text-sm">
                  <div class="flex justify-between">
                    <span class="text-gray-600">Version:</span>
                    <span class="font-semibold">{{ metrics.system.jvmVersion }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">Vendor:</span>
                    <span class="font-semibold">{{ metrics.system.jvmVendor }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">Name:</span>
                    <span class="font-semibold text-xs">{{ metrics.system.jvmName }}</span>
                  </div>
                </div>
              </div>

              <div class="p-3 bg-gray-50 rounded-lg">
                <div class="flex items-center gap-2 mb-2">
                  <i class="pi pi-list text-green-600"></i>
                  <h4 class="font-semibold text-gray-700">Thread Information</h4>
                </div>
                <div class="space-y-2 text-sm">
                  <div class="flex justify-between">
                    <span class="text-gray-600">Active Threads:</span>
                    <span class="font-bold text-green-600">{{ metrics.threads.active }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">Peak Threads:</span>
                    <span class="font-semibold">{{ metrics.threads.peak }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">Daemon Threads:</span>
                    <span class="font-semibold">{{ metrics.threads.daemon }}</span>
                  </div>
                </div>
              </div>

              <div class="p-3 bg-gray-50 rounded-lg">
                <div class="flex items-center gap-2 mb-2">
                  <i class="pi pi-calendar text-purple-600"></i>
                  <h4 class="font-semibold text-gray-700">Timestamp</h4>
                </div>
                <p class="text-sm text-gray-600">{{ formatTimestamp(metrics.timestamp) }}</p>
              </div>
            </div>
          </template>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Chart as ChartJS, ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js'
import { Doughnut, Bar } from 'vue-chartjs'
import Card from 'primevue/card'
import Button from 'primevue/button'
import ProgressBar from 'primevue/progressbar'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import Tag from 'primevue/tag'
import Divider from 'primevue/divider'
import api from '@/services/api'
import { toast } from 'vue3-toastify'

// Register Chart.js components
ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend)

// Reactive state
const metrics = ref(null)
const health = ref(null)
const isLoading = ref(false)
const error = ref(null)
const lastUpdated = ref(null)
const isAutoRefresh = ref(false)
const refreshInterval = ref(null)

// Fetch system metrics
const fetchSystemMetrics = async () => {
  try {
    const response = await api.getSystemMetrics()
    const data = response.data

    // Map backend response to expected format
    metrics.value = {
      cpu: {
        usage: data.cpu?.usagePercentage || data.cpu?.systemCpuLoad || 0,
        availableProcessors: data.cpu?.availableProcessors || 0,
        loadAverage: data.cpu?.systemLoadAverage || 0
      },
      memory: {
        heap: {
          used: (data.memory?.heap?.usedMB || 0) * 1024 * 1024,
          max: (data.memory?.heap?.maxMB || 0) * 1024 * 1024,
          usagePercentage: data.memory?.heap?.usedPercentage || data.memory?.usedPercentage || 0
        },
        physical: {
          total: (data.memory?.physical?.totalMB || 0) * 1024 * 1024,
          free: (data.memory?.physical?.freeMB || 0) * 1024 * 1024
        }
      },
      storage: {
        totalGB: data.storage?.totalGB || 0,
        usedGB: data.storage?.usedGB || 0,
        freeGB: data.storage?.freeGB || 0,
        usagePercentage: data.storage?.usedPercentage || 0
      },
      threads: {
        active: data.threads?.activeCount || 0,
        peak: data.threads?.peakCount || 0,
        daemon: data.threads?.daemonCount || 0
      },
      system: {
        uptime: (data.system?.uptimeMillis || 0) / 1000,
        jvmVersion: data.system?.javaVersion || data.system?.jvmVersion || 'N/A',
        jvmVendor: data.system?.jvmVendor || 'N/A',
        jvmName: data.system?.jvmName || 'N/A'
      },
      timestamp: data.timestamp || Date.now()
    }
    error.value = null
    lastUpdated.value = new Date().toLocaleString()
  } catch (err) {
    console.error('Error fetching system metrics:', err)
    error.value = 'Failed to fetch system metrics. Please ensure the backend is running.'
    toast.error('Failed to fetch system metrics')
  }
}

// Fetch system health
const fetchSystemHealth = async () => {
  try {
    const response = await api.getSystemHealth()
    const data = response.data

    // Map backend response to expected format
    health.value = {
      status: data.status || 'UNKNOWN',
      components: {
        cpu: {
          status: data.cpuHealth || 'UNKNOWN',
          details: { load: data.issueCount || 0 }
        },
        memory: {
          status: data.memoryHealth || 'UNKNOWN',
          details: { issues: data.issues || 'None' }
        },
        storage: {
          status: data.storageHealth || 'UNKNOWN',
          details: null
        }
      }
    }
    error.value = null
  } catch (err) {
    console.error('Error fetching system health:', err)
    error.value = 'Failed to fetch system health. Please ensure the backend is running.'
    toast.error('Failed to fetch system health')
  }
}

// Fetch all data
const fetchAllData = async () => {
  isLoading.value = true
  try {
    await Promise.all([fetchSystemMetrics(), fetchSystemHealth()])
  } finally {
    isLoading.value = false
  }
}

// Auto-refresh controls
const startAutoRefresh = () => {
  isAutoRefresh.value = true
  refreshInterval.value = setInterval(() => {
    fetchAllData()
  }, 10000) // 10 seconds
  toast.success('Auto-refresh started (every 10 seconds)')
}

const stopAutoRefresh = () => {
  isAutoRefresh.value = false
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
    refreshInterval.value = null
  }
  toast.info('Auto-refresh stopped')
}

// CPU Chart Data
const cpuChartData = computed(() => {
  if (!metrics.value) return null

  const usage = metrics.value.cpu.usage
  const available = 100 - usage

  return {
    labels: ['Used', 'Available'],
    datasets: [{
      data: [usage, available],
      backgroundColor: ['#3B82F6', '#E5E7EB'],
      borderWidth: 0
    }]
  }
})

// Memory Chart Data
const memoryChartData = computed(() => {
  if (!metrics.value) return null

  return {
    labels: ['Heap Used', 'Heap Max', 'Physical Total', 'Physical Free'],
    datasets: [{
      label: 'Memory (MB)',
      data: [
        metrics.value.memory.heap.used / (1024 * 1024),
        metrics.value.memory.heap.max / (1024 * 1024),
        metrics.value.memory.physical.total / (1024 * 1024),
        metrics.value.memory.physical.free / (1024 * 1024)
      ],
      backgroundColor: ['#10B981', '#34D399', '#6EE7B7', '#A7F3D0'],
      borderWidth: 0
    }]
  }
})

// Storage Chart Data
const storageChartData = computed(() => {
  if (!metrics.value) return null

  return {
    labels: ['Used', 'Free'],
    datasets: [{
      data: [metrics.value.storage.usedGB, metrics.value.storage.freeGB],
      backgroundColor: ['#A855F7', '#E5E7EB'],
      borderWidth: 0
    }]
  }
})

// Chart options
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom'
    }
  }
}

const memoryChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    y: {
      beginAtZero: true
    }
  }
}

// Health status computed properties
const healthStatusClass = computed(() => {
  if (!health.value) return 'bg-gray-100'

  switch (health.value.status) {
    case 'UP':
      return 'bg-green-100 text-green-800'
    case 'DOWN':
      return 'bg-red-100 text-red-800'
    case 'OUT_OF_SERVICE':
      return 'bg-orange-100 text-orange-800'
    default:
      return 'bg-yellow-100 text-yellow-800'
  }
})

const healthStatusIcon = computed(() => {
  if (!health.value) return 'pi-question-circle'

  switch (health.value.status) {
    case 'UP':
      return 'pi-check-circle'
    case 'DOWN':
      return 'pi-times-circle'
    case 'OUT_OF_SERVICE':
      return 'pi-exclamation-circle'
    default:
      return 'pi-info-circle'
  }
})

// Helper functions
const formatPercentage = (value) => {
  return `${value.toFixed(2)}%`
}

const formatBytes = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

const formatGB = (gb) => {
  return gb.toFixed(2)
}

const formatUptime = (seconds) => {
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)

  if (days > 0) {
    return `${days}d ${hours}h`
  } else if (hours > 0) {
    return `${hours}h ${minutes}m`
  } else {
    return `${minutes}m`
  }
}

const formatUptimeDetailed = (seconds) => {
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)

  return `${days} days, ${hours} hours, ${minutes} minutes, ${secs} seconds`
}

const formatTimestamp = (timestamp) => {
  return new Date(timestamp).toLocaleString()
}

const formatComponentName = (name) => {
  return name.replace(/([A-Z])/g, ' $1').trim()
}

const formatComponentDetails = (details) => {
  if (typeof details === 'object') {
    return Object.entries(details)
      .map(([key, value]) => `${key}: ${value}`)
      .join(', ')
  }
  return details
}

const getComponentClass = (status) => {
  switch (status) {
    case 'UP':
      return 'bg-green-50 border-green-200'
    case 'DOWN':
      return 'bg-red-50 border-red-200'
    case 'OUT_OF_SERVICE':
      return 'bg-orange-50 border-orange-200'
    default:
      return 'bg-yellow-50 border-yellow-200'
  }
}

const getComponentIcon = (status) => {
  switch (status) {
    case 'UP':
      return 'pi-check-circle text-green-600'
    case 'DOWN':
      return 'pi-times-circle text-red-600'
    case 'OUT_OF_SERVICE':
      return 'pi-exclamation-circle text-orange-600'
    default:
      return 'pi-info-circle text-yellow-600'
  }
}

const getComponentSeverity = (status) => {
  switch (status) {
    case 'UP':
      return 'success'
    case 'DOWN':
      return 'danger'
    case 'OUT_OF_SERVICE':
      return 'warning'
    default:
      return 'info'
  }
}

// Lifecycle hooks
onMounted(() => {
  fetchAllData()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.system-performance {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.p-card {
  border-radius: 12px;
  overflow: hidden;
}

.p-progressbar {
  height: 8px;
  border-radius: 4px;
}

.hover\:shadow-xl {
  transition: all 0.3s ease;
}

.hover\:shadow-xl:hover {
  transform: translateY(-2px);
}
</style>
