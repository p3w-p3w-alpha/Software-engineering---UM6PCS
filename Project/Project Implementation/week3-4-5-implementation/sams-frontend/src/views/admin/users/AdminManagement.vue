<template>
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

  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Admin Management</h1>
        <p class="mt-2 text-gray-600">Manage system administrators and their permissions</p>
      </div>
      <button
        v-if="authStore.isSuperAdmin"
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors"
      >
        <i class="pi pi-plus mr-2"></i>Add Admin
      </button>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Total Admins</p>
            <p class="text-2xl font-bold text-gray-900">{{ stats.total }}</p>
          </div>
          <i class="pi pi-shield text-3xl text-purple-500"></i>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Super Admins</p>
            <p class="text-2xl font-bold text-red-600">{{ stats.superAdmins }}</p>
          </div>
          <i class="pi pi-crown text-3xl text-red-500"></i>
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
            <p class="text-sm text-gray-600">Last Login</p>
            <p class="text-sm font-bold text-gray-900">{{ stats.lastLogin }}</p>
          </div>
          <i class="pi pi-clock text-3xl text-blue-500"></i>
        </div>
      </div>
    </div>

    <!-- Security Alert -->
    <div v-if="authStore.isSuperAdmin" class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
      <div class="flex items-start">
        <i class="pi pi-exclamation-triangle text-yellow-600 text-xl mr-3 mt-0.5"></i>
        <div>
          <h4 class="font-medium text-yellow-900">Security Notice</h4>
          <p class="text-sm text-yellow-800 mt-1">
            Admin accounts have elevated privileges. Ensure strong passwords and enable two-factor authentication.
            Review admin activities regularly in the audit logs.
          </p>
        </div>
      </div>
    </div>

    <!-- Admin Table -->
    <div class="bg-white rounded-lg shadow">
      <DataTable
        :columns="columns"
        :data="filteredAdmins"
        :loading="loading"
        searchable
        search-placeholder="Search administrators by name or email..."
        :search-keys="['username', 'email', 'firstName', 'lastName']"
      >
        <template #cell-name="{ row }">
          <div class="flex items-center gap-3">
            <div class="relative">
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-purple-500 to-red-500 flex items-center justify-center text-white font-semibold">
                {{ getInitials(row) }}
              </div>
              <div v-if="row.role === 'SUPER_ADMIN'" class="absolute -top-1 -right-1">
                <i class="pi pi-crown text-yellow-500 text-xs"></i>
              </div>
            </div>
            <div>
              <p class="font-medium text-gray-900">{{ row.firstName }} {{ row.lastName }}</p>
              <p class="text-xs text-gray-500">{{ row.email }}</p>
            </div>
          </div>
        </template>

        <template #cell-role="{ value }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="getRoleClass(value)"
          >
            {{ value === 'SUPER_ADMIN' ? 'SUPER ADMIN' : value }}
          </span>
        </template>

        <template #cell-permissions="{ row }">
          <div class="flex flex-wrap gap-1">
            <span
              v-for="perm in getTopPermissions(row.permissions)"
              :key="perm"
              class="px-2 py-0.5 text-xs bg-gray-100 text-gray-700 rounded"
            >
              {{ perm }}
            </span>
            <span
              v-if="getPermissionsCount(row.permissions) > 3"
              class="px-2 py-0.5 text-xs bg-blue-100 text-blue-700 rounded font-medium"
            >
              +{{ getPermissionsCount(row.permissions) - 3 }} more
            </span>
          </div>
        </template>

        <template #cell-lastLogin="{ row }">
          <span class="text-sm text-gray-600">
            {{ formatLastLogin(row.lastLogin) }}
          </span>
        </template>

        <template #cell-status="{ row }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="row.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
          >
            {{ row.active ? 'Active' : 'Inactive' }}
          </span>
        </template>

        <template #row-actions="{ row }">
          <div class="flex items-center gap-2">
            <button
              @click="viewAdmin(row)"
              class="text-blue-600 hover:text-blue-900 text-sm font-medium"
              title="View Details"
            >
              <i class="pi pi-eye"></i>
            </button>
            <button
              v-if="canEditAdmin(row)"
              @click="editAdmin(row)"
              class="text-green-600 hover:text-green-900 text-sm font-medium"
              title="Edit"
            >
              <i class="pi pi-pencil"></i>
            </button>
            <button
              v-if="canEditAdmin(row)"
              @click="managePermissions(row)"
              class="text-purple-600 hover:text-purple-900 text-sm font-medium"
              title="Manage Permissions"
            >
              <i class="pi pi-key"></i>
            </button>
            <button
              @click="viewActivityLog(row)"
              class="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
              title="Activity Log"
            >
              <i class="pi pi-history"></i>
            </button>
            <button
              v-if="canToggleAdmin(row)"
              @click="toggleStatus(row)"
              :class="row.active ? 'text-yellow-600 hover:text-yellow-900' : 'text-green-600 hover:text-green-900'"
              class="text-sm font-medium"
              :title="row.active ? 'Deactivate' : 'Activate'"
            >
              <i :class="row.active ? 'pi pi-lock' : 'pi pi-unlock'"></i>
            </button>
            <button
              v-if="canDeleteAdmin(row)"
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

    <!-- Create/Edit Admin Modal -->
    <Modal
      v-model="showCreateModal"
      :title="editingAdmin ? 'Edit Administrator' : 'Create New Administrator'"
      :confirm-text="editingAdmin ? 'Update' : 'Create'"
      :confirm-disabled="!isFormValid || submitting"
      @confirm="saveAdmin"
    >
      <div class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">First Name *</label>
            <input
              v-model="adminForm.firstName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Last Name *</label>
            <input
              v-model="adminForm.lastName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Username *</label>
            <input
              v-model="adminForm.username"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              :disabled="editingAdmin"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Email *</label>
            <input
              v-model="adminForm.email"
              type="email"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
        </div>

        <div v-if="!editingAdmin">
          <label class="block text-sm font-medium text-gray-700 mb-2">Password *</label>
          <input
            v-model="adminForm.password"
            type="password"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Minimum 8 characters with mixed case and numbers"
            required
          />
        </div>

        <div v-if="authStore.isSuperAdmin">
          <label class="block text-sm font-medium text-gray-700 mb-2">Admin Type *</label>
          <select
            v-model="adminForm.role"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            required
          >
            <option value="ADMIN">Standard Admin</option>
            <option value="SUPER_ADMIN">Super Admin</option>
          </select>
        </div>

        <div>
          <label class="flex items-center">
            <input
              v-model="adminForm.active"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
            />
            <span class="ml-2 text-sm text-gray-700">Active Account</span>
          </label>
        </div>

        <div v-if="adminForm.role === 'SUPER_ADMIN'" class="bg-red-50 border border-red-200 rounded-lg p-4">
          <p class="text-sm text-red-800">
            <i class="pi pi-exclamation-triangle mr-2"></i>
            Super Admin accounts have full system access. Grant this role carefully.
          </p>
        </div>
      </div>
    </Modal>

    <!-- Permissions Modal -->
    <Modal
      v-model="showPermissionsModal"
      title="Manage Permissions"
      confirm-text="Save Permissions"
      @confirm="savePermissions"
    >
      <div v-if="selectedAdmin" class="space-y-4">
        <div class="mb-4">
          <p class="text-sm text-gray-600">Managing permissions for:</p>
          <p class="font-medium">{{ selectedAdmin.firstName }} {{ selectedAdmin.lastName }}</p>
        </div>

        <div class="space-y-3">
          <div v-for="category in permissionCategories" :key="category.name" class="border rounded-lg p-3">
            <h4 class="font-medium mb-2">{{ category.label }}</h4>
            <div class="grid grid-cols-2 gap-2">
              <label v-for="perm in category.permissions" :key="perm.value" class="flex items-center">
                <input
                  v-model="selectedPermissions"
                  :value="perm.value"
                  type="checkbox"
                  class="rounded border-gray-300 text-blue-600"
                />
                <span class="ml-2 text-sm">{{ perm.label }}</span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'
