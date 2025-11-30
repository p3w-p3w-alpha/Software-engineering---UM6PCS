<template>
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

  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Submission History</h1>
      <p class="mt-2 text-gray-600">View all your submitted assignments and grades</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Submissions List -->
    <div v-else-if="submissions.length > 0" class="space-y-4">
      <div
        v-for="submission in submissions"
        :key="submission.id"
        class="bg-white rounded-lg shadow hover:shadow-md transition-shadow"
      >
        <div class="p-6">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <!-- Assignment Info -->
              <div class="flex items-center space-x-3 mb-2">
                <h3 class="text-xl font-semibold text-gray-900">
                  {{ submission.assignment?.title }}
                </h3>
                <span
                  class="px-2 py-1 text-xs font-semibold rounded-full"
                  :class="getGradeStatusClass(submission)"
                >
                  {{ getGradeStatusText(submission) }}
                </span>
              </div>
              <p class="text-sm text-gray-600 mb-4">
                {{ submission.assignment?.course?.code }} - {{ submission.assignment?.course?.name }}
              </p>

              <!-- Submission Details -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm mb-4">
                <div>
                  <span class="text-gray-500">Submitted:</span>
                  <p class="font-medium text-gray-900">
                    {{ formatDate(submission.submittedAt) }}
                  </p>
                </div>
                <div>
                  <span class="text-gray-500">Due Date:</span>
                  <p class="font-medium" :class="getSubmissionTimingClass(submission)">
                    {{ formatDate(submission.assignment?.dueDate) }}
                  </p>
                </div>
                <div v-if="submission.grade !== null && submission.grade !== undefined">
                  <span class="text-gray-500">Grade:</span>
                  <p class="font-medium text-blue-600">
                    {{ submission.grade }} / {{ submission.assignment?.maxGrade }}
                  </p>
                </div>
                <div v-if="submission.grade !== null && submission.grade !== undefined">
                  <span class="text-gray-500">Percentage:</span>
                  <p class="font-medium" :class="getPercentageClass(submission)">
                    {{ calculatePercentage(submission) }}%
                  </p>
                </div>
              </div>

              <!-- Submission Notes -->
              <div v-if="submission.notes" class="mb-4 p-3 bg-gray-50 rounded-md">
                <p class="text-sm font-medium text-gray-700 mb-1">Your Notes:</p>
                <p class="text-sm text-gray-600">{{ submission.notes }}</p>
              </div>

              <!-- Faculty Feedback -->
              <div v-if="submission.feedback" class="mb-4 p-4 bg-blue-50 rounded-md">
                <p class="text-sm font-medium text-blue-900 mb-1">Faculty Feedback:</p>
                <p class="text-sm text-blue-800">{{ submission.feedback }}</p>
              </div>

              <!-- Submitted Files -->
              <div v-if="submission.filePaths && submission.filePaths.length > 0" class="mb-4">
                <p class="text-sm font-medium text-gray-700 mb-2">Submitted Files:</p>
                <div class="flex flex-wrap gap-2">
                  <button
                    v-for="(filePath, index) in submission.filePaths"
                    :key="index"
                    @click="downloadFile(filePath)"
                    class="flex items-center space-x-2 px-3 py-2 bg-gray-100 hover:bg-gray-200 rounded-md text-sm text-gray-700 transition-colors"
                  >
                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <span>{{ getFileName(filePath) }}</span>
                  </button>
                </div>
              </div>

              <!-- Late Submission Badge -->
              <div v-if="isLateSubmission(submission)" class="inline-flex items-center px-3 py-1 bg-red-100 text-red-800 text-xs font-medium rounded-full">
                <svg class="h-4 w-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                </svg>
                Late Submission
              </div>
            </div>

            <!-- Actions -->
            <div class="ml-6">
              <button
                @click="viewDetails(submission)"
                class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
              >
                View Details
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No submissions yet</h3>
      <p class="mt-1 text-sm text-gray-500">You haven't submitted any assignments.</p>
      <router-link
        to="/student/assignments"
        class="mt-4 inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
      >
        View Assignments
      </router-link>
    </div>

    <!-- Details Modal -->
    <Modal v-model="showDetailsModal" title="Submission Details" size="large">
      <div v-if="selectedSubmission" class="space-y-4">
        <div>
          <h3 class="text-lg font-semibold text-gray-900">{{ selectedSubmission.assignment?.title }}</h3>
          <p class="text-sm text-gray-600">{{ selectedSubmission.assignment?.course?.code }} - {{ selectedSubmission.assignment?.course?.name }}</p>
        </div>

        <div class="border-t pt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-3">Assignment Description</h4>
          <p class="text-gray-900">{{ selectedSubmission.assignment?.description }}</p>
        </div>

        <div class="grid grid-cols-2 gap-4 border-t pt-4">
          <div>
            <h4 class="text-sm font-medium text-gray-700 mb-1">Submitted Date</h4>
            <p class="text-gray-900">{{ formatDate(selectedSubmission.submittedAt) }}</p>
          </div>
          <div>
            <h4 class="text-sm font-medium text-gray-700 mb-1">Due Date</h4>
            <p class="text-gray-900">{{ formatDate(selectedSubmission.assignment?.dueDate) }}</p>
          </div>
          <div v-if="selectedSubmission.grade !== null && selectedSubmission.grade !== undefined">
            <h4 class="text-sm font-medium text-gray-700 mb-1">Grade</h4>
            <p class="text-gray-900">{{ selectedSubmission.grade }} / {{ selectedSubmission.assignment?.maxGrade }} ({{ calculatePercentage(selectedSubmission) }}%)</p>
          </div>
          <div v-if="selectedSubmission.gradedAt">
            <h4 class="text-sm font-medium text-gray-700 mb-1">Graded Date</h4>
            <p class="text-gray-900">{{ formatDate(selectedSubmission.gradedAt) }}</p>
          </div>
        </div>

        <div v-if="selectedSubmission.notes" class="border-t pt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-2">Your Notes</h4>
          <p class="text-gray-900">{{ selectedSubmission.notes }}</p>
        </div>

        <div v-if="selectedSubmission.feedback" class="border-t pt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-2">Faculty Feedback</h4>
          <div class="p-4 bg-blue-50 rounded-md">
            <p class="text-blue-900">{{ selectedSubmission.feedback }}</p>
          </div>
        </div>

        <div v-if="selectedSubmission.filePaths && selectedSubmission.filePaths.length > 0" class="border-t pt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-3">Submitted Files</h4>
          <div class="space-y-2">
            <button
              v-for="(filePath, index) in selectedSubmission.filePaths"
              :key="index"
              @click="downloadFile(filePath)"
              class="w-full flex items-center justify-between p-3 bg-gray-50 hover:bg-gray-100 rounded-md transition-colors"
            >
              <div class="flex items-center space-x-3">
                <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <span class="text-sm font-medium text-gray-900">{{ getFileName(filePath) }}</span>
              </div>
              <svg class="h-5 w-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
      <template #footer>
        <button
          @click="showDetailsModal = false"
          class="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200"
        >
          Close
        </button>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import Modal from '../../components/Modal.vue'

