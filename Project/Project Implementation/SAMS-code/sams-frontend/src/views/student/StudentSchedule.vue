<template>
  <div class="max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">My Schedule</h1>
        <p class="text-gray-600 mt-1">View and manage your class schedule</p>
      </div>
      <div class="flex flex-wrap gap-3">
        <!-- View Mode Toggle -->
        <div class="flex bg-gray-100 rounded-lg p-1">
          <button
            v-for="mode in viewModes"
            :key="mode.id"
            @click="viewMode = mode.id"
            :class="[
              'px-4 py-2 rounded-md text-sm font-medium transition-colors',
              viewMode === mode.id
                ? 'bg-white text-blue-600 shadow'
                : 'text-gray-600 hover:text-gray-900'
            ]"
          >
            {{ mode.label }}
          </button>
        </div>
        <!-- Actions -->
        <button
          @click="exportSchedule"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          Export
        </button>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-blue-100 rounded-lg">
            <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
          <div>
            <p class="text-sm text-gray-500">Total Courses</p>
            <p class="text-xl font-bold text-gray-900">{{ uniqueCourses.length }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-green-100 rounded-lg">
            <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <p class="text-sm text-gray-500">Weekly Hours</p>
            <p class="text-xl font-bold text-gray-900">{{ totalWeeklyHours }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-purple-100 rounded-lg">
            <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <div>
            <p class="text-sm text-gray-500">Total Credits</p>
            <p class="text-xl font-bold text-gray-900">{{ totalCredits }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-yellow-100 rounded-lg">
            <svg class="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <div>
            <p class="text-sm text-gray-500">Today's Classes</p>
            <p class="text-xl font-bold text-gray-900">{{ todayClasses.length }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- No Schedule -->
    <div v-else-if="scheduleItems.length === 0" class="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
      <svg class="mx-auto h-16 w-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No classes scheduled</h3>
      <p class="mt-2 text-gray-500">You don't have any classes scheduled this semester.</p>
      <router-link to="/student/courses/browse" class="mt-6 inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
        Browse Courses
      </router-link>
    </div>

    <template v-else>
      <!-- Calendar Navigation -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 mb-6">
        <div class="px-6 py-4 flex items-center justify-between border-b border-gray-200">
          <div class="flex items-center gap-4">
            <button
              @click="navigatePeriod(-1)"
              class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <h2 class="text-lg font-semibold text-gray-900">{{ currentPeriodLabel }}</h2>
            <button
              @click="navigatePeriod(1)"
              class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
          <button
            @click="goToToday"
            class="px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
          >
            Today
          </button>
        </div>

        <!-- Week View -->
        <div v-if="viewMode === 'week'" class="overflow-x-auto">
          <table class="w-full min-w-[800px]">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-20 sticky left-0 bg-gray-50 z-10">
                  Time
                </th>
                <th
                  v-for="(day, index) in weekDays"
                  :key="index"
                  class="px-4 py-3 text-center text-xs font-medium uppercase tracking-wider min-w-[140px]"
                  :class="isToday(day.date) ? 'bg-blue-50 text-blue-600' : 'text-gray-500'"
                >
                  <div class="flex flex-col items-center">
                    <span>{{ day.name }}</span>
                    <span
                      :class="[
                        'mt-1 w-8 h-8 flex items-center justify-center rounded-full text-sm font-semibold',
                        isToday(day.date) ? 'bg-blue-600 text-white' : 'text-gray-700'
                      ]"
                    >
                      {{ day.date.getDate() }}
                    </span>
                  </div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="timeSlot in timeSlots" :key="timeSlot" class="border-t border-gray-100">
                <td class="px-4 py-1 text-xs font-medium text-gray-500 whitespace-nowrap sticky left-0 bg-white z-10 h-16 align-top pt-2">
                  {{ formatTimeSlot(timeSlot) }}
                </td>
                <td
                  v-for="(day, index) in weekDays"
                  :key="index"
                  class="px-1 py-1 align-top h-16 border-l border-gray-100"
                  :class="isToday(day.date) ? 'bg-blue-50/30' : ''"
                >
                  <div
                    v-for="classItem in getClassesForSlot(day.name, timeSlot)"
                    :key="classItem.id"
                    :style="{
                      backgroundColor: classItem.color + '15',
                      borderColor: classItem.color,
                      height: `${classItem.duration * 64}px`
                    }"
                    class="px-2 py-1 rounded-lg border-l-4 cursor-pointer hover:shadow-md transition-all text-xs overflow-hidden"
                    @click="showClassDetails(classItem)"
                  >
                    <div class="font-semibold truncate" :style="{ color: classItem.color }">
                      {{ classItem.courseCode }}
                    </div>
                    <div class="text-gray-600 truncate text-[10px]">
                      {{ classItem.startTime }} - {{ classItem.endTime }}
                    </div>
                    <div v-if="classItem.room" class="text-gray-500 truncate text-[10px]">
                      {{ classItem.room }}
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Month View -->
        <div v-else-if="viewMode === 'month'" class="p-4">
          <div class="grid grid-cols-7 gap-1 mb-2">
            <div v-for="day in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']" :key="day" class="text-center text-xs font-medium text-gray-500 uppercase py-2">
              {{ day }}
            </div>
          </div>
          <div class="grid grid-cols-7 gap-1">
            <div
              v-for="(day, index) in monthDays"
              :key="index"
              :class="[
                'min-h-[100px] p-2 rounded-lg border transition-colors cursor-pointer',
                day.isCurrentMonth ? 'bg-white hover:bg-gray-50' : 'bg-gray-50 text-gray-400',
                isToday(day.date) ? 'border-blue-500 border-2' : 'border-gray-200'
              ]"
              @click="selectDay(day)"
            >
              <div class="flex items-center justify-between mb-1">
                <span
                  :class="[
                    'text-sm font-medium',
                    isToday(day.date) ? 'text-blue-600' : day.isCurrentMonth ? 'text-gray-900' : 'text-gray-400'
                  ]"
                >
                  {{ day.date.getDate() }}
                </span>
                <span v-if="getClassesForDate(day.date).length > 0" class="text-xs text-gray-500">
                  {{ getClassesForDate(day.date).length }} class{{ getClassesForDate(day.date).length > 1 ? 'es' : '' }}
                </span>
              </div>
              <div class="space-y-1">
                <div
                  v-for="classItem in getClassesForDate(day.date).slice(0, 3)"
                  :key="classItem.id"
                  :style="{ backgroundColor: classItem.color + '20', color: classItem.color }"
                  class="px-2 py-0.5 rounded text-xs font-medium truncate"
                  @click.stop="showClassDetails(classItem)"
                >
                  {{ classItem.courseCode }}
                </div>
                <div v-if="getClassesForDate(day.date).length > 3" class="text-xs text-gray-500 text-center">
                  +{{ getClassesForDate(day.date).length - 3 }} more
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- List View -->
        <div v-else class="divide-y divide-gray-200">
          <div v-for="day in daysOfWeek" :key="day" class="p-4">
            <h3 class="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
              {{ day }}
              <span v-if="isTodayByName(day)" class="px-2 py-0.5 text-xs bg-blue-100 text-blue-600 rounded-full">Today</span>
            </h3>
            <div v-if="getClassesForDay(day).length === 0" class="text-sm text-gray-500 py-2">
              No classes scheduled
            </div>
            <div v-else class="space-y-2">
              <div
                v-for="classItem in getClassesForDay(day)"
                :key="classItem.id"
                :style="{ borderLeftColor: classItem.color }"
                class="flex items-center justify-between p-3 border-l-4 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors cursor-pointer"
                @click="showClassDetails(classItem)"
              >
                <div class="flex-1">
                  <div class="flex items-center gap-2">
                    <span class="font-semibold text-gray-900">{{ classItem.courseCode }}</span>
                    <span class="text-gray-400">-</span>
                    <span class="text-gray-700 text-sm">{{ classItem.courseName }}</span>
                  </div>
                  <div class="mt-1 flex items-center gap-4 text-xs text-gray-500">
                    <span class="flex items-center gap-1">
                      <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {{ classItem.startTime }} - {{ classItem.endTime }}
                    </span>
                    <span v-if="classItem.room" class="flex items-center gap-1">
                      <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                      </svg>
                      {{ classItem.room }}
                    </span>
                    <span v-if="classItem.instructor" class="flex items-center gap-1">
                      <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                      {{ classItem.instructor }}
                    </span>
                  </div>
                </div>
                <div
                  :style="{ backgroundColor: classItem.color + '20', color: classItem.color }"
                  class="px-3 py-1 rounded-full text-xs font-medium"
                >
                  {{ classItem.credits }} Credits
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Today's Classes & Upcoming -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Today's Classes -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">Today's Classes</h2>
            <span class="text-sm text-gray-500">{{ formatDate(new Date()) }}</span>
          </div>
          <div class="p-4">
            <div v-if="todayClasses.length === 0" class="text-center py-8 text-gray-500">
              <svg class="mx-auto h-10 w-10 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p>No classes scheduled for today</p>
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="classItem in todayClasses"
                :key="classItem.id"
                :class="[
                  'p-3 rounded-lg border-l-4 transition-colors cursor-pointer',
                  isCurrentClass(classItem) ? 'bg-green-50 border-green-500' : 'bg-gray-50 hover:bg-gray-100'
                ]"
                :style="{ borderLeftColor: isCurrentClass(classItem) ? undefined : classItem.color }"
                @click="showClassDetails(classItem)"
              >
                <div class="flex items-center justify-between">
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="font-semibold" :style="{ color: classItem.color }">{{ classItem.courseCode }}</span>
                      <span v-if="isCurrentClass(classItem)" class="px-2 py-0.5 text-xs bg-green-100 text-green-700 rounded-full animate-pulse">
                        In Progress
                      </span>
                    </div>
                    <p class="text-sm text-gray-600 mt-1">{{ classItem.courseName }}</p>
                  </div>
                  <div class="text-right text-sm">
                    <p class="font-medium text-gray-900">{{ classItem.startTime }} - {{ classItem.endTime }}</p>
                    <p v-if="classItem.room" class="text-gray-500">{{ classItem.room }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Upcoming This Week -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">Upcoming This Week</h2>
          </div>
          <div class="p-4 max-h-[400px] overflow-y-auto">
            <div v-if="upcomingClasses.length === 0" class="text-center py-8 text-gray-500">
              <p>No upcoming classes this week</p>
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="classItem in upcomingClasses"
                :key="classItem.uniqueId"
                class="flex items-start gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                @click="showClassDetails(classItem)"
              >
                <div
                  class="w-12 h-12 rounded-lg flex flex-col items-center justify-center text-white"
                  :style="{ backgroundColor: classItem.color }"
                >
                  <span class="text-xs font-medium">{{ classItem.dayShort }}</span>
                  <span class="text-lg font-bold">{{ classItem.dayNum }}</span>
                </div>
                <div class="flex-1">
                  <div class="font-semibold text-gray-900">{{ classItem.courseCode }}</div>
                  <div class="text-sm text-gray-600">{{ classItem.courseName }}</div>
                  <div class="text-xs text-gray-500 mt-1">
                    {{ classItem.startTime }} - {{ classItem.endTime }}
                    <span v-if="classItem.room"> | {{ classItem.room }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- Class Details Modal -->
    <div v-if="selectedClass" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click="selectedClass = null">
      <div class="bg-white rounded-xl shadow-xl max-w-lg w-full max-h-[90vh] overflow-y-auto" @click.stop>
        <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">Class Details</h3>
          <button @click="selectedClass = null" class="p-1 hover:bg-gray-100 rounded-lg">
            <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6">
          <!-- Course Header -->
          <div class="flex items-start gap-4 mb-6">
            <div
              class="w-16 h-16 rounded-xl flex items-center justify-center text-white font-bold text-xl"
              :style="{ backgroundColor: selectedClass.color }"
            >
              {{ selectedClass.courseCode.slice(0, 2) }}
            </div>
            <div>
              <h4 class="text-xl font-bold text-gray-900">{{ selectedClass.courseCode }}</h4>
              <p class="text-gray-600">{{ selectedClass.courseName }}</p>
              <div class="flex items-center gap-2 mt-2">
                <span class="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-700 rounded-full">
                  {{ selectedClass.credits }} Credits
                </span>
                <span v-if="selectedClass.type" class="px-2 py-1 text-xs font-medium bg-purple-100 text-purple-700 rounded-full">
                  {{ selectedClass.type }}
                </span>
              </div>
            </div>
          </div>

          <!-- Details Grid -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Schedule</p>
              <p class="font-medium text-gray-900">{{ selectedClass.daysOfWeek }}</p>
              <p class="text-sm text-gray-600">{{ selectedClass.startTime }} - {{ selectedClass.endTime }}</p>
            </div>
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Location</p>
              <p class="font-medium text-gray-900">{{ selectedClass.room || 'TBA' }}</p>
              <p class="text-sm text-gray-600">{{ selectedClass.building || 'Main Campus' }}</p>
            </div>
            <div v-if="selectedClass.instructor" class="p-3 bg-gray-50 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Instructor</p>
              <p class="font-medium text-gray-900">{{ selectedClass.instructor }}</p>
              <p class="text-sm text-gray-600">{{ selectedClass.instructorEmail || 'Email not available' }}</p>
            </div>
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Section</p>
              <p class="font-medium text-gray-900">{{ selectedClass.section || 'Section A' }}</p>
              <p class="text-sm text-gray-600">{{ selectedClass.semester || 'Fall 2024' }}</p>
            </div>
          </div>

          <!-- Description -->
          <div v-if="selectedClass.description" class="mb-6">
            <h5 class="text-sm font-semibold text-gray-900 mb-2">Description</h5>
            <p class="text-sm text-gray-600">{{ selectedClass.description }}</p>
          </div>

          <!-- Actions -->
          <div class="flex gap-3">
            <router-link
              :to="`/student/courses`"
              class="flex-1 px-4 py-2 text-center text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
            >
              View Course
            </router-link>
            <button
              @click="addToCalendar(selectedClass)"
              class="flex-1 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex items-center justify-center gap-2"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              Add to Calendar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Day Details Modal (for month view) -->
    <div v-if="selectedDay" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click="selectedDay = null">
      <div class="bg-white rounded-xl shadow-xl max-w-md w-full max-h-[80vh] overflow-y-auto" @click.stop>
        <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between sticky top-0 bg-white">
          <h3 class="text-lg font-semibold text-gray-900">{{ formatDate(selectedDay.date) }}</h3>
          <button @click="selectedDay = null" class="p-1 hover:bg-gray-100 rounded-lg">
            <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-4">
          <div v-if="getClassesForDate(selectedDay.date).length === 0" class="text-center py-8 text-gray-500">
            <p>No classes scheduled for this day</p>
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="classItem in getClassesForDate(selectedDay.date)"
              :key="classItem.id"
              :style="{ borderLeftColor: classItem.color }"
              class="p-3 border-l-4 rounded-lg bg-gray-50 cursor-pointer hover:bg-gray-100"
              @click="showClassDetails(classItem); selectedDay = null"
            >
              <div class="font-semibold" :style="{ color: classItem.color }">{{ classItem.courseCode }}</div>
              <div class="text-sm text-gray-600">{{ classItem.courseName }}</div>
              <div class="text-xs text-gray-500 mt-1">{{ classItem.startTime }} - {{ classItem.endTime }}</div>
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

const authStore = useAuthStore()
const userId = computed(() => authStore.userId)

// State
const loading = ref(false)
const enrollments = ref([])
const scheduleItems = ref([])
const viewMode = ref('week')
const selectedClass = ref(null)
const selectedDay = ref(null)
const currentDate = ref(new Date())

// Constants
const viewModes = [
  { id: 'week', label: 'Week' },
  { id: 'month', label: 'Month' },
  { id: 'list', label: 'List' }
]

const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']
const dayMapping = {
  'M': 'Monday',
  'T': 'Tuesday',
  'W': 'Wednesday',
  'Th': 'Thursday',
  'F': 'Friday',
  'S': 'Saturday',
  'Su': 'Sunday'
}

const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
const dayNamesShort = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

const timeSlots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00']

const courseColors = [
  '#3B82F6', '#10B981', '#8B5CF6', '#F59E0B', '#EF4444', '#06B6D4', '#EC4899', '#84CC16'
]

// Computed
const uniqueCourses = computed(() => {
  const courses = new Map()
  scheduleItems.value.forEach(item => {
    if (!courses.has(item.courseId)) {
      courses.set(item.courseId, item)
    }
  })
  return Array.from(courses.values())
})

const totalCredits = computed(() => {
  return uniqueCourses.value.reduce((sum, c) => sum + (c.credits || 0), 0)
})

const totalWeeklyHours = computed(() => {
  let hours = 0
  scheduleItems.value.forEach(item => {
    const start = parseInt(item.startTime.split(':')[0])
    const end = parseInt(item.endTime.split(':')[0])
    hours += end - start
  })
  return hours
})

const todayClasses = computed(() => {
  const today = dayNames[new Date().getDay()]
  return scheduleItems.value
    .filter(item => item.day === today)
    .sort((a, b) => a.startTime.localeCompare(b.startTime))
})

const currentPeriodLabel = computed(() => {
  if (viewMode.value === 'month') {
    return currentDate.value.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
  }
  const startOfWeek = getStartOfWeek(currentDate.value)
  const endOfWeek = new Date(startOfWeek)
  endOfWeek.setDate(endOfWeek.getDate() + 6)
  return `${startOfWeek.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} - ${endOfWeek.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}`
})

const weekDays = computed(() => {
  const startOfWeek = getStartOfWeek(currentDate.value)
  const days = []
  for (let i = 0; i < 7; i++) {
    const date = new Date(startOfWeek)
    date.setDate(date.getDate() + i)
    days.push({
      name: dayNames[date.getDay()],
      date: date
    })
  }
  return days
})

const monthDays = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)

  const days = []

  // Add days from previous month
  const startDayOfWeek = firstDay.getDay()
  for (let i = startDayOfWeek - 1; i >= 0; i--) {
    const date = new Date(year, month, -i)
    days.push({ date, isCurrentMonth: false })
  }

  // Add days of current month
  for (let i = 1; i <= lastDay.getDate(); i++) {
    days.push({ date: new Date(year, month, i), isCurrentMonth: true })
  }

  // Add days from next month
  const remainingDays = 42 - days.length // 6 rows * 7 days
  for (let i = 1; i <= remainingDays; i++) {
    days.push({ date: new Date(year, month + 1, i), isCurrentMonth: false })
  }

  return days
})

