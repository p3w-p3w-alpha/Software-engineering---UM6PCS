<template>
  <div class="p-6">
    <!-- Welcome Header -->
    <div class="mb-8 animate-fade-in">
      <h1 class="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600">
        Welcome, Professor {{ authStore.userName }}!
      </h1>
      <p class="text-gray-600 mt-2">Here's an overview of your teaching activities.</p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8 animate-slide-in-up">
      <!-- My Courses Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg">
            <i class="pi pi-book text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ courses.length }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">My Courses</h3>
        <p class="text-xs text-gray-500 mt-1">Active courses</p>
      </div>

      <!-- Total Students Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg">
            <i class="pi pi-users text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ totalStudents }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Total Students</h3>
        <p class="text-xs text-gray-500 mt-1">Across all courses</p>
      </div>

      <!-- Assignments Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-lg">
            <i class="pi pi-file-edit text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ assignments.length }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Assignments</h3>
        <p class="text-xs text-gray-500 mt-1">Created assignments</p>
      </div>

      <!-- Pending Grades Card -->
      <div class="glass-card p-6 card-hover">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-gradient-to-br from-orange-500 to-orange-600 rounded-xl shadow-lg">
            <i class="pi pi-exclamation-circle text-2xl text-white"></i>
          </div>
          <span class="text-3xl font-bold text-gray-900">{{ pendingGrades }}</span>
        </div>
        <h3 class="text-sm font-medium text-gray-600">Pending Grades</h3>
        <p class="text-xs text-gray-500 mt-1">Needs grading</p>
      </div>
    </div>

    <!-- Tab View -->
    <Card class="mb-6">
      <template #content>
        <TabView>
          <!-- My Courses Tab -->
          <TabPanel header="My Courses">
            <div v-if="loading" class="space-y-3">
              <Skeleton height="100px" v-for="n in 3" :key="n" />
            </div>
            <div v-else-if="courses.length === 0" class="text-center py-12">
              <i class="pi pi-book text-6xl text-gray-300 mb-4"></i>
              <p class="text-gray-500">No courses assigned</p>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div
                v-for="course in courses"
                :key="course.id"
                class="p-6 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-blue-50 hover:to-indigo-50 transition-all cursor-pointer transform hover:scale-[1.02]"
                @click="router.push(`/faculty/courses/${course.id}`)"
              >
                <div class="flex items-start justify-between mb-3">
                  <div>
                    <h3 class="font-bold text-gray-900">{{ course.name }}</h3>
                    <p class="text-sm text-gray-600 mt-1">{{ course.code }}</p>
                  </div>
                  <Badge :value="`${course.studentCount || 0} students`" severity="info" />
                </div>
                <p class="text-sm text-gray-600 line-clamp-2">{{ course.description }}</p>
                <div class="flex items-center gap-4 mt-4 text-xs text-gray-500">
                  <span class="flex items-center gap-1">
                    <i class="pi pi-clock"></i>
                    {{ course.schedule || 'N/A' }}
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="pi pi-book"></i>
                    {{ course.credits || 3 }} credits
                  </span>
                </div>
              </div>
            </div>
          </TabPanel>

          <!-- Assignments Tab -->
          <TabPanel header="Assignments">
            <div class="flex justify-between items-center mb-4">
              <div>
                <h3 class="font-semibold text-gray-900">All Assignments</h3>
                <p class="text-sm text-gray-600">Manage course assignments</p>
              </div>
              <Button
                icon="pi pi-plus"
                label="Create Assignment"
                class="btn-primary"
                @click="router.push('/faculty/assignments/create')"
              />
            </div>
            <div v-if="loading" class="space-y-3">
              <Skeleton height="80px" v-for="n in 3" :key="n" />
            </div>
            <div v-else-if="assignments.length === 0" class="text-center py-12">
              <i class="pi pi-file-edit text-6xl text-gray-300 mb-4"></i>
              <p class="text-gray-500">No assignments created yet</p>
              <Button
                label="Create First Assignment"
                class="btn-primary mt-4"
                @click="router.push('/faculty/assignments/create')"
              />
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="assignment in assignments"
                :key="assignment.id"
                class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-purple-50 hover:to-pink-50 transition-all cursor-pointer"
                @click="router.push(`/faculty/assignments/${assignment.id}`)"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <h3 class="font-semibold text-gray-900">{{ assignment.title }}</h3>
                    <p class="text-sm text-gray-600 mt-1">{{ assignment.courseName }}</p>
                    <div class="flex items-center gap-4 mt-3">
                      <span class="text-xs text-gray-500">
                        <i class="pi pi-calendar mr-1"></i>
                        Due: {{ formatDate(assignment.dueDate) }}
                      </span>
                      <span class="text-xs text-gray-500">
                        <i class="pi pi-file mr-1"></i>
                        {{ assignment.submissionCount || 0 }} submissions
                      </span>
                    </div>
                  </div>
                  <Badge
                    :value="assignment.status || 'ACTIVE'"
                    :severity="assignment.status === 'ACTIVE' ? 'success' : 'secondary'"
                  />
                </div>
              </div>
            </div>
          </TabPanel>

          <!-- Recent Submissions Tab -->
          <TabPanel header="Recent Submissions">
            <div class="flex justify-between items-center mb-4">
              <div>
                <h3 class="font-semibold text-gray-900">Student Submissions</h3>
                <p class="text-sm text-gray-600">Review and grade submissions</p>
              </div>
              <Button
                icon="pi pi-list"
                label="View All"
                class="btn-secondary"
                @click="router.push('/faculty/submissions')"
              />
            </div>
            <div v-if="loading" class="space-y-3">
              <Skeleton height="80px" v-for="n in 5" :key="n" />
            </div>
            <div v-else-if="submissions.length === 0" class="text-center py-12">
              <i class="pi pi-inbox text-6xl text-gray-300 mb-4"></i>
              <p class="text-gray-500">No recent submissions</p>
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="submission in submissions.slice(0, 10)"
                :key="submission.id"
                class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-orange-50 hover:to-yellow-50 transition-all cursor-pointer"
                @click="router.push(`/faculty/submissions/${submission.id}/grade`)"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <h3 class="font-semibold text-gray-900">{{ submission.studentName }}</h3>
                    <p class="text-sm text-gray-600 mt-1">{{ submission.assignmentTitle }}</p>
                    <div class="flex items-center gap-4 mt-3 text-xs text-gray-500">
                      <span>
                        <i class="pi pi-clock mr-1"></i>
                        {{ formatDate(submission.submittedAt) }}
                      </span>
                      <span v-if="submission.isLate" class="text-red-600 font-medium">
                        <i class="pi pi-exclamation-triangle mr-1"></i>
                        Late Submission
                      </span>
                    </div>
                  </div>
                  <Badge
                    :value="submission.status || 'PENDING'"
                    :severity="getSubmissionSeverity(submission.status)"
                  />
                </div>
              </div>
            </div>
          </TabPanel>

          <!-- Students Tab -->
          <TabPanel header="My Students">
            <div v-if="loading" class="space-y-3">
              <Skeleton height="60px" v-for="n in 5" :key="n" />
            </div>
            <div v-else-if="students.length === 0" class="text-center py-12">
              <i class="pi pi-users text-6xl text-gray-300 mb-4"></i>
              <p class="text-gray-500">No students enrolled</p>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div
                v-for="student in students"
                :key="student.id"
                class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-indigo-50 hover:to-purple-50 transition-all cursor-pointer"
              >
                <div class="flex items-center gap-3">
                  <Avatar
                    :label="student.name?.charAt(0) || 'S'"
                    size="large"
                    shape="circle"
                    class="bg-gradient-to-br from-indigo-500 to-purple-600 text-white"
                  />
                  <div class="flex-1">
                    <h3 class="font-semibold text-gray-900">{{ student.name }}</h3>
                    <p class="text-sm text-gray-600">{{ student.email }}</p>
                    <p class="text-xs text-gray-500 mt-1">
                      {{ student.courseName }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </TabPanel>
        </TabView>
      </template>
    </Card>

    <!-- Quick Actions -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <button
        @click="router.push('/faculty/assignments')"
        class="p-6 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-file-edit text-3xl mb-2"></i>
        <h3 class="font-semibold">Create Assignment</h3>
        <p class="text-sm text-blue-100 mt-1">New assignment task</p>
      </button>

      <button
        @click="router.push('/faculty/submissions')"
        class="p-6 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-check-circle text-3xl mb-2"></i>
        <h3 class="font-semibold">Grade Submissions</h3>
        <p class="text-sm text-orange-100 mt-1">Review student work</p>
      </button>

      <button
        @click="router.push('/faculty/attendance')"
        class="p-6 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-calendar-check text-3xl mb-2"></i>
        <h3 class="font-semibold">Take Attendance</h3>
        <p class="text-sm text-green-100 mt-1">Mark student presence</p>
      </button>

      <button
        @click="router.push('/messages')"
        class="p-6 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-xl hover:shadow-xl transform hover:-translate-y-1 transition-all"
      >
        <i class="pi pi-envelope text-3xl mb-2"></i>
        <h3 class="font-semibold">Send Message</h3>
        <p class="text-sm text-purple-100 mt-1">Contact students</p>
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
import TabView from 'primevue/tabview'
import TabPanel from 'primevue/tabpanel'
import Avatar from 'primevue/avatar'
import api from '../services/api'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const courses = ref([])
const assignments = ref([])
const submissions = ref([])
const students = ref([])
const loading = ref(true)

const totalStudents = computed(() => {
  return courses.value.reduce((sum, course) => sum + (course.studentCount || 0), 0)
})

const pendingGrades = computed(() => {
  return submissions.value.filter(s => s.status === 'SUBMITTED' || s.status === 'PENDING').length
})

onMounted(async () => {
  await loadFacultyData()
})

async function loadFacultyData() {
  try {
    loading.value = true
    const facultyId = authStore.userId

    // Load all data in parallel
    const [coursesRes, assignmentsRes, submissionsRes, studentsRes] = await Promise.all([
      api.getFacultyCourses(facultyId).catch(() => ({ data: [] })),
      api.getFacultyAssignments(facultyId).catch(() => ({ data: [] })),
      api.getFacultySubmissions(facultyId).catch(() => ({ data: [] })),
      api.getFacultyStudents(facultyId).catch(() => ({ data: [] }))
    ])

    courses.value = coursesRes.data || []
    assignments.value = assignmentsRes.data || []
    submissions.value = submissionsRes.data || []
    students.value = studentsRes.data || []
  } catch (error) {
    console.error('Error loading faculty data:', error)
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

function getSubmissionSeverity(status) {
  const severities = {
    'PENDING': 'warning',
    'SUBMITTED': 'info',
    'GRADED': 'success',
    'RETURNED': 'secondary'
  }
  return severities[status] || 'secondary'
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