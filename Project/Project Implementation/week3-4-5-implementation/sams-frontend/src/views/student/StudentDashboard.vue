<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar
      :navigation="navigation"
      :unread-count="unreadNotifications"
      @toggle-notifications="showNotifications = !showNotifications"
    />

    <!-- Main Content -->
    <div class="flex">
      <!-- Sidebar -->
      <Sidebar :title="sidebarTitle" :subtitle="sidebarSubtitle" :menu-items="menuItems" />

      <!-- Content Area -->
      <main class="flex-1 p-8">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>

        <!-- Default Dashboard Content -->
        <div v-if="$route.path === '/student'">
          <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900">Welcome back, {{ userName }}!</h1>
            <p class="mt-2 text-gray-600">Here's what's happening with your academic journey.</p>
          </div>

          <!-- Stats Cards -->
          <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4 mb-8">
            <StatCard
              title="Enrolled Courses"
              :value="stats.enrolledCourses"
              icon="academic-cap"
              color="blue"
            />
            <StatCard
              title="Current GPA"
              :value="stats.gpa"
              icon="chart-bar"
              color="green"
            />
            <StatCard
              title="Credits"
              :value="`${stats.completedCredits}/${stats.totalCredits}`"
              icon="book-open"
              color="purple"
            />
            <StatCard
              title="Pending Payments"
              :value="`$${stats.pendingPayments}`"
              icon="currency-dollar"
              color="yellow"
            />
          </div>

          <!-- Quick Actions & Upcoming -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <!-- Quick Actions -->
            <div class="bg-white rounded-lg shadow p-6">
              <h2 class="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
              <div class="space-y-3">
                <router-link
                  to="/student/courses/browse"
                  class="flex items-center p-3 rounded-lg bg-blue-50 text-blue-700 hover:bg-blue-100 transition-colors"
                >
                  <svg class="h-6 w-6 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                  Browse & Enroll in Courses
                </router-link>
                <router-link
                  to="/student/payments"
                  class="flex items-center p-3 rounded-lg bg-green-50 text-green-700 hover:bg-green-100 transition-colors"
                >
                  <svg class="h-6 w-6 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                  </svg>
                  View Payment Status
                </router-link>
                <router-link
                  to="/student/grades"
                  class="flex items-center p-3 rounded-lg bg-purple-50 text-purple-700 hover:bg-purple-100 transition-colors"
                >
                  <svg class="h-6 w-6 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                  </svg>
                  Check My Grades
                </router-link>
                <router-link
                  to="/student/degree-progress"
                  class="flex items-center p-3 rounded-lg bg-indigo-50 text-indigo-700 hover:bg-indigo-100 transition-colors"
                >
                  <svg class="h-6 w-6 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                  </svg>
                  Track Degree Progress
                </router-link>
              </div>
            </div>

            <!-- Upcoming Assignments -->
            <div class="bg-white rounded-lg shadow p-6">
              <h2 class="text-lg font-semibold text-gray-900 mb-4">Upcoming Assignments</h2>
              <div v-if="upcomingAssignments.length === 0" class="text-center text-gray-500 py-8">
                No upcoming assignments
              </div>
              <div v-else class="space-y-3">
                <div
                  v-for="assignment in upcomingAssignments"
                  :key="assignment.id"
                  class="flex items-center justify-between p-3 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors"
                >
                  <div>
                    <p class="font-medium text-gray-900">{{ assignment.title }}</p>
                    <p class="text-sm text-gray-500">{{ assignment.courseName }}</p>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-medium" :class="getDueDateColor(assignment.dueDate)">
                      {{ formatDueDate(assignment.dueDate) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Current Enrollments -->
          <div class="bg-white rounded-lg shadow">
            <div class="px-6 py-4 border-b border-gray-200">
              <h2 class="text-lg font-semibold text-gray-900">Current Enrollments</h2>
            </div>
            <div class="p-6">
              <DataTable
                :columns="enrollmentColumns"
                :data="currentEnrollments"
                :loading="loadingEnrollments"
                :paginated="false"
                empty-message="No active enrollments"
              >
                <template #cell-status="{ value }">
                  <span
                    class="px-2 py-1 text-xs font-semibold rounded-full"
                    :class="getStatusClass(value)"
                  >
                    {{ value }}
                  </span>
                </template>
              </DataTable>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Notification Panel -->
    <NotificationPanel
      :is-open="showNotifications"
      :notifications="notifications"
      :loading="loadingNotifications"
      @close="showNotifications = false"
      @mark-read="markNotificationAsRead"
      @mark-all-read="markAllNotificationsAsRead"
      @notification-click="handleNotificationClick"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import websocketService from '../../services/websocket'
import Navbar from '../../components/Navbar.vue'
import Sidebar from '../../components/Sidebar.vue'
import StatCard from '../../components/StatCard.vue'
import DataTable from '../../components/DataTable.vue'
import NotificationPanel from '../../components/NotificationPanel.vue'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.userName)
const userId = computed(() => authStore.userId)

const navigation = [
  { name: 'Dashboard', href: '/student' },
  { name: 'Courses', href: '/student/courses' },
  { name: 'Grades', href: '/student/grades' },
  { name: 'Payments', href: '/student/payments' }
]

const sidebarTitle = 'Student Portal'
const sidebarSubtitle = 'Manage your academic journey'

const menuItems = [
  { name: 'Dashboard', href: '/student', icon: 'home' },
  { name: 'Browse Courses', href: '/student/courses/browse', icon: 'search' },
  { name: 'My Enrollments', href: '/student/enrollments', icon: 'book-open' },
  { name: 'My Grades', href: '/student/grades', icon: 'chart-bar' },
  { name: 'Payments', href: '/student/payments', icon: 'credit-card' },
  { name: 'Degree Progress', href: '/student/degree-progress', icon: 'academic-cap' },
  { name: 'Assignments', href: '/student/assignments', icon: 'clipboard-list' }
]

// Data
const stats = ref({
  enrolledCourses: 0,
  gpa: '0.00',
  completedCredits: 0,
  totalCredits: 0,
  pendingPayments: 0
})

const currentEnrollments = ref([])
const upcomingAssignments = ref([])
const notifications = ref([])

const loadingEnrollments = ref(false)
const loadingNotifications = ref(false)
const showNotifications = ref(false)

const enrollmentColumns = [
  { key: 'courseCode', label: 'Course Code' },
  { key: 'courseName', label: 'Course Name' },
  { key: 'creditHours', label: 'Credits' },
  { key: 'schedule', label: 'Schedule' },
  { key: 'status', label: 'Status' }
]

const unreadNotifications = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

// Methods
const loadDashboardData = async () => {
  try {
    loadingEnrollments.value = true

    // Load enrollments
    const enrollmentsRes = await api.getStudentEnrollments(userId.value)
    const enrollments = enrollmentsRes.data

    currentEnrollments.value = enrollments
      .filter(e => e.status === 'ACTIVE' || e.status === 'PENDING_PAYMENT')
      .map(e => ({
        id: e.id,
        courseCode: e.course.courseCode,
        courseName: e.course.courseName,
        creditHours: e.course.creditHours,
        schedule: `${e.course.daysOfWeek} ${e.course.startTime}-${e.course.endTime}`,
        status: e.status
      }))

    stats.value.enrolledCourses = currentEnrollments.value.length

    // Load grades for GPA
    const gradesRes = await api.getStudentGrades(userId.value)
    if (gradesRes.data.gpa) {
      stats.value.gpa = gradesRes.data.gpa.toFixed(2)
    }

    // Load degree progress
    try {
      const progressRes = await api.getStudentProgress(userId.value)
      stats.value.completedCredits = progressRes.data.creditsCompleted || 0
      stats.value.totalCredits = progressRes.data.degreeProgram?.totalCreditsRequired || 0
    } catch (error) {
      console.log('No degree progress data available')
    }

    // Load payments
    const paymentsRes = await api.getStudentPayments(userId.value)
    const pendingPayments = paymentsRes.data.filter(p => p.status === 'PENDING' || p.status === 'PAID')
    stats.value.pendingPayments = pendingPayments.reduce((sum, p) => sum + p.remainingAmount, 0)

    // Load assignments
    const assignmentsRes = await api.getStudentAssignments(userId.value)
    upcomingAssignments.value = assignmentsRes.data
      .filter(a => new Date(a.dueDate) > new Date())
      .slice(0, 5)

  } catch (error) {
    console.error('Error loading dashboard data:', error)
  } finally {
    loadingEnrollments.value = false
  }
}

const loadNotifications = async () => {
  try {
    loadingNotifications.value = true
    const response = await api.getUserNotifications(userId.value, 0, 20)
    notifications.value = response.data.content || response.data
  } catch (error) {
    console.error('Error loading notifications:', error)
  } finally {
    loadingNotifications.value = false
  }
}

const markNotificationAsRead = async (notificationId) => {
  try {
    await api.markNotificationAsRead(notificationId)
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.read = true
    }
  } catch (error) {
    console.error('Error marking notification as read:', error)
  }
}

