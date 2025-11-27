<template>
  <div class="max-w-4xl mx-auto">
    <!-- Back Button -->
    <button
      @click="$router.back()"
      class="mb-6 flex items-center text-gray-600 hover:text-gray-900"
    >
      <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      Back to Assignments
    </button>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Assignment Not Found -->
    <div v-else-if="!assignment" class="bg-white rounded-lg shadow p-12 text-center">
      <h3 class="text-lg font-medium text-gray-900">Assignment not found</h3>
      <p class="mt-2 text-gray-600">The assignment you're looking for doesn't exist.</p>
    </div>

    <!-- Submission Form -->
    <div v-else>
      <!-- Assignment Info Card -->
      <div class="bg-white rounded-lg shadow mb-6">
        <div class="p-6 border-b border-gray-200">
          <h1 class="text-2xl font-bold text-gray-900">{{ assignment.title }}</h1>
          <p class="text-sm text-gray-600 mt-1">
            {{ assignment.course?.code }} - {{ assignment.course?.name }}
          </p>
        </div>
        <div class="p-6">
          <div class="prose max-w-none mb-6">
            <p class="text-gray-700">{{ assignment.description }}</p>
          </div>
          <div class="grid grid-cols-3 gap-4 text-sm">
            <div>
              <span class="text-gray-500">Due Date:</span>
              <p class="font-medium mt-1" :class="getDueDateClass()">
                {{ formatDate(assignment.dueDate) }}
              </p>
            </div>
            <div>
              <span class="text-gray-500">Maximum Grade:</span>
              <p class="font-medium text-gray-900 mt-1">{{ assignment.maxGrade }} points</p>
            </div>
            <div>
              <span class="text-gray-500">Status:</span>
              <p class="font-medium mt-1" :class="isPastDue() ? 'text-red-600' : 'text-green-600'">
                {{ isPastDue() ? 'Overdue' : 'On Time' }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Late Submission Warning -->
      <div v-if="isPastDue()" class="bg-red-50 border-l-4 border-red-400 p-4 mb-6">
        <div class="flex">
          <svg class="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
          <div class="ml-3">
            <p class="text-sm text-red-700">
              <strong>Warning:</strong> This assignment is past due. Late submissions may receive a grade penalty.
            </p>
          </div>
        </div>
      </div>

      <!-- Submission Form -->
      <form @submit.prevent="submitAssignment" class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-6">Submit Your Work</h2>

        <!-- File Upload Area -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Upload Files <span class="text-red-500">*</span>
          </label>

          <!-- Drag & Drop Zone -->
          <div
            @drop.prevent="handleDrop"
            @dragover.prevent="dragOver = true"
            @dragleave="dragOver = false"
            :class="[
              'border-2 border-dashed rounded-lg p-8 text-center transition-colors',
              dragOver ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400'
            ]"
          >
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <div class="mt-4">
              <label class="cursor-pointer">
                <span class="text-blue-600 hover:text-blue-500 font-medium">Upload files</span>
                <span class="text-gray-600"> or drag and drop</span>
                <input
                  ref="fileInput"
                  type="file"
                  multiple
                  @change="handleFileSelect"
                  class="hidden"
                  accept=".pdf,.doc,.docx,.txt,.zip,.jpg,.jpeg,.png"
                />
              </label>
            </div>
            <p class="text-xs text-gray-500 mt-2">
              PDF, DOC, DOCX, TXT, ZIP, JPG, PNG up to 10MB each
            </p>
          </div>

          <!-- Selected Files List -->
          <div v-if="selectedFiles.length > 0" class="mt-4 space-y-2">
            <div
              v-for="(file, index) in selectedFiles"
              :key="index"
              class="flex items-center justify-between p-3 bg-gray-50 rounded-md"
            >
              <div class="flex items-center space-x-3 flex-1 min-w-0">
                <svg class="h-5 w-5 text-gray-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900 truncate">{{ file.name }}</p>
                  <p class="text-xs text-gray-500">{{ formatFileSize(file.size) }}</p>
                </div>
              </div>
              <button
                type="button"
                @click="removeFile(index)"
                class="ml-4 text-red-600 hover:text-red-800"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Upload Progress -->
          <div v-if="uploading" class="mt-4">
            <div class="flex items-center justify-between text-sm text-gray-600 mb-2">
              <span>Uploading files...</span>
              <span>{{ uploadProgress }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="bg-blue-600 h-2 rounded-full transition-all duration-300"
                :style="{ width: uploadProgress + '%' }"
              ></div>
            </div>
          </div>
        </div>

        <!-- Comments/Notes -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Comments (Optional)
          </label>
          <textarea
            v-model="submissionNotes"
            rows="4"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Add any comments or notes about your submission..."
          ></textarea>
        </div>

        <!-- Error Message -->
        <div v-if="errorMessage" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
          <p class="text-sm text-red-600">{{ errorMessage }}</p>
        </div>

        <!-- Submit Buttons -->
        <div class="flex items-center justify-end space-x-4">
          <button
            type="button"
            @click="$router.back()"
            class="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            :disabled="selectedFiles.length === 0 || submitting"
            class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            <LoadingSpinner v-if="submitting" class="mr-2" size="small" color="white" />
            <span>{{ submitting ? 'Submitting...' : 'Submit Assignment' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const assignment = ref(null)
const loading = ref(true)
const submitting = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const selectedFiles = ref([])
const submissionNotes = ref('')
const errorMessage = ref('')
const dragOver = ref(false)
const fileInput = ref(null)

onMounted(async () => {
  await loadAssignment()
})

async function loadAssignment() {
  try {
    loading.value = true
    const assignmentId = route.params.id
    const response = await api.getAssignmentById(assignmentId)
    assignment.value = response.data
  } catch (error) {
    console.error('Error loading assignment:', error)
    assignment.value = null
  } finally {
    loading.value = false
  }
}

function handleFileSelect(event) {
  const files = Array.from(event.target.files)
  addFiles(files)
}

function handleDrop(event) {
  dragOver.value = false
  const files = Array.from(event.dataTransfer.files)
  addFiles(files)
}

function addFiles(files) {
  const maxSize = 10 * 1024 * 1024 // 10MB
  const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text/plain', 'application/zip', 'image/jpeg', 'image/jpg', 'image/png']

  for (const file of files) {
    if (file.size > maxSize) {
      errorMessage.value = `File "${file.name}" is too large. Maximum size is 10MB.`
      continue
    }
    if (!allowedTypes.includes(file.type)) {
      errorMessage.value = `File "${file.name}" has an unsupported format.`
      continue
    }
    selectedFiles.value.push(file)
  }
}

function removeFile(index) {
  selectedFiles.value.splice(index, 1)
  errorMessage.value = ''
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

async function submitAssignment() {
  if (selectedFiles.value.length === 0) {
    errorMessage.value = 'Please select at least one file to submit.'
    return
  }

  try {
    submitting.value = true
    uploading.value = true
    uploadProgress.value = 0
    errorMessage.value = ''

    // Upload files first
    const uploadedFiles = []
    for (let i = 0; i < selectedFiles.value.length; i++) {
      const file = selectedFiles.value[i]
      const formData = new FormData()
      formData.append('file', file)
      formData.append('assignmentId', assignment.value.id)

      const response = await api.uploadAssignmentFile(formData)
      uploadedFiles.push(response.data)

      uploadProgress.value = Math.round(((i + 1) / selectedFiles.value.length) * 100)
    }

    // Create submission
    const submissionData = {
      assignmentId: assignment.value.id,
      studentId: authStore.userId,
      submittedAt: new Date().toISOString(),
      notes: submissionNotes.value,
      filePaths: uploadedFiles.map(f => f.filePath || f.path),
      status: 'SUBMITTED'
    }

    await api.submitAssignment(submissionData)

    // Success - redirect to assignments page
    router.push({
      path: '/student/assignments',
      query: { submitted: 'true' }
    })
  } catch (error) {
    console.error('Error submitting assignment:', error)
    errorMessage.value = error.response?.data?.message || 'Failed to submit assignment. Please try again.'
  } finally {
    submitting.value = false
    uploading.value = false
  }
}

function isPastDue() {
  if (!assignment.value) return false
  return new Date(assignment.value.dueDate) < new Date()
}

function getDueDateClass() {
  if (!assignment.value) return ''
  const now = new Date()
  const dueDate = new Date(assignment.value.dueDate)
  const hoursUntilDue = (dueDate - now) / (1000 * 60 * 60)

  if (hoursUntilDue < 0) return 'text-red-600 font-bold'
  if (hoursUntilDue < 24) return 'text-orange-600 font-bold'
  if (hoursUntilDue < 72) return 'text-yellow-600'
  return 'text-gray-900'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
