<template>
  <div class="p-6 bg-gray-50 min-h-screen">
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
    <div class="mb-6 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">My Courses</h1>
        <p class="text-gray-600 mt-2">Comprehensive course analytics, grades, and materials management</p>
      </div>
      <div class="flex gap-3">
        <button @click="refreshCourses" class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
          <svg class="h-5 w-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Refresh
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- No Courses -->
    <div v-else-if="courses.length === 0" class="bg-white rounded-xl shadow-sm p-12 text-center">
      <svg class="mx-auto h-16 w-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
      </svg>
      <h3 class="mt-4 text-lg font-medium text-gray-900">No courses assigned</h3>
      <p class="mt-2 text-gray-500">You don't have any courses assigned to you yet.</p>
    </div>

    <!-- Main Content -->
    <div v-else>
      <!-- Course Selection Cards -->
      <div v-if="!selectedCourse" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="course in courses"
          :key="course.id"
          class="bg-white rounded-xl shadow-sm hover:shadow-lg transition-all cursor-pointer transform hover:-translate-y-1 border border-gray-100"
          @click="selectCourse(course)"
        >
          <div class="p-6">
            <!-- Course Header -->
            <div class="flex items-start justify-between mb-4">
              <div class="flex-1">
                <div class="flex items-center gap-2">
                  <span class="px-2 py-1 text-xs font-bold rounded bg-gradient-to-r from-blue-500 to-indigo-600 text-white">
                    {{ course.courseCode }}
                  </span>
                </div>
                <h3 class="text-lg font-bold text-gray-900 mt-2">{{ course.courseName }}</h3>
              </div>
              <span class="px-3 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-600">
                {{ course.credits }} Credits
              </span>
            </div>

            <!-- Course Stats -->
            <div class="grid grid-cols-3 gap-3 mb-4">
              <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-3 text-center">
                <p class="text-2xl font-bold text-blue-700">{{ course.enrolledCount || 0 }}</p>
                <p class="text-xs text-blue-600">Students</p>
              </div>
              <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-lg p-3 text-center">
                <p class="text-2xl font-bold text-green-700">{{ course.assignmentCount || 0 }}</p>
                <p class="text-xs text-green-600">Assignments</p>
              </div>
              <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-lg p-3 text-center">
                <p class="text-2xl font-bold text-purple-700">{{ course.avgGrade || '-' }}</p>
                <p class="text-xs text-purple-600">Avg Grade</p>
              </div>
            </div>

            <!-- Schedule Info -->
            <div v-if="course.daysOfWeek && course.startTime" class="flex items-center text-sm text-gray-600 mb-4 bg-gray-50 p-2 rounded-lg">
              <svg class="h-4 w-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              {{ course.daysOfWeek }} | {{ formatTime(course.startTime) }} - {{ formatTime(course.endTime) }}
            </div>

            <!-- View Course Button -->
            <button class="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-lg font-medium hover:from-blue-700 hover:to-indigo-700 transition-all flex items-center justify-center gap-2">
              <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              View Analytics
            </button>
          </div>
        </div>
      </div>

      <!-- Course Analytics View -->
      <div v-else>
        <!-- Back Button and Course Header -->
        <div class="mb-6 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <button @click="selectedCourse = null" class="p-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
              <svg class="h-5 w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <div>
              <h2 class="text-2xl font-bold text-gray-900">{{ selectedCourse.courseCode }} - {{ selectedCourse.courseName }}</h2>
              <p class="text-gray-600">{{ selectedCourse.credits }} Credits | {{ selectedCourse.enrolledCount || 0 }} Students Enrolled</p>
            </div>
          </div>
        </div>

        <!-- Tab Navigation -->
        <div class="bg-white rounded-xl shadow-sm mb-6">
          <div class="border-b border-gray-200">
            <nav class="flex -mb-px">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="activeTab = tab.id"
                class="flex-1 py-4 px-6 text-center font-medium text-sm border-b-2 transition-colors"
                :class="[
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600 bg-blue-50/50'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                <span class="flex items-center justify-center gap-2">
                  <component :is="tab.icon" class="h-5 w-5" />
                  {{ tab.name }}
                </span>
              </button>
            </nav>
          </div>
        </div>

        <!-- Tab Content -->
        <div class="space-y-6">
          <!-- Overview Tab -->
          <div v-if="activeTab === 'overview'">
            <!-- Summary Cards -->
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
              <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-blue-500">
                <p class="text-sm text-gray-600 mb-1">Total Students</p>
                <p class="text-3xl font-bold text-gray-900">{{ selectedCourse.enrolledCount || 0 }}</p>
              </div>
              <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-green-500">
                <p class="text-sm text-gray-600 mb-1">Class Average</p>
                <p class="text-3xl font-bold text-gray-900">{{ courseStats.classAverage || '-' }}%</p>
              </div>
              <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-purple-500">
                <p class="text-sm text-gray-600 mb-1">Attendance Rate</p>
                <p class="text-3xl font-bold text-gray-900">{{ courseStats.attendanceRate || '-' }}%</p>
              </div>
              <div class="bg-white rounded-xl shadow-sm p-6 border-l-4 border-orange-500">
                <p class="text-sm text-gray-600 mb-1">Assignments</p>
                <p class="text-3xl font-bold text-gray-900">{{ courseAssignments.length }}</p>
              </div>
            </div>

            <!-- Grade Distribution Chart -->
            <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Grade Distribution</h3>
              <div class="h-64">
                <canvas ref="gradeDistributionChart"></canvas>
              </div>
            </div>

            <!-- Performance Trend Chart -->
            <div class="bg-white rounded-xl shadow-sm p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Class Performance Trend</h3>
              <div class="h-64">
                <canvas ref="performanceTrendChart"></canvas>
              </div>
            </div>
          </div>

          <!-- Grades Tab -->
          <div v-if="activeTab === 'grades'">
            <!-- Graded Items Overview -->
            <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Graded Items Summary</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <div v-for="item in gradedItemsSummary" :key="item.type"
                     class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                  <div class="flex items-center justify-between mb-2">
                    <span class="font-medium text-gray-700">{{ item.type }}</span>
                    <span class="px-2 py-1 text-xs rounded-full" :class="item.bgClass">{{ item.count }}</span>
                  </div>
                  <div class="space-y-1">
                    <div class="flex justify-between text-sm">
                      <span class="text-gray-500">Mean:</span>
                      <span class="font-semibold">{{ item.mean }}%</span>
                    </div>
                    <div class="flex justify-between text-sm">
                      <span class="text-gray-500">Median:</span>
                      <span class="font-semibold">{{ item.median }}%</span>
                    </div>
                    <div class="flex justify-between text-sm">
                      <span class="text-gray-500">Std Dev:</span>
                      <span class="font-semibold">{{ item.stdDev }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Individual Assignment Stats -->
            <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Assignment Performance</h3>
              <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Assignment</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Max Points</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Mean</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Median</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Submissions</th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="assignment in courseAssignments" :key="assignment.id" class="hover:bg-gray-50">
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="font-medium text-gray-900">{{ assignment.title }}</div>
                        <div class="text-sm text-gray-500">Due: {{ formatDate(assignment.dueDate) }}</div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <span class="px-2 py-1 text-xs font-semibold rounded-full" :class="getAssignmentTypeClass(assignment.type)">
                          {{ assignment.type || 'Assignment' }}
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-gray-900">{{ assignment.maxPoints || 100 }}</td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <span class="font-semibold" :class="getGradeColor(assignment.meanScore)">
                          {{ assignment.meanScore || '-' }}%
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-gray-900">{{ assignment.medianScore || '-' }}%</td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="flex items-center">
                          <span class="text-gray-900">{{ assignment.submissionCount || 0 }}</span>
                          <span class="text-gray-500">/{{ selectedCourse.enrolledCount }}</span>
                        </div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <button @click="viewAssignmentDetails(assignment)" class="text-blue-600 hover:text-blue-800 font-medium text-sm">
                          Details
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Grade Statistics Chart -->
            <div class="bg-white rounded-xl shadow-sm p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Assignment Scores Comparison</h3>
              <div class="h-80">
                <canvas ref="assignmentScoresChart"></canvas>
              </div>
            </div>
          </div>

          <!-- Students Tab -->
          <div v-if="activeTab === 'students'">
            <!-- Search and Filter -->
            <div class="bg-white rounded-xl shadow-sm p-4 mb-6">
              <div class="flex flex-wrap gap-4">
                <div class="flex-1 min-w-[200px]">
                  <input
                    v-model="studentSearch"
                    type="text"
                    placeholder="Search students..."
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <select v-model="studentFilter" class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
                  <option value="all">All Students</option>
                  <option value="excellent">Excellent (90%+)</option>
                  <option value="good">Good (70-89%)</option>
                  <option value="needs-improvement">Needs Improvement (&lt;70%)</option>
                  <option value="at-risk">At Risk (&lt;50%)</option>
                </select>
              </div>
            </div>

            <!-- Student List -->
            <div class="bg-white rounded-xl shadow-sm overflow-hidden">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Student</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Assignments</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quizzes</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Midterm</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Final</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Overall</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Attendance</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="student in filteredStudents" :key="student.id" class="hover:bg-gray-50 cursor-pointer" @click="viewStudentDetails(student)">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center">
                        <div class="flex-shrink-0 h-10 w-10 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-full flex items-center justify-center">
                          <span class="text-white font-semibold text-sm">
                            {{ getInitials(student.firstName, student.lastName) }}
                          </span>
                        </div>
                        <div class="ml-4">
                          <div class="text-sm font-medium text-gray-900">{{ student.firstName }} {{ student.lastName }}</div>
                          <div class="text-sm text-gray-500">{{ student.email }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span :class="getGradeColor(student.assignmentAvg)">{{ student.assignmentAvg || '-' }}%</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span :class="getGradeColor(student.quizAvg)">{{ student.quizAvg || '-' }}%</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span :class="getGradeColor(student.midtermScore)">{{ student.midtermScore || '-' }}%</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span :class="getGradeColor(student.finalScore)">{{ student.finalScore || '-' }}%</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center gap-2">
                        <span class="font-bold" :class="getGradeColor(student.overallGrade)">{{ student.overallGrade || '-' }}%</span>
                        <span class="px-2 py-1 text-xs font-semibold rounded-full" :class="getLetterGradeClass(student.letterGrade)">
                          {{ student.letterGrade || '-' }}
                        </span>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center gap-2">
                        <div class="w-16 bg-gray-200 rounded-full h-2">
                          <div class="h-2 rounded-full" :class="getAttendanceBarColor(student.attendance)" :style="{ width: `${student.attendance || 0}%` }"></div>
                        </div>
                        <span class="text-sm text-gray-600">{{ student.attendance || 0 }}%</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <button @click.stop="viewStudentDetails(student)" class="text-blue-600 hover:text-blue-800 font-medium text-sm">
                        View Details
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Materials Tab -->
          <div v-if="activeTab === 'materials'">
            <!-- Upload Section -->
            <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Upload Course Materials</h3>

              <!-- Drag and Drop Zone -->
              <div
                @dragover.prevent="handleDragOver"
                @dragleave.prevent="handleDragLeave"
                @drop.prevent="handleDrop"
                class="border-2 border-dashed rounded-xl p-8 text-center transition-all"
                :class="[
                  isDragging
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-300 hover:border-blue-400 hover:bg-gray-50'
                ]"
              >
                <svg class="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <p class="text-gray-600 mb-2">Drag and drop your PDF files here, or</p>
                <label class="cursor-pointer">
                  <span class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors inline-block">
                    Browse Files
                  </span>
                  <input type="file" class="hidden" accept=".pdf" @change="handleFileSelect" multiple />
                </label>
                <p class="text-sm text-gray-500 mt-2">Only PDF files are accepted (Max 10MB each)</p>
              </div>

              <!-- Selected Files -->
              <div v-if="selectedFiles.length > 0" class="mt-6">
                <h4 class="text-sm font-medium text-gray-700 mb-3">Selected Files</h4>
                <div class="space-y-3">
                  <div v-for="(file, index) in selectedFiles" :key="index" class="flex items-center justify-between bg-gray-50 rounded-lg p-3">
                    <div class="flex items-center gap-3">
                      <svg class="h-8 w-8 text-red-500" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
                      </svg>
                      <div>
                        <p class="font-medium text-gray-900">{{ file.name }}</p>
                        <p class="text-sm text-gray-500">{{ formatFileSize(file.size) }}</p>
                      </div>
                    </div>
                    <div class="flex items-center gap-3">
                      <select v-model="file.materialType" class="px-3 py-1 border border-gray-300 rounded-lg text-sm">
                        <option value="lecture">Lecture</option>
                        <option value="assignment">Assignment</option>
                        <option value="lab">Lab</option>
                        <option value="notes">Notes</option>
                        <option value="syllabus">Syllabus</option>
                        <option value="other">Other</option>
                      </select>
                      <button @click="removeFile(index)" class="p-1 text-gray-400 hover:text-red-500">
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>
                <button
                  @click="uploadFiles"
                  :disabled="uploading"
                  class="mt-4 px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 transition-colors flex items-center gap-2"
                >
                  <span v-if="uploading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                  {{ uploading ? 'Uploading...' : 'Upload All Files' }}
                </button>
              </div>
            </div>

            <!-- Existing Materials -->
            <div class="bg-white rounded-xl shadow-sm p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Course Materials</h3>

              <!-- Material Type Tabs -->
              <div class="flex gap-2 mb-4 flex-wrap">
                <button
                  v-for="type in materialTypes"
                  :key="type.value"
                  @click="activeMaterialType = type.value"
                  class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                  :class="[
                    activeMaterialType === type.value
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  ]"
                >
                  {{ type.label }} ({{ getMaterialCount(type.value) }})
                </button>
              </div>

              <!-- Materials List -->
              <div v-if="filteredMaterials.length > 0" class="space-y-3">
                <div v-for="material in filteredMaterials" :key="material.id" class="flex items-center justify-between bg-gray-50 rounded-lg p-4 hover:bg-gray-100 transition-colors">
                  <div class="flex items-center gap-4">
                    <div class="p-2 bg-red-100 rounded-lg">
                      <svg class="h-8 w-8 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
                      </svg>
                    </div>
                    <div>
                      <p class="font-medium text-gray-900">{{ material.title || material.fileName }}</p>
                      <p class="text-sm text-gray-500">
                        {{ formatDate(material.uploadedAt) }} | {{ formatFileSize(material.fileSize) }}
                      </p>
                    </div>
                  </div>
                  <div class="flex items-center gap-2">
                    <button @click="downloadMaterial(material)" class="px-3 py-1 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 text-sm font-medium">
                      Download
                    </button>
                    <button @click="deleteMaterial(material)" class="px-3 py-1 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 text-sm font-medium">
                      Delete
                    </button>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-gray-500">
                No materials uploaded for this category yet.
              </div>
            </div>
          </div>

          <!-- Attendance Tab -->
          <div v-if="activeTab === 'attendance'">
            <!-- Attendance Overview -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
              <div class="bg-white rounded-xl shadow-sm p-6">
                <p class="text-sm text-gray-600 mb-1">Average Attendance</p>
                <p class="text-3xl font-bold text-green-600">{{ courseStats.attendanceRate || 0 }}%</p>
              </div>
              <div class="bg-white rounded-xl shadow-sm p-6">
                <p class="text-sm text-gray-600 mb-1">Total Sessions</p>
                <p class="text-3xl font-bold text-blue-600">{{ courseStats.totalSessions || 0 }}</p>
              </div>
              <div class="bg-white rounded-xl shadow-sm p-6">
                <p class="text-sm text-gray-600 mb-1">At-Risk Students</p>
                <p class="text-3xl font-bold text-red-600">{{ courseStats.atRiskCount || 0 }}</p>
              </div>
            </div>

            <!-- Attendance Chart -->
            <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">Attendance Trend</h3>
              <div class="h-64">
                <canvas ref="attendanceChart"></canvas>
              </div>
            </div>

            <!-- Student Attendance Table -->
            <div class="bg-white rounded-xl shadow-sm overflow-hidden">
              <div class="p-4 border-b border-gray-200">
                <h3 class="text-lg font-semibold text-gray-900">Student Attendance Records</h3>
              </div>
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Student</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Present</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Absent</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Late</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rate</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="student in studentAttendanceData" :key="student.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center">
                        <div class="flex-shrink-0 h-8 w-8 bg-blue-100 rounded-full flex items-center justify-center">
                          <span class="text-blue-600 font-semibold text-xs">
                            {{ getInitials(student.firstName, student.lastName) }}
                          </span>
                        </div>
                        <div class="ml-3">
                          <div class="text-sm font-medium text-gray-900">{{ student.firstName }} {{ student.lastName }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-green-600 font-medium">{{ student.presentCount || 0 }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-red-600 font-medium">{{ student.absentCount || 0 }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-yellow-600 font-medium">{{ student.lateCount || 0 }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center gap-2">
                        <div class="w-20 bg-gray-200 rounded-full h-2">
                          <div class="h-2 rounded-full" :class="getAttendanceBarColor(student.rate)" :style="{ width: `${student.rate || 0}%` }"></div>
                        </div>
                        <span class="text-sm font-medium">{{ student.rate || 0 }}%</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 text-xs font-semibold rounded-full" :class="getAttendanceStatusClass(student.rate)">
                        {{ getAttendanceStatus(student.rate) }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Student Performance Modal -->
    <div v-if="showStudentModal && selectedStudent" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click="showStudentModal = false">
      <div class="bg-white rounded-2xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto" @click.stop>
        <!-- Modal Header -->
        <div class="sticky top-0 bg-white px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="h-12 w-12 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-full flex items-center justify-center">
              <span class="text-white font-bold text-lg">
                {{ getInitials(selectedStudent.firstName, selectedStudent.lastName) }}
              </span>
            </div>
            <div>
              <h3 class="text-xl font-bold text-gray-900">{{ selectedStudent.firstName }} {{ selectedStudent.lastName }}</h3>
              <p class="text-sm text-gray-600">{{ selectedStudent.email }}</p>
            </div>
          </div>
          <button @click="showStudentModal = false" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Modal Content -->
        <div class="p-6">
          <!-- Performance Summary Cards -->
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4 text-center">
              <p class="text-sm text-blue-600 font-medium">Overall Grade</p>
              <p class="text-3xl font-bold text-blue-700 mt-1">{{ selectedStudent.overallGrade || '-' }}%</p>
              <p class="text-lg font-semibold text-blue-800">{{ selectedStudent.letterGrade || '-' }}</p>
            </div>
            <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4 text-center">
              <p class="text-sm text-green-600 font-medium">Attendance</p>
              <p class="text-3xl font-bold text-green-700 mt-1">{{ selectedStudent.attendance || 0 }}%</p>
            </div>
            <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-4 text-center">
              <p class="text-sm text-purple-600 font-medium">Assignments</p>
              <p class="text-3xl font-bold text-purple-700 mt-1">{{ selectedStudent.assignmentAvg || '-' }}%</p>
            </div>
            <div class="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl p-4 text-center">
              <p class="text-sm text-orange-600 font-medium">Exams</p>
              <p class="text-3xl font-bold text-orange-700 mt-1">{{ selectedStudent.examAvg || '-' }}%</p>
            </div>
          </div>

          <!-- Performance Chart -->
          <div class="bg-gray-50 rounded-xl p-6 mb-6">
            <h4 class="text-lg font-semibold text-gray-900 mb-4">Performance Overview</h4>
            <div class="h-64">
              <canvas ref="studentPerformanceChart"></canvas>
            </div>
          </div>

          <!-- Grade Breakdown -->
          <div class="bg-gray-50 rounded-xl p-6 mb-6">
            <h4 class="text-lg font-semibold text-gray-900 mb-4">Grade Breakdown</h4>
            <div class="space-y-4">
              <div v-for="grade in selectedStudent.grades" :key="grade.id" class="flex items-center justify-between bg-white rounded-lg p-3">
                <div>
                  <p class="font-medium text-gray-900">{{ grade.assignmentTitle || 'Unknown Assignment' }}</p>
                  <p class="text-sm text-gray-500">{{ grade.type }} | {{ formatDate(grade.gradedDate) }}</p>
                </div>
                <div class="text-right">
                  <p class="text-lg font-bold" :class="getGradeColor(grade.score)">{{ grade.score }}%</p>
                  <p class="text-sm text-gray-500">{{ grade.score }}/{{ grade.maxPoints }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Attendance History -->
          <div class="bg-gray-50 rounded-xl p-6">
            <h4 class="text-lg font-semibold text-gray-900 mb-4">Attendance History</h4>
            <div class="h-48">
              <canvas ref="studentAttendanceChart"></canvas>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick, h } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import Chart from 'chart.js/auto'

const authStore = useAuthStore()
const userId = computed(() => authStore.userId)

// State
const loading = ref(false)
const courses = ref([])
const selectedCourse = ref(null)
const activeTab = ref('overview')

// Course Data
const courseStats = ref({})
const courseAssignments = ref([])
const courseGrades = ref([])
const courseMaterials = ref([])
const roster = ref([])

// Charts
const gradeDistributionChart = ref(null)
const performanceTrendChart = ref(null)
const assignmentScoresChart = ref(null)
const attendanceChart = ref(null)
const studentPerformanceChart = ref(null)
const studentAttendanceChart = ref(null)

let chartInstances = {}

// Student Modal
const showStudentModal = ref(false)
const selectedStudent = ref(null)

// File Upload
const isDragging = ref(false)
const selectedFiles = ref([])
const uploading = ref(false)
const activeMaterialType = ref('all')

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

// Filters
const studentSearch = ref('')
const studentFilter = ref('all')

// Tab definitions
const tabs = [
  { id: 'overview', name: 'Overview', icon: h('svg', { class: 'h-5 w-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z' })]) },
  { id: 'grades', name: 'Grades', icon: h('svg', { class: 'h-5 w-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' })]) },
  { id: 'students', name: 'Students', icon: h('svg', { class: 'h-5 w-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' })]) },
  { id: 'materials', name: 'Materials', icon: h('svg', { class: 'h-5 w-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z' })]) },
  { id: 'attendance', name: 'Attendance', icon: h('svg', { class: 'h-5 w-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z' })]) }
]

