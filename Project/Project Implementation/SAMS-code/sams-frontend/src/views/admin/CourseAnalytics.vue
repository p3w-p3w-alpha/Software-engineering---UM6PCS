<!--
  Course Analytics view - admin dashboard for viewing course performance and attendance
  took a while to get teh charts working but they look pretty good now
  definately helps track how well courses are doing
-->
<template>
  <div class="course-analytics min-h-screen bg-gray-50 p-6">
    <!-- Header section -->
    <div class="mb-6">
      <!-- page title with icon -->
      <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-3">
        <i class="pi pi-chart-line text-blue-600"></i>
        Course Analytics
      </h1>
      <p class="text-gray-600 mt-2">View attendance and performance statistics for courses</p>
    </div>

    <!-- Course Selection - dropdown to filter by specific course -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Select Course</label>
          <select
            v-model="selectedCourseId"
            @change="loadCourseAnalytics"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">Choose a course...</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.courseCode }} - {{ course.courseName }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Date Range</label>
          <select
            v-model="dateRange"
            @change="loadCourseAnalytics"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 90 days</option>
            <option value="all">All time</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">View Student</label>
          <select
            v-model="selectedStudentId"
            @change="loadStudentStats"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            :disabled="!selectedCourseId"
          >
            <option value="">All Students (Course Overview)</option>
            <option v-for="student in enrolledStudents" :key="student.id" :value="student.id">
              {{ student.username }} - {{ student.email }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- No Course Selected -->
    <div v-else-if="!selectedCourseId" class="bg-white rounded-lg shadow p-12 text-center">
      <i class="pi pi-info-circle text-5xl text-gray-400 mb-4"></i>
      <h3 class="text-xl font-semibold text-gray-700 mb-2">Select a Course</h3>
      <p class="text-gray-500">Choose a course from the dropdown above to view analytics</p>
    </div>

    <!-- Analytics Content -->
    <div v-else>
      <!-- Course Info Card -->
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">{{ selectedCourse?.courseName }}</h2>
            <p class="text-gray-600">{{ selectedCourse?.courseCode }} | {{ selectedCourse?.credits }} Credits</p>
          </div>
          <div class="text-right">
            <p class="text-sm text-gray-500">Enrolled Students</p>
            <p class="text-3xl font-bold text-blue-600">{{ selectedCourse?.enrolledCount || 0 }}</p>
          </div>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <!-- Attendance Rate -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="p-3 bg-green-100 rounded-full">
              <i class="pi pi-check-circle text-2xl text-green-600"></i>
            </div>
            <span class="text-3xl font-bold text-gray-900">{{ attendanceStats.rate?.toFixed(1) || 0 }}%</span>
          </div>
          <h3 class="text-sm font-medium text-gray-600">Attendance Rate</h3>
          <p class="text-xs text-gray-500">{{ attendanceStats.present || 0 }} present out of {{ attendanceStats.total || 0 }}</p>
        </div>

        <!-- Average Grade -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="p-3 bg-blue-100 rounded-full">
              <i class="pi pi-star text-2xl text-blue-600"></i>
            </div>
            <span class="text-3xl font-bold text-gray-900">{{ performanceStats.averageGrade?.toFixed(1) || 'N/A' }}</span>
          </div>
          <h3 class="text-sm font-medium text-gray-600">Average Grade</h3>
          <p class="text-xs text-gray-500">Class performance average</p>
        </div>

        <!-- Pass Rate -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="p-3 bg-purple-100 rounded-full">
              <i class="pi pi-percentage text-2xl text-purple-600"></i>
            </div>
            <span class="text-3xl font-bold text-gray-900">{{ performanceStats.passRate?.toFixed(1) || 0 }}%</span>
          </div>
          <h3 class="text-sm font-medium text-gray-600">Pass Rate</h3>
          <p class="text-xs text-gray-500">Students with passing grades</p>
        </div>

        <!-- Late Arrivals -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="p-3 bg-yellow-100 rounded-full">
              <i class="pi pi-clock text-2xl text-yellow-600"></i>
            </div>
            <span class="text-3xl font-bold text-gray-900">{{ attendanceStats.late || 0 }}</span>
          </div>
          <h3 class="text-sm font-medium text-gray-600">Late Arrivals</h3>
          <p class="text-xs text-gray-500">Total late check-ins</p>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <!-- Attendance Chart -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">Attendance Over Time</h3>
          <div class="h-64">
            <canvas ref="attendanceChartRef"></canvas>
          </div>
        </div>

        <!-- Grade Distribution Chart -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">Grade Distribution</h3>
          <div class="h-64">
            <canvas ref="gradeChartRef"></canvas>
          </div>
        </div>
      </div>

      <!-- Student Performance Table (when viewing specific student) -->
      <div v-if="selectedStudentId" class="bg-white rounded-lg shadow p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">
          Student Performance: {{ getStudentName(selectedStudentId) }}
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="p-4 bg-gray-50 rounded-lg">
            <p class="text-sm text-gray-600">Attendance Rate</p>
            <p class="text-2xl font-bold text-green-600">{{ studentStats.attendanceRate?.toFixed(1) || 0 }}%</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-lg">
            <p class="text-sm text-gray-600">Current Grade</p>
            <p class="text-2xl font-bold text-blue-600">{{ studentStats.currentGrade || 'N/A' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-lg">
            <p class="text-sm text-gray-600">Assignments Completed</p>
            <p class="text-2xl font-bold text-purple-600">{{ studentStats.assignmentsCompleted || 0 }}/{{ studentStats.totalAssignments || 0 }}</p>
          </div>
        </div>
      </div>

      <!-- Enrolled Students Table -->
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-800">Enrolled Students Performance</h3>
          <button
            @click="exportAnalytics"
            class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 flex items-center gap-2"
          >
            <i class="pi pi-download"></i>
            Export CSV
          </button>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Student</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Attendance</th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Grade</th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Status</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="student in studentPerformanceList" :key="student.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="font-medium text-gray-900">{{ student.name }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ student.email }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <div class="flex items-center justify-center gap-2">
                    <div class="w-20 bg-gray-200 rounded-full h-2">
                      <div
                        class="h-2 rounded-full"
                        :class="getAttendanceClass(student.attendanceRate)"
                        :style="{ width: student.attendanceRate + '%' }"
                      ></div>
                    </div>
                    <span class="text-sm font-medium">{{ student.attendanceRate?.toFixed(0) }}%</span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <span
                    class="px-2 py-1 text-sm font-semibold rounded-full"
                    :class="getGradeClass(student.grade)"
                  >
                    {{ student.grade || 'N/A' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <span
                    class="px-2 py-1 text-xs font-semibold rounded-full"
                    :class="student.status === 'ENROLLED' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'"
                  >
                    {{ student.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <button
                    @click="viewStudentDetails(student.id)"
                    class="text-blue-600 hover:text-blue-800 text-sm"
                  >
                    View Details
                  </button>
                </td>
              </tr>
              <tr v-if="studentPerformanceList.length === 0">
                <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                  No students enrolled in this course
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import api from '@/services/api'
import Chart from 'chart.js/auto'

// Reactive data
const courses = ref([])
const selectedCourseId = ref('')
const selectedCourse = ref(null)
const selectedStudentId = ref('')
const enrolledStudents = ref([])
const dateRange = ref('30')
const loading = ref(false)

// Stats
const attendanceStats = ref({
  rate: 0,
  present: 0,
  late: 0,
  absent: 0,
  total: 0
})

const performanceStats = ref({
  averageGrade: 0,
  passRate: 0,
  highestGrade: 0,
  lowestGrade: 0
})

const studentStats = ref({
  attendanceRate: 0,
  currentGrade: null,
  assignmentsCompleted: 0,
  totalAssignments: 0
})

const studentPerformanceList = ref([])

// Chart refs
const attendanceChartRef = ref(null)
const gradeChartRef = ref(null)
let attendanceChart = null
let gradeChart = null

// Load courses on mount
onMounted(async () => {
  await loadCourses()
})

async function loadCourses() {
  try {
    const response = await api.getAllCourses()
    courses.value = response.data
  } catch (error) {
    console.error('Error loading courses:', error)
  }
}

async function loadCourseAnalytics() {
  if (!selectedCourseId.value) return

  loading.value = true
  selectedStudentId.value = ''

  try {
    // Load course details
    const courseResponse = await api.getCourseById(selectedCourseId.value)
    selectedCourse.value = courseResponse.data

    // Load enrolled students
    const enrollmentsResponse = await api.get(`/enrollments/course/${selectedCourseId.value}`)
    enrolledStudents.value = enrollmentsResponse.data
      .filter(e => e.status === 'ENROLLED' || e.status === 'ACTIVE')
      .map(e => ({
        id: e.student?.id,
        username: e.student?.username,
        email: e.student?.email
      }))

    // Load attendance statistics
    await loadAttendanceStats()

    // Load performance statistics
    await loadPerformanceStats()

    // Load student performance list
    await loadStudentPerformanceList()

    // Render charts after data is loaded
    await nextTick()
    renderCharts()

  } catch (error) {
    console.error('Error loading course analytics:', error)
  } finally {
    loading.value = false
  }
}

async function loadAttendanceStats() {
  try {
    const endDate = new Date()
    const startDate = new Date()

    if (dateRange.value !== 'all') {
      startDate.setDate(startDate.getDate() - parseInt(dateRange.value))
    } else {
      startDate.setFullYear(startDate.getFullYear() - 1)
    }

    // Try to get attendance for the course
    const response = await api.get('/attendance', {
      params: {
        courseId: selectedCourseId.value,
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0]
      }
    }).catch(() => ({ data: [] }))

    const records = response.data || []
    const present = records.filter(r => r.status === 'PRESENT').length
    const late = records.filter(r => r.status === 'LATE').length
    const absent = records.filter(r => r.status === 'ABSENT').length
    const total = records.length

    attendanceStats.value = {
      rate: total > 0 ? ((present + late) / total) * 100 : 100,
      present,
      late,
      absent,
      total
    }
  } catch (error) {
    console.error('Error loading attendance stats:', error)
    attendanceStats.value = { rate: 0, present: 0, late: 0, absent: 0, total: 0 }
  }
}

async function loadPerformanceStats() {
  try {
    const response = await api.get('/grades', {
      params: { courseId: selectedCourseId.value }
    }).catch(() => ({ data: [] }))

    const grades = response.data || []

    if (grades.length > 0) {
      const gradeValues = grades.map(g => g.gradePoints || 0)
      const avg = gradeValues.reduce((a, b) => a + b, 0) / gradeValues.length
      const passing = grades.filter(g => (g.gradePoints || 0) >= 2.0).length

      performanceStats.value = {
        averageGrade: avg,
        passRate: (passing / grades.length) * 100,
        highestGrade: Math.max(...gradeValues),
        lowestGrade: Math.min(...gradeValues)
      }
    } else {
      performanceStats.value = { averageGrade: 0, passRate: 0, highestGrade: 0, lowestGrade: 0 }
    }
  } catch (error) {
    console.error('Error loading performance stats:', error)
  }
}

async function loadStudentPerformanceList() {
  try {
    const students = []

    for (const enrollment of enrolledStudents.value) {
      // Get student attendance
      let attendanceRate = 100
      try {
        const attResponse = await api.get(`/attendance/user/${enrollment.id}`).catch(() => ({ data: [] }))
        const records = attResponse.data || []
        if (records.length > 0) {
          const present = records.filter(r => r.status === 'PRESENT' || r.status === 'LATE').length
          attendanceRate = (present / records.length) * 100
        }
      } catch (e) {
        // Default to 100% if no records
      }

      // Get student grade
      let grade = null
      try {
        const gradeResponse = await api.get('/grades', {
          params: { studentId: enrollment.id, courseId: selectedCourseId.value }
        }).catch(() => ({ data: [] }))
        if (gradeResponse.data && gradeResponse.data.length > 0) {
          grade = gradeResponse.data[0].gradeValue || gradeResponse.data[0].gradePoints
        }
      } catch (e) {
        // No grade
      }

      students.push({
        id: enrollment.id,
        name: enrollment.username,
        email: enrollment.email,
        attendanceRate,
        grade,
        status: 'ENROLLED'
      })
    }

    studentPerformanceList.value = students
  } catch (error) {
    console.error('Error loading student performance list:', error)
  }
}

async function loadStudentStats() {
  if (!selectedStudentId.value) {
    studentStats.value = { attendanceRate: 0, currentGrade: null, assignmentsCompleted: 0, totalAssignments: 0 }
    return
  }

  try {
    // Get attendance rate
    const attResponse = await api.get(`/attendance/user/${selectedStudentId.value}`).catch(() => ({ data: [] }))
    const records = attResponse.data || []
    let attendanceRate = 100
    if (records.length > 0) {
      const present = records.filter(r => r.status === 'PRESENT' || r.status === 'LATE').length
      attendanceRate = (present / records.length) * 100
    }

    // Get grade
    let currentGrade = null
    const gradeResponse = await api.get('/grades', {
      params: { studentId: selectedStudentId.value, courseId: selectedCourseId.value }
    }).catch(() => ({ data: [] }))
    if (gradeResponse.data && gradeResponse.data.length > 0) {
      currentGrade = gradeResponse.data[0].gradeValue
    }

    studentStats.value = {
      attendanceRate,
      currentGrade,
      assignmentsCompleted: 0,
      totalAssignments: 0
    }
  } catch (error) {
    console.error('Error loading student stats:', error)
  }
}

function renderCharts() {
  // Destroy existing charts
  if (attendanceChart) attendanceChart.destroy()
  if (gradeChart) gradeChart.destroy()

  // Attendance Chart
  if (attendanceChartRef.value) {
    const ctx = attendanceChartRef.value.getContext('2d')
    attendanceChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Present', 'Late', 'Absent'],
        datasets: [{
          data: [
            attendanceStats.value.present,
            attendanceStats.value.late,
            attendanceStats.value.absent
          ],
          backgroundColor: ['#10B981', '#F59E0B', '#EF4444'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom'
          }
        }
      }
    })
  }

  // Grade Distribution Chart
  if (gradeChartRef.value) {
    const ctx = gradeChartRef.value.getContext('2d')

    // Calculate grade distribution
    const gradeDistribution = { A: 0, B: 0, C: 0, D: 0, F: 0 }
    studentPerformanceList.value.forEach(student => {
      if (student.grade) {
        const g = String(student.grade).toUpperCase()
        if (g.startsWith('A')) gradeDistribution.A++
        else if (g.startsWith('B')) gradeDistribution.B++
        else if (g.startsWith('C')) gradeDistribution.C++
        else if (g.startsWith('D')) gradeDistribution.D++
        else if (g.startsWith('F')) gradeDistribution.F++
      }
    })

    gradeChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['A', 'B', 'C', 'D', 'F'],
        datasets: [{
          label: 'Students',
          data: Object.values(gradeDistribution),
          backgroundColor: ['#10B981', '#3B82F6', '#F59E0B', '#F97316', '#EF4444'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    })
  }
}

function getStudentName(studentId) {
  const student = enrolledStudents.value.find(s => s.id === studentId)
  return student?.username || 'Unknown'
}

function getAttendanceClass(rate) {
  if (rate >= 90) return 'bg-green-500'
  if (rate >= 75) return 'bg-yellow-500'
  return 'bg-red-500'
}

function getGradeClass(grade) {
  if (!grade) return 'bg-gray-100 text-gray-800'
  const g = String(grade).toUpperCase()
  if (g.startsWith('A')) return 'bg-green-100 text-green-800'
  if (g.startsWith('B')) return 'bg-blue-100 text-blue-800'
  if (g.startsWith('C')) return 'bg-yellow-100 text-yellow-800'
  if (g.startsWith('D')) return 'bg-orange-100 text-orange-800'
  return 'bg-red-100 text-red-800'
}

function viewStudentDetails(studentId) {
  selectedStudentId.value = studentId
  loadStudentStats()
}

function exportAnalytics() {
  const headers = ['Student', 'Email', 'Attendance Rate', 'Grade', 'Status']
  const rows = studentPerformanceList.value.map(s => [
    s.name,
    s.email,
    `${s.attendanceRate?.toFixed(1)}%`,
    s.grade || 'N/A',
    s.status
  ])

  const csv = [headers.join(','), ...rows.map(r => r.join(','))].join('\n')
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `course_analytics_${selectedCourse.value?.courseCode}_${Date.now()}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.course-analytics {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
