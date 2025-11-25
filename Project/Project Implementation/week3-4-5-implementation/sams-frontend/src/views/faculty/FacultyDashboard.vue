<template>
  <div class="min-h-screen bg-gray-50">
    <Navbar
      :navigation="navigation"
      :unread-count="unreadNotifications"
      @toggle-notifications="showNotifications = !showNotifications"
    />

    <div class="flex">
      <Sidebar :title="sidebarTitle" :subtitle="sidebarSubtitle" :menu-items="menuItems" />

      <main class="flex-1 p-8">
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-900">Faculty Dashboard</h1>
          <p class="mt-2 text-gray-600">Welcome, {{ userName }}! Manage your courses and students.</p>
        </div>

        <!-- Stats Overview -->
        <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4 mb-8">
          <StatCard title="My Courses" :value="stats.myCourses" icon="academic-cap" color="blue" />
          <StatCard title="Total Students" :value="stats.totalStudents" icon="users" color="green" />
          <StatCard title="Assignments" :value="stats.totalAssignments" icon="clipboard-list" color="purple" />
          <StatCard title="Pending Grades" :value="stats.pendingGrades" icon="pencil-alt" color="yellow" />
        </div>

        <!-- My Courses -->
        <div class="bg-white rounded-lg shadow mb-6">
          <div class="px-6 py-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">My Courses</h2>
          </div>
          <div class="p-6">
            <div v-if="loadingCourses" class="flex justify-center py-8">
              <LoadingSpinner />
            </div>
            <div v-else-if="myCourses.length === 0" class="text-center py-8 text-gray-500">
              <p>No courses assigned</p>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div
                v-for="course in myCourses"
                :key="course.id"
                class="border border-gray-200 rounded-lg p-4 hover:border-blue-300 transition-colors cursor-pointer"
                @click="viewCourse(course)"
              >
                <h3 class="font-semibold text-gray-900">{{ course.courseCode }}</h3>
                <p class="text-sm text-gray-600 mt-1">{{ course.courseName }}</p>
                <div class="mt-3 flex items-center justify-between text-sm">
                  <span class="text-gray-600">{{ course.enrolledCount || 0 }} students</span>
                  <span class="text-gray-600">{{ course.creditHours }} credits</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Assignments -->
        <div class="bg-white rounded-lg shadow">
          <div class="px-6 py-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">Recent Assignments</h2>
          </div>
          <div class="p-6">
            <DataTable
              :columns="assignmentColumns"
              :data="recentAssignments"
              :loading="loadingAssignments"
              :paginated="false"
              empty-message="No assignments created"
            >
              <template #cell-dueDate="{ value }">
                <span :class="getDueDateClass(value)">{{ formatDate(value) }}</span>
              </template>
              <template #row-actions="{ row }">
                <button class="text-blue-600 hover:text-blue-900 text-sm font-medium">
                  View Submissions
                </button>
              </template>
            </DataTable>
          </div>
        </div>
      </main>
    </div>

    <NotificationPanel
      :is-open="showNotifications"
      :notifications="notifications"
      :loading="loadingNotifications"
      @close="showNotifications = false"
      @mark-read="markNotificationAsRead"
      @mark-all-read="markAllNotificationsAsRead"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import Navbar from '../../components/Navbar.vue'
import Sidebar from '../../components/Sidebar.vue'
import StatCard from '../../components/StatCard.vue'
import DataTable from '../../components/DataTable.vue'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import NotificationPanel from '../../components/NotificationPanel.vue'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.userName)
const userId = computed(() => authStore.userId)

const navigation = [
  { name: 'Dashboard', href: '/faculty' },
  { name: 'My Courses', href: '/faculty/courses' },
  { name: 'Grades', href: '/faculty/grades' }
]

const sidebarTitle = 'Faculty Portal'
const sidebarSubtitle = 'Instructor Dashboard'

const menuItems = [
  { name: 'Dashboard', href: '/faculty', icon: 'home' },
  { name: 'My Courses', href: '/faculty/courses', icon: 'academic-cap' },
  { name: 'Grade Management', href: '/faculty/grades', icon: 'pencil-alt' },
  { name: 'Assignments', href: '/faculty/assignments', icon: 'clipboard-list' }
]

const stats = ref({
  myCourses: 0,
  totalStudents: 0,
  totalAssignments: 0,
  pendingGrades: 0
})

const myCourses = ref([])
const recentAssignments = ref([])
const notifications = ref([])

const loadingCourses = ref(false)
const loadingAssignments = ref(false)
const loadingNotifications = ref(false)
const showNotifications = ref(false)

const assignmentColumns = [
  { key: 'title', label: 'Assignment' },
  { key: 'courseName', label: 'Course' },
  { key: 'dueDate', label: 'Due Date' },
  { key: 'submissions', label: 'Submissions' }
]

const unreadNotifications = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const loadDashboardData = async () => {
  try {
    loadingCourses.value = true

    // Load faculty's courses
    const coursesRes = await api.getAllCourses()
    // In a real implementation, we'd filter by faculty ID
    myCourses.value = coursesRes.data.slice(0, 6) // Mock: show first 6 courses
    stats.value.myCourses = myCourses.value.length

    // Calculate total students
    stats.value.totalStudents = myCourses.value.reduce((sum, course) =>
      sum + (course.enrolledCount || 0), 0
    )

    // Load assignments
    loadingAssignments.value = true
    const assignmentsRes = await api.getFacultyAssignments(userId.value)
    recentAssignments.value = assignmentsRes.data.slice(0, 10).map(a => ({
      ...a,
      courseName: a.course?.courseName || 'N/A',
      submissions: `${a.submissionsCount || 0}/${a.totalStudents || 0}`
    }))
    stats.value.totalAssignments = assignmentsRes.data.length

  } catch (error) {
    console.error('Error loading dashboard data:', error)
  } finally {
    loadingCourses.value = false
    loadingAssignments.value = false
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

const viewCourse = (course) => {
  router.push(`/faculty/courses/${course.id}`)
}

const markNotificationAsRead = async (notificationId) => {
  try {
    await api.markNotificationAsRead(notificationId)
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) notification.read = true
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

const getDueDateClass = (dueDate) => {
  const days = Math.ceil((new Date(dueDate) - new Date()) / (1000 * 60 * 60 * 24))
  if (days < 0) return 'text-red-600'
  if (days <= 3) return 'text-orange-600'
  return 'text-gray-600'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

onMounted(() => {
  loadDashboardData()
  loadNotifications()
})
</script>