const materialTypes = [
  { value: 'all', label: 'All' },
  { value: 'lecture', label: 'Lectures' },
  { value: 'assignment', label: 'Assignments' },
  { value: 'lab', label: 'Labs' },
  { value: 'notes', label: 'Notes' },
  { value: 'syllabus', label: 'Syllabus' }
]

// Computed
const gradedItemsSummary = computed(() => {
  const types = ['Assignment', 'Quiz', 'Midterm', 'Final']
  return types.map(type => {
    const items = courseAssignments.value.filter(a => (a.type || 'Assignment') === type)
    const scores = items.flatMap(a => a.scores || [])
    return {
      type,
      count: items.length,
      mean: scores.length > 0 ? Math.round(scores.reduce((a, b) => a + b, 0) / scores.length) : '-',
      median: scores.length > 0 ? calculateMedian(scores) : '-',
      stdDev: scores.length > 1 ? calculateStdDev(scores).toFixed(1) : '-',
      bgClass: type === 'Assignment' ? 'bg-blue-100 text-blue-800' :
               type === 'Quiz' ? 'bg-green-100 text-green-800' :
               type === 'Midterm' ? 'bg-orange-100 text-orange-800' :
               'bg-purple-100 text-purple-800'
    }
  })
})

const filteredStudents = computed(() => {
  let students = roster.value

  // Search filter
  if (studentSearch.value) {
    const search = studentSearch.value.toLowerCase()
    students = students.filter(s =>
      (s.firstName?.toLowerCase().includes(search)) ||
      (s.lastName?.toLowerCase().includes(search)) ||
      (s.email?.toLowerCase().includes(search))
    )
  }

  // Grade filter
  if (studentFilter.value !== 'all') {
    students = students.filter(s => {
      const grade = s.overallGrade || 0
      switch (studentFilter.value) {
        case 'excellent': return grade >= 90
        case 'good': return grade >= 70 && grade < 90
        case 'needs-improvement': return grade >= 50 && grade < 70
        case 'at-risk': return grade < 50
        default: return true
      }
    })
  }

  return students
})

