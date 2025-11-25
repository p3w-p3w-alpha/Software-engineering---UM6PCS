<template>
  <div
    class="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 p-6 border border-gray-100 overflow-hidden relative group"
  >
    <!-- Background decoration -->
    <div class="absolute top-0 right-0 -mt-4 -mr-4 h-24 w-24 rounded-full opacity-10 group-hover:opacity-20 transition-opacity duration-300" :class="bgColorClass"></div>

    <div class="relative z-10">
      <!-- Icon -->
      <div class="flex items-center justify-between mb-4">
        <div class="p-3 rounded-lg" :class="iconBgClass">
          <component v-if="icon" :is="icon" class="h-6 w-6" :class="iconColorClass" />
          <div v-else class="h-6 w-6 rounded-full" :class="iconColorClass"></div>
        </div>
        <div v-if="trend" class="flex items-center space-x-1 text-sm">
          <component :is="trendIcon" class="h-4 w-4" :class="trendColorClass" />
          <span :class="trendColorClass" class="font-medium">{{ trend }}</span>
        </div>
      </div>

      <!-- Content -->
      <div>
        <p class="text-sm font-medium text-gray-600 mb-1">{{ title }}</p>
        <div class="flex items-end justify-between">
          <h3 class="text-3xl font-bold" :class="valueColorClass">
            <span v-if="loading" class="inline-block">
              <div class="h-9 w-20 bg-gray-200 rounded animate-pulse"></div>
            </span>
            <span v-else class="counter-animation">{{ value }}</span>
          </h3>
          <p v-if="subtitle" class="text-xs text-gray-500 mb-1">{{ subtitle }}</p>
        </div>
      </div>

      <!-- Progress bar if provided -->
      <div v-if="progress !== undefined" class="mt-4">
        <div class="flex justify-between text-xs text-gray-600 mb-1">
          <span>Progress</span>
          <span>{{ progress }}%</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
          <div
            class="h-2 rounded-full transition-all duration-1000 ease-out"
            :class="progressColorClass"
            :style="{ width: progress + '%' }"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ArrowTrendingUpIcon, ArrowTrendingDownIcon } from '@heroicons/vue/24/outline'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  icon: {
    type: Object,
    default: null
  },
  color: {
    type: String,
    default: 'blue',
    validator: (value) => ['blue', 'green', 'purple', 'orange', 'red', 'indigo', 'pink'].includes(value)
  },
  trend: {
    type: String,
    default: null
  },
  trendDirection: {
    type: String,
    default: 'up',
    validator: (value) => ['up', 'down'].includes(value)
  },
  progress: {
    type: Number,
    default: undefined
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const colorClasses = {
  blue: {
    icon: 'text-blue-600',
    iconBg: 'bg-blue-100',
    value: 'text-blue-600',
    bg: 'bg-blue-600',
    progress: 'bg-blue-600'
  },
  green: {
    icon: 'text-green-600',
    iconBg: 'bg-green-100',
    value: 'text-green-600',
    bg: 'bg-green-600',
    progress: 'bg-green-600'
  },
  purple: {
    icon: 'text-purple-600',
    iconBg: 'bg-purple-100',
    value: 'text-purple-600',
    bg: 'bg-purple-600',
    progress: 'bg-purple-600'
  },
  orange: {
    icon: 'text-orange-600',
    iconBg: 'bg-orange-100',
    value: 'text-orange-600',
    bg: 'bg-orange-600',
    progress: 'bg-orange-600'
  },
  red: {
    icon: 'text-red-600',
    iconBg: 'bg-red-100',
    value: 'text-red-600',
    bg: 'bg-red-600',
    progress: 'bg-red-600'
  },
  indigo: {
    icon: 'text-indigo-600',
    iconBg: 'bg-indigo-100',
    value: 'text-indigo-600',
    bg: 'bg-indigo-600',
    progress: 'bg-indigo-600'
  },
  pink: {
    icon: 'text-pink-600',
    iconBg: 'bg-pink-100',
    value: 'text-pink-600',
    bg: 'bg-pink-600',
    progress: 'bg-pink-600'
  }
}

const iconColorClass = computed(() => colorClasses[props.color].icon)
const iconBgClass = computed(() => colorClasses[props.color].iconBg)
const valueColorClass = computed(() => colorClasses[props.color].value)
const bgColorClass = computed(() => colorClasses[props.color].bg)
const progressColorClass = computed(() => colorClasses[props.color].progress)

const trendIcon = computed(() => props.trendDirection === 'up' ? ArrowTrendingUpIcon : ArrowTrendingDownIcon)
const trendColorClass = computed(() => props.trendDirection === 'up' ? 'text-green-600' : 'text-red-600')
</script>

<style scoped>
.counter-animation {
  display: inline-block;
  animation: countUp 0.5s ease-out;
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