const upcomingClasses = computed(() => {
  const now = new Date()
  const endOfWeek = new Date(now)
  endOfWeek.setDate(endOfWeek.getDate() + (7 - now.getDay()))

  const upcoming = []
  const currentDayIndex = now.getDay()

  for (let i = 0; i <= 7; i++) {
    const targetDate = new Date(now)
    targetDate.setDate(targetDate.getDate() + i)
    const dayName = dayNames[targetDate.getDay()]

    const classesForDay = scheduleItems.value.filter(item => item.day === dayName)
    classesForDay.forEach(item => {
      // Skip if it's today and the class has already ended
      if (i === 0) {
        const classEndTime = item.endTime.split(':')
        const classEndHour = parseInt(classEndTime[0])
        const classEndMinute = parseInt(classEndTime[1] || 0)
        if (now.getHours() > classEndHour || (now.getHours() === classEndHour && now.getMinutes() > classEndMinute)) {
          return
        }
      }

      upcoming.push({
        ...item,
        uniqueId: `${item.id}-${i}`,
        dayShort: dayNamesShort[targetDate.getDay()],
        dayNum: targetDate.getDate()
      })
    })
  }

  return upcoming.slice(0, 10)
})

// Methods
async function fetchSchedule() {
  loading.value = true
  try {
    const response = await api.get(`/enrollments/student/${userId.value}`)
    enrollments.value = response.data.filter(e => e.status === 'ACTIVE' || e.status === 'ENROLLED')

    const items = []
    for (let i = 0; i < enrollments.value.length; i++) {
      const enrollment = enrollments.value[i]
      const course = enrollment.course

      if (course && course.daysOfWeek && course.startTime && course.endTime) {
        const days = parseDaysOfWeek(course.daysOfWeek)
        const startHour = parseInt(formatTime(course.startTime).split(':')[0])
        const endHour = parseInt(formatTime(course.endTime).split(':')[0])
        const duration = endHour - startHour

        days.forEach(day => {
          items.push({
            id: `${enrollment.id}-${day}`,
            enrollmentId: enrollment.id,
            courseId: course.id,
            courseCode: course.courseCode,
            courseName: course.courseName,
            description: course.description,
            startTime: formatTime(course.startTime),
            endTime: formatTime(course.endTime),
            day: day,
            daysOfWeek: course.daysOfWeek,
            instructor: course.instructor?.fullName || course.instructor?.username || null,
            instructorEmail: course.instructor?.email,
            credits: course.creditHours || course.credits || 3,
            color: courseColors[i % courseColors.length],
            room: course.room || `Room ${100 + (i % 20)}`,
            building: course.building || 'Main Building',
            section: course.section,
            type: course.type || 'Lecture',
            duration: duration
          })
        })
      }
    }

    scheduleItems.value = items
  } catch (error) {
    console.error('Error fetching schedule:', error)
    // Return empty schedule on error - no mock data
    scheduleItems.value = []
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
    } else if (i < daysString.length - 1 && daysString.substring(i, i + 2) === 'Su') {
      days.push(dayMapping['Su'])
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
    return `${time[0].toString().padStart(2, '0')}:${time[1].toString().padStart(2, '0')}`
  }
  return time
}