const studentAttendanceData = computed(() => {
  return roster.value.map(student => ({
    ...student,
    presentCount: student.presentCount || Math.floor(Math.random() * 20) + 10,
    absentCount: student.absentCount || Math.floor(Math.random() * 5),
    lateCount: student.lateCount || Math.floor(Math.random() * 3),
    rate: student.attendance || Math.floor(Math.random() * 30) + 70
  }))
})

const filteredMaterials = computed(() => {
  if (activeMaterialType.value === 'all') {
    return courseMaterials.value
  }
  return courseMaterials.value.filter(m => m.type === activeMaterialType.value)
})

// Methods
async function fetchCourses() {
  loading.value = true
  try {
    const response = await api.get(`/courses/instructor/${userId.value}`)
    courses.value = response.data

    // Fetch additional data for each course
    for (let course of courses.value) {
      try {
        const countResponse = await api.get(`/enrollments/course/${course.id}/count`)
        course.enrolledCount = countResponse.data

        // Get assignment count
        try {
          const assignmentsResponse = await api.getCourseAssignments(course.id)
          course.assignmentCount = assignmentsResponse.data?.length || 0
        } catch (e) {
          course.assignmentCount = 0
        }

        // Calculate average grade
        try {
          const gradesResponse = await api.getCourseGrades(course.id)
          const grades = gradesResponse.data || []
          if (grades.length > 0) {
            const avg = grades.reduce((sum, g) => sum + (g.score || 0), 0) / grades.length
            course.avgGrade = Math.round(avg)
          }
        } catch (e) {
          course.avgGrade = null
        }
      } catch (error) {
        console.error(`Error fetching data for course ${course.id}:`, error)
        course.enrolledCount = 0
      }
    }
  } catch (error) {
    console.error('Error fetching courses:', error)
  } finally {
    loading.value = false
  }
}

