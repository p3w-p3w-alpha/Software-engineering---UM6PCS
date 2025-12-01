<!--
  Teacher Schedule view for managing faculty office hours and schedules
  admin dashboard for viewing and editing teacher availability
  definately helps students know when thier professors are available
-->
<template>
  <div class="max-w-7xl mx-auto">
    <!-- Toast Notification - shows success/error feedback to user -->
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
      <h1 class="text-3xl font-bold text-gray-900">Teacher Schedules</h1>
      <p class="mt-2 text-gray-600">View and manage teacher office hours and schedules</p>
    </div>

    <!-- Teacher Selector -->
    <div class="mb-6 bg-white rounded-lg shadow p-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Select Teacher</label>
          <select
            v-model="selectedTeacherId"
            @change="loadTeacherSchedule"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">-- Select a teacher --</option>
            <option v-for="teacher in teachers" :key="teacher.id" :value="teacher.userId">
              {{ teacher.userName }} - {{ teacher.department }}
            </option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="showAddModal = true"
            :disabled="!selectedTeacherId"
            class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed font-medium transition-colors"
          >
            + Add Office Hours
          </button>
        </div>
      </div>
    </div>

    <!-- Schedule Display -->
    <div v-if="selectedTeacherId && schedule" class="bg-white rounded-lg shadow">
      <!-- Statistics Cards -->
      <div class="p-6 border-b border-gray-200">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="bg-blue-50 rounded-lg p-4">
            <p class="text-sm text-blue-600 font-medium">Total Office Hours</p>
            <p class="text-2xl font-bold text-blue-900">{{ schedule.totalOfficeHours || 0 }}</p>
          </div>
          <div class="bg-green-50 rounded-lg p-4">
            <p class="text-sm text-green-600 font-medium">Hours per Week</p>
            <p class="text-2xl font-bold text-green-900">{{ schedule.totalWeeklyHours || 0 }}</p>
          </div>
          <div class="bg-purple-50 rounded-lg p-4">
            <p class="text-sm text-purple-600 font-medium">Active Days</p>
            <p class="text-2xl font-bold text-purple-900">{{ schedule.activeDays || 0 }}</p>
          </div>
          <div class="bg-orange-50 rounded-lg p-4">
            <p class="text-sm text-orange-600 font-medium">Avg. Students/Slot</p>
            <p class="text-2xl font-bold text-orange-900">{{ schedule.averageStudentsPerSlot || 0 }}</p>
          </div>
        </div>
      </div>

      <!-- Weekly Calendar View -->
      <div class="p-6">
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Weekly Schedule</h2>
        <div class="grid grid-cols-7 gap-2">
          <!-- Day Headers -->
          <div v-for="day in daysOfWeek" :key="day" class="text-center font-semibold text-gray-700 p-2 bg-gray-100 rounded">
            {{ day }}
          </div>

          <!-- Office Hours Slots -->
          <div v-for="day in daysOfWeek" :key="'slots-' + day" class="border border-gray-200 rounded-lg p-2 min-h-[200px]">
            <div v-if="getOfficeHoursForDay(day).length > 0" class="space-y-2">
              <div
                v-for="slot in getOfficeHoursForDay(day)"
                :key="slot.id"
                class="bg-blue-50 border-l-4 border-blue-500 rounded p-3 hover:bg-blue-100 transition-colors cursor-pointer"
                @click="viewSlotDetails(slot)"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <p class="text-sm font-semibold text-blue-900">
                      {{ formatTime(slot.startTime) }} - {{ formatTime(slot.endTime) }}
                    </p>
                    <p class="text-xs text-gray-600 mt-1">
                      <span class="inline-block mr-2">📍 {{ slot.location || 'Not specified' }}</span>
                    </p>
                    <p class="text-xs text-gray-600">
                      <span :class="getConsultationTypeClass(slot.consultationType)">
                        {{ slot.consultationType }}
                      </span>
                    </p>
                    <p class="text-xs text-gray-500 mt-1">
                      Capacity: {{ slot.maxStudentsPerSlot }} | {{ slot.slotDurationMinutes }} min
                    </p>
                  </div>
                  <button
                    @click.stop="editSlot(slot)"
                    class="text-blue-600 hover:text-blue-800"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="flex items-center justify-center h-full text-gray-400 text-sm">
              No office hours
            </div>
          </div>
        </div>
      </div>

      <!-- Office Hours List -->
      <div class="p-6 border-t border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900 mb-4">All Office Hours</h2>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Day</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Time</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Capacity</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="slot in allOfficeHours" :key="slot.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ slot.dayOfWeek }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatTime(slot.startTime) }} - {{ formatTime(slot.endTime) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ slot.location || 'N/A' }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span :class="getConsultationTypeClass(slot.consultationType)">
                    {{ slot.consultationType }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ slot.maxStudentsPerSlot }} students
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span :class="slot.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                    class="px-2 py-1 text-xs font-medium rounded-full">
                    {{ slot.active ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                  <button @click="editSlot(slot)" class="text-blue-600 hover:text-blue-900">Edit</button>
                  <button @click="deleteSlot(slot.id)" class="text-red-600 hover:text-red-900">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No teacher selected</h3>
      <p class="mt-1 text-sm text-gray-500">Select a teacher to view their schedule</p>
    </div>

    <!-- Add/Edit Office Hours Modal -->
    <div v-if="showAddModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-2xl font-bold text-gray-900">{{ editingSlot ? 'Edit' : 'Add' }} Office Hours</h2>
        </div>

        <div class="p-6 space-y-4">
          <!-- Day of Week -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Day of Week *</label>
            <select v-model="officeHoursForm.dayOfWeek" class="w-full px-4 py-2 border border-gray-300 rounded-md">
              <option value="">Select Day</option>
              <option value="MONDAY">Monday</option>
              <option value="TUESDAY">Tuesday</option>
              <option value="WEDNESDAY">Wednesday</option>
              <option value="THURSDAY">Thursday</option>
              <option value="FRIDAY">Friday</option>
              <option value="SATURDAY">Saturday</option>
              <option value="SUNDAY">Sunday</option>
            </select>
          </div>

          <!-- Time -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Start Time *</label>
              <input
                type="time"
                v-model="officeHoursForm.startTime"
                class="w-full px-4 py-2 border border-gray-300 rounded-md"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">End Time *</label>
              <input
                type="time"
                v-model="officeHoursForm.endTime"
                class="w-full px-4 py-2 border border-gray-300 rounded-md"
              />
            </div>
          </div>

          <!-- Location & Type -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Location</label>
              <input
                type="text"
                v-model="officeHoursForm.location"
                placeholder="e.g., CS-305"
                class="w-full px-4 py-2 border border-gray-300 rounded-md"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Consultation Type *</label>
              <select v-model="officeHoursForm.consultationType" class="w-full px-4 py-2 border border-gray-300 rounded-md">
                <option value="IN_PERSON">In-Person</option>
                <option value="ONLINE">Online</option>
                <option value="BOTH">Both</option>
              </select>
            </div>
          </div>

          <!-- Meeting Link -->
          <div v-if="officeHoursForm.consultationType === 'ONLINE' || officeHoursForm.consultationType === 'BOTH'">
            <label class="block text-sm font-medium text-gray-700 mb-2">Meeting Link</label>
            <input
              type="url"
              v-model="officeHoursForm.meetingLink"
              placeholder="https://zoom.us/j/..."
              class="w-full px-4 py-2 border border-gray-300 rounded-md"
            />
          </div>

          <!-- Capacity Settings -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Max Students per Slot</label>
              <input
                type="number"
                v-model.number="officeHoursForm.maxStudentsPerSlot"
                min="1"
                class="w-full px-4 py-2 border border-gray-300 rounded-md"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Slot Duration (minutes)</label>
              <select v-model.number="officeHoursForm.slotDurationMinutes" class="w-full px-4 py-2 border border-gray-300 rounded-md">
                <option :value="15">15 minutes</option>
                <option :value="20">20 minutes</option>
                <option :value="30">30 minutes</option>
                <option :value="45">45 minutes</option>
                <option :value="60">60 minutes</option>
              </select>
            </div>
          </div>

          <!-- Booking Settings -->
          <div class="space-y-3">
            <div class="flex items-center">
              <input
                type="checkbox"
                v-model="officeHoursForm.bookingRequired"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded"
              />
              <label class="ml-2 text-sm text-gray-700">Booking Required</label>
            </div>
            <div v-if="officeHoursForm.bookingRequired">
              <label class="block text-sm font-medium text-gray-700 mb-2">Advance Booking Days</label>
              <input
                type="number"
                v-model.number="officeHoursForm.advanceBookingDays"
                min="1"
                class="w-full px-4 py-2 border border-gray-300 rounded-md"
              />
            </div>
            <div class="flex items-center">
              <input
                type="checkbox"
                v-model="officeHoursForm.recurring"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded"
              />
              <label class="ml-2 text-sm text-gray-700">Recurring Weekly</label>
            </div>
          </div>

          <!-- Notes -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Notes</label>
            <textarea
              v-model="officeHoursForm.notes"
              rows="3"
              placeholder="Additional information about this office hours slot..."
              class="w-full px-4 py-2 border border-gray-300 rounded-md"
            ></textarea>
          </div>

          <!-- Active Status -->
          <div class="flex items-center">
            <input
              type="checkbox"
              v-model="officeHoursForm.active"
              class="h-4 w-4 text-blue-600 border-gray-300 rounded"
            />
            <label class="ml-2 text-sm text-gray-700">Active</label>
          </div>
        </div>

        <div class="p-6 border-t border-gray-200 flex justify-end space-x-3">
          <button
            @click="closeModal"
            class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 font-medium"
          >
            Cancel
          </button>
          <button
            @click="saveOfficeHours"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium"
          >
            {{ editingSlot ? 'Update' : 'Create' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// teacher schedule management script - handles office hours and weekly calendar
// using composition API for managing teacher schedules and time slots
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'

// reactive state - holds all teacher data and schedule info
const teachers = ref([])
const selectedTeacherId = ref('')
const schedule = ref(null)
const allOfficeHours = ref([])
const showAddModal = ref(false)
const editingSlot = ref(null)

// days of the week array - could have used a constant but this works fine
const daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']

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

const officeHoursForm = ref({
  teacherId: null,
  dayOfWeek: '',
  startTime: '',
  endTime: '',
  location: '',
  consultationType: 'IN_PERSON',
  meetingLink: '',
  maxStudentsPerSlot: 2,
  slotDurationMinutes: 30,
  bookingRequired: true,
  advanceBookingDays: 7,
  recurring: true,
  notes: '',
  active: true
})

onMounted(async () => {
  await loadTeachers()
})

async function loadTeachers() {
  try {
    const response = await api.getAllTeacherProfiles()
    teachers.value = response.data
  } catch (error) {
    console.error('Error loading teachers:', error)
  }
}

async function loadTeacherSchedule() {
  if (!selectedTeacherId.value) {
    schedule.value = null
    allOfficeHours.value = []
    return
  }

  try {
    // Load schedule
    const scheduleResponse = await api.getTeacherSchedule(selectedTeacherId.value)
    schedule.value = scheduleResponse.data

    // Load office hours
    const officeHoursResponse = await api.getTeacherOfficeHours(selectedTeacherId.value)
    allOfficeHours.value = officeHoursResponse.data
  } catch (error) {
    console.error('Error loading teacher schedule:', error)
    schedule.value = null
    allOfficeHours.value = []
  }
}

function getOfficeHoursForDay(day) {
  if (!allOfficeHours.value) return []
  return allOfficeHours.value.filter(slot => slot.dayOfWeek === day && slot.active)
}

function formatTime(timeString) {
  if (!timeString) return ''
  const [hours, minutes] = timeString.split(':')
  const hour = parseInt(hours)
  const ampm = hour >= 12 ? 'PM' : 'AM'
  const displayHour = hour % 12 || 12
  return `${displayHour}:${minutes} ${ampm}`
}

function getConsultationTypeClass(type) {
  const classes = {
    'IN_PERSON': 'px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800',
    'ONLINE': 'px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800',
    'BOTH': 'px-2 py-1 text-xs font-medium rounded-full bg-purple-100 text-purple-800'
  }
  return classes[type] || 'px-2 py-1 text-xs font-medium rounded-full bg-gray-100 text-gray-800'
}

function viewSlotDetails(slot) {
  // TODO: Open a detailed view modal
}

function editSlot(slot) {
  editingSlot.value = slot
  officeHoursForm.value = {
    teacherId: selectedTeacherId.value,
    dayOfWeek: slot.dayOfWeek,
    startTime: slot.startTime,
    endTime: slot.endTime,
    location: slot.location || '',
    consultationType: slot.consultationType,
    meetingLink: slot.meetingLink || '',
    maxStudentsPerSlot: slot.maxStudentsPerSlot,
    slotDurationMinutes: slot.slotDurationMinutes,
    bookingRequired: slot.bookingRequired,
    advanceBookingDays: slot.advanceBookingDays || 7,
    recurring: slot.recurring,
    notes: slot.notes || '',
    active: slot.active
  }
  showAddModal.value = true
}

async function saveOfficeHours() {
  try {
    const data = {
      ...officeHoursForm.value,
      teacherId: selectedTeacherId.value
    }

    if (editingSlot.value) {
      await api.updateOfficeHours(editingSlot.value.id, data)
    } else {
      await api.createOfficeHours(data)
    }

    closeModal()
    await loadTeacherSchedule()
  } catch (error) {
    console.error('Error saving office hours:', error)
    showNotification('Error saving office hours. Please try again.', 'error')
  }
}

async function deleteSlot(id) {
  if (!confirm('Are you sure you want to delete this office hours slot?')) return

  try {
    await api.deleteOfficeHours(id)
    await loadTeacherSchedule()
  } catch (error) {
    console.error('Error deleting office hours:', error)
    showNotification('Error deleting office hours. Please try again.', 'error')
  }
}

function closeModal() {
  showAddModal.value = false
  editingSlot.value = null
  officeHoursForm.value = {
    teacherId: null,
    dayOfWeek: '',
    startTime: '',
    endTime: '',
    location: '',
    consultationType: 'IN_PERSON',
    meetingLink: '',
    maxStudentsPerSlot: 2,
    slotDurationMinutes: 30,
    bookingRequired: true,
    advanceBookingDays: 7,
    recurring: true,
    notes: '',
    active: true
  }
}
</script>
