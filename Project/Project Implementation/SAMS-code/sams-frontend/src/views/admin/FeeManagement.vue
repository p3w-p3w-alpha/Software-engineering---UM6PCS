<!--
  Fee Management view - admin dashboard for creating and managing fee structures
  complex form validation here to make sure all teh financial data is correct
-->
<template>
  <div class="max-w-7xl mx-auto">
    <!-- Toast Notification - feedback for successful/failed operations -->
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

    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Fee Management</h1>
        <p class="mt-2 text-gray-600">Create and manage fee structures</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors"
      >
        + Create Fee Structure
      </button>
    </div>

    <!-- Filter Options -->
    <div class="mb-6 flex gap-4">
      <select
        v-model="filterCategory"
        @change="filterFees"
        class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
      >
        <option value="">All Categories</option>
        <option value="ACADEMIC">Academic</option>
        <option value="NON_ACADEMIC">Non-Academic</option>
        <option value="HOSTEL">Hostel</option>
        <option value="TRANSPORTATION">Transportation</option>
        <option value="MISCELLANEOUS">Miscellaneous</option>
      </select>

      <select
        v-model="filterStatus"
        @change="filterFees"
        class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
      >
        <option value="">All Status</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
      </select>
    </div>

    <!-- Fee Structures Table -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div v-if="loading" class="p-8 text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
        <p class="mt-4 text-gray-600">Loading fee structures...</p>
      </div>

      <table v-else class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fee Name</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="filteredFees.length === 0">
            <td colspan="7" class="px-6 py-8 text-center text-gray-500">
              No fee structures found
            </td>
          </tr>
          <tr v-for="fee in filteredFees" :key="fee.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="font-medium text-gray-900">{{ fee.feeName }}</div>
              <div class="text-sm text-gray-500">{{ fee.description }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="font-mono text-sm text-gray-700">{{ fee.feeCode }}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                class="px-2 py-1 text-xs font-semibold rounded-full"
                :class="getCategoryClass(fee.category)"
              >
                {{ fee.category }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="text-lg font-semibold text-gray-900">${{ fee.defaultAmount.toFixed(2) }}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">
                {{ fee.mandatory ? 'Mandatory' : 'Optional' }}
              </div>
              <div class="text-xs text-gray-500">
                {{ fee.recurring ? 'Recurring' : 'One-time' }}
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                class="px-2 py-1 text-xs font-semibold rounded-full"
                :class="fee.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ fee.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="editFee(fee)"
                class="text-blue-600 hover:text-blue-900 mr-4"
              >
                Edit
              </button>
              <button
                @click="toggleFeeStatus(fee)"
                class="text-yellow-600 hover:text-yellow-900 mr-4"
              >
                {{ fee.active ? 'Deactivate' : 'Activate' }}
              </button>
              <button
                @click="confirmDelete(fee)"
                class="text-red-600 hover:text-red-900"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Fee Modal -->
    <div
      v-if="showCreateModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showCreateModal = false"
    >
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">
          {{ editingFee ? 'Edit Fee Structure' : 'Create Fee Structure' }}
        </h2>

        <form @submit.prevent="saveFee" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Fee Name *</label>
              <input
                v-model="feeForm.feeName"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., Tuition Fee"
                required
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Fee Code *</label>
              <input
                v-model="feeForm.feeCode"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500 font-mono uppercase"
                placeholder="e.g., TUITION"
                required
                :disabled="editingFee"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Category *</label>
              <select
                v-model="feeForm.category"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                required
              >
                <option value="">Select Category</option>
                <option value="ACADEMIC">Academic</option>
                <option value="NON_ACADEMIC">Non-Academic</option>
                <option value="HOSTEL">Hostel</option>
                <option value="TRANSPORTATION">Transportation</option>
                <option value="MISCELLANEOUS">Miscellaneous</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Default Amount *</label>
              <input
                v-model.number="feeForm.defaultAmount"
                type="number"
                step="0.01"
                min="0"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="0.00"
                required
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
            <textarea
              v-model="feeForm.description"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Brief description of this fee"
            ></textarea>
          </div>

          <div class="grid grid-cols-3 gap-4">
            <div class="flex items-center">
              <input
                v-model="feeForm.mandatory"
                type="checkbox"
                id="mandatory"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label for="mandatory" class="ml-2 block text-sm text-gray-700">
                Mandatory
              </label>
            </div>

            <div class="flex items-center">
              <input
                v-model="feeForm.recurring"
                type="checkbox"
                id="recurring"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label for="recurring" class="ml-2 block text-sm text-gray-700">
                Recurring
              </label>
            </div>

            <div class="flex items-center">
              <input
                v-model="feeForm.active"
                type="checkbox"
                id="active"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label for="active" class="ml-2 block text-sm text-gray-700">
                Active
              </label>
            </div>
          </div>

          <div class="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              @click="showCreateModal = false"
              class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 font-medium"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="submitting"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium disabled:opacity-50"
            >
              {{ submitting ? 'Saving...' : (editingFee ? 'Update Fee' : 'Create Fee') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showDeleteModal = false"
    >
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Confirm Deletion</h2>
        <p class="text-gray-600 mb-6">
          Are you sure you want to delete "{{ feeToDelete?.feeName }}"? This action cannot be undone.
        </p>
        <div class="flex justify-end space-x-3">
          <button
            @click="showDeleteModal = false"
            class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 font-medium"
          >
            Cancel
          </button>
          <button
            @click="deleteFee"
            :disabled="submitting"
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 font-medium disabled:opacity-50"
          >
            {{ submitting ? 'Deleting...' : 'Delete' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// fee management script - handles creating and updating fee structures
// took a while to get all teh different categories and types working
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'

// reactive state for managing fees and modal visibility
const fees = ref([])
const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showDeleteModal = ref(false)

// Toast notification state - wierd name but it makes sense
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

function showNotification(message, type = 'success') {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 5000)
}
const editingFee = ref(null)
const feeToDelete = ref(null)
const filterCategory = ref('')
const filterStatus = ref('')

const feeForm = ref({
  feeName: '',
  feeCode: '',
  category: '',
  defaultAmount: 0,
  description: '',
  mandatory: true,
  recurring: true,
  active: true
})

const filteredFees = computed(() => {
  let filtered = fees.value

  if (filterCategory.value) {
    filtered = filtered.filter(f => f.category === filterCategory.value)
  }

  if (filterStatus.value === 'active') {
    filtered = filtered.filter(f => f.active === true)
  } else if (filterStatus.value === 'inactive') {
    filtered = filtered.filter(f => f.active === false)
  }

  return filtered
})

onMounted(() => {
  loadFees()
})

async function loadFees() {
  try {
    loading.value = true
    const response = await api.getAllFeeStructures()
    fees.value = response.data
  } catch (error) {
    console.error('Error loading fees:', error)
    showNotification('Failed to load fee structures', 'error')
  } finally {
    loading.value = false
  }
}

function filterFees() {
  // Filtering is handled by computed property
}

function editFee(fee) {
  editingFee.value = fee
  feeForm.value = {
    feeName: fee.feeName,
    feeCode: fee.feeCode,
    category: fee.category,
    defaultAmount: fee.defaultAmount,
    description: fee.description || '',
    mandatory: fee.mandatory,
    recurring: fee.recurring,
    active: fee.active
  }
  showCreateModal.value = true
}

async function saveFee() {
  try {
    submitting.value = true

    if (editingFee.value) {
      await api.updateFeeStructure(editingFee.value.id, feeForm.value)
    } else {
      await api.createFeeStructure(feeForm.value)
    }

    showCreateModal.value = false
    await loadFees()
    resetForm()
  } catch (error) {
    console.error('Error saving fee:', error)
    showNotification(error.response?.data || 'Failed to save fee structure', 'error')
  } finally {
    submitting.value = false
  }
}

async function toggleFeeStatus(fee) {
  try {
    const updatedFee = {
      ...fee,
      active: !fee.active
    }
    await api.updateFeeStructure(fee.id, updatedFee)
    await loadFees()
  } catch (error) {
    console.error('Error updating fee status:', error)
    showNotification('Failed to update fee status', 'error')
  }
}

function confirmDelete(fee) {
  feeToDelete.value = fee
  showDeleteModal.value = true
}

async function deleteFee() {
  try {
    submitting.value = true
    await api.deleteFeeStructure(feeToDelete.value.id)
    showDeleteModal.value = false
    await loadFees()
    feeToDelete.value = null
  } catch (error) {
    console.error('Error deleting fee:', error)
    showNotification(error.response?.data || 'Failed to delete fee structure', 'error')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  editingFee.value = null
  feeForm.value = {
    feeName: '',
    feeCode: '',
    category: '',
    defaultAmount: 0,
    description: '',
    mandatory: true,
    recurring: true,
    active: true
  }
}

function getCategoryClass(category) {
  const classes = {
    'ACADEMIC': 'bg-blue-100 text-blue-800',
    'NON_ACADEMIC': 'bg-purple-100 text-purple-800',
    'HOSTEL': 'bg-green-100 text-green-800',
    'TRANSPORTATION': 'bg-yellow-100 text-yellow-800',
    'MISCELLANEOUS': 'bg-gray-100 text-gray-800'
  }
  return classes[category] || 'bg-gray-100 text-gray-800'
}
</script>
