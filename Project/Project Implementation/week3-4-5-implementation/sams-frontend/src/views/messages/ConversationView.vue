<template>
  <div class="flex flex-col h-full bg-white">
    <!-- Header -->
    <div class="px-4 py-4 border-b border-gray-200 flex items-center justify-between bg-white">
      <div class="flex items-center space-x-3">
        <!-- Back button (mobile) -->
        <button
          @click="$emit('back')"
          class="md:hidden p-2 text-gray-600 hover:text-gray-900"
        >
          <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>

        <!-- User Avatar & Info -->
        <div v-if="otherUser" class="flex items-center space-x-3">
          <div class="relative">
            <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
              {{ getInitials(otherUser) }}
            </div>
            <div v-if="otherUser.online" class="absolute bottom-0 right-0 h-3 w-3 bg-green-400 border-2 border-white rounded-full"></div>
          </div>
          <div>
            <h2 class="text-sm font-semibold text-gray-900">
              {{ otherUser.firstName }} {{ otherUser.lastName }}
            </h2>
            <p class="text-xs text-gray-500">
              {{ otherUser.online ? 'Active now' : 'Offline' }} • {{ otherUser.role }}
            </p>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex items-center space-x-2">
        <button
          @click="refreshMessages"
          class="p-2 text-gray-600 hover:text-gray-900"
          title="Refresh"
        >
          <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Messages Area -->
    <div ref="messagesContainer" class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
      <!-- Loading State -->
      <div v-if="loadingMessages" class="flex justify-center py-8">
        <LoadingSpinner />
      </div>

      <!-- Messages -->
      <div v-else-if="messages.length > 0">
        <div
          v-for="(message, index) in messages"
          :key="message.id"
          class="flex"
          :class="message.senderId === authStore.userId ? 'justify-end' : 'justify-start'"
        >
          <div
            :class="[
              'max-w-xs md:max-w-md lg:max-w-lg px-4 py-2 rounded-2xl',
              message.senderId === authStore.userId
                ? 'bg-blue-600 text-white rounded-br-none'
                : 'bg-white border border-gray-200 text-gray-900 rounded-bl-none'
            ]"
          >
            <p class="text-sm break-words">{{ message.content }}</p>
            <div class="flex items-center justify-end space-x-1 mt-1">
              <span
                :class="[
                  'text-xs',
                  message.senderId === authStore.userId ? 'text-blue-100' : 'text-gray-500'
                ]"
              >
                {{ formatMessageTime(message.sentAt || message.createdAt) }}
              </span>
              <!-- Read Receipt (for sent messages) -->
              <svg
                v-if="message.senderId === authStore.userId && message.read"
                class="h-4 w-4 text-blue-200"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
            </div>
          </div>
        </div>

        <!-- Typing Indicator -->
        <div v-if="isTyping" class="flex justify-start">
          <div class="bg-white border border-gray-200 px-4 py-3 rounded-2xl rounded-bl-none">
            <div class="flex space-x-1">
              <div class="h-2 w-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0ms"></div>
              <div class="h-2 w-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 150ms"></div>
              <div class="h-2 w-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 300ms"></div>
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
        <p class="text-sm text-gray-500">Send a message to start the conversation</p>
      </div>
    </div>

    <!-- Message Input -->
    <div class="px-4 py-4 border-t border-gray-200 bg-white">
      <!-- File Attachment Preview -->
      <div v-if="attachedFile" class="mb-3 p-3 bg-gray-50 border border-gray-200 rounded-lg">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="p-2 bg-red-100 rounded-lg">
              <svg class="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <p class="text-sm font-medium text-gray-900">{{ attachedFile.name }}</p>
              <p class="text-xs text-gray-500">{{ formatFileSize(attachedFile.size) }}</p>
            </div>
          </div>
          <button @click="removeAttachment" class="p-1 text-gray-400 hover:text-gray-600">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div v-if="uploadProgress > 0 && uploadProgress < 100" class="mt-2">
          <div class="h-1.5 w-full bg-gray-200 rounded-full overflow-hidden">
            <div class="h-full bg-blue-600 transition-all duration-300" :style="{ width: uploadProgress + '%' }"></div>
          </div>
        </div>
      </div>

      <!-- Drag & Drop Zone -->
      <div
        v-if="isDragging"
        @dragover.prevent
        @drop.prevent="handleDrop"
        @dragleave="isDragging = false"
        class="mb-3 p-6 border-2 border-dashed border-blue-400 bg-blue-50 rounded-lg text-center"
      >
        <svg class="mx-auto h-10 w-10 text-blue-500 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
        </svg>
        <p class="text-sm text-blue-600 font-medium">Drop your PDF file here</p>
        <p class="text-xs text-blue-500 mt-1">Max size: 10MB</p>
      </div>

      <form @submit.prevent="sendMessage" class="flex items-end space-x-2">
        <!-- Attachment Button -->
        <div class="relative">
          <input
            ref="fileInput"
            type="file"
            accept=".pdf"
            @change="handleFileSelect"
            class="hidden"
          />
          <button
            type="button"
            @click="$refs.fileInput.click()"
            class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
            title="Attach PDF file"
          >
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
            </svg>
          </button>
        </div>

        <div
          class="flex-1"
          @dragover.prevent="isDragging = true"
          @dragleave="isDragging = false"
          @drop.prevent="handleDrop"
        >
          <textarea
            v-model="messageContent"
            @keydown.enter.exact.prevent="sendMessage"
            @input="handleTyping"
            rows="1"
            placeholder="Type a message..."
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 resize-none"
            style="max-height: 120px"
          ></textarea>
          <p class="text-xs text-gray-500 mt-1">Press Enter to send • Drag & drop PDF to attach</p>
        </div>
        <button
          type="submit"
          :disabled="(!messageContent.trim() && !attachedFile) || sending"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
        >
          <LoadingSpinner v-if="sending" size="small" color="white" class="mr-2" />
          <svg v-else class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
          <span class="ml-2">Send</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import websocketService from '../../services/websocket'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const props = defineProps({
  otherUserId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['back', 'message-sent', 'error'])

const authStore = useAuthStore()

const messages = ref([])
const otherUser = ref(null)
const messageContent = ref('')
const loadingMessages = ref(true)
const sending = ref(false)
const isTyping = ref(false)
const messagesContainer = ref(null)
const fileInput = ref(null)
const attachedFile = ref(null)
const isDragging = ref(false)
const uploadProgress = ref(0)

const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB

let typingTimeout = null

onMounted(async () => {
  await loadOtherUser()
  await loadMessages()
  scrollToBottom()

  // Connect to WebSocket for real-time messages
  connectWebSocket()
})

onUnmounted(() => {
  // Clean up
  if (typingTimeout) {
    clearTimeout(typingTimeout)
  }
})

// Watch for other user ID changes
watch(() => props.otherUserId, async () => {
  await loadOtherUser()
  await loadMessages()
  scrollToBottom()
})

async function loadOtherUser() {
  try {
    const response = await api.getUserById(props.otherUserId)
    otherUser.value = response.data
  } catch (error) {
    console.error('Error loading user:', error)
  }
}

async function loadMessages() {
  try {
    loadingMessages.value = true
    const response = await api.getConversation(authStore.userId, props.otherUserId)
    messages.value = response.data || []

    // Mark messages as read
    await api.markConversationAsRead(authStore.userId, props.otherUserId)
  } catch (error) {
    console.error('Error loading messages:', error)
  } finally {
    loadingMessages.value = false
  }
}

async function refreshMessages() {
  await loadMessages()
  scrollToBottom()
}

function connectWebSocket() {
  // Subscribe to new messages in this conversation
  websocketService.subscribeToConversation(authStore.userId, props.otherUserId, (message) => {
    messages.value.push(message)
    scrollToBottom()

    // Mark as read if it's from the other user
    if (message.senderId === props.otherUserId) {
      api.markMessageAsRead(message.id, authStore.userId).catch(err => {
        console.error('Error marking message as read:', err)
      })
    }
  })

  // Subscribe to typing indicators
  websocketService.subscribeToTyping(props.otherUserId, (typing) => {
    isTyping.value = typing
    if (typing) {
      scrollToBottom()
    }
  })
}

async function sendMessage() {
  if ((!messageContent.value.trim() && !attachedFile.value) || sending.value) return

  try {
    sending.value = true

    let fileData = null
    if (attachedFile.value) {
      fileData = await uploadFile()
    }

    const content = messageContent.value.trim()
    let fullContent = content

    // Add file link to message content if file was uploaded
    if (fileData) {
      const fileInfo = `[PDF: ${attachedFile.value.name}]${fileData.downloadUrl ? ' ' + fileData.downloadUrl : ''}`
      fullContent = content ? `${content}\n\n${fileInfo}` : fileInfo
    }

    const messageData = {
      senderId: authStore.userId,
      receiverId: props.otherUserId,
      content: fullContent,
      createdAt: new Date().toISOString(),
      attachmentId: fileData?.id || null,
      attachmentName: attachedFile.value?.name || null
    }

    const response = await api.sendMessage(messageData)

    // Add message to list
    messages.value.push(response.data)

    // Clear input and attachment
    messageContent.value = ''
    removeAttachment()

    // Scroll to bottom
    scrollToBottom()

    // Notify parent
    emit('message-sent')
  } catch (error) {
    console.error('Error sending message:', error)
    // Parent component should handle displaying error notification
    emit('error', 'Failed to send message. Please try again.')
  } finally {
    sending.value = false
  }
}

function handleTyping() {
  // Send typing indicator
  websocketService.sendTypingIndicator(authStore.userId, props.otherUserId, true)

  // Clear existing timeout
  if (typingTimeout) {
    clearTimeout(typingTimeout)
  }

  // Set timeout to stop typing indicator
  typingTimeout = setTimeout(() => {
    websocketService.sendTypingIndicator(authStore.userId, props.otherUserId, false)
  }, 1000)
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
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

  // Same day - show time
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' })
  }

  // Different day - show date
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

// File handling functions
function handleFileSelect(event) {
  const file = event.target.files[0]
  if (file) {
    validateAndAttachFile(file)
  }
}

function handleDrop(event) {
  isDragging.value = false
  const file = event.dataTransfer.files[0]
  if (file) {
    validateAndAttachFile(file)
  }
}

function validateAndAttachFile(file) {
  // Check file type
  if (file.type !== 'application/pdf') {
    emit('error', 'Only PDF files are allowed')
    return
  }

  // Check file size
  if (file.size > MAX_FILE_SIZE) {
    emit('error', 'File size exceeds 10MB limit')
    return
  }

  attachedFile.value = file
}

function removeAttachment() {
  attachedFile.value = null
  uploadProgress.value = 0
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

async function uploadFile() {
  if (!attachedFile.value) return null

  try {
    const formData = new FormData()
    formData.append('file', attachedFile.value)
    formData.append('uploadedBy', authStore.userId)
    formData.append('description', `Attachment from message to ${otherUser.value?.username || 'user'}`)

    // Simulate upload progress
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 100)

    const response = await api.uploadFile(formData)

    clearInterval(progressInterval)
    uploadProgress.value = 100

    return response.data
  } catch (error) {
    console.error('Error uploading file:', error)
    throw error
  }
}
</script>

<style scoped>
/* Auto-grow textarea */
textarea {
  overflow-y: auto;
}
</style>
