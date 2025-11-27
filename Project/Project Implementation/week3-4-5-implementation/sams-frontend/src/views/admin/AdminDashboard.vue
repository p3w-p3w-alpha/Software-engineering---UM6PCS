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
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>

        <!-- Default Dashboard Content -->
        <div v-if="$route.path === '/admin'">
          <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
            <p class="mt-2 text-gray-600">Welcome, {{ userName }}! Manage the academic system.</p>
          </div>

          <!-- Stats Overview -->
          <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4 mb-8">
            <StatCard
              title="Total Users"
              :value="stats.totalUsers"
              :icon="UsersIcon"
              color="blue"
            />
            <StatCard
              title="Active Courses"
              :value="stats.activeCourses"
              :icon="AcademicCapIcon"
              color="green"
            />
            <StatCard
              title="Pending Payments"
              :value="stats.pendingPayments"
              :icon="CurrencyDollarIcon"
              color="orange"
            />
            <StatCard
              title="Total Revenue"
              :value="`$${stats.totalRevenue}`"
              :icon="ChartBarIcon"
              color="purple"
            />
          </div>

          <!-- Main Content Grid -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Pending Payments Approval -->
            <div class="bg-white rounded-lg shadow">
              <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h2 class="text-lg font-semibold text-gray-900">Pending Payment Approvals</h2>
                <router-link
                  to="/admin/payments"
                  class="text-sm text-blue-600 hover:text-blue-800 font-medium"
                >
                  View All
                </router-link>
              </div>
              <div class="p-6">
                <div v-if="loadingPayments" class="flex justify-center py-4">
                  <LoadingSpinner />
                </div>
                <div v-else-if="pendingPayments.length === 0" class="text-center py-8 text-gray-500">
                  <p class="text-sm">No pending payments</p>
                </div>
                <div v-else class="space-y-3">
                  <div
                    v-for="payment in pendingPayments.slice(0, 5)"
                    :key="payment.id"
                    class="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                  >
                    <div class="flex-1">
                      <p class="font-medium text-gray-900">{{ payment.student.username }}</p>
                      <p class="text-sm text-gray-600">${{ payment.paidAmount }} • {{ payment.semester.name }}</p>
                    </div>
                    <router-link
                      :to="`/admin/payments/${payment.id}`"
                      class="px-3 py-1 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700"
                    >
                      Review
                    </router-link>
                  </div>
                </div>
              </div>
            </div>

            <!-- Recent User Registrations -->
            <div class="bg-white rounded-lg shadow">
              <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h2 class="text-lg font-semibold text-gray-900">Recent User Registrations</h2>
                <router-link
                  to="/admin/users"
                  class="text-sm text-blue-600 hover:text-blue-800 font-medium"
                >
                  View All
                </router-link>
              </div>
              <div class="p-6">
                <div v-if="loadingUsers" class="flex justify-center py-4">
                  <LoadingSpinner />
                </div>
                <div v-else class="space-y-3">
                  <div
                    v-for="user in recentUsers.slice(0, 5)"
                    :key="user.id"
                    class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                  >
                    <div>
                      <p class="font-medium text-gray-900">{{ user.username }}</p>
                      <p class="text-sm text-gray-600">{{ user.email }} • {{ user.role }}</p>
                    </div>
                    <span
                      class="px-2 py-1 text-xs font-semibold rounded-full"
                      :class="user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                    >
                      {{ user.active ? 'Active' : 'Inactive' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
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
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import NotificationPanel from '../../components/NotificationPanel.vue'
import { UsersIcon, AcademicCapIcon, CurrencyDollarIcon, ChartBarIcon } from '@heroicons/vue/24/outline'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.userName)
const userId = computed(() => authStore.userId)

const navigation = [
  { name: 'Dashboard', href: '/admin' },
  { name: 'Users', href: '/admin/users' },
  { name: 'Courses', href: '/admin/courses' },
  { name: 'Payments', href: '/admin/payments' }
]

const sidebarTitle = 'Admin Portal'
const sidebarSubtitle = authStore.isSuperAdmin ? 'Super Administrator' : 'Administrator'

const menuItems = [
  { name: 'Dashboard', href: '/admin', icon: 'home' },
  { name: 'User Management', href: '/admin/users', icon: 'users' },
  { name: 'Course Management', href: '/admin/courses', icon: 'academic-cap' },
  { name: 'Payment Approval', href: '/admin/payments', icon: 'credit-card' },
  { name: 'Semester Management', href: '/admin/semesters', icon: 'calendar' },
  { name: 'Degree Programs', href: '/admin/degree-programs', icon: 'book-open' },
  { name: 'Reports', href: '/admin/reports', icon: 'chart-bar' }
]

const stats = ref({
  totalUsers: 0,
  activeCourses: 0,
  pendingPayments: 0,
  totalRevenue: 0
})

const pendingPayments = ref([])
const recentUsers = ref([])
const notifications = ref([])

const loadingPayments = ref(false)
const loadingUsers = ref(false)
const loadingNotifications = ref(false)
const showNotifications = ref(false)

const unreadNotifications = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const loadDashboardStats = async () => {
  try {
    // Load users
    loadingUsers.value = true
    const usersRes = await api.getAllUsers()
    const users = usersRes.data
    stats.value.totalUsers = users.length
    recentUsers.value = users.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

    // Load courses
    const coursesRes = await api.getAllCourses()
    stats.value.activeCourses = coursesRes.data.filter(c => c.active).length

    // Load payments
    loadingPayments.value = true
    const paymentsRes = await api.getPendingApprovalPayments()
    pendingPayments.value = paymentsRes.data
    stats.value.pendingPayments = pendingPayments.value.length

    // Calculate total revenue (approved payments)
    const allPaymentsRes = await api.getAllPayments()
    stats.value.totalRevenue = allPaymentsRes.data
      .filter(p => p.status === 'APPROVED')
      .reduce((sum, p) => sum + p.paidAmount, 0)

  } catch (error) {
    console.error('Error loading dashboard stats:', error)
  } finally {
    loadingPayments.value = false
    loadingUsers.value = false
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

const connectWebSocket = async () => {
  try {
    await websocketService.connect(authStore.token)
    websocketService.subscribeToNotifications(userId.value, (notification) => {
      notifications.value.unshift(notification)
    })
  } catch (error) {
    console.error('WebSocket connection failed:', error)
  }
}

onMounted(() => {
  loadDashboardStats()
  loadNotifications()
  connectWebSocket()
})
</script>
