<template>
  <div class="max-w-7xl mx-auto">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Payments & Billing</h1>
      <p class="mt-2 text-gray-600">Manage your tuition payments and view payment history</p>
    </div>

    <!-- Current Semester Payment Status -->
    <div class="bg-white rounded-lg shadow mb-6">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-900">Current Semester Payment</h2>
      </div>
      <div class="p-6">
        <div v-if="loadingPayment" class="flex justify-center py-8">
          <LoadingSpinner />
        </div>

        <div v-else-if="!currentPayment" class="text-center py-8 text-gray-500">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="mt-2">No payment required at this time</p>
        </div>

        <div v-else>
          <!-- Payment Summary Card -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div class="bg-blue-50 rounded-lg p-4">
              <p class="text-sm text-blue-600 font-medium">Total Amount</p>
              <p class="text-2xl font-bold text-blue-900 mt-1">${{ currentPayment.amount }}</p>
            </div>
            <div class="bg-green-50 rounded-lg p-4">
              <p class="text-sm text-green-600 font-medium">Paid Amount</p>
              <p class="text-2xl font-bold text-green-900 mt-1">${{ currentPayment.paidAmount }}</p>
            </div>
            <div class="bg-red-50 rounded-lg p-4">
              <p class="text-sm text-red-600 font-medium">Remaining</p>
              <p class="text-2xl font-bold text-red-900 mt-1">${{ currentPayment.remainingAmount }}</p>
            </div>
          </div>

          <!-- Payment Status -->
          <div class="flex items-center justify-between mb-6">
            <div>
              <span class="text-sm text-gray-600">Status:</span>
              <span
                class="ml-2 px-3 py-1 text-sm font-semibold rounded-full"
                :class="getPaymentStatusClass(currentPayment.status)"
              >
                {{ currentPayment.status }}
              </span>
            </div>
            <div v-if="currentPayment.dueDate" class="text-sm text-gray-600">
              Due Date: <span class="font-medium">{{ formatDate(currentPayment.dueDate) }}</span>
            </div>
          </div>

          <!-- Enrolled Courses in Payment -->
          <div class="mb-6">
            <h3 class="text-md font-semibold text-gray-900 mb-3">Included Courses</h3>
            <div class="space-y-2">
              <div
                v-for="enrollment in currentPayment.enrollments"
                :key="enrollment.id"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <div>
                  <p class="font-medium text-gray-900">{{ enrollment.course.courseCode }} - {{ enrollment.course.courseName }}</p>
                  <p class="text-sm text-gray-600">{{ enrollment.course.creditHours }} Credits</p>
                </div>
                <p class="font-semibold text-gray-900">${{ enrollment.course.courseFee }}</p>
              </div>
            </div>
          </div>

          <!-- Submit Payment Button -->
          <div v-if="currentPayment.status === 'PENDING'" class="mb-6">
            <button
              @click="showPaymentModal = true"
              class="w-full md:w-auto px-6 py-3 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 transition-colors"
            >
              Submit Payment
            </button>
          </div>

          <!-- Payment Submitted Info -->
          <div v-if="currentPayment.status === 'PAID'" class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6">
            <div class="flex">
              <svg class="h-5 w-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div class="ml-3">
                <p class="text-sm text-yellow-700">
                  Your payment has been submitted and is awaiting admin approval. You will be notified once it's processed.
                </p>
                <div v-if="currentPayment.transactionReference" class="mt-2">
                  <p class="text-xs text-yellow-600">Transaction Reference: <span class="font-mono">{{ currentPayment.transactionReference }}</span></p>
                </div>
              </div>
            </div>
          </div>

          <!-- Payment Approved Info -->
          <div v-if="currentPayment.status === 'APPROVED'" class="bg-green-50 border-l-4 border-green-400 p-4 mb-6">
            <div class="flex">
              <svg class="h-5 w-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div class="ml-3">
                <p class="text-sm text-green-700 font-medium">
                  Payment approved! Your enrollments are now active.
                </p>
                <p v-if="currentPayment.approvedBy" class="text-xs text-green-600 mt-1">
                  Approved by {{ currentPayment.approvedBy.username }} on {{ formatDate(currentPayment.updatedAt) }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Payment History -->
    <div class="bg-white rounded-lg shadow">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-900">Payment History</h2>
      </div>
      <div class="p-6">
        <DataTable
          :columns="historyColumns"
          :data="paymentHistory"
          :loading="loadingHistory"
          searchable
          search-placeholder="Search payment history..."
          :search-keys="['semester', 'transactionReference']"
        >
          <template #cell-amount="{ value }">
            <span class="font-semibold">${{ value }}</span>
          </template>
          <template #cell-status="{ value }">
            <span
              class="px-2 py-1 text-xs font-semibold rounded-full"
              :class="getPaymentStatusClass(value)"
            >
              {{ value }}
            </span>
          </template>
          <template #row-actions="{ row }">
            <button
              @click="viewPaymentDetails(row)"
              class="text-blue-600 hover:text-blue-900 text-sm font-medium"
            >
              View Details
            </button>
          </template>
        </DataTable>
      </div>
    </div>

    <!-- Payment Submission Modal -->
    <Modal
      v-model="showPaymentModal"
      title="Submit Payment"
      confirm-text="Submit Payment"
      :confirm-disabled="!paymentForm.transactionReference || submitting"
      @confirm="submitPayment"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Amount</label>
          <input
            v-model="paymentForm.amount"
            type="number"
            step="0.01"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter amount"
          />
          <p class="mt-1 text-xs text-gray-500">Total due: ${{ currentPayment?.remainingAmount || 0 }}</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Method</label>
          <select
            v-model="paymentForm.paymentMethod"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="BANK_TRANSFER">Bank Transfer</option>
            <option value="CREDIT_CARD">Credit Card</option>
            <option value="DEBIT_CARD">Debit Card</option>
            <option value="CASH">Cash</option>
            <option value="CHECK">Check</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Transaction Reference *</label>
          <input
            v-model="paymentForm.transactionReference"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="e.g., TXN123456789"
            required
          />
          <p class="mt-1 text-xs text-gray-500">Enter your payment confirmation number</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Date</label>
          <input
            v-model="paymentForm.paymentDate"
            type="date"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Notes (Optional)</label>
          <textarea
            v-model="paymentForm.notes"
            rows="3"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            placeholder="Any additional information..."
          ></textarea>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'

const authStore = useAuthStore()

const currentPayment = ref(null)
const paymentHistory = ref([])
const loadingPayment = ref(false)
const loadingHistory = ref(false)
const showPaymentModal = ref(false)
const submitting = ref(false)

const paymentForm = ref({
  amount: 0,
  paymentMethod: 'BANK_TRANSFER',
  transactionReference: '',
  paymentDate: new Date().toISOString().split('T')[0],
  notes: ''
})

const historyColumns = [
  { key: 'semester', label: 'Semester' },
  { key: 'amount', label: 'Amount' },
  { key: 'paidAmount', label: 'Paid' },
  { key: 'status', label: 'Status' },
  { key: 'transactionReference', label: 'Reference' },
  { key: 'updatedAt', label: 'Last Updated' }
]

const loadCurrentPayment = async () => {
  try {
    loadingPayment.value = true
    const response = await api.getStudentPayments(authStore.userId)
    const payments = response.data

    // Find the most recent pending or paid payment
    currentPayment.value = payments.find(p =>
      p.status === 'PENDING' || p.status === 'PAID' || p.status === 'APPROVED'
    ) || null

    if (currentPayment.value) {
      paymentForm.value.amount = currentPayment.value.remainingAmount
    }
  } catch (error) {
    console.error('Error loading payment:', error)
  } finally {
    loadingPayment.value = false
  }
}

const loadPaymentHistory = async () => {
  try {
    loadingHistory.value = true
    const response = await api.getStudentPayments(authStore.userId)
    paymentHistory.value = response.data.map(p => ({
      ...p,
      semester: p.semester?.name || 'N/A',
      updatedAt: formatDate(p.updatedAt)
    }))
  } catch (error) {
    console.error('Error loading payment history:', error)
  } finally {
    loadingHistory.value = false
  }
}

const submitPayment = async () => {
  try {
    submitting.value = true
    await api.submitPayment(currentPayment.value.id, paymentForm.value)

    alert('Payment submitted successfully! Awaiting admin approval.')
    showPaymentModal.value = false

    // Reload data
    await loadCurrentPayment()
    await loadPaymentHistory()

    // Reset form
    paymentForm.value = {
      amount: 0,
      paymentMethod: 'BANK_TRANSFER',
      transactionReference: '',
      paymentDate: new Date().toISOString().split('T')[0],
      notes: ''
    }
  } catch (error) {
    const errorMessage = error.response?.data?.message || 'Payment submission failed'
    alert(errorMessage)
  } finally {
    submitting.value = false
  }
}

const viewPaymentDetails = async (payment) => {
  try {
    const response = await api.getPaymentHistory(payment.id)
    const history = response.data

    // Show payment history in alert (in production, use a proper modal)
    const historyText = history.map(h =>
      `${h.actionType} - ${h.newStatus} by ${h.performedBy?.username || 'System'} on ${formatDate(h.createdAt)}`
    ).join('\n')

    alert(`Payment History for ${payment.semester}:\n\n${historyText}`)
  } catch (error) {
    console.error('Error loading payment details:', error)
  }
}

const getPaymentStatusClass = (status) => {
  const classes = {
    'PENDING': 'bg-gray-100 text-gray-800',
    'PAID': 'bg-yellow-100 text-yellow-800',
    'APPROVED': 'bg-green-100 text-green-800',
    'REJECTED': 'bg-red-100 text-red-800',
    'PARTIAL': 'bg-blue-100 text-blue-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

onMounted(() => {
  loadCurrentPayment()
  loadPaymentHistory()
})
</script>
