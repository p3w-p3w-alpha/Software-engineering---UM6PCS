<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">My Assignments</h1>
      <p class="mt-2 text-gray-600">View and submit your course assignments</p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-5 gap-4 mb-8">
      <div
        v-for="stat in stats"
        :key="stat.id"
        @click="activeTab = stat.id"
        class="bg-white rounded-xl shadow-sm border-2 p-4 cursor-pointer transition-all hover:shadow-md"
        :class="[activeTab === stat.id ? stat.borderClass : 'border-transparent']"
      >
        <div class="flex items-center">
          <div class="p-3 rounded-lg" :class="stat.bgClass">
            <component :is="stat.icon" class="w-6 h-6" :class="stat.iconClass" />
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500">{{ stat.label }}</p>
            <p class="text-2xl font-bold" :class="stat.textClass">{{ stat.count }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Course</label>
          <select
            v-model="filters.courseId"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">All Courses</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.courseCode || course.code }} - {{ course.courseName || course.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Due Date</label>
          <select
            v-model="filters.dueDate"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">Any Time</option>
            <option value="today">Due Today</option>
            <option value="week">Due This Week</option>
            <option value="month">Due This Month</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Search</label>
          <div class="relative">
            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              v-model="filters.search"
              type="text"
              placeholder="Search assignments..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
        </div>
        <div class="flex items-end">
          <button
            @click="resetFilters"
            class="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
          >
            Reset Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredAssignments.length === 0" class="bg-white rounded-xl shadow-sm border border-gray-100 p-12 text-center">
      <svg class="mx-auto h-16 w-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No assignments found</h3>
      <p class="mt-2 text-gray-500">
        {{ activeTab === 'all' ? 'No assignments available.' : `No ${activeTab} assignments.` }}
      </p>
    </div>

    <!-- Assignments List -->
    <div v-else class="space-y-4">
      <div
        v-for="assignment in filteredAssignments"
        :key="assignment.id"
        class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow"
      >
        <div class="p-6">
          <div class="flex items-start gap-6">
            <!-- Status Indicator -->
            <div class="flex-shrink-0">
              <div
                class="w-12 h-12 rounded-xl flex items-center justify-center"
                :class="getStatusBgClass(assignment)"
              >
                <component :is="getStatusIcon(assignment)" class="w-6 h-6" :class="getStatusIconClass(assignment)" />
              </div>
            </div>

            <!-- Assignment Content -->
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between mb-2">
                <div>
                  <div class="flex items-center gap-3 mb-1">
                    <h3 class="text-lg font-semibold text-gray-900">{{ assignment.title }}</h3>
                    <span
                      class="px-2 py-1 text-xs font-semibold rounded-full"
                      :class="getStatusClass(assignment)"
                    >
                      {{ getStatusText(assignment) }}
                    </span>
                  </div>
                  <p class="text-sm text-gray-500">
                    {{ assignment.course?.courseCode || assignment.course?.code }} - {{ assignment.course?.courseName || assignment.course?.name }}
                  </p>
                </div>

                <!-- Grade Display (if graded) -->
                <div v-if="assignment.submission && assignment.submission.grade !== null && assignment.submission.grade !== undefined" class="text-right">
                  <div class="text-3xl font-bold" :class="getGradeColor(assignment.submission.grade, assignment.maxGrade)">
                    {{ assignment.submission.grade }}
                  </div>
                  <div class="text-sm text-gray-500">/ {{ assignment.maxGrade }} points</div>
                </div>
              </div>

              <!-- Description -->
              <p class="text-gray-600 text-sm mb-4 line-clamp-2">{{ assignment.description }}</p>

              <!-- Meta Info -->
              <div class="flex flex-wrap items-center gap-4 text-sm mb-4">
                <div class="flex items-center text-gray-500">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span :class="getDueDateClass(assignment)">
                    Due: {{ formatDate(assignment.dueDate) }}
                  </span>
                </div>
                <div class="flex items-center text-gray-500">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                  </svg>
                  Max: {{ assignment.maxGrade }} pts
                </div>
                <div v-if="assignment.submission" class="flex items-center text-green-600">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  Submitted: {{ formatDate(assignment.submission.submittedAt) }}
                </div>
                <div v-if="getTimeRemaining(assignment)" class="flex items-center" :class="getTimeRemainingClass(assignment)">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {{ getTimeRemaining(assignment) }}
                </div>
              </div>

              <!-- Faculty Feedback -->
              <div v-if="assignment.submission?.feedback" class="p-3 bg-blue-50 rounded-lg mb-4">
                <div class="flex items-start gap-2">
                  <svg class="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                  </svg>
                  <div>
                    <p class="text-sm font-medium text-blue-900">Instructor Feedback</p>
                    <p class="text-sm text-blue-700 mt-1">{{ assignment.submission.feedback }}</p>
                  </div>
                </div>
              </div>

              <!-- Progress Bar (for submitted but ungraded) -->
              <div v-if="assignment.submission && assignment.submission.grade === null" class="mb-4">
                <div class="flex items-center justify-between text-sm mb-1">
                  <span class="text-gray-500">Grading Status</span>
                  <span class="text-amber-600 font-medium">Awaiting Review</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div class="bg-amber-500 h-2 rounded-full w-1/2 animate-pulse"></div>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex flex-wrap gap-2">
                <router-link
                  v-if="!assignment.submission && !isPastDue(assignment)"
                  :to="`/student/assignments/${assignment.id}/submit`"
                  class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition-colors"
                >
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                  </svg>
                  Submit Assignment
                </router-link>
                <router-link
                  v-if="assignment.submission"
                  :to="`/student/submissions/${assignment.submission.id}`"
                  class="inline-flex items-center px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                >
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  View Submission
                </router-link>
                <button
                  v-if="isPastDue(assignment) && !assignment.submission"
                  class="inline-flex items-center px-4 py-2 bg-red-100 text-red-700 text-sm font-medium rounded-lg cursor-not-allowed"
                  disabled
                >
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Past Due
                </button>
                <button
                  @click="viewDetails(assignment)"
                  class="inline-flex items-center px-4 py-2 border border-gray-300 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Details
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Assignment Details Modal -->
    <div
      v-if="showDetailsModal && selectedAssignment"
      class="fixed inset-0 z-50 overflow-y-auto"
      @click.self="showDetailsModal = false"
    >
      <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>

        <div class="relative bg-white rounded-xl shadow-xl max-w-2xl w-full mx-auto z-10">
          <!-- Modal Header -->
          <div class="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-4 rounded-t-xl">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-indigo-200 text-sm">{{ selectedAssignment.course?.courseCode || selectedAssignment.course?.code }}</p>
                <h2 class="text-xl font-bold text-white">{{ selectedAssignment.title }}</h2>
              </div>
              <button @click="showDetailsModal = false" class="text-white hover:text-indigo-200">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Modal Body -->
          <div class="p-6 space-y-6">
            <!-- Description -->
            <div>
              <h4 class="text-sm font-medium text-gray-500 mb-2">Description</h4>
              <p class="text-gray-900">{{ selectedAssignment.description || 'No description provided.' }}</p>
            </div>

            <!-- Info Grid -->
            <div class="grid grid-cols-2 gap-4">
              <div class="p-4 bg-gray-50 rounded-lg">
                <p class="text-sm text-gray-500">Due Date</p>
                <p class="text-lg font-semibold" :class="getDueDateClass(selectedAssignment)">
                  {{ formatDate(selectedAssignment.dueDate) }}
                </p>
              </div>
              <div class="p-4 bg-gray-50 rounded-lg">
                <p class="text-sm text-gray-500">Maximum Points</p>
                <p class="text-lg font-semibold text-gray-900">{{ selectedAssignment.maxGrade }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-lg">
                <p class="text-sm text-gray-500">Status</p>
                <span
                  class="inline-flex px-2 py-1 text-sm font-semibold rounded-full"
                  :class="getStatusClass(selectedAssignment)"
                >
                  {{ getStatusText(selectedAssignment) }}
                </span>
              </div>
              <div v-if="selectedAssignment.submission && selectedAssignment.submission.grade !== null && selectedAssignment.submission.grade !== undefined" class="p-4 bg-gray-50 rounded-lg">
                <p class="text-sm text-gray-500">Your Grade</p>
                <p class="text-lg font-semibold" :class="getGradeColor(selectedAssignment.submission.grade, selectedAssignment.maxGrade)">
                  {{ selectedAssignment.submission.grade }} / {{ selectedAssignment.maxGrade }}
                </p>
              </div>
            </div>

            <!-- Attachments -->
            <div v-if="selectedAssignment.attachments && selectedAssignment.attachments.length > 0">
              <h4 class="text-sm font-medium text-gray-500 mb-2">Attachments</h4>
              <div class="space-y-2">
                <a
                  v-for="(file, index) in selectedAssignment.attachments"
                  :key="index"
                  :href="file.url"
                  target="_blank"
                  class="flex items-center gap-2 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span class="text-gray-900">{{ file.name }}</span>
                </a>
              </div>
            </div>

            <!-- Feedback -->
            <div v-if="selectedAssignment.submission?.feedback">
              <h4 class="text-sm font-medium text-gray-500 mb-2">Instructor Feedback</h4>
              <div class="p-4 bg-blue-50 rounded-lg">
                <p class="text-blue-800">{{ selectedAssignment.submission.feedback }}</p>
              </div>
            </div>
          </div>

          <!-- Modal Footer -->
          <div class="px-6 py-4 bg-gray-50 rounded-b-xl flex justify-end gap-3">
            <button
              @click="showDetailsModal = false"
              class="px-4 py-2 text-gray-700 hover:bg-gray-200 rounded-lg transition-colors"
            >
              Close
            </button>
            <router-link
              v-if="!selectedAssignment.submission && !isPastDue(selectedAssignment)"
              :to="`/student/assignments/${selectedAssignment.id}/submit`"
              class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
            >
              Submit Now
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'

const authStore = useAuthStore()

// State
const assignments = ref([])
const courses = ref([])
const loading = ref(true)
const showDetailsModal = ref(false)
const selectedAssignment = ref(null)
const activeTab = ref('all')

const filters = ref({
  courseId: '',
  dueDate: '',
  search: ''
})

// SVG Icon Components
const ClipboardIcon = {
  render() {
    return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' })
    ])
  }
}

