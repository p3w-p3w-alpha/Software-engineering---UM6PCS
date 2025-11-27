<template>
  <div class="p-6">
    <!-- Welcome Header -->
    <div class="mb-8 animate-fade-in">
      <h1 class="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600">
        Welcome Back, {{ authStore.userName }}!
      </h1>
      <p class="text-gray-600 mt-2">Here's what's happening with your studies today.</p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8 animate-slide-in-up">
      <!-- Enrolled Courses Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg">
            <i class="pi pi-book text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ enrollments.length }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Enrolled Courses</h3>
        <p class="text-xs text-gray-500 mt-1">Active this semester</p>
      </div>

      <!-- Active Assignments Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-orange-500 to-orange-600 rounded-xl shadow-lg">
            <i class="pi pi-file-edit text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ activeAssignments }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Active Assignments</h3>
        <p class="text-xs text-gray-500 mt-1">Due soon</p>
      </div>

      <!-- Study Groups Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-lg">
            <i class="pi pi-users text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ studyGroups.length }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Study Groups</h3>
        <p class="text-xs text-gray-500 mt-1">Active groups</p>
      </div>

      <!-- GPA Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg">
            <i class="pi pi-chart-line text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ gpa.toFixed(2) }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Current GPA</h3>
        <p class="text-xs text-gray-500 mt-1">Overall average</p>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Enrolled Courses -->
      <Card class="animate-slide-in-up" style="animation-delay: 0.1s">
        <template #header>
          <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <div>
                <h2 class="text-xl font-bold text-gray-900">My Courses</h2>
                <p class="text-sm text-gray-600 mt-1">Enrolled for this semester</p>
              </div>
              <Button
                icon="pi pi-plus"
                label="Browse"
                class="btn-primary"
                @click="router.push('/student/courses')"
              />
            </div>
          </div>
        </template>
        <template #content>
          <div v-if="loading" class="space-y-3">
            <Skeleton height="80px" v-for="n in 3" :key="n" />
          </div>
          <div v-else-if="enrollments.length === 0" class="text-center py-12">
            <i class="pi pi-book text-6xl text-gray-300 mb-4"></i>
            <p class="text-gray-500">No courses enrolled yet</p>
            <Button
              label="Browse Courses"
              class="btn-primary mt-4"
              @click="router.push('/student/courses/browse')"
            />
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="enrollment in enrollments.slice(0, 5)"
              :key="enrollment.id"
              class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-indigo-50 hover:to-purple-50 transition-all cursor-pointer transform hover:scale-[1.02]"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900">{{ enrollment.courseName || enrollment.course?.name || 'Course' }}</h3>
                  <p class="text-sm text-gray-600 mt-1">{{ enrollment.courseCode || enrollment.course?.code || 'N/A' }}</p>
                  <div class="flex items-center gap-2 mt-2">
                    <Badge
                      :value="enrollment.status"
                      :severity="getEnrollmentSeverity(enrollment.status)"
                    />
                    <span class="text-xs text-gray-500">{{ enrollment.credits || 3 }} credits</span>
                  </div>
                </div>
                <i class="pi pi-angle-right text-gray-400 text-xl"></i>
              </div>
            </div>
          </div>
        </template>
      </Card>

      <!-- Recent Assignments -->
      <Card class="animate-slide-in-up" style="animation-delay: 0.2s">
        <template #header>
          <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <div>
                <h2 class="text-xl font-bold text-gray-900">Assignments</h2>
                <p class="text-sm text-gray-600 mt-1">Upcoming and pending</p>
              </div>
              <Button
                icon="pi pi-list"
                label="View All"
                class="btn-secondary"
                @click="router.push('/student/assignments')"
              />
            </div>
          </div>
        </template>
        <template #content>
          <div v-if="loading" class="space-y-3">
            <Skeleton height="80px" v-for="n in 3" :key="n" />
          </div>
          <div v-else-if="assignments.length === 0" class="text-center py-12">
            <i class="pi pi-file-edit text-6xl text-gray-300 mb-4"></i>
            <p class="text-gray-500">No assignments available</p>
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="assignment in assignments.slice(0, 5)"
              :key="assignment.id"
              class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-orange-50 hover:to-yellow-50 transition-all cursor-pointer transform hover:scale-[1.02]"
              @click="router.push(`/student/assignments/${assignment.id}/submit`)"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900">{{ assignment.title }}</h3>
                  <p class="text-sm text-gray-600 mt-1 line-clamp-2">{{ assignment.description }}</p>
                  <div class="flex items-center gap-2 mt-3">
                    <i class="pi pi-calendar text-xs text-gray-500"></i>
                    <span class="text-xs text-gray-500">Due: {{ formatDate(assignment.dueDate) }}</span>
                    <Badge
                      :value="getAssignmentStatus(assignment)"
                      :severity="getAssignmentSeverity(assignment)"
                      class="ml-auto"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </Card>

      <!-- Recent Grades -->
      <Card class="animate-slide-in-up" style="animation-delay: 0.3s">
        <template #header>
          <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <div>
                <h2 class="text-xl font-bold text-gray-900">Recent Grades</h2>
                <p class="text-sm text-gray-600 mt-1">Your latest scores</p>
              </div>
              <Button
                icon="pi pi-chart-bar"
                label="View All"
                class="btn-secondary"
                @click="router.push('/student/grades')"
              />
            </div>
          </div>
        </template>
        <template #content>
          <div v-if="loading" class="space-y-3">
            <Skeleton height="60px" v-for="n in 4" :key="n" />
          </div>
          <div v-else-if="grades.length === 0" class="text-center py-12">
            <i class="pi pi-chart-bar text-6xl text-gray-300 mb-4"></i>
            <p class="text-gray-500">No grades available yet</p>
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="grade in grades.slice(0, 6)"
              :key="grade.id"
              class="flex items-center justify-between p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:shadow-md transition-all"
            >
              <div class="flex-1">
                <p class="font-medium text-gray-900">{{ grade.courseName || grade.assignmentTitle }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ grade.courseCode || 'Assignment' }}</p>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold" :class="getGradeColor(grade.grade)">
                  {{ grade.grade }}
                </p>
                <p class="text-xs text-gray-500">/ {{ grade.maxGrade || 100 }}</p>
              </div>
            </div>
          </div>
        </template>
      </Card>

      <!-- Study Groups -->
      <Card class="animate-slide-in-up" style="animation-delay: 0.4s">
        <template #header>
          <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <div>
                <h2 class="text-xl font-bold text-gray-900">Study Groups</h2>
                <p class="text-sm text-gray-600 mt-1">Collaborative learning</p>
              </div>
              <Button
                icon="pi pi-users"
                label="Browse"
                class="btn-secondary"
                @click="router.push('/studygroups')"
              />
            </div>
          </div>
        </template>
        <template #content>
          <div v-if="loading" class="space-y-3">
            <Skeleton height="80px" v-for="n in 3" :key="n" />
          </div>
          <div v-else-if="studyGroups.length === 0" class="text-center py-12">
            <i class="pi pi-users text-6xl text-gray-300 mb-4"></i>
            <p class="text-gray-500">No study groups joined</p>
            <Button
              label="Find Groups"
              class="btn-primary mt-4"
              @click="router.push('/studygroups')"
            />
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="group in studyGroups.slice(0, 4)"
              :key="group.id"
              class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-purple-50 hover:to-pink-50 transition-all cursor-pointer transform hover:scale-[1.02]"
              @click="router.push(`/studygroups/${group.id}`)"
            >
              <h3 class="font-semibold text-gray-900">{{ group.name }}</h3>
              <p class="text-sm text-gray-600 mt-1 line-clamp-2">{{ group.description }}</p>
              <div class="flex items-center gap-4 mt-3 text-xs text-gray-500">
                <span class="flex items-center gap-1">
                  <i class="pi pi-users"></i>
                  {{ group.memberCount || 0 }} members
                </span>
                <span class="flex items-center gap-1">
                  <i class="pi pi-book"></i>
                  {{ group.courseName || 'General' }}
                </span>
              </div>
            </div>
          </div>
        </template>
      </Card>
    </div>

    <!-- Quick Actions -->
    <div class="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4">
      <button
        @click="router.push('/student/courses/browse')"
        class="p-6 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-search text-3xl mb-2"></i>
        <h3 class="font-semibold">Browse Courses</h3>
        <p class="text-sm text-blue-100 mt-1">Find new courses to enroll</p>
      </button>

      <button
        @click="router.push('/student/assignments')"
        class="p-6 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-file-edit text-3xl mb-2"></i>
        <h3 class="font-semibold">Submit Assignment</h3>
        <p class="text-sm text-orange-100 mt-1">Upload your work</p>
      </button>

      <button
        @click="router.push('/studygroups')"
        class="p-6 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-users text-3xl mb-2"></i>
        <h3 class="font-semibold">Join Study Group</h3>
        <p class="text-sm text-purple-100 mt-1">Collaborate with peers</p>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToast } from 'primevue/usetoast'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Badge from 'primevue/badge'
