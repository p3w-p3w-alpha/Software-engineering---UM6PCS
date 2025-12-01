<template>
  <div class="max-w-7xl mx-auto">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Degree Progress</h1>
      <p class="mt-2 text-gray-600">Track your progress toward graduation</p>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <div v-else-if="!progress" class="bg-white rounded-lg shadow p-12 text-center">
      <p class="text-gray-500">You are not currently enrolled in a degree program</p>
    </div>

    <div v-else>
      <!-- Program Overview -->
      <div class="bg-white rounded-lg shadow mb-6 p-6">
        <div class="flex items-start justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">{{ progress.degreeProgram.name }}</h2>
            <p class="text-gray-600 mt-1">{{ progress.degreeProgram.code }}</p>
          </div>
          <span
            class="px-4 py-2 text-sm font-semibold rounded-full"
            :class="getStatusClass(progress.status)"
          >
            {{ progress.status }}
          </span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div>
            <p class="text-sm text-gray-600">Start Date</p>
            <p class="text-lg font-semibold text-gray-900 mt-1">{{ formatDate(progress.startDate) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Expected Graduation</p>
            <p class="text-lg font-semibold text-gray-900 mt-1">{{ formatDate(progress.expectedGraduationDate) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Current GPA</p>
            <p class="text-lg font-semibold text-gray-900 mt-1">{{ progress.currentGpa?.toFixed(2) || '0.00' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600">Status</p>
            <p
              class="text-lg font-semibold mt-1"
              :class="progress.onTrack ? 'text-green-600' : 'text-red-600'"
            >
              {{ progress.onTrack ? 'On Track' : 'At Risk' }}
            </p>
          </div>
        </div>
      </div>

      <!-- Progress Circle -->
      <div class="bg-white rounded-lg shadow mb-6 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-6">Completion Progress</h3>

        <div class="flex items-center justify-center mb-6">
          <div class="relative inline-flex items-center justify-center">
            <svg class="transform -rotate-90 w-48 h-48">
              <circle
                cx="96"
                cy="96"
                r="88"
                stroke="currentColor"
                stroke-width="12"
                fill="transparent"
                class="text-gray-200"
              />
              <circle
                cx="96"
                cy="96"
                r="88"
                stroke="currentColor"
                stroke-width="12"
                fill="transparent"
                :stroke-dasharray="circumference"
                :stroke-dashoffset="circumference - (progress.completionPercentage / 100) * circumference"
                class="text-blue-600 transition-all duration-1000"
              />
            </svg>
            <div class="absolute inset-0 flex items-center justify-center flex-col">
              <span class="text-4xl font-bold text-gray-900">{{ Math.round(progress.completionPercentage) }}%</span>
              <span class="text-sm text-gray-600 mt-1">Complete</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="text-center">
            <p class="text-3xl font-bold text-gray-900">{{ progress.creditsCompleted }}</p>
            <p class="text-sm text-gray-600 mt-1">Credits Completed</p>
          </div>
          <div class="text-center">
            <p class="text-3xl font-bold text-gray-900">{{ progress.creditsRemaining }}</p>
            <p class="text-sm text-gray-600 mt-1">Credits Remaining</p>
          </div>
          <div class="text-center">
            <p class="text-3xl font-bold text-gray-900">{{ progress.degreeProgram.totalCreditsRequired }}</p>
            <p class="text-sm text-gray-600 mt-1">Total Required</p>
          </div>
        </div>
      </div>

      <!-- Graduation Readiness -->
      <div
        v-if="progress.completionPercentage >= 90"
        class="bg-green-50 border-l-4 border-green-400 p-4 mb-6"
      >
        <div class="flex">
          <svg class="h-6 w-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-green-800">Approaching Graduation!</h3>
            <p class="mt-1 text-sm text-green-700">
              You're almost there! Complete the remaining {{ progress.creditsRemaining }} credits to be eligible for graduation.
              Minimum GPA required: {{ progress.degreeProgram.minimumGpa }}
            </p>
          </div>
        </div>
      </div>

      <!-- Degree Requirements -->
      <div class="bg-white rounded-lg shadow">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Degree Requirements</h3>
        </div>
        <div class="p-6">
          <div v-if="progress.degreeProgram.requirements && progress.degreeProgram.requirements.length > 0" class="space-y-4">
            <div
              v-for="requirement in progress.degreeProgram.requirements"
              :key="requirement.id"
              class="border border-gray-200 rounded-lg p-4"
            >
              <div class="flex items-start justify-between mb-2">
                <div>
                  <h4 class="font-semibold text-gray-900">{{ requirement.name }}</h4>
                  <p class="text-sm text-gray-600">{{ requirement.type }} • {{ requirement.creditsRequired }} credits required</p>
                </div>
                <span
                  v-if="requirement.mandatory"
                  class="px-2 py-1 text-xs font-semibold bg-red-100 text-red-800 rounded-full"
                >
                  Required
                </span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2 mt-3">
                <div
                  class="bg-blue-600 h-2 rounded-full transition-all duration-500"
                  :style="{ width: calculateRequirementProgress(requirement) + '%' }"
                ></div>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-8 text-gray-500">
            <p>No specific requirements defined for this program</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const authStore = useAuthStore()

const progress = ref(null)
const loading = ref(false)

const circumference = 2 * Math.PI * 88 // radius = 88

const loadProgress = async () => {
  try {
    loading.value = true
    const response = await api.getStudentProgress(authStore.userId)
    progress.value = response.data
  } catch (error) {
    console.error('Error loading degree progress:', error)
    progress.value = null
  } finally {
    loading.value = false
  }
}

const getStatusClass = (status) => {
  const classes = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'ON_HOLD': 'bg-yellow-100 text-yellow-800',
    'COMPLETED': 'bg-blue-100 text-blue-800',
    'WITHDRAWN': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const calculateRequirementProgress = (requirement) => {
  // This would ideally come from the backend
  // For now, return a placeholder based on overall progress
  return Math.min(100, progress.value.completionPercentage)
}

onMounted(() => {
  loadProgress()
})
</script>