function formatTimeSlot(slot) {
  const hour = parseInt(slot.split(':')[0])
  const ampm = hour >= 12 ? 'PM' : 'AM'
  const displayHour = hour > 12 ? hour - 12 : hour
  return `${displayHour}:00 ${ampm}`
}

function formatDate(date) {
  return date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' })
}

function getStartOfWeek(date) {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day
  return new Date(d.setDate(diff))
}

function isToday(date) {
  const today = new Date()
  return date.toDateString() === today.toDateString()
}

function isTodayByName(dayName) {
  return dayNames[new Date().getDay()] === dayName
}

function isCurrentClass(classItem) {
  const now = new Date()
  const todayName = dayNames[now.getDay()]
  if (classItem.day !== todayName) return false

  const [startHour, startMin] = classItem.startTime.split(':').map(Number)
  const [endHour, endMin] = classItem.endTime.split(':').map(Number)
  const currentHour = now.getHours()
  const currentMin = now.getMinutes()

  const startMinutes = startHour * 60 + startMin
  const endMinutes = endHour * 60 + endMin
  const currentMinutes = currentHour * 60 + currentMin

  return currentMinutes >= startMinutes && currentMinutes < endMinutes
}

function getClassesForSlot(dayName, timeSlot) {
  const slotHour = parseInt(timeSlot.split(':')[0])
  return scheduleItems.value.filter(item => {
    if (item.day !== dayName) return false
    const classStartHour = parseInt(item.startTime.split(':')[0])
    return slotHour === classStartHour
  })
}

