<!--
  GlobalSearch - universal search component for students, courses, assignments, study groups
  shows dropdown with categorized results and recient searches
  the debouncing was wierd to implement but works for now
-->

<template>
  <div class="relative" ref="searchContainer" role="search" aria-label="Global search">
    <!-- Search Input Field -->
    <div class="relative">
      <label for="global-search-input" class="sr-only">Search students, courses, assignments</label>
      <input
        id="global-search-input"
        v-model="searchQuery"
        @focus="isOpen = true"
        @input="handleSearch"
        type="text"
        placeholder="Search students, courses, assignments..."
        class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        aria-label="Search students, courses, assignments"
        aria-autocomplete="list"
        :aria-expanded="isOpen && searchQuery.length >= 2"
        aria-controls="search-results"
      />
      <svg class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>

      <!-- Loading Spinner -->
      <div v-if="searching" class="absolute right-3 top-2.5">
        <svg class="animate-spin h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
    </div>

    <!-- Search Results Dropdown -->
    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="isOpen && searchQuery.length >= 2"
        id="search-results"
        class="absolute mt-2 w-full bg-white rounded-lg shadow-xl border border-gray-200 z-50 max-h-96 overflow-y-auto"
        role="listbox"
        aria-label="Search results"
      >
        <!-- Results Summary -->
        <div v-if="hasResults" class="px-4 py-2 bg-gray-50 border-b border-gray-200">
          <p class="text-xs text-gray-600">
            Found {{ totalResults }} results for "{{ searchQuery }}"
          </p>
        </div>

        <!-- Students Section -->
        <div v-if="results.users && results.users.length > 0" class="py-2">
          <p class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase">Students & Faculty</p>
          <button
            v-for="user in results.users"
            :key="'user-' + user.id"
            @click="navigate(`/profile/${user.id}`)"
            class="w-full px-4 py-2 hover:bg-gray-50 transition-colors text-left"
          >
            <div class="flex items-center space-x-3">
              <div class="h-8 w-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xs font-semibold">
                {{ getInitials(user.firstName, user.lastName) }}
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">
                  {{ user.firstName }} {{ user.lastName }}
                </p>
                <p class="text-xs text-gray-500 truncate">{{ user.email }} • {{ user.role }}</p>
              </div>
            </div>
          </button>
        </div>

        <!-- Courses Section -->
        <div v-if="results.courses && results.courses.length > 0" class="py-2 border-t border-gray-200">
          <p class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase">Courses</p>
          <button
            v-for="course in results.courses"
            :key="'course-' + course.id"
            @click="navigate(`/student/courses/${course.id}`)"
            class="w-full px-4 py-2 hover:bg-gray-50 transition-colors text-left"
          >
            <div class="flex items-center space-x-3">
              <div class="flex-shrink-0 p-2 bg-purple-100 rounded">
                <svg class="h-5 w-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ course.code }} - {{ course.name }}</p>
                <p class="text-xs text-gray-500 truncate">{{ course.credits }} credits • {{ course.enrollments }} students</p>
              </div>
            </div>
          </button>
        </div>

        <!-- Assignments Section -->
        <div v-if="results.assignments && results.assignments.length > 0" class="py-2 border-t border-gray-200">
          <p class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase">Assignments</p>
          <button
            v-for="assignment in results.assignments"
            :key="'assignment-' + assignment.id"
            @click="navigate(`/student/assignments/${assignment.id}`)"
            class="w-full px-4 py-2 hover:bg-gray-50 transition-colors text-left"
          >
            <div class="flex items-center space-x-3">
              <div class="flex-shrink-0 p-2 bg-green-100 rounded">
                <svg class="h-5 w-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ assignment.title }}</p>
                <p class="text-xs text-gray-500 truncate">
                  {{ assignment.courseCode }} • Due: {{ formatDate(assignment.dueDate) }}
                </p>
              </div>
            </div>
          </button>
        </div>

        <!-- Study Groups Section -->
        <div v-if="results.studyGroups && results.studyGroups.length > 0" class="py-2 border-t border-gray-200">
          <p class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase">Study Groups</p>
          <button
            v-for="group in results.studyGroups"
            :key="'group-' + group.id"
            @click="navigate(`/studygroups/${group.id}`)"
            class="w-full px-4 py-2 hover:bg-gray-50 transition-colors text-left"
          >
            <div class="flex items-center space-x-3">
              <div class="flex-shrink-0 p-2 bg-yellow-100 rounded">
                <svg class="h-5 w-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ group.name }}</p>
                <p class="text-xs text-gray-500 truncate">{{ group.courseCode }} • {{ group.memberCount }} members</p>
              </div>
            </div>
          </button>
        </div>

        <!-- No Results -->
        <div v-if="!searching && !hasResults && searchQuery.length >= 2" class="px-4 py-8 text-center">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <p class="mt-2 text-sm text-gray-600">No results found for "{{ searchQuery }}"</p>
          <p class="text-xs text-gray-500 mt-1">Try different keywords or check your spelling</p>
        </div>

        <!-- Recent Searches -->
        <div v-if="!searchQuery && recentSearches.length > 0" class="py-2">
          <p class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase">Recent Searches</p>
          <button
            v-for="(search, index) in recentSearches"
            :key="'recent-' + index"
            @click="searchQuery = search; handleSearch()"
            class="w-full px-4 py-2 hover:bg-gray-50 transition-colors text-left flex items-center justify-between"
          >
            <span class="text-sm text-gray-700">{{ search }}</span>
            <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()

