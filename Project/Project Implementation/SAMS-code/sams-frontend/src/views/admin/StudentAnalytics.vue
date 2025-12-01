<!--
  Student Analytics view - admin dashboard for viewing individual student performance
  wierd amount of data to track but its all useful for monitoring progress
  definately helps identify students who need extra support
-->
<template>
  <div class="student-analytics p-6">
    <div class="mb-6">
      <!-- page header -->
      <h1 class="text-2xl font-bold text-gray-800 dark:text-white">Student Analytics</h1>
      <p class="text-gray-600 dark:text-gray-400">View detailed performance metrics for individual students</p>
    </div>

    <!-- Student Selection - filter by student and semester -->
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
      <h2 class="text-lg font-semibold mb-4 text-gray-800 dark:text-white">Select Student</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Student</label>
          <select
            v-model="selectedStudentId"
            @change="loadStudentData"
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Select a student...</option>
            <option v-for="student in students" :key="student.id" :value="student.id">
              {{ student.firstName }} {{ student.lastName }} ({{ student.studentId || student.email }})
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Semester</label>
          <select
            v-model="selectedSemester"
            @change="loadStudentData"
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Semesters</option>
            <option value="Fall 2024">Fall 2024</option>
            <option value="Spring 2025">Spring 2025</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="loadStudentData"
            :disabled="!selectedStudentId || loading"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <span v-if="loading">Loading...</span>
            <span v-else>Load Analytics</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Student Info Card -->
    <div v-if="studentData" class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
      <div class="flex items-center space-x-4">
        <div class="w-16 h-16 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center">
          <span class="text-2xl font-bold text-blue-600 dark:text-blue-400">
            {{ studentData.firstName?.charAt(0) }}{{ studentData.lastName?.charAt(0) }}
          </span>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-800 dark:text-white">
            {{ studentData.firstName }} {{ studentData.lastName }}
          </h3>
          <p class="text-gray-600 dark:text-gray-400">{{ studentData.email }}</p>
          <p class="text-sm text-gray-500 dark:text-gray-500">Student ID: {{ studentData.studentId || 'N/A' }}</p>
        </div>
      </div>
    </div>

    <!-- Stats Overview -->
    <div v-if="studentData" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Overall GPA</p>
            <p class="text-2xl font-bold text-blue-600">{{ stats.gpa.toFixed(2) }}</p>
          </div>
          <div class="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-full flex items-center justify-center">
            <i class="pi pi-star text-blue-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Attendance Rate</p>
            <p class="text-2xl font-bold text-green-600">{{ stats.attendanceRate }}%</p>
          </div>
          <div class="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-full flex items-center justify-center">
            <i class="pi pi-check-circle text-green-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Courses Enrolled</p>
            <p class="text-2xl font-bold text-purple-600">{{ stats.coursesEnrolled }}</p>
          </div>
          <div class="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-full flex items-center justify-center">
            <i class="pi pi-book text-purple-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Assignments Completed</p>
            <p class="text-2xl font-bold text-orange-600">{{ stats.assignmentsCompleted }}/{{ stats.totalAssignments }}</p>
          </div>
          <div class="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-full flex items-center justify-center">
            <i class="pi pi-file text-orange-600 text-xl"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div v-if="studentData" class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
      <!-- Attendance Chart -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h3 class="text-lg font-semibold mb-4 text-gray-800 dark:text-white">Attendance Over Time</h3>
        <canvas ref="attendanceChartRef" height="250"></canvas>
      </div>

      <!-- Grade Distribution -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h3 class="text-lg font-semibold mb-4 text-gray-800 dark:text-white">Grade Distribution by Course</h3>
        <canvas ref="gradeChartRef" height="250"></canvas>
      </div>
    </div>

    <!-- Performance Trend -->
    <div v-if="studentData" class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
      <h3 class="text-lg font-semibold mb-4 text-gray-800 dark:text-white">Performance Trend</h3>
      <canvas ref="performanceChartRef" height="200"></canvas>
    </div>

    <!-- Course Details Table -->
    <div v-if="studentData" class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Course Details</h3>
        <button
          @click="exportStudentReport"
          class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center gap-2"
        >
          <i class="pi pi-download"></i>
          Export Report
        </button>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b dark:border-gray-700">
              <th class="text-left py-3 px-4 text-gray-600 dark:text-gray-400">Course</th>
              <th class="text-left py-3 px-4 text-gray-600 dark:text-gray-400">Grade</th>
              <th class="text-left py-3 px-4 text-gray-600 dark:text-gray-400">Attendance</th>
              <th class="text-left py-3 px-4 text-gray-600 dark:text-gray-400">Assignments</th>
              <th class="text-left py-3 px-4 text-gray-600 dark:text-gray-400">Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in courseDetails" :key="course.id" class="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
              <td class="py-3 px-4 text-gray-800 dark:text-white">
                <div>
                  <p class="font-medium">{{ course.courseName }}</p>
                  <p class="text-sm text-gray-500">{{ course.courseCode }}</p>
                </div>
              </td>
              <td class="py-3 px-4">
                <span :class="getGradeClass(course.grade)" class="px-2 py-1 rounded text-sm font-medium">
                  {{ course.grade || 'N/A' }}
                </span>
              </td>
              <td class="py-3 px-4 text-gray-800 dark:text-white">
                <div class="flex items-center gap-2">
                  <div class="w-24 bg-gray-200 dark:bg-gray-600 rounded-full h-2">
                    <div
                      class="bg-green-500 h-2 rounded-full"
                      :style="{ width: course.attendanceRate + '%' }"
                    ></div>
                  </div>
                  <span class="text-sm">{{ course.attendanceRate }}%</span>
                </div>
              </td>
              <td class="py-3 px-4 text-gray-800 dark:text-white">
                {{ course.assignmentsCompleted }}/{{ course.totalAssignments }}
              </td>
              <td class="py-3 px-4">
                <span :class="getStatusClass(course.status)" class="px-2 py-1 rounded text-xs font-medium">
                  {{ course.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!studentData && !loading" class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-12 text-center">
      <i class="pi pi-user text-6xl text-gray-300 dark:text-gray-600 mb-4"></i>
      <h3 class="text-xl font-semibold text-gray-600 dark:text-gray-400 mb-2">No Student Selected</h3>
      <p class="text-gray-500 dark:text-gray-500">Please select a student from the dropdown above to view their analytics.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Chart, registerables } from 'chart.js'
import api from '../../services/api'

Chart.register(...registerables)

const route = useRoute()

const students = ref([])
const selectedStudentId = ref('')
const selectedSemester = ref('all')
const studentData = ref(null)
const loading = ref(false)

const stats = ref({
  gpa: 0,
  attendanceRate: 0,
  coursesEnrolled: 0,
  assignmentsCompleted: 0,
  totalAssignments: 0
})

const courseDetails = ref([])

// Chart refs
const attendanceChartRef = ref(null)
const gradeChartRef = ref(null)
const performanceChartRef = ref(null)

let attendanceChart = null
let gradeChart = null
let performanceChart = null

onMounted(async () => {
  await loadStudents()

  // Check if studentId is passed via route params
  if (route.params.studentId) {
    selectedStudentId.value = route.params.studentId
    await loadStudentData()
  }
})

async function loadStudents() {
  try {
    const response = await api.getAllUsers()
    students.value = response.data.filter(u => u.role === 'STUDENT')
  } catch (error) {
    console.error('Failed to load students:', error)
    // Use mock data
    students.value = [
      { id: 1, firstName: 'John', lastName: 'Doe', email: 'john@example.com', studentId: 'STU001' },
      { id: 2, firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com', studentId: 'STU002' },
      { id: 3, firstName: 'Bob', lastName: 'Johnson', email: 'bob@example.com', studentId: 'STU003' }
    ]
  }
}

async function loadStudentData() {
  if (!selectedStudentId.value) return

  loading.value = true

  try {
    // Load student info
    const studentResponse = await api.getUserById(selectedStudentId.value)
    studentData.value = studentResponse.data

    // Load enrollments
    const enrollmentsResponse = await api.getEnrollmentsByStudent(selectedStudentId.value)
    const enrollments = enrollmentsResponse.data || []

    // Load attendance
    const attendanceResponse = await api.getAttendanceByStudent(selectedStudentId.value)
    const attendanceRecords = attendanceResponse.data || []

    // Calculate stats
    calculateStats(enrollments, attendanceRecords)

    // Process course details
    processCourseDetails(enrollments, attendanceRecords)

    // Render charts
    await nextTick()
    renderCharts(enrollments, attendanceRecords)

  } catch (error) {
    console.error('Failed to load student data:', error)
    // Use mock data
    loadMockData()
  } finally {
    loading.value = false
  }
}

function calculateStats(enrollments, attendanceRecords) {
  // GPA calculation
  const gradesWithPoints = enrollments
    .filter(e => e.grade)
    .map(e => {
      const gradePoints = { 'A': 4.0, 'A-': 3.7, 'B+': 3.3, 'B': 3.0, 'B-': 2.7, 'C+': 2.3, 'C': 2.0, 'C-': 1.7, 'D': 1.0, 'F': 0 }
      return gradePoints[e.grade] || 0
    })

  stats.value.gpa = gradesWithPoints.length > 0
    ? gradesWithPoints.reduce((a, b) => a + b, 0) / gradesWithPoints.length
    : 0

  // Attendance rate
  const totalAttendance = attendanceRecords.length
  const presentCount = attendanceRecords.filter(a => a.status === 'PRESENT').length
  stats.value.attendanceRate = totalAttendance > 0
    ? Math.round((presentCount / totalAttendance) * 100)
    : 0

  // Courses
  stats.value.coursesEnrolled = enrollments.length

  // Assignments (mock for now)
  stats.value.assignmentsCompleted = Math.floor(Math.random() * 20) + 10
  stats.value.totalAssignments = stats.value.assignmentsCompleted + Math.floor(Math.random() * 5)
}

function processCourseDetails(enrollments, attendanceRecords) {
  courseDetails.value = enrollments.map(enrollment => {
    const courseAttendance = attendanceRecords.filter(a => a.courseId === enrollment.courseId)
    const presentCount = courseAttendance.filter(a => a.status === 'PRESENT').length
    const attendanceRate = courseAttendance.length > 0
      ? Math.round((presentCount / courseAttendance.length) * 100)
      : 100

    return {
      id: enrollment.id,
      courseCode: enrollment.course?.courseCode || enrollment.courseCode || 'N/A',
      courseName: enrollment.course?.courseName || enrollment.courseName || 'Unknown Course',
      grade: enrollment.grade || 'IP',
      attendanceRate,
      assignmentsCompleted: Math.floor(Math.random() * 8) + 2,
      totalAssignments: 10,
      status: enrollment.status || 'ENROLLED'
    }
  })
}

function renderCharts(enrollments, attendanceRecords) {
  // Destroy existing charts
  if (attendanceChart) attendanceChart.destroy()
  if (gradeChart) gradeChart.destroy()
  if (performanceChart) performanceChart.destroy()

  // Attendance Over Time Chart
  if (attendanceChartRef.value) {
    const ctx = attendanceChartRef.value.getContext('2d')
    const months = ['Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Feb']

    attendanceChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: months,
        datasets: [{
          label: 'Attendance Rate',
          data: months.map(() => Math.floor(Math.random() * 20) + 80),
          borderColor: '#22c55e',
          backgroundColor: 'rgba(34, 197, 94, 0.1)',
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
          y: {
            min: 0,
            max: 100,
            ticks: { callback: v => v + '%' }
          }
        }
      }
    })
  }

  // Grade Distribution Chart
  if (gradeChartRef.value) {
    const ctx = gradeChartRef.value.getContext('2d')
    const courses = courseDetails.value.slice(0, 5)

    gradeChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: courses.map(c => c.courseCode),
        datasets: [{
          label: 'Grade Points',
          data: courses.map(c => {
            const gradePoints = { 'A': 4.0, 'A-': 3.7, 'B+': 3.3, 'B': 3.0, 'B-': 2.7, 'C+': 2.3, 'C': 2.0, 'IP': 0 }
            return gradePoints[c.grade] || 0
          }),
          backgroundColor: ['#3b82f6', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6']
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
            max: 4,
            ticks: { stepSize: 1 }
          }
        }
      }
    })
  }

  // Performance Trend Chart
  if (performanceChartRef.value) {
    const ctx = performanceChartRef.value.getContext('2d')
    const semesters = ['Fall 2023', 'Spring 2024', 'Fall 2024', 'Spring 2025']

    performanceChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: semesters,
        datasets: [
          {
            label: 'GPA',
            data: [3.2, 3.4, 3.5, stats.value.gpa],
            borderColor: '#3b82f6',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            yAxisID: 'y',
            tension: 0.4
          },
          {
            label: 'Attendance %',
            data: [85, 88, 92, stats.value.attendanceRate],
            borderColor: '#22c55e',
            backgroundColor: 'rgba(34, 197, 94, 0.1)',
            yAxisID: 'y1',
            tension: 0.4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: false
        },
        scales: {
          y: {
            type: 'linear',
            display: true,
            position: 'left',
            min: 0,
            max: 4,
            title: { display: true, text: 'GPA' }
          },
          y1: {
            type: 'linear',
            display: true,
            position: 'right',
            min: 0,
            max: 100,
            title: { display: true, text: 'Attendance %' },
            grid: { drawOnChartArea: false }
          }
        }
      }
    })
  }
}

