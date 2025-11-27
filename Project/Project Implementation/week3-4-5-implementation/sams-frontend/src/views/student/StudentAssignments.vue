<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">My Assignments</h1>
      <p class="mt-2 text-gray-600">View and submit your course assignments</p>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Course</label>
          <select
            v-model="filters.courseId"
            @change="loadAssignments"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Courses</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.code }} - {{ course.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Status</label>
          <select
            v-model="filters.status"
            @change="filterAssignments"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Status</option>
            <option value="upcoming">Upcoming</option>
            <option value="overdue">Overdue</option>
            <option value="submitted">Submitted</option>
            <option value="graded">Graded</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Search</label>
          <input
            v-model="filters.search"
            @input="filterAssignments"
            type="text"
            placeholder="Search assignments..."
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <div class="flex items-end">
          <button
            @click="resetFilters"
            class="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors"
          >
            Reset Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Assignments List -->
    <div v-else-if="filteredAssignments.length > 0" class="space-y-4">
      <div
        v-for="assignment in filteredAssignments"
        :key="assignment.id"
        class="bg-white rounded-lg shadow hover:shadow-md transition-shadow"
      >
        <div class="p-6">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <!-- Assignment Title & Course -->
              <div class="flex items-center space-x-3 mb-2">
                <h3 class="text-xl font-semibold text-gray-900">
                  {{ assignment.title }}
                </h3>
                <span
                  class="px-2 py-1 text-xs font-semibold rounded-full"
                  :class="getStatusClass(assignment)"
                >
                  {{ getStatusText(assignment) }}
                </span>
              </div>
              <p class="text-sm text-gray-600 mb-2">
                {{ assignment.course?.code }} - {{ assignment.course?.name }}
              </p>

              <!-- Description -->
              <p class="text-gray-700 mb-4">{{ assignment.description }}</p>

              <!-- Assignment Info -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span class="text-gray-500">Due Date:</span>
                  <p class="font-medium" :class="getDueDateClass(assignment)">
                    {{ formatDate(assignment.dueDate) }}
                  </p>
                </div>
                <div>
                  <span class="text-gray-500">Max Grade:</span>
                  <p class="font-medium text-gray-900">{{ assignment.maxGrade }} points</p>
                </div>
                <div v-if="assignment.submission">
                  <span class="text-gray-500">Submitted:</span>
                  <p class="font-medium text-green-600">
                    {{ formatDate(assignment.submission.submittedAt) }}
                  </p>
                </div>
                <div v-if="assignment.submission?.grade !== null && assignment.submission?.grade !== undefined">
                  <span class="text-gray-500">Grade:</span>
                  <p class="font-medium text-blue-600">
                    {{ assignment.submission.grade }} / {{ assignment.maxGrade }}
                  </p>
                </div>
              </div>

              <!-- Faculty Feedback -->
              <div v-if="assignment.submission?.feedback" class="mt-4 p-4 bg-blue-50 rounded-md">
                <p class="text-sm font-medium text-blue-900 mb-1">Faculty Feedback:</p>
                <p class="text-sm text-blue-800">{{ assignment.submission.feedback }}</p>
              </div>
            </div>

            <!-- Actions -->
            <div class="ml-6 flex flex-col space-y-2">
              <router-link
                v-if="!assignment.submission && !isPastDue(assignment)"
                :to="`/student/assignments/${assignment.id}/submit`"
                class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors text-center"
              >
                Submit
              </router-link>
              <router-link
                v-if="assignment.submission"
                :to="`/student/submissions/${assignment.submission.id}`"
                class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-md hover:bg-green-700 transition-colors text-center"
              >
                View Submission
              </router-link>
              <button
                @click="viewDetails(assignment)"
                class="px-4 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-200 transition-colors"
              >
                Details
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
      <h3 class="mt-2 text-sm font-medium text-gray-900">No assignments found</h3>
      <p class="mt-1 text-sm text-gray-500">No assignments match your current filters.</p>
    </div>

    <!-- Assignment Details Modal -->
    <Modal v-model="showDetailsModal" title="Assignment Details" size="large">
      <div v-if="selectedAssignment" class="space-y-4">
        <div>
          <h3 class="text-lg font-semibold text-gray-900">{{ selectedAssignment.title }}</h3>
          <p class="text-sm text-gray-600">{{ selectedAssignment.course?.code }} - {{ selectedAssignment.course?.name }}</p>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-1">Description</h4>
          <p class="text-gray-900">{{ selectedAssignment.description }}</p>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <h4 class="text-sm font-medium text-gray-700 mb-1">Due Date</h4>
            <p class="text-gray-900">{{ formatDate(selectedAssignment.dueDate) }}</p>
          </div>
          <div>
            <h4 class="text-sm font-medium text-gray-700 mb-1">Maximum Grade</h4>
            <p class="text-gray-900">{{ selectedAssignment.maxGrade }} points</p>
          </div>
        </div>
        <div v-if="selectedAssignment.attachments && selectedAssignment.attachments.length > 0">
          <h4 class="text-sm font-medium text-gray-700 mb-2">Attachments</h4>
          <div class="space-y-2">
            <a
              v-for="(file, index) in selectedAssignment.attachments"
              :key="index"
              :href="file.url"
              target="_blank"
              class="flex items-center space-x-2 text-blue-600 hover:text-blue-800"
            >
              <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <span>{{ file.name }}</span>
            </a>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import Modal from '../../components/Modal.vue'

const router = useRouter()
const authStore = useAuthStore()

const assignments = ref([])
const courses = ref([])
const loading = ref(true)
const showDetailsModal = ref(false)
const selectedAssignment = ref(null)

const filters = ref({
  courseId: '',
  status: '',
  search: ''
})

const filteredAssignments = computed(() => {
  let result = [...assignments.value]

  // Filter by status
  if (filters.value.status) {
    const now = new Date()
    result = result.filter(assignment => {
      const dueDate = new Date(assignment.dueDate)
      const hasSubmission = !!assignment.submission
      const isGraded = hasSubmission && assignment.submission.grade !== null

      switch (filters.value.status) {
        case 'upcoming':
          return !hasSubmission && dueDate > now
        case 'overdue':
          return !hasSubmission && dueDate < now
        case 'submitted':
          return hasSubmission && !isGraded
        case 'graded':
          return isGraded
        default:
          return true
      }
    })
  }

  // Filter by search
  if (filters.value.search) {
    const searchLower = filters.value.search.toLowerCase()
    result = result.filter(assignment =>
      assignment.title.toLowerCase().includes(searchLower) ||
      assignment.description.toLowerCase().includes(searchLower) ||
      assignment.course?.code.toLowerCase().includes(searchLower)
    )
  }

  // Sort by due date
  result.sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))

  return result
})