import Skeleton from 'primevue/skeleton'
import api from '../services/api'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const enrollments = ref([])
const assignments = ref([])
const grades = ref([])
const studyGroups = ref([])
const loading = ref(true)

const gpa = computed(() => {
  if (grades.value.length === 0) return 0
  const total = grades.value.reduce((sum, grade) => sum + (parseFloat(grade.grade) || 0), 0)
  return (total / grades.value.length) / 25 // Convert to 4.0 scale
})

const activeAssignments = computed(() => {
  return assignments.value.filter(a =>
    a.status !== 'SUBMITTED' && a.status !== 'GRADED'
  ).length
})

onMounted(async () => {
  await loadStudentData()
})

async function loadStudentData() {
  try {
    loading.value = true
    const studentId = authStore.userId

    // Load all data in parallel
    const [enrollmentsRes, assignmentsRes, gradesRes, groupsRes] = await Promise.all([
      api.getStudentEnrollments(studentId).catch(() => ({ data: [] })),
      api.getStudentAssignments(studentId).catch(() => ({ data: [] })),
      api.getStudentGrades(studentId).catch(() => ({ data: [] })),
      api.getUserStudyGroups(studentId).catch(() => ({ data: [] }))
    ])

    enrollments.value = enrollmentsRes.data || []
    assignments.value = assignmentsRes.data || []
    grades.value = gradesRes.data || []
    studyGroups.value = groupsRes.data || []
  } catch (error) {
    console.error('Error loading student data:', error)
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Failed to load dashboard data',
      life: 3000
    })
  } finally {
    loading.value = false
  }
}

