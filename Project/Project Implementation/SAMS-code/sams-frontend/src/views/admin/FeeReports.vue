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
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Fee Reports</h1>
      <p class="mt-2 text-gray-600">View fee collection reports and analytics</p>
    </div>

    <!-- Report Options -->
    <div class="mb-6 bg-white rounded-lg shadow p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Generate Report</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Semester</label>
          <select
            v-model="selectedSemester"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">Select Semester</option>
            <option v-for="semester in semesters" :key="semester.id" :value="semester.id">
              {{ semester.name }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Report Type</label>
          <select
            v-model="reportType"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="SUMMARY">Summary Report</option>
            <option value="DETAILED">Detailed Report</option>
            <option value="BY_CATEGORY">By Category</option>
            <option value="BY_STUDENT">By Student</option>
          </select>
        </div>

        <div class="flex items-end">
          <button
            @click="generateReport"
            :disabled="!selectedSemester || loading"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors disabled:opacity-50"
          >
            Generate Report
          </button>
        </div>
      </div>
    </div>

    <!-- Report Results -->
    <div v-if="report" class="bg-white rounded-lg shadow p-6">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-bold text-gray-900">{{ reportTypeName }} - {{ semesterName }}</h2>
        <button
          @click="exportReport"
          class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 font-medium"
        >
          Export to CSV
        </button>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-blue-50 rounded-lg p-4">
          <p class="text-sm text-blue-600 font-medium">Total Fees</p>
          <p class="text-2xl font-bold text-blue-900 mt-1">
            ${{ (report.totalFeesCollected + report.totalFeesOutstanding).toFixed(2) }}
          </p>
        </div>

        <div class="bg-green-50 rounded-lg p-4">
          <p class="text-sm text-green-600 font-medium">Collected</p>
          <p class="text-2xl font-bold text-green-900 mt-1">
            ${{ report.totalFeesCollected.toFixed(2) }}
          </p>
        </div>

        <div class="bg-yellow-50 rounded-lg p-4">
          <p class="text-sm text-yellow-600 font-medium">Outstanding</p>
          <p class="text-2xl font-bold text-yellow-900 mt-1">
            ${{ report.totalFeesOutstanding.toFixed(2) }}
          </p>
        </div>

        <div class="bg-purple-50 rounded-lg p-4">
          <p class="text-sm text-purple-600 font-medium">Collection Rate</p>
          <p class="text-2xl font-bold text-purple-900 mt-1">
            {{ report.collectionRate.toFixed(1) }}%
          </p>
        </div>
      </div>

      <!-- Category Breakdown -->
      <div v-if="report.categorySummaries && report.categorySummaries.length > 0" class="mb-8">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Fee Breakdown by Category</h3>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Total Amount</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="category in report.categorySummaries" :key="category.category">
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="font-medium text-gray-900">{{ category.category }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <span class="text-lg font-semibold text-gray-900">${{ category.totalAmount.toFixed(2) }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Student Summaries -->
      <div v-if="report.studentSummaries && report.studentSummaries.length > 0">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Student Fee Summary</h3>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Total Fees</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Paid</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Outstanding</th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="student in report.studentSummaries" :key="student.studentId">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="font-medium text-gray-900">{{ student.studentName }}</div>
                  <div class="text-sm text-gray-500">{{ student.program }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <span class="text-gray-900">${{ student.totalFees.toFixed(2) }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <span class="text-green-600 font-medium">${{ student.paidAmount.toFixed(2) }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <span class="text-yellow-600 font-medium">${{ student.outstandingAmount.toFixed(2) }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <span
                    class="px-2 py-1 text-xs font-semibold rounded-full"
                    :class="getStatusClass(student.status)"
                  >
                    {{ student.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Additional Stats -->
      <div class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="border border-gray-200 rounded-lg p-4">
          <p class="text-sm text-gray-600">Total Students</p>
          <p class="text-xl font-bold text-gray-900 mt-1">{{ report.totalStudents }}</p>
        </div>

        <div class="border border-gray-200 rounded-lg p-4">
          <p class="text-sm text-gray-600">Students with Outstanding Fees</p>
          <p class="text-xl font-bold text-gray-900 mt-1">{{ report.studentsWithOutstandingFees }}</p>
        </div>

        <div class="border border-gray-200 rounded-lg p-4">
          <p class="text-sm text-gray-600">Total Discounts Given</p>
          <p class="text-xl font-bold text-gray-900 mt-1">${{ report.totalDiscountsGiven?.toFixed(2) || '0.00' }}</p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="bg-white rounded-lg shadow p-12 text-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
      <p class="text-gray-600">Generating report...</p>
    </div>

    <!-- Empty State -->
    <div v-if="!report && !loading" class="bg-white rounded-lg shadow p-12 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No report generated</h3>
      <p class="mt-1 text-sm text-gray-500">Select a semester and report type to generate a report.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'

const loading = ref(false)

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
const report = ref(null)
const semesters = ref([])
const selectedSemester = ref('')
const reportType = ref('SUMMARY')

const reportTypeName = computed(() => {
  const types = {
    'SUMMARY': 'Summary Report',
    'DETAILED': 'Detailed Report',
    'BY_CATEGORY': 'Category Report',
    'BY_STUDENT': 'Student Report'
  }
  return types[reportType.value] || 'Report'
})

const semesterName = computed(() => {
  const semester = semesters.value.find(s => s.id === selectedSemester.value)
  return semester ? semester.name : ''
})

onMounted(async () => {
  await loadSemesters()
})

async function loadSemesters() {
  try {
    const response = await api.getSemesters()
    semesters.value = response.data
    if (semesters.value.length > 0) {
      selectedSemester.value = semesters.value[0].id
    }
  } catch (error) {
    console.error('Error loading semesters:', error)
  }
}

async function generateReport() {
  if (!selectedSemester.value) return

  try {
    loading.value = true
    const response = await api.getFeeReport(selectedSemester.value, reportType.value)
    report.value = response.data
  } catch (error) {
    console.error('Error generating report:', error)
    showNotification('Failed to generate report', 'error')
  } finally {
    loading.value = false
  }
}

function exportReport() {
  if (!report.value) return

  // Simple CSV export
  let csv = 'Student Name,Program,Total Fees,Paid Amount,Outstanding,Status\n'

  report.value.studentSummaries.forEach(student => {
    csv += `"${student.studentName}","${student.program}",${student.totalFees},${student.paidAmount},${student.outstandingAmount},"${student.status}"\n`
  })

  const blob = new Blob([csv], { type: 'text/csv' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `fee-report-${semesterName.value}-${Date.now()}.csv`
  a.click()
  window.URL.revokeObjectURL(url)
}

function getStatusClass(status) {
  const classes = {
    'PAID': 'bg-green-100 text-green-800',
    'PARTIAL': 'bg-yellow-100 text-yellow-800',
    'PENDING': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}
</script>
