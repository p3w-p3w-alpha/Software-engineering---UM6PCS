<template>
  <div class="min-h-screen bg-gray-50 py-8">
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

    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <button
          @click="$router.back()"
          class="flex items-center text-gray-600 hover:text-gray-900 mb-4"
        >
          <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          Back to Grades
        </button>

        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Grade Entry</h1>
            <p class="mt-2 text-sm text-gray-600">
              Enter detailed grades for student assignments and assessments
            </p>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <LoadingSpinner size="large" />
      </div>

      <!-- Main Content -->
      <div v-else class="space-y-6">
        <!-- Student & Course Info Card -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-start justify-between">
            <div class="flex items-start space-x-4">
              <!-- Student Avatar -->
              <div class="h-16 w-16 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xl font-semibold">
                {{ getInitials(studentInfo) }}
              </div>

              <!-- Student Details -->
              <div>
                <h2 class="text-xl font-semibold text-gray-900">
                  {{ studentInfo.firstName }} {{ studentInfo.lastName }}
                </h2>
                <p class="text-sm text-gray-600 mt-1">
                  ID: {{ studentInfo.username }} • {{ studentInfo.email }}
                </p>
                <div class="flex items-center space-x-4 mt-2">
                  <span class="text-sm text-gray-600">
                    <span class="font-medium">Course:</span> {{ courseInfo.name }}
                  </span>
                  <span class="text-sm text-gray-600">
                    <span class="font-medium">Code:</span> {{ courseInfo.code }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Current Grade Summary -->
            <div class="text-right">
              <p class="text-sm text-gray-600">Current Grade</p>
              <div class="mt-1">
                <span class="text-3xl font-bold" :class="getGradeColor(currentGrade.percentage)">
                  {{ currentGrade.percentage.toFixed(1) }}%
                </span>
                <span class="ml-2 text-xl font-semibold text-gray-700">
                  {{ currentGrade.letterGrade }}
                </span>
              </div>
              <p class="text-xs text-gray-500 mt-1">
                {{ currentGrade.status }}
              </p>
            </div>
          </div>
        </div>

        <!-- Grade Components -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900">Grade Components</h3>
            <p class="text-sm text-gray-600 mt-1">
              Enter scores for each assessment component
            </p>
          </div>

          <div class="p-6">
            <!-- Assignments -->
            <div class="mb-8">
              <div class="flex items-center justify-between mb-4">
                <h4 class="text-md font-semibold text-gray-900 flex items-center">
                  <svg class="h-5 w-5 mr-2 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  Assignments ({{ gradeWeights.assignments }}%)
                </h4>
                <span class="text-sm font-medium text-gray-600">
                  Average: {{ calculateComponentAverage('assignments').toFixed(1) }}%
                </span>
              </div>

              <div class="space-y-3">
                <div
                  v-for="assignment in assignments"
                  :key="assignment.id"
                  class="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg"
                >
                  <div class="flex-1">
                    <p class="font-medium text-gray-900">{{ assignment.title }}</p>
                    <p class="text-xs text-gray-500 mt-1">
                      Due: {{ formatDate(assignment.dueDate) }}
                      <span v-if="assignment.isLate" class="ml-2 text-red-600 font-medium">LATE</span>
                    </p>
                  </div>

                  <div class="flex items-center space-x-3">
                    <div class="flex items-center space-x-2">
                      <input
                        v-model.number="assignment.score"
                        @input="calculateGrades"
                        type="number"
                        min="0"
                        :max="assignment.maxPoints"
                        step="0.5"
                        class="w-20 px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                        :placeholder="`0-${assignment.maxPoints}`"
                      />
                      <span class="text-gray-600">/ {{ assignment.maxPoints }}</span>
                    </div>

                    <div class="w-16 text-right">
                      <span
                        v-if="assignment.score !== null && assignment.score !== ''"
                        class="font-semibold"
                        :class="getGradeColor((assignment.score / assignment.maxPoints) * 100)"
                      >
                        {{ ((assignment.score / assignment.maxPoints) * 100).toFixed(0) }}%
                      </span>
                      <span v-else class="text-gray-400">—</span>
                    </div>

                    <button
                      v-if="assignment.submissionId"
                      @click="viewSubmission(assignment.submissionId)"
                      class="text-blue-600 hover:text-blue-700"
                      title="View Submission"
                    >
                      <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                    </button>
                  </div>
                </div>

                <div v-if="assignments.length === 0" class="text-center py-8 text-gray-500">
                  No assignments available
                </div>
              </div>
            </div>

            <!-- Midterm Exam -->
            <div class="mb-8">
              <div class="flex items-center justify-between mb-4">
                <h4 class="text-md font-semibold text-gray-900 flex items-center">
                  <svg class="h-5 w-5 mr-2 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  Midterm Exam ({{ gradeWeights.midterm }}%)
                </h4>
              </div>

              <div class="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg">
                <div class="flex-1">
                  <p class="font-medium text-gray-900">Midterm Examination</p>
                  <p class="text-xs text-gray-500 mt-1">Comprehensive assessment</p>
                </div>

                <div class="flex items-center space-x-3">
                  <div class="flex items-center space-x-2">
                    <input
                      v-model.number="midtermScore"
                      @input="calculateGrades"
                      type="number"
                      min="0"
                      max="100"
                      step="0.5"
                      class="w-20 px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                      placeholder="0-100"
                    />
                    <span class="text-gray-600">/ 100</span>
                  </div>

                  <div class="w-16 text-right">
                    <span
                      v-if="midtermScore !== null && midtermScore !== ''"
                      class="font-semibold"
                      :class="getGradeColor(midtermScore)"
                    >
                      {{ midtermScore.toFixed(0) }}%
                    </span>
                    <span v-else class="text-gray-400">—</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Final Exam -->
            <div class="mb-8">
              <div class="flex items-center justify-between mb-4">
                <h4 class="text-md font-semibold text-gray-900 flex items-center">
                  <svg class="h-5 w-5 mr-2 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  </svg>
                  Final Exam ({{ gradeWeights.finalExam }}%)
                </h4>
              </div>

              <div class="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg">
                <div class="flex-1">
                  <p class="font-medium text-gray-900">Final Examination</p>
                  <p class="text-xs text-gray-500 mt-1">Cumulative assessment</p>
                </div>

                <div class="flex items-center space-x-3">
                  <div class="flex items-center space-x-2">
                    <input
                      v-model.number="finalExamScore"
                      @input="calculateGrades"
                      type="number"
                      min="0"
                      max="100"
                      step="0.5"
                      class="w-20 px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                      placeholder="0-100"
                    />
                    <span class="text-gray-600">/ 100</span>
                  </div>

                  <div class="w-16 text-right">
                    <span
                      v-if="finalExamScore !== null && finalExamScore !== ''"
                      class="font-semibold"
                      :class="getGradeColor(finalExamScore)"
                    >
                      {{ finalExamScore.toFixed(0) }}%
                    </span>
                    <span v-else class="text-gray-400">—</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Participation -->
            <div>
              <div class="flex items-center justify-between mb-4">
                <h4 class="text-md font-semibold text-gray-900 flex items-center">
                  <svg class="h-5 w-5 mr-2 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  Participation ({{ gradeWeights.participation }}%)
                </h4>
              </div>

              <div class="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg">
                <div class="flex-1">
                  <p class="font-medium text-gray-900">Class Participation & Attendance</p>
                  <p class="text-xs text-gray-500 mt-1">Active engagement in class activities</p>
                </div>

                <div class="flex items-center space-x-3">
                  <div class="flex items-center space-x-2">
                    <input
                      v-model.number="participationScore"
                      @input="calculateGrades"
                      type="number"
                      min="0"
                      max="100"
                      step="1"
                      class="w-20 px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                      placeholder="0-100"
                    />
                    <span class="text-gray-600">/ 100</span>
                  </div>

                  <div class="w-16 text-right">
                    <span
                      v-if="participationScore !== null && participationScore !== ''"
                      class="font-semibold"
                      :class="getGradeColor(participationScore)"
                    >
                      {{ participationScore.toFixed(0) }}%
                    </span>
                    <span v-else class="text-gray-400">—</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Final Grade Calculation -->
        <div class="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-lg shadow-sm border border-blue-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Final Grade Calculation</h3>

          <div class="space-y-3 mb-6">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-700">Assignments ({{ gradeWeights.assignments }}%)</span>
              <span class="font-medium">{{ calculateComponentAverage('assignments').toFixed(1) }}% × 0.{{ gradeWeights.assignments }}</span>
              <span class="font-semibold text-blue-600">
                {{ (calculateComponentAverage('assignments') * gradeWeights.assignments / 100).toFixed(2) }}
              </span>
            </div>

            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-700">Midterm ({{ gradeWeights.midterm }}%)</span>
              <span class="font-medium">{{ (midtermScore || 0).toFixed(1) }}% × 0.{{ gradeWeights.midterm }}</span>
              <span class="font-semibold text-blue-600">
                {{ ((midtermScore || 0) * gradeWeights.midterm / 100).toFixed(2) }}
              </span>
            </div>

            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-700">Final Exam ({{ gradeWeights.finalExam }}%)</span>
              <span class="font-medium">{{ (finalExamScore || 0).toFixed(1) }}% × 0.{{ gradeWeights.finalExam }}</span>
              <span class="font-semibold text-blue-600">
                {{ ((finalExamScore || 0) * gradeWeights.finalExam / 100).toFixed(2) }}
              </span>
            </div>

            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-700">Participation ({{ gradeWeights.participation }}%)</span>
              <span class="font-medium">{{ (participationScore || 0).toFixed(1) }}% × 0.{{ gradeWeights.participation }}</span>
              <span class="font-semibold text-blue-600">
                {{ ((participationScore || 0) * gradeWeights.participation / 100).toFixed(2) }}
              </span>
            </div>
          </div>

          <div class="pt-4 border-t-2 border-blue-300">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600">Final Grade</p>
                <div class="flex items-baseline space-x-3 mt-1">
                  <span class="text-4xl font-bold" :class="getGradeColor(currentGrade.percentage)">
                    {{ currentGrade.percentage.toFixed(2) }}%
                  </span>
                  <span class="text-2xl font-semibold text-gray-700">
                    {{ currentGrade.letterGrade }}
                  </span>
                  <span class="text-sm text-gray-600">
                    ({{ currentGrade.gradePoint }} GPA)
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Comments & Feedback -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Comments & Feedback</h3>

          <textarea
            v-model="comments"
            rows="4"
            placeholder="Add comments or feedback for the student (optional)..."
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          ></textarea>
        </div>

        <!-- Action Buttons -->
        <div class="flex items-center justify-between pt-4">
          <button
            @click="$router.back()"
            class="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>

          <div class="flex items-center space-x-3">
            <button
              @click="saveDraft"
              :disabled="saving"
              class="px-6 py-3 border border-blue-300 text-blue-600 rounded-lg hover:bg-blue-50 transition-colors disabled:opacity-50"
            >
              Save as Draft
            </button>

            <button
              @click="submitGrades"
              :disabled="saving || !isGradeComplete"
              class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 flex items-center"
            >
              <LoadingSpinner v-if="saving" size="small" color="white" class="mr-2" />
              <span>{{ saving ? 'Submitting...' : 'Submit Final Grade' }}</span>
            </button>
          </div>
        </div>

        <!-- Warning Message -->
        <div v-if="!isGradeComplete" class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
          <div class="flex">
            <svg class="h-5 w-5 text-yellow-600 mr-3" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
            <p class="text-sm text-yellow-800">
              Please enter scores for all grade components before submitting the final grade.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const saving = ref(false)

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

const studentInfo = ref({})
const courseInfo = ref({})
const assignments = ref([])
const midtermScore = ref(null)
const finalExamScore = ref(null)
const participationScore = ref(null)
const comments = ref('')

const gradeWeights = ref({
  assignments: 30,
  midterm: 25,
  finalExam: 35,
  participation: 10
})

const currentGrade = ref({
  percentage: 0,
  letterGrade: 'N/A',
  gradePoint: 0,
  status: 'Not Graded'
})

onMounted(async () => {
  await loadGradeData()
})

async function loadGradeData() {
  try {
    loading.value = true

    const enrollmentId = route.params.enrollmentId
    const courseId = route.query.courseId

    // Load student enrollment info
    const enrollmentResponse = await api.getEnrollmentById(enrollmentId)
    const enrollment = enrollmentResponse.data

    // Load student info
    const studentResponse = await api.getUserById(enrollment.studentId)
    studentInfo.value = studentResponse.data

    // Load course info
    const courseResponse = await api.getCourseById(courseId)
    courseInfo.value = courseResponse.data

    // Load assignments for the course
    const assignmentsResponse = await api.getAssignmentsByCourse(courseId)
    const allAssignments = assignmentsResponse.data || []

    // Get student submissions
    const submissionsResponse = await api.getStudentSubmissions(enrollment.studentId)
    const submissions = submissionsResponse.data || []

    // Map assignments with student scores
    assignments.value = allAssignments.map(assignment => {
      const submission = submissions.find(s => s.assignmentId === assignment.id)
      return {
        id: assignment.id,
        title: assignment.title,
        dueDate: assignment.dueDate,
        maxPoints: assignment.maxGrade || 100,
        score: submission?.grade || null,
        submissionId: submission?.id,
        isLate: submission?.isLate || false
      }
    })

    // Load existing grade if available
    if (enrollment.midtermGrade) midtermScore.value = enrollment.midtermGrade
    if (enrollment.finalGrade) finalExamScore.value = enrollment.finalGrade
    if (enrollment.participationGrade) participationScore.value = enrollment.participationGrade
    if (enrollment.comments) comments.value = enrollment.comments

    calculateGrades()
  } catch (error) {
    console.error('Error loading grade data:', error)
    showNotification('Failed to load grade data. Please try again.', 'error')
  } finally {
    loading.value = false
  }
}

function calculateComponentAverage(component) {
  if (component === 'assignments') {
    const gradedAssignments = assignments.value.filter(a => a.score !== null && a.score !== '')
    if (gradedAssignments.length === 0) return 0

    const total = gradedAssignments.reduce((sum, a) => sum + (a.score / a.maxPoints * 100), 0)
    return total / gradedAssignments.length
  }
  return 0
}

function calculateGrades() {
  const assignmentsAvg = calculateComponentAverage('assignments')
  const midterm = midtermScore.value || 0
  const finalExam = finalExamScore.value || 0
  const participation = participationScore.value || 0

  const finalPercentage =
    (assignmentsAvg * gradeWeights.value.assignments / 100) +
    (midterm * gradeWeights.value.midterm / 100) +
    (finalExam * gradeWeights.value.finalExam / 100) +
    (participation * gradeWeights.value.participation / 100)

  currentGrade.value = {
    percentage: finalPercentage,
    letterGrade: getLetterGrade(finalPercentage),
    gradePoint: getGradePoint(finalPercentage),
    status: isGradeComplete.value ? 'Complete' : 'In Progress'
  }
}

function getLetterGrade(percentage) {
  if (percentage >= 93) return 'A'
  if (percentage >= 90) return 'A-'
  if (percentage >= 87) return 'B+'
  if (percentage >= 83) return 'B'
  if (percentage >= 80) return 'B-'
  if (percentage >= 77) return 'C+'
  if (percentage >= 73) return 'C'
  if (percentage >= 70) return 'C-'
  if (percentage >= 67) return 'D+'
  if (percentage >= 63) return 'D'
  if (percentage >= 60) return 'D-'
  return 'F'
}

function getGradePoint(percentage) {
  if (percentage >= 93) return 4.0
  if (percentage >= 90) return 3.7
  if (percentage >= 87) return 3.3
  if (percentage >= 83) return 3.0
  if (percentage >= 80) return 2.7
  if (percentage >= 77) return 2.3
  if (percentage >= 73) return 2.0
  if (percentage >= 70) return 1.7
  if (percentage >= 67) return 1.3
  if (percentage >= 63) return 1.0
  if (percentage >= 60) return 0.7
  return 0.0
}

const isGradeComplete = computed(() => {
  const hasAssignmentGrades = assignments.value.length === 0 ||
    assignments.value.some(a => a.score !== null && a.score !== '')
  const hasMidterm = midtermScore.value !== null && midtermScore.value !== ''
  const hasFinal = finalExamScore.value !== null && finalExamScore.value !== ''
  const hasParticipation = participationScore.value !== null && participationScore.value !== ''

  return hasAssignmentGrades && hasMidterm && hasFinal && hasParticipation
})

async function saveDraft() {
  try {
    saving.value = true

    const enrollmentId = route.params.enrollmentId

    const gradeData = {
      midtermGrade: midtermScore.value,
      finalGrade: finalExamScore.value,
      participationGrade: participationScore.value,
      comments: comments.value,
      status: 'DRAFT'
    }

    await api.updateEnrollmentGrades(enrollmentId, gradeData)

    showNotification('Grade draft saved successfully!', 'success')
  } catch (error) {
    console.error('Error saving draft:', error)
    showNotification('Failed to save draft. Please try again.', 'error')
  } finally {
    saving.value = false
  }
}

async function submitGrades() {
  if (!isGradeComplete.value) return

  try {
    saving.value = true

    const enrollmentId = route.params.enrollmentId

    const gradeData = {
      midtermGrade: midtermScore.value,
      finalGrade: finalExamScore.value,
      participationGrade: participationScore.value,
      overallGrade: currentGrade.value.percentage,
      letterGrade: currentGrade.value.letterGrade,
      gradePoint: currentGrade.value.gradePoint,
      comments: comments.value,
      status: 'FINALIZED'
    }

    await api.updateEnrollmentGrades(enrollmentId, gradeData)

    showNotification('Final grade submitted successfully!', 'success')
    setTimeout(() => router.back(), 1500)
  } catch (error) {
    console.error('Error submitting grades:', error)
    showNotification(error.response?.data?.message || 'Failed to submit grades. Please try again.', 'error')
  } finally {
    saving.value = false
  }
}

function viewSubmission(submissionId) {
  router.push(`/faculty/submissions/${submissionId}/grade`)
}

function getInitials(user) {
  if (!user || !user.firstName) return '?'
  const first = user.firstName[0] || ''
  const last = user.lastName?.[0] || ''
  return (first + last).toUpperCase()
}

function getGradeColor(percentage) {
  if (percentage >= 90) return 'text-green-600'
  if (percentage >= 80) return 'text-blue-600'
  if (percentage >= 70) return 'text-yellow-600'
  if (percentage >= 60) return 'text-orange-600'
  return 'text-red-600'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>
