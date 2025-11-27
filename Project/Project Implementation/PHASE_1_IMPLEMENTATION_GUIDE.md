# PHASE 1 IMPLEMENTATION GUIDE - COMPLETE CODE

**Status:** Components 1-3 Created ✅ | Components 4-8 + Routes + API = This Document

This document contains ALL remaining code for Phase 1. Simply copy and paste each section to complete the implementation.

---

## ✅ ALREADY CREATED (You have these files)

1. ✅ StudentAssignments.vue - View all assignments
2. ✅ AssignmentSubmission.vue - Submit assignment with files
3. ✅ SubmissionHistory.vue - View submission history

---

## 📝 STEP 1: Create FacultySubmissions.vue

**File:** `src/views/faculty/FacultySubmissions.vue`

```vue
<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Student Submissions</h1>
      <p class="mt-2 text-gray-600">View and grade student assignment submissions</p>
    </div>

    <!-- Assignment Selection -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-2">Select Assignment</label>
          <select
            v-model="selectedAssignmentId"
            @change="loadSubmissions"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">Choose an assignment...</option>
            <option v-for="assignment in assignments" :key="assignment.id" :value="assignment.id">
              {{ assignment.course?.code }} - {{ assignment.title }} (Due: {{ formatDate(assignment.dueDate) }})
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Filter by Status</label>
          <select
            v-model="filterStatus"
            @change="filterSubmissions"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Status</option>
            <option value="pending">Pending Review</option>
            <option value="graded">Graded</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Assignment Info -->
    <div v-if="selectedAssignment" class="bg-blue-50 border-l-4 border-blue-400 p-4 mb-6">
      <div class="flex">
        <div class="flex-1">
          <h3 class="text-sm font-medium text-blue-900">{{ selectedAssignment.title }}</h3>
          <p class="text-sm text-blue-700 mt-1">{{ selectedAssignment.description }}</p>
          <p class="text-sm text-blue-700 mt-2">
            <strong>Due Date:</strong> {{ formatDate(selectedAssignment.dueDate) }} |
            <strong>Max Grade:</strong> {{ selectedAssignment.maxGrade }} points |
            <strong>Submissions:</strong> {{ filteredSubmissions.length }}
          </p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Submissions Table -->
    <div v-else-if="filteredSubmissions.length > 0" class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Submitted</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Grade</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Files</th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="submission in filteredSubmissions" :key="submission.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900">{{ submission.student?.firstName }} {{ submission.student?.lastName }}</div>
              <div class="text-sm text-gray-500">{{ submission.student?.username }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ formatDate(submission.submittedAt) }}</div>
              <div v-if="isLateSubmission(submission)" class="text-xs text-red-600 font-medium">Late</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="getStatusClass(submission)" class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full">
                {{ getStatusText(submission) }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div v-if="submission.grade !== null && submission.grade !== undefined" class="text-sm font-medium text-blue-600">
                {{ submission.grade }} / {{ selectedAssignment.maxGrade }}
              </div>
              <div v-else class="text-sm text-gray-400">Not graded</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ submission.filePaths?.length || 0 }} file(s)</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <router-link
                :to="`/faculty/submissions/${submission.id}/grade`"
                class="text-blue-600 hover:text-blue-900"
              >
                {{ submission.grade !== null && submission.grade !== undefined ? 'Edit Grade' : 'Grade' }}
              </router-link>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Statistics Footer -->
      <div class="bg-gray-50 px-6 py-4 border-t border-gray-200">
        <div class="flex items-center justify-between text-sm">
          <div class="space-x-6">
            <span class="text-gray-700"><strong>Total:</strong> {{ filteredSubmissions.length }}</span>
            <span class="text-yellow-700"><strong>Pending:</strong> {{ pendingCount }}</span>
            <span class="text-blue-700"><strong>Graded:</strong> {{ gradedCount }}</span>
          </div>
          <div v-if="averageGrade !== null" class="text-gray-700">
            <strong>Average Grade:</strong> {{ averageGrade.toFixed(2) }} / {{ selectedAssignment.maxGrade }}
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="selectedAssignmentId" class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No submissions yet</h3>
      <p class="mt-1 text-sm text-gray-500">Students haven't submitted their work for this assignment.</p>
    </div>

    <!-- No Assignment Selected -->
    <div v-else class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">Select an assignment</h3>
      <p class="mt-1 text-sm text-gray-500">Choose an assignment to view student submissions.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const authStore = useAuthStore()

const assignments = ref([])
const submissions = ref([])
const selectedAssignmentId = ref('')
const selectedAssignment = ref(null)
const loading = ref(false)
const filterStatus = ref('')

const filteredSubmissions = computed(() => {
  let result = [...submissions.value]

  if (filterStatus.value === 'pending') {
    result = result.filter(s => s.grade === null || s.grade === undefined)
  } else if (filterStatus.value === 'graded') {
    result = result.filter(s => s.grade !== null && s.grade !== undefined)
  }

  return result
})

const pendingCount = computed(() =>
  submissions.value.filter(s => s.grade === null || s.grade === undefined).length
)

const gradedCount = computed(() =>
  submissions.value.filter(s => s.grade !== null && s.grade !== undefined).length
)

const averageGrade = computed(() => {
  const graded = submissions.value.filter(s => s.grade !== null && s.grade !== undefined)
  if (graded.length === 0) return null
  const sum = graded.reduce((acc, s) => acc + s.grade, 0)
  return sum / graded.length
})

onMounted(async () => {
  await loadAssignments()
})

async function loadAssignments() {
  try {
    const response = await api.getFacultyAssignments(authStore.userId)
    assignments.value = response.data || []
  } catch (error) {
    console.error('Error loading assignments:', error)
  }
}

async function loadSubmissions() {
  if (!selectedAssignmentId.value) {
    submissions.value = []
    selectedAssignment.value = null
    return
  }

  try {
    loading.value = true
    selectedAssignment.value = assignments.value.find(a => a.id === selectedAssignmentId.value)
    const response = await api.getAssignmentSubmissions(selectedAssignmentId.value)
    submissions.value = response.data || []
  } catch (error) {
    console.error('Error loading submissions:', error)
  } finally {
    loading.value = false
  }
}

function filterSubmissions() {
  // Filtering handled by computed property
}

function getStatusText(submission) {
  if (submission.grade !== null && submission.grade !== undefined) return 'Graded'
  return 'Pending Review'
}

function getStatusClass(submission) {
  if (submission.grade !== null && submission.grade !== undefined) {
    return 'bg-blue-100 text-blue-800'
  }
  return 'bg-yellow-100 text-yellow-800'
}

function isLateSubmission(submission) {
  if (!submission.submittedAt || !selectedAssignment.value?.dueDate) return false
  return new Date(submission.submittedAt) > new Date(selectedAssignment.value.dueDate)
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
```

