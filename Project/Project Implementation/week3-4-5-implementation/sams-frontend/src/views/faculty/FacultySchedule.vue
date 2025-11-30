<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header -->
    <div class="mb-6 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Teaching Schedule</h1>
        <p class="text-gray-600 mt-2">View and manage your teaching schedule for the semester</p>
      </div>
      <div class="flex gap-3">
        <button
          v-for="mode in viewModes"
          :key="mode.id"
          @click="viewMode = mode.id"
          :class="viewMode === mode.id ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border border-gray-200'"
          class="px-4 py-2 rounded-lg font-medium transition-all shadow-sm hover:shadow"
        >
          {{ mode.label }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- No Schedule -->
    <div v-else-if="scheduleItems.length === 0" class="bg-white rounded-xl shadow-sm p-12 text-center">
      <svg class="mx-auto h-16 w-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No classes scheduled</h3>
      <p class="mt-2 text-gray-500">You don't have any courses assigned for this semester.</p>
    </div>

    <template v-else>
      <!-- Calendar Controls -->
      <div class="bg-white rounded-xl shadow-sm p-4 mb-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <button @click="navigateCalendar(-1)" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="h-5 w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <h2 class="text-xl font-semibold text-gray-900">
              {{ viewMode === 'month' ? currentMonthYear : currentWeekRange }}
            </h2>
            <button @click="navigateCalendar(1)" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="h-5 w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
          <div class="flex items-center gap-4">
            <button @click="goToToday" class="px-4 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors font-medium">
              Today
            </button>
            <!-- Course Legend -->
            <div class="flex items-center gap-3">
              <div v-for="course in uniqueCourses" :key="course.id" class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full" :style="{ backgroundColor: course.color }"></div>
                <span class="text-sm text-gray-600">{{ course.courseCode }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Month View -->
      <div v-if="viewMode === 'month'" class="bg-white rounded-xl shadow-sm overflow-hidden">
        <!-- Day Headers -->
        <div class="grid grid-cols-7 bg-gray-50 border-b border-gray-200">
          <div v-for="day in weekDays" :key="day" class="px-4 py-3 text-center text-sm font-semibold text-gray-700">
            {{ day }}
          </div>
        </div>

        <!-- Calendar Grid -->
        <div class="grid grid-cols-7">
          <div
            v-for="(date, index) in calendarDates"
            :key="index"
            class="min-h-[120px] border-b border-r border-gray-100 p-2 transition-colors"
            :class="[
              !date.isCurrentMonth ? 'bg-gray-50 text-gray-400' : 'bg-white',
              date.isToday ? 'bg-blue-50' : ''
            ]"
          >
            <div class="flex items-center justify-between mb-2">
              <span
                class="text-sm font-medium w-7 h-7 flex items-center justify-center rounded-full"
                :class="date.isToday ? 'bg-blue-600 text-white' : ''"
              >
                {{ date.day }}
              </span>
            </div>
            <!-- Events for this date -->
            <div class="space-y-1">
              <div
                v-for="event in getEventsForDate(date.fullDate)"
                :key="event.id"
                @click="showClassDetails(event)"
                class="px-2 py-1 rounded text-xs font-medium cursor-pointer hover:opacity-80 transition-opacity truncate"
                :style="{ backgroundColor: event.color + '20', color: event.color }"
              >
                {{ event.startTime }} {{ event.courseCode }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Week View -->
      <div v-if="viewMode === 'week'" class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <div class="min-w-[900px]">
            <!-- Time and Day Headers -->
            <div class="grid grid-cols-8 bg-gray-50 border-b border-gray-200">
              <div class="px-4 py-3 text-center text-sm font-semibold text-gray-500 border-r border-gray-200">
                Time
              </div>
              <div
                v-for="(day, index) in currentWeekDates"
                :key="index"
                class="px-4 py-3 text-center border-r border-gray-200"
                :class="day.isToday ? 'bg-blue-50' : ''"
              >
                <div class="text-sm font-semibold text-gray-700">{{ day.dayName }}</div>
                <div
                  class="text-lg font-bold mt-1 w-8 h-8 flex items-center justify-center mx-auto rounded-full"
                  :class="day.isToday ? 'bg-blue-600 text-white' : 'text-gray-900'"
                >
                  {{ day.date }}
                </div>
              </div>
            </div>

            <!-- Time Grid -->
            <div class="relative">
              <div v-for="hour in hoursOfDay" :key="hour" class="grid grid-cols-8 border-b border-gray-100">
                <div class="px-4 py-6 text-right text-sm text-gray-500 border-r border-gray-200 bg-gray-50">
                  {{ formatHour(hour) }}
                </div>
                <div
                  v-for="(day, dayIndex) in currentWeekDates"
                  :key="dayIndex"
                  class="relative border-r border-gray-100 min-h-[60px]"
                  :class="day.isToday ? 'bg-blue-50/30' : ''"
                >
                  <!-- Class blocks -->
                  <div
                    v-for="event in getEventsForHour(day.fullDate, hour)"
                    :key="event.id"
                    @click="showClassDetails(event)"
                    class="absolute left-1 right-1 rounded-lg p-2 cursor-pointer hover:shadow-lg transition-all z-10 border-l-4"
                    :style="{
                      backgroundColor: event.color + '15',
                      borderColor: event.color,
                      top: getEventTopPosition(event) + 'px',
                      height: getEventHeight(event) + 'px'
                    }"
                  >
                    <div class="font-semibold text-sm" :style="{ color: event.color }">
                      {{ event.courseCode }}
                    </div>
                    <div class="text-xs text-gray-600 truncate">{{ event.courseName }}</div>
                    <div class="text-xs text-gray-500">{{ event.startTime }} - {{ event.endTime }}</div>
                  </div>
                </div>
              </div>

              <!-- Current time indicator -->
              <div
                v-if="showCurrentTimeIndicator"
                class="absolute left-0 right-0 flex items-center z-20 pointer-events-none"
                :style="{ top: currentTimePosition + 'px' }"
              >
                <div class="w-24 text-xs text-red-500 font-medium text-right pr-2">{{ currentTime }}</div>
                <div class="flex-1 h-0.5 bg-red-500"></div>
                <div class="w-2 h-2 rounded-full bg-red-500"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Grid View (Original Weekly Grid) -->
      <div v-if="viewMode === 'grid'" class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-24 border-r border-gray-200">
                  Time
                </th>
                <th v-for="day in daysOfWeek" :key="day" class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider border-r border-gray-200">
                  {{ day }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="timeSlot in timeSlots" :key="timeSlot" class="border-t border-gray-200">
                <td class="px-4 py-3 text-sm font-medium text-gray-700 whitespace-nowrap bg-gray-50 border-r border-gray-200">
                  {{ formatTimeSlot(timeSlot) }}
                </td>
                <td v-for="day in daysOfWeek" :key="day" class="px-2 py-2 align-top border-r border-gray-100 min-h-[80px]">
                  <div
                    v-if="getClassForSlot(day, timeSlot)"
                    :style="{ backgroundColor: getClassForSlot(day, timeSlot).color + '15', borderColor: getClassForSlot(day, timeSlot).color }"
                    class="p-3 rounded-lg border-l-4 cursor-pointer hover:shadow-md transition-all"
                    @click="showClassDetails(getClassForSlot(day, timeSlot))"
                  >
                    <div class="font-semibold text-sm" :style="{ color: getClassForSlot(day, timeSlot).color }">
                      {{ getClassForSlot(day, timeSlot).courseCode }}
                    </div>
                    <div class="text-xs text-gray-600 mt-1 truncate">
                      {{ getClassForSlot(day, timeSlot).courseName }}
                    </div>
                    <div class="text-xs text-gray-500 mt-1">
                      {{ getClassForSlot(day, timeSlot).startTime }} - {{ getClassForSlot(day, timeSlot).endTime }}
                    </div>
                    <div class="flex items-center gap-1 mt-2 text-xs text-gray-500">
                      <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                      </svg>
                      {{ getClassForSlot(day, timeSlot).enrolledCount || 0 }} students
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- List View -->
      <div v-if="viewMode === 'list'" class="space-y-4">
        <div v-for="day in daysOfWeek" :key="day" class="bg-white rounded-xl shadow-sm overflow-hidden">
          <div class="px-6 py-4 bg-gradient-to-r from-gray-50 to-white border-b border-gray-200 flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">{{ day }}</h3>
            <span class="px-3 py-1 bg-gray-100 text-gray-600 text-sm rounded-full font-medium">
              {{ getClassesForDay(day).length }} {{ getClassesForDay(day).length === 1 ? 'class' : 'classes' }}
            </span>
          </div>
          <div class="p-6">
            <div v-if="getClassesForDay(day).length === 0" class="text-center text-gray-500 py-4">
              <svg class="mx-auto h-8 w-8 text-gray-300 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" />
              </svg>
              No classes scheduled
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="classItem in getClassesForDay(day)"
                :key="classItem.id"
                :style="{ borderLeftColor: classItem.color }"
                class="flex items-center justify-between p-4 border-l-4 rounded-xl bg-gray-50 hover:bg-gray-100 transition-all cursor-pointer group"
                @click="showClassDetails(classItem)"
              >
                <div class="flex-1">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-lg flex items-center justify-center" :style="{ backgroundColor: classItem.color + '20' }">
                      <span class="font-bold text-sm" :style="{ color: classItem.color }">{{ classItem.courseCode.slice(0, 2) }}</span>
                    </div>
                    <div>
                      <span class="font-semibold text-gray-900">{{ classItem.courseCode }}</span>
                      <span class="text-gray-500 mx-2">-</span>
                      <span class="text-gray-700">{{ classItem.courseName }}</span>
                    </div>
                  </div>
                  <div class="mt-3 flex items-center gap-6 text-sm text-gray-600">
                    <span class="flex items-center gap-2">
                      <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {{ classItem.startTime }} - {{ classItem.endTime }}
                    </span>
                    <span class="flex items-center gap-2">
                      <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                      </svg>
                      {{ classItem.enrolledCount || 0 }} / {{ classItem.capacity }} students
                    </span>
                    <span class="flex items-center gap-2">
                      <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                      {{ classItem.credits }} Credits
                    </span>
                  </div>
                </div>
                <div class="opacity-0 group-hover:opacity-100 transition-opacity">
                  <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Schedule Summary Cards -->
      <div class="mt-6 grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-3">
            <div class="p-3 bg-blue-100 rounded-lg">
              <svg class="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-600">Total Courses</p>
              <p class="text-2xl font-bold text-gray-900">{{ uniqueCourses.length }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-3">
            <div class="p-3 bg-green-100 rounded-lg">
              <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-600">Classes/Week</p>
              <p class="text-2xl font-bold text-gray-900">{{ scheduleItems.length }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-3">
            <div class="p-3 bg-purple-100 rounded-lg">
              <svg class="h-6 w-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-600">Total Students</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalStudents }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-3">
            <div class="p-3 bg-orange-100 rounded-lg">
              <svg class="h-6 w-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-600">Teaching Hours</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalTeachingHours }}h</p>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- Class Details Modal -->
    <div v-if="selectedClass" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click="selectedClass = null">
      <div class="bg-white rounded-2xl shadow-xl max-w-lg w-full" @click.stop>
        <div class="p-6 border-b border-gray-200">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-xl flex items-center justify-center" :style="{ backgroundColor: selectedClass.color + '20' }">
              <span class="font-bold text-lg" :style="{ color: selectedClass.color }">
                {{ selectedClass.courseCode.slice(0, 2) }}
              </span>
            </div>
            <div>
              <h3 class="text-xl font-bold text-gray-900">{{ selectedClass.courseCode }}</h3>
              <p class="text-gray-600">{{ selectedClass.courseName }}</p>
            </div>
          </div>
        </div>

        <div class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-gray-50 rounded-lg p-4">
              <p class="text-sm text-gray-500">Schedule</p>
              <p class="font-semibold text-gray-900 mt-1">{{ selectedClass.daysOfWeek }}</p>
              <p class="text-gray-700">{{ selectedClass.startTime }} - {{ selectedClass.endTime }}</p>
            </div>
            <div class="bg-gray-50 rounded-lg p-4">
              <p class="text-sm text-gray-500">Enrollment</p>
              <p class="font-semibold text-gray-900 mt-1">{{ selectedClass.enrolledCount || 0 }} / {{ selectedClass.capacity }}</p>
              <p class="text-gray-700">students enrolled</p>
            </div>
          </div>

          <div class="bg-gray-50 rounded-lg p-4">
            <p class="text-sm text-gray-500">Credits</p>
            <p class="font-semibold text-gray-900 mt-1">{{ selectedClass.credits }} Credit Hours</p>
          </div>

          <div v-if="selectedClass.description" class="bg-gray-50 rounded-lg p-4">
            <p class="text-sm text-gray-500">Description</p>
            <p class="text-gray-700 mt-1">{{ selectedClass.description }}</p>
          </div>
        </div>

        <div class="p-6 bg-gray-50 rounded-b-2xl flex justify-end gap-3">
          <button @click="selectedClass = null" class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
            Close
          </button>
          <button @click="viewCourseDetails" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
            View Course Details
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'

const router = useRouter()
const authStore = useAuthStore()
const userId = computed(() => authStore.userId)

const loading = ref(false)
const courses = ref([])
const scheduleItems = ref([])
const viewMode = ref('week')
const selectedClass = ref(null)
const currentDate = ref(new Date())
const currentTimePosition = ref(0)
const currentTime = ref('')
let timeInterval = null

const viewModes = [
  { id: 'month', label: 'Month' },
  { id: 'week', label: 'Week' },
  { id: 'grid', label: 'Grid' },
  { id: 'list', label: 'List' }
]

const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']
const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
const dayMapping = {
  'M': 'Monday',
  'T': 'Tuesday',
  'W': 'Wednesday',
  'Th': 'Thursday',
  'F': 'Friday'
}

const timeSlots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00']
const hoursOfDay = [8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18]

const courseColors = [
  '#3B82F6', '#10B981', '#8B5CF6', '#F59E0B',
  '#EF4444', '#06B6D4', '#EC4899', '#84CC16'
]

// Computed properties
const uniqueCourses = computed(() => {
  const seen = new Set()
  return scheduleItems.value.filter(item => {
    if (seen.has(item.courseId)) return false
    seen.add(item.courseId)
    return true
  })
})

const totalStudents = computed(() => {
  return uniqueCourses.value.reduce((sum, course) => sum + (course.enrolledCount || 0), 0)
})

const totalTeachingHours = computed(() => {
  let hours = 0
  scheduleItems.value.forEach(item => {
    const start = parseInt(item.startTime.split(':')[0])
    const end = parseInt(item.endTime.split(':')[0])
    hours += (end - start)
  })
  return hours
})

const currentMonthYear = computed(() => {
  return currentDate.value.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
})

const currentWeekRange = computed(() => {
  const startOfWeek = getStartOfWeek(currentDate.value)
  const endOfWeek = new Date(startOfWeek)
  endOfWeek.setDate(endOfWeek.getDate() + 6)

  const startMonth = startOfWeek.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
  const endMonth = endOfWeek.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })

  return `${startMonth} - ${endMonth}`
})

