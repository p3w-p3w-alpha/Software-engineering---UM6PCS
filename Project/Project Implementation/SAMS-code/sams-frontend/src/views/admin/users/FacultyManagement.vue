<!--
  Faculty Management view - admin dashboard for managing teacher accounts and courses
  definately helps keep track of all thier courses and office hours
  took a while to implement the course assignment features
-->
<template>
  <div class="max-w-7xl mx-auto">
    <!-- Toast Notification - shows success/error messages -->
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
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Faculty Management</h1>
        <p class="mt-2 text-gray-600">Manage faculty members, their courses, and office hours</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors"
      >
        <i class="pi pi-plus mr-2"></i>Add Faculty Member
      </button>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Total Faculty</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.total }}</p>
          </div>
          <i class="pi pi-users text-3xl text-blue-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Active Teaching</p>
            <p class="text-2xl font-bold text-green-600">{{ stats.activeTeaching }}</p>
          </div>
          <i class="pi pi-book text-3xl text-green-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Total Courses</p>
            <p class="text-2xl font-bold text-purple-600">{{ stats.totalCourses }}</p>
          </div>
          <i class="pi pi-briefcase text-3xl text-purple-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Avg Course Load</p>
            <p class="text-2xl font-bold text-orange-600">{{ stats.avgCourseLoad }}</p>
          </div>
          <i class="pi pi-chart-bar text-3xl text-orange-500"></i>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Department</label>
          <select v-model="filters.department" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All Departments</option>
            <option value="CS">Computer Science</option>
            <option value="MATH">Mathematics</option>
            <option value="PHY">Physics</option>
            <option value="CHEM">Chemistry</option>
            <option value="BIO">Biology</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
          <select v-model="filters.status" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All Status</option>
            <option value="ACTIVE">Active</option>
            <option value="ON_LEAVE">On Leave</option>
            <option value="INACTIVE">Inactive</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Rank</label>
          <select v-model="filters.rank" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All Ranks</option>
            <option value="PROFESSOR">Professor</option>
            <option value="ASSOCIATE">Associate Professor</option>
            <option value="ASSISTANT">Assistant Professor</option>
            <option value="LECTURER">Lecturer</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Course Load</label>
          <select v-model="filters.courseLoad" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">Any Load</option>
            <option value="low">Low (1-2 courses)</option>
            <option value="normal">Normal (3-4 courses)</option>
            <option value="high">High (5+ courses)</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Faculty Table -->
    <div class="bg-white rounded-lg shadow">
      <DataTable
        :columns="columns"
        :data="filteredFaculty"
        :loading="loading"
        searchable
        search-placeholder="Search faculty by name, email, or employee ID..."
        :search-keys="['username', 'email', 'employeeId', 'firstName', 'lastName']"
      >
        <template #cell-employeeId="{ row }">
          <span class="font-mono text-sm">{{ row.employeeId || 'N/A' }}</span>
        </template>

        <template #cell-name="{ row }">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-full bg-gradient-to-br from-green-500 to-blue-500 flex items-center justify-center text-white font-semibold">
              {{ getInitials(row) }}
            </div>
            <div>
              <p class="font-medium text-gray-900">{{ row.firstName }} {{ row.lastName }}</p>
              <p class="text-xs text-gray-500">{{ row.email }}</p>
            </div>
          </div>
        </template>

        <template #cell-department="{ row }">
          <span class="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
            {{ row.department || 'Unassigned' }}
          </span>
        </template>

        <template #cell-rank="{ row }">
          <span class="text-sm font-medium">{{ row.rank || 'N/A' }}</span>
        </template>

        <template #cell-courses="{ row }">
          <div class="flex items-center gap-2">
            <span class="text-sm">{{ row.currentCourses?.length || 0 }} courses</span>
            <button
              @click="viewCourses(row)"
              class="text-blue-600 hover:text-blue-900"
            >
              <i class="pi pi-eye text-xs"></i>
            </button>
          </div>
        </template>

        <template #cell-officeHours="{ row }">
          <button
            @click="viewOfficeHours(row)"
            class="text-sm text-purple-600 hover:text-purple-900 font-medium"
          >
            View Schedule
          </button>
        </template>

        <template #cell-status="{ value }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="getFacultyStatusClass(value)"
          >
            {{ value || 'ACTIVE' }}
          </span>
        </template>

        <template #row-actions="{ row }">
          <div class="flex items-center gap-2">
            <button
              @click="viewFaculty(row)"
              class="text-blue-600 hover:text-blue-900 text-sm font-medium"
              title="View Details"
            >
              <i class="pi pi-eye"></i>
            </button>
            <button
              @click="editFaculty(row)"
              class="text-green-600 hover:text-green-900 text-sm font-medium"
              title="Edit"
            >
              <i class="pi pi-pencil"></i>
            </button>
            <button
              @click="assignCourse(row)"
              class="text-purple-600 hover:text-purple-900 text-sm font-medium"
              title="Assign Course"
            >
              <i class="pi pi-plus-circle"></i>
            </button>
            <button
              @click="viewPerformance(row)"
              class="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
              title="Performance Analytics"
            >
              <i class="pi pi-chart-line"></i>
            </button>
            <button
              @click="toggleStatus(row)"
              :class="row.active ? 'text-yellow-600 hover:text-yellow-900' : 'text-green-600 hover:text-green-900'"
              class="text-sm font-medium"
              :title="row.active ? 'Deactivate' : 'Activate'"
            >
              <i :class="row.active ? 'pi pi-lock' : 'pi pi-unlock'"></i>
            </button>
            <button
              @click="confirmDelete(row)"
              class="text-red-600 hover:text-red-900 text-sm font-medium"
              title="Delete"
            >
              <i class="pi pi-trash"></i>
            </button>
          </div>
        </template>
      </DataTable>
    </div>

    <!-- Create/Edit Faculty Modal -->
    <Modal
      v-model="showCreateModal"
      :title="editingFaculty ? 'Edit Faculty Member' : 'Create New Faculty Member'"
      :confirm-text="editingFaculty ? 'Update' : 'Create'"
      :confirm-disabled="!isFormValid || submitting"
      @confirm="saveFaculty"
    >
      <div class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">First Name *</label>
            <input
              v-model="facultyForm.firstName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Last Name *</label>
            <input
              v-model="facultyForm.lastName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Employee ID</label>
            <input
              v-model="facultyForm.employeeId"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Auto-generated if empty"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Email *</label>
            <input
              v-model="facultyForm.email"
              type="email"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Username *</label>
            <input
              v-model="facultyForm.username"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              :disabled="editingFaculty"
              required
            />
          </div>
          <div v-if="!editingFaculty">
            <label class="block text-sm font-medium text-gray-700 mb-2">Password *</label>
            <input
              v-model="facultyForm.password"
              type="password"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Department *</label>
            <select
              v-model="facultyForm.department"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="">Select Department</option>
              <option value="CS">Computer Science</option>
              <option value="MATH">Mathematics</option>
              <option value="PHY">Physics</option>
              <option value="CHEM">Chemistry</option>
              <option value="BIO">Biology</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Academic Rank</label>
            <select
              v-model="facultyForm.rank"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select Rank</option>
              <option value="PROFESSOR">Professor</option>
              <option value="ASSOCIATE">Associate Professor</option>
              <option value="ASSISTANT">Assistant Professor</option>
              <option value="LECTURER">Lecturer</option>
            </select>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Office Location</label>
          <input
            v-model="facultyForm.office"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="e.g., Building A, Room 203"
          />
        </div>

        <div>
          <label class="flex items-center">
            <input
              v-model="facultyForm.active"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
            />
            <span class="ml-2 text-sm text-gray-700">Active Account</span>
          </label>
        </div>
      </div>
    </Modal>

    <!-- View Faculty Details Modal -->
    <Modal
      v-model="showDetailsModal"
      title="Faculty Member Details"
      :confirm-text="null"
      cancel-text="Close"
    >
      <div v-if="selectedFaculty" class="space-y-4">
        <div class="flex items-center gap-4 pb-4 border-b">
          <div class="w-16 h-16 rounded-full bg-gradient-to-br from-green-500 to-blue-500 flex items-center justify-center text-white text-2xl font-bold">
            {{ getInitials(selectedFaculty) }}
          </div>
          <div>
            <h3 class="text-lg font-semibold">{{ selectedFaculty.firstName }} {{ selectedFaculty.lastName }}</h3>
            <p class="text-gray-600">{{ selectedFaculty.rank || 'Faculty Member' }}</p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <p class="text-sm text-gray-600">Employee ID</p>
            <p class="font-medium">{{ selectedFaculty.employeeId || 'N/A' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Email</p>
            <p class="font-medium">{{ selectedFaculty.email }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Department</p>
            <p class="font-medium">{{ selectedFaculty.department || 'Unassigned' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Office</p>
            <p class="font-medium">{{ selectedFaculty.office || 'N/A' }}</p>
          </div>
        </div>

        <div>
          <h4 class="font-medium mb-2">Current Courses ({{ selectedFaculty.currentCourses?.length || 0 }})</h4>
          <div class="space-y-2">
            <div v-for="course in (selectedFaculty.currentCourses || [])" :key="course.id"
                 class="flex justify-between items-center p-2 bg-gray-50 rounded">
              <div>
                <p class="font-medium">{{ course.name }}</p>
                <p class="text-sm text-gray-600">{{ course.code }} • {{ course.students }} students</p>
              </div>
              <button class="text-blue-600 hover:text-blue-900">
                <i class="pi pi-arrow-right"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
// faculty management script - handles CRUD operations for teacher accounts
// wierd that we need separate management for faculty vs students but it works
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../../../services/api'
import DataTable from '../../../components/DataTable.vue'
import Modal from '../../../components/Modal.vue'

const router = useRouter()

// Toast notification state - recieve feedback from API calls
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

function showNotification(message, type = 'success') {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 5000)
}

// Reactive data
const faculty = ref([])
const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const showDeleteModal = ref(false)
const editingFaculty = ref(null)
const selectedFaculty = ref(null)
const facultyToDelete = ref(null)

// Filters
const filters = ref({
  department: '',
  status: '',
  rank: '',
  courseLoad: ''
})

// Stats
const stats = ref({
  total: 0,
  activeTeaching: 0,
  totalCourses: 0,
  avgCourseLoad: 0
})

// Form data
const facultyForm = ref({
  username: '',
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  employeeId: '',
  department: '',
  rank: '',
  office: '',
  active: true
})

// Table columns
const columns = [
  { key: 'employeeId', label: 'Employee ID' },
  { key: 'name', label: 'Name' },
  { key: 'department', label: 'Department' },
  { key: 'rank', label: 'Rank' },
  { key: 'courses', label: 'Courses' },
  { key: 'officeHours', label: 'Office Hours' },
  { key: 'status', label: 'Status' }
]

// Computed
const filteredFaculty = computed(() => {
  let result = faculty.value.filter(f => f.role === 'FACULTY')

  if (filters.value.department) {
    result = result.filter(f => f.department === filters.value.department)
  }

  if (filters.value.status) {
    result = result.filter(f => {
      if (filters.value.status === 'ACTIVE') return f.active
      if (filters.value.status === 'INACTIVE') return !f.active
      return true
    })
  }

  if (filters.value.rank) {
    result = result.filter(f => f.rank === filters.value.rank)
  }

  return result
})

const isFormValid = computed(() => {
  if (editingFaculty.value) {
    return facultyForm.value.firstName && facultyForm.value.lastName &&
           facultyForm.value.email && facultyForm.value.department
  }
  return facultyForm.value.username && facultyForm.value.email &&
         facultyForm.value.password && facultyForm.value.firstName &&
         facultyForm.value.lastName && facultyForm.value.department
})

// Methods
const loadFaculty = async () => {
  try {
    loading.value = true
    const response = await api.getAllUsers()
    faculty.value = response.data

    // Calculate stats
    const facultyUsers = response.data.filter(u => u.role === 'FACULTY')
    stats.value = {
      total: facultyUsers.length,
      activeTeaching: facultyUsers.filter(f => f.active && f.currentCourses?.length > 0).length,
      totalCourses: facultyUsers.reduce((sum, f) => sum + (f.currentCourses?.length || 0), 0),
      avgCourseLoad: facultyUsers.length ?
        (facultyUsers.reduce((sum, f) => sum + (f.currentCourses?.length || 0), 0) / facultyUsers.length).toFixed(1) : 0
    }
  } catch (error) {
    console.error('Error loading faculty:', error)
  } finally {
    loading.value = false
  }
}

const saveFaculty = async () => {
  try {
    submitting.value = true
    const userData = {
      ...facultyForm.value,
      role: 'FACULTY'
    }

    if (editingFaculty.value) {
      await api.updateUser(editingFaculty.value.id, userData)
    } else {
      await api.createUser(userData)
    }

    showCreateModal.value = false
    resetForm()
    await loadFaculty()
  } catch (error) {
    console.error('Error saving faculty:', error)
    showNotification('Failed to save faculty member', 'error')
  } finally {
    submitting.value = false
  }
}

const viewFaculty = (faculty) => {
  selectedFaculty.value = faculty
  showDetailsModal.value = true
}

const editFaculty = (faculty) => {
  editingFaculty.value = faculty
  facultyForm.value = {
    username: faculty.username,
    email: faculty.email,
    firstName: faculty.firstName || '',
    lastName: faculty.lastName || '',
    employeeId: faculty.employeeId || '',
    department: faculty.department || '',
    rank: faculty.rank || '',
    office: faculty.office || '',
    active: faculty.active
  }
  showCreateModal.value = true
}

const viewCourses = (faculty) => {
  router.push(`/admin/faculty/${faculty.id}/courses`)
}

const viewOfficeHours = (faculty) => {
  router.push(`/admin/faculty/${faculty.id}/office-hours`)
}

const assignCourse = (faculty) => {
  router.push(`/admin/faculty/${faculty.id}/assign-course`)
}

const viewPerformance = (faculty) => {
  router.push(`/admin/analytics/faculty/${faculty.id}`)
}

const toggleStatus = async (faculty) => {
  try {
    await api.toggleUserActive(faculty.id)
    faculty.active = !faculty.active
  } catch (error) {
    console.error('Error toggling faculty status:', error)
    showNotification('Failed to update faculty status', 'error')
  }
}

const confirmDelete = (faculty) => {
  facultyToDelete.value = faculty
  showDeleteModal.value = true
}

const resetForm = () => {
  editingFaculty.value = null
  facultyForm.value = {
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    employeeId: '',
    department: '',
    rank: '',
    office: '',
    active: true
  }
}

// Utility functions
const getInitials = (user) => {
  return `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase() || 'U'
}

const getFacultyStatusClass = (status) => {
  const classes = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'ON_LEAVE': 'bg-yellow-100 text-yellow-800',
    'INACTIVE': 'bg-gray-100 text-gray-800'
  }
  return classes[status] || 'bg-green-100 text-green-800'
}

onMounted(() => {
  loadFaculty()
})
</script>