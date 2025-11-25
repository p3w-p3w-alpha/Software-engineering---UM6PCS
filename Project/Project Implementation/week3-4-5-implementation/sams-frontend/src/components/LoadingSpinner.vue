<template>
  <div class="flex items-center justify-center" :class="containerClass">
    <div class="relative" :style="{ width: size + 'px', height: size + 'px' }">
      <!-- Outer ring -->
      <div
        class="absolute inset-0 border-4 border-gray-200 rounded-full"
        :class="{ 'dark:border-gray-700': darkMode }"
      ></div>
      <!-- Spinning ring -->
      <div
        class="absolute inset-0 border-4 border-transparent rounded-full animate-spin"
        :class="colorClass"
        style="border-top-color: currentColor; animation-duration: 0.8s;"
      ></div>
    </div>
    <span v-if="text" class="ml-3 text-sm" :class="textColorClass">{{ text }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: Number,
    default: 40
  },
  color: {
    type: String,
    default: 'blue'
  },
  text: {
    type: String,
    default: ''
  },
  darkMode: {
    type: Boolean,
    default: false
  },
  containerClass: {
    type: String,
    default: ''
  }
})

const colorClass = computed(() => {
  const colors = {
    blue: 'text-blue-600',
    green: 'text-green-600',
    purple: 'text-purple-600',
    red: 'text-red-600',
    orange: 'text-orange-600',
    indigo: 'text-indigo-600'
  }
  return colors[props.color] || colors.blue
})

const textColorClass = computed(() => {
  return props.darkMode ? 'text-gray-300' : 'text-gray-700'
})
</script>