async function selectCourse(course) {
  selectedCourse.value = course
  activeTab.value = 'overview'
  await loadCourseData()
}

async function loadCourseData() {
  if (!selectedCourse.value) return

  const courseId = selectedCourse.value.id

  try {
    // Load enrollments/roster
    const enrollmentsResponse = await api.get(`/enrollments/course/${courseId}`)
    roster.value = enrollmentsResponse.data.map(e => ({
      id: e.student?.id,
      firstName: e.student?.firstName || e.student?.username?.split(' ')[0] || 'Student',
      lastName: e.student?.lastName || e.student?.username?.split(' ')[1] || '',
      email: e.student?.email || 'N/A',
      enrollmentId: e.id,
      enrollmentDate: e.enrollmentDate,
      // Simulated grade data - in real app, this would come from API
      assignmentAvg: Math.floor(Math.random() * 30) + 70,
      quizAvg: Math.floor(Math.random() * 30) + 65,
      midtermScore: Math.floor(Math.random() * 40) + 60,
      finalScore: Math.floor(Math.random() * 40) + 55,
      overallGrade: null,
      letterGrade: null,
      attendance: Math.floor(Math.random() * 25) + 75,
      grades: []
    }))

    // Calculate overall grades
    roster.value.forEach(student => {
      const assignments = student.assignmentAvg * 0.3
      const quizzes = student.quizAvg * 0.2
      const midterm = student.midtermScore * 0.2
      const final = student.finalScore * 0.3
      student.overallGrade = Math.round(assignments + quizzes + midterm + final)
      student.letterGrade = getLetterGrade(student.overallGrade)
      student.examAvg = Math.round((student.midtermScore + student.finalScore) / 2)
    })

    // Load assignments
    try {
      const assignmentsResponse = await api.getCourseAssignments(courseId)
      courseAssignments.value = assignmentsResponse.data.map(a => ({
        ...a,
        meanScore: Math.floor(Math.random() * 25) + 70,
        medianScore: Math.floor(Math.random() * 25) + 72,
        submissionCount: Math.floor(Math.random() * roster.value.length)
      }))
    } catch (e) {
      // Generate sample assignments
      courseAssignments.value = [
        { id: 1, title: 'Assignment 1', type: 'Assignment', dueDate: new Date(), maxPoints: 100, meanScore: 82, medianScore: 85, submissionCount: roster.value.length - 2 },
        { id: 2, title: 'Quiz 1', type: 'Quiz', dueDate: new Date(), maxPoints: 50, meanScore: 78, medianScore: 80, submissionCount: roster.value.length },
        { id: 3, title: 'Midterm Exam', type: 'Midterm', dueDate: new Date(), maxPoints: 100, meanScore: 72, medianScore: 74, submissionCount: roster.value.length },
        { id: 4, title: 'Assignment 2', type: 'Assignment', dueDate: new Date(), maxPoints: 100, meanScore: 85, medianScore: 87, submissionCount: roster.value.length - 1 }
      ]
    }

    // Load course materials (mock for now)
    courseMaterials.value = [
      { id: 1, title: 'Week 1 - Introduction', type: 'lecture', fileName: 'intro.pdf', fileSize: 2500000, uploadedAt: new Date() },
      { id: 2, title: 'Course Syllabus', type: 'syllabus', fileName: 'syllabus.pdf', fileSize: 150000, uploadedAt: new Date() },
      { id: 3, title: 'Lab 1 Instructions', type: 'lab', fileName: 'lab1.pdf', fileSize: 800000, uploadedAt: new Date() }
    ]

    // Calculate course stats
    courseStats.value = {
      classAverage: Math.round(roster.value.reduce((sum, s) => sum + (s.overallGrade || 0), 0) / (roster.value.length || 1)),
      attendanceRate: Math.round(roster.value.reduce((sum, s) => sum + (s.attendance || 0), 0) / (roster.value.length || 1)),
      totalSessions: 24,
      atRiskCount: roster.value.filter(s => s.attendance < 70).length
    }

    // Draw charts after data is loaded
    await nextTick()
    drawCharts()
  } catch (error) {
    console.error('Error loading course data:', error)
  }
}

