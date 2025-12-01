<template>
  <header class="topbar">
    <div class="topbar-container">
      <!-- Left Section: Logo & Branding -->
      <div class="topbar-left">
        <router-link to="/" class="logo-link">
          <div class="logo-icon">
            <AcademicCapIcon class="w-6 h-6" />
          </div>
          <span class="logo-text">SAMS</span>
        </router-link>

        <!-- Breadcrumb (optional) -->
        <div class="breadcrumb" v-if="currentPageTitle">
          <ChevronRightIcon class="w-4 h-4 text-gray-500" />
          <span class="breadcrumb-text">{{ currentPageTitle }}</span>
        </div>
      </div>

      <!-- Center Section: Search -->
      <div class="topbar-center">
        <div class="search-wrapper" @click="openSearch">
          <MagnifyingGlassIcon class="search-icon" />
          <span class="search-placeholder">Search...</span>
          <kbd class="search-shortcut">
            <span>⌘</span>K
          </kbd>
        </div>
      </div>

      <!-- Right Section: Actions & User -->
      <div class="topbar-right">
        <!-- Quick Actions -->
        <button class="topbar-action" @click="toggleTheme" title="Toggle Theme">
          <SunIcon v-if="isDarkMode" class="action-icon" />
          <MoonIcon v-else class="action-icon" />
        </button>

        <!-- Notifications -->
        <button class="topbar-action notification-btn" @click="toggleNotifications">
          <BellIcon class="action-icon" />
          <span v-if="unreadCount > 0" class="notification-badge">
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </button>

        <!-- User Menu -->
        <div class="user-menu" ref="userMenuRef">
          <button class="user-button" @click="toggleUserMenu">
            <div class="user-avatar">
              <img v-if="userAvatar" :src="userAvatar" alt="Avatar" />
              <span v-else class="avatar-initials">{{ userInitials }}</span>
            </div>
            <div class="user-info">
              <span class="user-name">{{ userName }}</span>
              <span class="user-role">{{ userRoleDisplay }}</span>
            </div>
            <ChevronDownIcon class="chevron-icon" :class="{ 'rotated': showUserMenu }" />
          </button>

          <!-- Dropdown Menu -->
          <Transition name="dropdown">
            <div v-if="showUserMenu" class="user-dropdown">
              <!-- User Info Header -->
              <div class="dropdown-header">
                <div class="dropdown-avatar">
                  <img v-if="userAvatar" :src="userAvatar" alt="Avatar" />
                  <span v-else class="dropdown-avatar-initials">{{ userInitials }}</span>
                </div>
                <div class="dropdown-user-details">
                  <span class="dropdown-user-name">{{ fullUserName }}</span>
                  <span class="dropdown-user-email">{{ userEmail }}</span>
                </div>
              </div>
              <div class="dropdown-divider"></div>

              <router-link :to="`/profile/${authStore.userId}`" class="dropdown-item" @click="showUserMenu = false">
                <UserCircleIcon class="dropdown-icon" />
                <span>View Profile</span>
              </router-link>
              <router-link to="/connections" class="dropdown-item" @click="showUserMenu = false">
                <UsersIcon class="dropdown-icon" />
                <span>My Connections</span>
              </router-link>
              <router-link to="/messages" class="dropdown-item" @click="showUserMenu = false">
                <ChatBubbleLeftRightIcon class="dropdown-icon" />
                <span>Messages</span>
              </router-link>
              <div class="dropdown-divider"></div>
              <router-link to="/settings" class="dropdown-item" @click="showUserMenu = false">
                <Cog6ToothIcon class="dropdown-icon" />
                <span>Settings</span>
              </router-link>
              <div class="dropdown-divider"></div>
              <button @click="handleLogout" class="dropdown-item logout-item">
                <ArrowRightOnRectangleIcon class="dropdown-icon" />
                <span>Sign Out</span>
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>

    <!-- Notification Panel -->
    <Transition name="slide-down">
      <NotificationPanel
        v-if="showNotifications"
        @close="showNotifications = false"
        class="notification-panel"
      />
    </Transition>

    <!-- Search Modal -->
    <Transition name="fade">
      <div v-if="showSearch" class="search-modal-overlay" @click.self="closeSearch">
        <div class="search-modal">
          <div class="search-modal-input">
            <MagnifyingGlassIcon class="w-5 h-5 text-gray-400" />
            <input
              ref="searchInputRef"
              v-model="searchQuery"
              type="text"
              placeholder="Search for pages, users, courses..."
              class="search-input"
              @keydown.escape="closeSearch"
            />
            <kbd class="search-esc">ESC</kbd>
          </div>
          <div class="search-results" v-if="searchQuery">
            <div class="search-section">
              <span class="search-section-title">Pages</span>
              <router-link
                v-for="result in searchResults"
                :key="result.path"
                :to="result.path"
                class="search-result-item"
                @click="closeSearch"
              >
                <component :is="result.icon" class="w-4 h-4" />
                <span>{{ result.label }}</span>
              </router-link>
            </div>
          </div>
          <div class="search-hint" v-else>
            <span>Start typing to search...</span>
          </div>
        </div>
      </div>
    </Transition>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notifications'
