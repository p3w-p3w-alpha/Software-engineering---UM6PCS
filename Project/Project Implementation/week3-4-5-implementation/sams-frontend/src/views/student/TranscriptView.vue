<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Academic Transcript</h1>
            <p class="mt-2 text-sm text-gray-600">
              Your complete academic record and grade history
            </p>
          </div>

          <div class="flex items-center space-x-3">
            <button
              @click="printTranscript"
              class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors flex items-center"
            >
              <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
              </svg>
              Print
            </button>

            <button
              @click="exportPDF"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center"
            >
              <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              Export PDF
            </button>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Transcript Content -->
      <div v-else id="transcript-content" class="space-y-6">
        <!-- Student Information Card -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-start justify-between">
            <div class="flex items-start space-x-4">
              <!-- Student Avatar -->
              <div class="h-20 w-20 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-2xl font-semibold">
                {{ getInitials(studentInfo) }}
              </div>

              <!-- Student Details -->
              <div>
                <h2 class="text-2xl font-semibold text-gray-900">
                  {{ studentInfo.firstName }} {{ studentInfo.lastName }}
                </h2>
                <div class="mt-2 space-y-1">
                  <p class="text-sm text-gray-600">
                    <span class="font-medium">Student ID:</span> {{ studentInfo.username }}
                  </p>
                  <p class="text-sm text-gray-600">
                    <span class="font-medium">Email:</span> {{ studentInfo.email }}
                  </p>
                  <p class="text-sm text-gray-600">
                    <span class="font-medium">Department:</span> {{ studentInfo.department || 'Not specified' }}
                  </p>
                  <p class="text-sm text-gray-600">
                    <span class="font-medium">Enrollment Date:</span> {{ formatDate(studentInfo.createdAt) }}
                  </p>
                </div>
              </div>
            </div>

            <!-- Academic Summary -->
            <div class="text-right">
              <div class="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-lg p-4 border border-blue-200">
                <p class="text-sm text-gray-600 mb-2">Cumulative GPA</p>
                <div class="flex items-baseline justify-end space-x-2">
                  <span class="text-4xl font-bold" :class="getGPAColor(cumulativeGPA)">
                    {{ cumulativeGPA.toFixed(2) }}
                  </span>
                  <span class="text-lg text-gray-600">/ 4.0</span>
                </div>
                <p class="text-xs text-gray-500 mt-2">
                  {{ totalCredits }} Credits Earned
                </p>
              </div>

              <div class="mt-3">
                <span
                  class="inline-block px-3 py-1 rounded-full text-sm font-medium"
                  :class="getAcademicStandingClass(academicStanding)"
                >
                  {{ academicStanding }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Overall Statistics -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
            <div class="flex items-center">
              <div class="flex-shrink-0 p-3 bg-blue-100 rounded-lg">
                <svg class="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
              <div class="ml-4">
                <p class="text-sm text-gray-600">Total Courses</p>
                <p class="text-2xl font-semibold text-gray-900">{{ totalCourses }}</p>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
            <div class="flex items-center">
              <div class="flex-shrink-0 p-3 bg-green-100 rounded-lg">
                <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div class="ml-4">
                <p class="text-sm text-gray-600">Completed</p>
                <p class="text-2xl font-semibold text-gray-900">{{ completedCourses }}</p>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
            <div class="flex items-center">
              <div class="flex-shrink-0 p-3 bg-yellow-100 rounded-lg">
                <svg class="h-6 w-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div class="ml-4">
                <p class="text-sm text-gray-600">In Progress</p>
                <p class="text-2xl font-semibold text-gray-900">{{ inProgressCourses }}</p>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
            <div class="flex items-center">
              <div class="flex-shrink-0 p-3 bg-purple-100 rounded-lg">
                <svg class="h-6 w-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                </svg>
              </div>
              <div class="ml-4">
                <p class="text-sm text-gray-600">Total Credits</p>
                <p class="text-2xl font-semibold text-gray-900">{{ totalCredits }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Semester-by-Semester Breakdown -->
        <div class="space-y-6">
          <div
            v-for="semester in semesterData"
            :key="semester.id"
            class="bg-white rounded-lg shadow-sm border border-gray-200"
          >
            <!-- Semester Header -->
            <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
              <div class="flex items-center justify-between">
                <div>
                  <h3 class="text-lg font-semibold text-gray-900">
                    {{ semester.name }}
                  </h3>
                  <p class="text-sm text-gray-600 mt-1">
                    {{ formatDateRange(semester.startDate, semester.endDate) }}
                  </p>
                </div>

                <div class="flex items-center space-x-6">
                  <div class="text-right">
                    <p class="text-sm text-gray-600">Semester GPA</p>
                    <p class="text-2xl font-bold" :class="getGPAColor(semester.gpa)">
                      {{ semester.gpa.toFixed(2) }}
                    </p>
                  </div>

                  <div class="text-right">
                    <p class="text-sm text-gray-600">Credits</p>
                    <p class="text-2xl font-semibold text-gray-900">
                      {{ semester.credits }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Course List -->
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Course Code
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Course Name
                    </th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Credits
                    </th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Grade
                    </th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Letter
                    </th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Points
                    </th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr
                    v-for="course in semester.courses"
                    :key="course.id"
                    class="hover:bg-gray-50"
                  >
                    <td class="px-6 py-4 whitespace-nowrap">
                      <p class="text-sm font-medium text-gray-900">{{ course.code }}</p>
                    </td>
                    <td class="px-6 py-4">
                      <p class="text-sm text-gray-900">{{ course.name }}</p>
                      <p v-if="course.instructor" class="text-xs text-gray-500 mt-1">
                        {{ course.instructor }}
                      </p>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <p class="text-sm text-gray-900">{{ course.credits }}</p>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <p
                        v-if="course.grade !== null"
                        class="text-sm font-semibold"
                        :class="getGradeColor(course.grade)"
                      >
                        {{ course.grade.toFixed(1) }}%
                      </p>
                      <p v-else class="text-sm text-gray-400">—</p>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <span
                        v-if="course.letterGrade"
                        class="inline-block px-3 py-1 text-sm font-semibold rounded"
                        :class="getLetterGradeBadge(course.letterGrade)"
                      >
                        {{ course.letterGrade }}
                      </span>
                      <p v-else class="text-sm text-gray-400">—</p>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <p v-if="course.gradePoint !== null" class="text-sm text-gray-900">
                        {{ course.gradePoint.toFixed(1) }}
                      </p>
                      <p v-else class="text-sm text-gray-400">—</p>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <span
                        class="inline-block px-2 py-1 text-xs font-medium rounded-full"
                        :class="getStatusBadge(course.status)"
                      >
                        {{ course.status }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Grade Distribution Chart -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-6">Grade Distribution</h3>

          <div class="space-y-4">
            <div v-for="grade in gradeDistribution" :key="grade.letter" class="flex items-center">
              <div class="w-16 text-sm font-medium text-gray-700">
                {{ grade.letter }}
              </div>
              <div class="flex-1">
                <div class="flex items-center">
                  <div class="flex-1 bg-gray-200 rounded-full h-8 overflow-hidden">
                    <div
                      class="h-full rounded-full transition-all duration-500"
                      :class="grade.color"
                      :style="{ width: `${grade.percentage}%` }"
                    ></div>
                  </div>
                  <div class="ml-4 w-16 text-right">
                    <span class="text-sm font-semibold text-gray-900">{{ grade.count }}</span>
                    <span class="text-xs text-gray-500 ml-1">({{ grade.percentage.toFixed(0) }}%)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- GPA Trend (placeholder for future chart) -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">GPA Trend</h3>
          <div class="flex items-center justify-center h-48 bg-gray-50 rounded-lg">
            <p class="text-gray-500 text-sm">
              GPA trend visualization will be added in a future update
            </p>
          </div>
        </div>

        <!-- Academic Notes -->
        <div v-if="academicNotes.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Academic Notes</h3>
          <div class="space-y-3">
            <div
              v-for="(note, index) in academicNotes"
              :key="index"
              class="p-4 bg-blue-50 border border-blue-200 rounded-lg"
            >
              <div class="flex items-start">
                <svg class="h-5 w-5 text-blue-600 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                </svg>
                <div class="flex-1">
                  <p class="text-sm text-gray-900">{{ note.message }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ formatDate(note.date) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Transcript Footer -->
        <div class="bg-gray-50 rounded-lg border border-gray-200 p-6 print:border-t print:rounded-none">
          <div class="text-center text-sm text-gray-600">
            <p>This transcript was generated on {{ new Date().toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' }) }}</p>
            <p class="mt-2">© {{ new Date().getFullYear() }} Student Academic Management System</p>
            <p class="mt-1 text-xs">This is an unofficial transcript. For official transcripts, please contact the registrar's office.</p>
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

const loading = ref(true)
const studentInfo = ref({})
const semesterData = ref([])
const academicNotes = ref([])

onMounted(async () => {
  await loadTranscriptData()
})

async function loadTranscriptData() {
  try {
    loading.value = true

    // Load student info
    const userResponse = await api.getUserById(authStore.userId)
    studentInfo.value = userResponse.data

    // Load all enrollments
    const enrollmentsResponse = await api.getStudentEnrollments(authStore.userId)
    const enrollments = enrollmentsResponse.data || []

    // Group enrollments by semester
    const semesterMap = new Map()

    for (const enrollment of enrollments) {
      // Load course details
      const courseResponse = await api.getCourseById(enrollment.courseId)
      const course = courseResponse.data

      // Load semester details
      if (!semesterMap.has(enrollment.semesterId)) {
        const semesterResponse = await api.getSemesterById(enrollment.semesterId)
        const semester = semesterResponse.data
        semesterMap.set(enrollment.semesterId, {
          ...semester,
          courses: []
        })
      }

      // Add course to semester
      const semester = semesterMap.get(enrollment.semesterId)
      semester.courses.push({
        id: course.id,
        code: course.code,
        name: course.name,
        credits: course.credits,
        grade: enrollment.overallGrade,
        letterGrade: enrollment.letterGrade,
        gradePoint: enrollment.gradePoint,
        status: enrollment.status || 'ACTIVE',
        instructor: course.instructorName
      })
    }

    // Calculate semester GPAs
    semesterData.value = Array.from(semesterMap.values()).map(semester => {
      const gradedCourses = semester.courses.filter(c => c.gradePoint !== null)
      const totalPoints = gradedCourses.reduce((sum, c) => sum + (c.gradePoint * c.credits), 0)
      const totalCredits = gradedCourses.reduce((sum, c) => sum + c.credits, 0)

      return {
        ...semester,
        gpa: totalCredits > 0 ? totalPoints / totalCredits : 0,
        credits: totalCredits
      }
    }).sort((a, b) => new Date(a.startDate) - new Date(b.startDate))

    // Mock academic notes (replace with real data if available)
    if (cumulativeGPA.value >= 3.5) {
      academicNotes.value.push({
        message: 'Congratulations! You made the Dean\'s List this semester.',
        date: new Date().toISOString()
      })
    }
  } catch (error) {
    console.error('Error loading transcript data:', error)
    alert('Failed to load transcript data. Please try again.')
  } finally {
    loading.value = false
  }
}

const totalCourses = computed(() => {
  return semesterData.value.reduce((sum, sem) => sum + sem.courses.length, 0)
})

const completedCourses = computed(() => {
  let count = 0
  semesterData.value.forEach(sem => {
    sem.courses.forEach(course => {
      if (course.status === 'COMPLETED' || course.letterGrade) {
        count++
      }
    })
  })
  return count
})

const inProgressCourses = computed(() => {
  let count = 0
  semesterData.value.forEach(sem => {
    sem.courses.forEach(course => {
      if (course.status === 'ACTIVE' && !course.letterGrade) {
        count++
      }
    })
  })
  return count
})

const totalCredits = computed(() => {
  return semesterData.value.reduce((sum, sem) => sum + sem.credits, 0)
})

const cumulativeGPA = computed(() => {
  let totalPoints = 0
  let totalCredits = 0

  semesterData.value.forEach(sem => {
    sem.courses.forEach(course => {
      if (course.gradePoint !== null) {
        totalPoints += course.gradePoint * course.credits
        totalCredits += course.credits
      }
    })
  })

  return totalCredits > 0 ? totalPoints / totalCredits : 0
})

const academicStanding = computed(() => {
  const gpa = cumulativeGPA.value
  if (gpa >= 3.75) return 'Dean\'s List'
  if (gpa >= 3.5) return 'Honors'
  if (gpa >= 3.0) return 'Good Standing'
  if (gpa >= 2.5) return 'Satisfactory'
  if (gpa >= 2.0) return 'Probation'
  return 'Academic Warning'
})

const gradeDistribution = computed(() => {
  const distribution = {
    'A': { letter: 'A', count: 0, color: 'bg-green-500' },
    'A-': { letter: 'A-', count: 0, color: 'bg-green-400' },
    'B+': { letter: 'B+', count: 0, color: 'bg-blue-500' },
    'B': { letter: 'B', count: 0, color: 'bg-blue-400' },
    'B-': { letter: 'B-', count: 0, color: 'bg-blue-300' },
    'C+': { letter: 'C+', count: 0, color: 'bg-yellow-500' },
    'C': { letter: 'C', count: 0, color: 'bg-yellow-400' },
    'C-': { letter: 'C-', count: 0, color: 'bg-yellow-300' },
    'D': { letter: 'D', count: 0, color: 'bg-orange-400' },
    'F': { letter: 'F', count: 0, color: 'bg-red-500' }
  }

  semesterData.value.forEach(sem => {
    sem.courses.forEach(course => {
      if (course.letterGrade && distribution[course.letterGrade]) {
        distribution[course.letterGrade].count++
      }
    })
  })

  const total = Object.values(distribution).reduce((sum, grade) => sum + grade.count, 0)

  return Object.values(distribution).map(grade => ({
    ...grade,
    percentage: total > 0 ? (grade.count / total) * 100 : 0
  }))
})

function getInitials(user) {
  if (!user || !user.firstName) return '?'
  const first = user.firstName[0] || ''
  const last = user.lastName?.[0] || ''
  return (first + last).toUpperCase()
}

function getGPAColor(gpa) {
  if (gpa >= 3.5) return 'text-green-600'
  if (gpa >= 3.0) return 'text-blue-600'
  if (gpa >= 2.5) return 'text-yellow-600'
  if (gpa >= 2.0) return 'text-orange-600'
  return 'text-red-600'
}

function getGradeColor(grade) {
  if (grade >= 90) return 'text-green-600'
  if (grade >= 80) return 'text-blue-600'
  if (grade >= 70) return 'text-yellow-600'
  if (grade >= 60) return 'text-orange-600'
  return 'text-red-600'
}

function getLetterGradeBadge(letter) {
  if (['A', 'A-'].includes(letter)) return 'bg-green-100 text-green-800'
  if (['B+', 'B', 'B-'].includes(letter)) return 'bg-blue-100 text-blue-800'
  if (['C+', 'C', 'C-'].includes(letter)) return 'bg-yellow-100 text-yellow-800'
  if (['D+', 'D', 'D-'].includes(letter)) return 'bg-orange-100 text-orange-800'
  return 'bg-red-100 text-red-800'
}

function getStatusBadge(status) {
  if (status === 'COMPLETED') return 'bg-green-100 text-green-800'
  if (status === 'ACTIVE') return 'bg-blue-100 text-blue-800'
  if (status === 'WITHDRAWN') return 'bg-gray-100 text-gray-800'
  return 'bg-yellow-100 text-yellow-800'
}

function getAcademicStandingClass(standing) {
  if (standing === 'Dean\'s List') return 'bg-green-100 text-green-800'
  if (standing === 'Honors') return 'bg-blue-100 text-blue-800'
  if (standing === 'Good Standing') return 'bg-gray-100 text-gray-800'
  if (standing === 'Satisfactory') return 'bg-yellow-100 text-yellow-800'
  return 'bg-red-100 text-red-800'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })
}

function formatDateRange(start, end) {
  return `${formatDate(start)} - ${formatDate(end)}`
}

function printTranscript() {
  window.print()
}

function exportPDF() {
  alert('PDF export functionality will be implemented with a PDF library like jsPDF.')
  // Implementation with jsPDF would go here
}
</script>

<style>
@media print {
  body {
    background: white;
  }

  .no-print {
    display: none !important;
  }

  @page {
    margin: 1cm;
  }
}
</style>
