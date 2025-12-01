<template>
  <div class="max-w-7xl mx-auto">
    <!-- Toast Notification -->
    <div v-if="showToast" class="fixed top-4 right-4 z-50 max-w-sm">
      <div :class="[
        'rounded-lg px-4 py-3 shadow-lg',
        toastType === 'success' ? 'bg-green-100 border border-green-400 text-green-700 dark:bg-green-900/30 dark:border-green-700 dark:text-green-400' : 'bg-red-100 border border-red-400 text-red-700 dark:bg-red-900/30 dark:border-red-700 dark:text-red-400'
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
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Course Management</h1>
        <p class="mt-2 text-gray-600 dark:text-gray-400">Manage all courses in the system</p>
      </div>
      <button
        @click="openCreateModal"
        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium shadow-sm flex items-center"
      >
        <svg class="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Add Course
      </button>
    </div>

    <!-- Filters and Search -->
    <div class="bg-white dark:bg-slate-800 rounded-lg shadow-sm border border-gray-200 dark:border-slate-700 p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Search</label>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search by code or name..."
            class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Department</label>
          <select
            v-model="filterDepartment"
            class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
          >
            <option value="">All Departments</option>
            <option value="Computer Science">Computer Science</option>
            <option value="Mathematics">Mathematics</option>
            <option value="Physics">Physics</option>
            <option value="Engineering">Engineering</option>
            <option value="Business">Business</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Semester</label>
          <select
            v-model="filterSemester"
            class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
          >
            <option value="">All Semesters</option>
            <option value="Fall 2024">Fall 2024</option>
            <option value="Spring 2025">Spring 2025</option>
            <option value="Summer 2025">Summer 2025</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="resetFilters"
            class="w-full px-4 py-2 bg-gray-100 dark:bg-slate-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-slate-600 transition-colors"
          >
            Reset Filters
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- Courses Table -->
    <div v-else class="bg-white dark:bg-slate-800 rounded-lg shadow overflow-hidden border border-gray-200 dark:border-slate-700">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-slate-700">
          <thead class="bg-gray-50 dark:bg-slate-900/50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Course Code
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Course Name
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Department
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Credits
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Instructor
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Enrollment
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-slate-800 divide-y divide-gray-200 dark:divide-slate-700">
            <tr v-if="filteredCourses.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500 dark:text-gray-400">
                <svg class="mx-auto h-12 w-12 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                <p class="mt-4">No courses found</p>
              </td>
            </tr>
            <tr v-for="course in filteredCourses" :key="course.id" class="hover:bg-gray-50 dark:hover:bg-slate-700/50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-semibold text-blue-600 dark:text-blue-400">{{ course.courseCode }}</div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-white">{{ course.courseName }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">{{ course.description?.substring(0, 50) }}...</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-300">
                {{ course.department || 'N/A' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ course.credits || course.creditHours }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-white">{{ course.instructor?.username || course.instructorName || 'Not Assigned' }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-white">
                  {{ course.enrolledCount || 0 }} / {{ course.capacity }}
                </div>
                <div class="w-full bg-gray-200 dark:bg-slate-600 rounded-full h-1.5 mt-1">
                  <div
                    class="bg-blue-600 h-1.5 rounded-full"
                    :style="{ width: `${getEnrollmentPercentage(course)}%` }"
                  ></div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-3">
                <button
                  @click="viewCourse(course)"
                  class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                >
                  View
                </button>
                <button
                  @click="editCourse(course)"
                  class="text-indigo-600 hover:text-indigo-900 dark:text-indigo-400 dark:hover:text-indigo-300"
                >
                  Edit
                </button>
                <button
                  @click="confirmDelete(course)"
                  class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Course Modal -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-gray-600/50 dark:bg-black/60 overflow-y-auto h-full w-full z-50"
      @click.self="closeModal"
    >
      <div class="relative top-20 mx-auto p-8 border w-full max-w-3xl shadow-lg rounded-lg bg-white dark:bg-slate-800 dark:border-slate-700">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-2xl font-bold text-gray-900 dark:text-white">
            {{ editingCourse ? 'Edit Course' : 'Create New Course' }}
          </h2>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form @submit.prevent="saveCourse" class="space-y-6">
          <div class="grid grid-cols-2 gap-4">
            <!-- Course Code -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Course Code <span class="text-red-500">*</span>
              </label>
              <input
                v-model="courseForm.courseCode"
                type="text"
                required
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                placeholder="e.g., CS101"
              />
            </div>

            <!-- Credits -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Credits <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="courseForm.credits"
                type="number"
                min="1"
                max="6"
                required
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                placeholder="3"
              />
            </div>
          </div>

          <!-- Course Name -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Course Name <span class="text-red-500">*</span>
            </label>
            <input
              v-model="courseForm.courseName"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              placeholder="e.g., Introduction to Computer Science"
            />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Description</label>
            <textarea
              v-model="courseForm.description"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              placeholder="Course description..."
            ></textarea>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <!-- Department -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Department</label>
              <select
                v-model="courseForm.department"
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              >
                <option value="">Select Department</option>
                <option value="Computer Science">Computer Science</option>
                <option value="Mathematics">Mathematics</option>
                <option value="Physics">Physics</option>
                <option value="Engineering">Engineering</option>
                <option value="Business">Business</option>
              </select>
            </div>

            <!-- Capacity -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Capacity <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="courseForm.capacity"
                type="number"
                min="1"
                required
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                placeholder="30"
              />
            </div>
          </div>

          <div class="grid grid-cols-3 gap-4">
            <!-- Days of Week -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Days</label>
              <input
                v-model="courseForm.daysOfWeek"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                placeholder="e.g., MWF"
              />
            </div>

            <!-- Start Time -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Start Time</label>
              <input
                v-model="courseForm.startTime"
                type="time"
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              />
            </div>

            <!-- End Time -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">End Time</label>
              <input
                v-model="courseForm.endTime"
                type="time"
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <!-- Semester -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Semester</label>
              <select
                v-model="courseForm.semester"
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              >
                <option value="">Select Semester</option>
                <option value="Fall 2024">Fall 2024</option>
                <option value="Spring 2025">Spring 2025</option>
                <option value="Summer 2025">Summer 2025</option>
              </select>
            </div>

            <!-- Instructor -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Instructor</label>
              <select
                v-model="courseForm.instructorId"
                class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              >
                <option :value="null">Select Instructor</option>
                <option v-for="instructor in activeInstructors" :key="instructor.id" :value="Number(instructor.id)">
                  {{ instructor.firstName || instructor.username }} {{ instructor.lastName || '' }}
                </option>
              </select>
            </div>
          </div>

          <!-- Course Fee -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Course Fee ($)</label>
            <input
              v-model.number="courseForm.courseFee"
              type="number"
              min="0"
              step="0.01"
              class="w-full px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              placeholder="0.00"
            />
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Fee that students must pay to enroll in this course</p>
          </div>

          <!-- Form Actions -->
          <div class="flex justify-end space-x-3 pt-4 border-t dark:border-slate-700">
            <button
              type="button"
              @click="closeModal"
              class="px-6 py-2 border border-gray-300 dark:border-slate-600 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-slate-700 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="submitting"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
            >
              {{ submitting ? 'Saving...' : (editingCourse ? 'Update Course' : 'Create Course') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- View Course Modal with Enrollments & Waitlist -->
    <div
      v-if="showViewModal && selectedCourse"
      class="fixed inset-0 bg-gray-600/50 dark:bg-black/60 overflow-y-auto h-full w-full z-50"
      @click.self="showViewModal = false"
    >
      <div class="relative top-10 mx-auto p-8 border w-full max-w-4xl shadow-lg rounded-lg bg-white dark:bg-slate-800 dark:border-slate-700 mb-10">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-2xl font-bold text-gray-900 dark:text-white">Course Details</h2>
          <button @click="showViewModal = false" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="space-y-6">
          <!-- Basic Info -->
          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Course Code</label>
              <p class="text-lg font-semibold text-gray-900 dark:text-white">{{ selectedCourse.courseCode }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Credits</label>
              <p class="text-lg font-semibold text-gray-900 dark:text-white">{{ selectedCourse.credits || selectedCourse.creditHours }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Course Fee</label>
              <p class="text-lg font-semibold text-green-600 dark:text-green-400">${{ selectedCourse.courseFee || '0.00' }}</p>
            </div>
          </div>

          <div>
            <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Course Name</label>
            <p class="text-lg font-semibold text-gray-900 dark:text-white">{{ selectedCourse.courseName }}</p>
          </div>

          <div>
            <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Description</label>
            <p class="text-gray-700 dark:text-gray-300">{{ selectedCourse.description || 'No description provided' }}</p>
          </div>

          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Department</label>
              <p class="text-gray-900 dark:text-white">{{ selectedCourse.department || 'N/A' }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Semester</label>
              <p class="text-gray-900 dark:text-white">{{ selectedCourse.semester || 'N/A' }}</p>
            </div>
            <div>
              <label class="text-sm font-medium text-gray-500 dark:text-gray-400">Capacity</label>
              <p class="text-gray-900 dark:text-white">{{ selectedCourse.enrolledCount || 0 }} / {{ selectedCourse.capacity }}</p>
            </div>
          </div>

          <!-- Instructor Management Section -->
          <div class="bg-gray-50 dark:bg-slate-900/50 rounded-lg p-4 border border-gray-200 dark:border-slate-700">
            <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">Instructor Assignment</h3>
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-500 dark:text-gray-400">Current Instructor:</p>
                <p v-if="selectedCourse.instructor" class="text-gray-900 dark:text-white font-medium">
                  {{ selectedCourse.instructor.username || selectedCourse.instructorName }}
                  <span class="text-sm text-gray-500 dark:text-gray-400">({{ selectedCourse.instructor.email || '' }})</span>
                </p>
                <p v-else class="text-gray-500 dark:text-gray-400 italic">Not assigned</p>
              </div>
              <div class="flex items-center gap-2">
                <select
                  v-model="selectedInstructorId"
                  class="px-3 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 text-sm bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                >
                  <option :value="null">Select instructor...</option>
                  <option v-for="inst in activeInstructors" :key="inst.id" :value="Number(inst.id)">
                    {{ inst.firstName || inst.username }} {{ inst.lastName || '' }}
                  </option>
                </select>
                <button
                  @click="assignInstructorToCourse"
                  :disabled="!selectedInstructorId || assigningInstructor"
                  class="px-3 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {{ assigningInstructor ? 'Assigning...' : 'Assign' }}
                </button>
                <button
                  v-if="selectedCourse.instructor"
                  @click="removeInstructorFromCourse"
                  :disabled="removingInstructor"
                  class="px-3 py-2 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 text-sm rounded-lg hover:bg-red-200 dark:hover:bg-red-900/50 disabled:opacity-50"
                >
                  {{ removingInstructor ? 'Removing...' : 'Remove' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Tabs for Enrollments and Waitlist -->
          <div class="border-t dark:border-slate-700 pt-6">
            <div class="flex space-x-4 border-b border-gray-200 dark:border-slate-700 mb-4">
              <button
                @click="activeViewTab = 'enrollments'"
                :class="[
                  'px-4 py-2 text-sm font-medium border-b-2 -mb-px',
                  activeViewTab === 'enrollments'
                    ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                    : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
                ]"
              >
                Enrolled Students ({{ courseEnrollments.length }})
              </button>
              <button
                @click="activeViewTab = 'waitlist'"
                :class="[
                  'px-4 py-2 text-sm font-medium border-b-2 -mb-px',
                  activeViewTab === 'waitlist'
                    ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                    : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
                ]"
              >
                Waiting List ({{ courseWaitlist.length }})
              </button>
            </div>

            <!-- Enrollments Tab -->
            <div v-if="activeViewTab === 'enrollments'" class="max-h-64 overflow-y-auto">
              <div v-if="loadingEnrollments" class="text-center py-4">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              </div>
              <div v-else-if="courseEnrollments.length === 0" class="text-center py-8 text-gray-500 dark:text-gray-400">
                <p>No students enrolled yet</p>
              </div>
              <table v-else class="min-w-full divide-y divide-gray-200 dark:divide-slate-700">
                <thead class="bg-gray-50 dark:bg-slate-900/50">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Student</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Email</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Status</th>
                    <th class="px-4 py-2 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white dark:bg-slate-800 divide-y divide-gray-200 dark:divide-slate-700">
                  <tr v-for="enrollment in courseEnrollments" :key="enrollment.id">
                    <td class="px-4 py-3 text-sm font-medium text-gray-900 dark:text-white">{{ enrollment.student?.username }}</td>
                    <td class="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{{ enrollment.student?.email }}</td>
                    <td class="px-4 py-3">
                      <span
                        class="px-2 py-1 text-xs font-semibold rounded-full"
                        :class="getEnrollmentStatusClass(enrollment.status)"
                      >{{ enrollment.status }}</span>
                    </td>
                    <td class="px-4 py-3 text-right">
                      <button
                        @click="removeEnrollment(enrollment.id)"
                        class="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 text-sm"
                      >Remove</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Waitlist Tab -->
            <div v-if="activeViewTab === 'waitlist'" class="max-h-64 overflow-y-auto">
              <div v-if="loadingWaitlist" class="text-center py-4">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              </div>
              <div v-else-if="courseWaitlist.length === 0" class="text-center py-8 text-gray-500 dark:text-gray-400">
                <p>No students on waiting list</p>
              </div>
              <table v-else class="min-w-full divide-y divide-gray-200 dark:divide-slate-700">
                <thead class="bg-gray-50 dark:bg-slate-900/50">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Position</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Student</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Email</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Date Added</th>
                    <th class="px-4 py-2 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white dark:bg-slate-800 divide-y divide-gray-200 dark:divide-slate-700">
                  <tr v-for="(student, index) in courseWaitlist" :key="student.studentId">
                    <td class="px-4 py-3 text-sm font-bold text-gray-900 dark:text-white">#{{ student.position || index + 1 }}</td>
                    <td class="px-4 py-3 text-sm font-medium text-gray-900 dark:text-white">{{ student.username }}</td>
                    <td class="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{{ student.email }}</td>
                    <td class="px-4 py-3 text-sm text-gray-500 dark:text-gray-400">{{ formatDate(student.enrollmentDate) }}</td>
                    <td class="px-4 py-3 text-right space-x-2">
                      <button
                        @click="promoteFromWaitlist(student)"
                        class="text-green-600 hover:text-green-800 dark:text-green-400 dark:hover:text-green-300 text-sm"
                        :disabled="selectedCourse.enrolledCount >= selectedCourse.capacity"
                      >Promote</button>
                      <button
                        @click="removeFromWaitlist(student.studentId)"
                        class="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 text-sm"
                      >Remove</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Add Student to Course -->
          <div class="border-t dark:border-slate-700 pt-4">
            <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">Add Student to Course</h3>
            <div class="flex gap-3">
              <select
                v-model="selectedStudentToAdd"
                class="flex-1 px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
              >
                <option value="">Select a student...</option>
                <option v-for="student in availableStudents" :key="student.id" :value="student.id">
                  {{ student.username }} ({{ student.email }})
                </option>
              </select>
              <button
                @click="addStudentToCourse"
                :disabled="!selectedStudentToAdd"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Add Student
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-gray-600/50 dark:bg-black/60 overflow-y-auto h-full w-full z-50"
      @click.self="showDeleteModal = false"
    >
      <div class="relative top-20 mx-auto p-6 border w-full max-w-md shadow-lg rounded-lg bg-white dark:bg-slate-800 dark:border-slate-700">
        <h2 class="text-xl font-bold text-gray-900 dark:text-white mb-4">Confirm Deletion</h2>
        <p class="text-gray-600 dark:text-gray-300 mb-6">
          Are you sure you want to delete <strong class="dark:text-white">{{ courseToDelete?.courseCode }} - {{ courseToDelete?.courseName }}</strong>?
          This action cannot be undone.
        </p>
        <div class="flex justify-end space-x-3">
          <button
            @click="showDeleteModal = false"
            class="px-4 py-2 border border-gray-300 dark:border-slate-600 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-slate-700"
          >
            Cancel
          </button>
          <button
            @click="deleteCourse"
            :disabled="submitting"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50"
          >
            {{ submitting ? 'Deleting...' : 'Delete Course' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../services/api'

const courses = ref([])
const instructors = ref([])
const loading = ref(false)
const submitting = ref(false)

const showModal = ref(false)
const showViewModal = ref(false)
const showDeleteModal = ref(false)

const editingCourse = ref(null)
const selectedCourse = ref(null)
const courseToDelete = ref(null)

// Enrollment & Waitlist
const courseEnrollments = ref([])
const courseWaitlist = ref([])
const loadingEnrollments = ref(false)
const loadingWaitlist = ref(false)
const activeViewTab = ref('enrollments')
const selectedStudentToAdd = ref('')
const availableStudents = ref([])

const searchQuery = ref('')
const filterDepartment = ref('')
const filterSemester = ref('')

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

// Instructor assignment
const selectedInstructorId = ref(null)
const assigningInstructor = ref(false)
const removingInstructor = ref(false)

const courseForm = ref({
  courseCode: '',
  courseName: '',
  description: '',
  credits: 3,
  department: '',
  capacity: 30,
  daysOfWeek: '',
  startTime: '',
  endTime: '',
  semester: '',
  instructorId: null,
  courseFee: 0
})

const filteredCourses = computed(() => {
  let filtered = courses.value

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(course =>
      course.courseCode?.toLowerCase().includes(query) ||
      course.courseName?.toLowerCase().includes(query)
    )
  }

  // Department filter
  if (filterDepartment.value) {
    filtered = filtered.filter(course => course.department === filterDepartment.value)
  }

  // Semester filter
  if (filterSemester.value) {
    filtered = filtered.filter(course => course.semester === filterSemester.value)
  }

  return filtered
})

// Filter only active instructors for dropdowns
const activeInstructors = computed(() => {
  return instructors.value.filter(inst => inst.active !== false)
})

onMounted(async () => {
  await loadCourses()
  await loadInstructors()
})

async function loadCourses() {
  try {
    loading.value = true
    const response = await api.get('/courses')
    courses.value = response.data

    // Load enrollment counts for each course
    for (let course of courses.value) {
      try {
        const countResponse = await api.get(`/enrollments/course/${course.id}/count`)
        course.enrolledCount = countResponse.data
      } catch (error) {
        console.error(`Error loading enrollment count for course ${course.id}:`, error)
        course.enrolledCount = 0
      }
    }
  } catch (error) {
    console.error('Error loading courses:', error)
    showNotification(error.userMessage || error.response?.data?.message || 'Failed to load courses', 'error')
  } finally {
    loading.value = false
  }
}

async function loadInstructors() {
  try {
    // Use the dedicated role endpoint for better performance
    const response = await api.get('/users/role/FACULTY')
    instructors.value = response.data
    console.log('Loaded instructors:', instructors.value.length)
  } catch (error) {
    console.error('Error loading instructors:', error)
    // Fallback to getting all users if role endpoint fails
    try {
      const allUsersResponse = await api.get('/users')
      instructors.value = allUsersResponse.data.filter(user => user.role === 'FACULTY')
    } catch (fallbackError) {
      console.error('Fallback also failed:', fallbackError)
      instructors.value = []
    }
  }
}

function openCreateModal() {
  editingCourse.value = null
  resetForm()
  showModal.value = true
}

function editCourse(course) {
  editingCourse.value = course
  courseForm.value = {
    courseCode: course.courseCode,
    courseName: course.courseName,
    description: course.description || '',
    credits: course.credits || course.creditHours,
    department: course.department || '',
    capacity: course.capacity,
    daysOfWeek: course.daysOfWeek || '',
    startTime: course.startTime || '',
    endTime: course.endTime || '',
    semester: course.semester || '',
    instructorId: course.instructorId ? Number(course.instructorId) : (course.instructor?.id ? Number(course.instructor.id) : null),
    courseFee: course.courseFee || 0
  }
  showModal.value = true
}

async function viewCourse(course) {
  selectedCourse.value = course
  showViewModal.value = true
  activeViewTab.value = 'enrollments'
  selectedStudentToAdd.value = ''
  selectedInstructorId.value = null

  // Load enrollments and waitlist in parallel
  await Promise.all([
    loadCourseEnrollments(course.id),
    loadCourseWaitlist(course.id),
    loadAvailableStudents(course.id)
  ])
}

async function assignInstructorToCourse() {
  if (!selectedInstructorId.value || !selectedCourse.value) return

  try {
    assigningInstructor.value = true
    await api.assignInstructor(selectedCourse.value.id, Number(selectedInstructorId.value))
    showNotification('Instructor assigned successfully', 'success')

    // Refresh the course data
    const response = await api.getCourseById(selectedCourse.value.id)
    selectedCourse.value = response.data
    selectedInstructorId.value = null
    await loadCourses()
  } catch (error) {
    console.error('Error assigning instructor:', error)
    showNotification(error.response?.data?.message || 'Failed to assign instructor', 'error')
  } finally {
    assigningInstructor.value = false
  }
}

async function removeInstructorFromCourse() {
  if (!selectedCourse.value) return
  if (!confirm('Are you sure you want to remove the instructor from this course?')) return

  try {
    removingInstructor.value = true
    await api.removeInstructor(selectedCourse.value.id)
    showNotification('Instructor removed successfully', 'success')

    // Refresh the course data
    const response = await api.getCourseById(selectedCourse.value.id)
    selectedCourse.value = response.data
    await loadCourses()
  } catch (error) {
    console.error('Error removing instructor:', error)
    showNotification(error.response?.data?.message || 'Failed to remove instructor', 'error')
  } finally {
    removingInstructor.value = false
  }
}

async function loadCourseEnrollments(courseId) {
  try {
    loadingEnrollments.value = true
    const response = await api.get(`/enrollments/course/${courseId}`)
    // Filter to only show ENROLLED status (not WAITLISTED)
    courseEnrollments.value = response.data.filter(e => e.status === 'ENROLLED' || e.status === 'ACTIVE')
  } catch (error) {
    console.error('Error loading enrollments:', error)
    courseEnrollments.value = []
  } finally {
    loadingEnrollments.value = false
  }
}

async function loadCourseWaitlist(courseId) {
  try {
    loadingWaitlist.value = true
    const response = await api.get(`/courses/${courseId}/waitlist`)
    courseWaitlist.value = response.data
  } catch (error) {
    console.error('Error loading waitlist:', error)
    courseWaitlist.value = []
  } finally {
    loadingWaitlist.value = false
  }
}

async function loadAvailableStudents(courseId) {
  try {
    // Get all students
    const usersResponse = await api.get('/users')
    const students = usersResponse.data.filter(u => u.role === 'STUDENT')

    // Get current enrollments for this course
    const enrollmentsResponse = await api.get(`/enrollments/course/${courseId}`)
    const enrolledStudentIds = enrollmentsResponse.data.map(e => e.student?.id)

    // Filter out already enrolled students
    availableStudents.value = students.filter(s => !enrolledStudentIds.includes(s.id))
  } catch (error) {
    console.error('Error loading available students:', error)
    availableStudents.value = []
  }
}

async function removeEnrollment(enrollmentId) {
  if (!confirm('Are you sure you want to remove this student from the course?')) return

  try {
    await api.delete(`/enrollments/${enrollmentId}`)
    showNotification('Student removed from course', 'success')
    await loadCourseEnrollments(selectedCourse.value.id)
    await loadCourseWaitlist(selectedCourse.value.id)
    await loadCourses()
  } catch (error) {
    console.error('Error removing enrollment:', error)
    showNotification(error.userMessage || error.response?.data?.message || 'Failed to remove student from course', 'error')
  }
}

async function promoteFromWaitlist(student) {
  if (!confirm(`Promote ${student.username} from waitlist to enrolled?`)) return

  try {
    // Find the enrollment by student and course and update status
    const enrollmentsResponse = await api.get(`/enrollments/course/${selectedCourse.value.id}`)
    const waitlistEnrollment = enrollmentsResponse.data.find(e =>
      e.student?.id === student.studentId && e.status === 'WAITLISTED'
    )

    if (waitlistEnrollment) {
      await api.patch(`/enrollments/${waitlistEnrollment.id}/status`, null, {
        params: { status: 'ENROLLED' }
      })
      showNotification(`${student.username} has been enrolled in the course`, 'success')
      await loadCourseEnrollments(selectedCourse.value.id)
      await loadCourseWaitlist(selectedCourse.value.id)
      await loadCourses()
    }
  } catch (error) {
    console.error('Error promoting from waitlist:', error)
    showNotification(error.userMessage || error.response?.data?.message || 'Failed to promote student from waitlist', 'error')
  }
}

async function removeFromWaitlist(studentId) {
  if (!confirm('Remove this student from the waitlist?')) return

  try {
    const enrollmentsResponse = await api.get(`/enrollments/course/${selectedCourse.value.id}`)
    const waitlistEnrollment = enrollmentsResponse.data.find(e =>
      e.student?.id === studentId && e.status === 'WAITLISTED'
    )

    if (waitlistEnrollment) {
      await api.delete(`/enrollments/${waitlistEnrollment.id}`)
      showNotification('Student removed from waitlist', 'success')
      await loadCourseWaitlist(selectedCourse.value.id)
    }
  } catch (error) {
    console.error('Error removing from waitlist:', error)
    showNotification(error.userMessage || error.response?.data?.message || 'Failed to remove student from waitlist', 'error')
  }
}

async function addStudentToCourse() {
  if (!selectedStudentToAdd.value) return

  try {
    await api.post('/enrollments', {
      studentId: selectedStudentToAdd.value,
      courseId: selectedCourse.value.id
    })
    showNotification('Student added to course', 'success')
    selectedStudentToAdd.value = ''
    await loadCourseEnrollments(selectedCourse.value.id)
    await loadCourseWaitlist(selectedCourse.value.id)
    await loadAvailableStudents(selectedCourse.value.id)
    await loadCourses()
  } catch (error) {
    console.error('Error adding student:', error)
    const message = error.response?.data?.message || 'Failed to add student to course'
    showNotification(message, 'error')
  }
}

function getEnrollmentStatusClass(status) {
  const classes = {
    'ENROLLED': 'bg-green-100 text-green-800',
    'ACTIVE': 'bg-green-100 text-green-800',
    'WAITLISTED': 'bg-yellow-100 text-yellow-800',
    'DROPPED': 'bg-red-100 text-red-800',
    'COMPLETED': 'bg-blue-100 text-blue-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function confirmDelete(course) {
  courseToDelete.value = course
  showDeleteModal.value = true
}

async function saveCourse() {
  try {
    submitting.value = true

    const courseData = {
      ...courseForm.value,
      creditHours: courseForm.value.credits
    }

    if (editingCourse.value) {
      await api.put(`/courses/${editingCourse.value.id}`, courseData)
      showNotification('Course updated successfully', 'success')
    } else {
      await api.post('/courses', courseData)
      showNotification('Course created successfully', 'success')
    }

    await loadCourses()
    closeModal()
  } catch (error) {
    console.error('Error saving course:', error)
    showNotification(error.response?.data?.message || 'Failed to save course. Please try again.', 'error')
  } finally {
    submitting.value = false
  }
}

async function deleteCourse() {
  try {
    submitting.value = true
    await api.delete(`/courses/${courseToDelete.value.id}`)
    await loadCourses()
    showDeleteModal.value = false
    courseToDelete.value = null
    showNotification('Course deleted successfully', 'success')
  } catch (error) {
    console.error('Error deleting course:', error)
    showNotification(error.response?.data?.message || 'Failed to delete course. Please try again.', 'error')
  } finally {
    submitting.value = false
  }
}

function closeModal() {
  showModal.value = false
  editingCourse.value = null
  resetForm()
}

function resetForm() {
  courseForm.value = {
    courseCode: '',
    courseName: '',
    description: '',
    credits: 3,
    department: '',
    capacity: 30,
    daysOfWeek: '',
    startTime: '',
    endTime: '',
    semester: '',
    instructorId: null,
    courseFee: 0
  }
}

function resetFilters() {
  searchQuery.value = ''
  filterDepartment.value = ''
  filterSemester.value = ''
}

function getEnrollmentPercentage(course) {
  if (!course.capacity) return 0
  const enrolled = course.enrolledCount || 0
  return Math.min((enrolled / course.capacity) * 100, 100)
}
</script>

<style scoped>
/* Smooth transitions for modals */
</style>
