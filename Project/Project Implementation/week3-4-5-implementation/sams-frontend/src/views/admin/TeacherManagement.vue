<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Teacher Management</h1>
        <p class="mt-2 text-gray-600">Manage teacher profiles and information</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors"
      >
        + Add Teacher Profile
      </button>
    </div>

    <!-- Filters -->
    <div class="mb-6 bg-white rounded-lg shadow p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Filter Teachers</h2>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Department</label>
          <select v-model="filters.department" class="w-full px-4 py-2 border border-gray-300 rounded-md">
            <option value="">All Departments</option>
            <option value="Computer Science">Computer Science</option>
            <option value="Mathematics">Mathematics</option>
            <option value="Physics">Physics</option>
            <option value="Engineering">Engineering</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Designation</label>
          <select v-model="filters.designation" class="w-full px-4 py-2 border border-gray-300 rounded-md">
            <option value="">All Designations</option>
            <option value="Professor">Professor</option>
            <option value="Associate Professor">Associate Professor</option>
            <option value="Assistant Professor">Assistant Professor</option>
            <option value="Lecturer">Lecturer</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Status</label>
          <select v-model="filters.active" class="w-full px-4 py-2 border border-gray-300 rounded-md">
            <option :value="undefined">All Status</option>
            <option :value="true">Active</option>
            <option :value="false">Inactive</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="applyFilters"
            class="w-full px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 font-medium transition-colors"
          >
            Apply Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Teachers Table -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Teacher</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Designation</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Experience</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Courses</th>
            <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="teacher in teachers" :key="teacher.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-semibold">
                  {{ getInitials(teacher.userName) }}
                </div>
                <div class="ml-4">
                  <div class="text-sm font-medium text-gray-900">{{ teacher.userName }}</div>
                  <div class="text-sm text-gray-500">{{ teacher.userEmail }}</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ teacher.department || 'N/A' }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ teacher.designation || 'N/A' }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ teacher.yearsOfExperience || 0 }} years</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ teacher.totalCoursesTaught || 0 }} courses</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-center">
              <span
                class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full cursor-pointer"
                :class="teacher.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                @click="toggleStatus(teacher.id)"
              >
                {{ teacher.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="viewTeacher(teacher)"
                class="text-blue-600 hover:text-blue-900 mr-3"
              >
                View
              </button>
              <button
                @click="editTeacher(teacher)"
                class="text-indigo-600 hover:text-indigo-900 mr-3"
              >
                Edit
              </button>
              <button
                @click="deleteTeacher(teacher.id)"
                class="text-red-600 hover:text-red-900"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-full max-w-3xl shadow-lg rounded-md bg-white">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-bold text-gray-900">
            {{ showEditModal ? 'Edit Teacher Profile' : 'Create Teacher Profile' }}
          </h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <form @submit.prevent="saveTeacher" class="space-y-4">
          <!-- Basic Information -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Qualification *</label>
              <input
                v-model="form.qualification"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
                placeholder="e.g., Ph.D. in Computer Science"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Specialization *</label>
              <input
                v-model="form.specialization"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
                placeholder="e.g., Machine Learning, AI"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Department *</label>
              <select v-model="form.department" required class="w-full px-3 py-2 border border-gray-300 rounded-md">
                <option value="">Select Department</option>
                <option value="Computer Science">Computer Science</option>
                <option value="Mathematics">Mathematics</option>
                <option value="Physics">Physics</option>
                <option value="Engineering">Engineering</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Designation *</label>
              <select v-model="form.designation" required class="w-full px-3 py-2 border border-gray-300 rounded-md">
                <option value="">Select Designation</option>
                <option value="Professor">Professor</option>
                <option value="Associate Professor">Associate Professor</option>
                <option value="Assistant Professor">Assistant Professor</option>
                <option value="Lecturer">Lecturer</option>
              </select>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Employee ID</label>
              <input
                v-model="form.employeeId"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
                placeholder="e.g., EMP-001"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Years of Experience</label>
              <input
                v-model.number="form.yearsOfExperience"
                type="number"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
            </div>
          </div>

          <!-- Contact Information -->
          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Office Room</label>
              <input
                v-model="form.officeRoom"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
                placeholder="e.g., Room 305"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Office Phone</label>
              <input
                v-model="form.officePhone"
                type="tel"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
                placeholder="e.g., +1-555-0123"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Office Email</label>
              <input
                v-model="form.officeEmail"
                type="email"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
                placeholder="e.g., teacher@university.edu"
              />
            </div>
          </div>

          <!-- Bio -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Bio</label>
            <textarea
              v-model="form.bio"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="Brief biography..."
            ></textarea>
          </div>

          <!-- Research Interests -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Research Interests</label>
            <input
              v-model="form.researchInterests"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="e.g., Machine Learning, Data Science"
            />
          </div>

          <!-- Capacity Settings -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Max Courses Per Semester</label>
              <input
                v-model.number="form.maxCoursesPerSemester"
                type="number"
                min="1"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Max Students Per Course</label>
              <input
                v-model.number="form.maxStudentsPerCourse"
                type="number"
                min="1"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
            </div>
          </div>

          <!-- Checkboxes -->
          <div class="flex items-center space-x-6">
            <label class="flex items-center">
              <input
                v-model="form.availableForConsultation"
                type="checkbox"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span class="ml-2 text-sm text-gray-700">Available for Consultation</span>
            </label>
            <label class="flex items-center">
              <input
                v-model="form.active"
                type="checkbox"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span class="ml-2 text-sm text-gray-700">Active</span>
            </label>
          </div>

          <!-- Form Actions -->
          <div class="flex justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              @click="closeModal"
              class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="loading"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {{ loading ? 'Saving...' : 'Save Teacher Profile' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- View Details Modal -->
    <div v-if="showViewModal && selectedTeacher" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-full max-w-4xl shadow-lg rounded-md bg-white">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-bold text-gray-900">Teacher Profile Details</h3>
          <button @click="showViewModal = false" class="text-gray-400 hover:text-gray-600">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <div class="space-y-6">
          <!-- Basic Info -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-sm font-medium text-gray-500">Name</label>
              <p class="text-sm text-gray-900">{{ selectedTeacher.userName }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500">Email</label>
              <p class="text-sm text-gray-900">{{ selectedTeacher.userEmail }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500">Department</label>
              <p class="text-sm text-gray-900">{{ selectedTeacher.department }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500">Designation</label>
              <p class="text-sm text-gray-900">{{ selectedTeacher.designation }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500">Qualification</label>
              <p class="text-sm text-gray-900">{{ selectedTeacher.qualification }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500">Specialization</label>
              <p class="text-sm text-gray-900">{{ selectedTeacher.specialization }}</p>
            </div>
          </div>

          <!-- Bio -->
          <div v-if="selectedTeacher.bio">
            <label class="text-sm font-medium text-gray-500">Bio</label>
            <p class="text-sm text-gray-900">{{ selectedTeacher.bio }}</p>
          </div>

          <!-- Statistics -->
          <div class="grid grid-cols-4 gap-4">
            <div class="bg-blue-50 p-4 rounded-lg">
              <p class="text-sm text-blue-600 font-medium">Experience</p>
              <p class="text-2xl font-bold text-blue-900">{{ selectedTeacher.yearsOfExperience || 0 }} years</p>
            </div>
            <div class="bg-green-50 p-4 rounded-lg">
              <p class="text-sm text-green-600 font-medium">Courses Taught</p>
              <p class="text-2xl font-bold text-green-900">{{ selectedTeacher.totalCoursesTaught || 0 }}</p>
            </div>
            <div class="bg-purple-50 p-4 rounded-lg">
              <p class="text-sm text-purple-600 font-medium">Students Taught</p>
              <p class="text-2xl font-bold text-purple-900">{{ selectedTeacher.totalStudentsTaught || 0 }}</p>
            </div>
            <div class="bg-yellow-50 p-4 rounded-lg">
              <p class="text-sm text-yellow-600 font-medium">Avg Rating</p>
              <p class="text-2xl font-bold text-yellow-900">{{ selectedTeacher.averageRating?.toFixed(1) || 'N/A' }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const teachers = ref([])
const loading = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const showViewModal = ref(false)
const selectedTeacher = ref(null)

const filters = ref({
  department: '',
  designation: '',
  active: undefined
})

const form = ref({
  userId: null,
  qualification: '',
  specialization: '',
  department: '',
  designation: '',
  employeeId: '',
  yearsOfExperience: 0,
  officeRoom: '',
  officePhone: '',
  officeEmail: '',
  bio: '',
  researchInterests: '',
  maxCoursesPerSemester: 4,
  maxStudentsPerCourse: 40,
  availableForConsultation: true,
  active: true
})

onMounted(async () => {
  await loadTeachers()
})

async function loadTeachers() {
  try {
    loading.value = true
    const response = await api.getAllTeacherProfiles()
    teachers.value = response.data
  } catch (error) {
    console.error('Error loading teachers:', error)
    alert('Failed to load teachers')
  } finally {
    loading.value = false
  }
}

async function applyFilters() {
  try {
    loading.value = true
    const response = await api.getTeachersByFilters(filters.value)
    teachers.value = response.data
  } catch (error) {
    console.error('Error applying filters:', error)
    alert('Failed to apply filters')
  } finally {
    loading.value = false
  }
}

function getInitials(name) {
  return name
    .split(' ')
    .map(n => n[0])
    .join('')
    .toUpperCase()
}

function viewTeacher(teacher) {
  selectedTeacher.value = teacher
  showViewModal.value = true
}

function editTeacher(teacher) {
  selectedTeacher.value = teacher
  Object.keys(form.value).forEach(key => {
    if (teacher[key] !== undefined) {
      form.value[key] = teacher[key]
    }
  })
  showEditModal.value = true
}

async function saveTeacher() {
  try {
    loading.value = true
    if (showEditModal.value && selectedTeacher.value) {
      await api.updateTeacherProfile(selectedTeacher.value.id, form.value)
    } else {
      await api.createTeacherProfile(form.value)
    }
    await loadTeachers()
    closeModal()
    alert('Teacher profile saved successfully!')
  } catch (error) {
    console.error('Error saving teacher:', error)
    alert(error.response?.data || 'Failed to save teacher profile')
  } finally {
    loading.value = false
  }
}

async function toggleStatus(profileId) {
  try {
    await api.toggleTeacherProfileStatus(profileId)
    await loadTeachers()
  } catch (error) {
    console.error('Error toggling status:', error)
    alert('Failed to toggle status')
  }
}

async function deleteTeacher(profileId) {
  if (!confirm('Are you sure you want to delete this teacher profile?')) return

  try {
    await api.deleteTeacherProfile(profileId)
    await loadTeachers()
    alert('Teacher profile deleted successfully')
  } catch (error) {
    console.error('Error deleting teacher:', error)
    alert('Failed to delete teacher profile')
  }
}

function closeModal() {
  showCreateModal.value = false
  showEditModal.value = false
  selectedTeacher.value = null
  // Reset form
  Object.keys(form.value).forEach(key => {
    if (typeof form.value[key] === 'boolean') {
      form.value[key] = key === 'active' || key === 'availableForConsultation'
    } else if (typeof form.value[key] === 'number') {
      form.value[key] = key === 'maxCoursesPerSemester' ? 4 : key === 'maxStudentsPerCourse' ? 40 : 0
    } else {
      form.value[key] = ''
    }
  })
}
</script>
