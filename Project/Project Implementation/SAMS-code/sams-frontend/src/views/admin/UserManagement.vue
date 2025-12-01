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
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">User Management</h1>
        <p class="mt-2 text-gray-600">Create and manage user accounts</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors"
      >
        + Create User
      </button>
    </div>

    <!-- Users Table -->
    <div class="bg-white rounded-lg shadow">
      <DataTable
        :columns="columns"
        :data="users"
        :loading="loading"
        searchable
        search-placeholder="Search users by name, email, or role..."
        :search-keys="['username', 'email', 'role']"
      >
        <template #cell-role="{ value }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="getRoleClass(value)"
          >
            {{ value }}
          </span>
        </template>

        <template #cell-active="{ value }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="value ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
          >
            {{ value ? 'Active' : 'Inactive' }}
          </span>
        </template>

        <template #row-actions="{ row }">
          <div class="flex items-center space-x-2">
            <button
              @click="editUser(row)"
              class="text-blue-600 hover:text-blue-900 text-sm font-medium"
            >
              Edit
            </button>
            <button
              @click="toggleUserStatus(row)"
              class="text-yellow-600 hover:text-yellow-900 text-sm font-medium"
            >
              {{ row.active ? 'Deactivate' : 'Activate' }}
            </button>
            <button
              v-if="canDeleteUser(row)"
              @click="confirmDelete(row)"
              class="text-red-600 hover:text-red-900 text-sm font-medium"
            >
              Delete
            </button>
          </div>
        </template>
      </DataTable>
    </div>

    <!-- Create/Edit User Modal -->
    <Modal
      v-model="showCreateModal"
      :title="editingUser ? 'Edit User' : 'Create New User'"
      :confirm-text="editingUser ? 'Update User' : 'Create User'"
      :confirm-disabled="!isFormValid || submitting"
      @confirm="saveUser"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Username *</label>
          <input
            v-model="userForm.username"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter username"
            :disabled="editingUser"
            required
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Email *</label>
          <input
            v-model="userForm.email"
            type="email"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="user@example.com"
            required
          />
        </div>

        <div v-if="!editingUser">
          <label class="block text-sm font-medium text-gray-700 mb-2">Password *</label>
          <input
            v-model="userForm.password"
            type="password"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter password"
            required
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Role *</label>
          <select
            v-model="userForm.role"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            required
          >
            <option value="">Select a role</option>
            <option value="STUDENT">Student</option>
            <option value="FACULTY">Faculty</option>
            <option v-if="authStore.isSuperAdmin" value="ADMIN">Admin</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">First Name</label>
          <input
            v-model="userForm.firstName"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter first name"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Last Name</label>
          <input
            v-model="userForm.lastName"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter last name"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Gender</label>
          <select
            v-model="userForm.gender"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            aria-label="Select gender"
          >
            <option value="">Select gender</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHER">Other</option>
            <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
          </select>
        </div>

        <div>
          <label class="flex items-center">
            <input
              v-model="userForm.active"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
            />
            <span class="ml-2 text-sm text-gray-700">Active</span>
          </label>
        </div>
      </div>
    </Modal>

    <!-- Delete Confirmation Modal -->
    <Modal
      v-model="showDeleteModal"
      title="Confirm Delete"
      confirm-text="Delete User"
      cancel-text="Cancel"
      @confirm="deleteUser"
    >
      <div>
        <p class="text-sm text-gray-600 mb-4">
          Are you sure you want to delete this user?
        </p>
        <div v-if="userToDelete" class="bg-red-50 border border-red-200 rounded-lg p-4">
          <p class="font-semibold text-gray-900">{{ userToDelete.username }}</p>
          <p class="text-sm text-gray-600">{{ userToDelete.email }} • {{ userToDelete.role }}</p>
        </div>
        <p class="text-sm text-red-600 mt-4 font-medium">
          This action cannot be undone!
        </p>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'

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

const users = ref([])
const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showDeleteModal = ref(false)
const editingUser = ref(null)
const userToDelete = ref(null)

const userForm = ref({
  username: '',
  email: '',
  password: '',
  role: '',
  firstName: '',
  lastName: '',
  gender: '',
  active: true
})

const columns = [
  { key: 'username', label: 'Username' },
  { key: 'email', label: 'Email' },
  { key: 'role', label: 'Role' },
  { key: 'firstName', label: 'First Name' },
  { key: 'lastName', label: 'Last Name' },
  { key: 'active', label: 'Status' }
]

const isFormValid = computed(() => {
  if (editingUser.value) {
    return userForm.value.username && userForm.value.email && userForm.value.role
  }
  return userForm.value.username && userForm.value.email && userForm.value.password && userForm.value.role
})

const loadUsers = async () => {
  try {
    loading.value = true
    const response = await api.getAllUsers()
    users.value = response.data
  } catch (error) {
    console.error('Error loading users:', error)
    showNotification('Failed to load users', 'error')
  } finally {
    loading.value = false
  }
}

const saveUser = async () => {
  try {
    submitting.value = true

    if (editingUser.value) {
      await api.updateUser(editingUser.value.id, userForm.value)
      showNotification('User updated successfully', 'success')
    } else {
      await api.createUser(userForm.value)
      showNotification('User created successfully', 'success')
    }

    showCreateModal.value = false
    resetForm()
    await loadUsers()
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.response?.data || 'Operation failed'
    showNotification(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}

const editUser = (user) => {
  editingUser.value = user
  userForm.value = {
    username: user.username,
    email: user.email,
    role: user.role,
    firstName: user.firstName || '',
    lastName: user.lastName || '',
    gender: user.gender || '',
    active: user.active
  }
  showCreateModal.value = true
}

const toggleUserStatus = async (user) => {
  try {
    await api.toggleUserActive(user.id)
    user.active = !user.active
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.response?.data || 'Failed to toggle user status'
    showNotification(errorMessage, 'error')
  }
}

const confirmDelete = (user) => {
  userToDelete.value = user
  showDeleteModal.value = true
}

const deleteUser = async () => {
  try {
    await api.deleteUser(userToDelete.value.id)
    showDeleteModal.value = false
    showNotification('User deleted successfully', 'success')
    await loadUsers()
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.response?.data || 'Failed to delete user'
    showNotification(errorMessage, 'error')
  }
}

const canDeleteUser = (user) => {
  // SUPER_ADMIN cannot be deleted
  if (user.role === 'SUPER_ADMIN') return false

  // Only SUPER_ADMIN can delete ADMIN users
  if (user.role === 'ADMIN') {
    return authStore.isSuperAdmin
  }

  return true
}

const getRoleClass = (role) => {
  const classes = {
    'SUPER_ADMIN': 'bg-purple-100 text-purple-800',
    'ADMIN': 'bg-blue-100 text-blue-800',
    'FACULTY': 'bg-green-100 text-green-800',
    'STUDENT': 'bg-gray-100 text-gray-800'
  }
  return classes[role] || 'bg-gray-100 text-gray-800'
}

const resetForm = () => {
  editingUser.value = null
  userForm.value = {
    username: '',
    email: '',
    password: '',
    role: '',
    firstName: '',
    lastName: '',
    gender: '',
    active: true
  }
}

onMounted(() => {
  loadUsers()
})
</script>