const ClockIcon = {
  render() {
    return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z' })
    ])
  }
}

const CheckCircleIcon = {
  render() {
    return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z' })
    ])
  }
}

const StarIcon = {
  render() {
    return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z' })
    ])
  }
}

const ExclamationIcon = {
  render() {
    return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z' })
    ])
  }
}

// Computed
const pendingAssignments = computed(() =>
  assignments.value.filter(a => !a.submission && !isPastDue(a))
)

const submittedAssignments = computed(() =>
  assignments.value.filter(a => a.submission && a.submission.grade === null)
)

const gradedAssignments = computed(() =>
  assignments.value.filter(a => a.submission?.grade !== null && a.submission?.grade !== undefined)
)

const overdueAssignments = computed(() =>
  assignments.value.filter(a => !a.submission && isPastDue(a))
)

const stats = computed(() => [
  {
    id: 'all',
    label: 'All',
    count: assignments.value.length,
    icon: ClipboardIcon,
    bgClass: 'bg-gray-100',
    iconClass: 'text-gray-600',
    textClass: 'text-gray-900',
    borderClass: 'border-gray-400'
  },
  {
    id: 'pending',
    label: 'Pending',
    count: pendingAssignments.value.length,
    icon: ClockIcon,
    bgClass: 'bg-amber-100',
    iconClass: 'text-amber-600',
    textClass: 'text-amber-600',
    borderClass: 'border-amber-400'
  },
  {
    id: 'submitted',
    label: 'Submitted',
    count: submittedAssignments.value.length,
    icon: CheckCircleIcon,
    bgClass: 'bg-blue-100',
    iconClass: 'text-blue-600',
    textClass: 'text-blue-600',
    borderClass: 'border-blue-400'
  },
  {
    id: 'graded',
    label: 'Graded',
    count: gradedAssignments.value.length,
    icon: StarIcon,
    bgClass: 'bg-green-100',
    iconClass: 'text-green-600',
    textClass: 'text-green-600',
    borderClass: 'border-green-400'
  },
  {
    id: 'overdue',
    label: 'Overdue',
    count: overdueAssignments.value.length,
    icon: ExclamationIcon,
    bgClass: 'bg-red-100',
    iconClass: 'text-red-600',
    textClass: 'text-red-600',
    borderClass: 'border-red-400'
  }
])