const calendarDates = computed(() => {
  const dates = []
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()

  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)

  // Start from Sunday of the week containing the first day
  const startDate = new Date(firstDay)
  startDate.setDate(startDate.getDate() - startDate.getDay())

  // Generate 42 days (6 weeks)
  for (let i = 0; i < 42; i++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + i)

    const today = new Date()
    const isToday = date.toDateString() === today.toDateString()

    dates.push({
      day: date.getDate(),
      fullDate: date,
      isCurrentMonth: date.getMonth() === month,
      isToday
    })
  }

  return dates
})

const currentWeekDates = computed(() => {
  const dates = []
  const startOfWeek = getStartOfWeek(currentDate.value)

  for (let i = 0; i < 7; i++) {
    const date = new Date(startOfWeek)
    date.setDate(date.getDate() + i)

    const today = new Date()
    const isToday = date.toDateString() === today.toDateString()

    dates.push({
      date: date.getDate(),
      dayName: date.toLocaleDateString('en-US', { weekday: 'short' }),
      fullDate: date,
      isToday
    })
  }

  return dates
})

const showCurrentTimeIndicator = computed(() => {
  const now = new Date()
  const hour = now.getHours()
  return hour >= 8 && hour <= 18
})

// Methods
async function fetchSchedule() {
  loading.value = true
  try {
    const response = await api.get(`/courses/instructor/${userId.value}`)
    courses.value = response.data

    for (let course of courses.value) {
      try {
        const countResponse = await api.get(`/enrollments/course/${course.id}/count`)
        course.enrolledCount = countResponse.data
      } catch (error) {
        course.enrolledCount = 0
      }
    }

    const items = []
    for (let i = 0; i < courses.value.length; i++) {
      const course = courses.value[i]

      if (course.daysOfWeek && course.startTime && course.endTime) {
        const days = parseDaysOfWeek(course.daysOfWeek)

        days.forEach(day => {
          items.push({
            id: `${course.id}-${day}`,
            courseId: course.id,
            courseCode: course.courseCode,
            courseName: course.courseName,
            description: course.description,
            startTime: formatTime(course.startTime),
            endTime: formatTime(course.endTime),
            day: day,
            daysOfWeek: course.daysOfWeek,
            enrolledCount: course.enrolledCount,
            capacity: course.capacity,
            credits: course.credits,
            color: courseColors[i % courseColors.length]
          })
        })
      }
    }

    scheduleItems.value = items
  } catch (error) {
    console.error('Error fetching schedule:', error)
  } finally {
    loading.value = false
  }
}