function drawCharts() {
  // Destroy existing charts
  Object.values(chartInstances).forEach(chart => {
    if (chart) chart.destroy()
  })

  // Grade Distribution Chart
  if (gradeDistributionChart.value) {
    const ctx = gradeDistributionChart.value.getContext('2d')
    const gradeCounts = {
      'A': roster.value.filter(s => s.overallGrade >= 90).length,
      'B': roster.value.filter(s => s.overallGrade >= 80 && s.overallGrade < 90).length,
      'C': roster.value.filter(s => s.overallGrade >= 70 && s.overallGrade < 80).length,
      'D': roster.value.filter(s => s.overallGrade >= 60 && s.overallGrade < 70).length,
      'F': roster.value.filter(s => s.overallGrade < 60).length
    }

    chartInstances.gradeDistribution = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: Object.keys(gradeCounts),
        datasets: [{
          label: 'Number of Students',
          data: Object.values(gradeCounts),
          backgroundColor: ['#10b981', '#3b82f6', '#f59e0b', '#f97316', '#ef4444'],
          borderRadius: 8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true, ticks: { stepSize: 1 } }
        }
      }
    })
  }

  // Performance Trend Chart
  if (performanceTrendChart.value) {
    const ctx = performanceTrendChart.value.getContext('2d')
    chartInstances.performanceTrend = new Chart(ctx, {
      type: 'line',
      data: {
        labels: courseAssignments.value.map(a => a.title),
        datasets: [{
          label: 'Class Average',
          data: courseAssignments.value.map(a => a.meanScore),
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          fill: true,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top' }
        },
        scales: {
          y: { beginAtZero: false, min: 0, max: 100 }
        }
      }
    })
  }

  // Assignment Scores Chart
  if (assignmentScoresChart.value) {
    const ctx = assignmentScoresChart.value.getContext('2d')
    chartInstances.assignmentScores = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: courseAssignments.value.map(a => a.title),
        datasets: [
          {
            label: 'Mean Score',
            data: courseAssignments.value.map(a => a.meanScore),
            backgroundColor: '#3b82f6',
            borderRadius: 4
          },
          {
            label: 'Median Score',
            data: courseAssignments.value.map(a => a.medianScore),
            backgroundColor: '#10b981',
            borderRadius: 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top' }
        },
        scales: {
          y: { beginAtZero: false, min: 0, max: 100 }
        }
      }
    })
  }

  // Attendance Chart
  if (attendanceChart.value) {
    const ctx = attendanceChart.value.getContext('2d')
    const weeks = ['Week 1', 'Week 2', 'Week 3', 'Week 4', 'Week 5', 'Week 6', 'Week 7', 'Week 8']
    chartInstances.attendance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: weeks,
        datasets: [{
          label: 'Attendance Rate (%)',
          data: weeks.map(() => Math.floor(Math.random() * 15) + 80),
          borderColor: '#10b981',
          backgroundColor: 'rgba(16, 185, 129, 0.1)',
          fill: true,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: { beginAtZero: false, min: 50, max: 100 }
        }
      }
    })
  }
}

