<template>
  <div class="relative" ref="container">
    <!-- Notification Bell Button -->
    <button
      @click="togglePanel"
      class="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full transition-colors"
    >
      <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
      </svg>

      <!-- Unread Badge -->
      <span
        v-if="unreadCount > 0"
        class="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full"
      >
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <!-- Notification Panel -->
    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="isOpen"
        class="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50"
      >
        <!-- Header -->
        <div class="px-4 py-3 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Notifications</h3>
            <div class="flex items-center space-x-2">
              <button
                v-if="unreadCount > 0"
                @click="markAllAsRead"
                class="text-sm text-blue-600 hover:text-blue-700"
              >
                Mark all read
              </button>
              <button
                @click="togglePanel"
                class="text-gray-400 hover:text-gray-600"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Filter Tabs -->
          <div class="mt-3 flex space-x-2">
            <button
              @click="filter = 'all'"
              :class="filter === 'all' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'"
              class="px-3 py-1 text-sm rounded-full transition-colors"
            >
              All
            </button>
            <button
              @click="filter = 'unread'"
              :class="filter === 'unread' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'"
              class="px-3 py-1 text-sm rounded-full transition-colors"
            >
              Unread ({{ unreadCount }})
            </button>
          </div>
        </div>

        <!-- Notifications List -->
        <div class="max-h-96 overflow-y-auto">
          <div v-if="filteredNotifications.length > 0" class="divide-y divide-gray-200">
            <div
              v-for="notification in filteredNotifications"
              :key="notification.id"
              @click="handleNotificationClick(notification)"
              class="px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors"
              :class="!notification.read ? 'bg-blue-50' : ''"
            >
              <div class="flex items-start space-x-3">
                <!-- Icon -->
                <div
                  class="flex-shrink-0 p-2 rounded-full"
                  :class="getNotificationIconClass(notification.type)"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      :d="getNotificationIconPath(notification.type)"
                    />
                  </svg>
                </div>

                <!-- Content -->
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900">{{ notification.title }}</p>
                  <p class="text-sm text-gray-600 mt-1">{{ notification.message }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ formatRelativeTime(notification.createdAt) }}</p>
                </div>

                <!-- Unread Indicator -->
                <div v-if="!notification.read" class="flex-shrink-0">
                  <div class="h-2 w-2 bg-blue-600 rounded-full"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="px-4 py-12 text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            <p class="mt-2 text-sm text-gray-600">No notifications</p>
          </div>
        </div>

        <!-- Footer -->
        <div class="px-4 py-3 border-t border-gray-200 bg-gray-50">
          <button
            @click="viewAllNotifications"
            class="text-sm text-blue-600 hover:text-blue-700 font-medium"
          >
            View all notifications
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'
import websocketService from '../services/websocket'

const router = useRouter()
const authStore = useAuthStore()

const isOpen = ref(false)
const filter = ref('all')
const notifications = ref([])
const container = ref(null)

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const filteredNotifications = computed(() => {
  if (filter.value === 'unread') {
    return notifications.value.filter(n => !n.read)
  }
  return notifications.value
})

onMounted(async () => {
  await loadNotifications()
  subscribeToNotifications()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

async function loadNotifications() {
  try {
    const response = await api.getUserNotifications(authStore.userId, 0, 20)
    notifications.value = response.data || []
  } catch (error) {
    console.error('Error loading notifications:', error)
  }
}

function subscribeToNotifications() {
  websocketService.subscribeToNotifications(authStore.userId, (notification) => {
    notifications.value.unshift(notification)

    // Show browser notification if permitted
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.message,
        icon: '/logo.png'
      })
    }
  })
}

function togglePanel() {
  isOpen.value = !isOpen.value
}

function handleClickOutside(event) {
  if (container.value && !container.value.contains(event.target)) {
    isOpen.value = false
  }
}

async function handleNotificationClick(notification) {
  // Mark as read
  if (!notification.read) {
    try {
      await api.markNotificationAsRead(notification.id)
      notification.read = true
    } catch (error) {
      console.error('Error marking notification as read:', error)
    }
  }

  // Navigate if has link
  if (notification.link) {
    router.push(notification.link)
    isOpen.value = false
  }
}

async function markAllAsRead() {
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

function viewAllNotifications() {
  router.push('/notifications')
  isOpen.value = false
}

function getNotificationIconClass(type) {
  const classes = {
    'assignment': 'bg-blue-100 text-blue-600',
    'grade': 'bg-green-100 text-green-600',
    'message': 'bg-purple-100 text-purple-600',
    'payment': 'bg-yellow-100 text-yellow-600',
    'announcement': 'bg-orange-100 text-orange-600',
    'default': 'bg-gray-100 text-gray-600'
  }
  return classes[type] || classes['default']
}

function getNotificationIconPath(type) {
  const paths = {
    'assignment': 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
    'grade': 'M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z',
    'message': 'M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z',
    'payment': 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    'announcement': 'M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z'
  }
  return paths[type] || paths['announcement']
}

function formatRelativeTime(dateString) {
  if (!dateString) return 'just now'

  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return 'just now'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>