// component state - search UI and results
const isOpen = ref(false)  // dropdown visibility
const searchQuery = ref('')  // current search text
const searching = ref(false)  // loading state
const searchContainer = ref(null)  // for click outside detection

// search results seperated by category
const results = ref({
  users: [],
  courses: [],
  assignments: [],
  studyGroups: []
})

// recent searches from localStorage
const recentSearches = ref([])

// timeout for debouncing search - prevents too many API calls
let searchTimeout = null

const hasResults = computed(() => {
  return results.value.users.length > 0 ||
    results.value.courses.length > 0 ||
    results.value.assignments.length > 0 ||
    results.value.studyGroups.length > 0
})

const totalResults = computed(() => {
  return results.value.users.length +
    results.value.courses.length +
    results.value.assignments.length +
    results.value.studyGroups.length
})

onMounted(() => {
  loadRecentSearches()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// handles search input with debouncing
function handleSearch() {
  if (searchQuery.value.length < 2) {
    results.value = { users: [], courses: [], assignments: [], studyGroups: [] }
    return
  }

  // clear previous timeout - debounce mechanism
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  // wait 300ms before searching - prevents excessive API calls
  searchTimeout = setTimeout(async () => {
    await performSearch()
  }, 300)
}

// perform actual search across all data types
async function performSearch() {
  try {
    searching.value = true

    const query = searchQuery.value.toLowerCase()

    // Search users - students and faculty
    const usersResponse = await api.getAllUsers()
    const allUsers = usersResponse.data || []
    results.value.users = allUsers
      .filter(u => {
        const fullName = `${u.firstName} ${u.lastName}`.toLowerCase()
        return fullName.includes(query) || u.email.toLowerCase().includes(query) || u.username.toLowerCase().includes(query)
      })
      .slice(0, 5)  // limit to 5 results per category

    // Search courses
    const coursesResponse = await api.getAllCourses()
    const allCourses = coursesResponse.data || []
    results.value.courses = allCourses
      .filter(c => c.name.toLowerCase().includes(query) || c.code.toLowerCase().includes(query))
      .slice(0, 5)

    // Search assignments (mock - replace with real API)
    results.value.assignments = []

    // Search study groups
    const groupsResponse = await api.getAllStudyGroups()
    const allGroups = groupsResponse.data || []
    results.value.studyGroups = allGroups
      .filter(g => g.name.toLowerCase().includes(query))
      .slice(0, 5)

    // Save to recent searches
    if (!recentSearches.value.includes(searchQuery.value)) {
      recentSearches.value.unshift(searchQuery.value)
      recentSearches.value = recentSearches.value.slice(0, 5)
      saveRecentSearches()
    }
  } catch (error) {
    console.error('Search error:', error)
  } finally {
    searching.value = false
  }
}

function navigate(path) {
  router.push(path)
  isOpen.value = false
  searchQuery.value = ''
}

function handleClickOutside(event) {
  if (searchContainer.value && !searchContainer.value.contains(event.target)) {
    isOpen.value = false
  }
}

function loadRecentSearches() {
  const saved = localStorage.getItem('recentSearches')
  if (saved) {
    recentSearches.value = JSON.parse(saved)
  }
}

function saveRecentSearches() {
  localStorage.setItem('recentSearches', JSON.stringify(recentSearches.value))
}

function getInitials(firstName, lastName) {
  if (!firstName) return '?'
  const first = firstName[0] || ''
  const last = lastName?.[0] || ''
  return (first + last).toUpperCase()
}

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>