function parseDaysOfWeek(daysString) {
  const days = []
  let i = 0

  while (i < daysString.length) {
    if (i < daysString.length - 1 && daysString.substring(i, i + 2) === 'Th') {
      days.push(dayMapping['Th'])
      i += 2
    } else {
      const char = daysString[i]
      if (dayMapping[char]) {
        days.push(dayMapping[char])
      }
      i++
    }
  }

  return days
}

function formatTime(time) {
  if (!time) return ''
  if (typeof time === 'string') return time.substring(0, 5)
  if (Array.isArray(time)) {
    const hour = time[0].toString().padStart(2, '0')
    const minute = time[1].toString().padStart(2, '0')
    return `${hour}:${minute}`
  }
  return time
}

function formatTimeSlot(slot) {
  const hour = parseInt(slot.split(':')[0])
  const ampm = hour >= 12 ? 'PM' : 'AM'
  const displayHour = hour > 12 ? hour - 12 : hour
  return `${displayHour}:00 ${ampm}`
}

function formatHour(hour) {
  const ampm = hour >= 12 ? 'PM' : 'AM'
  const displayHour = hour > 12 ? hour - 12 : (hour === 0 ? 12 : hour)
  return `${displayHour}:00 ${ampm}`
}

function getStartOfWeek(date) {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day
  return new Date(d.setDate(diff))
}

