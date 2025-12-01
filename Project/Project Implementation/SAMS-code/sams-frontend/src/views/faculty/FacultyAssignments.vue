<template>
  <div class="p-6">
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

    <!-- Header -->
    <div class="mb-6 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Assignments</h1>
        <p class="text-gray-600 mt-2">Create and manage course assignments</p>
      </div>
      <button @click="showCreateModal = true" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium">
        <svg class="h-5 w-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Create Assignment
      </button>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Filter by Course</label>
          <select v-model="filterCourse" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            <option value="">All Courses</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.courseCode }} - {{ course.courseName }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Filter by Status</label>
          <select v-model="filterStatus" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            <option value="">All Status</option>
            <option value="active">Active</option>
            <option value="overdue">Overdue</option>
            <option value="upcoming">Upcoming</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="refreshAssignments" class="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors">
            <svg class="h-5 w-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Refresh
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- No Assignments -->
    <div v-else-if="filteredAssignments.length === 0" class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No assignments found</h3>
      <p class="mt-2 text-gray-500">Create your first assignment to get started</p>
      <button @click="showCreateModal = true" class="mt-6 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
        Create Assignment
      </button>
    </div>

    <!-- Assignments List -->
    <div v-else class="space-y-4">
      <div
        v-for="assignment in filteredAssignments"
        :key="assignment.id"
        class="bg-white rounded-lg shadow hover:shadow-md transition-shadow"
      >
        <div class="p-6">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-3 mb-2">
                <h3 class="text-xl font-bold text-gray-900">{{ assignment.title }}</h3>
                <span v-if="assignment.isOverdue" class="px-3 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">
                  Overdue
                </span>
                <span v-else-if="!assignment.active" class="px-3 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-800">
                  Inactive
                </span>
                <span v-else class="px-3 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                  Active
                </span>
              </div>
              <p class="text-sm text-gray-600 mb-3">{{ assignment.courseName }}</p>
              <p class="text-gray-700 mb-4">{{ assignment.description }}</p>

              <!-- Assignment Details -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                <div>
                  <p class="text-xs text-gray-600">Due Date</p>
                  <p class="text-sm font-medium text-gray-900">{{ formatDateTime(assignment.dueDate) }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-600">Max Points</p>
                  <p class="text-sm font-medium text-gray-900">{{ assignment.maxPoints }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-600">Submissions</p>
                  <p class="text-sm font-medium text-gray-900">{{ assignment.submissionCount || 0 }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-600">Late Submissions</p>
                  <p class="text-sm font-medium" :class="assignment.allowLateSubmissions ? 'text-green-600' : 'text-red-600'">
                    {{ assignment.allowLateSubmissions ? 'Allowed' : 'Not Allowed' }}
                  </p>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="flex gap-2">
                <button
                  @click="viewSubmissions(assignment)"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  View Submissions ({{ assignment.submissionCount || 0 }})
                </button>
                <button
                  @click="editAssignment(assignment)"
                  class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium"
                >
                  Edit
                </button>
                <button
                  @click="confirmDelete(assignment)"
                  class="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors text-sm font-medium"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Assignment Modal -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click="closeModals">
      <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full m-4 max-h-[90vh] overflow-y-auto" @click.stop>
        <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">
            {{ showEditModal ? 'Edit Assignment' : 'Create New Assignment' }}
          </h3>
          <button @click="closeModals" class="text-gray-400 hover:text-gray-600">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form @submit.prevent="showEditModal ? updateAssignment() : createAssignment()" class="px-6 py-4 space-y-4">
          <!-- Course Selection -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Course *</label>
            <select v-model="assignmentForm.courseId" required class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="">Select a course</option>
              <option v-for="course in courses" :key="course.id" :value="course.id">
                {{ course.courseCode }} - {{ course.courseName }}
              </option>
            </select>
          </div>

          <!-- Title -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Title *</label>
            <input
              v-model="assignmentForm.title"
              type="text"
              required
              placeholder="e.g., Homework 1: Introduction to Programming"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Description *</label>
            <textarea
              v-model="assignmentForm.description"
              required
              rows="4"
              placeholder="Provide detailed instructions for the assignment..."
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            ></textarea>
          </div>

          <!-- Due Date and Max Points -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Due Date *</label>
              <input
                v-model="assignmentForm.dueDate"
                type="datetime-local"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Max Points *</label>
              <input
                v-model.number="assignmentForm.maxPoints"
                type="number"
                min="1"
                required
                placeholder="100"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <!-- Late Submissions -->
          <div class="flex items-center gap-4">
            <label class="flex items-center cursor-pointer">
              <input v-model="assignmentForm.allowLateSubmissions" type="checkbox" class="mr-2 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded" />
              <span class="text-sm font-medium text-gray-700">Allow Late Submissions</span>
            </label>

            <div v-if="assignmentForm.allowLateSubmissions" class="flex-1">
              <label class="block text-sm font-medium text-gray-700 mb-2">Late Penalty (% per day)</label>
              <input
                v-model.number="assignmentForm.latePenaltyPerDay"
                type="number"
                min="0"
                max="100"
                placeholder="10"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <!-- File Restrictions -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Allowed File Types</label>
              <input
                v-model="assignmentForm.allowedFileTypes"
                type="text"
                placeholder="pdf,docx,zip"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <p class="text-xs text-gray-500 mt-1">Comma-separated list (e.g., pdf,docx,zip)</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Max File Size (MB)</label>
              <input
                v-model.number="assignmentForm.maxFileSizeMb"
                type="number"
                min="1"
                placeholder="10"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <!-- Form Actions -->
          <div class="flex justify-end gap-3 pt-4 border-t border-gray-200">
            <button
              type="button"
              @click="closeModals"
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="submitting"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
            >
              {{ submitting ? 'Saving...' : (showEditModal ? 'Update Assignment' : 'Create Assignment') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click="showDeleteModal = false">
      <div class="bg-white rounded-lg shadow-xl max-w-md w-full m-4" @click.stop>
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Delete Assignment</h3>
        </div>
        <div class="px-6 py-4">
          <p class="text-gray-700">
            Are you sure you want to delete <strong>{{ assignmentToDelete?.title }}</strong>?
          </p>
          <p class="text-gray-600 text-sm mt-2">
            This action cannot be undone. All student submissions for this assignment will also be affected.
          </p>
        </div>
        <div class="px-6 py-4 bg-gray-50 flex justify-end gap-3">
          <button
            @click="showDeleteModal = false"
            class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
          >
            Cancel
          </button>
          <button
            @click="deleteAssignment"
            :disabled="submitting"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
          >
            {{ submitting ? 'Deleting...' : 'Delete Assignment' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'

const router = useRouter()
const authStore = useAuthStore()
const userId = computed(() => authStore.userId)

const loading = ref(false)
const submitting = ref(false)
const assignments = ref([])
const courses = ref([])

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

const filterCourse = ref('')
const filterStatus = ref('')

const showCreateModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)

const assignmentForm = ref({
  courseId: '',
  title: '',
  description: '',
  dueDate: '',
  maxPoints: 100,
  allowLateSubmissions: true,
  latePenaltyPerDay: 10,
  allowedFileTypes: 'pdf,docx,zip',
  maxFileSizeMb: 10
})

const assignmentToEdit = ref(null)
const assignmentToDelete = ref(null)

const filteredAssignments = computed(() => {
  let filtered = assignments.value

  if (filterCourse.value) {
    filtered = filtered.filter(a => a.courseId === parseInt(filterCourse.value))
  }

  if (filterStatus.value === 'active') {
    filtered = filtered.filter(a => a.active && !a.isOverdue)
  } else if (filterStatus.value === 'overdue') {
    filtered = filtered.filter(a => a.isOverdue)
  } else if (filterStatus.value === 'upcoming') {
    filtered = filtered.filter(a => !a.isOverdue && a.active)
  }

  return filtered
})

async function fetchCourses() {
  try {
    const response = await api.get(`/courses/instructor/${userId.value}`)
    courses.value = response.data
  } catch (error) {
    console.error('Error fetching courses:', error)
  }
}

async function fetchAssignments() {
  loading.value = true
  try {
    const response = await api.get(`/assignments/faculty/${userId.value}`)
    assignments.value = response.data
  } catch (error) {
    console.error('Error fetching assignments:', error)
  } finally {
    loading.value = false
  }
}

async function createAssignment() {
  submitting.value = true
  try {
    await api.post(`/assignments?courseId=${assignmentForm.value.courseId}&facultyId=${userId.value}`, assignmentForm.value)
    await fetchAssignments()
    closeModals()
    resetForm()
  } catch (error) {
    console.error('Error creating assignment:', error)
    showNotification('Failed to create assignment. Please try again.', 'error')
  } finally {
    submitting.value = false
  }
}

async function updateAssignment() {
  submitting.value = true
  try {
    await api.put(`/assignments/${assignmentToEdit.value.id}?facultyId=${userId.value}`, assignmentForm.value)
    await fetchAssignments()
    closeModals()
    resetForm()
  } catch (error) {
    console.error('Error updating assignment:', error)
    showNotification('Failed to update assignment. Please try again.', 'error')
  } finally {
    submitting.value = false
  }
}

async function deleteAssignment() {
  submitting.value = true
  try {
    await api.delete(`/assignments/${assignmentToDelete.value.id}?facultyId=${userId.value}`)
    await fetchAssignments()
    showDeleteModal.value = false
    assignmentToDelete.value = null
  } catch (error) {
    console.error('Error deleting assignment:', error)
    showNotification('Failed to delete assignment. Please try again.', 'error')
  } finally {
    submitting.value = false
  }
}

function editAssignment(assignment) {
  assignmentToEdit.value = assignment
  assignmentForm.value = {
    courseId: assignment.courseId,
    title: assignment.title,
    description: assignment.description,
    dueDate: formatDateTimeForInput(assignment.dueDate),
    maxPoints: assignment.maxPoints,
    allowLateSubmissions: assignment.allowLateSubmissions,
    latePenaltyPerDay: assignment.latePenaltyPerDay || 10,
    allowedFileTypes: assignment.allowedFileTypes || 'pdf,docx,zip',
    maxFileSizeMb: assignment.maxFileSizeMb || 10
  }
  showEditModal.value = true
}

function confirmDelete(assignment) {
  assignmentToDelete.value = assignment
  showDeleteModal.value = true
}

function viewSubmissions(assignment) {
  router.push({ name: 'FacultySubmissions', query: { assignmentId: assignment.id } })
}

function closeModals() {
  showCreateModal.value = false
  showEditModal.value = false
  assignmentToEdit.value = null
}

function resetForm() {
  assignmentForm.value = {
    courseId: '',
    title: '',
    description: '',
    dueDate: '',
    maxPoints: 100,
    allowLateSubmissions: true,
    latePenaltyPerDay: 10,
    allowedFileTypes: 'pdf,docx,zip',
    maxFileSizeMb: 10
  }
}

function refreshAssignments() {
  fetchAssignments()
}

function formatDateTime(dateTime) {
  if (!dateTime) return 'N/A'
  const date = new Date(dateTime)
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function formatDateTimeForInput(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

onMounted(async () => {
  await fetchCourses()
  await fetchAssignments()
})
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}
</style>
