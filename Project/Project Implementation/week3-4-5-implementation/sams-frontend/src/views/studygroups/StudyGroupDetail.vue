<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <LoadingSpinner size="large" />
    </div>

    <!-- Main Content -->
    <div v-else class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-6">
        <button
          @click="$router.back()"
          class="flex items-center text-gray-600 hover:text-gray-900 mb-4"
        >
          <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          Back to Study Groups
        </button>

        <!-- Group Header Card -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center space-x-3 mb-2">
                <h1 class="text-3xl font-bold text-gray-900">{{ groupInfo.name }}</h1>
                <span
                  class="inline-block px-3 py-1 text-sm font-medium rounded-full"
                  :class="groupInfo.isPrivate ? 'bg-purple-100 text-purple-800' : 'bg-green-100 text-green-800'"
                >
                  {{ groupInfo.isPrivate ? 'Private' : 'Public' }}
                </span>
                <span
                  v-if="isGroupOwner"
                  class="inline-block px-3 py-1 text-sm font-medium bg-blue-100 text-blue-800 rounded-full"
                >
                  Owner
                </span>
              </div>

              <p class="text-gray-600 mb-4">{{ groupInfo.courseCode }} • {{ groupInfo.courseName }}</p>
              <p class="text-gray-700">{{ groupInfo.description }}</p>

              <div class="mt-4 flex items-center space-x-6 text-sm text-gray-600">
                <div class="flex items-center">
                  <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  {{ members.length }} / {{ groupInfo.maxMembers }} members
                </div>

                <div class="flex items-center">
                  <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Created {{ formatDate(groupInfo.createdAt) }}
                </div>
              </div>
            </div>

            <div class="flex items-center space-x-3">
              <button
                v-if="isGroupOwner"
                @click="showEditModal = true"
                class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Edit Group
              </button>

              <button
                v-if="!isGroupOwner"
                @click="leaveGroup"
                class="px-4 py-2 border border-red-300 text-red-600 rounded-lg hover:bg-red-50 transition-colors"
              >
                Leave Group
              </button>

              <button
                v-if="isGroupOwner"
                @click="deleteGroup"
                class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              >
                Delete Group
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Two Column Layout -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Main Content (Chat) - 2/3 width -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200">
            <!-- Chat Header -->
            <div class="px-6 py-4 border-b border-gray-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-900">Group Discussion</h3>
                <button
                  @click="refreshMessages"
                  class="text-gray-600 hover:text-gray-900"
                  title="Refresh messages"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                </button>
              </div>
            </div>

            <!-- Messages Area -->
            <div ref="messagesContainer" class="h-96 overflow-y-auto p-6 space-y-4 bg-gray-50">
              <!-- Loading Messages -->
              <div v-if="loadingMessages" class="flex justify-center py-8">
                <LoadingSpinner />
              </div>

              <!-- Messages -->
              <div v-else-if="messages.length > 0">
                <div
                  v-for="message in messages"
                  :key="message.id"
                  class="flex items-start space-x-3"
                  :class="message.senderId === authStore.userId ? 'flex-row-reverse space-x-reverse' : ''"
                >
                  <!-- Avatar -->
                  <div class="flex-shrink-0">
                    <div class="h-8 w-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xs font-semibold">
                      {{ getInitials(message.senderName) }}
                    </div>
                  </div>

                  <!-- Message Content -->
                  <div class="flex-1 max-w-xl">
                    <div class="flex items-baseline space-x-2">
                      <span class="text-sm font-medium text-gray-900">
                        {{ message.senderName }}
                      </span>
                      <span class="text-xs text-gray-500">
                        {{ formatMessageTime(message.createdAt) }}
                      </span>
                    </div>
                    <div
                      :class="[
                        'mt-1 px-4 py-2 rounded-lg',
                        message.senderId === authStore.userId
                          ? 'bg-blue-600 text-white'
                          : 'bg-white border border-gray-200 text-gray-900'
                      ]"
                    >
                      <p class="text-sm break-words">{{ message.content }}</p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Empty State -->
              <div v-else class="flex flex-col items-center justify-center h-full">
                <svg class="h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                <h3 class="text-sm font-medium text-gray-900 mb-1">No messages yet</h3>
                <p class="text-sm text-gray-500">Start the conversation!</p>
              </div>
            </div>

            <!-- Message Input -->
            <div class="px-6 py-4 border-t border-gray-200">
              <form @submit.prevent="sendMessage" class="flex items-center space-x-3">
                <input
                  v-model="newMessage"
                  type="text"
                  placeholder="Type a message..."
                  class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
                />
                <button
                  type="submit"
                  :disabled="!newMessage.trim() || sending"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
                >
                  <LoadingSpinner v-if="sending" size="small" color="white" class="mr-2" />
                  <svg v-else class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                  </svg>
                </button>
              </form>
            </div>
          </div>
        </div>

        <!-- Sidebar (Members & Resources) - 1/3 width -->
        <div class="space-y-6">
          <!-- Members Section -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200">
            <div class="px-6 py-4 border-b border-gray-200">
              <h3 class="text-lg font-semibold text-gray-900">Members ({{ members.length }})</h3>
            </div>

            <div class="p-4 max-h-96 overflow-y-auto">
              <div class="space-y-3">
                <div
                  v-for="member in members"
                  :key="member.id"
                  class="flex items-center space-x-3 p-2 hover:bg-gray-50 rounded-lg transition-colors"
                >
                  <!-- Avatar -->
                  <div class="relative">
                    <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                      {{ getInitials(member.name) }}
                    </div>
                    <div v-if="member.online" class="absolute bottom-0 right-0 h-3 w-3 bg-green-400 border-2 border-white rounded-full"></div>
                  </div>

                  <!-- Member Info -->
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">
                      {{ member.name }}
                      <span v-if="member.id === groupInfo.creatorId" class="ml-2 text-xs text-blue-600">(Owner)</span>
                    </p>
                    <p class="text-xs text-gray-500 truncate">{{ member.email }}</p>
                  </div>

                  <!-- Actions (only for group owner) -->
                  <button
                    v-if="isGroupOwner && member.id !== groupInfo.creatorId"
                    @click="removeMember(member.id)"
                    class="text-red-600 hover:text-red-700"
                    title="Remove member"
                  >
                    <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <!-- Invite Link (for owners) -->
            <div v-if="isGroupOwner" class="px-6 py-4 border-t border-gray-200 bg-gray-50">
              <button
                @click="copyInviteLink"
                class="w-full px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-white transition-colors flex items-center justify-center"
              >
                <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                </svg>
                Copy Invite Link
              </button>
            </div>
          </div>

          <!-- Resources Section -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200">
            <div class="px-6 py-4 border-b border-gray-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-900">Resources</h3>
                <button
                  @click="showResourceModal = true"
                  class="text-blue-600 hover:text-blue-700"
                  title="Add resource"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                </button>
              </div>
            </div>

            <div class="p-4">
              <div v-if="resources.length > 0" class="space-y-3">
                <div
                  v-for="resource in resources"
                  :key="resource.id"
                  class="p-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <p class="text-sm font-medium text-gray-900">{{ resource.title }}</p>
                      <p class="text-xs text-gray-500 mt-1">
                        Shared by {{ resource.sharedBy }} • {{ formatDate(resource.createdAt) }}
                      </p>
                    </div>
                    <a
                      :href="resource.url"
                      target="_blank"
                      class="text-blue-600 hover:text-blue-700"
                      title="Open resource"
                    >
                      <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                      </svg>
                    </a>
                  </div>
                </div>
              </div>

              <div v-else class="text-center py-8">
                <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p class="mt-2 text-sm text-gray-500">No resources yet</p>
                <button
                  @click="showResourceModal = true"
                  class="mt-2 text-sm text-blue-600 hover:text-blue-700"
                >
                  Add the first resource
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import websocketService from '../../services/websocket'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const loadingMessages = ref(false)
const sending = ref(false)
const showEditModal = ref(false)
const showResourceModal = ref(false)

