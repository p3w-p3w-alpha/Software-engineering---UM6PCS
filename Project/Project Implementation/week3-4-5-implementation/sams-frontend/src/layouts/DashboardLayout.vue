<template>
  <div class="dashboard-layout min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
    <!-- Toast for notifications -->
    <Toast position="top-right" />

    <!-- Overlay for mobile -->
    <div
      v-if="!sidebarCollapsed && isMobile"
      @click="sidebarCollapsed = true"
      class="fixed inset-0 bg-black/50 z-30 md:hidden transition-opacity"
    ></div>

    <!-- Modern Sidebar -->
    <div
      :class="[
        'sidebar fixed left-0 top-0 h-full z-40 transition-all duration-300 ease-in-out',
        sidebarCollapsed ? '-translate-x-full md:translate-x-0 md:w-20' : 'translate-x-0 w-64'
      ]"
    >
      <div class="h-full bg-gradient-to-b from-indigo-900 via-purple-900 to-pink-900 shadow-2xl">
        <!-- Logo Section -->
        <div class="p-6 border-b border-white/10">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="relative">
                <div class="absolute inset-0 bg-white/20 rounded-xl blur-xl"></div>
                <div class="relative w-10 h-10 bg-white/10 rounded-xl flex items-center justify-center backdrop-blur-xl">
                  <i class="pi pi-graduation-cap text-white text-xl"></i>
                </div>
              </div>
              <Transition name="fade">
                <span v-if="!sidebarCollapsed" class="text-white font-bold text-xl">SAMS</span>
              </Transition>
            </div>
            <Button
              icon="pi pi-bars"
              class="p-button-text p-button-rounded text-white hover:bg-white/10"
              @click="sidebarCollapsed = !sidebarCollapsed"
            />
          </div>
        </div>

        <!-- User Profile Section -->
        <div class="p-4 border-b border-white/10">
          <div class="flex items-center gap-3">
            <Avatar
              :label="userInitials"
              :image="userAvatar"
              class="bg-gradient-to-br from-indigo-400 to-purple-500"
              size="large"
              shape="circle"
            />
            <Transition name="slide-left">
              <div v-if="!sidebarCollapsed" class="flex-1">
                <p class="text-white font-semibold">{{ userName }}</p>
                <p class="text-white/70 text-sm">{{ userRole }}</p>
              </div>
            </Transition>
          </div>
        </div>

        <!-- Navigation Menu -->
        <nav class="p-4 overflow-y-auto custom-scrollbar" style="max-height: calc(100vh - 240px)">
          <ul class="space-y-2">
            <li v-for="(item, index) in menuItems" :key="item.path">
              <!-- Item with children -->
              <div v-if="item.children">
                <div
                  @click="toggleSubmenu(item.path)"
                  :class="[
                    'flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-all duration-300',
                    isMenuActive(item) ? 'bg-white/20 text-white shadow-lg backdrop-blur-xl' : 'text-white/70 hover:bg-white/10 hover:text-white'
                  ]"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  class="animate-slide-in-left"
                >
                  <i :class="[item.icon, 'text-xl']"></i>
                  <Transition name="slide-left">
                    <span v-if="!sidebarCollapsed" class="font-medium">{{ item.label }}</span>
                  </Transition>
                  <i v-if="!sidebarCollapsed" :class="['pi', expandedMenus.includes(item.path) ? 'pi-chevron-down' : 'pi-chevron-right', 'ml-auto text-sm']"></i>
                </div>
                <!-- Submenu -->
                <Transition name="slide-down">
                  <ul v-if="expandedMenus.includes(item.path) && !sidebarCollapsed" class="mt-2 ml-4 space-y-1">
                    <li v-for="child in item.children" :key="child.path">
                      <router-link
                        :to="child.path"
                        v-slot="{ isActive }"
                        custom
                      >
                        <div
                          @click="navigateTo(child.path)"
                          :class="[
                            'flex items-center gap-3 px-4 py-2 rounded-lg cursor-pointer transition-all duration-200',
                            isActive ? 'bg-white/15 text-white' : 'text-white/60 hover:bg-white/10 hover:text-white'
                          ]"
                        >
                          <i :class="[child.icon, 'text-sm']"></i>
                          <span class="text-sm">{{ child.label }}</span>
                        </div>
                      </router-link>
                    </li>
                  </ul>
                </Transition>
              </div>
              <!-- Regular item without children -->
              <router-link
                v-else
                :to="item.path"
                v-slot="{ isActive }"
                custom
              >
                <div
                  @click="navigateTo(item.path)"
                  :class="[
                    'flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-all duration-300',
                    isActive
                      ? 'bg-white/20 text-white shadow-lg backdrop-blur-xl'
                      : 'text-white/70 hover:bg-white/10 hover:text-white'
                  ]"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  class="animate-slide-in-left"
                >
                  <i :class="[item.icon, 'text-xl']"></i>
                  <Transition name="slide-left">
                    <span v-if="!sidebarCollapsed" class="font-medium">{{ item.label }}</span>
                  </Transition>
                  <Transition name="fade">
                    <Badge
                      v-if="item.badge && !sidebarCollapsed"
                      :value="item.badge"
                      severity="danger"
                      class="ml-auto"
                    />
                  </Transition>
                </div>
              </router-link>
            </li>
          </ul>
        </nav>

        <!-- Bottom Actions -->
        <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-white/10">
          <div class="space-y-2">
            <button
              @click="toggleDarkMode"
              class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-white/70 hover:bg-white/10 hover:text-white transition-all duration-300"
            >
              <i :class="darkMode ? 'pi pi-sun' : 'pi pi-moon'" class="text-xl"></i>
              <Transition name="slide-left">
                <span v-if="!sidebarCollapsed" class="font-medium">
                  {{ darkMode ? 'Light Mode' : 'Dark Mode' }}
                </span>
              </Transition>
            </button>
            <button
              @click="handleLogout"
              class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-white/70 hover:bg-red-500/20 hover:text-red-300 transition-all duration-300"
            >
              <i class="pi pi-sign-out text-xl"></i>
              <Transition name="slide-left">
                <span v-if="!sidebarCollapsed" class="font-medium">Logout</span>
              </Transition>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content Area -->
    <div
      :class="[
        'main-content transition-all duration-300 min-h-screen',
        sidebarCollapsed ? 'md:ml-20' : 'md:ml-64'
      ]"
    >
      <!-- Top Navbar -->
      <nav class="navbar sticky top-0 z-30 bg-white/80 backdrop-blur-xl border-b border-gray-200 shadow-sm">
        <div class="px-6 py-4">
          <div class="flex items-center justify-between">
            <!-- Breadcrumb -->
            <Breadcrumb :model="breadcrumbItems" class="text-sm">
              <template #item="{ item }">
                <router-link v-if="item.route" :to="item.route" class="text-gray-600 hover:text-indigo-600 transition-colors">
                  {{ item.label }}
                </router-link>
                <span v-else class="text-gray-900 font-medium">{{ item.label }}</span>
              </template>
              <template #separator>
                <i class="pi pi-angle-right text-gray-400"></i>
              </template>
            </Breadcrumb>

            <!-- Right Actions -->
            <div class="flex items-center gap-2 md:gap-3">
              <!-- Global Search (hidden on small screens) -->
              <div class="relative hidden md:block search-container">
                <InputText
                  v-model="searchQuery"
                  placeholder="Search users, courses..."
                  class="pl-10 pr-10 py-2 w-48 lg:w-72 rounded-xl border-gray-200 focus:border-indigo-500"
                  @keyup.enter="performSearch"
                  @focus="searchQuery.length >= 2 && searchResults.totalResults > 0 ? showSearchResults = true : null"
                />
                <i class="pi pi-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
                <button
                  v-if="searchQuery"
                  @click="clearSearch"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <i class="pi pi-times text-sm"></i>
                </button>

                <!-- Search Results Dropdown -->
                <Transition name="scale">
                  <div
                    v-if="showSearchResults"
                    class="absolute top-12 left-0 w-80 lg:w-96 bg-white rounded-xl shadow-2xl border border-gray-200 overflow-hidden z-50 search-results-panel"
                  >
                    <!-- Loading State -->
                    <div v-if="searchLoading" class="p-6 text-center">
                      <i class="pi pi-spin pi-spinner text-2xl text-indigo-500"></i>
                      <p class="mt-2 text-gray-500">Searching...</p>
                    </div>

                    <!-- Results -->
                    <div v-else-if="searchResults.totalResults > 0" class="max-h-96 overflow-y-auto">
                      <!-- Users Section -->
                      <div v-if="searchResults.users && searchResults.users.length > 0">
                        <div class="px-4 py-2 bg-gray-50 border-b border-gray-100">
                          <span class="text-xs font-semibold text-gray-500 uppercase">Users</span>
                        </div>
                        <div
                          v-for="user in searchResults.users"
                          :key="'user-' + user.id"
                          @click="navigateToSearchResult('user', user)"
                          class="px-4 py-3 hover:bg-indigo-50 cursor-pointer border-b border-gray-100 flex items-center gap-3 transition-colors"
                        >
                          <Avatar
                            :label="user.username ? user.username.substring(0, 2).toUpperCase() : '?'"
                            size="small"
                            shape="circle"
                            class="bg-gradient-to-br from-indigo-400 to-purple-500"
                          />
                          <div class="flex-1 min-w-0">
                            <p class="font-medium text-gray-900 truncate">{{ user.username }}</p>
                            <p class="text-sm text-gray-500 truncate">{{ user.email }}</p>
                          </div>
                          <Badge :value="user.role" severity="info" class="text-xs" />
                        </div>
                      </div>

                      <!-- Courses Section -->
                      <div v-if="searchResults.courses && searchResults.courses.length > 0">
                        <div class="px-4 py-2 bg-gray-50 border-b border-gray-100">
                          <span class="text-xs font-semibold text-gray-500 uppercase">Courses</span>
                        </div>
                        <div
                          v-for="course in searchResults.courses"
                          :key="'course-' + course.id"
                          @click="navigateToSearchResult('course', course)"
                          class="px-4 py-3 hover:bg-indigo-50 cursor-pointer border-b border-gray-100 flex items-center gap-3 transition-colors"
                        >
                          <div class="w-10 h-10 bg-gradient-to-br from-green-400 to-emerald-500 rounded-lg flex items-center justify-center">
                            <i class="pi pi-book text-white"></i>
                          </div>
                          <div class="flex-1 min-w-0">
                            <p class="font-medium text-gray-900 truncate">{{ course.name }}</p>
                            <p class="text-sm text-gray-500">{{ course.code }} - {{ course.credits }} credits</p>
                          </div>
                        </div>
                      </div>

                      <!-- Total Results Footer -->
                      <div class="px-4 py-2 bg-gray-50 text-center">
                        <span class="text-xs text-gray-500">Found {{ searchResults.totalResults }} result(s)</span>
                      </div>
                    </div>

                    <!-- No Results -->
                    <div v-else class="p-6 text-center">
                      <i class="pi pi-search text-3xl text-gray-300"></i>
                      <p class="mt-2 text-gray-500">No results found for "{{ searchQuery }}"</p>
                      <p class="text-sm text-gray-400">Try different keywords</p>
                    </div>
                  </div>
                </Transition>
              </div>

              <!-- Search button for mobile -->
              <Button
                icon="pi pi-search"
                class="p-button-rounded p-button-text hover:bg-indigo-50 md:hidden"
                @click="performSearch"
              />

              <!-- Quick Actions -->
              <Button
                icon="pi pi-plus"
                class="p-button-rounded p-button-text hover:bg-indigo-50"
                v-tooltip="'Quick Add'"
                @click="showQuickAdd = true"
              />

              <!-- Notifications -->
              <div class="relative">
                <Button
                  icon="pi pi-bell"
                  :badge="unreadNotifications > 0 ? unreadNotifications.toString() : null"
                  class="p-button-rounded p-button-text hover:bg-indigo-50 notification-btn"
                  @click="toggleNotifications"
                />
                <Transition name="scale">
                  <div
                    v-if="showNotifications"
                    class="absolute right-0 top-12 w-80 bg-white rounded-xl shadow-2xl border border-gray-200 overflow-hidden notification-panel"
                  >
                    <NotificationPanel
                      :notifications="notifications"
                      :loading="notificationsLoading"
                      :is-open="showNotifications"
                      @close="showNotifications = false"
                      @mark-read="handleMarkRead"
                      @mark-all-read="handleMarkAllRead"
                      @notification-click="handleNotificationClick"
                    />
                  </div>
                </Transition>
              </div>

              <!-- User Menu -->
              <div class="relative">
                <button
                  @click="showUserMenu = !showUserMenu"
                  class="flex items-center gap-2 px-3 py-2 rounded-xl hover:bg-gray-100 transition-colors"
                >
                  <Avatar
                    :label="userInitials"
                    :image="userAvatar"
                    size="small"
                    shape="circle"
                    class="bg-gradient-to-br from-indigo-400 to-purple-500"
                  />
                  <i class="pi pi-angle-down text-gray-600"></i>
                </button>
                <Transition name="scale">
                  <div
                    v-if="showUserMenu"
                    class="absolute right-0 top-12 w-56 bg-white rounded-xl shadow-2xl border border-gray-200 overflow-hidden"
                  >
                    <div class="p-4 border-b border-gray-100">
                      <p class="font-semibold text-gray-900">{{ userName }}</p>
                      <p class="text-sm text-gray-600">{{ userEmail }}</p>
                    </div>
                    <div class="p-2">
                      <router-link
                        to="/profile"
                        class="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-gray-50 transition-colors"
                        @click="showUserMenu = false"
                      >
                        <i class="pi pi-user text-gray-600"></i>
                        <span class="text-gray-700">Profile</span>
                      </router-link>
                      <router-link
                        to="/settings"
                        class="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-gray-50 transition-colors"
                        @click="showUserMenu = false"
                      >
                        <i class="pi pi-cog text-gray-600"></i>
                        <span class="text-gray-700">Settings</span>
                      </router-link>
                      <Divider class="my-2" />
                      <button
                        @click="handleLogout"
                        class="w-full flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-red-50 text-red-600 transition-colors"
                      >
                        <i class="pi pi-sign-out"></i>
                        <span>Logout</span>
                      </button>
                    </div>
                  </div>
                </Transition>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <!-- Page Content with Animation -->
      <main class="p-6">
        <router-view v-slot="{ Component }">
          <Transition name="page" mode="out-in">
            <component :is="Component" />
          </Transition>
        </router-view>
      </main>

      <!-- Floating Action Button -->
      <div class="fixed bottom-6 right-6 z-20">
        <SpeedDial
          :model="speedDialItems"
          direction="up"
          :transitionDelay="50"
          showIcon="pi pi-plus"
          hideIcon="pi pi-times"
          class="speeddial-custom"
        />
      </div>
    </div>

    <!-- Quick Add Dialog -->
    <Dialog
      v-model:visible="showQuickAdd"
      header="Quick Add"
      modal
      class="w-full max-w-lg"
      :pt="{
        header: { class: 'bg-gradient-to-r from-indigo-500 to-purple-600 text-white' }
      }"
    >
      <div class="grid grid-cols-2 gap-4 p-4">
        <button
          v-for="action in quickAddActions"
          :key="action.label"
          @click="handleQuickAction(action)"
          class="p-6 bg-gradient-to-br from-gray-50 to-gray-100 hover:from-indigo-50 hover:to-purple-50 rounded-xl transition-all duration-300 transform hover:scale-105 hover:shadow-lg"
        >
          <i :class="[action.icon, 'text-3xl mb-3']" :style="{ color: action.color }"></i>
          <p class="font-medium text-gray-800">{{ action.label }}</p>
        </button>
      </div>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notifications'
