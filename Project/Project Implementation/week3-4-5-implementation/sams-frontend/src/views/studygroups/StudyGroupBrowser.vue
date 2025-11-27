<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Study Groups</h1>
            <p class="mt-2 text-sm text-gray-600">
              Find and join study groups to collaborate with your classmates
            </p>
          </div>

          <button
            @click="showCreateModal = true"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center"
          >
            <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Create Study Group
          </button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="mb-6">
        <div class="border-b border-gray-200">
          <nav class="-mb-px flex space-x-8">
            <button
              @click="activeTab = 'browse'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'browse'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              Browse All Groups
            </button>
            <button
              @click="activeTab = 'mygroups'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'mygroups'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              My Groups
              <span
                v-if="myGroups.length > 0"
                class="ml-2 inline-block px-2 py-0.5 text-xs bg-blue-100 text-blue-600 rounded-full"
              >
                {{ myGroups.length }}
              </span>
            </button>
          </nav>
        </div>
      </div>

      <!-- Search and Filters -->
      <div class="mb-6 bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <!-- Search -->
          <div class="md:col-span-2">
            <div class="relative">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search study groups..."
                class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
              />
              <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- Course Filter -->
          <div>
            <select
              v-model="selectedCourse"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">All Courses</option>
              <option v-for="course in courses" :key="course.id" :value="course.id">
                {{ course.code }} - {{ course.name }}
              </option>
            </select>
          </div>

          <!-- Privacy Filter -->
          <div>
            <select
              v-model="privacyFilter"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">All Types</option>
              <option value="PUBLIC">Public</option>
              <option value="PRIVATE">Private</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Browse Tab -->
      <div v-else-if="activeTab === 'browse'">
        <!-- Study Groups Grid -->
        <div v-if="filteredGroups.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="group in filteredGroups"
            :key="group.id"
            class="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
            @click="viewGroup(group.id)"
          >
            <!-- Group Header -->
            <div class="p-6 border-b border-gray-200">
              <div class="flex items-start justify-between mb-3">
                <div class="flex-1">
                  <h3 class="text-lg font-semibold text-gray-900 line-clamp-1">
                    {{ group.name }}
                  </h3>
                  <p class="text-sm text-gray-600 mt-1">
                    {{ group.courseCode }}
                  </p>
                </div>

                <span
                  class="inline-block px-2 py-1 text-xs font-medium rounded-full"
                  :class="group.isPrivate ? 'bg-purple-100 text-purple-800' : 'bg-green-100 text-green-800'"
                >
                  {{ group.isPrivate ? 'Private' : 'Public' }}
                </span>
              </div>

              <p class="text-sm text-gray-600 line-clamp-2">
                {{ group.description || 'No description provided' }}
              </p>
            </div>

            <!-- Group Stats -->
            <div class="px-6 py-4 bg-gray-50">
              <div class="flex items-center justify-between text-sm">
                <div class="flex items-center text-gray-600">
                  <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  {{ group.memberCount }} / {{ group.maxMembers }} members
                </div>

                <div class="flex items-center">
                  <!-- Member Avatars -->
                  <div class="flex -space-x-2">
                    <div
                      v-for="(member, index) in group.recentMembers.slice(0, 3)"
                      :key="index"
                      class="h-8 w-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xs font-semibold border-2 border-white"
                      :title="member.name"
                    >
                      {{ getInitials(member.name) }}
                    </div>
                  </div>
                </div>
              </div>

              <div class="mt-3 flex items-center justify-between">
                <span class="text-xs text-gray-500">
                  Created {{ formatRelativeTime(group.createdAt) }}
                </span>

                <button
                  v-if="!group.isMember"
                  @click.stop="joinGroup(group.id)"
                  :disabled="group.memberCount >= group.maxMembers"
                  class="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {{ group.memberCount >= group.maxMembers ? 'Full' : 'Join' }}
                </button>

                <span
                  v-else
                  class="px-3 py-1 text-sm bg-green-100 text-green-800 rounded font-medium"
                >
                  Joined
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-12">
          <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <h3 class="mt-4 text-lg font-medium text-gray-900">No study groups found</h3>
          <p class="mt-2 text-sm text-gray-600">
            Try adjusting your filters or create a new study group
          </p>
          <button
            @click="showCreateModal = true"
            class="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Create Study Group
          </button>
        </div>
      </div>

      <!-- My Groups Tab -->
      <div v-else-if="activeTab === 'mygroups'">
        <div v-if="myGroups.length > 0" class="space-y-4">
          <div
            v-for="group in myGroups"
            :key="group.id"
            class="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
            @click="viewGroup(group.id)"
          >
            <div class="p-6">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center space-x-3 mb-2">
                    <h3 class="text-xl font-semibold text-gray-900">
                      {{ group.name }}
                    </h3>
                    <span
                      v-if="group.isCreator"
                      class="inline-block px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full"
                    >
                      Owner
                    </span>
                    <span
                      class="inline-block px-2 py-1 text-xs font-medium rounded-full"
                      :class="group.isPrivate ? 'bg-purple-100 text-purple-800' : 'bg-green-100 text-green-800'"
                    >
                      {{ group.isPrivate ? 'Private' : 'Public' }}
                    </span>
                  </div>

                  <p class="text-sm text-gray-600 mb-3">{{ group.courseCode }}</p>
                  <p class="text-sm text-gray-700">{{ group.description }}</p>

                  <div class="mt-4 flex items-center space-x-6 text-sm text-gray-600">
                    <div class="flex items-center">
                      <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                      </svg>
                      {{ group.memberCount }} members
                    </div>

                    <div class="flex items-center">
                      <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                      </svg>
                      {{ group.messageCount || 0 }} messages
                    </div>

                    <div class="flex items-center">
                      <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      Active {{ formatRelativeTime(group.lastActivity || group.createdAt) }}
                    </div>
                  </div>
                </div>

                <button
                  @click.stop="leaveGroup(group.id)"
                  class="px-4 py-2 border border-red-300 text-red-600 rounded-lg hover:bg-red-50 transition-colors"
                >
                  Leave Group
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State for My Groups -->
        <div v-else class="text-center py-12">
          <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <h3 class="mt-4 text-lg font-medium text-gray-900">You haven't joined any study groups yet</h3>
          <p class="mt-2 text-sm text-gray-600">
            Browse available groups or create your own to start collaborating
          </p>
          <div class="mt-6 flex justify-center space-x-4">
            <button
              @click="activeTab = 'browse'"
              class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Browse Groups
            </button>
            <button
              @click="showCreateModal = true"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Create Study Group
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Study Group Modal -->
    <CreateStudyGroup
      v-model="showCreateModal"
      @group-created="handleGroupCreated"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import CreateStudyGroup from './CreateStudyGroup.vue'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const activeTab = ref('browse')