function viewStudentDetails(student) {
  selectedStudent.value = {
    ...student,
    grades: courseAssignments.value.map(a => ({
      id: a.id,
      assignmentTitle: a.title,
      type: a.type || 'Assignment',
      score: Math.floor(Math.random() * 30) + 65,
      maxPoints: a.maxPoints || 100,
      gradedDate: new Date()
    }))
  }
  showStudentModal.value = true

  nextTick(() => {
    drawStudentCharts()
  })
}

function drawStudentCharts() {
  // Student Performance Radar Chart
  if (studentPerformanceChart.value) {
    if (chartInstances.studentPerformance) {
      chartInstances.studentPerformance.destroy()
    }

    const ctx = studentPerformanceChart.value.getContext('2d')
    chartInstances.studentPerformance = new Chart(ctx, {
      type: 'radar',
      data: {
        labels: ['Assignments', 'Quizzes', 'Midterm', 'Final', 'Attendance'],
        datasets: [{
          label: 'Performance',
          data: [
            selectedStudent.value.assignmentAvg,
            selectedStudent.value.quizAvg,
            selectedStudent.value.midtermScore,
            selectedStudent.value.finalScore,
            selectedStudent.value.attendance
          ],
          backgroundColor: 'rgba(59, 130, 246, 0.2)',
          borderColor: '#3b82f6',
          pointBackgroundColor: '#3b82f6'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          r: {
            beginAtZero: true,
            max: 100
          }
        }
      }
    })
  }

  // Student Attendance Chart
  if (studentAttendanceChart.value) {
    if (chartInstances.studentAttendance) {
      chartInstances.studentAttendance.destroy()
    }

    const ctx = studentAttendanceChart.value.getContext('2d')
    const sessions = ['Session 1', 'Session 2', 'Session 3', 'Session 4', 'Session 5', 'Session 6', 'Session 7', 'Session 8']
    chartInstances.studentAttendance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: sessions,
        datasets: [{
          label: 'Attendance',
          data: sessions.map(() => Math.random() > 0.15 ? 1 : 0),
          backgroundColor: sessions.map(() => Math.random() > 0.15 ? '#10b981' : '#ef4444'),
          borderRadius: 4
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
            beginAtZero: true,
            max: 1,
            ticks: {
              callback: function(value) {
                return value === 1 ? 'Present' : 'Absent'
              }
            }
          }
        }
      }
    })
  }
}

