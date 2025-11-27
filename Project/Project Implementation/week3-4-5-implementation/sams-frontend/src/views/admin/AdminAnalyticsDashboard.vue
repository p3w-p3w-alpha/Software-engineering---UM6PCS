<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900">Advanced Analytics</h1>
        <p class="mt-2 text-sm text-gray-600">
          Comprehensive insights and data visualization for institutional performance
        </p>
      </div>

      <!-- Date Range Filter -->
      <div class="mb-6 bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Date Range</label>
            <select
              v-model="dateRange"
              @change="loadAnalytics"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="7">Last 7 Days</option>
              <option value="30">Last 30 Days</option>
              <option value="90">Last 90 Days</option>
              <option value="180">Last 6 Months</option>
              <option value="365">Last Year</option>
            </select>
          </div>
          <div class="flex-1"></div>
          <button
            @click="exportReport"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors flex items-center"
          >
            <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Export Report
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Analytics Content -->
      <div v-else class="space-y-6">
        <!-- KPI Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-2">
              <p class="text-sm font-medium text-gray-600">Total Students</p>
              <div class="p-2 bg-blue-100 rounded-lg">
                <svg class="h-5 w-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ analytics.totalStudents }}</p>
            <p class="text-sm text-green-600 mt-2">+{{ analytics.newStudentsThisPeriod }} this period</p>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-2">
              <p class="text-sm font-medium text-gray-600">Active Courses</p>
              <div class="p-2 bg-purple-100 rounded-lg">
                <svg class="h-5 w-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ analytics.activeCourses }}</p>
            <p class="text-sm text-gray-600 mt-2">{{ analytics.totalEnrollments }} total enrollments</p>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-2">
              <p class="text-sm font-medium text-gray-600">Average GPA</p>
              <div class="p-2 bg-green-100 rounded-lg">
                <svg class="h-5 w-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                </svg>
              </div>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ analytics.averageGPA.toFixed(2) }}</p>
            <p class="text-sm" :class="analytics.gpaChange >= 0 ? 'text-green-600' : 'text-red-600'">
              {{ analytics.gpaChange >= 0 ? '+' : '' }}{{ analytics.gpaChange.toFixed(2) }} vs last period
            </p>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-2">
              <p class="text-sm font-medium text-gray-600">Revenue</p>
              <div class="p-2 bg-yellow-100 rounded-lg">
                <svg class="h-5 w-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <p class="text-3xl font-bold text-gray-900">${{ formatNumber(analytics.totalRevenue) }}</p>
            <p class="text-sm text-gray-600 mt-2">${{ formatNumber(analytics.pendingPayments) }} pending</p>
          </div>
        </div>

        <!-- Enrollment Trends Chart -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Enrollment Trends</h3>
          <div class="h-64 flex items-center justify-center bg-gray-50 rounded-lg">
            <p class="text-gray-500">Chart visualization placeholder - Integrate Chart.js or similar</p>
          </div>
        </div>

        <!-- Grade Distribution -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Distribution</h3>
            <div class="space-y-4">
              <div v-for="grade in gradeDistribution" :key="grade.letter" class="flex items-center">
                <div class="w-12 text-sm font-medium text-gray-700">{{ grade.letter }}</div>
                <div class="flex-1">
                  <div class="flex items-center">
                    <div class="flex-1 bg-gray-200 rounded-full h-8 overflow-hidden">
                      <div
                        class="h-full rounded-full transition-all duration-500"
                        :class="grade.color"
                        :style="{ width: `${grade.percentage}%` }"
                      ></div>
                    </div>
                    <div class="ml-4 w-20 text-right">
                      <span class="text-sm font-semibold text-gray-900">{{ grade.count }}</span>
                      <span class="text-xs text-gray-500 ml-1">({{ grade.percentage.toFixed(0) }}%)</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Attendance Overview -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Attendance Overview</h3>
            <div class="space-y-4">
              <div class="flex items-center justify-between p-4 bg-green-50 rounded-lg">
                <div>
                  <p class="text-sm font-medium text-gray-700">Excellent (90-100%)</p>
                  <p class="text-2xl font-bold text-green-600">{{ attendance.excellent }}</p>
                </div>
                <div class="text-sm text-gray-600">{{ ((attendance.excellent / attendance.total) * 100).toFixed(0) }}%</div>
              </div>
              <div class="flex items-center justify-between p-4 bg-yellow-50 rounded-lg">
                <div>
                  <p class="text-sm font-medium text-gray-700">Good (75-89%)</p>
                  <p class="text-2xl font-bold text-yellow-600">{{ attendance.good }}</p>
                </div>
                <div class="text-sm text-gray-600">{{ ((attendance.good / attendance.total) * 100).toFixed(0) }}%</div>
              </div>
              <div class="flex items-center justify-between p-4 bg-red-50 rounded-lg">
                <div>
                  <p class="text-sm font-medium text-gray-700">At Risk (<75%)</p>
                  <p class="text-2xl font-bold text-red-600">{{ attendance.atRisk }}</p>
                </div>
                <div class="text-sm text-gray-600">{{ ((attendance.atRisk / attendance.total) * 100).toFixed(0) }}%</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Top Performing Courses -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Top Performing Courses</h3>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Course</th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Enrollments</th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Avg Grade</th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Completion Rate</th>
                  <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Satisfaction</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-for="course in topCourses" :key="course.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4">
                    <p class="text-sm font-medium text-gray-900">{{ course.name }}</p>
                    <p class="text-xs text-gray-500">{{ course.code }}</p>
                  </td>
                  <td class="px-6 py-4 text-center text-sm text-gray-900">{{ course.enrollments }}</td>
                  <td class="px-6 py-4 text-center">
                    <span class="text-sm font-semibold" :class="getGradeColor(course.avgGrade)">
                      {{ course.avgGrade.toFixed(1) }}%
                    </span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <span class="text-sm font-semibold text-gray-900">{{ course.completionRate }}%</span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <div class="flex items-center justify-center">
                      <span class="text-sm font-semibold text-gray-900">{{ course.satisfaction.toFixed(1) }}</span>
                      <svg class="h-4 w-4 ml-1 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Demographics -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Student Demographics</h3>
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600">Male</span>
                <span class="text-sm font-semibold text-gray-900">{{ demographics.male }} ({{ demographics.malePercent }}%)</span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600">Female</span>
                <span class="text-sm font-semibold text-gray-900">{{ demographics.female }} ({{ demographics.femalePercent }}%)</span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600">Other/Prefer not to say</span>
                <span class="text-sm font-semibold text-gray-900">{{ demographics.other }} ({{ demographics.otherPercent }}%)</span>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Payment Status</h3>
            <div class="space-y-3">
              <div class="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                <span class="text-sm font-medium text-gray-700">Paid in Full</span>
                <span class="text-sm font-bold text-green-600">{{ paymentStatus.paidInFull }}%</span>
              </div>
              <div class="flex items-center justify-between p-3 bg-yellow-50 rounded-lg">
                <span class="text-sm font-medium text-gray-700">Partial Payment</span>
                <span class="text-sm font-bold text-yellow-600">{{ paymentStatus.partial }}%</span>
              </div>
              <div class="flex items-center justify-between p-3 bg-red-50 rounded-lg">
                <span class="text-sm font-medium text-gray-700">Overdue</span>
                <span class="text-sm font-bold text-red-600">{{ paymentStatus.overdue }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const loading = ref(true)
const dateRange = ref('30')

const analytics = ref({
  totalStudents: 0,
  newStudentsThisPeriod: 0,
  activeCourses: 0,
  totalEnrollments: 0,
  averageGPA: 0,
  gpaChange: 0,
  totalRevenue: 0,
  pendingPayments: 0
})

const gradeDistribution = ref([
  { letter: 'A', count: 0, percentage: 0, color: 'bg-green-500' },
  { letter: 'B', count: 0, percentage: 0, color: 'bg-blue-500' },
  { letter: 'C', count: 0, percentage: 0, color: 'bg-yellow-500' },
  { letter: 'D', count: 0, percentage: 0, color: 'bg-orange-500' },
  { letter: 'F', count: 0, percentage: 0, color: 'bg-red-500' }
])

const attendance = ref({
  total: 0,
  excellent: 0,
  good: 0,
  atRisk: 0
})

const topCourses = ref([])

const demographics = ref({
  male: 0,
  female: 0,
  other: 0,
  malePercent: 0,
  femalePercent: 0,
  otherPercent: 0
})

const paymentStatus = ref({
  paidInFull: 0,
  partial: 0,
  overdue: 0
})

onMounted(async () => {
  await loadAnalytics()
})

async function loadAnalytics() {
  try {
    loading.value = true

    // Load complete dashboard data
    const response = await api.getCompleteDashboard()
    const data = response.data

    // Populate analytics
    analytics.value = {
      totalStudents: data.totalUsers || 0,
      newStudentsThisPeriod: Math.floor(Math.random() * 50),
      activeCourses: data.totalCourses || 0,
      totalEnrollments: data.totalEnrollments || 0,
      averageGPA: 3.45,
      gpaChange: 0.12,
      totalRevenue: data.totalRevenue || 0,
      pendingPayments: data.pendingPayments || 0
    }

    // Mock grade distribution (replace with real data)
    const totalGrades = 500
    gradeDistribution.value = [
      { letter: 'A', count: 150, percentage: 30, color: 'bg-green-500' },
      { letter: 'B', count: 180, percentage: 36, color: 'bg-blue-500' },
      { letter: 'C', count: 100, percentage: 20, color: 'bg-yellow-500' },
      { letter: 'D', count: 45, percentage: 9, color: 'bg-orange-500' },
      { letter: 'F', count: 25, percentage: 5, color: 'bg-red-500' }
    ]

    // Mock attendance
    attendance.value = {
      total: 1000,
      excellent: 650,
      good: 250,
      atRisk: 100
    }

    // Mock top courses
    topCourses.value = [
      { id: 1, name: 'Introduction to Computer Science', code: 'CS101', enrollments: 120, avgGrade: 87.5, completionRate: 95, satisfaction: 4.6 },
      { id: 2, name: 'Calculus I', code: 'MATH201', enrollments: 95, avgGrade: 82.3, completionRate: 88, satisfaction: 4.2 },
      { id: 3, name: 'English Literature', code: 'ENG301', enrollments: 78, avgGrade: 89.1, completionRate: 92, satisfaction: 4.7 },
      { id: 4, name: 'Physics II', code: 'PHYS202', enrollments: 65, avgGrade: 79.8, completionRate: 85, satisfaction: 4.1 },
      { id: 5, name: 'Data Structures', code: 'CS201', enrollments: 88, avgGrade: 85.6, completionRate: 90, satisfaction: 4.5 }
    ]

    // Mock demographics
    demographics.value = {
      male: 580,
      female: 650,
      other: 20,
      malePercent: 46,
      femalePercent: 52,
      otherPercent: 2
    }

    // Mock payment status
    paymentStatus.value = {
      paidInFull: 75,
      partial: 18,
      overdue: 7
    }
  } catch (error) {
    console.error('Error loading analytics:', error)
    alert('Failed to load analytics data.')
  } finally {
    loading.value = false
  }
}

function exportReport() {
  alert('Report export functionality - Implement CSV/PDF export')
}

function formatNumber(num) {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function getGradeColor(grade) {
  if (grade >= 90) return 'text-green-600'
  if (grade >= 80) return 'text-blue-600'
  if (grade >= 70) return 'text-yellow-600'
  return 'text-orange-600'
}
</script>
