<!--
  NotificationPanel - notification dropdown panel component
  displays list of notifications with filtering and mark as read functionality
  handles real-time updates via websockets - took a while to get working
-->

<template>
  <div class="notification-panel-content" role="dialog" aria-labelledby="notification-panel-title" aria-modal="true">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-indigo-600 to-purple-600 px-4 py-3 flex items-center justify-between">
      <h2 id="notification-panel-title" class="text-lg font-semibold text-white">Notifications</h2>
      <button
        @click="$emit('close')"
        class="text-white hover:text-indigo-200 transition-colors"
        aria-label="Close notifications panel"
      >
        <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Tabs -->
    <div class="flex border-b border-gray-200 bg-gray-50" role="tablist" aria-label="Notification filters">
      <button
        @click="activeTab = 'all'"
        class="flex-1 px-4 py-2 text-sm font-medium transition-colors"
        :class="[
          activeTab === 'all'
            ? 'border-b-2 border-indigo-600 text-indigo-600 bg-white'
            : 'text-gray-500 hover:text-gray-700'
        ]"
        role="tab"
        :aria-selected="activeTab === 'all'"
        aria-controls="notification-list"
      >
        All
      </button>
      <button
        @click="activeTab = 'unread'"
        class="flex-1 px-4 py-2 text-sm font-medium transition-colors relative"
        :class="[
          activeTab === 'unread'
            ? 'border-b-2 border-indigo-600 text-indigo-600 bg-white'
            : 'text-gray-500 hover:text-gray-700'
        ]"
        role="tab"
        :aria-selected="activeTab === 'unread'"
        aria-controls="notification-list"
      >
        Unread
        <span
          v-if="unreadCount > 0"
          class="ml-1 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-bold leading-none text-white bg-red-500 rounded-full"
          :aria-label="`${unreadCount} unread notifications`"
        >
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </span>
      </button>
    </div>

    <!-- Notifications List -->
    <div id="notification-list" class="max-h-80 overflow-y-auto" role="tabpanel" aria-label="Notifications list">
      <div v-if="loading" class="flex justify-center items-center h-32" aria-label="Loading notifications">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>

      <div v-else-if="filteredNotifications.length === 0" class="p-6 text-center">
        <svg class="mx-auto h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        <p class="mt-2 text-sm text-gray-500">{{ activeTab === 'unread' ? 'No unread notifications' : 'No notifications yet' }}</p>
      </div>

      <div v-else class="divide-y divide-gray-100">
        <div
          v-for="notification in filteredNotifications"
          :key="notification.id"
          class="p-3 hover:bg-gray-50 cursor-pointer transition-colors"
          :class="{ 'bg-indigo-50/50': !notification.read }"
          @click="handleNotificationClick(notification)"
        >
          <div class="flex items-start gap-3">
            <!-- Type Icon -->
            <div
              class="flex-shrink-0 h-9 w-9 rounded-full flex items-center justify-center"
              :class="getTypeColor(notification.type)"
            >
              <svg class="h-4 w-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="getTypeIconPath(notification.type)" />
              </svg>
            </div>

            <!-- Content -->
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">
                {{ notification.title }}
              </p>
              <p class="mt-0.5 text-xs text-gray-600 line-clamp-2">
                {{ notification.message }}
              </p>
              <p class="mt-1 text-xs text-gray-400">
                {{ formatDate(notification.createdAt || notification.time) }}
              </p>
            </div>

            <!-- Unread Indicator -->
            <div v-if="!notification.read" class="flex-shrink-0">
              <span class="h-2 w-2 bg-indigo-600 rounded-full block animate-pulse"></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="border-t border-gray-200 p-3 bg-gray-50 flex items-center justify-between">
      <button
        v-if="hasUnread"
        @click="markAllAsRead"
        class="text-xs text-indigo-600 hover:text-indigo-700 font-medium"
      >
        Mark all as read
      </button>
      <span v-else class="text-xs text-gray-400">You're all caught up!</span>
      <router-link
        to="/notifications"
        class="text-xs text-gray-500 hover:text-gray-700"
        @click="$emit('close')"
      >
        View all
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// notification panel props
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false  // controls panel visibility
  },
  notifications: {
    type: Array,
    default: () => []  // list of notification objects
  },
  loading: {
    type: Boolean,
    default: false  // shows loading spinner
  }
})

const emit = defineEmits(['close', 'mark-read', 'mark-all-read', 'notification-click'])

// active tab state - 'all' or 'unread'
const activeTab = ref('all')

// filter notifications based on selected tab
const filteredNotifications = computed(() => {
  if (activeTab.value === 'unread') {
    return props.notifications.filter(n => !n.read)
  }
  return props.notifications
})

const unreadCount = computed(() => {
  return props.notifications.filter(n => !n.read).length
})

const hasUnread = computed(() => unreadCount.value > 0)

const getTypeColor = (type) => {
  const colors = {
    ENROLLMENT: 'bg-blue-500',
    GRADE: 'bg-green-500',
    PAYMENT: 'bg-amber-500',
    ASSIGNMENT: 'bg-purple-500',
    ANNOUNCEMENT: 'bg-indigo-500',
    MESSAGE: 'bg-pink-500',
    CONNECTION: 'bg-teal-500',
    SYSTEM: 'bg-gray-500',
    info: 'bg-blue-500',
    success: 'bg-green-500',
    warning: 'bg-amber-500',
    error: 'bg-red-500'
  }
  return colors[type] || 'bg-gray-500'
}

const getTypeIconPath = (type) => {
  const paths = {
    ENROLLMENT: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253',
    GRADE: 'M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z',
    PAYMENT: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    ASSIGNMENT: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
    ANNOUNCEMENT: 'M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z',
    MESSAGE: 'M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z',
    CONNECTION: 'M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z',
    SYSTEM: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z',
    info: 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    success: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    warning: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z',
    error: 'M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z'
  }
  return paths[type] || paths['info']
}

const formatDate = (dateString) => {
  if (!dateString) return 'Just now'

  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes}m ago`
  if (hours < 24) return `${hours}h ago`
  if (days < 7) return `${days}d ago`
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

const handleNotificationClick = (notification) => {
  emit('notification-click', notification)
  if (!notification.read) {
    emit('mark-read', notification.id)
  }
}

const markAllAsRead = () => {
  emit('mark-all-read')
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