// File Upload Methods
function handleDragOver(e) {
  isDragging.value = true
}

function handleDragLeave(e) {
  isDragging.value = false
}

function handleDrop(e) {
  isDragging.value = false
  const files = Array.from(e.dataTransfer.files).filter(f => f.type === 'application/pdf')
  addFiles(files)
}

function handleFileSelect(e) {
  const files = Array.from(e.target.files)
  addFiles(files)
  e.target.value = ''
}

function addFiles(files) {
  const newFiles = files.map(f => ({
    file: f,
    name: f.name,
    size: f.size,
    materialType: 'lecture'
  }))
  selectedFiles.value.push(...newFiles)
}

function removeFile(index) {
  selectedFiles.value.splice(index, 1)
}

async function uploadFiles() {
  if (selectedFiles.value.length === 0) return

  uploading.value = true
  try {
    for (const file of selectedFiles.value) {
      try {
        await api.uploadCourseMaterial(
          file.file,
          selectedCourse.value.id,
          file.materialType,
          file.name.replace('.pdf', '')
        )
      } catch (e) {
        console.error('Upload error:', e)
      }
    }

    // Add to local materials list (mock)
    selectedFiles.value.forEach(file => {
      courseMaterials.value.push({
        id: Date.now() + Math.random(),
        title: file.name.replace('.pdf', ''),
        type: file.materialType,
        fileName: file.name,
        fileSize: file.size,
        uploadedAt: new Date()
      })
    })

    selectedFiles.value = []
    showNotification('Files uploaded successfully!', 'success')
  } catch (error) {
    console.error('Error uploading files:', error)
    showNotification('Failed to upload some files', 'error')
  } finally {
    uploading.value = false
  }
}