import NotificationPanel from '@/components/NotificationPanel.vue'
import {
  AcademicCapIcon,
  MagnifyingGlassIcon,
  BellIcon,
  SunIcon,
  MoonIcon,
  ChevronRightIcon,
  ChevronDownIcon,
  UserCircleIcon,
  Cog6ToothIcon,
  ArrowRightOnRectangleIcon,
  HomeIcon,
  BookOpenIcon,
  UsersIcon,
  ChartBarIcon,
  ChatBubbleLeftRightIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

// Refs
const userMenuRef = ref(null)
const searchInputRef = ref(null)
const showUserMenu = ref(false)
const showNotifications = ref(false)
const showSearch = ref(false)
const searchQuery = ref('')
const isDarkMode = ref(true)

// Computed
const userName = computed(() => authStore.user?.firstName || authStore.user?.username || 'User')
const fullUserName = computed(() => {
  const first = authStore.user?.firstName || ''
  const last = authStore.user?.lastName || ''
  return first && last ? `${first} ${last}` : first || authStore.user?.username || 'User'
})
const userEmail = computed(() => authStore.user?.email || '')
const userAvatar = computed(() => {
  const picture = authStore.user?.profilePicture
  if (!picture) return null
  if (picture.startsWith('http')) return picture
  return `http://localhost:8080/api/files/download?filePath=${encodeURIComponent(picture)}`
})
const userInitials = computed(() => {
  const first = authStore.user?.firstName?.[0] || ''
  const last = authStore.user?.lastName?.[0] || ''
  return (first + last).toUpperCase() || userName.value[0]?.toUpperCase() || 'U'
})
const userRoleDisplay = computed(() => {
  const role = authStore.userRole
  const roleMap = {
    'SUPER_ADMIN': 'Super Admin',
    'ADMIN': 'Administrator',
    'FACULTY': 'Faculty',
    'STUDENT': 'Student'
  }
  return roleMap[role] || role
})

const unreadCount = computed(() => notificationStore.unreadCount || 0)

const currentPageTitle = computed(() => {
  const path = route.path
  const pathMap = {
    '/admin': 'Dashboard',
    '/admin/users': 'User Management',
    '/admin/courses': 'Courses',
    '/faculty': 'Dashboard',
    '/student': 'Dashboard',
    '/messages': 'Messages',
    '/settings': 'Settings',
    '/profile': 'Profile',
  }
  return pathMap[path] || route.meta?.title || ''
})

// Search results (simple page search)
const searchResults = computed(() => {
  if (!searchQuery.value) return []

  const query = searchQuery.value.toLowerCase()
  const allPages = [
    { path: '/admin', label: 'Admin Dashboard', icon: HomeIcon },
    { path: '/admin/users', label: 'User Management', icon: UsersIcon },
    { path: '/admin/courses', label: 'Course Management', icon: BookOpenIcon },
    { path: '/admin/analytics', label: 'Analytics', icon: ChartBarIcon },
    { path: '/student', label: 'Student Dashboard', icon: HomeIcon },
    { path: '/faculty', label: 'Faculty Dashboard', icon: HomeIcon },
    { path: '/messages', label: 'Messages', icon: BookOpenIcon },
    { path: '/settings', label: 'Settings', icon: Cog6ToothIcon },
    { path: '/profile', label: 'Profile', icon: UserCircleIcon },
  ]

  return allPages.filter(page =>
    page.label.toLowerCase().includes(query)
  ).slice(0, 5)
})

// Methods
const toggleUserMenu = (event) => {
  event.stopPropagation()
  showUserMenu.value = !showUserMenu.value
  showNotifications.value = false
}

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
  showUserMenu.value = false
}

const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark', isDarkMode.value)
}

const openSearch = () => {
  showSearch.value = true
  nextTick(() => {
    searchInputRef.value?.focus()
  })
}

const closeSearch = () => {
  showSearch.value = false
  searchQuery.value = ''
}

const handleLogout = async () => {
  showUserMenu.value = false
  await authStore.logout()
  router.push('/login')
}

// Keyboard shortcut for search
const handleKeydown = (event) => {
  if ((event.metaKey || event.ctrlKey) && event.key === 'k') {
    event.preventDefault()
    openSearch()
  }
}