import { useToast } from 'primevue/usetoast'
import api from '../services/api'
import Avatar from 'primevue/avatar'
import Badge from 'primevue/badge'
import Button from 'primevue/button'
import Breadcrumb from 'primevue/breadcrumb'
import InputText from 'primevue/inputtext'
import SpeedDial from 'primevue/speeddial'
import Dialog from 'primevue/dialog'
import Divider from 'primevue/divider'
import Toast from 'primevue/toast'
import NotificationPanel from '../components/NotificationPanel.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const toast = useToast()

// State
const sidebarCollapsed = ref(false)
const darkMode = ref(false)
const showNotifications = ref(false)
const showUserMenu = ref(false)
const showQuickAdd = ref(false)
const searchQuery = ref('')
const isMobile = ref(window.innerWidth < 768)
const expandedMenus = ref(['/admin/users']) // Keep Users menu expanded by default
const showSearchResults = ref(false)
const searchLoading = ref(false)
const searchResults = ref({ users: [], courses: [], totalResults: 0 })

// Computed notifications from store
const unreadNotifications = computed(() => notificationStore.unreadCount)
const notifications = computed(() => notificationStore.recentNotifications)
const notificationsLoading = computed(() => notificationStore.loading)

// User Info
const userName = computed(() => authStore.user?.username || authStore.userName || 'User')
const userEmail = computed(() => authStore.user?.email || 'user@example.com')
const userRole = computed(() => authStore.user?.role || authStore.userRole || 'User')
const userAvatar = computed(() => authStore.user?.avatar || null)
const userInitials = computed(() => {
  const name = userName.value
  if (name.length < 2) return name.toUpperCase()
  return name.substring(0, 2).toUpperCase()
})