async function downloadMaterial(material) {
  try {
    const response = await api.downloadCourseMaterial(material.id)
    const blob = new Blob([response.data], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = material.fileName
    a.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Error downloading material:', error)
    showNotification('Download functionality will be available when backend endpoint is implemented', 'error')
  }
}

async function deleteMaterial(material) {
  if (!confirm('Are you sure you want to delete this material?')) return

  try {
    await api.deleteCourseMaterial(material.id)
    courseMaterials.value = courseMaterials.value.filter(m => m.id !== material.id)
  } catch (error) {
    console.error('Error deleting material:', error)
    // Remove locally for demo
    courseMaterials.value = courseMaterials.value.filter(m => m.id !== material.id)
  }
}

// Utility Functions
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

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

function getInitials(firstName, lastName) {
  const first = firstName ? firstName.charAt(0).toUpperCase() : ''
  const last = lastName ? lastName.charAt(0).toUpperCase() : ''
  return first + last || 'NA'
}

function getLetterGrade(score) {
  if (score >= 90) return 'A'
  if (score >= 80) return 'B'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

function getGradeColor(score) {
  if (!score) return 'text-gray-500'
  if (score >= 90) return 'text-green-600'
  if (score >= 80) return 'text-blue-600'
  if (score >= 70) return 'text-yellow-600'
  if (score >= 60) return 'text-orange-600'
  return 'text-red-600'
}

function getLetterGradeClass(grade) {
  const classes = {
    'A': 'bg-green-100 text-green-800',
    'B': 'bg-blue-100 text-blue-800',
    'C': 'bg-yellow-100 text-yellow-800',
    'D': 'bg-orange-100 text-orange-800',
    'F': 'bg-red-100 text-red-800'
  }
  return classes[grade] || 'bg-gray-100 text-gray-800'
}

function getAttendanceBarColor(rate) {
  if (rate >= 90) return 'bg-green-500'
  if (rate >= 75) return 'bg-blue-500'
  if (rate >= 60) return 'bg-yellow-500'
  return 'bg-red-500'
}

function getAttendanceStatus(rate) {
  if (rate >= 90) return 'Excellent'
  if (rate >= 75) return 'Good'
  if (rate >= 60) return 'Warning'
  return 'At Risk'
}

function getAttendanceStatusClass(rate) {
  if (rate >= 90) return 'bg-green-100 text-green-800'
  if (rate >= 75) return 'bg-blue-100 text-blue-800'
  if (rate >= 60) return 'bg-yellow-100 text-yellow-800'
  return 'bg-red-100 text-red-800'
}

function getAssignmentTypeClass(type) {
  const classes = {
    'Assignment': 'bg-blue-100 text-blue-800',
    'Quiz': 'bg-green-100 text-green-800',
    'Midterm': 'bg-orange-100 text-orange-800',
    'Final': 'bg-purple-100 text-purple-800'
  }
  return classes[type] || 'bg-gray-100 text-gray-800'
}

function calculateMedian(arr) {
  const sorted = [...arr].sort((a, b) => a - b)
  const mid = Math.floor(sorted.length / 2)
  return sorted.length % 2 !== 0 ? sorted[mid] : Math.round((sorted[mid - 1] + sorted[mid]) / 2)
}

function calculateStdDev(arr) {
  const mean = arr.reduce((a, b) => a + b, 0) / arr.length
  const squareDiffs = arr.map(value => Math.pow(value - mean, 2))
  return Math.sqrt(squareDiffs.reduce((a, b) => a + b, 0) / arr.length)
}

function getMaterialCount(type) {
  if (type === 'all') return courseMaterials.value.length
  return courseMaterials.value.filter(m => m.type === type).length
}

function refreshCourses() {
  fetchCourses()
}

function viewAssignmentDetails(assignment) {
  // TODO: Open a modal with detailed assignment analytics
}

onMounted(() => {
  fetchCourses()
})

// Watch for tab changes to redraw charts
watch(activeTab, async (newTab) => {
  if (newTab === 'overview' || newTab === 'grades' || newTab === 'attendance') {
    await nextTick()
    drawCharts()
  }
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