const filteredAssignments = computed(() => {
  let result = [...assignments.value]

  // Filter by tab
  switch (activeTab.value) {
    case 'pending':
      result = pendingAssignments.value
      break
    case 'submitted':
      result = submittedAssignments.value
      break
    case 'graded':
      result = gradedAssignments.value
      break
    case 'overdue':
      result = overdueAssignments.value
      break
  }

  // Filter by course
  if (filters.value.courseId) {
    result = result.filter(a => a.course?.id === filters.value.courseId)
  }

  // Filter by due date
  if (filters.value.dueDate) {
    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const endOfWeek = new Date(today)
    endOfWeek.setDate(today.getDate() + (7 - today.getDay()))
    const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0)

    result = result.filter(a => {
      const dueDate = new Date(a.dueDate)
      switch (filters.value.dueDate) {
        case 'today':
          return dueDate >= today && dueDate < new Date(today.getTime() + 24 * 60 * 60 * 1000)
        case 'week':
          return dueDate >= today && dueDate <= endOfWeek
        case 'month':
          return dueDate >= today && dueDate <= endOfMonth
        default:
          return true
      }
    })
  }

  // Filter by search
  if (filters.value.search) {
    const searchLower = filters.value.search.toLowerCase()
    result = result.filter(a =>
      a.title.toLowerCase().includes(searchLower) ||
      (a.description && a.description.toLowerCase().includes(searchLower)) ||
      (a.course?.courseCode || a.course?.code || '').toLowerCase().includes(searchLower)
    )
  }

  // Sort by due date
  result.sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))

  return result
})

