<template>
  <Modal v-model="isOpen" title="New Message" size="medium">
    <div class="space-y-4">
      <!-- Search Users -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Search Users
        </label>
        <div class="relative">
          <input
            v-model="searchQuery"
            @input="searchUsers"
            type="text"
            placeholder="Search by name, username, or email..."
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          />
          <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="searching" class="flex justify-center py-8">
        <LoadingSpinner />
      </div>

      <!-- Search Results -->
      <div v-else-if="searchResults.length > 0" class="max-h-96 overflow-y-auto border border-gray-200 rounded-lg">
        <button
          v-for="user in searchResults"
          :key="user.id"
          @click="selectUser(user)"
          :class="[
            'w-full p-3 hover:bg-gray-50 transition-colors text-left border-b border-gray-100 last:border-b-0',
            selectedUser?.id === user.id ? 'bg-blue-50' : ''
          ]"
        >
          <div class="flex items-center space-x-3">
            <!-- Avatar -->
            <div class="flex-shrink-0">
              <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                {{ getInitials(user) }}
              </div>
            </div>

            <!-- User Info -->
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold text-gray-900 truncate">
                {{ user.firstName }} {{ user.lastName }}
              </p>
              <p class="text-xs text-gray-500 truncate">@{{ user.username }} • {{ user.email }}</p>
              <span class="inline-block mt-1 px-2 py-0.5 text-xs font-medium rounded-full" :class="getRoleBadgeClass(user.role)">
                {{ user.role }}
              </span>
            </div>

            <!-- Check Icon (if selected) -->
            <svg
              v-if="selectedUser?.id === user.id"
              class="h-5 w-5 text-blue-600"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
            </svg>
          </div>
        </button>
      </div>

      <!-- No Results -->
      <div v-else-if="searchQuery.length > 0 && !searching" class="text-center py-8">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <p class="mt-2 text-sm text-gray-500">No users found matching "{{ searchQuery }}"</p>
      </div>

      <!-- Initial State -->
      <div v-else class="text-center py-8">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <p class="mt-2 text-sm text-gray-500">Search for a user to start a conversation</p>
      </div>

      <!-- First Message (if user selected) -->
      <div v-if="selectedUser" class="pt-4 border-t border-gray-200">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          First Message
        </label>
        <textarea
          v-model="firstMessage"
          rows="3"
          placeholder="Type your first message..."
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
        ></textarea>
      </div>

      <!-- Error Message -->
      <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-md">
        <p class="text-sm text-red-600">{{ errorMessage }}</p>
      </div>
    </div>

    <template #footer>
      <div class="flex items-center justify-end space-x-3">
        <button
          @click="closeModal"
          type="button"
          class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
        >
          Cancel
        </button>
        <button
          @click="startConversation"
          :disabled="!selectedUser || !firstMessage.trim() || sending"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
        >
          <LoadingSpinner v-if="sending" size="small" color="white" class="mr-2" />
          <span>{{ sending ? 'Sending...' : 'Start Conversation' }}</span>
        </button>
      </div>
    </template>
  </Modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import Modal from '../../components/Modal.vue'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'conversation-started'])

const authStore = useAuthStore()

const searchQuery = ref('')
const searchResults = ref([])
const selectedUser = ref(null)
const firstMessage = ref('')
const searching = ref(false)
const sending = ref(false)
const errorMessage = ref('')

let searchTimeout = null

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// Watch for modal close to reset state
watch(isOpen, (newValue) => {
  if (!newValue) {
    resetModal()
  }
})

async function searchUsers() {
  if (searchQuery.value.length < 2) {
    searchResults.value = []
    return
  }

  // Clear existing timeout
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  // Debounce search
  searchTimeout = setTimeout(async () => {
    try {
      searching.value = true
      errorMessage.value = ''

      // Search users (you can add a dedicated search endpoint or filter all users)
      const response = await api.getAllUsers()
      const allUsers = response.data || []

      // Filter out current user and search by name, username, email
      const query = searchQuery.value.toLowerCase()
      searchResults.value = allUsers
        .filter(user => user.id !== authStore.userId)
        .filter(user => {
          const fullName = `${user.firstName} ${user.lastName}`.toLowerCase()
          const username = user.username.toLowerCase()
          const email = user.email.toLowerCase()
          return fullName.includes(query) || username.includes(query) || email.includes(query)
        })
        .slice(0, 20) // Limit to 20 results
    } catch (error) {
      console.error('Error searching users:', error)
      errorMessage.value = 'Failed to search users. Please try again.'
    } finally {
      searching.value = false
    }
  }, 300)
}

function selectUser(user) {
  selectedUser.value = user
}

async function startConversation() {
  if (!selectedUser.value || !firstMessage.value.trim()) return

  try {
    sending.value = true
    errorMessage.value = ''

    // Send first message
    const messageData = {
      senderId: authStore.userId,
      receiverId: selectedUser.value.id,
      content: firstMessage.value.trim(),
      createdAt: new Date().toISOString()
    }

    await api.sendMessage(messageData)

    // Emit event to parent
    emit('conversation-started', selectedUser.value.id)

    // Close modal
    closeModal()
  } catch (error) {
    console.error('Error starting conversation:', error)
    errorMessage.value = error.response?.data?.message || 'Failed to start conversation. Please try again.'
  } finally {
    sending.value = false
  }
}

function closeModal() {
  isOpen.value = false
}

function resetModal() {
  searchQuery.value = ''
  searchResults.value = []
  selectedUser.value = null
  firstMessage.value = ''
  errorMessage.value = ''
}

function getInitials(user) {
  if (!user) return '?'
  const first = user.firstName?.[0] || ''
  const last = user.lastName?.[0] || ''
  return (first + last).toUpperCase() || user.username?.[0]?.toUpperCase() || '?'
}

function getRoleBadgeClass(role) {
  const classes = {
    'STUDENT': 'bg-blue-100 text-blue-800',
    'FACULTY': 'bg-purple-100 text-purple-800',
    'ADMIN': 'bg-orange-100 text-orange-800',
    'SUPER_ADMIN': 'bg-red-100 text-red-800'
  }
  return classes[role] || 'bg-gray-100 text-gray-800'
}
</script>