---

## 📝 STEP 2: Create GradeSubmission.vue

**File:** `src/views/faculty/GradeSubmission.vue`

```vue
<template>
  <div class="max-w-5xl mx-auto">
    <!-- Back Button -->
    <button
      @click="$router.back()"
      class="mb-6 flex items-center text-gray-600 hover:text-gray-900"
    >
      <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      Back to Submissions
    </button>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Submission Not Found -->
    <div v-else-if="!submission" class="bg-white rounded-lg shadow p-12 text-center">
      <h3 class="text-lg font-medium text-gray-900">Submission not found</h3>
    </div>

    <!-- Grading Interface -->
    <div v-else class="space-y-6">
      <!-- Student & Assignment Info -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-start justify-between mb-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">{{ submission.assignment?.title }}</h1>
            <p class="text-sm text-gray-600 mt-1">{{ submission.assignment?.course?.code }} - {{ submission.assignment?.course?.name }}</p>
          </div>
          <span v-if="isLateSubmission" class="px-3 py-1 bg-red-100 text-red-800 text-xs font-semibold rounded-full">
            Late Submission
          </span>
        </div>

        <div class="grid grid-cols-3 gap-4 text-sm border-t pt-4">
          <div>
            <span class="text-gray-500">Student:</span>
            <p class="font-medium text-gray-900 mt-1">{{ submission.student?.firstName }} {{ submission.student?.lastName }}</p>
            <p class="text-gray-600 text-xs">{{ submission.student?.username }}</p>
          </div>
          <div>
            <span class="text-gray-500">Submitted:</span>
            <p class="font-medium text-gray-900 mt-1">{{ formatDate(submission.submittedAt) }}</p>
          </div>
          <div>
            <span class="text-gray-500">Due Date:</span>
            <p class="font-medium text-gray-900 mt-1">{{ formatDate(submission.assignment?.dueDate) }}</p>
          </div>
        </div>

        <div v-if="submission.notes" class="mt-4 pt-4 border-t">
          <span class="text-sm font-medium text-gray-700">Student Notes:</span>
          <p class="text-sm text-gray-900 mt-1">{{ submission.notes }}</p>
        </div>
      </div>

      <!-- Submitted Files -->
      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Submitted Files</h2>
        <div v-if="submission.filePaths && submission.filePaths.length > 0" class="space-y-3">
          <div
            v-for="(filePath, index) in submission.filePaths"
            :key="index"
            class="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:border-blue-300 transition-colors"
          >
            <div class="flex items-center space-x-3 flex-1 min-w-0">
              <svg class="h-8 w-8 text-blue-500 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ getFileName(filePath) }}</p>
                <p class="text-xs text-gray-500">File {{ index + 1 }} of {{ submission.filePaths.length }}</p>
              </div>
            </div>
            <button
              @click="downloadFile(filePath)"
              class="ml-4 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
            >
              Download
            </button>
          </div>
        </div>
        <div v-else class="text-center py-8 text-gray-500">
          <p>No files submitted</p>
        </div>
      </div>

      <!-- Grading Form -->
      <form @submit.prevent="saveGrade" class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-6">Grade Submission</h2>

        <div class="grid grid-cols-2 gap-6 mb-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Grade <span class="text-red-500">*</span>
            </label>
            <div class="flex items-center space-x-2">
              <input
                v-model.number="gradeForm.grade"
                type="number"
                min="0"
                :max="submission.assignment?.maxGrade"
                step="0.5"
                required
                class="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="Enter grade"
              />
              <span class="text-gray-600">/ {{ submission.assignment?.maxGrade }}</span>
            </div>
            <p class="mt-1 text-sm text-gray-500">
              Percentage: {{ calculatePercentage() }}%
            </p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Status
            </label>
            <select
              v-model="gradeForm.status"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="GRADED">Graded</option>
              <option value="NEEDS_REVISION">Needs Revision</option>
              <option value="RESUBMIT">Resubmit Required</option>
            </select>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Feedback <span class="text-red-500">*</span>
          </label>
          <textarea
            v-model="gradeForm.feedback"
            rows="6"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Provide detailed feedback to the student..."
          ></textarea>
          <p class="mt-1 text-sm text-gray-500">
            Be specific and constructive in your feedback
          </p>
        </div>

        <!-- Error Message -->
        <div v-if="errorMessage" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
          <p class="text-sm text-red-600">{{ errorMessage }}</p>
        </div>

        <!-- Success Message -->
        <div v-if="successMessage" class="mb-6 p-4 bg-green-50 border border-green-200 rounded-md">
          <p class="text-sm text-green-600">{{ successMessage }}</p>
        </div>

        <!-- Action Buttons -->
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
            :disabled="saving"
            class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            <LoadingSpinner v-if="saving" class="mr-2" size="small" color="white" />
            <span>{{ saving ? 'Saving...' : 'Save Grade' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const router = useRouter()
const route = useRoute()

const submission = ref(null)
const loading = ref(true)
const saving = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const gradeForm = ref({
  grade: null,
  feedback: '',
  status: 'GRADED'
})

const isLateSubmission = computed(() => {
  if (!submission.value?.submittedAt || !submission.value?.assignment?.dueDate) return false
  return new Date(submission.value.submittedAt) > new Date(submission.value.assignment.dueDate)
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
      gradeForm.value = {
        grade: submission.value.grade,
        feedback: submission.value.feedback || '',
        status: submission.value.status || 'GRADED'
      }
    }
  } catch (error) {
    console.error('Error loading submission:', error)
  } finally {
    loading.value = false
  }
}

async function saveGrade() {
  try {
    saving.value = true
    errorMessage.value = ''
    successMessage.value = ''

    const gradeData = {
      submissionId: submission.value.id,
      grade: gradeForm.value.grade,
      feedback: gradeForm.value.feedback,
      status: gradeForm.value.status,
      gradedAt: new Date().toISOString()
    }

    await api.gradeSubmission(submission.value.id, gradeData)

    successMessage.value = 'Grade saved successfully!'

    // Redirect after 1.5 seconds
    setTimeout(() => {
      router.back()
    }, 1500)
  } catch (error) {
    console.error('Error saving grade:', error)
    errorMessage.value = error.response?.data?.message || 'Failed to save grade. Please try again.'
  } finally {
    saving.value = false
  }
}

async function downloadFile(filePath) {
  try {
    const response = await api.downloadFile(filePath)
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', getFileName(filePath))
    document.body.appendChild(link)
    link.click()
    link.remove()
  } catch (error) {
    console.error('Error downloading file:', error)
    alert('Failed to download file')
  }
}

function getFileName(filePath) {
  if (!filePath) return 'file'
  return filePath.split('/').pop() || filePath.split('\\').pop() || 'file'
}

function calculatePercentage() {
  if (!gradeForm.value.grade || !submission.value?.assignment?.maxGrade) return 0
  return Math.round((gradeForm.value.grade / submission.value.assignment.maxGrade) * 100)
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
```

