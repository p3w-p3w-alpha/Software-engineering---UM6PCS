<template>
  <div class="max-w-7xl mx-auto">
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
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Grade Management</h1>
      <p class="mt-2 text-gray-600">Manage student grades for your courses</p>
    </div>

    <!-- Course Selection -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <label class="block text-sm font-medium text-gray-700 mb-2">Select Course</label>
      <select
        v-model="selectedCourseId"
        @change="loadCourseGrades"
        class="w-full md:w-96 px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
      >
        <option value="">Choose a course...</option>
        <option v-for="course in courses" :key="course.id" :value="course.id">
          {{ course.code }} - {{ course.name }}
        </option>
      </select>
    </div>

    <!-- Course Stats -->
    <div v-if="selectedCourse" class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <StatCard
        title="Total Students"
        :value="enrollments.length"
        :icon="UsersIcon"
        color="blue"
      />
      <StatCard
        title="Graded"
        :value="gradedCount"
        :icon="CheckCircleIcon"
        color="green"
      />
      <StatCard
        title="Pending"
        :value="pendingCount"
        :icon="ClockIcon"
        color="yellow"
      />
      <StatCard
        title="Average Grade"
        :value="averageGrade"
        :icon="ChartBarIcon"
        color="purple"
      />
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Grades Table -->
    <div v-else-if="enrollments.length > 0" class="bg-white rounded-lg shadow overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
        <h2 class="text-lg font-semibold text-gray-900">Student Grades</h2>
        <div class="flex items-center space-x-4">
          <button
            @click="showBulkGradeModal = true"
            class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-md hover:bg-green-700"
          >
            Bulk Grade Entry
          </button>
          <button
            @click="exportGrades"
            class="px-4 py-2 bg-gray-600 text-white text-sm font-medium rounded-md hover:bg-gray-700"
          >
            Export CSV
          </button>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Grade</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Last Updated</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="enrollment in enrollments" :key="enrollment.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">
                  {{ enrollment.student.firstName }} {{ enrollment.student.lastName }}
                </div>
                <div class="text-sm text-gray-500">ID: {{ enrollment.student.username }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ enrollment.student.email }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div v-if="enrollment.grade">
                  <div class="text-sm font-semibold" :class="getGradeColorClass(enrollment.grade.letterGrade)">
                    {{ enrollment.grade.letterGrade }} ({{ enrollment.grade.numericGrade }})
                  </div>
                  <div class="text-xs text-gray-500">{{ enrollment.grade.gradePoints }} GPA</div>
                </div>
                <span v-else class="text-sm text-gray-400">Not graded</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="enrollment.grade?.finalized ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'"
                  class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full"
                >
                  {{ enrollment.grade?.finalized ? 'Finalized' : 'Draft' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ enrollment.grade ? formatDate(enrollment.grade.updatedAt) : 'N/A' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  v-if="!enrollment.grade?.finalized"
                  @click="openGradeEntry(enrollment)"
                  class="text-blue-600 hover:text-blue-900 mr-4"
                >
                  {{ enrollment.grade ? 'Edit' : 'Grade' }}
                </button>
                <span v-else class="text-gray-400 mr-4 cursor-not-allowed" title="Grade is finalized">
                  Locked
                </span>
                <button
                  v-if="enrollment.grade && !enrollment.grade.finalized"
                  @click="finalizeGrade(enrollment.grade.id)"
                  class="text-green-600 hover:text-green-900"
                >
                  Finalize
                </button>
                <button
                  v-if="enrollment.grade && enrollment.grade.finalized"
                  @click="unfinalizeGrade(enrollment.grade.id)"
                  class="text-orange-600 hover:text-orange-900"
                >
                  Unlock
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Grade Distribution Chart -->
      <div class="p-6 border-t border-gray-200">
        <h3 class="text-sm font-medium text-gray-700 mb-4">Grade Distribution</h3>
        <div class="grid grid-cols-2 md:grid-cols-6 gap-4">
          <div v-for="(count, grade) in gradeDistribution" :key="grade" class="text-center">
            <div class="text-2xl font-bold" :class="getGradeColorClass(grade)">{{ count }}</div>
            <div class="text-xs text-gray-500">{{ grade }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="selectedCourseId" class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No students enrolled</h3>
      <p class="mt-1 text-sm text-gray-500">This course has no enrolled students.</p>
    </div>

    <!-- Grade Entry Modal -->
    <Modal v-model="showGradeModal" :title="gradeModalTitle" size="medium">
      <form @submit.prevent="saveGrade" class="space-y-4">
        <div v-if="selectedEnrollment">
          <p class="text-sm text-gray-600 mb-4">
            Student: <strong>{{ selectedEnrollment.student.firstName }} {{ selectedEnrollment.student.lastName }}</strong>
          </p>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Letter Grade *</label>
              <select
                v-model="gradeForm.letterGrade"
                @change="calculateGradePoints"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Select grade...</option>
                <option value="A">A</option>
                <option value="A-">A-</option>
                <option value="B+">B+</option>
                <option value="B">B</option>
                <option value="B-">B-</option>
                <option value="C+">C+</option>
                <option value="C">C</option>
                <option value="C-">C-</option>
                <option value="D+">D+</option>
                <option value="D">D</option>
                <option value="F">F</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Numeric Grade (0-100) *</label>
              <input
                v-model.number="gradeForm.numericGrade"
                @input="calculateLetterGrade"
                type="number"
                min="0"
                max="100"
                step="0.5"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Grade Points</label>
            <input
              v-model="gradeForm.gradePoints"
              type="text"
              readonly
              class="w-full px-4 py-2 border border-gray-300 rounded-md bg-gray-50"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Comments</label>
            <textarea
              v-model="gradeForm.comments"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Optional comments about the grade..."
            ></textarea>
          </div>

          <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-md">
            <p class="text-sm text-red-600">{{ errorMessage }}</p>
          </div>
        </div>
      </form>

      <template #footer>
        <div class="flex items-center justify-end space-x-3">
          <button
            @click="showGradeModal = false"
            type="button"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            @click="saveGrade"
            :disabled="saving"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 flex items-center"
          >
            <LoadingSpinner v-if="saving" size="small" color="white" class="mr-2" />
            <span>{{ saving ? 'Saving...' : 'Save Grade' }}</span>
          </button>
        </div>
      </template>
    </Modal>

    <!-- Bulk Grade Entry Modal -->
    <Modal v-model="showBulkGradeModal" title="Bulk Grade Entry" size="large">
      <div class="space-y-4">
        <p class="text-sm text-gray-600">Enter grades for multiple students at once</p>
        <div class="max-h-96 overflow-y-auto border border-gray-200 rounded-lg">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50 sticky top-0">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Student</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Grade</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Score (0-100)</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="(enrollment, index) in enrollments" :key="enrollment.id">
                <td class="px-4 py-2 text-sm">{{ enrollment.student.firstName }} {{ enrollment.student.lastName }}</td>
                <td class="px-4 py-2">
                  <input
                    v-model="bulkGrades[index].letterGrade"
                    type="text"
                    class="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
                    placeholder="A, B+..."
                  />
                </td>
                <td class="px-4 py-2">
                  <input
                    v-model.number="bulkGrades[index].numericGrade"
                    type="number"
                    min="0"
                    max="100"
                    step="0.5"
                    class="w-24 px-2 py-1 border border-gray-300 rounded text-sm"
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <template #footer>
        <div class="flex items-center justify-end space-x-3">
          <button
            @click="showBulkGradeModal = false"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            @click="saveBulkGrades"
            :disabled="saving"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
          >
            Save All Grades
          </button>
        </div>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import StatCard from '../../components/StatCard.vue'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import Modal from '../../components/Modal.vue'
import { UsersIcon, CheckCircleIcon, ClockIcon, ChartBarIcon } from '@heroicons/vue/24/outline'

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

const courses = ref([])
const selectedCourseId = ref('')
const selectedCourse = ref(null)
const enrollments = ref([])
const loading = ref(false)
const showGradeModal = ref(false)
const showBulkGradeModal = ref(false)
const selectedEnrollment = ref(null)
const saving = ref(false)
const errorMessage = ref('')

const gradeForm = ref({
  letterGrade: '',
  numericGrade: null,
  gradePoints: null,
  comments: ''
})

const bulkGrades = ref([])

const gradeModalTitle = computed(() =>
  selectedEnrollment.value?.grade ? 'Edit Grade' : 'Assign Grade'
)

const gradedCount = computed(() =>
  enrollments.value.filter(e => e.grade).length
)

const pendingCount = computed(() =>
  enrollments.value.filter(e => !e.grade).length
)

const averageGrade = computed(() => {
  const graded = enrollments.value.filter(e => e.grade?.numericGrade != null)
  if (graded.length === 0) return 'N/A'
  const sum = graded.reduce((acc, e) => acc + e.grade.numericGrade, 0)
  return (sum / graded.length).toFixed(2)
})

const gradeDistribution = computed(() => {
  const dist = { 'A': 0, 'B': 0, 'C': 0, 'D': 0, 'F': 0 }
  enrollments.value.forEach(e => {
    if (e.grade?.letterGrade) {
      const base = e.grade.letterGrade[0]
      if (dist[base] !== undefined) dist[base]++
    }
  })
  return dist
})

onMounted(async () => {
  await loadCourses()
})

async function loadCourses() {
  try {
    const response = await api.getFacultyAssignments(authStore.userId)
    // Get unique courses from assignments
    const coursesMap = new Map()
    response.data.forEach(assignment => {
      if (assignment.course) {
        coursesMap.set(assignment.course.id, assignment.course)
      }
    })
    courses.value = Array.from(coursesMap.values())
  } catch (error) {
    console.error('Error loading courses:', error)
  }
}

async function loadCourseGrades() {
  if (!selectedCourseId.value) {
    enrollments.value = []
    return
  }

  try {
    loading.value = true
    selectedCourse.value = courses.value.find(c => c.id === selectedCourseId.value)

    // Load enrollments with grades
    const enrollmentsRes = await api.getCourseEnrollments(selectedCourseId.value)
    const gradesRes = await api.getCourseGrades(selectedCourseId.value)

    // Merge enrollments with grades - match by student ID since grade response includes student info
    enrollments.value = enrollmentsRes.data.map(enrollment => {
      const matchedGrade = gradesRes.data.find(g => g.student?.id === enrollment.student?.id)
      return {
        ...enrollment,
        grade: matchedGrade ? {
          id: matchedGrade.id,
          letterGrade: matchedGrade.gradeValue,
          gradePoints: matchedGrade.gradePoints,
          numericGrade: null, // Backend doesn't store numeric grade separately
          updatedAt: matchedGrade.createdAt,
          finalized: matchedGrade.finalized || false,
          finalizedAt: matchedGrade.finalizedAt
        } : null
      }
    })

    // Initialize bulk grades array
    bulkGrades.value = enrollments.value.map(e => ({
      enrollmentId: e.id,
      letterGrade: e.grade?.letterGrade || '',
      numericGrade: e.grade?.numericGrade || null
    }))
  } catch (error) {
    console.error('Error loading grades:', error)
  } finally {
    loading.value = false
  }
}

function openGradeEntry(enrollment) {
  selectedEnrollment.value = enrollment
  if (enrollment.grade) {
    gradeForm.value = {
      letterGrade: enrollment.grade.letterGrade,
      numericGrade: enrollment.grade.numericGrade,
      gradePoints: enrollment.grade.gradePoints,
      comments: enrollment.grade.comments || ''
    }
  } else {
    gradeForm.value = { letterGrade: '', numericGrade: null, gradePoints: null, comments: '' }
  }
  showGradeModal.value = true
}

async function saveGrade() {
  try {
    saving.value = true
    errorMessage.value = ''

    const gradeData = {
      enrollmentId: selectedEnrollment.value.id,
      letterGrade: gradeForm.value.letterGrade,
      numericGrade: gradeForm.value.numericGrade,
      gradePoints: gradeForm.value.gradePoints,
      comments: gradeForm.value.comments
    }

    await api.assignGrade(gradeData)
    await loadCourseGrades()
    showGradeModal.value = false
  } catch (error) {
    console.error('Error saving grade:', error)
    errorMessage.value = error.response?.data?.message || 'Failed to save grade'
  } finally {
    saving.value = false
  }
}

async function saveBulkGrades() {
  try {
    saving.value = true
    const validGrades = bulkGrades.value.filter(g => g.letterGrade && g.numericGrade != null)

    for (const grade of validGrades) {
      await api.assignGrade({
        enrollmentId: grade.enrollmentId,
        letterGrade: grade.letterGrade,
        numericGrade: grade.numericGrade,
        gradePoints: calculateGradePointsFromLetter(grade.letterGrade)
      })
    }

    await loadCourseGrades()
    showBulkGradeModal.value = false
  } catch (error) {
    console.error('Error saving bulk grades:', error)
    showNotification('Failed to save some grades', 'error')
  } finally {
    saving.value = false
  }
}

async function finalizeGrade(gradeId) {
  if (!confirm('Are you sure you want to finalize this grade? Once finalized, it cannot be edited without unlocking.')) return

  try {
    await api.finalizeGrade(gradeId)
    await loadCourseGrades()
    showNotification('Grade finalized successfully', 'success')
  } catch (error) {
    console.error('Error finalizing grade:', error)
    showNotification('Failed to finalize grade', 'error')
  }
}

async function unfinalizeGrade(gradeId) {
  if (!confirm('Are you sure you want to unlock this grade for editing?')) return

  try {
    await api.unfinalizeGrade(gradeId)
    await loadCourseGrades()
    showNotification('Grade unlocked for editing', 'success')
  } catch (error) {
    console.error('Error unlocking grade:', error)
    showNotification('Failed to unlock grade', 'error')
  }
}

function calculateLetterGrade() {
  const score = gradeForm.value.numericGrade
  if (score == null) return

  if (score >= 93) gradeForm.value.letterGrade = 'A'
  else if (score >= 90) gradeForm.value.letterGrade = 'A-'
  else if (score >= 87) gradeForm.value.letterGrade = 'B+'
  else if (score >= 83) gradeForm.value.letterGrade = 'B'
  else if (score >= 80) gradeForm.value.letterGrade = 'B-'
  else if (score >= 77) gradeForm.value.letterGrade = 'C+'
  else if (score >= 73) gradeForm.value.letterGrade = 'C'
  else if (score >= 70) gradeForm.value.letterGrade = 'C-'
  else if (score >= 67) gradeForm.value.letterGrade = 'D+'
  else if (score >= 60) gradeForm.value.letterGrade = 'D'
  else gradeForm.value.letterGrade = 'F'

  calculateGradePoints()
}

function calculateGradePoints() {
  gradeForm.value.gradePoints = calculateGradePointsFromLetter(gradeForm.value.letterGrade)
}

function calculateGradePointsFromLetter(letter) {
  const points = {
    'A': 4.0, 'A-': 3.7, 'B+': 3.3, 'B': 3.0, 'B-': 2.7,
    'C+': 2.3, 'C': 2.0, 'C-': 1.7, 'D+': 1.3, 'D': 1.0, 'F': 0.0
  }
  return points[letter] || null
}

function getGradeColorClass(grade) {
  if (!grade) return 'text-gray-500'
  const base = grade[0]
  const colors = { 'A': 'text-green-600', 'B': 'text-blue-600', 'C': 'text-yellow-600', 'D': 'text-orange-600', 'F': 'text-red-600' }
  return colors[base] || 'text-gray-500'
}

function exportGrades() {
  const csv = [
    ['Student', 'Email', 'Letter Grade', 'Numeric Grade', 'Grade Points'],
    ...enrollments.value.map(e => [
      `${e.student.firstName} ${e.student.lastName}`,
      e.student.email,
      e.grade?.letterGrade || '',
      e.grade?.numericGrade || '',
      e.grade?.gradePoints || ''
    ])
  ].map(row => row.join(',')).join('\n')

  const blob = new Blob([csv], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `grades_${selectedCourse.value?.code}.csv`
  a.click()
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>
