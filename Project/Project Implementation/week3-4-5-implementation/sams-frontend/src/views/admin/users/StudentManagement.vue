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
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Student Management</h1>
        <p class="mt-2 text-gray-600">Manage student accounts, enrollments, and academic records</p>
      </div>
      <div class="flex gap-3">
        <button
          @click="showBulkImportModal = true"
          class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 font-medium transition-colors"
        >
          <i class="pi pi-upload mr-2"></i>Bulk Import
        </button>
        <button
          @click="showCreateModal = true"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors"
        >
          <i class="pi pi-plus mr-2"></i>Add Student
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Total Students</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.total }}</p>
          </div>
          <i class="pi pi-users text-3xl text-blue-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Active</p>
            <p class="text-2xl font-bold text-green-600">{{ stats.active }}</p>
          </div>
          <i class="pi pi-check-circle text-3xl text-green-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">On Waiting List</p>
            <p class="text-2xl font-bold text-yellow-600">{{ stats.waitingList }}</p>
          </div>
          <i class="pi pi-clock text-3xl text-yellow-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Graduated</p>
            <p class="text-2xl font-bold text-purple-600">{{ stats.graduated }}</p>
          </div>
          <i class="pi pi-graduation-cap text-3xl text-purple-500"></i>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
          <select v-model="filters.status" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All Status</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="SUSPENDED">Suspended</option>
            <option value="GRADUATED">Graduated</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Program</label>
          <select v-model="filters.program" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All Programs</option>
            <option value="CS">Computer Science</option>
            <option value="ENG">Engineering</option>
            <option value="BUS">Business</option>
            <option value="MED">Medicine</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Year</label>
          <select v-model="filters.year" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All Years</option>
            <option value="1">First Year</option>
            <option value="2">Second Year</option>
            <option value="3">Third Year</option>
            <option value="4">Fourth Year</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Payment Status</label>
          <select v-model="filters.paymentStatus" class="w-full px-3 py-2 border border-gray-300 rounded-md">
            <option value="">All</option>
            <option value="PAID">Fees Paid</option>
            <option value="PENDING">Payment Pending</option>
            <option value="OVERDUE">Overdue</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Students Table -->
    <div class="bg-white rounded-lg shadow">
      <DataTable
        :columns="columns"
        :data="filteredStudents"
        :loading="loading"
        searchable
        search-placeholder="Search students by name, email, or student ID..."
        :search-keys="['username', 'email', 'studentId', 'firstName', 'lastName']"
      >
        <template #cell-studentId="{ row }">
          <span class="font-mono text-sm">{{ row.studentId || 'N/A' }}</span>
        </template>

        <template #cell-name="{ row }">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white font-semibold">
              {{ getInitials(row) }}
            </div>
            <div>
              <p class="font-medium text-gray-900">{{ row.firstName }} {{ row.lastName }}</p>
              <p class="text-xs text-gray-500">{{ row.email }}</p>
            </div>
          </div>
        </template>

        <template #cell-program="{ row }">
          <span class="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
            {{ row.program || 'Undeclared' }}
          </span>
        </template>

        <template #cell-year="{ row }">
          <span class="text-sm">Year {{ row.year || 1 }}</span>
        </template>

        <template #cell-gpa="{ row }">
          <span :class="getGPAClass(row.gpa)" class="font-semibold">
            {{ row.gpa ? row.gpa.toFixed(2) : 'N/A' }}
          </span>
        </template>

        <template #cell-status="{ value }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="getStatusClass(value)"
          >
            {{ value }}
          </span>
        </template>

        <template #cell-paymentStatus="{ row }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="getPaymentStatusClass(row.paymentStatus)"
          >
            {{ row.paymentStatus || 'PENDING' }}
          </span>
        </template>

        <template #row-actions="{ row }">
          <div class="flex items-center gap-2">
            <button
              @click="viewStudent(row)"
              class="text-blue-600 hover:text-blue-900 text-sm font-medium"
              title="View Details"
            >
              <i class="pi pi-eye"></i>
            </button>
            <button
              @click="editStudent(row)"
              class="text-green-600 hover:text-green-900 text-sm font-medium"
              title="Edit"
            >
              <i class="pi pi-pencil"></i>
            </button>
            <button
              @click="viewTranscript(row)"
              class="text-purple-600 hover:text-purple-900 text-sm font-medium"
              title="View Transcript"
            >
              <i class="pi pi-file-pdf"></i>
            </button>
            <button
              @click="manageEnrollments(row)"
              class="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
              title="Manage Enrollments"
            >
              <i class="pi pi-book"></i>
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

    <!-- Create/Edit Student Modal -->
    <Modal
      v-model="showCreateModal"
      :title="editingStudent ? 'Edit Student' : 'Create New Student'"
      :confirm-text="editingStudent ? 'Update' : 'Create'"
      :confirm-disabled="!isFormValid || submitting"
      @confirm="saveStudent"
    >
      <div class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">First Name *</label>
            <input
              v-model="studentForm.firstName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Last Name *</label>
            <input
              v-model="studentForm.lastName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Student ID</label>
            <input
              v-model="studentForm.studentId"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Auto-generated if empty"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Email *</label>
            <input
              v-model="studentForm.email"
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
              v-model="studentForm.username"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              :disabled="editingStudent"
              required
            />
          </div>
          <div v-if="!editingStudent">
            <label class="block text-sm font-medium text-gray-700 mb-2">Password *</label>
            <input
              v-model="studentForm.password"
              type="password"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Program</label>
            <select
              v-model="studentForm.program"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select Program</option>
              <option value="CS">Computer Science</option>
              <option value="ENG">Engineering</option>
              <option value="BUS">Business</option>
              <option value="MED">Medicine</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Academic Year</label>
            <select
              v-model="studentForm.year"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="1">First Year</option>
              <option value="2">Second Year</option>
              <option value="3">Third Year</option>
              <option value="4">Fourth Year</option>
            </select>
          </div>
        </div>

        <div>
          <label class="flex items-center">
            <input
              v-model="studentForm.active"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
            />
            <span class="ml-2 text-sm text-gray-700">Active Account</span>
          </label>
        </div>
      </div>
    </Modal>

    <!-- View Student Details Modal -->
    <Modal
      v-model="showDetailsModal"
      title="Student Details"
      :confirm-text="null"
      cancel-text="Close"
    >
      <div v-if="selectedStudent" class="space-y-4">
        <div class="flex items-center gap-4 pb-4 border-b">
          <div class="w-16 h-16 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white text-2xl font-bold">
            {{ getInitials(selectedStudent) }}
          </div>
          <div>
            <h3 class="text-lg font-semibold">{{ selectedStudent.firstName }} {{ selectedStudent.lastName }}</h3>
            <p class="text-gray-600">{{ selectedStudent.email }}</p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <p class="text-sm text-gray-600">Student ID</p>
            <p class="font-medium">{{ selectedStudent.studentId || 'N/A' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Username</p>
            <p class="font-medium">{{ selectedStudent.username }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Program</p>
            <p class="font-medium">{{ selectedStudent.program || 'Undeclared' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Year</p>
            <p class="font-medium">Year {{ selectedStudent.year || 1 }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">GPA</p>
            <p class="font-medium">{{ selectedStudent.gpa?.toFixed(2) || 'N/A' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Status</p>
            <p class="font-medium">
              <span :class="getStatusClass(selectedStudent.active ? 'ACTIVE' : 'INACTIVE')" class="px-2 py-1 text-xs rounded-full">
                {{ selectedStudent.active ? 'ACTIVE' : 'INACTIVE' }}
              </span>
            </p>
          </div>
        </div>

        <div>
          <h4 class="font-medium mb-2">Current Enrollments</h4>
          <div class="space-y-2">
            <div v-for="enrollment in selectedStudent.enrollments" :key="enrollment.id" class="flex justify-between items-center p-2 bg-gray-50 rounded">
              <span>{{ enrollment.courseName }}</span>
              <span class="text-sm text-gray-600">{{ enrollment.grade || 'In Progress' }}</span>
            </div>
          </div>
        </div>
      </div>
    </Modal>

    <!-- Delete Confirmation Modal -->
    <Modal
      v-model="showDeleteModal"
      title="Confirm Delete"
      confirm-text="Delete Student"
      cancel-text="Cancel"
      @confirm="deleteStudent"
    >
      <div>
        <p class="text-sm text-gray-600 mb-4">
          Are you sure you want to delete this student? This action cannot be undone.
        </p>
        <div v-if="studentToDelete" class="bg-red-50 border border-red-200 rounded-lg p-4">
          <p class="font-semibold text-gray-900">{{ studentToDelete.firstName }} {{ studentToDelete.lastName }}</p>
          <p class="text-sm text-gray-600">{{ studentToDelete.email }}</p>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../../../services/api'
import DataTable from '../../../components/DataTable.vue'
import Modal from '../../../components/Modal.vue'

const router = useRouter()

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

// Reactive data
const students = ref([])
const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const showDeleteModal = ref(false)
const showBulkImportModal = ref(false)
const editingStudent = ref(null)
const selectedStudent = ref(null)
const studentToDelete = ref(null)

// Filters
const filters = ref({
  status: '',
  program: '',
  year: '',
  paymentStatus: ''
})

// Stats
const stats = ref({
  total: 0,
  active: 0,
  waitingList: 0,
  graduated: 0
})

// Form data
const studentForm = ref({
  username: '',
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  studentId: '',
  program: '',
  year: 1,
  active: true
})

// Table columns
const columns = [
  { key: 'studentId', label: 'Student ID' },
  { key: 'name', label: 'Name' },
  { key: 'program', label: 'Program' },
  { key: 'year', label: 'Year' },
  { key: 'gpa', label: 'GPA' },
  { key: 'status', label: 'Status' },
  { key: 'paymentStatus', label: 'Payment' }
]

// Computed
const filteredStudents = computed(() => {
  let result = students.value.filter(s => s.role === 'STUDENT')

  if (filters.value.status) {
    result = result.filter(s => {
      if (filters.value.status === 'ACTIVE') return s.active
      if (filters.value.status === 'INACTIVE') return !s.active
      // Add more status filters as needed
      return true
    })
  }

  if (filters.value.program) {
    result = result.filter(s => s.program === filters.value.program)
  }

  if (filters.value.year) {
    result = result.filter(s => s.year === parseInt(filters.value.year))
  }

  if (filters.value.paymentStatus) {
    result = result.filter(s => s.paymentStatus === filters.value.paymentStatus)
  }

  return result
})

const isFormValid = computed(() => {
  if (editingStudent.value) {
    return studentForm.value.firstName && studentForm.value.lastName && studentForm.value.email
  }
  return studentForm.value.username && studentForm.value.email && studentForm.value.password &&
         studentForm.value.firstName && studentForm.value.lastName
})

// Methods
const loadStudents = async () => {
  try {
    loading.value = true
    const response = await api.getAllUsers()
    students.value = response.data

    // Calculate stats
    const studentUsers = response.data.filter(u => u.role === 'STUDENT')
    stats.value = {
      total: studentUsers.length,
      active: studentUsers.filter(s => s.active).length,
      waitingList: studentUsers.filter(s => s.waitingList).length || 0,
      graduated: studentUsers.filter(s => s.graduated).length || 0
    }
  } catch (error) {
    console.error('Error loading students:', error)
  } finally {
    loading.value = false
  }
}

const saveStudent = async () => {
  try {
    submitting.value = true
    const userData = {
      ...studentForm.value,
      role: 'STUDENT'
    }

    if (editingStudent.value) {
      await api.updateUser(editingStudent.value.id, userData)
    } else {
      await api.createUser(userData)
    }

    showCreateModal.value = false
    resetForm()
    await loadStudents()
  } catch (error) {
    console.error('Error saving student:', error)
    showNotification('Failed to save student', 'error')
  } finally {
    submitting.value = false
  }
}

const viewStudent = (student) => {
  selectedStudent.value = student
  showDetailsModal.value = true
}

const editStudent = (student) => {
  editingStudent.value = student
  studentForm.value = {
    username: student.username,
    email: student.email,
    firstName: student.firstName || '',
    lastName: student.lastName || '',
    studentId: student.studentId || '',
    program: student.program || '',
    year: student.year || 1,
    active: student.active
  }
  showCreateModal.value = true
}

const viewTranscript = (student) => {
  router.push(`/admin/students/${student.id}/transcript`)
}

const manageEnrollments = (student) => {
  router.push(`/admin/students/${student.id}/enrollments`)
}

const toggleStatus = async (student) => {
  try {
    await api.toggleUserActive(student.id)
    student.active = !student.active
  } catch (error) {
    console.error('Error toggling student status:', error)
    showNotification('Failed to update student status', 'error')
  }
}

const confirmDelete = (student) => {
  studentToDelete.value = student
  showDeleteModal.value = true
}

const deleteStudent = async () => {
  try {
    await api.deleteUser(studentToDelete.value.id)
    showDeleteModal.value = false
    await loadStudents()
  } catch (error) {
    console.error('Error deleting student:', error)
    showNotification('Failed to delete student', 'error')
  }
}

const resetForm = () => {
  editingStudent.value = null
  studentForm.value = {
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    studentId: '',
    program: '',
    year: 1,
    active: true
  }
}

// Utility functions
const getInitials = (user) => {
  return `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase() || 'U'
}

const getStatusClass = (status) => {
  const classes = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'INACTIVE': 'bg-gray-100 text-gray-800',
    'SUSPENDED': 'bg-red-100 text-red-800',
    'GRADUATED': 'bg-purple-100 text-purple-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getPaymentStatusClass = (status) => {
  const classes = {
    'PAID': 'bg-green-100 text-green-800',
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'OVERDUE': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getGPAClass = (gpa) => {
  if (!gpa) return 'text-gray-500'
  if (gpa >= 3.5) return 'text-green-600'
  if (gpa >= 3.0) return 'text-blue-600'
  if (gpa >= 2.5) return 'text-yellow-600'
  return 'text-red-600'
}

onMounted(() => {
  loadStudents()
})
</script>