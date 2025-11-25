<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">Student Dashboard</h1>
            <p class="text-sm text-gray-600 mt-1">Welcome, <span class="font-semibold">{{ authStore.userName }}</span></p>
          </div>
          <div class="flex items-center gap-4">
            <nav class="hidden md:flex items-center gap-2">
              <router-link to="/student" class="px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">Dashboard</router-link>
              <router-link to="/student/courses/browse" class="px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">Browse Courses</router-link>
              <router-link to="/student/payments" class="px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">Payments</router-link>
              <router-link to="/student/grades" class="px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">Grades</router-link>
              <router-link to="/student/degree-progress" class="px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">Progress</router-link>
            </nav>
            <button
              @click="handleLogout"
              class="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors flex items-center gap-2"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
              </svg>
              Logout
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Enrolled Courses</div>
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ enrollments.length }}</div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Active Assignments</div>
            <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ activeAssignments }}</div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Study Groups</div>
            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ studyGroups.length }}</div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Notifications</div>
            <svg class="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ unreadCount }}</div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Enrolled Courses -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-xl font-semibold text-gray-900">My Courses</h2>
            <p class="text-sm text-gray-600 mt-1">Your enrolled courses for this semester</p>
          </div>
          <div class="p-6">
            <div v-if="enrollments.length === 0" class="text-gray-500 text-center py-8">
              No courses enrolled yet
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="enrollment in enrollments"
                :key="enrollment.id"
                class="border border-gray-200 rounded-lg p-4 hover:border-primary-300 hover:shadow-sm transition-all"
              >
                <h3 class="font-semibold text-gray-900">{{ enrollment.courseName || 'Course' }}</h3>
                <p class="text-sm text-gray-600 mt-1">{{ enrollment.courseCode || 'N/A' }}</p>
                <div class="mt-3">
                  <span :class="getEnrollmentStatusClass(enrollment.status)"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
                    {{ enrollment.status }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Assignments -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-xl font-semibold text-gray-900">Recent Assignments</h2>
            <p class="text-sm text-gray-600 mt-1">Upcoming and pending assignments</p>
          </div>
          <div class="p-6">
            <div v-if="assignments.length === 0" class="text-gray-500 text-center py-8">
              No assignments available
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="assignment in assignments.slice(0, 5)"
                :key="assignment.id"
                class="border border-gray-200 rounded-lg p-4 hover:border-primary-300 hover:shadow-sm transition-all"
              >
                <h3 class="font-semibold text-gray-900">{{ assignment.title }}</h3>
                <p class="text-sm text-gray-600 mt-1 line-clamp-2">{{ assignment.description }}</p>
                <div class="mt-3 flex justify-between items-center">
                  <span class="text-xs text-gray-500">
                    Due: {{ formatDate(assignment.dueDate) }}
                  </span>
                  <span :class="getAssignmentStatusClass(assignment)"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
                    {{ assignment.status || 'Pending' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Grades -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-xl font-semibold text-gray-900">My Grades</h2>
            <p class="text-sm text-gray-600 mt-1">Your academic performance</p>
          </div>
          <div class="p-6">
            <div v-if="grades.length === 0" class="text-gray-500 text-center py-8">
              No grades available yet
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="grade in grades"
                :key="grade.id"
                class="flex justify-between items-center p-4 border border-gray-200 rounded-lg hover:border-primary-300 hover:shadow-sm transition-all"
              >
                <div>
                  <p class="font-medium text-gray-900">{{ grade.courseName || grade.assignmentTitle }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ grade.courseCode || 'Assignment' }}</p>
                </div>
                <div class="text-right">
                  <p class="text-2xl font-bold" :class="getGradeColor(grade.grade)">
                    {{ grade.grade }}
                  </p>
                  <p class="text-xs text-gray-500">/ {{ grade.maxGrade || 100 }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Study Groups -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-xl font-semibold text-gray-900">Study Groups</h2>
            <p class="text-sm text-gray-600 mt-1">Your collaborative learning groups</p>
          </div>
          <div class="p-6">
            <div v-if="studyGroups.length === 0" class="text-gray-500 text-center py-8">
              No study groups joined
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="group in studyGroups"
                :key="group.id"
                class="border border-gray-200 rounded-lg p-4 hover:border-primary-300 hover:shadow-sm transition-all"
              >
                <h3 class="font-semibold text-gray-900">{{ group.name }}</h3>
                <p class="text-sm text-gray-600 mt-1">{{ group.description }}</p>
                <div class="mt-3 flex items-center text-xs text-gray-500">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
                  </svg>
                  <span>{{ group.memberCount || 0 }} members</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const enrollments = ref([])
const assignments = ref([])
const grades = ref([])
const studyGroups = ref([])
const unreadCount = ref(0)

const activeAssignments = computed(() => {
  return assignments.value.filter(a =>
    a.status !== 'SUBMITTED' && a.status !== 'GRADED'
  ).length
})

onMounted(async () => {
  await loadStudentData()
})

async function loadStudentData() {
  try {
    const studentId = authStore.userId

    // Load enrollments
    const enrollmentsResponse = await api.getStudentEnrollments(studentId)
    enrollments.value = enrollmentsResponse.data

    // Load assignments
    const assignmentsResponse = await api.getStudentAssignments(studentId)
    assignments.value = assignmentsResponse.data

    // Load grades
    const gradesResponse = await api.getStudentGrades(studentId)
    grades.value = gradesResponse.data

    // Load study groups
    const groupsResponse = await api.getUserStudyGroups(studentId)
    studyGroups.value = groupsResponse.data

    // Load unread notifications count
    const notifCountResponse = await api.getUnreadNotificationCount(studentId)
    unreadCount.value = notifCountResponse.data
  } catch (error) {
    console.error('Error loading student data:', error)
  }
}

function getEnrollmentStatusClass(status) {
  const classes = {
    'ENROLLED': 'bg-green-100 text-green-800',
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'COMPLETED': 'bg-blue-100 text-blue-800',
    'DROPPED': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getAssignmentStatusClass(assignment) {
  const status = assignment.status || 'PENDING'
  const classes = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'SUBMITTED': 'bg-blue-100 text-blue-800',
    'GRADED': 'bg-green-100 text-green-800',
    'OVERDUE': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getGradeColor(grade) {
  if (grade >= 90) return 'text-green-600'
  if (grade >= 80) return 'text-blue-600'
  if (grade >= 70) return 'text-yellow-600'
  if (grade >= 60) return 'text-orange-600'
  return 'text-red-600'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
