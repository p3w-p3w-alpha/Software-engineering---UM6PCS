<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">My Attendance</h1>
      <p class="text-gray-600 mt-2">Track your class attendance, statistics, and trends</p>
    </div>

    <!-- Error Alert -->
    <div
      v-if="errorMessage"
      class="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3"
    >
      <svg class="w-6 h-6 text-red-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <div class="flex-1">
        <p class="text-sm font-medium text-red-800">{{ errorMessage }}</p>
      </div>
      <button @click="errorMessage = ''" class="text-red-600 hover:text-red-800">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-5 gap-4 mb-8">
      <!-- Overall Attendance -->
      <div class="md:col-span-2 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl shadow-lg p-6 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium opacity-90">Overall Attendance</p>
            <p class="text-4xl font-bold mt-2">{{ stats.percentage.toFixed(1) }}%</p>
            <p class="text-sm opacity-75 mt-1">
              {{ stats.present }} / {{ stats.totalClasses }} classes attended
            </p>
          </div>
          <!-- Circular Progress -->
          <div class="relative w-24 h-24">
            <svg class="w-24 h-24 transform -rotate-90">
              <circle
                class="text-white/20"
                stroke-width="8"
                stroke="currentColor"
                fill="transparent"
                r="40"
                cx="48"
                cy="48"
              />
              <circle
                class="text-white"
                stroke-width="8"
                stroke="currentColor"
                fill="transparent"
                r="40"
                cx="48"
                cy="48"
                :stroke-dasharray="`${stats.percentage * 2.51} 251`"
              />
            </svg>
            <span class="absolute inset-0 flex items-center justify-center text-xl font-bold">
              {{ stats.percentage.toFixed(0) }}%
            </span>
          </div>
        </div>
        <!-- Status indicator -->
        <div class="mt-4 p-2 rounded-lg" :class="getAttendanceStatusBg(stats.percentage)">
          <p class="text-sm font-medium flex items-center gap-2">
            <svg v-if="stats.percentage >= 75" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            {{ getAttendanceStatus(stats.percentage) }}
          </p>
        </div>
      </div>

      <!-- Present -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Present</p>
            <p class="text-3xl font-bold text-green-600 mt-2">{{ stats.present }}</p>
          </div>
          <div class="p-3 bg-green-100 rounded-lg">
            <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Absent -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Absent</p>
            <p class="text-3xl font-bold text-red-600 mt-2">{{ stats.absent }}</p>
          </div>
          <div class="p-3 bg-red-100 rounded-lg">
            <svg class="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Late -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Late</p>
            <p class="text-3xl font-bold text-amber-600 mt-2">{{ stats.late }}</p>
          </div>
          <div class="p-3 bg-amber-100 rounded-lg">
            <svg class="h-6 w-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
      <!-- Weekly Attendance Trend -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Weekly Attendance Trend</h3>
        <div class="h-64">
          <canvas ref="trendChart"></canvas>
        </div>
      </div>

      <!-- Attendance Distribution -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Attendance Distribution</h3>
        <div class="h-64">
          <canvas ref="distributionChart"></canvas>
        </div>
      </div>
    </div>

    <!-- Course-wise Attendance -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 mb-8">
      <div class="px-6 py-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-900">Attendance by Course</h3>
      </div>
      <div class="p-6">
        <div v-if="courseAttendance.length === 0" class="text-center py-8 text-gray-500">
          No course attendance data available
        </div>
        <div v-else class="space-y-4">
          <div
            v-for="course in courseAttendance"
            :key="course.id"
            class="p-4 bg-gray-50 rounded-lg"
          >
            <div class="flex items-center justify-between mb-2">
              <div>
                <h4 class="font-medium text-gray-900">{{ course.code }} - {{ course.name }}</h4>
                <p class="text-sm text-gray-500">
                  {{ course.present }} / {{ course.total }} classes attended
                </p>
              </div>
              <div class="flex items-center gap-4">
                <span
                  class="px-3 py-1 text-sm font-semibold rounded-full"
                  :class="getPercentageClass(course.percentage)"
                >
                  {{ course.percentage.toFixed(1) }}%
                </span>
                <span
                  v-if="course.percentage < 75"
                  class="text-xs text-red-600 bg-red-100 px-2 py-1 rounded"
                >
                  Below threshold
                </span>
              </div>
            </div>
            <!-- Progress bar -->
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="h-2 rounded-full transition-all duration-500"
                :class="getProgressBarClass(course.percentage)"
                :style="{ width: `${course.percentage}%` }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters and Records -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="px-6 py-4 border-b border-gray-200">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <h3 class="text-lg font-semibold text-gray-900">Attendance Records</h3>
          <div class="flex flex-wrap gap-4">
            <div>
              <input
                v-model="filters.startDate"
                type="date"
                class="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                @change="fetchAttendance"
              />
            </div>
            <div>
              <input
                v-model="filters.endDate"
                type="date"
                class="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                @change="fetchAttendance"
              />
            </div>
            <select
              v-model="filters.status"
              class="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">All Status</option>
              <option value="PRESENT">Present</option>
              <option value="ABSENT">Absent</option>
              <option value="LATE">Late</option>
              <option value="EXCUSED">Excused</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Calendar Heat Map -->
      <div class="px-6 py-4 border-b border-gray-100">
        <h4 class="text-sm font-medium text-gray-700 mb-3">Attendance Calendar</h4>
        <div class="flex flex-wrap gap-1">
          <div
            v-for="day in calendarDays"
            :key="day.date"
            :class="getCalendarDayClass(day)"
            class="w-4 h-4 rounded-sm cursor-pointer transition-all hover:scale-125"
            :title="`${day.date}: ${day.status || 'No class'}`"
          ></div>
        </div>
        <div class="flex items-center gap-4 mt-3 text-xs text-gray-500">
          <span class="flex items-center gap-1">
            <span class="w-3 h-3 bg-green-500 rounded-sm"></span> Present
          </span>
          <span class="flex items-center gap-1">
            <span class="w-3 h-3 bg-red-500 rounded-sm"></span> Absent
          </span>
          <span class="flex items-center gap-1">
            <span class="w-3 h-3 bg-amber-500 rounded-sm"></span> Late
          </span>
          <span class="flex items-center gap-1">
            <span class="w-3 h-3 bg-blue-500 rounded-sm"></span> Excused
          </span>
          <span class="flex items-center gap-1">
            <span class="w-3 h-3 bg-gray-200 rounded-sm"></span> No class
          </span>
        </div>
      </div>

      <!-- Records Table -->
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Day</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Check-in</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Remarks</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="loading">
              <td colspan="6" class="px-6 py-12 text-center">
                <div class="flex justify-center">
                  <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredAttendance.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                <svg class="mx-auto h-12 w-12 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                No attendance records found for the selected period
              </td>
            </tr>
            <tr v-else v-for="record in filteredAttendance" :key="record.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {{ formatDate(record.date) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ getDayOfWeek(record.date) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ record.courseName || record.course?.courseCode || record.courseCode || '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(record.status)" class="px-3 py-1 text-xs font-semibold rounded-full">
                  {{ record.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ record.checkInTime || '-' }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">
                {{ record.remarks || '-' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Warning Alert -->
    <div
      v-if="stats.percentage < 75 && stats.totalClasses > 0"
      class="mt-6 p-4 bg-red-50 border border-red-200 rounded-xl"
    >
      <div class="flex items-start gap-3">
        <svg class="w-6 h-6 text-red-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <div>
          <h4 class="font-semibold text-red-800">Attendance Warning</h4>
          <p class="text-sm text-red-700 mt-1">
            Your attendance is below the required 75% threshold. You need to attend {{ getRequiredClasses() }} more classes
            to reach the minimum attendance requirement. Please improve your attendance to avoid academic penalties.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import Chart from 'chart.js/auto'

const authStore = useAuthStore()
const userId = computed(() => authStore.userId)

// State
const loading = ref(false)
const errorMessage = ref('')
const attendanceRecords = ref([])
const courseAttendance = ref([])

const stats = ref({
  totalClasses: 0,
  present: 0,
  absent: 0,
  late: 0,
  excused: 0,
  percentage: 0
})

const filters = ref({
  startDate: getDefaultStartDate(),
  endDate: getDefaultEndDate(),
  status: ''
})

// Chart refs
const trendChart = ref(null)
const distributionChart = ref(null)
let trendChartInstance = null
let distributionChartInstance = null

// Computed
const filteredAttendance = computed(() => {
  if (!filters.value.status) {
    return attendanceRecords.value
  }
  return attendanceRecords.value.filter(record => record.status === filters.value.status)
})

const calendarDays = computed(() => {
  const days = []
  const start = new Date(filters.value.startDate)
  const end = new Date(filters.value.endDate)

  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const dateStr = d.toISOString().split('T')[0]
    const record = attendanceRecords.value.find(r => r.date === dateStr)
    days.push({
      date: dateStr,
      status: record?.status || null
    })
  }

  return days.slice(-90) // Last 90 days
})

// Functions
function getDefaultStartDate() {
  const date = new Date()
  date.setMonth(date.getMonth() - 3)
  return date.toISOString().split('T')[0]
}

function getDefaultEndDate() {
  return new Date().toISOString().split('T')[0]
}

async function fetchAttendance() {
  loading.value = true
  errorMessage.value = ''
  try {
    // Fetch attendance records
    const response = await api.getAttendanceByUserAndDateRange(
      userId.value,
      filters.value.startDate,
      filters.value.endDate
    )
    attendanceRecords.value = response.data || []

    // Fetch statistics
    const statsResponse = await api.getAttendanceStatisticsByUser(
      userId.value,
      filters.value.startDate,
      filters.value.endDate
    )

    if (statsResponse.data) {
      stats.value = {
        totalClasses: statsResponse.data.totalCount || statsResponse.data.totalRecords || 0,
        present: statsResponse.data.presentCount || 0,
        absent: statsResponse.data.absentCount || 0,
        late: statsResponse.data.lateCount || 0,
        excused: statsResponse.data.excusedCount || 0,
        percentage: statsResponse.data.attendancePercentage || 0
      }
    }

    // Calculate course-wise attendance
    calculateCourseAttendance()

  } catch (error) {
    console.error('Error fetching attendance:', error)
    // Show error state - no mock data
    errorMessage.value = 'Failed to load attendance data. Please try again later.'
    stats.value = {
      totalClasses: 0,
      present: 0,
      absent: 0,
      late: 0,
      excused: 0,
      percentage: 0
    }
    attendanceRecords.value = []
    courseAttendance.value = []
  } finally {
    loading.value = false
    await nextTick()
    initCharts()
  }
}

function calculateCourseAttendance() {
  const courses = {}

  attendanceRecords.value.forEach(record => {
    const code = record.courseName || record.course?.courseCode || record.courseCode || 'Unknown'
    const name = record.courseName || record.course?.courseName || 'Unknown Course'

    if (!courses[code]) {
      courses[code] = {
        id: code,
        code,
        name,
        total: 0,
        present: 0
      }
    }

    courses[code].total++
    if (record.status === 'PRESENT' || record.status === 'LATE') {
      courses[code].present++
    }
  })

  courseAttendance.value = Object.values(courses).map(c => ({
    ...c,
    percentage: c.total > 0 ? (c.present / c.total) * 100 : 0
  }))
}

function initCharts() {
  // Trend Chart
  if (trendChart.value) {
    if (trendChartInstance) trendChartInstance.destroy()

    const weeklyData = getWeeklyData()

    trendChartInstance = new Chart(trendChart.value, {
      type: 'line',
      data: {
        labels: weeklyData.labels,
        datasets: [{
          label: 'Attendance %',
          data: weeklyData.data,
          borderColor: 'rgb(99, 102, 241)',
          backgroundColor: 'rgba(99, 102, 241, 0.1)',
          tension: 0.3,
          fill: true,
          pointBackgroundColor: 'rgb(99, 102, 241)',
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
          pointRadius: 5
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: {
            min: 0,
            max: 100,
            ticks: {
              callback: value => value + '%'
            }
          }
        }
      }
    })
  }

  // Distribution Chart
  if (distributionChart.value) {
    if (distributionChartInstance) distributionChartInstance.destroy()

    distributionChartInstance = new Chart(distributionChart.value, {
      type: 'doughnut',
      data: {
        labels: ['Present', 'Absent', 'Late', 'Excused'],
        datasets: [{
          data: [stats.value.present, stats.value.absent, stats.value.late, stats.value.excused],
          backgroundColor: ['#10B981', '#EF4444', '#F59E0B', '#3B82F6'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: { usePointStyle: true }
          }
        }
      }
    })
  }
}

function getWeeklyData() {
  const weeks = {}
  const now = new Date()

  // Initialize last 8 weeks
  for (let i = 7; i >= 0; i--) {
    const weekStart = new Date(now)
    weekStart.setDate(weekStart.getDate() - weekStart.getDay() - (i * 7))
    const weekKey = weekStart.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
    weeks[weekKey] = { total: 0, present: 0 }
  }

  attendanceRecords.value.forEach(record => {
    const recordDate = new Date(record.date)
    const weekStart = new Date(recordDate)
    weekStart.setDate(weekStart.getDate() - weekStart.getDay())
    const weekKey = weekStart.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })

    if (weeks[weekKey]) {
      weeks[weekKey].total++
      if (record.status === 'PRESENT' || record.status === 'LATE') {
        weeks[weekKey].present++
      }
    }
  })

  return {
    labels: Object.keys(weeks),
    data: Object.values(weeks).map(w => w.total > 0 ? (w.present / w.total) * 100 : 0)
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function getDayOfWeek(dateString) {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('en-US', { weekday: 'short' })
}

function getStatusBadgeClass(status) {
  const classes = {
    'PRESENT': 'bg-green-100 text-green-800',
    'ABSENT': 'bg-red-100 text-red-800',
    'LATE': 'bg-amber-100 text-amber-800',
    'EXCUSED': 'bg-blue-100 text-blue-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getPercentageClass(percentage) {
  if (percentage >= 90) return 'bg-green-100 text-green-700'
  if (percentage >= 75) return 'bg-blue-100 text-blue-700'
  if (percentage >= 60) return 'bg-amber-100 text-amber-700'
  return 'bg-red-100 text-red-700'
}

function getProgressBarClass(percentage) {
  if (percentage >= 90) return 'bg-green-500'
  if (percentage >= 75) return 'bg-blue-500'
  if (percentage >= 60) return 'bg-amber-500'
  return 'bg-red-500'
}

function getAttendanceStatus(percentage) {
  if (percentage >= 90) return 'Excellent attendance! Keep it up.'
  if (percentage >= 75) return 'Good attendance. You\'re on track.'
  if (percentage >= 60) return 'Warning: Attendance below threshold.'
  return 'Critical: Attendance too low!'
}

function getAttendanceStatusBg(percentage) {
  if (percentage >= 90) return 'bg-white/20'
  if (percentage >= 75) return 'bg-green-500/30'
  if (percentage >= 60) return 'bg-amber-500/30'
  return 'bg-red-500/30'
}

function getCalendarDayClass(day) {
  if (!day.status) return 'bg-gray-200'
  const classes = {
    'PRESENT': 'bg-green-500',
    'ABSENT': 'bg-red-500',
    'LATE': 'bg-amber-500',
    'EXCUSED': 'bg-blue-500'
  }
  return classes[day.status] || 'bg-gray-200'
}

function getRequiredClasses() {
  const currentPresent = stats.value.present + stats.value.late
  const currentTotal = stats.value.totalClasses
  // Required: (currentPresent + x) / (currentTotal + x) >= 0.75
  // Solve for x: x >= (0.75 * currentTotal - currentPresent) / 0.25
  const required = Math.ceil((0.75 * currentTotal - currentPresent) / 0.25)
  return Math.max(0, required)
}

onMounted(() => {
  fetchAttendance()
})

watch(filters, () => {
  fetchAttendance()
}, { deep: true })
</script>