const searchQuery = ref('')
const selectedCourse = ref('')
const privacyFilter = ref('')
const showCreateModal = ref(false)

const allGroups = ref([])
const myGroups = ref([])
const courses = ref([])

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    loading.value = true

    // Load all study groups
    const groupsResponse = await api.getAllStudyGroups()
    allGroups.value = groupsResponse.data || []

    // Load user's groups
    const myGroupsResponse = await api.getUserStudyGroups(authStore.userId)
    myGroups.value = myGroupsResponse.data || []

    // Mark which groups the user is a member of
    const myGroupIds = new Set(myGroups.value.map(g => g.id))
    allGroups.value = allGroups.value.map(group => ({
      ...group,
      isMember: myGroupIds.has(group.id)
    }))

    // Load user's courses for filter
    const enrollmentsResponse = await api.getStudentEnrollments(authStore.userId)
    const enrollments = enrollmentsResponse.data || []

    // Load course details
    const coursePromises = enrollments.map(e => api.getCourseById(e.courseId))
    const courseResponses = await Promise.all(coursePromises)
    courses.value = courseResponses.map(r => r.data)
  } catch (error) {
    console.error('Error loading study groups:', error)
    alert('Failed to load study groups. Please try again.')
  } finally {
    loading.value = false
  }
}

const filteredGroups = computed(() => {
  let groups = allGroups.value

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    groups = groups.filter(g =>
      g.name.toLowerCase().includes(query) ||
      g.description?.toLowerCase().includes(query) ||
      g.courseCode?.toLowerCase().includes(query)
    )
  }

  // Course filter
  if (selectedCourse.value) {
    groups = groups.filter(g => g.courseId === selectedCourse.value)
  }

  // Privacy filter
  if (privacyFilter.value) {
    const isPrivate = privacyFilter.value === 'PRIVATE'
    groups = groups.filter(g => g.isPrivate === isPrivate)
  }

  return groups
})

async function joinGroup(groupId) {
  try {
    await api.joinStudyGroup(groupId, authStore.userId)

    // Update local state
    const group = allGroups.value.find(g => g.id === groupId)
    if (group) {
      group.isMember = true
      group.memberCount++
      myGroups.value.push(group)
    }

    alert('Successfully joined the study group!')
  } catch (error) {
    console.error('Error joining group:', error)
    alert(error.response?.data?.message || 'Failed to join group. Please try again.')
  }
}

async function leaveGroup(groupId) {
  if (!confirm('Are you sure you want to leave this study group?')) return

  try {
    await api.leaveStudyGroup(groupId, authStore.userId)

    // Update local state
    const group = allGroups.value.find(g => g.id === groupId)
    if (group) {
      group.isMember = false
      group.memberCount--
    }

    myGroups.value = myGroups.value.filter(g => g.id !== groupId)

    alert('Successfully left the study group.')
  } catch (error) {
    console.error('Error leaving group:', error)
    alert('Failed to leave group. Please try again.')
  }
}

function viewGroup(groupId) {
  router.push(`/studygroups/${groupId}`)
}

function handleGroupCreated(newGroup) {
  // Add new group to lists
  allGroups.value.unshift(newGroup)
  myGroups.value.unshift(newGroup)

  // Switch to my groups tab
  activeTab.value = 'mygroups'

  // Navigate to the new group
  router.push(`/studygroups/${newGroup.id}`)
}

function getInitials(name) {
  if (!name) return '?'
  const parts = name.split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return name.substring(0, 2).toUpperCase()
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
  if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 604800)}w ago`

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>