function navigateCalendar(direction) {
  const newDate = new Date(currentDate.value)
  if (viewMode.value === 'month') {
    newDate.setMonth(newDate.getMonth() + direction)
  } else {
    newDate.setDate(newDate.getDate() + (direction * 7))
  }
  currentDate.value = newDate
}

function goToToday() {
  currentDate.value = new Date()
}

function getEventsForDate(date) {
  const dayName = date.toLocaleDateString('en-US', { weekday: 'long' })
  return scheduleItems.value.filter(item => item.day === dayName)
}

function getEventsForHour(date, hour) {
  const dayName = date.toLocaleDateString('en-US', { weekday: 'long' })
  return scheduleItems.value.filter(item => {
    if (item.day !== dayName) return false
    const startHour = parseInt(item.startTime.split(':')[0])
    return startHour === hour
  })
}

function getEventTopPosition(event) {
  const startMinutes = parseInt(event.startTime.split(':')[1]) || 0
  return (startMinutes / 60) * 60 // 60px per hour
}

function getEventHeight(event) {
  const startHour = parseInt(event.startTime.split(':')[0])
  const startMin = parseInt(event.startTime.split(':')[1]) || 0
  const endHour = parseInt(event.endTime.split(':')[0])
  const endMin = parseInt(event.endTime.split(':')[1]) || 0

  const durationInMinutes = (endHour * 60 + endMin) - (startHour * 60 + startMin)
  return (durationInMinutes / 60) * 60 - 4 // 60px per hour, minus padding
}

