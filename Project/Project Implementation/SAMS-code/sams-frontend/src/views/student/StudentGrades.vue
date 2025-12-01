<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">My Grades</h1>
      <p class="mt-2 text-gray-600">Track your academic performance, GPA trends, and class rankings</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
    </div>

    <template v-else>
      <!-- GPA Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <!-- Cumulative GPA -->
        <div class="bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl shadow-lg p-6 text-white">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium opacity-90">Cumulative GPA</p>
              <p class="text-4xl font-bold mt-2">{{ overallGPA }}</p>
              <p class="text-sm opacity-75 mt-1">out of 4.00</p>
            </div>
            <div class="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
          </div>
          <!-- GPA Progress Bar -->
          <div class="mt-4">
            <div class="w-full bg-white/20 rounded-full h-2">
              <div
                class="bg-white h-2 rounded-full transition-all duration-500"
                :style="{ width: `${(parseFloat(overallGPA) / 4) * 100}%` }"
              ></div>
            </div>
          </div>
        </div>

        <!-- Total Credits -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center">
            <div class="p-3 bg-blue-100 rounded-lg">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm text-gray-500">Total Credits</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalCredits }}</p>
            </div>
          </div>
        </div>

        <!-- Courses Completed -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center">
            <div class="p-3 bg-green-100 rounded-lg">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm text-gray-500">Completed</p>
              <p class="text-2xl font-bold text-gray-900">{{ completedCourses }}</p>
            </div>
          </div>
        </div>

        <!-- Class Rank -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center">
            <div class="p-3 bg-amber-100 rounded-lg">
              <svg class="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm text-gray-500">Class Rank</p>
              <p class="text-2xl font-bold text-gray-900">
                <span v-if="classRank">{{ classRank.rank }}<span class="text-sm text-gray-500">/{{ classRank.total }}</span></span>
                <span v-else class="text-gray-400">N/A</span>
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <!-- GPA Trend Chart -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">GPA Trend</h3>
          <div class="h-64">
            <canvas ref="gpaTrendChart"></canvas>
          </div>
        </div>

        <!-- Grade Distribution -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Distribution</h3>
          <div class="h-64">
            <canvas ref="gradeDistChart"></canvas>
          </div>
        </div>
      </div>

      <!-- Academic Standing -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-8">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">Academic Standing</h3>
          <span
            class="px-3 py-1 text-sm font-semibold rounded-full"
            :class="getStandingClass(academicStanding)"
          >
            {{ academicStanding }}
          </span>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div
            v-for="standing in standingLevels"
            :key="standing.name"
            class="p-4 rounded-lg border-2 transition-all"
            :class="[
              academicStanding === standing.name
                ? standing.activeClass
                : 'border-gray-200 bg-gray-50'
            ]"
          >
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium" :class="academicStanding === standing.name ? standing.textClass : 'text-gray-600'">
                {{ standing.name }}
              </span>
              <span class="text-xs text-gray-500">{{ standing.range }}</span>
            </div>
            <div class="mt-2 h-1 rounded-full" :class="standing.barClass"></div>
          </div>
        </div>
      </div>

      <!-- Grades by Semester -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h2 class="text-lg font-semibold text-gray-900">Grades by Semester</h2>
          <div class="flex gap-2">
            <button
              @click="expandAllSemesters"
              class="text-sm text-indigo-600 hover:text-indigo-700"
            >
              Expand All
            </button>
            <span class="text-gray-300">|</span>
            <button
              @click="collapseAllSemesters"
              class="text-sm text-indigo-600 hover:text-indigo-700"
            >
              Collapse All
            </button>
          </div>
        </div>

        <div v-if="gradesBySemester.length === 0" class="p-12 text-center">
          <svg class="mx-auto h-16 w-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          <h3 class="mt-4 text-lg font-medium text-gray-900">No grades available</h3>
          <p class="mt-2 text-gray-500">Your grades will appear here once they are posted.</p>
        </div>

        <div v-else class="divide-y divide-gray-200">
          <div
            v-for="semester in gradesBySemester"
            :key="semester.id"
            class="transition-all"
          >
            <!-- Semester Header -->
            <button
              @click="toggleSemester(semester.id)"
              class="w-full px-6 py-4 flex items-center justify-between hover:bg-gray-50 transition-colors"
            >
              <div class="flex items-center gap-4">
                <svg
                  class="w-5 h-5 text-gray-400 transition-transform"
                  :class="{ 'rotate-90': expandedSemesters.includes(semester.id) }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
                <div class="text-left">
                  <h3 class="font-semibold text-gray-900">{{ semester.name }}</h3>
                  <p class="text-sm text-gray-500">{{ semester.grades.length }} courses • {{ semester.totalCredits }} credits</p>
                </div>
              </div>
              <div class="flex items-center gap-6">
                <div class="text-right">
                  <p class="text-sm text-gray-500">Semester GPA</p>
                  <p class="text-xl font-bold" :class="getGPAColor(semester.gpa)">{{ semester.gpa }}</p>
                </div>
                <!-- Mini grade distribution -->
                <div class="flex gap-1">
                  <div
                    v-for="(count, grade) in semester.gradeDistribution"
                    :key="grade"
                    class="w-2 rounded-full"
                    :class="getGradeBarClass(grade)"
                    :style="{ height: `${Math.max(count * 8, 8)}px` }"
                    :title="`${grade}: ${count}`"
                  ></div>
                </div>
              </div>
            </button>

            <!-- Semester Grades Table -->
            <div
              v-show="expandedSemesters.includes(semester.id)"
              class="border-t border-gray-100 bg-gray-50"
            >
              <table class="min-w-full">
                <thead>
                  <tr class="bg-gray-100">
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Credits</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Grade</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Points</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quality Points</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-100">
                  <tr v-for="grade in semester.grades" :key="grade.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4">
                      <div class="flex items-center">
                        <div class="w-10 h-10 bg-indigo-100 rounded-lg flex items-center justify-center mr-3">
                          <span class="text-sm font-bold text-indigo-600">{{ getInitials(grade.courseCode) }}</span>
                        </div>
                        <div>
                          <div class="text-sm font-medium text-gray-900">{{ grade.courseCode }}</div>
                          <div class="text-sm text-gray-500">{{ grade.courseName }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-sm text-gray-900">
                      {{ grade.creditHours }}
                    </td>
                    <td class="px-6 py-4">
                      <span
                        class="px-3 py-1 text-sm font-bold rounded-full"
                        :class="getGradeClass(grade.letterGrade)"
                      >
                        {{ grade.letterGrade }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-sm text-gray-900">
                      {{ grade.gradePoints?.toFixed(2) || '0.00' }}
                    </td>
                    <td class="px-6 py-4 text-sm font-medium text-gray-900">
                      {{ ((grade.gradePoints || 0) * (grade.creditHours || 0)).toFixed(2) }}
                    </td>
                  </tr>
                </tbody>
                <tfoot class="bg-gray-100">
                  <tr>
                    <td class="px-6 py-3 text-sm font-semibold text-gray-900">Semester Total</td>
                    <td class="px-6 py-3 text-sm font-semibold text-gray-900">{{ semester.totalCredits }}</td>
                    <td colspan="2"></td>
                    <td class="px-6 py-3 text-sm font-semibold text-gray-900">{{ semester.totalQualityPoints }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Grade Scale Reference -->
      <div class="mt-8 bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Scale Reference</h3>
        <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
          <div v-for="scale in gradeScale" :key="scale.letter" class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
            <span
              class="w-10 h-10 flex items-center justify-center text-sm font-bold rounded-lg"
              :class="getGradeClass(scale.letter)"
            >
              {{ scale.letter }}
            </span>
            <div>
              <p class="text-sm font-medium text-gray-900">{{ scale.points }}</p>
              <p class="text-xs text-gray-500">{{ scale.range }}</p>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import Chart from 'chart.js/auto'

const authStore = useAuthStore()

// State
const grades = ref([])
const loading = ref(true)
const classRank = ref(null)
const expandedSemesters = ref([])
const gpaTrendChart = ref(null)
const gradeDistChart = ref(null)
let gpaTrendChartInstance = null
let gradeDistChartInstance = null

// Grade scale
const gradeScale = [
  { letter: 'A', points: '4.00', range: '90-100%' },
  { letter: 'A-', points: '3.70', range: '87-89%' },
  { letter: 'B+', points: '3.30', range: '83-86%' },
  { letter: 'B', points: '3.00', range: '80-82%' },
  { letter: 'B-', points: '2.70', range: '77-79%' },
  { letter: 'C+', points: '2.30', range: '73-76%' },
  { letter: 'C', points: '2.00', range: '70-72%' },
  { letter: 'C-', points: '1.70', range: '67-69%' },
  { letter: 'D', points: '1.00', range: '60-66%' },
  { letter: 'F', points: '0.00', range: '<60%' }
]

const standingLevels = [
  { name: "Dean's List", range: 'GPA ≥ 3.50', activeClass: 'border-green-500 bg-green-50', textClass: 'text-green-700', barClass: 'bg-green-500' },
  { name: 'Good Standing', range: 'GPA ≥ 2.00', activeClass: 'border-blue-500 bg-blue-50', textClass: 'text-blue-700', barClass: 'bg-blue-500' },
  { name: 'Academic Warning', range: '1.50 ≤ GPA < 2.00', activeClass: 'border-amber-500 bg-amber-50', textClass: 'text-amber-700', barClass: 'bg-amber-500' },
  { name: 'Academic Probation', range: 'GPA < 1.50', activeClass: 'border-red-500 bg-red-50', textClass: 'text-red-700', barClass: 'bg-red-500' }
]

// Computed
const overallGPA = computed(() => {
  if (grades.value.length === 0) return '0.00'

  let totalPoints = 0
  let totalCredits = 0

  grades.value.forEach(grade => {
    if (grade.gradePoints && grade.creditHours) {
      totalPoints += grade.gradePoints * grade.creditHours
      totalCredits += grade.creditHours
    }
  })

  return totalCredits > 0 ? (totalPoints / totalCredits).toFixed(2) : '0.00'
})

const totalCredits = computed(() => {
  return grades.value.reduce((sum, grade) => sum + (grade.creditHours || 0), 0)
})

const completedCourses = computed(() => {
  return grades.value.filter(g => g.letterGrade && g.letterGrade !== 'F').length
})

const academicStanding = computed(() => {
  const gpa = parseFloat(overallGPA.value)
  if (gpa >= 3.50) return "Dean's List"
  if (gpa >= 2.00) return 'Good Standing'
  if (gpa >= 1.50) return 'Academic Warning'
  return 'Academic Probation'
})

const gradesBySemester = computed(() => {
  const semesters = {}

  grades.value.forEach(grade => {
    const semesterName = grade.semester || 'No Semester'
    if (!semesters[semesterName]) {
      semesters[semesterName] = {
        id: semesterName,
        name: semesterName,
        grades: [],
        gradeDistribution: {}
      }
    }
    semesters[semesterName].grades.push(grade)

    // Count grades for distribution
    const letter = grade.letterGrade || 'N/A'
    semesters[semesterName].gradeDistribution[letter] = (semesters[semesterName].gradeDistribution[letter] || 0) + 1
  })

  // Calculate GPA and totals for each semester
  Object.values(semesters).forEach(semester => {
    let totalPoints = 0
    let totalCredits = 0

    semester.grades.forEach(grade => {
      if (grade.gradePoints && grade.creditHours) {
        totalPoints += grade.gradePoints * grade.creditHours
        totalCredits += grade.creditHours
      }
    })

    semester.gpa = totalCredits > 0 ? (totalPoints / totalCredits).toFixed(2) : '0.00'
    semester.totalCredits = totalCredits
    semester.totalQualityPoints = totalPoints.toFixed(2)
  })

  return Object.values(semesters).sort((a, b) => a.name.localeCompare(b.name))
})

const gradeDistribution = computed(() => {
  const distribution = {}
  grades.value.forEach(grade => {
    const letter = grade.letterGrade || 'N/A'
    distribution[letter] = (distribution[letter] || 0) + 1
  })
  return distribution
})

// Methods
const loadGrades = async () => {
  try {
    loading.value = true
    const response = await api.getStudentGrades(authStore.userId)
    grades.value = (response.data || []).map(grade => ({
      ...grade,
      courseCode: grade.course?.courseCode || grade.enrollment?.course?.courseCode || grade.courseCode || 'N/A',
      courseName: grade.course?.courseName || grade.enrollment?.course?.courseName || grade.courseName || 'N/A',
      creditHours: grade.course?.credits || grade.enrollment?.course?.creditHours || grade.creditHours || 0,
      letterGrade: grade.gradeValue || grade.letterGrade || 'N/A',
      semester: grade.enrollment?.semester?.name || grade.semester || 'Current Semester'
    }))

    // Expand first semester by default
    if (gradesBySemester.value.length > 0) {
      expandedSemesters.value = [gradesBySemester.value[0].id]
    }

    // Try to get class rank
    await loadClassRank()
  } catch (error) {
    console.error('Error loading grades:', error)
  } finally {
    loading.value = false
    await nextTick()
    initCharts()
  }
}

const loadClassRank = async () => {
  try {
    const response = await api.getStudentGPASummary(authStore.userId)
    if (response.data?.rank) {
      classRank.value = response.data
    }
  } catch (error) {
    // Class rank not available - this is expected if the API doesn't support it
    classRank.value = null
  }
}

const initCharts = () => {
  if (gpaTrendChart.value && gradesBySemester.value.length > 0) {
    if (gpaTrendChartInstance) gpaTrendChartInstance.destroy()

    const labels = gradesBySemester.value.map(s => s.name)
    const data = gradesBySemester.value.map(s => parseFloat(s.gpa))

    gpaTrendChartInstance = new Chart(gpaTrendChart.value, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Semester GPA',
          data,
          borderColor: 'rgb(99, 102, 241)',
          backgroundColor: 'rgba(99, 102, 241, 0.1)',
          tension: 0.3,
          fill: true,
          pointBackgroundColor: 'rgb(99, 102, 241)',
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
          pointRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: {
            min: 0,
            max: 4,
            ticks: { stepSize: 0.5 }
          }
        }
      }
    })
  }

  if (gradeDistChart.value && Object.keys(gradeDistribution.value).length > 0) {
    if (gradeDistChartInstance) gradeDistChartInstance.destroy()

    const orderedGrades = ['A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D', 'F']
    const labels = orderedGrades.filter(g => gradeDistribution.value[g])
    const data = labels.map(g => gradeDistribution.value[g])
    const colors = labels.map(g => getChartColor(g))

    gradeDistChartInstance = new Chart(gradeDistChart.value, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data,
          backgroundColor: colors,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: { usePointStyle: true }
          }
        }
      }
    })
  }
}