// Click outside handler
const handleClickOutside = (event) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    showUserMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  z-index: 40;
  background: rgba(10, 10, 15, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.topbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
  max-width: 1800px;
  margin: 0 auto;
}

/* Left Section */
.topbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #00d4ff, #a855f7);
  border-radius: 10px;
  color: white;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #00d4ff, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
}

.breadcrumb-text {
  font-size: 14px;
  color: rgba(248, 250, 252, 0.6);
}

/* Center Section */
.topbar-center {
  flex: 1;
  max-width: 480px;
  margin: 0 40px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: rgba(18, 18, 26, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.search-wrapper:hover {
  border-color: rgba(0, 212, 255, 0.3);
  background: rgba(18, 18, 26, 0.95);
}

.search-icon {
  width: 18px;
  height: 18px;
  color: rgba(248, 250, 252, 0.4);
}

.search-placeholder {
  flex: 1;
  font-size: 14px;
  color: rgba(248, 250, 252, 0.4);
}

.search-shortcut {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 600;
  color: rgba(248, 250, 252, 0.5);
  background: rgba(255, 255, 255, 0.08);
  border-radius: 6px;
}

/* Right Section */
.topbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.topbar-action {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: transparent;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.topbar-action:hover {
  background: rgba(255, 255, 255, 0.08);
}

.action-icon {
  width: 20px;
  height: 20px;
  color: rgba(248, 250, 252, 0.7);
}

.topbar-action:hover .action-icon {
  color: #00d4ff;
}

.notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #ec4899, #ef4444);
  border-radius: 9px;
  box-shadow: 0 0 10px rgba(236, 72, 153, 0.5);
}

/* User Menu */
.user-menu {
  position: relative;
  margin-left: 8px;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px 6px 6px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-button:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(0, 212, 255, 0.3);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(135deg, #00d4ff, #a855f7);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-initials {
  font-size: 12px;
  font-weight: 700;
  color: white;
}

.user-info {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: #f8fafc;
  line-height: 1.2;
}

.user-role {
  font-size: 11px;
  color: rgba(248, 250, 252, 0.5);
}

.chevron-icon {
  width: 16px;
  height: 16px;
  color: rgba(248, 250, 252, 0.5);
  transition: transform 0.2s ease;
}

.chevron-icon.rotated {
  transform: rotate(180deg);
}

/* User Dropdown */
.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 220px;
  padding: 8px;
  background: rgba(18, 18, 26, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  z-index: 9999;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  font-size: 14px;
  color: rgba(248, 250, 252, 0.8);
  text-decoration: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;
  border: none;
  width: 100%;
}

.dropdown-item:hover {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
}

.logout-item:hover {
  background: rgba(236, 72, 153, 0.1);
  color: #ec4899;
}

.dropdown-icon {
  width: 18px;
  height: 18px;
}

.dropdown-divider {
  height: 1px;
  margin: 6px 0;
  background: rgba(255, 255, 255, 0.08);
}

/* Dropdown Header */
.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
}

.dropdown-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  overflow: hidden;
  background: linear-gradient(135deg, #00d4ff, #a855f7);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.dropdown-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dropdown-avatar-initials {
  font-size: 16px;
  font-weight: 700;
  color: white;
}

.dropdown-user-details {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.dropdown-user-name {
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-user-email {
  font-size: 12px;
  color: rgba(248, 250, 252, 0.5);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Search Modal */
.search-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 120px;
  z-index: 100;
}

.search-modal {
  width: 100%;
  max-width: 560px;
  background: rgba(18, 18, 26, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.search-modal-input {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: 16px;
  color: #f8fafc;
}

.search-input::placeholder {
  color: rgba(248, 250, 252, 0.4);
}

.search-esc {
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 600;
  color: rgba(248, 250, 252, 0.5);
  background: rgba(255, 255, 255, 0.08);
  border-radius: 6px;
}

.search-results {
  padding: 12px;
}

.search-section-title {
  display: block;
  padding: 8px 12px;
  font-size: 11px;
  font-weight: 600;
  color: rgba(248, 250, 252, 0.4);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.search-result-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  color: rgba(248, 250, 252, 0.8);
  text-decoration: none;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.search-result-item:hover {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
}

.search-hint {
  padding: 24px;
  text-align: center;
  color: rgba(248, 250, 252, 0.4);
}

/* Notification Panel Positioning */
.notification-panel {
  position: absolute;
  top: 60px;
  right: 24px;
}

/* Transitions */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .topbar-center {
    display: none;
  }

  .user-info {
    display: none;
  }

  .breadcrumb {
    display: none;
  }
}
</style>