const authStore = useAuthStore()

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

const submissions = ref([])
const loading = ref(true)
const showDetailsModal = ref(false)
const selectedSubmission = ref(null)

onMounted(async () => {
  await loadSubmissions()
})

async function loadSubmissions() {
  try {
    loading.value = true
    const response = await api.getStudentSubmissions(authStore.userId)
    submissions.value = response.data || []
    // Sort by submitted date (newest first)
    submissions.value.sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt))
  } catch (error) {
    console.error('Error loading submissions:', error)
  } finally {
    loading.value = false
  }
}

function viewDetails(submission) {
  selectedSubmission.value = submission
  showDetailsModal.value = true
}

async function downloadFile(filePath) {
  try {
    const response = await api.downloadFile(filePath)
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', getFileName(filePath))
    document.body.appendChild(link)
    link.click()
    link.remove()
  } catch (error) {
    console.error('Error downloading file:', error)
    showNotification('Failed to download file', 'error')
  }
}

function getFileName(filePath) {
  if (!filePath) return 'file'
  return filePath.split('/').pop() || filePath.split('\\').pop() || 'file'
}

function getGradeStatusText(submission) {
  if (submission.grade !== null && submission.grade !== undefined) {
    return 'Graded'
  }
  return 'Pending Review'
}

function getGradeStatusClass(submission) {
  if (submission.grade !== null && submission.grade !== undefined) {
    return 'bg-blue-100 text-blue-800'
  }
  return 'bg-yellow-100 text-yellow-800'
}

function getSubmissionTimingClass(submission) {
  if (isLateSubmission(submission)) {
    return 'text-red-600'
  }
  return 'text-gray-900'
}

function isLateSubmission(submission) {
  if (!submission.submittedAt || !submission.assignment?.dueDate) return false
  return new Date(submission.submittedAt) > new Date(submission.assignment.dueDate)
}

function calculatePercentage(submission) {
  if (!submission.assignment?.maxGrade || submission.grade === null || submission.grade === undefined) return 0
  return Math.round((submission.grade / submission.assignment.maxGrade) * 100)
}

function getPercentageClass(submission) {
  const percentage = calculatePercentage(submission)
  if (percentage >= 90) return 'text-green-600'
  if (percentage >= 80) return 'text-blue-600'
  if (percentage >= 70) return 'text-yellow-600'
  if (percentage >= 60) return 'text-orange-600'
  return 'text-red-600'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