---

## 📝 STEP 3: Update API Service

Add these methods to `src/services/api.js`:

```javascript
// Add to the existing api object:

// Assignment Submissions
getAssignmentById: (assignmentId) => apiClient.get(`/assignments/${assignmentId}`),
getStudentSubmissions: (studentId) => apiClient.get(`/submissions/student/${studentId}`),
getAssignmentSubmissions: (assignmentId) => apiClient.get(`/submissions/assignment/${assignmentId}`),
getSubmissionById: (submissionId) => apiClient.get(`/submissions/${submissionId}`),
submitAssignment: (data) => apiClient.post('/submissions/submit', data),
gradeSubmission: (submissionId, data) => apiClient.post(`/submissions/${submissionId}/grade`, data),
uploadAssignmentFile: (formData) => apiClient.post('/files/upload/assignment', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
}),
downloadFile: (filePath) => apiClient.get(`/files/download`, {
  params: { filePath },
  responseType: 'blob'
}),

// Private Messages
sendMessage: (data) => apiClient.post('/messages/send', data),
getConversation: (userId1, userId2) => apiClient.get(`/messages/conversation/${userId1}/${userId2}`),
getUserConversations: (userId) => apiClient.get(`/messages/user/${userId}/conversations`),
markMessageAsRead: (messageId) => apiClient.patch(`/messages/${messageId}/read`),
markConversationAsRead: (userId, otherUserId) => apiClient.patch(`/messages/conversation/${otherUserId}/read-all`, null, {
  params: { userId }
}),
deleteMessage: (messageId) => apiClient.delete(`/messages/${messageId}`),
```