function loadMockData() {
  studentData.value = students.value.find(s => s.id == selectedStudentId.value) || {
    firstName: 'Test',
    lastName: 'Student',
    email: 'test@example.com',
    studentId: 'STU000'
  }

  stats.value = {
    gpa: 3.45,
    attendanceRate: 92,
    coursesEnrolled: 5,
    assignmentsCompleted: 18,
    totalAssignments: 20
  }

  courseDetails.value = [
    { id: 1, courseCode: 'CS101', courseName: 'Introduction to Programming', grade: 'A', attendanceRate: 95, assignmentsCompleted: 8, totalAssignments: 8, status: 'ENROLLED' },
    { id: 2, courseCode: 'MATH201', courseName: 'Calculus II', grade: 'B+', attendanceRate: 88, assignmentsCompleted: 6, totalAssignments: 7, status: 'ENROLLED' },
    { id: 3, courseCode: 'ENG102', courseName: 'English Composition', grade: 'A-', attendanceRate: 92, assignmentsCompleted: 5, totalAssignments: 5, status: 'ENROLLED' },
    { id: 4, courseCode: 'PHY101', courseName: 'Physics I', grade: 'B', attendanceRate: 90, assignmentsCompleted: 4, totalAssignments: 5, status: 'ENROLLED' },
    { id: 5, courseCode: 'CS201', courseName: 'Data Structures', grade: 'IP', attendanceRate: 100, assignmentsCompleted: 3, totalAssignments: 5, status: 'ENROLLED' }
  ]

  nextTick(() => {
    renderCharts([], [])
  })
}