import api from '../../../services/api'
import DataTable from '../../../components/DataTable.vue'
import Modal from '../../../components/Modal.vue'

const router = useRouter()
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

// Reactive data
const admins = ref([])
const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showDeleteModal = ref(false)
const showPermissionsModal = ref(false)
const editingAdmin = ref(null)
const selectedAdmin = ref(null)
const adminToDelete = ref(null)
const selectedPermissions = ref([])

// Stats
const stats = ref({
  total: 0,
  superAdmins: 0,
  active: 0,
  lastLogin: 'N/A'
})

// Form data
const adminForm = ref({
  username: '',
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  role: 'ADMIN',
  active: true
})

// Table columns
const columns = [
  { key: 'name', label: 'Name' },
  { key: 'role', label: 'Role' },
  { key: 'permissions', label: 'Permissions' },
  { key: 'lastLogin', label: 'Last Login' },
  { key: 'status', label: 'Status' }
]

// Permission categories
const permissionCategories = [
  {
    name: 'users',
    label: 'User Management',
    permissions: [
      { value: 'USER_CREATE', label: 'Create Users' },
      { value: 'USER_READ', label: 'View Users' },
      { value: 'USER_UPDATE', label: 'Update Users' },
      { value: 'USER_DELETE', label: 'Delete Users' }
    ]
  },
  {
    name: 'courses',
    label: 'Course Management',
    permissions: [
      { value: 'COURSE_CREATE', label: 'Create Courses' },
      { value: 'COURSE_UPDATE', label: 'Update Courses' },
      { value: 'COURSE_DELETE', label: 'Delete Courses' },
      { value: 'ENROLLMENT_MANAGE', label: 'Manage Enrollments' }
    ]
  },
  {
    name: 'academic',
    label: 'Academic Management',
    permissions: [
      { value: 'GRADE_MANAGE', label: 'Manage Grades' },
      { value: 'ASSIGNMENT_CREATE', label: 'Create Assignments' },
      { value: 'ASSIGNMENT_MANAGE', label: 'Manage Assignments' },
      { value: 'ATTENDANCE_MANAGE', label: 'Manage Attendance' }
    ]
  },
  {
    name: 'system',
    label: 'System Administration',
    permissions: [
      { value: 'SYSTEM_SETTINGS', label: 'System Settings' },
      { value: 'REPORT_VIEW', label: 'View Reports' },
      { value: 'NOTIFICATION_SEND', label: 'Send Notifications' },
      { value: 'PAYMENT_APPROVE', label: 'Approve Payments' }
    ]
  }
]