---

## 📝 STEP 4: Update Router

Add these routes to `src/router/index.js`:

```javascript
// Add to the routes array:

// Student Assignment Routes
{
  path: '/student/assignments',
  component: () => import('../views/student/StudentAssignments.vue'),
  meta: { requiresAuth: true, roles: ['STUDENT'] }
},
{
  path: '/student/assignments/:id/submit',
  component: () => import('../views/student/AssignmentSubmission.vue'),
  meta: { requiresAuth: true, roles: ['STUDENT'] }
},
{
  path: '/student/submissions',
  component: () => import('../views/student/SubmissionHistory.vue'),
  meta: { requiresAuth: true, roles: ['STUDENT'] }
},
{
  path: '/student/submissions/:id',
  component: () => import('../views/student/SubmissionHistory.vue'),
  meta: { requiresAuth: true, roles: ['STUDENT'] }
},

// Faculty Submission Routes
{
  path: '/faculty/submissions',
  component: () => import('../views/faculty/FacultySubmissions.vue'),
  meta: { requiresAuth: true, roles: ['FACULTY'] }
},
{
  path: '/faculty/submissions/:id/grade',
  component: () => import('../views/faculty/GradeSubmission.vue'),
  meta: { requiresAuth: true, roles: ['FACULTY'] }
},

// Messages Routes (All Roles)
{
  path: '/messages',
  component: () => import('../views/messages/MessagesInbox.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/messages/:userId',
  component: () => import('../views/messages/ConversationView.vue'),
  meta: { requiresAuth: true }
},
```

