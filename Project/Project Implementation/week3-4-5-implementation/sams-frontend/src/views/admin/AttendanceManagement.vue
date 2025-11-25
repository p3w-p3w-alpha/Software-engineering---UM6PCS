<template>
  <div class="attendance-management bg-gray-50 min-h-screen p-8">
    <!-- Page Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Attendance Management</h1>
      <p class="mt-2 text-gray-600">Track and manage student and faculty attendance</p>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 gap-6 md:grid-cols-3 mb-8">
      <!-- Students Attendance Card -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-700">Students</h3>
          <div class="p-3 bg-blue-100 rounded-full">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
        </div>
        <div v-if="studentStats">
          <p class="text-3xl font-bold text-gray-900">{{ studentStats.presentCount + studentStats.lateCount }}</p>
          <p class="text-sm text-gray-500">Total Present</p>
          <div class="mt-4 pt-4 border-t border-gray-200">
            <div class="flex justify-between text-sm">
              <span class="text-green-600">On-Time: {{ studentStats.presentCount }}</span>
              <span class="text-yellow-600">Late: {{ studentStats.lateCount }}</span>
              <span class="text-red-600">Absent: {{ studentStats.absentCount }}</span>
            </div>
            <div class="mt-2">
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div
                  class="bg-green-500 h-2 rounded-full"
                  :style="{ width: studentStats.attendancePercentage + '%' }"
                ></div>
              </div>
              <p class="text-xs text-gray-500 mt-1">{{ studentStats.attendancePercentage?.toFixed(1) }}% attendance rate</p>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-4">
          <p class="text-gray-400">Loading...</p>
        </div>
      </div>

      <!-- Teachers Attendance Card -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-700">Teachers</h3>
          <div class="p-3 bg-purple-100 rounded-full">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
        </div>
        <div v-if="facultyStats">
          <p class="text-3xl font-bold text-gray-900">{{ facultyStats.presentCount + facultyStats.lateCount }}</p>
          <p class="text-sm text-gray-500">Total Present</p>
          <div class="mt-4 pt-4 border-t border-gray-200">
            <div class="flex justify-between text-sm">
              <span class="text-green-600">On-Time: {{ facultyStats.presentCount }}</span>
              <span class="text-yellow-600">Late: {{ facultyStats.lateCount }}</span>
              <span class="text-red-600">Absent: {{ facultyStats.absentCount }}</span>
            </div>
            <div class="mt-2">
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div
                  class="bg-purple-500 h-2 rounded-full"
                  :style="{ width: facultyStats.attendancePercentage + '%' }"
                ></div>
              </div>
              <p class="text-xs text-gray-500 mt-1">{{ facultyStats.attendancePercentage?.toFixed(1) }}% attendance rate</p>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-4">
          <p class="text-gray-400">Loading...</p>
        </div>
      </div>

      <!-- Summary Card -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-700">Overall Summary</h3>
          <div class="p-3 bg-green-100 rounded-full">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
        </div>
        <div v-if="overallStats">
          <p class="text-3xl font-bold text-gray-900">{{ overallStats.totalCount }}</p>
          <p class="text-sm text-gray-500">Total Records</p>
          <div class="mt-4 space-y-2">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Present:</span>
              <span class="font-semibold text-green-600">{{ overallStats.presentCount }}</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Late:</span>
              <span class="font-semibold text-yellow-600">{{ overallStats.lateCount }}</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Absent:</span>
              <span class="font-semibold text-red-600">{{ overallStats.absentCount }}</span>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-4">
          <p class="text-gray-400">Loading...</p>
        </div>
      </div>
    </div>

    <!-- Date Selection and Actions -->
    <div class="bg-white rounded-lg shadow p-6 mb-8">
      <div class="flex flex-wrap items-center gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Select Date</label>
          <input
            type="date"
            v-model="selectedDate"
            @change="loadAttendanceData"
            class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">View Type</label>
          <select
            v-model="viewType"
            class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="STUDENT">Students</option>
            <option value="FACULTY">Teachers</option>
          </select>
        </div>
        <div class="ml-auto flex gap-2">
          <button
            @click="showMarkAttendanceModal = true"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Mark Attendance
          </button>
          <button
            @click="exportAttendance"
            class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Export
          </button>
        </div>
      </div>
    </div>

    <!-- Attendance Grid -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">Attendance Records for {{ selectedDate }}</h2>
      </div>

      <div v-if="loading" class="p-8 text-center">
        <p class="text-gray-500">Loading attendance records...</p>
      </div>

      <div v-else-if="attendanceRecords.length === 0" class="p-8 text-center">
        <p class="text-gray-500">No attendance records found for {{ selectedDate }}</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Name
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Role
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Check-in Time
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Notes
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr
              v-for="record in attendanceRecords"
              :key="record.id"
              class="hover:bg-gray-50"
            >
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ record.userId }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ record.fullName }}</div>
                <div class="text-sm text-gray-500">{{ record.username }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                  {{ record.role }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="getStatusClass(record.status)"
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                >
                  {{ record.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatTime(record.checkInTime) }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-500">
                {{ record.notes || '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editAttendance(record)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="deleteRecord(record.id)"
                  class="text-red-600 hover:text-red-900"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Mark Attendance Modal -->
    <div
      v-if="showMarkAttendanceModal"
      class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50"
      @click.self="closeMarkAttendanceModal"
    >
      <div class="relative top-20 mx-auto p-5 border w-11/12 max-w-2xl shadow-lg rounded-md bg-white">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-gray-900">Mark Attendance</h3>
          <button
            @click="closeMarkAttendanceModal"
            class="text-gray-400 hover:text-gray-600"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form @submit.prevent="submitAttendance">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">User</label>
              <select
                v-model="attendanceForm.userId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Select user...</option>
                <option v-for="user in availableUsers" :key="user.id" :value="user.id">
                  {{ user.fullName }} ({{ user.username }}) - {{ user.role }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date</label>
              <input
                type="date"
                v-model="attendanceForm.date"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <select
                v-model="attendanceForm.status"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="PRESENT">Present</option>
                <option value="LATE">Late</option>
                <option value="ABSENT">Absent</option>
                <option value="SICK">Sick</option>
                <option value="EXCUSED">Excused</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Notes (Optional)</label>
              <textarea
                v-model="attendanceForm.notes"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="Add any additional notes..."
              ></textarea>
            </div>
          </div>

          <div class="mt-6 flex justify-end gap-3">
            <button
              type="button"
              @click="closeMarkAttendanceModal"
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Save Attendance
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue'
import api from '@/services/api'

export default {
  name: 'AttendanceManagement',
  setup() {
    // Reactive data
    const selectedDate = ref(new Date().toISOString().split('T')[0])
    const viewType = ref('STUDENT')
    const loading = ref(false)
    const attendanceRecords = ref([])
    const studentStats = ref(null)
    const facultyStats = ref(null)
    const overallStats = ref(null)
    const showMarkAttendanceModal = ref(false)
    const availableUsers = ref([])

    const attendanceForm = ref({
      userId: '',
      date: new Date().toISOString().split('T')[0],
      status: 'PRESENT',
      notes: ''
    })

    // Methods
    const loadAttendanceData = async () => {
      loading.value = true
      try {
        // Load attendance records for selected date
        const response = await api.getAttendanceByDate(selectedDate.value)
        attendanceRecords.value = response.data.filter(record => record.role === viewType.value)

        // Load statistics
        await loadStatistics()
      } catch (error) {
        console.error('Error loading attendance data:', error)
        alert('Failed to load attendance data')
      } finally {
        loading.value = false
      }
    }

    const loadStatistics = async () => {
      try {
        // Calculate date range (last 7 days)
        const endDate = new Date()
        const startDate = new Date()
        startDate.setDate(startDate.getDate() - 7)

        const startDateStr = startDate.toISOString().split('T')[0]
        const endDateStr = endDate.toISOString().split('T')[0]

        // Load statistics by role
        const statsResponse = await api.getAttendanceStatisticsByRole(startDateStr, endDateStr)
        studentStats.value = statsResponse.data.STUDENT
        facultyStats.value = statsResponse.data.FACULTY

        // Load overall statistics
        const overallResponse = await api.getAttendanceStatisticsByDateRange(startDateStr, endDateStr)
        overallStats.value = overallResponse.data
      } catch (error) {
        console.error('Error loading statistics:', error)
      }
    }

    const loadAvailableUsers = async () => {
      try {
        const response = await api.getAllUsers()
        availableUsers.value = response.data.map(user => ({
          id: user.id,
          username: user.username,
          fullName: `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username,
          role: user.role
        }))
      } catch (error) {
        console.error('Error loading users:', error)
      }
    }

    const submitAttendance = async () => {
      try {
        await api.markAttendance(attendanceForm.value)
        alert('Attendance marked successfully')
        closeMarkAttendanceModal()
        loadAttendanceData()
      } catch (error) {
        console.error('Error marking attendance:', error)
        alert('Failed to mark attendance')
      }
    }

    const closeMarkAttendanceModal = () => {
      showMarkAttendanceModal.value = false
      attendanceForm.value = {
        userId: '',
        date: new Date().toISOString().split('T')[0],
        status: 'PRESENT',
        notes: ''
      }
    }

    const editAttendance = (record) => {
      attendanceForm.value = {
        userId: record.userId,
        date: record.date,
        status: record.status,
        notes: record.notes || ''
      }
      showMarkAttendanceModal.value = true
    }

    const deleteRecord = async (id) => {
      if (!confirm('Are you sure you want to delete this attendance record?')) return

      try {
        await api.deleteAttendance(id)
        alert('Attendance record deleted successfully')
        loadAttendanceData()
      } catch (error) {
        console.error('Error deleting attendance:', error)
        alert('Failed to delete attendance record')
      }
    }

    const exportAttendance = () => {
      // TODO: Implement export functionality
      alert('Export functionality coming soon!')
    }

    const getStatusClass = (status) => {
      const classes = {
        PRESENT: 'bg-green-100 text-green-800',
        LATE: 'bg-yellow-100 text-yellow-800',
        ABSENT: 'bg-red-100 text-red-800',
        SICK: 'bg-orange-100 text-orange-800',
        EXCUSED: 'bg-blue-100 text-blue-800'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }

    const formatTime = (timeString) => {
      if (!timeString) return '-'
      return new Date(timeString).toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // Watch viewType changes
    watch(viewType, () => {
      loadAttendanceData()
    })

    // Initialize
    onMounted(() => {
      loadAttendanceData()
      loadAvailableUsers()
    })

    return {
      selectedDate,
      viewType,
      loading,
      attendanceRecords,
      studentStats,
      facultyStats,
      overallStats,
      showMarkAttendanceModal,
      availableUsers,
      attendanceForm,
      loadAttendanceData,
      submitAttendance,
      closeMarkAttendanceModal,
      editAttendance,
      deleteRecord,
      exportAttendance,
      getStatusClass,
      formatTime
    }
  }
}
</script>

<style scoped>
/* Add any custom styles here if needed */
</style>