const getChartColor = (grade) => {
  const colors = {
    'A': '#10B981', 'A-': '#34D399',
    'B+': '#3B82F6', 'B': '#60A5FA', 'B-': '#93C5FD',
    'C+': '#F59E0B', 'C': '#FBBF24', 'C-': '#FCD34D',
    'D': '#F97316',
    'F': '#EF4444'
  }
  return colors[grade] || '#9CA3AF'
}

const toggleSemester = (id) => {
  const index = expandedSemesters.value.indexOf(id)
  if (index > -1) {
    expandedSemesters.value.splice(index, 1)
  } else {
    expandedSemesters.value.push(id)
  }
}

const expandAllSemesters = () => {
  expandedSemesters.value = gradesBySemester.value.map(s => s.id)
}

const collapseAllSemesters = () => {
  expandedSemesters.value = []
}

const getGradeClass = (letterGrade) => {
  const classes = {
    'A': 'bg-green-100 text-green-800',
    'A-': 'bg-green-100 text-green-700',
    'B+': 'bg-blue-100 text-blue-800',
    'B': 'bg-blue-100 text-blue-700',
    'B-': 'bg-blue-100 text-blue-600',
    'C+': 'bg-amber-100 text-amber-800',
    'C': 'bg-amber-100 text-amber-700',
    'C-': 'bg-amber-100 text-amber-600',
    'D': 'bg-orange-100 text-orange-800',
    'F': 'bg-red-100 text-red-800'
  }
  return classes[letterGrade] || 'bg-gray-100 text-gray-800'
}