// Computed
const filteredAdmins = computed(() => {
  return admins.value.filter(a => a.role === 'ADMIN' || a.role === 'SUPER_ADMIN')
})

const isFormValid = computed(() => {
  if (editingAdmin.value) {
    return adminForm.value.firstName && adminForm.value.lastName && adminForm.value.email
  }
  return adminForm.value.username && adminForm.value.email && adminForm.value.password &&
         adminForm.value.firstName && adminForm.value.lastName
})

// Methods
const loadAdmins = async () => {
  try {
    loading.value = true
    const response = await api.getAllUsers()
    admins.value = response.data

    // Calculate stats
    const adminUsers = response.data.filter(u => u.role === 'ADMIN' || u.role === 'SUPER_ADMIN')
    stats.value = {
      total: adminUsers.length,
      superAdmins: adminUsers.filter(a => a.role === 'SUPER_ADMIN').length,
      active: adminUsers.filter(a => a.active).length,
      lastLogin: '2 hours ago' // Mock data - replace with real last login
    }
  } catch (error) {
    console.error('Error loading admins:', error)
  } finally {
    loading.value = false
  }
}

const saveAdmin = async () => {
  try {
    submitting.value = true
    const userData = { ...adminForm.value }

    if (editingAdmin.value) {
      await api.updateUser(editingAdmin.value.id, userData)
    } else {
      await api.createUser(userData)
    }

    showCreateModal.value = false
    resetForm()
    await loadAdmins()
  } catch (error) {
    console.error('Error saving admin:', error)
    showNotification('Failed to save administrator', 'error')
  } finally {
    submitting.value = false
  }
}

const viewAdmin = (admin) => {
  router.push(`/admin/admins/${admin.id}/details`)
}

const editAdmin = (admin) => {
  editingAdmin.value = admin
  adminForm.value = {
    username: admin.username,
    email: admin.email,
    firstName: admin.firstName || '',
    lastName: admin.lastName || '',
    role: admin.role,
    active: admin.active
  }
  showCreateModal.value = true
}

const managePermissions = (admin) => {
  selectedAdmin.value = admin
  selectedPermissions.value = parsePermissions(admin.permissions)
  showPermissionsModal.value = true
}

const savePermissions = async () => {
  try {
    // API call to save permissions would go here
    showPermissionsModal.value = false
    await loadAdmins()
  } catch (error) {
    console.error('Error saving permissions:', error)
    showNotification('Failed to save permissions', 'error')
  }
}

const viewActivityLog = (admin) => {
  router.push(`/admin/audit-log?userId=${admin.id}`)
}

const toggleStatus = async (admin) => {
  try {
    await api.toggleUserActive(admin.id)
    admin.active = !admin.active
  } catch (error) {
    console.error('Error toggling admin status:', error)
    showNotification('Failed to update admin status', 'error')
  }
}

const confirmDelete = (admin) => {
  adminToDelete.value = admin
  showDeleteModal.value = true
}

const resetForm = () => {
  editingAdmin.value = null
  adminForm.value = {
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    role: 'ADMIN',
    active: true
  }
}

// Utility functions
const getInitials = (user) => {
  return `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase() || 'A'
}

const getRoleClass = (role) => {
  return role === 'SUPER_ADMIN' ? 'bg-red-100 text-red-800' : 'bg-purple-100 text-purple-800'
}

const parsePermissions = (permissionsString) => {
  try {
    return JSON.parse(permissionsString || '[]')
  } catch {
    return []
  }
}

const getTopPermissions = (permissionsString) => {
  const perms = parsePermissions(permissionsString)
  return perms.slice(0, 3).map(p => p.replace(/_/g, ' '))
}

const getPermissionsCount = (permissionsString) => {
  return parsePermissions(permissionsString).length
}

const formatLastLogin = (date) => {
  if (!date) return 'Never'
  // Format date logic here
  return '2 hours ago'
}

const canEditAdmin = (admin) => {
  if (!authStore.isSuperAdmin) return false
  if (admin.role === 'SUPER_ADMIN' && admin.id === authStore.userId) return true
  if (admin.role === 'SUPER_ADMIN') return authStore.isSuperAdmin
  return true
}

const canToggleAdmin = (admin) => {
  if (!authStore.isSuperAdmin) return false
  return admin.id !== authStore.userId
}

const canDeleteAdmin = (admin) => {
  if (admin.role === 'SUPER_ADMIN') return false
  if (admin.id === authStore.userId) return false
  return authStore.isSuperAdmin
}

onMounted(() => {
  loadAdmins()
})
</script>