const groupInfo = ref({})
const members = ref([])
const messages = ref([])
const resources = ref([])
const newMessage = ref('')
const messagesContainer = ref(null)

onMounted(async () => {
  await loadGroupData()
  await loadMessages()
  connectWebSocket()
})

onUnmounted(() => {
  // Clean up WebSocket subscription
  if (groupInfo.value.id) {
    websocketService.unsubscribe(`group-${groupInfo.value.id}`)
  }
})

async function loadGroupData() {
  try {
    loading.value = true

    const groupId = route.params.id

    // Load group details
    const groupResponse = await api.getStudyGroupById(groupId)
    groupInfo.value = groupResponse.data

    // Load group members
    const membersResponse = await api.getStudyGroupMembers(groupId)
    members.value = membersResponse.data || []

    // Load resources
    const resourcesResponse = await api.getStudyGroupResources(groupId)
    resources.value = resourcesResponse.data || []
  } catch (error) {
    console.error('Error loading group data:', error)
    alert('Failed to load group data. Please try again.')
  } finally {
    loading.value = false
  }
}

async function loadMessages() {
  try {
    loadingMessages.value = true

    const groupId = route.params.id
    const messagesResponse = await api.getStudyGroupMessages(groupId)
    messages.value = messagesResponse.data || []

    scrollToBottom()
  } catch (error) {
    console.error('Error loading messages:', error)
  } finally {
    loadingMessages.value = false
  }
}