const getGradeBarClass = (grade) => {
  const classes = {
    'A': 'bg-green-500', 'A-': 'bg-green-400',
    'B+': 'bg-blue-500', 'B': 'bg-blue-400', 'B-': 'bg-blue-300',
    'C+': 'bg-amber-500', 'C': 'bg-amber-400', 'C-': 'bg-amber-300',
    'D': 'bg-orange-500',
    'F': 'bg-red-500'
  }
  return classes[grade] || 'bg-gray-400'
}

const getGPAColor = (gpa) => {
  const value = parseFloat(gpa)
  if (value >= 3.50) return 'text-green-600'
  if (value >= 3.00) return 'text-blue-600'
  if (value >= 2.00) return 'text-amber-600'
  return 'text-red-600'
}

const getStandingClass = (standing) => {
  const classes = {
    "Dean's List": 'bg-green-100 text-green-700',
    'Good Standing': 'bg-blue-100 text-blue-700',
    'Academic Warning': 'bg-amber-100 text-amber-700',
    'Academic Probation': 'bg-red-100 text-red-700'
  }
  return classes[standing] || 'bg-gray-100 text-gray-700'
}

const getInitials = (code) => {
  if (!code) return '??'
  return code.replace(/[0-9]/g, '').slice(0, 2).toUpperCase()
}

onMounted(() => {
  loadGrades()
})

watch(grades, () => {
  nextTick(() => initCharts())
})
</script>