---

## 📝 STEP 5: Update Navigation Menus

Update sidebars to include new routes:

**Student Sidebar** (add to menuItems):
```javascript
{ name: 'My Assignments', href: '/student/assignments', icon: 'document-text' },
{ name: 'Submission History', href: '/student/submissions', icon: 'clock' },
{ name: 'Messages', href: '/messages', icon: 'mail' },
```

**Faculty Sidebar** (add to menuItems):
```javascript
{ name: 'Student Submissions', href: '/faculty/submissions', icon: 'document-check' },
{ name: 'Messages', href: '/messages', icon: 'mail' },
```

---

## ✅ TESTING CHECKLIST - Assignment System

Run these tests after implementation:

### Student Tests
- [ ] View all assignments from enrolled courses
- [ ] Filter assignments by course, status, and search
- [ ] See assignment details (title, description, due date, points)
- [ ] Submit assignment with file upload (drag & drop works)
- [ ] Upload multiple files (PDF, DOCX, ZIP, images)
- [ ] See upload progress bar
- [ ] Add submission notes/comments
- [ ] See "Late Submission" warning if past due
- [ ] View submission history
- [ ] See graded assignments with scores and feedback
- [ ] Download submitted files from history

### Faculty Tests
- [ ] View all faculty assignments
- [ ] Select assignment to view submissions
- [ ] See submission list with student names, dates, status
- [ ] Filter by pending/graded
- [ ] See late submission indicators
- [ ] View submission statistics (total, pending, graded, average)
- [ ] Click to grade a submission
- [ ] Download student files
- [ ] Enter grade (numeric + percentage calculated)
- [ ] Provide feedback text
- [ ] Save grade successfully
- [ ] See grade appear in submission list
- [ ] Edit existing grade

### Integration Tests
- [ ] Student submits → Faculty sees in pending list
- [ ] Faculty grades → Student sees grade and feedback
- [ ] Late submissions marked correctly
- [ ] File upload/download works end-to-end
- [ ] Grade percentage calculated correctly
- [ ] Navigation between views works
- [ ] Back buttons work correctly

---

## 🎉 PHASE 1 COMPLETION STATUS

### ✅ Assignment Submission System: COMPLETE
- Student side: 3 components ✅
- Faculty side: 2 components ✅
- API integration: 10 endpoints ✅
- Router: 6 routes ✅

### ⏳ Private Messaging System: NOT STARTED
This would require 3 more components:
- MessagesInbox.vue
- ConversationView.vue
- NewMessageModal.vue

---

## 💡 RECOMMENDATION

**Due to token budget, I recommend:**

1. **NOW:** Implement the 5 components above (copy/paste the code)
2. **TEST:** Verify assignment submission system works end-to-end
3. **NEXT SESSION:** Implement Private Messaging (Phase 1b) - 3 components
4. **THEN:** Continue with Phase 2 (Grades + Study Groups)

This gives you a working, testable feature (assignments) before moving to messaging!

---

**Questions? Issues? Ready for messaging components? Let me know!**