// Methods
const loadCourses = async () => {
  try {
    const enrollmentsRes = await api.getStudentEnrollments(authStore.userId)
    const uniqueCourses = (enrollmentsRes.data || [])
      .filter(e => e.course && e.status === 'ACTIVE')
      .map(e => e.course)
      .filter((course, index, self) =>
        index === self.findIndex(c => c.id === course.id)
      )
    courses.value = uniqueCourses
  } catch (error) {
    console.error('Error loading courses:', error)
  }
}

const loadAssignments = async () => {
  try {
    loading.value = true
    const response = await api.getStudentAssignments(authStore.userId)
    const assignmentsData = response.data || []

    // Get student submissions
    const submissionsRes = await api.getStudentSubmissions(authStore.userId)
    const submissions = submissionsRes.data || []

    // Map submissions to assignments
    assignments.value = assignmentsData.map(assignment => {
      const submission = submissions.find(s => s.assignment?.id === assignment.id)
      return {
        ...assignment,
        submission: submission || null
      }
    })
  } catch (error) {
    console.error('Error loading assignments:', error)
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value = {
    courseId: '',
    dueDate: '',
    search: ''
  }
  activeTab.value = 'all'
}

const viewDetails = (assignment) => {
  selectedAssignment.value = assignment
  showDetailsModal.value = true
}

const isPastDue = (assignment) => {
  return new Date(assignment.dueDate) < new Date()
}

const getStatusText = (assignment) => {
  const hasSubmission = !!assignment.submission
  const isGraded = hasSubmission && assignment.submission?.grade !== null && assignment.submission?.grade !== undefined

  if (isGraded) return 'Graded'
  if (hasSubmission) return 'Submitted'
  if (isPastDue(assignment)) return 'Overdue'
  return 'Pending'
}

const getStatusClass = (assignment) => {
  const status = getStatusText(assignment)
  const classes = {
    'Graded': 'bg-green-100 text-green-700',
    'Submitted': 'bg-blue-100 text-blue-700',
    'Overdue': 'bg-red-100 text-red-700',
    'Pending': 'bg-amber-100 text-amber-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

const getStatusBgClass = (assignment) => {
  const status = getStatusText(assignment)
  const classes = {
    'Graded': 'bg-green-100',
    'Submitted': 'bg-blue-100',
    'Overdue': 'bg-red-100',
    'Pending': 'bg-amber-100'
  }
  return classes[status] || 'bg-gray-100'
}

const getStatusIconClass = (assignment) => {
  const status = getStatusText(assignment)
  const classes = {
    'Graded': 'text-green-600',
    'Submitted': 'text-blue-600',
    'Overdue': 'text-red-600',
    'Pending': 'text-amber-600'
  }
  return classes[status] || 'text-gray-600'
}

const getStatusIcon = (assignment) => {
  const status = getStatusText(assignment)
  const icons = {
    'Graded': StarIcon,
    'Submitted': CheckCircleIcon,
    'Overdue': ExclamationIcon,
    'Pending': ClockIcon
  }
  return icons[status] || ClipboardIcon
}

const getDueDateClass = (assignment) => {
  if (assignment.submission) return 'text-gray-900'
  const now = new Date()
  const dueDate = new Date(assignment.dueDate)
  const hoursUntilDue = (dueDate - now) / (1000 * 60 * 60)

  if (hoursUntilDue < 0) return 'text-red-600 font-bold'
  if (hoursUntilDue < 24) return 'text-orange-600 font-semibold'
  if (hoursUntilDue < 72) return 'text-amber-600'
  return 'text-gray-900'
}

const getTimeRemaining = (assignment) => {
  if (assignment.submission) return null

  const now = new Date()
  const dueDate = new Date(assignment.dueDate)
  const diff = dueDate - now

  if (diff < 0) return null

  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(hours / 24)

  if (days > 0) return `${days} day${days > 1 ? 's' : ''} left`
  if (hours > 0) return `${hours} hour${hours > 1 ? 's' : ''} left`
  return 'Due soon'
}

const getTimeRemainingClass = (assignment) => {
  const now = new Date()
  const dueDate = new Date(assignment.dueDate)
  const hours = (dueDate - now) / (1000 * 60 * 60)

  if (hours < 24) return 'text-red-600 font-semibold'
  if (hours < 72) return 'text-amber-600'
  return 'text-green-600'
}

const getGradeColor = (grade, maxGrade) => {
  const percentage = (grade / maxGrade) * 100
  if (percentage >= 90) return 'text-green-600'
  if (percentage >= 80) return 'text-blue-600'
  if (percentage >= 70) return 'text-amber-600'
  if (percentage >= 60) return 'text-orange-600'
  return 'text-red-600'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(async () => {
  await loadCourses()
  await loadAssignments()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
