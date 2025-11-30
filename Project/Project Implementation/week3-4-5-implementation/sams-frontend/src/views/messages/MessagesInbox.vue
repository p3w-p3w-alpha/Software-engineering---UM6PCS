<template>
  <div class="h-screen flex overflow-hidden bg-gray-100">
    <!-- Sidebar - Conversations List -->
    <div class="w-full md:w-96 bg-white border-r border-gray-200 flex flex-col">
      <!-- Header -->
      <div class="px-4 py-4 border-b border-gray-200">
        <div class="flex items-center justify-between mb-4">
          <h1 class="text-xl font-bold text-gray-900">Messages</h1>
          <button
            @click="showNewMessageModal = true"
            class="p-2 bg-blue-600 text-white rounded-full hover:bg-blue-700 transition-colors"
            title="New message"
          >
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
          </button>
        </div>

        <!-- Search -->
        <div class="relative">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search conversations..."
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          />
          <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>

      <!-- Conversations List -->
      <div class="flex-1 overflow-y-auto">
        <!-- Loading State -->
        <div v-if="loadingConversations" class="flex justify-center py-8">
          <LoadingSpinner />
        </div>

        <!-- Conversations -->
        <div v-else-if="filteredConversations.length > 0" class="divide-y divide-gray-200">
          <button
            v-for="conversation in filteredConversations"
            :key="conversation.otherUser.id"
            @click="selectConversation(conversation)"
            :class="[
              'w-full p-4 hover:bg-gray-50 transition-colors text-left',
              selectedUserId === conversation.otherUser.id ? 'bg-blue-50' : ''
            ]"
          >
            <div class="flex items-start space-x-3">
              <!-- Avatar -->
              <div class="flex-shrink-0">
                <div class="h-12 w-12 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                  {{ getInitials(conversation.otherUser) }}
                </div>
                <div v-if="conversation.otherUser.online" class="absolute h-3 w-3 bg-green-400 border-2 border-white rounded-full -mt-3 ml-9"></div>
              </div>

              <!-- Conversation Info -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between mb-1">
                  <p class="text-sm font-semibold text-gray-900 truncate">
                    {{ conversation.otherUser.firstName }} {{ conversation.otherUser.lastName }}
                  </p>
                  <span class="text-xs text-gray-500">
                    {{ formatMessageTime(conversation.lastMessage?.createdAt) }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <p class="text-sm text-gray-600 truncate">
                    {{ conversation.lastMessage?.content || 'No messages yet' }}
                  </p>
                  <span
                    v-if="conversation.unreadCount > 0"
                    class="ml-2 px-2 py-0.5 bg-blue-600 text-white text-xs font-semibold rounded-full"
                  >
                    {{ conversation.unreadCount }}
                  </span>
                </div>
                <p class="text-xs text-gray-500 mt-1">{{ conversation.otherUser.role }}</p>
              </div>
            </div>
          </button>
        </div>

        <!-- Empty State -->
        <div v-else class="flex flex-col items-center justify-center py-12 px-4">
          <svg class="h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
          <h3 class="text-sm font-medium text-gray-900 mb-1">No conversations</h3>
          <p class="text-sm text-gray-500 text-center mb-4">Start a conversation by clicking the + button above</p>
          <button
            @click="showNewMessageModal = true"
            class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
          >
            New Message
          </button>
        </div>
      </div>
    </div>

    <!-- Main Content - Conversation View -->
    <div class="flex-1 flex flex-col">
      <!-- No Conversation Selected -->
      <div v-if="!selectedUserId" class="flex-1 flex items-center justify-center bg-gray-50">
        <div class="text-center">
          <svg class="mx-auto h-20 w-20 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
          <h3 class="text-lg font-medium text-gray-900 mb-2">Your Messages</h3>
          <p class="text-gray-500 mb-6">Select a conversation or start a new one</p>
          <button
            @click="showNewMessageModal = true"
            class="px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"
          >
            Start Messaging
          </button>
        </div>
      </div>

      <!-- Conversation View -->
      <ConversationView
        v-else
        :other-user-id="selectedUserId"
        @back="selectedUserId = null"
        @message-sent="handleMessageSent"
      />
    </div>

    <!-- New Message Modal -->
    <NewMessageModal
      v-model="showNewMessageModal"
      @conversation-started="handleConversationStarted"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import websocketService from '../../services/websocket'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import ConversationView from './ConversationView.vue'
import NewMessageModal from './NewMessageModal.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const conversations = ref([])
const selectedUserId = ref(null)
const loadingConversations = ref(true)
const showNewMessageModal = ref(false)
const searchQuery = ref('')

const filteredConversations = computed(() => {
  if (!searchQuery.value) return conversations.value

  const query = searchQuery.value.toLowerCase()
  return conversations.value.filter(conv => {
    const name = `${conv.otherUser.firstName} ${conv.otherUser.lastName}`.toLowerCase()
    const username = conv.otherUser.username.toLowerCase()
    return name.includes(query) || username.includes(query)
  })
})

onMounted(async () => {
  await loadConversations()

  // Check if a specific user is selected from route params
  if (route.params.userId) {
    selectedUserId.value = parseInt(route.params.userId)
  }

  // Connect to WebSocket for real-time updates
  connectWebSocket()
})

onUnmounted(() => {
  // Clean up WebSocket subscriptions if needed
})

async function loadConversations() {
  try {
    loadingConversations.value = true
    const response = await api.getUserConversations(authStore.userId)
    const rawMessages = response.data || []

    // Transform raw messages into conversations grouped by the other user
    const conversationMap = new Map()
    const currentUserId = authStore.userId

    for (const msg of rawMessages) {
      // Determine the other user in this message
      const isCurrentUserSender = msg.senderId === currentUserId
      const otherUserId = isCurrentUserSender ? msg.receiverId : msg.senderId
      const otherUserName = isCurrentUserSender ? msg.receiverName : msg.senderName

      // Parse name into first/last
      const nameParts = (otherUserName || 'Unknown User').split(' ')
      const firstName = nameParts[0] || 'Unknown'
      const lastName = nameParts.slice(1).join(' ') || ''

      if (!conversationMap.has(otherUserId)) {
        conversationMap.set(otherUserId, {
          otherUser: {
            id: otherUserId,
            firstName: firstName,
            lastName: lastName,
            username: otherUserName?.toLowerCase().replace(/\s+/g, '') || 'user',
            role: 'User',
            online: false
          },
          lastMessage: {
            content: msg.content,
            createdAt: msg.sentAt
          },
          unreadCount: (!isCurrentUserSender && !msg.read) ? 1 : 0
        })
      } else {
        const conv = conversationMap.get(otherUserId)
        // Update last message if this one is newer
        const existingTime = new Date(conv.lastMessage.createdAt)
        const newTime = new Date(msg.sentAt)
        if (newTime > existingTime) {
          conv.lastMessage = {
            content: msg.content,
            createdAt: msg.sentAt
          }
        }
        // Count unread messages
        if (!isCurrentUserSender && !msg.read) {
          conv.unreadCount++
        }
      }
    }

    conversations.value = Array.from(conversationMap.values())

    // Sort by last message time
    conversations.value.sort((a, b) => {
      const timeA = a.lastMessage?.createdAt ? new Date(a.lastMessage.createdAt) : new Date(0)
      const timeB = b.lastMessage?.createdAt ? new Date(b.lastMessage.createdAt) : new Date(0)
      return timeB - timeA
    })
  } catch (error) {
    console.error('Error loading conversations:', error)
  } finally {
    loadingConversations.value = false
  }
}

function connectWebSocket() {
  // Subscribe to new messages
  websocketService.subscribeToMessages(authStore.userId, (message) => {
    handleNewMessage(message)
  })
}

function handleNewMessage(message) {
  // Find or create conversation
  const otherUserId = message.senderId === authStore.userId ? message.receiverId : message.senderId

  let conversation = conversations.value.find(c => c.otherUser.id === otherUserId)

  if (conversation) {
    // Update existing conversation
    conversation.lastMessage = message

    // Increment unread count if not currently viewing this conversation
    if (selectedUserId.value !== otherUserId && message.senderId !== authStore.userId) {
      conversation.unreadCount = (conversation.unreadCount || 0) + 1
    }

    // Move to top
    conversations.value = [conversation, ...conversations.value.filter(c => c.otherUser.id !== otherUserId)]
  } else {
    // Reload conversations to get the new one
    loadConversations()
  }
}

function selectConversation(conversation) {
  selectedUserId.value = conversation.otherUser.id

  // Mark as read
  if (conversation.unreadCount > 0) {
    conversation.unreadCount = 0
    api.markConversationAsRead(authStore.userId, conversation.otherUser.id).catch(err => {
      console.error('Error marking conversation as read:', err)
    })
  }

  // Update URL
  router.push(`/messages/${conversation.otherUser.id}`)
}

function handleConversationStarted(userId) {
  showNewMessageModal.value = false
  selectedUserId.value = userId

  // Reload conversations to include the new one
  loadConversations()

  // Update URL
  router.push(`/messages/${userId}`)
}

function handleMessageSent() {
  // Reload conversations to update last message
  loadConversations()
}

function getInitials(user) {
  if (!user) return '?'
  const first = user.firstName?.[0] || ''
  const last = user.lastName?.[0] || ''
  return (first + last).toUpperCase() || user.username?.[0]?.toUpperCase() || '?'
}

function formatMessageTime(dateString) {
  if (!dateString) return ''

  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return 'Just now'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>