onMounted(async () => {
  await loadCourses()
  await loadAssignments()
})

async function loadCourses() {
  try {
    const enrollmentsRes = await api.getStudentEnrollments(authStore.userId)
    // Extract unique courses from enrollments
    const uniqueCourses = enrollmentsRes.data
      .filter(enrollment => enrollment.course)
      .map(enrollment => enrollment.course)
      .filter((course, index, self) =>
        index === self.findIndex(c => c.id === course.id)
      )
    courses.value = uniqueCourses
  } catch (error) {
    console.error('Error loading courses:', error)
  }
}

async function loadAssignments() {
  try {
    loading.value = true
    const response = await api.getStudentAssignments(authStore.userId)

    // Merge assignments with their submissions
    const assignmentsData = response.data || []

    // Get student submissions
    const submissionsRes = await api.getStudentSubmissions(authStore.userId)
    const submissions = submissionsRes.data || []

    // Map submissions to assignments
    assignments.value = assignmentsData.map(assignment => {
      const submission = submissions.find(s => s.assignment?.id === assignment.id)
      return {
        ...assignment,
        submission: submission || null
      }
    })
  } catch (error) {
    console.error('Error loading assignments:', error)
  } finally {
    loading.value = false
  }
}

function filterAssignments() {
  // Filtering is handled by computed property
}

function resetFilters() {
  filters.value = {
    courseId: '',
    status: '',
    search: ''
  }
}

function viewDetails(assignment) {
  selectedAssignment.value = assignment
  showDetailsModal.value = true
}

function getStatusText(assignment) {
  const now = new Date()
  const dueDate = new Date(assignment.dueDate)
  const hasSubmission = !!assignment.submission
  const isGraded = hasSubmission && assignment.submission.grade !== null

  if (isGraded) return 'Graded'
  if (hasSubmission) return 'Submitted'
  if (dueDate < now) return 'Overdue'
  return 'Pending'
}

function getStatusClass(assignment) {
  const status = getStatusText(assignment)
  const classes = {
    'Graded': 'bg-blue-100 text-blue-800',
    'Submitted': 'bg-green-100 text-green-800',
    'Overdue': 'bg-red-100 text-red-800',
    'Pending': 'bg-yellow-100 text-yellow-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getDueDateClass(assignment) {
  if (assignment.submission) return 'text-gray-900'
  const now = new Date()
  const dueDate = new Date(assignment.dueDate)
  const hoursUntilDue = (dueDate - now) / (1000 * 60 * 60)

  if (hoursUntilDue < 0) return 'text-red-600 font-bold'
  if (hoursUntilDue < 24) return 'text-orange-600 font-bold'
  if (hoursUntilDue < 72) return 'text-yellow-600'
  return 'text-gray-900'
}

function isPastDue(assignment) {
  return new Date(assignment.dueDate) < new Date()
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