function getClassForSlot(day, timeSlot) {
  return scheduleItems.value.find(item => {
    if (item.day !== day) return false
    const slotHour = parseInt(timeSlot.split(':')[0])
    const classStartHour = parseInt(item.startTime.split(':')[0])
    const classEndHour = parseInt(item.endTime.split(':')[0])
    return slotHour >= classStartHour && slotHour < classEndHour
  })
}

function getClassesForDay(day) {
  return scheduleItems.value
    .filter(item => item.day === day)
    .sort((a, b) => a.startTime.localeCompare(b.startTime))
}

function showClassDetails(classItem) {
  selectedClass.value = classItem
}

function viewCourseDetails() {
  selectedClass.value = null
  router.push({ name: 'FacultyCourses' })
}

function updateCurrentTime() {
  const now = new Date()
  const hours = now.getHours()
  const minutes = now.getMinutes()

  currentTime.value = now.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' })

  // Calculate position (60px per hour, starting from 8am)
  const minutesSince8am = (hours - 8) * 60 + minutes
  currentTimePosition.value = (minutesSince8am / 60) * 60 + 48 // 48px for header
}

onMounted(() => {
  fetchSchedule()
  updateCurrentTime()
  timeInterval = setInterval(updateCurrentTime, 60000) // Update every minute
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
table {
  border-collapse: separate;
  border-spacing: 0;
}

td {
  min-height: 80px;
}
</style>
