<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <!-- Toast Notification -->
    <div v-if="showToast" class="fixed top-4 right-4 z-50 max-w-sm">
      <div :class="[
        'rounded-lg px-4 py-3 shadow-lg',
        toastType === 'success' ? 'bg-green-100 border border-green-400 text-green-700' : 'bg-red-100 border border-red-400 text-red-700'
      ]">
        <div class="flex items-center justify-between">
          <span>{{ toastMessage }}</span>
          <button @click="showToast = false" class="ml-4">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Profile Content -->
      <div v-else class="space-y-6">
        <!-- Profile Header -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <!-- Cover Image -->
          <div class="h-32 bg-gradient-to-r from-blue-400 to-indigo-500"></div>

          <!-- Profile Info -->
          <div class="px-6 pb-6">
            <div class="flex items-end justify-between -mt-16 mb-4">
              <div class="flex items-end space-x-4">
                <!-- Avatar -->
                <div class="relative">
                  <div class="h-32 w-32 rounded-full bg-white p-2 shadow-lg">
                    <div class="h-full w-full rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-3xl font-bold">
                      {{ getInitials(user.firstName, user.lastName) }}
                    </div>
                  </div>
                  <div v-if="user.online" class="absolute bottom-2 right-2 h-6 w-6 bg-green-400 border-4 border-white rounded-full"></div>
                </div>

                <!-- Name & Role -->
                <div class="pb-2">
                  <h1 class="text-2xl font-bold text-gray-900">
                    {{ user.firstName }} {{ user.lastName }}
                  </h1>
                  <p class="text-gray-600">@{{ user.username }}</p>
                  <span class="inline-block mt-2 px-3 py-1 text-sm font-medium rounded-full" :class="getRoleBadge(user.role)">
                    {{ user.role }}
                  </span>
                </div>
              </div>

              <!-- Action Buttons -->
              <div v-if="!isOwnProfile" class="flex items-center space-x-3 pb-2">
                <button
                  v-if="!isConnected"
                  @click="sendConnectionRequest"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                  Connect
                </button>
                <button
                  @click="sendMessage"
                  class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Message
                </button>
              </div>
            </div>

            <!-- Bio -->
            <div v-if="user.bio" class="mt-4">
              <p class="text-gray-700">{{ user.bio }}</p>
            </div>

            <!-- Info Grid -->
            <div class="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4">
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <p class="text-2xl font-bold text-gray-900">{{ stats.connections }}</p>
                <p class="text-sm text-gray-600">Connections</p>
              </div>
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <p class="text-2xl font-bold text-gray-900">{{ stats.studyGroups }}</p>
                <p class="text-sm text-gray-600">Study Groups</p>
              </div>
              <div v-if="user.role === 'STUDENT'" class="text-center p-4 bg-gray-50 rounded-lg">
                <p class="text-2xl font-bold text-blue-600">{{ stats.gpa }}</p>
                <p class="text-sm text-gray-600">GPA</p>
              </div>
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <p class="text-2xl font-bold text-gray-900">{{ stats.courses }}</p>
                <p class="text-sm text-gray-600">{{ user.role === 'FACULTY' ? 'Teaching' : 'Enrolled' }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Tabs -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
          <div class="border-b border-gray-200 px-6">
            <nav class="-mb-px flex space-x-8">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="activeTab = tab.id"
                :class="[
                  'py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                {{ tab.name }}
              </button>
            </nav>
          </div>

          <!-- Tab Content -->
          <div class="p-6">
            <!-- About Tab -->
            <div v-if="activeTab === 'about'">
              <dl class="space-y-4">
                <div>
                  <dt class="text-sm font-medium text-gray-600">Email</dt>
                  <dd class="mt-1 text-sm text-gray-900">{{ user.email }}</dd>
                </div>
                <div v-if="user.department">
                  <dt class="text-sm font-medium text-gray-600">Department</dt>
                  <dd class="mt-1 text-sm text-gray-900">{{ user.department }}</dd>
                </div>
                <div v-if="user.joinedDate">
                  <dt class="text-sm font-medium text-gray-600">Member Since</dt>
                  <dd class="mt-1 text-sm text-gray-900">{{ formatDate(user.joinedDate) }}</dd>
                </div>
              </dl>
            </div>

            <!-- Connections Tab -->
            <div v-else-if="activeTab === 'connections'">
              <div v-if="connections.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div
                  v-for="connection in connections"
                  :key="connection.id"
                  class="flex items-center space-x-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50"
                >
                  <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                    {{ getInitials(connection.firstName, connection.lastName) }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">
                      {{ connection.firstName }} {{ connection.lastName }}
                    </p>
                    <p class="text-xs text-gray-500 truncate">{{ connection.role }}</p>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-gray-500">
                No connections to display
              </div>
            </div>

            <!-- Activity Tab -->
            <div v-else-if="activeTab === 'activity'">
              <div v-if="activities.length > 0" class="space-y-4">
                <div
                  v-for="activity in activities"
                  :key="activity.id"
                  class="flex items-start space-x-3 p-4 border border-gray-200 rounded-lg"
                >
                  <div class="flex-shrink-0 h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                    <svg class="h-5 w-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                    </svg>
                  </div>
                  <div class="flex-1">
                    <p class="text-sm text-gray-900">{{ activity.description }}</p>
                    <p class="text-xs text-gray-500 mt-1">{{ formatRelativeTime(activity.timestamp) }}</p>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-gray-500">
                No recent activity
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const activeTab = ref('about')
const user = ref({})
const connections = ref([])
const activities = ref([])

// Toast notification state
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

function showNotification(message, type = 'success') {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 5000)
}

const tabs = [
  { id: 'about', name: 'About' },
  { id: 'connections', name: 'Connections' },
  { id: 'activity', name: 'Activity' }
]

const isOwnProfile = computed(() => {
  return user.value.id === authStore.userId
})

const isConnected = computed(() => {
  return false // Check connection status
})

const stats = computed(() => ({
  connections: connections.value.length,
  studyGroups: 0,
  gpa: user.value.gpa || '0.00',
  courses: user.value.coursesCount || 0
}))

onMounted(async () => {
  await loadProfile()
})

async function loadProfile() {
  try {
    loading.value = true
    const userId = route.params.id

    const response = await api.getUserById(userId)
    user.value = response.data

    // Load connections
    const connectionsResponse = await api.getUserConnections(userId)
    connections.value = (connectionsResponse.data || []).slice(0, 10)

    // User-specific activity feed not yet implemented in backend
    // When backend adds /api/users/{userId}/activities endpoint, integrate here
    activities.value = []
  } catch (error) {
    console.error('Error loading profile:', error)
    showNotification('Failed to load profile.', 'error')
  } finally {
    loading.value = false
  }
}

function sendConnectionRequest() {
  showNotification('Connection request sent!', 'success')
}

function sendMessage() {
  router.push(`/messages/${user.value.id}`)
}

function getInitials(firstName, lastName) {
  if (!firstName) return '?'
  const first = firstName[0] || ''
  const last = lastName?.[0] || ''
  return (first + last).toUpperCase()
}

function getRoleBadge(role) {
  const badges = {
    'STUDENT': 'bg-blue-100 text-blue-800',
    'FACULTY': 'bg-purple-100 text-purple-800',
    'ADMIN': 'bg-orange-100 text-orange-800',
    'SUPER_ADMIN': 'bg-red-100 text-red-800'
  }
  return badges[role] || 'bg-gray-100 text-gray-800'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
}

function formatRelativeTime(dateString) {
  if (!dateString) return 'recently'
  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return 'just now'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>