function connectWebSocket() {
  // Subscribe to group messages
  websocketService.subscribeToGroup(groupInfo.value.id, (message) => {
    messages.value.push(message)
    scrollToBottom()
  })
}

async function sendMessage() {
  if (!newMessage.value.trim() || sending.value) return

  try {
    sending.value = true

    const messageData = {
      groupId: groupInfo.value.id,
      senderId: authStore.userId,
      content: newMessage.value.trim(),
      createdAt: new Date().toISOString()
    }

    const response = await api.sendGroupMessage(messageData)
    messages.value.push(response.data)

    newMessage.value = ''
    scrollToBottom()
  } catch (error) {
    console.error('Error sending message:', error)
    alert('Failed to send message. Please try again.')
  } finally {
    sending.value = false
  }
}

async function refreshMessages() {
  await loadMessages()
}

async function leaveGroup() {
  if (!confirm('Are you sure you want to leave this study group?')) return

  try {
    await api.leaveStudyGroup(groupInfo.value.id, authStore.userId)
    alert('Successfully left the study group.')
    router.push('/studygroups')
  } catch (error) {
    console.error('Error leaving group:', error)
    alert('Failed to leave group. Please try again.')
  }
}

async function deleteGroup() {
  if (!confirm('Are you sure you want to delete this study group? This action cannot be undone.')) return

  try {
    await api.deleteStudyGroup(groupInfo.value.id)
    alert('Study group deleted successfully.')
    router.push('/studygroups')
  } catch (error) {
    console.error('Error deleting group:', error)
    alert('Failed to delete group. Please try again.')
  }
}

async function removeMember(memberId) {
  if (!confirm('Are you sure you want to remove this member?')) return

  try {
    await api.removeStudyGroupMember(groupInfo.value.id, memberId)
    members.value = members.value.filter(m => m.id !== memberId)
    alert('Member removed successfully.')
  } catch (error) {
    console.error('Error removing member:', error)
    alert('Failed to remove member. Please try again.')
  }
}

function copyInviteLink() {
  const link = `${window.location.origin}/studygroups/${groupInfo.value.id}`
  navigator.clipboard.writeText(link)
  alert('Invite link copied to clipboard!')
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const isGroupOwner = computed(() => {
  return groupInfo.value.creatorId === authStore.userId
})

function getInitials(name) {
  if (!name) return '?'
  const parts = name.split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return name.substring(0, 2).toUpperCase()
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function formatMessageTime(dateString) {
  if (!dateString) return ''

  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return 'Just now'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`

  // Same day - show time
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' })
  }

  // Different day - show date
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>