const markAllNotificationsAsRead = async () => {
  try {
    const unreadIds = notifications.value.filter(n => !n.read).map(n => n.id)
    for (const id of unreadIds) {
      await api.markNotificationAsRead(id)
    }
    notifications.value.forEach(n => n.read = true)
  } catch (error) {
    console.error('Error marking all as read:', error)
  }
}

const handleNotificationClick = (notification) => {
  if (notification.relatedLink) {
    router.push(notification.relatedLink)
  }
  showNotifications.value = false
}

const getStatusClass = (status) => {
  const classes = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'PENDING_PAYMENT': 'bg-yellow-100 text-yellow-800',
    'WAITLISTED': 'bg-blue-100 text-blue-800',
    'DROPPED': 'bg-gray-100 text-gray-800',
    'COMPLETED': 'bg-purple-100 text-purple-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getDueDateColor = (dueDate) => {
  const days = Math.ceil((new Date(dueDate) - new Date()) / (1000 * 60 * 60 * 24))
  if (days <= 1) return 'text-red-600'
  if (days <= 3) return 'text-orange-600'
  return 'text-gray-600'
}

const formatDueDate = (dueDate) => {
  const days = Math.ceil((new Date(dueDate) - new Date()) / (1000 * 60 * 60 * 24))
  if (days === 0) return 'Due today'
  if (days === 1) return 'Due tomorrow'
  return `Due in ${days} days`
}

// Initialize WebSocket
const connectWebSocket = async () => {
  try {
    await websocketService.connect(authStore.token)

    // Subscribe to notifications
    websocketService.subscribeToNotifications(userId.value, (notification) => {
      notifications.value.unshift(notification)
    })
  } catch (error) {
    console.error('WebSocket connection failed:', error)
  }
}

onMounted(() => {
  loadDashboardData()
  loadNotifications()
  connectWebSocket()
})
</script>
