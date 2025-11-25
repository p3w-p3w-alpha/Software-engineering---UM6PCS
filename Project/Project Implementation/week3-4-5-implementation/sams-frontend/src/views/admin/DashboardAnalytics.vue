<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 p-6">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Dashboard Analytics</h1>
      <p class="text-gray-600 mt-1">Comprehensive overview of your academic management system</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="text-center">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        <p class="mt-4 text-gray-600">Loading analytics...</p>
      </div>
    </div>

    <!-- Dashboard Content -->
    <div v-else>
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Students -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 font-medium">Total Students</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">{{ dashboardStats.totalStudents || 0 }}</p>
              <p class="text-sm text-green-600 mt-2">
                <span class="font-medium">{{ dashboardStats.activeStudents || 0 }}</span> active
              </p>
            </div>
            <div class="bg-blue-100 rounded-full p-3">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
              </svg>
            </div>
          </div>
        </div>

        <!-- Total Faculty -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 font-medium">Total Faculty</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">{{ dashboardStats.totalFaculty || 0 }}</p>
              <p class="text-sm text-green-600 mt-2">
                <span class="font-medium">{{ dashboardStats.activeFaculty || 0 }}</span> active
              </p>
            </div>
            <div class="bg-purple-100 rounded-full p-3">
              <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
              </svg>
            </div>
          </div>
        </div>

        <!-- Total Courses -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 font-medium">Total Courses</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">{{ dashboardStats.totalCourses || 0 }}</p>
              <p class="text-sm text-gray-500 mt-2">Active courses</p>
            </div>
            <div class="bg-green-100 rounded-full p-3">
              <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
            </div>
          </div>
        </div>

        <!-- Total Enrollments -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 font-medium">Total Enrollments</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">{{ dashboardStats.totalEnrollments || 0 }}</p>
              <p class="text-sm text-gray-500 mt-2">All time</p>
            </div>
            <div class="bg-orange-100 rounded-full p-3">
              <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <!-- Enrollment Trends Chart -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Enrollment Trends</h3>
          <div class="h-64">
            <LineChart v-if="enrollmentChartData" :data="enrollmentChartData" />
          </div>
        </div>

        <!-- Grade Distribution Chart -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Distribution</h3>
          <div class="h-64">
            <DoughnutChart v-if="gradeChartData" :data="gradeChartData" />
          </div>
        </div>
      </div>

      <!-- Financial Summary and Recent Activities -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Financial Summary -->
        <div class="lg:col-span-2 bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Financial Summary</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-green-50 rounded-lg p-4">
              <p class="text-sm text-gray-600 font-medium">Total Revenue</p>
              <p class="text-2xl font-bold text-green-700 mt-2">
                ${{ formatCurrency(financialSummary.totalRevenue) }}
              </p>
              <p class="text-xs text-gray-500 mt-1">{{ financialSummary.completedPaymentCount }} payments</p>
            </div>
            <div class="bg-yellow-50 rounded-lg p-4">
              <p class="text-sm text-gray-600 font-medium">Pending</p>
              <p class="text-2xl font-bold text-yellow-700 mt-2">
                ${{ formatCurrency(financialSummary.pendingPayments) }}
              </p>
              <p class="text-xs text-gray-500 mt-1">{{ financialSummary.pendingPaymentCount }} pending</p>
            </div>
            <div class="bg-blue-50 rounded-lg p-4">
              <p class="text-sm text-gray-600 font-medium">Collection Rate</p>
              <p class="text-2xl font-bold text-blue-700 mt-2">
                {{ formatPercentage(financialSummary.collectionRate) }}%
              </p>
              <p class="text-xs text-gray-500 mt-1">Success rate</p>
            </div>
          </div>
        </div>

        <!-- Recent Activities -->
        <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Recent Activities</h3>
          <div class="space-y-4 max-h-64 overflow-y-auto">
            <div v-for="activity in recentActivities" :key="activity.timestamp"
                 class="flex items-start space-x-3 text-sm">
              <div class="bg-gray-100 rounded-full p-2 mt-1">
                <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
              </div>
              <div class="flex-1">
                <p class="text-gray-700">{{ activity.description }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ formatDate(activity.timestamp) }}</p>
              </div>
            </div>
            <div v-if="recentActivities.length === 0" class="text-center text-gray-500 py-4">
              No recent activities
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
import LineChart from '@/components/charts/LineChart.vue'
import DoughnutChart from '@/components/charts/DoughnutChart.vue'

const loading = ref(true)
const dashboardStats = ref({})
const enrollmentTrends = ref([])
const gradeDistribution = ref([])
const financialSummary = ref({})
const recentActivities = ref([])

const enrollmentChartData = ref(null)
const gradeChartData = ref(null)

onMounted(async () => {
  await loadDashboardData()
})

async function loadDashboardData() {
  try {
    loading.value = true

    // Load complete dashboard data
    const response = await api.getCompleteDashboard()
    const data = response.data

    dashboardStats.value = data.stats || {}
    enrollmentTrends.value = data.enrollmentTrends || []
    gradeDistribution.value = data.gradeDistribution || []
    financialSummary.value = data.financialSummary || {}
    recentActivities.value = data.recentActivities || []

    // Prepare enrollment chart data
    enrollmentChartData.value = {
      labels: enrollmentTrends.value.map(t => t.period),
      datasets: [{
        label: 'Enrollments',
        data: enrollmentTrends.value.map(t => t.enrollmentCount),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        tension: 0.4
      }]
    }

    // Prepare grade distribution chart data
    gradeChartData.value = {
      labels: gradeDistribution.value.map(g => g.gradeValue),
      datasets: [{
        label: 'Grades',
        data: gradeDistribution.value.map(g => g.count),
        backgroundColor: [
          'rgba(34, 197, 94, 0.8)',
          'rgba(59, 130, 246, 0.8)',
          'rgba(251, 191, 36, 0.8)',
          'rgba(249, 115, 22, 0.8)',
          'rgba(239, 68, 68, 0.8)'
        ]
      }]
    }
  } catch (error) {
    console.error('Error loading dashboard data:', error)
  } finally {
    loading.value = false
  }
}

function formatCurrency(amount) {
  if (!amount) return '0.00'
  return amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatPercentage(value) {
  if (!value) return '0.0'
  return value.toFixed(1)
}

function formatDate(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
