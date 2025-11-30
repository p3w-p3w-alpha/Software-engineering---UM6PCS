<template>
  <nav class="bg-gradient-to-r from-blue-600 to-blue-700 shadow-lg" role="navigation" aria-label="Main navigation">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="flex h-16 items-center justify-between">
        <!-- Logo and Title -->
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <h1 class="text-2xl font-bold text-white">SAMS</h1>
          </div>
          <div class="hidden md:block ml-10">
            <div class="flex items-baseline space-x-4">
              <router-link
                v-for="item in navigation"
                :key="item.name"
                :to="item.href"
                class="px-3 py-2 rounded-md text-sm font-medium transition-colors"
                :class="[
                  isCurrentRoute(item.href)
                    ? 'bg-blue-800 text-white'
                    : 'text-blue-100 hover:bg-blue-500 hover:text-white'
                ]"
              >
                {{ item.name }}
              </router-link>
            </div>
          </div>
        </div>

        <!-- Right side: Notifications and User Menu -->
        <div class="flex items-center space-x-4">
          <!-- Notifications Bell -->
          <button
            @click="$emit('toggle-notifications')"
            class="relative p-2 text-blue-100 hover:text-white hover:bg-blue-500 rounded-full transition-colors"
            aria-label="Toggle notifications panel"
            :aria-expanded="false"
          >
            <svg
              class="h-6 w-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
              />
            </svg>
            <span
              v-if="unreadCount > 0"
              class="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full"
            >
              {{ unreadCount > 99 ? '99+' : unreadCount }}
            </span>
          </button>

          <!-- User Menu -->
          <div class="relative">
            <button
              @click="showUserMenu = !showUserMenu"
              class="flex items-center space-x-3 p-2 rounded-md hover:bg-blue-500 transition-colors"
              aria-label="User menu"
              aria-haspopup="true"
              :aria-expanded="showUserMenu"
            >
              <div class="flex items-center justify-center h-8 w-8 rounded-full bg-blue-500 text-white font-bold">
                {{ userInitials }}
              </div>
              <div class="hidden md:block text-left">
                <div class="text-sm font-medium text-white">{{ userName }}</div>
                <div class="text-xs text-blue-100">{{ userRole }}</div>
              </div>
              <svg
                class="h-5 w-5 text-blue-100"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </button>

            <!-- Dropdown Menu -->
            <transition
              enter-active-class="transition ease-out duration-100"
              enter-from-class="transform opacity-0 scale-95"
              enter-to-class="transform opacity-100 scale-100"
              leave-active-class="transition ease-in duration-75"
              leave-from-class="transform opacity-100 scale-100"
              leave-to-class="transform opacity-0 scale-95"
            >
              <div
                v-if="showUserMenu"
                class="absolute right-0 mt-2 w-48 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 z-50"
              >
                <div class="py-1">
                  <button
                    @click="handleLogout"
                    class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  >
                    Sign out
                  </button>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>

    <!-- Mobile menu button -->
    <div class="md:hidden px-4 pb-3">
      <div class="flex flex-col space-y-1">
        <router-link
          v-for="item in navigation"
          :key="item.name"
          :to="item.href"
          class="px-3 py-2 rounded-md text-sm font-medium"
          :class="[
            isCurrentRoute(item.href)
              ? 'bg-blue-800 text-white'
              : 'text-blue-100 hover:bg-blue-500 hover:text-white'
          ]"
        >
          {{ item.name }}
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const props = defineProps({
  navigation: {
    type: Array,
    required: true
  },
  unreadCount: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['toggle-notifications'])

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const showUserMenu = ref(false)

const userName = computed(() => authStore.userName)
const userRole = computed(() => authStore.userRole)
const userInitials = computed(() => {
  const name = authStore.userName || 'U'
  return name.substring(0, 2).toUpperCase()
})

const isCurrentRoute = (href) => {
  return route.path.startsWith(href)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

// Close dropdown when clicking outside
const handleClickOutside = (event) => {
  if (showUserMenu.value && !event.target.closest('button')) {
    showUserMenu.value = false
  }
}

// Properly register and cleanup event listener to avoid memory leaks
onMounted(() => {
  window.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>