// Dynamic Menu Items based on user role
const menuItems = computed(() => {
  // Normalize role for path: SUPER_ADMIN -> admin, ADMIN -> admin, STUDENT -> student, FACULTY -> faculty
  const rolePath = userRole.value === 'SUPER_ADMIN' || userRole.value === 'ADMIN'
    ? 'admin'
    : userRole.value.toLowerCase()

  const baseItems = [
    {
      label: 'Dashboard',
      icon: 'pi pi-home',
      path: `/${rolePath}`,
      badge: null
    }
  ]

  if (authStore.isAdmin) {
    return [
      ...baseItems,
      {
        label: 'Users',
        icon: 'pi pi-users',
        path: '/admin/users',
        children: [
          { label: 'All Users', icon: 'pi pi-list', path: '/admin/users' },
          { label: 'Students', icon: 'pi pi-user', path: '/admin/students' },
          { label: 'Faculty', icon: 'pi pi-briefcase', path: '/admin/faculty' },
          { label: 'Admins', icon: 'pi pi-shield', path: '/admin/admins' }
        ]
      },
      { label: 'Courses', icon: 'pi pi-book', path: '/admin/courses' },
      { label: 'Fees', icon: 'pi pi-dollar', path: '/admin/fees', badge: '5' },
      { label: 'Payments', icon: 'pi pi-wallet', path: '/admin/payments', badge: '3' },
      { label: 'Attendance', icon: 'pi pi-calendar-check', path: '/admin/attendance' },
      { label: 'Analytics', icon: 'pi pi-chart-bar', path: '/admin/analytics' },
      { label: 'Reports', icon: 'pi pi-chart-line', path: '/admin/reports' },
      { label: 'System', icon: 'pi pi-server', path: '/admin/system-health' },
      { label: 'Settings', icon: 'pi pi-cog', path: '/admin/settings' }
    ]
  }

  if (authStore.isStudent) {
    return [
      ...baseItems,
      { label: 'My Courses', icon: 'pi pi-book', path: '/student/courses' },
      { label: 'Assignments', icon: 'pi pi-file-edit', path: '/student/assignments', badge: '2' },
      { label: 'Grades', icon: 'pi pi-chart-line', path: '/student/grades' },
      { label: 'Attendance', icon: 'pi pi-calendar-check', path: '/student/attendance' },
      { label: 'Fees', icon: 'pi pi-dollar', path: '/student/fees' },
      { label: 'Schedule', icon: 'pi pi-calendar', path: '/student/schedule' },
      { label: 'Study Groups', icon: 'pi pi-users', path: '/student/study-groups' },
      { label: 'Messages', icon: 'pi pi-envelope', path: '/student/messages', badge: '3' }
    ]
  }

  if (authStore.isFaculty) {
    return [
      ...baseItems,
      { label: 'My Courses', icon: 'pi pi-book', path: '/faculty/courses' },
      { label: 'Assignments', icon: 'pi pi-file-edit', path: '/faculty/assignments' },
      { label: 'Grading', icon: 'pi pi-pencil', path: '/faculty/grading', badge: '8' },
      { label: 'Attendance', icon: 'pi pi-calendar-check', path: '/faculty/attendance' },
      { label: 'Schedule', icon: 'pi pi-calendar', path: '/faculty/schedule' },
      { label: 'Messages', icon: 'pi pi-envelope', path: '/faculty/messages', badge: '1' }
    ]
  }

  return baseItems
})

