<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">Faculty Dashboard</h1>
            <p class="text-sm text-gray-600 mt-1">Welcome, <span class="font-semibold">{{ authStore.userName }}</span></p>
          </div>
          <button
            @click="handleLogout"
            class="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
            </svg>
            Logout
          </button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">My Courses</div>
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ courses.length }}</div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Total Students</div>
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ totalStudents }}</div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Assignments</div>
            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ assignments.length }}</div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-gray-600">Pending Grades</div>
            <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
          </div>
          <div class="text-3xl font-bold text-gray-900">{{ pendingGrades }}</div>
        </div>
      </div>

      <!-- Tabs Section -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="border-b border-gray-200">
          <nav class="flex -mb-px">
            <button
              @click="activeTab = 'courses'"
              :class="activeTab === 'courses' ? 'border-primary-500 text-primary-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
              class="px-6 py-4 border-b-2 font-medium text-sm transition-colors"
            >
              My Courses
            </button>
            <button
              @click="activeTab = 'assignments'"
              :class="activeTab === 'assignments' ? 'border-primary-500 text-primary-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
              class="px-6 py-4 border-b-2 font-medium text-sm transition-colors"
            >
              Assignments
            </button>
            <button
              @click="activeTab = 'students'"
              :class="activeTab === 'students' ? 'border-primary-500 text-primary-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
              class="px-6 py-4 border-b-2 font-medium text-sm transition-colors"
            >
              Students
            </button>
          </nav>
        </div>

        <div class="p-6">
          <!-- Courses Tab -->
          <div v-if="activeTab === 'courses'">
            <div class="flex justify-between items-center mb-6">
              <div>
                <h2 class="text-xl font-semibold text-gray-900">Courses I Teach</h2>
                <p class="text-sm text-gray-600 mt-1">Manage your courses and enrollments</p>
              </div>
              <button
                @click="openCreateCourseModal"
                class="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition-colors flex items-center gap-2"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                Create Course
              </button>
            </div>

            <div v-if="courses.length === 0" class="text-gray-500 text-center py-12 border border-gray-200 rounded-lg">
              No courses assigned yet
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div
                v-for="course in courses"
                :key="course.id"
                class="border border-gray-200 rounded-lg p-5 hover:border-primary-300 hover:shadow-sm transition-all"
              >
                <div class="flex justify-between items-start mb-3">
                  <div>
                    <h3 class="font-semibold text-gray-900 text-lg">{{ course.name }}</h3>
                    <p class="text-sm text-gray-600 mt-1">{{ course.code }}</p>
                  </div>
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                    {{ course.credits }} credits
                  </span>
                </div>
                <p class="text-sm text-gray-600 mb-4">{{ course.description }}</p>
                <div class="flex justify-between items-center text-sm">
                  <div class="flex items-center text-gray-500">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
                    </svg>
                    <span>{{ getCourseEnrollmentCount(course.id) }} students</span>
                  </div>
                  <button
                    @click="viewCourseDetails(course)"
                    class="text-primary-600 hover:text-primary-800 font-medium transition-colors"
                  >
                    View Details
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Assignments Tab -->
          <div v-if="activeTab === 'assignments'">
            <div class="flex justify-between items-center mb-6">
              <div>
                <h2 class="text-xl font-semibold text-gray-900">My Assignments</h2>
                <p class="text-sm text-gray-600 mt-1">Create and manage course assignments</p>
              </div>
              <button
                @click="openCreateAssignmentModal"
                class="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition-colors flex items-center gap-2"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                Create Assignment
              </button>
            </div>

            <div v-if="assignments.length === 0" class="text-gray-500 text-center py-12 border border-gray-200 rounded-lg">
              No assignments created yet
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="assignment in assignments"
                :key="assignment.id"
                class="border border-gray-200 rounded-lg p-4 hover:border-primary-300 hover:shadow-sm transition-all"
              >
                <div class="flex justify-between items-start">
                  <div class="flex-1">
                    <h3 class="font-semibold text-gray-900">{{ assignment.title }}</h3>
                    <p class="text-sm text-gray-600 mt-1">{{ assignment.description }}</p>
                    <div class="mt-3 flex items-center gap-4 text-xs text-gray-500">
                      <span class="flex items-center">
                        <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
                        </svg>
                        {{ assignment.courseName || assignment.courseCode }}
                      </span>
                      <span class="flex items-center">
                        <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                        </svg>
                        Due: {{ formatDate(assignment.dueDate) }}
                      </span>
                      <span>Max Grade: {{ assignment.maxGrade }}</span>
                    </div>
                  </div>
                  <button
                    @click="viewAssignmentSubmissions(assignment)"
                    class="ml-4 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg text-sm font-medium transition-colors"
                  >
                    View Submissions
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Students Tab -->
          <div v-if="activeTab === 'students'">
            <div class="mb-6">
              <h2 class="text-xl font-semibold text-gray-900 mb-2">Enrolled Students</h2>
              <p class="text-sm text-gray-600 mb-4">View students enrolled in your courses</p>
              <select
                v-model="selectedCourseId"
                @change="loadCourseStudents"
                class="w-full md:w-64 px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
              >
                <option value="">Select a course</option>
                <option v-for="course in courses" :key="course.id" :value="course.id">
                  {{ course.code }} - {{ course.name }}
                </option>
              </select>
            </div>

            <div v-if="!selectedCourseId" class="text-gray-500 text-center py-12 border border-gray-200 rounded-lg">
              Please select a course to view enrolled students
            </div>
            <div v-else-if="courseStudents.length === 0" class="text-gray-500 text-center py-12 border border-gray-200 rounded-lg">
              No students enrolled in this course
            </div>
            <div v-else class="overflow-x-auto border border-gray-200 rounded-lg">
              <table class="w-full">
                <thead class="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Student ID</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Name</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Email</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Status</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="student in courseStudents" :key="student.id" class="hover:bg-gray-50 transition-colors">
                    <td class="px-6 py-4 text-sm text-gray-900">{{ student.studentId }}</td>
                    <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ student.firstName }} {{ student.lastName }}</td>
                    <td class="px-6 py-4 text-sm text-gray-600">{{ student.email }}</td>
                    <td class="px-6 py-4">
                      <span :class="getEnrollmentStatusClass(student.enrollmentStatus)"
                            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
                        {{ student.enrollmentStatus }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-sm">
                      <button
                        @click="viewStudentGrades(student)"
                        class="text-primary-600 hover:text-primary-800 font-medium transition-colors"
                      >
                        View Grades
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Create Course Modal -->
    <transition name="modal">
      <div v-if="showCourseModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 px-4" @click.self="closeCourseModal">
        <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full">
          <div class="bg-primary-600 px-6 py-5 rounded-t-2xl">
            <h3 class="text-xl font-semibold text-white">Create New Course</h3>
          </div>
          <form @submit.prevent="saveCourse" class="p-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Course Code</label>
              <input
                v-model="courseFormData.code"
                type="text"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
                placeholder="e.g., CS101"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Course Name</label>
              <input
                v-model="courseFormData.name"
                type="text"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
                placeholder="Enter course name"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
              <textarea
                v-model="courseFormData.description"
                rows="3"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
                placeholder="Course description"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Credits</label>
              <input
                v-model.number="courseFormData.credits"
                type="number"
                required
                min="1"
                max="6"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
              />
            </div>
            <div class="flex gap-3 pt-4">
              <button
                type="submit"
                class="flex-1 px-4 py-2.5 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition-colors"
              >
                Create Course
              </button>
              <button
                type="button"
                @click="closeCourseModal"
                class="flex-1 px-4 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-lg transition-colors"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Create Assignment Modal -->
    <transition name="modal">
      <div v-if="showAssignmentModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 px-4" @click.self="closeAssignmentModal">
        <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full">
          <div class="bg-primary-600 px-6 py-5 rounded-t-2xl">
            <h3 class="text-xl font-semibold text-white">Create New Assignment</h3>
          </div>
          <form @submit.prevent="saveAssignment" class="p-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Course</label>
              <select
                v-model="assignmentFormData.courseId"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
              >
                <option value="">Select Course</option>
                <option v-for="course in courses" :key="course.id" :value="course.id">
                  {{ course.code }} - {{ course.name }}
                </option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Title</label>
              <input
                v-model="assignmentFormData.title"
                type="text"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
                placeholder="Assignment title"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
              <textarea
                v-model="assignmentFormData.description"
                rows="3"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
                placeholder="Assignment description"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Due Date</label>
              <input
                v-model="assignmentFormData.dueDate"
                type="date"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Max Grade</label>
              <input
                v-model.number="assignmentFormData.maxGrade"
                type="number"
                required
                min="1"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
              />
            </div>
            <div class="flex gap-3 pt-4">
              <button
                type="submit"
                class="flex-1 px-4 py-2.5 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition-colors"
              >
                Create Assignment
              </button>
              <button
                type="button"
                @click="closeAssignmentModal"
                class="flex-1 px-4 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-lg transition-colors"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('courses')
const courses = ref([])
const assignments = ref([])
const courseStudents = ref([])
const selectedCourseId = ref('')
const enrollmentsByCourse = ref({})

const showCourseModal = ref(false)
const showAssignmentModal = ref(false)

const courseFormData = ref({
  code: '',
  name: '',
  description: '',
  credits: 3
})

const assignmentFormData = ref({
  courseId: '',
  title: '',
  description: '',
  dueDate: '',
  maxGrade: 100
})

const totalStudents = computed(() => {
  return Object.values(enrollmentsByCourse.value).reduce((sum, enrollments) => sum + enrollments.length, 0)
})

const pendingGrades = computed(() => {
  return 0 // Placeholder - would need submission data
})

onMounted(async () => {
  await loadFacultyData()
})

async function loadFacultyData() {
  try {
    const facultyId = authStore.userId

    // Load courses - For now using all courses, ideally filter by faculty
    const coursesResponse = await api.getAllCourses()
    courses.value = coursesResponse.data

    // Load assignments
    const assignmentsResponse = await api.getFacultyAssignments(facultyId)
    assignments.value = assignmentsResponse.data

    // Load enrollments for each course
    for (const course of courses.value) {
      try {
        const enrollmentsResponse = await api.getCourseEnrollments(course.id)
        enrollmentsByCourse.value[course.id] = enrollmentsResponse.data
      } catch (error) {
        console.error(`Error loading enrollments for course ${course.id}:`, error)
        enrollmentsByCourse.value[course.id] = []
      }
    }
  } catch (error) {
    console.error('Error loading faculty data:', error)
  }
}

function getCourseEnrollmentCount(courseId) {
  return enrollmentsByCourse.value[courseId]?.length || 0
}

async function loadCourseStudents() {
  if (!selectedCourseId.value) {
    courseStudents.value = []
    return
  }

  try {
    const response = await api.getCourseEnrollments(selectedCourseId.value)
    courseStudents.value = response.data
  } catch (error) {
    console.error('Error loading course students:', error)
    courseStudents.value = []
  }
}

function openCreateCourseModal() {
  courseFormData.value = {
    code: '',
    name: '',
    description: '',
    credits: 3
  }
  showCourseModal.value = true
}

function closeCourseModal() {
  showCourseModal.value = false
}

async function saveCourse() {
  try {
    const courseData = {
      ...courseFormData.value,
      facultyId: authStore.userId
    }
    await api.createCourse(courseData)
    await loadFacultyData()
    closeCourseModal()
  } catch (error) {
    console.error('Error creating course:', error)
    alert('Failed to create course')
  }
}

function openCreateAssignmentModal() {
  assignmentFormData.value = {
    courseId: '',
    title: '',
    description: '',
    dueDate: '',
    maxGrade: 100
  }
  showAssignmentModal.value = true
}

function closeAssignmentModal() {
  showAssignmentModal.value = false
}

async function saveAssignment() {
  try {
    const assignmentData = {
      ...assignmentFormData.value,
      facultyId: authStore.userId
    }
    await api.createAssignment(assignmentData)
    await loadFacultyData()
    closeAssignmentModal()
  } catch (error) {
    console.error('Error creating assignment:', error)
    alert('Failed to create assignment')
  }
}

function viewCourseDetails(course) {
  // Placeholder - would navigate to detailed course view
  console.log('View course details:', course)
  alert(`Course details for ${course.name} - Feature coming soon`)
}

function viewAssignmentSubmissions(assignment) {
  // Placeholder - would navigate to submissions view
  console.log('View assignment submissions:', assignment)
  alert(`View submissions for ${assignment.title} - Feature coming soon`)
}

function viewStudentGrades(student) {
  // Placeholder - would navigate to student grade view
  console.log('View student grades:', student)
  alert(`View grades for ${student.firstName} ${student.lastName} - Feature coming soon`)
}

function getEnrollmentStatusClass(status) {
  const classes = {
    'ENROLLED': 'bg-green-100 text-green-800',
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'COMPLETED': 'bg-blue-100 text-blue-800',
    'DROPPED': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
