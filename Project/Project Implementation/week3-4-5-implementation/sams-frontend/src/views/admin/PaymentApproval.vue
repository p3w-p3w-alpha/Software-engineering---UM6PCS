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
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Payment Approval</h1>
      <p class="mt-2 text-gray-600">Review and approve student payment submissions</p>
    </div>

    <!-- Filter Tabs -->
    <div class="mb-6">
      <div class="border-b border-gray-200">
        <nav class="-mb-px flex space-x-8">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            @click="activeTab = tab.value"
            class="py-4 px-1 border-b-2 font-medium text-sm transition-colors"
            :class="[
              activeTab === tab.value
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            ]"
          >
            {{ tab.label }}
            <span
              v-if="tab.count > 0"
              class="ml-2 py-0.5 px-2 rounded-full text-xs font-semibold"
              :class="[
                activeTab === tab.value
                  ? 'bg-blue-100 text-blue-600'
                  : 'bg-gray-100 text-gray-600'
              ]"
            >
              {{ tab.count }}
            </span>
          </button>
        </nav>
      </div>
    </div>

    <!-- Payments Table -->
    <div class="bg-white rounded-lg shadow">
      <DataTable
        :columns="columns"
        :data="filteredPayments"
        :loading="loading"
        searchable
        search-placeholder="Search by student name or transaction reference..."
        :search-keys="['studentName', 'transactionReference']"
      >
        <template #cell-student="{ row }">
          <div>
            <p class="font-medium text-gray-900">{{ row.studentName }}</p>
            <p class="text-sm text-gray-600">{{ row.studentEmail }}</p>
          </div>
        </template>

        <template #cell-amount="{ row }">
          <div>
            <p class="font-semibold text-gray-900">${{ row.paidAmount }}</p>
            <p class="text-xs text-gray-500">of ${{ row.amount }}</p>
          </div>
        </template>

        <template #cell-status="{ value }">
          <span
            class="px-2 py-1 text-xs font-semibold rounded-full"
            :class="getStatusClass(value)"
          >
            {{ value }}
          </span>
        </template>

        <template #row-actions="{ row }">
          <button
            @click="viewPaymentDetails(row)"
            class="text-blue-600 hover:text-blue-900 text-sm font-medium"
          >
            Review
          </button>
        </template>
      </DataTable>
    </div>

    <!-- Payment Details Modal -->
    <Modal
      v-model="showDetailsModal"
      title="Payment Details"
      size="lg"
      :show-footer="selectedPayment?.status === 'PAID'"
      :show-cancel="true"
      :show-confirm="false"
    >
      <div v-if="selectedPayment">
        <!-- Student Info -->
        <div class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-2">Student Information</h3>
          <div class="bg-gray-50 rounded-lg p-4">
            <p class="font-semibold text-gray-900">{{ selectedPayment.studentName }}</p>
            <p class="text-sm text-gray-600">{{ selectedPayment.studentEmail }}</p>
          </div>
        </div>

        <!-- Payment Info -->
        <div class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-2">Payment Information</h3>
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-blue-50 rounded-lg p-4">
              <p class="text-sm text-blue-600 font-medium">Total Amount</p>
              <p class="text-2xl font-bold text-blue-900 mt-1">${{ selectedPayment.amount }}</p>
            </div>
            <div class="bg-green-50 rounded-lg p-4">
              <p class="text-sm text-green-600 font-medium">Paid Amount</p>
              <p class="text-2xl font-bold text-green-900 mt-1">${{ selectedPayment.paidAmount }}</p>
            </div>
          </div>
        </div>

        <!-- Transaction Details -->
        <div class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-2">Transaction Details</h3>
          <div class="bg-gray-50 rounded-lg p-4 space-y-2">
            <div class="flex justify-between">
              <span class="text-sm text-gray-600">Semester:</span>
              <span class="text-sm font-medium text-gray-900">{{ selectedPayment.semester }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-600">Payment Method:</span>
              <span class="text-sm font-medium text-gray-900">{{ selectedPayment.paymentMethod || 'N/A' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-600">Transaction Reference:</span>
              <span class="text-sm font-medium text-gray-900 font-mono">{{ selectedPayment.transactionReference || 'N/A' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-600">Payment Date:</span>
              <span class="text-sm font-medium text-gray-900">{{ formatDate(selectedPayment.paymentDate) }}</span>
            </div>
            <div v-if="selectedPayment.notes" class="pt-2 border-t border-gray-200">
              <span class="text-sm text-gray-600 block mb-1">Notes:</span>
              <p class="text-sm text-gray-900">{{ selectedPayment.notes }}</p>
            </div>
          </div>
        </div>

        <!-- Enrolled Courses -->
        <div class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-2">Enrolled Courses</h3>
          <div class="space-y-2">
            <div
              v-for="enrollment in selectedPayment.enrollments"
              :key="enrollment.id"
              class="flex items-center justify-between p-3 rounded-lg"
              :class="enrollment.status === 'WAITLISTED' ? 'bg-yellow-50 border border-yellow-200' : 'bg-gray-50'"
            >
              <div>
                <div class="flex items-center gap-2">
                  <p class="font-medium text-gray-900">{{ enrollment.course.courseCode }} - {{ enrollment.course.courseName }}</p>
                  <span
                    v-if="enrollment.status === 'WAITLISTED'"
                    class="px-2 py-0.5 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800"
                  >
                    Waitlisted
                  </span>
                </div>
                <p class="text-sm text-gray-600">{{ enrollment.course.creditHours }} Credits</p>
                <p v-if="enrollment.status === 'WAITLISTED'" class="text-xs text-yellow-700 mt-1">
                  <i class="pi pi-info-circle mr-1"></i>
                  Will be moved to enrolled upon approval
                </p>
              </div>
              <p class="font-semibold text-gray-900">${{ enrollment.course.courseFee }}</p>
            </div>
          </div>
        </div>

        <!-- Waitlist Notice -->
        <div v-if="hasWaitlistedEnrollments" class="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
          <div class="flex items-start gap-3">
            <i class="pi pi-exclamation-triangle text-yellow-600 text-xl mt-0.5"></i>
            <div>
              <h4 class="font-semibold text-yellow-800">Waitlist Notice</h4>
              <p class="text-sm text-yellow-700 mt-1">
                This student has {{ waitlistedCount }} course(s) on the waitlist.
                Approving this payment will automatically move them from waitlist to enrolled status.
              </p>
            </div>
          </div>
        </div>

        <!-- Approval Actions -->
        <div v-if="selectedPayment.status === 'PAID'" class="flex space-x-3">
          <button
            @click="approvePayment"
            :disabled="processing"
            class="flex-1 px-4 py-3 bg-green-600 text-white font-medium rounded-md hover:bg-green-700 disabled:opacity-50 transition-colors"
          >
            Approve Payment
          </button>
          <button
            @click="showRejectModal = true"
            :disabled="processing"
            class="flex-1 px-4 py-3 bg-red-600 text-white font-medium rounded-md hover:bg-red-700 disabled:opacity-50 transition-colors"
          >
            Reject Payment
          </button>
        </div>
      </div>
    </Modal>

    <!-- Rejection Reason Modal -->
    <Modal
      v-model="showRejectModal"
      title="Reject Payment"
      confirm-text="Reject"
      @confirm="rejectPayment"
    >
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">Rejection Reason *</label>
        <textarea
          v-model="rejectionReason"
          rows="4"
          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
          placeholder="Enter the reason for rejection..."
          required
        ></textarea>
        <p class="mt-2 text-sm text-gray-500">
          This reason will be visible to the student.
        </p>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../services/api'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'

const payments = ref([])
const loading = ref(false)
const processing = ref(false)
const activeTab = ref('pending')
const showDetailsModal = ref(false)
const showRejectModal = ref(false)
const selectedPayment = ref(null)
const rejectionReason = ref('')

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

const tabs = computed(() => [
  { label: 'Pending Review', value: 'pending', count: payments.value.filter(p => p.status === 'PAID').length },
  { label: 'Approved', value: 'approved', count: payments.value.filter(p => p.status === 'APPROVED').length },
  { label: 'Rejected', value: 'rejected', count: payments.value.filter(p => p.status === 'REJECTED').length },
  { label: 'All', value: 'all', count: 0 }
])

const columns = [
  { key: 'student', label: 'Student' },
  { key: 'semester', label: 'Semester' },
  { key: 'amount', label: 'Amount' },
  { key: 'transactionReference', label: 'Reference' },
  { key: 'status', label: 'Status' },
  { key: 'paymentDate', label: 'Payment Date' }
]

const filteredPayments = computed(() => {
  if (activeTab.value === 'all') return payments.value

  const statusMap = {
    'pending': 'PAID',
    'approved': 'APPROVED',
    'rejected': 'REJECTED'
  }

  return payments.value.filter(p => p.status === statusMap[activeTab.value])
})

// Waitlist computed properties
const hasWaitlistedEnrollments = computed(() => {
  if (!selectedPayment.value?.enrollments) return false
  return selectedPayment.value.enrollments.some(e => e.status === 'WAITLISTED')
})

const waitlistedCount = computed(() => {
  if (!selectedPayment.value?.enrollments) return 0
  return selectedPayment.value.enrollments.filter(e => e.status === 'WAITLISTED').length
})

const loadPayments = async () => {
  try {
    loading.value = true
    const response = await api.getAllPayments()
    payments.value = response.data.map(p => ({
      ...p,
      studentName: p.studentName || p.student?.username || 'N/A',
      studentEmail: p.studentEmail || p.student?.email || 'N/A',
      semester: p.semesterName || p.semester?.name || 'N/A',
      paymentDate: formatDate(p.paymentDate)
    }))
  } catch (error) {
    console.error('Error loading payments:', error)
    showNotification(error.userMessage || error.response?.data?.message || 'Failed to load payments', 'error')
  } finally {
    loading.value = false
  }
}

const viewPaymentDetails = async (payment) => {
  try {
    // Fetch full payment details with enrollments
    const response = await api.getPaymentById(payment.id)
    selectedPayment.value = {
      ...response.data,
      studentName: response.data.studentName || response.data.student?.username || 'N/A',
      studentEmail: response.data.studentEmail || response.data.student?.email || 'N/A',
      semester: response.data.semesterName || response.data.semester?.name || 'N/A'
    }
    showDetailsModal.value = true
  } catch (error) {
    console.error('Error loading payment details:', error)
    showNotification(error.userMessage || error.response?.data?.message || 'Failed to load payment details', 'error')
  }
}

const approvePayment = async () => {
  try {
    processing.value = true

    // Check for waitlisted enrollments before approval
    const waitlistedEnrollments = selectedPayment.value.enrollments?.filter(e => e.status === 'WAITLISTED') || []

    // Approve the payment
    await api.approvePayment(selectedPayment.value.id)

    // If there were waitlisted enrollments, promote them to enrolled
    if (waitlistedEnrollments.length > 0) {
      for (const enrollment of waitlistedEnrollments) {
        try {
          // Call API to promote from waitlist to enrolled
          await api.promoteFromWaitlist(enrollment.courseId, enrollment.studentId || selectedPayment.value.student?.id)
        } catch (enrollError) {
          console.warn(`Failed to promote enrollment ${enrollment.id} from waitlist:`, enrollError)
        }
      }

      showNotification(`Payment approved successfully! ${waitlistedEnrollments.length} course(s) have been moved from waitlist to enrolled status.`, 'success')
    } else {
      showNotification('Payment approved successfully! Student enrollments are now active.', 'success')
    }

    showDetailsModal.value = false
    await loadPayments()
  } catch (error) {
    const errorMessage = error.response?.data?.message || 'Failed to approve payment'
    showNotification(errorMessage, 'error')
  } finally {
    processing.value = false
  }
}

const rejectPayment = async () => {
  if (!rejectionReason.value.trim()) {
    showNotification('Please provide a rejection reason', 'error')
    return
  }

  try {
    processing.value = true
    await api.rejectPayment(selectedPayment.value.id, rejectionReason.value)
    showNotification('Payment rejected', 'success')
    showRejectModal.value = false
    showDetailsModal.value = false
    rejectionReason.value = ''
    await loadPayments()
  } catch (error) {
    const errorMessage = error.response?.data?.message || 'Failed to reject payment'
    showNotification(errorMessage, 'error')
  } finally {
    processing.value = false
  }
}

const getStatusClass = (status) => {
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
  loadPayments()
})
</script>
