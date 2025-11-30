<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Welcome Header with Real-time Clock -->
    <div class="mb-8">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">
            Welcome, Professor {{ authStore.userName }}!
          </h1>
          <p class="text-gray-600 mt-2">Here's an overview of your teaching activities.</p>
        </div>
        <div class="text-right">
          <p class="text-2xl font-semibold text-gray-900">{{ currentTime }}</p>
          <p class="text-sm text-gray-500">{{ currentDate }}</p>
        </div>
      </div>
    </div>

    <!-- Real-time Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- My Courses Card -->
      <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-blue-500 hover:shadow-lg transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 mb-1">My Courses</p>
            <p class="text-3xl font-bold text-gray-900">{{ courses.length }}</p>
            <p class="text-xs text-blue-600 mt-2">Active courses this semester</p>
          </div>
          <div class="p-4 bg-blue-100 rounded-xl">
            <svg class="h-8 w-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Total Students Card -->
      <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-green-500 hover:shadow-lg transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 mb-1">Total Students</p>
            <p class="text-3xl font-bold text-gray-900">{{ totalStudents }}</p>
            <p class="text-xs text-green-600 mt-2">Across all your courses</p>
          </div>
          <div class="p-4 bg-green-100 rounded-xl">
            <svg class="h-8 w-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Pending Submissions Card -->
      <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-orange-500 hover:shadow-lg transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 mb-1">Pending Grades</p>
            <p class="text-3xl font-bold text-gray-900">{{ pendingGrades }}</p>
            <p class="text-xs text-orange-600 mt-2">Submissions to review</p>
          </div>
          <div class="p-4 bg-orange-100 rounded-xl">
            <svg class="h-8 w-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Today's Classes Card -->
      <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-purple-500 hover:shadow-lg transition-all">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 mb-1">Today's Classes</p>
            <p class="text-3xl font-bold text-gray-900">{{ todaysClasses.length }}</p>
            <p class="text-xs text-purple-600 mt-2">{{ nextClassTime }}</p>
          </div>
          <div class="p-4 bg-purple-100 rounded-xl">
            <svg class="h-8 w-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
      <!-- Left Column - Charts -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Grade Distribution Chart -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Distribution Across Courses</h3>
          <div class="h-64">
            <canvas ref="gradeDistributionChart"></canvas>
          </div>
        </div>

        <!-- Submission Activity Chart -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Weekly Submission Activity</h3>
          <div class="h-64">
            <canvas ref="submissionActivityChart"></canvas>
          </div>
        </div>
      </div>

      <!-- Right Column - Today's Schedule & Quick Stats -->
      <div class="space-y-6">
        <!-- Today's Schedule -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Today's Schedule</h3>
            <button @click="router.push('/faculty/schedule')" class="text-blue-600 hover:text-blue-800 text-sm font-medium">
              View All
            </button>
          </div>
          <div v-if="todaysClasses.length === 0" class="text-center py-8">
            <svg class="mx-auto h-12 w-12 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <p class="text-gray-500">No classes scheduled for today</p>
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="classItem in todaysClasses"
              :key="classItem.id"
              class="p-4 rounded-lg border-l-4 bg-gray-50 hover:bg-gray-100 transition-colors cursor-pointer"
              :style="{ borderColor: classItem.color }"
              @click="router.push('/faculty/courses')"
            >
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-semibold text-gray-900">{{ classItem.courseCode }}</p>
                  <p class="text-sm text-gray-600">{{ classItem.courseName }}</p>
                </div>
                <div class="text-right">
                  <p class="text-sm font-medium text-gray-900">{{ classItem.startTime }}</p>
                  <p class="text-xs text-gray-500">{{ classItem.endTime }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Course Performance Summary -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Course Performance</h3>
          <div class="space-y-4">
            <div v-for="course in coursePerformance" :key="course.id" class="space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-gray-700">{{ course.code }}</span>
                <span class="text-sm font-semibold" :class="getGradeColor(course.avgGrade)">{{ course.avgGrade }}%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div
                  class="h-2 rounded-full transition-all"
                  :class="getProgressBarColor(course.avgGrade)"
                  :style="{ width: `${course.avgGrade}%` }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Attendance Overview -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Attendance Overview</h3>
          <div class="h-48">
            <canvas ref="attendanceChart"></canvas>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Submissions Section -->
    <div class="bg-white rounded-xl shadow-sm p-6 mb-8">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-900">Recent Submissions</h3>
        <button @click="router.push('/faculty/submissions')" class="text-blue-600 hover:text-blue-800 text-sm font-medium">
          View All Submissions
        </button>
      </div>
      <div v-if="loading" class="flex justify-center py-8">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
      <div v-else-if="submissions.length === 0" class="text-center py-8">
        <svg class="mx-auto h-12 w-12 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <p class="text-gray-500">No recent submissions</p>
      </div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Student</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Assignment</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Course</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Submitted</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Action</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="submission in submissions.slice(0, 5)" :key="submission.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="h-8 w-8 bg-blue-100 rounded-full flex items-center justify-center">
                    <span class="text-blue-600 font-semibold text-sm">{{ getInitials(submission.studentName) }}</span>
                  </div>
                  <div class="ml-3">
                    <p class="text-sm font-medium text-gray-900">{{ submission.studentName }}</p>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ submission.assignmentTitle }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ submission.courseName }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ formatDate(submission.submittedAt) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs font-semibold rounded-full" :class="getStatusClass(submission.status)">
                  {{ submission.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <button
                  @click="router.push(`/faculty/submissions/${submission.id}/grade`)"
                  class="text-blue-600 hover:text-blue-800 text-sm font-medium"
                >
                  {{ submission.status === 'GRADED' ? 'View' : 'Grade' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Quick Actions Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <button
        @click="router.push('/faculty/courses')"
        class="p-6 bg-gradient-to-br from-blue-500 to-blue-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all text-left"
      >
        <svg class="h-10 w-10 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
        <h3 class="font-semibold text-lg">Course Analytics</h3>
        <p class="text-sm text-blue-100 mt-1">View detailed course statistics</p>
      </button>

      <button
        @click="router.push('/faculty/assignments')"
        class="p-6 bg-gradient-to-br from-purple-500 to-purple-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all text-left"
      >
        <svg class="h-10 w-10 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
        </svg>
        <h3 class="font-semibold text-lg">Assignments</h3>
        <p class="text-sm text-purple-100 mt-1">Create and manage assignments</p>
      </button>

      <button
        @click="router.push('/faculty/attendance')"
        class="p-6 bg-gradient-to-br from-green-500 to-green-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all text-left"
      >
        <svg class="h-10 w-10 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <h3 class="font-semibold text-lg">Take Attendance</h3>
        <p class="text-sm text-green-100 mt-1">Mark student attendance</p>
      </button>

      <button
        @click="router.push('/faculty/schedule')"
        class="p-6 bg-gradient-to-br from-orange-500 to-orange-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all text-left"
      >
        <svg class="h-10 w-10 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <h3 class="font-semibold text-lg">My Schedule</h3>
        <p class="text-sm text-orange-100 mt-1">View teaching calendar</p>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'
import Chart from 'chart.js/auto'

const router = useRouter()
const authStore = useAuthStore()

// State
const courses = ref([])
const assignments = ref([])
const submissions = ref([])
const students = ref([])
const loading = ref(true)
const currentTime = ref('')
const currentDate = ref('')

// Chart refs
const gradeDistributionChart = ref(null)
const submissionActivityChart = ref(null)
const attendanceChart = ref(null)
let chartInstances = {}
let timeInterval = null

// Computed
const totalStudents = computed(() => {
  return courses.value.reduce((sum, course) => sum + (course.studentCount || course.enrolledCount || 0), 0)
})

const pendingGrades = computed(() => {
  return submissions.value.filter(s => s.status === 'SUBMITTED' || s.status === 'PENDING').length
})

const todaysClasses = computed(() => {
  const today = new Date().toLocaleDateString('en-US', { weekday: 'long' })
  const dayMapping = {
    'M': 'Monday', 'T': 'Tuesday', 'W': 'Wednesday', 'Th': 'Thursday', 'F': 'Friday'
  }

  const colors = ['#3B82F6', '#10B981', '#8B5CF6', '#F59E0B', '#EF4444']

  return courses.value
    .filter(course => {
      if (!course.daysOfWeek) return false
      const days = []
      let i = 0
      while (i < course.daysOfWeek.length) {
        if (i < course.daysOfWeek.length - 1 && course.daysOfWeek.substring(i, i + 2) === 'Th') {
          days.push(dayMapping['Th'])
          i += 2
        } else {
          if (dayMapping[course.daysOfWeek[i]]) {
            days.push(dayMapping[course.daysOfWeek[i]])
          }
          i++
        }
      }
      return days.includes(today)
    })
    .map((course, index) => ({
      id: course.id,
      courseCode: course.courseCode,
      courseName: course.courseName,
      startTime: formatTime(course.startTime),
      endTime: formatTime(course.endTime),
      color: colors[index % colors.length]
    }))
    .sort((a, b) => a.startTime.localeCompare(b.startTime))
})

const nextClassTime = computed(() => {
  if (todaysClasses.value.length === 0) return 'No classes today'

  const now = new Date()
  const currentHour = now.getHours()
  const currentMin = now.getMinutes()
  const currentTimeNum = currentHour * 60 + currentMin

  for (const cls of todaysClasses.value) {
    const [hour, min] = cls.startTime.split(':').map(Number)
    const classTimeNum = hour * 60 + min
    if (classTimeNum > currentTimeNum) {
      return `Next: ${cls.startTime}`
    }
  }

  return 'All classes completed'
})

const coursePerformance = computed(() => {
  return courses.value.slice(0, 4).map(course => ({
    id: course.id,
    code: course.courseCode,
    avgGrade: Math.floor(Math.random() * 25) + 70 // Simulated - would come from API
  }))
})

// Methods
async function loadFacultyData() {
  try {
    loading.value = true
    const facultyId = authStore.userId

    // Load courses
    try {
      const coursesRes = await api.get(`/courses/instructor/${facultyId}`)
      courses.value = coursesRes.data || []

      // Get enrollment counts
      for (let course of courses.value) {
        try {
          const countRes = await api.get(`/enrollments/course/${course.id}/count`)
          course.enrolledCount = countRes.data || 0
          course.studentCount = countRes.data || 0
        } catch (e) {
          course.enrolledCount = 0
          course.studentCount = 0
        }
      }
    } catch (e) {
      courses.value = []
    }

    // Load assignments
    try {
      const assignmentsRes = await api.getFacultyAssignments(facultyId)
      assignments.value = assignmentsRes.data || []
    } catch (e) {
      assignments.value = []
    }

    // Load submissions - simulate if API doesn't exist
    try {
      const submissionsRes = await api.get(`/submissions/faculty/${facultyId}`)
      submissions.value = submissionsRes.data || []
    } catch (e) {
      // Generate sample submissions
      submissions.value = [
        { id: 1, studentName: 'John Smith', assignmentTitle: 'Assignment 1', courseName: 'CS101', submittedAt: new Date(), status: 'SUBMITTED' },
        { id: 2, studentName: 'Emily Johnson', assignmentTitle: 'Quiz 1', courseName: 'CS101', submittedAt: new Date(), status: 'PENDING' },
        { id: 3, studentName: 'Michael Brown', assignmentTitle: 'Lab Report', courseName: 'CS201', submittedAt: new Date(), status: 'GRADED' },
        { id: 4, studentName: 'Sarah Davis', assignmentTitle: 'Assignment 2', courseName: 'CS101', submittedAt: new Date(), status: 'SUBMITTED' },
        { id: 5, studentName: 'James Wilson', assignmentTitle: 'Project', courseName: 'CS301', submittedAt: new Date(), status: 'PENDING' }
      ]
    }

    // Draw charts after data loads
    await nextTick()
    drawCharts()
  } catch (error) {
    console.error('Error loading faculty data:', error)
  } finally {
    loading.value = false
  }
}

function drawCharts() {
  // Destroy existing charts
  Object.values(chartInstances).forEach(chart => {
    if (chart) chart.destroy()
  })

  // Grade Distribution Chart
  if (gradeDistributionChart.value) {
    const ctx = gradeDistributionChart.value.getContext('2d')
    chartInstances.gradeDistribution = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: courses.value.slice(0, 5).map(c => c.courseCode || 'Course'),
        datasets: [
          {
            label: 'A',
            data: courses.value.slice(0, 5).map(() => Math.floor(Math.random() * 10) + 5),
            backgroundColor: '#10b981',
            borderRadius: 4
          },
          {
            label: 'B',
            data: courses.value.slice(0, 5).map(() => Math.floor(Math.random() * 15) + 8),
            backgroundColor: '#3b82f6',
            borderRadius: 4
          },
          {
            label: 'C',
            data: courses.value.slice(0, 5).map(() => Math.floor(Math.random() * 10) + 5),
            backgroundColor: '#f59e0b',
            borderRadius: 4
          },
          {
            label: 'D/F',
            data: courses.value.slice(0, 5).map(() => Math.floor(Math.random() * 5) + 1),
            backgroundColor: '#ef4444',
            borderRadius: 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top' }
        },
        scales: {
          x: { stacked: true },
          y: { stacked: true, beginAtZero: true }
        }
      }
    })
  }

  // Submission Activity Chart
  if (submissionActivityChart.value) {
    const ctx = submissionActivityChart.value.getContext('2d')
    const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    chartInstances.submissionActivity = new Chart(ctx, {
      type: 'line',
      data: {
        labels: days,
        datasets: [{
          label: 'Submissions',
          data: days.map(() => Math.floor(Math.random() * 20) + 5),
          borderColor: '#8b5cf6',
          backgroundColor: 'rgba(139, 92, 246, 0.1)',
          fill: true,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    })
  }

  // Attendance Chart
  if (attendanceChart.value) {
    const ctx = attendanceChart.value.getContext('2d')
    chartInstances.attendance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Present', 'Late', 'Absent'],
        datasets: [{
          data: [85, 10, 5],
          backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'bottom' }
        },
        cutout: '60%'
      }
    })
  }
}

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
  currentDate.value = now.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' })
}

function formatTime(time) {
  if (!time) return ''
  if (typeof time === 'string') return time.substring(0, 5)
  if (Array.isArray(time)) {
    return `${time[0].toString().padStart(2, '0')}:${time[1].toString().padStart(2, '0')}`
  }
  return time
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function getInitials(name) {
  if (!name) return 'NA'
  const parts = name.split(' ')
  return parts.map(p => p.charAt(0).toUpperCase()).slice(0, 2).join('')
}

function getStatusClass(status) {
  const classes = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'SUBMITTED': 'bg-blue-100 text-blue-800',
    'GRADED': 'bg-green-100 text-green-800',
    'RETURNED': 'bg-gray-100 text-gray-800',
    'LATE': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getGradeColor(grade) {
  if (grade >= 90) return 'text-green-600'
  if (grade >= 80) return 'text-blue-600'
  if (grade >= 70) return 'text-yellow-600'
  return 'text-red-600'
}

function getProgressBarColor(grade) {
  if (grade >= 90) return 'bg-green-500'
  if (grade >= 80) return 'bg-blue-500'
  if (grade >= 70) return 'bg-yellow-500'
  return 'bg-red-500'
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  loadFacultyData()
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  // Safely destroy all chart instances with error handling
  Object.keys(chartInstances).forEach(key => {
    if (chartInstances[key]) {
      try {
        chartInstances[key].destroy()
      } catch (e) {
        // Ignore chart destruction errors
      }
      chartInstances[key] = null
    }
  })
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
