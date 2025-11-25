<template>
  <div class="animate-pulse">
    <!-- Card Skeleton -->
    <div v-if="type === 'card'" class="bg-white rounded-lg shadow p-6">
      <div class="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
      <div class="h-8 bg-gray-300 rounded w-1/2"></div>
    </div>

    <!-- Table Row Skeleton -->
    <div v-else-if="type === 'table-row'" class="flex space-x-4 py-4">
      <div class="h-4 bg-gray-200 rounded flex-1"></div>
      <div class="h-4 bg-gray-200 rounded flex-1"></div>
      <div class="h-4 bg-gray-200 rounded flex-1"></div>
      <div class="h-4 bg-gray-200 rounded w-20"></div>
    </div>

    <!-- List Item Skeleton -->
    <div v-else-if="type === 'list-item'" class="border border-gray-200 rounded-lg p-4 mb-3">
      <div class="h-5 bg-gray-300 rounded w-2/3 mb-2"></div>
      <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
      <div class="h-4 bg-gray-200 rounded w-3/4"></div>
    </div>

    <!-- Text Skeleton -->
    <div v-else-if="type === 'text'" class="space-y-2">
      <div class="h-4 bg-gray-200 rounded" :class="widthClass"></div>
    </div>

    <!-- Avatar Skeleton -->
    <div v-else-if="type === 'avatar'" class="flex items-center space-x-4">
      <div class="rounded-full bg-gray-200" :style="{ width: size + 'px', height: size + 'px' }"></div>
      <div class="flex-1 space-y-2">
        <div class="h-4 bg-gray-200 rounded w-1/2"></div>
        <div class="h-3 bg-gray-200 rounded w-1/3"></div>
      </div>
    </div>

    <!-- Custom -->
    <div v-else>
      <div class="h-4 bg-gray-200 rounded" :class="widthClass"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['card', 'table-row', 'list-item', 'text', 'avatar'].includes(value)
  },
  width: {
    type: String,
    default: 'full'
  },
  size: {
    type: Number,
    default: 48
  }
})

const widthClass = computed(() => {
  const widths = {
    full: 'w-full',
    '3/4': 'w-3/4',
    '1/2': 'w-1/2',
    '1/3': 'w-1/3',
    '1/4': 'w-1/4'
  }
  return widths[props.width] || widths.full
})
</script>
