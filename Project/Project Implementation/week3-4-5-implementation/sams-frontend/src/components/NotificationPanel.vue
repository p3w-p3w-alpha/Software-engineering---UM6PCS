<template>
  <transition
    enter-active-class="transition ease-out duration-200"
    enter-from-class="opacity-0 translate-x-full"
    enter-to-class="opacity-100 translate-x-0"
    leave-active-class="transition ease-in duration-150"
    leave-from-class="opacity-100 translate-x-0"
    leave-to-class="opacity-0 translate-x-full"
  >
    <div
      v-if="isOpen"
      class="fixed right-0 top-0 h-full w-96 bg-white shadow-2xl z-50 flex flex-col"
    >
      <!-- Header -->
      <div class="bg-blue-600 px-6 py-4 flex items-center justify-between">
        <h2 class="text-lg font-semibold text-white">Notifications</h2>
        <button
          @click="$emit('close')"
          class="text-white hover:text-blue-200 transition-colors"
        >
          <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>

      <!-- Tabs -->
      <div class="flex border-b border-gray-200">
        <button
          @click="activeTab = 'all'"
          class="flex-1 px-4 py-3 text-sm font-medium transition-colors"
          :class="[
            activeTab === 'all'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          All
        </button>
        <button
          @click="activeTab = 'unread'"
          class="flex-1 px-4 py-3 text-sm font-medium transition-colors relative"
          :class="[
            activeTab === 'unread'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          Unread
          <span
            v-if="unreadCount > 0"
            class="ml-2 inline-flex items-center justify-center px-2 py-0.5 text-xs font-bold leading-none text-white bg-red-600 rounded-full"
          >
            {{ unreadCount }}
          </span>
        </button>
      </div>

      <!-- Notifications List -->
      <div class="flex-1 overflow-y-auto">
        <div v-if="loading" class="flex justify-center items-center h-32">
          <LoadingSpinner />
        </div>

        <div v-else-if="filteredNotifications.length === 0" class="p-6 text-center">
          <svg
            class="mx-auto h-12 w-12 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
            />
          </svg>
          <p class="mt-2 text-sm text-gray-500">No notifications</p>
        </div>

        <div v-else class="divide-y divide-gray-200">
          <div
            v-for="notification in filteredNotifications"
            :key="notification.id"
            class="p-4 hover:bg-gray-50 cursor-pointer transition-colors"
            :class="{ 'bg-blue-50': !notification.read }"
            @click="handleNotificationClick(notification)"
          >
            <div class="flex items-start">
              <div
                class="flex-shrink-0 h-10 w-10 rounded-full flex items-center justify-center"
                :class="getTypeColor(notification.type)"
              >
                <component :is="getTypeIcon(notification.type)" class="h-5 w-5 text-white" />
              </div>
              <div class="ml-3 flex-1">
                <p class="text-sm font-medium text-gray-900">
                  {{ notification.title }}
                </p>
                <p class="mt-1 text-sm text-gray-600">
                  {{ notification.message }}
                </p>
                <p class="mt-1 text-xs text-gray-500">
                  {{ formatDate(notification.createdAt) }}
                </p>
              </div>
              <div v-if="!notification.read" class="flex-shrink-0 ml-2">
                <span class="h-2 w-2 bg-blue-600 rounded-full block"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="border-t border-gray-200 p-4">
        <button
          v-if="hasUnread"
          @click="markAllAsRead"
          class="w-full px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
        >
          Mark all as read
        </button>
      </div>
    </div>
  </transition>

  <!-- Backdrop -->
  <transition
    enter-active-class="transition-opacity ease-out duration-200"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="transition-opacity ease-in duration-150"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div
      v-if="isOpen"
      class="fixed inset-0 bg-black bg-opacity-50 z-40"
      @click="$emit('close')"
    ></div>
  </transition>
</template>

<script setup>
import { ref, computed } from 'vue'
import LoadingSpinner from './LoadingSpinner.vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  notifications: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'mark-read', 'mark-all-read', 'notification-click'])

const activeTab = ref('all')

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
    PAYMENT: 'bg-yellow-500',
    ASSIGNMENT: 'bg-purple-500',
    ANNOUNCEMENT: 'bg-indigo-500',
    SYSTEM: 'bg-gray-500'
  }
  return colors[type] || 'bg-gray-500'
}

const getTypeIcon = (type) => {
  // Return a simple SVG for now - in production you'd use proper icons
  return 'svg'
}

const formatDate = (dateString) => {
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
  return date.toLocaleDateString()
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