// Breadcrumb Items
const breadcrumbItems = computed(() => {
  const paths = route.path.split('/').filter(p => p)
  const items = [{ label: 'Home', route: '/' }]

  let currentPath = ''
  paths.forEach((path, index) => {
    currentPath += `/${path}`
    items.push({
      label: path.charAt(0).toUpperCase() + path.slice(1).replace(/-/g, ' '),
      route: index === paths.length - 1 ? null : currentPath
    })
  })

  return items
})

// Speed Dial Items
const speedDialItems = ref([
  {
    label: 'Add User',
    icon: 'pi pi-user-plus',
    command: () => toast.add({ severity: 'info', summary: 'Add User', life: 3000 })
  },
  {
    label: 'Create Course',
    icon: 'pi pi-book',
    command: () => toast.add({ severity: 'info', summary: 'Create Course', life: 3000 })
  },
  {
    label: 'New Assignment',
    icon: 'pi pi-file-edit',
    command: () => toast.add({ severity: 'info', summary: 'New Assignment', life: 3000 })
  },
  {
    label: 'Send Message',
    icon: 'pi pi-envelope',
    command: () => toast.add({ severity: 'info', summary: 'Send Message', life: 3000 })
  }
])

// Quick Add Actions
const quickAddActions = ref([
  { label: 'New User', icon: 'pi pi-user-plus', color: '#6366f1' },
  { label: 'New Course', icon: 'pi pi-book', color: '#10b981' },
  { label: 'New Assignment', icon: 'pi pi-file-edit', color: '#f59e0b' },
  { label: 'New Event', icon: 'pi pi-calendar-plus', color: '#ef4444' },
  { label: 'New Announcement', icon: 'pi pi-megaphone', color: '#8b5cf6' },
  { label: 'New Report', icon: 'pi pi-chart-line', color: '#3b82f6' }
])

