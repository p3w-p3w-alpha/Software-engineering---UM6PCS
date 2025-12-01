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
      <h1 class="text-3xl font-bold text-gray-900">Browse Courses</h1>
      <p class="mt-2 text-gray-600">Find and enroll in courses for the current semester</p>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <input
          v-model="filters.search"
          type="text"
          placeholder="Search courses..."
          class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
        />
        <select
          v-model="filters.department"
          class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">All Departments</option>
          <option v-for="dept in departments" :key="dept" :value="dept">{{ dept }}</option>
        </select>
        <select
          v-model="filters.credits"
          class="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">All Credits</option>
          <option value="1">1 Credit</option>
          <option value="2">2 Credits</option>
          <option value="3">3 Credits</option>
          <option value="4">4 Credits</option>
        </select>
        <button
          @click="resetFilters"
          class="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200"
        >
          Reset Filters
        </button>
      </div>
    </div>

    <!-- Courses Grid -->
    <div v-if="loading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <div v-else-if="filteredCourses.length === 0" class="bg-white rounded-lg shadow p-12 text-center">
      <p class="text-gray-500">No courses found matching your criteria</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="course in filteredCourses"
        :key="course.id"
        class="bg-white rounded-lg shadow hover:shadow-lg transition-shadow border border-gray-200"
      >
        <div class="p-6">
          <div class="flex items-start justify-between mb-3">
            <div>
              <h3 class="text-lg font-semibold text-gray-900">{{ course.courseCode }}</h3>
              <p class="text-sm text-gray-600">{{ course.courseName }}</p>
            </div>
            <span class="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
              {{ course.credits || course.creditHours }} Credits
            </span>
          </div>

          <div class="space-y-2 text-sm text-gray-600 mb-4">
            <div class="flex items-center">
              <svg class="h-4 w-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              {{ course.capacity - course.enrolled }} / {{ course.capacity }} seats available
            </div>
            <div v-if="course.daysOfWeek" class="flex items-center">
              <svg class="h-4 w-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              {{ course.daysOfWeek }} {{ course.startTime }}-{{ course.endTime }}
            </div>
            <div class="flex items-center">
              <svg class="h-4 w-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Fee: ${{ course.courseFee }}
            </div>
          </div>

          <div v-if="course.prerequisites && course.prerequisites.length > 0" class="mb-4">
            <p class="text-xs text-gray-500 mb-1">Prerequisites:</p>
            <div class="flex flex-wrap gap-1">
              <span
                v-for="prereq in course.prerequisites"
                :key="prereq.id"
                class="px-2 py-0.5 text-xs bg-gray-100 text-gray-700 rounded"
              >
                {{ prereq.courseCode }}
              </span>
            </div>
          </div>

          <button
            @click="enrollInCourse(course)"
            :disabled="course.enrolled >= course.capacity || isEnrolled(course.id) || enrolling"
            class="w-full px-4 py-2 rounded-md text-sm font-medium transition-colors"
            :class="[
              isEnrolled(course.id)
                ? 'bg-gray-300 text-gray-600 cursor-not-allowed'
                : course.enrolled >= course.capacity
                ? 'bg-yellow-600 text-white hover:bg-yellow-700'
                : 'bg-blue-600 text-white hover:bg-blue-700'
            ]"
          >
            {{
              isEnrolled(course.id)
                ? 'Already Enrolled'
                : course.enrolled >= course.capacity
                ? 'Join Waitlist'
                : 'Enroll Now'
            }}
          </button>
        </div>
      </div>
    </div>

    <!-- Enrollment Confirmation Modal -->
    <Modal
      v-model="showEnrollModal"
      title="Confirm Enrollment"
      confirm-text="Enroll"
      @confirm="confirmEnrollment"
    >
      <div v-if="selectedCourse">
        <p class="text-sm text-gray-600 mb-4">
          You are about to enroll in:
        </p>
        <div class="bg-gray-50 rounded-lg p-4 mb-4">
          <h4 class="font-semibold text-gray-900">{{ selectedCourse.courseCode }} - {{ selectedCourse.courseName }}</h4>
          <p class="text-sm text-gray-600 mt-2">{{ selectedCourse.credits || selectedCourse.creditHours }} Credits • Fee: ${{ selectedCourse.courseFee }}</p>
          <p v-if="selectedCourse.daysOfWeek" class="text-sm text-gray-600">
            {{ selectedCourse.daysOfWeek }} {{ selectedCourse.startTime }}-{{ selectedCourse.endTime }}
          </p>
        </div>
        <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4">
          <div class="flex">
            <svg class="h-5 w-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <div class="ml-3">
              <p class="text-sm text-yellow-700">
                Your enrollment will be in <strong>PENDING_PAYMENT</strong> status. You must complete payment for it to become active.
              </p>
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
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import Modal from '../../components/Modal.vue'

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

const courses = ref([])
const myEnrollments = ref([])
const loading = ref(false)
const enrolling = ref(false)
const showEnrollModal = ref(false)
const selectedCourse = ref(null)

const filters = ref({
  search: '',
  department: '',
  credits: ''
})

const departments = ref(['Computer Science', 'Mathematics', 'Physics', 'Engineering', 'Business'])

const filteredCourses = computed(() => {
  return courses.value.filter(course => {
    const matchesSearch = !filters.value.search ||
      course.courseCode.toLowerCase().includes(filters.value.search.toLowerCase()) ||
      course.courseName.toLowerCase().includes(filters.value.search.toLowerCase())

    // Department filter - only apply if course has department field
    const matchesDepartment = !filters.value.department ||
      !course.department ||
      course.department === filters.value.department

    // Credits filter - check both possible field names
    const courseCredits = course.credits || course.creditHours
    const matchesCredits = !filters.value.credits ||
      courseCredits === parseInt(filters.value.credits)

    return matchesSearch && matchesDepartment && matchesCredits
  })
})

const isEnrolled = (courseId) => {
  return myEnrollments.value.some(e => e.course.id === courseId)
}

const loadCourses = async () => {
  try {
    loading.value = true
    const response = await api.getAllCourses()
    courses.value = response.data.map(course => ({
      ...course,
      enrolled: course.activeEnrollmentCount || 0
    }))
  } catch (error) {
    console.error('Error loading courses:', error)
  } finally {
    loading.value = false
  }
}

const loadMyEnrollments = async () => {
  try {
    const response = await api.getStudentEnrollments(authStore.userId)
    myEnrollments.value = response.data
  } catch (error) {
    console.error('Error loading enrollments:', error)
  }
}

const enrollInCourse = (course) => {
  selectedCourse.value = course
  showEnrollModal.value = true
}

const confirmEnrollment = async () => {
  try {
    enrolling.value = true
    await api.createEnrollment({
      studentId: authStore.userId,
      courseId: selectedCourse.value.id
    })

    // Reload data
    await loadMyEnrollments()
    await loadCourses()

    showEnrollModal.value = false
    showNotification('Enrollment successful! Please proceed to payment.', 'success')
    router.push('/student/payments')
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.response?.data || 'Enrollment failed'
    showNotification(errorMessage, 'error')
  } finally {
    enrolling.value = false
  }
}

const resetFilters = () => {
  filters.value = {
    search: '',
    department: '',
    credits: ''
  }
}

onMounted(() => {
  loadCourses()
  loadMyEnrollments()
})
</script>
