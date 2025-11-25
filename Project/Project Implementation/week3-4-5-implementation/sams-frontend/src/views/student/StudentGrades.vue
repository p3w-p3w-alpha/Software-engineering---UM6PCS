<template>
  <div class="max-w-7xl mx-auto">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">My Grades</h1>
      <p class="mt-2 text-gray-600">View your academic performance and GPA</p>
    </div>

    <!-- GPA Summary -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg shadow p-6 text-white">
        <p class="text-sm font-medium opacity-90">Overall GPA</p>
        <p class="text-4xl font-bold mt-2">{{ overallGPA }}</p>
      </div>
      <div class="bg-white rounded-lg shadow p-6">
        <p class="text-sm font-medium text-gray-600">Total Credits</p>
        <p class="text-3xl font-bold text-gray-900 mt-2">{{ totalCredits }}</p>
      </div>
      <div class="bg-white rounded-lg shadow p-6">
        <p class="text-sm font-medium text-gray-600">Courses Completed</p>
        <p class="text-3xl font-bold text-gray-900 mt-2">{{ completedCourses }}</p>
      </div>
    </div>

    <!-- Grades by Semester -->
    <div class="bg-white rounded-lg shadow">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-900">Grades by Semester</h2>
      </div>
      <div class="p-6">
        <div v-if="loading" class="flex justify-center py-8">
          <LoadingSpinner />
        </div>

        <div v-else-if="gradesBySemester.length === 0" class="text-center py-8 text-gray-500">
          <p>No grades available</p>
        </div>

        <div v-else class="space-y-6">
          <div
            v-for="semester in gradesBySemester"
            :key="semester.id"
            class="border border-gray-200 rounded-lg overflow-hidden"
          >
            <div class="bg-gray-50 px-6 py-3 flex items-center justify-between">
              <h3 class="font-semibold text-gray-900">{{ semester.name }}</h3>
              <span class="text-sm text-gray-600">GPA: <span class="font-semibold">{{ semester.gpa }}</span></span>
            </div>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Course</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Credits</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Grade</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Grade Points</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="grade in semester.grades" :key="grade.id">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="text-sm font-medium text-gray-900">{{ grade.courseCode }}</div>
                      <div class="text-sm text-gray-500">{{ grade.courseName }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ grade.creditHours }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        class="px-3 py-1 text-sm font-semibold rounded-full"
                        :class="getGradeClass(grade.letterGrade)"
                      >
                        {{ grade.letterGrade }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ grade.gradePoints }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
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

const grades = ref([])
const loading = ref(false)

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

const gradesBySemester = computed(() => {
  const semesters = {}

  grades.value.forEach(grade => {
    const semesterName = grade.semester || 'No Semester'
    if (!semesters[semesterName]) {
      semesters[semesterName] = {
        id: semesterName,
        name: semesterName,
        grades: []
      }
    }
    semesters[semesterName].grades.push(grade)
  })

  // Calculate GPA for each semester
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
  })

  return Object.values(semesters)
})

const loadGrades = async () => {
  try {
    loading.value = true
    const response = await api.getStudentGrades(authStore.userId)
    grades.value = response.data.map(grade => ({
      ...grade,
      courseCode: grade.enrollment?.course?.courseCode || 'N/A',
      courseName: grade.enrollment?.course?.courseName || 'N/A',
      creditHours: grade.enrollment?.course?.creditHours || 0,
      semester: grade.enrollment?.course?.semester?.name || 'No Semester'
    }))
  } catch (error) {
    console.error('Error loading grades:', error)
  } finally {
    loading.value = false
  }
}

const getGradeClass = (letterGrade) => {
  const classes = {
    'A': 'bg-green-100 text-green-800',
    'A-': 'bg-green-100 text-green-800',
    'B+': 'bg-blue-100 text-blue-800',
    'B': 'bg-blue-100 text-blue-800',
    'B-': 'bg-blue-100 text-blue-800',
    'C+': 'bg-yellow-100 text-yellow-800',
    'C': 'bg-yellow-100 text-yellow-800',
    'C-': 'bg-yellow-100 text-yellow-800',
    'D': 'bg-orange-100 text-orange-800',
    'F': 'bg-red-100 text-red-800'
  }
  return classes[letterGrade] || 'bg-gray-100 text-gray-800'
}

onMounted(() => {
  loadGrades()
})
</script>