// Notification refresh interval
let notificationRefreshInterval = null

// Methods
const navigateTo = (path) => {
  router.push(path)
}

const toggleSubmenu = (path) => {
  const index = expandedMenus.value.indexOf(path)
  if (index > -1) {
    expandedMenus.value.splice(index, 1)
  } else {
    expandedMenus.value.push(path)
  }
}

const isMenuActive = (item) => {
  if (route.path === item.path) return true
  if (item.children) {
    return item.children.some(child => route.path === child.path)
  }
  return false
}

const toggleDarkMode = () => {
  darkMode.value = !darkMode.value
  document.documentElement.classList.toggle('dark-mode')
  toast.add({
    severity: 'info',
    summary: darkMode.value ? 'Dark Mode Enabled' : 'Light Mode Enabled',
    life: 2000
  })
}

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
  showUserMenu.value = false
}

// Notification handlers
const handleMarkRead = async (notificationId) => {
  try {
    await notificationStore.markAsRead(notificationId)
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Failed to mark notification as read',
      life: 3000
    })
  }
}

const handleMarkAllRead = async () => {
  try {
    await notificationStore.markAllAsRead(authStore.userId)
    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: 'All notifications marked as read',
      life: 2000
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Failed to mark all notifications as read',
      life: 3000
    })
  }
}

