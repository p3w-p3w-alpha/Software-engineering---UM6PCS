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
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-900">Attendance Management</h1>
      <p class="text-gray-600 mt-2">Track and manage student attendance</p>
    </div>

    <!-- Course and Date Selection -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Select Course *</label>
          <select v-model="selectedCourseId" @change="loadRoster" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            <option value="">Choose a course</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.courseCode }} - {{ course.courseName }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Date *</label>
          <input
            v-model="selectedDate"
            type="date"
            :max="today"
            @change="checkExistingAttendance"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <div class="flex items-end">
          <button
            @click="loadRoster"
            :disabled="!selectedCourseId"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg class="h-5 w-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Load Roster
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- No Course Selected -->
    <div v-else-if="!selectedCourseId" class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No course selected</h3>
      <p class="mt-2 text-gray-500">Select a course and date to start marking attendance</p>
    </div>

    <!-- Attendance Table -->
    <div v-else-if="roster.length > 0" class="bg-white rounded-lg shadow">
      <!-- Table Header with Bulk Actions -->
      <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-gray-900">
            Class Roster - {{ selectedCourse?.courseCode }}
          </h2>
          <p class="text-sm text-gray-600 mt-1">
            {{ roster.length }} students | {{ formatDate(selectedDate) }}
          </p>
        </div>
        <div class="flex gap-2">
          <button
            @click="markAllAs('PRESENT')"
            class="px-4 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors text-sm font-medium"
          >
            Mark All Present
          </button>
          <button
            @click="markAllAs('ABSENT')"
            class="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors text-sm font-medium"
          >
            Mark All Absent
          </button>
          <button
            @click="saveAttendance"
            :disabled="submitting || !hasChanges"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
          >
            {{ submitting ? 'Saving...' : 'Save Attendance' }}
          </button>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Student ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Name
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Email
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Remarks
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="student in roster" :key="student.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ student.student.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-10 w-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <span class="text-blue-600 font-semibold">
                      {{ getInitials(student.student.firstName, student.student.lastName) }}
                    </span>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">
                      {{ student.student.firstName }} {{ student.student.lastName }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                {{ student.student.email }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <select
                  v-model="student.attendanceStatus"
                  @change="hasChanges = true"
                  class="px-3 py-1 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  :class="getStatusSelectClass(student.attendanceStatus)"
                >
                  <option value="PRESENT">Present</option>
                  <option value="ABSENT">Absent</option>
                  <option value="LATE">Late</option>
                  <option value="EXCUSED">Excused</option>
                </select>
              </td>
              <td class="px-6 py-4">
                <input
                  v-model="student.remarks"
                  type="text"
                  placeholder="Optional notes..."
                  @input="hasChanges = true"
                  class="w-full px-3 py-1 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Save Footer -->
      <div v-if="hasChanges" class="px-6 py-4 bg-yellow-50 border-t border-yellow-200">
        <p class="text-sm text-yellow-800">
          <svg class="h-5 w-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          You have unsaved changes. Click "Save Attendance" to persist your changes.
        </p>
      </div>
    </div>

    <!-- No Students -->
    <div v-else class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No students enrolled</h3>
      <p class="mt-2 text-gray-500">There are no students enrolled in this course</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'

const authStore = useAuthStore()
const userId = computed(() => authStore.userId)

const loading = ref(false)
const submitting = ref(false)
const hasChanges = ref(false)

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
const selectedCourse = computed(() => courses.value.find(c => c.id === parseInt(selectedCourseId.value)))

const today = new Date().toISOString().split('T')[0]
const selectedDate = ref(today)

const roster = ref([])

async function fetchCourses() {
  try {
    const response = await api.get(`/courses/instructor/${userId.value}`)
    courses.value = response.data
  } catch (error) {
    console.error('Error fetching courses:', error)
  }
}

async function loadRoster() {
  if (!selectedCourseId.value) return

  loading.value = true
  hasChanges.value = false

  try {
    // Get enrolled students
    const response = await api.get(`/enrollments/course/${selectedCourseId.value}`)
    const enrollments = response.data.filter(e => e.status === 'ENROLLED' || e.status === 'ACTIVE')

    // Initialize attendance status for each student
    roster.value = enrollments.map(enrollment => ({
      id: enrollment.id,
      student: enrollment.student,
      attendanceStatus: 'PRESENT', // Default to present
      remarks: '',
      enrollmentId: enrollment.id
    }))

    // Check if attendance already exists for this date
    await checkExistingAttendance()

  } catch (error) {
    console.error('Error loading roster:', error)
    roster.value = []
  } finally {
    loading.value = false
  }
}

async function checkExistingAttendance() {
  if (!selectedDate.value || !selectedCourseId.value || roster.value.length === 0) return

  try {
    // Get existing attendance for this date
    const response = await api.get(`/attendance/date/${selectedDate.value}`)
    const existingRecords = response.data

    // Update roster with existing attendance data
    roster.value.forEach(student => {
      const existingRecord = existingRecords.find(
        record => record.user?.id === student.student.id
      )

      if (existingRecord) {
        student.attendanceStatus = existingRecord.status
        student.remarks = existingRecord.remarks || ''
        student.attendanceId = existingRecord.id
      }
    })

  } catch (error) {
    console.error('Error checking existing attendance:', error)
  }
}

async function saveAttendance() {
  if (!selectedCourseId.value || !selectedDate.value || roster.value.length === 0) {
    showNotification('Please select a course and ensure roster is loaded', 'error')
    return
  }

  submitting.value = true

  try {
    // Prepare bulk attendance data
    const attendanceRecords = roster.value.map(student => ({
      userId: student.student.id,
      date: selectedDate.value,
      status: student.attendanceStatus,
      remarks: student.remarks || null
    }))

    // Use bulk mark endpoint
    await api.post('/attendance/mark-bulk', attendanceRecords)

    hasChanges.value = false
    showNotification(`Attendance saved successfully for ${roster.value.length} students`, 'success')

  } catch (error) {
    console.error('Error saving attendance:', error)
    showNotification('Failed to save attendance. Please try again.', 'error')
  } finally {
    submitting.value = false
  }
}

function markAllAs(status) {
  roster.value.forEach(student => {
    student.attendanceStatus = status
  })
  hasChanges.value = true
}

function getInitials(firstName, lastName) {
  const first = firstName ? firstName.charAt(0).toUpperCase() : ''
  const last = lastName ? lastName.charAt(0).toUpperCase() : ''
  return first + last || 'NA'
}

function getStatusSelectClass(status) {
  const classes = {
    'PRESENT': 'bg-green-50 text-green-800 border-green-300',
    'ABSENT': 'bg-red-50 text-red-800 border-red-300',
    'LATE': 'bg-yellow-50 text-yellow-800 border-yellow-300',
    'EXCUSED': 'bg-blue-50 text-blue-800 border-blue-300'
  }
  return classes[status] || 'bg-gray-50 text-gray-800'
}

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}
</style>
