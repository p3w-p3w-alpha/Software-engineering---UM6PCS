<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900">Student Submissions</h1>
        <p class="mt-2 text-sm text-gray-600">
          Review and grade student assignment submissions
        </p>
      </div>

      <!-- Assignment Selection -->
      <div class="mb-6 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- Assignment Dropdown -->
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Select Assignment
            </label>
            <select
              v-model="selectedAssignmentId"
              @change="loadSubmissions"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Choose an assignment...</option>
              <option v-for="assignment in assignments" :key="assignment.id" :value="assignment.id">
                {{ assignment.courseCode }} - {{ assignment.title }} (Due: {{ formatDate(assignment.dueDate) }})
              </option>
            </select>
          </div>

          <!-- Filter Dropdown -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Filter by Status
            </label>
            <select
              v-model="statusFilter"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">All Submissions</option>
              <option value="PENDING">Pending Review</option>
              <option value="GRADED">Graded</option>
              <option value="NEEDS_REVISION">Needs Revision</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Assignment Info & Submissions -->
      <div v-else-if="selectedAssignmentId && currentAssignment">
        <!-- Assignment Details Card -->
        <div class="mb-6 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <h2 class="text-xl font-semibold text-gray-900">{{ currentAssignment.title }}</h2>
              <p class="mt-2 text-gray-700">{{ currentAssignment.description }}</p>

              <div class="mt-4 flex items-center space-x-6 text-sm text-gray-600">
                <div class="flex items-center">
                  <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  Due: {{ formatDate(currentAssignment.dueDate) }}
                </div>
                <div class="flex items-center">
                  <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                  </svg>
                  Max Grade: {{ currentAssignment.maxGrade }} points
                </div>
              </div>
            </div>

            <!-- Statistics -->
            <div class="ml-6">
              <div class="grid grid-cols-3 gap-4">
                <div class="text-center">
                  <p class="text-2xl font-bold text-blue-600">{{ submissionStats.total }}</p>
                  <p class="text-xs text-gray-600">Total</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-yellow-600">{{ submissionStats.pending }}</p>
                  <p class="text-xs text-gray-600">Pending</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-green-600">{{ submissionStats.graded }}</p>
                  <p class="text-xs text-gray-600">Graded</p>
                </div>
              </div>
              <div v-if="submissionStats.averageGrade > 0" class="mt-4 text-center">
                <p class="text-sm text-gray-600">Average Grade</p>
                <p class="text-xl font-semibold text-gray-900">
                  {{ submissionStats.averageGrade.toFixed(1) }} / {{ currentAssignment.maxGrade }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Submissions Table -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Student
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Submitted
                  </th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Grade
                  </th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr
                  v-for="submission in filteredSubmissions"
                  :key="submission.id"
                  class="hover:bg-gray-50"
                >
                  <!-- Student Info -->
                  <td class="px-6 py-4">
                    <div class="flex items-center">
                      <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold">
                        {{ getInitials(submission.studentName) }}
                      </div>
                      <div class="ml-4">
                        <p class="text-sm font-medium text-gray-900">{{ submission.studentName }}</p>
                        <p class="text-xs text-gray-500">{{ submission.studentEmail }}</p>
                      </div>
                    </div>
                  </td>

                  <!-- Submission Date -->
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div>
                      <p class="text-sm text-gray-900">{{ formatDateTime(submission.submittedAt) }}</p>
                      <p v-if="submission.isLate" class="text-xs text-red-600 font-medium">LATE SUBMISSION</p>
                      <p v-else class="text-xs text-green-600">On Time</p>
                    </div>
                  </td>

                  <!-- Status -->
                  <td class="px-6 py-4 whitespace-nowrap text-center">
                    <span
                      class="inline-block px-3 py-1 text-xs font-medium rounded-full"
                      :class="getStatusBadge(submission.status)"
                    >
                      {{ submission.status || 'PENDING' }}
                    </span>
                  </td>

                  <!-- Grade -->
                  <td class="px-6 py-4 whitespace-nowrap text-center">
                    <div v-if="submission.grade !== null && submission.grade !== undefined">
                      <p class="text-sm font-semibold text-gray-900">
                        {{ submission.grade }} / {{ currentAssignment.maxGrade }}
                      </p>
                      <p class="text-xs text-gray-500">
                        {{ ((submission.grade / currentAssignment.maxGrade) * 100).toFixed(0) }}%
                      </p>
                    </div>
                    <p v-else class="text-sm text-gray-400">Not Graded</p>
                  </td>

                  <!-- Actions -->
                  <td class="px-6 py-4 whitespace-nowrap text-center">
                    <div class="flex items-center justify-center space-x-2">
                      <button
                        @click="viewSubmission(submission)"
                        class="text-blue-600 hover:text-blue-700"
                        title="View Details"
                      >
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      </button>

                      <button
                        @click="downloadFiles(submission)"
                        class="text-green-600 hover:text-green-700"
                        title="Download Files"
                      >
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                      </button>

                      <button
                        @click="gradeSubmission(submission)"
                        class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors"
                      >
                        {{ submission.grade !== null ? 'Edit Grade' : 'Grade' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Empty State -->
          <div v-if="filteredSubmissions.length === 0" class="text-center py-12">
            <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="mt-4 text-lg font-medium text-gray-900">No submissions found</h3>
            <p class="mt-2 text-sm text-gray-600">
              {{ statusFilter ? 'No submissions match the selected filter.' : 'Students have not submitted this assignment yet.' }}
            </p>
          </div>
        </div>
      </div>

      <!-- Initial State -->
      <div v-else class="text-center py-12">
        <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <h3 class="mt-4 text-lg font-medium text-gray-900">Select an assignment</h3>
        <p class="mt-2 text-sm text-gray-600">
          Choose an assignment from the dropdown to view student submissions
        </p>
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

const loading = ref(false)
const assignments = ref([])
const submissions = ref([])
const selectedAssignmentId = ref('')
const statusFilter = ref('')

const currentAssignment = computed(() => {
  return assignments.value.find(a => a.id === selectedAssignmentId.value)
})

const filteredSubmissions = computed(() => {
  if (!statusFilter.value) return submissions.value
  return submissions.value.filter(s => (s.status || 'PENDING') === statusFilter.value)
})

const submissionStats = computed(() => {
  const total = submissions.value.length
  const pending = submissions.value.filter(s => !s.status || s.status === 'PENDING').length
  const graded = submissions.value.filter(s => s.status === 'GRADED').length

  const gradedSubmissions = submissions.value.filter(s => s.grade !== null && s.grade !== undefined)
  const averageGrade = gradedSubmissions.length > 0
    ? gradedSubmissions.reduce((sum, s) => sum + s.grade, 0) / gradedSubmissions.length
    : 0

  return { total, pending, graded, averageGrade }
})

onMounted(async () => {
  await loadAssignments()
})

async function loadAssignments() {
  try {
    loading.value = true
    const response = await api.getFacultyAssignments(authStore.userId)
    assignments.value = response.data || []
  } catch (error) {
    console.error('Error loading assignments:', error)
    alert('Failed to load assignments. Please try again.')
  } finally {
    loading.value = false
  }
}

async function loadSubmissions() {
  if (!selectedAssignmentId.value) {
    submissions.value = []
    return
  }

  try {
    loading.value = true
    const response = await api.getAssignmentSubmissions(selectedAssignmentId.value)
    submissions.value = response.data || []
  } catch (error) {
    console.error('Error loading submissions:', error)
    alert('Failed to load submissions. Please try again.')
  } finally {
    loading.value = false
  }
}

function gradeSubmission(submission) {
  router.push(`/faculty/submissions/${submission.id}/grade`)
}

function viewSubmission(submission) {
  // For now, just navigate to grade page
  gradeSubmission(submission)
}

async function downloadFiles(submission) {
  if (!submission.filePath) {
    alert('No files attached to this submission.')
    return
  }

  try {
    const response = await api.downloadFile(submission.filePath)
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', submission.fileName || 'submission.zip')
    document.body.appendChild(link)
    link.click()
    link.remove()
  } catch (error) {
    console.error('Error downloading file:', error)
    alert('Failed to download file. Please try again.')
  }
}

function getInitials(name) {
  if (!name) return '?'
  const parts = name.split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return name.substring(0, 2).toUpperCase()
}

function getStatusBadge(status) {
  const badges = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'GRADED': 'bg-green-100 text-green-800',
    'NEEDS_REVISION': 'bg-red-100 text-red-800',
    'RESUBMIT': 'bg-orange-100 text-orange-800'
  }
  return badges[status] || 'bg-gray-100 text-gray-800'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function formatDateTime(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit'
  })
}
</script>