const handleNotificationClick = async (notification) => {
  // Mark as read if not already
  if (!notification.read) {
    await handleMarkRead(notification.id)
  }

  // Navigate to the action URL if provided
  if (notification.actionUrl) {
    router.push(notification.actionUrl)
    showNotifications.value = false
  }
}

const performSearch = async () => {
  if (!searchQuery.value || searchQuery.value.trim().length < 2) {
    toast.add({
      severity: 'warn',
      summary: 'Search',
      detail: 'Please enter at least 2 characters to search',
      life: 2000
    })
    return
  }

  searchLoading.value = true
  showSearchResults.value = true

  try {
    const response = await api.globalSearch(searchQuery.value.trim())
    searchResults.value = response.data
  } catch (error) {
    console.error('Search error:', error)
    toast.add({
      severity: 'error',
      summary: 'Search Failed',
      detail: 'Unable to perform search. Please try again.',
      life: 3000
    })
    searchResults.value = { users: [], courses: [], totalResults: 0 }
  } finally {
    searchLoading.value = false
  }
}

const clearSearch = () => {
  searchQuery.value = ''
  showSearchResults.value = false
  searchResults.value = { users: [], courses: [], totalResults: 0 }
}

const navigateToSearchResult = (type, item) => {
  showSearchResults.value = false
  searchQuery.value = ''

  if (type === 'user') {
    router.push(`/profile/${item.id}`)
  } else if (type === 'course') {
    if (authStore.isAdmin) {
      router.push(`/admin/courses`)
    } else if (authStore.isFaculty) {
      router.push(`/faculty/courses`)
    } else {
      router.push(`/student/courses`)
    }
  }
}