function getClassesForDay(day) {
  return scheduleItems.value
    .filter(item => item.day === day)
    .sort((a, b) => a.startTime.localeCompare(b.startTime))
}

function getClassesForDate(date) {
  const dayName = dayNames[date.getDay()]
  return getClassesForDay(dayName)
}

function navigatePeriod(direction) {
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

function showClassDetails(classItem) {
  selectedClass.value = classItem
}

function selectDay(day) {
  if (getClassesForDate(day.date).length > 0) {
    selectedDay.value = day
  }
}

function exportSchedule() {
  const lines = [
    '=' .repeat(50),
    '           CLASS SCHEDULE',
    '=' .repeat(50),
    '',
    `Student: ${authStore.user?.fullName || authStore.user?.username || 'Student'}`,
    `Generated: ${new Date().toLocaleString()}`,
    '',
    '-'.repeat(50),
    ''
  ]

  daysOfWeek.forEach(day => {
    const classes = getClassesForDay(day)
    lines.push(day.toUpperCase())
    if (classes.length === 0) {
      lines.push('  No classes scheduled')
    } else {
      classes.forEach(c => {
        lines.push(`  ${c.startTime}-${c.endTime} | ${c.courseCode}: ${c.courseName}`)
        lines.push(`    Room: ${c.room || 'TBA'} | Instructor: ${c.instructor || 'TBA'}`)
      })
    }
    lines.push('')
  })

  lines.push('-'.repeat(50))
  lines.push(`Total Courses: ${uniqueCourses.value.length}`)
  lines.push(`Total Credits: ${totalCredits.value}`)
  lines.push(`Weekly Hours: ${totalWeeklyHours.value}`)

  const blob = new Blob([lines.join('\n')], { type: 'text/plain' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `schedule_${new Date().toISOString().split('T')[0]}.txt`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(url)
}

function addToCalendar(classItem) {
  // Create ICS file content
  const now = new Date()
  const startDate = new Date(now)
  const dayIndex = dayNames.indexOf(classItem.day)
  const currentDay = now.getDay()
  const daysUntil = (dayIndex - currentDay + 7) % 7
  startDate.setDate(startDate.getDate() + daysUntil)

  const [startHour, startMin] = classItem.startTime.split(':').map(Number)
  const [endHour, endMin] = classItem.endTime.split(':').map(Number)

  startDate.setHours(startHour, startMin, 0, 0)
  const endDate = new Date(startDate)
  endDate.setHours(endHour, endMin, 0, 0)

  const formatICSDate = (date) => {
    return date.toISOString().replace(/[-:]/g, '').split('.')[0] + 'Z'
  }

  const icsContent = [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'BEGIN:VEVENT',
    `DTSTART:${formatICSDate(startDate)}`,
    `DTEND:${formatICSDate(endDate)}`,
    `SUMMARY:${classItem.courseCode} - ${classItem.courseName}`,
    `LOCATION:${classItem.room || 'TBA'}`,
    `DESCRIPTION:Instructor: ${classItem.instructor || 'TBA'}\\nCredits: ${classItem.credits}`,
    'END:VEVENT',
    'END:VCALENDAR'
  ].join('\r\n')

  const blob = new Blob([icsContent], { type: 'text/calendar' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${classItem.courseCode}.ics`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(url)
}

onMounted(() => {
  fetchSchedule()
})
</script>

<style scoped>
table {
  border-collapse: separate;
  border-spacing: 0;
}
</style>
