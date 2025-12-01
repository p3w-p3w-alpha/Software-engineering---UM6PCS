<!--
  DataTable - reusable data table component with search, pagination, and sorting
  handles student lists, course enrollments, and other tabular data
  the pagination logic was tricky but works for now
-->

<template>
  <div class="overflow-hidden rounded-lg border border-gray-200 bg-white shadow">
    <!-- Search and Actions toolbar -->
    <div v-if="searchable || $slots.actions" class="border-b border-gray-200 bg-gray-50 px-6 py-4">
      <div class="flex items-center justify-between">
        <div v-if="searchable" class="flex-1 max-w-md">
          <label for="table-search" class="sr-only">Search table</label>
          <input
            id="table-search"
            v-model="searchQuery"
            type="text"
            :placeholder="searchPlaceholder"
            class="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
            aria-label="Search table data"
          />
        </div>
        <div v-if="$slots.actions" class="ml-4">
          <slot name="actions"></slot>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th
              v-for="column in columns"
              :key="column.key"
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
              :class="column.headerClass"
            >
              {{ column.label }}
            </th>
            <th v-if="$slots['row-actions']" scope="col" class="relative px-6 py-3">
              <span class="sr-only">Actions</span>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="loading">
            <td :colspan="columns.length + ($slots['row-actions'] ? 1 : 0)" class="px-6 py-12 text-center">
              <LoadingSpinner />
            </td>
          </tr>
          <tr v-else-if="filteredData.length === 0">
            <td
              :colspan="columns.length + ($slots['row-actions'] ? 1 : 0)"
              class="px-6 py-12 text-center text-sm text-gray-500"
            >
              {{ emptyMessage }}
            </td>
          </tr>
          <tr
            v-else
            v-for="(row, index) in paginatedData"
            :key="index"
            class="hover:bg-gray-50 transition-colors"
          >
            <td
              v-for="column in columns"
              :key="column.key"
              class="whitespace-nowrap px-6 py-4 text-sm"
              :class="column.cellClass"
            >
              <slot :name="`cell-${column.key}`" :row="row" :value="row[column.key]">
                {{ row[column.key] }}
              </slot>
            </td>
            <td v-if="$slots['row-actions']" class="whitespace-nowrap px-6 py-4 text-right text-sm font-medium">
              <slot name="row-actions" :row="row"></slot>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div
      v-if="paginated && filteredData.length > 0"
      class="border-t border-gray-200 bg-white px-6 py-3 flex items-center justify-between"
    >
      <div class="text-sm text-gray-700">
        Showing
        <span class="font-medium">{{ startIndex + 1 }}</span>
        to
        <span class="font-medium">{{ endIndex }}</span>
        of
        <span class="font-medium">{{ filteredData.length }}</span>
        results
      </div>
      <nav class="flex space-x-2" aria-label="Pagination">
        <button
          @click="previousPage"
          :disabled="currentPage === 1"
          class="px-3 py-1 rounded-md border border-gray-300 text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          aria-label="Go to previous page"
        >
          Previous
        </button>
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="goToPage(page)"
          class="px-3 py-1 rounded-md text-sm font-medium"
          :class="[
            page === currentPage
              ? 'bg-blue-600 text-white'
              : 'border border-gray-300 text-gray-700 bg-white hover:bg-gray-50'
          ]"
          :aria-label="'Go to page ' + page"
          :aria-current="page === currentPage ? 'page' : undefined"
        >
          {{ page }}
        </button>
        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="px-3 py-1 rounded-md border border-gray-300 text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          aria-label="Go to next page"
        >
          Next
        </button>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import LoadingSpinner from './LoadingSpinner.vue'

// data table props - lots of customization here
const props = defineProps({
  columns: {
    type: Array,
    required: true  // column definitions with key and label
  },
  data: {
    type: Array,
    default: () => []  // table data rows
  },
  loading: {
    type: Boolean,
    default: false  // shows loading spinner
  },
  searchable: {
    type: Boolean,
    default: false  // enables search bar
  },
  searchPlaceholder: {
    type: String,
    default: 'Search...'
  },
  searchKeys: {
    type: Array,
    default: () => []  // which columns to search in
  },
  paginated: {
    type: Boolean,
    default: true  // enable pagination
  },
  perPage: {
    type: Number,
    default: 10  // rows per page
  },
  emptyMessage: {
    type: String,
    default: 'No data available'
  }
})

// component state for search and pagination
const searchQuery = ref('')
const currentPage = ref(1)

const filteredData = computed(() => {
  if (!props.searchable || !searchQuery.value) {
    return props.data
  }

  const query = searchQuery.value.toLowerCase()
  const keys = props.searchKeys.length > 0 ? props.searchKeys : props.columns.map(c => c.key)

  return props.data.filter(row => {
    return keys.some(key => {
      const value = row[key]
      return value && value.toString().toLowerCase().includes(query)
    })
  })
})

const totalPages = computed(() => {
  return Math.ceil(filteredData.value.length / props.perPage)
})

const startIndex = computed(() => {
  return (currentPage.value - 1) * props.perPage
})

const endIndex = computed(() => {
  return Math.min(startIndex.value + props.perPage, filteredData.value.length)
})

const paginatedData = computed(() => {
  if (!props.paginated) {
    return filteredData.value
  }
  return filteredData.value.slice(startIndex.value, endIndex.value)
})

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)

  if (end - start < maxVisible - 1) {
    start = Math.max(1, end - maxVisible + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const goToPage = (page) => {
  currentPage.value = page
}
</script>