const handleQuickAction = (action) => {
  showQuickAdd.value = false
  toast.add({
    severity: 'success',
    summary: action.label,
    detail: `Opening ${action.label} form...`,
    life: 3000
  })
}

const handleLogout = async () => {
  try {
    await authStore.logout()
    toast.add({
      severity: 'success',
      summary: 'Logged Out',
      detail: 'You have been successfully logged out',
      life: 2000
    })
    setTimeout(() => {
      router.push('/login')
    }, 500)
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Logout Failed',
      detail: 'An error occurred during logout',
      life: 3000
    })
  }
}

// Lifecycle
onMounted(async () => {
  // Initialize dark mode from local storage
  const savedDarkMode = localStorage.getItem('darkMode')
  if (savedDarkMode === 'true') {
    darkMode.value = true
    document.documentElement.classList.add('dark-mode')
  }

  // Initialize sidebar state for mobile
  if (window.innerWidth < 768) {
    sidebarCollapsed.value = true
  }

  // Handle window resize for responsive behavior
  const handleResize = () => {
    isMobile.value = window.innerWidth < 768
    if (isMobile.value) {
      sidebarCollapsed.value = true
    }
  }
  window.addEventListener('resize', handleResize)

  // Close dropdowns on outside click
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.notification-btn') && !e.target.closest('.notification-panel')) {
      showNotifications.value = false
    }
    if (!e.target.closest('.user-menu-btn') && !e.target.closest('.user-menu-panel')) {
      showUserMenu.value = false
    }
    if (!e.target.closest('.search-container') && !e.target.closest('.search-results-panel')) {
      showSearchResults.value = false
    }
  })

  // Initialize notifications with token for WebSocket
  const userId = authStore.userId
  const token = authStore.token
  if (userId) {
    await notificationStore.initialize(userId, token)

    // Refresh notification count periodically (every 30 seconds)
    notificationRefreshInterval = setInterval(() => {
      notificationStore.loadUnreadCount(userId)
    }, 30000)
  }
})

// Cleanup on unmount
onUnmounted(() => {
  if (notificationRefreshInterval) {
    clearInterval(notificationRefreshInterval)
  }
  notificationStore.unsubscribeFromRealTime()
})

// Watch dark mode changes
watch(darkMode, (newValue) => {
  localStorage.setItem('darkMode', newValue)
})
</script>

<style scoped>
/* Custom scrollbar for sidebar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* Speed Dial Custom Styles */
:deep(.speeddial-custom .p-speeddial-button) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 20px rgba(99, 102, 241, 0.4);
}

:deep(.speeddial-custom .p-speeddial-button:hover) {
  transform: scale(1.1);
}

/* Notification Badge Animation */
.notification-btn {
  position: relative;
}

.notification-btn::after {
  content: '';
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(239, 68, 68, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
  }
}

/* Transition Animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.3s;
}

.slide-left-enter-from {
  transform: translateX(-10px);
  opacity: 0;
}

.slide-left-leave-to {
  transform: translateX(-10px);
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: all 0.2s;
}

.scale-enter-from,
.scale-leave-to {
  transform: scale(0.95);
  opacity: 0;
}

.page-enter-active,
.page-leave-active {
  transition: all 0.3s;
}

.page-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Animation for slide in left */
.animate-slide-in-left {
  animation: slideInLeft 0.3s ease-out forwards;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>