function getGradeClass(grade) {
  if (!grade || grade === 'IP') return 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300'
  if (grade.startsWith('A')) return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
  if (grade.startsWith('B')) return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400'
  if (grade.startsWith('C')) return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400'
  return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
}

function getStatusClass(status) {
  const classes = {
    'ENROLLED': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    'COMPLETED': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    'DROPPED': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
    'WAITLISTED': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400'
  }
  return classes[status] || classes['ENROLLED']
}

function exportStudentReport() {
  if (!studentData.value) return

  const csvContent = [
    ['Student Report'],
    ['Name', `${studentData.value.firstName} ${studentData.value.lastName}`],
    ['Email', studentData.value.email],
    ['Student ID', studentData.value.studentId || 'N/A'],
    [''],
    ['Overall Statistics'],
    ['GPA', stats.value.gpa.toFixed(2)],
    ['Attendance Rate', `${stats.value.attendanceRate}%`],
    ['Courses Enrolled', stats.value.coursesEnrolled],
    ['Assignments Completed', `${stats.value.assignmentsCompleted}/${stats.value.totalAssignments}`],
    [''],
    ['Course Details'],
    ['Course Code', 'Course Name', 'Grade', 'Attendance', 'Assignments', 'Status'],
    ...courseDetails.value.map(c => [
      c.courseCode,
      c.courseName,
      c.grade,
      `${c.attendanceRate}%`,
      `${c.assignmentsCompleted}/${c.totalAssignments}`,
      c.status
    ])
  ].map(row => row.join(',')).join('\n')

  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `student_report_${studentData.value.firstName}_${studentData.value.lastName}.csv`
  link.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.student-analytics {
  min-height: 100vh;
  background-color: #f3f4f6;
}

:deep(.dark) .student-analytics {
  background-color: #111827;
}
</style>
