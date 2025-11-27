<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <button
          @click="$router.back()"
          class="flex items-center text-gray-600 hover:text-gray-900 mb-4"
        >
          <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          Back to Submissions
        </button>

        <h1 class="text-3xl font-bold text-gray-900">Grade Submission</h1>
        <p class="mt-2 text-sm text-gray-600">
          Review and grade student assignment submission
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Main Content -->
      <div v-else class="space-y-6">
        <!-- Student & Assignment Info -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-start justify-between">
            <div class="flex items-start space-x-4">
              <!-- Student Avatar -->
              <div class="h-16 w-16 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xl font-semibold">
                {{ getInitials(submission.studentName) }}
              </div>

              <!-- Details -->
              <div>
                <h2 class="text-xl font-semibold text-gray-900">{{ submission.studentName }}</h2>
                <p class="text-sm text-gray-600 mt-1">{{ submission.studentEmail }}</p>

                <div class="mt-3">
                  <p class="text-sm font-medium text-gray-900">{{ submission.assignmentTitle }}</p>
                  <div class="flex items-center space-x-4 mt-1 text-sm text-gray-600">
                    <span>Submitted: {{ formatDateTime(submission.submittedAt) }}</span>
                    <span v-if="submission.isLate" class="text-red-600 font-medium">LATE SUBMISSION</span>
                    <span v-else class="text-green-600">On Time</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Current Grade (if exists) -->
            <div v-if="submission.grade !== null && submission.grade !== undefined" class="text-right">
              <p class="text-sm text-gray-600">Current Grade</p>
              <p class="text-3xl font-bold text-blue-600">{{ submission.grade }}</p>
              <p class="text-sm text-gray-600">/ {{ submission.maxGrade }}</p>
            </div>
          </div>
        </div>

        <!-- Submission Content -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Submission Content</h3>

          <!-- Student Comments -->
          <div v-if="submission.comments" class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Student Comments
            </label>
            <div class="p-4 bg-gray-50 rounded-lg border border-gray-200">
              <p class="text-gray-700">{{ submission.comments }}</p>
            </div>
          </div>

          <!-- Attached Files -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Attached Files
            </label>
            <div v-if="submission.files && submission.files.length > 0" class="space-y-2">
              <div
                v-for="(file, index) in submission.files"
                :key="index"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-200"
              >
                <div class="flex items-center space-x-3">
                  <svg class="h-8 w-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                  </svg>
                  <div>
                    <p class="text-sm font-medium text-gray-900">{{ file.name }}</p>
                    <p class="text-xs text-gray-500">{{ formatFileSize(file.size) }}</p>
                  </div>
                </div>
                <button
                  @click="downloadFile(file)"
                  class="px-3 py-1 text-sm text-blue-600 hover:text-blue-700 font-medium"
                >
                  Download
                </button>
              </div>
            </div>
            <div v-else class="p-4 bg-gray-50 rounded-lg border border-gray-200 text-center text-gray-500">
              No files attached
            </div>
          </div>
        </div>

        <!-- Grading Section -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Assignment</h3>

          <form @submit.prevent="submitGrade" class="space-y-6">
            <!-- Grade Input -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Grade <span class="text-red-500">*</span>
              </label>
              <div class="flex items-center space-x-4">
                <input
                  v-model.number="gradeForm.grade"
                  type="number"
                  min="0"
                  :max="submission.maxGrade"
                  step="0.5"
                  required
                  class="w-32 px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 text-lg font-semibold"
                />
                <span class="text-lg text-gray-600">/ {{ submission.maxGrade }}</span>
                <div class="flex-1"></div>
                <div v-if="gradeForm.grade !== null && gradeForm.grade !== ''" class="text-right">
                  <p class="text-sm text-gray-600">Percentage</p>
                  <p class="text-2xl font-bold" :class="getGradeColor(gradePercentage)">
                    {{ gradePercentage.toFixed(1) }}%
                  </p>
                </div>
              </div>
            </div>

            <!-- Feedback -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Feedback <span class="text-red-500">*</span>
              </label>
              <textarea
                v-model="gradeForm.feedback"
                rows="6"
                required
                placeholder="Provide detailed feedback for the student..."
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
              ></textarea>
              <p class="mt-1 text-xs text-gray-500">
                {{ gradeForm.feedback.length }} characters
              </p>
            </div>

            <!-- Status -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Status <span class="text-red-500">*</span>
              </label>
              <select
                v-model="gradeForm.status"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="GRADED">Graded - Accepted</option>
                <option value="NEEDS_REVISION">Needs Revision</option>
                <option value="RESUBMIT">Resubmit Required</option>
              </select>
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-lg">
              <div class="flex">
                <svg class="h-5 w-5 text-red-600 mr-3" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                </svg>
                <p class="text-sm text-red-800">{{ errorMessage }}</p>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="flex items-center justify-between pt-4 border-t border-gray-200">
              <button
                @click="$router.back()"
                type="button"
                class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>

              <button
                type="submit"
                :disabled="submitting || !isFormValid"
                class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
              >
                <LoadingSpinner v-if="submitting" size="small" color="white" class="mr-2" />
                <span>{{ submitting ? 'Submitting...' : 'Submit Grade' }}</span>
              </button>
            </div>
          </form>
        </div>

        <!-- Previous Feedback (if regrading) -->
        <div v-if="submission.previousFeedback" class="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
          <h3 class="text-lg font-semibold text-yellow-900 mb-2">Previous Feedback</h3>
          <p class="text-sm text-yellow-800">{{ submission.previousFeedback }}</p>
          <p class="text-xs text-yellow-700 mt-2">
            Previous Grade: {{ submission.previousGrade }} / {{ submission.maxGrade }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const errorMessage = ref('')

const submission = ref({})
const gradeForm = ref({
  grade: null,
  feedback: '',
  status: 'GRADED'
})

const gradePercentage = computed(() => {
  if (gradeForm.value.grade === null || gradeForm.value.grade === '') return 0
  return (gradeForm.value.grade / submission.value.maxGrade) * 100
})

const isFormValid = computed(() => {
  return gradeForm.value.grade !== null &&
    gradeForm.value.grade !== '' &&
    gradeForm.value.grade >= 0 &&
    gradeForm.value.grade <= submission.value.maxGrade &&
    gradeForm.value.feedback.trim().length > 0 &&
    gradeForm.value.status !== ''
})

onMounted(async () => {
  await loadSubmission()
})

async function loadSubmission() {
  try {
    loading.value = true
    const submissionId = route.params.id

    const response = await api.getSubmissionById(submissionId)
    submission.value = response.data

    // Pre-fill form if already graded
    if (submission.value.grade !== null && submission.value.grade !== undefined) {
      gradeForm.value.grade = submission.value.grade
      gradeForm.value.feedback = submission.value.feedback || ''
      gradeForm.value.status = submission.value.status || 'GRADED'
    }
  } catch (error) {
    console.error('Error loading submission:', error)
    errorMessage.value = 'Failed to load submission. Please try again.'
  } finally {
    loading.value = false
  }
}

async function submitGrade() {
  if (!isFormValid.value || submitting.value) return

  try {
    submitting.value = true
    errorMessage.value = ''

    const gradeData = {
      grade: gradeForm.value.grade,
      feedback: gradeForm.value.feedback.trim(),
      status: gradeForm.value.status,
      gradedAt: new Date().toISOString()
    }

    await api.gradeSubmission(submission.value.id, gradeData)

    alert('Grade submitted successfully!')
    router.back()
  } catch (error) {
    console.error('Error submitting grade:', error)
    errorMessage.value = error.response?.data?.message || 'Failed to submit grade. Please try again.'
  } finally {
    submitting.value = false
  }
}

async function downloadFile(file) {
  try {
    const response = await api.downloadFile(file.path)
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', file.name)
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

function getGradeColor(percentage) {
  if (percentage >= 90) return 'text-green-600'
  if (percentage >= 80) return 'text-blue-600'
  if (percentage >= 70) return 'text-yellow-600'
  if (percentage >= 60) return 'text-orange-600'
  return 'text-red-600'
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

function formatFileSize(bytes) {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}
</script>
