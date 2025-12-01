<template>
  <div class="max-w-7xl mx-auto">
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
    <!-- Header -->
    <div class="mb-8">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">My Courses</h1>
          <p class="mt-2 text-gray-600">View your enrolled courses and track your progress</p>
        </div>
        <router-link
          to="/student/courses/browse"
          class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
        >
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Browse & Enroll
        </router-link>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div class="flex items-center">
          <div class="p-3 bg-indigo-100 rounded-lg">
            <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500">Enrolled Courses</p>
            <p class="text-2xl font-bold text-gray-900">{{ activeEnrollments.length }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div class="flex items-center">
          <div class="p-3 bg-green-100 rounded-lg">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500">Completed</p>
            <p class="text-2xl font-bold text-gray-900">{{ completedEnrollments.length }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div class="flex items-center">
          <div class="p-3 bg-blue-100 rounded-lg">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500">Total Credits</p>
            <p class="text-2xl font-bold text-gray-900">{{ totalCredits }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div class="flex items-center">
          <div class="p-3 bg-amber-100 rounded-lg">
            <svg class="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500">Pending Payment</p>
            <p class="text-2xl font-bold text-gray-900">{{ pendingPaymentEnrollments.length }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Tabs -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 mb-6">
      <div class="flex border-b border-gray-200">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          class="flex-1 px-4 py-3 text-sm font-medium transition-colors"
          :class="[
            activeTab === tab.id
              ? 'border-b-2 border-indigo-600 text-indigo-600 bg-indigo-50/50'
              : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'
          ]"
        >
          {{ tab.label }}
          <span
            v-if="tab.count > 0"
            class="ml-2 px-2 py-0.5 text-xs rounded-full"
            :class="[
              activeTab === tab.id
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-600'
            ]"
          >
            {{ tab.count }}
          </span>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredEnrollments.length === 0" class="bg-white rounded-xl shadow-sm border border-gray-100 p-12 text-center">
      <svg class="mx-auto h-16 w-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No courses found</h3>
      <p class="mt-2 text-gray-500">
        {{ activeTab === 'all' ? "You haven't enrolled in any courses yet." : `No courses with ${activeTab} status.` }}
      </p>
      <router-link
        to="/student/courses/browse"
        class="mt-4 inline-flex items-center px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
      >
        Browse Available Courses
      </router-link>
    </div>

    <!-- Course Cards -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div
        v-for="enrollment in filteredEnrollments"
        :key="enrollment.id"
        class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow"
      >
        <!-- Course Header -->
        <div class="p-6">
          <div class="flex items-start justify-between mb-4">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <span class="px-2 py-1 text-xs font-semibold rounded-full bg-indigo-100 text-indigo-700">
                  {{ enrollment.course?.courseCode }}
                </span>
                <span
                  class="px-2 py-1 text-xs font-semibold rounded-full"
                  :class="getStatusClass(enrollment.status)"
                >
                  {{ formatStatus(enrollment.status) }}
                </span>
              </div>
              <h3 class="text-lg font-semibold text-gray-900">{{ enrollment.course?.courseName }}</h3>
            </div>
            <div class="text-right">
              <p class="text-2xl font-bold text-indigo-600">{{ enrollment.course?.creditHours }}</p>
              <p class="text-xs text-gray-500">Credits</p>
            </div>
          </div>

          <!-- Course Info -->
          <div class="grid grid-cols-2 gap-4 mb-4">
            <div class="flex items-center text-sm text-gray-600">
              <svg class="w-4 h-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span class="truncate">{{ getInstructorName(enrollment.course) }}</span>
            </div>
            <div class="flex items-center text-sm text-gray-600">
              <svg class="w-4 h-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span>{{ enrollment.course?.daysOfWeek || 'TBD' }}</span>
            </div>
            <div class="flex items-center text-sm text-gray-600">
              <svg class="w-4 h-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>{{ formatTime(enrollment.course) }}</span>
            </div>
            <div class="flex items-center text-sm text-gray-600">
              <svg class="w-4 h-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span>{{ enrollment.course?.location || 'Room TBD' }}</span>
            </div>
          </div>

          <!-- Progress Bar (for active courses) -->
          <div v-if="enrollment.status === 'ACTIVE'" class="mb-4">
            <div class="flex justify-between text-sm mb-1">
              <span class="text-gray-500">Course Progress</span>
              <span class="font-medium text-gray-900">{{ getCourseProgress(enrollment) }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="bg-indigo-600 h-2 rounded-full transition-all duration-500"
                :style="{ width: `${getCourseProgress(enrollment)}%` }"
              ></div>
            </div>
          </div>

          <!-- Grade Display (if completed) -->
          <div v-if="enrollment.status === 'COMPLETED' && enrollment.grade" class="mb-4 p-3 bg-green-50 rounded-lg">
            <div class="flex items-center justify-between">
              <span class="text-sm text-green-700">Final Grade</span>
              <span class="text-lg font-bold text-green-700">{{ enrollment.grade }}</span>
            </div>
          </div>

          <!-- Payment Warning -->
          <div v-if="enrollment.status === 'PENDING_PAYMENT'" class="mb-4 p-3 bg-amber-50 border border-amber-200 rounded-lg">
            <div class="flex items-center">
              <svg class="w-5 h-5 text-amber-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <span class="text-sm text-amber-700">Payment required - ${{ enrollment.course?.courseFee }}</span>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-2">
            <button
              @click="viewCourseDetail(enrollment)"
              class="flex-1 px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition-colors"
            >
              View Details
            </button>
            <router-link
              v-if="enrollment.status === 'PENDING_PAYMENT'"
              to="/student/payments"
              class="px-4 py-2 bg-amber-600 text-white text-sm font-medium rounded-lg hover:bg-amber-700 transition-colors"
            >
              Pay Now
            </router-link>
            <button
              v-if="enrollment.status === 'ACTIVE' || enrollment.status === 'PENDING_PAYMENT'"
              @click="confirmDrop(enrollment)"
              class="px-4 py-2 border border-red-300 text-red-600 text-sm font-medium rounded-lg hover:bg-red-50 transition-colors"
            >
              Drop
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Course Detail Modal -->
    <div
      v-if="showDetailModal && selectedEnrollment"
      class="fixed inset-0 z-50 overflow-y-auto"
      @click.self="showDetailModal = false"
    >
      <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>

        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full mx-auto z-10">
          <!-- Modal Header -->
          <div class="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-4 rounded-t-xl">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-indigo-200 text-sm">{{ selectedEnrollment.course?.courseCode }}</p>
                <h2 class="text-xl font-bold text-white">{{ selectedEnrollment.course?.courseName }}</h2>
              </div>
              <button @click="showDetailModal = false" class="text-white hover:text-indigo-200">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Modal Body -->
          <div class="p-6 max-h-[70vh] overflow-y-auto">
            <!-- Tabs -->
            <div class="flex border-b border-gray-200 mb-6">
              <button
                v-for="tab in detailTabs"
                :key="tab.id"
                @click="activeDetailTab = tab.id"
                class="px-4 py-2 text-sm font-medium transition-colors"
                :class="[
                  activeDetailTab === tab.id
                    ? 'border-b-2 border-indigo-600 text-indigo-600'
                    : 'text-gray-500 hover:text-gray-700'
                ]"
              >
                {{ tab.label }}
              </button>
            </div>

            <!-- Overview Tab -->
            <div v-if="activeDetailTab === 'overview'">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Course Information -->
                <div class="space-y-4">
                  <h3 class="font-semibold text-gray-900">Course Information</h3>
                  <div class="bg-gray-50 rounded-lg p-4 space-y-3">
                    <div class="flex justify-between">
                      <span class="text-gray-500">Credits</span>
                      <span class="font-medium">{{ selectedEnrollment.course?.creditHours }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-gray-500">Schedule</span>
                      <span class="font-medium">{{ selectedEnrollment.course?.daysOfWeek || 'TBD' }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-gray-500">Time</span>
                      <span class="font-medium">{{ formatTime(selectedEnrollment.course) }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-gray-500">Location</span>
                      <span class="font-medium">{{ selectedEnrollment.course?.location || 'TBD' }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-gray-500">Fee</span>
                      <span class="font-medium">${{ selectedEnrollment.course?.courseFee }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-gray-500">Status</span>
                      <span
                        class="px-2 py-1 text-xs font-semibold rounded-full"
                        :class="getStatusClass(selectedEnrollment.status)"
                      >
                        {{ formatStatus(selectedEnrollment.status) }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Instructor Information -->
                <div class="space-y-4">
                  <h3 class="font-semibold text-gray-900">Instructor</h3>
                  <div class="bg-gray-50 rounded-lg p-4">
                    <div v-if="courseInstructor" class="flex items-start gap-4">
                      <div class="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center">
                        <span class="text-2xl font-bold text-indigo-600">
                          {{ getInitials(courseInstructor.name || courseInstructor.username) }}
                        </span>
                      </div>
                      <div class="flex-1">
                        <h4 class="font-semibold text-gray-900">{{ courseInstructor.name || courseInstructor.username }}</h4>
                        <p class="text-sm text-gray-500">{{ courseInstructor.email }}</p>
                        <p v-if="courseInstructor.department" class="text-sm text-gray-500">{{ courseInstructor.department }}</p>
                        <div class="mt-2 flex gap-2">
                          <router-link
                            :to="`/profile/${courseInstructor.id}`"
                            class="text-xs text-indigo-600 hover:text-indigo-700"
                          >
                            View Profile
                          </router-link>
                          <span class="text-gray-300">|</span>
                          <router-link
                            :to="`/messages/${courseInstructor.id}`"
                            class="text-xs text-indigo-600 hover:text-indigo-700"
                          >
                            Send Message
                          </router-link>
                        </div>
                      </div>
                    </div>
                    <div v-else class="text-center py-4 text-gray-500">
                      No instructor assigned
                    </div>
                  </div>
                </div>
              </div>

              <!-- Course Description -->
              <div class="mt-6">
                <h3 class="font-semibold text-gray-900 mb-3">Description</h3>
                <p class="text-gray-600">
                  {{ selectedEnrollment.course?.description || 'No description available for this course.' }}
                </p>
              </div>
            </div>

            <!-- Performance Tab -->
            <div v-if="activeDetailTab === 'performance'">
              <div v-if="loadingPerformance" class="flex justify-center py-12">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
              </div>
              <div v-else class="space-y-6">
                <!-- Performance Summary -->
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <!-- Grade Summary -->
                  <div class="bg-gradient-to-br from-indigo-50 to-purple-50 rounded-xl p-4 border border-indigo-100">
                    <div class="flex items-center gap-3 mb-3">
                      <div class="p-2 bg-indigo-100 rounded-lg">
                        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                        </svg>
                      </div>
                      <h4 class="font-semibold text-gray-900">Course Grade</h4>
                    </div>
                    <div class="text-center">
                      <p class="text-3xl font-bold text-indigo-600">
                        {{ coursePerformance.grades.length > 0 ? coursePerformance.grades[0]?.gradeValue || 'N/A' : 'N/A' }}
                      </p>
                      <p class="text-sm text-gray-500">{{ coursePerformance.grades.length }} grade(s) recorded</p>
                    </div>
                  </div>

                  <!-- Attendance Summary -->
                  <div class="bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl p-4 border border-green-100">
                    <div class="flex items-center gap-3 mb-3">
                      <div class="p-2 bg-green-100 rounded-lg">
                        <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                      </div>
                      <h4 class="font-semibold text-gray-900">Attendance</h4>
                    </div>
                    <div class="text-center">
                      <p class="text-3xl font-bold text-green-600">{{ coursePerformance.attendance.percentage }}%</p>
                      <p class="text-sm text-gray-500">{{ coursePerformance.attendance.total }} classes recorded</p>
                    </div>
                  </div>

                  <!-- Class Standing -->
                  <div class="bg-gradient-to-br from-amber-50 to-orange-50 rounded-xl p-4 border border-amber-100">
                    <div class="flex items-center gap-3 mb-3">
                      <div class="p-2 bg-amber-100 rounded-lg">
                        <svg class="w-5 h-5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                        </svg>
                      </div>
                      <h4 class="font-semibold text-gray-900">Standing</h4>
                    </div>
                    <div class="text-center">
                      <p class="text-3xl font-bold text-amber-600">
                        {{ coursePerformance.attendance.percentage >= 75 ? 'Good' : 'At Risk' }}
                      </p>
                      <p class="text-sm text-gray-500">Based on attendance & grades</p>
                    </div>
                  </div>
                </div>

                <!-- Attendance Breakdown -->
                <div class="bg-white rounded-xl border border-gray-200 p-4">
                  <h4 class="font-semibold text-gray-900 mb-4">Attendance Breakdown</h4>
                  <div class="flex items-center gap-4 mb-4">
                    <div class="flex-1 bg-gray-100 rounded-full h-4 overflow-hidden">
                      <div class="h-full flex">
                        <div
                          class="bg-green-500 transition-all"
                          :style="{ width: `${coursePerformance.attendance.total > 0 ? (coursePerformance.attendance.present / coursePerformance.attendance.total) * 100 : 0}%` }"
                        ></div>
                        <div
                          class="bg-amber-500 transition-all"
                          :style="{ width: `${coursePerformance.attendance.total > 0 ? (coursePerformance.attendance.late / coursePerformance.attendance.total) * 100 : 0}%` }"
                        ></div>
                        <div
                          class="bg-red-500 transition-all"
                          :style="{ width: `${coursePerformance.attendance.total > 0 ? (coursePerformance.attendance.absent / coursePerformance.attendance.total) * 100 : 0}%` }"
                        ></div>
                      </div>
                    </div>
                  </div>
                  <div class="grid grid-cols-3 gap-4 text-center">
                    <div>
                      <div class="flex items-center justify-center gap-2 mb-1">
                        <div class="w-3 h-3 bg-green-500 rounded-full"></div>
                        <span class="text-sm text-gray-600">Present</span>
                      </div>
                      <p class="text-xl font-bold text-green-600">{{ coursePerformance.attendance.present }}</p>
                    </div>
                    <div>
                      <div class="flex items-center justify-center gap-2 mb-1">
                        <div class="w-3 h-3 bg-amber-500 rounded-full"></div>
                        <span class="text-sm text-gray-600">Late</span>
                      </div>
                      <p class="text-xl font-bold text-amber-600">{{ coursePerformance.attendance.late }}</p>
                    </div>
                    <div>
                      <div class="flex items-center justify-center gap-2 mb-1">
                        <div class="w-3 h-3 bg-red-500 rounded-full"></div>
                        <span class="text-sm text-gray-600">Absent</span>
                      </div>
                      <p class="text-xl font-bold text-red-600">{{ coursePerformance.attendance.absent }}</p>
                    </div>
                  </div>
                </div>

                <!-- Grades List -->
                <div class="bg-white rounded-xl border border-gray-200 p-4">
                  <h4 class="font-semibold text-gray-900 mb-4">Grade History</h4>
                  <div v-if="coursePerformance.grades.length === 0" class="text-center py-8">
                    <svg class="mx-auto h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p class="mt-4 text-gray-500">No grades recorded yet</p>
                  </div>
                  <div v-else class="space-y-3">
                    <div
                      v-for="grade in coursePerformance.grades"
                      :key="grade.id"
                      class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                    >
                      <div>
                        <p class="font-medium text-gray-900">{{ grade.assignmentName || 'Course Grade' }}</p>
                        <p class="text-xs text-gray-500">{{ formatDate(grade.createdAt) }}</p>
                      </div>
                      <div class="text-right">
                        <p class="text-lg font-bold text-indigo-600">{{ grade.gradeValue }}</p>
                        <p class="text-xs text-gray-500">{{ grade.gradePoints }} points</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Classmates Tab -->
            <div v-if="activeDetailTab === 'classmates'">
              <div v-if="loadingClassmates" class="flex justify-center py-12">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
              </div>
              <div v-else-if="classmates.length === 0" class="text-center py-12">
                <svg class="mx-auto h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                <p class="mt-4 text-gray-500">No other students enrolled in this course</p>
              </div>
              <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div
                  v-for="classmate in classmates"
                  :key="classmate.id"
                  class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <div class="w-10 h-10 bg-indigo-100 rounded-full flex items-center justify-center">
                    <span class="text-sm font-bold text-indigo-600">
                      {{ getInitials(classmate.student?.name || classmate.student?.username) }}
                    </span>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="font-medium text-gray-900 truncate">
                      {{ classmate.student?.name || classmate.student?.username }}
                    </p>
                    <p class="text-xs text-gray-500 truncate">{{ classmate.student?.email }}</p>
                  </div>
                  <div class="flex gap-1">
                    <router-link
                      :to="`/profile/${classmate.student?.id}`"
                      class="p-2 text-gray-400 hover:text-indigo-600 transition-colors"
                      title="View Profile"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                    </router-link>
                    <router-link
                      :to="`/messages/${classmate.student?.id}`"
                      class="p-2 text-gray-400 hover:text-indigo-600 transition-colors"
                      title="Send Message"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                      </svg>
                    </router-link>
                  </div>
                </div>
              </div>
              <p class="mt-4 text-sm text-gray-500 text-center">
                {{ classmates.length }} student{{ classmates.length !== 1 ? 's' : '' }} enrolled
              </p>
            </div>

            <!-- Materials Tab -->
            <div v-if="activeDetailTab === 'materials'">
              <div v-if="loadingMaterials" class="flex justify-center py-12">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
              </div>
              <div v-else-if="courseMaterials.length === 0" class="text-center py-12">
                <svg class="mx-auto h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p class="mt-4 text-gray-500">No materials uploaded for this course yet</p>
              </div>
              <div v-else class="space-y-3">
                <div
                  v-for="material in courseMaterials"
                  :key="material.id"
                  class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <div class="p-2 bg-white rounded-lg shadow-sm">
                    <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="font-medium text-gray-900 truncate">{{ material.title }}</p>
                    <p class="text-xs text-gray-500">{{ material.type }} • {{ formatFileSize(material.size) }}</p>
                  </div>
                  <button
                    @click="downloadMaterial(material)"
                    class="p-2 text-indigo-600 hover:bg-indigo-100 rounded-lg transition-colors"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <!-- Assignments Tab -->
            <div v-if="activeDetailTab === 'assignments'">
              <div v-if="loadingAssignments" class="flex justify-center py-12">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
              </div>
              <div v-else-if="courseAssignments.length === 0" class="text-center py-12">
                <svg class="mx-auto h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
                <p class="mt-4 text-gray-500">No assignments for this course yet</p>
              </div>
              <div v-else class="space-y-3">
                <div
                  v-for="assignment in courseAssignments"
                  :key="assignment.id"
                  class="p-4 bg-gray-50 rounded-lg"
                >
                  <div class="flex items-start justify-between">
                    <div>
                      <h4 class="font-medium text-gray-900">{{ assignment.title }}</h4>
                      <p class="text-sm text-gray-500 mt-1">{{ assignment.description }}</p>
                      <p class="text-xs text-gray-400 mt-2">
                        Due: {{ formatDate(assignment.dueDate) }}
                      </p>
                    </div>
                    <span
                      class="px-2 py-1 text-xs font-semibold rounded-full"
                      :class="getAssignmentStatusClass(assignment)"
                    >
                      {{ getAssignmentStatus(assignment) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Drop Confirmation Modal -->
    <div
      v-if="showDropModal && enrollmentToDrop"
      class="fixed inset-0 z-50 overflow-y-auto"
      @click.self="showDropModal = false"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>

        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full mx-auto z-10 p-6">
          <div class="text-center">
            <div class="mx-auto w-12 h-12 bg-red-100 rounded-full flex items-center justify-center mb-4">
              <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Drop Course?</h3>
            <p class="text-gray-500 mb-6">
              Are you sure you want to drop <strong>{{ enrollmentToDrop.course?.courseCode }} - {{ enrollmentToDrop.course?.courseName }}</strong>? This action cannot be undone.
            </p>
            <div class="flex gap-3">
              <button
                @click="showDropModal = false"
                class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
              <button
                @click="dropCourse"
                :disabled="dropping"
                class="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
              >
                {{ dropping ? 'Dropping...' : 'Drop Course' }}
              </button>
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

// State
const enrollments = ref([])
const loading = ref(true)
const activeTab = ref('all')

// Modal states
const showDetailModal = ref(false)
const selectedEnrollment = ref(null)
const activeDetailTab = ref('overview')
const courseInstructor = ref(null)
const classmates = ref([])
const courseMaterials = ref([])
const courseAssignments = ref([])
const loadingClassmates = ref(false)
const loadingMaterials = ref(false)
const loadingAssignments = ref(false)
const loadingPerformance = ref(false)
const coursePerformance = ref({
  grades: [],
  attendance: { present: 0, absent: 0, late: 0, total: 0, percentage: 0 },
  averageGrade: null
})

// Drop modal
const showDropModal = ref(false)
const enrollmentToDrop = ref(null)
const dropping = ref(false)

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

// Computed
const activeEnrollments = computed(() =>
  enrollments.value.filter(e => e.status === 'ACTIVE')
)

const completedEnrollments = computed(() =>
  enrollments.value.filter(e => e.status === 'COMPLETED')
)

const pendingPaymentEnrollments = computed(() =>
  enrollments.value.filter(e => e.status === 'PENDING_PAYMENT')
)

const totalCredits = computed(() =>
  activeEnrollments.value.reduce((sum, e) => sum + (e.course?.creditHours || 0), 0)
)

const tabs = computed(() => [
  { id: 'all', label: 'All Courses', count: enrollments.value.length },
  { id: 'active', label: 'Active', count: activeEnrollments.value.length },
  { id: 'pending', label: 'Pending Payment', count: pendingPaymentEnrollments.value.length },
  { id: 'completed', label: 'Completed', count: completedEnrollments.value.length }
])

const detailTabs = [
  { id: 'overview', label: 'Overview' },
  { id: 'performance', label: 'Performance' },
  { id: 'classmates', label: 'Classmates' },
  { id: 'materials', label: 'Materials' },
  { id: 'assignments', label: 'Assignments' }
]

const filteredEnrollments = computed(() => {
  switch (activeTab.value) {
    case 'active':
      return activeEnrollments.value
    case 'pending':
      return pendingPaymentEnrollments.value
    case 'completed':
      return completedEnrollments.value
    default:
      return enrollments.value
  }
})

// Methods
const loadEnrollments = async () => {
  try {
    loading.value = true
    const response = await api.getStudentEnrollments(authStore.userId)
    enrollments.value = response.data || []
  } catch (error) {
    console.error('Error loading enrollments:', error)
    enrollments.value = []
  } finally {
    loading.value = false
  }
}

const getStatusClass = (status) => {
  const classes = {
    ACTIVE: 'bg-green-100 text-green-700',
    PENDING_PAYMENT: 'bg-amber-100 text-amber-700',
    COMPLETED: 'bg-blue-100 text-blue-700',
    DROPPED: 'bg-red-100 text-red-700',
    WAITLISTED: 'bg-purple-100 text-purple-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

const formatStatus = (status) => {
  const labels = {
    ACTIVE: 'Active',
    PENDING_PAYMENT: 'Pending Payment',
    COMPLETED: 'Completed',
    DROPPED: 'Dropped',
    WAITLISTED: 'Waitlisted'
  }
  return labels[status] || status
}

const getInstructorName = (course) => {
  if (course?.instructor) {
    return course.instructor.name || course.instructor.username || 'Instructor'
  }
  return 'TBD'
}

const formatTime = (course) => {
  if (course?.startTime && course?.endTime) {
    return `${course.startTime} - ${course.endTime}`
  }
  return 'TBD'
}

const getCourseProgress = (enrollment) => {
  // Calculate progress based on weeks passed since enrollment (assuming 16-week semester)
  // This provides an estimate based on timeline progression
  if (enrollment.status === 'COMPLETED') return 100
  if (enrollment.status === 'DROPPED') return 0

  const enrolledDate = new Date(enrollment.enrolledAt || enrollment.enrollmentDate || Date.now())
  const now = new Date()
  const weeksPassed = Math.floor((now - enrolledDate) / (7 * 24 * 60 * 60 * 1000))
  const semesterWeeks = 16 // Standard semester length
  return Math.min(Math.round((weeksPassed / semesterWeeks) * 100), 100)
}

const getInitials = (name) => {
  if (!name) return '?'
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
}

const viewCourseDetail = async (enrollment) => {
  selectedEnrollment.value = enrollment
  activeDetailTab.value = 'overview'
  showDetailModal.value = true

  const courseId = enrollment.course?.id

  // Load instructor info
  if (enrollment.course?.instructor) {
    courseInstructor.value = enrollment.course.instructor
  } else if (enrollment.course?.instructorId) {
    try {
      const response = await api.getUserById(enrollment.course.instructorId)
      courseInstructor.value = response.data
    } catch (error) {
      console.error('Error loading instructor:', error)
      courseInstructor.value = null
    }
  }

  // Load all course data in parallel with proper error handling
  // Using Promise.allSettled to prevent one failure from blocking others
  await Promise.allSettled([
    loadClassmates(courseId),
    loadMaterials(courseId),
    loadAssignments(courseId),
    loadPerformance(courseId)
  ])
}

const loadClassmates = async (courseId) => {
  if (!courseId) return
  loadingClassmates.value = true
  try {
    const response = await api.getCourseEnrollments(courseId)
    // Filter out current student
    classmates.value = (response.data || []).filter(
      e => e.student?.id !== authStore.userId && e.status === 'ACTIVE'
    )
  } catch (error) {
    console.error('Error loading classmates:', error)
    classmates.value = []
  } finally {
    loadingClassmates.value = false
  }
}

const loadMaterials = async (courseId) => {
  if (!courseId) return
  loadingMaterials.value = true
  try {
    const response = await api.getCourseMaterials(courseId)
    courseMaterials.value = response.data || []
  } catch (error) {
    console.error('Error loading materials:', error)
    courseMaterials.value = []
  } finally {
    loadingMaterials.value = false
  }
}

const loadAssignments = async (courseId) => {
  if (!courseId) return
  loadingAssignments.value = true
  try {
    const response = await api.getCourseAssignments(courseId)
    courseAssignments.value = response.data || []
  } catch (error) {
    console.error('Error loading assignments:', error)
    courseAssignments.value = []
  } finally {
    loadingAssignments.value = false
  }
}

const loadPerformance = async (courseId) => {
  if (!courseId) return
  loadingPerformance.value = true
  try {
    // Load grades for this course
    const gradesResponse = await api.getStudentGrades(authStore.userId)
    const courseGrades = (gradesResponse.data || []).filter(g => g.courseId === courseId || g.course?.id === courseId)

    // Load attendance for this course
    const attendanceResponse = await api.getStudentAttendance(authStore.userId)
    const courseAttendance = (attendanceResponse.data || []).filter(a => a.courseId === courseId || a.course?.id === courseId)

    // Calculate attendance stats
    const present = courseAttendance.filter(a => a.status === 'PRESENT').length
    const absent = courseAttendance.filter(a => a.status === 'ABSENT').length
    const late = courseAttendance.filter(a => a.status === 'LATE').length
    const total = courseAttendance.length
    const percentage = total > 0 ? Math.round((present / total) * 100) : 0

    // Calculate average grade
    let avgGrade = null
    if (courseGrades.length > 0) {
      const gradePoints = courseGrades.map(g => g.gradePoints || 0)
      avgGrade = (gradePoints.reduce((a, b) => a + b, 0) / gradePoints.length).toFixed(2)
    }

    coursePerformance.value = {
      grades: courseGrades,
      attendance: { present, absent, late, total, percentage },
      averageGrade: avgGrade
    }
  } catch (error) {
    console.error('Error loading performance:', error)
    coursePerformance.value = {
      grades: [],
      attendance: { present: 0, absent: 0, late: 0, total: 0, percentage: 0 },
      averageGrade: null
    }
  } finally {
    loadingPerformance.value = false
  }
}

const formatFileSize = (bytes) => {
  if (!bytes) return 'Unknown'
  const kb = bytes / 1024
  if (kb < 1024) return `${kb.toFixed(1)} KB`
  return `${(kb / 1024).toFixed(1)} MB`
}

const downloadMaterial = async (material) => {
  try {
    const response = await api.downloadCourseMaterial(material.id)
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', material.title || 'material')
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Error downloading material:', error)
    showNotification('Failed to download material', 'error')
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'No due date'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getAssignmentStatus = (assignment) => {
  const now = new Date()
  const dueDate = new Date(assignment.dueDate)

  if (assignment.submitted) return 'Submitted'
  if (now > dueDate) return 'Overdue'
  return 'Pending'
}

const getAssignmentStatusClass = (assignment) => {
  const status = getAssignmentStatus(assignment)
  const classes = {
    Submitted: 'bg-green-100 text-green-700',
    Overdue: 'bg-red-100 text-red-700',
    Pending: 'bg-amber-100 text-amber-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

const confirmDrop = (enrollment) => {
  enrollmentToDrop.value = enrollment
  showDropModal.value = true
}

const dropCourse = async () => {
  if (!enrollmentToDrop.value) return

  dropping.value = true
  try {
    await api.dropEnrollment(enrollmentToDrop.value.id)
    await loadEnrollments()
    showDropModal.value = false
    enrollmentToDrop.value = null
  } catch (error) {
    console.error('Error dropping course:', error)
    showNotification(error.response?.data?.message || 'Failed to drop course', 'error')
  } finally {
    dropping.value = false
  }
}

onMounted(() => {
  loadEnrollments()
})
</script>
