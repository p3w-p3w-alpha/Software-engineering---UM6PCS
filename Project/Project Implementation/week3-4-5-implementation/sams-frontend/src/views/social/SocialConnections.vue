<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900">My Connections</h1>
        <p class="mt-2 text-sm text-gray-600">
          Build your academic network and stay connected with classmates and faculty
        </p>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 p-3 bg-blue-100 rounded-lg">
              <svg class="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm text-gray-600">Total Connections</p>
              <p class="text-2xl font-semibold text-gray-900">{{ connections.length }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 p-3 bg-yellow-100 rounded-lg">
              <svg class="h-6 w-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm text-gray-600">Pending Requests</p>
              <p class="text-2xl font-semibold text-gray-900">{{ pendingRequests.length }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 p-3 bg-green-100 rounded-lg">
              <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm text-gray-600">Network Growth</p>
              <p class="text-2xl font-semibold text-gray-900">+{{ recentConnectionsCount }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Tabs -->
      <div class="mb-6">
        <div class="border-b border-gray-200">
          <nav class="-mb-px flex space-x-8">
            <button
              @click="activeTab = 'connections'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'connections'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              My Connections ({{ connections.length }})
            </button>
            <button
              @click="activeTab = 'requests'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'requests'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              Requests
              <span v-if="pendingRequests.length > 0" class="ml-2 inline-block px-2 py-0.5 text-xs bg-yellow-100 text-yellow-800 rounded-full">
                {{ pendingRequests.length }}
              </span>
            </button>
            <button
              @click="activeTab = 'discover'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'discover'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              Discover People
            </button>
          </nav>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- My Connections Tab -->
      <div v-else-if="activeTab === 'connections'">
        <!-- Search -->
        <div class="mb-6">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search your connections..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            />
            <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>

        <!-- Connections Grid -->
        <div v-if="filteredConnections.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="connection in filteredConnections"
            :key="connection.id"
            class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow"
          >
            <div class="flex items-start justify-between mb-4">
              <div class="flex items-start space-x-4">
                <div class="relative">
                  <div class="h-12 w-12 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                    {{ getInitials(connection.name) }}
                  </div>
                  <div v-if="connection.online" class="absolute bottom-0 right-0 h-3 w-3 bg-green-400 border-2 border-white rounded-full"></div>
                </div>
                <div class="flex-1 min-w-0">
                  <h3 class="text-sm font-semibold text-gray-900 truncate">{{ connection.name }}</h3>
                  <p class="text-xs text-gray-500 truncate">{{ connection.email }}</p>
                  <span class="inline-block mt-1 px-2 py-0.5 text-xs font-medium rounded-full" :class="getRoleBadge(connection.role)">
                    {{ connection.role }}
                  </span>
                </div>
              </div>
            </div>

            <div class="flex items-center space-x-2">
              <button
                @click="viewProfile(connection.id)"
                class="flex-1 px-3 py-2 text-sm border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                View Profile
              </button>
              <button
                @click="sendMessage(connection.id)"
                class="flex-1 px-3 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Message
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-12">
          <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <h3 class="mt-4 text-lg font-medium text-gray-900">No connections yet</h3>
          <p class="mt-2 text-sm text-gray-600">
            Start building your network by discovering and connecting with people
          </p>
          <button
            @click="activeTab = 'discover'"
            class="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Discover People
          </button>
        </div>
      </div>

      <!-- Requests Tab -->
      <div v-else-if="activeTab === 'requests'">
        <div v-if="pendingRequests.length > 0" class="space-y-4">
          <div
            v-for="request in pendingRequests"
            :key="request.id"
            class="bg-white rounded-lg shadow-sm border border-gray-200 p-6"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-4">
                <div class="h-12 w-12 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                  {{ getInitials(request.senderName) }}
                </div>
                <div>
                  <h3 class="text-sm font-semibold text-gray-900">{{ request.senderName }}</h3>
                  <p class="text-xs text-gray-500">{{ request.senderEmail }}</p>
                  <p class="text-xs text-gray-400 mt-1">{{ formatRelativeTime(request.createdAt) }}</p>
                </div>
              </div>

              <div class="flex items-center space-x-3">
                <button
                  @click="acceptRequest(request.id)"
                  class="px-4 py-2 bg-green-600 text-white text-sm rounded-lg hover:bg-green-700 transition-colors"
                >
                  Accept
                </button>
                <button
                  @click="declineRequest(request.id)"
                  class="px-4 py-2 border border-gray-300 text-gray-700 text-sm rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Decline
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-12">
          <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <h3 class="mt-4 text-lg font-medium text-gray-900">No pending requests</h3>
          <p class="mt-2 text-sm text-gray-600">
            You don't have any connection requests at the moment
          </p>
        </div>
      </div>

      <!-- Discover Tab -->
      <div v-else-if="activeTab === 'discover'">
        <!-- Search -->
        <div class="mb-6">
          <div class="relative">
            <input
              v-model="discoverSearchQuery"
              type="text"
              placeholder="Search for people to connect..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            />
            <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>

        <!-- Suggested People -->
        <div v-if="suggestedPeople.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="person in suggestedPeople"
            :key="person.id"
            class="bg-white rounded-lg shadow-sm border border-gray-200 p-6"
          >
            <div class="text-center mb-4">
              <div class="h-16 w-16 mx-auto rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xl font-semibold">
                {{ getInitials(person.name) }}
              </div>
              <h3 class="mt-3 text-sm font-semibold text-gray-900">{{ person.name }}</h3>
              <p class="text-xs text-gray-500">{{ person.email }}</p>
              <span class="inline-block mt-2 px-2 py-0.5 text-xs font-medium rounded-full" :class="getRoleBadge(person.role)">
                {{ person.role }}
              </span>
              <p v-if="person.mutualConnections > 0" class="text-xs text-gray-600 mt-2">
                {{ person.mutualConnections }} mutual connections
              </p>
            </div>

            <button
              @click="sendConnectionRequest(person.id)"
              :disabled="person.requestSent"
              class="w-full px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ person.requestSent ? 'Request Sent' : 'Connect' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const activeTab = ref('connections')
const searchQuery = ref('')
const discoverSearchQuery = ref('')

const connections = ref([])
const pendingRequests = ref([])
const suggestedPeople = ref([])

const filteredConnections = computed(() => {
  if (!searchQuery.value) return connections.value
  const query = searchQuery.value.toLowerCase()
  return connections.value.filter(c =>
    c.name.toLowerCase().includes(query) ||
    c.email.toLowerCase().includes(query)
  )
})

const recentConnectionsCount = computed(() => {
  const thirtyDaysAgo = new Date()
  thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)
  return connections.value.filter(c => new Date(c.connectedAt) > thirtyDaysAgo).length
})

onMounted(async () => {
  await loadConnections()
})

async function loadConnections() {
  try {
    loading.value = true

    // Load connections
    const connectionsResponse = await api.getUserConnections(authStore.userId)
    connections.value = connectionsResponse.data || []

    // Load pending requests (mock for now)
    pendingRequests.value = []

    // Load suggested people
    const usersResponse = await api.getAllUsers()
    const allUsers = usersResponse.data || []

    const connectedIds = new Set(connections.value.map(c => c.id))
    connectedIds.add(authStore.userId)

    suggestedPeople.value = allUsers
      .filter(u => !connectedIds.has(u.id))
      .slice(0, 12)
      .map(u => ({
        ...u,
        requestSent: false,
        mutualConnections: 0
      }))
  } catch (error) {
    console.error('Error loading connections:', error)
    alert('Failed to load connections. Please try again.')
  } finally {
    loading.value = false
  }
}

async function sendConnectionRequest(userId) {
  try {
    // Mock API call - implement when backend ready
    const person = suggestedPeople.value.find(p => p.id === userId)
    if (person) {
      person.requestSent = true
    }
    alert('Connection request sent!')
  } catch (error) {
    console.error('Error sending connection request:', error)
    alert('Failed to send connection request.')
  }
}

async function acceptRequest(requestId) {
  try {
    // Mock API call
    pendingRequests.value = pendingRequests.value.filter(r => r.id !== requestId)
    alert('Connection request accepted!')
    await loadConnections()
  } catch (error) {
    console.error('Error accepting request:', error)
  }
}

async function declineRequest(requestId) {
  try {
    pendingRequests.value = pendingRequests.value.filter(r => r.id !== requestId)
    alert('Connection request declined.')
  } catch (error) {
    console.error('Error declining request:', error)
  }
}

function viewProfile(userId) {
  router.push(`/profile/${userId}`)
}

function sendMessage(userId) {
  router.push(`/messages/${userId}`)
}

function getInitials(name) {
  if (!name) return '?'
  const parts = name.split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return name.substring(0, 2).toUpperCase()
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

function formatRelativeTime(dateString) {
  if (!dateString) return 'recently'
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