function getEnrollmentSeverity(status) {
  const severities = {
    'ENROLLED': 'success',
    'PENDING': 'warning',
    'COMPLETED': 'info',
    'DROPPED': 'danger'
  }
  return severities[status] || 'secondary'
}

function getAssignmentStatus(assignment) {
  if (!assignment.dueDate) return 'PENDING'
  const dueDate = new Date(assignment.dueDate)
  const now = new Date()
  if (assignment.status === 'SUBMITTED') return 'SUBMITTED'
  if (assignment.status === 'GRADED') return 'GRADED'
  if (dueDate < now) return 'OVERDUE'
  return 'PENDING'
}

function getAssignmentSeverity(assignment) {
  const status = getAssignmentStatus(assignment)
  const severities = {
    'PENDING': 'warning',
    'SUBMITTED': 'info',
    'GRADED': 'success',
    'OVERDUE': 'danger'
  }
  return severities[status] || 'secondary'
}

function getGradeColor(grade) {
  const numGrade = parseFloat(grade)
  if (numGrade >= 90) return 'text-green-600'
  if (numGrade >= 80) return 'text-blue-600'
  if (numGrade >= 70) return 'text-yellow-600'
  if (numGrade >= 60) return 'text-orange-600'
  return 'text-red-600'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>

<style scoped>
.card-hover {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>